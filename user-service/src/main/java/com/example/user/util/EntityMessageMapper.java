package com.example.user.util;

import com.example.user.entity.PortfolioItem;
import com.example.user.entity.User;
import com.youyk.user.Holding;
import com.youyk.user.StockTradeRequest;
import com.youyk.user.StockTradeResponse;
import com.youyk.user.UserInformation;

import java.util.List;

public class EntityMessageMapper {

    public static UserInformation toUserInformation(User user, List<PortfolioItem> items){
        List<Holding> holdings = items.stream()
                .map(i -> Holding.newBuilder().setTicker(i.getTicker()).setQuantity(i.getQuantity()).build())
                .toList();
        return UserInformation.newBuilder()
                .setUserId(user.getId())
                .setName(user.getName())
                .setBalance(user.getBalance())
                .addAllHoldings(holdings)
                .build();
    }

    public static PortfolioItem toPortfolioItem(StockTradeRequest request){

        return PortfolioItem.builder()
                .userId(request.getUserId())
                .ticker(request.getTicker())
                .quantity(request.getQuantity())
                .build();
    }

    public static StockTradeResponse toStockTradeResponse(StockTradeRequest request, int balance){
        return StockTradeResponse.newBuilder()
                .setPrice(request.getPrice())
                .setUserId(request.getUserId())
                .setTicker(request.getTicker())
                .setQuantity(request.getQuantity())
                .setPrice(request.getPrice() * request.getQuantity())
                .setBalance(balance)
                .build();
    }

}
