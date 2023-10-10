package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.Date;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class DeliveryValidation {

	private DeliveryValidation() {

	}
	
	public static final BiConsumer<AONContext, Delivery> EMPTY_DOMAIN = (ctx, delivery) -> {
		if(delivery.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};
	
	public static final BiConsumer<AONContext, Delivery> EMPTY_CUSTOMER = (ctx, delivery) -> {
		if(delivery.getCustomer() == null || delivery.getCustomer().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("customer"));
	};
	
	public static final BiConsumer<AONContext, Delivery> EMPTY_DATE = (ctx, delivery) -> {
		if(delivery.getDate() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("date"));
	};

	public static void validate(AONContext ctx, Delivery delivery) throws AonCoreException{
		autocomplete(ctx, delivery);

		EMPTY_DOMAIN
		.andThen(EMPTY_CUSTOMER)
		.andThen(EMPTY_DATE)
		.accept(ctx, delivery);
	}

	public static final BiConsumer<AONContext, Delivery> COMPLETE_DATE = (ctx, delivery) -> {
		if(delivery.getDate() == null) {
			delivery.setDate(new Date());
		}
	};
	
	public static final BiConsumer<AONContext, Delivery> COMPLETE_TOTAL_PACKAGES = (ctx, delivery) -> {
		if(delivery.getTotalPackages() == null) {
			delivery.setTotalPackages(0.0);
		}
	};
	
	public static final BiConsumer<AONContext, Delivery> COMPLETE_TOTAL_WEIGHT = (ctx, delivery) -> {
		if(delivery.getTotalWeight() == null) {
			delivery.setTotalWeight(0.0);
		}
	};
	
	public static final BiConsumer<AONContext, Delivery> COMPLETE_REGISTRY_ADDRESS = (ctx, delivery) -> {
		if(delivery.getAddress() == null || delivery.getAddress().getId() == null) {
			RegistryAddressDAO.getMain(ctx, delivery.getCustomer().getId());
		}
	};
	
	public static void autocomplete(AONContext ctx, Delivery delivery) throws AonCoreException{
		COMPLETE_DATE
		.andThen(COMPLETE_TOTAL_PACKAGES)
		.andThen(COMPLETE_TOTAL_WEIGHT)
		.andThen(COMPLETE_REGISTRY_ADDRESS)
		.accept(ctx, delivery);
		
	}
	
}
