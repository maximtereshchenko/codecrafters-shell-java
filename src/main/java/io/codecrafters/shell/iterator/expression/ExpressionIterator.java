package io.codecrafters.shell.iterator.expression;

import io.codecrafters.shell.iterator.CachingIterator;
import io.codecrafters.shell.iterator.token.LineBreak;
import io.codecrafters.shell.iterator.token.Literal;
import io.codecrafters.shell.iterator.token.Redirection;
import io.codecrafters.shell.iterator.token.Token;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public final class ExpressionIterator extends CachingIterator<Expression> {

    private final Iterator<Token> tokenIterator;

    public ExpressionIterator(Iterator<Token> tokenIterator) {
        this.tokenIterator = tokenIterator;
    }

    @Override
    protected Optional<Expression> nextElement() {
        if (!tokenIterator.hasNext()) {
            return Optional.empty();
        }
        return switch (tokenIterator.next()) {
            case LineBreak() -> nextElement();
            case Literal(var value) -> Optional.of(command(value));
            case Redirection ignored -> throw new IllegalStateException();
        };
    }

    private Expression command(String name) {
        var arguments = new ArrayList<String>();
        while (tokenIterator.hasNext()) {
            var next = tokenIterator.next();
            switch (next) {
                case LineBreak() -> {
                    return new Command(name, arguments);
                }
                case Literal(var value) -> arguments.add(value);
                case Redirection(var source, var mode) -> {
                    return redirection(new Command(name, arguments), source, mode);
                }
            }
        }
        return new Command(name, arguments);
    }

    private Expression redirection(
        Expression expression,
        Redirection.Source source,
        Redirection.Mode mode
    ) {
        if (!tokenIterator.hasNext()) {
            throw new IllegalStateException();
        }
        return switch (tokenIterator.next()) {
            case LineBreak ignored -> throw new IllegalStateException();
            case Redirection ignored -> throw new IllegalStateException();
            case Literal(var value) -> new RedirectionExpression(
                expression,
                switch (source) {
                    case OUTPUT -> RedirectionExpression.Source.OUTPUT;
                    case ERROR -> RedirectionExpression.Source.ERROR;
                },
                Paths.get(value),
                switch (mode) {
                    case OVERWRITE -> RedirectionExpression.Mode.OVERWRITE;
                    case APPEND -> RedirectionExpression.Mode.APPEND;
                }
            );
        };
    }
}
