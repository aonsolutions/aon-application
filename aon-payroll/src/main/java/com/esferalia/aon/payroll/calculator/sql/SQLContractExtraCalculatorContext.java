package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.AgreementExtra.parseAgreementDate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.esferalia.aon.payroll.calculator.AbstractIterator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelColumns;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLContractExtraCalculatorContext 
	extends SQLContractSalaryCalculatorContext{

	
	
	
	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, null);
	}

	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, criteria );
	}
	

	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate, Criteria criteria) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, chargeDate,criteria);
	}

	public SQLContractExtraCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate, Criteria criteria, OrderByList orderByList) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, chargeDate,criteria, OLDER);
	}

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.EXTRA;
	}
	
	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		int chargeMonth = getChargeMonth();
		Collection<IContractPayment> extraPayments = 
				new FilterCollection<IContractPayment>(
						new ExtraPaymentFilter(chargeMonth), 
						super.getContractPayments()); 
		return extraPayments;
	}
	
	private int getChargeMonth(){
		Date chargeDate = getChargeDate();
		return CommonUtil.getMonth(chargeDate);
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
								agreementCriteria);
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
	
	
	private static class FilterCollection<E> extends AbstractIterator<E>{
		
		private interface Filter<E> {
			boolean accept(E e );
		}
		
		private Filter<E> filter;
		private Iterator<E> iterator;
		
		private E next;
		
		public FilterCollection( Filter<E> filter, 
				Collection<E> collection ) {
			this.filter = filter;
			this.iterator = collection.iterator();
		}

		@Override
		public boolean hasNext() {
			if ( next == null ) {
				next = _next();
			}
			return next != null ;
		}

		@Override
		public E next() {
			if ( hasNext() ) {
				E e = next;
				next = null;
				return e;
			}
			else {
				throw new NoSuchElementException();				
			}
		}
		
		private E _next() {
			while (iterator.hasNext()) {
				E e = (E) iterator.next();
				if ( filter.accept(e)) {
					return e;
				}
			}
			return null;
		}
		
	}
	
	private static class ExtraPaymentFilter 
		implements FilterCollection.Filter<IContractPayment>
	{
		private Month month ;
		
		public ExtraPaymentFilter(int month) {
			this.month = Month.getMonthByValue(month);
		}
		
		@Override
		public boolean accept(IContractPayment e) {
			return e.getSalaryType() == SalaryType.EXTRA && 
					e.getMonth() == this.month;
		}
	}
	
}