package io.codecrafters.shell;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class Tokens implements Iterator<Token> {

    private final StringBuilder spaceBuffer = new StringBuilder();
    private final Reader reader;
    private Token next;

    Tokens(Reader reader) {
        this.reader = reader;
    }

    @Override
    public boolean hasNext() {
        if (next == null) {
            if (spaceBuffer.indexOf(System.lineSeparator()) != -1) {
                next = new LineBreak();
                spaceBuffer.setLength(0);
                return true;
            }
            try {
                for (var nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
                    if (Character.isWhitespace(nextChar)) {
                        spaceBuffer.append(nextChar);
                        if (spaceBuffer.indexOf(System.lineSeparator()) != -1) {
                            next = new LineBreak();
                            spaceBuffer.setLength(0);
                            return true;
                        }
                    } else if (nextChar == '\'') {
                        readUntilSingleQuote();
                        return true;
                    } else {
                        readNormal((char) nextChar);
                        return true;
                    }
                }
                return false;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return true;
    }

    @Override
    public Token next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        var result = next;
        next = null;
        return result;
    }

    private void readNormal(char first) throws IOException {
        var builder = new StringBuilder().append(first);
        for (var nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
            if (Character.isWhitespace(nextChar)) {
                spaceBuffer.append((char) nextChar);
                break;
            }
            builder.append((char) nextChar);
        }
        next = new Literal(builder.toString());
    }

    private void readUntilSingleQuote() throws IOException {
        var builder = new StringBuilder();
        for (var nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
            if (nextChar == '\'') {
                break;
            }
            builder.append((char) nextChar);
        }
        next = new Literal(builder.toString());
    }
}
