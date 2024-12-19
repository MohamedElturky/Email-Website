package edu.alexu.mail.controller;

import edu.alexu.mail.model.Contact;
import edu.alexu.mail.service.ContactService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/all")
    public List<Contact> getAllContacts(@RequestParam int userId) {
        return contactService.getAllContacts(userId);
    }

    @GetMapping("/all/sorted")
    public List<Contact> getAllContactsSortedLexicographicallyByName(@RequestParam int userId) {
        return contactService.getContactsSortedLexicographicallyByName(userId);
    }

    @GetMapping("/all/name")
    public List<Contact> getAllContactsByName(@RequestParam int userId, @RequestParam String name) {
        return contactService.getContactsByName(userId, name);
    }

    @GetMapping("/all/email-addresses")
    public List<Contact> getAllContactsByEmailAddresses(
            @RequestParam int userId,
            @RequestParam List<String> emailAddress) {
        return contactService.getContactsByEmailAddresses(userId, emailAddress);
    }

    @GetMapping
    public Contact getContact(@RequestParam int id) {
        return contactService.getContactById(id);
    }

    @PostMapping
    public Contact addContact(@RequestBody Contact contact) {
        return contactService.addContact(contact);
    }

    @PutMapping("/rename")
    public Contact renameContact(@RequestParam int id, @RequestParam String name) {
        return contactService.renameContact(id, name);
    }

    @PutMapping("/update-email-addresses")
    public Contact updateEmailAddresses(@RequestParam int id,
                                        @RequestBody List<String> emailAddresses) {
        return contactService.updateEmailAddresses(id, emailAddresses);
    }

    @DeleteMapping
    public void deleteContact(@RequestParam int id) {
        contactService.deleteContact(id);
    }
}
