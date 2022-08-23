package com.rscoket.config;

import com.rscoket.constants.UploadConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;

@Configuration
public class RSocketConfig {
    @Bean
    public RSocketStrategies getRSocketStrategies(){
        return RSocketStrategies.builder()
                .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
                .metadataExtractorRegistry(metadataExtractorRegistry -> {
                    metadataExtractorRegistry.metadataToExtract(
                            MediaType.valueOf(UploadConstants.MIME_FILE_NAME),String.class,UploadConstants.FILE_NAME);
                    metadataExtractorRegistry.metadataToExtract(
                            MediaType.valueOf(UploadConstants.MIME_FILE_EXTENSION),String.class,UploadConstants.FILE_EXT);
                }).build();
    }
}
