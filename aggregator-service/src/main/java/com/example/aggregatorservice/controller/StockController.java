package com.example.aggregatorservice.controller;

import com.example.aggregatorservice.service.PriceUpdateListener;
import com.google.common.util.concurrent.Uninterruptibles;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 순서
 * 1. Springboot appliction이 켜진다
 * 2. PriceUpdateSubscriptionInitializer의 run 메소드가 실행된다.
 * 3. PriceUpdateSubscriptionInitializer의 run 메소드에서 stockClient.getPriceUpdates(Empty.getDefaultInstance(), listener);가 실행된다.
 * 4. PriceUpdateListener의 onNext 메소드가 실행된다.
 * 5. PriceUpdateListener의 onNext 메소드에서 emitters.removeIf(e -> !this.send(e, dto));가 실행된다.
 */
@RestController
@RequestMapping("stock")
@RequiredArgsConstructor
public class StockController {
    private final PriceUpdateListener listener;

    //"text/event-stream"이라는 MIME 타입을 나타냅니다.
    //"text/event-stream" MIME 타입은 Server-Sent Events (SSE)를 위한 표준 MIME 타입입니다.
    // SSE는 서버에서 클라이언트로 실시간으로 데이터를 전송하는 기술입니다.
    //TEXT_EVENT_STREAM_VALUE는 proto message를 지원하지 않는다. 그렇기 때문에 dto 패키지 사용한다.
    @GetMapping(value = "updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    //TradeController의 trade 메소드나 UserController의 getUserInformation 메소드는 unary request인데 반면 SseEmitter를 반환하는 메소드는 unary response가 아닌 server streaming response입니다.
    public SseEmitter priceUpdates(){
      /*  //timeout 2초, 2초동안만 실행되고 멈춤
        SseEmitter emitter = new SseEmitter(2000L);
        Runnable runnable = () -> {
            for (int i = 0; i < 5; i++) {
                Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                try {
                    emitter.send("hello-"+i);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            emitter.complete();
        };

        new Thread(runnable).start();
        return emitter;*/

        return listener.createEmitter();
    }
}
