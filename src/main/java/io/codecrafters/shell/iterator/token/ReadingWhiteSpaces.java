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
        return transition(ReadingAppendingOperator.forOutput());
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
        return new Transition(
            transition.next(),
            token()
                .<Result>map(Found::new)
                .orElseGet(Continue::new)
                .combined(transition.result())
        );
    }

    private Optional<Token> token() {
        if (builder.indexOf(System.lineSeparator()) == -1) {
            return Optional.empty();
        }
        builder.setLength(0);
        return Optional.of(SimpleToken.LINE_BREAK);
    }
}
