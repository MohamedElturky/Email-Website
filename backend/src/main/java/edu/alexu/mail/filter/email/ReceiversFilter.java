package edu.alexu.mail.filter.email;

import edu.alexu.mail.filter.Filter;
import edu.alexu.mail.model.Email;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReceiversFilter implements Filter<Email> {

    private final List<String> receiversEmailAddresses;

    public ReceiversFilter(List<String> receiversEmailAddresses) {
        this.receiversEmailAddresses = receiversEmailAddresses;
    }

    @Override
    public List<Email> apply(List<Email> emails) {
        return emails
                .stream()
                .filter(email ->
                        !Collections.disjoint(email.getReceiversEmailAddresses(),
                                receiversEmailAddresses))
                .collect(Collectors.toList());
    }
}
