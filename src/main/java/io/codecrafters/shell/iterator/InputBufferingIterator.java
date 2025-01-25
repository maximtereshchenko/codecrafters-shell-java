package io.codecrafters.shell.iterator;

import io.codecrafters.shell.iterator.token.Literal;
import io.codecrafters.shell.iterator.token.TokenIterator;

import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class InputBufferingIterator implements Iterator<Character> {

    private final Iterator<Character> original;
    private final PrintStream output;
    private final Path path;
    private final UnaryOperator<String> onTab;
    private final StringBuilder buffer = new StringBuilder();
    private Iterator<Character> delegate = Collections.emptyIterator();

    public InputBufferingIterator(
        Iterator<Character> original,
        PrintStream output,
        Path path,
        UnaryOperator<String> onTab
    ) {
        this.original = original;
        this.output = output;
        this.path = path;
        this.onTab = onTab;
    }

    @Override
    public boolean hasNext() {
        if (!delegate.hasNext()) {
            readNextCharacters();
        }
        return delegate.hasNext();
    }

    @Override
    public Character next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return delegate.next();
    }

    private void readNextCharacters() {
        while (original.hasNext()) {
            var next = original.next();
            if (next == '\t') {
                lastToken().map(onTab).ifPresent(this::buffer);
            } else {
                buffer(next);
            }
            if (buffer.toString().endsWith(System.lineSeparator())) {
                flush();
                return;
            }
        }
        flush();
    }

    private void buffer(String chars) {
        for (var i = 0; i < chars.length(); i++) {
            buffer(chars.charAt(i));
        }
    }

    private void buffer(char next) {
        buffer.append(next);
        output.print(next);
    }

    private void flush() {
        delegate = new CharacterIterator(new StringReader(buffer.toString()));
        buffer.setLength(0);
    }

    private Optional<String> lastToken() {
        var iterator = new TokenIterator(path, new CharacterIterator(new StringReader(buffer.toString())));
        var last = Optional.<String>empty();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof Literal(var value)) {
                last = Optional.of(value);
            }
        }
        return last;
    }
}
