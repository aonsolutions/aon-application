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
			ITimedVariable<?> var = Variables.this.getVariable((String) key,
					period);
			read.put((String) key, var);
			return var != null ? var.getValue(period) : null;
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

		public Map<String, ITimedVariable<?>> getRead() {
			return read;
		}

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

	public void putAll(Variables variables) {
		for (Entry<String, List<ITimedVariable<?>>> entry : variables.vars
				.entrySet()) {
			for (ITimedVariable<?> timedVar: entry.getValue()) {
				put(entry.getKey(),timedVar);
			}
		}
	}

	public void put(String name, ITimedVariable<?> var) {
		List<ITimedVariable<?>> values = vars.get(name);
		if (values == null) {
			values = new ArrayList<ITimedVariable<?>>();
			values.add(var);
			vars.put(name, values);
		} else {
			int index = Collections.binarySearch(values, var, this);
			if (index >= 0) {
				values.set(index, var);
			} else {
				int position = -(index + 1);
				values.add(position, var);
				if (values.size() == 1) {
					return;
				} // Only one value, all ok.

				// Fix NEXT value
				if (position + 1 < values.size()) {
					ITimedVariable<?> next = values.get(position + 1);
					if (intersects(var, next)) {
						Date start = var.getPeriod().getEnd();
						Date end = next.getPeriod().getEnd();
						if (Period.compare(start, end) >= 0) {
							values.remove(position + 1);
						} // La nueva variable sobreescribe totalmente el
							// antiguo valor.
						else {
							start = Variables.add(start, 1);
							ITimedVariable<?> wrapNext = new WrapTimedVariable<Object>(
									start, end, next);
							values.set(position + 1, wrapNext);
						} // La nueva variable sobreescribe parcialmente el
							// antiguo valor.
					}
				}

				// Fix PREV value
				if (position - 1 >= 0) {
					ITimedVariable<?> prev = values.get(position - 1);
					if (intersects(var, prev)) {
						Date end = var.getPeriod().getStart();
						Date start = prev.getPeriod().getStart(); // start
																	// remain
																	// untouch
						if (Period.compare(start, end) >= 0) {
							values.remove(position - 1);
						} else {
							end = Variables.add(end, -1);
							ITimedVariable<?> wrapPrev = new WrapTimedVariable<Object>(
									start, end, prev);
							values.set(position - 1, wrapPrev);
						}

						end = var.getPeriod().getEnd();
						if (Period.compare(prev.getPeriod().getEnd(), end) > 0) {
							ITimedVariable<?> wrapPrev = new WrapTimedVariable<Object>(
									Variables.add(end, +1), prev.getPeriod()
											.getEnd(), prev);
							values.add(position + 1, wrapPrev);

						}
					} // Eliminamos
				}

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

	public List<ITimedVariable<?>> getValues(String var) {

		return vars.get(var);
	}

	public List<Period> getPeriods(String var) {

		List<ITimedVariable<?>> values = vars.get(var);
		if (values == null) {
			return null;
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

	public List<PeriodMap> getBindings(Set<String> vars, Date start, Date end)
			throws UndefinedVariablesException {
		List<PeriodMap> list = new LinkedList<PeriodMap>();

		List<Period> periods = new LinkedList<Period>();
		periods.add(new Period(start, end));

		for (String var : vars) {
			List<Period> varPeriods = getPeriods(var);
			if (varPeriods == null) {
				continue;// throw new UndefinedVariablesException(var);
			}
			List<Period> intersectedPeriods = Period.intersect(periods,
					varPeriods);
			if (intersectedPeriods.size() > 0) {
				periods = intersectedPeriods;
			}
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
			if (var.getPeriod().intersects(p)) {
				ret = var;
			}
		}

		return ret;

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

			ret.add(new WrapTimedVariable<T>(intersect, (ITimedVariable<T>) var));
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

}
