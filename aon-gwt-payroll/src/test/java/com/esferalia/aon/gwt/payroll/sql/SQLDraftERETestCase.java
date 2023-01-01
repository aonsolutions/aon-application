package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTOR;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.server.SalaryDraftBuilder;
import com.esferalia.aon.gwt.payroll.server.SalaryDraftCalculatorContext;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLERETestCase;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.cost.Costs;
import com.esferalia.aon.salary.data.IData;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.Payments;

import junit.framework.Assert;

public class SQLDraftERETestCase extends SQLERETestCase {
	
	
	private static class ISalaryDraft implements ISalary {
		
		SalaryDraft salaryDraft;
		
		
		
		public ISalaryDraft(SalaryDraft salaryDraft) {
			super();
			this.salaryDraft = salaryDraft;
		}

		@Override
		public Integer getId() {
			return salaryDraft.getId();
		}

		@Override
		public String getEnterpriseName() {
			return salaryDraft.getEnterpriseName();
		}

		@Override
		public String getEnterpriseAddress() {
			return salaryDraft.getEnterpriseAddress();
		}

		@Override
		public String getEnterpriseDocument() {
			return salaryDraft.getEnterpriseDocument();
		}

		@Override
		public String getCcc() {
			return salaryDraft.getEnterpriseCCC();
		}

		@Override
		public String getEmployeeName() {
			return salaryDraft.getEmployeeName();
		}

		@Override
		public String getEmployeeDocument() {
			return salaryDraft.getEmployeeDocument();
		}

		@Override
		public Integer getRegistration() {
			throw new NoSuchMethodError();
		}

		@Override
		public String getSocialSecurityNumber() {
			return salaryDraft.getEmployeeSS();
		}

		@Override
		public String getCategory() {
			throw new NoSuchMethodError();
		}

		@Override
		public String getQuoteGroup() {
			return salaryDraft.getEmployeeQuoteGroup();
		}

		@Override
		public java.util.Date getSeniorityDate() {
			throw new NoSuchMethodError();
		}

		@Override
		public SalaryType getType() {
			return SalaryType.valueOf(salaryDraft.getType().name());
		}

		@Override
		public java.util.Date getChargeDate() {
			return salaryDraft.getChargeDate();
		}

		@Override
		public java.util.Date getIssueDate() {
			return salaryDraft.getIssueDate();
		}

		@Override
		public java.util.Date getStartDate() {
			return salaryDraft.getStartDate();
		}

		@Override
		public java.util.Date getEndDate() {
			return salaryDraft.getEndDate();
		}

		@Override
		public boolean isFullTime() {
			throw new NoSuchMethodError();
		}

		@Override
		public Integer getTimeUnits() {
			return salaryDraft.getTimeUnits();
		}

		@Override
		public Payments getPayments() throws SalaryException {
			throw new NoSuchMethodError();
		}

		@Override
		public Double getTotalPayment() {
			return salaryDraft.getTotalPayment();
		}

		@Override
		public Deductions getDeductions() throws SalaryException {
			throw new NoSuchMethodError();
		}

		@Override
		public Double getSocialSecurityContributions() {
			return salaryDraft.getTotalDeduction();
		}

		@Override
		public Double getTotalDeduction() {
			return salaryDraft.getTotalDeduction();
		}

		@Override
		public Costs getEnterpriseCosts() throws SalaryException {
			throw new NoSuchMethodError();
		}

		@Override
		public <T extends IPayment> Collection<T> getPaymentS() throws SalaryException {
			throw new NoSuchMethodError();
		}

		@Override
		public <T extends IDeduction> Collection<T> getDeductionS() throws SalaryException {
			throw new NoSuchMethodError();
		}

		@Override
		public <T extends IDeduction> Collection<T> getCostS() throws SalaryException {
			throw new NoSuchMethodError();
		}
		
		@Override
		public <T extends IDeduction> Collection<T> getEmbargoS() throws SalaryException {
			throw new NoSuchMethodError();
		}
		
		@Override
		public <T extends IData> Map<String, List<T>> getDataS() throws SalaryException {
			throw new NoSuchMethodError();
		}
		
		@Override
		public Double getTotalIrpf() {
			throw new NoSuchMethodError();
		}

		@Override
		public Double getTotalLiquid() {
			return salaryDraft.getTotalLiquid();
		}

		@Override
		public Double getTotalEnterprise() {
			return salaryDraft.getTotalEnterprise();
		}

		@Override
		public Double getRemuneration() {
			return salaryDraft.getRemuneration();
		}

		@Override
		public Double getExtraPayProration() {
			throw new NoSuchMethodError();
		}

		@Override
		public Double getCommonBase() {
			return salaryDraft.getCgcBase();
		}

		@Override
		public Double getRawCommonBase() {
			throw new NoSuchMethodError();
		}

		@Override
		public Double getProfessionalBase() {
			return salaryDraft.getCgpBase();
		}

		@Override
		public Double getOvertimeBase() {
			throw new NoSuchMethodError();
		}

		@Override
		public Double getNonEstructuralOvertimeBase() {
			throw new NoSuchMethodError();
		}

		@Override
		public Double getIrpfBase() {
			return salaryDraft.getIrpfBase();
		}

		@Override
		public Double getInKindIrpfBase() {
			return salaryDraft.getInkindIrpfBase();
		}
		
	}
	

	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			java.sql.Date issueDate, ContractRecord contract)
			throws ExpressionException, SQLException {

		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(issueDate);
		
		return EmployeesServiceHelper.getSalaryCalculatorContext(connection,
				draft, null);
	};

	@Override
	protected ISalary calculate(ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		SalaryDraft draft =
		((SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext>) ctx).getDraft();
		new SmartContractSalaryCalculator<ISalary>(new SalaryDraftBuilder(draft)).calculate(ctx);
		return new ISalaryDraft(draft);
	}
	
	@Test
	public void testEREIX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(CGC_BASE_MIN.getName(),
								Integer.toString(Integer.MIN_VALUE));
						put(CGP_BASE_MIN.getName(),
								Integer.toString(Integer.MIN_VALUE));
						put(CGC_BASE_MAX.getName(),
								Integer.toString(Integer.MAX_VALUE));
						put(CGP_BASE_MAX.getName(),
								Integer.toString(Integer.MAX_VALUE));
					}
				},
				new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", },
				new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10",
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05" },
				newAgreement(aonContext, new Extra[] {}, Collections.emptyMap()));



		PaymentConceptRecord ere = addConcept(aonContext, ERE.getName());
		addPayment(aonContext, contract, ere, null,
				"TRACE('ERE = %f \r\n', (DIAS_ERE * BASE_REGULADORA)); DIAS_ERE * BASE_REGULADORA");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(endDate);
		
		Date startEre = getFirstDayOfMonth(getToday());
		int ereDays = (int) Math.max(1,(Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1)));
		Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);
		
		NumberVariable ereVar = new NumberVariable();
		ereVar.setName(ERE_FACTOR.getName());
		ereVar.setStartDate(startEre);
		ereVar.setEndDate(endEre);
		ereVar.setValue(1.00);
		
		draft.addDraftVariable(ereVar);

		ISQLContractSalaryCalculatorContext ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);

		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder() {
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}

		Assert.assertEquals(
				((1500.00 + 250.00) * 1.10 )*(get(endDate, DAY_OF_MONTH) - ereDays) / get(endDate, DAY_OF_MONTH), 
				salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals((1750.00 * 1.10), salary.getCommonBase(), DELTA);

		Assert.assertEquals(salary.getTotalPayment() * 0.15,
				salary.getSocialSecurityContributions(), DELTA);
		
		
	}

}
