package su.foxogram.structures;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Objects;

import static su.foxogram.Main.dotenv;

public class Cache {
    private static Jedis jedis;

    public static void main(String[] args) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        try (JedisPool jedisPool = new JedisPool(poolConfig, dotenv.get("REDIS_HOST"), Integer.parseInt(Objects.requireNonNull(dotenv.get("REDIS_PORT"))), 2000, dotenv.get("REDIS_PASS"))) {
            jedis = jedisPool.getResource();
        }
    }

    public static void set(String field, String value) {
        jedis.set(field, value);
    }

    public static void get(String field) {
        jedis.get(field);
    }
}
