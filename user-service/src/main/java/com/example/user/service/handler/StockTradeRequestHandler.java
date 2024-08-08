package com.example.user.service.handler;

import com.example.user.entity.PortfolioItem;
import com.example.user.entity.User;
import com.example.user.entity.exception.InsufficientBalanceException;
import com.example.user.entity.exception.InsufficientSharesException;
import com.example.user.entity.exception.UnknownTickerException;
import com.example.user.entity.exception.UnknownUserException;
import com.example.user.repository.PortfolioItemRepository;
import com.example.user.repository.UserRepository;
import com.example.user.util.EntityMessageMapper;
import com.youyk.common.Ticker;
import com.youyk.user.StockTradeRequest;
import com.youyk.user.StockTradeResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StockTradeRequestHandler {
    private final UserRepository userRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    @Transactional
    public StockTradeResponse buyStock(StockTradeRequest request){
        // validate request
        this.validateTicker(request.getTicker());
        User user = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UnknownUserException(request.getUserId()));

        int totalPrice = request.getQuantity() * request.getPrice();
        this.validateUserBalance(user.getId(), user.getBalance(), totalPrice);

        //valid request
        user.setBalance(user.getBalance() - totalPrice);
        //update
        this.portfolioItemRepository.findByUserIdAndTicker(user.getId(), request.getTicker())
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + request.getQuantity()),
                        () -> this.portfolioItemRepository.save(EntityMessageMapper.toPortfolioItem(request))
                );

        return EntityMessageMapper.toStockTradeResponse(request, user.getBalance());

    }

    @Transactional
    public StockTradeResponse sellStock(StockTradeRequest request){
        // validate request
        //ticker validation
        this.validateTicker(request.getTicker());
        //사용자 존재 여부 확인
        User user = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UnknownUserException(request.getUserId()));

        //사용자가 판매할 stock이 있는지 validate
        PortfolioItem portfolioItem = this.portfolioItemRepository.findByUserIdAndTicker(user.getId(),
                        request.getTicker())
                .filter(pi -> pi.getQuantity() >= request.getQuantity())
                .orElseThrow(() -> new InsufficientSharesException(user.getId()));

        //valid request
        int totalPrice = request.getQuantity() * request.getPrice();
        //valid request
        user.setBalance(user.getBalance() + totalPrice);
        portfolioItem.setQuantity(portfolioItem.getQuantity() - request.getQuantity());


        return EntityMessageMapper.toStockTradeResponse(request, user.getBalance());

    }

    private void validateTicker(Ticker ticker){
        if (Ticker.UNKNOWN.equals(ticker)) {
            throw new UnknownTickerException();
        }
    }

    private void validateUserBalance(Integer userId, Integer userBalance, Integer totalPrice){
        if(totalPrice > userBalance){
            throw new InsufficientBalanceException(userId);
        }
    }
}
