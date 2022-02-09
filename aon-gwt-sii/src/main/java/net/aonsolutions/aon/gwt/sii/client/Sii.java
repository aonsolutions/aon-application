package net.aonsolutions.aon.gwt.sii.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;

import net.aonsolutions.aon.gwt.sii.client.sii.SiiMain;

public class Sii implements EntryPoint {
	
	AonData aonData;
	final ISiiAsync impl = GWT.create(ISii.class);


	public Sii(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Sii() {

	}
	
	public void onModuleLoad(String entryPoint){
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC
		));
		
		Polymer.whenReady(o -> {
			new SiiMain(aonData).onModuleLoad();
			return null;
		});
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
