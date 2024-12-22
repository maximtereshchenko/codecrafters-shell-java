package io.codecrafters.shell.iterator.token;

import java.util.ArrayList;
import java.util.List;
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
        return new Transition(new ReadingWhiteSpaces(), new Found(tokens()));
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

    private List<Token> tokens() {
        var tokes = new ArrayList<Token>();
        if (!builder.toString().equals("1")) {
            tokes.add(new Literal(builder));
        }
        tokes.add(new RedirectionOperator());
        return tokes;
    }
}
