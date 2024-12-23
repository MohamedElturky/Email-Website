package edu.alexu.mail.listener;

import edu.alexu.mail.model.Email;
import edu.alexu.mail.model.Folder;
import edu.alexu.mail.model.FolderType;
import edu.alexu.mail.model.User;
import edu.alexu.mail.service.EmailService;
import edu.alexu.mail.service.FolderService;
import edu.alexu.mail.service.ContactService;
import jakarta.persistence.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserListener {

    private final FolderService folderService;
    private final ContactService contactService;
    private final EmailService emailService;

    private List<Email> receivedEmails;

    public UserListener(@Lazy FolderService folderService,
                        @Lazy ContactService contactService,
                        @Lazy EmailService emailService) {
        this.folderService = folderService;
        this.contactService = contactService;
        this.emailService = emailService;
        receivedEmails = null;
    }

    @PrePersist
    public void prePersist(User user) {
        this.receivedEmails = emailService.getAllEmailsReceived(user.getEmailAddress());
    }

    @PostPersist
    public void postPersist(User user) {
        int userId = user.getId();

        List<Folder> defaultFolders = folderService.createDefaultUserFolders(userId);
        Folder inboxFolder = folderService.findDefaultFolder(defaultFolders, FolderType.INBOX);

        // Populate inbox folder.
        receivedEmails.forEach(email -> folderService.moveEmail(email.getId(), inboxFolder));
    }

    @PreRemove
    public void preRemove(User user) {
        int userId = user.getId();
        folderService.deleteAllFoldersByUserId(userId);
        contactService.deleteAllContactsByUserId(userId);
    }
}
