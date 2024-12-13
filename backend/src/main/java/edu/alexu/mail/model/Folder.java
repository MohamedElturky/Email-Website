package edu.alexu.mail.model;

import jakarta.persistence.*;

@Entity
@Table(name = "folders")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String label;

    int parentFolderId;

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getParentFolderId() {
        return parentFolderId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setParentFolderId(int parentFolderId) {
        this.parentFolderId = parentFolderId;
    }
}
