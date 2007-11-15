package com.code.aon.marketplace.plu;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Ticket{

	private int number;

	private String serie;

	private Date saleDate;
	
	private int customer;

	private String plu;

	private String description;

	private double quantity;
	
	private double price;
	
	private double total;

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @return the plu
	 */
	public String getPlu() {
		return plu;
	}

	/**
	 * @param plu the plu to set
	 */
	public void setPlu(String plu) {
		this.plu = plu;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the quantity
	 */
	public double getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the total
	 */
	public double getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(double total) {
		this.total = total;
	}

	/**
	 * @return the saleDate
	 */
	public Date getSaleDate() {
		return saleDate;
	}

	/**
	 * @param saleDate the saleDate to set
	 */
	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}

	/**
	 * @return the seler
	 */
	public int getCustomer() {
		return customer;
	}

	/**
	 * @param seler the seler to set
	 */
	public void setCustomer(int customer) {
		this.customer = customer;
	}

	public String getCode(){
		if (saleDate != null) return (new SimpleDateFormat("yyyyMMdd").format(saleDate));
		return (new SimpleDateFormat("yyyyMMdd").format(new Date()));
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}
}
