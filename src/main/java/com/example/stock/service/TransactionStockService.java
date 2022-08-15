package com.example.stock.service;

public class TransactionStockService {
	
	private final  StockService stockService;
	
	
	public TransactionStockService(StockService stockService) {
		this.stockService = stockService;
	}
	
	public void decrease(Long productId, Long quantity) {
		startTransaction();
		stockService.decrease(productId, quantity);
		
		endTransaction();
	}
	
	private void startTransaction() {
	
	}
	
	private void endTransaction() {
	
	}
}
