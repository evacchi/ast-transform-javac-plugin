A simple hello-world `javac` plug-in to rewrite a Java syntax tree.

It matches the AST for the code:

    from(Arrays.asList("foo", "bar", "baz", "quux"))
        .filter(s -> s.length() > 3);

injecting an invocation to `index()` decorating the lambda in `filter()`, yielding:


    from(Arrays.asList("foo", "bar", "baz", "quux"))
        .filter(
            index(s -> s.length() > 3)
        );

To run against the example code:

```
    mvn compile
    javac -Xplugin:MyPlugin -cp target/classes \
        src/test/resources/project1/**/*.java \
        -d target/classes-processed
```

The same example will also run as a test using `mvn verify`. 
In this case, the FailSafe Maven plugin has to be configured with explicit module visibility overrides (probably because otherwise it runs in a different mode than plain `javac` launcher).


