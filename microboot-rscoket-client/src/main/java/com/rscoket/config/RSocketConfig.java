package com.rscoket.config;

import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class RSocketConfig {
    @Bean
    public RSocketStrategies getRSocketStrategies(){
        return RSocketStrategies.builder()
                .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))//定義傳輸編碼器
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))//解碼器
                .build();
    }

    @Bean
    public Mono<RSocketRequester> getRSocketRequester(RSocketRequester.Builder builder){
        return Mono.just(
                builder.rsocketConnector(rSocketConnector-> rSocketConnector.reconnect(
                        Retry.fixedDelay(2, Duration.ofSeconds(2))))//兩秒內如果無法正常連
                        .dataMimeType(MediaType.APPLICATION_CBOR)
                        .transport(TcpClientTransport.create(6869)));
    }
}
