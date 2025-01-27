package io.codecrafters.shell.token;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class TokenFactoryTests {

    private final Path path = Paths.get("/home");
    private final TokenFactory tokenFactory = new TokenFactory(path);

    @Test
    void givenEmptyString_thenNoTokens() {
        assertThat(tokenFactory.tokens("")).isEmpty();
    }

    @Test
    void givenSpaces_thenNoTokens() {
        assertThat(tokenFactory.tokens("     ")).isEmpty();
    }

    @Test
    void givenWord_thenLiteralToken() {
        assertThat(tokenFactory.tokens("token")).containsExactly(new Literal("token"));
    }

    @Test
    void givenWords_thenLiteralTokens() {
        assertThat(tokenFactory.tokens("first second"))
            .containsExactly(new Literal("first"), new Literal("second"));
    }

    @Test
    void givenSingleQuotedWord_thenLiteralToken() {
        assertThat(tokenFactory.tokens("'token'")).containsExactly(new Literal("token"));
    }

    @Test
    void givenSingleQuotedWords_thenLiteralTokens() {
        assertThat(tokenFactory.tokens("'first' 'second'"))
            .containsExactly(new Literal("first"), new Literal("second"));
    }

    @Test
    void givenDoubleQuotedWord_thenLiteralToken() {
        assertThat(
            tokenFactory.tokens(
                """
                "token"
                """
            )
        )
            .containsExactly(new Literal("token"));
    }

    @Test
    void givenDoubleQuotedWords_thenLiteralTokens() {
        assertThat(
            tokenFactory.tokens(
                """
                "first" "second"
                """
            )
        )
            .containsExactly(new Literal("first"), new Literal("second"));
    }

    @Test
    void givenWordWithQuotedParts_thenSingleToken() {
        assertThat(
            tokenFactory.tokens(
                """
                first'second'"third"
                """
            )
        )
            .containsExactly(new Literal("firstsecondthird"));
    }

    @Test
    void givenSingleQuotedBackslash_thenBackslashLiteralToken() {
        assertThat(tokenFactory.tokens("'\\'")).containsExactly(new Literal("\\"));
    }

    @Test
    void givenSingleQuotedDoubleQuote_thenDoubleQuoteLiteralToken() {
        assertThat(tokenFactory.tokens("'\"'")).containsExactly(new Literal("\""));
    }

    @Test
    void givenSingleQuotedSpace_thenLiteralToken() {
        assertThat(tokenFactory.tokens("' '")).containsExactly(new Literal(" "));
    }

    @Test
    void givenSingleQuotedRedirectionOperator_thenLiteralToken() {
        assertThat(tokenFactory.tokens("'>'")).containsExactly(new Literal(">"));
    }

    @Test
    void givenDoubleQuotedSingleQuote_thenSingleQuoteLiteralToken() {
        assertThat(
            tokenFactory.tokens(
                """
                "'"
                """
            )
        )
            .containsExactly(new Literal("'"));
    }

    @Test
    void givenDoubleQuotedEscapedDoubleQuote_thenDoubleQuoteLiteralToken() {
        assertThat(
            tokenFactory.tokens(
                """
                "\\\""
                """
            )
        )
            .containsExactly(new Literal("\""));
    }

    @Test
    void givenDoubleQuotedWords_thenLiteralToken() {
        assertThat(
            tokenFactory.tokens(
                """
                "first second"
                """
            )
        )
            .containsExactly(new Literal("first second"));
    }

    @Test
    void givenDoubleQuotedRedirectionOperator_thenLiteralToken() {
        assertThat(
            tokenFactory.tokens(
                """
                ">"
                """
            )
        )
            .containsExactly(new Literal(">"));
    }

    @Test
    void givenEscapedSpace_thenLiteralToken() {
        assertThat(tokenFactory.tokens("\\ ")).containsExactly(new Literal(" "));
    }

    @Test
    void givenEscapedSingleQuote_thenLiteralToken() {
        assertThat(tokenFactory.tokens("\\'")).containsExactly(new Literal("'"));
    }

    @Test
    void givenEscapedDoubleQuote_thenLiteralToken() {
        assertThat(tokenFactory.tokens("\\\"")).containsExactly(new Literal("\""));
    }

    @Test
    void givenEscapedRedirectionOperator_thenLiteralToken() {
        assertThat(tokenFactory.tokens("\\>")).containsExactly(new Literal(">"));
    }

    @Test
    void givenEscapedBackslash_thenLiteralToken() {
        assertThat(tokenFactory.tokens("\\\\")).containsExactly(new Literal("\\"));
    }

    @Test
    void givenDoubleQuotedEscapedSpace_thenLiteralTokenWithBackslash() {
        assertThat(
            tokenFactory.tokens(
                """
                "\\ "
                """
            )
        )
            .containsExactly(new Literal("\\ "));
    }

    @Test
    void givenDoubleQuotedEscapedSingleQuote_thenLiteralTokenWithBackslash() {
        assertThat(
            tokenFactory.tokens(
                """
                "\\'"
                """
            )
        )
            .containsExactly(new Literal("\\'"));
    }

    @Test
    void givenDoubleQuotedEscapedBackslash_thenLiteralToken() {
        assertThat(
            tokenFactory.tokens(
                """
                "\\\\"
                """
            )
        )
            .containsExactly(new Literal("\\"));
    }

    @Test
    void givenDoubleQuotedEscapedRedirectionOperator_thenLiteralToken() {
        assertThat(
            tokenFactory.tokens(
                """
                "\\>"
                """
            )
        )
            .containsExactly(new Literal("\\>"));
    }

    @Test
    void givenDoubleQuotedEscapedCharacter_thenLiteralToken() {
        assertThat(
            tokenFactory.tokens(
                """
                "\\a"
                """
            )
        )
            .containsExactly(new Literal("\\a"));
    }

    @Test
    void givenEscapedSymbolInToken_thenLiteralToken() {
        assertThat(tokenFactory.tokens("first\\second"))
            .containsExactly(new Literal("firstsecond"));
    }

    @Test
    void givenEscapedSymbolInSpaces_thenLiteralTokens() {
        assertThat(tokenFactory.tokens("first \\  second"))
            .containsExactly(new Literal("first"), new Literal(" "), new Literal("second"));
    }

    @Test
    void givenRedirectionOperatorNextToToken_thenTokens() {
        assertThat(tokenFactory.tokens("first> second"))
            .containsExactly(
                new Literal("first"),
                new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.OVERWRITE),
                new Literal("second")
            );
    }

    @Test
    void givenSpaceBeforeRedirectionOperator_thenTokens() {
        assertThat(tokenFactory.tokens("first > second"))
            .containsExactly(
                new Literal("first"),
                new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.OVERWRITE),
                new Literal("second")
            );
    }

    @Test
    void givenExplicitOutputRedirection_thenTokens() {
        assertThat(tokenFactory.tokens("first 1> second"))
            .containsExactly(
                new Literal("first"),
                new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.OVERWRITE),
                new Literal("second")
            );
    }

    @Test
    void givenExplicitErrorRedirection_thenTokens() {
        assertThat(tokenFactory.tokens("first 2> second"))
            .containsExactly(
                new Literal("first"),
                new Redirection(Redirection.Source.ERROR, Redirection.Mode.OVERWRITE),
                new Literal("second")
            );
    }

    @Test
    void givenSpaceAfterRedirectionOperator_thenTokens() {
        assertThat(tokenFactory.tokens("first >second"))
            .containsExactly(
                new Literal("first"),
                new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.OVERWRITE),
                new Literal("second")
            );
    }

    @Test
    void givenSpaceBeforeToken_thenNotExtraToken() {
        assertThat(tokenFactory.tokens(" first"))
            .containsExactly(new Literal("first"));
    }

    @Test
    void givenTilda_thenPathLiteral() {
        assertThat(tokenFactory.tokens("~")).containsExactly(new Literal(path.toString()));
    }

    @Test
    void givenDoubleQuotedTilda_thenPathLiteral() {
        assertThat(
            tokenFactory.tokens(
                """
                "~"
                """
            )
        )
            .containsExactly(new Literal(path.toString()));
    }

    @Test
    void givenSingleQuotedTilda_thenTildaLiteral() {
        assertThat(tokenFactory.tokens("'~'")).containsExactly(new Literal("~"));
    }

    @Test
    void givenEscapedTilda_thenTildaLiteral() {
        assertThat(tokenFactory.tokens("\\~")).containsExactly(new Literal("~"));
    }

    @Test
    void givenSpaceBeforeTilda_thenPathLiteral() {
        assertThat(tokenFactory.tokens("command ~"))
            .containsExactly(new Literal("command"), new Literal(path.toString()));
    }

    @Test
    void givenDoubleQuotedEscapedTilda_thenBackslashTildaLiteral() {
        assertThat(
            tokenFactory.tokens(
                """
                "\\~"
                """
            )
        )
            .containsExactly(new Literal("\\~"));
    }

    @Test
    void givenAppendOperator_thenOutputAppendingToken() {
        assertThat(tokenFactory.tokens("command >> file"))
            .containsExactly(
                new Literal("command"),
                new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.APPEND),
                new Literal("file")
            );
    }

    @Test
    void givenRedirectionToDoubleQuotedDestination_OutputRedirectionToken() {
        assertThat(
            tokenFactory.tokens(
                """
                command >"file"
                """
            )
        )
            .containsExactly(
                new Literal("command"),
                new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.OVERWRITE),
                new Literal("file")
            );
    }

    @Test
    void givenRedirectionToSingleQuotedDestination_thenOutputRedirectionToken() {
        assertThat(tokenFactory.tokens("command >'file'"))
            .containsExactly(
                new Literal("command"),
                new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.OVERWRITE),
                new Literal("file")
            );
    }

    @Test
    void givenRedirectionToEscapedDestination_thenOutputRedirectionToken() {
        assertThat(tokenFactory.tokens("command >\\ file"))
            .containsExactly(
                new Literal("command"),
                new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.OVERWRITE),
                new Literal(" file")
            );
    }

    @Test
    void givenRedirectionToTilda_thenOutputRedirectionToken() {
        assertThat(tokenFactory.tokens("command >~"))
            .containsExactly(
                new Literal("command"),
                new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.OVERWRITE),
                new Literal(path.toString())
            );
    }

    @Test
    void givenRedirectionOnEnd_thenOutputRedirectionToken() {
        assertThat(tokenFactory.tokens("command >"))
            .containsExactly(
                new Literal("command"),
                new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.OVERWRITE)
            );
    }

    @Test
    void givenRedirection_thenOutputRedirectionToken() {
        assertThat(tokenFactory.tokens(">"))
            .containsExactly(new Redirection(Redirection.Source.OUTPUT, Redirection.Mode.OVERWRITE));
    }

    @Test
    void givenEscapeSymbolOnEnd_thenEmptyToken() {
        assertThat(tokenFactory.tokens("command \\")).containsExactly(new Literal("command"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"command", "'command", "\"command\\"})
    void givenUnquotedToken_thenCouldNotReadTokenThrown(String input) {
        assertThatThrownBy(() -> tokenFactory.tokens(input))
            .isInstanceOf(CouldNotReadToken.class)
            .hasMessageContaining("command")
            .hasMessageEndingWith(input.substring(0, 1));
    }
}