package com.esferalia.aon.salary.expression;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mvel2.MVEL;
import org.mvel2.PropertyAccessException;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.compiler.CompiledAccExpression;
import org.mvel2.templates.TemplateRuntime;

import com.esferalia.aon.salary.expression.Variables.PeriodMap;


public class ExpressionContext {
	
	
	Variables variables;
	
	
	public  static Set<String> getVariables(String script) {
		Set<String> names = new HashSet<String>(); 
		Matcher matcher = VARIABLE_PATTERN.matcher(script);
		while ( matcher.find() ) {
			names.add(matcher.group());
		}
		return names;
	}


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

	public void addVariable(Object name, ITimedVariable<?> timedVariable) {
		variables.put(name.toString(), timedVariable);
	}

	public void addVariable(Object name, Object value, Date start, Date end) {
		ITimedVariable<Object> timedObject = 
			new TimedObject<Object>(value, start, end );
		this.addVariable(name.toString(), timedObject);
	}
	
	public boolean containsVariable(Object name, Date start, Date end) {
		return variables.containsKey(name.toString(), new Period(start, end));
	}

	public <T> T getVariable(Object name, Date start, Date end,Class<T> toType   ) {
		return ( T ) variables.get(name.toString(), new Period(start, end));
	}
	
	public List<ITimedObject<Object>> addExpression(IExpression expression,Date start, Date end ) 
	throws ExpressionException {
		return addExpression(expression, start, end, Object.class );
	}

	public <T> List<ITimedObject<T>> addExpression(IExpression expression, Date start, Date end, Class<T> toType ) 
	throws ExpressionException 
	{
		String name = expression.getName();
		String script = expression.getExpression();
		List<ITimedObject<T>> values = this.eval(script, start, end, toType );
		if ( name != null ) {
			for (ITimedObject<T> timedObject : values) {
				this.addVariable(name, ( TimedObject<T> ) timedObject );
			}
		}
		return values;
	}

	public List<ITimedObject<Object>> eval(String script, Date start, Date end) 
		throws ExpressionException 
	{
		return eval(script, start, end, Object.class );
	}
	

	public <T> List<ITimedObject<T>>  eval(String script, Date start, Date end, Class<T> toType) 
		throws ExpressionException 
	{
		if ( script == null ) {
			return Collections.emptyList();
		}
		
		Set<String> inputs = getVariables(script);
		List<PeriodMap> bindingsList = 
			variables.getBindings(inputs, start, end);
		List<ITimedObject<T>> values = 
			new LinkedList<ITimedObject<T>>();
		for (PeriodMap bindings : bindingsList) {
			try {
				T value = MVEL.eval(script, bindings, toType);
				values.add(new TimedObject<T>(value, bindings.getPeriod()));
			} catch ( UnresolveablePropertyException e ) {
				throw new UndefinedVariableException(e.getName(), e.getLocalizedMessage());
			} catch ( PropertyAccessException e ) {
				throw new UndefinedVariableException("", e.getLocalizedMessage());
			}
		}
		
		return values;
	}
	
	public String evalTemplate(String template, Date start, Date end ) {
		Map<String, Object> vars = variables.getPeriodMap(start, end);
		Object result = TemplateRuntime.eval(template, vars);
		return result != null ? result.toString() : null;
	}

	public List<IExpression> getExpressionVariables()  {
		List<IExpression> expressions = new LinkedList<IExpression>();
		return expressions;
	}
	
	public Set<String> variablesSet()  {
		return variables.varsSet();
	}
	
	
	public void clear() {
		variables.clear();
	}
	
	// ------------------------------------------
	//
	// ------------------------------------------
	
	@Override
	protected void finalize() throws Throwable {
		clear();
		super.finalize();
	}
	
	
	//Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
	private static final Pattern VARIABLE_PATTERN = 
		Pattern.compile("[A-Z_][A-Z0-9_]*");
	
	
	public static void main(String[] args) throws SecurityException, NoSuchMethodException {
		org.mvel2.compiler.CompiledAccExpression expr;
		
		System.out.println( MVEL.compileGetExpression("A + B +C +F(1000) + ( X='G'?:100:0.00)") );
		
		System.out.println( getVariables("A + B +C +F(1000) + ( X='G'?:100:0.00)") );

	}
	
}
