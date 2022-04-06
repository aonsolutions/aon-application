package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.SSPECData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SSPECObject {
	
	// ----------------------------------------------- Variables 
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private List<SSPECData> ssPECDataList = Collections.emptyList();
	private Integer contractId;
	private Date contractStartDate;
	
	// ----------------------------------------------- Constructor 
	
	public SSPECObject(Integer contractId, Date contractStartDate) {
		this.contractId = contractId;
		this.contractStartDate =  contractStartDate;
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
	
	public void syncSSPECData(Consumer<List<SSPECData>> success, Consumer<Throwable> failure) {
		impl.syncEmployeeSSPECs(this.contractId, new AsyncCallback<List<SSPECData>>() {
			
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
	
}

