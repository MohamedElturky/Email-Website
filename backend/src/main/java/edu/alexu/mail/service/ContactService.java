package edu.alexu.mail.service;

import edu.alexu.mail.model.Contact;
import edu.alexu.mail.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> getAllContacts(int userId) {
        return contactRepository.findAllByUserId(userId);
    }

    public List<Contact> getContactsSortedLexicographicallyByName(int userId) {
        List<Contact> contacts = contactRepository.findAllByUserId(userId);
        contacts.sort(Comparator.comparing(Contact::getName));
        return contacts;
    }

    public List<Contact> getContactsByName(int userId, String name) {
        return contactRepository
                .findAllByUserId(userId)
                .stream()
                .filter(contact -> contact.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public List<Contact> getContactsByEmailAddresses(int userId, List<String> emailAddresses) {
        return contactRepository
                .findAllByUserId(userId)
                .stream()
                .filter(contact -> !Collections.disjoint(
                        contact.getEmailAddresses(), emailAddresses))
                .collect(Collectors.toList());
    }

    public Contact getContactById(int id) {
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact != null) {
            return contact;
        }
        else throw new RuntimeException("Contact not found.");
    }

    public Contact addContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public void deleteContact(int id) {
        contactRepository.deleteById(id);
    }

    public Contact renameContact(int id, String newName) {
        Contact contact = contactRepository.findById(id).orElse(null);

        if (contact != null) {
            contact.setName(newName);
            return contactRepository.save(contact);
        }
        else {
            throw new RuntimeException("Contact not found.");
        }
    }

    public Contact updateEmailAddresses(int id, List<String> emailAddresses) {
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact != null) {
            contact.setEmailAddresses(emailAddresses);
            return contactRepository.save(contact);
        }
        else {
            throw new RuntimeException("Contact not found.");
        }
    }
}
