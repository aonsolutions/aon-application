package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.calculator.IContractCost;
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
			contractPayment.month = sqlContractPayment.getMonth();
			contractPayment.startDate = sqlContractPayment.getStartDate();
			contractPayment.endDate = sqlContractPayment.getEndDate();
			contractPayment.expression = sqlContractPayment.getExpression();
			contractPayment.expressionScope = sqlContractPayment.getScope();
			contractPayment.irpfExpression = sqlContractPayment.getIrpfExpression();
			contractPayment.quoteExpression = sqlContractPayment.getQuoteExpression();
			contractPayment.description = sqlContractPayment.getDescription();
			contractPayment.salaryType = sqlContractPayment.getSalaryType();
			contractPayment.descriptionDecorable = sqlContractPayment.isDescriptionDecorable();
			
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

			contractDeduction.id = sqlContractDeduction.getId();
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

	public static Collection<IContractCost> costsCollection(ResultSet rs) 
	throws SQLException {
		SQLContractCost sqlContractCosts = 
			new SQLContractCost(rs);
		List<IContractCost> contractCostList = 
			new ArrayList<IContractCost>();
		
		for (IContractCost sqlContractCost : sqlContractCosts) {
			ContractCost contractCost = 
				new ContractCost();

			contractCost.id = sqlContractCost.getId();
			contractCost.type = sqlContractCost.getType();
			contractCost.name = sqlContractCost.getName();
			contractCost.startDate = sqlContractCost.getStartDate();
			contractCost.endDate = sqlContractCost.getEndDate();
			contractCost.expression = sqlContractCost.getExpression();
			contractCost.description = sqlContractCost.getDescription();
			
			contractCostList.add(contractCost);
		}
		
		return contractCostList;
	}

	private static class ContractPayment implements IContractPayment{
		
		protected Integer id;
		protected String name;
		protected PaymentType type;
		protected String description;
		protected String expression;
		protected String irpfExpression;
		protected String quoteExpression;
		protected Date startDate;
		protected Date endDate;
		protected Month month;
		protected boolean readOnly;
		protected boolean descriptionDecorable;
		protected Double amount;
		protected ExpressionScope expressionScope;
		protected SalaryType salaryType;
		
		
		@Override
		public Integer getId() {
			return id;
		}
		
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
		
		@Override
		public boolean isDescriptionDecorable() {
			return descriptionDecorable;
		}
	}
	
	private static class ContractDeduction implements IContractDeduction {

		protected Integer id;
		protected String name;
		protected DeductionType type;
		protected String description;
		protected String expression;
		protected Date startDate;
		protected Date endDate;
		protected Month month;
		protected boolean readOnly;
		protected Double amount;
		protected ExpressionScope expressionScope;

		@Override
		public Integer getId() {
			// TODO Auto-generated method stub
			return null;
		}
		
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
	
	private static class ContractCost extends ContractDeduction implements IContractCost {
	}
}
