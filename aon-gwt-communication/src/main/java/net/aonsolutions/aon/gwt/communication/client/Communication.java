package net.aonsolutions.aon.gwt.communication.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class Communication implements EntryPoint {
	
	AonData aonData;
	final ICommunicationAsync impl = GWT.create(ICommunication.class);


	public Communication(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Communication() {

	}
	
	public void onModuleLoad(String entryPoint){
		new CommunicationMain(aonData).onModuleLoad();
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
