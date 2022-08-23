package com.rscoket.server.service.action;

import com.rscoket.server.service.MessageService;
import com.rscoket.vo.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
@Slf4j
public class MessageAction {
    @Autowired
    private MessageService messageService;//注入業務類
    @MessageMapping("message.echo")
    public  Mono<Message> echoMessage(Mono<Message> messageMono){
        return messageMono
                //響應處理
                .doOnNext(msg ->this.messageService.echo(msg))
                //日誌處理
                .doOnNext(msg -> log.info("[消息接收]{}",msg));
    }
    @MessageMapping("message.delete")
    public void deleteMessage(Mono<String> title){
        title.doOnNext(msg -> log.info("[消息刪除]{}",msg)).subscribe();//日誌輸出
    }

    @MessageMapping("message.list")
    public Flux<Message> listMessage(Mono<String> title){
        return Flux.fromStream(this.messageService.list().stream());
    }

    @MessageMapping("message.get")
    public Flux<Message> getMessage(Flux<String> title){
       return  title.doOnNext(t -> log.info("[消息接收] title = {}",t))
               .map(titleInfo->titleInfo.toLowerCase())
               .map(this.messageService::get)
               .delayElements(Duration.ofSeconds(1));
    }
}
