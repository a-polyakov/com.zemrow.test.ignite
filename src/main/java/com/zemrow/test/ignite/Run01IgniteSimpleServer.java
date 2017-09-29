package com.zemrow.test.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.EventType;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 1 Кластер
 * автоматический мониторинг нод кластера
 * поиск и добавление в сети по маске
 *
 * @author Alexandr Polyakov
 */
public class Run01IgniteSimpleServer implements Serializable {

    static final long serialVersionUID = 1L;

    public static Ignite initIgniteNote() {
        return initIgnite(false);
    }

    public static Ignite initIgniteClient() {
        return initIgnite(true);
    }

    private static Ignite initIgnite(boolean clientMode) {
        final IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(clientMode);
        // подгрузка классав (в теории одна новая нода должна обновить все остальные)
        cfg.setPeerClassLoadingEnabled(true);
//        cfg.setDeploymentMode(DeploymentMode.ISOLATED);
        cfg.setDeploymentMode(DeploymentMode.CONTINUOUS);
        cfg.setIncludeEventTypes(EventType.EVTS_TASK_EXECUTION);
//        cfg.setIncludeEventTypes(EventType.EVTS_CACHE);
        final TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        final TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder(true);
        tcpDiscoveryVmIpFinder.setAddresses(Arrays.asList(new String[]{"127.0.0.1:47700..47710"}));
        tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
        tcpDiscoverySpi.setLocalPort(47700);
        tcpDiscoverySpi.setLocalPortRange(10);
        cfg.setDiscoverySpi(tcpDiscoverySpi);


        final Ignite ignite = Ignition.start(cfg);
        return ignite;
    }

    public static void waitInputLine(){
        final Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
    }

    public static void main(String args[]) {
        try (Ignite ignite = initIgniteNote()) {
            waitInputLine();
        }
    }
}
