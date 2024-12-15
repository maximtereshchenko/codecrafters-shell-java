package io.codecrafters.shell;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Optional;

final class Tokens extends CachingIterator<Token> {

    private final StringBuilder spaceBuffer = new StringBuilder();
    private final Reader reader;

    Tokens(Reader reader) {
        this.reader = reader;
    }

    @Override
    Optional<Token> nextElement() {
        if (spaceBuffer.indexOf(System.lineSeparator()) != -1) {
            spaceBuffer.setLength(0);
            return Optional.of(new LineBreak());
        }
        try {
            for (var nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
                if (Character.isWhitespace(nextChar)) {
                    spaceBuffer.append(nextChar);
                    if (spaceBuffer.indexOf(System.lineSeparator()) != -1) {
                        spaceBuffer.setLength(0);
                        return Optional.of(new LineBreak());
                    }
                } else if (nextChar == '\'') {
                    return Optional.of(readUntilSingleQuote());
                } else {
                    return Optional.of(readNormal((char) nextChar));
                }
            }
            return Optional.empty();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Literal readNormal(char first) throws IOException {
        var builder = new StringBuilder().append(first);
        for (var nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
            if (Character.isWhitespace(nextChar)) {
                spaceBuffer.append((char) nextChar);
                break;
            }
            builder.append((char) nextChar);
        }
        return new Literal(builder.toString());
    }

    private Literal readUntilSingleQuote() throws IOException {
        var builder = new StringBuilder();
        for (var nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
            if (nextChar == '\'') {
                break;
            }
            builder.append((char) nextChar);
        }
        return new Literal(builder.toString());
    }
}
