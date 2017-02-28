package net.aonsolutions.aon.gwt.warehouse.server;

import javax.servlet.annotation.WebServlet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import net.aonsolutions.aon.gwt.warehouse.client.IWarehouse;
import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingParams;

@WebServlet(name = "WarehouseGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_warehouse" })
public class WarehouseImpl extends RemoteServiceServlet implements IWarehouse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public CarrierPackingParams readXml(String xml) {
		return XMLUtils.readXml(xml);
	}

	@Override
	public String writeXml(CarrierPackingParams params) {
		return XMLUtils.writeXml(params);
	}
}