package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.Date;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class SalesValidation {

	private SalesValidation() {

	}
	
	public static final BiConsumer<AONContext, Sales> EMPTY_CUSTOMER = (ctx, sales) -> {
		if(sales.getCustomer() == null || sales.getCustomer().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("customer"));
	};
	
	public static final BiConsumer<AONContext, Sales> EMPTY_DATE = (ctx, sales) -> {
		if(sales.getDate() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("date"));
	};

	public static void validate(AONContext ctx, Sales sales) throws AonCoreException{
		autocomplete(ctx, sales);

		EMPTY_CUSTOMER
		.andThen(EMPTY_DATE)
		.accept(ctx, sales);
	}

	public static final BiConsumer<AONContext, Sales> COMPLETE_DATE = (ctx, sales) -> {
		if(sales.getDate() == null) {
			sales.setDate(new Date());
		}
	};
	
	public static void autocomplete(AONContext ctx, Sales sales) throws AonCoreException{
		COMPLETE_DATE 
		.accept(ctx, sales);
		
	}
	
}
