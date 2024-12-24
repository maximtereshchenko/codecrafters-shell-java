package io.codecrafters.shell.iterator.token;

import io.codecrafters.shell.iterator.CharacterIterator;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

final class TokenIteratorTests {

    private final Path path = Paths.get("").toAbsolutePath();

    @Test
    void givenEmptyString_thenNoTokens() {
        assertThat(tokens("")).isEmpty();
    }

    @Test
    void givenWord_thenLiteralToken() {
        assertThat(tokens("token")).containsExactly(new Literal("token"));
    }

    @Test
    void givenWords_thenLiteralTokens() {
        assertThat(tokens("first second"))
            .containsExactly(new Literal("first"), new Literal("second"));
    }

    @Test
    void givenSingleQuotedWord_thenLiteralToken() {
        assertThat(tokens("'token'")).containsExactly(new Literal("token"));
    }

    @Test
    void givenSingleQuotedWords_thenLiteralTokens() {
        assertThat(tokens("'first' 'second'"))
            .containsExactly(new Literal("first"), new Literal("second"));
    }

    @Test
    void givenDoubleQuotedWord_thenLiteralToken() {
        assertThat(
            tokens(
                """
                "token"\
                """
            )
        )
            .containsExactly(new Literal("token"));
    }

    @Test
    void givenDoubleQuotedWords_thenLiteralTokens() {
        assertThat(
            tokens(
                """
                "first" "second"\
                """
            )
        )
            .containsExactly(new Literal("first"), new Literal("second"));
    }

    @Test
    void givenWordWithQuotedParts_thenSingleToken() {
        assertThat(
            tokens(
                """
                first'second'"third"\
                """
            )
        )
            .containsExactly(new Literal("firstsecondthird"));
    }

    @Test
    void givenSingleQuotedBackslash_thenBackslashLiteralToken() {
        assertThat(tokens("'\\'")).containsExactly(new Literal("\\"));
    }

    @Test
    void givenSingleQuotedDoubleQuote_thenDoubleQuoteLiteralToken() {
        assertThat(tokens("'\"'")).containsExactly(new Literal("\""));
    }

    @Test
    void givenSingleQuotedSpace_thenLiteralToken() {
        assertThat(tokens("' '")).containsExactly(new Literal(" "));
    }

    @Test
    void givenSingleQuotedRedirectionOperator_thenLiteralToken() {
        assertThat(tokens("'>'")).containsExactly(new Literal(">"));
    }

    @Test
    void givenDoubleQuotedSingleQuote_thenSingleQuoteLiteralToken() {
        assertThat(
            tokens(
                """
                "'"\
                """
            )
        )
            .containsExactly(new Literal("'"));
    }

    @Test
    void givenDoubleQuotedEscapedDoubleQuote_thenDoubleQuoteLiteralToken() {
        assertThat(
            tokens(
                """
                "\\\""\
                """
            )
        )
            .containsExactly(new Literal("\""));
    }

    @Test
    void givenDoubleQuotedWords_thenLiteralToken() {
        assertThat(
            tokens(
                """
                "first second"\
                """
            )
        )
            .containsExactly(new Literal("first second"));
    }

    @Test
    void givenDoubleQuotedRedirectionOperator_thenLiteralToken() {
        assertThat(
            tokens(
                """
                ">"\
                """
            )
        )
            .containsExactly(new Literal(">"));
    }

    @Test
    void givenEscapedSpace_thenLiteralToken() {
        assertThat(tokens("\\ ")).containsExactly(new Literal(" "));
    }

    @Test
    void givenEscapedSingleQuote_thenLiteralToken() {
        assertThat(tokens("\\'")).containsExactly(new Literal("'"));
    }

    @Test
    void givenEscapedDoubleQuote_thenLiteralToken() {
        assertThat(tokens("\\\"")).containsExactly(new Literal("\""));
    }

    @Test
    void givenEscapedRedirectionOperator_thenLiteralToken() {
        assertThat(tokens("\\>")).containsExactly(new Literal(">"));
    }

    @Test
    void givenEscapedBackslash_thenLiteralToken() {
        assertThat(tokens("\\\\")).containsExactly(new Literal("\\"));
    }

    @Test
    void givenDoubleQuotedEscapedSpace_thenLiteralTokenWithBackslash() {
        assertThat(
            tokens(
                """
                "\\ "\
                """
            )
        )
            .containsExactly(new Literal("\\ "));
    }

    @Test
    void givenDoubleQuotedEscapedSingleQuote_thenLiteralTokenWithBackslash() {
        assertThat(
            tokens(
                """
                "\\'"\
                """
            )
        )
            .containsExactly(new Literal("\\'"));
    }

    @Test
    void givenDoubleQuotedEscapedBackslash_thenLiteralToken() {
        assertThat(
            tokens(
                """
                "\\\\"\
                """
            )
        )
            .containsExactly(new Literal("\\"));
    }

    @Test
    void givenDoubleQuotedEscapedRedirectionOperator_thenLiteralToken() {
        assertThat(
            tokens(
                """
                "\\>"\
                """
            )
        )
            .containsExactly(new Literal("\\>"));
    }

    @Test
    void givenDoubleQuotedEscapedCharacter_thenLiteralToken() {
        assertThat(
            tokens(
                """
                "\\a"\
                """
            )
        )
            .containsExactly(new Literal("\\a"));
    }

    @Test
    void givenEscapedSymbolInToken_thenLiteralToken() {
        assertThat(tokens("first\\second"))
            .containsExactly(new Literal("firstsecond"));
    }

    @Test
    void givenEscapedSymbolInSpaces_thenLiteralTokens() {
        assertThat(tokens("first \\  second"))
            .containsExactly(new Literal("first"), new Literal(" "), new Literal("second"));
    }

    @Test
    void givenRedirectionOperatorNextToToken_thenTokens() {
        assertThat(tokens("first> second"))
            .containsExactly(new Literal("first"), SimpleToken.OUTPUT_REDIRECTION, new Literal("second"));
    }

    @Test
    void givenSpaceBeforeRedirectionOperator_thenTokens() {
        assertThat(tokens("first > second"))
            .containsExactly(new Literal("first"), SimpleToken.OUTPUT_REDIRECTION, new Literal("second"));
    }

    @Test
    void givenExplicitOutputRedirection_thenTokens() {
        assertThat(tokens("first 1> second"))
            .containsExactly(new Literal("first"), SimpleToken.OUTPUT_REDIRECTION, new Literal("second"));
    }

    @Test
    void givenExplicitErrorRedirection_thenTokens() {
        assertThat(tokens("first 2> second"))
            .containsExactly(new Literal("first"), SimpleToken.ERROR_REDIRECTION, new Literal("second"));
    }

    @Test
    void givenSpaceAfterRedirectionOperator_thenTokens() {
        assertThat(tokens("first >second"))
            .containsExactly(new Literal("first"), SimpleToken.OUTPUT_REDIRECTION, new Literal("second"));
    }

    @Test
    void givenLineBreaks_thenLineBreakTokens() {
        assertThat(
            tokens(
                """
                first
                second
                """
            )
        )
            .containsExactly(
                new Literal("first"),
                SimpleToken.LINE_BREAK,
                new Literal("second"),
                SimpleToken.LINE_BREAK
            );
    }

    @Test
    void givenSpaceBeforeToken_thenNotExtraToken() {
        assertThat(tokens(" first"))
            .containsExactly(new Literal("first"));
    }

    @Test
    void givenTilda_thenPathLiteral() {
        assertThat(tokens("~")).containsExactly(new Literal(path.toString()));
    }

    @Test
    void givenDoubleQuotedTilda_thenPathLiteral() {
        assertThat(
            tokens(
                """
                "~"\
                """
            )
        )
            .containsExactly(new Literal(path.toString()));
    }

    @Test
    void givenSingleQuotedTilda_thenTildaLiteral() {
        assertThat(tokens("'~'")).containsExactly(new Literal("~"));
    }

    @Test
    void givenEscapedTilda_thenTildaLiteral() {
        assertThat(tokens("\\~")).containsExactly(new Literal("~"));
    }

    @Test
    void givenSpaceBeforeTilda_thenPathLiteral() {
        assertThat(tokens("command ~"))
            .containsExactly(new Literal("command"), new Literal(path.toString()));
    }

    @Test
    void givenDoubleQuotedEscapedTilda_thenBackslashTildaLiteral() {
        assertThat(
            tokens(
                """
                "\\~"\
                """
            )
        )
            .containsExactly(new Literal("\\~"));
    }

    @Test
    void givenAppendOperator_thenOutputAppendingToken() {
        assertThat(tokens("command >> file"))
            .containsExactly(new Literal("command"), SimpleToken.OUTPUT_APPENDING, new Literal("file"));
    }

    @Test
    void givenRedirectionToDoubleQuotedDestination_OutputRedirectionToken() {
        assertThat(
            tokens(
                """
                command >"file"\
                """
            )
        )
            .containsExactly(new Literal("command"), SimpleToken.OUTPUT_REDIRECTION, new Literal("file"));
    }

    @Test
    void givenRedirectionToSingleQuotedDestination_thenOutputRedirectionToken() {
        assertThat(tokens("command >'file'"))
            .containsExactly(new Literal("command"), SimpleToken.OUTPUT_REDIRECTION, new Literal("file"));
    }

    @Test
    void givenRedirectionToEscapedDestination_thenOutputRedirectionToken() {
        assertThat(tokens("command >\\ file"))
            .containsExactly(new Literal("command"), SimpleToken.OUTPUT_REDIRECTION, new Literal(" file"));
    }

    @Test
    void givenRedirectionToTilda_OutputRedirectionToken() {
        assertThat(tokens("command >~"))
            .containsExactly(new Literal("command"), SimpleToken.OUTPUT_REDIRECTION, new Literal(path.toString()));
    }

    @Test
    void givenRedirectionOnEnd_OutputRedirectionToken() {
        assertThat(tokens("command >"))
            .containsExactly(new Literal("command"), SimpleToken.OUTPUT_REDIRECTION);
    }

    private Iterable<Token> tokens(String raw) {
        return () -> new TokenIterator(path, new CharacterIterator(new StringReader(raw)));
    }
}