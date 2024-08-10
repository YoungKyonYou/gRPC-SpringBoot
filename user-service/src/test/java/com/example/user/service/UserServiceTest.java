package com.example.user.service;

import com.youyk.common.Ticker;
import com.youyk.user.StockTradeRequest;
import com.youyk.user.StockTradeResponse;
import com.youyk.user.TradeAction;
import com.youyk.user.UserInformation;
import com.youyk.user.UserInformationRequest;
import com.youyk.user.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * gRPC 서버의 포트를 -1로 설정하면, 실제 네트워크 포트를 사용하지 않고,
 * 프로세스 내부에서 통신하도록 설정합니다. 이는 통합 테스트에서 자주 사용되는 설정으로,
 * 실제 네트워크 환경을 사용하지 않고도 서버와 클라이언트 간의 통신을 테스트할 수 있게 해줍니다.\
 *
 *
 * 프로세스 내부에서 통신할 때 사용할 이름을 integration-test로 설정합니다.
 * 이 이름은 클라이언트가 서버에 연결할 때 사용됩니다.
 *
 * gRPC 클라이언트가 서버에 연결할 주소를 설정합니다. in-process:integration-test는
 * 프로세스 내부에서 integration-test라는 이름으로 실행되는 서버에 연결하라는 의미입니다.
 * 이 설정 덕분에 네트워크를 거치지 않고도 서버와 클라이언트 간의 통신을 테스트할 수 있습니다.
 */
@SpringBootTest(properties = {
        "grpc.server.port = -1",
        "grpc.server.in-process-name=integration-test",
        "grpc.client.user-service.address=in-process:integration-test"
})
public class UserServiceTest {

    // @GrpcClient의 "user-service"는 위 @SpringbootTest의 설정에서
    //grpc.client.user-service.address=in-process:integration-test로 설정한 이름과 동일해야 합니다.
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub stub;

    @Test
    public void test(){
        UserInformationRequest request = UserInformationRequest.newBuilder()
                .setUserId(1)
                .build();

        UserInformation response = this.stub.getUserInformation(request);

        Assertions.assertEquals(10_000, response.getBalance());
        Assertions.assertEquals("Sam", response.getName());
        Assertions.assertTrue(response.getHoldingsList().isEmpty());
    }

    @Test
    public void unknownUserTest(){
        StatusRuntimeException ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            UserInformationRequest request = UserInformationRequest.newBuilder()
                    .setUserId(10)
                    .build();

            this.stub.getUserInformation(request);

        });

        Assertions.assertEquals(Code.NOT_FOUND, ex.getStatus().getCode());


    }

    @Test
    public void unknownTickerBuyTest(){
        StatusRuntimeException ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            StockTradeRequest request = StockTradeRequest.newBuilder()
                    .setUserId(1)
                    .setPrice(1)
                    .setQuantity(1)
                    .setAction(TradeAction.BUY)
                    .build();
            this.stub.tradeStock(request);
        });
        Assertions.assertEquals(Code.INVALID_ARGUMENT, ex.getStatus().getCode());
    }

    @Test
    public void insufficientSharesTest(){
        StatusRuntimeException ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            StockTradeRequest request = StockTradeRequest.newBuilder()
                    .setUserId(1)
                    .setPrice(1)
                    .setQuantity(1000)
                    .setTicker(Ticker.AMAZON)
                    .setAction(TradeAction.SELL)
                    .build();
            this.stub.tradeStock(request);
        });
        Assertions.assertEquals(Code.FAILED_PRECONDITION, ex.getStatus().getCode());
    }

    @Test
    public void insufficientBalanceTest(){
        StatusRuntimeException ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            StockTradeRequest request = StockTradeRequest.newBuilder()
                    .setUserId(1)
                    .setPrice(1)
                    .setQuantity(10001)
                    .setTicker(Ticker.AMAZON)
                    .setAction(TradeAction.BUY)
                    .build();

            this.stub.tradeStock(request);
        });
        Assertions.assertEquals(Code.FAILED_PRECONDITION, ex.getStatus().getCode());
    }

    @Test
    public void buySellTest(){
        //buy
        //아마존 주식을 100의 가격으로 user2가 5개 구매
        StockTradeRequest buyRequest = StockTradeRequest.newBuilder()
                .setUserId(2)
                .setPrice(100)
                .setQuantity(5)
                .setTicker(Ticker.AMAZON)
                .setAction(TradeAction.BUY)
                .build();

        StockTradeResponse buyResponse = this.stub.tradeStock(buyRequest);

        //validate balance
        //5개 구매했으니 남은 돈은 10000에서 500을 뺀 9500
        Assertions.assertEquals(9500, buyResponse.getBalance());


        //check holding
        UserInformationRequest userRequest = UserInformationRequest.newBuilder().setUserId(2).build();
        UserInformation userResponse = this.stub.getUserInformation(userRequest);
        //보유 주식 종류가 1인 것.
        Assertions.assertEquals(1, userResponse.getHoldingsCount());
        Assertions.assertEquals(Ticker.AMAZON, userResponse.getHoldingsList().get(0).getTicker());

        //sell
        StockTradeRequest sellRequest = buyRequest.toBuilder().setAction(TradeAction.SELL).setPrice(102).build();
        StockTradeResponse sellResponse = this.stub.tradeStock(sellRequest);

        //validate balance
        Assertions.assertEquals(10010, sellResponse.getBalance());
    }


}