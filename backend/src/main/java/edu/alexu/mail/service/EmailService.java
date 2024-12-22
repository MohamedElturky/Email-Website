package edu.alexu.mail.service;


import edu.alexu.mail.model.Folders;
import org.springframework.stereotype.Service;

import edu.alexu.mail.model.Email;
import edu.alexu.mail.model.Folder;
import edu.alexu.mail.repository.EmailRepository;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final AttachmentService attachmentService;
    private final FolderService folderService;
    private final UserService userService;

    public EmailService(EmailRepository emailRepository,
                        AttachmentService attachmentService,
                        FolderService folderService, UserService userService) {
        this.emailRepository = emailRepository;
        this.attachmentService = attachmentService;
        this.folderService = folderService;
        this.userService = userService;
    }

    public List<Email> getAllEmailsByUserId(int userId) {
        String userEmailAddress = userService.getEmailAddress(userId);
        List<Email> emails = getAllEmailsSent(userId);
        emails.addAll(getAllEmailsReceived(userEmailAddress));
        return emails;
    }

    public List<Email> getAllEmailsSent(int userId) {
        return  emailRepository.findAllBySenderId(userId);
    }

    public List<Email> getAllEmailsReceived(String userEmailAddress) {
        return emailRepository.findAllByReceiverEmailAddress(userEmailAddress.toLowerCase());
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

    public List<Email> getAllEmailsBySender(int userId, String senderEmailAddress) {

        return getAllEmailsReceived(userService.getEmailAddress(userId))
                .stream()
                .filter(email -> userService.getEmailAddress(email.getSenderId()).equalsIgnoreCase(senderEmailAddress))
                .toList();
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

        // Move sent email to sender's sent folder.
        int senderSentFolderId = folderService.getFolder(senderId, Folders.SENT).getId();
        folderService.moveEmail(emailId, senderSentFolderId);


        // Add received email to receivers' inbox folder.
        email.getReceiversEmailAddresses()
                .stream()
                .filter(userService::isRegisteredUser)
                .map(userService::getUserId)
                .map(userId -> folderService.getFolder(userId, Folders.INBOX).getId())
                .forEach(inboxFolderId -> folderService.moveEmail(emailId, inboxFolderId));

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

    public Email restoreEmail(Integer emailId, int userId) {
        int trashFolderId = folderService.getFolder(userId, Folders.TRASH).getId();
        int inboxFolderId = folderService.getFolder(userId, Folders.INBOX).getId();

        folderService.moveEmail(emailId, trashFolderId, inboxFolderId);

        return getEmail(emailId);
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
        Email draft = getEmail(draftId);
        int userId = draft.getSenderId();
        int draftFolderId = folderService.getFolder(userId, Folders.DRAFT).getId();
        folderService.deleteEmailFromFolder(draftId, draftFolderId);
        return createEmail(draft);
    }
}
