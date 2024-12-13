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

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public List<Contact> getContactsSortedLexicographicallyByName() {
        List<Contact> contacts = contactRepository.findAll();
        contacts.sort(Comparator.comparing(Contact::getName));
        return contacts;
    }

    public List<Contact> getContactsByName(String name) {
        return contactRepository
                .findAll()
                .stream()
                .filter(contact -> contact.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public List<Contact> getContactsByEmailAddresses(List<String> emailAddresses) {
        return contactRepository
                .findAll()
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
        else throw new RuntimeException("Contact not found");
    }

    public Contact addContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public void removeContact(int id) {
        contactRepository.deleteById(id);
    }

    public Contact renameContact(int id, String newName) {
        Contact contact = contactRepository.findById(id).orElse(null);

        if (contact != null) {
            contact.setName(newName);
            return contactRepository.save(contact);
        }
        else {
            throw new RuntimeException("Contact not found");
        }
    }

    public Contact addContactEmailAddress(int id, String emailAddress) {
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact != null) {
            contact.getEmailAddresses().add(emailAddress);
            return contactRepository.save(contact);
        }
        else {
            throw new RuntimeException("Contact not found");
        }
    }

    public Contact deleteContactEmailAddress(int id, String emailAddress) {
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact != null) {
            contact.getEmailAddresses().remove(emailAddress);
            return contactRepository.save(contact);
        }
        else {
            throw new RuntimeException("Contact not found");
        }
    }
}
