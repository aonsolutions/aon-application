package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fee.Fee;
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
		if (f.getWorkplace().getId() == null ) 
			throw new AonCoreException(AonError.EMPTY_WORKPLACE.getMessage());
	};
	
	public static void validate(AONContext ctx, Fee f) throws AonCoreException{
		EMPTY_DOMAIN
			.andThen(EMPTY_WORKPLACE)
			.accept(f, ctx);
	}
}
