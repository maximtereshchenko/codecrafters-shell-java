package io.codecrafters.shell.iterator.token;

public final class LineBreak implements Token {

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof LineBreak;
    }
}
