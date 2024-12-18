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
    public List<Contact> getAllContacts(int userId) {
        return contactService.getAllContacts(userId);
    }

    @GetMapping("/all/sorted")
    public List<Contact> getAllContactsSortedLexicographicallyByName(int userId) {
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
    public Contact getContact(int id) {
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

    @PutMapping("/add-email-address")
    public Contact addEmailAddressToContact(int id, String emailAddress) {
        return contactService.addEmailAddressToContact(id, emailAddress);
    }

    @PutMapping("/delete-email-address")
    public Contact deleteEmailAddressFromContact(int id, String emailAddress) {
        return contactService.deleteEmailAddressFromContact(id, emailAddress);
    }

    @DeleteMapping
    public void deleteContact(int id) {
        contactService.deleteContact(id);
    }
}
