package com.rscoket.test;

import com.rscoket.StartRSocketClientApplication;
import com.rscoket.constants.UploadConstants;
import com.rscoket.type.UploadStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringBootTest(classes = StartRSocketClientApplication.class)
public class TestUploadRSocket {
    @Autowired
    private Mono<RSocketRequester> requesterMono;//來進行服務調用
    @Value("classpath:/images/goodUploadImg.jpg")
    private Resource resource;

    @Test
    public void testUpload() {
        String fileName = "123-" + UUID.randomUUID();
        String fileExt = this.resource.getFilename().substring(this.resource.getFilename().lastIndexOf(".") + 1);
        Flux<DataBuffer> resourceFlux = DataBufferUtils.read(
                        this.resource, new DefaultDataBufferFactory(), 1024)
                .doOnNext(s -> System.out.println("文件上傳" + s));
        Flux<UploadStatus> uploadStatusFlux = this.requesterMono
                .map(r -> r.route("message.upload")//配置路由地址
                        .metadata(metadataSpec -> {
                            System.out.println("[上傳測試] 文件名稱:" + fileName + "." + fileExt);
                            metadataSpec.metadata(fileName, MimeType.valueOf(UploadConstants.MIME_FILE_NAME));//配置文件名稱
                            metadataSpec.metadata(fileExt, MimeType.valueOf(UploadConstants.MIME_FILE_EXTENSION));//配置文件後綴
                        }).data(resourceFlux))//配置文件上傳
                .flatMapMany(r->r.retrieveFlux(UploadStatus.class))
                .doOnNext(o-> System.out.println("上傳進度:"+o));
        uploadStatusFlux.blockLast();//進行阻塞
    }
}
