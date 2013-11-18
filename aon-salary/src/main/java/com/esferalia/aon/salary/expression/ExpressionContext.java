package com.esferalia.aon.salary.expression;

import java.lang.reflect.Method;
import java.util.Calendar;
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

import org.mvel2.CompileException;
import org.mvel2.ErrorDetail;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.PropertyAccessException;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.util.MethodStub;

import com.esferalia.aon.salary.expression.Variables.NotFoundHandler;
import com.esferalia.aon.salary.expression.Variables.PeriodMap;

public class ExpressionContext {

	public static final String REMOVE_VARIABLE = "REMOVE_VARIABLE()";
	private static final String REMOVE_VARIABLE_STUB = "REMOVE_VARIABLE";

	private static final Pattern VARIABLE_PATTERN = Pattern
			.compile("[A-Za-z_][A-Za-z0-9_]*");

	private static final Set<String> RESERVED_WORDS = new HashSet<String>() {
		{
			add("isdef");
		}
	};

	public static void removeVariable() throws RemoveVariableException {
		throw new RemoveVariableException();
	}

	private static class RemoveVariableException extends ExpressionException {
	}

	public static class RemoveVariableError extends Error {

		private RemovedExpressionVariable<?> var;

		public RemoveVariableError(RemovedExpressionVariable<?> var) {
			this.var = var;
		}

		public RemovedExpressionVariable<?> getVariable() {
			return var;
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

	public static Set<String> getVariables(String script) {
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

	public static void analyze(String script) {
		ParserContext ctx = new ParserContext();
		MVEL.analysisCompile(script, ctx);

		if (ctx.getErrorList() != null)
			throw new CompileException("Failed to compile: "
					+ ctx.getErrorList().size() + " compilation error(s): ",
					ctx.getErrorList());

		Map<String, Object> vars = new HashMap<String, Object>();
		for (String input : ctx.getInputs().keySet())
			vars.put(input, false);

		MVEL.eval(script, vars);
	}

	public static class ExpressionExceptionWrapper extends RuntimeException {
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

	private static String getUndefinedProperty(PropertyAccessException e,
			PeriodMap bindings) {
		String property = null;

		char expr[] = e.getExpr();
		int end = e.getCursor();
		do {
			while (end-- >= 0)
				if (Character.isJavaIdentifierPart(expr[end]))
					break;
			int start = end;
			while (start >= 0) {
				if (!Character.isJavaIdentifierPart(expr[start]))
					break;
				else
					start--;
			}
			int offset = start + 1;
			int len = end - offset + 1;
			property = new String(expr, offset, len);
		} while (bindings.containsKey(property));

		return property;
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

	public void addVariable(Object name, ITimedVariable<?> timedVariable) {
		variables.put(name.toString(), timedVariable);
	}

	public void addVariable(Object name, Object value, Date start, Date end) {
		ITimedVariable<Object> timedObject = new TimedObject<Object>(value,
				start, end);
		this.addVariable(name.toString(), timedObject);
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

	public <T> T getVariable(Object name, Date start, Date end, Class<T> toType) {
		return (T) variables.get(name.toString(), new Period(start, end));
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
					this.addVariable(name, var);
				}
			}
			return values;
		} catch (RemoveVariableException e) {
			Period period = new Period(start, end);
			RemovedExpressionVariable<T> var = new RemovedExpressionVariable<T>(
					name, period, expression);
			this.addVariable(name, var);
			return Collections.singletonList((ITimedResult<T>) var);
		}
	}

	public List<ITimedResult<Object>> eval(String script, Date start, Date end)
			throws ExpressionException {
		return eval(script, start, end, Object.class);
	}

	public <T> List<ITimedResult<T>> eval(String script, Date start, Date end,
			Class<T> toType) throws ExpressionException {
		if (script == null) {
			return Collections.emptyList();
		}

		Set<String> inputs = getVariables(script);
		List<PeriodMap> bindingsList = variables
				.getBindings(inputs, start, end);
		List<ITimedResult<T>> values = new LinkedList<ITimedResult<T>>();
		for (PeriodMap bindings : bindingsList) {
			try {
				T value = MVEL.eval(script, bindings, toType);
				values.add(new TimedResult<T>(value, bindings.getPeriod(),
						bindings.getRead()));
			} catch (PropertyAccessException e) {
				throwExpressionException(e);
				throw new UndefinedVariablesException(getUndefinedProperty(e,
						bindings));
			} /*
			 * catch (RemoveVariableError e) { throw new
			 * UndefinedVariablesException(e.getName()); }
			 */catch (UnresolveablePropertyException e) {
				throw new UndefinedVariablesException(e.getName());
			} catch (ExpressionExceptionWrapper e) {
				throw e.getExpressionException();
			}
		}

		return values;
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

	// ------------------------------------------
	//
	// ------------------------------------------
	@Override
	protected void finalize() throws Throwable {
		clear();
		super.finalize();
	}

	// ------------------------------------------
	//
	// ------------------------------------------
	private void initImplicitVariables() {
		try {
			if (!isDef(REMOVE_VARIABLE_STUB)) {

				Method removeVariable = ExpressionContext.class
						.getDeclaredMethod("removeVariable");
				MethodStub removeVariableStub = new MethodStub(removeVariable);

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(0); // EPOCH
				Date startDate = calendar.getTime();

				addVariable(REMOVE_VARIABLE_STUB, removeVariableStub,
						startDate, null);
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Set<String> getVariableSet(String script) {
		ParserContext ctx = new ParserContext();
		MVEL.analysisCompile(script, ctx);

		Set<String> variables = new HashSet<String>();

		for (String input : ctx.getInputs().keySet()) {
			if (isJavaIdentifier(input))
				variables.add(input);
		}

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
		if (!Character.isJavaIdentifierStart(string.charAt(0)))
			return false;
		for (int i = 1; i < string.length(); i++)
			if (!Character.isJavaIdentifierPart(string.charAt(i)))
				return false;
		return true;
	}

	public static void main(String[] args) throws Exception {
		String expression = "X = 'HOLA' ? 10 / J : 100.00  ";
		ParserContext ctx = new ParserContext();
		try {
			MVEL.analysisCompile(expression, ctx);
			for (Map.Entry<String, Class> var : ctx.getVariables().entrySet())
				System.out.println(var.getKey() + " = " + var.getValue());
			if (ctx.getErrorList() != null)
				for (ErrorDetail err : ctx.getErrorList())
					System.out.println(err.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (String var : ctx.getIndexedVariables())
			System.out.println("Indexed Variable : " + var);
		for (String var : ctx.getIndexedVarNames())
			System.out.println("Indexed Variable : " + var);
		for (Map.Entry<String, Class> var : ctx.getVariables().entrySet())
			System.out.println("Variable : " + var.getKey() + " = "
					+ var.getValue());
		for (Map.Entry<String, Class> var : ctx.getInputs().entrySet())
			System.out.println("Input : " + var.getKey() + " = "
					+ var.getValue());
		if (ctx.getErrorList() != null)
			for (ErrorDetail err : ctx.getErrorList())
				System.out.println(err.getMessage());
		System.out.println("LineCount : " + ctx.getLineCount());
		System.out.println("LineOffset : " + ctx.getLineOffset());
		System.out.println("LastLineLabel : " + ctx.getLastLineLabel());

		try {
			Map<String, Object> vars = new HashMap<String, Object>();
			for (Map.Entry<String, Class> var : ctx.getInputs().entrySet())
				vars.put(var.getKey(), false);
			System.out.println(MVEL.eval(expression, vars));
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}

	}

}
