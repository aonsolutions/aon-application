package com.esferalia.aon.ui.payroll.controller.salary;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import javax.faces.context.FacesContext;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.salary.enumeration.DeductionType;

public class SortedSalaryCosts {
	
	
	public static class CustomSalaryCost extends SalaryCost{
		
		private SalaryCost salaryCost;
		
		
		public CustomSalaryCost(SalaryCost salaryCost) {
			this.salaryCost = salaryCost;
		}

		public String getName() {
			String concept = salaryCost.getCostConcept();
			try {
				return  AonUtil.getMessage("payrollBundle", 
						"payroll_salary_cost_" + concept);
			} catch ( MissingResourceException e ) {
				Locale locale = getLocale();
				return salaryCost.getType().getName(locale);
			}
		}
		
		public String getDescription() {
			return salaryCost.getDescription();
		}

		public double getAmount() {
			return salaryCost.getAmount();
		}
		
	}
	
	public static class CustomSalaryCostList {

		private DeductionType deductionType;
		private Collection<SalaryCost> costs;
		
		public CustomSalaryCostList(DeductionType deductionType) {
			this.deductionType = deductionType;
			this.costs = new LinkedList<SalaryCost>();
		}
		
		public int getCount() {
			return costs.size();
		}

		public String getTitle() {
			Locale locale = getLocale();
			return deductionType.getName(locale);
		}
		
		public Collection<SalaryCost> getList() {
			return costs;
		}

		private void add(SalaryCost salaryCost) {
			this.costs.add(salaryCost);
		}

	}

	private double total;
	private Map<String, CustomSalaryCostList> costsMap;
	
	public SortedSalaryCosts() {
		initMap();
		total = 0.00;
	}
	
	public void setSalaryCosts(Collection<SalaryCost> costs ) {
		loadMap(costs);
	}
	
	public double getTotal() {
		return total;
	}
	
	public Map<String, CustomSalaryCostList> getCosts() {
		return costsMap;
	}
	
	private void initMap() {
		costsMap = new HashMap<String, CustomSalaryCostList>();
		for (DeductionType type : DeductionType.values()) {
			costsMap.put(type.name(), new CustomSalaryCostList(type));
		} 
	}
	
	private void loadMap(Collection<SalaryCost> salaryCosts ) {
		for (SalaryCost salaryCost : salaryCosts) {
			double amount = salaryCost.getAmount();
			if ( amount != 0.00 ) {
				DeductionType type = salaryCost.getType();
				CustomSalaryCostList ownSalaryCosts = 
					costsMap.get(type.name());
				ownSalaryCosts.add(new CustomSalaryCost( salaryCost ) );
				total += amount;
			}
		}
	}
	
	private static Locale getLocale() {
		return FacesContext.getCurrentInstance().getViewRoot().getLocale();
	}
	
	
}
