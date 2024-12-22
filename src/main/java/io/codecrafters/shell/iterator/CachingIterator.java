package io.codecrafters.shell.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

public abstract class CachingIterator<T> implements Iterator<T> {

    private T next;

    @Override
    public final boolean hasNext() {
        if (next == null) {
            nextElement().ifPresent(element -> next = element);
        }
        return next != null;
    }

    @Override
    public final T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        var result = next;
        next = null;
        return result;
    }

    protected abstract Optional<T> nextElement();
}
