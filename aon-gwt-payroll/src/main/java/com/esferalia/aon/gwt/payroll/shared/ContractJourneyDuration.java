package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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
		Optional<Date> fromDate = contractJourneyDuration.descendingMap().keySet().stream().findFirst();
		ArrayList<String> visitedDays = new ArrayList<>();
		ArrayList<JourneyDuration> journies = new ArrayList<>();
		for(Entry<Date, ArrayList<JourneyDuration>> jouneryEntry : contractJourneyDuration.descendingMap().entrySet()) {
			if(visitedDays.size() != 7 || journies.size() != 7) {
				for(JourneyDuration journey : jouneryEntry.getValue()) {
					if(!visitedDays.contains(journey.getName()) && journey.getExpression() != "0") {
						visitedDays.add(journey.getName());
						journies.add(journey);
					}
				}
			}
		}
		
		for(JourneyDuration journey : journies) {
			String expression = journey.getExpression();
			if(AonStringUtils.isNotBlank(expression))
				expression = expression.replace(",", ".");
			hours += Double.parseDouble(((null == expression || "" == expression || "NL" == expression) ? "0" : expression));
			if("HORAS_LUNES" == journey.getName()) result += " L : " + ((null == expression || "" == expression) ? "NL" : expression) + " ";
			if("HORAS_MARTES" == journey.getName()) result += ", M : " + ((null == expression || "" == expression) ? "NL" : expression) + " ";
			if("HORAS_MIERCOLES" == journey.getName()) result += ", X : " + ((null == expression || "" == expression) ? "NL" : expression) + " ";
			if("HORAS_JUEVES" == journey.getName()) result += ", J : " + ((null == expression || "" == expression) ? "NL" : expression) + " ";
			if("HORAS_VIERNES" == journey.getName()) result += ", V : " + ((null == expression || "" == expression) ? "NL" : expression) + " ";
			if("HORAS_SABADO" == journey.getName()) result += ", S : " + ((null == expression || "" == expression) ? "NL" : expression) + " ";
			if("HORAS_DOMINGO" == journey.getName()) result += ", D : " + ((null == expression || "" == expression) ? "NL" : expression);
		}
		
		String resultText = "Desde " + formatDate(fromDate.get()) + " " + result + ((hours > 0.0) ? " ("+ (Math.round(hours * 100.0) / 100.0) +" horas semanales)" : "");
		
		return resultText;
	}
	
	private String formatDate(Date date) {
		return date.getDate() + "/" + (date.getMonth()+1) + "/" + (date.getYear()+1900);
	}

}

