package com.esferalia.aon.payroll.calculator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.util.MethodStub;

import com.esferalia.aon.payroll.cgpj.CGPJ;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.DismissalType;
import com.esferalia.aon.salary.expression.ExpressionContext;
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
		throw new ExpressionException(e);
	    }
	}
	
	@Variable(ContextVariable.EVAL_TEMPLATE)
	public static final <T,U> String join( Map<T,U> map, String template) {
	    Map<String, Object> ctx = new HashMap<>();
	    ctx.put("VALUES", map.values());
	    ctx.put("values", map.values());
	    return  TemplateRuntime.eval(template, ctx).toString();
	}

//	public static final <T,U> String join( String prefix, String suffix, Map<T,U> map, BiFunction<T, U, String> f) {
//	    return  map.entrySet().stream().map(entry -> f.apply(entry.getKey(), entry.getValue()) ).collect(Collectors.joining("", prefix, suffix));
//	}

	// ------------------------------------------------------------------------
	// 
	// ------------------------------------------------------------------------
	public static void load(ExpressionContext context, Date startDate, Date endDate){
		for (Method method : AonFunctions.class.getDeclaredMethods()) {
			Variable variable = method.getAnnotation(Variable.class);
			if ( variable != null ) {
				ContextVariable contextVariable = variable.value();
				MethodStub methodStub = new MethodStub(method);
				context.setVariable(contextVariable, methodStub, startDate, endDate);
			}
		}
	}
	
	
}
