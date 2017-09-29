package com.zemrow.test.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteCallable;

import java.util.Collection;
import java.util.Scanner;

/**
 * 3 Заставить все ноды выполнить что-то
 *
 * @author Alexandr Polyakov
 */
public class Run03IgniteBroadcast {
    public static void main(String[] args) {
        try (final Ignite ignite = Run01IgniteSimpleServer.initIgniteNote()) {
            final Collection broadcastResults = ignite.compute().broadcast(new IgniteCallable() {
                @Override
                public Object call() {
                    final String result = "Node: " + ignite.cluster().localNode().id() + " execute broadcast";
                    System.out.println(result);
                    return result;
                }
            });
            System.out.println("Current node:++" + ignite.cluster().localNode().id() + " Result: " + broadcastResults);
            final Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            scanner.close();
        }
    }
}
