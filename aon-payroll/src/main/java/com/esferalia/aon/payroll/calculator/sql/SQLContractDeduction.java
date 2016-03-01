package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.util.Date;

import com.code.aon.AonVersion;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDeductionColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.DeductionConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SQLContractDeduction 
	extends SQLCollection<IContractDeduction> 
	implements IContractDeduction {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static final String SCOPE_ALIAS = "scope";

	public SQLContractDeduction() {
	}
	
	
	protected SQLContractDeduction(ResultSet resultSet) {
		super(resultSet);
	}
	
	//-------------------------------------------
	// SQLCollection<IContractDeduction>
	//-------------------------------------------

	@Override
	public IContractDeduction next() {
		return this;
	}

	//-------------------------------------------
	// IContractDeduction
	//-------------------------------------------
	
	@Override
	public Integer getId() {
		return getInt(ContractDeductionColumns.ID);
	}
	
	@Override
	public ExpressionScope getScope() {
		return getEnum(SCOPE_ALIAS, ExpressionScope.class );
	}

	@Override
	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getAmount() {
		throw new UnsupportedOperationException();
	}


	@Override
	public String getName() {
		return getString(PaymentConceptColumns.CODE);
	}

	@Override
	public Date getStartDate() {
		return getDate(ContractDeductionColumns.START_DATE);
	}

	@Override
	public Date getEndDate() {
		return getDate(ContractDeductionColumns.END_DATE);
	}
	@Override
	public DeductionType getType() {
		Integer ordinal = getInt(ContractDeductionColumns.TYPE, 
				DeductionConceptColumns.TYPE);
		try {
			return DeductionType.values()[ordinal];
		} catch ( NullPointerException e) {
			return DeductionType.OTHER; 
		}catch ( IndexOutOfBoundsException e) {
			return DeductionType.OTHER; 
		}
	}

	@Override
	public String getDescription() {
		return getString(ContractDeductionColumns.DESCRIPTION, 
				DeductionConceptColumns.DESCRIPTION);
	}

	@Override
	public String getExpression() {
		return getString(ContractDeductionColumns.EXPRESSION, 
				DeductionConceptColumns.EXPRESSION);
	}
	
	
	private Integer getInt(String deductionLabel, String conceptColumn ) {
		return super.getInt(deductionLabel, SQLConstants.DEDUCTION_CONCEPT +"."+ conceptColumn );
	}

	private String getString(String deductionLabel, String conceptColumn ) {
		return super.getString(deductionLabel, SQLConstants.DEDUCTION_CONCEPT +"."+ conceptColumn );
	}
	
	
}
