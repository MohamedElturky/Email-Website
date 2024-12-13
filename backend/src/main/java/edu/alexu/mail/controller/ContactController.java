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

    @GetMapping("/contacts")
    public List<Contact> getAllContacts() {
        return contactService.getAllContacts();
    }

    @GetMapping("/contacts/sorted")
    public List<Contact> getSortedContacts() {
        return contactService.getContactsSortedLexicographicallyByName();
    }

    @GetMapping("/contacts/name")
    public List<Contact> getContactsByName(@RequestParam String name) {
        return contactService.getContactsByName(name);
    }

    @GetMapping("/contacts/email-addresses")
    public List<Contact> getContactsByEmailAddresses(
            @RequestParam List<String> emailAddresses) {
        return contactService.getContactsByEmailAddresses(emailAddresses);
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
    public Contact addContactEmailAddress(int id, String emailAddress) {
        return contactService.addContactEmailAddress(id, emailAddress);
    }

    @PutMapping("/remove-email-address")
    public Contact deleteContactEmailAddress(int id, String emailAddress) {
        return contactService.deleteContactEmailAddress(id, emailAddress);
    }

    @DeleteMapping
    public void removeContact(int id) {
        contactService.removeContact(id);
    }
}
