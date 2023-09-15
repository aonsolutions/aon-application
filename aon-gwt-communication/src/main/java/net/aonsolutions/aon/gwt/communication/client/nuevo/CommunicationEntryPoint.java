package net.aonsolutions.aon.gwt.communication.client.nuevo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import net.aonsolutions.aon.gwt.communication.client.ICommunication;
import net.aonsolutions.aon.gwt.communication.client.ICommunicationAsync;

public class CommunicationEntryPoint implements EntryPoint {
	
	final ICommunicationAsync impl = GWT.create(ICommunication.class);

	
	public CommunicationEntryPoint() {

	}
	
	@Override
	public void onModuleLoad() {
		new CommunicationMain().onModuleLoad();
	}

}
