package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.ISystemCost;
import com.esferalia.aon.payroll.calculator.ISystemDeduction;
import com.esferalia.aon.payroll.calculator.ISystemPayment;
import com.esferalia.aon.payroll.calculator.SimpleContractDeduction;
import com.esferalia.aon.payroll.calculator.SimpleContractPayment;
import com.esferalia.aon.payroll.calculator.SimpleSystemCost;
import com.esferalia.aon.payroll.calculator.SimpleSystemDeduction;
import com.esferalia.aon.payroll.calculator.SimpleSystemPayment;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemCostColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDeductionColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemPaymentColumns;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SQLCollections {
	
	
	public static Collection<ISystemPayment> systemPaymentsCollection(ResultSet rs) 
			throws SQLException {
			
			SQLContractPayment sqlContractPayments = 
				new SQLContractPayment(rs);
			List<ISystemPayment> systemPaymentList = 
				new ArrayList<ISystemPayment>();
			
			for (IContractPayment sqlContractPayment : sqlContractPayments) {
				
				SimpleSystemPayment systemPayment = 
					new SimpleSystemPayment(sqlContractPayment, rs.getInt(SystemPaymentColumns.DOMAIN));
			
				systemPaymentList.add(systemPayment);
			}
			
			return systemPaymentList;
		}

	public static Collection<ISystemPayment> agreementPaymentsCollection(ResultSet rs) 
			throws SQLException {
			
			SQLContractPayment sqlContractPayments = 
				new SQLContractPayment(rs);
			List<ISystemPayment> systemPaymentList = 
				new ArrayList<ISystemPayment>();
			
			for (IContractPayment sqlContractPayment : sqlContractPayments) {
				
				SimpleSystemPayment systemPayment = 
					new SimpleSystemPayment(sqlContractPayment, rs.getInt(AgreementPaymentColumns.DOMAIN));
			
				systemPaymentList.add(systemPayment);
			}
			
			return systemPaymentList;
		}

	public static Collection<IContractPayment> contractPaymentsCollection(ResultSet rs) 
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
	
	public static Collection<ISystemDeduction> systemDeductionsCollection(ResultSet rs) 
	throws SQLException {
		SQLContractDeduction sqlSystemDeductions = 
			new SQLContractDeduction(rs);
		List<ISystemDeduction> systemDeductionList = 
			new ArrayList<ISystemDeduction>();
		
		for (IContractDeduction sqlSystemDeduction : sqlSystemDeductions) {
			SimpleSystemDeduction systemDeduction = 
				new SimpleSystemDeduction(sqlSystemDeduction);
			systemDeduction.setDomain(rs.getInt(SystemDeductionColumns.DOMAIN));
			systemDeductionList.add(systemDeduction);
		}
		
		return systemDeductionList;
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

	public static Collection<ISystemCost> systemCostsCollection(ResultSet rs) 
	throws SQLException {
		SQLContractCost sqlContractCosts = 
			new SQLContractCost(rs);
		List<ISystemCost> systemCostList = 
			new ArrayList<ISystemCost>();
		
		for (IContractCost sqlContractCost : sqlContractCosts) {
			SimpleSystemCost systemCost = 
				new SimpleSystemCost(sqlContractCost);
			systemCost.setDomain(rs.getInt(SystemCostColumns.DOMAIN));

			systemCostList.add(systemCost);
		}
		
		return systemCostList;
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
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		public ContractCost() {
		}
		public ContractCost(IContractCost contractCost){
			super(contractCost);
		}
	}
}
