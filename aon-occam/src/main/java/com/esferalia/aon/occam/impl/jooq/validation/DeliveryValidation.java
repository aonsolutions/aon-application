package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.Date;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
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
		if(delivery.getIssueTime() == null) 
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
		if(delivery.getIssueTime() == null) {
			delivery.setIssueTime(new Date());
		}
	};
	
	public static void autocomplete(AONContext ctx, Delivery delivery) throws AonCoreException{
		COMPLETE_DATE 
		.accept(ctx, delivery);
		
	}
	
}
