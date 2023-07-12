package com.esferalia.aon.payroll.calculator.test;

import static com.esferalia.aon.payroll.AgreementExtra.parseAgreementEndDate;
import static com.esferalia.aon.payroll.AgreementExtra.parseAgreementStartDate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractExtraCalculatorContext;
import com.esferalia.aon.payroll.sql.AbstractSQL.AgreementExtra;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class ContractExtraCalculatorTest extends AbstractSalaryCalculatorTest{

	private static final String EXTRA_SQL = "SELECT agreement_extra.*"
		+" FROM contract"
		+", agreement_level_category"
		+", agreement_level"
		+", agreement_extra"
		+" WHERE contract.agreement_level_category = agreement_level_category.id"
		+" AND agreement_level_category.agreement_level = agreement_level.id"
		+" AND agreement_level.agreement = agreement_extra.agreement"
		+" AND contract.id = ? "
		+" AND agreement_extra.issue_date = ? "
		;
	
	private PreparedStatement extraStmt = null;
	
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		extraStmt = connection.prepareStatement(EXTRA_SQL);
	}

	@Override
	public void tearDown() throws Exception {
		if ( extraStmt != null )
			extraStmt.close();
		super.tearDown();
	}
	
	@Override
	protected SalaryType getType() {
		return SalaryType.EXTRA;
	}
	
	
	@Override
	protected Criteria getCriteria() {
		Criteria criteria =  
				super.getCriteria();
		/*
		criteria.addEqualExpression(SalaryColumns.EMPLOYEE_DOCUMENT, 
				"08968656J" ); */ 
		return criteria;
	}
	
	@Override
	protected ISQLContractSalaryCalculatorContext getSQLContractSalaryCalculatorContext ( Connection connection,
			Integer contract, Date startDate, Date endDate, Date issueDate, Date chargeDate ) 
					throws SQLException, ExpressionException, SalaryException {
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( SQLConstants.CONTRACT + "." + ContractColumns.ID, contract);
		
		
		//long extraDays = CommonUtil.getDaysBetweenDates(startDate, endDate);
		
		
		AgreementExtra extra = getExtra(contract, chargeDate);
		
		if ( extra != null ) {
		    
			endDate = parseAgreementEndDate(extra.getEndDate(), CommonUtil.getYear(chargeDate));
			startDate = parseAgreementStartDate(extra.getStartDate(), CommonUtil.getYear(chargeDate));
		}
		
		return new SQLContractExtraCalculatorContext(connection, startDate, endDate, endDate, chargeDate, criteria, ISQLContractSalaryCalculatorContext.OLDER);
	}
	
	
	private AgreementExtra getExtra (Integer contract, Date chargeDate ) throws SQLException{
		
		ResultSet rs = null;
		
		try {
			extraStmt.setInt(1, contract);										// contract.id = ?
			extraStmt.setString(2, String.format("%1$td/%1$tm", chargeDate) );	// agreement_extra.issue_date = ?
			
			rs = extraStmt.executeQuery();
			
			if ( !rs.next() ) {
				return null;
			}
			
			
			AgreementExtra extra = new AgreementExtra();
			extra.setEndDate(rs.getString(AgreementExtraColumns.END_DATE));
			extra.setStartDate(rs.getString(AgreementExtraColumns.START_DATE));
			extra.setIssueDate(rs.getString(AgreementExtraColumns.ISSUE_DATE));
			
			if ( rs.next()) {
				return null; 
			} // TODO : more than one extra.
			
			return extra ;
			
		} finally {
			if ( rs != null )
				rs.close();
		}
	}

}
