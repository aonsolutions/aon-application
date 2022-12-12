package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.calculator.ContextFunctions.parseExtraDate;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ACTUAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTHLY_PAYMENTS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NATURAL_MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.watson.util.AonStringUtils.isNotBlank;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.mvel2.CompileException;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.DelegateContractPayment;
import com.esferalia.aon.payroll.DelegateIterator;
import com.esferalia.aon.payroll.calculator.CompositePayments;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.SimpleContractPayment;
import com.esferalia.aon.payroll.calculator.UndefinedContextVariablesException;
import com.esferalia.aon.payroll.calculator.sql.FilterCollection.Filter;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementPaymentsFactory.IExtraPayment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SQLContractExtraCalculatorContext extends SQLContractSalaryCalculatorContext {
	
	private static final String DEFAULT_EXTRA_NAME = "PAGA_EXTRA";
	

	public SQLContractExtraCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate)
			throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, null);
	}

	public SQLContractExtraCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, issueDate, criteria);
	}

	public SQLContractExtraCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria);
	}

	public SQLContractExtraCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria, OrderByList orderByList) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria, OLDER);
	}


	// -------------------------------------------------------------------------

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.EXTRA;
	}
	
	@Override
	public Collection<IContractCost> getContractCosts() throws AonException {
		return Collections.emptyList();
	}

	@Override
	public Collection<IContractBonus> getContractBonus() throws AonException {
		return Collections.emptyList();
	}

	@Override
	public Collection<IContractPayment> getContractPayments() throws AonException {
		
		SSRegimeType ssRegime = getSSRegime();
		
		if ( ssRegime == SSRegimeType.SELF_EMPLOYED ) {
			addSalaryContractPayments();

			return new FilterCollection<>(
					getExtraPaymentFilter(), 
					super.getContractPayments());
		} // TODO: This should not be necessary!!!
		
		Collection<IContractPayment> overridePayments = getOverridePayments();
		
		Collection<IContractPayment> monthlyQuotedPayments = getMonthlyQuotedPayments();

		Collection<IContractPayment> implicitPayemnts = merge(overridePayments, monthlyQuotedPayments); 
		
		if ( implicitPayemnts.size() == getMonths() ) 
			return implicitPayemnts;
		
		
		addSalaryContractPayments();
		
		Filter<IContractPayment> extraPaymentFilter = getExtraPaymentFilter();
		
		Collection<IContractPayment> contractPayments = new ContractPayments(getExtraContractPayments(), extraPaymentFilter);

		return  
		new FilterCollection<>(extraPaymentFilter, new CompositePayments<IContractPayment>(implicitPayemnts, contractPayments,getWarnPayment(monthlyQuotedPayments)));
		
	}
	
	

	@Override
	protected void loadContractData(ExpressionContext ctx) throws SQLException {
		super.loadContractData(ctx);
		try {
			fixMonthVariables(ctx);
		} catch (ExpressionException e) {
		}
	}
	
	@Override
	protected void initContractExpressionCtx(NextHook hook) throws SQLException, ExpressionException {
		super.initContractExpressionCtx(hook);
		try {
			initMonthVariables(getExpressionContext());
		} catch (ExpressionException  e) {
		}
	}

	@Override
	protected ITimedVariable<Number> getExtraDays(ITimedVariable<Number> monthDays) {
		return monthDays;
	}
	
	@Override
	protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(Connection conn, Date startDate,
			Date endDate, Date issueDate, Criteria criteria, int start, int end) {
		ISQLContractSalaryCalculatorContext ctx =  super.getNoItCalculatorContext(conn, startDate, endDate, issueDate, criteria, start, end);
		try {
			initMonthVariables(ctx.getExpressionContext());
		} catch (ExpressionException  e) {
		}
		return ctx;
	}

	@Override
	protected Object onAllGuarantee(Double guarenteed, Double totalPayment) throws UndefinedContextVariablesException {
		return Math.max(0.00, guarenteed - 0.00);
	}

	@Override
	protected Object onGuarantee(List<ITimedResult<Double>> guarenteeResults) {
		double guarantee = 0.00;
		for( ITimedResult<Double> guarenteeResult: guarenteeResults) {
			guarantee += guarenteeResult.getValue() ;
		}

		return guarantee;
	}
	// -------------------------------------------------------------------------

	protected FilterCollection.Filter<IContractPayment>  getExtraPaymentFilter() {
		Date issueDate = getIssueDate();
		return new ExtraPaymentFilter(issueDate);
	}

	protected Collection<IContractPayment> getExtraContractPayments() throws AonException {
		return super.getContractPayments();
	}

	// -------------------------------------------------------------------------
	
	private Collection<IContractPayment> getWarnPayment(Collection<IContractPayment> payments) {
		
		SimpleContractPayment warnPayment = new SimpleContractPayment();
		
		warnPayment.setStartDate(getStart());
		warnPayment.setEndDate(getEnd());
		warnPayment.setDescription("AVISO");
		warnPayment.setExpression("HIDE(\""
		+"<div>Atenci&oacute;n, el prorrateo de la paga est&aacute; incompleto."
		+"No se han emitido las n&oacute;minas de "+ getMissed(payments) + "."
		+"<div>Por favor revise las n&oacute;minas y paga extra.</div>"
		+"<div>&nbsp;</div><div class='aon-text-right'>Disculpe las molestias, <span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
		);
		warnPayment.setType(PaymentType.CRA_0000);
		warnPayment.setSalaryType(getSalaryType());
		int month = CommonUtil.getMonth(getIssueDate());
		warnPayment.setMonth(Month.getMonthByValue(month));
		warnPayment.setExpressionScope(ExpressionScope.SYSTEM);
		
//		warnPayment.setName("AVISO");
//		warnPayment.setIrpfExpression("_P");
//		warnPayment.setQuoteExpression("_P");
		warnPayment.setId(Integer.MAX_VALUE);
		
		return Collections.singleton(warnPayment);
	}


	private Collection<IContractPayment> getMonthlyQuotedPayments() throws AonException {

		Collection<IContractPayment> extraPayments = new ArrayList<IContractPayment>();
		FilterCollection.Filter<IContractPayment> filter = getExtraPaymentFilter();
		for ( IContractPayment p : super.getContractPayments() ) {
			if ( filter.accept(p) )  {
				extraPayments.add(new SimpleContractPayment(p));
			}
		}
		
		List<IContractPayment> monthlyQuotedPayments = new ArrayList<IContractPayment>();
		
		int contractId = getId();
		
		AON.getSalaries(new AONContext(connection),
		p -> p.getIsSalaryProperty().eq(true)
			.and(p.getContractProperty().eq(contractId))
			.and(p.getStartDateProperty().le(getEnd()))
			.and(p.getEndDateProperty().ge(getStart())))
		.forEach(salary -> {
			List<IContractPayment> payments = new ArrayList<IContractPayment>();
			
			
			salary.getPayments().stream()
			.filter( p -> isNotProrrated(salary,p))
			.distinct().forEach( salaryPayment -> 
			getContractPaymentByDescription(salaryPayment, extraPayments)
			.ifPresent( p -> payments.add(salary2ContractPayment(salary,salaryPayment, p)))
			);
			
			
//			if ( payments.isEmpty() )  {
//				salary.getPayments().forEach( salaryPayment -> 
//				getContractPaymentByDescription(salaryPayment, extraPayments)
//				.ifPresent( p -> payments.add(salary2ContractPayment(salary,salaryPayment, p)))
//				);
//			}

			if ( payments.isEmpty() )  {
				salary.getPayments().stream()
				.filter(p -> isNotProrrated(salary,p))
				.forEach( salaryPayment -> 
				getContractPaymentByName(salaryPayment, extraPayments)
				.ifPresent( p -> {
					if ( payments.isEmpty() )
						payments.add(salary2ContractPayment(salary,salaryPayment, p));
				})
				);
				
			}
			
				
			payments.stream().findFirst()
			.ifPresentOrElse(
			(p) -> monthlyQuotedPayments.addAll(payments), 
			() -> extraPayments.stream().findAny().ifPresent(p -> monthlyQuotedPayments.add(salary2ContractPayment(salary, p,"0.00"))));

		})
		;
		
		
		return monthlyQuotedPayments;
	}
	
	private Collection<IContractPayment> getOverridePayments()  {
		String overrideVarName = getOverrideVarName();
		
		Filter<IContractPayment> extraPaymentFilter = getExtraPaymentFilter();
		
		try {
			for ( IContractPayment p : super.getContractPayments() )
				if ( extraPaymentFilter.accept(p))
					return getOverridePayments( overrideVarName , p);
		} catch (AonException e) {
		}
		
		return Collections.emptyList();
	}

	protected String getOverrideVarName() {
		int issueDay = AonDateUtils.get(getIssueDate(), Calendar.DAY_OF_MONTH ); 
		int issueMonth = AonDateUtils.get(getIssueDate(), Calendar.MONTH ) +1; 
		
		String overrideVarName = String.format("%s_%d_%d", DEFAULT_EXTRA_NAME, issueDay, issueMonth);
		return overrideVarName;
	}
	
	
	private Collection<IContractPayment> getOverridePayments(String varName, IContractPayment contractPayment){


		final Collection<IContractPayment> payments = new LinkedList<IContractPayment>();

		Period[] periods = getPeriods(varName);

		for (Period period : periods) {

			//DelegateContractPayment payment = new DelegateContractPayment(contractPayment) {
			@SuppressWarnings("serial")
			SimpleContractPayment payment = new SimpleContractPayment(contractPayment) {
				
				@Override
				public Integer getId() {
					return  Integer.MIN_VALUE + ( super.getId() % 1000 ); //;super.getId() * (-1)
				}
				
				@Override
				public Date getEndDate() {
					return period.getEnd();
				}

				@Override
				public Date getStartDate() {
					return period.getStart();
				}
				
				@Override
				public SalaryType getSalaryType() {
					return SalaryType.EXTRA;
				}

				@Override
				public ExpressionScope getScope() {
					return ExpressionScope.APPLICATION;
				}
				
				@Override
				public String getExpression() {
					return String.format(Locale.ROOT,"/*var:%s*/%s",varName, varName);
				}
				
				@Override
				public String getName() {
					String name = super.getName();
					return AonStringUtils.isNotBlank(name) ? name : DEFAULT_EXTRA_NAME ;
				}
			};
			
			
			;
			
			payments.add(payment);
		}

		
		if ( payments.isEmpty() )  {
			ExpressionImpl expression = new ExpressionImpl().setName(varName).setScope(ExpressionScope.SYSTEM);
			onUndefinedData(expression, varName, getStartDate(), getEndDate(), varName);
		}
		
		return payments;

	}

	private IContractPayment salary2ContractPayment(
			com.esferalia.aon.occam.api.model.Salary salary, 
			com.esferalia.aon.occam.api.model.Salary.Payment salaryPayment, 
			IContractPayment contractPayment) {
		DelegateContractPayment payment = new DelegateContractPayment(contractPayment) {
			
			@Override
			public Integer getId() {
				return  Integer.MIN_VALUE + ( contractPayment.getId() % 1000 ); //;super.getId() * (-1)
			}
			
			@Override
			public Date getEndDate() {
				return salary.getEndDate();
			}

			@Override
			public Date getStartDate() {
				return salary.getStartDate();
			}
			
			@Override
			public SalaryType getSalaryType() {
				return SalaryType.EXTRA;
			}

			@Override
			public ExpressionScope getScope() {
				return ExpressionScope.APPLICATION;
			}
			
			@Override
			public String getExpression() {
				return String.format(Locale.ROOT,"/*var:%s*/%f", getOverrideVarName(), salaryPayment.getQuote());
			}
			
			@Override
			public String getName() {
				String name = super.getName();
				return AonStringUtils.isNotBlank(name) ? name : DEFAULT_EXTRA_NAME ;
			}
		};
		
		;
		
		return payment;
	}
	
	private IContractPayment salary2ContractPayment(
			com.esferalia.aon.occam.api.model.Salary salary, 
			IContractPayment contractPayment,
			String expression) {
		DelegateContractPayment payment = new DelegateContractPayment(contractPayment) {
			
			@Override
			public Integer getId() {
				return  Integer.MIN_VALUE + ( contractPayment.getId() % 1000 ); //;super.getId() * (-1)
			}
			
			@Override
			public Date getEndDate() {
				return salary.getEndDate();
			}

			@Override
			public Date getStartDate() {
				return salary.getStartDate();
			}
			
			@Override
			public String getExpression() {
				return expression;
			}
			@Override
			public ExpressionScope getScope() {
				return ExpressionScope.APPLICATION;
			}
		};
		
		;
		
		return payment;
	}

	private Optional<IContractPayment> getContractPaymentByDescription(com.esferalia.aon.occam.api.model.Salary.Payment salaryPayment, Collection<IContractPayment> contractPayments ) {
		for (IContractPayment contractPayment : contractPayments) {
			if ( AonStringUtils.equals(contractPayment.getDescription(), salaryPayment.getDescription()) ) {
				return Optional.of(contractPayment);
			}
		}
		
		return Optional.empty();
	}

	private Optional<IContractPayment> getContractPaymentByName(com.esferalia.aon.occam.api.model.Salary.Payment salaryPayment, Collection<IContractPayment> contractPayments ) {
		for (IContractPayment contractPayment : contractPayments) {
			if ( AonStringUtils.equals(contractPayment.getName(), salaryPayment.getName()) ) {
				return Optional.of(contractPayment);
			}
		}
		
		return Optional.empty();
	}
	
	private void addSalaryContractPayments() throws ExpressionException, AonException {
		ExpressionContext expressionContext = super.getExpressionContext();
		
		List<IContractPayment> undefPayments = new ArrayList<IContractPayment>();
		
		for (IContractPayment p : super.getContractPayments()) {
			

			Date paymentStart = Period.max(p.getStartDate(), getStart());
			Date paymentEnd = Period.min(p.getEndDate(), getEnd());
			
			if ( Period.compare(paymentStart, paymentEnd)> 0)
				continue;

			if (isSalaryPayment(p)) {
				try {
					addSalaryPayment(expressionContext, p, paymentStart, paymentEnd);
				}catch (com.esferalia.aon.salary.expression.InterruptedException e) {
					throw e;
				}catch (UndefinedVariablesException e) {
					undefPayments.add(new SimpleContractPayment(p));
				}catch (ExpressionException e) {
				}catch (CompileException e) {
					expressionContext.setVariable(p.getName(), 0.00, paymentStart, paymentEnd);
				}
			}
		}
		
		List<IContractPayment> resolved = new ArrayList<IContractPayment>();
		do {
			resolved.clear();
			for ( IContractPayment p : undefPayments ) {
				

				Date paymentStart = Period.max(p.getStartDate(), getStart());
				Date paymentEnd = Period.min(p.getEndDate(), getEnd());
				
				if ( Period.compare(paymentStart, paymentEnd)> 0)
					continue;
	
				if (isSalaryPayment(p)) {
					try {
						addSalaryPayment(expressionContext, p, paymentStart, paymentEnd);
						resolved.add(p);
					}catch (com.esferalia.aon.salary.expression.InterruptedException e) {
						throw e;
					}catch (ExpressionException e) {
					}catch (CompileException e) {
					}
				}
			}
			undefPayments.removeAll(resolved);
		}
		while (resolved.size() > 0 && undefPayments.size() > 0);
		
		for ( IContractPayment p : undefPayments ) {
			
			Date paymentStart = Period.max(p.getStartDate(), getStart());
			Date paymentEnd = Period.min(p.getEndDate(), getEnd());
			addResult(expressionContext, p, paymentStart, paymentEnd, 0.00);
		}		
		
	}

	private void addSalaryPaymentByMonth(ExpressionContext expressionContext, IContractPayment payment, Date paymentStart,
			Date paymentEnd) throws ExpressionException, UndefinedVariablesException {

		List<ITimedVariable<?>> monthDaysList = new ArrayList<ITimedVariable<?>>(
				expressionContext.getTimedVariables(NATURAL_MONTH_DAYS.getName()));
		
		Period paymentPeriod = new Period(paymentStart, paymentEnd);
		
		for (ITimedVariable<?> monthDays : monthDaysList) {
			Period intersect = monthDays.getPeriod().intersect(paymentPeriod);
			if ( intersect == null )
				continue;
			try {
				addSalaryPayment(expressionContext, payment, intersect.getStart(), intersect.getEnd());
			} catch ( CompileException e ){
				expressionContext.setVariable(payment.getName(), 0.00, intersect.getStart(), intersect.getEnd());
			}
		}
	
	}

	private void addSalaryPayment(ExpressionContext expressionContext, IContractPayment payment, Date paymentStart,
			Date paymentEnd) throws ExpressionException, UndefinedVariablesException {
		
		List<ITimedResult<Double>> results = expressionContext.eval(payment.getExpression(), paymentStart, paymentEnd,
				Double.class);
		
		
		if ( results.isEmpty() && isNotBlank(payment.getName()) )
			expressionContext.setVariable(payment.getName(), 0.00, paymentStart, paymentEnd);
		
		if ( results.size() == 1 && isConstant(payment, results)) {
			results = expressionContext.eval(String.format("%s(%s)", ContextVariable.FRACTIONATE, payment.getExpression()), paymentStart, paymentEnd,
					Double.class);
		}
		
		for (ITimedResult<Double> result : results) {

			Date resultStart = result.getPeriod().getStart();
			Date resultEnd = result.getPeriod().getEnd();

			Double resultDouble = result.getValue();
			double resultValue = resultDouble != null ? resultDouble : 0.00;

			addResult(expressionContext, payment, resultStart, resultEnd, resultValue);
		}
		Date start = results.get(0).getPeriod().getStart();
		if ( start.after(paymentStart) && isNotBlank(payment.getName())) {
			expressionContext.setVariable(payment.getName(), 0.00, paymentStart, prev(start));
		}
		
		Date end = results.get(results.size()-1).getPeriod().getEnd();
		if ( end.before(paymentEnd) && isNotBlank(payment.getName()))
			expressionContext.setVariable(payment.getName(), 0.00, next(end), paymentEnd);
		
	}

	protected void addResult(ExpressionContext expressionContext, IContractPayment payment, Date resultStart,
			Date resultEnd, double resultValue) {
		
		addResult(
		expressionContext,  
		resultStart, 
		resultEnd, 
		resultValue, 
		payment.getName(),
		isMonthlyPayment(payment) ? MONTHLY_PAYMENTS: null 
		);
	}

	protected void addResult(ExpressionContext expressionContext, Date resultStart,
			Date resultEnd, double resultValue, String ...names ) {
		Arrays.asList(names).stream()
		.filter(n -> AonStringUtils.isNotBlank(n))
		.forEach(n -> addResult(expressionContext, n, resultStart, resultEnd, resultValue)) ;
		;
	}
	protected void addResult(ExpressionContext expressionContext, String name, Date resultStart,
			Date resultEnd, double resultValue ) {
		Date valueStart = resultStart;
		List<ITimedVariable<Number>> prevs = expressionContext.getVariables(name, resultStart,
				resultEnd);

		for (ITimedVariable<Number> prev : prevs) {
			Date prevStart = prev.getPeriod().getStart();
			Date prevEnd = prev.getPeriod().getEnd();
			try {
				Number prevValue = prev.getValue(prev.getPeriod());
				if (valueStart.compareTo(prevStart) < 0)
					expressionContext.setVariable(name, resultValue, valueStart, prev(prevStart));
				
				double value = resultValue ; 
				if (isResultVariabl(prev))
					value += prevValue.doubleValue();
				
				expressionContext.setVariable(name, value, prevStart,
						prevEnd);
				
				
				valueStart = next(prevEnd);
			} catch (Exception e) {
				//System.err.println(String.format("ERROR [%s]: %s", payment.getName(), e.getLocalizedMessage()));
			}
		}
		
		if (valueStart.compareTo(resultEnd) <= 0)
			expressionContext.setVariable(name, resultValue, valueStart, resultEnd);
	}

	private void initMonthVariables(ExpressionContext ctx) throws UndefinedVariablesException, ExpressionException {
		List<ITimedVariable<?>> monthDaysList = new ArrayList<ITimedVariable<?>>(
				ctx.getTimedVariables(NATURAL_MONTH_DAYS.getName()));

		int months = monthDaysList.size();
		
		for (ITimedVariable<?> monthDays : monthDaysList) {

			Period month = monthDays.getPeriod();
			{
				List<ITimedVariable<Object>> vars = ctx.getVariables(WORKED_DAYS, month.getStart(), month.getEnd());
				for (ITimedVariable<Object> var : vars) {
					List<ITimedResult<Double>> workedDays = ctx.eval(String.format("%s/%d", WORKED_DAYS, months),
							var.getPeriod().getStart(), var.getPeriod().getEnd(), Double.class);
					for (ITimedResult<Double> workedDay : workedDays) {
						ctx.setVariable(WORKED_DAYS, workedDay.getValue(), workedDay.getPeriod().getStart(),
								workedDay.getPeriod().getEnd());
	
					}
					// Number days = (Number) var.getValue(var.getPeriod());
					// ctx.setVariable(WORKED_DAYS, days.doubleValue() / months,
					// var.getPeriod().getStart(), var.getPeriod().getEnd());
				}

				for (ITimedVariable<Object> var : ctx.getVariables(TOTAL_WORKED_DAYS, month.getStart(), month.getEnd())) {
					List<ITimedResult<Double>> totalWorkedDays = ctx.eval(String.format("%s/%d", TOTAL_WORKED_DAYS, months),
							var.getPeriod().getStart(), var.getPeriod().getEnd(), Double.class);
					for (ITimedResult<Double> totalWorkedDay : totalWorkedDays) {
						ctx.setVariable(TOTAL_WORKED_DAYS, totalWorkedDay.getValue(), totalWorkedDay.getPeriod().getStart(),
								totalWorkedDay.getPeriod().getEnd());
	
					}
				}
			}
			
			{
				List<ITimedVariable<Object>> vars = ctx.getVariables(ACTUAL_DAYS, month.getStart(), month.getEnd());
				for (ITimedVariable<Object> var : vars) {
					List<ITimedResult<Double>> actualDays = ctx.eval(String.format("%s/%d", ACTUAL_DAYS, months),
							var.getPeriod().getStart(), var.getPeriod().getEnd(), Double.class);
					for (ITimedResult<Double> workedDay : actualDays) {
						ctx.setVariable(ACTUAL_DAYS, workedDay.getValue(), workedDay.getPeriod().getStart(),
								workedDay.getPeriod().getEnd());
	
					}
				}
			}

			// ctx.setVariable(MONTH_DAYS, days.doubleValue() * months,
			// month.getStart(), month.getEnd());
			// ctx.setVariable(PAY_DAYS, days.doubleValue() * months,
			// month.getStart(), month.getEnd());
		}
	}
	
	private void fixMonthVariables(ExpressionContext ctx) throws UndefinedVariablesException, ExpressionException {
		List<ITimedVariable<?>> monthDaysList = new ArrayList<ITimedVariable<?>>(
				ctx.getTimedVariables(NATURAL_MONTH_DAYS.getName()));

		int months = monthDaysList.size();
		
		String monthVariables [] =
		ctx.variablesSet().stream()
		.filter((String name) -> !ContextVariable.isContextVariable(name) )
		.filter((String name) -> name.startsWith("DIAS"))
		.filter((String name) -> ctx.getVariables(name).size() > 1 )
		.toArray(String[]::new);
		
		 

		for (ITimedVariable<?> monthDays : monthDaysList) {
			Period month = monthDays.getPeriod();
			for ( String monthVariable: monthVariables ) {
				List<ITimedVariable<Object>> vars = ctx.getVariables(monthVariable, month.getStart(), month.getEnd());
				for (ITimedVariable<Object> var : vars) {
					List<ITimedResult<Double>> monthResults = ctx.eval(String.format("%s/%d", monthVariable, months),
							var.getPeriod().getStart(), var.getPeriod().getEnd(), Double.class);
					for (ITimedResult<Double> monthResult : monthResults) {
						ctx.setVariable(monthVariable, monthResult.getValue(), monthResult.getPeriod().getStart(),
								monthResult.getPeriod().getEnd());

					}
				}
			}
		}
	}

	private int getMonths() {
		try {
			return getExpressionContext().eval(NATURAL_MONTH_DAYS.getName(), getStartDate(), getEndDate()).size();
		} catch ( Throwable t ) {
			return getExpressionContext().getTimedVariables(NATURAL_MONTH_DAYS.getName()).size();
		}
	}
	
	private String getMissed( Collection<IContractPayment> payments) {
		
		Integer salaryMonths [] = payments.stream().map(s -> AonDateUtils.get(s.getStartDate(), Calendar.MONTH ))
		.toArray(Integer[]::new);
		Arrays.sort(salaryMonths);
		
		String meses [] = 
			{"Enero", 
			"Febrero", 
			"Marzo", 
			"Abril", 
			"Mayo", 
			"Junio", 
			"Julio", 
			"Agosto", 
			"Septiembre", 
			"Octubre",
			"Noviembre", 
			"Diciembre"};

		Stream<Integer> months = Stream.empty();
		
		try {
			months = 
			getExpressionContext()
			.eval(NATURAL_MONTH_DAYS.getName(), getStartDate(), getEndDate())
			.stream().map(v -> AonDateUtils.get(v.getPeriod().getStart(), Calendar.MONTH));
		} catch (ExpressionException e) {
			months = 
			getExpressionContext()
			.getTimedVariables(NATURAL_MONTH_DAYS.getName())
			.stream().map(v -> AonDateUtils.get(v.getPeriod().getStart(), Calendar.MONTH));
		}
		 
		return
		months
		.filter(month -> Arrays.binarySearch(salaryMonths, month) < 0 )
		.map(month -> meses[month])
		.collect(Collectors.joining(", "))
		.replaceFirst("(^.+),([^,]+$)", "$1 y $2" )
		;
		
		
	}
	
	private boolean intercets ( IContractPayment p ) {
		return new Period(p.getStartDate(), p.getEndDate()).intersects(new Period(getStart(), getEnd()));
	}
	
	private boolean isNotProrrated(Salary salary, Salary.Payment salaryPayment) {
		int extraMonth = AonDateUtils.get(getIssueDate(), Calendar.MONTH);
		int salaryMonth = AonDateUtils.get(salary.getIssueDate(), Calendar.MONTH);
		
		Double amount = salaryPayment.getAmount();
		return
			AonNumberUtils.isNotValid(amount) 
			|| AonNumberUtils.todouble(amount)  == 0.00 
			|| extraMonth == salaryMonth ;
				
		
	}
	
	// -------------------------------------------
	//
	// -------------------------------------------

	private final class ContractPayments extends AbstractCollection<IContractPayment> {
		private Collection<IContractPayment> contractPayments ;
		private final Filter<IContractPayment> extraPaymentFilter;

		private ContractPayments(Collection<IContractPayment> contractPayments, Filter<IContractPayment> extraPaymentFilter) {
			this.contractPayments = contractPayments;
			this.extraPaymentFilter = extraPaymentFilter;
		}

		@Override
		public int size() {
			return contractPayments.size();
		}

		@Override
		public Iterator<IContractPayment> iterator() {
			return new DelegateIterator<IContractPayment>(contractPayments.iterator()) {
				@Override
				public IContractPayment next() {
					return new DelegateContractPayment(super.next()) {
						@Override
						public String getName() {
							String name = super.getName();
							if ( AonStringUtils.isNotBlank(name) ) 
								return name;
							if ( extraPaymentFilter.accept(this) ) 
								return DEFAULT_EXTRA_NAME ;
							return name;
						}
						
						@Override
						public String getExpression() {
							return String.format("/*var:%s*/%s", getOverrideVarName(), super.getExpression());
						}
					};
				}
			};
		}
	}

	public static class DateFormatException extends IllegalArgumentException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		public DateFormatException(String s) {
			super(s);
		}

	}

	public static interface AgreementExtraCallback {
		public void sqlContractExtraCalculatorContext(IContractSalaryCalculatorContext ctx) throws AonException;
	}

	private static class ExtraPaymentFilter implements FilterCollection.Filter<IContractPayment> {
		
		private Month month;
		private Calendar issueDate;
		
		public ExtraPaymentFilter(Date issueDate) {
			int month = CommonUtil.getMonth(issueDate);
			this.month = Month.getMonthByValue(month);
			this.issueDate = Calendar.getInstance();
			this.issueDate.setTime(issueDate);
		}

		@Override
		public boolean accept(IContractPayment e) {
			try {
			IExtraPayment p = (IExtraPayment) e;
			Calendar pIssueDate = parseExtraDate(p.getExtraIssueDate(), new Date());
			return  
			//p.getSalaryType() == SalaryType.EXTRA && 
			p.getMonth() == this.month && 
			pIssueDate.get(DAY_OF_MONTH) ==  issueDate.get(DAY_OF_MONTH) 
			;
			} catch ( Exception c ) {
				if ( e instanceof DelegateContractPayment ) {
					return accept(((DelegateContractPayment) e).getPayment());
				}
				return  ( e.getSalaryType() == SalaryType.EXTRA 
						|| e.getType() == PaymentType.CRA_0004  )
						&& e.getMonth() == this.month; 
			}
			
		}
		
		
	}
	
	private static boolean isResultVariabl(ITimedVariable<?> var) {
		return !( var instanceof IExpressionVariable<?>);
	}
	
	private static boolean isConstant(IPayment payment, List<ITimedResult<Double>> results) {
		
		if ( Arrays.asList(new String []{ ContextVariable.PREST_IT, ContextVariable.GUARENTEED }).contains(payment.getName()) )
			return false;
		
		List<String> VARS = Arrays.asList(new String []{ ContextVariable.GUARANTEE, ContextVariable.REGULATORY_BASE.getName()});
		
		for (ITimedResult<Double> result : results)
			if ( result.getContext().keySet().stream().anyMatch(key -> VARS.contains(key) ))
				return false;
		
		return true;
	}
	
	private static boolean isSalaryPayment(IContractPayment p) {
		return ( StringUtils.isNotBlank(p.getName())
				|| p.getType() == PaymentType.CRA_0001
				) && p.getSalaryType() == SalaryType.SALARY;		
	}

	private static boolean isMonthlyPayment(IContractPayment p) {
		return ( p.getType() == PaymentType.CRA_0001 ) 
				&& !AonStringUtils.equals(ContextVariable.PREST_IT, p.getName());		
	}
	
	private static Collection<IContractPayment>  merge ( Collection<IContractPayment> l1, Collection<IContractPayment> l2 ){
		
		if ( l1.isEmpty() )
			return l2;
		
		ArrayList<IContractPayment> merge = new ArrayList<>(l1);
		for (IContractPayment p : l2) {
			if ( !intersects(l1, p))  
				merge.add(p);
		}
		return merge;
	}

	private static boolean  intersects ( Collection<IContractPayment> payments, IContractPayment payment){
		Period period = new Period(payment.getStartDate(), payment.getEndDate());
		return payments.stream().map( p -> new Period(p.getStartDate(), p.getEndDate())).anyMatch( period::intersects );
	}
	
}