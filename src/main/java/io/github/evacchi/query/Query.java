package io.github.evacchi.query;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Query<T> {

    public static AtomicInteger IndexCount = new AtomicInteger(0);
    public static AtomicInteger IndexHits = new AtomicInteger(0);

    public static <T> Predicate<T> index(Predicate<T> predicate) {
        IndexCount.incrementAndGet();
        return t -> {
            IndexHits.incrementAndGet();
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
