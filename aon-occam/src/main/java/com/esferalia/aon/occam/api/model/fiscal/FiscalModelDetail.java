package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Mod131Key;

public class FiscalModelDetail implements Serializable {

	private static final long serialVersionUID = 5819123654568341791L;
	
	private Integer id;
	private String type;
	private String description;
	private double accumulatedAmount;
	private double declaredAmount;
	private double resultAmount;
	private double adjustAmount;
	private double amount;
	
	public Integer getId() {
		return id;
	}
	public FiscalModelDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getType() {
		return type;
	}
	public FiscalModelDetail setType(String type) {
		this.type = type;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public FiscalModelDetail setDescription(String description) {
		this.description = description;
		return this;
	}
	public double getAccumulatedAmount() {
		return accumulatedAmount;
	}
	public FiscalModelDetail setAccumulatedAmount(double accumulatedAmount) {
		this.accumulatedAmount = accumulatedAmount;
		return this;
	}
	public double getDeclaredAmount() {
		return declaredAmount;
	}
	public FiscalModelDetail setDeclaredAmount(double declaredAmount) {
		this.declaredAmount = declaredAmount;
		return this;
	}
	public double getResultAmount() {
		return resultAmount;
	}
	public FiscalModelDetail setResultAmount(double resultAmount) {
		this.resultAmount = resultAmount;
		return this;
	}
	public double getAdjustAmount() {
		return adjustAmount;
	}
	public FiscalModelDetail setAdjustAmount(double adjustAmount) {
		this.adjustAmount = adjustAmount;
		return this;
	}
	public double getAmount() {
		return amount;
	}
	public FiscalModelDetail setAmount(double amount) {
		this.amount = amount;
		return this;
	}
	public Mod131Key getKey() {
		return Mod131Key.getKey(getType());
	}
	
}
