package edu.alexu.mail.filter.email.attribute;

import edu.alexu.mail.filter.Filter;
import edu.alexu.mail.model.Email;
import edu.alexu.mail.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

public class SenderFilter implements Filter<Email> {

    private final String senderEmailAddress;
    private final UserService userService;

    public SenderFilter(String senderEmailAddress,
                        UserService userService) {
        this.senderEmailAddress = senderEmailAddress;
        this.userService = userService;
    }

    @Override
    public List<Email> apply(List<Email> emails) {
        return emails
                .stream()
                .filter(email -> userService.getEmailAddress(email.getSenderId()).equalsIgnoreCase(senderEmailAddress))
                .collect(Collectors.toList());
    }
}
