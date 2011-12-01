package com.esferalia.aon.payroll.calculator.sql;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDeductionColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractEmbargoColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.DeductionConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemCostColumns;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SQLContractCost 
	extends SQLCollection<IContractCost> 
	implements IContractCost{
	
	public SQLContractCost() {
	}
	
	
	protected SQLContractCost(ResultSet resultSet) {
		super(resultSet);
	}
	
	//-------------------------------------------
	// SQLCollection<IContractDeduction>
	//-------------------------------------------

	@Override
	public IContractCost next() {
		return this;
	}

	//-------------------------------------------
	// IContractDeduction
	//-------------------------------------------
	

	@Override
	public DeductionType getType() {
		Integer ordinal = getInt(SystemCostColumns.TYPE);
		return ordinal != null ? DeductionType.values()[ordinal] : null ;
	}


	@Override
	public String getDescription() {
		return getString(SystemCostColumns.DESCRIPTION);
	}


	@Override
	public String getExpression() {
		return getString(SystemCostColumns.EXPRESSION);
	}


	@Override
	public double getAmount() {
		throw new UnsupportedOperationException();
	}


	@Override
	public String getName() {
		return getString(SystemCostColumns.CODE);
	}


	@Override
	public ExpressionScope getScope() {
		return ExpressionScope.SYSTEM;
	}


	@Override
	public boolean isReadOnly() {
		return true;
	}


	@Override
	public Date getStartDate() {
		return getDate(SystemCostColumns.START_DATE);
	}


	@Override
	public Date getEndDate() {
		return getDate(SystemCostColumns.END_DATE);
	}

	
}
