/**
 * 
 */
package com.esferalia.aon.salary.expression;

import static org.junit.Assert.*;
import junit.framework.Assert;

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
	}

}
