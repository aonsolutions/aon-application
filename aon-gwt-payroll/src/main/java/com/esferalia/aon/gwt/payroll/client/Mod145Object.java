package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.mod145.Mod145;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod145Object {
	
	// ----------------------------------------------- Variables 
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private Integer contractId;
	private Integer domainId;
	private List<Mod145> mod145List;
	
	// ----------------------------------------------- Constructor 
	
	public Mod145Object(Integer contractId, Integer domainId) {
		this.contractId = contractId;
		this.domainId = domainId;
		this.mod145List = new ArrayList<>();
	}

	// ----------------------------------------------- DataBase.Methods
	
	public void getMod145List(Consumer<List<Mod145>> success, Consumer<Throwable> failure) {
		impl.getMod145List(this.contractId, new AsyncCallback<List<Mod145>>() {
			
			@Override
			public void onSuccess(List<Mod145> mod145ListIn) {
				mod145List = mod145ListIn;
				success.accept(mod145ListIn);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void saveMod145(Mod145 mod145, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.saveMod145(mod145, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void printMod145(Mod145 mod145, Consumer<String> success, Consumer<Throwable> failure) {
		impl.printMod145(mod145, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				success.accept(dataURI);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	// ----------------------------------------------- Methods
	
	public Integer getDomainId() {
		return this.domainId;
	}
	
	public Integer getContractId() {
		return this.contractId;
	}
	
	public List<Date> getDateList() {
		List<Date> dateList = this.mod145List.stream().map(mod145 -> mod145.getStartDate()).collect(Collectors.toList());
		if(!dateList.isEmpty() && dateList.size() > 1) {
			dateList.sort((o1, o2) -> o1.compareTo(o2));
			Collections.reverse(dateList);
		}
		return dateList;
	}

	public Mod145 getMod145ByDate(Date date) {
		Optional<Mod145> mod145Opt = this.mod145List.stream().filter(mod145 -> mod145.getStartDate().equals(date)).findFirst();
		return mod145Opt.isPresent() ? mod145Opt.get() : null;
	}

	public Mod145 getRecentMod145() {
		List<Date> dateList = getDateList();
		if(dateList.isEmpty()) return null;
		
		return getMod145ByDate(dateList.get(0));
	}
}

