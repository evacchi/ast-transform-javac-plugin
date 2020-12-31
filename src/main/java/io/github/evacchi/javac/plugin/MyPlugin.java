package io.github.evacchi.javac.plugin;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;

public class MyPlugin implements Plugin {

    @Override
    public String getName() {
        return "MyPlugin";
    }

    @Override
    public void init(JavacTask task, String... args) {
        System.out.println("HELLO WORLD!");
        Context context = ((BasicJavacTask) task).getContext();
        Log.instance(context)
                .printRawLines(Log.WriterKind.NOTICE, "Hello from " + getName());

        task.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent e) {
                System.out.print("STARTED ");
                System.out.println(e);
            }

            @Override
            public void finished(TaskEvent e) {
//                if (e.getKind() != TaskEvent.Kind.PARSE) {
//                    return;
//                }
                e.getCompilationUnit().accept(new TreeScanner<Void, Void>() {
                    @Override
                    public Void visitMethod(MethodTree method, Void v) {
                        List<VariableTree> parametersToInstrument
                                = method.getParameters().stream()
//                                .filter(SampleJavacPlugin.this::shouldInstrument)
                                .collect(Collectors.toList());

                        if (!parametersToInstrument.isEmpty()) {
                            Collections.reverse(parametersToInstrument);
                            parametersToInstrument.forEach(p -> addCheck(method, p, context));
                        }
                        return super.visitMethod(method, v);
                    }
                }, null);
            }
        });


//
    }
    private static JCTree.JCIf createCheck(VariableTree parameter, Context context) {
        TreeMaker factory = TreeMaker.instance(context);
        Names symbolsTable = Names.instance(context);

//        return factory.at(((JCTree) parameter).pos)
//                .If(factory.Parens(createIfCondition(factory, symbolsTable, parameter)),
//                    createIfBlock(factory, symbolsTable, parameter),
//                    null);

        return null;
    }

    private void addCheck(MethodTree method, VariableTree parameter, Context context) {
        JCTree.JCIf check = createCheck(parameter, context);
        JCTree.JCBlock body = (JCTree.JCBlock) method.getBody();
        body.stats = body.stats.prepend(check);
    }
}