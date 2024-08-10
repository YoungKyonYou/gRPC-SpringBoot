package com.example.aggregatorservice.service;

import com.youyk.stock.StockPriceRequest;
import com.youyk.stock.StockPriceResponse;
import com.youyk.stock.StockServiceGrpc;
import com.youyk.user.StockTradeRequest;
import com.youyk.user.StockTradeResponse;
import com.youyk.user.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userClient;

    @GrpcClient("stock-service")
    private StockServiceGrpc.StockServiceBlockingStub stockClient;

    public StockTradeResponse trade(StockTradeRequest request){
        StockPriceRequest priceRequest = StockPriceRequest.newBuilder()
                .setTicker(request.getTicker()).build();

        StockPriceResponse priceResponse = this.stockClient.getStockPrice(priceRequest);
        StockTradeRequest tradeRequest = request.toBuilder().setPrice(priceResponse.getPrice()).build();
        return this.userClient.tradeStock(tradeRequest);
    }

}
