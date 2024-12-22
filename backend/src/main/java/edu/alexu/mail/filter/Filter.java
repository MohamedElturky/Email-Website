package edu.alexu.mail.filter;

import java.util.List;

public interface Filter<T> {
    List<T> apply(List<T> items);
}
