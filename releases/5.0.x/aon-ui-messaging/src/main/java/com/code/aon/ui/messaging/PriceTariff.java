package com.code.aon.ui.messaging;

public class PriceTariff {

	private String filter, currency;
	private Integer number;
	private Double price;

	public PriceTariff(String filter, Integer number, Double price, String currency) {
		this.filter = filter;
		this.number = number;
		this.price = price;
		this.currency = currency;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
