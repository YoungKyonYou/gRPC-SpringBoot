package com.example.aggregatorservice.service;

import com.google.protobuf.Empty;
import com.youyk.stock.StockServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

//application이 시작되면 메서드가 실행된다.
//CommandLineRunner 인터페이스를 구현하여 애플리케이션 시작 시 특정 코드를 실행하도록 합니다.
@RequiredArgsConstructor
@Service
public class PriceUpdateSubscriptionInitializer implements CommandLineRunner {

    @GrpcClient("stock-service")
    private StockServiceGrpc.StockServiceStub stockClient;

    private final PriceUpdateListener listener;


    //Spring Boot 애플리케이션 시작 시 run 메소드가 자동으로 호출됩니다.
    // 이 메소드에서는 gRPC 서비스를 호출하여 주식 가격 업데이트를 구독합니다.
    //stockClient.getPriceUpdates(Empty.getDefaultInstance(), listener);
    // 코드는 stock-service gRPC 서비스의 getPriceUpdates 메소드를 호출합니다.
    // 이 메소드는 서버에서 클라이언트로 스트리밍하는 메소드로
    // , 서버에서 주식 가격 업데이트를 보내면 클라이언트에서 이를 받아 처리합니다.
    @Override
    public void run(String... args) throws Exception {
        //여기서 application이 처음 시작되면 이걸 호출하게 되고 이걸 호출하면 PriceUpdateListener가 생성되면서 PriceUpdateListener의 onNext가 호출된다.
        this.stockClient
                .withWaitForReady() //stockService가 준비될 때까지 기다린다. 블럭을 하지 않는다. stockService가 준비되지 않을 때 이게 없으면 바로 exception이 발생함 (stock update를 기다려야 함)
                .getPriceUpdates(Empty.getDefaultInstance(), listener);
    }
}
