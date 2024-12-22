package edu.alexu.mail.filter;

import edu.alexu.mail.filter.connective.AndFilter;
import edu.alexu.mail.filter.connective.ConnectiveType;
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

    public FilterBuilder<T> addConnectiveFilter(ConnectiveType connectiveType) {
        switch (connectiveType) {
            case AND -> {
                Filter<T> filter1 = buildStack.pop();
                Filter<T> filter2 = buildStack.pop();
                buildStack.push(new AndFilter<>(filter1, filter2));
            }
            case OR -> {
                Filter<T> filter1 = buildStack.pop();
                Filter<T> filter2 = buildStack.pop();
                buildStack.push(new OrFilter<>(filter1, filter2));
            }
            case NOT -> {
                Filter<T> filter1 = buildStack.pop();
                buildStack.push(new NotFilter<>(filter1));
            }
        }
        return this;
    }

    public Filter<T> build() {
        Filter<T> filter = buildStack.pop();
        buildStack.clear();
        return filter;
    }
}
