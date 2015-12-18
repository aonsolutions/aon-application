/**
 * 
 */
package com.esferalia.aon.salary.expression;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mvel2.MVEL;
import org.mvel2.PropertyAccessException;

import com.esferalia.aon.salary.expression.ExpressionContext.UnknownUndefVarException;
import com.esferalia.aon.salary.expression.Variables.PeriodMap;

import junit.framework.Assert;

/**
 * @author rtrepiana
 *
 */
public class ExpressionContextTestCase {

	@Test
	public void testGetUndefinedProperty() throws UnknownUndefVarException {
		try {
			MVEL.eval("100+NO_DEFINIDA*4");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("NO_DEFINIDA", ExpressionContext
					.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("100 * 100 /*un comentario inofensivo ?*/ + NO_DEFINIDA");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("NO_DEFINIDA", ExpressionContext
					.getUndefinedProperty(e, (PeriodMap) null));
		}
		try {
			MVEL.eval("NO_DEFINIDA + 100 * 100 /*un comentario inofensivo ?*/");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("NO_DEFINIDA", ExpressionContext
					.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval(
					"100 * 100 /*un comentario inofensivo ?*/ + NO_DEFINIDA + 100 * 100 /*un comentario inofensivo ?*/");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("NO_DEFINIDA", ExpressionContext
					.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("(100) + (NO_DEFINIDA) + 400");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("NO_DEFINIDA", ExpressionContext
					.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval(
					"(100) + {'1':'HOLA','2':'ADIOS','3':'...'}[NO_DEFINIDA] + 400");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("NO_DEFINIDA", ExpressionContext
					.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("(100) + F(FF(NO_DEFINIDA)) + 400");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("NO_DEFINIDA", ExpressionContext
					.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("(100) + F(FF(NO_DEFINIDA&100)) + 400");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("NO_DEFINIDA", ExpressionContext
					.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("(100) + F(FF(NO_DEFINIDA[0])) + 400");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("NO_DEFINIDA", ExpressionContext
					.getUndefinedProperty(e, (PeriodMap) null));
		}

		try {
			MVEL.eval("X");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("X", ExpressionContext.getUndefinedProperty(e,
					(PeriodMap) null));
		}

		try {
			MVEL.eval(" X  ");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("X", ExpressionContext.getUndefinedProperty(e,
					(PeriodMap) null));
		}

		try {
			MVEL.eval(" \r\nX  ");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("X", ExpressionContext.getUndefinedProperty(e,
					(PeriodMap) null));
		}

		try {
			MVEL.eval("1!=1?X:Y");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("Y", ExpressionContext.getUndefinedProperty(e,
					(PeriodMap) null));
		}

		try {
			MVEL.eval("100+100000+X");
			Assert.fail();
		} catch (PropertyAccessException e) {
			Assert.assertEquals("X", ExpressionContext.getUndefinedProperty(e,
					(PeriodMap) null));
		}

	}

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
		} catch (PropertyAccessException e) {
		} catch (UndefinedVariablesException e) {
			Assert.assertEquals("NO_DEFINIDA", e.getVariableNames()[0]);
		} catch (ExpressionException e) {
			Assert.fail();
		}

		try {
			context.eval("100*NO_DEFINIDA", start, end, Double.class);
			Assert.fail();
		} catch (PropertyAccessException e) {
		} catch (UndefinedVariablesException e) {
			Assert.assertEquals("NO_DEFINIDA", e.getVariableNames()[0]);
		} catch (ExpressionException e) {
			Assert.fail();
		}
		try {
			context.setVariable("DIAS_MES", 30, start, end);
			context.setVariable("DIAS_NOMINA", 30, start, end);
			context.setVariable("HORAS_NOMINA", 400, start, end);
			context.setVariable("TIEMPO_COMPLETO", true, start, end);
			context.setVariable("GRUPO_COTIZACION", "01", start, end);
			String expression = "[\r\n"
					+ "\"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA),\r\n"
					+ "\"02\":(TIEMPO_COMPLETO ? 876.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.28 * HORAS_NOMINA),\r\n"
					+ "\"03\":(TIEMPO_COMPLETO ? 762.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.59 * HORAS_NOMINA),\r\n"
					+ "\"04\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+ "\"05\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+ "\"06\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+ "\"07\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+ "\"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+ "\"09\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+ "\"10\":(TIEMPO_COMPLETO ? 25.22* DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+ "\"11\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA)\r\n"
					+ "]\r\n" + "[GRUPO_COTIZACION]\r\n";

			System.out.println(expression);

			List<ITimedResult<Double>> results = context.eval(expression, start,
					end, Double.class);
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
			String expression = "[\r\n"
					+ "\t\t\t\t\"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA),\r\n"
					+ "\t\t\t\t\"02\":(TIEMPO_COMPLETO ? 876.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.28 * HORAS_NOMINA),\r\n"
					+ "\t\t\t\t\"03\":(TIEMPO_COMPLETO ? 762.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.59 * HORAS_NOMINA),\r\n"
					+ "\t\t\t\t\"04\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+ "\t\t\t\t\"05\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+ "\t\t\t\t\"06\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+ "\t\t\t\t\"07\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),\r\n"
					+ "\t\t\t\t\"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+ "\t\t\t\t\"09\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMIhttp://bilbaomarathon.com/assets/absolutamedia15.pdfNA : 4.56 * HORAS_NOMINA),\r\n"
					+ "\t\t\t\t\"10\":(TIEMPO_COMPLETO ? 25.22* DIAS_NOMINA : 4.56 * HORAS_NOMINA),\r\n"
					+ "\t\t\t\t\"11\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA)\r\n"
					+ "\t\t\t\t]\r\n" + "\t\t\t\t[GRUPO_COTIZACION]\r\n";

			System.out.println(expression);

			List<ITimedResult<Double>> results = context.eval(expression, start,
					end, Double.class);
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
			String expression = "["
					+ "   \"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA),"
					+ "   \"02\":(TIEMPO_COMPLETO ? 876.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.28 * HORAS_NOMINA),"
					+ "   \"03\":(TIEMPO_COMPLETO ? 762.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.59 * HORAS_NOMINA),"
					+ "   \"04\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),"
					+ "   \"05\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),"
					+ "   \"06\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),"
					+ "   \"07\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA),"
					+ "   \"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),"
					+ "   \"09\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA),"
					+ "   \"10\":(TIEMPO_COMPLETO ? 25.22* DIAS_NOMINA : 4.56 * HORAS_NOMINA),"
					+ "   \"11\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA)"
					+ "   ]" + "   [GRUPO_COTIZACION]";

			System.out.println(expression);

			List<ITimedResult<Double>> results = context.eval(expression, start,
					end, Double.class);
			Assert.assertEquals(1056.90, results.get(0).getValue());
		} catch (UndefinedVariablesException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testGetVariableSet() {

		String expression = "GRUPO_COTIZACION";
		Set<String> variables = ExpressionContext.getVariableSet(expression);
		Assert.assertEquals(1, variables.size());
		Assert.assertEquals(true, variables.contains("GRUPO_COTIZACION"));

		expression = "3262.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)";
		variables = ExpressionContext.getVariableSet(expression);
		Assert.assertEquals(2, variables.size());
		Assert.assertEquals(true, variables.contains("DIAS_MES"));
		Assert.assertEquals(true, variables.contains("DIAS_NOMINA"));

		expression = "[\"a\": 0.65, \"b\": 1.00, \"d\": 3.35, \"e\": 1.80, \"f\": 3.35, \"g\": 2.10, \"h\": 1.40]";
		variables = ExpressionContext.getVariableSet(expression);
		Assert.assertEquals(0, variables.size());

		expression = "def(total_liquido){ MAX(((total_liquido - SMI) * 0.30),0)+MAX(((total_liquido - 2 * SMI ) * 0.20),0) + MAX((( total_liquido - 3 * SMI ) * 0.10),0) +MAX((( total_liquido - 4 * SMI ) * 0.15),0) + MAX((( total_liquido - 5 * SMI ) * 0.15),0)}";
		variables = ExpressionContext.getVariableSet(expression);

		// expression =
		// "["
		// +" \"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 :
		// DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA),"
		// +" \"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 *
		// HORAS_NOMINA)"
		// +" ]"
		// +" [GRUPO_COTIZACION]";
		// variables = ExpressionContext.getVariableSet(expression);
		//
		// Assert.assertEquals(true, variables.contains("TIEMPO_COMPLETO"));
		// Assert.assertEquals(true, variables.contains("DIAS_NOMINA"));
		// Assert.assertEquals(true, variables.contains("HORAS_NOMINA"));
		// Assert.assertEquals(true, variables.contains("DIAS_MES"));
		// Assert.assertEquals(true, variables.contains("GRUPO_COTIZACION"));

	}

	@Test
	public void testDeferredVariablesRead() throws ExpressionException {

		ExpressionContext ctx = new ExpressionContext();
		Period period = new Period(new Date(), new Date());
		
		
		IExpression baseCgcMin = new IExpression() {

			@Override
			public boolean isReadOnly() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public ExpressionScope getScope() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				return "BASE_CGC_MIN";
			}

			@Override
			public String getExpression() {
				return "(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA)";
			}
		};

		
		IExpression monthDays = new IExpression() {

			@Override
			public boolean isReadOnly() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public ExpressionScope getScope() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				return "DIAS_MES";
			}

			@Override
			public String getExpression() {
				return "30.00";
			}
		};



		ctx.putVariable(baseCgcMin.getName(), new ExpressionContext.DeferredExpressionVariable<Double>(
				period, 
				baseCgcMin));
		ctx.putVariable(monthDays.getName(), new ExpressionContext.DeferredExpressionVariable<Double>(
				period, 
				monthDays));
		ctx.setVariable("TIEMPO_COMPLETO", true, period.getStart(), period.getEnd());
		ctx.setVariable("DIAS_NOMINA", 30.00, period.getStart(), period.getEnd());
		
		List<ITimedResult<Double>> results = ctx.eval(
				baseCgcMin.getName(),
				period.getStart(), 
				period.getEnd(), 
				Double.class);

		Assert.assertEquals(1, results.size());
		Assert.assertEquals(1056.90,
				results.get(0).getValue(results.get(0).getPeriod()));

		Assert.assertEquals(4, results.get(0).getContext().size());
		
		Assert.assertEquals(30.00, results.get(0).getContext()
				.get("DIAS_NOMINA").getValue(period));

		Assert.assertEquals(30.00, results.get(0).getContext()
				.get("DIAS_MES").getValue(period));
	}

}
