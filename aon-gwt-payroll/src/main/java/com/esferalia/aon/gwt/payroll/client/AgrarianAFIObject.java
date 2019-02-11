package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.AgrarianJourney;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AgrarianAFIObject {

	private DomainEnterprisesServiceAsync enterpriseImpl;
	
	private Enterprise enterprise;
	private String enterpriseName;
	private Map<String, CCC> agrarianCCCs;
	
	private Date startDate;
	private Date endDate;
	
	private Integer cccId;
	
	private Map<Integer, List<AgrarianJourney>> agrarianJourney;
	
	public AgrarianAFIObject(DomainEnterprisesServiceAsync newInstance) {
		enterpriseImpl = newInstance;
		enterpriseName = "";
		agrarianCCCs = new HashMap<String, CCC>();
		
		startDate = null;
		endDate = null;
		
		cccId = null;
	}
	
	public String getEnterpriseName(){
		return this.enterpriseName;
	}
	
	public Map<String, CCC> getAgrarianCCCs(){
		return this.agrarianCCCs;
	}
	
	public void getEnterprises(Consumer<List<Enterprise>> success, Consumer<Throwable> failure){
		this.enterpriseImpl.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {
			
			@Override
			public void onSuccess(List<Enterprise> enterprises) {
				if(enterprises.size() == 1){
					enterpriseName = enterprises.get(0).getName();
					enterprise = enterprises.get(0);
				}
				
				for (Enterprise enterprise: enterprises)
					for(Activity activity : enterprise.getActivities())
						for(CCC ccc : activity.getCccs())
							if(ccc.getRegime() == "0163")
								agrarianCCCs.put(activity.getDescription(), ccc);
						
				success.accept(enterprises);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void getAgrarianJourney(Consumer<Map<Integer, List<AgrarianJourney>>> success, Consumer<Throwable> failure){
		
		this.enterpriseImpl.getEmployeeAgrarianJourney(this.startDate, this.endDate, this.cccId, new AsyncCallback<Map<Integer, List<AgrarianJourney>>>() {
			
			@Override
			public void onSuccess(Map<Integer, List<AgrarianJourney>> result) {
				agrarianJourney = result;
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}

	public void setFindingDates(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public void setFindingCCC(String selectedCCC) {
		for(Activity activity : enterprise.getActivities())
			for(CCC ccc : activity.getCccs())
				if(ccc.getCode().equals(selectedCCC))
					this.cccId = ccc.getId();
	}

	public boolean checkDateAgraria(Integer contractId, Date date) {
		List<AgrarianJourney> journiesList = this.agrarianJourney.get(contractId);
		for(AgrarianJourney journey : journiesList){
			if((journey.getStartDate().before(date) || journey.getStartDate().equals(date)) &&
			   (journey.getEndDate().after(date) || journey.getEndDate().equals(date)))
			   return true;
		}
		return false;
	}

	public Map<Integer, List<AgrarianJourney>> getAgrarianJourney() {
		return this.agrarianJourney;
	}

	public Integer getTotalDaysByContract(Integer contractId) {
		Integer totalDays = 0;
		List<AgrarianJourney> journiesList = this.agrarianJourney.get(contractId);
		for(AgrarianJourney journey : journiesList){
			totalDays += journey.getTotalDays();
		}
		return totalDays;
	}
}
