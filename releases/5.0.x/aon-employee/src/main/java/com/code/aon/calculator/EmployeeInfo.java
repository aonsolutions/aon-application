package com.code.aon.calculator;

import com.code.aon.calculator.enumeration.ContractType;

public class EmployeeInfo {

    private double grossSalary;
    private double netSalary;
    private int wageNumber;
    private int offspringNumber;
	private ContractType contractType;

    public double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(double netSalary) {
        this.netSalary = netSalary;
    }

    public int getWageNumber() {
        return wageNumber;
    }

    public void setWageNumber(int wageNumber) {
        this.wageNumber = wageNumber;
    }

    public int getOffspringNumber() {
        return offspringNumber;
    }

    public void setOffspringNumber(int offspringNumber) {
        this.offspringNumber = offspringNumber;
    }

	public ContractType getContractType() {
		return contractType;
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

    public double getPaymentBase() throws CalculatorException {
        double paymentBase = grossSalary / 12;
        if (paymentBase > getMaximumBase()) {
            paymentBase = getMaximumBase();
        } else if (paymentBase < getMinimumBase()) {
            paymentBase = getMinimumBase();
        }
        return paymentBase;
    }

    public double getMaximumBase() throws CalculatorException {
        return 2996.10;
    }

    public double getMinimumBase() throws CalculatorException {
        return 666.00;
    }

    public double getCommonContingenciesPercent() throws CalculatorException {
        return 4.70;
    }

    public double getUnemploymentPercent() throws CalculatorException {
        if (contractType == ContractType.GENERAL_TYPE) {
            return 1.55;
        } else if (contractType == ContractType.TEMPORARY_FULL_TIME) {
            return 1.60;
        } else {
            return 1.60;
        }
    }

    public double getOccupationalTrainingPercent() throws CalculatorException {
        return 0.10;
    }

    public double getTaxBase() throws CalculatorException {
        double taxBase = grossSalary / (12 + wageNumber);
        return taxBase;
    }

    public double getIncomeTaxPercent(double grossSalary) throws CalculatorException {
        IncomeTaxManager taxManager = new IncomeTaxManager(grossSalary, offspringNumber);
        return taxManager.getIncomeTaxPercent();
    }

    public double getIncomeTaxPercent() throws CalculatorException {
        return getIncomeTaxPercent(this.grossSalary);
    }

}
