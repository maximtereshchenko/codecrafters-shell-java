package io.codecrafters.shell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

final class Inputs extends CachingIterator<Input> {

    private final Iterator<Token> tokenIterator;

    Inputs(Iterator<Token> tokenIterator) {
        this.tokenIterator = tokenIterator;
    }

    @Override
    Optional<Input> nextElement() {
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
            return Optional.empty();
        }
        return Optional.of(new Input(buffer.getFirst(), buffer.subList(1, buffer.size())));
    }
}
