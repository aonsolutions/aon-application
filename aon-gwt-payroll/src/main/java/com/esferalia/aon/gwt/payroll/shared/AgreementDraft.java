package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class AgreementDraft extends Agreement {

	public static class Level implements Serializable, HasId<Integer> {

		private Integer id;
		private String description;

		@Override
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
		@Override
		public int hashCode() {
			return id ;
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Level && id.equals(((Level) obj).id);
		}

	}

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

		private Map<Key, Variable> map ;
		
		public SalaryTable() {
			map = new HashMap<Key, Variable>();
		}

		public SalaryTable(SalaryTable salaryTable) {
			map = new HashMap<Key, Variable>(salaryTable.map);
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
		
		public Collection<Variable> getVariables(int level) {
			List<Variable> vars = new LinkedList<Variable>();
			for (Entry<Key, Variable> entry : map.entrySet()) {
				if ( entry.getKey().level == level ) 
					vars.add(entry.getValue());
			}
			return vars;
		}

		
		
	}
	
	static class HasIdSet<T extends HasId<?>> extends AbstractSet<T> {
		
		Map<?, T> map ;
		
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
	
	private Set<Date> datesWithChanges;

	private Set<Extra> extras;
	private Map<Integer, Extra> draftExtras;

	// TODO : Must this be at 'Agreement'?
	private Set<Payment> payments;
	private Map<Integer,Payment> draftPayments;

	private Set<Level> levels;
	private Map<Integer, Level> draftLevels;

	private Set<String> variables;

	private SalaryTable salaryTable;
	private SalaryTable draftSalaryTable;
	
	private Map<Integer,Set<String>> categories;
	private Map<Integer,Set<String>> draftCategories;
	

	public AgreementDraft() {
		hasChanges = false;
		draftSalaryTable = new SalaryTable();
		draftExtras = new HashMap<Integer,Extra>();
		draftLevels = new HashMap<Integer, Level>();
		draftPayments = new HashMap<Integer,Payment>();
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
	
	public Set<Extra> getExtras() {
		return extras;
	}
	
	public void setExtras(Set<Extra> extras) {
		this.extras = extras;
	}

	public Set<Payment> getPayments() {
		return payments;
	}

	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

	public Set<Level> getLevels() {
		return levels;
	}

	public void setLevels(Set<Level> levels) {
		this.levels = levels;
	}

	public Set<String> getVariables() {
		return variables;
	}

	public void setVariables(Set<String> variables) {
		this.variables = variables;
	}
	
	public SalaryTable getSalaryTable() {
		return salaryTable;
	}

	public void setSalaryTable(SalaryTable salaryTable) {
		this.salaryTable = salaryTable;
	}
	
	public Set<Date> getDatesWithChanges() {
		return datesWithChanges;
	}
	
	public void setDatesWithChanges(Set<Date> datesWithChanges) {
		this.datesWithChanges = datesWithChanges;
	}
	
	public Map<Integer, Set<String>> getCategoriesMap() {
		return categories;
	}

	public void setCategoriesMap(Map<Integer, Set<String>> categories) {
		this.categories = categories;
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
	
	public void removeDraftLevel(Level level ) {
		draftLevels.remove(level.getId());
	}

	public Set<Payment> getDraftPayments() {
		return new HasIdSet<Payment>(draftPayments);
	}
	
	public Payment addDraftPayment(Payment payment) {
		return draftPayments.put(payment.getId(),payment);
	}
	
	public Payment removeDraftPaymet(Payment payment){
		return draftPayments.remove(payment.getId());
	}
	
	public SalaryTable getDraftSalaryTable() {
		return draftSalaryTable;
	}
	
	public Variable addDraftVariable(Level level, Variable var) {
		return draftSalaryTable.put(level.getId(), var);
	}
	
	public Set<String> addDraftCategories(Level level, Set<String> categories){
				
		return draftCategories.put(level.getId(), categories);
	}
	
	public Map<Integer, Set<String>> getDraftCategories() {
		return draftCategories;
	}
	
	
	public void clearDrafts(){
		draftLevels.clear();
		draftExtras.clear();
		draftPayments.clear();
		draftSalaryTable.clear();
		draftCategories.clear();
	}

	public boolean hasDrafts(){
		return  (draftLevels.size() > 0) ||
				(draftExtras.size() > 0) ||
				(draftPayments.size() > 0) ||
				(draftSalaryTable.size() > 0 ) ||
				(draftCategories.size() > 0);
	}
	
	public boolean hasChanges() {
		return hasChanges;
	}
	
	public void setHasChanges(boolean hasChanges) {
		this.hasChanges = hasChanges;
	}
	
	
	
}
