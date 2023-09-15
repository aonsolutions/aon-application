package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class DeliveryDetailValidation {

	private DeliveryDetailValidation() {

	}
	
	public static final BiConsumer<AONContext, DeliveryDetail> EMPTY_DOMAIN = (ctx, deliveryDetail) -> {
		if(deliveryDetail.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};
	
	public static void validate(AONContext ctx, DeliveryDetail delivery) throws AonCoreException{
		autocomplete(ctx, delivery);

		EMPTY_DOMAIN
		.accept(ctx, delivery);
	}

	public static final BiConsumer<AONContext, DeliveryDetail> COMPLETE_WAREHOUSE = (ctx, deliveryDetail) -> {
		if(deliveryDetail.getWarehouse() == null) {
			Warehouse warehouse = WarehouseDAO.getWarehouse(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
			deliveryDetail.setWarehouse(warehouse.getId());
		}
	};
	
	public static void autocomplete(AONContext ctx, DeliveryDetail delivery) throws AonCoreException{
		COMPLETE_WAREHOUSE
		.accept(ctx, delivery);
		
	}
	
}
