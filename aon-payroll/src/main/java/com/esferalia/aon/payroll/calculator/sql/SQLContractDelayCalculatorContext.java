package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LEAVE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
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
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.Objects;
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
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.ExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class SQLContractDelayCalculatorContext extends
		SQLContractSalaryCalculatorContext {
	
	private static final String MONTH_LEAVE_DAYS = "DIAS_IT_MES";
	private static final String ERE_DAYS = Arrays.stream(ContextVariable.ERE_DAYSS).map(v -> "'"+v.getName()+"'" ).collect(Collectors.joining(","));
	private static final String ERE_BASES = Arrays.stream(ContextVariable.ERE_BASES).map(v -> "'"+v.getName()+"'" ).collect(Collectors.joining(","));
	private static final String FREE_BASES = Arrays.stream(ContextVariable.FREE_BASES).map(v -> "'"+v.getName()+"'" ).collect(Collectors.joining(","));
	
	public static class DelaySQLContractSalaryCalculatorContext extends SQLContractSalaryCalculatorContext{

		
		private long prevDays = 0;
		private SQLContractSalaryCalculatorContext monthCtx;

		public DelaySQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate,
				Date issueDate, Criteria criteria, long prevDays ) throws SQLException, ExpressionException {
			super(connection, startDate, endDate, issueDate, criteria);
			this.prevDays = prevDays;
			this.monthCtx = getMonthContext(connection, startDate, endDate, criteria);
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
			SQLNoItContractSalaryCalculatorContext ctx =  (SQLNoItContractSalaryCalculatorContext) super.getNoItCalculatorContext(conn, startDate, endDate, issueDate, criteria, start, end);
			
			//ctx.lastLeaveEnd = 
					
			monthCtx.getLeavesPeriods().stream()
			.sorted((p1,p2) -> Period.compare(p2.getEnd(), p1.getEnd()))
			.findFirst().ifPresent(p -> {
				ctx.lastLeaveEnd = p.getEnd();
			});
			;
			
			if ( AonDateUtils.getDay(startDate) == 1 )
				return ctx;
			ctx.getExpressionContext().setVariable(ContextVariable.ACTIVE_DAYS.getName(), prevDays, startDate, endDate);
			return ctx;
		}
		
		
		@Override
		public boolean next() throws SQLException, ExpressionException {
			monthCtx.next();
			return super.next(ctx -> load(ctx, getStart(), getEnd()) );
		}
		
		@Variable(ContextVariable.ON_ACCOUNT_AGREEMENT)
		public static Object onAccountAgreement(Object obj) {
			return 0.00;
		}
		
		@Override
		protected void loadDaysContextVariables(ContractExpressionContext ctx) throws ExpressionException {
			//monthCtx.loadDaysContextVariables(ctx);
			super.loadDaysContextVariables(ctx);
			
			override(ctx, ContextVariable.ERE_DAYSS);
			override(ctx, ContextVariable.QUOTE_DAYS, ContextVariable.WORKED_DAYS, ContextVariable.TOTAL_WORKED_DAYS);
			
			loadMothItDays(ctx);
		}
		
		@Override
		protected boolean isWholeMonth() {
			return this.monthCtx.isWholeMonth();
		}

		private void loadMothItDays(ContractExpressionContext ctx) {
			try {
			
				double monthItDays = 
				monthCtx.getExpressionContext().getVariables(ContextVariable.LEAVE_DAYS).stream()
				.collect(Collectors.summingDouble(v -> ((Number) v.getValue(v.getPeriod())).doubleValue()));
				ctx.setVariable(MONTH_LEAVE_DAYS, monthItDays, monthCtx.getStart(), monthCtx.getEnd());

			} catch (Exception e) {
			}
		}
		

		
		private void override (ContractExpressionContext ctx, ContextVariable ...ctxVars  ) {
			for ( ContextVariable ctxVar : ctxVars ) {
				for ( ITimedVariable<?> var : ctx.getVariables(ctxVar.getName()) ) {
					ctx.putVariable(ctxVar.getName(), new ITimedVariable<Double>() {
						@Override
						public Period getPeriod() {
							return var.getPeriod();
						}
						@Override
						public Double getValue(Period period) {
							return monthCtx.getVariable(ctxVar, period, Double.class);
						}
					});
				}
			}
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
		
		private static SQLContractSalaryCalculatorContext getMonthContext(Connection connection, Date startDate, Date endDate, Criteria criteria) 
		throws SQLException, ExpressionException {
			return new SQLContractSalaryCalculatorContext(
					connection, 
					getFirstDayOfMonth(startDate), 
					getLastDayOfMonth(endDate), 
					getLastDayOfMonth(endDate), 
					criteria);
			
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
			
			if ( isGuarenteedPayment(contractPayment))
				return Collections.singletonList(result);

			if ( isITPayment(contractPayment))
				return Collections.singletonList( isInIT(result.getPeriod()) ? fixItResult(result) : new TimedResult<Double>(0.00, result.getPeriod(), result.getContext()));

			if (contractPayment.getType() == PaymentType.CRA_0002 
				|| contractPayment.getType() == PaymentType.CRA_0003 ) {
				try {
					return shareResult(result, expressionContext);
				} catch (ExpressionException e) {
				}
			}
			
			int month = AonDateUtils.getMonth(result.getPeriod().getStart());
			
			//expressionContext.getVariable(MONTH_DAYS, start, end, Number.class).doubleValue();
			
			double activeDays ;
			try {
				activeDays = SQLContractDelayCalculatorContext.getMonthDays(expressionContext, start, end); 
			} catch ( ExpressionException e ) {
				activeDays = monthDays.get(month);
			}
			
			double resultDays ;
			try {
				resultDays = getResultDays(expressionContext, result);
			} catch ( ExpressionException e ) {
				resultDays = AonDateUtils.getDay(result.getPeriod().getEnd()) 
				- AonDateUtils.getDay(result.getPeriod().getStart()) + 1;
			}
			
			if ( resultDays == (double) monthDays.get(month) )
				resultDays = activeDays;

			if ( !result.getContext().isEmpty() )
				activeDays  -= itDays.get(month);
			
			
			
			double value = result.getValue() / activeDays * resultDays;
			
			return Collections.singletonList(new TimedResult<Double>(value, result.getPeriod(), result.getContext()));
		}

		private List<ITimedResult<Double>> shareResult(ITimedResult<Double> result, ExpressionContext expressionContext)
				throws ExpressionException {
			double workedDays = getWorkedDays(expressionContext, result.getPeriod());
			double totalWorkedDays = getTotalWorkedDays(expressionContext, result.getPeriod());
			double value = result.getValue() / totalWorkedDays *  workedDays;
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
		
		@Override
		protected List<ITimedResult<Double>> fixStrikeResults(IContractPayment contractPayment,
				List<ITimedResult<Double>> results, List<Period> strikes, Date start, Date end,
				ExpressionContext expressionContext) throws UnsupportedOperationException {
			if (results.size() == 1
				&& isConstant(results.get(0))
				&& isPermanentPayment(contractPayment, results.get(0).getPeriod()) ) {
				try {
					return subtractOFFPart(results, strikes, expressionContext);
				} catch (UndefinedVariablesException e) {
				} catch (ExpressionException e) {
				}
			} 

			return super.fixStrikeResults(contractPayment, results, strikes, start, end, expressionContext);
		}
		
//		private Map<String, Double> guarenteed = new HashMap<String, Double>();
		
		@Override
		protected List<ITimedResult<Double>> checkCra0055Results(IContractPayment contractPayment,
				List<ITimedResult<Double>> results, Date start, Date end, ExpressionContext expressionContext) {
			if ( results.size() == 1 &&
				 results.get(0).getContext().size() <= 1) {
//				String key = String.format("%d-%2$tY-%2$tm", contractPayment.getId() , start);
//				if ( AonUtils.equals(guarenteed.get(key), results.get(0).getValue()) ) 
//					return Collections.emptyList();
//				guarenteed.put(key, results.get(0).getValue());
				return Collections.singletonList(fixItResult(results.get(0), expressionContext));
			}
			return super.checkCra0055Results(contractPayment, results, start, end, expressionContext);
		}
		
		
		protected static boolean isConstant(ITimedResult<Double> result) {
			if ( result.getContext().size() == 0 ) 
				return true;
			
			if ( result.getContext().size() > 1 ) 
				return false;
			return 
			result.getContext().values().stream().findFirst()
			.map( v -> AonUtils.equals(v.getValue(v.getPeriod()), result.getValue(result.getPeriod())) )
			.orElse(false);
		}
		
		
		// --------------------------------------------------------------------
		
		private ITimedResult<Double> fixItResult(ITimedResult<Double> result) {
			int resultDays = AonDateUtils.getDay(result.getPeriod().getEnd()) 
					- AonDateUtils.getDay(result.getPeriod().getStart()) + 1;
			int month = AonDateUtils.getMonth(result.getPeriod().getStart());
			double value = result.getValue() / itDays.get(month) * resultDays;
			return new TimedResult<Double>(value, result.getPeriod(), result.getContext());
			
		}
		
		private ITimedResult<Double> fixItResult(ITimedResult<Double> result,  ExpressionContext ctx) {
			double monthItDays = getMonthItDays(ctx);
			double resultItDays = getItDays(ctx, result.getPeriod());
			double value = result.getValue() / monthItDays * resultItDays;
			return new TimedResult<Double>(value, result.getPeriod(), result.getContext());
			
		}

		private boolean isInIT(Period p) {
			return Period.intersects(its.iterator(), Collections.singletonList(p).iterator());
		}

		private static boolean isITPayment(IContractPayment contractPayment) {
			PaymentType type = contractPayment.getType();
			String name = contractPayment.getName();
			if ( ContextVariable.PREST_IT.equals(name))
				return true;
			return false;
		}

		private static boolean isGuarenteedPayment(IContractPayment contractPayment) {
			PaymentType type = contractPayment.getType();
			if ( PaymentType.CRA_0055 == type )
				return true;
			if ( PaymentType.CRA_0054 == type )
				return true;
			String name = contractPayment.getName();
			if ( ContextVariable.GUARENTEED.equals(name))
				return true;
			return false;
		}
		
		private static double getMonthItDays(ExpressionContext ctx) {
			return ctx.getVariables(MONTH_LEAVE_DAYS).stream()
			.collect(Collectors.summingDouble(v -> ((Number) v.getValue(v.getPeriod())).doubleValue()))
			;
		}

		private static double getItDays(ExpressionContext ctx, Period p) {
			return ctx.getVariables(LEAVE_DAYS).stream()
			.collect(Collectors.summingDouble(v -> ((Number) v.getValue(v.getPeriod())).doubleValue()))
			;
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
			Collection<IContractPayment> overridePayments = getOverridePayments();
			Collection<IContractPayment> differencePayments = getDifferencePayments();
			
			List<Period> overridePeriods = overridePayments.stream()
					.map( p -> new Period(p.getStartDate(), p.getEndDate()))
					.collect(Collectors.toList());
			
			ArrayList<IContractPayment> implicitPayments = new ArrayList<IContractPayment>(overridePayments);
			for (IContractPayment differencePayment : differencePayments) {
				Period differencePeriod = new Period(differencePayment.getStartDate(), differencePayment.getEndDate());
				boolean intersects = overridePeriods.stream().anyMatch( p -> differencePeriod.intersects(p));
				if ( intersects ) 
					continue;
				else 
					implicitPayments.add(differencePayment);
			}

			return new CompositeIterator<IContractPayment>(
					explicitPayments.iterator(), implicitPayments.iterator());
		} catch (SQLException e) {
			throw new AonException(e);
		}

	}
	
	@Override
	protected void loadDaysContextVariables(ContractExpressionContext ctx) throws ExpressionException {
		override(ctx, 
				0.00, 
				ContextVariable.CGC_BASE_MIN, 
				ContextVariable.CGP_BASE_MIN 
		);
		super.loadDaysContextVariables(ctx);
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
	
	private Collection<IContractPayment> getOverridePayments()
			throws ExpressionException, SQLException, SalaryException {


		final Collection<IContractPayment> payments = new LinkedList<IContractPayment>();

		Collection<Period> periods = getPeriods(ContextVariable.DELAY_AMOUNT);

		for (Period period : periods) {

			double totalWorkedDays = getTotalDays(period, ContextVariable.WORKED_DAYS);
			
			double totalITDays = getTotalDays(period, ContextVariable.LEAVE_DAYS);
			
			ContractPayment workPayment = 
			new DelayPaymentBuilder.DelayContractPayment();
			workPayment.setId(null);
			workPayment.setStartDate(period.getStart());
			workPayment.setEndDate(period.getEnd());
			workPayment.setSalaryType(SalaryType.DELAY);

			// TODO: Generic Delays ? 
			workPayment.setType(getPaymentType(PaymentType.CRA_0008));
			workPayment.setIrpfExpression(ContextVariable.ALL);
			workPayment.setDescription(getDescriptionForSalaryDelay(workPayment, payments.size()));
			workPayment.setExpression(
					String.format(
					Locale.ROOT,
					"/*var:%s*/"
					+ "%s/%f*%s", 
					ContextVariable.DELAY_AMOUNT.getName(), 
					
					ContextVariable.DELAY_AMOUNT.getName(), 
					totalWorkedDays > 0 ? totalWorkedDays : totalITDays, 
					totalWorkedDays > 0 ? ContextVariable.WORKED_DAYS.getName() : ContextVariable.LEAVE_DAYS.getName() 
					));
			workPayment.setQuoteExpression(
					String.format(
					Locale.ROOT,
					"/*var:%s*/"
					+ "isdef %s ? %s/%f*%s : %s", 
					ContextVariable.DELAY_QUOTE.getName(), 
					ContextVariable.DELAY_QUOTE.getName(), 
					
					ContextVariable.DELAY_QUOTE.getName(), 
					totalWorkedDays > 0 ? totalWorkedDays : totalITDays, 
					totalWorkedDays > 0 ? ContextVariable.WORKED_DAYS.getName() : ContextVariable.LEAVE_DAYS.getName() ,
					ContextVariable.ALL
					));

			payments.add(workPayment);
			
			if ( totalITDays > 0 && totalWorkedDays > 0 ) {
        			ContractPayment itPayment = 
        			new DelayPaymentBuilder.DelayContractPayment();
        			itPayment.setId(null);
        			itPayment.setStartDate(period.getStart());
        			itPayment.setEndDate(period.getEnd());
        			itPayment.setSalaryType(SalaryType.DELAY);
        
        			// TODO: Generic Delays ? 
        			itPayment.setType(getPaymentType(PaymentType.CRA_0008));
        			itPayment.setIrpfExpression(ContextVariable.ALL);
        			itPayment.setDescription(getDescriptionForSalaryDelay(itPayment, payments.size()));
        			itPayment.setExpression(
        					String.format(
        					Locale.ROOT,
        					"/*var:%s*/"
        					+ "0.00*%s", 
        					ContextVariable.DELAY_AMOUNT.getName(), 
        					ContextVariable.LEAVE_DAYS.getName() 
        					));
        			itPayment.setQuoteExpression(
        					String.format(
        					Locale.ROOT,
        					"/*var:%s*/"
        					+ "%s", 
        					ContextVariable.DELAY_QUOTE.getName(), 
        					ContextVariable.ALL
        					));
        			payments.add(itPayment);
			}
		}

		
		if ( payments.isEmpty() )  {
			ExpressionImpl expression = new ExpressionImpl().setName(ContextVariable.DELAY_AMOUNT.getName()).setScope(ExpressionScope.SYSTEM);
			onUndefinedData(expression, ContextVariable.DELAY_AMOUNT.getName(), getStartDate(), getEndDate(), ContextVariable.DELAY_AMOUNT.getName());
		}
		
		return payments;

	}


	private void override (ContractExpressionContext ctx, Double value, ContextVariable ...ctxVars  ) {
		for ( ContextVariable ctxVar : ctxVars ) {
			for ( ITimedVariable<?> var : ctx.getVariables(ctxVar.getName()) ) {
				ctx.putVariable(ctxVar.getName(), new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return var.getPeriod();
					}
					@Override
					public Double getValue(Period period) {
						return value;
					}
				});
			}
		}
	}


	private double getTotalDays(Period period, ContextVariable contextVariable) {
		double totalWorkedDays;
		try {
			totalWorkedDays = 
			getExpressionContext().eval(contextVariable.getName(), period.getStart(), period.getEnd(), Double.class)
			.stream().collect(Collectors.summingDouble( ITimedResult::getValue ))
			;
		} catch ( ExpressionException e ) {
			totalWorkedDays = 
			getPeriods(contextVariable).stream()
			.map( p -> p.intersect(period)).filter( Objects::nonNull)
			.collect(Collectors.summingLong( Period::getDays ));
		}
		return totalWorkedDays;
	}

	private Collection<IContractPayment> getDifferencePayments()
			throws ExpressionException, SQLException, SalaryException {

		Date startDate = getStartDate();
		Date endDate = getEndDate();

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
			@Override
			public PaymentType getPaymentType(IContractPayment payment) {
				return SQLContractDelayCalculatorContext.this.getPaymentType(PaymentType.CRA_0008);
			}
			
			@Override
			public String getExpressionFor(IContractPayment payment, String expression) {
				return String.format("/*var:%s*/%s",ContextVariable.DELAY_AMOUNT.getName(), expression);
			}

			@Override
			public String getQuoteExpressionFor(IContractPayment payment, String quoteExpression) {
				return String.format("/*var:%1$s*/isdef %1$s ? %1$s : %2$s",
						ContextVariable.DELAY_QUOTE.getName(), 
						quoteExpression);
			}
		};
		ExtraDelayPaymentDecorator extraPaymentDecorator = new ExtraDelayPaymentDecorator() {
			@Override
			public  int getOrdinal(IContractPayment payment) {
				return payments.size()+1;
			}
			@Override
			public PaymentType getPaymentType(IContractPayment payment) {
				return PaymentType.CRA_0000;
			}

			@Override
			public String getExpressionFor(IContractPayment payment, String expression) {
				return expression;
			}

			@Override
			public String getQuoteExpressionFor(IContractPayment payment, String quoteExpression) {
				return quoteExpression;
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
			
			
			DelaySQLContractSalaryCalculatorContext ctx = new DelaySQLContractSalaryCalculatorContext(
					connection, 
					period.getStart(), 
					period.getEnd(),
					period.getEnd(), 
					criteria, 
					prevDays) ;
			
			while (ctx.next()) {
				calculator.calculate(ctx);
				payments.addAll(delayPaymentBuilder.getContractPayments());
				payments.addAll(extrasDelayPaymentBuilder.getContractPayments());
			}
			
			prevDays += period.daysStream().count();

		}


		return payments;

	}
	
	private PaymentType getPaymentType(PaymentType def) {
		
		try {
			List<ITimedResult<PaymentType>> results = 
			getExpressionContext().eval(ContextVariable.DELAY_CAUSE.getName(), getStartDate(), getEndDate(), PaymentType.class);
			List<PaymentType> paymentTypes = results.stream().map( r -> r.getValue()).distinct().collect(Collectors.toList());
			if ( paymentTypes.size() == 1 ) 
				return paymentTypes.get(0);
		
		} catch (Exception e) {
			getExpressionContext().putVariable(
					ContextVariable.DELAY_CAUSE.getName(), 
					new ExpressionVariable<>(def, 
					new Period(getStartDate(), getEndDate()), 
					new ExpressionImpl()
					.setScope(ExpressionScope.CONTRACT)
					.setName(ContextVariable.DELAY_CAUSE.getName())
					.setExpression(def.name())
					));
		}
		getExpressionContext().readVariable(ContextVariable.DELAY_CAUSE.getName(), getStartDate(), getEndDate(), PaymentType.class);
		return def;
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
				+" AND " + SALARY_DATA + "." + SalaryDataColumns.NAME 
				+ " IN( '" + CGC_BASE.getName() + "', " + FREE_BASES + ", " + ERE_BASES + " )"
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
		PaymentType getPaymentType(IContractPayment payment);
		String getExpressionFor(IContractPayment payment, String expression);
		String getQuoteExpressionFor(IContractPayment payment, String quoteExpression);
	}

	private static class DelayPaymentBuilder<T extends ISalary> extends AbstractSalaryBuilder<T> {

		private static final String GARANTIZADO = 
				"(SELECT"
				+ " SUM(" + SalaryDataColumns.EXPRESSION + ")"
				+ " FROM " + SALARY_DATA 
				+ " WHERE " + SalaryDataColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryDataColumns.NAME + " = 'GARANTIZADO'"
				+ " AND " + SalaryDataColumns.START_DATE + " = ? " 
				+ " AND " + SalaryDataColumns.END_DATE + " =  ? " 
				+ ")"
				;

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
				+ " AND (" + SalaryPaymentColumns.TYPE  + " = 55"
				+ " OR " + SalaryPaymentColumns.PAYMENT_CONCEPT + " = 'GARANTIZADO' "
				+ " OR " + SalaryPaymentColumns.DESCRIPTION  + " LIKE '%MEJORA%PREST.SS.INCAPACIDAD%TEMPORAL%')"
				+ ")"
				+", 0.00)";
				;

		private static final String OTHERS_IRPF_SQL = 
				"IFNULL((SELECT"
				+ " SUM(" + SalaryPaymentColumns.IRPF+")"
				+ " FROM " + SALARY_PAYMENT 
				+ " WHERE " + SalaryPaymentColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryPaymentColumns.TYPE + " NOT IN( 55 )"
				+ " AND " + SalaryPaymentColumns.PAYMENT_CONCEPT + " NOT IN( 'PREST_IT', 'GARANTIZADO')"
				+ " AND " + SalaryPaymentColumns.DESCRIPTION  + " NOT LIKE '%MEJORA%PREST.SS.INCAPACIDAD%TEMPORAL%')"
				+ ")"
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
				+ " AND (" + SalaryPaymentColumns.TYPE  + " = 55"
				+ " OR " + SalaryPaymentColumns.PAYMENT_CONCEPT + " = 'GARANTIZADO'"
				+ " OR " + SalaryPaymentColumns.DESCRIPTION  + " LIKE '%MEJORA%PREST.SS.INCAPACIDAD%TEMPORAL%')"
				+ ")"
				+", 0.00)";
				;

		private static final String OTHERS_AMOUNT_SQL = 
				"IFNULL((SELECT"
				+ " SUM(" + SalaryPaymentColumns.AMOUNT+")"
				+ " FROM " + SALARY_PAYMENT 
				+ " WHERE " + SalaryPaymentColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND (" + SalaryPaymentColumns.TYPE + " IS NULL OR " + SalaryPaymentColumns.TYPE + " NOT IN( 55 )" + ")"
				+ " AND (" + SalaryPaymentColumns.PAYMENT_CONCEPT + " IS NULL OR " + SalaryPaymentColumns.PAYMENT_CONCEPT + " NOT IN( 'PREST_IT', 'GARANTIZADO')" + ")"
				+ " AND (" + SalaryPaymentColumns.DESCRIPTION + " IS NULL OR " + SalaryPaymentColumns.DESCRIPTION  + " NOT LIKE '%MEJORA%PREST.SS.INCAPACIDAD%TEMPORAL%')" + ")"
				+", 0.00)";
				;

		private static final String IT_DAYS = 
				"(SELECT"
				+ " SUM(" + SalaryDataColumns.EXPRESSION + ")"
				+ " FROM " + SALARY_DATA 
				+ " WHERE " + SalaryDataColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND (" 
				+ SalaryDataColumns.NAME + " LIKE 'DIAS_ENFERMEDAD%'" 
				+ " OR " + SalaryDataColumns.NAME + " IN ('DIAS_MATERNIDAD', 'DIAS_PATERNIDAD'," + ERE_DAYS + " )"
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
		
		private static final String DROP_DAYS = 
				"IFNULL((SELECT"
				+ " SUM(" + "(DATEDIFF("+SalaryDataColumns.END_DATE+", "+ SalaryDataColumns.START_DATE +") + 1 )" + ")"
				+ " FROM " + SALARY_DATA 
				+ " WHERE " + SalaryDataColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryDataColumns.NAME + " = 'DIAS_AUSENCIA'"
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
				+ " OR " + SalaryDataColumns.NAME + " IN ('DIAS_MATERNIDAD', 'DIAS_PATERNIDAD', " + ERE_DAYS + " )"
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

		private static final String PREST_IT = 
				"(SELECT"
				+ " SUM(" + SalaryDataColumns.EXPRESSION + ")"
				+ " FROM " + SALARY_DATA 
				+ " WHERE " + SalaryDataColumns.SALARY + " = " + SALARY +"." + SalaryColumns.ID 
				+ " AND " + SalaryDataColumns.NAME + " = 'PREST_IT'"
				+ " AND " + SalaryDataColumns.START_DATE + " = ? " 
				+ " AND " + SalaryDataColumns.END_DATE + " =  ? " 
				+ " AND " + SalaryDataColumns.EXPRESSION + " != '0.0' "
				+ ")"
				;
		
		private static final String GTZDO_IT = 
				"(IFNULL(GARANTIZADO,IFNULL((GTZDO / ALLITDAYS  * ITDAYS ),0.00)))";


		private static final String AMOUNT = 
				"(( OTHERSAMOUNT ) / ALLWORKEDDAYS * ( IF(WORKEDDAYS > 0.00, WORKEDDAYS , IF(DROPDAYS > 0.00, 0.00 ,(DATEDIFF(?, ?) + 1 )) ) ))";
		
		private static final String PAYMENT = 
				"(IFNULL( " + "IFNULL(" + SALARY_PAYMENT +"." + SalaryPaymentColumns.AMOUNT + ",PRESTIT )" + " + " + GTZDO_IT
				+ ", (" + SalaryColumns.TOTAL_PAYMENT + "- ( PRESTITAMOUNT + GTZDO )) / ALLWORKEDDAYS * WORKEDDAYS )"
				+ ")";

		private static final String IRPF = 
				"(IFNULL( " + "IFNULL(" + SALARY_PAYMENT +"." + SalaryPaymentColumns.IRPF +", PRESTIT )" + " + " + GTZDO_IT
				+ ", (" + SalaryColumns.IRPF_BASE + " - ( PRESTITIRPF + GTZDO )) / ALLWORKEDDAYS * WORKEDDAYS )"
				+ ")";
		
		private static final String SALARY_SQL = 
				"SELECT "
				+ SalaryPaymentColumns.ID
				+", ITDAYS"
				+ ", " + SalaryColumns.CGC_BASE
				+ ", IF ( ITDAYS IS NULL , "+ AMOUNT +" , " + IRPF + " ) AS " + SalaryColumns.IRPF_BASE
				+ ", IF ( ITDAYS IS NULL , "+ AMOUNT +" , " + PAYMENT + " ) AS " + SalaryColumns.TOTAL_PAYMENT

				+" FROM ("
					+" SELECT " 
					
					+ SALARY_PAYMENT +"." + "*"
					
					+ ", " + SALARY + "." + SalaryColumns.IRPF_BASE
					+ ", " + SALARY + "." + SalaryColumns.TOTAL_PAYMENT
					
					+ ", " + GARANTIZADO_AMOUNT_SQL +  ""
					+ " AS GTZDO" 
	
					+ ", " + GARANTIZADO +  ""
					+ " AS GARANTIZADO" 
					
					+ ", (" + ALL_IT_DAYS +")" 
					+ " AS ALLITDAYS" 
	
					+ ", (" + IT_DAYS +")" 
					+ " AS ITDAYS" 
	
					+ ", (" + ALL_WORKED_DAYS +")" 
					+ " AS ALLWORKEDDAYS" 
	
					+ ", (" + WORKED_DAYS +")" 
					+ " AS WORKEDDAYS" 
	
					+ ", (" + DROP_DAYS +")" 
					+ " AS DROPDAYS" 
					
					+ ", (" + PREST_IT +")" 
					+ " AS PRESTIT" 

					+ ", (" + PREST_IT_IRPF_SQL +")" 
					+ " AS PRESTITIRPF" 
					
					+ ", (" + PREST_IT_AMOUNT_SQL +")" 
					+ " AS PRESTITAMOUNT" 
	
					+ ", (" + OTHERS_AMOUNT_SQL +")" 
					+ " AS OTHERSAMOUNT" 

					+ ", SUM(" + SALARY_DATA + "."+ SalaryDataColumns.EXPRESSION 
					+ ") AS " + SalaryColumns.CGC_BASE
					
					+ " FROM "
					+ SALARY 
					+" INNER JOIN " + SALARY_DATA + " ON (" + SALARY + "." + SalaryColumns.ID + " = " + SALARY_DATA + "." + SalaryDataColumns.SALARY + ")" 
					+" LEFT JOIN " + SALARY_PAYMENT + " ON (" + SALARY_DATA + "." + SalaryDataColumns.SALARY +  " = " + SALARY_PAYMENT + "." + SalaryPaymentColumns.SALARY 
															+ " AND  "+ SALARY_PAYMENT + "." +SalaryPaymentColumns.PAYMENT_CONCEPT + " =  'PREST_IT'"  
															+ " AND  ROUND("+ SALARY_PAYMENT + "." +SalaryPaymentColumns.QUOTE + ",2) =  CONVERT(" + SALARY_DATA + "."+ SalaryDataColumns.EXPRESSION +", DECIMAL(15,2))"
															+")" 
					
					+ " WHERE " 
					+ SALARY + "." + SalaryColumns.CONTRACT + " = ? " 
					+ " AND " + SALARY + "." + SalaryColumns.TYPE + "  = ? " 
					+ " AND " + SALARY_DATA + "." + SalaryDataColumns.START_DATE + "  = ? " 
					+ " AND " + SALARY_DATA + "." + SalaryDataColumns.END_DATE + " = ? "
					+ " AND " + SALARY_DATA + "." + SalaryDataColumns.NAME + "  IN ('" + CGC_BASE.getName() + "', " + FREE_BASES+  ", " + ERE_BASES + ")" 
					+ " GROUP BY 1"
	//				+ " ORDER BY 1"
				+") AS " + SALARY_PAYMENT
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
		
		private double directBase ;

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
		public void createNewSalary() {
			super.createNewSalary();
			this.directBase = 0.00;
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
			values.put(SalaryColumns.CGC_BASE, cgcBase /*+ directBase*/);
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
			
			if ( DIRECT_PAY.getName().equals(payment.getName() ))
				directBase += quote;
			
			//System.out.printf("*[%1$td-%2$td] %3$s : %4$f,  %5$f \r\n", startDate, endDate, description, amount, quote);
		}

		public Collection<IContractPayment> getContractPayments()
				throws SQLException {

			Set<String> fields = values.keySet();

			Map<String, Double> paidValues = getPaidSalary(fields, SalaryType.SALARY, SalaryType.DELAY);

			Map<String, Double> diffValues = new HashMap<String, Double>();
			
			for (String field : fields) {
				Double value = values.get(field);
				value = value == null ? 0.00 : value;
				Double paidValue = paidValues.get(field);
				paidValue = paidValue == null ? 0.00 : paidValue;
				Double diffValue = value - paidValue;
				diffValues.put(field, diffValue);
				
				 
			}
			
//			values.forEach((k,v) -> System.out.printf("[NEW] %s=%s", k,v ));
//			System.out.println();
//			paidValues.forEach((k,v) -> System.out.printf("[OLD] %s=%s", k,v ));
//			System.out.println();
//			diffValues.forEach((k,v) -> System.out.printf("[DIFF] %s=%s", k,v ));
//			System.out.println();

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

		private Map<String, Double> getPaidSalary(Set<String> fields, SalaryType ...types) throws SQLException{
		    Map<String, Double> totalPaidSalary = new HashMap<>();
		    for (SalaryType salaryType : types) {
			Map<String, Double> paidSalary = getPaidSalary(fields, salaryType);
			paidSalary.forEach((field, value) -> totalPaidSalary.merge(field, value, Double::sum) );
		    } 
		    return totalPaidSalary;
		}
		private Map<String, Double> getPaidSalary(Set<String> fields, SalaryType salaryType)
				throws SQLException {
			ResultSet rs = null;
			try {

				rs = initResultSet(stmt, contract, salaryType, startDate, endDate);

				Map<String, Double> values = new HashMap<>();

				for (String field : fields) {
					values.put(field, 0.00);
				}

				while (rs.next()) {
					Integer itDays = rs.getInt("ITDAYS");
					Integer id = rs.getInt(SALARY_PAYMENT + "." + SalaryPaymentColumns.ID);
					if ( id != 0 && itDays != 0 && !prestIts.add(id) )
						continue;
					for (String field : fields) {
						Double value = values.get(field);
						value += rs.getDouble(field) ;
						values.put(field, value);
					}
					break;
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
			paymentConcept.setType( paymentDecorator.getPaymentType(payment) );
			// we use Locale.US to avoid ',' instead of '.' like decimals
			// separator.
			// Be care that MVEL like any other expression language don't
			// understand ','.
			paymentConcept.setExpression(paymentDecorator.getExpressionFor(payment, String.format(Locale.US, "%.2f", 
					amount)));
			paymentConcept.setIrpfExpression(String.format(Locale.US, "%.2f",
					irpf));
			paymentConcept.setQuoteExpression(paymentDecorator.getQuoteExpressionFor(payment, String.format(Locale.US, "%.2f", 
					quote)));
			paymentConcept.setDescription(paymentDecorator.getDescriptionFor(payment));

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
			// AMOUNT DATEDIFF(?, ?)
			stmt.setDate(i++, sqlEndDate); 
			stmt.setDate(i++, sqlStartDate); 
			// AMOUNT DATEDIFF(?, ?)
			stmt.setDate(i++, sqlEndDate); 
			stmt.setDate(i++, sqlStartDate); 
			// GARANTIZADO
			stmt.setDate(i++, sqlStartDate); 
			stmt.setDate(i++, sqlEndDate); 
			// IT_DAYS
			stmt.setDate(i++, sqlStartDate); 
			stmt.setDate(i++, sqlEndDate); 
			// WORKED_DAYS
			stmt.setDate(i++, sqlStartDate); 
			stmt.setDate(i++, sqlEndDate); 
			// DROP_DAYS
			stmt.setDate(i++, sqlStartDate); 
			stmt.setDate(i++, sqlEndDate); 
			//PREST_IT
			stmt.setDate(i++, sqlStartDate); 
			stmt.setDate(i++, sqlEndDate); 
			
			// DATEDIFF(?, ?) 
			//stmt.setDate(i++, sqlEndDate); 
			//stmt.setDate(i++, sqlStartDate); 
			
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
				+ ", 1.00 AS ITDAYS " 
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
				+ " AND " 	+ SQLConstants.SALARY + "." + SalaryColumns.END_DATE + " >= ? " 
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
		
		@Override
		public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
				Date endDate, IPayment payment, Map context) {
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

	private static double getWorkedDays(ExpressionContext expressionContext, ITimedResult<?> result ) throws ExpressionException {
		
		return getWorkedDays(expressionContext, result.getPeriod());
	}

	private static double getWorkedDays(ExpressionContext expressionContext, Period period ) throws ExpressionException {
		
		Date start = period.getStart();
		Date end = period.getEnd();
		
		return expressionContext.eval(WORKED_DAYS.getName(), start, end, Number.class).stream().collect(Collectors.summingDouble(v -> v.getValue().doubleValue()));
	}

	private static double getTotalWorkedDays(ExpressionContext expressionContext, Period period ) throws ExpressionException {
		
		Date start = period.getStart();
		Date end = period.getEnd();
		
		return expressionContext.eval(ContextVariable.TOTAL_WORKED_DAYS.getName(), start, end, Number.class).stream().collect(Collectors.summingDouble(v -> v.getValue().doubleValue()));
	}

	private static double getResultDays(ExpressionContext expressionContext, ITimedResult<?> result ) throws ExpressionException {
		
		Date start = result.getPeriod().getStart();
		Date end = result.getPeriod().getEnd();
		
		double resultDays = expressionContext.eval(QUOTE_DAYS.getName(), start, end, Number.class).stream().collect(Collectors.summingDouble(v -> v.getValue().doubleValue()));
		
		for ( ITimedResult<Number> maternityFactor : expressionContext.eval(MATERNITY_FACTOR.getName(), start, end, Number.class) ) {
			resultDays  -= (1.00 - maternityFactor.getValue().doubleValue()) * Math.min(maternityFactor.getPeriod().daysStream().count(), resultDays); 
		}
		
		return resultDays;
		
	}
	
	

}
