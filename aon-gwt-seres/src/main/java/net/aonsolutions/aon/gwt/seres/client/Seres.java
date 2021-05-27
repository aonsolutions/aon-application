package net.aonsolutions.aon.gwt.seres.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;

import net.aonsolutions.aon.gwt.seres.client.seres.SeresMain;

public class Seres implements EntryPoint {
	
	AonData aonData;
	final ISeresAsync impl = GWT.create(ISeres.class);


	public Seres(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Seres() {

	}
	
	public void onModuleLoad(String entryPoint){
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC
		));
		
		Polymer.whenReady(o -> {
			new SeresMain(aonData).onModuleLoad();
			return null;
		});
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
