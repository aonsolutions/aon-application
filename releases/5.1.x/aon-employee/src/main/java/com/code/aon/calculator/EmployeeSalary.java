package com.code.aon.calculator;


public class EmployeeSalary {

    private double grossSalary;
    private int wageNumber;
    private double commonContingencies;
    private double commonContingenciesPercent;
    private double unemployment;
    private double unemploymentPercent;
    private double occupationalTraining;
    private double occupationalTrainingPercent;
    private double incomeTax;
    private double incomeTaxPercent;

    public double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public int getWageNumber() {
        return wageNumber;
    }

    public void setWageNumber(int wageNumber) {
        this.wageNumber = wageNumber;
    }

    public double getCommonContingencies() {
        return commonContingencies;
    }

    public void setCommonContingencies(double commonContingencies) {
        this.commonContingencies = commonContingencies;
    }

    public double getCommonContingenciesPercent() {
        return commonContingenciesPercent;
    }

    public void setCommonContingenciesPercent(double commonContingenciesPercent) {
        this.commonContingenciesPercent = commonContingenciesPercent;
    }

    public double getUnemployment() {
        return unemployment;
    }

    public void setUnemployment(double unemployment) {
        this.unemployment = unemployment;
    }

    public double getUnemploymentPercent() {
        return unemploymentPercent;
    }

    public void setUnemploymentPercent(double unemploymentPercent) {
        this.unemploymentPercent = unemploymentPercent;
    }

    public double getOccupationalTraining() {
        return occupationalTraining;
    }

    public void setOccupationalTraining(double occupationalTraining) {
        this.occupationalTraining = occupationalTraining;
    }

    public double getOccupationalTrainingPercent() {
        return occupationalTrainingPercent;
    }

    public void setOccupationalTrainingPercent(double occupationalTrainingPercent) {
        this.occupationalTrainingPercent = occupationalTrainingPercent;
    }

    public double getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(double incomeTax) {
        this.incomeTax = incomeTax;
    }

    public double getIncomeTaxPercent() {
        return incomeTaxPercent;
    }

    public void setIncomeTaxPercent(double incomeTaxPercent) {
        this.incomeTaxPercent = incomeTaxPercent;
    }

    public double getNetSalary() {
        return (grossSalary - commonContingencies - unemployment - occupationalTraining - incomeTax);
    }

    public double getNetExtraSalary() {
        return (wageNumber == 0) ? 0 : (grossSalary - incomeTax);
    }

}
