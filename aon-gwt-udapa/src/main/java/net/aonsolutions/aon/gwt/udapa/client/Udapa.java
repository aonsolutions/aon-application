package net.aonsolutions.aon.gwt.udapa.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;

import net.aonsolutions.aon.gwt.udapa.client.quality.UdapaQuality;

public class Udapa implements EntryPoint {
	
	AonData aonData;

	public Udapa(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Udapa() {

	}
	
	public void onModuleLoad(String entryPoint){
		new UdapaQuality(aonData).onModuleLoad();
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
