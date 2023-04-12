package com.woowahan.recipe.service;

import com.woowahan.recipe.domain.entity.StockEntity;
import com.woowahan.recipe.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StockServiceTest {
    @Autowired
    private StockService stockService;
    @Autowired
    private StockRepository stockRepository;


    /*@BeforeEach
    void setUp() {
        StockEntity stock = stockRepository.findById(84L).get();
    }*/


    /*@Test
    void 재고_옮기기() throws Exception {
        // given
        for (Long i = 1L; i <= 116L; i++) {
            ItemEntity item = itemRepository.findById(i).orElseThrow(() -> new RuntimeException("테스트에서 아이템 아이디 못찾았다냥"));
            StockEntity stock = new StockEntity();
            stock.setItem(item);
            stock.setQuantity(item.getItemStock());
            StockEntity newStock = stockRepository.saveAndFlush(stock);
            item.setStock(newStock);
            itemRepository.save(item);
        }
    }*/

    @Test
    @Transactional
    void 동시요청_100개() throws Exception {
        // given
        int threadCount = 100;
        // 멀티쓰레드 이용
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(84L, 1);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        // then
        StockEntity stock = stockRepository.findById(84L).get();
        assertThat(stock.getQuantity()).isEqualTo(200);

    }
}
