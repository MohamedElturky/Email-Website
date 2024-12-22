package edu.alexu.mail.service;

import edu.alexu.mail.model.Email;
import edu.alexu.mail.model.FolderType;
import edu.alexu.mail.repository.EmailRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.alexu.mail.model.User;
import edu.alexu.mail.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final FolderService folderService;
    private final ContactService contactService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       FolderService folderService,
                       ContactService contactService,
                       AuthenticationService authenticationService,
                       EmailRepository emailRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.folderService = folderService;
        this.contactService = contactService;
        this.authenticationService = authenticationService;
        this.emailRepository = emailRepository;
    }

    public User registerUser(String emailAddress, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setEmailAddress(emailAddress.toLowerCase());
        user.setHashedPassword(hashedPassword);

        User registeredUser = userRepository.save(user);
        int registeredUserId = registeredUser.getId();
        String registeredUserEmailAddress = registeredUser.getEmailAddress();

        folderService.createDefaultUserFolders(registeredUserId);



        int sentFolderId = folderService.getFolder(registeredUserId, FolderType.SENT).getId();
        int inboxFolderId = folderService.getFolder(registeredUserId, FolderType.INBOX).getId();

        List<Email> sentEmails = emailRepository.findAllBySenderId(registeredUserId);
        List<Email> receivedEmails = emailRepository.findAllByReceiverEmailAddress(registeredUserEmailAddress);

        // Populate inbox and sent folders.
        sentEmails.forEach(email -> folderService.moveEmail(email.getId(), sentFolderId));
        receivedEmails.forEach(email -> folderService.moveEmail(email.getId(), inboxFolderId));

        return registeredUser;
    }

    public void deleteUser(String password, int userId) {
        User authenticateUser = authenticationService.authenticateUser(getEmailAddress(userId), password);
        if (authenticateUser != null) {
            userRepository.deleteById(userId);
            folderService.deleteAllFoldersByUserId(userId);
            contactService.deleteAllContactsByUserId(userId);
        }
    }

    public String getEmailAddress(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getEmailAddress();
        }
        else throw new RuntimeException("User not found.");
    }

    public int getUserId(String userEmailAddress) {
        User user = userRepository.findByEmailAddress(userEmailAddress).orElse(null);
        if (user != null) {
            return user.getId();
        }
        else throw new RuntimeException("User not found.");
    }

    public boolean isRegisteredUser(String userEmailAddress) {
        return userRepository.existsByEmailAddress(userEmailAddress);
    }
}
