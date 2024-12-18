package edu.alexu.mail.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.alexu.mail.model.User;
import edu.alexu.mail.repository.UserRepository;


@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthenticationService(PasswordEncoder passwordEncoder,
                                 UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User authenticateUser(String emailAddress, String rawPassword) {
        User user = userRepository.findByEmailAddress(emailAddress).orElse(null);

        if (user != null
            && passwordEncoder.matches(rawPassword, user.getHashedPassword())) {
            return user;
        }
        else {
            return null;
        }
    }
}
