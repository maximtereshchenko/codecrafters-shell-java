package io.codecrafters.shell.iterator.token;

import java.util.Objects;

public record Literal(CharSequence value) implements Token {

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof Literal(CharSequence other) &&
               value.toString().equals(other.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
