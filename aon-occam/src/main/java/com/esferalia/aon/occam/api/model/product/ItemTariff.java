package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.tariff.Tariff;

public class ItemTariff implements Serializable {
	
	private static final long serialVersionUID = 1699629262657144964L;
	
	private Integer id;
	private Integer domain;
	private Integer item;
	private Tariff tariff;
	private ItemTariffType type;
	private double profitPercent;
	private double price;
	
	private byte tariffType;

	public Integer getId() {
		return id;
	}
	public ItemTariff setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public ItemTariff setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getItem() {
		return item;
	}
	public ItemTariff setItem(Integer item) {
		this.item = item;
		return this;
	}
	public Tariff getTariff() {
		return tariff;
	}
	public ItemTariff setTariff(Tariff tariff) {
		this.tariff = tariff;
		return this;
	}
	public ItemTariffType getType() {
		return type;
	}
	public ItemTariff setType(ItemTariffType type) {
		this.type = type;
		return this;
	}
	public double getProfitPercent() {
		return profitPercent;
	}
	public ItemTariff setProfitPercent(double profitPercent) {
		this.profitPercent = profitPercent;
		return this;
	}
	public double getPrice() {
		return price;
	}
	public ItemTariff setPrice(double price) {
		this.price = price;
		return this;
	}
	public byte getTariffType() {
		return tariffType;
	}
	public ItemTariff setTariffType(byte tariffType) {
		this.tariffType = tariffType;
		return this;
	}
	
}
