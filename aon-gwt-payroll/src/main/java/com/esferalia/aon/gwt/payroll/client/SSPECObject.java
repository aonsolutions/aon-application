package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.SSPECData;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SSPECObject {
	
	// ----------------------------------------------- Variables 
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private List<SSPECData> ssPECDataList;
	private List<SSPECData> ssPECDataListFiltered;
	private Integer contractId;
	private Date contractStartDate;
	private Date contractEndDate;
	
	// ----------------------------------------------- Constructor 
	
	public SSPECObject(Integer contractId, Date contractStartDate, Date contractEndDate) {
		this.contractId = contractId;
		this.contractStartDate =  contractStartDate;
		this.contractEndDate = contractEndDate;
		this.ssPECDataList = new ArrayList<>();
		this.ssPECDataListFiltered = new ArrayList<>();
	}

	// ----------------------------------------------- DataBase.Methods
	
	public void getSSPECData(Consumer<List<SSPECData>> success, Consumer<Throwable> failure) {
		impl.getEmployeeSSPECs(this.contractId, new AsyncCallback<List<SSPECData>>() {
			
			@Override
			public void onSuccess(List<SSPECData> result) {
				ssPECDataList = result;
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void syncSSPECData(Date startDate, Date endDate, Consumer<List<SSPECData>> success, Consumer<Throwable> failure) {
		if ( startDate == null ) {
			startDate = this.getContractStartDate();
		}
		if ( endDate == null ) {
			endDate = this.getContractEndDate();
		}
		impl.syncEmployeeSSPECs(this.contractId, startDate, endDate, new AsyncCallback<List<SSPECData>>() {
			
			@Override
			public void onSuccess(List<SSPECData> result) {
				ssPECDataList = result;
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public List<SSPECData> getSSPecDatas(String yearValue, String monthValue) {
		this.ssPECDataListFiltered.clear();
		
		// Se filtra solo por tipo de variable sin fechas
		if(AonStringUtils.isBlank(yearValue))
			this.ssPECDataListFiltered.addAll(this.ssPECDataList);
		// Se filtra por tipo de variable y fechas
		else {
			Integer year = Integer.parseInt(yearValue);
			Date startDate = DateUtils.getFirstDayOfYear(year - 1900);
			Date endDate = DateUtils.getLastDayOfYear(year - 1900);
			
			if(AonStringUtils.isNotBlank(monthValue)) {
				Integer month = Integer.parseInt(monthValue);
				startDate.setMonth(month);
				startDate = DateUtils.getFirstDayOfMonth(startDate);
				endDate = DateUtils.getLastDayOfMonth(startDate);
			}
			
			for(SSPECData ssPECData : this.ssPECDataList) {
				if(	(isInPeriod(startDate, endDate, ssPECData.getStartDate(), ssPECData.getEndDate()) ||
					(null != ssPECData.getEndDate() && isInPeriod(startDate, endDate, ssPECData.getEndDate(), ssPECData.getEndDate())) ||
					(null == ssPECData.getEndDate() && (isInPeriod(startDate, endDate, ssPECData.getStartDate(), ssPECData.getEndDate()) || DateUtils.isBeforeOrEquals(ssPECData.getStartDate(), startDate)))))
					
					this.ssPECDataListFiltered.add(ssPECData);
			}
		}
		
		this.ssPECDataListFiltered.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
		Collections.reverse(this.ssPECDataListFiltered);
		
		return this.ssPECDataListFiltered;
	}
	
	private boolean isInPeriod(Date start, Date end, Date varStart, Date varEnd) {
		return null == varStart || 
				(DateUtils.isAfterOrEquals(varStart, start) && DateUtils.isBeforeOrEquals(varStart, end)) || 
				(null == varEnd && DateUtils.isBeforeOrEquals(varStart, end)) ||
				(DateUtils.isBeforeOrEquals(varStart, start) && DateUtils.isAfterOrEquals(varEnd, end));
	}
	
	// ----------------------------------------------- Getter
	
	public List<SSPECData> getSSPECDataList() {
		return this.ssPECDataList;
	}
	
	public Integer getContractId() {
		return this.contractId;
	}
	
	public Date getContractStartDate() {
		return this.contractStartDate;
	}
	
	public Date getContractEndDate() {
		return this.contractEndDate;
	}
	
}

