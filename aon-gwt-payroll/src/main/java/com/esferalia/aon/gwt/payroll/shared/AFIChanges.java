package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
public class AFIChanges implements Serializable {

	// CLASS AFI CHANGE
	public static class AFIChange implements Serializable{
		private String name;
		private String value;
		
		public AFIChange() {
			super();
		}

		public AFIChange(String name, String value) {
			super();
			this.name = name;
			this.value = AonStringUtils.replace(value, "\"", "");
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
	}
	
	//BEGIN AFI CHANGES CLASS
	private Map<Date, ArrayList<AFIChange>> afiChanges;
	
	public AFIChanges() {
		super();
		this.afiChanges = new TreeMap<Date, ArrayList<AFIChange>>();
	}
	
	public Map<Date, ArrayList<AFIChange>> getAFIChanges() {
		return this.afiChanges;
	}
	
	public ArrayList<AFIChange> getAFIChangessByDate(Date date){
		return this.afiChanges.get(date);
	}
	
	public void deleteAFIChangeByDate(Date date){
		this.afiChanges.remove(date);
	}
	
	public void addAFIChange(Date date) {
		ArrayList<AFIChange> afiChangesList = new ArrayList<>();
		
		AFIChange tc2 = new AFIChange("TC2", null);
		AFIChange quoteGroup = new AFIChange("GRUPO_COTIZACION", null);
		AFIChange ocupation = new AFIChange("OCUPACION", null);
		AFIChange partialityCoef = new AFIChange("COEFICIENTE_PARCIALIDAD", null);
		
		afiChangesList.add(tc2);
		afiChangesList.add(quoteGroup);
		afiChangesList.add(ocupation);
		afiChangesList.add(partialityCoef);
		
		DateUtils.resetTime(date);
		
		this.afiChanges.put(date, afiChangesList);
	}
	
	public void updateAFIChange(Date date, String name, String value) {
		for(AFIChange afiChange : this.afiChanges.get(date)) {
			if(afiChange.getName().equals(name)) {
				afiChange.setValue(value);
			}
		}
	}

	public void addAFIChangeByDate(Date date, String name, String value) {
		ArrayList<AFIChange> afiChangeList = this.afiChanges.get(date);
		boolean exist = false;
		if(afiChangeList.isEmpty()) {
			afiChangeList.add(new AFIChange(name, value));
		}else {
			for(AFIChange afiChange : afiChangeList) {
				if(afiChange.getName().equals(name)) {
					exist = true;
					afiChange.setValue(value);
					break;
				}
			}
			if(!exist)
				afiChangeList.add(new AFIChange(name, value));
		}
	}

	public boolean hasChange(String type) {
		ArrayList<Date> datesList = new ArrayList<Date>();
		datesList.addAll(afiChanges.keySet());
		
		if(!datesList.isEmpty()) {
			Date date = datesList.get(datesList.size() - 1);
			for(AFIChange afiChange : afiChanges.get(date)) {
				if(afiChange.getName().equals(type))
					return true;
			}
					
		}
		return false;
	}
	
		
}
