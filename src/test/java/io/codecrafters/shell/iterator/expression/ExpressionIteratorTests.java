package io.codecrafters.shell.iterator.expression;

import io.codecrafters.shell.iterator.token.LineBreak;
import io.codecrafters.shell.iterator.token.Literal;
import io.codecrafters.shell.iterator.token.RedirectionOperator;
import io.codecrafters.shell.iterator.token.Token;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

final class ExpressionIteratorTests {

    @Test
    void givenNoTokens_thenNoExpressions() {
        assertThat(expressions()).isEmpty();
    }

    @Test
    void givenSingleLiteral_thenCommand() {
        assertThat(expressions("command")).containsExactly(new Command("command"));
    }

    @Test
    void givenLiterals_thenCommandWithArguments() {
        assertThat(expressions("command", "first", "second"))
            .containsExactly(new Command("command", List.of("first", "second")));
    }

    @Test
    void givenLiteralsWithLineBreak_thenMultipleCommands() {
        assertThat(expressions(new Literal("first"), new LineBreak(), new Literal("second")))
            .containsExactly(new Command("first"), new Command("second"));
    }

    @Test
    void givenLineBreaks_thenNoExpressions() {
        assertThat(expressions(new LineBreak(), new LineBreak())).isEmpty();
    }

    @Test
    void givenRedirectionOperator_thenOutputRedirection() {
        assertThat(expressions(new Literal("command"), new RedirectionOperator(), new Literal("file")))
            .containsExactly(new OutputRedirection(new Command("command"), Paths.get("file")));
    }

    private Iterable<Expression> expressions(String... literals) {
        return expressions(
            Stream.of(literals)
                .map(Literal::new)
                .toArray(Token[]::new)
        );
    }

    private Iterable<Expression> expressions() {
        return expressions(List.of());
    }

    private Iterable<Expression> expressions(Token... tokens) {
        return expressions(List.of(tokens));
    }

    private Iterable<Expression> expressions(List<Token> tokens) {
        return () -> new ExpressionIterator(tokens.iterator());
    }
}