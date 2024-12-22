package edu.alexu.mail.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.alexu.mail.model.User;
import edu.alexu.mail.repository.UserRepository;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final FolderService folderService;
    private final ContactService contactService;
    private final EmailService emailService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       FolderService folderService,
                       ContactService contactService,
                       AuthenticationService authenticationService,
                       EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.folderService = folderService;
        this.contactService = contactService;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
    }

    public User registerUser(String emailAddress, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setEmailAddress(emailAddress);
        user.setHashedPassword(hashedPassword);

        User registeredUser = userRepository.save(user);
        int registeredUserId = registeredUser.getId();
        String registeredUserEmailAddress = registeredUser.getEmailAddress();

        folderService.createUserDefaultFolders(registeredUserId);

        emailService.populateInbox(registeredUserId);
        emailService.populateSent(registeredUserId, registeredUserEmailAddress);

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
}
