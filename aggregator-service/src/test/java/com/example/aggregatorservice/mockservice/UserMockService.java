package com.example.aggregatorservice.mockservice;

import com.youyk.user.StockTradeRequest;
import com.youyk.user.StockTradeResponse;
import com.youyk.user.UserInformation;
import com.youyk.user.UserInformationRequest;
import com.youyk.user.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class UserMockService extends UserServiceGrpc.UserServiceImplBase {
    @Override
    public void getUserInformation(UserInformationRequest request, StreamObserver<UserInformation> responseObserver) {
        if(request.getUserId() == 1){
            UserInformation user = UserInformation.newBuilder()
                    .setUserId(1)
                    .setBalance(100)
                    .setName("integration-test")
                    .build();

            responseObserver.onNext(user);
            responseObserver.onCompleted();
        }else{
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        }
    }

    @Override
    public void tradeStock(StockTradeRequest request, StreamObserver<StockTradeResponse> responseObserver) {
        StockTradeResponse response = StockTradeResponse.newBuilder()
                .setUserId(request.getUserId())
                .setTicker(request.getTicker())
                .setAction(request.getAction())
                .setPrice(request.getPrice())
                .setQuantity(request.getQuantity())
                .setTotalPrice(1000)
                .setBalance(0)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
