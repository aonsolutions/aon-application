package com.esferalia.aon.occam.api;

import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;

public interface ISerfruit {

	Stream<Sales> getSalesStream(AONContext ctx, SalesFilter filter, Options... options);

	public void saveDeliveryPackaging(AONContext ctx, Delivery delivery, List<DeliveryPackaging> packaging);
	
	public void saveCarrierPacking(AONContext ctx, Delivery delivery, CarrierPacking carrierPacking);
	
}
