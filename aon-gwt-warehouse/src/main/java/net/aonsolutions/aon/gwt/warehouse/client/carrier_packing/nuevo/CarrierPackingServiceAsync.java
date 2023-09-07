package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import java.util.List;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingFilter;

public interface CarrierPackingServiceAsync {

	void getCarrierPackingList(Occam occam, CarrierPackingFilter params, AsyncCallback<List<CarrierPacking>> callback);
}
