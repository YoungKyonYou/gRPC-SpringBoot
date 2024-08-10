package com.example.aggregatorservice.dto;

public record PriceUpdateDto(
        String ticker,
        Integer price
) {
}
