package io.github.evacchi.javac;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;

public class MyPlugin implements Plugin {

    @Override
    public String getName() {
        return "MyPlugin";
    }

    @Override
    public void init(JavacTask task, String... args) {
        System.out.println("HELLO WORLD!");
//        Context context = task.getContext();
//        Log.instance(context)
//          .printRawLines(Log.WriterKind.NOTICE, "Hello from " + getName());
    }
}