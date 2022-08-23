package com.example.demo.server.handler;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class MessageRSocketHandler implements RSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        //Payload表示所有的附加數據訊息,對於RSocket通訊來講,所有的數據都通過此結構傳輸
        //一般這種無響應的操作可以應用在日紀錄的模式上,例如:客戶端傳送一個日誌,是不需要等待響應的
        String message = payload.getMetadataUtf8(); //獲取數據
        log.info("fireAndForget接收請求數據:{}",message);
        return Mono.empty();//返回空數據
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        String message = payload.getDataUtf8(); //獲取數據
        log.info("requestResponse接收請求數據：{}",message);
        return Mono.just(DefaultPayload.create("[echo]" + message));
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        String message = payload.getDataUtf8(); //獲取數據
        log.info("requestStream接收請求數據:{}",message);
        return Flux.fromStream(
                message.chars()//接收一個字符陣列
                        .mapToObj(Character::toUpperCase)//將文字轉為大寫
                .map(Object::toString)//透過ToString轉換為字串
                .map(DefaultPayload::create));//創建Payload的附加屬性
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return Flux.from(payloads).map(Payload::getDataUtf8).map(msg->{
            log.info("requestChannel接收請求參數:{}",msg);
            return msg;
        }).map(DefaultPayload::create);
    }
}
