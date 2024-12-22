package io.codecrafters.shell.iterator.token;

import io.codecrafters.shell.iterator.CharacterIterator;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

final class TokenIteratorTests {

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
            .containsExactly(new Literal("first"), new RedirectionOperator(), new Literal("second"));
    }

    @Test
    void givenSpaceBeforeRedirectionOperator_thenTokens() {
        assertThat(tokens("first > second"))
            .containsExactly(new Literal("first"), new RedirectionOperator(), new Literal("second"));
    }

    @Test
    void givenExplicitOutputRedirection_thenTokens() {
        assertThat(tokens("first 1> second"))
            .containsExactly(new Literal("first"), new RedirectionOperator(), new Literal("second"));
    }

    @Test
    void givenSpaceAfterRedirectionOperator_thenTokens() {
        assertThat(tokens("first >second"))
            .containsExactly(new Literal("first"), new RedirectionOperator(), new Literal("second"));
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
            .containsExactly(new Literal("first"), new LineBreak(), new Literal("second"), new LineBreak());
    }

    @Test
    void givenSpaceBeforeToken_thenNotExtraToken() {
        assertThat(tokens(" first"))
            .containsExactly(new Literal("first"));
    }

    private Iterable<Token> tokens(String raw) {
        return () -> new TokenIterator(new CharacterIterator(new StringReader(raw)));
    }
}