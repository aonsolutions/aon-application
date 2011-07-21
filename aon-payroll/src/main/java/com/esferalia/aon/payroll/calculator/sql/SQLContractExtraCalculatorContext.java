package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.AbstractIterator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.ActiveTimedVariable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.AbstractSQL.AgreementLevel;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractPaymentColumns;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.sun.org.apache.bcel.internal.generic.ISUB;

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
	
	private static final Pattern AGREEMENT_DATE_PATTERN = 
		Pattern.compile("(\\d+)/(\\d+)\\s*\\+?([-]?\\d+)?");
	/**
	 * 
	 * @param string dd/mm [year offset]
	 * @return
	 */
	public static Date parseAgreementDate ( String string, int year ) {
		
		Matcher matcher = AGREEMENT_DATE_PATTERN.matcher(string);
		
		if ( !matcher.matches() ) {
			throw new  DateFormatException();
		}

		String days = matcher.group(1);
		String month = matcher.group(2);
		String yearOffset = matcher.group(3);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(days));
		calendar.set(Calendar.MONTH, Integer.parseInt(month)-1);
		if ( yearOffset != null ) {
			year += Integer.parseInt(yearOffset);
		}
		calendar.set(Calendar.YEAR,  year );
		return calendar.getTime();
	}
	
	
	
}