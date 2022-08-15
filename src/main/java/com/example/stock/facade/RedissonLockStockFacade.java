package com.example.stock.facade;

import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockStockFacade {
	
	private final RedissonClient redissonClient;
	private final StockService stockService;
	
	public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
		this.redissonClient = redissonClient;
		this.stockService = stockService;
	}
	
	public void decrease(Long id, Long quantity) {
		RLock lock = redissonClient.getLock("stock-" + id);
		try {
			boolean avaliable = false;
			try {
				avaliable = lock.tryLock(5, 1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if(!avaliable) {
				System.out.println("Lock is not avaliable");
				return;
			}
			stockService.decrease(id, quantity);
		} finally {
			lock.unlock();
		}
	}
}
