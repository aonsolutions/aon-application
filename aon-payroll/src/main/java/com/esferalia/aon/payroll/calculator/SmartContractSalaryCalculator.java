package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.add;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.MONTH;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import com.code.aon.common.AonException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementPaymentsFactory.IExtraPayment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
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
			
			if ( payment.getType() == PaymentType.CRA_0055 		//MEJORAS DE LA PRESTACIÓN ECONÓMICA POR IT 
				|| payment.getType() == PaymentType.CRA_0056 	//MEJORAS DE LA PRESTACIÓN  DISTINTAS A LAS DE IT  (maternidad, paternidad, riesgo.. y lactancia )
			) {
				return Collections.emptyList();
			} // not quote, also included in 'BASE REGULADORA'
			
			
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
		T t =  super.calculate(ctx);
		return t;
	}
	
	// IContractSalaryCalculatorContext.IListener -----------------------------
	
	// ------------------------------------------------------------------------

	@Override
	protected List<ITimedResult<Double>> fixItResults(
			IContractPayment contractPayment,
			List<ITimedResult<Double>> results, 
			List<Period> its, 
			Date start, 
			Date end, 
			ExpressionContext expressionContext) throws UnsupportedOperationException {
		
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
	protected QuoteCalculator getQuoteCalculator(IContractSalaryCalculatorContext ctx) {
		return new SmartQuoteCalculator(super.getQuoteCalculator(ctx));
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
	
	private ITimedResult<Double> quote(ITimedResult<Double> result, IContractPayment contractPayment) throws ExpressionException {
		ExpressionContext expressionContext = new ExpressionContext();
		expressionContext.putVariable(ContextVariable.ALL, result);
		if ( !AonStringUtils.isEmpty(contractPayment.getName()) )
			expressionContext.putVariable(contractPayment.getName(), result);
		expressionContext.setVariable(ContextVariable.CONTEXT, expressionContext, result.getPeriod().getStart(), result.getPeriod().getEnd());
		expressionContext.setVariable(ContextVariable.PAYMENT_VARIABLE, contractPayment, result.getPeriod().getStart(), result.getPeriod().getEnd());
		ContextFunctions.loadFunctions(expressionContext, result.getPeriod().getStart(), result.getPeriod().getEnd());
		
		Double value = 0.00;
		for ( ITimedResult<Number> quote : expressionContext.eval(contractPayment.getQuoteExpression(), result.getPeriod().getStart(), result.getPeriod().getEnd(), Number.class) )
			value += quote.getValue().doubleValue();
		
		
		return new TimedResult<Double>(value, result.getPeriod(), result.getContext());
	}
	
	// ------------------------------------------------------------------------

	private Date getStartIT(ExpressionContext expressionContext) throws UndefinedVariablesException, ExpressionException {
		
		List<ITimedResult<Date>> dates = expressionContext.eval(ContextVariable.IT_START.getName(), ctx.getStartDate(), ctx.getEndDate(), Date.class);
	
		for ( ITimedResult<Date> date : dates  ) {
			return date.getValue();
		}
		
		throw new ExpressionException();
	}
	// ------------------------------------------------------------------------
	
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
	
}