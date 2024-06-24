package com.esferalia.aon.occam.mod200.api.model;

import java.io.Serializable;

public class UteBase implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private double base;
	private double percent;
	private double amount; // A partir del ejercicio 2023 añaden el importe de la deducción. Se graba en el campo incomes de la tabla

	public double getPercent() {
		return percent;
	}

	public UteBase setPercent(double percent) {
		this.percent = percent;
		return this;
	}

	public double getBase() {
		return base;
	}

	public UteBase setBase(double base) {
		this.base = base;
		return this;
	}

	public double getAmount() {
		return amount;
	}

	public UteBase setAmount(double amount) {
		this.amount = amount;
		return this;
	}

}
