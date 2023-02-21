package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
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
			this.value = AonStringUtils.isBlank(value) ? value : AonStringUtils.replace(value, "\"", "");
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
	private NavigableMap<Date, ArrayList<AFIChange>> afiChanges;
	
	public AFIChanges() {
		super();
		this.afiChanges = new TreeMap<>();
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
		
		Entry<Date, ArrayList<AFIChange>> lastEntry = afiChanges.lastEntry();
		
		AFIChange tc2 = new AFIChange("TC2", getValueOfEntry(lastEntry.getValue(), "TC2"));
		AFIChange quoteGroup = new AFIChange("GRUPO_COTIZACION", getValueOfEntry(lastEntry.getValue(), "GRUPO_COTIZACION"));
		AFIChange ocupation = new AFIChange("OCUPACION", getValueOfEntry(lastEntry.getValue(), "OCUPACION"));
		AFIChange partialityCoef = new AFIChange("COEFICIENTE_PARCIALIDAD", getValueOfEntry(lastEntry.getValue(), "COEFICIENTE_PARCIALIDAD"));
		
		afiChangesList.add(tc2);
		afiChangesList.add(quoteGroup);
		afiChangesList.add(ocupation);
		afiChangesList.add(partialityCoef);
		
		DateUtils.resetTime(date);
		
		this.afiChanges.put(date, afiChangesList);
	}
	
	private String getValueOfEntry(ArrayList<AFIChange> afiChangeValues, String key) {
		for(AFIChange afiChange : afiChangeValues)
			if(AonStringUtils.equalsIgnoreCase(key, afiChange.getName()))
				return afiChange.getValue();
		return null;
	}

	public void updateAFIChange(Date date, String name, String value) {
		for(AFIChange afiChange : this.afiChanges.get(date))
			if(afiChange.getName().equals(name))
				afiChange.setValue(value);
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

	public boolean hasChange(String type, String value) {
		ArrayList<Date> datesList = new ArrayList<>();
		datesList.addAll(afiChanges.keySet());
		
		if(!datesList.isEmpty() && datesList.size() > 1) {
			Date date = datesList.get(datesList.size() - 1);
			for(AFIChange afiChange : afiChanges.get(date)) {
				if(afiChange.getName().equals(type) && (null != afiChange.getValue() && !value.equals(afiChange.getValue())))
					return true;
			}		
		}
		return false;
	}
	
	public String getChangeValue(String type) {
		ArrayList<Date> datesList = new ArrayList<>();
		datesList.addAll(afiChanges.keySet());
		
		if(!datesList.isEmpty() && datesList.size() > 1) {
			Date date = datesList.get(datesList.size() - 1);
			for(AFIChange afiChange : afiChanges.get(date)) {
				if(afiChange.getName().equals(type))
					return afiChange.getValue();
			}		
		}
		return null;
	}
	
	public Date getChangeDate() {
		ArrayList<Date> datesList = new ArrayList<>();
		datesList.addAll(afiChanges.keySet());
		return datesList.get(datesList.size() - 1);
	}
		
}
