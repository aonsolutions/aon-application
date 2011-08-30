package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.AgreementExtra.parseAgreementDate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelColumns;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLContractExtraCalculatorContext 
	extends SQLContractSalaryCalculatorContext{

	
	
	
	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate) throws SQLException,
			ExpressionException {
		this( connection , startDate, endDate, issueDate, null);
	}

	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria) throws SQLException,
			ExpressionException {
		this( connection , startDate, endDate, issueDate, criteria, getPaymentsCriteria(SalaryType.EXTRA));
	}
	
	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria, Criteria paymentsCriteria) throws SQLException,
			ExpressionException {
		this( connection , startDate, endDate, issueDate, null,criteria, paymentsCriteria );
	}

	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate, Criteria criteria, Criteria paymentsCriteria) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, chargeDate, criteria, paymentsCriteria );
	}

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.EXTRA;
	}
	

	
	//-------------------------------------------
	//
	//-------------------------------------------
	
	public static class DateFormatException  extends IllegalArgumentException {
		
	}
	

	public static interface AgreementExtraCallback {
		public void sqlContractExtraCalculatorContext(IContractSalaryCalculatorContext ctx)throws AonException;
	}

	public static void foreachAgreementExtra(Connection connection, int year , Criteria criteria, AgreementExtraCallback cb) 
	 throws SQLException, ExpressionException, AonException {
		String sql = CriteriaUtilities.toSQLString(criteria, "SELECT *  FROM agreement_extra");
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while ( rs.next() ) {
				Date startDate = 
					parseAgreementDate(rs.getString(AgreementExtraColumns.START_DATE), year);
				Date endDate =  
					parseAgreementDate(rs.getString(AgreementExtraColumns.END_DATE), year);
				
				if ( ! startDate.after(endDate) ) {
				
					Date issueDate =  
						parseAgreementDate(rs.getString(AgreementExtraColumns.ISSUE_DATE), year);

					Criteria agreementCriteria = 
							new Criteria();
					agreementCriteria.addEqualExpression(
							SQLConstants.AGREEMENT_LEVEL + "." + AgreementLevelColumns.AGREEMENT, 
							rs.getInt(AgreementExtraColumns.AGREEMENT));
					
					SQLContractExtraCalculatorContext ctx = 
						new SQLContractExtraCalculatorContext(connection, 
								startDate, 
								endDate, 
								issueDate, 
								issueDate,
								agreementCriteria, 
								getPaymentsCriteria(SalaryType.EXTRA));
					while ( ctx.next() ) {
						cb.sqlContractExtraCalculatorContext(ctx);
					}
					ctx.close();
				}
			}
		}
		finally {
			if ( stmt != null ){
				stmt.close();
			}
			if ( stmt != null ){
				stmt.close();
			}
		}
	}
	
	
	
}