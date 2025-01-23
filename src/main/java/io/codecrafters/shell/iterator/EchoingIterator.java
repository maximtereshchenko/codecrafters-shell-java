package io.codecrafters.shell.iterator;

import java.io.PrintStream;
import java.util.Iterator;

public final class EchoingIterator implements Iterator<Character> {

    private final Iterator<Character> original;
    private final PrintStream output;

    public EchoingIterator(Iterator<Character> original, PrintStream output) {
        this.original = original;
        this.output = output;
    }

    @Override
    public boolean hasNext() {
        return original.hasNext();
    }

    @Override
    public Character next() {
        var next = original.next();
        output.print(next);
        return next;
    }
}
