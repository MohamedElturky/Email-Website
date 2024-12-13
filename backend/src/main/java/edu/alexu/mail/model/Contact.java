package edu.alexu.mail.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "contact_name")
    private String name;

    private List<String> emailAddresses;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmailAddresses(List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }
}
