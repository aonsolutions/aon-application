package com.code.aon.employee.calculator;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.Contract;
import com.code.aon.employee.Salary;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;
import com.esferalia.aon.salary.deduction.DeductionsFactoryContext;
import com.esferalia.aon.salary.deduction.DeductionsFactoryManager;
import com.esferalia.aon.salary.deduction.IDeductionsFactory;
import com.esferalia.aon.salary.payment.IPaymentsFactory;
import com.esferalia.aon.salary.payment.PaymentsFactoryContext;
import com.esferalia.aon.salary.payment.PaymentsFactoryManager;

public class ContractSalaryCalculator implements ISalaryCalculator{

	@Override
	public boolean accept(SalaryCalculatorContext ctx) {
		ISalaryProxy proxy = ctx.getSalaryProxy();
		return (proxy instanceof Contract); 
	}

	@Override
	public ISalary calculate(SalaryCalculatorContext ctx) throws SalaryException {
		Contract contract = (Contract) ctx.getSalaryProxy();
		
		Salary salary = new Salary();
		salary.setContract(contract);
		fillEnterpriseData(salary,contract);
		fillEmployeeData(salary,contract);
		fillSalaryData(ctx,salary,contract);
		fillPayments(salary,contract);
		fillBasesData(salary,contract);
		fillDeductions(salary,contract);
		salary.setTotalLiquid(CommonUtil.round(salary.getTotalPayment() - salary.getTotalDeduction()));
		return salary;
	}

	private void fillEnterpriseData(Salary salary, Contract contract) {
		salary.setEnterpriseName(contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
		salary.setEnterpriseAddress(contract.getWorkPlace().getAddress().getFullAddress());
		salary.setEnterpriseDocument(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
		salary.setCcc(contract.getEnterpriseCCC().getCcc());
	}

	private void fillEmployeeData(Salary salary, Contract contract) {
		salary.setEmployeeName(contract.getPerson().getFullName());
		salary.setEmployeeDocument(contract.getPerson().getRegistry().getDocument());
		salary.setRegistration(0); // TODO ¿?¿?¿?¿?¿?
		salary.setSocialSecurityNumber(contract.getPerson().getSocialSecurityNumber());
		salary.setCategory("XXX"); // TODO ¿?¿?¿?¿?¿?
		salary.setSeniorityDate(contract.getStartDate());
	}

	private void fillSalaryData(SalaryCalculatorContext ctx, Salary salary, Contract contract) {
		salary.setIssueDate( ctx.getIssueDate() );
		salary.setStartDate( ctx.getStartDate());
		salary.setEndDate( ctx.getEndDate());
		salary.setTimeUnits( ((int) CommonUtil.getDaysBetweenDates(salary.getStartDate(), salary.getEndDate())) + 1);
	}

	private void fillPayments(Salary salary, Contract contract) throws SalaryException {
		PaymentsFactoryManager manager =  PaymentsFactoryManager.getInstance();
		PaymentsFactoryContext pfc = new PaymentsFactoryContext();
		pfc.setSalaryProxy(contract);
		pfc.setCurrentSalary(salary);
		IPaymentsFactory factory = manager.getFactory( pfc );
		salary.setPayments( factory.getPayments(pfc) );
		salary.setTotalPayment( salary.getPayments().getTotal() ); 
	}

	private void fillDeductions(Salary salary, Contract contract) throws SalaryException {
		DeductionsFactoryManager manager =  DeductionsFactoryManager.getInstance();
		DeductionsFactoryContext dfc = new DeductionsFactoryContext();
		dfc.setSalaryProxy(contract);
		dfc.setCurrentSalary(salary);
		IDeductionsFactory factory = manager.getFactory( dfc );
		salary.setDeductions( factory.getDeductions(dfc) );
		salary.setTotalDeduction( salary.getDeductions().getTotal() );
	}

	private void fillBasesData(Salary salary, Contract contract) {
		salary.setRemuneration(salary.getTotalPayment()); // TODO ¿?¿?¿?¿?¿?
		salary.setExtraPayProration(0.0); // TODO ¿?¿?¿?¿?¿?
		salary.setCommonBase(salary.getTotalPayment()); // TODO ¿?¿?¿?¿?¿?
		salary.setProfessionalBase(salary.getTotalPayment()); // TODO ¿?¿?¿?¿?¿?
		salary.setOvertimeBase(salary.getTotalPayment()); // TODO ¿?¿?¿?¿?¿?
		salary.setIrpfBase(salary.getTotalPayment()); // TODO ¿?¿?¿?¿?¿?
	}


}
