package com.rscoket.server.service.action;

import com.rscoket.constants.UploadConstants;
import com.rscoket.type.UploadStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

@Controller
@Slf4j
public class UploadAction {
    @Value("${output.file.path:upload}")
    private Path outputPath;//文件保存路徑

    @MessageMapping("message.upload")
    public Flux<UploadStatus> upload(
            @Headers Map<String,Object> metaData,
            @Payload Flux<DataBuffer> content) throws Exception{
        log.info("[上傳檔案路徑] outputPath={}",this.outputPath);
        var fileName = metaData.get(UploadConstants.FILE_NAME);//獲取文件名稱
        var fileExt = metaData.get(UploadConstants.FILE_EXT);//文件後綴
        var path = Paths.get(fileName+"."+fileExt);//獲取文件操作路徑
        log.info("[文件上傳]fileName = {},FileExt = {},Path = {} ",fileName,fileExt,path);
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                this.outputPath.resolve(path),//解析出輸出路徑
                StandardOpenOption.CREATE, //文件創建
                StandardOpenOption.WRITE //文件寫入
        );//異步文件通道
        return Flux.concat(DataBufferUtils.write(content,channel)
                .map(s->UploadStatus.CHUNK_COMPLETED), Mono.just(UploadStatus.COMPLETED))
                .doOnComplete(()->{
                    try{
                        channel.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                })
                .onErrorReturn(UploadStatus.FAILED);//上傳失敗
    }
}
