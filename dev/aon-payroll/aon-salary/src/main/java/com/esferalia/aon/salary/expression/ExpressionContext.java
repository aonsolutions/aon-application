package com.esferalia.aon.salary.expression;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.lang.math.NumberUtils;

public class ExpressionContext {
	
	
	
	private Bindings bindings ;

	private Map<String,IExpression> map;
	
	
	public ExpressionContext() {
		map = new HashMap<String, IExpression>();
		bindings = new SimpleBindings();
	}

	public void putAll( Map<String,IExpression> map ) {
		map.putAll(map);
	}
	
	public IExpression get(String key) {
		return map.get(key);
	}
	
	public IExpression put(IExpression expression) throws ExpressionException {
		String name = expression.getName();
		String script = expression.getExpression();
		try {
			Object result = ENGINE.eval(script, bindings );
			if ( name != null ) {
				bindings.put(name, result);
			}
		} catch (ScriptException e) {
			throw new ExpressionException(e.getMessage(),e);
		}
		
		return map.put(name,expression);
	}

	public IExpression put(String name, String script) throws ExpressionException {
		ExpressionImpl expressionImpl = new ExpressionImpl();
		expressionImpl.setName(name);
		expressionImpl.setExpression(script);
		expressionImpl.setScope(ExpressionScope.SALARY);
		return this.put(expressionImpl);
	}

	private static final ScriptEngine ENGINE = 
		new ScriptEngineManager().getEngineByName("JavaScript");
	
	public double resolve(IExpression expression) throws ExpressionException {
		double d = 0.0;
		
		Object result = null;
		String name = expression.getName();
		if ( name != null ) {
			put ( expression );
			result =  bindings.get(name);
		}
		else {
			try {
				result = ENGINE.eval(expression.getExpression(), bindings);
			} catch (ScriptException e) {
			}
		}
	
		if ( result instanceof Number) {
			d = ( ( Number ) result).doubleValue();
		}
		
		return d;
	}
	

	public Collection<IExpression> getValues() {
		return map.values();
	}
	
	public List<IExpression> getExpressionVariables()  {
		List<IExpression> expressions = new LinkedList<IExpression>();
		for (IExpression exp : map.values()) {
			if (NumberUtils.isNumber(exp.getExpression()) ) {
				expressions.add(exp);
			}
		}
		return expressions;
	}
}
