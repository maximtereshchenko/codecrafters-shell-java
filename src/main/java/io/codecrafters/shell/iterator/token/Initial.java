package io.codecrafters.shell.iterator.token;

import java.nio.file.Path;
import java.util.Optional;

final class Initial implements State {

    @Override
    public Transition onWhitespace(char whitespace) {
        return new ReadingWhiteSpaces().onWhitespace(whitespace);
    }

    @Override
    public Transition onSingleQuote() {
        return new Transition(new ReadingSingleQuotedToken());
    }

    @Override
    public Transition onDoubleQuote() {
        return new Transition(new ReadingDoubleQuotedToken());
    }

    @Override
    public Transition onBackslash() {
        return new Transition(new ReadingLiteralCharacterValue(ReadingToken::new));
    }

    @Override
    public Transition onRedirectionOperator() {
        return new Transition(new ReadingAppendingOperator(Redirection.Source.OUTPUT));
    }

    @Override
    public Transition onTilda(Path path) {
        return new ReadingToken().onTilda(path);
    }

    @Override
    public Transition onCharacter(char character) {
        return new ReadingToken().onCharacter(character);
    }

    @Override
    public Optional<Token> onEnd() {
        return Optional.empty();
    }
}
