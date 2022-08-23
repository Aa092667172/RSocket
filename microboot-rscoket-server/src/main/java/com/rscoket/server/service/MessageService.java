package com.rscoket.server.service;

import com.rscoket.vo.Message;
import org.springframework.stereotype.Service;

import java.util.List;

//本次簡化編寫樣本,不在創建業務介面,直接實現業務類處理
@Service
public class MessageService {
    //響應集合數據
    public List<Message> list(){
        return List.of(
                new Message("lin yu kai","測試"),
                new Message("spring","boot"),
                new Message("readis","Server 1")
        );
    }
    public Message get(String title){
        return new Message(title,"[" + title +"]");
    }
    public Message echo(Message message){
        message.setTitle("[ECHO]" + message.getTitle());
        message.setContent("[ECHO]" + message.getContent());
        return message;
    }
}
