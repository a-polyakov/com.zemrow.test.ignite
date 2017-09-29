package com.zemrow.test.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteCallable;

/**
 * 2 Распределенный вызов
 * пишем обычную функцию а выполняется на любой ноде кластера
 * полноценная балансировка нагрузки и тд
 *
 * @author Alexandr Polyakov
 */
public class Run02IgniteCall {
    public static void main(String[] args) {
        try (final Ignite ignite = Run01IgniteSimpleServer.initIgniteNote()) {
            final Object result = ignite.compute().call(new IgniteCallable<Object>() {
                @Override
                public Object call() {
                    final String result = "Node222: " + ignite.cluster().localNode().id() + " execute call";
                    System.out.println(result);
                    return result;
                }
            });
            System.out.println("Current node:++" + ignite.cluster().localNode().id() + " Result: " + result);
            Run01IgniteSimpleServer.waitInputLine();
        }
    }
}
