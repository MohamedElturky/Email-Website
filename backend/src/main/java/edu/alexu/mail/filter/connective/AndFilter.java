package edu.alexu.mail.filter.connective;

import edu.alexu.mail.filter.Filter;

import java.util.List;

public class AndFilter<T> implements Filter<T> {

    private final Filter<T> filter;
    private final Filter<T> anotherFilter;

    public AndFilter(Filter<T> filter, Filter<T> anotherFilter) {
        this.filter = filter;
        this.anotherFilter = anotherFilter;
    }

    @Override
    public List<T> apply(List<T> items) {
        List<T> firstResult = filter.apply(items);
        return anotherFilter.apply(firstResult);
    }
}
