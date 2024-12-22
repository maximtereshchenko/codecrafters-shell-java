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
        if (!tokenIterator.hasNext()) {
            return Optional.empty();
        }
        var buffer = new ArrayList<String>();
        while (tokenIterator.hasNext()) {
            var token = tokenIterator.next();
            if (token instanceof LineBreak) {
                break;
            }
            if (token instanceof Literal(CharSequence value)) {
                buffer.add(value.toString());
            }
        }
        if (buffer.isEmpty()) {
            return nextElement();
        }
        return Optional.of(new Input(buffer.getFirst(), buffer.subList(1, buffer.size()), Optional.empty()));
    }
}
