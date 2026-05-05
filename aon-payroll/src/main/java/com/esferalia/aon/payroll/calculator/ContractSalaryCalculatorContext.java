package com.esferalia.aon.payroll.calculator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractDelayCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSettleCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLExtraSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.calculator.OutOfDateException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;

public class ContractSalaryCalculatorContext extends
		AbstractContractSalaryCalculatorContext implements ISalaryProxy {

	private ISQLContractSalaryCalculatorContext ctx;
	private Contract contract;

	@Deprecated
	public ContractSalaryCalculatorContext(Contract contract, int year,
			Month month, SalaryType salaryType) throws SalaryException {
		try {
			this.contract = contract;
			try {
				ContextBuilder builder = new ContextBuilder(year, month,
						this.contract);
				this.ctx = salaryType.accept(builder);
			} catch (RuntimeException e) {
				throw new SalaryException(e.getCause());
			}

			if (this.ctx == null) {
				throw new SalaryException("¿?");
			}

			if (!this.ctx.next()) {
				throw new OutOfDateException();
			}
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	public ContractSalaryCalculatorContext(Contract contract, Date startDate,
			Date endDate, SalaryType salaryType) throws SalaryException {
		try {
			this.contract = contract;
			try {
				ContextBuilder builder = new ContextBuilder(startDate, endDate,
						this.contract);
				this.ctx = salaryType.accept(builder);
			} catch (RuntimeException e) {
				throw new SalaryException(e.getCause());
			}

			if (this.ctx == null) {
				throw new SalaryException("¿?");
			}

			if (!this.ctx.next()) {
				throw new OutOfDateException();
			}
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new SalaryException(e.getMessage(), e);
		}
	}

	public Contract getContract() {
		return contract;
	}

	// ------------------------------------------
	// ISalaryProxy
	// ------------------------------------------
	@Override
	public ISalary getSalary() throws SalaryException {
		ISalaryCalculator<Salary, IContractSalaryCalculatorContext> sc = new ContractSalaryCalculator<Salary>();
		sc.setSalaryBuilder(new SalaryBuilder());
		ISalary salary = sc.calculate(this.ctx);
		return salary;
	}

	// ------------------------------------------
	// IContractSalaryCalculatorContext
	// ------------------------------------------
	@Override
	public SalaryType getSalaryType() {
		return ctx.getSalaryType();
	}

	@Override
	public Date getIrpfDate() {
		return ctx.getChargeDate();
	}

	@Override
	public Date getChargeDate() {
		return ctx.getChargeDate();
	}

	@Override
	public Date getIssueDate() {
		return ctx.getIssueDate();
	}

	@Override
	public Date getStartDate() {
		return ctx.getStartDate();
	}

	@Override
	public Date getEndDate() {
		return ctx.getEndDate();
	}

	@Override
	public ISalaryProxy getSalaryProxy() {
		return this;
	}

	@Override
	public ExpressionContext getExpressionContext() {
		return ctx.getExpressionContext();
	}

	@Override
	public String getCcc() {
		return ctx.getCcc();
	}

	@Override
	public String getEnterpriseCity() {
		return ctx.getEnterpriseCity();
	}

	@Override
	public String getEnterpriseName() {
		return ctx.getEnterpriseName();
	}

	@Override
	public String getEnterpriseAddress() {
		return ctx.getEnterpriseAddress();
	}

	@Override
	public String getEnterpriseDocument() {
		return ctx.getEnterpriseDocument();
	}

	@Override
	public SSRegimeType getSSRegime() {
		return ctx.getSSRegime();
	}
	
	@Override
    public CCCType getCCCType() {
		return ctx.getCCCType();
    }

	@Override
	public String getCategory() {
		return ctx.getCategory();
	}

	@Override
	public String getQuoteGroup() {
		return ctx.getQuoteGroup();
	}

	@Override
	public String getEmployeeCity() {
		return ctx.getEmployeeCity();
	}
	
	@Override
	public String getEmployeeAddress() {
		return ctx.getEmployeeAddress();
	}
	
	@Override
	public String getEmployeeName() {
		return ctx.getEmployeeName();
	}

	@Override
	public String getEmployeeDocument() {
		return ctx.getEmployeeDocument();
	}

	@Override
	public String getSocialSecurityNumber() {
		return ctx.getSocialSecurityNumber();
	}

	@Override
	public Integer getRegistration() {
		return ctx.getRegistration();
	}

	@Override
	public Date getSeniorityDate() {
		return ctx.getSeniorityDate();
	}
	
	@Override
	public Collection<ISystemPayment> getSystemPayments() {
		return ctx.getSystemPayments();
	}

	@Override
	public Collection<IContractPayment> getAgreementPayments() {
		return ctx.getAgreementPayments();
	}

	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		return ctx.getContractPayments();
	}

	@Override
	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {
		return ctx.getContractDeductions();
	}

	@Override
	public Collection<IContractEmbargo> getContractEmbargos()
			throws AonException {
		return ctx.getContractEmbargos();
	}

	@Override
	public Collection<IContractCost> getContractCosts() throws AonException {
		return ctx.getContractCosts();
	}

	@Override
	public Collection<IContractBonus> getContractBonus() throws AonException {
		return ctx.getContractBonus();
	}

	public ExpressionContext getSystemExpressionContext() {
		return ctx.getSystemExpressionContext();
	}

	public ExpressionContext getImplicitExpressionContext() {
		return ctx.getImplicitExpressionContext();
	}

	public ExpressionContext getAgreementExpressionContext() {
		return ctx.getAgreementExpressionContext();
	}

	private static class ContextBuilder implements
			SalaryTypeVisitor<ISQLContractSalaryCalculatorContext> {
		private Date endDate;
		private Date startDate;
		private Contract contract;

		@Deprecated
		public ContextBuilder(int year, Month month, Contract contract) {
			this.contract = contract;
			this.startDate = getStartDate(year, month);
			this.endDate = getEndDate(year, month);
		}

		public ContextBuilder(Date startDate, Date endDate, Contract contract) {
			this.contract = contract;
			this.startDate = startDate;
			this.endDate = endDate;
		}

		private Connection getConnection() {
			String sessionFactoryName = HibernateUtil
					.getSessionFactoryName(Contract.class.getName());
			return HibernateUtil.getSession(sessionFactoryName).connection();
		}

		private Criteria getCriteria() {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression("contract.id", contract.getId());
			return criteria;
		}

		private Date getStartDate(int year, Month month) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month.getValue());
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			return calendar.getTime();
		}

		private Date getEndDate(int year, Month month) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month.getValue());
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			return calendar.getTime();
		}

		@Override
		public ISQLContractSalaryCalculatorContext visitSalary(
				SalaryType salaryType) {
			Date issueDate = endDate; // By default isuue date is equal to end
										// date
			Connection connection = getConnection();
			Criteria criteria = getCriteria();
			ISQLContractSalaryCalculatorContext sqlCtx = null;
			try {
				sqlCtx = new SQLContractSalaryCalculatorContext(connection,
						startDate, endDate, issueDate, criteria);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} catch (ExpressionException e) {
				throw new RuntimeException(e);
			}
			return sqlCtx;
		}

		@Override
		public ISQLContractSalaryCalculatorContext visitExtra(
				SalaryType salaryType) {
			Connection connection = getConnection();
			Criteria criteria = getCriteria();
			Date chargeDate = endDate; // By default issue date is equal to end date

			ISQLContractSalaryCalculatorContext sqlCtx = null;

			int year = CommonUtil.getYear(chargeDate);
			Month month = Month.getMonthByValue(CommonUtil.getMonth(chargeDate));
			
			// TODO: Get Extra for this month & contract.
			
			try {
				sqlCtx = new SQLExtraSalaryCalculatorContext(
						connection, 
						year,
						-1, 
						chargeDate, 
						criteria);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return sqlCtx;
		}

		@Override
		public ISQLContractSalaryCalculatorContext visitSettle(
				SalaryType salaryType) {
			Date issueDate = endDate; // By default isuue date is equal to end
										// date
			Connection connection = getConnection();
			Criteria criteria = getCriteria();
			ISQLContractSalaryCalculatorContext sqlCtx = null;
			try {
				sqlCtx = new SQLContractSettleCalculatorContext(connection,
						startDate, endDate, issueDate, criteria);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} catch (ExpressionException e) {
				throw new RuntimeException(e);
			}
			return sqlCtx;
		}

		@Override
		public ISQLContractSalaryCalculatorContext visitDelay(
				SalaryType salaryType) {

			Date issueDate = endDate; // By default isuue date is equal to end
										// date
			Connection connection = getConnection();
			Criteria criteria = getCriteria();
			ISQLContractSalaryCalculatorContext sqlCtx = null;
			try {
				sqlCtx = new SQLContractDelayCalculatorContext(connection,
						startDate, endDate, issueDate, criteria) {
					@Override
					protected String getDescriptionForSalaryDelay(
							IContractPayment payment, int ordinal) {
						return String
								.format("NOMINA DEL  %1$td/%1$tm/%1$tY  AL  %2$td/%2$tm/%2$tY",
										payment.getStartDate(),
										payment.getEndDate());
					}

					@Override
					protected String getDescriptionForExtraDelay(
							IContractPayment payment, int ordinal) {
						return String.format("PAGA EXTRA     %1$tm/%1$tY",
								payment.getEndDate());
					}
				};
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} catch (ExpressionException e) {
				throw new RuntimeException(e);
			}
			return sqlCtx;
		}
		
		@Override
		public ISQLContractSalaryCalculatorContext visitProcedural(SalaryType salaryType) {
			return visitSalary(salaryType);
		}

	}

	@Override
	public IListener getListener() {
		return ctx.getListener();
	}

	@Override
	public void setListener(IListener listener) {
		ctx.setListener(listener);
	}
	
	public ISalary getSalary(Date date) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IContractSalaryCalculatorContext getContractSalaryCalculatorContext(
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getDate(String tableLabel, String columnLabel) {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

}
