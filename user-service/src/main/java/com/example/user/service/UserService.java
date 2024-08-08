package com.example.user.service;


import com.example.user.service.handler.StockTradeRequestHandler;
import com.example.user.service.handler.UserInformationRequestHandler;
import com.youyk.user.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

//서버에 자동으로 등록된다

/**
 * 이 어노테이션을 사용하면,
 * 스프링 부트 애플리케이션은 애플리케이션 시작 시
 * 해당 클래스를 gRPC 서버로 자동 등록합니다.
 * 이렇게 하면 클라이언트는 gRPC 프로토콜을 사용하여
 * 이 서비스에 연결하고 메서드를 호출할 수 있습니다.
 */
@RequiredArgsConstructor
//Controller Class로 인식할 것 RestController처럼
@GrpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {
    private final UserInformationRequestHandler userRequestHandler;
    private final StockTradeRequestHandler tradeRequestHandler;
    @Override
    public void getUserInformation(UserInformationRequest request, StreamObserver<UserInformation> responseObserver) {
        UserInformation userInformation = this.userRequestHandler.getUserInformation(request);
        responseObserver.onNext(userInformation);
        responseObserver.onCompleted();
    }

    @Override
    public void tradeStock(StockTradeRequest request, StreamObserver<StockTradeResponse> responseObserver) {
        StockTradeResponse response = TradeAction.SELL.equals(request.getAction()) ?
                this.tradeRequestHandler.sellStock(request) :
                this.tradeRequestHandler.buyStock(request);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
