package io.codecrafters.shell.expression;

import io.codecrafters.shell.token.TokenFactory;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class ExpressionFactoryTests {

    private final ExpressionFactory expressionFactory = new ExpressionFactory(new TokenFactory(Paths.get("")));

    @Test
    void givenNoTokens_thenNoExpressions() {
        assertThat(expressionFactory.expression("")).isEmpty();
    }

    @Test
    void givenSingleLiteral_thenCommand() {
        assertThat(expressionFactory.expression("command")).contains(new Command("command"));
    }

    @Test
    void givenLiterals_thenCommandWithArguments() {
        assertThat(expressionFactory.expression("command first second"))
            .contains(new Command("command", List.of("first", "second")));
    }

    @Test
    void givenOutputRedirection_thenOutputRedirection() {
        assertThat(expressionFactory.expression("command > file"))
            .contains(
                new RedirectionExpression(
                    new Command("command"),
                    RedirectionExpression.Source.OUTPUT,
                    Paths.get("file"),
                    RedirectionExpression.Mode.OVERWRITE
                )
            );
    }

    @Test
    void givenErrorRedirection_thenErrorRedirection() {
        assertThat(expressionFactory.expression("command 2> file"))
            .contains(
                new RedirectionExpression(
                    new Command("command"),
                    RedirectionExpression.Source.ERROR,
                    Paths.get("file"),
                    RedirectionExpression.Mode.OVERWRITE
                )
            );
    }
}