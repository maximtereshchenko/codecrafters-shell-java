package io.codecrafters.shell.iterator.token;

import java.nio.file.Path;
import java.util.Optional;

final class Final implements State {

    @Override
    public Transition onWhitespace(char whitespace) {
        return onSingleQuote();
    }

    @Override
    public Transition onSingleQuote() {
        throw new IllegalStateException();
    }

    @Override
    public Transition onDoubleQuote() {
        return onSingleQuote();
    }

    @Override
    public Transition onBackslash() {
        return onSingleQuote();
    }

    @Override
    public Transition onRedirectionOperator() {
        return onSingleQuote();
    }

    @Override
    public Transition onTilda(Path path) {
        return onSingleQuote();
    }

    @Override
    public Transition onCharacter(char character) {
        return onSingleQuote();
    }

    @Override
    public Optional<Token> onEnd() {
        return Optional.empty();
    }
}
