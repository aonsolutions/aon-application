package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

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
		} else if("deposit_text_mode".equals(entryPoint)){
			new DepositTextMode(getAonData()).onModuleLoad();
		} else {
		
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
