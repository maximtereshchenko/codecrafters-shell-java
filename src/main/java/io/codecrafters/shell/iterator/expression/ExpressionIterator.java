package io.codecrafters.shell.iterator.expression;

import io.codecrafters.shell.iterator.CachingIterator;
import io.codecrafters.shell.iterator.token.LineBreak;
import io.codecrafters.shell.iterator.token.Literal;
import io.codecrafters.shell.iterator.token.RedirectionOperator;
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
            case LineBreak ignored -> nextElement();
            case Literal(var value) -> Optional.of(command(value));
            case RedirectionOperator ignored -> throw new IllegalStateException();
        };
    }

    private Expression command(String name) {
        var arguments = new ArrayList<String>();
        while (tokenIterator.hasNext()) {
            var next = tokenIterator.next();
            switch (next) {
                case LineBreak ignored -> {
                    return new Command(name, arguments);
                }
                case Literal(var value) -> arguments.add(value);
                case RedirectionOperator ignored -> {
                    return outputRedirection(new Command(name, arguments));
                }
            }
        }
        return new Command(name, arguments);
    }

    private OutputRedirection outputRedirection(Expression expression) {
        if (!tokenIterator.hasNext()) {
            throw new IllegalStateException();
        }
        return switch (tokenIterator.next()) {
            case LineBreak ignored -> throw new IllegalStateException();
            case Literal(var value) -> new OutputRedirection(expression, Paths.get(value));
            case RedirectionOperator ignored -> throw new IllegalStateException();
        };
    }
}
