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
import java.util.function.Function;

public final class InputBufferingIterator implements Iterator<Character> {

    private final Iterator<Character> original;
    private final PrintStream output;
    private final Path path;
    private final Function<String, Optional<String>> onTab;
    private final StringBuilder buffer = new StringBuilder();
    private Iterator<Character> sink = Collections.emptyIterator();

    public InputBufferingIterator(
        Iterator<Character> original,
        PrintStream output,
        Path path,
        Function<String, Optional<String>> onTab
    ) {
        this.original = original;
        this.output = output;
        this.path = path;
        this.onTab = onTab;
    }

    @Override
    public boolean hasNext() {
        if (!sink.hasNext()) {
            readNextCharacters();
        }
        return sink.hasNext();
    }

    @Override
    public Character next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return sink.next();
    }

    private void readNextCharacters() {
        while (original.hasNext()) {
            var next = original.next();
            switch (next) {
                case '\t' -> lastToken().flatMap(onTab).ifPresent(this::buffer);
                default -> buffer(next);
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
        sink = new CharacterIterator(new StringReader(buffer.toString()));
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
