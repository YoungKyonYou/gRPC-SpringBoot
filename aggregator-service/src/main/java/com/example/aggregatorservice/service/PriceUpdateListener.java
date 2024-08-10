package com.example.aggregatorservice.service;

import com.example.aggregatorservice.dto.PriceUpdateDto;
import com.youyk.stock.PriceUpdate;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j

@Service
public class PriceUpdateListener implements StreamObserver<PriceUpdate> {

    private final Set<SseEmitter> emitters = Collections.synchronizedSet(new HashSet<>());
    private final long sseTimeout;

    public PriceUpdateListener(@Value("${sse.timeout:300000}") long sseTimeout) {
        this.sseTimeout = sseTimeout;
    }

    public SseEmitter createEmitter(){
        SseEmitter emitter = new SseEmitter(this.sseTimeout);
        this.emitters.add(emitter);

        //timeout 시 브라우저는 값을 더 이상 받지 않는다. 그렇기 때문에 set에 있을 필요가 없음으로 지운다.
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        emitter.onError(ex -> this.emitters.remove(emitter));
        return emitter;
    }

    @Override
    public void onNext(PriceUpdate priceUpdate) {
        PriceUpdateDto dto = new PriceUpdateDto(priceUpdate.getTicker().toString(), priceUpdate.getPrice());
        //emit이 성공적이면 우리는 지속적으로 emit 할 것이고 error(false)가 발생하면 emitter를 제거한다.
        //removeIf는 true가 되면 제거한다.
        this.emitters.removeIf(e -> !this.send(e, dto));
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("streaming error", throwable);
        this.emitters.forEach(e -> e.completeWithError(throwable));
        //Removes all of the elements from this set
        this.emitters.clear();
    }

    @Override
    public void onCompleted() {
        this.emitters.forEach(ResponseBodyEmitter::complete);
        //Removes all of the elements from this set
        this.emitters.clear();
    }

    private boolean send(SseEmitter emitter, Object o){
        try {
            emitter.send(o);
            return true;
        } catch (IOException e) {
            log.warn("sse error {}", e.getMessage());
            return false;
        }
    }
}
