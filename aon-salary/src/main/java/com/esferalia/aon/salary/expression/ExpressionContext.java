package com.esferalia.aon.salary.expression;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import org.apache.commons.lang.math.NumberUtils;
import org.mvel2.MVEL;

public class ExpressionContext {
	
	
	private static class CompositeBindings 
		extends SimpleBindings {
		
		private Bindings readOnly;
		
		public CompositeBindings(Map<String, Object> vars, 
				Bindings readOnly) {
			super(vars);
			this.readOnly = readOnly;
		}
		
		@Override
		public Object get(Object key) {
			if ( super.containsKey(key)  ){
				return super.get(key);
			}
			else {
				return readOnly.get(key);
			}
		}		

		@Override
		public boolean containsKey(Object key) {
			return super.containsKey(key) || readOnly.containsKey(key);
		}
		
	}
	
	
	
	private Bindings bindings ;

	private Map<String,IExpression> map;
	
	
	public ExpressionContext() {
		map = new HashMap<String, IExpression>();
		bindings = new SimpleBindings();
	}
	
	public ExpressionContext(ExpressionContext expressionContext){
		this();
		map.putAll(expressionContext.map);
		bindings.putAll(expressionContext.bindings);
	}
	
	public Object put(IExpression expression) throws ExpressionException {
		String name = expression.getName();
		String script = expression.getExpression();
		try {
			Object result = MVEL.eval(script, bindings );
			if ( name != null ) {
				bindings.put(name, result);
			}
			return result;
		} catch (Exception e) {
			throw new ExpressionException(e.getMessage(),e);
		}
	}

	private <T> T put(IExpression expression, Class<T> toType) throws ExpressionException {
		String name = expression.getName();
		String script = expression.getExpression();
		try {
			T result = MVEL.eval(script, bindings, toType );
			if ( name != null ) {
				bindings.put(name, result);
			}
			return result;
		} catch (Exception e) {
			throw new ExpressionException(e.getMessage(),e);
		}
	}

	public void put(String name, Double value) {
		this.bindings.put(name, value);
	}

	public Object put(String name, String script) throws ExpressionException {
		ExpressionImpl expressionImpl = new ExpressionImpl();
		expressionImpl.setName(name);
		expressionImpl.setExpression(script);
		expressionImpl.setScope(ExpressionScope.SALARY);
		return this.put(expressionImpl);
	}

	
	public Double resolve(IExpression expression) throws ExpressionException {
		String name = expression.getName();
		if ( name != null ) {
			return put ( expression, Double.class );
		}
		else {
			try {
				return  MVEL.eval(expression.getExpression(), bindings, Double.class);
			} catch (Exception e) {
				throw new ExpressionException(e.getMessage(),e);
			}
		}
	}
	
	public Object eval(String script ) throws ExpressionException {
		if ( script == null )
			return null;
		try {
			return MVEL.eval(script, bindings);
		} catch (Exception e) {
			throw new ExpressionException(e.getMessage(), e);
		}
	}
	
	public <T> T  eval(String script, Class<T> toType) throws ExpressionException {
		if ( script == null )
			return null;
		try {
			return MVEL.eval(script, bindings, toType );
		} catch (Exception e) {
			throw new ExpressionException(e.getMessage(), e);
		}
	}

	public Object eval(String script, Map<String, Object> vars ) throws ExpressionException {
		if ( script == null )
			return null;
		try {
			Bindings locals = 
				new CompositeBindings(vars, bindings);
			return MVEL.eval(script, locals );
		} catch (Exception e) {
			throw new ExpressionException(e.getMessage(), e);
		}
	}

	public <T> T  eval(String script, Map<String, Object> vars, Class<T> toType) throws ExpressionException {
		if ( script == null )
			return null;
		try {
			Bindings locals = 
				new CompositeBindings(vars, bindings);
			return MVEL.eval(script, locals, toType );
		} catch (Exception e) {
			throw new ExpressionException(e.getMessage(), e);
		}
	}
	
	public Collection<IExpression> getValues() {
		return map.values();
	}
	
	public List<IExpression> getExpressionVariables()  {
		List<IExpression> expressions = new LinkedList<IExpression>();
		for (IExpression exp : map.values()) {
			if (NumberUtils.isNumber(exp.getExpression()) ) {
				System.out.println("expressions.add(" + exp.getName() + ", " + exp.getExpression()+ ")");
				expressions.add(exp);
			}
		}
		return expressions;
	}
	
}
