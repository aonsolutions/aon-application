package net.aonsolutions.aon.gwt.warehouse.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;

import net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.CarrierPacking;
import net.aonsolutions.aon.gwt.warehouse.client.elaboration.Elaboration;

public class Warehouse implements EntryPoint {

	
	private static final String ENTRY_POINT_PARAM = "entryPoint";
	
	//    ================================================================== WAREHOUSE
	//

	private static final String CARRIER_PACKING_ENTRY_POINT = "carrier_packing";
	
	private static final String ELABORATION_ENTRY_POINT = "elaboration";

	AonData aonData;

	
	public Warehouse(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Warehouse() {

	}
	
	public void onModuleLoad(String entryPoint){	
		if(CARRIER_PACKING_ENTRY_POINT.equalsIgnoreCase(entryPoint)){
			new CarrierPacking(aonData).onModuleLoad();
		} else if (ELABORATION_ENTRY_POINT.equalsIgnoreCase(entryPoint)) {
			new Elaboration(aonData).onModuleLoad();
		}
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
