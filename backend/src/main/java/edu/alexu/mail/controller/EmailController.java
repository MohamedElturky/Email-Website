package edu.alexu.mail.controller;

import edu.alexu.mail.filter.email.EmailFilterType;
import org.springframework.web.bind.annotation.*;

import edu.alexu.mail.model.Email;
import edu.alexu.mail.service.EmailService;

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

    @GetMapping("/folder/default")
    public List<Email> getFolderEmailsSortedByDate(@RequestParam int folderId) {
        return emailService.getFolderEmailsSortedByDate(folderId);
    }

    @GetMapping("/folder/priority")
    public List<Email> getFolderEmailsSortedByPriority(@RequestParam int folderId) {
        return emailService.getFolderEmailsSortedByPriority(folderId);
    }

    @PutMapping("/all/filter")
    public List<Email> getFilterEmails(@RequestBody List<Email> emails, @RequestParam EmailFilterType filterType, @RequestParam Object... args) {
        return emailService.filterBy(emails, filterType, args);
    }

    @GetMapping("/all/on-or-after")
    public List<Email> getAllEmailsOnOrAfter(@RequestParam int userId,
                                             @RequestParam LocalDateTime dateTime) {
        return emailService.getAllEmailsOnOrAfter(userId, dateTime);
    }

    @GetMapping("/all/on-or-before")
    public List<Email> getAllEmailsOnOrBefore(@RequestParam int userId,
                                               @RequestParam LocalDateTime dateTime) {
        return emailService.getAllEmailsOnOrBefore(userId, dateTime);
    }

    @GetMapping("/all/on-or-between")
    public List<Email> getAllEmailsOnOrBetween(@RequestParam int userId,
                                                @RequestParam LocalDateTime startDateTime,
                                                @RequestParam LocalDateTime endDateTime) {
        return emailService.getAllEmailsOnOrBetween(userId, startDateTime, endDateTime);
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
        return emailService.getAllEmailsByAttachmentsFileNames(userId, attachments);
    }

    @PostMapping
    public Email createEmail(@RequestBody Email email) {
        return emailService.createEmail(email);
    }

    @PostMapping("/draft")
    public Email createDraft(@RequestBody Email email) {
        return emailService.createDraft(email);
    }

    @PutMapping("/draft")
    public Email editDraft(@RequestBody Email email) {
        return emailService.editDraft(email);
    }

    @PostMapping("/draft/send")
    public Email sendDraft(@RequestParam int draftId) {
        return emailService.sendDraft(draftId);
    }

    @GetMapping
    public Email getEmail(@RequestParam int id) {
        return emailService.getEmail(id);
    }

    @DeleteMapping
    public void deleteEmail(@RequestParam int userId, @RequestParam Integer emailId) {
        emailService.deleteEmail(userId, emailId);
    }

    @PutMapping("/restore")
    public Email restoreEmail( @RequestParam int userId, @RequestParam int emailId) {
        return emailService.restoreEmail(userId, emailId);
    }

}
