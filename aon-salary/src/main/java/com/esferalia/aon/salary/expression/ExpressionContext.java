package com.esferalia.aon.salary.expression;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.UnresolveablePropertyException;

import com.code.aon.common.util.CommonUtil;

public class ExpressionContext {
	
	
	Variables variables;

	public ExpressionContext() {
		variables = new Variables();
	}
	
	public ExpressionContext(ExpressionContext expressionContext){
		variables = new Variables(expressionContext.variables);
	}
	
	public Collection<IExpression> getValues() {
		throw new UnsupportedOperationException();
	}

	public double resolve(IExpression expression) throws ExpressionException {	
		throw new UnsupportedOperationException();
	}

	public void addVariable(Object name, ITimedObject<?> timedObject) {
		variables.put(name.toString(), timedObject);
	}

	public void addVariable(Object name, Object value, Date start, Date end) {
		ITimedObject<Object> timedObject = 
			new TimedObject<Object>(value, start, end );
		this.addVariable(name.toString(), timedObject);
	}

	public List<ITimedObject<Object>> addExpression(IExpression expression,Date start, Date end ) 
	throws ExpressionException {
		return addExpression(expression, start, end, Object.class );
	}

	public <T> List<ITimedObject<T>> addExpression(IExpression expression, Date start, Date end, Class<T> toType ) 
	throws ExpressionException {
		String name = expression.getName();
		String script = expression.getExpression();
		List<ITimedObject<T>> values = this.eval(script, start, end, toType );
		if ( name != null ) {
			for (ITimedObject<T> timedObject : values) {
				this.addVariable(name, timedObject );
			}
		}
		return values;
	}

	public List<ITimedObject<Object>> eval(String script, Date start, Date end) throws ExpressionException {
		return eval(script, start, end, Object.class );
	}
	
	public <T> List<ITimedObject<T>>  eval(String script, Date start, Date end, Class<T> toType) throws ExpressionException {
		if ( script == null )
			return Collections.emptyList();
		try {
			ParserContext pCtx = new ParserContext();
			Serializable compiledExpression = MVEL.compileExpression(script, pCtx);
			
			Set<String> inputs = pCtx.getInputs().keySet();
			List<ITimedObject<T>> values = new LinkedList<ITimedObject<T>>();
			List<Map<String, Object>> bindingsList = 
				variables.getBindings(inputs, start, end);
			for (Map<String, Object> bindings : bindingsList) {
				T value = MVEL.executeExpression(compiledExpression, this, bindings, toType);
				values.add(new TimedObject<T>(value, start, end ));
			}
			
			return values;
		} catch (Exception e) {
			throw new ExpressionException(e.getMessage(), e);
		}
	}

	public List<IExpression> getExpressionVariables()  {
		List<IExpression> expressions = new LinkedList<IExpression>();
		return expressions;
	}
	
	// ------------------------------------------
	//
	// ------------------------------------------
	
	public Double ROUND(Double value) {
		return CommonUtil.round(value);
	}
	
	
	// ------------------------------------------
	//
	// ------------------------------------------
	
	
	
}
