package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
			return obj instanceof Level && id == ((Level) obj).id;
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

		private Map<Key, Variable> map = new HashMap<Key, Variable>();;

		public int size() {
			return map.size();
		}

		public void put(int level, Variable var) {
			map.put(Key.make(level, var.name), var);
		}

		public Variable get(int level, String var) {
			return map.get(Key.make(level, var));
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

	// TODO : Must this be at 'Agreement'?
	private Set<Payment> payments;
	private Map<Integer,Payment> draftPayments;

	private Set<Level> levels;
	private Map<Integer, Level> draftLevels;

	private Set<String> variables;

	private SalaryTable salaryTable;
	private SalaryTable draftSalaryTable;
	
	private Map<Integer,Set<String>> categories;
	

	public AgreementDraft() {
		draftSalaryTable = new SalaryTable();
		draftLevels = new HashMap<Integer, Level>();
		draftPayments = new HashMap<Integer,Payment>();
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
	
	public Map<Integer, Set<String>> getCategoriesMap() {
		return categories;
	}

	public void setCategoriesMap(Map<Integer, Set<String>> categories) {
		this.categories = categories;
	}
	
	public Set<Level> getDraftLevels() {
		return new HasIdSet<Level>(draftLevels);
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
	
	public void addDraftVariable(Level level, Variable var) {
		draftSalaryTable.put(level.getId(), var);
	}
	
	public boolean hasDrafts(){
		return  (draftLevels.size() > 0) ||
				(draftPayments.size() > 0) ||
				(draftSalaryTable.size() > 0 );
	}
	
}
