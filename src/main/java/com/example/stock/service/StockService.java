package com.example.stock.service;

import com.example.stock.infrastructure.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
	private final StockRepository stockRepository;
	
	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}
	
	@Transactional
	public void decrease(Long productId, Long quantity) {
		stockRepository.findByProductId(productId)
			.decrease(quantity);
	}
}
