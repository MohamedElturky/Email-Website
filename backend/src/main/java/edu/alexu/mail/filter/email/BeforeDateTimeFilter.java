package edu.alexu.mail.filter.email;

import edu.alexu.mail.model.Email;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BeforeDateTimeFilter extends DateTimeFilter {

    public BeforeDateTimeFilter(LocalDateTime dateTime) {
        super(dateTime);
    }

    @Override
    public List<Email> apply(List<Email> emails) {
        return emails
                .stream()
                .filter(email -> email.getCreationDateTime().isBefore(dateTime))
                .collect(Collectors.toList());
    }
}
