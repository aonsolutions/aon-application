package com.esferalia.aon.salary.expression;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.PropertyAccessException;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.ast.ASTNode;
import org.mvel2.integration.Interceptor;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;

import com.code.aon.AonVersion;
import com.esferalia.aon.salary.expression.Variables.NotFoundHandler;
import com.esferalia.aon.salary.expression.Variables.PeriodMap;

public class ExpressionContext {
	

	public static class RetryExpressionException extends MacroException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		@Override
		public String doMacro(String expr) {
			return expr;
		}
		
	}

	public static class UnknownUndefVarException extends UndefinedVariablesException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	}

	public abstract static class MacroException extends ExpressionException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		public abstract String doMacro(String expr);
		
	}

	public abstract static class DeferredException extends ExpressionException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		public abstract <T> void eval(ExpressionContext context, Class<T> toType)
				throws ExpressionException;

	}
	
	private static class DelegateMap<K,V> implements Map<K,V> {
		
		Map<K,V> map;

		public DelegateMap(Map<K, V> map) {
			this.map = map;
		}

		public void clear() {
			map.clear();
		}

		public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			return map.compute(key, remappingFunction);
		}

		public V computeIfAbsent(K arg0, Function<? super K, ? extends V> arg1) {
			return map.computeIfAbsent(arg0, arg1);
		}

		public V computeIfPresent(K arg0, BiFunction<? super K, ? super V, ? extends V> arg1) {
			return map.computeIfPresent(arg0, arg1);
		}

		public boolean containsKey(Object arg0) {
			return map.containsKey(arg0);
		}

		public boolean containsValue(Object arg0) {
			return map.containsValue(arg0);
		}

		public Set<Entry<K, V>> entrySet() {
			return map.entrySet();
		}

		public boolean equals(Object arg0) {
			return map.equals(arg0);
		}

		public void forEach(BiConsumer<? super K, ? super V> arg0) {
			map.forEach(arg0);
		}

		public V get(Object arg0) {
			return map.get(arg0);
		}

		public V getOrDefault(Object key, V defaultValue) {
			return map.getOrDefault(key, defaultValue);
		}

		public int hashCode() {
			return map.hashCode();
		}

		public boolean isEmpty() {
			return map.isEmpty();
		}

		public Set<K> keySet() {
			return map.keySet();
		}

		public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			return map.merge(key, value, remappingFunction);
		}

		public V put(K arg0, V arg1) {
			return map.put(arg0, arg1);
		}

		public void putAll(Map<? extends K, ? extends V> arg0) {
			map.putAll(arg0);
		}

		public V putIfAbsent(K key, V value) {
			return map.putIfAbsent(key, value);
		}

		public boolean remove(Object key, Object value) {
			return map.remove(key, value);
		}

		public V remove(Object arg0) {
			return map.remove(arg0);
		}

		public boolean replace(K key, V oldValue, V newValue) {
			return map.replace(key, oldValue, newValue);
		}

		public V replace(K key, V value) {
			return map.replace(key, value);
		}

		public void replaceAll(BiFunction<? super K, ? super V, ? extends V> arg0) {
			map.replaceAll(arg0);
		}

		public int size() {
			return map.size();
		}

		public Collection<V> values() {
			return map.values();
		}
		
		
		
	}

	private static ThreadLocal<PeriodMap> currentBindings = new ThreadLocal<Variables.PeriodMap>();

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("[A-Za-z_\u00F1][A-Za-z0-9_\u00D1]*");
	

	private static Pattern CHAINED_OR_PATTERN = Pattern.compile(
			String.format("(%1$s)\\s+(?:O|OR)\\s+(%1$s)%2$s", 
					VARIABLE_PATTERN.pattern(),
					String.format("(?:\\s+(?:O|OR)\\s+(%1$s))?", VARIABLE_PATTERN.pattern()).repeat(10)),
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	private static final Set<String> RESERVED_WORDS = new HashSet<String>() {
		{
			add("O");
			add("isdef");
		}
	};

	static enum VariableName {
		THIS;
	}

	private static enum MethodName {
		GET_VARIABLE, REMOVE_VARIABLE;
	}

	@Target(value = ElementType.METHOD)
	@Retention(value = RetentionPolicy.RUNTIME)
	private static @interface ContextMethod {
		MethodName name();
	}

	@ContextMethod(name = MethodName.REMOVE_VARIABLE)
	public static void removeVariable() throws RemoveVariableException {
		throw new RemoveVariableException();
	}

	@ContextMethod(name = MethodName.GET_VARIABLE)
	public static Object getVariable(ExpressionContext ctx, String variable, Date date) throws RemoveVariableException {
		return ctx.variables.get(variable, new Period(date, date));
	}

	private static class RemoveVariableException extends ExpressionException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	}

	private static class ExpressionResult<V> implements ITimedResult<V>, IExpressionVariable<V> {
		
		IExpression expression;
		ITimedResult<V> result;
		
		public ExpressionResult(ITimedResult<V> result, IExpression expression) {
			this.result = result;
			this.expression = expression;
		}

		@Override
		public IExpression getExpression() {
			return expression;
		}

		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return result.getContext();
		}

		@Override
		public Period getPeriod() {
			return result.getPeriod();
		}
		
		@Override
		public V getValue() {
			return getValue(getPeriod());
		}

		@Override
		public V getValue(Period period) {
			return result.getValue(period);
		}
		
	}

	public static class RemoveVariableError extends Error {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private RemovedExpressionVariable<?> var;

		public RemoveVariableError(RemovedExpressionVariable<?> var) {
			this.var = var;
		}

		public RemovedExpressionVariable<?> getVariable() {
			return var;
		}
	}

	public static class DeferredExpressionException extends DeferredException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private Date end;
		private Date start;
		private IExpression expression;

		public DeferredExpressionException(IExpression expression, Period p) {
			this(expression, p.getStart(), p.getEnd());
		}

		public DeferredExpressionException(IExpression expression, Date start, Date end) {
			this.end = end;
			this.start = start;
			this.expression = expression;
		}
		
		public IExpression getExpression() {
			return expression;
		}

		@Override
		public <T> void eval(ExpressionContext context, Class<T> toType) throws ExpressionException {
			
			
			
			List<ITimedResult<Object>> results = context.eval(expression, start, end, Object.class);
			
			for (ITimedResult<Object> result : results) {
				context.putVariable(expression.getName(), new ExpressionResult<>(result, expression));
			}

			Period deferredPeriod = new Period(start, end);
			List<Period> calculatedPeriods =results.stream().map(r -> r.getPeriod()).collect(Collectors.toList());
			
			Period.sub(deferredPeriod, calculatedPeriods)
			.forEach(p -> {
				context.getVariables(expression.getName(), p.getStart(), p.getEnd()).forEach( v -> {
					context.subVariable(expression.getName(), v.getPeriod().getStart(), v.getPeriod().getEnd());
				});
			});
			
		}
		
	}

	public static class DeferredExpressionVariable<T> extends ExpressionVariable<T> implements ITimedResult<T> {

		public DeferredExpressionVariable(Period p, IExpression expression) {
			super(null, p, expression);
		}

		public DeferredExpressionVariable(Date start, Date end, IExpression expression) {
			this(new Period(start, end), expression);
		}

		@Override
		public T getValue() {
			return getValue(getPeriod());
		}

		@Override
		public T getValue(Period period) {
			IExpression expression = getExpression();
			throw new ExpressionExceptionWrapper(new DeferredExpressionException(expression, period));
		}

		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return Collections.emptyMap();
		}

	}

	public static class RemovedExpressionVariable<T> extends ExpressionVariable<T> implements ITimedResult<T> {

		private String name;

		public RemovedExpressionVariable(String name, Period p, IExpression expression) {
			super(null, p, expression);
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public T getValue() {
			throw new RemoveVariableError(this);

		}

		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return Collections.emptyMap();
		}

	}

	public static class UndefinedExpressionVariable<T> extends ExpressionVariable<T> implements ITimedResult<T> {

		private String name;

		public UndefinedExpressionVariable(String name, Date start, Date end, IExpression expression) {
			this(name, new Period(start, end), expression);
		}

		public UndefinedExpressionVariable(String name, Period p, IExpression expression) {
			super(null, p, expression);
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public T getValue() {
			UndefinedVariablesException undefined = new UndefinedVariablesException(name);
			throw new ExpressionExceptionWrapper(undefined);
		}

		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return Collections.emptyMap();
		}

	}

	public static class TimedConstant<V> extends TimedObject<V> implements IConstantVariable {

		public TimedConstant(V value, Date start, Date end) {
			super(value, start, end);
		}

		public TimedConstant(V value, Period period) {
			super(value, period);
		}

	}

	public static class ExpressionConstant<V> extends ExpressionVariable<V> implements IConstantVariable {

		public ExpressionConstant(V value, Period p, IExpression expression, Map<String, ITimedVariable<?>> context) {
			super(value, p, expression, context);
		}

		public ExpressionConstant(V value, Period p, IExpression expression) {
			super(value, p, expression);
		}

	}

	public static class DaysVariable implements ITimedObject<Long> , ITimedVariable<Long> {
		
		Period period;
		
		public DaysVariable(Date start, Date end) {
			this( new Period(start, end) );
		}

		public DaysVariable(Period period) {
			this.period = period;
		}
		
		@Override
		public Period getPeriod() {
			return period;
		}
		
		@Override
		public Long getValue() {
			return period.getDays();
		}

		@Override
		public Long getValue(Period period) {
			Period intersect = period.intersect(this.period);
			if ( intersect == null )
				return 0L;
			return intersect.getDays();
		}
	}

	public static Set<String> getVarNames(String script) {
		Set<String> names = new HashSet<String>();
		// clean strings literals
		script = script.replaceAll("'[^']*'", "");
		script = script.replaceAll("\"[^\"]*\"", "");

		Matcher matcher = VARIABLE_PATTERN.matcher(script);
		while (matcher.find()) {
			String var = matcher.group();
			if (!RESERVED_WORDS.contains(var)) {
				names.add(var);
			}

		}
		return names;
	}

	public static Set<String> getInputs(String script) {
		Set<String> names = new HashSet<String>();
		ParserContext ctx = new ParserContext();
		MVEL.compileExpression(script, ctx);
		names.addAll(ctx.getInputs().keySet());
		names.addAll(ctx.getFunctions().keySet());
		return names;
	}

	public static Object eval(String script) {
		return MVEL.eval(script);
	}

	public static <T> T eval(String script, Class<T> type) {
		return MVEL.eval(script, type);
	}

	public static <T> T eval(String script, Map<String,Object> context, Class<T> type) {
		return MVEL.eval(script, context, type);
	}

	public static class ExpressionExceptionWrapper extends RuntimeException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		public ExpressionExceptionWrapper(ExpressionException e) {
			super(e);
		}

		public ExpressionException getExpressionException() {
			return (ExpressionException) getCause();
		}
	}

	private static void throwExpressionException(PropertyAccessException child) throws ExpressionException {
		Throwable parent = child.getCause();
		while (parent != null) {
			if (parent instanceof ExpressionException) {
				throw (ExpressionException) parent;
			}
			parent = parent.getCause();
		}
	}


	private static String getUndefinedProperty(PropertyAccessException e) {
		for (Throwable parent = e.getCause(); parent != null; parent = parent.getCause()) {
			if (parent instanceof UnresolveablePropertyException)
				return ((UnresolveablePropertyException) parent).getName();

		}
		return null;
	}

	private static String getUnresolvableProperty(PropertyAccessException e) 
			throws UnknownUndefVarException {
		e.setCursor(0);
		String message = e.getMessage();
		
		Matcher matcher = 
		Pattern.compile("unresolvable\\s*property\\s*or\\s*identifier\\s*:\\s*([_a-zA-Z]+)")
		.matcher(message);
	
		if( !matcher.find() )
			throw new UnknownUndefVarException();
		return matcher.group(1);
	}

	public static String getUndefinedProperty(PropertyAccessException e, PeriodMap bindings)
			throws UnknownUndefVarException {

		String property = getUndefinedProperty(e);
		if (property != null)
			return property;

		// int end = e.getCursor();
		// do {
		// if (end <= 0)
		// throw new UnknownUndefVarException();
		// while (end-- > 0)
		// if (Character.isJavaIdentifierPart(expr[end]))
		// break;
		// int start = end;
		// while (start >= 0) {
		// if (!Character.isJavaIdentifierPart(expr[start]))
		// break;
		// else
		// start--;
		// }
		// int offset = start + 1;
		// int len = end - offset + 1;
		// property = new String(expr, offset, len);
		// end = start;
		// } while (!isJavaIdentifier(property) ||
		// bindings.containsKey(property));

		char expr[] = e.getExpr();
		int start = e.getCursor();
		
		if (start >= expr.length)
			throw new UnknownUndefVarException();
		if (start < 0)
			return getUnresolvableProperty(e);

		if (!Character.isJavaIdentifierStart(expr[start]))
			throw new UnknownUndefVarException();

		int end = start + 1;
		for (; end < expr.length && Character.isJavaIdentifierPart(expr[end]); end++)
			;

		return new String(expr, start, end - start);
	}

	private Set<String> read;
	private Variables variables;

	public ExpressionContext() {
		this(new Variables(null));
	}

	public ExpressionContext(Variables variables) {
		this.variables = variables;
		initImplicitVariables();
		this.read = new HashSet<String>();
	}

	public ExpressionContext(NotFoundHandler notFoundHandler) {
		this(new Variables(notFoundHandler));
	}

	public ExpressionContext(ExpressionContext expressionContext) {
		this(expressionContext, null);
	}

	public ExpressionContext(ExpressionContext expressionContext, NotFoundHandler notFoundHandler) {
		this(new Variables(expressionContext.variables, notFoundHandler));
	}

	public Collection<IExpression> getValues() {
		throw new UnsupportedOperationException();
	}

	public double resolve(IExpression expression) throws ExpressionException {
		throw new UnsupportedOperationException();
	}

	public List<ITimedVariable<?>> putVariable(Object name, ITimedVariable<?> timedVariable) {
		if ( name.equals("SALARIO_BASE") ) {
			System.err.println("ExpressionContext.setVariable " + name + " = " + timedVariable.getValue(timedVariable.getPeriod()) + " [" + timedVariable.getPeriod().getStart() + ", " + timedVariable.getPeriod().getEnd() + "]");
			Thread.dumpStack();
		}
		return variables.put(name.toString(), timedVariable);
	}

	public List<ITimedVariable<?>> setVariable(Object name, Object value, Date start, Date end) {
		ITimedVariable<Object> timedObject = new TimedConstant<Object>(value, start, end);
		return this.putVariable(name.toString(), timedObject);
	}

	public void removeVariable(Object name) {
		variables.remove(name.toString());
	}

	public void subVariable(Object name, Date start, Date end) {
		Period period = new Period(start, end);
		variables.sub(name.toString(), period);
	}

	public void removeVariable(Object name, Date start, Date end) {
		Period period = new Period(start, end);
		variables.remove(name.toString(), period);
	}

	public boolean isDef(Object name) {
		return variables.containsKey(name.toString());
	}

	public boolean containsVariable(Object name, Date start, Date end) {
		return variables.containsKey(name.toString(), new Period(start, end));
	}

	public ITimedVariable<?> getVariable(Object name, Date start, Date end) {
		return variables.getVariable(name.toString(), new Period(start, end));
	}

	public List<Period> getPeriods(Object name) {
		return variables.getPeriods(name.toString());
	}

	public <T> List<ITimedVariable<T>> getVariables(Object name) {
		return variables.getVariables(name.toString());
	}

	public <T> List<ITimedVariable<T>> getVariables(Object name, Date start, Date end) {
		return variables.getVariables(name.toString(), new Period(start, end));
	}

	public <T> T getVariable(Object name, Date start, Date end, Class<T> toType) {
		return (T) variables.get(name.toString(), new Period(start, end));
	}
	
	public boolean isRead(Object ...names) {
		for (Object name : names)
			if ( read.contains(name))
				return getCurrentBindings().containsKey(name);
		
		return false;
	}
	
	public Map<String, ITimedVariable<?>>  getRead() {
		return getCurrentBindings().getRead();
	}

	public <T> T readVariable(Object name, Date start, Date end, Class<T> toType) {
		read.add(name.toString());
		return (T) getCurrentBindings().get(name);
	}

	public void add(ExpressionContext ctx) {
		variables.putAll(ctx.variables);
	}

	public List<ITimedResult<Object>> addExpression(IExpression expression, Date start, Date end)
			throws ExpressionException {
		return addExpression(expression, start, end, Object.class);
	}

	public <T> List<ITimedResult<T>> addExpression(IExpression expression, Date start, Date end, Class<T> toType)
			throws ExpressionException {
		String name = expression.getName();
		String script = expression.getExpression();
		try {
			List<ITimedResult<T>> values = this.eval(script, start, end, toType);
			if (name != null) {

				for (ITimedResult<T> obj : values) {
					IExpressionVariable<T> var = new ExpressionConstant<T>(obj.getValue(), obj.getPeriod(), expression,
							obj.getContext());
					this.putVariable(name, var);
				}
			}
			return values;
		} catch (RemoveVariableException e) {
			Period period = new Period(start, end);
			RemovedExpressionVariable<T> var = new RemovedExpressionVariable<T>(name, period, expression);
			this.putVariable(name, var);
			return Collections.singletonList((ITimedResult<T>) var);
		}
	}

	public List<LazyExpressionVariable> addLazyExpression(IExpression expression, Date start, Date end) {
		String script = expression.getExpression();

		Set<String> inputs = null;

		if (StringUtils.isBlank(script))
			inputs = Collections.emptySet();
		else
			inputs = getVarNames(script);
		
		List<PeriodMap> bindings = variables.getBindings(inputs, start, end);
		List<LazyExpressionVariable> lazyExpressionVariables = new ArrayList<LazyExpressionVariable>(bindings.size());
		for (PeriodMap periodMap : bindings) {
			Period period = periodMap.getPeriod();
			LazyExpressionVariable lazyExpressionVariable = new LazyExpressionVariable(this, expression, period.getStart(), period.getEnd());
			putVariable(expression.getName(),lazyExpressionVariable);
			lazyExpressionVariables.add(lazyExpressionVariable);
		}
		return lazyExpressionVariables;
	}

	public <V> void addPullExpression(IExpression expression, Date start, Date end, Class<V> toType)
			throws ExpressionException {
		String script = expression.getExpression();

		Set<String> inputs = null;

		if (StringUtils.isBlank(script))
			inputs = Collections.emptySet();
		else
			inputs = getVarNames(script);

		List<PeriodMap> bindings = variables.getBindings(inputs, start, end);
		for (PeriodMap periodMap : bindings) {

			putVariable(expression.getName(), new ITimedVariable<V>() {

				Period period = periodMap.getPeriod();

				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public V getValue(Period period) {
					List<ITimedResult<V>> results;
					try {
						results = ExpressionContext.this.eval(expression.getExpression(), period.getStart(),
								period.getEnd(), toType);
						return results.size() > 0 ? results.get(0).getValue() : null;
					} catch (ExpressionException e) {
						throw new ExpressionExceptionWrapper(e);
					}
				}
			});
		}

	}

	public List<ITimedResult<Object>> eval(String script, Date start, Date end) throws ExpressionException {
		return eval(script, start, end, Object.class);
	}

	public <T> List<ITimedResult<T>> eval(String script, Date start, Date end, Class<T> toType)
			throws ExpressionException, UndefinedVariablesException {
		if (script == null) {
			ITimedResult<T> result = (new TimedResult<T>((T) null, new Period(start, end),
					Collections.<String, ITimedVariable<?>> emptyMap()));
			return Collections.singletonList(result);
		}
		Set<String> inputs = getVarNames(script);
		List<PeriodMap> bindingsList = variables.getBindings(inputs, start, end);
		try {

			do {
				try {
					return eval(script, bindingsList, toType);
				} catch (MacroException e) {
					script = e.doMacro(script);
					inputs = getVarNames(script);
					bindingsList = variables.getBindings(inputs, start, end);
				}
			} while (true);

		} catch (DeferredException e) {
			e.eval(this, toType);
			return eval(script, start, end, toType);
		} catch (UnknownUndefVarException e) {
			return evalUnknowUndefVariable(script, inputs, start, end, toType);
		} catch (CompileException e) {
			throw e;
		} catch ( ExpressionExceptionWrapper e) {
			throw e.getExpressionException();
		}
	}
	
	public List<ITimedResult<Object>> dryEval(String script, Date start, Date end)
			throws ExpressionException, UndefinedVariablesException {
		return dryEval(script, start, end, Object.class);
	}

	public <T> List<ITimedResult<T>> dryEval(String script, Date start, Date end, Class<T> toType)
			throws ExpressionException, UndefinedVariablesException {
		if (script == null) {
			ITimedResult<T> result = (new TimedResult<T>((T) null, new Period(start, end),
					Collections.<String, ITimedVariable<?>> emptyMap()));
			return Collections.singletonList(result);
		}
		Set<String> inputs = getVarNames(script);
		List<PeriodMap> bindingsList = variables.getBindings(inputs, start, end);
		try {

			do {
				try {
					return eval(script, bindingsList, toType);
				} catch (MacroException e) {
					script = e.doMacro(script);
					inputs = getVarNames(script);
					bindingsList = variables.getBindings(inputs, start, end);
				}
			} while (true);

		} catch (DeferredException e) {
			ExpressionContext dryCtx = new ExpressionContext(this);
			e.eval(dryCtx, toType);
			return dryCtx.dryEval(script, start, end, toType);
		} catch (UnknownUndefVarException e) {
			return evalUnknowUndefVariable(script, inputs, start, end, toType);
		} catch (CompileException e) {
			throw e;
		} catch ( ExpressionExceptionWrapper e) {
			throw e.getExpressionException();
		}
	}
	


	public String evalTemplate(String template, Date start, Date end) throws ExpressionException {
		try {
			Map<String, Object> vars = variables.getPeriodMap(start, end);
//			Map<String, Object> printVars = new DelegateMap<String,Object>(vars){
//				@Override
//				public Object get(Object arg0) {
//					Object object = super.get(arg0);
//					
//					if ( object == null )
//						return "";
//					if ( object instanceof Float )
//						return Math.round((( Float) object) * 100.00 )/  100.00;
//					if ( object instanceof Double )
//						return Math.round((( Double) object) * 100.00 )/  100.00;
//					
//					return object;
//				}
//			};
			
			Object result = TemplateRuntime.eval(template, vars);
			
			return result != null ? round(result.toString()) : null;
		} catch (PropertyAccessException e) {
			
			for ( Throwable t = e.getCause(); t != null ; t = t.getCause() )
				if ( t instanceof MacroException macroException )
					return evalTemplate(macroException.doMacro(template), start, end );
				
			throwExpressionException(e);
			throw new UndefinedVariablesException(getUndefinedProperty(e, (PeriodMap) null));
		} catch (UnresolveablePropertyException e) {
			throw new UndefinedVariablesException(e.getName());
		} catch (ExpressionExceptionWrapper e) {
			throw e.getExpressionException();
		} catch (CompileException e) {
			throw e;
		}
	}

	public List<IExpression> getExpressionVariables() {
		List<IExpression> expressions = new LinkedList<IExpression>();
		return expressions;
	}

	public Set<String> variablesSet() {
		return variables.varsSet();
	}

	public Set<String> declaredVariablesSet() {
		return variables.varsSet();
	}

	public void clear() {
		variables.clear();
	}

	public ExpressionContext getSnapshot(Set<String> vars) {
		Variables snapshotVariables = variables.getSnapshot(vars);
		ExpressionContext snapshotContext = new ExpressionContext(snapshotVariables);
		return snapshotContext;

	}

	public List<ITimedVariable<?>> getTimedVariables(String var) {
		return variables.get(var);
	}

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	@Override
	protected void finalize() throws Throwable {
		clear();
		super.finalize();
	}
	
	protected List<PeriodMap> getBindingsNew(Set<String> vars, Date start, Date end) throws UndefinedVariablesException {
		return variables.getBindingsNew(vars, start, end);
	}
	
	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	private <T> List<ITimedResult<T>> eval(IExpression expression, Date start, Date end, Class<T> toType)
			throws ExpressionException, UndefinedVariablesException {
		String script = expression.getExpression();
		
		if (script == null) {
			ITimedResult<T> result = (new TimedResult<T>((T) null, new Period(start, end),
					Collections.<String, ITimedVariable<?>> emptyMap()));
			return Collections.singletonList(result);
		}
		Set<String> inputs = getVarNames(script);
		List<PeriodMap> bindingsList = variables.getBindings(inputs, start, end);
		try {

			do {
				try {
					return eval(script, bindingsList, toType);
				} catch (MacroException e) {
					script = e.doMacro(script);
					inputs = getVarNames(script);
					bindingsList = variables.getBindings(inputs, start, end);
				}
			} while (true);

		} catch (DeferredExpressionException e) {
			if ( e.getExpression() == expression )
				throw new UndefinedVariablesException(this.getRead(), script, expression.getName());
			e.eval(this, toType);
			return eval(script, start, end, toType);
		} catch (DeferredException e) {
			e.eval(this, toType);
			return eval(script, start, end, toType);
		} catch (UnknownUndefVarException e) {
			return evalUnknowUndefVariable(script, inputs, start, end, toType);
		} catch (CompileException e) {
			throw e;
		} catch ( ExpressionExceptionWrapper e) {
			throw e.getExpressionException();
		}

	}

	private <T> List<ITimedResult<T>> eval(String script, List<PeriodMap> bindingsList, Class<T> toType)
			throws ExpressionException {
		if (script == null) {
			return Collections.emptyList();
		}
		
		PeriodMap parentBindings = getCurrentBindings();

		List<ITimedResult<T>> values = new LinkedList<ITimedResult<T>>();
		for (PeriodMap bindings : bindingsList) {
			try {
				bindings.cleanRead();
				setCurrentBindings(bindings);
				// Fix MVEL 2.2.4-Final BUG with multiline scripts
				String fixedScript = script.replaceAll("\\]\\s+\\[", "][");
				
				fixedScript = chainedOr2Isdef(fixedScript);
				
				T value = MVEL.eval(fixedScript, bindings, toType);
				values.add(new TimedResult<T>(value, bindings.getPeriod(), bindings.getRead()));
			} catch (PropertyAccessException e) {
				tryDeferredException(e, parentBindings, script);
				// throwExpressionException(e);
				// throw new UndefinedVariablesException(getUndefinedProperty(e, bindings));
			} catch (UnresolveablePropertyException e) {
				throw new UndefinedVariablesException(this.getRead(), e.getName());
			} catch (ExpressionExceptionWrapper e) {
				throw e.getExpressionException();
			} catch (CompileException e) {
				throw e;
			} finally{
				if ( parentBindings != null ) {
					setCurrentBindings(parentBindings);
				}
			}
		}
		read(values);
		return values;
	}
	private void tryDeferredException(PropertyAccessException child, PeriodMap bindings, String script) throws ExpressionException {
		try {
			throwExpressionException(child);
		}catch ( DeferredExpressionException e ) {
			if ( e.getExpression().getExpression().equals(script) )
				throw new UndefinedVariablesException(this.getRead(), script, e.getExpression().getName());
			e.eval(this, Object.class);
			throw new RetryExpressionException();
		}catch ( DeferredException e ) {
			e.eval(this, Object.class);
			throw new RetryExpressionException();
		}
		throw new UndefinedVariablesException(this.getRead(), getUndefinedProperty(child, bindings));
	}


	private <T> List<ITimedResult<T>> evalUnknowUndefVariable(String script, Collection<String> inputs, Date start,
			Date end, Class<T> toType) throws ExpressionException {

		Set<String> vars = new HashSet<String>();
		for (String input : inputs) {
			String regex = String.format("%s\\((.+)\\)", input);
			Matcher matcher = Pattern.compile(regex).matcher(script);

			if (!matcher.find()) {
				vars.add(input);
				continue;
			}

			String repl = String.format("%s(%s, '%s', $1)", MethodName.GET_VARIABLE, VariableName.THIS, input);
			StringBuffer sb = new StringBuffer();
			do {
				matcher.appendReplacement(sb, repl);
			} while (matcher.find());
			matcher.appendTail(sb);

			script = sb.toString();
		}

		List<PeriodMap> bindingsList = variables.getBindings(vars, start, end);

		return eval(script, bindingsList, toType);

	}

	private void initImplicitVariables() {

		Date epoch = new Date(0); // January 1, 1970, 00:00:00

		setVariable(VariableName.THIS, this, epoch, null);

		for (Method method : ExpressionContext.class.getDeclaredMethods()) {
			ContextMethod implicit = method.getAnnotation(ContextMethod.class);
			if (implicit != null)
				setVariable(implicit.name(), method, epoch, null);
		}
	}

	private  <T> void read(List<ITimedResult<T>> results) {
		results.forEach(result -> read.addAll(result.getContext().keySet()));
	}
	
	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------

	public static Period getCurrentPeriod() {
		return currentBindings.get().getPeriod();
	}

	public static PeriodMap getCurrentBindings() {
		return currentBindings.get();
	}

	private static void setCurrentBindings(PeriodMap map) {
		currentBindings.set(map);
	}

	public static Set<String> getVariableSet(String script) {

		if (StringUtils.isBlank(script))
			return Collections.emptySet();

		ParserContext ctx = new ParserContext();
		
		MVEL.analysisCompile(script, ctx);
		

		Set<String> variables = new HashSet<String>();

		for (String input : ctx.getInputs().keySet()) {
			if (isJavaIdentifier(input))
				variables.add(input);
		}

		ctx.getVariables();

		return variables;

	}

	public static Set<String> getVariableSet(String... scripts) {
		StringBuffer buffer = new StringBuffer();
		for (String script : scripts) {
			buffer.append(script + ";");
		}
		return getVariableSet(buffer.toString());
	}

	private static boolean isJavaIdentifier(String string) {

		if (string == null || string.length() == 0 || !Character.isJavaIdentifierStart(string.charAt(0)))
			return false;
		for (int i = 1; i < string.length(); i++)
			if (!Character.isJavaIdentifierPart(string.charAt(i)))
				return false;
		return true;
	}

	private static String round(String str) {
		return str.replaceAll("([0-9,]+\\.[0-9]{2})[0-9]+", "$1");
	}

	public static String evalTemplate(String template, Variables vars) {

		Map<String, Object> map = new AbstractMap<String, Object>(){

			@Override
			public Set<Entry<String, Object>> entrySet() {
				return
				vars.varsSet().stream().map(name -> new Entry<String, Object>() {

					@Override
					public String getKey() {
						return name;
					}

					@Override
					public Object getValue() {

						double sum = 						
						vars.get(name).stream()
						.map(v -> v.getValue(v.getPeriod()))
						.filter(Number.class::isInstance)
						.mapToDouble(v -> ((Number)v).doubleValue() )
						.sum();
						if ( sum % 1 == 0.00 ) 
							return Math.round(sum);
						else 
							return sum;
					}

					@Override
					public Object setValue(Object value) {
						throw new UnsupportedOperationException();
					}
				}).collect(Collectors.toSet());
			}
			
		};
		
		Object result = TemplateRuntime.eval(template, map);
		
		return result != null ? round(result.toString()) : null;
	}
	
	public static String evalTemplate(String template, Map<String,Object> map) {
		Object result = TemplateRuntime.eval(template, map);
		return result != null ? round(result.toString()) : null;
	}
	
	public static String chainedOr2Isdef(String expression) {
		int start = 0;
		Matcher matcher = CHAINED_OR_PATTERN.matcher(expression);
		StringBuilder expressionBuilder = new StringBuilder();
		while (matcher.find()) {
			
			int[] groups = IntStream.rangeClosed(1, matcher.groupCount()).filter(group -> matcher.group(group) != null)
					.toArray();

			String isDefExpression = "" ;
			
			int i = groups.length -1 ;
			if ( i >= 0 ) {
				isDefExpression = matcher.group(groups[i--]);
			}

			for (; i >= 0; i--) {
				String variable = matcher.group(groups[i]);
				isDefExpression = String.format("isdef %1$s ? %1$s : ( %2$s ) ", variable, isDefExpression );
				
			}

			expressionBuilder.append(expression.substring(start, matcher.start())).append(isDefExpression);
			
			
			start = matcher.end();
		}
		expressionBuilder.append(expression.substring(start));
		return expressionBuilder.toString();
	}


}
