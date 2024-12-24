package io.codecrafters.shell.iterator.token;

import java.nio.file.Path;
import java.util.Optional;

final class ReadingToken implements State {

    private final StringBuilder builder;

    ReadingToken(StringBuilder builder) {
        this.builder = builder;
    }

    ReadingToken() {
        this(new StringBuilder());
    }

    @Override
    public Transition onWhitespace(char whitespace) {
        var transition = new ReadingWhiteSpaces().onWhitespace(whitespace);
        return new Transition(
            transition.next(),
            new Found(new Literal(builder)).combined(transition.result())
        );
    }

    @Override
    public Transition onSingleQuote() {
        return new Transition(new ReadingSingleQuotedToken(builder));
    }

    @Override
    public Transition onDoubleQuote() {
        return new Transition(new ReadingDoubleQuotedToken(builder));
    }

    @Override
    public Transition onBackslash() {
        return new Transition(new ReadingLiteralCharacterValue(builder, ReadingToken::new));
    }

    @Override
    public Transition onRedirectionOperator() {
        return switch (builder.toString()) {
            case "1" -> new Transition(ReadingAppendingOperator.forOutput());
            case "2" -> new Transition(ReadingAppendingOperator.forError());
            default -> new Transition(ReadingAppendingOperator.forOutput(), new Literal(builder));
        };
    }

    @Override
    public Transition onTilda(Path path) {
        builder.append(path.toString());
        return new Transition(this);
    }

    @Override
    public Transition onCharacter(char character) {
        builder.append(character);
        return new Transition(this);
    }

    @Override
    public Optional<Token> onEnd() {
        return Optional.of(new Literal(builder));
    }
}
