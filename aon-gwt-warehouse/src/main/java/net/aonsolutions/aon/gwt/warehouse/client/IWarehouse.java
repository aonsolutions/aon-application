package net.aonsolutions.aon.gwt.warehouse.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingParams;

@RemoteServiceRelativePath("gwt_warehouse")
public interface IWarehouse extends RemoteService{

	public CarrierPackingParams readXml(String xml);
	public String writeXml(CarrierPackingParams params);


}
