package io.codecrafters.shell.iterator.token;

import io.codecrafters.shell.iterator.CachingIterator;

import java.nio.file.Path;
import java.util.*;

public final class TokenIterator extends CachingIterator<Token> {

    private final Path path;
    private final Iterator<Character> characterIterator;
    private final Queue<Token> queue = new LinkedList<>();
    private State state = new Initial();

    public TokenIterator(Path path, Iterator<Character> characterIterator) {
        this.path = path;
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
            case '~' -> state.onTilda(path);
            default -> state.onCharacter(next);
        };
    }
}
