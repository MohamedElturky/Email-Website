package edu.alexu.mail.model;

public enum FolderType {
    INBOX("Inbox"),
    SENT("Sent"),
    DRAFT("Draft"),
    TRASH("Trash"),;

    private final String stringRepresentation;

    FolderType(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @Override
    public String toString() {
        return this.stringRepresentation;
    }
}
