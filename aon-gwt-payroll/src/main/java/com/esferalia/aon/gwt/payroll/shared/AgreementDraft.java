package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.common.shared.StringUtils;

public class AgreementDraft extends Agreement {

	public static class SalaryTable implements Serializable {

		public static class Key implements Serializable {

			int level;
			String var;

			@Override
			public boolean equals(Object obj) {
				return obj instanceof Key && level == ((Key) obj).level
						&& var.equals(((Key) obj).var);
			}

			@Override
			public int hashCode() {
				return level * 31 + var.hashCode();
			}

			private static Key make(int level, String var) {
				Key key = new Key();
				key.var = var;
				key.level = level;
				return key;
			}

		}

		public static class Entry implements Serializable{
			int level;
			Variable variable;
			

			public Entry(int level, Variable variable) {
				this.level = level;
				this.variable = variable;
			}

			public int getLevel() {
				return level;
			}
			
			public Variable getVariable() {
				return variable;
			}

		}
		
		public static SalaryTable emptySalaryTable(){
			return new SalaryTable(Collections.<Key, Variable>emptyMap());
		}

		private Map<Key, Variable> map;

		public SalaryTable(Map<Key, Variable> map) {
			this.map = map;
		}

		public SalaryTable() {
			this( new HashMap<Key, Variable>());
		}

		public SalaryTable(SalaryTable salaryTable) {
			this(new HashMap<Key, Variable>(salaryTable.map));
		}

		public int size() {
			return map.size();
		}

		public void clear() {
			map.clear();
		}

		public void putAll(SalaryTable salaryTable) {
			map.putAll(salaryTable.map);
		}

		public Variable put(int level, Variable var) {
			return map.put(Key.make(level, var.name), var);
		}

		public Variable get(int level, String var) {
			return map.get(Key.make(level, var));
		}
		
		public boolean contains(String var) {
			for ( Key key : map.keySet() ) 
				if ( key.var.equals(var) )
					return true;
			return false;
		}

		public boolean contains(int level, String var) {
			return map.containsKey(Key.make(level, var));
		}

		public Collection<Variable> getVariables(int level) {
			List<Variable> vars = new LinkedList<Variable>();
			for (Map.Entry<Key, Variable> entry : map.entrySet()) {
				if (entry.getKey().level == level)
					vars.add(entry.getValue());
			}
			return vars;
		}

		public SortedSet<Integer> getAllLevels() {
			Set<Key> keys = map.keySet();
			SortedSet<Integer> levels = new TreeSet<Integer>();
			for (Key key : keys)
				levels.add(key.level);
			return levels;
		}

		public Collection<Variable> getAllVariables() {
			return map.values();
		}

		public Collection<Entry> getEntries() {
			List<Entry> entries = new ArrayList<Entry>();
			for (Map.Entry<Key, Variable> entry : map.entrySet()) {
				Variable variable = entry.getValue();
				if ( variable == null)
					continue;
				int level = entry.getKey().level;
				entries.add(new Entry(level, variable));
			}
			return entries;
		}

	}

	static class HasIdSet<T extends HasId<?>> extends AbstractSet<T> {

		Map<?, T> map;

		public HasIdSet(Map<?, T> map) {
			this.map = map;
		}

		@Override
		public int size() {
			return map.size();
		}

		@Override
		public Iterator<T> iterator() {
			return map.values().iterator();
		}

	}

	private Date startDate;
	private Date endDate;
	
	private boolean hasChanges;

	private SortedSet<Date> datesWithChanges;

	private Set<Event> events;

	private Set<Extra> extras;
	private Map<Integer, Extra> draftExtras;

	// TODO : Must this be at 'Agreement'?
	private Set<Payment> payments;
	private Map<Integer, Payment> draftPayments;

	private Map<Integer, Level> draftLevels;

	private Set<String> variables;

	private SalaryTable salaryTable;
	private SalaryTable draftSalaryTable;

	private Map<Integer, Set<String>> draftCategories;

	public AgreementDraft() {
		hasChanges = false;
		draftSalaryTable = new SalaryTable();
		draftExtras = new HashMap<Integer, Extra>();
		draftLevels = new HashMap<Integer, Level>();
		draftPayments = new HashMap<Integer, Payment>();
		draftCategories = new HashMap<Integer, Set<String>>();
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Set<Event> getEvents() {
		return events;
	}
	
	public void setEvents(Set<Event> events) {
		this.events = events;
	}

	public Set<Extra> getExtras() {
		return extras != null ? extras : Collections.<Extra>emptySet();
	}

	public void setExtras(Set<Extra> extras) {
		this.extras = extras;
	}

	public Set<Payment> getPayments() {
		return payments != null ? payments : Collections.<Payment>emptySet();
	}

	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

	public Set<String> getVariables() {
		return variables != null ? variables : Collections.<String>emptySet();
	}

	public void setVariables(Set<String> variables) {
		this.variables = variables;
	}
	
	public SalaryTable getSalaryTable() {
		return salaryTable != null ? salaryTable : SalaryTable.emptySalaryTable();
	}

	public void setSalaryTable(SalaryTable salaryTable) {
		this.salaryTable = salaryTable;
	}

	public SortedSet<Date> getDatesWithChanges() {
		return datesWithChanges != null ? datesWithChanges : new TreeSet<Date>();//Collections.<Date>emptySortedSet();
	}

	public void setDatesWithChanges(SortedSet<Date> datesWithChanges) {
		this.datesWithChanges = datesWithChanges;
	}

	public Set<Level> getDraftLevels() {
		return new HasIdSet<Level>(draftLevels);
	}

	public Set<Extra> getDraftExtras() {
		return new HasIdSet<Extra>(draftExtras);
	}

	public Extra addDraftExtra(Extra extra) {
		return draftExtras.put(extra.getId(), extra);
	}

	public void removeDraftExtra(Extra extra) {
		draftExtras.remove(extra.getId());
	}

	public Level addDraftLevel(Level level) {
		return draftLevels.put(level.getId(), level);
	}

	public void removeDraftLevel(Level level) {
		draftLevels.remove(level.getId());
	}

	public Set<Payment> getDraftPayments() {
		return new HasIdSet<Payment>(draftPayments);
	}

	public Payment addDraftPayment(Payment payment) {
		return draftPayments.put(payment.getId(), payment);
	}

	public Payment removeDraftPaymet(Payment payment) {
		return draftPayments.remove(payment.getId());
	}

	public SalaryTable getDraftSalaryTable() {
		return draftSalaryTable;
	}

	public Variable addDraftVariable(int levelId, Variable var) {
		return draftSalaryTable.put(levelId, var);
	}

	public Variable addDraftVariable(Level level, Variable var) {
		return draftSalaryTable.put(level.getId(), var);
	}

	public Set<String> addDraftCategories(Level level, Set<String> categories) {

		return draftCategories.put(level.getId(), categories);
	}

	public Map<Integer, Set<String>> getDraftCategories() {
		return draftCategories;
	}

	public void clearDrafts() {
		draftLevels.clear();
		draftExtras.clear();
		draftPayments.clear();
		draftSalaryTable.clear();
		draftCategories.clear();
	}

	public boolean hasDrafts() {
		return (draftLevels.size() > 0) || (draftExtras.size() > 0)
				|| (draftPayments.size() > 0) || (draftSalaryTable.size() > 0)
				|| (draftCategories.size() > 0);
	}

	public boolean hasChanges() {
		return hasChanges;
	}

	public void setHasChanges(boolean hasChanges) {
		this.hasChanges = hasChanges;
	}

	public boolean hasExtrasWithoutDates() {
		for (Extra extra : getAllExtras()) {
			if (!isRemove(extra) && !hasDates(extra)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasLevelsWithoutCategories() {

		if (super.hasLevelsWithoutCategories())
			return true;

		for (Level level : getAllLevels()) {
			if (!isRemove(level) && !hasCategories(level))
				return true;
		}

		return false;
	}

	public boolean isDraftPayment(Payment payment) {
		return draftPayments.containsKey(payment.getId());
	}

	public boolean isDraftVariable(int level, String var) {
		return salaryTable.contains(level, var);
	}
	// ------------------------------------------------------------------------

	public static boolean isRemove(Extra extra) {
		return StringUtils.equals("REMOVE()", extra.getIssueDate());
	}

	public static boolean isRemove(Level level) {
		return StringUtils.equals("REMOVE()", level.getDescription());
	}

	public static boolean isRemove(Payment payment) {
		return StringUtils.equals("REMOVE()", payment.getExpression());
	}

	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------
	private Set<Extra> getAllExtras() {
		Set<Extra> all = new HashSet<Extra>(draftExtras.values());
		all.addAll(extras);
		return all;
	}

	private Set<Level> getAllLevels() {
		Set<Level> all = new HashSet<Level>(draftLevels.values());
		all.addAll(getLevels());
		return all;
	}

	private boolean hasDates(Extra extra) {

		if (StringUtils.isBlank(extra.getStartDate()))
			return false;
		if (StringUtils.isBlank(extra.getEndDate()))
			return false;
		if (StringUtils.isBlank(extra.getIssueDate()))
			return false;

		return true;
	}

	private boolean hasCategories(Level level) {
		Set<String> set = getCategories(level);
		return set != null && set.size() > 0;
	}

	private Set<String> getCategories(Level level) {
		int levelId = level.getId();
		if (draftCategories.containsKey(levelId))
			return draftCategories.get(levelId);
		else
			return getCategoriesMap().get(levelId);
	}

}
