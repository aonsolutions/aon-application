package com.code.aon.accounting;



public class ProfitAndLossComparison {
	
    private String id;
    private String description;
    
	private Double unpaidBalance;
	private Double creditBalance;
	private Double budgetUnpaidBalance;
	private Double budgetCreditBalance;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getUnpaidBalance() {
		if(unpaidBalance!=null)
			return unpaidBalance;
		return 0.0;
	}

	public void setUnpaidBalance(Double unpaidBalance) {
		this.unpaidBalance = unpaidBalance;
	}

	public Double getCreditBalance() {
		if(creditBalance!=null)
			return creditBalance;
		return 0.0;
	}

	public void setCreditBalance(Double creditBalance) {
		this.creditBalance = creditBalance;
	}

	public Double getBudgetUnpaidBalance() {
		if(budgetUnpaidBalance!=null)
			return budgetUnpaidBalance;
		return 0.0;
	}

	public void setBudgetUnpaidBalance(Double budgetUnpaidBalance) {
		this.budgetUnpaidBalance = budgetUnpaidBalance;
	}

	public Double getBudgetCreditBalance() {
		if(budgetCreditBalance!=null)
			return budgetCreditBalance;
		return 0.0;
	}

	public void setBudgetCreditBalance(Double budgetCreditBalance) {
		this.budgetCreditBalance = budgetCreditBalance;
	}

	/**
	 * Acumulado, haber - debe
	 */
	public Double getAccumulated() {
		if(getId()==null)
			return getCreditBalance()+getUnpaidBalance();
		return getAcumulatedlDifference(getUnpaidBalance(), getCreditBalance()); 
	}

	/**
	 * PResupuestado, haber - debe
	 */
	public Double getBudgeted() {
		if(getId()==null)
			return getBudgetCreditBalance()+getBudgetUnpaidBalance();
		return getAcumulatedlDifference(getBudgetUnpaidBalance(), getBudgetCreditBalance());
	}

	/**
	 * diferencia entre el acumulado y el presupuestado
	 */
	public Double getDifference() {
		if(getAccumulated()!=null && getBudgeted()!=null)
			return getAccumulated() - getBudgeted();
		return 0.0;
	}

	/**
	 * Porcentaje de la diferencia sobre el acumulado
	 */
	public Double getPercent() {
		if(getAccumulated()!=0.0 && getDifference()!=0.0){
			return (getDifference().doubleValue()/getAccumulated().doubleValue());
		}
		if(getAccumulated()==0.0){
			return (getDifference().doubleValue()/getBudgeted().doubleValue());
		}
		return 0.0;
	}
	
	private Double getAcumulatedlDifference(Double debit, Double credit){
		if(getId().charAt(0)=='7'){
			return credit-debit;
		}else{
			return debit-credit;
		}
	}
	
}