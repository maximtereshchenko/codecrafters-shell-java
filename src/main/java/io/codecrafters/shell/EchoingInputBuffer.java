package io.codecrafters.shell;

import io.codecrafters.shell.iterator.buffer.InputBuffer;
import io.codecrafters.shell.iterator.token.Token;

import java.io.PrintStream;
import java.util.Iterator;

final class EchoingInputBuffer implements InputBuffer {

    private final InputBuffer original;
    private final PrintStream output;

    EchoingInputBuffer(InputBuffer original, PrintStream output) {
        this.original = original;
        this.output = output;
    }

    @Override
    public String autocomplete() {
        var completed = original.autocomplete();
        output.print(completed);
        return completed;
    }

    @Override
    public void offer(char character) {
        original.offer(character);
        output.print(character);
    }

    @Override
    public boolean endsWith(String suffix) {
        return original.endsWith(suffix);
    }

    @Override
    public Iterator<Token> tokens() {
        return original.tokens();
    }

    @Override
    public void clear() {
        original.clear();
    }
}
