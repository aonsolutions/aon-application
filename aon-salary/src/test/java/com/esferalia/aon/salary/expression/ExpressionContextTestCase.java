/**
 * 
 */
package com.esferalia.aon.salary.expression;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
	public void testGetUndefinedPropertyII() throws ExpressionException {
		
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
		try {
			context.setVariable("DIAS_MES", 30, start, end);
			context.setVariable("DIAS_NOMINA", 30, start, end);
			context.setVariable("HORAS_NOMINA", 400, start, end);
			context.setVariable("TIEMPO_COMPLETO", true, start, end);
			context.setVariable("GRUPO_COTIZACION", "01", start, end);
			String expression = 				
					"[\r\n"
					+"\"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA),\r\n" 
					+"\"02\":(TIEMPO_COMPLETO ? 876.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.28 * HORAS_NOMINA),\r\n"
					+"\"03\":(TIEMPO_COMPLETO ? 762.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.59 * HORAS_NOMINA),\r\n"
					+"\"04\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+"\"05\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+"\"06\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+"\"07\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+"\"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+"\"09\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+"\"10\":(TIEMPO_COMPLETO ? 25.22* DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+"\"11\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA)\r\n"
					+"]\r\n"
					+"[GRUPO_COTIZACION]\r\n";
			
			System.out.println(expression);
			
			List<ITimedResult<Double>> results = context.eval(expression,
				start, end, Double.class);
			Assert.assertEquals(1056.90, results.get(0).getValue());
		} catch (UndefinedVariablesException e) {
			Assert.fail(e.getMessage());
		} 

		try {
			context.setVariable("DIAS_MES", 30, start, end);
			context.setVariable("DIAS_NOMINA", 30, start, end);
			context.setVariable("HORAS_NOMINA", 400, start, end);
			context.setVariable("TIEMPO_COMPLETO", true, start, end);
			context.setVariable("GRUPO_COTIZACION", "01", start, end);
			String expression = 				
					"[\r\n"
					+"\t\t\t\t\"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA),\r\n" 
					+"\t\t\t\t\"02\":(TIEMPO_COMPLETO ? 876.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.28 * HORAS_NOMINA),\r\n"
					+"\t\t\t\t\"03\":(TIEMPO_COMPLETO ? 762.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.59 * HORAS_NOMINA),\r\n"
					+"\t\t\t\t\"04\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+"\t\t\t\t\"05\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+"\t\t\t\t\"06\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+"\t\t\t\t\"07\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+"\t\t\t\t\"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+"\t\t\t\t\"09\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+"\t\t\t\t\"10\":(TIEMPO_COMPLETO ? 25.22* DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+"\t\t\t\t\"11\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA)\r\n"
					+"\t\t\t\t]\r\n"
					+"\t\t\t\t[GRUPO_COTIZACION]\r\n";
			
			System.out.println(expression);
			
			List<ITimedResult<Double>> results = context.eval(expression,
				start, end, Double.class);
			Assert.assertEquals(1056.90, results.get(0).getValue());
		} catch (UndefinedVariablesException e) {
			Assert.fail(e.getMessage());
		} 

		try {
			context.setVariable("DIAS_MES", 30, start, end);
			context.setVariable("DIAS_NOMINA", 30, start, end);
			context.setVariable("HORAS_NOMINA", 400, start, end);
			context.setVariable("TIEMPO_COMPLETO", true, start, end);
			context.setVariable("GRUPO_COTIZACION", "01", start, end);
			String expression = 				
					"["
					+"   \"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA)," 
					+"   \"02\":(TIEMPO_COMPLETO ? 876.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.28 * HORAS_NOMINA),"
					+"   \"03\":(TIEMPO_COMPLETO ? 762.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.59 * HORAS_NOMINA),"
					+"   \"04\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),"
					+"   \"05\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),"
					+"   \"06\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),"
					+"   \"07\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),"
					+"   \"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),"
					+"   \"09\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),"
					+"   \"10\":(TIEMPO_COMPLETO ? 25.22* DIAS_NOMINA : 4.56 * HORAS_NOMINA),"
					+"   \"11\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA)"
					+"   ]"
					+"   [GRUPO_COTIZACION]";
			
			System.out.println(expression);
			
			List<ITimedResult<Double>> results = context.eval(expression,
				start, end, Double.class);
			Assert.assertEquals(1056.90, results.get(0).getValue());
		} catch (UndefinedVariablesException e) {
			Assert.fail(e.getMessage());
		} 

	}
}
