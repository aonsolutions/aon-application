package com.code.aon.calculator;

import com.code.aon.calculator.enumeration.DismissalType;

public class Calculator {

    public CompanyExpenses calculateCompanyExpenses(EmployeeInfo employeeInfo) throws CalculatorException {
		CompanyInfo companyInfo = new CompanyInfo();
		companyInfo.setContractType(employeeInfo.getContractType());

		double paymentBase = employeeInfo.getPaymentBase();
		double commonContingencies = (paymentBase * companyInfo.getCommonContingenciesPercent() / 100) * 12;
        double unemployment = (paymentBase * companyInfo.getUnemploymentPercent() / 100) * 12;
        double wageGuarantee = (paymentBase * companyInfo.getWageGuaranteePercent() / 100) * 12;
        double occupationalTraining = (paymentBase * companyInfo.getOccupationalTrainingPercent() / 100) * 12;
        double accidentAndDisease = (paymentBase * companyInfo.getAccidentAndDiseasePercent() / 100) * 12;

        CompanyExpenses companyExpenses = new CompanyExpenses();
        companyExpenses.setGrossSalary(round(employeeInfo.getGrossSalary(), 2));
        companyExpenses.setCommonContingencies(round(commonContingencies, 2));
        companyExpenses.setCommonContingenciesPercent(companyInfo.getCommonContingenciesPercent());
        companyExpenses.setUnemployment(round(unemployment, 2));
        companyExpenses.setUnemploymentPercent(companyInfo.getUnemploymentPercent());
        companyExpenses.setWageGuarantee(round(wageGuarantee, 2));
        companyExpenses.setWageGuaranteePercent(companyInfo.getWageGuaranteePercent());
        companyExpenses.setOccupationalTraining(round(occupationalTraining, 2));
        companyExpenses.setOccupationalTrainingPercent(companyInfo.getOccupationalTrainingPercent());
        companyExpenses.setAccidentAndDisease(round(accidentAndDisease, 2));
        companyExpenses.setAccidentAndDiseasePercent(companyInfo.getAccidentAndDiseasePercent());
		return companyExpenses;
	}

    public EmployeeSalary calculateEmployeeSalary(EmployeeInfo employeeInfo) throws CalculatorException {
        double paymentBase = employeeInfo.getPaymentBase();
        double taxBase = employeeInfo.getTaxBase();
        double commonContingencies = (paymentBase * employeeInfo.getCommonContingenciesPercent() / 100);
        double unemployment = (paymentBase * employeeInfo.getUnemploymentPercent() / 100);
        double occupationalTraining = (paymentBase * employeeInfo.getOccupationalTrainingPercent() / 100);
        double incomeTax = (taxBase * employeeInfo.getIncomeTaxPercent() / 100);

        EmployeeSalary salary = new EmployeeSalary();
        salary.setGrossSalary(round(employeeInfo.getTaxBase(), 2));
        salary.setWageNumber(employeeInfo.getWageNumber());
        salary.setCommonContingencies(round(commonContingencies, 2));
        salary.setCommonContingenciesPercent(employeeInfo.getCommonContingenciesPercent());
        salary.setUnemployment(round(unemployment, 2));
        salary.setUnemploymentPercent(employeeInfo.getUnemploymentPercent());
        salary.setOccupationalTraining(round(occupationalTraining, 2));
        salary.setOccupationalTrainingPercent(employeeInfo.getOccupationalTrainingPercent());
        salary.setIncomeTax(round(incomeTax, 2));
        salary.setIncomeTaxPercent(employeeInfo.getIncomeTaxPercent());
        return salary;
    }

    public double calculateGrossSalary(EmployeeInfo employeeInfo) throws CalculatorException {
        double commonContingenciesPercent = employeeInfo.getCommonContingenciesPercent();
        double unemploymentPercent = employeeInfo.getUnemploymentPercent();
        double occupationalTrainingPercent = employeeInfo.getOccupationalTrainingPercent();
        double minBase = employeeInfo.getMinimumBase() * 12;
        double maxBase = employeeInfo.getMaximumBase() * 12;
        double minQuota = minBase * (commonContingenciesPercent + unemploymentPercent + occupationalTrainingPercent) / 100;
        double maxQuota = maxBase * (commonContingenciesPercent + unemploymentPercent + occupationalTrainingPercent) / 100;

        double grossSalary = 0;
        double incomeTaxPercent = employeeInfo.getIncomeTaxPercent(employeeInfo.getNetSalary());
        while (incomeTaxPercent <= 40) {
            double deductions = commonContingenciesPercent + unemploymentPercent + occupationalTrainingPercent + incomeTaxPercent;
            grossSalary = round(employeeInfo.getNetSalary() / (1 - deductions / 100), 2);
            if (minBase > grossSalary) {
                grossSalary = round((employeeInfo.getNetSalary() + minQuota) / (1 - incomeTaxPercent / 100), 2);
            } else if (maxBase < grossSalary) {
                grossSalary = round((employeeInfo.getNetSalary() + maxQuota) / (1 - incomeTaxPercent / 100), 2);
            }

            if (incomeTaxPercent == employeeInfo.getIncomeTaxPercent(grossSalary)) {
                break;
            } else {
                ++incomeTaxPercent;
            }
        }

        return grossSalary;
    }

    public DismissalExpenses calculateDismissal(DismissalInfo dismissalInfo) throws CalculatorException {
        DismissalExpenses dismissalExpenses = new DismissalExpenses();
        dismissalExpenses.setUnfairDismissalCompensation(dismissalInfo.getCompensation(DismissalType.UNFAIR));
        dismissalExpenses.setLayoffAsUnfairDismissalCompensation(dismissalInfo.getCompensation(DismissalType.LAYOFF_AS_UNFAIR));
        dismissalExpenses.setLayoffCompensation(dismissalInfo.getCompensation(DismissalType.LAYOFF));
        return dismissalExpenses;
    }

    private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }

}
