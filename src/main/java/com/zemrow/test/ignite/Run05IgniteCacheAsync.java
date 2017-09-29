package com.zemrow.test.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteInClosure;

import java.util.UUID;

/**
 * 5 асинхронный API, почти весь API можно вызвать в асинхронном режиме
 * например вызвать функцию получить future что-то продолжать делать и по завершению проверить а не завершилась та функция
 * или дождаться ее выполнения или добавить слушателя
 *
 * @author Alexandr Polyakov
 */
public class Run05IgniteCacheAsync {
    public static void main(String args[]){
        try(Ignite ignite= Run01IgniteSimpleServer.initIgniteNote()){
            CacheConfiguration<UUID, String> cfg=new CacheConfiguration<>("demoCache");
            cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

            IgniteCache<UUID, String> cache=ignite.getOrCreateCache(cfg);

            final UUID aId = UUID.randomUUID();
            final IgniteFuture<Void> fut =cache.putAsync(aId, "a");

            fut.listen(new IgniteInClosure<IgniteFuture<Void>>() {
                @Override
                public void apply(IgniteFuture<Void> objectIgniteFuture) {
                    System.out.println(objectIgniteFuture.get());
                }
            });

            fut.get();


//            System.out.println(cache.get(bId));
        }
    }
}
