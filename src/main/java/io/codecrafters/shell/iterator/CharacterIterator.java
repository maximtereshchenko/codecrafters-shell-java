package io.codecrafters.shell.iterator;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Optional;

public final class CharacterIterator extends CachingIterator<Character> {

    private final Reader reader;

    public CharacterIterator(Reader reader) {
        this.reader = reader;
    }

    @Override
    protected Optional<Character> nextElement() {
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
