package com.example.stockservice.service;


import com.example.stockservice.Tickers;
import com.example.stockservice.service.event.StockPriceEvent;
import com.google.protobuf.Empty;
import com.youyk.stock.PriceUpdate;
import com.youyk.stock.StockPriceRequest;
import com.youyk.stock.StockPriceResponse;
import com.youyk.stock.StockServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

@GrpcService
public class StockServiceImpl extends StockServiceGrpc.StockServiceImplBase implements
        ApplicationListener<StockPriceEvent> {
    private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    private final Tickers tickers;

    private final Set<ServerCallStreamObserver<PriceUpdate>> set;

    public StockServiceImpl(Tickers tickers) {
        this.tickers = tickers;
        this.set = Collections.synchronizedSet(new HashSet<>());
    }


    @Override
    public void getStockPrice(StockPriceRequest request, StreamObserver<StockPriceResponse> responseObserver) {
        this.tickers.getPrice(request.getTicker())
                .map(v -> StockPriceResponse.newBuilder().setTicker(request.getTicker()).setPrice(v.intValue()).build())
                .ifPresentOrElse(v -> {
                    responseObserver.onNext(v);
                    responseObserver.onCompleted();
                },
                        () -> responseObserver.onError((Throwable) Status.INVALID_ARGUMENT.withDescription(String.valueOf(request.getTicker()) + " is not valid").asRuntimeException()));
    }

    //AggregatorServiceApplication이 켜지면 PriceUpdateSubscriptionInitializer가 실행되고 이 메소드가 실행된다.
    @Override
    public void getPriceUpdates(Empty request, StreamObserver<PriceUpdate> responseObserver) {
        ServerCallStreamObserver<PriceUpdate> o = (ServerCallStreamObserver<PriceUpdate>)responseObserver;
        this.set.add(o);
        //클라이언트가 스트림을 취소했을 때 호출될 콜백 핸들러를 설정합니다.
        o.setOnCancelHandler(() -> cancel(o));
        //스트림이 닫혔을 때 호출될 콜백 핸들러를 설정합니다.
        o.setOnCloseHandler(() -> cancel(o));
    }

    public void onApplicationEvent(StockPriceEvent event) {
        for (ServerCallStreamObserver<PriceUpdate> o : this.set)
            o.onNext(event.getPriceUpdate());
    }

    private void cancel(ServerCallStreamObserver<PriceUpdate> o) {
        log.info("price updates observer cancelled");
        this.set.remove(o);
    }
}
