package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.calculator.CompositeIterator;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryPaymentColumns;
import com.esferalia.aon.salary.AbstractSalaryBuilder;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.payment.IPayment;

public class SQLContractDelayCalculatorContext extends
		SQLContractSalaryCalculatorContext {
	
	
	private static class DelaySQLContractSalaryCalculatorContext extends SQLContractSalaryCalculatorContext{

		public DelaySQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate,
				Date issueDate, Criteria criteria) throws SQLException, ExpressionException {
			super(connection, startDate, endDate, issueDate, criteria);
		}
		
		@Override
		public Object br(Date date) throws ExpressionException, SQLException, SalaryException {
			return super.calculateBr(date);
		}
		
	}
	

	public SQLContractDelayCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate) throws SQLException,
			ExpressionException {
		this(connection, startDate, endDate, issueDate, null);
	}

	public SQLContractDelayCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria)
			throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, criteria,
				getPaymentsCriteria(SalaryType.DELAY));
	}

	public SQLContractDelayCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate,
			Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria,
				getPaymentsCriteria(SalaryType.DELAY));
	}

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.DELAY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {

		Collection<IContractPayment> explicitPayments = super
				.getContractPayments();

		try {
			Collection<IContractPayment> implicitPayments = getDifferencePayments();

			return new CompositeIterator<IContractPayment>(
					explicitPayments.iterator(), implicitPayments.iterator());
		} catch (SQLException e) {
			throw new AonException(e);
		}

	}

	protected String getDescriptionForExtraDelay(IContractPayment payment, int ordinal) {
		return String
				.format("Atrasos en la Paga");
	}

	protected String getDescriptionForSalaryDelay(IContractPayment payment, int ordinal) {
		return String.format(
				"Atrasos en la Nómina");
	}

	private Collection<IContractPayment> getDifferencePayments()
			throws ExpressionException, SQLException, SalaryException {

		Date startDate = getStartDate();
		Date endDate = getEndDate();
		Date chargeDate = getChargeDate();

		Connection connection = getConnection();

		Criteria criteria = new Criteria();

		String identifier = String.format("%s.%s", SQLConstants.CONTRACT,
				ContractColumns.ID);
		criteria.addEqualExpression(identifier, getId());

		final Collection<IContractPayment> payments = new LinkedList<IContractPayment>();

		SalaryDelayPaymentDecorator salaryPaymentDecorator = new SalaryDelayPaymentDecorator() {
			@Override
			public int getOrdinal(IContractPayment payment) {
				return payments.size()+1;
			}
		};
		ExtraDelayPaymentDecorator extraPaymentDecorator = new ExtraDelayPaymentDecorator() {
		@Override
		public  int getOrdinal(IContractPayment payment) {
			return payments.size()+1;
		}
	};

		ContractSalaryCalculator calculator = new ContractSalaryCalculator();
		DelayPaymentBuilder delayPaymentBuilder = new DelayPaymentBuilder(
				getConnection(), salaryPaymentDecorator);

		ExtrasDelayPaymentBuilder extrasDelayPaymentBuilder = new ExtrasDelayPaymentBuilder(
				getConnection(), extraPaymentDecorator);
		
		CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>> compositeBuilder = 
				new CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>>(delayPaymentBuilder, extrasDelayPaymentBuilder);
		
		calculator.setSalaryBuilder(compositeBuilder);

		Collection<Period> periods = split(startDate, endDate);

		for (Period period : periods) {
			ISQLContractSalaryCalculatorContext ctx = new DelaySQLContractSalaryCalculatorContext(
					connection, period.getStart(), period.getEnd(),
					period.getEnd(), criteria) ;
			while (ctx.next()) {
				calculator.calculate(ctx);
				payments.addAll(delayPaymentBuilder.getContractPayments());
				payments.addAll(extrasDelayPaymentBuilder.getContractPayments());
			}

		}

//		ExtraDelayPaymentDecorator extraPaymentDecorator = new ExtraDelayPaymentDecorator() {
//			@Override
//			public  int getOrdinal(IContractPayment payment) {
//				return payments.size()+1;
//			}
//		};
//		ExtraDelayPaymentBuilder extraDelayPaymentBuilder = new ExtraDelayPaymentBuilder(
//				connection, extraPaymentDecorator);
//		calculator.setSalaryBuilder(extraDelayPaymentBuilder);
//
//		Collection<Extra> extras = getExtras(startDate, endDate);
//		for (Extra extra : extras) {
//			ISQLContractSalaryCalculatorContext ctx = new SQLContractExtraCalculatorContext(
//					connection, extra.getStartDate(), extra.getEndDate(),
//					extra.getEndDate(), extra.getChargeDate(), criteria);
//			while (ctx.next()) {
//				calculator.calculate(ctx);
//				payments.addAll(extraDelayPaymentBuilder.getContractPayments());
//			}
//		}

		return payments;

	}

	private Collection<Extra> getExtras(Date startDate, Date endDate)
			throws SQLException {
		Collection<Extra> extras = Collections.emptyList();
		Collection<Extra> agrementExtras = Collections.emptyList();
		
		/*
		AgreementKey enterpriseAgreementKey = getEnterpriseAgreementKey();
		if (enterpriseAgreementKey != null) {
			agrementExtras = getAgreementExtras(agreementKey, startDate, endDate);
			extras = getExtras(agrementExtras, startDate, endDate);
		}*/
		// TODO: Overriden Extras

		AgreementKey agreementKey = getAgreementKey();

		if (agreementKey != null) {
			agrementExtras = getAgreementExtras(agreementKey, startDate, endDate);
			extras = getExtras(agrementExtras, startDate, endDate);
		}

		if (extras.isEmpty()) {
			extras = getPaidExtras(agrementExtras, startDate, endDate);
		}

		return extras;

	}

	private Collection<Extra> getAgreementExtras(AgreementKey agreementKey,
			Date startDate, Date endDate) throws SQLException {
		Collection<Extra> extras = new LinkedList<Extra>();

		ResultSet rs = null;
		PreparedStatement stmt = null;

		Collection<Integer> years = years(startDate, endDate);

		Period period = new Period(startDate, endDate);

		try {
			Connection connection = getConnection();
			//@formatter:off
			stmt = connection.prepareStatement("SELECT *"
					+ " FROM agreement_extra" 
					+ " WHERE agreement= ?"
					+ " AND domain = ? ");
			//@formatter:on
			stmt.setInt(1, agreementKey.getId());
			stmt.setInt(2, agreementKey.getDomain());
			rs = stmt.executeQuery();
			while (rs.next()) {
				String extraIssue = rs
						.getString(AgreementExtraColumns.ISSUE_DATE);
				String extraStart = rs
						.getString(AgreementExtraColumns.START_DATE);
				String extraEnd = rs.getString(AgreementExtraColumns.END_DATE);

				for (Integer year : years) {
					Date extraIssueDate = AgreementExtra.parseAgreementDate(
							extraIssue, year);
					Date extraStartDate = AgreementExtra.parseAgreementDate(
							extraStart, year);
					Date extraEndDate = AgreementExtra.parseAgreementDate(
							extraEnd, year);

					Extra extra = new Extra(extraStartDate, extraEndDate,
							extraIssueDate);
					extras.add(extra);
				}
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

		return extras;
	}

	private Collection<Extra> getExtras(Collection<Extra> extras,
			Date startDate, Date endDate) throws SQLException {

		Collection<Extra> delayedExtras = new LinkedList<Extra>();

		Period period = new Period(startDate, endDate);

		for (Extra extra : extras) {
			if (period.contains(extra.chargeDate)) {
				delayedExtras.add(extra);
			}
		}

		return delayedExtras;
	}

	private Collection<Extra> getPaidExtras(Collection<Extra> agreementExtras,
			Date startDate, Date endDate) throws SQLException {
		Collection<Extra> extras = new LinkedList<Extra>();

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			Connection connection = getConnection();
			stmt = connection.prepareStatement("SELECT *" + " FROM salary"
					+ " WHERE contract = ?" + " AND type = ? "
					+ " AND charge_date >= ? " + " AND charge_date <= ? ");

			stmt.setInt(1, getId());
			stmt.setInt(2, SalaryType.EXTRA.ordinal());
			java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
			stmt.setDate(3, sqlStartDate);
			java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
			stmt.setDate(4, sqlEndDate);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Date extraStartDate = rs.getDate(SalaryColumns.START_DATE);
				Date extraEndDate = rs.getDate(SalaryColumns.END_DATE);
				Date chargeDate = rs.getDate(SalaryColumns.CHARGE_DATE);
				Extra extra = new Extra(extraStartDate, extraEndDate,
						chargeDate);
				Extra agreementExtra = getAgreementExtra(agreementExtras, extra);
				if (agreementExtra != null) {
					extra.startDate = agreementExtra.startDate;
					extra.endDate = agreementExtra.endDate;
				}

				extras.add(extra);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

		return extras;

	}

	private abstract class SalaryDelayPaymentDecorator implements
			IDelayPaymentDecorator {
		@Override
		public String getDescriptionFor(IContractPayment payment) {
			return getDescriptionForSalaryDelay(payment, getOrdinal(payment));
		}


	}

	private abstract class ExtraDelayPaymentDecorator implements
			IDelayPaymentDecorator {
		@Override
		public String getDescriptionFor(IContractPayment payment) {
			return getDescriptionForExtraDelay(payment, getOrdinal(payment));
		}
	}

	private static Collection<Integer> years(Date startDate, Date endDate) {
		int start = CommonUtil.getYear(startDate);
		int end = CommonUtil.getYear(startDate);
		Collection<Integer> years = new LinkedList<Integer>();
		for (int year = start; year <= end; year++) {
			years.add(year);
		}
		return years;
	}

	private static Collection<Period> split(Date startDate, Date endDate) {

		Collection<Period> periods = new LinkedList<Period>();

		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		start.set(Calendar.DAY_OF_MONTH, 1);

		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		end.set(Calendar.DAY_OF_MONTH, 1);

		while (start.compareTo(end) <= 0) {
			Date monthStart = start.getTime();
			Date monthEnd = CommonUtil.getMonthLastDay(monthStart);
			periods.add(new Period(monthStart, monthEnd));

			start.add(Calendar.MONTH, 1);
		}

		return periods;

	}

	public interface IDelayPaymentDecorator {
		int getOrdinal(IContractPayment payment);
		String getDescriptionFor(IContractPayment payment);

	}

	private static class DelayPaymentBuilder<T extends ISalary> extends AbstractSalaryBuilder<T> {

		private static final String SALARY_SQL = "SELECT *" + " FROM "
				+ SQLConstants.SALARY + " WHERE " + SalaryColumns.CONTRACT
				+ " = ? " + " AND " + SalaryColumns.TYPE + "  = ? " + " AND "
				+ SalaryColumns.START_DATE + "  = ? " + " AND "
				+ SalaryColumns.END_DATE + " = ? ";

		private SalaryType type;
		private Date startDate;
		private Date endDate;
		private Integer contract;

		private PreparedStatement stmt;

		private Map<String, Double> values;

		protected IDelayPaymentDecorator paymentDecorator;

		public DelayPaymentBuilder(Connection connection,
				IDelayPaymentDecorator paymentDecorator) throws SQLException {
			this.paymentDecorator = paymentDecorator;
			this.values = new HashMap<String, Double>();
			this.stmt = initStatement(connection);
		}

		@Override
		public void setType(SalaryType type) {
			this.type = type;
		}

		@Override
		public void setContract(Object contract) {
			this.contract = ((SQLSalaryProxy) contract).getContractId();
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

		public Collection<IContractPayment> getContractPayments()
				throws SQLException {

			Set<String> fields = values.keySet();

			Map<String, Double> paidValues = getPaidSalary(fields);

			Map<String, Double> diffValues = new HashMap<String, Double>();

			for (String field : fields) {
				Double value = values.get(field);
				value = value == null ? 0.00 : value;
				Double paidValue = paidValues.get(field);
				paidValue = paidValue == null ? 0.00 : paidValue;
				Double diffValue = value - paidValue;
				diffValues.put(field, diffValue);
				/*
				 * System.out.println(String.format("%s : %f - %f = %f.", field,
				 * value, paidValue, value -paidValue) );
				 */
			}
			
			

			List<IContractPayment> payments = new LinkedList<IContractPayment>();

			ContractPayment payment = createContractPayment(
					diffValues.get(SalaryColumns.TOTAL_PAYMENT),
					diffValues.get(SalaryColumns.IRPF_BASE),
					diffValues.get(SalaryColumns.CGC_BASE));
			payments.add(payment);

			return payments;
		}
		
		protected Double getValue (String key) {
			return values.getOrDefault(key, 0.00);
		}

		protected Double setValue (String key, Double value) {
			return values.put(key, value);
		}
		
		protected Double addValue (String key, Double value) {
			return values.put(key, values.getOrDefault(key, 0.00) + value );
		}

		
		private Map<String, Double> getPaidSalary(Set<String> fields)
				throws SQLException {
			ResultSet rs = null;
			try {

				rs = initResultSet(stmt, contract, type, startDate, endDate);

				Map<String, Double> values = new HashMap<String, Double>();

				for (String field : fields) {
					values.put(field, 0.00);
				}

				while (rs.next()) {
					
					for (String field : fields) {
						Double value = values.get(field);
						value += rs.getDouble(field);
						values.put(field, value);
					}
				}
				return values;
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
		}

		protected ContractPayment createContractPayment(double amount,
				double irpf, double quote) {

			ContractPayment payment = new ContractPayment();
			payment.setStartDate(startDate);
			payment.setEndDate(endDate);
			payment.setSalaryType(SalaryType.DELAY);


			PaymentConcept paymentConcept = new PaymentConcept();
			int ordinal = paymentDecorator.getOrdinal(payment);
			paymentConcept.setCode("__" + RN.roman(ordinal));
			
			// TODO: Generic Delays ? 
			paymentConcept.setType(PaymentType.CRA_0008 );
			// we use Locale.US to avoid ',' instead of '.' like decimals
			// separator.
			// Be care that MVEL like any other expression language don't
			// understand ','.
			paymentConcept.setExpression(String.format(Locale.US, "%.3f",
					amount));
			paymentConcept.setIrpfExpression(String.format(Locale.US, "%.3f",
					irpf));
			paymentConcept.setQuoteExpression(String.format(Locale.US, "%.3f",
					quote));
			paymentConcept.setDescription(paymentDecorator
					.getDescriptionFor(payment));

			payment.setPaymentConcept(paymentConcept);

			payment.setType(paymentConcept.getType());
			payment.setExpression(paymentConcept.getExpression());
			payment.setIrpfExpression(paymentConcept.getIrpfExpression());
			payment.setQuoteExpression(paymentConcept.getQuoteExpression());
			payment.setDescription(paymentConcept.getDescription());

			return payment;
		}

		protected PreparedStatement initStatement(Connection connection)
				throws SQLException {
			return connection.prepareStatement(SALARY_SQL);
		}

		protected ResultSet initResultSet(PreparedStatement stmt,
				Integer contract, SalaryType type, Date startDate, Date endDate)
				throws SQLException {
			ResultSet rs = null;
			stmt.setInt(1, contract); // SalaryColumns.CONTRACT + " = ? "
			stmt.setInt(2, type.ordinal()); // SalaryColumns.TYPE + " = ? "
			java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
			stmt.setDate(3, sqlStartDate); // SalaryColumns.START_DATE +
											// "  = ? "
			java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
			stmt.setDate(4, sqlEndDate); // SalaryColumns.END_DATE + "  = ? "

			return stmt.executeQuery();
		}
	}

	private static class ExtrasDelayPaymentBuilder extends DelayPaymentBuilder {

		private static final String EXTRA_PAYMENTS_SQL = 
				"SELECT " 
				+ " 0.00 AS " + SalaryColumns.CGC_BASE
				+ ", SUM(" + SQLConstants.SALARY_PAYMENT + "." + SalaryPaymentColumns.QUOTE + ") AS " + SalaryColumns.IRPF_BASE
				+ ", SUM(" + SQLConstants.SALARY_PAYMENT + "." + SalaryPaymentColumns.QUOTE + ") AS " + SalaryColumns.TOTAL_PAYMENT 

				+ " FROM " 	+ SQLConstants.SALARY
				+ " INNER JOIN " + SQLConstants.SALARY_PAYMENT 
				+ " ON (" + SQLConstants.SALARY + "." + SalaryColumns.ID 
				+ " = " + SQLConstants.SALARY_PAYMENT + "." + SalaryPaymentColumns.SALARY + ")"   
				
				+ " WHERE " + SQLConstants.SALARY + "." + SalaryColumns.CONTRACT + " = ? " 
				+ " AND " 	+ SQLConstants.SALARY + "." + SalaryColumns.TYPE + "  = ? " 
				+ " AND " 	+ SQLConstants.SALARY + "." + SalaryColumns.START_DATE + "  = ? " 
				+ " AND " 	+ SQLConstants.SALARY + "." + SalaryColumns.END_DATE + " = ? " 
				+ " AND " 	+ SQLConstants.SALARY_PAYMENT + "." + SalaryPaymentColumns.TYPE + " = ? "
				+ " AND " 	+ SQLConstants.SALARY_PAYMENT + "." + SalaryPaymentColumns.AMOUNT + " = 0.00 "
				+ " AND " 	+ SQLConstants.SALARY_PAYMENT + "." + SalaryPaymentColumns.IRPF + " = 0.00 "
				;

		public ExtrasDelayPaymentBuilder(Connection connection,
				IDelayPaymentDecorator paymentDecorator) throws SQLException {
			super(connection, paymentDecorator);
			super.setValue(SalaryColumns.CGC_BASE, 0.00);
			super.setValue(SalaryColumns.IRPF_BASE, 0.00);
			super.setValue(SalaryColumns.TOTAL_PAYMENT, 0.00);
		}
		
		// ------------------------------------------------------------- public
		
		@Override
		public void setCgcBase(Double cgcBase) {
		}
		
		@Override
		public void setIrpfBase(Double irpfBase) {
		}
		
		@Override
		public void setTotalPayment(Double totalPayment) {
		}
		
		
		@Override
		public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
				Map context) {
			if ( payment.getType() != PaymentType.CRA_0004 )
				return;
			
			super.addValue(SalaryColumns.IRPF_BASE, quote);
			super.addValue(SalaryColumns.TOTAL_PAYMENT, quote);
		}
		
		// ---------------------------------------------------------- protected
		
		@Override
		protected ContractPayment createContractPayment(double amount, double irpf, double quote) {
			ContractPayment contractPayment =  super.createContractPayment(amount, irpf, quote);
			contractPayment.setId(Integer.MIN_VALUE);
			return contractPayment;
		}
		
		@Override
		public Collection getContractPayments() throws SQLException {
			Collection contractPayments = super.getContractPayments();
			super.setValue(SalaryColumns.IRPF_BASE, 0.00);
			super.setValue(SalaryColumns.TOTAL_PAYMENT, 0.00);
			return contractPayments;
		}
		
		@Override
		protected PreparedStatement initStatement(Connection connection)
				throws SQLException {
			return connection.prepareStatement(EXTRA_PAYMENTS_SQL);
		}
		
		@Override
		protected ResultSet initResultSet(PreparedStatement stmt,
				Integer contract, SalaryType type, Date startDate, Date endDate)
				throws SQLException {
			
			stmt.setInt(1, contract); 			// SalaryColumns.CONTRACT + " = ? "
			
			stmt.setInt(2, type.ordinal()); 	// SalaryColumns.TYPE + " = ? "

			java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
			stmt.setDate(3, sqlStartDate); 		// SalaryColumns.START_DATE +
			
			java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
			stmt.setDate(4, sqlEndDate); 		// SalaryColumns.END_DATE + "  = ? "
			
			stmt.setInt(5, PaymentType.CRA_0004.ordinal()  ); 

			return stmt.executeQuery();
		}

	}

	private static class ExtraDelayPaymentBuilder extends DelayPaymentBuilder {

		private static final String EXTRA_SQL = "SELECT *" + " FROM "
				+ SQLConstants.SALARY + " WHERE " + SalaryColumns.CONTRACT
				+ " = ? " + " AND " + SalaryColumns.TYPE + "  = ? " + " AND "
				+ SalaryColumns.START_DATE + "  = ? " + " AND "
				+ SalaryColumns.END_DATE + " = ? " + " AND "
				+ SalaryColumns.CHARGE_DATE + " = ? ";

		private Date chargeDate;

		public ExtraDelayPaymentBuilder(Connection connection,
				IDelayPaymentDecorator paymentDecorator) throws SQLException {
			super(connection, paymentDecorator);
		}

		@Override
		public void setChargeDate(Date chargeDate) {
			this.chargeDate = chargeDate;
		}

		protected PreparedStatement initStatement(Connection connection)
				throws SQLException {
			return connection.prepareStatement(EXTRA_SQL);
		}

		protected ResultSet initResultSet(PreparedStatement stmt,
				Integer contract, SalaryType type, Date startDate, Date endDate)
				throws SQLException {
			ResultSet rs = null;
			stmt.setInt(1, contract); // SalaryColumns.CONTRACT + " = ? "
			stmt.setInt(2, type.ordinal()); // SalaryColumns.TYPE + " = ? "
			java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
			stmt.setDate(3, sqlStartDate); // SalaryColumns.START_DATE +
											// "  = ? "
			java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
			stmt.setDate(4, sqlEndDate); // SalaryColumns.END_DATE + "  = ? "

			java.sql.Date sqlChargeDate = new java.sql.Date(
					chargeDate.getTime());
			stmt.setDate(5, sqlChargeDate); // SalaryColumns.CHARGE_DATE +
											// "  = ? "

			return stmt.executeQuery();
		}

		@Override
		protected ContractPayment createContractPayment(double amount,
				double irpf, double quote) {
			ContractPayment payment = new ContractPayment();
			payment.setStartDate(chargeDate);
			payment.setEndDate(chargeDate);
			payment.setSalaryType(SalaryType.DELAY);
			// TODO: Generic Delays ? 
			payment.setType(PaymentType.CRA_0008);

			payment.setExpression(String.format(Locale.US, "%.3f", amount));
			payment.setIrpfExpression(String.format(Locale.US, "%.3f", irpf));
			payment.setQuoteExpression(String.format(Locale.US, "%.3f", quote));

			payment.setDescription(paymentDecorator.getDescriptionFor(payment));

			return payment;
		}
	}

	private static class Extra {
		private Date startDate;
		private Date endDate;
		private Date chargeDate;

		public Extra(Date startDate, Date endDate, Date chargeDate) {
			this.startDate = startDate;
			this.endDate = endDate;
			this.chargeDate = chargeDate;
		}

		public Date getStartDate() {
			return startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public Date getChargeDate() {
			return chargeDate;
		}

	}

	private static class RN {

	    enum Numeral {
	        I(1), IV(4), V(5), IX(9), X(10), XL(40), L(50), XC(90), C(100), CD(400), D(500), CM(900), M(1000);
	        int weigth;

	        Numeral(int weigth) {
	            this.weigth = weigth;
	        }
	    };

	    public static String roman(long n) {

	        if( n <= 0) {
	            throw new IllegalArgumentException();
	        }

	        StringBuilder buf = new StringBuilder();

	        final Numeral[] values = Numeral.values();
	        for (int i = values.length - 1; i >= 0; i--) {
	            while (n >= values[i].weigth) {
	                buf.append(values[i]);
	                n -= values[i].weigth;
	            }
	        }
	        return buf.toString();
	    }

	}
	private static Extra getAgreementExtra(Collection<Extra> agreementExtras,
			Extra extra) {

		List<Extra> candidates = new LinkedList<Extra>();
		for (Extra agreementExtra : agreementExtras) {
			if (agreementExtra.endDate.equals(extra.endDate)
					|| agreementExtra.startDate.equals(extra.startDate)
					|| agreementExtra.chargeDate.equals(extra.chargeDate)) {
				candidates.add(agreementExtra);
			}
		}
		return candidates.size() == 1 ? candidates.get(0) : null;
	}

}
