package io.codecrafters.shell.iterator.buffer;

import io.codecrafters.shell.iterator.token.Token;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class InputBufferingIterator implements Iterator<Token> {

    private final Iterator<Character> original;
    private final InputBuffer inputBuffer;
    private Iterator<Token> delegate = Collections.emptyIterator();

    public InputBufferingIterator(Iterator<Character> original, InputBuffer inputBuffer) {
        this.original = original;
        this.inputBuffer = inputBuffer;
    }

    @Override
    public boolean hasNext() {
        if (!delegate.hasNext()) {
            readNextCharacters();
        }
        return delegate.hasNext();
    }

    @Override
    public Token next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return delegate.next();
    }

    private void readNextCharacters() {
        while (original.hasNext()) {
            var next = original.next();
            if (next == '\t') {
                inputBuffer.autocomplete();
            } else {
                inputBuffer.offer(next);
            }
            if (inputBuffer.endsWith(System.lineSeparator())) {
                flush();
                return;
            }
        }
        flush();
    }

    private void flush() {
        delegate = inputBuffer.tokens();
        inputBuffer.clear();
    }
}
