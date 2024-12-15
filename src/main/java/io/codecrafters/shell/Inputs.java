package io.codecrafters.shell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class Inputs implements Iterator<Input> {

    private final Iterator<Token> tokenIterator;
    private Input next;

    Inputs(Iterator<Token> tokenIterator) {
        this.tokenIterator = tokenIterator;
    }

    @Override
    public boolean hasNext() {
        if (next == null) {
            var buffer = new ArrayList<String>();
            while (tokenIterator.hasNext()) {
                var token = tokenIterator.next();
                if (token instanceof LineBreak) {
                    break;
                }
                if (token instanceof Literal(String value)) {
                    buffer.add(value);
                }
            }
            if (buffer.isEmpty()) {
                return false;
            }
            next = new Input(buffer.getFirst(), buffer.subList(1, buffer.size()));
            return true;
        }
        return true;
    }

    @Override
    public Input next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        var result = next;
        next = null;
        return result;
    }
}
