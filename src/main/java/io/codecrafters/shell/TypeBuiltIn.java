package io.codecrafters.shell;

import java.util.LinkedHashSet;
import java.util.Optional;

final class TypeBuiltIn implements ExecutableExpression {

    private final String name;
    private final LinkedHashSet<CommandFactory> commandFactories;
    private final ExecutableExpression downstream;

    TypeBuiltIn(String name, LinkedHashSet<CommandFactory> commandFactories, ExecutableExpression downstream) {
        this.name = name;
        this.commandFactories = commandFactories;
        this.downstream = downstream;
    }

    @Override
    public void onNext(String line) {
        //empty
    }

    @Override
    public void onError(String line) {
        downstream.onError(line);
    }

    @Override
    public ExecutionResult onEnd() {
        commandFactories.stream()
            .map(factory -> factory.commandType(name))
            .flatMap(Optional::stream)
            .findAny()
            .map(this::description)
            .ifPresentOrElse(
                downstream::onNext,
                () -> downstream.onError(name + ": not found")
            );
        return downstream.onEnd();
    }

    @Override
    public void close() {
        downstream.close();
    }

    private String description(CommandType type) {
        return switch (type) {
            case BuiltIn(var builtIn) -> builtIn + " is a shell builtin";
            case External(var path) -> "%s is %s".formatted(name, path);
        };
    }
}
