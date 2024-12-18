package edu.alexu.mail.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.alexu.mail.model.Folder;
import edu.alexu.mail.model.User;
import edu.alexu.mail.repository.UserRepository;
import edu.alexu.mail.repository.FolderRepository;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       FolderRepository folderRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
    }

    public User registerUser(String emailAddress, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setEmailAddress(emailAddress);
        user.setHashedPassword(hashedPassword);

        User registeredUser = userRepository.save(user);

        for (String defaultFolder : Folder.defaultFolders) {
            folderRepository.save(new Folder(defaultFolder, registeredUser.getId()));
        }

        return registeredUser;
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
