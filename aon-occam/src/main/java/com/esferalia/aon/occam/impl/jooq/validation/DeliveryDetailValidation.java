package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

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
	
	public static final BiConsumer<AONContext, DeliveryDetail> COMPLETE_DESCRIPTION = (ctx, deliveryDetail) -> {
		if(AonStringUtils.isBlank(deliveryDetail.getDescription())) {
			String serialNumber = deliveryDetail.getItem().getSerialNumber();
			String description = deliveryDetail.getItem().getDescription();
			Product product = deliveryDetail.getItem().getProduct();
			if(AonStringUtils.isBlank(description)) {
				Item item = ItemDAO.get(ctx, f -> f.getIdProperty().eq(deliveryDetail.getItem().getId()));
				serialNumber = item.getSerialNumber();
			}
			
			if(!AonStringUtils.isBlank(description) &&  description.contains(serialNumber)) {
				deliveryDetail.setDescription(description);
			} else if(product != null && !AonStringUtils.isBlank(product.getName())) {
				deliveryDetail.setDescription(product.getName() + " #" + serialNumber);
			} else if(product != null && product.getId() != null) {
				Integer productId = product.getId();
				product = ProductDAO.get(ctx, f -> f.getIdProperty().eq(productId));
				deliveryDetail.setDescription(product.getName() + " #" + serialNumber);
			} else {
				Item item = ItemDAO.get(ctx, f -> f.getIdProperty().eq(deliveryDetail.getItem().getId()), new Options().setFull(true));
				deliveryDetail.setDescription(item.getProduct().getName() + " #" + item.getSerialNumber());
			}
		}
	};
	
	public static void autocomplete(AONContext ctx, DeliveryDetail delivery) throws AonCoreException{
		COMPLETE_WAREHOUSE
		.andThen(COMPLETE_DESCRIPTION)
		.accept(ctx, delivery);
		
	}
	
}
