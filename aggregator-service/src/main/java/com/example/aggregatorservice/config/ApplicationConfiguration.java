package com.example.aggregatorservice.config;



import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.protobuf.util.JsonFormat;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

@Configuration
@Slf4j
public class ApplicationConfiguration {

    @Bean
    public GrpcChannelConfigurer channelConfigurer(){
        return (channelBuilder, name) -> {
            log.info("channel builder '{}'", name);
            channelBuilder.executor(Executors.newCachedThreadPool()); // just for demo
        };
    }

    @Bean
    public ProtobufJsonFormatHttpMessageConverter protobufJsonFormatHttpMessageConverter() {
        //ignoringUnknownFields() 메소드는 파서가 알 수 없는 필드를 무시하도록 설정합니다.
        // 이는 JSON에 protobuf 메시지에 없는 필드가 포함되어 있을 때 유용합니다.
        //omittingInsignificantWhitespace() 메소드는 출력된 JSON에서 불필요한 공백을 제거하도록 설정합니다.
        // 이는 출력된 JSON을 더 읽기 쉽게 만듭니다.
        return new ProtobufJsonFormatHttpMessageConverter(
                JsonFormat.parser().ignoringUnknownFields(),
                JsonFormat.printer().omittingInsignificantWhitespace().includingDefaultValueFields()
        );
    }
}
