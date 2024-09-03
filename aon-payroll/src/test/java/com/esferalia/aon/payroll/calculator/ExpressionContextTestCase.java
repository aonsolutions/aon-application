package com.esferalia.aon.payroll.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;

public class ExpressionContextTestCase {

	@Test
	public void testDeferredExpressionVariable() throws ExpressionException {
		ExpressionContext expressionContext = new ExpressionContext();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH,1);
		final Date start = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		final Date end = calendar.getTime();

		ExpressionImpl expr = new ExpressionImpl();
		expr.setName("DEFERRED");
		expr.setExpression("DIAS_NOMINA");
		expr.setScope(ExpressionScope.SYSTEM);
		DeferredExpressionVariable<Object> deferredExprVar = new DeferredExpressionVariable<Object>(
				start, end, expr);
		expressionContext.putVariable(expr.getName(), deferredExprVar);
		
		ITimedVariable<Long> salaryDays = new ITimedVariable<Long>() {
			@Override
			public Long getValue(Period p) {
				return CommonUtil.getDaysBetweenDates(p.getStart(), p.getEnd());
			}

			@Override
			public Period getPeriod() {
				return new Period(start, end);
			}
		};
				
		expressionContext.putVariable("DIAS_NOMINA", salaryDays);
		
		
		calendar.set(Calendar.DAY_OF_MONTH,15);
		Date issue = calendar.getTime();
		List<ITimedResult<Double>> results = expressionContext.eval("DEFERRED", issue, end, Double.class);

		long last = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		assertEquals(results.get(0).getValue() ,(last -15.00) );
		
	}

}
