package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.calculator.CompositeIterator;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.salary.AbstractSalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;

public class SQLContractDelayCalculatorContext 
	extends SQLContractSalaryCalculatorContext{

	public SQLContractDelayCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate) throws SQLException,
			ExpressionException {
		this( connection , startDate, endDate, issueDate, null);
	}

	public SQLContractDelayCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, criteria, getPaymentsCriteria(SalaryType.DELAY));
	}
	

	public SQLContractDelayCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate, Criteria criteria ) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, chargeDate, criteria, getPaymentsCriteria(SalaryType.DELAY) );
	}

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.DELAY;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {

		Collection<IContractPayment> explicitPayments = 
			super.getContractPayments();
		
		try {
			Collection<IContractPayment> implicitPayments  = 
				getDifferencePayments();

			return new CompositeIterator<IContractPayment>(
					explicitPayments.iterator(), 
					implicitPayments.iterator());
		} catch (SQLException e) {
			throw new AonException(e);
		}
		
	}
	
	private Collection<IContractPayment>  getDifferencePayments() 
		throws ExpressionException, SQLException, SalaryException{
		
		
		Date startDate = getStartDate();
		Date endDate = getEndDate();
		Date chargeDate = getChargeDate();
		
		Connection connection = 
			getConnection();
		
		Criteria criteria  = new Criteria();
		
		String identifier = String.format("%s.%s", 
				SQLConstants.CONTRACT,
				ContractColumns.ID );
		criteria.addEqualExpression(
				identifier, 
				getId());
		
		ContractSalaryCalculator calculator = 
			new ContractSalaryCalculator();
		DelayPaymentBuilder delayPaymentBuilder = 
			new DelayPaymentBuilder(getConnection());
		calculator.setSalaryBuilder(delayPaymentBuilder);
		
		Collection<Period> periods = 
			split(startDate, endDate);
		
		Collection<IContractPayment> payments = 
			new LinkedList<IContractPayment>();
		
		for (Period period : periods) {
			ISQLContractSalaryCalculatorContext ctx = 
				new SQLContractSalaryCalculatorContext(
						connection, 
						period.getStart(),
						period.getEnd(),
						period.getEnd(),
						chargeDate,
						criteria);
			while ( ctx.next() ) {
				calculator.calculate(ctx);
			}
			
			payments.addAll(delayPaymentBuilder.getContractPayments());
		}
		
		Collection<Period> extras = getExtras(startDate, endDate);
		for (Period extra : extras) {
			ISQLContractSalaryCalculatorContext ctx = 
				new SQLContractExtraCalculatorContext(
						 connection, 
						 extra.getStart(), 
						 extra.getEnd(), 
						 extra.getEnd(), 
						 chargeDate, 
						 criteria );
			while ( ctx.next() ) {
				calculator.calculate(ctx);
			}
			payments.addAll(delayPaymentBuilder.getContractPayments());
		}
		
		return payments;

	}
	
	private Collection<Period> getExtras(Date startDate, Date endDate) 
	throws SQLException{
		Integer agreement = getAgreement();
		if ( agreement != null ) {
			return getExtras(agreement, startDate, endDate);
		}
		else {
			return getPaidExtras(startDate, endDate);
		}
	}
	
	private Collection<Period> getExtras(Integer agreement, Date startDate, Date endDate) 
		throws SQLException{
		
		Collection<Period> extras = 
			new LinkedList<Period>();
		
		ResultSet			rs = null;
		PreparedStatement	stmt = null;
		
		Collection<Integer> years = 
			years(startDate, endDate);
		
		Period period = new Period(startDate, endDate);
		
		try {
			Connection connection =
				getConnection();
			stmt = connection.prepareStatement(
				"SELECT *"
				+" FROM agreement_extra"
				+" WHERE agreement= ?"
			);
			stmt.setInt( 1, agreement );
			rs = stmt.executeQuery();
			while ( rs.next() ) {
				String extraIssue = 
					rs.getString(AgreementExtraColumns.ISSUE_DATE);
				String extraStart = 
					rs.getString(AgreementExtraColumns.START_DATE);
				String extraEnd = 
					rs.getString(AgreementExtraColumns.END_DATE);			
				
				for (Integer year : years) {
					Date extraIssueDate = 
						AgreementExtra.parseAgreementDate(extraIssue, year);
					
					if ( period.contains(extraIssueDate) ) {
						Date extraStartDate = 
							AgreementExtra.parseAgreementDate(extraStart, year);
						Date extraEndDate = 
							AgreementExtra.parseAgreementDate(extraEnd, year);
						Period extraPeriod = new Period ( extraStartDate, extraEndDate);
						extras.add(extraPeriod);
					}
				}
			}
		} finally {
			if ( rs != null ) {
				rs.close();
			}
			if ( stmt != null ) {
				stmt.close();
			}
		}
		
		return extras;
	}	
	
	
	private Collection<Period> getPaidExtras(Date startDate, Date endDate) 
	throws SQLException{
		Collection<Period> extras = 
			new LinkedList<Period>();
		
		ResultSet			rs = null;
		PreparedStatement	stmt = null;
		
		Collection<Integer> years = 
			years(startDate, endDate);
		
		Period period = new Period(startDate, endDate);
		
		try {
			Connection connection =
				getConnection();
			stmt = connection.prepareStatement(
				"SELECT *"
				+" FROM salary"
				+" WHERE contract = ?"
				+" AND type = ? "
				+" AND issue_date >= ? "
				+" AND issue_date <= ? "
			);
			stmt.setInt( 1, getId() );
			stmt.setInt( 2, SalaryType.EXTRA.ordinal() );
			java.sql.Date sqlStartDate = 
				new java.sql.Date(startDate.getTime());
			stmt.setDate( 3, sqlStartDate );
			java.sql.Date sqlEndDate = 
				new java.sql.Date(endDate.getTime());
			stmt.setDate( 4, sqlEndDate );
			rs = stmt.executeQuery();
			while ( rs.next() ) {
				Date extraStartDate = 
					rs.getDate(AgreementExtraColumns.START_DATE);
				Date extraEndDate = 
					rs.getDate(AgreementExtraColumns.END_DATE);
				Period extraPeriod = new Period ( extraStartDate, extraEndDate);
				extras.add(extraPeriod);
			}
		} finally {
			if ( rs != null ) {
				rs.close();
			}
			if ( stmt != null ) {
				stmt.close();
			}
		}
		
		return extras;
		
	}
	
	private static Collection<Integer> years ( Date startDate, Date endDate) {
		int start = CommonUtil.getYear(startDate);
		int end = CommonUtil.getYear(startDate);
		Collection<Integer> years = new LinkedList<Integer>();
		for ( int year=start; year <= end ; year++ ) {
			years.add(year);
		}
		return years;
	}
	
	private static Collection<Period> split(Date startDate, Date endDate) {
		
		Collection<Period> periods = 
			new LinkedList<Period>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate) ;
		
		int startMonth = calendar.get(Calendar.MONTH) ;
		int endMonth = CommonUtil.getMonth(endDate) ;

		for ( int month = startMonth ; month <= endMonth ; month++){
			calendar.set(Calendar.MONTH, month);

			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date start = calendar.getTime();
			
			calendar.set(Calendar.DAY_OF_MONTH, 
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date end = calendar.getTime();
			
			periods.add(new Period(start, end));
		}

		return periods;

	}
	
	private static class DelayPaymentBuilder extends AbstractSalaryBuilder {
		
		
		
		private static final String SALARY_SQL = "SELECT *"
			+ " FROM " + SQLConstants.SALARY
			+ " WHERE " + SalaryColumns.CONTRACT + " = ? "
			+ " AND " + SalaryColumns.TYPE + "  = ? "
			+ " AND " + SalaryColumns.START_DATE + "  = ? "
			+ " AND " + SalaryColumns.END_DATE + " = ? ";
		
		
		private SalaryType type;
		private Date startDate;
		private Date endDate;
		protected Integer contract;
		
		private PreparedStatement stmt;
		
		private Map<String, Double> values;

		public DelayPaymentBuilder(Connection connection) 
			throws SQLException{
			this.values = new HashMap<String, Double>();
			this.stmt = connection.prepareStatement(SALARY_SQL);
		}
		
		@Override
		public void setType(SalaryType type) {
			this.type = type;
		}

		@Override
		public void setContract(Object contract) {
			this.contract = ( ( SQLSalaryProxy ) contract).getId();
		}
		
		@Override
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		@Override
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		
		@Override
		public void setCgcBase(Double cgcBase) {
			values.put(SalaryColumns.CGC_BASE, cgcBase);
		}
		
		@Override
		public void setTotalPayment(Double totalPayment) {
			values.put(SalaryColumns.TOTAL_PAYMENT, totalPayment);
		}
		
		@Override
		public void setIrpfBase(Double irpfBase) {
			values.put(SalaryColumns.IRPF_BASE, irpfBase);
		}
		
		
		
		public Collection<IContractPayment>  getContractPayments() throws SQLException{
			
			Set<String> fields = values.keySet() ;
			
			Map<String, Double> paidValues = 
				getPaidSalary( fields );
			
			
			Map<String , Double> diffValues = 
				new HashMap<String, Double>();
			
			for (String field : fields) {
				Double value = values.get(field);
				Double paidValue = paidValues.get(field);
				Double diffValue = value -paidValue;
				diffValues.put(field, diffValue);
				/*
				System.out.printf("%s : %f - %f = %f.\r\n", 
						field, 
						value, 
						paidValue,
						value -paidValue
						);
						*/
			}
			
			List<IContractPayment> payments = 
				new LinkedList<IContractPayment>();
			
			ContractPayment payment = createContractPayment(
					diffValues.get(SalaryColumns.TOTAL_PAYMENT),
					diffValues.get(SalaryColumns.IRPF_BASE),
					diffValues.get(SalaryColumns.CGC_BASE) );
			payments.add(payment);
			
			
			return payments;
		}
		
		private Map<String, Double> getPaidSalary(Set<String> fields) throws SQLException {
			ResultSet rs = null;
			try {
				this.stmt.setInt(1, this.contract); 		// SalaryColumns.CONTRACT + " = ? "
				this.stmt.setInt(2, this.type.ordinal()); 	// SalaryColumns.TYPE + " = ? "
				java.sql.Date sqlStartDate = 
					new java.sql.Date(startDate.getTime());
				this.stmt.setDate(3, sqlStartDate); 		// SalaryColumns.START_DATE + "  = ? "
				java.sql.Date sqlEndDate = 
					new java.sql.Date(endDate.getTime());
				this.stmt.setDate(4, sqlEndDate); 			// SalaryColumns.END_DATE + "  = ? "
				rs = this.stmt.executeQuery();
				
				Map<String, Double> values =
					new HashMap<String, Double>();
				
				for (String field : fields) {
					values.put(field, 0.00 );
				}

				while ( rs.next()) {
					for (String field : fields) {
						Double value = values.get(field);
						value += rs.getDouble(field);
						values.put(field, value);
					}
				}
				return values;
			} finally {
				if ( rs != null ){ 
					rs.close();
				}
			}
		}
		
		private ContractPayment createContractPayment() {
			
			ContractPayment payment = new ContractPayment();
			payment.setStartDate(startDate);
			payment.setEndDate(endDate);
			payment.setSalaryType(SalaryType.DELAY);
			payment.setType(PaymentType.SALARY_SUPPLEMENTS);
			
			return payment;
		}
		
		private ContractPayment createContractPayment(double amount, double irpf, double quote ) {
			
			ContractPayment payment = new ContractPayment();
			payment.setStartDate(startDate);
			payment.setEndDate(endDate);
			payment.setSalaryType(SalaryType.DELAY);
			payment.setType(PaymentType.SALARY_SUPPLEMENTS);
			
			payment.setExpression(String.format("%f", amount ));
			payment.setIrpfExpression(String.format("%f", irpf ) );
			payment.setQuoteExpression(String.format("%f", quote ));

			return payment;
		}
		
	}
	
	
	
}
