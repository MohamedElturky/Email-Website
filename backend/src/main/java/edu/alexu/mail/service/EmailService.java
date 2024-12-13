package edu.alexu.mail.service;

import edu.alexu.mail.model.Email;
import edu.alexu.mail.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

@Service
public class EmailService {

    private final String cdn = System.getenv("cdn");


    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public List<Email> getEmailsSortedByDate() {
        List<Email> emails = emailRepository.findAll();
        emails.sort(Comparator.comparing(Email::getCreationDateTime));
        return emails;
    }

    public List<Email> getEmailsOnAndAfter(LocalDateTime dateTime) {
        return emailRepository
                .findAll()
                .stream()
                .filter(email -> email.getCreationDateTime().isAfter(dateTime)
                                    || email.getCreationDateTime().isEqual(dateTime))
                .collect(Collectors.toList());
    }

    public List<Email> getEmailsOnAndBefore(LocalDateTime dateTime) {
        return emailRepository
                .findAll()
                .stream()
                .filter(email -> email.getCreationDateTime().isBefore(dateTime)
                                    || email.getCreationDateTime().isEqual(dateTime))
                .collect(Collectors.toList());
    }

    public List<Email> getEmailsOnAndBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return emailRepository
                .findAll()
                .stream()
                .filter(email ->
                                (email.getCreationDateTime().isAfter(startDateTime)
                                        || email.getCreationDateTime().isEqual(startDateTime))
                                && (email.getCreationDateTime().isBefore(endDateTime)
                                        || email.getCreationDateTime().isEqual(endDateTime)))
                .collect(Collectors.toList());
    }

    public List<Email> getEmailsSortedByPriority() {
        List<Email> emails = emailRepository.findAll();
        emails.sort(Comparator.comparingInt(Email::getPriority));
        return emails;
    }



    public List<Email> getEmailsByReceivers(List<String> receivers) {
        return emailRepository
                .findAll()
                .stream()
                .filter(email -> receivers.contains(email.getReceiver()))
                .collect(Collectors.toList());
    }

    public List<Email> getEmailsBySender(String sender) {
        return emailRepository
                .findAll()
                .stream()
                .filter(email -> email.getSender().equalsIgnoreCase(sender))
                .collect(Collectors.toList());
    }

    public List<Email> getEmailsByTopic(String topic) {
        return emailRepository
                .findAll()
                .stream()
                .filter(email -> email.getTopic().equalsIgnoreCase(topic))
                .collect(Collectors.toList());
    }

    public List<Email> getEmailsByBody(String body) {
        return emailRepository
                .findAll()
                .stream()
                .filter(email -> StringUtils.containsIgnoreCase(
                        email.getBody(), body))
                .collect(Collectors.toList());
    }

    public List<Email> getEmailByAttachments(List<String> attachments) {
        return emailRepository
                .findAll()
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
                .collect(Collectors.toList());
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

    public void moveEmail(int id, String newPath) {
        Optional<Email> email = emailRepository.findById(id);
        if (email.isPresent()) {
            email.get().setPathname(newPath);
        }
        else {
            throw new RuntimeException("Email not found");
        }
    }

    public List<String> getAttachmentsFileNames(int id) throws IOException {
        Path directory = Paths.get(cdn + "\\" + id);
        return Files
                .walk(directory)
                .map(path -> path.getFileName().toString())
                .toList();
    }

    @Scheduled(cron = "@midnight")
    private void clearTrashFolder() {
        emailRepository
                .findAll()
                .forEach(email -> {
                    if (email.getPathname().equals("/Trash")
                            && LocalDateTime.now().minusDays(30).isAfter(email.getCreationDateTime())) {
                        emailRepository.delete(email);
                    }
                });
    }
}
