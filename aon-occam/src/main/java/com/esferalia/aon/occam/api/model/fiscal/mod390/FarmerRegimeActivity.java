package com.esferalia.aon.occam.api.model.fiscal.mod390;

import java.io.Serializable;

public class FarmerRegimeActivity implements Serializable {
	
	private static final long serialVersionUID = 2201792990904258961L;
	
	protected String codigo;
    protected double incomes;
    protected double quotaIndex;
    protected double accrualQuota;
    private double danaReduction;   // Reducción DANA 2024
    protected double inputQuotas;
    protected double quota;
    
	public String getCodigo() {
		return codigo;
	}
	public FarmerRegimeActivity setCodigo(String codigo) {
		this.codigo = codigo;
		return this;
	}

	public double getIncomes() {
		return incomes;
	}
	public FarmerRegimeActivity setIncomes(double incomes) {
		this.incomes = incomes;
		return this;
	}

	public double getQuotaIndex() {
		return quotaIndex;
	}
	public FarmerRegimeActivity setQuotaIndex(double quotaIndex) {
		this.quotaIndex = quotaIndex;
		return this;
	}
	
	public double getAccrualQuota() {
		return accrualQuota;
	}
	public FarmerRegimeActivity setAccrualQuota(double accrualQuota) {
		this.accrualQuota = accrualQuota;
		return this;
	}
	
	public double getInputQuotas() {
		return inputQuotas;
	}
	public FarmerRegimeActivity setInputQuotas(double inputQuotas) {
		this.inputQuotas = inputQuotas;
		return this;

	}
	
	public double getQuota() {
		return quota;
	}
	public FarmerRegimeActivity setQuota(double quota) {
		this.quota = quota;
		return this;
	}
	public double getDanaReduction() {
		return danaReduction;
	}
	public FarmerRegimeActivity setDanaReduction(double danaReduction) {
		this.danaReduction = danaReduction;
		return this;
	}
    
}
