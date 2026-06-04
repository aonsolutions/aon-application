package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.code.aon.AonVersion;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.BonusConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractBonusColumns;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SQLContractBonus extends SQLCollection<IContractBonus> implements IContractBonus{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public SQLContractBonus() {
	}
	
	
	protected SQLContractBonus(ResultSet resultSet) {
		super(resultSet);
	}

	@Override
	public IContractBonus next() {
		return this;
	}
	
	@Override
	public Integer getId() {
		return getInt(ContractBonusColumns.ID);
	}

	@Override
	public String getName() {
		Integer concept = getInt(ContractBonusColumns.BONUS_CONCEPT);
		return concept == null ? getPecAndQuota(getExpression()) : concept.toString();
	}

	@Override
	public double getAmount() {
		throw new UnsupportedOperationException();
	}


	@Override
	public ExpressionScope getScope() {
		return ExpressionScope.CONTRACT;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public Date getStartDate() {
		return getDate(ContractBonusColumns.START_DATE);
	}

	@Override
	public Date getEndDate() {
		return getDate(ContractBonusColumns.END_DATE);
	}

	@Override
	public String getDescription() {
		return getString(ContractBonusColumns.DESCRIPTION, BonusConceptColumns.DESCRIPTION );
	}

	@Override
	public String getExpression() {
		return getString(ContractBonusColumns.EXPRESSION, BonusConceptColumns.EXPRESSION );
	}
	
	private String getString(String deductionLabel, String conceptColumn ) {
		return super.getString(deductionLabel, SQLConstants.BONUS_CONCEPT +"."+ conceptColumn );
	}
	
	@Override
	public BonusType getType() {
		Integer ordinal = getInt(BonusConceptColumns.TYPE);
		return ordinal != null ? BonusType.values()[ordinal] : null ;
	}
	
	public static String getPecAndQuota(String string) {
		if ( AonStringUtils.isBlank(string) )
			return null;
		Matcher matcher = Pattern.compile("pec:\\d+,quota:\\d+").matcher(string);
		return matcher.find() ? matcher.group() : null;
	}
	
	
}
