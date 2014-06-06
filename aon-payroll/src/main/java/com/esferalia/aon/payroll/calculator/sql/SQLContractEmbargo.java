package com.esferalia.aon.payroll.calculator.sql;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.AonVersion;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractEmbargoColumns;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SQLContractEmbargo 
	extends SQLCollection<IContractEmbargo> 
	implements IContractEmbargo{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public SQLContractEmbargo() {
	}
	
	
	protected SQLContractEmbargo(ResultSet resultSet) {
		super(resultSet);
	}
	
	//-------------------------------------------
	// SQLCollection<IContractDeduction>
	//-------------------------------------------

	@Override
	public IContractEmbargo next() {
		return this;
	}

	//-------------------------------------------
	// IContractDeduction
	//-------------------------------------------
	
	@Override
	public Integer getId() {
		return getInt(ContractEmbargoColumns.ID);
	}
	
	@Override
	public Integer getEmbargo() {
		return getInt(ContractEmbargoColumns.ID);
	}

	@Override
	public DeductionType getType() {
		return DeductionType.OTHER;
	}


	@Override
	public String getDescription() {
		return getString(ContractEmbargoColumns.DESCRIPTION);
	}


	@Override
	public String getExpression() {
		return getString(ContractEmbargoColumns.EXPRESSION);
	}


	@Override
	public double getAmount() {
		double total = getDouble(ContractEmbargoColumns.AMOUNT);
		double paid = getDouble(SQLContractSalaryCalculatorContext.EMBARGO_PAID);
		return total - paid ;
	}


	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}


	@Override
	public ExpressionScope getScope() {
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}


	@Override
	public Date getStartDate() {
		return getDate(ContractEmbargoColumns.START_DATE);
	}


	@Override
	public Date getEndDate() {
		return getDate(ContractEmbargoColumns.END_DATE);
	}

	private Double getDouble(String columnName) {
		try {
			BigDecimal value = this.resultSet.getBigDecimal(columnName);
			return value == null ? 0.00 : value.doubleValue();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
