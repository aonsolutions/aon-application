package com.esferalia.aon.occam.impl.jooq;

import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ISerfruit;
import com.esferalia.aon.occam.api.Options;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.occam.impl.jooq.dao.SerfruitDAO;

public class SerfruitImpl implements ISerfruit {
	
	@Override
	public Stream<Sales> getSalesStream(AONContext ctx, SalesFilter filter, Options... options) {
		return ctx.getDslContext().transactionResult(
				configuration -> SerfruitDAO.getSalesStream(ctx, filter, options));
	}

	@Override
	public void saveDeliveryPackaging(AONContext ctx, Delivery delivery, List<DeliveryPackaging> packaging) {
		ctx.getDslContext().transaction(configuration ->  
			SerfruitDAO.saveDeliveryPackaging(ctx, delivery, packaging));
		
	}

	@Override
	public void saveCarrierPacking(AONContext ctx, Delivery delivery, CarrierPacking carrierPacking) {
		// TODO Auto-generated method stub
		
	}
}
