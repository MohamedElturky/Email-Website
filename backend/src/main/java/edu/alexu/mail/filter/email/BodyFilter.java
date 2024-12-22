package edu.alexu.mail.filter.email;

import edu.alexu.mail.filter.Filter;
import edu.alexu.mail.model.Email;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class BodyFilter implements Filter<Email> {

    private final String body;

    public BodyFilter(String body) {
        this.body = body;
    }

    @Override
    public List<Email> apply(List<Email> emails) {
        return emails
                .stream()
                .filter(email -> StringUtils.containsIgnoreCase(
                        email.getBody(), body))
                .toList();
    }
}
