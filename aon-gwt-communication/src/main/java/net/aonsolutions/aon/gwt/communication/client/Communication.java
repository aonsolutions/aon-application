package net.aonsolutions.aon.gwt.communication.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;

import net.aonsolutions.aon.gwt.communication.client.CommunicationMain;

public class Communication implements EntryPoint {
	
	AonData aonData;
	final ICommunicationAsync impl = GWT.create(ICommunication.class);


	public Communication(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Communication() {

	}
	
	public void onModuleLoad(String entryPoint){
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC
		));
		
		Polymer.whenReady(o -> {
			new CommunicationMain(aonData).onModuleLoad();
			return null;
		});
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
