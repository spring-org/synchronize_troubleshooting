package com.example.stock.service;

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
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class StockServiceTest {
	@Autowired
	private StockService stockService;
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
	
	@Test
	void stock_decrease() {
		stockService.decrease(1L, 10L);
		Stock stock = stockRepository.findByProductId(1L);
		assertThat(stock.getQuantity()).isEqualTo(90L);
	}
	
	@Test
	void multi_thread_stock_decrease() {
		Thread thread1 = new Thread(() -> stockService.decrease(1L, 10L));
		Thread thread2 = new Thread(() -> stockService.decrease(1L, 10L));
		thread1.start();
		thread2.start();
		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Stock stock = stockRepository.findByProductId(1L);
		assertThat(stock.getQuantity()).isEqualTo(80L);
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
				stockService.decrease(1L, 1L);
				countDownLatch.countDown();
			});
		}
		
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Stock stock = stockRepository.findByProductId(1L);
		// 100 - (1 * 100) = 0
		assertThat(stock.getQuantity()).isZero();
		
	}
	
	@Test
	void stock_decrease_not_enough_stock() {
		assertThrows(IllegalArgumentException.class, () -> stockService.decrease(1L, 110L));
	}
}