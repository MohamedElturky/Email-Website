package edu.alexu.mail.service;


import org.springframework.stereotype.Service;

import edu.alexu.mail.model.User;
import edu.alexu.mail.model.Email;
import edu.alexu.mail.model.Folder;
import edu.alexu.mail.repository.EmailRepository;
import edu.alexu.mail.repository.UserRepository;
import edu.alexu.mail.repository.FolderRepository;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

@Service
public class EmailService {


    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final AttachmentService attachmentService;

    public EmailService(EmailRepository emailRepository,
                        UserRepository userRepository,
                        FolderRepository folderRepository,
                        AttachmentService attachmentService) {
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.attachmentService = attachmentService;
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
            throw new RuntimeException("User not found");
        }
    }

    public List<Email> getFolderEmailsSortedByDate(int folderId) {
        Folder folder = folderRepository.findById(folderId).orElse(null);
        if (folder != null) {
            return folder
                    .getEmailsIds()
                    .stream()
                    .map(emailId -> emailRepository.findById(emailId).orElse(null))
                    .sorted(Comparator.comparing(Email::getCreationDateTime).reversed())
                    .toList();
        }
        else throw new RuntimeException("Folder not found");
    }

    public List<Email> getFolderEmailsSortedByPriority(int folderId) {
        Folder folder = folderRepository.findById(folderId).orElse(null);
        if (folder != null) {
            return folder
                    .getEmailsIds()
                    .stream()
                    .map(emailId -> emailRepository.findById(emailId).orElse(null))
                    .sorted(Comparator.comparing(Email::getPriority))
                    .toList();
        }
        else throw new RuntimeException("Folder not found");
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
            throw new RuntimeException("Start date cannot be after end date");
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
            throw new RuntimeException("User not found");
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
        List<Integer> receiverIds = email.getReceiversEmailAddresses()
                .stream()
                .map(receiverEmailAddress -> {
                    User receiver = userRepository.findByEmailAddress(receiverEmailAddress).orElse(null);
                    if (receiver != null) {
                        return receiver.getId();
                    }
                    else return null;
                })
                .toList();
        List<Folder> senderFolders = folderRepository.findAllByUserId(senderId);

        // Add sent email to sender's 'Sent' folder.
        for (Folder folder : senderFolders) {
            if (folder.getLabel().equalsIgnoreCase("Sent")) {
                folder.getEmailsIds().add(emailId);
                folderRepository.save(folder);
            }
        }

        // Add received email to receivers' 'Inbox' folder.
        for (Integer receiverId : receiverIds) {
            if (receiverId == null) continue;
            for (Folder folder : folderRepository.findAllByUserId(receiverId)) {
                if (folder.getLabel().equalsIgnoreCase("Inbox")) {
                    folder.getEmailsIds().add(emailId);
                    folderRepository.save(folder);
                }
            }
        }

        return sentEmail;
    }

    public Email getEmail(int id) {
        return emailRepository.findById(id).orElse(null);
    }

    public void deleteEmail(int userId, Integer emailId) {
        List<Folder> userFolders = new ArrayList<>(folderRepository.findAllByUserId(userId));
        for (Folder folder : userFolders) {
            folder.getEmailsIds().remove(emailId);
            folderRepository.save(folder);
        }

    }
}
