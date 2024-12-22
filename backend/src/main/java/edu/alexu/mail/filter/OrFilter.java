package edu.alexu.mail.filter;

import java.util.List;

public class OrFilter<T> implements Filter<T> {
    private final Filter<T> filter;
    private final Filter<T> otherFilter;

    public OrFilter(Filter<T> filter, Filter<T> otherFilter) {
        this.filter = filter;
        this.otherFilter = otherFilter;
    }

    @Override
    public List<T> apply(List<T> items) {
        List<T> firstResult = otherFilter.apply(items);
        firstResult.addAll(filter.apply(items));
        return firstResult;
    }
}
