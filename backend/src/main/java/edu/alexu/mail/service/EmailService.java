package edu.alexu.mail.service;

import org.springframework.stereotype.Service;

import edu.alexu.mail.model.User;
import edu.alexu.mail.model.Email;
import edu.alexu.mail.repository.EmailRepository;
import edu.alexu.mail.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

@Service
public class EmailService {

    private final String cdn = System.getenv("cdn");


    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    public EmailService(EmailRepository emailRepository,
                        UserRepository userRepository) {
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    private List<Email> getAllEmailsByUserId(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return emailRepository
                    .findAll()
                    .stream()
                    .filter(email -> email.getSenderId() == userId
                            || email.getReceiversEmailAddresses().contains(user.getEmailAddress()))
                    .toList();
        }
        else {
            throw new RuntimeException("User not found");
        }
    }

    public List<Email> getAllEmailsSortedByDate(int userId) {
        List<Email> emails = new ArrayList<>(getAllEmailsByUserId(userId));
        emails.sort(Comparator.comparing(Email::getCreationDateTime));
        return emails;
    }

    public List<Email> getAllEmailsSortedByPriority(int userId) {
        List<Email> emails = new ArrayList<>(getAllEmailsByUserId(userId));
        emails.sort(Comparator.comparingInt(Email::getPriority));
        return emails;
    }

    public List<Email> getAllEmailsOnAndAfter(int userId, LocalDateTime dateTime) {
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email -> !email.getCreationDateTime().isBefore(dateTime))
                .toList();
    }

    public List<Email> getAllEmailsOnAndBefore(int userId, LocalDateTime dateTime) {
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email -> !email.getCreationDateTime().isAfter(dateTime))
                .toList();
    }

    public List<Email> getAllEmailsOnAndBetween(int userId, LocalDateTime startDateTime,
                                                LocalDateTime endDateTime) {
        if (startDateTime.isAfter(endDateTime)) {
            throw new RuntimeException("Start date cannot be after end date");
        }
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email ->
                                !email.getCreationDateTime().isBefore(startDateTime) &&
                                !email.getCreationDateTime().isAfter(endDateTime))
                .toList();
    }

    public List<Email> getAllEmailsByReceivers(int userId, List<String> receiversEmailAddresses) {
        List<Email> emails = emailRepository.findAllBySenderId(userId);

        return emails
                .stream()
                .filter(email -> !Collections.disjoint(email.getReceiversEmailAddresses(),
                        receiversEmailAddresses))
                .toList();
    }

    public List<Email> getAllEmailsBySender(int userId, String sender) {

        User user = userRepository.findById(userId).orElse(null);
        User sndr = userRepository.findByEmailAddress(sender).orElse(null);

        if (user != null && sndr != null) {
            List<Email> receivedEmails = emailRepository
                    .findAll()
                    .stream()
                    .filter(email -> email.getReceiversEmailAddresses().contains(user.getEmailAddress()))
                    .toList();

            return receivedEmails
                    .stream()
                    .filter(email -> email.getSenderId() == sndr.getId())
                    .toList();
        }
        else if (user == null) {
            throw new RuntimeException("User not found");
        }
        else {
            throw new RuntimeException("Sender not found");
        }
    }

    public List<Email> getAllEmailsByTopic(int userId, String topic) {
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email -> email.getTopic().equalsIgnoreCase(topic))
                .toList();
    }

    public List<Email> getAllEmailsByBody(int userId, String body) {
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email -> StringUtils.containsIgnoreCase(
                        email.getBody(), body))
                .toList();
    }

    public List<Email> getAllEmailsByAttachments(int userId, List<String> attachments) {
        List<Email> emails = getAllEmailsByUserId(userId);
        return emails
                .stream()
                .filter(email -> {
                    try {
                        return !Collections.disjoint(getAttachmentsFileNames(email.getId()),
                                            attachments);
                    }
                    catch (IOException e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                })
                .toList();
    }


    public Email createEmail(Email email) {
        return emailRepository.save(email);
    }

    public Email getEmail(int id) {
        return emailRepository.findById(id).orElse(null);
    }

    public void deleteEmail(int id) {
        emailRepository.deleteById(id);
    }

    public List<String> getAttachmentsFileNames(int id) throws IOException {
        Path directory = Paths.get(cdn + "\\" + id);
        return Files
                .walk(directory)
                .map(path -> path.getFileName().toString())
                .toList();
    }
}
