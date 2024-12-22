package io.codecrafters.shell.iterator.token;

import io.codecrafters.shell.iterator.CachingIterator;

import java.util.*;
import java.util.function.Function;

public final class TokenIterator extends CachingIterator<Token> {

    private final Iterator<Character> characterIterator;
    private final Queue<Token> queue = new LinkedList<>();
    private State state = new Initial();

    public TokenIterator(Iterator<Character> characterIterator) {
        this.characterIterator = characterIterator;
    }

    @Override
    protected Optional<Token> nextElement() {
        if (queue.isEmpty()) {
            queueNextTokens();
        }
        return Optional.ofNullable(queue.poll());
    }

    private void queueNextTokens() {
        while (characterIterator.hasNext()) {
            var next = characterIterator.next();
            var transition = transition(next);
            state = transition.next();
            if (transition.result() instanceof Found(List<Token> tokens)) {
                queue.addAll(tokens);
                return;
            }
        }
        state.onEnd().ifPresent(queue::add);
        state = new Final();
    }

    private Transition transition(char next) {
        if (Character.isWhitespace(next)) {
            return state.onWhitespace(next);
        }
        return switch (next) {
            case '\'' -> state.onSingleQuote();
            case '"' -> state.onDoubleQuote();
            case '\\' -> state.onBackslash();
            case '>' -> state.onRedirectionOperator();
            default -> state.onCharacter(next);
        };
    }

    private sealed interface Result {

        Result combined(Result result);
    }

    private interface State {

        Transition onWhitespace(char whitespace);

        Transition onSingleQuote();

        Transition onDoubleQuote();

        Transition onBackslash();

        Transition onRedirectionOperator();

        Transition onCharacter(char character);

        Optional<Token> onEnd();
    }

    private static final class Initial implements State {

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
            throw new IllegalStateException();
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

    private static final class Final implements State {

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
        public Transition onCharacter(char character) {
            return onSingleQuote();
        }

        @Override
        public Optional<Token> onEnd() {
            return Optional.empty();
        }
    }

    private static final class ReadingToken implements State {

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
            return new Transition(new ReadingWhiteSpaces(), new RedirectionOperator());
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

    private static final class ReadingLiteralCharacterValue implements State {

        private final StringBuilder builder;
        private final Function<StringBuilder, State> next;

        ReadingLiteralCharacterValue(StringBuilder builder, Function<StringBuilder, State> next) {
            this.builder = builder;
            this.next = next;
        }

        ReadingLiteralCharacterValue(Function<StringBuilder, State> next) {
            this(new StringBuilder(), next);
        }

        @Override
        public Transition onWhitespace(char whitespace) {
            return onCharacter(whitespace);
        }

        @Override
        public Transition onSingleQuote() {
            return onCharacter('\'');
        }

        @Override
        public Transition onDoubleQuote() {
            return onCharacter('"');
        }

        @Override
        public Transition onBackslash() {
            return onCharacter('\\');
        }

        @Override
        public Transition onRedirectionOperator() {
            return onCharacter('>');
        }

        @Override
        public Transition onCharacter(char character) {
            builder.append(character);
            return new Transition(next.apply(builder));
        }

        @Override
        public Optional<Token> onEnd() {
            throw new IllegalStateException();
        }
    }

    private static final class ReadingSpecialCharacterValue implements State {

        private final StringBuilder builder;

        ReadingSpecialCharacterValue(StringBuilder builder) {
            this.builder = builder;
        }

        @Override
        public Transition onWhitespace(char whitespace) {
            return transitionWithEscape(whitespace);
        }

        @Override
        public Transition onSingleQuote() {
            return transitionWithEscape('\'');
        }

        @Override
        public Transition onDoubleQuote() {
            return transition('"');
        }

        @Override
        public Transition onBackslash() {
            return transition('\\');
        }

        @Override
        public Transition onRedirectionOperator() {
            return transitionWithEscape('>');
        }

        @Override
        public Transition onCharacter(char character) {
            return transitionWithEscape(character);
        }

        @Override
        public Optional<Token> onEnd() {
            throw new IllegalStateException();
        }

        private Transition transitionWithEscape(char character) {
            builder.append('\\');
            return transition(character);
        }

        private Transition transition(char character) {
            builder.append(character);
            return new Transition(new ReadingDoubleQuotedToken(builder));
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
            return onCharacter(whitespace);
        }

        @Override
        public Transition onSingleQuote() {
            return new Transition(new ReadingToken(builder));
        }

        @Override
        public Transition onDoubleQuote() {
            return onCharacter('"');
        }

        @Override
        public Transition onBackslash() {
            return onCharacter('\\');
        }

        @Override
        public Transition onRedirectionOperator() {
            return onCharacter('>');
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

    private static final class ReadingDoubleQuotedToken implements State {

        private final StringBuilder builder;

        ReadingDoubleQuotedToken(StringBuilder builder) {
            this.builder = builder;
        }

        ReadingDoubleQuotedToken() {
            this(new StringBuilder());
        }

        @Override
        public Transition onWhitespace(char whitespace) {
            return onCharacter(whitespace);
        }

        @Override
        public Transition onSingleQuote() {
            return onCharacter('\'');
        }

        @Override
        public Transition onDoubleQuote() {
            return new Transition(new ReadingToken(builder));
        }

        @Override
        public Transition onBackslash() {
            return new Transition(new ReadingSpecialCharacterValue(builder));
        }

        @Override
        public Transition onRedirectionOperator() {
            return onCharacter('>');
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
            return transition(new ReadingLiteralCharacterValue(builder, ReadingToken::new));
        }

        @Override
        public Transition onRedirectionOperator() {
            return combined(new Transition(new ReadingWhiteSpaces(), new RedirectionOperator()));
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

    private static final class Continue implements Result {

        @Override
        public Result combined(Result result) {
            return switch (result) {
                case Continue ignored -> this;
                case Found found -> found;
            };
        }
    }

    private record Found(List<Token> tokens) implements Result {

        Found(Token token) {
            this(List.of(token));
        }

        @Override
        public Result combined(Result result) {
            return switch (result) {
                case Continue ignored -> this;
                case Found(List<Token> foundTokens) -> {
                    var copy = new ArrayList<>(tokens);
                    copy.addAll(foundTokens);
                    yield new Found(copy);
                }
            };
        }
    }
}
