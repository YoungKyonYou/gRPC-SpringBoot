package com.example.aggregatorservice.controller;

import com.example.aggregatorservice.service.TradeService;
import com.youyk.user.StockTradeRequest;
import com.youyk.user.StockTradeResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("trade")
public class TradeController {

    private final TradeService tradeService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public StockTradeResponse trade(@RequestBody StockTradeRequest request){
        return this.tradeService.trade(request);
    }

}
