package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.payroll.calculator.ContextFunctions.parseExtraDate;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARENTEED;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.salary.enumeration.PaymentType.CRA_0001;
import static com.esferalia.aon.watson.util.AonDateUtils.add;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.net.whois.WhoisClient;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.AonException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.DelegateContractPayment;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractLeaveLoader.Leave;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementPaymentsFactory.IExtraPayment;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLExtraSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
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
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class SmartContractSalaryCalculator<T extends ISalary> extends GenericContractSalaryCalculator<T, ISQLContractSalaryCalculatorContext> {
	
	private static class PaymentResult {
		private IContractPayment payment;
		private Date start;
		private Date end;
		private Double value;
		
		public PaymentResult(IContractPayment payment, ITimedResult<Double> result) {
			super();
			this.payment = payment;
			this.value = result.getValue();
			this.end = result.getPeriod().getEnd();
			this.start = result.getPeriod().getStart();
		}

		public PaymentResult(IContractPayment payment, Date start, Date end, Double value) {
			super();
			this.payment = payment;
			this.start = start;
			this.end = end;
			this.value = value;
		}

		
	}
	
	private static class ALLContractPayment extends DelegateContractPayment{
		
		private ALLContractPayment(IContractPayment contractPayment) {
			super(contractPayment);
		}
		
		@Override
		public String getQuoteExpression() {
			return ContextVariable.ALL;
		
		}
		
	}
	
	private static class SalaryExtraPayment extends DelegateContractPayment{
		
		private double amount;
		
		private SalaryExtraPayment(IContractPayment contractPayment, double amount) {
			super(contractPayment);
			this.amount = amount;
		}
		
		@Override
		public SalaryType getSalaryType() {
			return SalaryType.SALARY;
		}
		
		
		@Override
		public String getIrpfExpression() {
			return Double.toString(amount);
		}
	}

	private static class PRORATIONContractPayment extends DelegateContractPayment{
		
		private PRORATIONContractPayment(IContractPayment contractPayment) {
			super(contractPayment);
		}
		
		@Override
		public String getQuoteExpression() {
			return String.format("%s()",ContextVariable.PRORATION);
		
		}
		
	}

	private static class GUARENTEEDContractPayment extends DelegateContractPayment{
		
		private GUARENTEEDContractPayment(IContractPayment contractPayment) {
			super(contractPayment);
		}
		
		@Override
		public PaymentType getType() {
			return PaymentType.CRA_0055;
		}
		
		@Override
		public String getQuoteExpression() {
			return "0.00";
		
		}
		
	}
	
	private static class DoubleReturnException extends RuntimeException{
		private double number;
		
		public DoubleReturnException(double number) {
			this.number = number;
		}
		
		public double getNumber() {
			return number;
		}
	}
	
	private static class SmartTaxCalculator extends TaxCalculator {
		
		private TaxCalculator delegate; 
		private ISQLContractSalaryCalculatorContext ctx;
		
		public SmartTaxCalculator(TaxCalculator delegate, ISQLContractSalaryCalculatorContext ctx) {
			super();
			this.ctx = ctx;
			this.delegate = delegate;
		}

		public double getIrpfBase() {
			return delegate.getIrpfBase();
		}

		public double getRenumeration() {
			return delegate.getRenumeration();
		}

		public double getTotalPayment() {
			return delegate.getTotalPayment();
		}

		public double getInKindIrpfBase() {
			return delegate.getInKindIrpfBase();
		}

		public double getMoneyIrpfBase() {
			return delegate.getMoneyIrpfBase();
		}

		public double tax(IContractPayment payment, Date start, Date end, Date issueDate, double amount)
				throws AonException {
			try {
				if ( payment.getType() == PaymentType.CRA_0004 
					&& payment.getMonth() == getMonth(issueDate) 
					&& payment.getSalaryType() == ctx.getSalaryType() 
					&& ctx.getSalaryType() == SalaryType.SALARY ) {
					amount = calculateExtra(ctx, payment, issueDate);
					payment = new SalaryExtraPayment(payment, amount);
					double tax = delegate.tax(payment, start, end, issueDate, amount );
					throw new YesExtraException(tax);
				}
				
				return delegate.tax(payment, start, end, issueDate, amount);
			
			} catch (ExtraException e) {
				return taxExtra(payment, start, end, issueDate, amount);
			}
		}
		
		private double taxExtra(IContractPayment payment, Date start, Date end, Date issueDate, double amount) 
				throws AonException {
			try {
				IExtraPayment extraPayment = getExtraPayment(payment);
				Date extraIssueDate = parseExtraDate(extraPayment.getExtraIssueDate(), issueDate).getTime();
				if ( !extraIssueDate.equals(issueDate) )
					throw new ExtraException();
				
				if ( !extraEmited(extraIssueDate, payment, ctx)) {
					amount = calculateExtra(ctx, extraPayment, extraIssueDate);
					payment = new SalaryExtraPayment(payment, amount);
					double tax = delegate.tax(payment, start, end, extraIssueDate, amount );
					throw new YesExtraException(tax);
				}
				
			} catch ( ClassCastException e ) {
			} catch ( NullPointerException e ) {
			} 
			throw new ExtraException();
		}
		
		private static IExtraPayment getExtraPayment(IPayment payment) {
			while ( payment instanceof IHasPayment<?> )
				payment = ((IHasPayment<?>) payment).getPayment();
			return ( IExtraPayment) payment;
		}

	}

	private class SmartQuoteCalculator extends QuoteCalculator {
		
		private QuoteCalculator delegate;
		
		private SmartQuoteCalculator(QuoteCalculator calculator) {
			this.delegate = calculator;
		}
		
		public Double getItBase() throws AonException {
			return delegate.getItBase();
		}

		public Double getCgcBase() throws AonException {
			return delegate.getCgcBase();
		}

		public Double getRawCgcBase() {
			return delegate.getRawCgcBase();
		}

		public Double getRawCgpBase() {
			return delegate.getRawCgpBase();
		}

		public Double getCgpBase() throws AonException {
			return delegate.getCgpBase();
		}

		public Double getProExtBase() throws AonException {
			return delegate.getProExtBase();
		}

		public Double getStructuralBase() throws AonException {
			return delegate.getStructuralBase();
		}

		public Double getNonStructuralBase() throws AonException {
			return delegate.getNonStructuralBase();
		}

		public Double getEreBase() throws AonException {
			return delegate.getEreBase();
		}

		public Double getMaternityBase() throws AonException {
			return delegate.getMaternityBase();
		}

		public Double getDirectPayBase() throws AonException {
			return delegate.getDirectPayBase();
		}

		public List<ITimedResult<Double>> quote(IContractPayment payment, Date start, Date end, double amount)
				throws AonException {
			
			
			PaymentType type =  getPaymentType(payment); //payment.getType();
			
			if ( type == null ) {
				return delegate.quote(payment, start, end, amount);
			}

			ExpressionScope scope =  payment.getScope();
			
			if ( scope == ExpressionScope.APPLICATION ) {
				return delegate.quote(payment, start, end, amount);
			}

			if ( !type.isBBCCIncluded() 
				&& type.isBBCCExcluded() ) {
				return Collections.emptyList();
			}
			
			if ( AonStringUtils.equals(GUARENTEED, payment.getName())) {
				return delegate.quote(new GUARENTEEDContractPayment(payment), start, end, amount);
			}

			if ( type == PaymentType.CRA_0033						// TODO: PLANES PENgit statusSIONES Y SIST. ALTERNATIVOS 					
				|| type == PaymentType.CRA_0000 					// TODO: This must be the only one check 
				|| ContextVariable.ERE.getName().equals(payment.getName()) 
				|| ContextVariable.PREST_IT.equals(payment.getName()) 
				|| ContextVariable.MATERNITY.getName().equals(payment.getName())
				|| ContextVariable.DIRECT_PAY.getName().equals(payment.getName())) {
					return delegate.quote(payment, start, end, amount);
			}


			if (payment.getMonth() != null 
				&&( type == PaymentType.CRA_0004 
				|| type == PaymentType.CRA_0005)) {
				return delegate.quote(new PRORATIONContractPayment(payment), start, end, amount);
			}
			
			if ( type.isBBCCIncluded() 
				&& !type.isBBCCExcluded() ) {
				return ( amount == 0.00 ) ? 
				Collections.emptyList()  
				: delegate.quote(new ALLContractPayment(payment), start, end, amount);
			}

			return delegate.quote(payment, start, end, amount);
		}
		
		public Double qu0te(IContractPayment payment, Date start, Date end, double amount) throws AonException {
			return delegate.qu0te(payment, start, end, amount);
		}

	}
	
	
	
	private List<PaymentResult> gtzdos;
	private ISQLContractSalaryCalculatorContext ctx;
	
	
	
	public SmartContractSalaryCalculator() {
		super();
		gtzdos = new ArrayList<PaymentResult>();
	}
	
	public SmartContractSalaryCalculator(ISalaryBuilder<T> salaryBuilder) {
		super(salaryBuilder);
		gtzdos = new ArrayList<PaymentResult>();
	}

	@Override
	public T calculate(ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		this.ctx = ctx;
		gtzdos.clear();

		initPaymentType(ctx);
		
		T t =  super.calculate(ctx);
		return t;
	}
	
	// IContractSalaryCalculatorContext.IListener -----------------------------
	
	// ------------------------------------------------------------------------

	@Override
	protected PaymentType getPaymentType(IContractPayment payment) {
		
		if ( payment.getType()  != null 
		&& payment.getType() != CRA_0001 )
			return payment.getType();
		
		if ( payment.getConceptId() != null ) 
			return payment.getType();
		
		if ( AonStringUtils.isBlank(payment.getDescription()) )
			return payment.getType();

		String description = normalize(payment.getDescription());
		PaymentType type = PAYMENTS_DESCRIPTIONS.get(description);
		if ( type != null )
			return type;
		
		for ( Entry<String,PaymentType> entry : PAYMENTS_DESCRIPTIONS.entrySet() )
			if ( AonStringUtils.getLevenshteinDistance(entry.getKey(), description, 2) != -1 )
				return entry.getValue();
		
		return CRA_0001;
		
	}

	@Override
	protected List<ITimedResult<Double>> fixConstantResult(IContractPayment contractPayment,
			ITimedResult<Double> result, Date start, Date end, ExpressionContext expressionContext)
			throws UnsupportedOperationException, UndefinedVariablesException {
		
		if ( PaymentType.CRA_0055 == contractPayment.getType() ) {
			List<Period> its = expressionContext.getPeriods(ContextVariable.LEAVE_DAYS);
			wait4PREST_IT(its, expressionContext);
			try {
				double gtzdo = result.getValue();
				List<ITimedResult<Double>> results = Collections.singletonList(result);
				return fixGtzdo(results , its, expressionContext, gtzdo);
			} catch (ExpressionException e) {
			}
		}
		
		return super.fixConstantResult(contractPayment, result, start, end, expressionContext);
	}

	@Override
	protected List<ITimedResult<Double>> fixItResults(
			IContractPayment contractPayment,
			List<ITimedResult<Double>> results, 
			List<Period> its, 
			Date start, 
			Date end, 
			ExpressionContext expressionContext) throws UnsupportedOperationException, UndefinedVariablesException {
		
		if (results.size() == 1 
				&& ( contractPayment.getType() == PaymentType.CRA_0002 
				|| contractPayment.getType() == PaymentType.CRA_0003 
				|| results.get(0).getContext().containsKey(ContextVariable.GROSS)
				|| results.get(0).getContext().containsKey(ContextVariable.LIQUID)))
				return  shareExtraITResults(results.get(0), its);
		
		try {
			Date startIt = getStartIT(expressionContext);
			List<ITimedResult<Double>> brResults = getPaymentBR(startIt, contractPayment);
			
			if ( results.size() == 1
				&& ( contains(brResults, results.get(0))
					|| contains(brResults, quote(results.get(0), contractPayment)))
					) {
				// It's a constant that already is included at BR, 
				// so we'll subtract the proportional part of IT.
				
				return subtractITPart(results, its, expressionContext);
			} else if (results.size() == 1 
					&& results.get(0).getContext().isEmpty()  
					&& notOnly4ThisMonth(startIt, contractPayment)
					) {
				
				// It's a constant that already is included at BR, 
				// so we'll subtract the proportional part of IT.

				return subtractITPart(results, its, expressionContext);
			} else if (results.size() == 1
				&& isWholeMonth(results.get(0))	
				&& contractPayment.getScope() == ExpressionScope.AGREEMENT
				&& allAgreementConstants(results.get(0).getContext()) ) {
				return subtractITPart(results, its, expressionContext);
			}
			
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		if (results.size() == 1 && results.get(0).getContext().isEmpty() ) {
			;
			//return  fixConstantResult(contractPayment, results.get(0), start, end, expressionContext);
		}

		throw new UnsupportedOperationException(String.format(IT_PAY_MSG, contractPayment.getDescription(),
					contractPayment.getExpression())); 
	}
	
	@Override
	protected List<ITimedResult<Double>> fixConstantAgreementResult(IContractPayment contractPayment,
			ITimedResult<Double> result, Date start, Date end, ExpressionContext expressionContext)
			throws UnsupportedOperationException, UndefinedVariablesException {
		Period period = result.getPeriod();
		long days = getDays(period);
		
		double monthDays = AonDateUtils.get(getLastDayOfMonth(period.getEnd()), Calendar.DAY_OF_MONTH);
		try {
			monthDays = expressionContext.eval(ContextVariable.MONTH_DAYS.getName(), period.getStart(), period.getEnd(), Number.class).get(0).getValue().doubleValue();
		} catch ( Throwable t ) {
		}
		
		
		double value = result.getValue() * days / monthDays;
		
		ITimedResult<Double> fixed = 
				new TimedResult<Double>(value, period, result.getContext());

		return Collections.singletonList(fixed);
	}

	private List<ITimedResult<Double>> subtractITPart(List<ITimedResult<Double>> results, List<Period> its,
			ExpressionContext expressionContext) throws UndefinedVariablesException, ExpressionException {
		Period period = results.get(0).getPeriod();
		double days = getDays(period, expressionContext);
		Double value = results.get(0).getValue();
		Map<String,ITimedVariable<?>> context = results.get(0).getContext();
		
		List<Period> actives = Period.sub(period, its);
		List<ITimedResult<Double>> fixed = 
				new ArrayList<ITimedResult<Double>>(actives.size());
		
		for ( Period active : actives  ) {
			double activeDays = getDays(active, expressionContext );
			Double activeValue = value / days * activeDays; 
			fixed.add( new TimedResult<Double>(activeValue, active, context));
		}
		return fixed;
	}
	
	@Override
	protected List<ITimedResult<Double>> fixExtraResults(IContractPayment contractPayment,
			List<ITimedResult<Double>> results, Date start, Date end, ExpressionContext expressionContext) {
		// TODO Auto-generated method stub
		List<ITimedResult<Double>> fixed  = new ArrayList<ITimedResult<Double>>(); 
		for ( ITimedResult<Double> result : results ) {
			Period period = result.getPeriod();
			Double value = result.getValue(result.getPeriod());
			
			try {
				double cgcBaseMin = getCgcBaseMin(expressionContext, period);
				if ( contractPayment.getSalaryType() == SalaryType.SALARY
					&& contractPayment.getMonth() != null )
					fixed.add( result );
				else if ( value == null || value == 0.00 || cgcBaseMin <= 0.00 || value < cgcBaseMin )
					fixed.add( result );
				else
					fixed.add( new TimedResult<Double>(value/12, period, result.getContext())); // TODO: 12?
			} catch (ExpressionException e) {
				fixed.add( result );
			}
			
		}
		
		return fixed; //super.fixExtraResults(contractPayment, results, start, end, expressionContext);
	}

	@Override
	protected List<ITimedResult<Double>> fixGuaranteedResults(IContractPayment contractPayment,
			List<ITimedResult<Double>> results, List<Period> its, Date start, Date end,
			ExpressionContext expressionContext) throws UnsupportedOperationException, UndefinedVariablesException {

		wait4PREST_IT(its, expressionContext);

		try {
			 ;
			ISQLContractSalaryCalculatorContext noItContractSalaryCalculatorContext = 
					ctx.getNoItContractSalaryCalculatorContext();
			double gtzdo = 0.00;
			try {
				new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()){
					@Override
					protected List<ITimedResult<Double>> fixGuaranteedResults(IContractPayment _contractPayment,
							List<ITimedResult<Double>> _results, List<Period> _its, Date _start, Date _end,
							ExpressionContext _expressionContext) throws UnsupportedOperationException, UndefinedVariablesException {
						
						if ( _contractPayment.getId().equals(contractPayment.getId() ))
							throw new  DoubleReturnException( _results.stream().collect(Collectors.summingDouble(r->r.getValue())));
						
						return super.fixGuaranteedResults(_contractPayment, _results, _its, _start, _end, _expressionContext);
					}
				}.calculate(noItContractSalaryCalculatorContext)
				;
			} catch ( DoubleReturnException e ) {
				gtzdo = e.getNumber();
			}
			
			
			return fixGtzdo(results, its, expressionContext, gtzdo);
			
		} catch ( ClassCastException | ExpressionException | SalaryException e ) {
			
			return super.fixGuaranteedResults(contractPayment, results, its, start, end, expressionContext);
		}
	}
	
	@Override
	protected List<ITimedResult<Double>> fixConstantAgreementGuaranteed(IContractPayment contractPayment,
			ITimedResult<Double> result, List<Period> its, Date start, Date end,
			ExpressionContext expressionContext) throws UnsupportedOperationException, UndefinedVariablesException {
		Double totalPayment = expressionContext.getVariable(ContextVariable.TOTAL_PAYMENT, start, end, Double.class);
		if ( totalPayment == null )
			throw new UndefinedContextVariablesException(ContextVariable.TOTAL_PAYMENT);
		try {
			
			List<ITimedResult<Double>> results = new ArrayList<ITimedResult<Double>>();
			
			double total = result.getValue() - totalPayment;
			long itDays = its.stream().collect(Collectors.summingLong(it -> it.daysStream().count()));
			double byDay = total / itDays;
			
			for ( Period it : its ) {
				double guaranteed = byDay * it.daysStream().count();
				double prestIt = expressionContext.getVariable(PREST_IT, it.getStart(), it.getEnd(), Double.class);
				results.add(new TimedResult<Double>(guaranteed-prestIt, it, result.getContext()));
			}
			
			
			return results;
			
		} catch (Exception e) {
			return super.fixConstantAgreementGuaranteed(contractPayment, result, its, start, end, expressionContext);
		}
	}

	protected List<ITimedResult<Double>> fixGtzdo(List<ITimedResult<Double>> results, List<Period> its,
			ExpressionContext expressionContext, double gtzdo) throws ExpressionException {
		if ( gtzdo <= 0.00 ) 
			return Collections.emptyList();
		
		double cgcBaseMin = getCgcBaseMin(expressionContext, ctx.getStartDate(), ctx.getEndDate());
		if ( gtzdo >= cgcBaseMin ) {
			double totalPayment = getTotalPayment(expressionContext, ctx.getStartDate(), ctx.getEndDate());
			gtzdo -= totalPayment;
		}
		if ( gtzdo <= 0.00 ) 
			return Collections.emptyList();
			
		double gtzdo4Day = gtzdo / getDays(its)  ;
		
		Map<String, ITimedVariable<?>>  finalContext = new HashMap<String, ITimedVariable<?>>(); 
		results.forEach(r->finalContext.putAll(r.getContext()));

		return its.stream().map(it -> new  TimedResult<Double>(gtzdo4Day * getDays(it), it, finalContext)).collect(Collectors.toList());
	}

	
	@Override
	protected List<ITimedResult<Double>> fixStrikeResults(
			IContractPayment contractPayment,
			List<ITimedResult<Double>> results, 
			List<Period> strikes, 
			Date start, 
			Date end,
			ExpressionContext expressionContext) throws UnsupportedOperationException {
		return  shareExtraITResults(results.get(0), strikes);
	}
	
	@Override
	protected TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
		return new SmartTaxCalculator(super.getTaxCalculator(ctx), this.ctx );
	}
	
	@Override
	protected QuoteCalculator getQuoteCalculator(IContractSalaryCalculatorContext ctx) {
		return new SmartQuoteCalculator(super.getQuoteCalculator(ctx));
	}
	
	@Override
	protected double resolveBonus(Date bonusStart, Date bonusEnd, IContractBonus contractBonus,
			ExpressionContext expressionContext) throws ExpressionException, UndefinedVariablesException {
		double bonus = super.resolveBonus(bonusStart, bonusEnd, contractBonus, expressionContext);
		
		boolean sectioned =  
		expressionContext.getPeriods(ContextVariable.CGC_BASE)
		.stream().filter(p -> p.getStart().compareTo(bonusStart) == 0)
		.count() > 0;
		if ( !sectioned )
			ContextFunctions.section(expressionContext, AonDateUtils.add(bonusStart, Calendar.DAY_OF_MONTH,-1));
		
		return bonus;
	}
	
	// ------------------------------------------------------------------------
	
	private List<ITimedResult<Double>> getPaymentBR(Date date, IContractPayment payment) {
		int contractId = ctx.getId();
		
		
		Date brEndDate = getLastDayOfMonth(add(date, MONTH, -1));
		java.sql.Date brEndSqlDate = new java.sql.Date(brEndDate.getTime());

		DSLContext dslContext = new AONContext(ctx.getConnection()).getDslContext();
		
		Result<Record> brPayments =
		dslContext
		.select()
		.from(SALARY)
		.join(SALARY_PAYMENT)
		.on(SALARY.ID.eq(SALARY_PAYMENT.SALARY))
		.where(SALARY.CONTRACT.eq(contractId))
		.and(SALARY.END_DATE.eq(brEndSqlDate))
		.and(SALARY.TYPE.eq((byte) SalaryType.SALARY.ordinal()))
		.and(AonStringUtils.isEmpty(payment.getName()) ?
			SALARY_PAYMENT.PAYMENT_CONCEPT.isNull() :
			SALARY_PAYMENT.PAYMENT_CONCEPT.eq(payment.getName()))
		.and(SALARY_PAYMENT.DESCRIPTION.eq(payment.getDescription()))
		.fetch();
		;
		
		List<ITimedResult<Double>> brResults = 
				new ArrayList<ITimedResult<Double>>(brPayments.size()); 
		
		for ( int i = 0; i < brPayments.size(); i++ ) {
			Record record = brPayments.get(i);
			Double quote = record.get(SALARY_PAYMENT.QUOTE);
			brResults.add(new TimedResult<Double>(
					record.get(SALARY_PAYMENT.QUOTE), 
					new Period(record.get(SALARY.START_DATE), 
							record.get(SALARY.START_DATE)), 
					Collections.emptyMap()));
		}
		
		return brResults;
	}
	
	private static Double calculateExtra(ISQLContractSalaryCalculatorContext ctx, IExtraPayment extraPayment, Date issueDate) throws AonException {
		SQLExtraSalaryCalculatorContext extraCtx = null;
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), ctx.getId());
			
			extraCtx =
			new SQLExtraSalaryCalculatorContext(
					ctx.getConnection()
					, extraPayment.getExtraId() 				//extra
					, AonDateUtils.get(issueDate, YEAR)			//year
					, issueDate									//chargeDate
					, criteria);
			extraCtx.next();
			return new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()) {
					@Override
					protected TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
						return TaxCalculator.getTaxCalculator(ctx);
					}
			}
			.calculate(extraCtx).getSalary().getTotalPayment();
		} catch (Exception e) {
			throw new AonException(e);
		} 
		finally {
			if ( extraCtx != null) {
				try {
					extraCtx.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	private static Double calculateExtra(ISQLContractSalaryCalculatorContext ctx, IContractPayment contractPayment, Date endDate) throws AonException {
		SQLContractSalaryCalculatorContext extraCtx = null;
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), ctx.getId());
			Date startDate = add(endDate, Calendar.YEAR, -1);
			startDate = add(startDate, Calendar.DATE, 1);
			extraCtx =
			new SQLContractSalaryCalculatorContext(ctx.getConnection(), startDate, endDate, endDate, criteria);
			extraCtx.next();
			return new SmartContractSalaryCalculator<Salary>(new SalaryBuilder() {
				private double extra = 0.00;
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					if ( contractPayment.getId().equals(((IContractPayment)payment).getId()) ) 
						extra += quote;
				}
				@Override
				public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					addPayment(0.00, quote, tax, null, startDate, endDate, payment, context);
				}
				
				@Override
				public Salary getSalary() {
					salary = super.getSalary();
					salary.setTotalPayment(extra);
					return salary;
				}
			}){
				@Override
				protected TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
					return TaxCalculator.getTaxCalculator(ctx);
				}
			}.calculate(extraCtx).getTotalPayment();
			
		} catch (Exception e) {
			throw new AonException(e);
		} 
		finally {
			if ( extraCtx != null) {
				try {
					extraCtx.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private static boolean extraEmited(Date issueDate, IContractPayment payment, ISQLContractSalaryCalculatorContext ctx) {
		
		int contractId = ctx.getId();
		
		DSLContext dslContext = new AONContext(ctx.getConnection()).getDslContext();
		
		return dslContext.fetchCount(
		dslContext
		.select(SALARY.ID)
		.from(SALARY)
		.innerJoin(SALARY_PAYMENT).onKey()
		.where(SALARY.CONTRACT.eq(contractId))
		.and(SALARY.TYPE.eq((byte) SalaryType.EXTRA.ordinal()))
		.and(SALARY_PAYMENT.DESCRIPTION.eq(payment.getDescription())) 			//TODO: sounds like.
		.and(SALARY.ISSUE_DATE.eq(new java.sql.Date(issueDate.getTime())))) > 0;
		
	}

	private ITimedResult<Double> quote(ITimedResult<Double> result, IContractPayment contractPayment) throws ExpressionException {
		ExpressionContext expressionContext = new ExpressionContext();
		expressionContext.putVariable(ContextVariable.ALL, result);
		if ( !AonStringUtils.isEmpty(contractPayment.getName()) )
			expressionContext.putVariable(contractPayment.getName(), result);
		expressionContext.setVariable(ContextVariable.CONTEXT, expressionContext, result.getPeriod().getStart(), result.getPeriod().getEnd());
		expressionContext.setVariable(ContextVariable.PAYMENT_VARIABLE, contractPayment, result.getPeriod().getStart(), result.getPeriod().getEnd());
		ContextFunctions.loadFunctions(expressionContext, result.getPeriod().getStart(), result.getPeriod().getEnd());
		
		String quoteExpression = contractPayment.getQuoteExpression();
		if ( AonStringUtils.isBlank(quoteExpression)) 
			quoteExpression = ContextVariable.ALL;
		
		Double value = 0.00;
		for ( ITimedResult<Number> quote : expressionContext.eval(quoteExpression, result.getPeriod().getStart(), result.getPeriod().getEnd(), Number.class) )
			value += quote.getValue().doubleValue();
		
		
		return new TimedResult<Double>(value, result.getPeriod(), result.getContext());
	}
	

	private Date getStartIT(ExpressionContext expressionContext) throws UndefinedVariablesException, ExpressionException {
		
		List<ITimedResult<Date>> dates = expressionContext.eval(ContextVariable.IT_START.getName(), ctx.getStartDate(), ctx.getEndDate(), Date.class);
	
		for ( ITimedResult<Date> date : dates  ) {
			return date.getValue();
		}
		
		throw new ExpressionException();
	}
	
	
	protected void wait4PREST_IT(List<Period> its, ExpressionContext expressionContext)
			throws UndefinedVariablesException {
		if (its.isEmpty())
			throw new UndefinedContextVariablesException(ContextVariable.LEAVE_DAYS);
		
		Double totalPayment = expressionContext.getVariable(ContextVariable.TOTAL_PAYMENT, ctx.getStartDate(), ctx.getEndDate(), Double.class);

		if (totalPayment == null)
			throw new UndefinedTotalPaymentException();
	}
	
	protected double getCgcBaseMin(ExpressionContext expressionContext, Period period) throws ExpressionException {
		double cgcBaseMin = expressionContext.eval(ContextVariable.CGC_BASE_MIN.getName(), period.getStart(), period.getEnd()).stream()
		.map(v->v.getValue(v.getPeriod())).filter(v -> v != null && v instanceof Number)
		.collect(Collectors.summingDouble(v -> ((Number)v).doubleValue()));
		return cgcBaseMin;
	}
	
	protected double getTotalPayment(ExpressionContext expressionContext, Period period) throws ExpressionException {
		double cgcBaseMin = expressionContext.eval(ContextVariable.TOTAL_PAYMENT.getName(), period.getStart(), period.getEnd()).stream()
		.map(v->v.getValue(v.getPeriod())).filter(v -> v != null && v instanceof Number)
		.collect(Collectors.summingDouble(v -> ((Number)v).doubleValue()));
		return cgcBaseMin;
	}

	protected double getTotalPayment(ExpressionContext expressionContext, Date start, Date end) throws ExpressionException {
		return getTotalPayment(expressionContext, new Period(start,end));
	}

	protected double getCgcBaseMin(ExpressionContext expressionContext, Date start, Date end) throws ExpressionException {
		return getCgcBaseMin(expressionContext, new Period(start,end));
	}
	
	// ------------------------------------------------------------------------
	
	private static long getDays(Period period){
		return period.daysStream().count();
	}

	private static long getDays(List<Period> periods){
		return periods.stream().collect(Collectors.summingLong(p->p.daysStream().count()));
	}

	private static double getDays(Period period, ExpressionContext expressionContext) throws UndefinedVariablesException, ExpressionException {

		List<ITimedResult<Number>> results = expressionContext.eval(ContextVariable.QUOTE_DAYS.getName(), period.getStart(), period.getEnd(), Number.class);
		
		double days = 0.00;
		for ( ITimedResult<Number> result : results  ) {
			days += result.getValue().doubleValue();
		}
		
		return days;
	}

	
	private static boolean contains ( List<ITimedResult<Double>> results, ITimedResult<Double>  result ) {
		for ( ITimedResult<Double> r: results ) { 
			if ( AonUtils.equals(r.getValue(), result.getValue())) 
				return true;
			
			Double d1 = r.getValue();
			Double d2 = result.getValue();
			
			return Math.abs(d2-d1) < 0.001;
		}
		return false;
		
	}
	
	private static boolean isWholeMonth ( ITimedResult<?> result ) {
		Period period = result.getPeriod();
		int lastDayOfMonth =AonDateUtils.get(AonDateUtils.getLastDayOfMonth(period.getEnd()), Calendar.DAY_OF_MONTH);
		
		return ( AonDateUtils.get(period.getStart(), Calendar.DAY_OF_MONTH) == 1) 
				&& ( AonDateUtils.get(period.getEnd(), Calendar.DAY_OF_MONTH) == lastDayOfMonth);
	}

	private static boolean notOnly4ThisMonth ( Date date, IContractPayment payment) {
		Date firstDayOfMonth = AonDateUtils.getFirstDayOfMonth(date);
		Date endDayOfMonth = AonDateUtils.getFirstDayOfMonth(date);
		
		if ( Period.compare(payment.getStartDate(), firstDayOfMonth) < 0 
				&& Period.compare(payment.getEndDate(), endDayOfMonth) > 0 )
			return true;
				
		return false;
	}
	
	private static Map<String, PaymentType> PAYMENTS_DESCRIPTIONS = new HashMap<String, PaymentType>();
	private static Set<String> PAYMENTS_DATABASES = new HashSet<String>();
	
	private static void initPaymentType(ISQLContractSalaryCalculatorContext ctx) {
		
		Connection connection = ctx.getConnection();
		try {
			if ( !PAYMENTS_DATABASES.add(connection.getCatalog()) )
				return;
		} catch (SQLException e1) {
			return;
		}
		
		DSLContext dslContext = new AONContext(ctx.getConnection()).getDslContext();
		
		dslContext
		.select(
		AGREEMENT_PAYMENT.DESCRIPTION
		,DSL.ifnull(AGREEMENT_PAYMENT.TYPE, PAYMENT_CONCEPT.TYPE)
		)
		.from(AGREEMENT_PAYMENT)
		.leftJoin(PAYMENT_CONCEPT).onKey()
		.where(AGREEMENT_PAYMENT.DESCRIPTION.isNotNull())
		.groupBy(AGREEMENT_PAYMENT.DESCRIPTION)
		.fetchLazy()
		.forEach(
		(r) -> {
			try {
				PaymentType paymentType = PaymentType.values()[r.value2()];
				if ( paymentType == CRA_0001 )
					return;
				
				String description = normalize(r.value1());
				
				
				PAYMENTS_DESCRIPTIONS.put(description, paymentType);
			} catch ( Exception e ) {
			}
		}
		);

		dslContext
		.select(
		CONTRACT_PAYMENT.DESCRIPTION
		,DSL.ifnull(CONTRACT_PAYMENT.TYPE, PAYMENT_CONCEPT.TYPE)
		)
		.from(CONTRACT_PAYMENT)
		.leftJoin(PAYMENT_CONCEPT).onKey()
		.where(CONTRACT_PAYMENT.DESCRIPTION.isNotNull())
		.groupBy(CONTRACT_PAYMENT.DESCRIPTION)
		.fetchLazy()
		.forEach(
		(r) -> {
			try {
				
				PaymentType paymentType = PaymentType.values()[r.value2()];
				if ( paymentType == CRA_0001 )
					return;
				
				String description = normalize(r.value1());
				
				PAYMENTS_DESCRIPTIONS.put(description, paymentType);
			} catch ( Exception e ) {
			}
		}
		);
	}

	
	private static String normalize(String description) {
		
		return description
		.toUpperCase()
		.replaceAll("\\s","")
		.replaceAll("\\[([^\\]])*\\]","")
		;
	}
	
}