package edu.alexu.mail.controller;

import edu.alexu.mail.service.AuthenticationService;
import edu.alexu.mail.service.UserService;
import org.springframework.web.bind.annotation.*;

import edu.alexu.mail.model.User;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService,
                          AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestParam String emailAddress, @RequestParam String password) {
        return userService.registerUser(emailAddress, password);
    }

    @GetMapping("/auth")
    public User authenticateUser(@RequestParam String emailAddress, @RequestParam String password) {
        return authenticationService.authenticateUser(emailAddress, password);
    }

    @DeleteMapping
    public void deleteUser(@RequestParam int id) {
        userService.deleteUser(id);
    }
}
