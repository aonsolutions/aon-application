package com.esferalia.aon.salary.expression;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
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

import org.apache.commons.lang.StringUtils;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.PropertyAccessException;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.templates.TemplateRuntime;

import com.code.aon.AonVersion;
import com.esferalia.aon.salary.expression.Variables.NotFoundHandler;
import com.esferalia.aon.salary.expression.Variables.PeriodMap;

public class ExpressionContext {

	public static class UnknownUndefVarException extends
			UndefinedVariablesException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	}

	public abstract static class MacroException extends ExpressionException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		public abstract String doMacro(String expr);
	}

	public abstract static class DeferredException extends ExpressionException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		public abstract <T> List<ITimedResult<T>> eval(ExpressionContext context, Class<T> toType)
				throws ExpressionException;

	}

	private static ThreadLocal<PeriodMap> currentBindings = new ThreadLocal<Variables.PeriodMap>();

	private static final Pattern VARIABLE_PATTERN = Pattern
			.compile("[A-Za-z_\u00F1][A-Za-z0-9_\u00D1]*");

	private static final Set<String> RESERVED_WORDS = new HashSet<String>() {
		{
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
	public static Object getVariable(ExpressionContext ctx, String variable,
			Date date) throws RemoveVariableException {
		return ctx.variables.get(variable, new Period(date, date));
	}

	private static class RemoveVariableException extends ExpressionException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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

		public DeferredExpressionException(IExpression expression, Date start,
				Date end) {
			this.end = end;
			this.start = start;
			this.expression = expression;
		}

		@Override
		public <T> List<ITimedResult<T>> eval(ExpressionContext context, Class<T> toType) throws ExpressionException {
			
			List<ITimedResult<T>> results = context.eval(
					expression.getExpression(), start, end, toType);
			
			for (ITimedResult<T> result : results)
				context.putVariable(expression.getName(), result);
			
			return results;
		}
	}

	public static class DeferredExpressionVariable<T> extends
			ExpressionVariable<T> implements ITimedResult<T> {

		public DeferredExpressionVariable(Period p, IExpression expression) {
			super(null, p, expression);
		}

		public DeferredExpressionVariable(Date start, Date end,
				IExpression expression) {
			this(new Period(start, end), expression);
		}

		@Override
		public T getValue() {
			return getValue(getPeriod());
		}

		@Override
		public T getValue(Period period) {
			IExpression expression = getExpression();
			throw new ExpressionExceptionWrapper(
					new DeferredExpressionException(expression, period));
		}

		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return Collections.emptyMap();
		}

	}

	public static class RemovedExpressionVariable<T> extends
			ExpressionVariable<T> implements ITimedResult<T> {

		private String name;

		public RemovedExpressionVariable(String name, Period p,
				IExpression expression) {
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

	public static class UndefinedExpressionVariable<T> extends
			ExpressionVariable<T> implements ITimedResult<T> {

		private String name;

		public UndefinedExpressionVariable(String name, Date start, Date end,
				IExpression expression) {
			this(name, new Period(start, end), expression);
		}

		public UndefinedExpressionVariable(String name, Period p,
				IExpression expression) {
			super(null, p, expression);
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public T getValue() {
			UndefinedVariablesException undefined = new UndefinedVariablesException(
					name);
			throw new ExpressionExceptionWrapper(undefined);
		}

		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return Collections.emptyMap();
		}

	}

	public static Set<String> getVarNames(String script) {
		Set<String> names = new HashSet<String>();
		Matcher matcher = VARIABLE_PATTERN.matcher(script);
		while (matcher.find()) {
			String var = matcher.group();
			if (!RESERVED_WORDS.contains(var)) {
				names.add(var);
			}
			
		}
		return names;
	}

	public static Object eval(String script) {
		return MVEL.eval(script);
	}

	public static <T> T eval(String script, Class<T> type) {
		return MVEL.eval(script, type);
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

	private static void throwExpressionException(PropertyAccessException child)
			throws ExpressionException {
		Throwable parent = child.getCause();
		while (parent != null) {
			if (parent instanceof ExpressionException) {
				throw (ExpressionException) parent;
			}
			parent = parent.getCause();
		}
	}

	private static String getUndefinedProperty(PropertyAccessException e) {
		for (Throwable parent = e.getCause(); parent != null; parent = parent
				.getCause()) {
			if (parent instanceof UnresolveablePropertyException)
				return ((UnresolveablePropertyException) parent).getName();

		}
		return null;
	}

	public static String getUndefinedProperty(PropertyAccessException e,
			PeriodMap bindings) throws UnknownUndefVarException {

		String property = getUndefinedProperty(e);
		if (property != null)
			return property;

//		int end = e.getCursor();
//		do {
//			if (end <= 0)
//				throw new UnknownUndefVarException();
//			while (end-- > 0)
//				if (Character.isJavaIdentifierPart(expr[end]))
//					break;
//			int start = end;
//			while (start >= 0) {
//				if (!Character.isJavaIdentifierPart(expr[start]))
//					break;
//				else
//					start--;
//			}
//			int offset = start + 1;
//			int len = end - offset + 1;
//			property = new String(expr, offset, len);
//			end = start;
//		} while (!isJavaIdentifier(property) || bindings.containsKey(property));
		
		char expr[] = e.getExpr();
		int start = e.getCursor();
		if ( start >= expr.length )
			throw new UnknownUndefVarException();
		
		if ( !Character.isJavaIdentifierStart(expr[start]))
			throw new UnknownUndefVarException();
		
		int end = start  + 1;
		for ( ;end < expr.length && Character.isJavaIdentifierPart(expr[end]); end++ );
		
		return new String(expr, start, end -start);
	}

	private Variables variables;

	public ExpressionContext() {
		this(new Variables(null));
	}

	public ExpressionContext(Variables variables) {
		this.variables = variables;
		initImplicitVariables();
	}

	public ExpressionContext(NotFoundHandler notFoundHandler) {
		this(new Variables(notFoundHandler));
	}

	public ExpressionContext(ExpressionContext expressionContext) {
		this(expressionContext, null);
	}

	public ExpressionContext(ExpressionContext expressionContext,
			NotFoundHandler notFoundHandler) {
		this(new Variables(expressionContext.variables, notFoundHandler));
	}

	public Collection<IExpression> getValues() {
		throw new UnsupportedOperationException();
	}

	public double resolve(IExpression expression) throws ExpressionException {
		throw new UnsupportedOperationException();
	}

	public List<ITimedVariable<?>> putVariable(Object name, ITimedVariable<?> timedVariable) {
		return variables.put(name.toString(), timedVariable);
	}

	public List<ITimedVariable<?>>  setVariable(Object name, Object value, Date start, Date end) {
		ITimedVariable<Object> timedObject = new TimedObject<Object>(value,
				start, end);
		return this.putVariable(name.toString(), timedObject);
	}

	public void removeVariable(Object name) {
		variables.remove(name.toString());
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

	public <T> List<ITimedVariable<T>> getVariables(Object name, Date start,
			Date end) {
		return variables.getVariables(name.toString(), new Period(start, end));
	}

	public <T> T getVariable(Object name, Date start, Date end, Class<T> toType) {
		return (T) variables.get(name.toString(), new Period(start, end));
	}

	public <T> T readVariable(Object name, Date start, Date end, Class<T> toType) {
		return (T) getCurrentBindings().get(name);
	}

	public void add(ExpressionContext ctx) {
		variables.putAll(ctx.variables);
	}

	public List<ITimedResult<Object>> addExpression(IExpression expression,
			Date start, Date end) throws ExpressionException {
		return addExpression(expression, start, end, Object.class);
	}

	public <T> List<ITimedResult<T>> addExpression(IExpression expression,
			Date start, Date end, Class<T> toType) throws ExpressionException {
		String name = expression.getName();
		String script = expression.getExpression();
		try {
			List<ITimedResult<T>> values = this
					.eval(script, start, end, toType);
			if (name != null) {

				for (ITimedResult<T> obj : values) {
					IExpressionVariable<T> var = new ExpressionVariable<T>(
							obj.getValue(), obj.getPeriod(), expression,
							obj.getContext());
					this.putVariable(name, var);
				}
			}
			return values;
		} catch (RemoveVariableException e) {
			Period period = new Period(start, end);
			RemovedExpressionVariable<T> var = new RemovedExpressionVariable<T>(
					name, period, expression);
			this.putVariable(name, var);
			return Collections.singletonList((ITimedResult<T>) var);
		}
	}

	public void addLazyExpression(IExpression expression, Date start, Date end)
			throws ExpressionException {
		String script = expression.getExpression();

		Set<String> inputs = null;

		if (StringUtils.isBlank(script))
			inputs = Collections.emptySet();
		else
			inputs = getVarNames(script);

		List<PeriodMap> bindings = variables.getBindings(inputs, start, end);
		for (PeriodMap periodMap : bindings) {
			Period period = periodMap.getPeriod();
			putVariable(expression.getName(), new LazyExpressionVariable(this,
					expression, period.getStart(), period.getEnd()));
		}

	}

	public <V> void addPullExpression(IExpression expression, Date start,
			Date end, Class<V> toType) throws ExpressionException {
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
						results = ExpressionContext.this.eval(
								expression.getExpression(), period.getStart(),
								period.getEnd(), toType);
						return results.size() > 0 ? results.get(0).getValue()
								: null;
					} catch (ExpressionException e) {
						throw new ExpressionExceptionWrapper(e);
					}
				}
			});
		}

	}

	public List<ITimedResult<Object>> eval(String script, Date start, Date end)
			throws ExpressionException {
		return eval(script, start, end, Object.class);
	}

	public <T> List<ITimedResult<T>> eval(String script, Date start, Date end,
			Class<T> toType) throws ExpressionException,
			UndefinedVariablesException {
		if (script == null) {
			ITimedResult<T> result = (new TimedResult<T>((T) null, new Period(
					start, end),
					Collections.<String, ITimedVariable<?>> emptyMap()));
			return Collections.singletonList(result);
		}
		Set<String> inputs = getVarNames(script);
		List<PeriodMap> bindingsList = variables
				.getBindings(inputs, start, end);
		try {
			return eval(script, bindingsList, toType);
		} catch (MacroException e) {
			return eval(e.doMacro(script), bindingsList, toType);
		} catch (DeferredException e) {
			e.eval(this, toType);
			return eval(script, start, end, toType);
		} catch (UnknownUndefVarException e) {
			return evalUnknowUndefVariable(script, inputs, start, end, toType);
		}

	}

	public String evalTemplate(String template, Date start, Date end) {
		Map<String, Object> vars = variables.getPeriodMap(start, end);
		Object result = TemplateRuntime.eval(template, vars);
		return result != null ? result.toString() : null;
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
		ExpressionContext snapshotContext = new ExpressionContext(
				snapshotVariables);
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

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	private <T> List<ITimedResult<T>> eval(String script,
			List<PeriodMap> bindingsList, Class<T> toType)
			throws ExpressionException {
		if (script == null) {
			return Collections.emptyList();
		}
		List<ITimedResult<T>> values = new LinkedList<ITimedResult<T>>();
		for (PeriodMap bindings : bindingsList) {
			try {
				bindings.cleanRead();
				setCurrentBindings(bindings);
				// Fix MVEL 2.2.4-Final BUG with multiline scripts 
				String fixedScript = script.replaceAll("\\]\\s+\\[", "][");
				T value = MVEL.eval(fixedScript, bindings, toType);
				values.add(new TimedResult<T>(value, bindings.getPeriod(),
						bindings.getRead()));
			} catch (PropertyAccessException e) {
				throwExpressionException(e);
				throw new UndefinedVariablesException(getUndefinedProperty(e,
						bindings));
			} catch (UnresolveablePropertyException e) {
				throw new UndefinedVariablesException(e.getName());
			} catch (ExpressionExceptionWrapper e) {
				throw e.getExpressionException();
			} catch (CompileException e) {
				e.printStackTrace();
				throw e;
			}
		}

		return values;
	}

	private <T> List<ITimedResult<T>> evalUnknowUndefVariable(String script,
			Collection<String> inputs, Date start, Date end, Class<T> toType)
			throws ExpressionException {

		Set<String> vars = new HashSet<String>();
		for (String input : inputs) {
			String regex = String.format("%s\\((.+)\\)", input);
			Matcher matcher = Pattern.compile(regex).matcher(script);

			if (!matcher.find()) {
				vars.add(input);
				continue;
			}

			String repl = String.format("%s(%s, '%s', $1)",
					MethodName.GET_VARIABLE, VariableName.THIS, input);
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
		
		if (string == null || string.length() == 0 || 
				!Character.isJavaIdentifierStart(string.charAt(0)))
			return false;
		for (int i = 1; i < string.length(); i++)
			if (!Character.isJavaIdentifierPart(string.charAt(i)))
				return false;
		return true;
	}

	// ------------------------------------------------------------------------
	

}
