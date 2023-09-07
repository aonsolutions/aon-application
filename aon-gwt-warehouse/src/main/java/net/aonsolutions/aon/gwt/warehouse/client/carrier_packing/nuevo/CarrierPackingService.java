package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import java.util.List;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingFilter;

@RemoteServiceRelativePath("ms/carrierPacking")
public interface CarrierPackingService extends RemoteService {
	
	List<CarrierPacking> getCarrierPackingList(Occam occam, CarrierPackingFilter params);

}
