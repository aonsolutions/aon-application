package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ContractBonusObject {
	
	// ----------------------------------------------- Variables 
	
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	private List<SSBonusData> ssBonusList = Collections.emptyList();
	private Integer contractId;
	
	// ----------------------------------------------- Constructor 
	
	public ContractBonusObject(Integer contractId) {
		this.contractId = contractId;
	}

	// ----------------------------------------------- DataBase.Methods
	
	public void getSSBonus(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {
		enterprisesService.getEmployeeSSBonuses(this.contractId, new AsyncCallback<List<SSBonusData>>() {
			
			@Override
			public void onSuccess(List<SSBonusData> ssBonusListResult) {
				ssBonusList = ssBonusListResult;
				success.accept(ssBonusListResult);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void syncSSBonus(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {
		enterprisesService.syncSSBonus(this.contractId, new AsyncCallback<List<SSBonusData>>() {
			
			@Override
			public void onSuccess(List<SSBonusData> ssBonusListResult) {
				ssBonusList = ssBonusListResult;
				success.accept(ssBonusListResult);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	// ----------------------------------------------- Getter
	
	public List<SSBonusData> getSSBonusList() {
		return this.ssBonusList;
	}
	
}

