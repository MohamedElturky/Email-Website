package edu.alexu.mail.filter.email.datetime;

import edu.alexu.mail.model.Email;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OnDateTimeFilter extends DateTimeFilter {

    public OnDateTimeFilter(LocalDateTime dateTime) {
        super(dateTime);
    }

    @Override
    public List<Email> apply(List<Email> emails) {
        return emails
                .stream()
                .filter(email -> email.getCreationDateTime().isEqual(dateTime))
                .collect(Collectors.toList());
    }
}
