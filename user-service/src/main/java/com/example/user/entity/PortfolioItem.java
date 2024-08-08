package com.example.user.entity;

import com.youyk.common.Ticker;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class PortfolioItem {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "customer_id")
    private Integer userId;
    private Ticker ticker;
    private Integer quantity;
}
