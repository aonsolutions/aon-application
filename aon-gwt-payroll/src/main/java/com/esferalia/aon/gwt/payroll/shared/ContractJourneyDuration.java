package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ContractJourneyDuration implements Serializable {
	
	private Map<Date, ArrayList<JourneyDuration>> contractJourneyDuration;
	
	public ContractJourneyDuration(){
		super();
		this.contractJourneyDuration = new HashMap<Date, ArrayList<JourneyDuration>>();
	}

	public Map<Date, ArrayList<JourneyDuration>> getContractJourneyDuration() {
		return contractJourneyDuration;
	}

	public void setContractJourneyDuration(Map<Date, ArrayList<JourneyDuration>> contractJourneyDuration) {
		this.contractJourneyDuration = contractJourneyDuration;
	}
	
	public void setContractJourneyDuration(Date startDate, ArrayList<JourneyDuration> journeyDurations) {
		this.contractJourneyDuration.put(startDate, journeyDurations);
	}
	
	public boolean isOverlapDate(Date startDate){
		
		if(this.contractJourneyDuration.keySet().size() == 0){
			return false;
		}else if(this.contractJourneyDuration.keySet().size() == 1){
			JourneyDuration journeyDuration = this.contractJourneyDuration.get(0).get(0);
			if(null == journeyDuration.getEndDate())
				return false;
			else{
				if(startDate.before(journeyDuration.getEndDate()))
					return true;
				else
					return false;
			}
		}else{
			for(Entry<Date, ArrayList<JourneyDuration>> entry : this.contractJourneyDuration.entrySet()){
				if(startDate.before(entry.getKey()) || startDate.equals(entry.getKey()))
					return true;
				else{
					JourneyDuration journeyDuration = this.contractJourneyDuration.get(entry.getKey()).get(0);
					if(null == journeyDuration.getEndDate())
						return false;
					if(startDate.before(journeyDuration.getEndDate()) || startDate.equals(journeyDuration.getEndDate()))
						return true;
				}
			}
		}
		
		return false;
	}
	
	public Date getStartDate(Date startDate){
		
		if(this.contractJourneyDuration.keySet().size() == 0){
			return startDate;
		}else if(this.contractJourneyDuration.keySet().size() == 1){
			JourneyDuration journeyDuration = this.contractJourneyDuration.get(0).get(0);
			if(null == journeyDuration.getEndDate())
				return journeyDuration.getStartDate();
			else{
				if(startDate.before(journeyDuration.getEndDate()))
					return null;
				else
					return journeyDuration.getEndDate();
			}
		}else{
			for(Entry<Date, ArrayList<JourneyDuration>> entry : this.contractJourneyDuration.entrySet()){
				if(startDate.before(entry.getKey()) || startDate.equals(entry.getKey()))
					return null;
				else{
					JourneyDuration journeyDuration = this.contractJourneyDuration.get(entry.getKey()).get(0);
					if(null == journeyDuration.getEndDate())
						return journeyDuration.getStartDate();
					if(startDate.before(journeyDuration.getEndDate()) || startDate.equals(journeyDuration.getEndDate()))
						return null;
				}
			}
		}
		
		return null;
	}
	
}

