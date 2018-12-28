package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT;
import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT_LEAVE;
import static com.esferalia.aon.payroll.sql.SQLConstants.SALARY;
import static com.esferalia.aon.payroll.sql.SQLConstants.SALARY_DATA;
import static com.esferalia.aon.payroll.sql.SQLConstants.SALARY_PAYMENT;
import static java.util.Calendar.DAY_OF_MONTH;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mvel2.util.MethodStub;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.calculator.CompositeIterator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.UndefinedContextVariablesException;
import com.esferalia.aon.payroll.calculator.Variable;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractLeaveColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryPaymentColumns;
import com.esferalia.aon.salary.AbstractSalaryBuilder;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.server.AonDateUtils;

public class SQLContractDelayCalculatorContext extends
		SQLContractSalaryCalculatorContext {
	
	
	private static class DelaySQLContractSalaryCalculatorContext extends SQLContractSalaryCalculatorContext{
		
		private long prevDays = 0;

		public DelaySQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate,
				Date issueDate, Criteria criteria, long prevDays ) throws SQLException, ExpressionException {
			super(connection, startDate, endDate, issueDate, criteria);
			this.prevDays = prevDays;
		}
		
		@Override
		public Object br(Date date) throws ExpressionException, SQLException, SalaryException {
			Date contractStart = super.getDate(CONTRACT, ContractColumns.START_DATE);
			if ( contractStart.before(getFirstDayOfMonth(date)))
				date = AonDateUtils.add(date, Calendar.MONTH, -1);
			
			Object br =  super.calculateBr(date);
			return br;
		}
		
		@Override
		protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(Connection conn, Date startDate,
				Date endDate, Date issueDate, Criteria criteria, int start, int end) {
			ISQLContractSalaryCalculatorContext ctx =  super.getNoItCalculatorContext(conn, startDate, endDate, issueDate, criteria, start, end);
			if ( AonDateUtils.getDay(startDate) == 1 )
				return ctx;
			ctx.getExpressionContext().setVariable(ContextVariable.ACTIVE_DAYS.getName(), prevDays, startDate, endDate);
			return ctx;
		}
		
		
		@Override
		public boolean next() throws SQLException, ExpressionException {
			return super.next(ctx -> load(ctx, getStart(), getEnd()) );
		}
		
		@Variable(ContextVariable.ON_ACCOUNT_AGREEMENT)
		public static Object onAccountAgreement(Object obj) {
			return 0.00;
		}
		
		public static void load(ExpressionContext context, Date startDate, Date endDate){
			for (Method method : DelaySQLContractSalaryCalculatorContext.class.getDeclaredMethods()) {
				Variable variable = method.getAnnotation(Variable.class);
				if ( variable != null ) {
					ContextVariable contextVariable = variable.value();
					MethodStub methodStub = new MethodStub(method);
					context.setVariable(contextVariable, methodStub, startDate, endDate);
				}
			}
		}
		
	}
	
	private static class SmartContractDelayCalculator<T extends ISalary>  extends SmartContractSalaryCalculator<T> {

		private Collection<Period> its;
		private Map<Integer, Integer> itDays;
		private Map<Integer, Integer> monthDays;
		
		public SmartContractDelayCalculator(Map<Integer, Integer> monthDays, Map<Integer, Integer> itDays, Collection<Period> its) {
			super();
			this.its = its;
			this.itDays = itDays;
			this.monthDays = monthDays;
		}

		@Override
		protected List<ITimedResult<Double>> fixConstantResult(IContractPayment contractPayment,
				ITimedResult<Double> result, Date start, Date end, ExpressionContext expressionContext)
				throws UnsupportedOperationException, UndefinedVariablesException {
			
			if ( isITPayment(contractPayment))
				return Collections.singletonList( isInIT(result.getPeriod()) ? fixItResult(result) : new TimedResult<Double>(0.00, result.getPeriod(), result.getContext()));
			
			
			int month = AonDateUtils.getMonth(result.getPeriod().getStart());
			
			//expressionContext.getVariable(MONTH_DAYS, start, end, Number.class).doubleValue();
			
			double activeDays ;
			try {
				activeDays = getMonthDays(expressionContext, start, end); 
			} catch ( ExpressionException e ) {
				activeDays = monthDays.get(month);
			}
			double resultDays = AonDateUtils.getDay(result.getPeriod().getEnd()) 
					- AonDateUtils.getDay(result.getPeriod().getStart()) + 1;
			
			if ( resultDays == (double) monthDays.get(month) )
				resultDays = activeDays;

			if ( !result.getContext().isEmpty() )
				activeDays  -= itDays.get(month);

			
			double value = result.getValue() / activeDays * resultDays;
			
			return Collections.singletonList(new TimedResult<Double>(value, result.getPeriod(), result.getContext()));
		}
		
		@Override
		protected List<ITimedResult<Double>> fixItResults(IContractPayment contractPayment,
				List<ITimedResult<Double>> results, List<Period> its, Date start, Date end,
				ExpressionContext expressionContext) throws UnsupportedOperationException, UndefinedVariablesException {
			
			if ( !isITPayment(contractPayment))  
				return results.stream().map( r-> new TimedResult<Double>(0.00, r.getPeriod(), r.getContext()) ).collect(Collectors.toList());

			if (results.size() > 1 || !results.get(0).getContext().isEmpty() )
				super.fixItResults(contractPayment, results, its, start, end, expressionContext);
				
			
			List<ITimedResult<Double>>  fixedResults = new LinkedList<ITimedResult<Double>>(); 
			for ( ITimedResult<Double> result: results ) {
				fixedResults.add(fixItResult(result));
			}

			return fixedResults; // super.fixItResults(contractPayment, results, its, start, end, expressionContext);
		}
		
		private ITimedResult<Double> fixItResult(ITimedResult<Double> result) {
			int resultDays = AonDateUtils.getDay(result.getPeriod().getEnd()) 
					- AonDateUtils.getDay(result.getPeriod().getStart()) + 1;
			int month = AonDateUtils.getMonth(result.getPeriod().getStart());
			double value = result.getValue() / itDays.get(month) * resultDays;
			return new TimedResult<Double>(value, result.getPeriod(), result.getContext());
			
		}
		
		private boolean isInIT(Period p) {
			return Period.intersects(its.iterator(), Collections.singletonList(p).iterator());
		}

		private static boolean isITPayment(IContractPayment contractPayment) {
			PaymentType type = contractPayment.getType();
			if ( PaymentType.CRA_0055 == type )
				return true;
			if ( PaymentType.CRA_0054 == type )
				return true;
			String name = contractPayment.getName();
			if ( ContextVariable.PREST_IT.equals(name))
				return true;
			if ( ContextVariable.GUARENTEED.equals(name))
				return true;
			return false;
		}

		private static boolean isConstant(List<ITimedResult<?>> results) {
			return results.size() == 1 && results.get(0).getContext().isEmpty();
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
	
	protected <T extends ISalary> ISalaryBuilder<T> getSalaryBuilder(ISalaryBuilder<T> salaryBuilder)
	{
		return salaryBuilder;
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

		
		DelayPaymentBuilder delayPaymentBuilder = new DelayPaymentBuilder(
				getConnection(), salaryPaymentDecorator);

		ExtrasDelayPaymentBuilder extrasDelayPaymentBuilder = new ExtrasDelayPaymentBuilder(
				getConnection(), extraPaymentDecorator);
		
		ISalaryBuilder<ISalary> compositeBuilder = getSalaryBuilder(
				new CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>>(delayPaymentBuilder, extrasDelayPaymentBuilder));
		

		Collection<Period> periods = getCgcPeriods(connection, getId(), startDate, endDate);//split(startDate, endDate);
		Collection<Period> itPeriods = getItPeriods(connection, getId(), startDate, endDate);
		Map<Integer, Integer> itDays = getItDays(connection, itPeriods, startDate, endDate);
		Map<Integer, Integer> monthDays = getMonthDays(startDate, endDate);

		//	ContractSalaryCalculator calculator = new ContractSalaryCalculator();
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractDelayCalculator<ISalary>(monthDays, itDays, itPeriods);
		calculator.setSalaryBuilder(compositeBuilder);
		
		long prevDays = 0;
		
		for (Period period : periods) {
			
			if  ( AonDateUtils.getDay(period.getStart()) == 1)
				prevDays = 0;
			
			
			ISQLContractSalaryCalculatorContext ctx = new DelaySQLContractSalaryCalculatorContext(
					connection, period.getStart(), period.getEnd(),
					period.getEnd(), criteria, prevDays ) ;
			while (ctx.next()) {
				calculator.calculate(ctx);
				payments.addAll(delayPaymentBuilder.getContractPayments());
				payments.addAll(extrasDelayPaymentBuilder.getContractPayments());
			}
			
			prevDays += period.daysStream().count();

		}


		return payments;

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

	private static Collection<Period> getCgcPeriods(Connection connection, Integer contract, Date startDate, Date endDate) 
	throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null ;

		Collection<Period> cgcPeriods = new LinkedList<Period>();
		try {
			stmt = connection.prepareStatement(
				"SELECT" 
				+" " + SALARY_DATA + "." + SalaryDataColumns.START_DATE 
				+"," + SALARY_DATA + "." + SalaryDataColumns.END_DATE 
				+" FROM " + SALARY
				+" INNER JOIN " + SALARY_DATA + " ON (" + SALARY + "." + SalaryColumns.ID + " = " + SALARY_DATA + "." + SalaryDataColumns.SALARY + ")" 
				+" WHERE " + SALARY + "." + SalaryColumns.CONTRACT + " = ? "
				+" AND " + SALARY + "." + SalaryColumns.TYPE + " = 0 " 
				+" AND " + SALARY + "." + SalaryColumns.START_DATE + " >= ? " 
				+" AND " + SALARY + "." + SalaryColumns.END_DATE + " <= ? "
				+" AND " + SALARY_DATA + "." + SalaryDataColumns.NAME + " = '" + CGC_BASE.getName() + "'"
				+" GROUP BY 1, 2"
				); 
			stmt.setInt(1, contract);
			stmt.setDate(2, toSqlDate(startDate));
			stmt.setDate(3, toSqlDate(endDate));
			rs = stmt.executeQuery();
			
			while ( rs.next() ) {
				java.sql.Date start = rs.getDate(SALARY_DATA + "." + SalaryDataColumns.START_DATE);
				java.sql.Date end = rs.getDate(SALARY_DATA + "." + SalaryDataColumns.END_DATE);
				cgcPeriods.add(new Period(start,end));
			}
			
		}catch ( SQLException e ) {
			//e.printStackTrace();
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
		
		return cgcPeriods;

	}

	private static Collection<Period> getItPeriods(Connection connection, Integer contract, Date startDate, Date endDate) 
	throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null ;

		Collection<Period> itPeriods = new LinkedList<Period>();
		try {
			stmt = connection.prepareStatement(
				"SELECT" 
				+" " + CONTRACT_LEAVE + "." + ContractLeaveColumns.START_DATE 
				+"," + CONTRACT_LEAVE + "." + ContractLeaveColumns.END_DATE 
				+" FROM " + CONTRACT_LEAVE
				+" WHERE " + CONTRACT_LEAVE + "." + ContractLeaveColumns.CONTRACT + " = ? "
				+" AND " + CONTRACT_LEAVE + "." + ContractLeaveColumns.START_DATE + " <= ? " 
				+" AND (" + CONTRACT_LEAVE + "." + ContractLeaveColumns.END_DATE + " >= ? "
				+"  OR " + CONTRACT_LEAVE + "." + ContractLeaveColumns.END_DATE + " IS NULL )"
				+" GROUP BY 1, 2"
				); 
			stmt.setInt(1, contract);
			stmt.setDate(2, toSqlDate(endDate));
			stmt.setDate(3, toSqlDate(startDate));
			rs = stmt.executeQuery();
			
			while ( rs.next() ) {
				java.sql.Date start = rs.getDate(CONTRACT_LEAVE + "." + ContractLeaveColumns.START_DATE);
				java.sql.Date end = rs.getDate(CONTRACT_LEAVE + "." + ContractLeaveColumns.END_DATE);
				itPeriods.add(new Period(start,end));
			}
			
		}catch ( SQLException e ) {
			//e.printStackTrace();
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
		
		return itPeriods;

	}
	
	private static Collection<Period> getMonthPeriods(Date startDate, Date endDate){
		Collection<Period> monthPeriods = new LinkedList<Period>();
		
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		
		while ( startCalendar.compareTo(endCalendar) <= 0 ) {
			Date start = startCalendar.getTime();
			startCalendar.set(DAY_OF_MONTH, startCalendar.getActualMaximum(DAY_OF_MONTH));
			Date end = startCalendar.compareTo(endCalendar) < 0 ? startCalendar.getTime(): endCalendar.getTime();
			
			monthPeriods.add(new Period(start,end));
			
			startCalendar.add(DAY_OF_MONTH, 1);
		}
		
		return monthPeriods;
	}

	private static Map<Integer,Integer> getMonthDays(Date startDate, Date endDate){
		Map<Integer,Integer> monthDays = new HashMap<Integer,Integer>();
		
		Collection<Period> monthPeriods = getMonthPeriods(startDate, endDate);
		for ( Period p: monthPeriods) { 
			int days = AonDateUtils.getDay(p.getEnd()) - AonDateUtils.getDay(p.getStart()) +1;
			monthDays.put(AonDateUtils.getMonth(p.getStart()), days);
		}
		
		
		return monthDays;
	}

	private static Map<Integer,Integer> getItDays(Connection connection, Collection<Period> itPeriods , Date startDate, Date endDate) throws SQLException{
		Map<Integer,Integer> itDays = new HashMap<Integer,Integer>();
		
		Collection<Period> monthPeriods = getMonthPeriods(startDate, endDate);
		for ( Period p: monthPeriods) 
			itDays.put(AonDateUtils.getMonth(p.getStart()), 0);
		
		
		List<Period> itPeriodsByMonth = Period.intersect(itPeriods, monthPeriods);
		for ( Period p: itPeriodsByMonth ) {
			int month = AonDateUtils.getMonth(p.getStart());
			int days = itDays.get(month) + AonDateUtils.getDay(p.getEnd()) - AonDateUtils.getDay(p.getStart()) +1;
			itDays.put(month , days );
		}

		return itDays;
	}
	
	public interface IDelayPaymentDecorator {
		int getOrdinal(IContractPayment payment);
		String getDescriptionFor(IContractPayment payment);

	}

	private static class DelayPaymentBuilder<T extends ISalary> extends AbstractSalaryBuilder<T> {

		private static final String PREST_IT_IRPF_SQL = 
				"IFNULL((SELECT"
				+ " SUM(" + SalaryPaymentColumns.IRPF+")"
				+ " FROM " + SALARY_PAYMENT 
				+ " WHERE " + SalaryPaymentColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryPaymentColumns.PAYMENT_CONCEPT + " = 'PREST_IT')"
				+", 0.00)";
				;

		private static final String GARANTIZADO_IRPF_SQL = 
				"IFNULL((SELECT"
				+ " SUM(" + SalaryPaymentColumns.IRPF+")"
				+ " FROM " + SALARY_PAYMENT 
				+ " WHERE " + SalaryPaymentColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryPaymentColumns.PAYMENT_CONCEPT + " = 'GARANTIZADO')"
				+", 0.00)";
				;

		private static final String PREST_IT_AMOUNT_SQL = 
				"IFNULL((SELECT"
				+ " SUM(" + SalaryPaymentColumns.AMOUNT+")"
				+ " FROM " + SALARY_PAYMENT 
				+ " WHERE " + SalaryPaymentColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryPaymentColumns.PAYMENT_CONCEPT + " = 'PREST_IT')"
				+", 0.00)";
				;

		private static final String GARANTIZADO_AMOUNT_SQL = 
				"IFNULL((SELECT"
				+ " SUM(" + SalaryPaymentColumns.AMOUNT+")"
				+ " FROM " + SALARY_PAYMENT 
				+ " WHERE " + SalaryPaymentColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryPaymentColumns.PAYMENT_CONCEPT + " = 'GARANTIZADO')"
				+", 0.00)";
				;

		private static final String IT_DAYS = 
				"(SELECT"
				+ " SUM(" + SalaryDataColumns.EXPRESSION + ")"
				+ " FROM " + SALARY_DATA 
				+ " WHERE " + SalaryDataColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND (" 
				+ SalaryDataColumns.NAME + " LIKE 'DIAS_ENFERMEDAD%'" 
				+ " OR " + SalaryDataColumns.NAME + " IN ('DIAS_MATERNIDAD', 'DIAS_PATERNIDAD')"
				+ ")"
				+ " AND " + SalaryDataColumns.START_DATE + " = ? " 
				+ " AND " + SalaryDataColumns.END_DATE + " =  ? " 
				+ ")"
				;

		private static final String WORKED_DAYS = 
				"IFNULL((SELECT"
				+ " SUM(" + SalaryDataColumns.EXPRESSION + ")"
				+ " FROM " + SALARY_DATA 
				+ " WHERE " + SalaryDataColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryDataColumns.NAME + " = 'DIAS_TRABAJADOS'"
				+ " AND " + SalaryDataColumns.START_DATE + " = ? " 
				+ " AND " + SalaryDataColumns.END_DATE + " =  ? " 
				+ "),0.00)"
				;
		
		private static final String ALL_IT_DAYS = 
				"(SELECT"
				+ " SUM(" + SalaryDataColumns.EXPRESSION + ")"
				+ " FROM " + SALARY_DATA 
				+ " WHERE " + SalaryDataColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND (" 
				+ SalaryDataColumns.NAME + " LIKE 'DIAS_ENFERMEDAD%'" 
				+ " OR " + SalaryDataColumns.NAME + " IN ('DIAS_MATERNIDAD', 'DIAS_PATERNIDAD')"
				+ ")"
				+ ")"
				;

		private static final String ALL_WORKED_DAYS = 
				"(SELECT"
				+ " SUM(" + SalaryDataColumns.EXPRESSION + ")"
				+ " FROM " + SALARY_DATA 
				+ " WHERE " + SalaryDataColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryDataColumns.NAME + " = 'DIAS_TRABAJADOS'"
				+ ")"
				;

		private static final String MATERNITY_BASE = 
				"IFNULL((SELECT"
				+ " SUM(" + SalaryDataColumns.EXPRESSION + ")"
				+ " FROM " + SALARY_DATA 
				+ " WHERE " + SalaryDataColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryDataColumns.NAME + " = 'BASE_MTNAD'"
				+ " AND " + SalaryDataColumns.START_DATE + " = ? " 
				+ " AND " + SalaryDataColumns.END_DATE + " =  ? " 
				+ "),0.00)"
				;

		private static final String SALARY_SQL = 
				"SELECT " 
				
				+ SALARY_PAYMENT +"." + SalaryPaymentColumns.ID
				
				+ ", @GTZDO:=" + GARANTIZADO_AMOUNT_SQL +  ""
				+ " AS GTZDO" 

				+ ", @GTZDOIT:=IFNULL((@GTZDO / " + ALL_IT_DAYS + " * " + IT_DAYS+"),0.00)"
				+ " AS GTZDOIT" 

				+ ", " + SALARY_DATA + "."+ SalaryDataColumns.EXPRESSION 
				+ " + " + MATERNITY_BASE
				+ " AS " + SalaryColumns.CGC_BASE
				
				+ ", (IFNULL( "+ SALARY_PAYMENT +"." + SalaryPaymentColumns.IRPF  + " + @GTZDOIT"
				+ ", (" + SalaryColumns.IRPF_BASE + "- (" + PREST_IT_IRPF_SQL + " + @GTZDO )) / " + ALL_WORKED_DAYS + " * " + WORKED_DAYS + ")"
				+ ")"
				+ " AS " + SalaryColumns.IRPF_BASE

				+ ", (IFNULL( " + SALARY_PAYMENT +"." + SalaryPaymentColumns.AMOUNT + " + @GTZDOIT"
				+ ", (" + SalaryColumns.TOTAL_PAYMENT + "- (" + PREST_IT_AMOUNT_SQL + " + @GTZDO )) / " + ALL_WORKED_DAYS + " * " + WORKED_DAYS +")"
				+ ")"
				+ " AS " + SalaryColumns.TOTAL_PAYMENT
				
				+ " FROM "
				+ SALARY 
				+" INNER JOIN " + SALARY_DATA + " ON (" + SALARY + "." + SalaryColumns.ID + " = " + SALARY_DATA + "." + SalaryDataColumns.SALARY + ")" 
				+" LEFT JOIN " + SALARY_PAYMENT + " ON (" + SALARY_DATA + "." + SalaryDataColumns.SALARY +  " = " + SALARY_PAYMENT + "." + SalaryPaymentColumns.SALARY 
														+ " AND  "+ SALARY_PAYMENT + "." +SalaryPaymentColumns.PAYMENT_CONCEPT + " =  'PREST_IT'"  
														+ " AND  "+ SALARY_PAYMENT + "." +SalaryPaymentColumns.QUOTE + " =  CONVERT(" + SALARY_DATA + "."+ SalaryDataColumns.EXPRESSION +", DECIMAL(15,3))"
														+")" 
				
				+ " WHERE " 
				+ SALARY + "." + SalaryColumns.CONTRACT + " = ? " 
				+ " AND " + SALARY + "." + SalaryColumns.TYPE + "  = ? " 
				+ " AND " + SALARY_DATA + "." + SalaryDataColumns.START_DATE + "  = ? " 
				+ " AND " + SALARY_DATA + "." + SalaryDataColumns.END_DATE + " = ? "
				+ " AND " + SALARY_DATA + "." + SalaryDataColumns.NAME + "  = '" + CGC_BASE.getName() + "'" 
				;

		protected static final class DelayContractPayment extends ContractPayment {
			@Override
			public ExpressionScope getScope() {
				return ExpressionScope.APPLICATION;
			}
		}

		private SalaryType type;
		private Date startDate;
		private Date endDate;
		private Integer contract;

		private Set<Integer> prestIts;

		private PreparedStatement stmt;

		private Map<String, Double> values;

		protected IDelayPaymentDecorator paymentDecorator;

		public DelayPaymentBuilder(Connection connection,
				IDelayPaymentDecorator paymentDecorator) throws SQLException {
			this.paymentDecorator = paymentDecorator;
			this.values = new HashMap<String, Double>();
			this.stmt = initStatement(connection);
			this.prestIts = new HashSet<Integer>();
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
		
		@Override
		public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
				Map<String, ITimedVariable<?>> context) {
			super.addZeroPayment(quote, tax, startDate, endDate, payment, context);
		}
		
		@Override
		public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
				Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
			super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
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
					Integer id = rs.getInt(SALARY_PAYMENT + "." + SalaryPaymentColumns.ID);
					if ( id != 0 && !prestIts.add(id) )
						continue;
					for (String field : fields) {
						Double value = values.get(field);
						value += rs.getDouble(field) ;
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

			ContractPayment payment = new DelayContractPayment();
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
			int i = 1;
			java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
			java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
			stmt.setDate(i++, sqlStartDate); 
			stmt.setDate(i++, sqlEndDate); 
			stmt.setDate(i++, sqlStartDate); 
			stmt.setDate(i++, sqlEndDate); 
			stmt.setDate(i++, sqlStartDate); 
			stmt.setDate(i++, sqlEndDate); 
			stmt.setDate(i++, sqlStartDate); 
			stmt.setDate(i++, sqlEndDate); 
			
			stmt.setInt(i++, contract); // SalaryColumns.CONTRACT + " = ? "
			stmt.setInt(i++, type.ordinal()); // SalaryColumns.TYPE + " = ? "
			stmt.setDate(i++, sqlStartDate); // SalaryColumns.START_DATE +
			stmt.setDate(i++, sqlEndDate); // SalaryColumns.END_DATE + "  = ? "

			return stmt.executeQuery();
		}
	}

	private static class ExtrasDelayPaymentBuilder extends DelayPaymentBuilder {

		private static final String EXTRA_PAYMENTS_SQL = 
				"SELECT " 
				+ SALARY_PAYMENT +"." + SalaryPaymentColumns.ID
				+ ", 0.00 AS " + SalaryColumns.CGC_BASE
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
			ContractPayment payment = new DelayContractPayment();
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
	
	private static double getMonthDays(ExpressionContext expressionContext, Date start, Date end ) throws ExpressionException {
		
		for ( ITimedResult<Number> monthDays : expressionContext.eval(MONTH_DAYS.getName(), start, end, Number.class) )
			return monthDays.getValue().doubleValue();
		
		throw new UndefinedContextVariablesException(MONTH_DAYS);
	}

}
