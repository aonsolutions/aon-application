package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.type.CarrierStatus;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class CarrierValidation {
	
	public static final BiConsumer<Carrier,AONContext> EMPTY_DOMAIN = (carrier,ctx) -> {
		if (carrier.getDomain() == null || carrier.getDomain().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	public static final BiConsumer<Carrier,AONContext> EMPTY_SCOPE = (carrier,ctx) -> {
		if (carrier.getScope().isEmpty()) 
			throw new AonCoreException(AonError.EMPTY_SCOPE.getMessage());
	};
	
	
	public static void validate(AONContext ctx, Carrier carrier) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_SCOPE)
		.accept(carrier, ctx);
	}

	public static void validateDeletion(AONContext ctx, Integer id) {
		// TODO Auto-generated method stub
	}
	
	public static final BiConsumer<Carrier,AONContext> COMPLETE_SCOPE = (carrier,ctx) -> {
		
	};
	
	public static final BiConsumer<Carrier,AONContext> COMPLETE_STATUS = (carrier,ctx) -> {
		if(carrier.getStatus() == null) {
			carrier.setStatus(CarrierStatus.ACTIVE);
		}
	};
	
	public static void autocomplete(AONContext ctx, Carrier carrier) {
		COMPLETE_SCOPE
		.andThen(COMPLETE_STATUS)
		.accept(carrier, ctx);
	}
	
	
}
