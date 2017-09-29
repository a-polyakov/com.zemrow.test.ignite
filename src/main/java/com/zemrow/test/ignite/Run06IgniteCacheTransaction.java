package com.zemrow.test.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;

import java.util.UUID;

/**
 * 6 поддержка транзакций
 *
 * @author Alexandr Polyakov
 */
public class Run06IgniteCacheTransaction {
    public static void main(String args[]){
        try(Ignite ignite= Run01IgniteSimpleServer.initIgniteNote()){
            CacheConfiguration<UUID, String> cfg=new CacheConfiguration<>("demoCache");
            cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

            IgniteCache<UUID, String> cache=ignite.getOrCreateCache(cfg);

            final UUID aId = UUID.randomUUID();
            cache.put(aId, "a");

            try (Transaction tx = ignite.transactions().txStart(TransactionConcurrency.PESSIMISTIC, TransactionIsolation.REPEATABLE_READ)){

                String s=cache.get(aId);
                cache.put(aId, s+"a");

                tx.commit();
            }

            System.out.println(cache.get(aId));
        }
    }
}
