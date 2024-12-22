package edu.alexu.mail.filter;

import edu.alexu.mail.filter.connective.AndFilter;
import edu.alexu.mail.filter.connective.NotFilter;
import edu.alexu.mail.filter.connective.OrFilter;
import org.springframework.stereotype.Service;

import java.util.Stack;

@Service
public class FilterBuilder <T> {

    private final Stack<Filter<T>> buildStack;

    public FilterBuilder() {
        buildStack = new Stack<>();
    }

    public FilterBuilder<T> addFilter(Filter<T> filter) {
        buildStack.push(filter);
        return this;
    }

    public FilterBuilder<T> or(Filter<T> filter) {
        buildStack.push(new OrFilter<>(buildStack.pop(), filter));
        return this;
    }

    public FilterBuilder<T> and(Filter<T> filter) {
        buildStack.push(new AndFilter<>(buildStack.pop(), filter));
        return this;
    }

    public FilterBuilder<T> not(Filter<T> filter) {
        buildStack.push(new NotFilter<>(filter));
        return this;
    }

    public FilterBuilder<T> or() {
        buildStack.push(new OrFilter<>(buildStack.pop(), buildStack.pop()));
        return this;
    }

    public FilterBuilder<T> and() {
        buildStack.push(new AndFilter<>(buildStack.pop(), buildStack.pop()));
        return this;
    }

    public FilterBuilder<T> not() {
        buildStack.push(new NotFilter<>(buildStack.pop()));
        return this;
    }

    public Filter<T> build() {
        Filter<T> filter = buildStack.pop();
        buildStack.clear();
        return filter;
    }
}
