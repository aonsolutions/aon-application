package net.aonsolutions.aon.gwt.warehouse.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingParams;

public interface IWarehouseAsync {

	void readXml(String xml, AsyncCallback<CarrierPackingParams> callback);
	void writeXml(CarrierPackingParams params, AsyncCallback<String> callback);


}
