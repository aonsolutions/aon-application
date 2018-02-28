package com.esferalia.aon.payroll.calculator;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedResult;

public class ContractSalaryCalculator4Dummies<T extends ISalary> extends ContractSalaryCalculator<T> implements IContractSalaryCalculatorContext.IListener{
	
	private IContractSalaryCalculatorContext.IListener listener;
	
	@Override
	public T calculate(ISalaryCalculatorContext ctx) throws SalaryException {
		listener = ((IContractSalaryCalculatorContext) ctx).getListener();
		
		((IContractSalaryCalculatorContext) ctx).setListener(this);
		T t =  super.calculate(ctx);
		((IContractSalaryCalculatorContext) ctx).setListener(listener);
		
		return t;
	}
	
	// IContractSalaryCalculatorContext.IListener -------------------------
	
	@Override
	public void onIrpf(IrpfOutcome irpfOutcome) {
		if ( listener != null )
			listener.onIrpf(irpfOutcome);
	}
	
	@Override
	public void onUndefinedData(IExpression expression,
			String variableName, String message, Date start, Date end) {
		if ( listener != null )
			listener.onUndefinedData(expression, variableName, message, start, end);
	};

	@Override
	public void onRedefinedImplicit(String name,
			ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
		if ( listener != null )
			listener.onRedefinedImplicit(name, redefined, implicit);
	}
	
	@Override
	public <U> U onConstantParameter(String func, U constant, ExpressionContext expressionContext) {
		
		if ( listener != null )
			constant = listener.onConstantParameter(func, constant, expressionContext);
		
		Period p = ExpressionContext.getCurrentBindings().getPeriod();
		
		Double workedDays = 
		expressionContext
		.getVariables(ContextVariable.WORKED_DAYS, p.getStart(), p.getEnd())
		.stream()
		.map(var-> (Number) var.getValue(var.getPeriod()))
		.collect(Collectors.summingDouble(n->n.doubleValue())); 
		
		Double monthDays = 
		expressionContext
		.getVariables(ContextVariable.MONTH_DAYS, p.getStart(), p.getEnd())
		.stream()
		.map(var-> (Number) var.getValue(var.getPeriod()))
		.collect(Collectors.summingDouble(n->n.doubleValue())); 
		
		Double d =  ((Number)constant).doubleValue()
		* workedDays
		/ monthDays;
		
		return (U) d;
	};
	
	// --------------------------------------------------------------------

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
		
		else if (results.size() == 1 && results.get(0).getContext().isEmpty() ) {
			return  fixConstantResult(contractPayment, results.get(0), start, end, expressionContext);
		}

		throw new UnsupportedOperationException(String.format(IT_PAY_MSG, contractPayment.getDescription(),
					contractPayment.getExpression())); 
	}
	
	@Override
	@SuppressWarnings("serial")
	protected List<ITimedResult<Double>> fixConstantResult(
			IContractPayment contractPayment,
			ITimedResult<Double> result, 
			Date start, 
			Date end, 
			ExpressionContext expressionContext) throws UnsupportedOperationException {
		
		if ( Period.compare(contractPayment.getStartDate(), start) < 0 ||
				Period.compare(contractPayment.getEndDate(), end) > 0 ){
			onCheckError(contractPayment, String.format(CONSTANT_PAY_MSG, contractPayment.getDescription()));
			return fixConstantResult(result.getValue(), expressionContext);
		}
		
		return Collections.singletonList(result);
	}

	protected List<ITimedResult<Double>> fixConstantResult(Double value,
			ExpressionContext expressionContext) {
		
		List<Period> workedDays = expressionContext.getVariables(ContextVariable.WORKED_DAYS)
				.stream().map(v->v.getPeriod()).collect(Collectors.toList());
		List<Period> monthDays = expressionContext.getVariables(ContextVariable.MONTH_DAYS)
				.stream().map(v->v.getPeriod()).collect(Collectors.toList());
		
		return Period.intersect(workedDays, monthDays)
				.stream()
				.map( period-> new TimedResult<Double>(
						value
						* expressionContext.getVariable(ContextVariable.WORKED_DAYS, period.getStart(), period.getEnd(), Number.class).doubleValue()
						/ expressionContext.getVariable(ContextVariable.MONTH_DAYS, period.getStart(), period.getEnd(), Number.class).doubleValue()
						, period
						, new HashMap<String, ITimedVariable<?>>(){
							{
								put(ContextVariable.MONTH_DAYS.getName(), expressionContext.getVariable(ContextVariable.MONTH_DAYS, period.getStart(), period.getEnd()));
								put(ContextVariable.WORKED_DAYS.getName(), expressionContext.getVariable(ContextVariable.WORKED_DAYS, period.getStart(), period.getEnd()));
							}
						})
				)
				.collect(Collectors.toList());
	}
}