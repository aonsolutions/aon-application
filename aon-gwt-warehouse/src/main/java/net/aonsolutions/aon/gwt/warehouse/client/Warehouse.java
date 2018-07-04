package net.aonsolutions.aon.gwt.warehouse.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;

import net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.CarrierPacking;
import net.aonsolutions.aon.gwt.warehouse.client.dataSheet.DataSheet;
import net.aonsolutions.aon.gwt.warehouse.client.elaboration.Elaboration;
import net.aonsolutions.aon.gwt.warehouse.client.movementList.MovementList;
import net.aonsolutions.aon.gwt.warehouse.client.stockForecast.StockForecast;

public class Warehouse implements EntryPoint {

	
	private static final String ENTRY_POINT_PARAM = "entryPoint";
	
	//    ================================================================== WAREHOUSE
	//

	private static final String CARRIER_PACKING_ENTRY_POINT = "carrier_packing";
	
	private static final String ELABORATION_ENTRY_POINT = "elaboration";
	
	private static final String STOCK_FORECAST_ENTRY_POINT = "stockForecast";
	
	private static final String MOVEMENT_LIST_ENTRY_POINT = "movementList";

	private static final String DATA_SHEET_ENTRY_POINT = "dataSheet";
	
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
		} else if (STOCK_FORECAST_ENTRY_POINT.equalsIgnoreCase(entryPoint)) {
			new StockForecast(aonData).onModuleLoad();
		} else if (MOVEMENT_LIST_ENTRY_POINT.equalsIgnoreCase(entryPoint)) {
			new MovementList(aonData).onModuleLoad();
		} else if (DATA_SHEET_ENTRY_POINT.equalsIgnoreCase(entryPoint)) {
			new DataSheet(aonData).onModuleLoad();
		}
	}
	
	@Override
	public void onModuleLoad() {
		String entryPoint = "default";
		onModuleLoad(entryPoint);
	}

}
