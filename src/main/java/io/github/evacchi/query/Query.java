package io.github.evacchi.query;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Query<T> {

    public static <T> Predicate<T> index(Predicate<T> predicate) {
        return t -> {
            System.out.println("INDEXED");
            return predicate.test(t);
        };
    }

    public static <T> Query<T> from(Collection<T> collection) {
        return new Query<>(collection);
    }

    private final Collection<T> collection;

    public Query(Collection<T> collection) {
        this.collection = collection;
    }

    public Collection<T> filter(Predicate<T> predicate) {
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }
}
