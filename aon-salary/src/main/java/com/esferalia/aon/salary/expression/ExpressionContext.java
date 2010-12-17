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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.util.CommonUtil;

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
	public IExpression put(IExpression expression) {
		
		String name = expression.getName();
		String script = expression.getExpression();
		//String script = String.format("%s = %s", name, value);
		try {
			Object result = ENGINE.eval(script, bindings );
			if ( name != null ) {
				bindings.put(name, result);
			}
			System.out.println ( "Bindings " + name + " = " + bindings.get(name) );
		} catch (ScriptException e) {
			System.out.println(e.getMessage());
		}
		
		return map.put(name,expression);
	}

	public IExpression put(String name, String script) {
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
		
		put ( expression );
		result =  bindings.get(name);
		
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
