package com.example.demo.server;

import com.example.demo.server.acceptor.MessageRSocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.Disposable;

public class MessageServer {
    private static Disposable disposable; //用於釋放任務

    public static void start() {
        RSocketServer rSocketServer = RSocketServer.create();//創建服務端
        rSocketServer.acceptor(new MessageRSocketAcceptor()); //創建連接器
        rSocketServer.payloadDecoder(PayloadDecoder.ZERO_COPY);//採用0拷貝技術
        disposable = rSocketServer.bind(TcpServerTransport.create(6565)).subscribe();//開啟訂閱
    }

    public static void stop() {//服務啟動
        disposable.dispose();//釋放
    }
}
