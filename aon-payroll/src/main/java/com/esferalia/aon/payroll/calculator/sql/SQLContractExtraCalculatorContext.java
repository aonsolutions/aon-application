package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.calculator.ContextFunctions.parseExtraDate;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ACTUAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NATURAL_MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.esferalia.aon.payroll.DelegateContractPayment;
import com.esferalia.aon.payroll.calculator.CompositePayments;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.SimpleContractPayment;
import com.esferalia.aon.payroll.calculator.UndefinedContextVariablesException;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementPaymentsFactory.IExtraPayment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SQLContractExtraCalculatorContext extends SQLContractSalaryCalculatorContext {

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
	public Collection<IContractPayment> getContractPayments() throws AonException {
		
		SSRegimeType ssRegime = getSSRegime();
		
		if ( ssRegime == SSRegimeType.SELF_EMPLOYED ) {
			addSalaryContractPayments();

			Collection<IContractPayment> extraPayments = 
					new FilterCollection<IContractPayment>(
					getExtraPaymentFilter(), 
					super.getContractPayments());
			return extraPayments;
		} // TODO: This should not be necessary!!!
		
		Collection<IContractPayment> monthlyQuotedPayments= getMonthlyQuotedPayments();
		if ( monthlyQuotedPayments.size() == getMonths() ) 
			return monthlyQuotedPayments;
		
		
		addSalaryContractPayments();

		Collection<IContractPayment> extraPayments = 
				new FilterCollection<IContractPayment>(
				getExtraPaymentFilter(), 
				new CompositePayments<IContractPayment>(super.getContractPayments(),getWarnPayment(monthlyQuotedPayments)));

		return extraPayments;
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
		initMonthVariables(getExpressionContext());
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
		for ( IContractPayment p : super.getContractPayments() )
			if ( filter.accept(p)) extraPayments.add(new SimpleContractPayment(p));
		
		List<IContractPayment> monthlyQuotedPayments = new ArrayList<IContractPayment>();
		
		int contractId = getId();
		
		AON.getSalaries(new AONContext(connection),
		p -> p.getIsSalaryProperty().eq(true)
			.and(p.getContractProperty().eq(contractId))
			.and(p.getStartDateProperty().le(getEnd()))
			.and(p.getEndDateProperty().ge(getStart())))
		.forEach(salary -> salary.getPayments().forEach( salaryPayment -> 
		getSalaryPaymentOf(salaryPayment, extraPayments)
		.ifPresent( p -> monthlyQuotedPayments.add(salary2ContractPayment(salary,salaryPayment, p)))
		));
		
		
		return monthlyQuotedPayments;
	}
	
	private IContractPayment salary2ContractPayment(
			com.esferalia.aon.occam.api.model.Salary salary, 
			com.esferalia.aon.occam.api.model.Salary.Payment salaryPayment, 
			IContractPayment contractPayment) {
		DelegateContractPayment payment = new DelegateContractPayment(contractPayment) {
			
			@Override
			public Integer getId() {
				return contractPayment.getId() * -1 ; //Integer.MIN_VALUE ; //super.getId() * (-1);
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
				return Double.toString(salaryPayment.getQuote());
			}
		};
		
		;
		
		return payment;
	}
	
	private Optional<IContractPayment> getSalaryPaymentOf(com.esferalia.aon.occam.api.model.Salary.Payment salaryPayment, Collection<IContractPayment> contractPayments ) {
		for (IContractPayment contractPayment : contractPayments) {
			if ( AonStringUtils.equals(contractPayment.getDescription(), salaryPayment.getDescription()) ) {
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

			if (StringUtils.isNotBlank(p.getName()) && p.getSalaryType() == SalaryType.SALARY) {
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
	
				if (StringUtils.isNotBlank(p.getName()) && p.getSalaryType() == SalaryType.SALARY) {
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
		
		if ( results.isEmpty() )
			expressionContext.setVariable(payment.getName(), 0.00, paymentStart, paymentEnd);
		
		if ( results.size() == 1 && isConstant(payment, results))
			results = expressionContext.eval(String.format("%s(%s)", ContextVariable.FRACTIONATE, payment.getExpression()), paymentStart, paymentEnd,
					Double.class);
		
		for (ITimedResult<Double> result : results) {

			Date resultStart = result.getPeriod().getStart();
			Date resultEnd = result.getPeriod().getEnd();

			Double resultDouble = result.getValue();
			double resultValue = resultDouble != null ? resultDouble : 0.00;

			addResult(expressionContext, payment, resultStart, resultEnd, resultValue);
		}
		Date start = results.get(0).getPeriod().getStart();
		if ( start.after(paymentStart) ) {
			expressionContext.setVariable(payment.getName(), 0.00, paymentStart, prev(start));
		}
		
		Date end = results.get(results.size()-1).getPeriod().getEnd();
		if ( end.before(paymentEnd) )
			expressionContext.setVariable(payment.getName(), 0.00, next(end), paymentEnd);
		
	}

	protected void addResult(ExpressionContext expressionContext, IContractPayment payment, Date resultStart,
			Date resultEnd, double resultValue) {
		Date valueStart = resultStart;
		List<ITimedVariable<Number>> prevs = expressionContext.getVariables(payment.getName(), resultStart,
				resultEnd);

		for (ITimedVariable<Number> prev : prevs) {
			Date prevStart = prev.getPeriod().getStart();
			Date prevEnd = prev.getPeriod().getEnd();
			try {
				Number prevValue = prev.getValue(prev.getPeriod());
				if (valueStart.compareTo(prevStart) < 0)
					expressionContext.setVariable(payment.getName(), resultValue, valueStart, prev(prevStart));
				
				double value = resultValue ; 
				if (isResultVariabl(prev))
					value += prevValue.doubleValue();
				
				expressionContext.setVariable(payment.getName(), value, prevStart,
						prevEnd);
				
				
				valueStart = next(prevEnd);
			} catch (Exception e) {
				//System.err.println(String.format("ERROR [%s]: %s", payment.getName(), e.getLocalizedMessage()));
			}
		}
		
		if (valueStart.compareTo(resultEnd) <= 0)
			expressionContext.setVariable(payment.getName(), resultValue, valueStart, resultEnd);
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

		return
		getExpressionContext()
		.getTimedVariables(NATURAL_MONTH_DAYS.getName())
		.stream().map(v -> AonDateUtils.get(v.getPeriod().getStart(), Calendar.MONTH))
		.filter(month -> Arrays.binarySearch(salaryMonths, month) < 0 )
		.map(month -> meses[month])
		.collect(Collectors.joining(", "))
		.replaceFirst("(^.+),([^,]+$)", "$1 y $2" )
		;
		
		
	}
	
	
	// -------------------------------------------
	//
	// -------------------------------------------

	public static class DateFormatException extends IllegalArgumentException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
			return  p.getSalaryType() == SalaryType.EXTRA 
					&& p.getMonth() == this.month 
					&& pIssueDate.get(DAY_OF_MONTH) ==  issueDate.get(DAY_OF_MONTH) 
					;
			} catch ( Exception c ) {
				return  e.getSalaryType() == SalaryType.EXTRA 
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

}