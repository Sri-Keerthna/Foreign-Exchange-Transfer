package com.spiralforge.ForXTransfer.utill;

public class Utility {
	private Utility() {
	}
	

	public static Double getTotalPrice(Integer quantity, Double price) {
		return price * quantity;
	}
	
	public static Double calculateChareges(Double amount) {
		Double charge=(amount*(5.0f/100.0f));
		return (double) (Math.round(charge * 100.0) / 100.0);
	}

}
