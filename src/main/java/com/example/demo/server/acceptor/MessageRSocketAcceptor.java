package com.example.demo.server.acceptor;

import com.example.demo.server.handler.MessageRSocketHandler;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import reactor.core.publisher.Mono;

public class MessageRSocketAcceptor implements SocketAcceptor {
    @Override
    //實現RSocket連接處理
    public Mono<RSocket> accept(ConnectionSetupPayload connectionSetupPayload, RSocket rSocket) {
        return Mono.just(new MessageRSocketHandler());// 配置自己的處理類
    }
}
