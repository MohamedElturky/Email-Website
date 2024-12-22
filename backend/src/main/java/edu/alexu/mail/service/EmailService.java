package edu.alexu.mail.service;


import edu.alexu.mail.model.Folders;
import org.springframework.stereotype.Service;

import edu.alexu.mail.model.User;
import edu.alexu.mail.model.Email;
import edu.alexu.mail.model.Folder;
import edu.alexu.mail.repository.EmailRepository;
import edu.alexu.mail.repository.UserRepository;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

@Service
public class EmailService {


    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final FolderService folderService;

    public EmailService(EmailRepository emailRepository,
                        UserRepository userRepository,
                        AttachmentService attachmentService,
                        FolderService folderService) {
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
        this.attachmentService = attachmentService;
        this.folderService = folderService;
    }

    public List<Email> getAllEmailsByUserId(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return emailRepository
                    .findAll()
                    .stream()
                    .filter(email -> email.getSenderId() == userId
                            || email.getReceiversEmailAddresses().contains(user.getEmailAddress()))
                    .toList();
        }
        else {
            throw new RuntimeException("User not found.");
        }
    }

    public List<Email> getAllEmailsSent(int userId) {
        return  getAllEmailsByUserId(userId)
                .stream()
                .filter(email -> email.getSenderId() == userId)
                .toList();
    }

    public List<Email> getAllEmailsReceived(int userId, String userEmailAddress) {
        return  getAllEmailsByUserId(userId)
                .stream()
                .filter(email -> email
                        .getReceiversEmailAddresses()
                        .stream()
                        .anyMatch(userEmailAddress::equals)
                )
                .toList();
    }

    public List<Email> getFolderEmailsSortedByDate(int folderId) {
        Folder folder = folderService.getFolder(folderId);
        if (folder != null) {
            return folder
                    .getEmailsIds()
                    .stream()
                    .map(this::getEmail)
                    .sorted(Comparator.comparing(Email::getCreationDateTime).reversed())
                    .toList();
        }
        else throw new RuntimeException("Folder not found.");
    }

    public List<Email> getFolderEmailsSortedByPriority(int folderId) {
        Folder folder = folderService.getFolder(folderId);
        if (folder != null) {
            return folder
                    .getEmailsIds()
                    .stream()
                    .map(this::getEmail)
                    .sorted(Comparator.comparing(Email::getPriority))
                    .toList();
        }
        else throw new RuntimeException("Folder not found.");
    }

    public List<Email> getAllEmailsOnAndAfter(int userId, LocalDateTime dateTime) {
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email -> !email.getCreationDateTime().isBefore(dateTime))
                .toList();
    }

    public List<Email> getAllEmailsOnAndBefore(int userId, LocalDateTime dateTime) {
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email -> !email.getCreationDateTime().isAfter(dateTime))
                .toList();
    }

    public List<Email> getAllEmailsOnAndBetween(int userId, LocalDateTime startDateTime,
                                                LocalDateTime endDateTime) {
        if (startDateTime.isAfter(endDateTime)) {
            throw new RuntimeException("Start date cannot be after end date.");
        }
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email ->
                                !email.getCreationDateTime().isBefore(startDateTime) &&
                                !email.getCreationDateTime().isAfter(endDateTime))
                .toList();
    }

    public List<Email> getAllEmailsByReceivers(int userId, List<String> receiversEmailAddresses) {
        List<Email> emails = emailRepository.findAllBySenderId(userId);

        return emails
                .stream()
                .filter(email -> !Collections.disjoint(email.getReceiversEmailAddresses(),
                        receiversEmailAddresses))
                .toList();
    }

    public List<Email> getAllEmailsBySender(int userId, String sender) {

        User user = userRepository.findById(userId).orElse(null);
        User sndr = userRepository.findByEmailAddress(sender).orElse(null);

        if (user != null && sndr != null) {
            List<Email> receivedEmails = emailRepository
                    .findAll()
                    .stream()
                    .filter(email -> email.getReceiversEmailAddresses().contains(user.getEmailAddress()))
                    .toList();

            return receivedEmails
                    .stream()
                    .filter(email -> email.getSenderId() == sndr.getId())
                    .toList();
        }
        else if (user == null) {
            throw new RuntimeException("User not found.");
        }
        else {
            throw new RuntimeException("Sender not found");
        }
    }

    public List<Email> getAllEmailsByTopic(int userId, String topic) {
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email -> email.getTopic().equalsIgnoreCase(topic))
                .toList();
    }

    public List<Email> getAllEmailsByBody(int userId, String body) {
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email -> StringUtils.containsIgnoreCase(
                        email.getBody(), body))
                .toList();
    }

    public List<Email> getAllEmailsByAttachments(int userId, List<String> attachments) {
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email -> {
                    try {
                        return !Collections.disjoint(attachmentService.
                                        getAttachmentsFileNames(email.getId()), attachments);
                    }
                    catch (IOException e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                })
                .toList();
    }


    public Email createEmail(Email email) {

        Email sentEmail = emailRepository.save(email);

        int emailId = sentEmail.getId();
        int senderId = sentEmail.getSenderId();

        int senderSentFolderId = folderService.getFolder(senderId, Folders.SENT).getId();
        List<Integer> receiversInboxFolderIds = email.getReceiversEmailAddresses()
                .stream()
                .map(receiverEmailAddress -> userRepository.findByEmailAddress(receiverEmailAddress).orElse(null))
                .filter(Objects::nonNull)
                .map(user -> folderService.getFolder(user.getId(), Folders.INBOX).getId())
                .toList();

        // Move sent email to sender's sent folder.
        folderService.moveEmail(emailId, senderSentFolderId);

        // Add received email to receivers' inbox folder.
        receiversInboxFolderIds.forEach(receiverInboxFolderId -> folderService.moveEmail(emailId, receiverInboxFolderId));

        return sentEmail;
    }

    public Email getEmail(int id) {
        Email email = emailRepository.findById(id).orElse(null);
        if (email != null) {
            return email;
        }
        else {
            throw new RuntimeException("Email not found.");
        }
    }

    public void deleteEmail(int userId, Integer emailId) {
        List<Folder> userFolders = folderService.getAllFolders(userId);
        Folder trashFolder = folderService.getFolder(userId, Folders.TRASH);

        userFolders.forEach(folder -> folderService.deleteEmailFromFolder(emailId, folder.getId()));

        // Move folder to trash.
        folderService.addEmailToFolder(emailId, trashFolder.getId());
    }

    public Email createDraft(Email email) {

        Email draft = emailRepository.save(email);

        int draftId = draft.getId();
        int senderId = draft.getSenderId();

        int draftFolderId = folderService.getFolder(senderId, Folders.DRAFT).getId();

        folderService.moveEmail(draftId, draftFolderId);

        return draft;
    }

    public Email editDraft(Email draft) {
        return emailRepository.save(draft);
    }

    public Email sendDraft(int draftId) {
        Email draft = emailRepository.findById(draftId).orElse(null);

        if (draft != null) {
            int userId = draft.getSenderId();
            int draftFolderId = folderService.getFolder(userId, Folders.DRAFT).getId();
            folderService.deleteEmailFromFolder(draftId, draftFolderId);
            return createEmail(draft);
        }
        else {
            throw new RuntimeException("Draft not found.");
        }
    }

    public void populateInbox(int userId) {
        int sentFolderId = folderService.getFolder(userId, Folders.SENT).getId();
        getAllEmailsSent(userId)
                .forEach(sentEmail -> folderService.moveEmail(sentEmail.getId(), sentFolderId));
    }

    public void populateSent(int userId, String userEmailAddress) {
        int inboxFolderId = folderService.getFolder(userId, Folders.INBOX).getId();
        getAllEmailsReceived(userId, userEmailAddress)
                .forEach(sentEmail -> folderService.moveEmail(sentEmail.getId(), inboxFolderId));
    }

}
