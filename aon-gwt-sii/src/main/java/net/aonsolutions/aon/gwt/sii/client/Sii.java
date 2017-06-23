package net.aonsolutions.aon.gwt.sii.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;

import net.aonsolutions.aon.gwt.sii.client.sii.SiiMain;

public class Sii implements EntryPoint {
	
	AonData aonData;

	public Sii(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Sii() {

	}
	
	public void onModuleLoad(String entryPoint){
		new SiiMain(aonData).onModuleLoad();
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
