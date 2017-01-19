package net.aonsolutions.aon.gwt.warehouse.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;

import net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.CarrierPacking;

public class Warehouse implements EntryPoint {

	
	private static final String ENTRY_POINT_PARAM = "entryPoint";
	
	//    ================================================================== STATS
	//

	private static final String CARRIER_PACKING_ENTRY_POINT = "carrier_packing";

	AonData aonData;

	
	public Warehouse(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Warehouse() {

	}
	
	public void onModuleLoad(String entryPoint){
		
		if(entryPoint.equalsIgnoreCase(CARRIER_PACKING_ENTRY_POINT)){
			new CarrierPacking(aonData).onModuleLoad();
		}
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
