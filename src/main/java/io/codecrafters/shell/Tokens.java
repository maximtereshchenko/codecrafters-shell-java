package io.codecrafters.shell;

import java.util.Iterator;
import java.util.Optional;

final class Tokens extends CachingIterator<Token> {

    private final StringBuilder spaceBuffer = new StringBuilder();
    private final Iterator<Character> characterIterator;

    Tokens(Iterator<Character> characterIterator) {
        this.characterIterator = characterIterator;
    }

    @Override
    Optional<Token> nextElement() {
        if (spaceBuffer.indexOf(System.lineSeparator()) != -1) {
            spaceBuffer.setLength(0);
            return Optional.of(new LineBreak());
        }
        while (characterIterator.hasNext()) {
            var next = characterIterator.next();
            if (Character.isWhitespace(next)) {
                spaceBuffer.append(next);
                if (spaceBuffer.indexOf(System.lineSeparator()) != -1) {
                    spaceBuffer.setLength(0);
                    return Optional.of(new LineBreak());
                }
            } else if (next == '\'') {
                return Optional.of(readUntilSingleQuote());
            } else {
                return Optional.of(readNormal(next));
            }
        }
        return Optional.empty();
    }

    private Literal readNormal(char first) {
        var builder = new StringBuilder().append(first);
        while (characterIterator.hasNext()) {
            var next = characterIterator.next();
            if (Character.isWhitespace(next)) {
                spaceBuffer.append(next);
                break;
            }
            builder.append(next);
        }
        return new Literal(builder.toString());
    }

    private Literal readUntilSingleQuote() {
        var builder = new StringBuilder();
        while (characterIterator.hasNext()) {
            var next = characterIterator.next();
            if (next == '\'') {
                break;
            }
            builder.append(next);
        }
        return new Literal(builder.toString());
    }
}
