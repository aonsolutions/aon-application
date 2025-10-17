package com.esferalia.aon.payroll.calculator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.mvel2.CompileException;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.util.MethodStub;

import com.esferalia.aon.payroll.cgpj.CGPJ;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.MacroException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonFunctions {
	
	@Variable(ContextVariable.MIN)
	public static final Double min(Double a, Double b) {
		return Math.min(a, b);
	}
	
	@Variable(ContextVariable.MAX)
	public static final Double max(Double a, Double b) {
		return Math.max(a, b);
	}
	
	@Variable(ContextVariable.UNDEFINED)
	public static final Double undefined(String var) throws UndefinedVariablesException {
		throw new UndefinedVariablesException(var);
	}
	
	@Variable(ContextVariable.TRACE)
	public static final Object trace(String format, Object obj) {
		System.out.printf(format, obj);
		return obj;
	}

	@Variable(ContextVariable.ON_ACCOUNT_AGREEMENT)
	public static final Object onAccountAgreement(Object obj) {
		return obj;
	}

	@Variable(ContextVariable.MONTH_START)
	public static final Date monthStart(Date date) {
		return AonDateUtils.getFirstDayOfMonth(date);
	}

	@Variable(ContextVariable.YEAR_START)
	public static final Date yearStart(Date date) {
		return AonDateUtils.getFirstDayOfYear(date);
	}

	@Variable(ContextVariable.MONTH_END)
	public static final Date monthEnd(Date date) {
		return AonDateUtils.getLastDayOfMonth(date);
	}

	@Variable(ContextVariable.YEAR_END)
	public static final Date yearEnd(Date date) {
		return AonDateUtils.getLastDayOfYear(date);
	}
	
	@Variable(ContextVariable.CALC_COMPENSATIONS)
	public static final Map<Integer, Map<String,Object>> calcCompensations(Date startDate, Date endDate, Double dailySalary ) throws ExpressionException {

	    Map<Integer, Map<String,Object>> compensations = new HashMap<>();
	    
	    try {
		CGPJ.calculateCompensations(
		    startDate, 
		    endDate, 
		    Optional.of(dailySalary), 
		    Optional.empty(), //monthlySalary, 
		    Optional.empty(), //yearSalary, 
		    (title, description, amount, days, months ) -> {
			String expression = AonStringUtils.replace(description, "meses", months + " meses");
			expression = AonStringUtils.replace(expression, "dias", days + " dias");
			Map<String,Object> compensation = new HashMap<>();
			compensation.put("title", title);
			compensation.put("expression", expression);
			compensation.put("description", description);
			compensation.put("amount", amount);
			compensation.put("days", days);
			compensation.put("months", months);
			int index = Integer.parseInt(title, 0, 1, 10);
			compensations.put(index, compensation);
		    } );
		
		
		return compensations;
		
	    } catch (IOException | ParseException e) {
			Map<String,Object> error = new HashMap<>();
			error.put("title", "AON ha tenido un problema");
			error.put("expression", "No se ha podido acceder al servicio de cálculo de indemnizaciones por extinción de contrato de trabajo");
			error.put("days", "");
			error.put("months", "");
			error.put("amount", "");
			error.put("description", "");
	    	return Collections.singletonMap(1, error );
	    }
	}
	
	@Variable(ContextVariable.EVAL_TEMPLATE)
	public static final <T,U> String join( Map<T,U> map, String template) {
	    Map<String, Object> ctx = new HashMap<>();
	    ctx.put("VALUES", map.values());
	    ctx.put("values", map.values());
	    return  TemplateRuntime.eval(template, ctx).toString();
	}

	@Variable(ContextVariable.FORMAT)
	public static final String format(String pattern, Date date) {
		return new SimpleDateFormat(pattern).format(date);
	}

	@Variable(ContextVariable.DATES)
	public static final String dates(String pattern, Date startDate, Date endDate) {
		StringBuilder stringBuilder = new StringBuilder();
		
		String regex = "\'[^']*\'";
		String[] patterns = pattern.splitWithDelimiters(regex,0);
		
		patterns = Arrays.stream(patterns).filter(AonStringUtils::isNotEmpty).toArray(String[]::new);
		
		int i ; 
		for ( i = 0; i < patterns.length && patterns[i].matches(regex) ; i++ )
			stringBuilder.append(patterns[i].replace("'", ""));
		if ( i < patterns.length ) 
			stringBuilder.append(format(patterns[i++], startDate));
		
		if ( endDate.compareTo(startDate) > 0 ) {
			for ( ; i < patterns.length && patterns[i].matches(regex) ; i++ )
				stringBuilder.append(patterns[i].replace("'", ""));
			if ( i < patterns.length ) 
				stringBuilder.append(format(patterns[i], endDate));
		}


		return  stringBuilder.toString();
	}

	public static final String dates(String pattern, Date startDate, Date endDate, Date start, Date end) {
		
		if ( AonDateUtils.compare(startDate, start) == 0 
				&& AonDateUtils.compare(endDate, end) == 0 ) {
			return AonStringUtils.EMPTY;
		}
		
		return  dates(pattern, startDate, endDate );
	}

	public static final String dates(String pattern) throws MacroException {
		throw new MacroException() {
			
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(String.format("%s\\s*\\(\\s*[\"']%s[\"']", ContextVariable.DATES, pattern), "$0, INICIO, FIN, INICIO_NOMINA, FIN_NOMINA");
			}
		};
	}

	// ------------------------------------------------------------------------
	// 
	// ------------------------------------------------------------------------
	public static void load(ExpressionContext context, Date startDate, Date endDate){
		for (Method method : AonFunctions.class.getDeclaredMethods()) {
			Variable variable = method.getAnnotation(Variable.class);
			if ( variable != null ) {
				ContextVariable contextVariable = variable.value();
				MethodStub methodStub = new MethodStub(method) {
					@Override
					public String toString() {
						return variable.string();
					}
				};
				context.setVariable(contextVariable, methodStub, startDate, endDate);
			}
		}
	}
	
	
	
}
