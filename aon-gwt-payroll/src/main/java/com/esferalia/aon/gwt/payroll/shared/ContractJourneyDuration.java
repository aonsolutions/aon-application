package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.user.client.Window;

public class ContractJourneyDuration implements Serializable {
	
	private TreeMap<Date, ArrayList<JourneyDuration>> contractJourneyDuration;
	
	public ContractJourneyDuration(){
		super();
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
				Window.alert("Entry Date : " + entryDate + ", Start Date : " + startDate);
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
					Window.alert("Journey StartDate : " + journeyDuration.getStartDate() + ", EndDate : " + endDate);
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

}

