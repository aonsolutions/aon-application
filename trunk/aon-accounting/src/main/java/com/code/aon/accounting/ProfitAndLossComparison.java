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
		if(unpaidBalance!=null && creditBalance!=null)
			return creditBalance - unpaidBalance;
		return 0.0;
	}

	/**
	 * PResupuestado, haber - debe
	 */
	public Double getBudgeted() {
		if(budgetUnpaidBalance!=null && budgetCreditBalance!=null)
			return budgetCreditBalance - budgetUnpaidBalance;
		return 0.0;
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
		if(getAccumulated()!=0.0 && getDifference()!=0.0)
			return (getDifference().doubleValue()/getAccumulated().doubleValue());
		return 0.0;
	}
	
}