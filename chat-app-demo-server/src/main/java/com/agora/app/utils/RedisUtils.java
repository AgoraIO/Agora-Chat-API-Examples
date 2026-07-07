package com.agora.app.utils;

import com.agora.app.config.redis.RedisConfigProperties;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class RedisUtils {

    private RedisUtils() {
    }

    private static final String DEFAULT_PASSWORD = "default_password";
    private static final String CLUSTER = "cluster";
    private static final String SENTINEL = "sentinel";

    public static RedisConnectionFactory connectionFactory(
            RedisConfigProperties.Property property) {
        Class clazz = JedisConnectionFactory.class;
        if ("lettuce".equals(property.getConnectionFactory())) {
            clazz = LettuceConnectionFactory.class;
        }
        return connectionFactory(property, null, clazz);
    }

    public static RedisConnectionFactory connectionFactory(
            RedisConfigProperties.Property property,
            ClientResources clientResources,
            Class<? extends RedisConnectionFactory> clazz) {

        JedisClientConfiguration jedisClientConfiguration = getJedisClientConfiguration(property);

        LettuceClientConfiguration lettuceClientConfiguration =
                getPoolClientConfiguration(property, clientResources);

        RedisConfiguration configuration =
                getRedisConfiguration(property);

        if (configuration instanceof RedisClusterConfiguration) {
            RedisClusterConfiguration clusterConfiguration =
                    (RedisClusterConfiguration) configuration;
            if (JedisConnectionFactory.class.equals(clazz)) {
                JedisConnectionFactory connectionFactory = new JedisConnectionFactory(
                        clusterConfiguration, jedisClientConfiguration);
                connectionFactory.afterPropertiesSet();
                return connectionFactory;
            } else if (LettuceConnectionFactory.class.equals(clazz)) {
                LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
                        clusterConfiguration, lettuceClientConfiguration);
                connectionFactory.afterPropertiesSet();
                return connectionFactory;
            }
            // not match connection factory

        } else if (configuration instanceof RedisSentinelConfiguration) {

            if (JedisConnectionFactory.class.equals(clazz)) {
                JedisConnectionFactory connectionFactory =
                        new JedisConnectionFactory((RedisSentinelConfiguration) configuration,
                                jedisClientConfiguration);
                connectionFactory.afterPropertiesSet();
                return connectionFactory;
            } else if (LettuceConnectionFactory.class.equals(clazz)) {
                LettuceConnectionFactory connectionFactory =
                        new LettuceConnectionFactory((RedisSentinelConfiguration) configuration);
                connectionFactory.afterPropertiesSet();
                return connectionFactory;
            }

        } else if (configuration instanceof RedisStandaloneConfiguration) {
            if (JedisConnectionFactory.class.equals(clazz)) {
                JedisConnectionFactory connectionFactory =
                        new JedisConnectionFactory((RedisStandaloneConfiguration) configuration,
                                jedisClientConfiguration);
                connectionFactory.afterPropertiesSet();
                return connectionFactory;
            } else if (LettuceConnectionFactory.class.equals(clazz)) {
                LettuceConnectionFactory connectionFactory =
                        new LettuceConnectionFactory((RedisStandaloneConfiguration) configuration,
                                lettuceClientConfiguration);
                connectionFactory.afterPropertiesSet();
                return connectionFactory;

            }
        }
        return null;
    }

    private static RedisConfiguration getRedisConfiguration(
            RedisConfigProperties.Property property) {

        List<String> uris = new ArrayList<>(Arrays.asList(property.getNodes().split(",")));

        if (CLUSTER.equalsIgnoreCase(property.getType())) {

            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
            redisClusterConfiguration.setMaxRedirects(property.getMaxRedirects());

            List<RedisNode> nodeList = new ArrayList<>();

            for (String uriString : uris) {
                URI uri = URI.create(uriString);
                RedisNode redisNode = new RedisNode(uri.getHost(), uri.getPort());
                nodeList.add(redisNode);
            }

            redisClusterConfiguration.setClusterNodes(nodeList);

            if (!property.getPassword().equals(DEFAULT_PASSWORD)) {
                redisClusterConfiguration.setPassword(RedisPassword.of(property.getPassword()));
            }

            return redisClusterConfiguration;

        } else if (SENTINEL.equals(property.getType())) {

            RedisSentinelConfiguration redisSentinelConfiguration =
                    new RedisSentinelConfiguration();

            redisSentinelConfiguration.setMaster(property.getMaster());

            List<RedisNode> nodeList = new ArrayList<>();
            uris.forEach(uriString -> {
                URI uri = URI.create(uriString);
                RedisNode redisNode = new RedisNode(uri.getHost(), uri.getPort());
                nodeList.add(redisNode);
            });
            if (!property.getPassword().equals(DEFAULT_PASSWORD)) {
                redisSentinelConfiguration.setPassword(RedisPassword.of(property.getPassword()));
            }
            redisSentinelConfiguration.setSentinels(nodeList);
            return redisSentinelConfiguration;
        }

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        URI uri = URI.create(uris.get(0));
        configuration.setHostName(uri.getHost());
        configuration.setPort(uri.getPort());
        if (!property.getPassword().equals(DEFAULT_PASSWORD)) {
            configuration.setPassword(RedisPassword.of(property.getPassword()));
        }

        return configuration;
    }

    private static JedisClientConfiguration getJedisClientConfiguration(
            RedisConfigProperties.Property property) {
        JedisPoolConfig jedisPoolConfig = getJedisPoolConfig(property);
        JedisClientConfiguration.JedisClientConfigurationBuilder builder =
                JedisClientConfiguration.builder();

        if (Boolean.TRUE.equals(property.getSsl())) {
            builder.useSsl();
        }

        return builder
                .usePooling()
                .poolConfig(jedisPoolConfig)
                .and()
                .readTimeout(Duration.ofMillis(property.getReadTimeout()))
                .connectTimeout(Duration.ofMillis(property.getConnectTimeout()))
                .build();
    }

    private static LettuceClientConfiguration getPoolClientConfiguration(
            RedisConfigProperties.Property property, ClientResources clientResources) {

        JedisPoolConfig jedisPoolConfig = getJedisPoolConfig(property);

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder =
                LettucePoolingClientConfiguration.builder();
        builder.poolConfig(jedisPoolConfig);

        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions
                .builder()
                .closeStaleConnections(true)
                .enablePeriodicRefresh()
                .enableAdaptiveRefreshTrigger()
                .enableAllAdaptiveRefreshTriggers()
                .build();

        ClusterClientOptions.Builder options = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions);

        ClusterClientOptions clusterClientOptions = options.build();

        builder.clientOptions(clusterClientOptions);

        if (clientResources != null) {
            builder.clientResources(clientResources);
        }

        if (Boolean.TRUE.equals(property.getSsl())) {
            builder.useSsl();
        }

        return builder.build();
    }

    private static JedisPoolConfig getJedisPoolConfig(RedisConfigProperties.Property property) {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        if (property.getMaxTotal() != null) {
            jedisPoolConfig.setMaxTotal(property.getMaxTotal());
        }
        if (property.getMaxIdle() != null) {
            jedisPoolConfig.setMaxIdle(property.getMaxIdle());
        }

        if (property.getMinIdle() != null) {
            jedisPoolConfig.setMinIdle(property.getMinIdle());
        }

        if (property.getMaxWait() != null) {
            jedisPoolConfig.setMaxWaitMillis(property.getMaxWait());
        }
        if (property.getTimeBetweenEvictionRuns() != null) {
            jedisPoolConfig.setTimeBetweenEvictionRunsMillis(property.getTimeBetweenEvictionRuns());
        }

        if (property.getMinEvictableIdleTimeMillis() != null) {
            jedisPoolConfig.setMinEvictableIdleTimeMillis(property.getMinEvictableIdleTimeMillis());
        }

        if (property.getTestOnBorrow() != null) {
            jedisPoolConfig.setTestOnBorrow(property.getTestOnBorrow());
        }

        if (property.getTestOnCreate() != null) {
            jedisPoolConfig.setTestOnCreate(property.getTestOnCreate());
        }

        if (property.getTestOnReturn() != null) {
            jedisPoolConfig.setTestOnReturn(property.getTestOnReturn());
        }
        return jedisPoolConfig;
    }
}
