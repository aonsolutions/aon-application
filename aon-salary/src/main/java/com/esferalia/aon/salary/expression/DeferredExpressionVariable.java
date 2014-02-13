package com.esferalia.aon.salary.expression;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;

public class DeferredExpressionVariable implements IExpressionVariable<Object> {

	private Period period;
	private ExpressionContext ctx;
	private IExpression expression;
	private Map<String, ITimedVariable<?>> context;

	public DeferredExpressionVariable(ExpressionContext ctx,
			IExpression expression, Period period) {
		this.ctx = ctx;
		this.period = period;
		this.expression = expression;
	}

	public DeferredExpressionVariable(ExpressionContext ctx,
			IExpression expression, Date start, Date end) {
		this(ctx, expression, new Period(start, end));
	}

	@Override
	public Period getPeriod() {
		return period;
	}

	@Override
	public Object getValue(Period period) {
		try {
			List<ITimedResult<Object>> results = ctx.eval(
					expression.getExpression(), period.getStart(),
					period.getEnd());
			for (ITimedResult<Object> result : results) {
				context = result.getContext();
				return result.getValue();
			}
			return null;
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		}

	}

	@Override
	public IExpression getExpression() {
		return expression;
	}

	@Override
	public Map<String, ITimedVariable<?>> getContext() {
		// TODO Auto-generated method stub
		return context;
	}

	// -------------------------------------------------------------------------

}
