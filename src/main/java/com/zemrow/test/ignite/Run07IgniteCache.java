package com.zemrow.test.ignite;

import com.zemrow.test.ignite.run07.entity.Relation;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import com.zemrow.test.ignite.run07.entity.Profile;
import com.zemrow.test.ignite.run07.entity.ProfileBridge;
import com.zemrow.test.ignite.run07.entity.RelationBridge;

import javax.cache.Cache;
import java.util.UUID;

/**
 * 7 распределенный кеш
 * держится в памяти
 * можно настроить чтобы сохранялся (в данном примере данные сбрасываются на диск по накоплению изменений или/и по времени)
 * настройка уровня надежности
 * поддержка SQL
 * @author Alexandr Polyakov
 */
public class Run07IgniteCache {
    public static void main(String[] args) {
        try (final Ignite ignite = Run01IgniteSimpleServer.initIgniteNote()) {

            final ProfileBridge personBridge = new ProfileBridge();
            final IgniteCache<Long, Profile> profileCache = ignite.getOrCreateCache(personBridge.getCacheConfiguration());

            if (profileCache.size() == 0) {
                long time = System.currentTimeMillis();
                profileCache.loadCache(null);
                time = System.currentTimeMillis() - time;
                System.out.println("profileCache size:" + profileCache.size() + " load: " + time);
            }

            final RelationBridge relationBridge = new RelationBridge();
            final IgniteCache<UUID, Relation> relationCache = ignite.getOrCreateCache(relationBridge.getCacheConfiguration());

            if (relationCache.size() == 0) {
                long time = System.currentTimeMillis();
                relationCache.loadCache(null);
                time = System.currentTimeMillis() - time;
                System.out.println("relationCache size:" + relationCache.size() + " load: " + time);
            }

//            profileCache.put(1L, new Profile(1L, "p1"));
//            profileCache.put(2L, new Profile(2L, "p2"));
//
//            final UUID id = UUID.randomUUID();
//            relationCache.put(id, new Relation(id, 1L, 2L));
//
            //TODO
            SqlQuery<UUID, Relation> sql = new SqlQuery(Relation.class, "base = ?");
            long count = 0;
            long time = System.currentTimeMillis();
            try (QueryCursor<Cache.Entry<UUID, Relation>> cursor = relationCache.query(sql.setArgs(1L))) {
                for (Cache.Entry<UUID, Relation> e : cursor) {
                    count++;
                }
            }
            time = System.currentTimeMillis() - time;
            System.out.println("count relation " + count + "  time " + time);


            Run01IgniteSimpleServer.waitInputLine();
        }
    }
}
