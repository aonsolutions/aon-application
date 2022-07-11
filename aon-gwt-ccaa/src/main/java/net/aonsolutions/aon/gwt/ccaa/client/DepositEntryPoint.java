package net.aonsolutions.aon.gwt.ccaa.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class DepositEntryPoint implements EntryPoint {
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	private AonData aonData;
		
	public DepositEntryPoint(AonData aonData) {
		this.aonData = aonData;
	}
	
	@Override
	public void onModuleLoad() {		

	}
	
	public void onModuleLoad(String entryPoint){
		if("deposit_new".equals(entryPoint)) {
			AON.ensureInjected();
			RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
			root.add(new Deposit2(getAonData()));
//			new Deposit(getAonData()).onModuleLoad();
		} else if("deposit_text_mode".equals(entryPoint)){
			new DepositTextMode(getAonData()).onModuleLoad();
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
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
}
