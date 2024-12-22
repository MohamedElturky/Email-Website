package edu.alexu.mail.filter.email;

import edu.alexu.mail.filter.Filter;
import edu.alexu.mail.model.Email;

import java.util.List;

public class TopicFilter implements Filter<Email> {

    private final String topic;

    public TopicFilter(String topic) {
        this.topic = topic;
    }

    @Override
    public List<Email> apply(List<Email> emails) {
        return emails
                .stream()
                .filter(email -> email.getTopic().equalsIgnoreCase(topic))
                .toList();
    }
}
