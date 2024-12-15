package io.codecrafters.shell;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Optional;

final class Characters extends CachingIterator<Character> {

    private final Reader reader;

    Characters(Reader reader) {
        this.reader = reader;
    }

    @Override
    Optional<Character> nextElement() {
        try {
            var next = reader.read();
            if (next == -1) {
                return Optional.empty();
            }
            return Optional.of((char) next);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
