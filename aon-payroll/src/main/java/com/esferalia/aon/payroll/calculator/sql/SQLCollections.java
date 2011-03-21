package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SQLCollections {
	
	
	public static Collection<IContractPayment> paymentsCollection(ResultSet rs) 
		throws SQLException {
		SQLContractPayment sqlContractPayments = 
			new SQLContractPayment(rs);
		List<IContractPayment> contractPaymentList = 
			new ArrayList<IContractPayment>();
		
		for (IContractPayment sqlContractPayment : sqlContractPayments) {
			ContractPayment contractPayment = 
				new ContractPayment();

			contractPayment.type = sqlContractPayment.getType();
			contractPayment.name = sqlContractPayment.getName();
			contractPayment.startDate = sqlContractPayment.getStartDate();
			contractPayment.endDate = sqlContractPayment.getEndDate();
			contractPayment.expression = sqlContractPayment.getExpression();
			contractPayment.irpfExpression = sqlContractPayment.getIrpfExpression();
			contractPayment.quoteExpression = sqlContractPayment.getQuoteExpression();
			contractPayment.description = sqlContractPayment.getDescription();
			contractPayment.salaryType = sqlContractPayment.getSalaryType();
			
			contractPaymentList.add(contractPayment);
		}
		
		return contractPaymentList;
	}
	
	public static Collection<IContractDeduction> deductionsCollection(ResultSet rs) 
	throws SQLException {
		SQLContractDeduction sqlContractDeductions = 
			new SQLContractDeduction(rs);
		List<IContractDeduction> contractDeductionList = 
			new ArrayList<IContractDeduction>();
		
		for (IContractDeduction sqlContractDeduction : sqlContractDeductions) {
			ContractDeduction contractDeduction = 
				new ContractDeduction();

			contractDeduction.type = sqlContractDeduction.getType();
			contractDeduction.name = sqlContractDeduction.getName();
			contractDeduction.startDate = sqlContractDeduction.getStartDate();
			contractDeduction.endDate = sqlContractDeduction.getEndDate();
			contractDeduction.expression = sqlContractDeduction.getExpression();
			contractDeduction.description = sqlContractDeduction.getDescription();
			
			contractDeductionList.add(contractDeduction);
		}
		
		return contractDeductionList;
	}

	private static class ContractPayment implements IContractPayment{
		
		private String name;
		private PaymentType type;
		private String description;
		private String expression;
		private String irpfExpression;
		private String quoteExpression;
		private Date startDate;
		private Date endDate;
		private Month month;
		private boolean readOnly;
		private Double amount;
		private ExpressionScope expressionScope;
		private SalaryType salaryType;
		
		
		@Override
		public PaymentType getType() {
			return type;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getExpression() {
			return expression;
		}

		@Override
		public double getAmount() {
			return amount;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public ExpressionScope getScope() {
			return expressionScope;
		}

		@Override
		public boolean isReadOnly() {
			return readOnly;
		}

		@Override
		public Month getMonth() {
			return month;
		}

		@Override
		public Date getStartDate() {
			return startDate;
		}

		@Override
		public Date getEndDate() {
			return endDate;
		}

		@Override
		public String getIrpfExpression() {
			return irpfExpression;
		}

		@Override
		public String getQuoteExpression() {
			return quoteExpression;
		}

		@Override
		public SalaryType getSalaryType() {
			return salaryType;
		}
	}
	
	private static class ContractDeduction implements IContractDeduction {

		private String name;
		private DeductionType type;
		private String description;
		private String expression;
		private Date startDate;
		private Date endDate;
		private Month month;
		private boolean readOnly;
		private Double amount;
		private ExpressionScope expressionScope;

		@Override
		public DeductionType getType() {
			return type;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getExpression() {
			return expression;
		}

		@Override
		public double getAmount() {
			return amount;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public ExpressionScope getScope() {
			return expressionScope;
		}

		@Override
		public boolean isReadOnly() {
			return readOnly;
		}


		@Override
		public Date getStartDate() {
			return startDate;
		}

		@Override
		public Date getEndDate() {
			return endDate;
		}
		
	}

}
