package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.payroll.sql.SQLWriter;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.Payments;

public class SQLSalaryBuilder extends  AbstractSQLSalaryBuilder {

	private static final String FORMAT = "[%s]: %s - %s";
	
	private SQLWriter sqlWriter;
	private int insertedSalaries;
	private ISalaryBuilderListener listener;
	
	
	private SalaryAdapter salaryAdapter ;
	
	public SQLSalaryBuilder(Connection connection) 
	throws SQLException
	{
		super();
		salaryAdapter = new SalaryAdapter();
		sqlWriter = new SQLWriter(connection);
	}
	
	@Override
	public ISalary getSalary() {
		try {
			insertSalary();
			if (listener.isDebugEnabled()) {
				String msg = String.format(FORMAT, 
						salary.getEmployeeDocument(),
						salary.getEnterpriseName(),
						salary.getEmployeeName());
				listener.onDebug(msg);
			}
			++insertedSalaries;
		} catch (SQLException e) {
		}
		return salaryAdapter;
	}

	public void begin() throws SQLException{
		sqlWriter.begin();
	}
	
	public void commit() throws SQLException{
		sqlWriter.commit();
	}

	public void rollback() throws SQLException{
		sqlWriter.rollback();
	}

	public void insertSalary() throws SQLException{
		int domainId = salary.getDomain();
		int salaryId = sqlWriter.insertSalary(salary);
		for (AbstractSQL.SalaryCost salaryCost : salaryCosts) {
			salaryCost.setSalary(salaryId);
			salaryCost.setDomain(domainId);
			sqlWriter.insertSalaryCost(salaryCost);
		}

		for (AbstractSQL.SalaryBonus salaryBonus : salaryBonuses) {
			salaryBonus.setSalary(salaryId);
			salaryBonus.setDomain(domainId);
			sqlWriter.insertSalaryBonus(salaryBonus);
		}

		for (AbstractSQL.SalaryEmbargo salaryEmbargo : salaryEmbargos) {
			salaryEmbargo.setSalary(salaryId);
			salaryEmbargo.setDomain(domainId);
			sqlWriter.insertSalaryEmbargo(salaryEmbargo);
		}

		for (AbstractSQL.SalaryPayment salaryPayment : salaryPayments) {
			salaryPayment.setSalary(salaryId);
			salaryPayment.setDomain(domainId);
			sqlWriter.insertSalaryPayment(salaryPayment);
		}

		for (AbstractSQL.SalaryDeduction salaryDeduction : salaryDeductions) {
			salaryDeduction.setSalary(salaryId);
			salaryDeduction.setDomain(domainId);
			sqlWriter.insertSalaryDeduction(salaryDeduction);
		}
	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		this.listener = listener;		
	}
	public ISalaryBuilderListener getListener() {
		return listener;
	}

	public int getInsertedSalaries() {
		return insertedSalaries;
	}
	
	
	private class SalaryAdapter implements ISalary{
		
		@Override
		public String getCategory() {
			return salary.getCategory();
		}

		@Override
		public String getCcc() {
			return salary.getCcc();
		}

		@Override
		public Double getCommonBase() {
			return salary.getCgcBase();
		}
		
				
		@Override
		public Double getProfessionalBase() {
			return salary.getCgpBase();
		}

		@Override
		public java.sql.Date getChargeDate() {
			return salary.getChargeDate();
		}

		@Override
		public String getEmployeeDocument() {
			return salary.getEmployeeDocument();
		}

		@Override
		public String getEmployeeName() {
			return salary.getEmployeeName();
		}

		@Override
		public java.sql.Date getEndDate() {
			return salary.getEndDate();
		}

		@Override
		public String getEnterpriseAddress() {
			return salary.getEnterpriseAddress();
		}

		@Override
		public String getEnterpriseDocument() {
			return salary.getEnterpriseDocument();
		}

		@Override
		public String getEnterpriseName() {
			return salary.getEnterpriseName();
		}
		
		@Override
		public Double getOvertimeBase() {
			return salary.getHextraBase();
		}

		@Override
		public Double getIrpfBase() {
			return salary.getIrpfBase();
		}

		@Override
		public java.sql.Date getIssueDate() {
			return salary.getIssueDate();
		}


		@Override
		public Double getNonEstructuralOvertimeBase() {
			return salary.getNonHextraBase();
		}

		
		@Override
		public Double getExtraPayProration() {
			return salary.getProExtBase();
		}

		@Override
		public String getQuoteGroup() {
			return salary.getQuoteGroup();
		}
		
		
		@Override
		public Double getRawCommonBase() {
			return salary.getRawCgcBase();
		}

		@Override
		public Integer getRegistration() {
			return salary.getRegistration();
		}

		@Override
		public Double getRemuneration() {
			return salary.getRemuneration();
		}

		@Override
		public java.sql.Date getSeniorityDate() {
			return salary.getSeniorityDate();
		}

		@Override
		public Double getSocialSecurityContributions() {
			return salary.getSocialSecurityContributions();
		}

		@Override
		public String getSocialSecurityNumber() {
			return salary.getSocialSecurityNumber();
		}

		@Override
		public java.sql.Date getStartDate() {
			return salary.getStartDate();
		}

		@Override
		public Integer getTimeUnits() {
			return salary.getTimeUnits();
		}

		@Override
		public Double getTotalDeduction() {
			return salary.getTotalDeduction();
		}

		@Override
		public Double getTotalEnterprise() {
			return salary.getTotalEnterprise();
		}

		@Override
		public Double getTotalIrpf() {
			return salary.getTotalIrpf();
		}

		@Override
		public Double getTotalLiquid() {
			return salary.getTotalLiquid();
		}

		@Override
		public Double getTotalPayment() {
			return salary.getTotalPayment();
		}
		
		@Override
		public SalaryType getType() {
			Short type = salary.getType();
			if ( type == null)
				return null;
			if ( type < 0 )
				return null;
			
			SalaryType types [] = SalaryType.values();
			if ( type >= types.length )
				return null;
			
			return types[type];
		}

		@Override
		public boolean isFullTime() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Payments getPayments() throws SalaryException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Deductions getDeductions() throws SalaryException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends IPayment> Collection<T> getPaymentS()
				throws SalaryException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends IDeduction> Collection<T> getDeductionS()
				throws SalaryException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
