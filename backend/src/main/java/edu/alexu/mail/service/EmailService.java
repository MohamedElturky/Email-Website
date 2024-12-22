package edu.alexu.mail.service;


import edu.alexu.mail.filter.*;
import edu.alexu.mail.filter.email.EmailFilterFactory;
import edu.alexu.mail.filter.email.EmailFilterType;
import edu.alexu.mail.model.FolderType;
import org.springframework.stereotype.Service;

import edu.alexu.mail.model.Email;
import edu.alexu.mail.model.Folder;
import edu.alexu.mail.repository.EmailRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmailService {

    private final EmailRepository emailRepository;
    private final FolderService folderService;
    private final UserService userService;

    private final EmailFilterFactory emailFilterFactory;

    public EmailService(EmailRepository emailRepository,
                        FolderService folderService,
                        UserService userService,
                        EmailFilterFactory emailFilterFactory) {
        this.emailRepository = emailRepository;
        this.folderService = folderService;
        this.userService = userService;
        this.emailFilterFactory = emailFilterFactory;
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
        return folder
                .getEmailsIds()
                .stream()
                .map(this::getEmail)
                .sorted(Comparator.comparing(Email::getCreationDateTime).reversed())
                .toList();
    }

    public List<Email> getFolderEmailsSortedByPriority(int folderId) {
        Folder folder = folderService.getFolder(folderId);
        return folder
                .getEmailsIds()
                .stream()
                .map(this::getEmail)
                .sorted(Comparator.comparing(Email::getPriority))
                .toList();
    }

    public List<Email> filterBy(List<Email> emails, EmailFilterType filterType, Object... arg) {
        Filter<Email> filter = emailFilterFactory.createFilter(filterType, arg);
        return filter.apply(emails);
    }

    public List<Email> getAllEmailsOnOrAfter(int userId, LocalDateTime dateTime) {
        return filterBy(getAllEmailsByUserId(userId), EmailFilterType.ON_OR_AFTER, dateTime);
    }

    public List<Email> getAllEmailsOnOrBefore(int userId, LocalDateTime dateTime) {
        return filterBy(getAllEmailsByUserId(userId), EmailFilterType.ON_OR_BEFORE, dateTime);
    }

    public List<Email> getAllEmailsOnOrBetween(int userId, LocalDateTime startDateTime,
                                               LocalDateTime endDateTime) {
        return filterBy(getAllEmailsByUserId(userId), EmailFilterType.ON_OR_BETWEEN, startDateTime, endDateTime);
    }

    public List<Email> getAllEmailsByReceivers(int userId, List<String> receiversEmailAddresses) {
        return filterBy(emailRepository.findAllBySenderId(userId), EmailFilterType.RECEIVERS, receiversEmailAddresses);
    }

    public List<Email> getAllEmailsBySender(int userId, String senderEmailAddress) {
        return filterBy(getAllEmailsReceived(userService.getEmailAddress(userId)), EmailFilterType.SENDER, senderEmailAddress);
    }

    public List<Email> getAllEmailsByTopic(int userId, String topic) {
        return filterBy(getAllEmailsByUserId(userId), EmailFilterType.TOPIC, topic);
    }

    public List<Email> getAllEmailsByBody(int userId, String body) {
        return filterBy(getAllEmailsByUserId(userId), EmailFilterType.BODY, body);

    }

    public List<Email> getAllEmailsByAttachmentsFileNames(int userId, List<String> attachments) {
        return filterBy(getAllEmailsByUserId(userId), EmailFilterType.ATTACHMENTS, attachments);
    }

    public Email createEmail(Email email) {

        Email sentEmail = emailRepository.save(email);

        int emailId = sentEmail.getId();
        int senderId = sentEmail.getSenderId();

        // Move sent email to sender's sent folder.
        int senderSentFolderId = folderService.getFolder(senderId, FolderType.SENT).getId();
        folderService.moveEmail(emailId, senderSentFolderId);


        // Add received email to receivers' inbox folder.
        email.getReceiversEmailAddresses()
                .stream()
                .filter(userService::isRegisteredUser)
                .map(userService::getUserId)
                .map(userId -> folderService.getFolder(userId, FolderType.INBOX).getId())
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
        Folder trashFolder = folderService.getFolder(userId, FolderType.TRASH);

        userFolders.forEach(folder -> folderService.deleteEmailFromFolder(emailId, folder.getId()));

        // Move folder to trash.
        folderService.addEmailToFolder(emailId, trashFolder.getId());
    }

    public Email restoreEmail(int userId, Integer emailId) {
        int trashFolderId = folderService.getFolder(userId, FolderType.TRASH).getId();
        int inboxFolderId = folderService.getFolder(userId, FolderType.INBOX).getId();

        folderService.moveEmail(emailId, trashFolderId, inboxFolderId);

        return getEmail(emailId);
    }

    public Email createDraft(Email email) {

        Email draft = emailRepository.save(email);

        int draftId = draft.getId();
        int senderId = draft.getSenderId();

        int draftFolderId = folderService.getFolder(senderId, FolderType.DRAFT).getId();

        folderService.moveEmail(draftId, draftFolderId);

        return draft;
    }

    public Email editDraft(Email draft) {
        return emailRepository.save(draft);
    }

    public Email sendDraft(int draftId) {
        Email draft = getEmail(draftId);
        int userId = draft.getSenderId();
        int draftFolderId = folderService.getFolder(userId, FolderType.DRAFT).getId();
        folderService.deleteEmailFromFolder(draftId, draftFolderId);
        return createEmail(draft);
    }
}
