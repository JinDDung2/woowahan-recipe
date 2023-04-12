package com.woowahan.recipe.domain.entity;

import com.woowahan.recipe.exception.AppException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static com.woowahan.recipe.exception.ErrorCode.NOT_ENOUGH_STOCK;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class StockEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stock_id")
    private Long id;

    private int quantity;

    @OneToOne(mappedBy = "stock")
    private ItemEntity item;

    public StockEntity(Long id, int quantity, ItemEntity item) {
        this.id = id;
        this.quantity = quantity;
        this.item = item;
    }

    public void decrease(int quantity) {
        if (this.quantity < quantity) {
            throw new AppException(NOT_ENOUGH_STOCK, NOT_ENOUGH_STOCK.getMessage());
        }
        this.quantity -= quantity;
    }

    public void increase(int quantity) {
        this.quantity += quantity;
    }
}
