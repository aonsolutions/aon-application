package net.aonsolutions.aon.gwt.invoice.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;

import net.aonsolutions.aon.gwt.invoice.client.sabbatic.SabbaticMain;

public class Invoice implements EntryPoint {
	
	AonData aonData;

	public Invoice(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Invoice() {

	}
	
	public void onModuleLoad(String entryPoint){
		Polymer.importHref(Arrays.asList(
			IronIconsElement.SRC
		));
		
		Polymer.whenReady(o -> {
			new SabbaticMain(aonData).onModuleLoad();
			return null;
		});
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
