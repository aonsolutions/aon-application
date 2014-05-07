package com.esferalia.aon.salary.expression;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;

public class LazyExpressionVariable implements IExpressionVariable<Object> {

	private Period period;
	private ExpressionContext ctx;
	private IExpression expression;

	public LazyExpressionVariable(ExpressionContext ctx,
			IExpression expression, Period period) {
		this.ctx = ctx;
		this.period = period;
		this.expression = expression;
	}

	public LazyExpressionVariable(ExpressionContext ctx,
			IExpression expression, Date start, Date end) {
		this(ctx, expression, new Period(start, end));
	}

	// ---------------------------------------------------- IExpressionVariable

	@Override
	public Period getPeriod() {
		return period;
	}

	@Override
	public Object getValue(Period period) {
		try {
			addExpression();
			return ctx.getVariable(expression.getName(), period.getStart(),
					period.getEnd(), Object.class);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, ITimedVariable<?>> getContext() {
		try {
			IExpressionVariable<?> var = (IExpressionVariable<?>) ctx
					.getVariable(expression.getName(), period.getStart(),
							period.getEnd());
			return var.getContext();
		} catch (ClassCastException e) {
			return Collections.emptyMap();
		}
	}

	@Override
	public IExpression getExpression() {
		return expression;
	}

	// -------------------------------------------------------------------------

	private void addExpression() {
		try {
			ctx.addVariable(expression.getName(), null, period.getStart(), period.getEnd());
			ctx.addExpression(expression, period.getStart(), period.getEnd());
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		}
	}

}
