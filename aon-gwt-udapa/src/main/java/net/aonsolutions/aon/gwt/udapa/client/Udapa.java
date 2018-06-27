package net.aonsolutions.aon.gwt.udapa.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;

import net.aonsolutions.aon.gwt.udapa.client.quality.UdapaQuality;
import net.aonsolutions.aon.gwt.udapa.client.quality.paturpat.PaturpatQuality;

public class Udapa implements EntryPoint {
		
	private static final String UDAPA_QUALITY_ENTRY_POINT = "udapa";	
	private static final String PATURPAT_QUALITY_ENTRY_POINT = "paturpat";
	
	AonData aonData;

	public Udapa(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Udapa() {

	}
	
	public void onModuleLoad(String entryPoint){
		if(UDAPA_QUALITY_ENTRY_POINT.equalsIgnoreCase(entryPoint)) {
			new UdapaQuality(aonData).onModuleLoad();	
		} else if(PATURPAT_QUALITY_ENTRY_POINT.equalsIgnoreCase(entryPoint)) {
			new PaturpatQuality(aonData).onModuleLoad();
		}
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
