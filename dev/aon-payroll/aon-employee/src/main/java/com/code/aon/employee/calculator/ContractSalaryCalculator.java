package com.code.aon.employee.calculator;

import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.Contract;
import com.code.aon.employee.Salary;
import com.code.aon.employee.SalaryPayment;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.DeductionsFactoryContext;
import com.esferalia.aon.salary.deduction.DeductionsFactoryManager;
import com.esferalia.aon.salary.deduction.IDeductionsFactory;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.IPaymentsFactory;
import com.esferalia.aon.salary.payment.Payments;
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
		fillSalaryData(salary,contract);
		salary.setPayments( getPayments(contract) );
		salary.setTotalPayment( salary.getPayments().getTotal() ); 
		salary.setDeductions( getDeductions(contract) );
		salary.setTotalDeduction( salary.getDeductions().getTotal() );
		fillBasesData(salary,contract);
		return salary;
	}

	private void fillEnterpriseData(Salary salary, Contract contract) {
		salary.setEnterpriseName(contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
		salary.setEnterpriseAddress(contract.getWorkPlace().getAddress().getFullAddress());
		salary.setEnterpriseDocument(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
		salary.setCcc(contract.getEnterpriseCCC().getCCC());
	}

	private void fillEmployeeData(Salary salary, Contract contract) {
		salary.setEmployeeName(contract.getPerson().getFullName());
		salary.setEmployeeDocument(contract.getPerson().getRegistry().getDocument());
		salary.setRegistration(0); // TODO ¿?¿?¿?¿?¿?
		salary.setSocialSecurityNumber(contract.getPerson().getSocialSecurityNumber());
		salary.setCategory("XXX"); // TODO ¿?¿?¿?¿?¿?
		salary.setSeniorityDate(contract.getStartDate());
	}

	private void fillSalaryData(Salary salary, Contract contract) {
		Date today = new Date();
		salary.setIssueDate( today );
		salary.setStartDate( CommonUtil.getMonthFirstDay(today));
		salary.setEndDate( CommonUtil.getMonthLastDay(today));
		salary.setTimeUnits( (int) CommonUtil.getDaysBetweenDates(salary.getStartDate(), salary.getEndDate()));
	}

	private Payments getPayments(Contract contract) throws SalaryException {
		PaymentsFactoryManager manager =  PaymentsFactoryManager.getInstance();
		PaymentsFactoryContext pfc = new PaymentsFactoryContext();
		pfc.setSalaryProxy(contract);
		IPaymentsFactory factory = manager.getFactory( pfc );
		Payments contractPayments = factory.getPayments(pfc);
		Payments salaryPayments = new Payments();
		if (contractPayments.getBaseSalary() != null) {
			salaryPayments.setBaseSalary( resolvePayment( contractPayments.getBaseSalary() ));	
		}
		if (contractPayments.getMovingCompensation() != null) {
			salaryPayments.setMovingCompensation( resolvePayment( contractPayments.getMovingCompensation() ));
		}
		if ( contractPayments.getOtherNonWage() != null) {
			salaryPayments.setOtherNonWage( resolvePayment( contractPayments.getOtherNonWage() ));	
		}
		if (contractPayments.getOvertimeHours() != null) {
			salaryPayments.setOvertimeHours( resolvePayment( contractPayments.getOvertimeHours() ));
		}
		if (contractPayments.getSalaryInKind() != null) {
			salaryPayments.setSalaryInKind( resolvePayment( contractPayments.getSalaryInKind() ));
		}
		if (contractPayments.getSpecialBonuses() != null) {
			salaryPayments.setSpecialBonuses( resolvePayment( contractPayments.getSpecialBonuses() ));
		}
		if (contractPayments.getSpecialSecurityBenefits() != null) {
			salaryPayments.setSpecialSecurityBenefits( resolvePayment( contractPayments.getSpecialSecurityBenefits() ));
		}
		for ( IPayment payment: contractPayments.getSalarySupplements().getValues() ) {
			if (payment != null) {
				salaryPayments.addSalarySupplements( resolvePayment( payment ));	
			}
		}
		for ( IPayment payment: contractPayments.getCompensationOrPrepaidExpenses().getValues() ) {
			if (payment != null) {
				salaryPayments.addSalarySupplements( resolvePayment( payment ));
			}
		}
		return salaryPayments;
	}

	private IPayment resolvePayment(IPayment p) {
		if (p != null) {
			SalaryPayment sp = new SalaryPayment();
			sp.setDescription(p.getDescription() );
			sp.setFunction(p.getFunction() );
			sp.setType(p.getType()  );
			if (NumberUtils.isNumber(p.getFunction()) ) {
				sp.setAmount( NumberUtils.toDouble(p.getFunction()) );	
			} else {
				sp.setAmount(0.0);
			}
			return sp;
		}
		return null;
	}

	private Deductions getDeductions(Contract contract) throws SalaryException  {
		DeductionsFactoryManager manager =  DeductionsFactoryManager.getInstance();
		DeductionsFactoryContext dfc = new DeductionsFactoryContext();
		dfc.setSalaryProxy(contract);
		IDeductionsFactory factory = manager.getFactory( dfc );
		return factory.getDeductions(dfc);
	}

	private void fillBasesData(Salary salary, Contract contract) {
		salary.setTotalLiquid(99999.99); // TODO ¿?¿?¿?¿?¿?
		salary.setRemuneration(99999.99); // TODO ¿?¿?¿?¿?¿?
		salary.setExtraPayProration(99999.99); // TODO ¿?¿?¿?¿?¿?
		salary.setCommonBase(99999.99); // TODO ¿?¿?¿?¿?¿?
		salary.setProfessionalBase(99999.99); // TODO ¿?¿?¿?¿?¿?
		salary.setOvertimeBase(99999.99); // TODO ¿?¿?¿?¿?¿?
		salary.setIrpfBase(99999.99); // TODO ¿?¿?¿?¿?¿?
	}


}
