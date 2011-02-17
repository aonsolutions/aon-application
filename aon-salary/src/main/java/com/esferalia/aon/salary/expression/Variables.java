package com.esferalia.aon.salary.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Variables implements Comparator<ITimedObject<?>> {

	
	private Map<String, List<ITimedObject<?>>> vars;
	
	private class PeriodMap implements Map<String, Object> {
		
		private Period period;
		
		public PeriodMap(Period period) {
			this.period = period;
		}
		
		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsKey(Object key) {
			return Variables.this.containsKey(key, this.period);
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
			return Variables.this.get((String)key, this.period);
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
			throw new UnsupportedOperationException();
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

	}
	
	public Variables() {
		 vars = new HashMap<String, List<ITimedObject<?>>>();
	}
	
	public Variables(Variables variables) {
		// TODO:  Delegate Map	
		vars = new HashMap<String, List<ITimedObject<?>>>(variables.vars);
	}

	public void put ( String name, ITimedObject<?> timedObject ){
		List<ITimedObject<?>> values =  
			vars.get(name);
		if ( values == null ) {
			values = new ArrayList<ITimedObject<?>>();
			values.add(timedObject);
			vars.put(name, values);
		}
		else {
			int index = Collections.binarySearch(values, timedObject, this);
			if ( index >= 0  ) {
				values.set(index, timedObject);
			}
			else {
				// TODO : cuidado con los que se superponen
				values.add(-(index + 1), timedObject);
			}
		}
		
	}

	public List<Period> getPeriods(String var ) {
		
		List<ITimedObject<?>> values =  vars.get(var);
		if ( values == null ) {
			return null;
		}
		
		List<Period> periods = 
			new LinkedList<Period>();
		for (ITimedObject<?> timedObject : values) {
			periods.add(timedObject.getPeriod());
		}
		
		return periods;
	}
	
	public Object get(String var, Period p){
		List<ITimedObject<?>> values =  vars.get(var);
		if ( values == null ) {
			return null;
		}
		
		for (ITimedObject<?> timedObject : values) {
			if ( timedObject.getPeriod().contains(p)) {
				return timedObject.getValue();
			}
		}
		
		return null;
		
	}
	
	public boolean containsKey(Object key, Period p){
		List<ITimedObject<?>> values =  vars.get(key);
		if ( values == null ) {
			return false;
		}
		
		for (ITimedObject<?> timedObject : values) {
			
			if ( timedObject.getPeriod().contains(p)) {
				return true;
			}
		}
		
		return false;
		
	}

	public List<Map<String, Object>> getBindings(Set<String> vars, Date start, Date end) {
		List<Map<String, Object>> list =
			new LinkedList<Map<String,Object>>();
		
		List<Period> periods = new LinkedList<Period>();
		periods.add(new Period(start, end ));

		for (String var : vars) {
			List<Period> varPeriods = getPeriods(var);
			if ( varPeriods != null ) {
				//throw new UnresolveablePropertyException(var);
				periods = Period.intersect(periods, varPeriods);
			}
		}
		
		for (Period period : periods) {
			list.add(new PeriodMap(period));
		}
		
		return list;
	}

	@Override
	public int compare(ITimedObject<?> o1, ITimedObject<?> o2) {
		Period p1 = o1.getPeriod();
		Period p2 = o2.getPeriod();
		return p1.compareTo(p2);
	}
	

}
