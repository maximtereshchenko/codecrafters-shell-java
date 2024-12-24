package io.codecrafters.shell.iterator.expression;

import io.codecrafters.shell.iterator.CachingIterator;
import io.codecrafters.shell.iterator.token.Literal;
import io.codecrafters.shell.iterator.token.SimpleToken;
import io.codecrafters.shell.iterator.token.Token;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

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
            case SimpleToken.LINE_BREAK -> nextElement();
            case Literal(var value) -> Optional.of(command(value));
            case SimpleToken.OUTPUT_REDIRECTION, SimpleToken.ERROR_REDIRECTION -> throw new IllegalStateException();
        };
    }

    private Expression command(String name) {
        var arguments = new ArrayList<String>();
        while (tokenIterator.hasNext()) {
            var next = tokenIterator.next();
            switch (next) {
                case SimpleToken.LINE_BREAK -> {
                    return new Command(name, arguments);
                }
                case Literal(var value) -> arguments.add(value);
                case SimpleToken.OUTPUT_REDIRECTION -> {
                    return redirection(path -> new OutputRedirection(new Command(name, arguments), path));
                }
                case SimpleToken.ERROR_REDIRECTION -> {
                    return redirection(path -> new ErrorRedirection(new Command(name, arguments), path));
                }
            }
        }
        return new Command(name, arguments);
    }

    private Expression redirection(Function<Path, Expression> function) {
        if (!tokenIterator.hasNext()) {
            throw new IllegalStateException();
        }
        return switch (tokenIterator.next()) {
            case SimpleToken.LINE_BREAK, SimpleToken.OUTPUT_REDIRECTION, SimpleToken.ERROR_REDIRECTION -> throw new IllegalStateException();
            case Literal(var value) -> function.apply(Paths.get(value));
        };
    }
}
