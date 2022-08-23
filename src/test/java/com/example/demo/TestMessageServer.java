package com.example.demo;

import com.example.demo.server.MessageServer;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestMessageServer {
    private static RSocket rSocket;
    @BeforeAll//在所有測試開始之前執行
    public static void setUpClient(){
        MessageServer.start();
        rSocket = RSocketConnector.connectWith(TcpClientTransport.create(6565)).block();//客戶端需要進行連接
    }


    @Test
    public void testFireAndForget(){
        getRequestPayload().flatMap(payload -> rSocket.fireAndForget(payload))
                .blockLast(Duration.ofMinutes(1));
    }

    @Test
    public void testRequestAndResponse(){
        getRequestPayload().flatMap(payload -> rSocket.requestResponse(payload))
                .doOnNext(response -> System.out.println("[RSocket測試類] 接收服務端響應數據:"+ response.getDataUtf8()))
                .blockLast(Duration.ofMinutes(1));
    }

    @Test
    public void testRequestAndStream(){
        getRequestPayload().flatMap(payload -> rSocket.requestStream(payload))
                .doOnNext(response -> System.out.println("[RSocket測試類] 接收服務端響應數據:"+ response.getDataUtf8()))
                .blockLast(Duration.ofMinutes(1));
    }

    @Test
    public void testRequestChannel(){
        rSocket.requestChannel(getRequestPayload())
                .doOnNext(response -> System.out.println("[RSocket測試類] 接收服務端響應數據:"+ response.getDataUtf8()))
                .blockLast(Duration.ofMinutes(1));
    }

    private static Flux<Payload> getRequestPayload(){ //傳遞所有的附加數據
        return Flux.just("test","hahahaha","springBoot","redis")
                .delayElements(Duration.ofSeconds(1))
                .map(DefaultPayload::create);
    }


    @AfterAll//所有測試結束關閉
    public static  void testStopServer(){
        MessageServer.stop();
    }
}
