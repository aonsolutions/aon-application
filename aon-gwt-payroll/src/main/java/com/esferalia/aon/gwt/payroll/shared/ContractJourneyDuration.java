package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.shared.DateUtils;

public class ContractJourneyDuration implements Serializable {
	
	private TreeMap<Date, ArrayList<JourneyDuration>> contractJourneyDuration;
	
	public ContractJourneyDuration(){
		super();
		this.contractJourneyDuration = new TreeMap<Date, ArrayList<JourneyDuration>>();
	}
	
	public void setContractJourneyDuration(Map<Date, ArrayList<JourneyDuration>> journies) {
		TreeMap<Date, ArrayList<JourneyDuration>> journiesUpdateDates = new TreeMap<>();
		for(Entry<Date, ArrayList<JourneyDuration>> entry : journies.entrySet()) {
			Date newStartDate = new Date(entry.getKey().getTime());
			DateUtils.resetTime(newStartDate);
			journiesUpdateDates.put(newStartDate, entry.getValue());
		}
		contractJourneyDuration = new TreeMap<>(journiesUpdateDates);
	}

	public TreeMap<Date, ArrayList<JourneyDuration>> getContractJourneyDuration() {
		return contractJourneyDuration;
	}

	public void setContractJourneyDuration(Date startDate, ArrayList<JourneyDuration> journeyDurations) {
		this.contractJourneyDuration.put(startDate, journeyDurations);
	}

	public boolean isOverlapDate(Date startDate){
		DateUtils.resetTime(startDate);
		if(this.contractJourneyDuration.keySet().size() == 0){
			return false;
		}else {
			for(Date entryDate : this.contractJourneyDuration.descendingKeySet()) {
//				Window.alert("Entry Date : " + entryDate + ", Start Date : " + startDate);
//				Date newDate = new Date(entryDate.getTime());
//				Window.alert("Entry Date : " + newDate + ", Start Date : " + startDate);
//				DateUtils.resetTime(newDate);
				if(startDate.before(entryDate) || startDate.equals(entryDate))
					return true;
			}
		}
		return false;
	}
	
	public void setEndDatePreviusPeriod(Date newStartDate) {
		if(this.contractJourneyDuration.keySet().size() != 0) {
			if(null == newStartDate) {
				for(JourneyDuration journeyDuration : this.contractJourneyDuration.descendingMap().entrySet().iterator().next().getValue())
					journeyDuration.setEndDate(null);
			}else {
				DateUtils.resetTime(newStartDate);
				Date endDate = DateUtils.copyDateOnly(newStartDate);
				DateUtils.addDays2Date(endDate, -1);
			
				for(JourneyDuration journeyDuration : this.contractJourneyDuration.descendingMap().entrySet().iterator().next().getValue()) {
					journeyDuration.setEndDate(endDate);
//					Window.alert("Journey StartDate : " + journeyDuration.getStartDate() + ", EndDate : " + endDate);
				}
			}
		}
	}
	
	public void delete(Date deletePeriod) {
		Date deleteDate = null;
		DateUtils.resetTime(deletePeriod);
		for(Date key : contractJourneyDuration.keySet()) {
			DateUtils.resetTime(key);
			if(deletePeriod.equals(key)) {
				deleteDate = key;
				break;
			}
		}
		this.contractJourneyDuration.remove(deleteDate);
	}
	
	public Integer getJourniesSize() {
		return this.contractJourneyDuration.entrySet().size();
	}

	public String getJourneyText() {
		String result = "";
		Double hours = 0.0;
		Date fromDate = null;
		ArrayList<String> visitedDays = new ArrayList<>();
		for(Entry<Date, ArrayList<JourneyDuration>> jouneryEntry : contractJourneyDuration.descendingMap().entrySet()) {
			if(visitedDays.size() != 7) {
				fromDate = DateUtils.copyDateOnly(jouneryEntry.getKey());
				for(JourneyDuration journey : jouneryEntry.getValue()) {
					if(!visitedDays.contains(journey.getName()) && journey.getExpression() != "0") {
						visitedDays.add(journey.getName());
						hours += Double.parseDouble(((null == journey.getExpression() || "" == journey.getExpression()) ? "0" : journey.getExpression()));
						if("HORAS_LUNES" == journey.getName()) result += " L : " + ((null == journey.getExpression() || "" == journey.getExpression()) ? "0" : journey.getExpression()) + " ";
						if("HORAS_MARTES" == journey.getName()) result += ", M : " + ((null == journey.getExpression() || "" == journey.getExpression()) ? "0" : journey.getExpression()) + " ";
						if("HORAS_MIERCOLES" == journey.getName()) result += ", X : " + ((null == journey.getExpression() || "" == journey.getExpression()) ? "0" : journey.getExpression()) + " ";
						if("HORAS_JUEVES" == journey.getName()) result += ", J : " + ((null == journey.getExpression() || "" == journey.getExpression()) ? "0" : journey.getExpression()) + " ";
						if("HORAS_VIERNES" == journey.getName()) result += ", V : " + ((null == journey.getExpression() || "" == journey.getExpression()) ? "0" : journey.getExpression()) + " ";
						if("HORAS_SABADO" == journey.getName()) result += ", S : " + ((null == journey.getExpression() || "" == journey.getExpression()) ? "0" : journey.getExpression()) + " ";
						if("HORAS_DOMINGO" == journey.getName()) result += ", D : " + ((null == journey.getExpression() || "" == journey.getExpression()) ? "0" : journey.getExpression());
						
					}
				}
			}
		}
		
		
//		for(JourneyDuration journeyDuration : contractJourneyDuration.descendingMap().entrySet().iterator().next().getValue()) {
//			hours += Double.parseDouble(((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()));
//			if("HORAS_LUNES" == journeyDuration.getName()) result += formatDate(journeyDuration.getStartDate()) + " L : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
//			if("HORAS_MARTES" == journeyDuration.getName()) result += ", M : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
//			if("HORAS_MIERCOLES" == journeyDuration.getName()) result += ", X : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
//			if("HORAS_JUEVES" == journeyDuration.getName()) result += ", J : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
//			if("HORAS_VIERNES" == journeyDuration.getName()) result += ", V : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
//			if("HORAS_SABADO" == journeyDuration.getName()) result += ", S : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
//			if("HORAS_DOMINGO" == journeyDuration.getName()) result += ", D : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression());
//			if(hours > 0.0) result += " ( " + hours + " horas semanales )";
//		}
		
		String resultText = "Desde " + formatDate(fromDate) + " " + result + ((hours > 0.0) ? " ("+hours+" horas semanales)" : "");
		
		return resultText;
	}
	
	private String formatDate(Date date) {
		return date.getDate() + "/" + (date.getMonth()+1) + "/" + (date.getYear()+1900);
	}

}

