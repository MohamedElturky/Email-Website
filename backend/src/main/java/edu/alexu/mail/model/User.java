package edu.alexu.mail.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String emailAddress;

    @Column(columnDefinition = "TEXT")
    private String hashedPassword;

    public int getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
