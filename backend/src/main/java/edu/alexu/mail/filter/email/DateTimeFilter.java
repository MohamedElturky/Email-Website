package edu.alexu.mail.filter.email;

import edu.alexu.mail.filter.Filter;
import edu.alexu.mail.model.Email;

import java.time.LocalDateTime;

public abstract class DateTimeFilter implements Filter<Email> {

    protected LocalDateTime dateTime;

    public DateTimeFilter(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
