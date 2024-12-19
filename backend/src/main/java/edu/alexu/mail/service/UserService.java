package edu.alexu.mail.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.alexu.mail.model.Email;
import edu.alexu.mail.model.Folder;
import edu.alexu.mail.model.User;

import edu.alexu.mail.repository.UserRepository;
import edu.alexu.mail.repository.FolderRepository;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    public UserService(PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       UserRepository userRepository,
                       FolderRepository folderRepository) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
    }

    public User registerUser(String emailAddress, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setEmailAddress(emailAddress);
        user.setHashedPassword(hashedPassword);

        User registeredUser = userRepository.save(user);

        Folder sentFolder = null;
        Folder inboxFolder = null;

        for (String defaultFolder : Folder.defaultFolders) {
            Folder folder = new Folder(defaultFolder, registeredUser.getId());
            if (defaultFolder.equalsIgnoreCase("Sent")) sentFolder = folder;
            else if (defaultFolder.equalsIgnoreCase("Inbox")) inboxFolder = folder;
            folderRepository.save(folder);
        }

        for (Email email : emailService.getAllEmailsByUserId(registeredUser.getId())) {
            if (email.getSenderId() == registeredUser.getId()) {
                if (sentFolder != null) {
                    sentFolder.getEmailsIds().add(email.getId());
                    folderRepository.save(sentFolder);
                }
                else throw new RuntimeException("Sent folder not found.");
            }
            if (email.getReceiversEmailAddresses().contains(registeredUser.getEmailAddress())) {
                if (inboxFolder != null) {
                    inboxFolder.getEmailsIds().add(email.getId());
                    folderRepository.save(inboxFolder);
                }
                else throw new RuntimeException("Inbox folder not found.");
            }
        }

        return registeredUser;
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public String getEmailAddress(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getEmailAddress();
        }
        else throw new RuntimeException("User not found.");
    }
}
