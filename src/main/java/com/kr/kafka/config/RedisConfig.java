package com.kr.kafka.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedisConfig {

//    @Value("${spring.data.redis.host}")
//    private String host;
//
//    @Value("${spring.data.redis.port}")
//    private int port;

//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(host);
//        redisStandaloneConfiguration.setPort(port);
//        return new LettuceConnectionFactory(redisStandaloneConfiguration);
//    }
//
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        log.info("▶ [레디스] 레디스 템플릿 Bean 등록");
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 레디스 key 시리얼 라이즈 string 처리 (default: JdkSerializationRedisSerializer)
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    private final RedisInfo redisInfo;
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(redisInfo.getNodes());
        redisClusterConfiguration.setMaxRedirects(redisInfo.getMaxRedirects());

        //----------------- (1) Socket Option
        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(100L)) // redis connect을 위한 timeout 100ms
                .keepAlive(true) // redis connect를 tcp keepAlivce 옵션 활용, 그러나 java 11 이상 활용해야하며 기본값은 false
                .build();

        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .dynamicRefreshSources(true) // 클러스터의 동적 소스 갱신 활성화
                .enableAllAdaptiveRefreshTriggers() // 노드들의 상태 변화가 발생하면 해당 내용을 반영하는 설정
                .enablePeriodicRefresh(Duration.ofSeconds(10L))
                .build();

        ClientOptions clientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
                .pingBeforeActivateConnection(true) // connection 연결을 위해 ping 명령어로 검증 - 기본값 true
                .autoReconnect(true) // 자동 재접속 연결 - 기본값 true
                .socketOptions(socketOptions)
                .maxRedirects(3)// moved, ask 등의 redirect 허용 횟수, 노드 개수와 동일하게 구성하면 명령어 실패 확률 낮아짐
                .build();

        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(150L)) // 명령어 타임아웃 시간 150ms
                .clientOptions(clientOptions)
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build();

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration);
        connectionFactory.setValidateConnection(false); // redis connection을 가져올때 검증 여부 - 기본값 false

        return connectionFactory;
    }

}
