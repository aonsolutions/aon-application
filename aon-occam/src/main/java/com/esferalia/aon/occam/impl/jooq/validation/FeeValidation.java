package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class FeeValidation {

	/**
	 * El dominio de las cuotas no puede estar vacio.
	 */
	public static BiConsumer<Fee,AONContext> EMPTY_DOMAIN = (f,ctx) -> {
		if (f.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El lugar de trabajo de las cuotas no puede estar vacio.
	 */
	public static BiConsumer<Fee,AONContext> EMPTY_WORKPLACE = (f,ctx) -> {

		if (f.getWorkplaceId() == null ) 
			throw new AonCoreException(AonError.EMPTY_WORKPLACE.getMessage());
	};
	
	public static BiConsumer<Fee, AONContext> CHECK_FEE = (f, ctx) -> {
		if (f.getSecurityLevel() == null) {
    		f.setSecurityLevel((byte) SecurityLevel.OFFICIAL.ordinal());
    	}
	};
	
	public static void validate(AONContext ctx, Fee f) throws AonCoreException{
		EMPTY_DOMAIN
			.andThen(EMPTY_WORKPLACE)
			.andThen(CHECK_FEE)
			.accept(f, ctx);
	}
}
