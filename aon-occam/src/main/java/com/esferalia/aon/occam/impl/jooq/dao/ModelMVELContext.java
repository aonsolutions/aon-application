package com.esferalia.aon.occam.impl.jooq.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.mvel2.MVEL;

import com.esferalia.aon.watson.util.AonStringUtils;

public class ModelMVELContext implements Map<String, Object> {

	private Map<String, Object> context;
	private Map<String, String> expressionMap;
	private Stack<String> stack = new Stack<String>();
	
	public ModelMVELContext() {
		this.context = new HashMap<String, Object>();
		this.expressionMap = new HashMap<String, String>();
	}

	public Map<String, String> getExpressionMap() {
		return expressionMap;
	}

	public void setExpressionMap(Map<String, String> expressionMap) {
		this.expressionMap = expressionMap;
	}
	
	// ***********************************************************************
	// java.util.Map inherited methods.
	// ***********************************************************************
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.context.containsKey( key );
	}
	
	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return this.context.entrySet();
	}

	@Override
	public Object get(Object keyObject) {
		String key = (String) keyObject;
		Object obj = null;
		if (this.context.containsKey(key) 
			&& (!expressionMap.containsKey(key) || stack.contains(key))) {
			obj = this.context.get(key);
		} else {
			obj = evaluate(key);
		}
		return obj;
	}
    
	public Object evaluate(String key) {
		if (expressionMap != null) {
			String exp = expressionMap.get(key);
			Object ret = null;
			if (AonStringUtils.isNotEmpty(exp)) {
				ret =  mvelEval(key,exp);
			}
			if (ret != null) {
				put(key, ret);
				return ret;
			}
		}
		return new Double(0);
	}
	public Object evaluateExpression(String key,String expression) {
		return mvelEval(key,expression);
	}
	private Object mvelEval(String key,String expression) {
		try {
			stack.push(key);
			return MVEL.eval( expression , this , this);
		} finally {
			stack.pop();
		}
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		return this.context.keySet();
	}

	@Override
	public Object put(String key, Object d) {
		return this.context.put(key, d);
	}
	
	@Override
	public void putAll(Map<? extends String,? extends  Object> m) {
		this.context.putAll(m);
	}

	@Override
	public Double remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return this.context.size();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void finalize() throws Throwable {
		context = null;
		super.finalize();
	}

	// ************************ Métodos disponibles en las expresiones MVEL.
	public double round(double value, int precision) {
		return new BigDecimal(Double.toString(value)).setScale(precision, RoundingMode.HALF_UP).doubleValue();
 	}
	public double round(double value) {
		return round(value, 2);
	}
	public boolean isZero(double value) {
		return round( value ) == 0.0;
	}
	public boolean isNotZero(double value) {
		return !isZero(value);
	}

}
