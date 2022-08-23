package com.rscoket.test;

import com.rscoket.StartRSocketClientApplication;
import com.rscoket.vo.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringBootTest(classes = StartRSocketClientApplication.class)
public class TestMessageRSocket {
    @Autowired
    private Mono<RSocketRequester> requesterMono;
    @Test
    public void testEchoMessage() {//測試服務響應
        this.requesterMono.map(r -> r.route("message.echo")
                        .data(new Message("王大明", "手速100的男人")))//配置請求數據
                .flatMap(r -> r.retrieveMono(Message.class))
                .doOnNext(o -> System.out.println(o)).block();
    }

    @Test
    public void testDeleteMessage() {//測試服務響應
        this.requesterMono.map(r -> r.route("message.delete")
                        .data("test"))//配置請求數據
                .flatMap(RSocketRequester.RetrieveSpec::send)
                .block();//發送不接收數據
    }

    @Test
    public void testListMessage() {//測試服務響應
        this.requesterMono.map(r -> r.route("message.list"))
                .flatMapMany(r-> r.retrieveFlux(Message.class))
                .doOnNext(o-> System.out.println(o))
                .blockLast();//發送不接收數據
    }

    @Test
    public void testGetMessage(){
        Flux<String> titles = Flux.just("我真棒","學習RSocket","spring Boot");//要獲取的消息的標題
        Flux<Message> messageFlux = this.requesterMono.map(r-> r.route("message.get").data(titles))
                .flatMapMany(r->r.retrieveFlux(Message.class))
                .doOnNext(o-> System.out.println(o));
        messageFlux.blockLast();
    }
}
