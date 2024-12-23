package edu.alexu.mail.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.alexu.mail.model.User;
import edu.alexu.mail.repository.UserRepository;


@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       AuthenticationService authenticationService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public User registerUser(String emailAddress, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setEmailAddress(emailAddress.toLowerCase());
        user.setHashedPassword(hashedPassword);

        return userRepository.save(user);
    }

    public void deleteUser(String password, int userId) {
        User user = authenticationService.authenticateUser(getEmailAddress(userId), password);
        userRepository.delete(user);
    }

    public User getUser(int userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    public User getUser(String userEmailAddress) {
        return userRepository.findByEmailAddress(userEmailAddress).orElseThrow();
    }

    public String getEmailAddress(int userId) {
        return getUser(userId).getEmailAddress();
    }

    public int getUserId(String userEmailAddress) {
        return getUser(userEmailAddress).getId();
    }

    public boolean isRegisteredUser(String userEmailAddress) {
        return userRepository.existsByEmailAddress(userEmailAddress);
    }
}
