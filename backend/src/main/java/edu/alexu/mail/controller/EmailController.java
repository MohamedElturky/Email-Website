package edu.alexu.mail.controller;

import org.springframework.web.bind.annotation.*;

import edu.alexu.mail.model.Email;
import edu.alexu.mail.service.EmailService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/email")
public class EmailController {

    EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/all")
    public List<Email> getAllEmailsSortedByDate(@RequestParam int userId) {
        return emailService.getAllEmailsSortedByDate(userId);
    }

    @GetMapping("/all/on-and-after")
    public List<Email> getAllEmailsOnAndAfter(@RequestParam int userId,
                                              @RequestParam LocalDateTime dateTime) {
        return emailService.getAllEmailsOnAndAfter(userId, dateTime);
    }

    @GetMapping("/all/on-and-before")
    public List<Email> getAllEmailsOnAndBefore(@RequestParam int userId,
                                               @RequestParam LocalDateTime dateTime) {
        return emailService.getAllEmailsOnAndBefore(userId, dateTime);
    }

    @GetMapping("/all/on-and-between")
    public List<Email> getAllEmailsOnAndBetween(@RequestParam int userId,
                                                @RequestParam LocalDateTime startDateTime,
                                                @RequestParam LocalDateTime endDateTime) {
        return emailService.getAllEmailsOnAndBetween(userId, startDateTime, endDateTime);
    }

    @GetMapping("/all/sorted/priority")
    public List<Email> getAllEmailsSortedByPriority(@RequestParam int userId) {
        return emailService.getAllEmailsSortedByPriority(userId);
    }

    @GetMapping("/all/receivers")
    public List<Email> getAllEmailsByReceivers(@RequestParam int userId,
                                               @RequestParam List<String> receiver) {
        return emailService.getAllEmailsByReceivers(userId, receiver);
    }

    @GetMapping("/all/sender")
    public List<Email> getAllEmailsBySender(@RequestParam int userId,
                                            @RequestParam String sender) {
        return emailService.getAllEmailsBySender(userId, sender);
    }

    @GetMapping("/all/topic")
    public List<Email> getAllEmailsByTopic(@RequestParam int userId,
                                           @RequestParam String topic) {
        return emailService.getAllEmailsByTopic(userId, topic);
    }

    @GetMapping("/all/body")
    public List<Email> getAllEmailsByBody(@RequestParam int userId,
                                          @RequestParam String body) {
        return emailService.getAllEmailsByBody(userId, body);
    }

    @GetMapping("/all/attachments")
    public List<Email> getAllEmailsByAttachments(@RequestParam int userId,
                                                 @RequestParam List<String> attachments) {
        return emailService.getAllEmailsByAttachments(userId, attachments);
    }

    @PostMapping
    public Email createEmail(@RequestBody Email email) {
        return emailService.createEmail(email);
    }

    @GetMapping
    public Email getEmail(@RequestParam int id) {
        return emailService.getEmail(id);
    }

    @DeleteMapping
    public void deleteEmail(@RequestParam int userId, @RequestParam Integer emailId) {
        emailService.deleteEmail(userId, emailId);
    }

    @GetMapping("/attachments")
    public List<String> getAttachmentsFileNames(@RequestParam int id) throws IOException {
        return emailService.getAttachmentsFileNames(id);
    }

}
