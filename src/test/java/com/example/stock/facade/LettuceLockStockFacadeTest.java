package com.example.stock.facade;

import com.example.stock.domain.Stock;
import com.example.stock.infrastructure.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LettuceLockStockFacadeTest {
	@Autowired
	private LettuceLockStockFacade stockService;
	@Autowired
	private StockRepository stockRepository;
	
	@BeforeEach
	void setUp() {
		Stock stock = new Stock(1L, 100L);
		stockRepository.save(stock);
	}
	
	@AfterEach
	void tearDown() {
		stockRepository.deleteAll();
	}
	
	/**
	 * race condition
	 */
	@Test
	void multi_thread_stock_decrease2() {
		int threadCount = 100;
		ExecutorService executeService = newFixedThreadPool(32);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			executeService.submit(() -> {
				try {
					stockService.decrease(1L, 1L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				countDownLatch.countDown();
			});
		}
		
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Stock stock = stockRepository.findById(1L).orElseThrow();
		// 100 - (1 * 100) = 0
		assertThat(stock.getQuantity()).isZero();
		
	}
}