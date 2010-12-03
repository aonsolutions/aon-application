package com.esferalia.aon.salary.expression;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

public class ExpressionContext {
	
	private Map<String,IExpression> map;
	
	public ExpressionContext() {
		map = new HashMap<String, IExpression>();
	}

	public void putAll( Map<String,IExpression> map ) {
		map.putAll(map);
	}
	
	public IExpression get(String key) {
		return map.get(key);
	}
	public IExpression put(IExpression expression) {
		return map.put(expression.getName(),expression);
	}
	
	public double resolve(IExpression expression) throws ExpressionException {
		double d = 0.0;
		if (NumberUtils.isNumber(expression.getExpression()) ) {
			d = NumberUtils.toDouble(expression.getExpression());	
		} 
		return d;
	}
	
	public Collection<IExpression> getValues() {
		return map.values();
	}
	
}
