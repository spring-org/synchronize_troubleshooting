package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.infrastructure.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptimisticLockStockService {
	private final StockRepository stockRepository;
	
	public OptimisticLockStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}
	
	@Transactional
	public void decrease(Long productId, Long quantity) {
		Stock stock = stockRepository.findByIdWithOptimisticLock(productId);
		stock.decrease(quantity);
		stockRepository.saveAndFlush(stock);
	}
}
