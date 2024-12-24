package io.codecrafters.shell.iterator.token;

import java.nio.file.Path;
import java.util.Optional;

final class ReadingAppendingOperator implements State {

    private final SimpleToken redirection;
    private final SimpleToken appending;

    private ReadingAppendingOperator(SimpleToken redirection, SimpleToken appending) {
        this.redirection = redirection;
        this.appending = appending;
    }

    static State forOutput() {
        return new ReadingAppendingOperator(SimpleToken.OUTPUT_REDIRECTION, SimpleToken.OUTPUT_APPENDING);
    }

    static State forError() {
        return new ReadingAppendingOperator(SimpleToken.ERROR_REDIRECTION, SimpleToken.ERROR_APPENDING);
    }

    @Override
    public Transition onWhitespace(char whitespace) {
        var transition = new ReadingWhiteSpaces().onWhitespace(whitespace);
        return new Transition(
            transition.next(),
            new Found(redirection).combined(transition.result())
        );
    }

    @Override
    public Transition onSingleQuote() {
        return new Transition(new ReadingSingleQuotedToken(), redirection);
    }

    @Override
    public Transition onDoubleQuote() {
        return new Transition(new ReadingDoubleQuotedToken(), redirection);
    }

    @Override
    public Transition onBackslash() {
        return new Transition(new ReadingLiteralCharacterValue(ReadingToken::new), redirection);
    }

    @Override
    public Transition onRedirectionOperator() {
        return new Transition(new ReadingWhiteSpaces(), new Found(appending));
    }

    @Override
    public Transition onTilda(Path path) {
        var transition = new ReadingToken().onTilda(path);
        return new Transition(
            transition.next(),
            new Found(redirection).combined(transition.result())
        );
    }

    @Override
    public Transition onCharacter(char character) {
        var transition = new ReadingToken().onCharacter(character);
        return new Transition(
            transition.next(),
            new Found(redirection).combined(transition.result())
        );
    }

    @Override
    public Optional<Token> onEnd() {
        return Optional.of(redirection);
    }
}
