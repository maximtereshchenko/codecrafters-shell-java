package io.codecrafters.shell.iterator;

import java.io.PrintStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class InputBufferingIterator implements Iterator<Character> {

    private final Iterator<Character> original;
    private final PrintStream output;
    private final StringBuilder buffer = new StringBuilder();
    private Iterator<Character> sink = Collections.emptyIterator();

    public InputBufferingIterator(Iterator<Character> original, PrintStream output) {
        this.original = original;
        this.output = output;
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
            buffer(original.next());
            if (buffer.toString().endsWith(System.lineSeparator())) {
                flush();
                return;
            }
        }
        flush();
    }

    private void buffer(char next) {
        buffer.append(next);
        output.print(next);
    }

    private void flush() {
        sink = new CharacterIterator(new StringReader(buffer.toString()));
        buffer.setLength(0);
    }
}
