package com.example.user.service.handler;

import com.example.user.entity.User;
import com.example.user.entity.exception.UnknownTickerException;
import com.example.user.entity.exception.UnknownUserException;
import com.example.user.repository.PortfolioItemRepository;
import com.example.user.repository.UserRepository;
import com.youyk.common.Ticker;
import com.youyk.user.StockTradeRequest;
import com.youyk.user.StockTradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StockTradeRequestHandler {
    private final UserRepository userRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public StockTradeResponse buyStock(StockTradeRequest request){
        // validate request
        this.validateTicker(request.getTicker());
        User user = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UnknownUserException(request.getUserId()));
        // update

    }

    private void validateTicker(Ticker ticker){
        if (Ticker.UNKNOWN.equals(ticker)) {
            throw new UnknownTickerException();
        }
    }
}
