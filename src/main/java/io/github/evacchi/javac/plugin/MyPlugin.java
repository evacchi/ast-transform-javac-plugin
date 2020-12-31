package io.github.evacchi.javac.plugin;

import java.util.List;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
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

    private TreeMaker treeMaker;
    private Names names;

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
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);

        task.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent e) {
                System.out.print("STARTED ");
                System.out.println(e);
            }

            @Override
            public void finished(TaskEvent e) {
                if (e.getKind() != TaskEvent.Kind.PARSE) {
                    return;
                }
                e.getCompilationUnit().accept(new TreeScanner<Void, Void>() {

                    @Override
                    public Void visitMethodInvocation(MethodInvocationTree mi, Void unused) {
                        MemberSelectTree m = (MemberSelectTree) mi.getMethodSelect();
                        if (m.getIdentifier().contentEquals("filter")) {
                            injectIndex(mi, treeMaker, names);
                        }

                        return null;
                    }

                }, null);
            }
        });


//
    }

    private void injectIndex(MethodInvocationTree mi, TreeMaker treeMaker, Names names) {
        List<? extends ExpressionTree> arguments = mi.getArguments();
        JCTree.JCMethodInvocation index = treeMaker.Apply(com.sun.tools.javac.util.List.nil(),
                                                          treeMaker.Ident(names.fromString("index")),
                                                          (com.sun.tools.javac.util.List<JCTree.JCExpression>) arguments);
        ((JCTree.JCMethodInvocation) mi).args = com.sun.tools.javac.util.List.of(index);
    }

}