package com.code.aon.calculator;


public class CompanyExpenses {

    private double grossSalary;
    private double commonContingencies;
    private double commonContingenciesPercent;
    private double unemployment;
    private double unemploymentPercent;
    private double wageGuarantee;
    private double wageGuaranteePercent;
    private double occupationalTraining;
    private double occupationalTrainingPercent;
    private double accidentAndDisease;
    private double accidentAndDiseasePercent;

    public double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(double grossSalary) {
        this.grossSalary = grossSalary;
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

    public double getWageGuarantee() {
        return wageGuarantee;
    }

    public void setWageGuarantee(double wageGuarantee) {
        this.wageGuarantee = wageGuarantee;
    }

    public double getWageGuaranteePercent() {
        return wageGuaranteePercent;
    }

    public void setWageGuaranteePercent(double wageGuaranteePercent) {
        this.wageGuaranteePercent = wageGuaranteePercent;
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

    public double getAccidentAndDisease() {
        return accidentAndDisease;
    }

    public void setAccidentAndDisease(double accidentAndDisease) {
        this.accidentAndDisease = accidentAndDisease;
    }

    public double getAccidentAndDiseasePercent() {
        return accidentAndDiseasePercent;
    }

    public void setAccidentAndDiseasePercent(double accidentAndDiseasePercent) {
        this.accidentAndDiseasePercent = accidentAndDiseasePercent;
    }

    public double getTotalAnnualExpenses() {
        return (grossSalary + commonContingencies + unemployment + wageGuarantee + occupationalTraining + accidentAndDisease);
    }

    public double getAverageMonthlyExpenses() {
        return (getTotalAnnualExpenses() / 12);
    }

}
