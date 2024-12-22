package edu.alexu.mail.filter.connective;

import edu.alexu.mail.filter.Filter;

import java.util.ArrayList;
import java.util.List;

public class NotFilter<T> implements Filter<T> {
    private final Filter<T> filter;

    public NotFilter(Filter<T> filter) {
        this.filter = filter;
    }

    @Override
    public List<T> apply(List<T> items) {
        List<T> original = new ArrayList<>(items);
        List<T> filtered = filter.apply(items);
        original.removeAll(filtered);
        return original;
    }
}
