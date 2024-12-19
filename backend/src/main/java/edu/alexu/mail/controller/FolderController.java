package edu.alexu.mail.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "folders")
public class Folder {

    public static final List<String> defaultFolders = Arrays.asList("Inbox", "Trash", "Draft", "Sent");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;

    private String label;

    @Column(columnDefinition = "BLOB")
    private List<Integer> emailsIds;

    public Folder() {
        this.emailsIds = new ArrayList<>();
    }

    public Folder(String label, int userId) {
        this();
        this.label = label;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getLabel() {
        return label;
    }

    public List<Integer> getEmailsIds() {
        return emailsIds;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEmailsIds(List<Integer> emailsIds) {
        this.emailsIds = emailsIds;
    }

    public void empty() {
        emailsIds.clear();
    }
}
