package io.codecrafters.shell.iterator.token;

import java.nio.file.Path;
import java.util.Optional;

final class ReadingWhiteSpaces implements State {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public Transition onWhitespace(char whitespace) {
        builder.append(whitespace);
        return transition(this);
    }

    @Override
    public Transition onSingleQuote() {
        return transition(new ReadingSingleQuotedToken());
    }

    @Override
    public Transition onDoubleQuote() {
        return transition(new ReadingDoubleQuotedToken());
    }

    @Override
    public Transition onBackslash() {
        return transition(new ReadingLiteralCharacterValue(ReadingToken::new));
    }

    @Override
    public Transition onRedirectionOperator() {
        return transition(new ReadingAppendingOperator(Redirection.Source.OUTPUT));
    }

    @Override
    public Transition onTilda(Path path) {
        return combined(new ReadingToken().onTilda(path));
    }

    @Override
    public Transition onCharacter(char character) {
        return combined(new ReadingToken().onCharacter(character));
    }

    @Override
    public Optional<Token> onEnd() {
        return token();
    }

    private Transition transition(State next) {
        return combined(new Transition(next));
    }

    private Transition combined(Transition transition) {
        return token().map(transition::withPrecedingToken).orElse(transition);
    }

    private Optional<Token> token() {
        if (builder.indexOf(System.lineSeparator()) == -1) {
            return Optional.empty();
        }
        builder.setLength(0);
        return Optional.of(new LineBreak());
    }
}
