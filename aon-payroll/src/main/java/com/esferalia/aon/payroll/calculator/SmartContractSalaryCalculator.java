package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.add;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.MONTH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
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
import com.esferalia.aon.watson.util.AonUtils;

public class SmartContractSalaryCalculator<T extends ISalary> extends GenericContractSalaryCalculator<T, ISQLContractSalaryCalculatorContext> {
	
	private ISQLContractSalaryCalculatorContext ctx;
	
	
	public SmartContractSalaryCalculator() {
		super();
	}
	
	public SmartContractSalaryCalculator(ISalaryBuilder<T> salaryBuilder) {
		super(salaryBuilder);
	}




	@Override
	public T calculate(ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		this.ctx = ctx;
		T t =  super.calculate(ctx);
		return t;
	}
	
	// IContractSalaryCalculatorContext.IListener -------------------------
	
	// ------------------------------------------------------------------------

	@Override
	protected List<ITimedResult<Double>> fixItResults(
			IContractPayment contractPayment,
			List<ITimedResult<Double>> results, 
			List<Period> its, 
			Date start, 
			Date end, 
			ExpressionContext expressionContext) throws UnsupportedOperationException {
		
		System.out.println("----->" + contractPayment.getDescription() + " : " + results.get(0).getValue());

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
				&& contains(brResults, results.get(0))) {
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
		}
		

		if (results.size() == 1 && results.get(0).getContext().isEmpty() ) {
			; //return  fixConstantResult(contractPayment, results.get(0), start, end, expressionContext);
		}

		throw new UnsupportedOperationException(String.format(IT_PAY_MSG, contractPayment.getDescription(),
					contractPayment.getExpression())); 
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
		.and(SALARY_PAYMENT.PAYMENT_CONCEPT.eq(payment.getName()))
		.and(SALARY_PAYMENT.DESCRIPTION.eq(payment.getDescription()))
		.fetch();
		;
		
		List<ITimedResult<Double>> brResults = 
				new ArrayList<ITimedResult<Double>>(brPayments.size()); 
		
		for ( int i = 0; i < brPayments.size(); i++ ) {
			Record record = brPayments.get(i);
			brResults.add(new TimedResult<Double>(
					record.get(SALARY_PAYMENT.QUOTE), 
					new Period(record.get(SALARY.START_DATE), 
							record.get(SALARY.START_DATE)), 
					Collections.emptyMap()));
		}
		
		return brResults;
	}
	
	private static boolean contains ( List<ITimedResult<Double>> results, ITimedResult<Double>  result ) {
		for ( ITimedResult<Double> r: results ) 
			if ( AonUtils.equals(r.getValue(), result.getValue())) 
				return true;
		return false;
	}
	
	private Date getStartIT(ExpressionContext expressionContext) throws UndefinedVariablesException, ExpressionException {
		
		List<ITimedResult<Date>> dates = expressionContext.eval(ContextVariable.IT_START.getName(), ctx.getStartDate(), ctx.getEndDate(), Date.class);
	
		for ( ITimedResult<Date> date : dates  ) {
			return date.getValue();
		}
		
		throw new ExpressionException();
	}
	
	private double getDays(Period period, ExpressionContext expressionContext) throws UndefinedVariablesException, ExpressionException {

		List<ITimedResult<Number>> results = expressionContext.eval(ContextVariable.QUOTE_DAYS.getName(), period.getStart(), period.getEnd(), Number.class);
		
		double days = 0.00;
		for ( ITimedResult<Number> result : results  ) {
			days += result.getValue().doubleValue();
		}
		
		return days;
	}
	
}