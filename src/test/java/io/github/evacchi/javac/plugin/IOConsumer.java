package io.github.evacchi.javac.plugin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

public interface IOConsumer<T> extends Consumer<T> {

    static <I> Consumer<I> of(IOConsumer<I> f) {
        return f;
    }

    @Override
    default void accept(T t) {
        try {
            acceptThrower(t);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    void acceptThrower(T t) throws IOException;
}
