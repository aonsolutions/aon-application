package com.code.aon.ui.calculator.controller;

import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.shared_tomahawk.util.MessageUtils;

import com.code.aon.calculator.Calculator;
import com.code.aon.calculator.CalculatorException;
import com.code.aon.calculator.CompanyExpenses;
import com.code.aon.calculator.DismissalExpenses;
import com.code.aon.calculator.DismissalInfo;
import com.code.aon.calculator.EmployeeInfo;
import com.code.aon.calculator.EmployeeSalary;
import com.code.aon.calculator.enumeration.ContractType;
import com.code.aon.ui.util.AonUtil;

public class CalculatorController {

    private double grossSalary;
    private double netSalary;
    private int wageNumber;
    private int offspringNumber;
    private ContractType contractType;
    private CompanyExpenses companyExpenses;
    private EmployeeSalary employeeSalary;
    private Date seniorityDate;
    private DismissalExpenses dismissalExpenses;

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

    public CompanyExpenses getCompanyExpenses() {
        return companyExpenses;
    }

    public void setCompanyExpenses(CompanyExpenses companyExpenses) {
        this.companyExpenses = companyExpenses;
    }

    public EmployeeSalary getEmployeeSalary() {
        return employeeSalary;
    }

    public void setEmployeeSalary(EmployeeSalary employeeSalary) {
        this.employeeSalary = employeeSalary;
    }

    public Date getSeniorityDate() {
        return seniorityDate;
    }

    public void setSeniorityDate(Date seniorityDate) {
        this.seniorityDate = seniorityDate;
    }

    public DismissalExpenses getDismissalExpenses() {
        return dismissalExpenses;
    }

    public void setDismissalExpenses(DismissalExpenses dismissalExpenses) {
        this.dismissalExpenses = dismissalExpenses;
    }

    public void onCalculateCompanyExpensesFromGrossSalary(ActionEvent event) {
        EmployeeInfo employeeInfo = new EmployeeInfo();
        employeeInfo.setGrossSalary(getGrossSalary());
        employeeInfo.setWageNumber(getWageNumber());
        employeeInfo.setOffspringNumber(getOffspringNumber());
        employeeInfo.setContractType(getContractType());

        Calculator calculator = new Calculator();
        try {
            setCompanyExpenses(calculator.calculateCompanyExpenses(employeeInfo));
            setEmployeeSalary(calculator.calculateEmployeeSalary(employeeInfo));
        } catch (CalculatorException e) {
            AonUtil.addErrorMessage(e.getMessage());
            throw new AbortProcessingException(e.getMessage(), e);
        }
    }

    public void onCalculateCompanyExpensesFromNetSalary(ActionEvent event) {
        EmployeeInfo employeeInfo = new EmployeeInfo();
        employeeInfo.setNetSalary(getNetSalary());
        employeeInfo.setWageNumber(getWageNumber());
        employeeInfo.setOffspringNumber(getOffspringNumber());
        employeeInfo.setContractType(getContractType());

        Calculator calculator = new Calculator();
        try {
            employeeInfo.setGrossSalary(calculator.calculateGrossSalary(employeeInfo));
            setCompanyExpenses(calculator.calculateCompanyExpenses(employeeInfo));
            setEmployeeSalary(calculator.calculateEmployeeSalary(employeeInfo));
        } catch (CalculatorException e) {
            AonUtil.addErrorMessage(e.getMessage());
            throw new AbortProcessingException(e.getMessage(), e);
        }
    }

    public void onCalculateCompanyExpensesFromDismissal(ActionEvent event) {
        DismissalInfo dismissalInfo = new DismissalInfo();
        dismissalInfo.setGrossSalary(getGrossSalary());
        Date sd = getSeniorityDate();
        if ( sd != null ) {
	        dismissalInfo.setSeniorityDate(sd);
	        Calculator calculator = new Calculator();
	        try {
	            setDismissalExpenses(calculator.calculateDismissal(dismissalInfo));
	        } catch (CalculatorException e) {
				MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
	            throw new AbortProcessingException(e.getMessage(), e);
	        }
        } else {
			FacesContext ctx = FacesContext.getCurrentInstance();
			String baseName = "com.code.aon.ui.employee.i18n.messages"; 
			ResourceBundle bundle = 
				ResourceBundle.getBundle( baseName, ctx.getViewRoot().getLocale() );
			String message = bundle.getString("aon_employee_seniority_date_exception");
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, message, null );
            throw new AbortProcessingException( message );
        }
    }

}
