package io.codecrafters.shell.iterator.token;

final class Continue implements Result {

    @Override
    public Result combined(Result result) {
        return switch (result) {
            case Continue ignored -> this;
            case Found found -> found;
        };
    }
}
