/**
 * 
 */
package com.esferalia.aon.salary.expression;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.mvel2.MVEL;
import org.mvel2.PropertyAccessException;

import com.esferalia.aon.salary.expression.ExpressionContext.UnknownUndefVarException;
import com.esferalia.aon.salary.expression.Variables.PeriodMap;

/**
 * @author rtrepiana
 *
 */
public class ExpressionContextTestCase {

	/**
	 * Test method for {@link com.esferalia.aon.salary.expression.ExpressionContext#getUndefinedProperty(org.mvel2.PropertyAccessException, com.esferalia.aon.salary.expression.Variables.PeriodMap)}.
	 * @throws UnknownUndefVarException 
	 */
	@Test
	public void testGetUndefinedProperty() throws UnknownUndefVarException {
		try {
			MVEL.eval("100+NO_DEFINIDA*4");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("NO_DEFINIDA",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("100 * 100 /*un comentario inofensivo ?*/ + NO_DEFINIDA");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("NO_DEFINIDA",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}
		try {
			MVEL.eval("NO_DEFINIDA + 100 * 100 /*un comentario inofensivo ?*/");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("NO_DEFINIDA",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("100 * 100 /*un comentario inofensivo ?*/ + NO_DEFINIDA + 100 * 100 /*un comentario inofensivo ?*/");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("NO_DEFINIDA",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("(100) + (NO_DEFINIDA) + 400");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("NO_DEFINIDA",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("(100) + {'1':'HOLA','2':'ADIOS','3':'...'}[NO_DEFINIDA] + 400");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("NO_DEFINIDA",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("(100) + F(FF(NO_DEFINIDA)) + 400");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("NO_DEFINIDA",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("(100) + F(FF(NO_DEFINIDA&100)) + 400");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("NO_DEFINIDA",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("(100) + F(FF(NO_DEFINIDA[0])) + 400");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("NO_DEFINIDA",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("X");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("X",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval(" X  ");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("X",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval(" \r\nX  ");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("X",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("1!=1?X:Y");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("Y",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("100+100000+X");
			Assert.fail();
		} catch ( PropertyAccessException e ) {
			Assert.assertEquals("X",ExpressionContext.getUndefinedProperty(e, (PeriodMap) null));
		}

	}

	@Test
	public void testGetUndefinedPropertyII() throws UnknownUndefVarException {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		calendar.set(Calendar.DATE, 1);
		Date start = calendar.getTime();
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.MONTH));
		Date end = calendar.getTime(); 
		ExpressionContext context = new ExpressionContext();
		
		try {
			context.eval("NO_DEFINIDA", start, end, Double.class);
			Assert.fail();
		} catch ( PropertyAccessException e ) {
		} catch (UndefinedVariablesException e) {
			Assert.assertEquals("NO_DEFINIDA",e.getVariableNames()[0]);
		} catch (ExpressionException e) {
			Assert.fail();
		}
		
		try {
			context.eval("100*NO_DEFINIDA", start, end, Double.class);
			Assert.fail();
		} catch ( PropertyAccessException e ) {
		} catch (UndefinedVariablesException e) {
			Assert.assertEquals("NO_DEFINIDA",e.getVariableNames()[0]);
		} catch (ExpressionException e) {
			Assert.fail();
		}
	}
}
