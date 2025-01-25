package io.codecrafters.shell;

import io.codecrafters.shell.iterator.CharacterIterator;
import io.codecrafters.shell.iterator.buffer.InputBuffer;
import io.codecrafters.shell.iterator.token.Literal;
import io.codecrafters.shell.iterator.token.Token;
import io.codecrafters.shell.iterator.token.TokenIterator;

import java.io.StringReader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;

final class StringBuilderInputBuffer implements InputBuffer {

    private final Path path;
    private final Autocomplete autocomplete;
    private final StringBuilder builder = new StringBuilder();

    StringBuilderInputBuffer(Path path, Autocomplete autocomplete) {
        this.path = path;
        this.autocomplete = autocomplete;
    }

    @Override
    public String autocomplete() {
        var completed = lastToken()
            .map(autocomplete::complete)
            .orElse("");
        builder.append(completed);
        return completed;
    }

    @Override
    public void offer(char character) {
        builder.append(character);
    }

    @Override
    public boolean endsWith(String suffix) {
        return builder.toString().endsWith(suffix);
    }

    @Override
    public Iterator<Token> tokens() {
        return new TokenIterator(path, new CharacterIterator(new StringReader(builder.toString())));
    }

    @Override
    public void clear() {
        builder.setLength(0);
    }

    private Optional<String> lastToken() {
        var iterator = tokens();
        var last = Optional.<String>empty();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof Literal(var value)) {
                last = Optional.of(value);
            }
        }
        return last;
    }
}
