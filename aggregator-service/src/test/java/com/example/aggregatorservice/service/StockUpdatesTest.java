package com.example.aggregatorservice.service;

import com.example.aggregatorservice.dto.PriceUpdateDto;
import com.example.aggregatorservice.mockservice.StockMockService;
import com.example.aggregatorservice.mockservice.UserMockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youyk.common.Ticker;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.devh.boot.grpc.server.service.GrpcService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
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
        "grpc.client.stock-service.address=in-process:integration-test"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockUpdatesTest {

    private static final String STOCK_UPDATES_ENDPOINT = "http://localhost:%d/stock/updates";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(StockUpdatesTest.class);
    @Autowired
    private ObjectMapper mapper;

    /**
     * 여기서 보려고 하는 것은 일단 application이 시작되면
     *  1. PriceUpdateSubscriptionInitializer에서 run 메서드를 실행한다
     *  2. StockMockService에서 5개의 주식 가격을 업데이트한다.
     */
    @Test
    public void stockUpdatesTest(){
        List<PriceUpdateDto> list = this.restTemplate.execute(
                STOCK_UPDATES_ENDPOINT.formatted(port),
                HttpMethod.GET,
                null,
                this::getResponse

        );

/*        ResponseEntity<List<PriceUpdateDto>> response = this.restTemplate.exchange(
                STOCK_UPDATES_ENDPOINT.formatted(port),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PriceUpdateDto>>() {}
        );

        List<PriceUpdateDto> dto = response.getBody();*/

        Assertions.assertEquals(5, list.size());
        Assertions.assertEquals(Ticker.AMAZON.toString(), list.get(0).ticker());
        Assertions.assertEquals(1, list.get(0).price());
    }

    private List<PriceUpdateDto> getResponse(ClientHttpResponse clientHttpResponse){
        ArrayList<PriceUpdateDto> list = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientHttpResponse.getBody()));){
            String line;
            while(Objects.nonNull(line = reader.readLine())){
                if(!line.isEmpty()){
                    log.info(line);
                    PriceUpdateDto dto = mapper.readValue(line.substring("data:".length()),
                            PriceUpdateDto.class);
                    list.add(dto);
                }
            }
        }catch (Exception e){
            log.error("streaming error", e);
        }
        return list;
    }

    //테스트가 시작되면 자동으로 아래 두 서비스를 등록하게 한다.
    @TestConfiguration
    static class TestConfig{
        @GrpcService
        public StockMockService stockMockService(){
            return new StockMockService();
        }
    }
}
