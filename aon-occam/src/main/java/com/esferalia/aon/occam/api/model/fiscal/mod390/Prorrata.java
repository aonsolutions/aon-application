package com.esferalia.aon.occam.api.model.fiscal.mod390;

import java.io.Serializable;

public class Prorrata implements Serializable {

	private static final long serialVersionUID = -2787127406538620223L;
	
	private String activity;
	private String cnae;
	private double amount;
	private double amountWithRight;
	private double percent;
	private String type;
	
	public String getActivity() {
		return activity;
	}
	public Prorrata setActivity(String activity) {
		this.activity = activity;
		return this;
	}
	
	public String getCnae() {
		return cnae;
	}
	public Prorrata setCnae(String cnae) {
		this.cnae = cnae;
		return this;
	}
	
	public double getAmount() {
		return amount;
	}
	public Prorrata setAmount(double amount) {
		this.amount = amount;
		return this;
	}
	
	public double getAmountWithRight() {
		return amountWithRight;
	}
	public Prorrata setAmountWithRight(double amountWithRight) {
		this.amountWithRight = amountWithRight;
		return this;
	}
	
	public double getPercent() {
		return percent;
	}
	public Prorrata setPercent(double percent) {
		this.percent = percent;
		return this;
	}
	
	public String getType() {
		return type;
	}
	public Prorrata setType(String type) {
		this.type = type;
		return this;
	}
}
