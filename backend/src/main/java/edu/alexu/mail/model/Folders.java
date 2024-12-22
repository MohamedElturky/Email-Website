package edu.alexu.mail.model;

public enum Folders {
    INBOX("Inbox"),
    SENT("Sent"),
    DRAFT("Draft"),
    TRASH("Trash"),;

    private final String stringRepresentation;

    Folders(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String getStringRepresentation() {
        return this.stringRepresentation;
    }
}
