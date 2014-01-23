package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class FarmerRegimeActivity implements Serializable, IsSerializable {
	
    protected String codigo;
    protected double incomes;
    protected double quotaIndex;
    protected double accrualQuota;
    protected double inputQuotas;
    protected double quota;
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public double getIncomes() {
		return incomes;
	}
	public void setIncomes(double incomes) {
		this.incomes = incomes;
	}
	public double getQuotaIndex() {
		return quotaIndex;
	}
	public void setQuotaIndex(double quotaIndex) {
		this.quotaIndex = quotaIndex;
	}
	public double getAccrualQuota() {
		return accrualQuota;
	}
	public void setAccrualQuota(double accrualQuota) {
		this.accrualQuota = accrualQuota;
	}
	public double getInputQuotas() {
		return inputQuotas;
	}
	public void setInputQuotas(double inputQuotas) {
		this.inputQuotas = inputQuotas;
	}
	public double getQuota() {
		return quota;
	}
	public void setQuota(double quota) {
		this.quota = quota;
	}
    
}
