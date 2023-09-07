package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingFilter;

public class CarrierPackingServiceAsyncDecorator implements CarrierPackingServiceAsync {

	private CarrierPackingServiceAsync esa;

	public CarrierPackingServiceAsyncDecorator(CarrierPackingServiceAsync carrierPackingServiceAsync) {
		this.esa = carrierPackingServiceAsync;
	}
	
	@Override
	public void getCarrierPackingList(Occam occam, CarrierPackingFilter params, AsyncCallback<List<CarrierPacking>> callback) {
		AON.start();
		esa.getCarrierPackingList(occam, params, new AsyncCallbackWrapper<>(callback));
	}
 	
	
}
