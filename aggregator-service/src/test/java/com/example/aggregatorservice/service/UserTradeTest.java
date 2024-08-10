package com.example.aggregatorservice.service;

import com.example.aggregatorservice.mockservice.StockMockService;
import com.example.aggregatorservice.mockservice.UserMockService;
import com.youyk.common.Ticker;
import com.youyk.user.StockTradeRequest;
import com.youyk.user.StockTradeResponse;
import com.youyk.user.TradeAction;
import com.youyk.user.UserInformation;
import net.devh.boot.grpc.server.service.GrpcService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @DirtiesContext가 붙은 테스트 메소드가 실행된 후에는 Spring은 현재의 애플리케이션 컨텍스트를 폐기하고,
 * 다음 테스트를 위해 새로운 애플리케이션 컨텍스트를 생성합니다.
 * 이렇게 함으로써 각 테스트가 독립적으로 실행될 수 있게 되며,
 * 테스트 간의 상태 공유로 인한 부작용을 방지할 수 있습니다.
 * @DirtiesContext는 테스트가 애플리케이션 컨텍스트의 상태를 변경하는 경우,
 * 예를 들어 빈의 상태를 변경하거나, 데이터베이스 상태를 변경하는 등의 경우에 유용하게 사용됩니다.
 * 하지만 애플리케이션 컨텍스트를 재생성하는 것은 비용이 크므로, 이 어노테이션은 신중하게 사용해야 합니다.
 */
@DirtiesContext
@SpringBootTest(properties = {
        "grpc.server.port=-1",
        "grpc.server.in-process-name=integration-test",
        "grpc.client.user-service.address=in-process:integration-test",
        "grpc.client.stock-service.address=in-process:integration-test"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTradeTest {

    private static final String USER_INFORMATION_ENDPOINT = "http://localhost:%d/user/%d";
    private static final String TRADE_ENDPOINT = "http://localhost:%d/trade";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void userInformationTest(){
        String url = USER_INFORMATION_ENDPOINT.formatted(port, 1);
        ResponseEntity<UserInformation> response = this.restTemplate.getForEntity(url, UserInformation.class);
        Assertions.assertEquals(200, response.getStatusCode().value());
        UserInformation user = response.getBody();

        Assertions.assertNotNull(user);
        Assertions.assertEquals(1, user.getUserId());
        Assertions.assertEquals("integration-test", user.getName());
        Assertions.assertEquals(100, user.getBalance());
    }

    @Test
    public void unknownUserTest(){
        String url = USER_INFORMATION_ENDPOINT.formatted(port, 2);

        ResponseEntity<UserInformation> response = this.restTemplate.getForEntity(url, UserInformation.class);
        Assertions.assertEquals(404, response.getStatusCode().value());
        UserInformation user = response.getBody();

        Assertions.assertNull(user);
    }

    @Test
    public void tradeTest(){
        StockTradeRequest tradeRequest = StockTradeRequest.newBuilder()
                .setUserId(1)
                .setPrice(10)
                .setTicker(Ticker.AMAZON)
                .setAction(TradeAction.BUY)
                .setQuantity(2)
                .build();
        String url = TRADE_ENDPOINT.formatted(port);
        ResponseEntity<StockTradeResponse> response = this.restTemplate.postForEntity(url,
                tradeRequest, StockTradeResponse.class);
        Assertions.assertEquals(200, response.getStatusCode().value());
        StockTradeResponse tradeResponse = response.getBody();

        Assertions.assertNotNull(tradeResponse);
        Assertions.assertEquals(Ticker.AMAZON, tradeResponse.getTicker());
        Assertions.assertEquals(1, tradeResponse.getUserId());
        Assertions.assertEquals(15, tradeResponse.getPrice());
        Assertions.assertEquals(1000, tradeResponse.getTotalPrice());
        Assertions.assertEquals(0, tradeResponse.getBalance());
    }


    //테스트가 시작되면 자동으로 아래 두 서비스를 등록하게 한다.
    @TestConfiguration
    static class TestConfig{
        @GrpcService
        public StockMockService stockMockService(){
            return new StockMockService();
        }

        @GrpcService
        public UserMockService userMockService(){
            return new UserMockService();
        }
    }
}
