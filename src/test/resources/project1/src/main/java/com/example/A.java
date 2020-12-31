package com.example;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.github.evacchi.query.Query.*;

public class A {
    public Collection<String> a() {
        return from(Arrays.asList("foo", "bar", "baz", "quux"))
                .filter(s -> s.length() > 3);
    }
} 
