package com.esferalia.aon.salary.expression;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import com.code.aon.AonVersion;

public class Variables implements Comparator<ITimedVariable<?>> {

	public static interface NotFoundHandler {
		List<ITimedVariable<?>> get(String var);
	}
	
	private static class NoopNotFoundHandler implements NotFoundHandler{
		
		private static NoopNotFoundHandler INSTANCE = new NoopNotFoundHandler();
		
		@Override
		public List<ITimedVariable<?>> get(String var) {
			return Collections.emptyList();
		}
	}
	
	public static class NotFoundVariableError extends Error {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private String variableName;

		public NotFoundVariableError(String variableName) {
			this.variableName = variableName;
		}

		public String getVariableName() {
			return variableName;
		}

	}

	private NotFoundHandler notFoundHandler;
	private Map<String, List<ITimedVariable<?>>> vars;

	public class PeriodMap implements Map<String, Object> {

		private Period period;

		private Map<String, ITimedVariable<?>> read = new HashMap<String, ITimedVariable<?>>();

		public PeriodMap(Period period) {
			this.period = period;
		}

		public Period getPeriod() {
			return period;
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsKey(Object key) {
			return Variables.this.containsKey((String) key, this.period);
		}

		@Override
		public boolean containsValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object get(Object key) {
			ITimedVariable<?> var = Variables.this.getVariable(key.toString(),
					period);
			
			read.put(key.toString(), var);
			
			if ( var instanceof ITimedResult<?>) {
				read(((ITimedResult<?>)var).getContext() );
			}
			
//			return var != null ? var.getValue(period) : null;
			
			Object value = var != null ? var.getValue(period) : null;
			read(key.toString(), var, value);
			return value;
		}

		@Override
		public boolean isEmpty() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<String> keySet() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object put(String key, Object value) {
			ITimedVariable<Object> timedObject = new TimedObject<Object>(value,
					this.period);
			Variables.this.put((String) key, timedObject);
			read(key, timedObject, value);
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<Object> values() {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void finalize() throws Throwable {
			period = null;
			super.finalize();
		}

		public void cleanRead() {
			read.clear();
		}
		

		public Map<String, ITimedVariable<?>> getRead() {
			return new HashMap<String, ITimedVariable<?>>(read);
		}
		
		public <T> T get(Object key, Function<Object,T> mapper, T def) {
			return get(key.toString(), mapper, def);	
		}

		public <T> T get(String key, Function<Object,T> mapper, T def) {
			ITimedVariable<?> var = Variables.this.getVariable(key,
					period);
			
			if ( var == null )
				var = new TimedObject<T>(def, period);
			
			Object value = var.getValue(period);
//			if ( value == null )
//				var = new TimedObject<T>(def, period);
			
//			read.put(key, var);
//			return value == null ? def : mapper.apply(value);
			
			T t = value == null ? def : mapper.apply(value);
			read(key, var,t);
			return t;
		}
		
		public <T> Collection<T> getAll(String key, Function<Object,T> mapper, T def) {
			List<ITimedVariable<Object>> variables = Variables.this.getVariables(key,
					period);
			List<T> result = new ArrayList<>();
			
			for (ITimedVariable<Object> variable : variables) {
				Object value = variable.getValue(period.intersect(variable.getPeriod()));
				T t = value == null ? def : mapper.apply(value);
				read(key, variable,t);
				result.add(t);
			}
			return result;
		}

		public <T> T get(Object key, Function<Object,T> mapper) {
			return get(key.toString(), mapper);
		}
		
		public <T> T get(String key, Function<Object,T> mapper) {
			ITimedVariable<?> var = Variables.this.getVariable(key,
					period);
			
			if ( var == null )
				return null;
			
			Object value = var.getValue(period);
			if ( value == null )
				return null;
			
//			read.put(key, var);
//			return mapper.apply(value);

			T t = mapper.apply(value);
			read(key, var, t);
			return t;
		}
		
		public <T> T look(Object key, Function<Object,T> mapper, T def) {
			return look(key.toString(), mapper, def);	
		}

		public <T> T look(String key, Function<Object,T> mapper, T def) {
			ITimedVariable<?> var = Variables.this.getVariable(key,
					period);
			
			if ( var == null )
				var = new TimedObject<T>(def, period);
			
			Object value = var.getValue(period);
			
			T t = value == null ? def : mapper.apply(value);
			return t;
		}

		public <T> ITimedVariable<T> get(Object key, Class<T> clazz) {
			return (ITimedVariable<T>) Variables.this.getVariable(key.toString(),
					period);
		}

		private <T>  void read(Map<String,ITimedVariable<?>> map) {
			for( Map.Entry<String,ITimedVariable<?>> entry : map.entrySet())
				read(entry.getKey(), entry.getValue(), entry.getValue().getValue(period));
		}

		private <T>  void read(String key, ITimedVariable<?> var, T value) {
			this.read.put(key, wrapVariable(period, var, value));
		}
		
		
	}
	
	
	public static ITimedVariable<?>  getNarrowVariable(ITimedVariable<?> var, Period period){
		if ( var instanceof LazyExpressionVariable )
			return new LazyExpressionVariable( ((LazyExpressionVariable)var).getExpressionContext(), ((LazyExpressionVariable)var).getExpression(), period);
		else if ( var instanceof IExpressionVariable<?>  )	
			return new WrapExpressionVariable<>(period, (IExpressionVariable<?>) var);
		else if ( var instanceof IConstantVariable ) 
			return new WrapTimedConstant<>(period, var); 
		else 
			return new WrapTimedVariable<>(period, var);
	}

	public Variables() {
		this(NoopNotFoundHandler.INSTANCE);
	}


	public Variables(NotFoundHandler notFoundHandler) {
		this.vars = new HashMap<String, List<ITimedVariable<?>>>();
		this.notFoundHandler = notFoundHandler;
	}

	public void clear() {
		vars.clear();
	}

	public Variables(Variables variables, NotFoundHandler notFoundHandler) {
		// TODO: Delegate Map
		vars = new HashMap<String, List<ITimedVariable<?>>>();
		for (Entry<String, List<ITimedVariable<?>>> var : variables.vars
				.entrySet()) {
			vars.put(var.getKey(),
					new ArrayList<ITimedVariable<?>>(var.getValue()));
		}
		this.notFoundHandler = notFoundHandler;
	}

	public int size() {
		return vars.size();
	}

	public Set<String> varsSet() {
		return vars.keySet();
	}

	public Map<String,List<ITimedVariable<?>>> putAll(Variables variables) {
		Map<String,List<ITimedVariable<?>>> redefined = new HashMap<String,List<ITimedVariable<?>>>();
		for (Entry<String, List<ITimedVariable<?>>> entry : variables.vars
				.entrySet()) {
			for (ITimedVariable<?> timedVar: entry.getValue()) {
				redefined.put(entry.getKey(),put(entry.getKey(),timedVar));
			}
		}
		return redefined;
	}

	
	public List<ITimedVariable<?>> put(String name, ITimedVariable<?> var) {
		List<ITimedVariable<?>> values = vars.get(name);
		if (values == null) {
			values = new ArrayList<ITimedVariable<?>>();
			values.add(var);
			vars.put(name, values);
			return Collections.emptyList();
		} else {
			int index = Collections.binarySearch(values, var, this);
			if (index >= 0) {
				values.set(index, var);
				return Collections.emptyList();
			} else {
				int position = -(index + 1);
				values.add(position, var);
				if (values.size() == 1) {
					return Collections.emptyList();
				} // Only one value, all ok.
				
				List<ITimedVariable<?>> redefined = new ArrayList<ITimedVariable<?>>();
				
				// Fix NEXT value
				if (position + 1 < values.size()) {
					ITimedVariable<?> next = values.get(position + 1);
					while (intersects(var, next)) {
						redefined.add(next);
						Date start = var.getPeriod().getEnd();
						Date end = next.getPeriod().getEnd();
						if (Period.compare(start, end) >= 0) {
							values.remove(position + 1);
						}else {
							start = Variables.add(start, 1);
							ITimedVariable<?> wrapNext = newWrapTimedVariable(
									start, end, next);
							values.set(position + 1, wrapNext);
						}
						if ( values.size() <= (position +1) )
							break;
						
						next = values.get(position + 1);

					}
				}

				// Fix PREV value
				if (position - 1 >= 0) {
					ITimedVariable<?> prev = values.get(position - 1);
					if (intersects(var, prev)) {
						redefined.add(prev);
						Date end = var.getPeriod().getStart();
						Date start = prev.getPeriod().getStart(); // start
																	// remain
																	// untouch
						if (Period.compare(start, end) >= 0) {
							values.remove(position - 1);
						} else {
							end = Variables.add(end, -1);
							ITimedVariable<?> wrapPrev = newWrapTimedVariable(
									start, end, prev);
							values.set(position - 1, wrapPrev);
						}

						end = var.getPeriod().getEnd();
						if (Period.compare(prev.getPeriod().getEnd(), end) > 0) {
							ITimedVariable<?> wrapPrev = newWrapTimedVariable(
									Variables.add(end, +1), prev.getPeriod()
											.getEnd(), prev);
							values.add(position + 1, wrapPrev);

						}
					} // Eliminamos
				}
				
				return redefined;

			}
		}

	}

	public void remove(String name, Period p) {
		List<ITimedVariable<?>> values = vars.get(name);

		if (values == null)
			return;

		Iterator<ITimedVariable<?>> iterator = values.iterator();

		while (iterator.hasNext()) {

			ITimedVariable<?> object = iterator.next();
			if (object.getPeriod().compareTo(p) == 0) {
				iterator.remove();
				return;
			}
		}
	}

	public void sub(String name, Period period) {
		List<ITimedVariable<?>> deleted = vars.get(name);
		if (deleted == null)
			return;
		
		List<ITimedVariable<?>> values = new ArrayList();
		
		for (ITimedVariable<?> var : deleted) {
			var.getPeriod().sub(period)
			.forEach( p -> values.add(newWrapTimedVariable(p.getStart(), p.getEnd(), var)));
		}
		
		Collections.sort(values, this);
		
		vars.put(name, values);

	}

	public List<ITimedVariable<?>> getValues(String var) {

		return vars.get(var);
	}

	public List<Period> getPeriods(String var) {

		List<ITimedVariable<?>> values = vars.get(var);
		if (values == null) {
			return Collections.emptyList();
		}

		List<Period> periods = new LinkedList<Period>();
		for (ITimedVariable<?> timedVar : values) {
			periods.add(timedVar.getPeriod());
		}

		return periods;
	}

	public Object get(String var, Period p) {
		ITimedVariable<?> variable = getVariable(var, p);

		return variable != null ? variable.getValue(p) : null;

	}

	public boolean containsKey(String key) {
		List<ITimedVariable<?>> values = get(key);
		return (values != null && values.size() > 0);

	}

	public boolean containsKey(String key, Period p) {
		List<ITimedVariable<?>> values = get(key);
		if (values == null) {
			return false;
		}

		for (ITimedVariable<?> timedObject : values) {

			if (timedObject.getPeriod().intersects(p)) {
				return true;
			}
		}

		return false;

	}


	public List<PeriodMap> getBindings(Set<String> vars, Date start, Date end)			{
		List<PeriodMap> list = new LinkedList<>();
		List<Period> periods = new LinkedList<>();
		Period mainPeriod = new Period(start, end);

		periods.add(mainPeriod);

		for (String var : vars) {
			List<Period> varPeriods = getPeriods(var);
			if (varPeriods == null || varPeriods.isEmpty()) {
				continue;// throw new UndefinedVariablesException(var);
			}
			List<Period> intersectedPeriods = Period.intersect(periods,
					varPeriods);

			if (intersectedPeriods.size() > 0) {
				periods = intersectedPeriods;
			} else {
				periods.addAll(Period.intersect(Collections.singleton(mainPeriod), varPeriods));
			}
		}

		for (Period period : periods) {
			list.add(new PeriodMap(period));
		}

		return list;
	}

	public List<PeriodMap> getBindingsNew(Set<String> vars, Date start, Date end){
		List<PeriodMap> list = new LinkedList<PeriodMap>();

		List<Period> periods = new LinkedList<Period>();
		periods.add(new Period(start, end));

		for (String var : vars) {
			List<Period> varPeriods = getPeriods(var);
			if (varPeriods == null)
				continue;

			List<Period> intersectedPeriods = Period.intersect(periods,
					varPeriods);
			
			if (intersectedPeriods.size() == 0)
				return Collections.emptyList();

			periods = intersectedPeriods;
		}

		for (Period period : periods) {
			list.add(new PeriodMap(period));
		}

		return list;
	}

	@Override
	public int compare(ITimedVariable<?> o1, ITimedVariable<?> o2) {
		Period p1 = o1.getPeriod();
		Period p2 = o2.getPeriod();
		return p1.compareTo(p2);
	}

	public boolean intersects(ITimedVariable<?> o1, ITimedVariable<?> o2) {
		Period p1 = o1.getPeriod();
		Period p2 = o2.getPeriod();
		return p1.intersect(p2) != null;
	}

	public PeriodMap getPeriodMap(Date start, Date end) {
		return new PeriodMap(new Period(start, end));
	}

	public ITimedVariable<?> getVariable(String name, Period p) {
		List<ITimedVariable<?>> values = get(name);
		if (values == null) {
			return null;
		}

		ITimedVariable<?> ret = null;

		for (ITimedVariable<?> var : values) {
			Period intersect = var.getPeriod().intersect(p);
			if ( intersect == null )
				continue;
			if ( var.getPeriod().equals(intersect) )
				ret = var;
			else if ( var instanceof IConstantVariable)
				ret = var;
			else 
				ret = wrapVariable(intersect,var);//new WrapTimedVariable(intersect,var);
		}
		
		return  ret;

	}

	public <T> List<ITimedVariable<T>> getVariables(String name) {
		List<ITimedVariable<?>> values = get(name);
		if (values == null)
			return Collections.emptyList();

		List<ITimedVariable<T>> ret = new ArrayList<ITimedVariable<T>>();

		for (ITimedVariable<?> var : values)
			ret.add((ITimedVariable<T>) var);

		return ret;

	}

	public <T> List<ITimedVariable<T>> getVariables(String name, Period p) {
		List<ITimedVariable<?>> values = get(name);
		if (values == null)
			return Collections.emptyList();

		List<ITimedVariable<T>> ret = new ArrayList<ITimedVariable<T>>();

		for (ITimedVariable<?> var : values) {
			Period intersect = var.getPeriod().intersect(p);
			if (intersect == null)
				continue;
			
			ret.add(wrapVariable(intersect, (ITimedVariable<T>) var));
			//ret.add(new WrapTimedVariable<T>(intersect, (ITimedVariable<T>) var));
		}

		return ret;

	}


	protected Variables getSnapshot(Set<String> variables) {

		Variables snapshot = new Variables();
		snapshot.notFoundHandler = notFoundHandler;
		snapshot.vars = new SnapshotMap<String, List<ITimedVariable<?>>>(
				variables, vars) {

			protected List<ITimedVariable<?>> getSnapShot(
					List<ITimedVariable<?>> value) {
				return new LinkedList<ITimedVariable<?>>(value);
			};
		};
		return snapshot;

	}

	@Override
	protected void finalize() throws Throwable {
		clear();
		super.finalize();
	}

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	protected List<ITimedVariable<?>> get(String var) {
		List<ITimedVariable<?>> values = vars.get(var);
		if (values == null && notFoundHandler != null) {
			values = notFoundHandler.get(var);
		}
		return values;
	}

	protected List<ITimedVariable<?>> remove(String var) {
		List<ITimedVariable<?>> values = vars.remove(var);
		return values == null ? Collections.emptyList() : values;
	}

	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	private void traceRemove(String name, ITimedVariable<?> cur,
			ITimedVariable<?> old) {
		System.out
				.printf("Eliminada %1$s=%2$s (%4$tF..%5$tF) %1$s=%3$s (%6$tF..%7$tF) \r\n",
						name, old.getValue(old.getPeriod()), cur.getValue(cur
								.getPeriod()), old.getPeriod().getStart(), old
								.getPeriod().getEnd(), cur.getPeriod()
								.getStart(), cur.getPeriod().getEnd());
	}

	private void traceUpdate(String name, ITimedVariable<?> cur,
			ITimedVariable<?> old) {
		System.out
				.printf("Modificada %1$s=%2$s (%4$tF..%5$tF) %1$s=%3$s (%6$tF..%7$tF) \r\n",
						name, old.getValue(old.getPeriod()), cur.getValue(cur
								.getPeriod()), old.getPeriod().getStart(), old
								.getPeriod().getEnd(), cur.getPeriod()
								.getStart(), cur.getPeriod().getEnd());
	}

	private static Date add(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTime();
	}
	
	private static <T> ITimedVariable<T> wrapVariable(Period period,
			ITimedVariable<T> timedVariable) {
		return timedVariable instanceof IExpressionVariable<?> ?
				new WrapExpressionVariable<>(period, (IExpressionVariable<T>)timedVariable):
				new WrapTimedVariable<>(period, timedVariable);
	}
	
	private static <T> ITimedVariable<T> wrapVariable(Period period,
			ITimedVariable<?> timedVariable, T value) {
		return timedVariable instanceof IExpressionVariable<?> ?
				new WrapExpressionVariable<T>(period, (IExpressionVariable<T>)timedVariable) {
		    			@Override
		    			public T getValue(Period period) {
		    			    return value;
		    			}
				}: 
				new WrapTimedVariable<T>(period, (ITimedVariable<T> ) timedVariable) {
				    @Override
				    public T getValue(Period period) {
					return value;
				    }
				};
	}

	private static <T> ITimedVariable<T> newWrapTimedVariable(Date start, Date end, ITimedVariable<T> var){
		return ( var instanceof IExpressionVariable<?> ) ? 
				new WrapExpressionVariable(start, end, (IExpressionVariable<T>) var) 
				: (( var instanceof IConstantVariable ) ? new WrapTimedConstant<T>(start, end, var): new WrapTimedVariable<T>(start, end, var));
	}
	
	
	private static class WrapTimedVariable<T> implements ITimedVariable<T> {

		private Period period;
		private ITimedVariable<? extends T> timedVariable;

		public WrapTimedVariable(Period period,
				ITimedVariable<? extends T> timedVariable) {
			this.period = period;
			this.timedVariable = timedVariable;
		}

		public WrapTimedVariable(Date start, Date end,
				ITimedVariable<? extends T> timedVariable) {
			this(new Period(start, end), timedVariable);
		}

		@Override
		public Period getPeriod() {
			return period;
		}

		@Override
		public T getValue(Period period) {
			return timedVariable.getValue(period);
		}

	}

	private static class WrapTimedConstant<T> extends WrapTimedVariable<T> implements IConstantVariable {

		public WrapTimedConstant(Date start, Date end,
				ITimedVariable<? extends T> timedVariable) {
			super(start, end, timedVariable);
		}

		public WrapTimedConstant(Period period,
				ITimedVariable<? extends T> timedVariable) {
			super(period, timedVariable);
		}
		
	}
	
	private static class WrapExpressionVariable<T> implements IExpressionVariable<T> , IWrapTimedVariable<T> {

		private Period period;
		private IExpressionVariable<T> expressionVariable;

		public WrapExpressionVariable(Period period,
				IExpressionVariable<T> timedVariable) {
			this.period = period;
			this.expressionVariable = timedVariable;
		}

		public WrapExpressionVariable(Date start, Date end,
				IExpressionVariable<T> timedVariable) {
			this(new Period(start, end), timedVariable);
		}

		@Override
		public Period getPeriod() {
			return period;
		}

		@Override
		public T getValue(Period period) {
			return expressionVariable.getValue(period);
		}
		
		
		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return expressionVariable.getContext();
		}
		
		@Override
		public IExpression getExpression() {
			return expressionVariable.getExpression();
		}
		
		@Override
		public ITimedVariable<T> getVariable() {
		    return expressionVariable;
		}
	}
}
