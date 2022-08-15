package com.example.stock.facade;

import com.example.stock.service.OptimisticLockStockService;
import org.springframework.stereotype.Service;

@Service
public class OptimisticLockStockFacade {
	private final OptimisticLockStockService stockService;
	
	public OptimisticLockStockFacade(OptimisticLockStockService stockService) {
		this.stockService = stockService;
	}
	
	public void decrease(Long productId, Long quantity) throws InterruptedException {
		while(true) {
			try {
				stockService.decrease(productId, quantity);
				break;
			} catch (Exception e) {
				Thread.sleep(50);
			}
		}
	}
}
