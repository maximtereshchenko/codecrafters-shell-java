package io.codecrafters.shell;

import java.util.Iterator;
import java.util.Optional;

final class Tokens extends CachingIterator<Token> {

    private final Iterator<Character> characterIterator;
    private State state = new Initial();

    Tokens(Iterator<Character> characterIterator) {
        this.characterIterator = characterIterator;
    }

    @Override
    Optional<Token> nextElement() {
        while (characterIterator.hasNext()) {
            var next = characterIterator.next();
            var transition = transition(next);
            state = transition.next();
            if (transition.result() instanceof Found(Token token)) {
                return Optional.of(token);
            }
        }
        var token = state.onEnd();
        state = new Final();
        return token;
    }

    private Transition transition(char next) {
        if (Character.isWhitespace(next)) {
            return state.onWhitespace(next);
        }
        if (next == '\'') {
            return state.onSingleQuote();
        }
        return state.onCharacter(next);
    }

    private sealed interface Result {}

    private interface State {

        Transition onWhitespace(char whitespace);

        Transition onSingleQuote();

        Transition onCharacter(char character);

        Optional<Token> onEnd();
    }

    private static final class Initial implements State {

        @Override
        public Transition onWhitespace(char whitespace) {
            return new Transition(new ReadingWhiteSpaces(whitespace));
        }

        @Override
        public Transition onSingleQuote() {
            return new Transition(new ReadingSingleQuotedToken());
        }

        @Override
        public Transition onCharacter(char character) {
            return new Transition(new ReadingToken(character));
        }

        @Override
        public Optional<Token> onEnd() {
            return Optional.empty();
        }
    }

    private static final class Final implements State {

        @Override
        public Transition onWhitespace(char whitespace) {
            throw new IllegalStateException();
        }

        @Override
        public Transition onSingleQuote() {
            throw new IllegalStateException();
        }

        @Override
        public Transition onCharacter(char character) {
            throw new IllegalStateException();
        }

        @Override
        public Optional<Token> onEnd() {
            return Optional.empty();
        }
    }

    private static final class ReadingToken implements State {

        private final StringBuilder builder = new StringBuilder();

        ReadingToken(char initial) {
            builder.append(initial);
        }

        @Override
        public Transition onWhitespace(char whitespace) {
            return new Transition(
                new ReadingWhiteSpaces(whitespace),
                new Literal(builder)
            );
        }

        @Override
        public Transition onSingleQuote() {
            return new Transition(new ReadingSingleQuotedToken(builder));
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

    private static final class ReadingSingleQuotedToken implements State {

        private final StringBuilder builder;

        ReadingSingleQuotedToken(StringBuilder builder) {
            this.builder = builder;
        }

        ReadingSingleQuotedToken() {
            this(new StringBuilder());
        }

        @Override
        public Transition onWhitespace(char whitespace) {
            builder.append(whitespace);
            return new Transition(this);
        }

        @Override
        public Transition onSingleQuote() {
            return new Transition(
                new ReadingWhiteSpaces(),
                new Literal(builder)
            );
        }

        @Override
        public Transition onCharacter(char character) {
            builder.append(character);
            return new Transition(this);
        }

        @Override
        public Optional<Token> onEnd() {
            throw new IllegalStateException();
        }
    }

    private static final class ReadingWhiteSpaces implements State {

        private final StringBuilder builder = new StringBuilder();

        ReadingWhiteSpaces() {}

        ReadingWhiteSpaces(char initial) {
            builder.append(initial);
        }

        @Override
        public Transition onWhitespace(char whitespace) {
            builder.append(whitespace);
            return transition(this);
        }

        @Override
        public Transition onSingleQuote() {
            return transition(new ReadingSingleQuotedToken(new StringBuilder()));
        }

        @Override
        public Transition onCharacter(char character) {
            return transition(new ReadingToken(character));
        }

        @Override
        public Optional<Token> onEnd() {
            return token();
        }

        private Transition transition(State next) {
            return new Transition(
                next,
                token()
                    .<Result>map(Found::new)
                    .orElseGet(Continue::new)
            );
        }

        private Optional<Token> token() {
            if (builder.indexOf(System.lineSeparator()) == -1) {
                return Optional.empty();
            }
            return Optional.of(new LineBreak());
        }
    }

    private record Transition(State next, Result result) {

        Transition(State next) {
            this(next, new Continue());
        }

        Transition(State next, Token token) {
            this(next, new Found(token));
        }
    }

    private static final class Continue implements Result {}

    private record Found(Token token) implements Result {}
}
