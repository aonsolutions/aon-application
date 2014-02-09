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
import com.esferalia.aon.payroll.calculator.SimpleContractDeduction;
import com.esferalia.aon.payroll.calculator.SimpleContractPayment;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SQLCollections {
	
	
	public static Collection<IContractPayment> paymentsCollection(ResultSet rs) 
		throws SQLException {
		SQLContractPayment sqlContractPayments = 
			new SQLContractPayment(rs);
		List<IContractPayment> contractPaymentList = 
			new ArrayList<IContractPayment>();
		
		for (IContractPayment sqlContractPayment : sqlContractPayments) {
			SimpleContractPayment contractPayment = 
				new SimpleContractPayment(sqlContractPayment);
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
			SimpleContractDeduction contractDeduction = 
				new SimpleContractDeduction(sqlContractDeduction);

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
				new ContractCost(sqlContractCost);

			contractCostList.add(contractCost);
		}
		
		return contractCostList;
	}

	
	private static class ContractCost extends SimpleContractDeduction implements IContractCost {
		
		public ContractCost() {
		}
		public ContractCost(IContractCost contractCost){
			super(contractCost);
		}
	}
}
