package com.woowahan.recipe.service;

import com.woowahan.recipe.domain.entity.StockEntity;
import com.woowahan.recipe.exception.AppException;
import com.woowahan.recipe.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.woowahan.recipe.exception.ErrorCode.NOT_ENOUGH_STOCK;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long id, int quantity) {
        StockEntity stock = stockRepository.findById(id).orElseThrow(() ->
                new AppException(NOT_ENOUGH_STOCK, NOT_ENOUGH_STOCK.getMessage()));

        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
