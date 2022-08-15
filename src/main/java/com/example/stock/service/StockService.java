package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.infrastructure.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
	private final StockRepository stockRepository;
	
	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}
	
//	@Transactional
	public synchronized void decrease(Long productId, Long quantity) {
		Stock stock = stockRepository.findByProductId(productId);
		stock.decrease(quantity);
		stockRepository.saveAndFlush(stock);
	}
}
