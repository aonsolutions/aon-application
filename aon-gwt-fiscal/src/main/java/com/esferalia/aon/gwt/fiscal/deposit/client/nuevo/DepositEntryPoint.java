package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo;

import static com.esferalia.aon.gwt.common.client.AONEntryPoint.getParameter;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemoryAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DepositEntryPoint implements EntryPoint {
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	private AonData aonData;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getCurrentUser()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;
		
	public DepositEntryPoint(AonData aonData) {
		this.aonData = aonData;
	}
	
	@Override
	public void onModuleLoad() {
		
	}
	
	public void onModuleLoad(String entryPoint){
		if("deposit_new".equals(entryPoint)) {
			new Deposit(getAonData()).onModuleLoad();
		} else {
			new com.esferalia.aon.gwt.fiscal.deposit.client.Deposit(getAonData()).onModuleLoad();
		}
	}
	
	// GETTERS && SETTERS
	
	public AonData getAonData() {
		return aonData;
	}
	
	public DepositEntryPoint setAonData(AonData aonData) {
		this.aonData = aonData;
		return this;
	}
	

}
