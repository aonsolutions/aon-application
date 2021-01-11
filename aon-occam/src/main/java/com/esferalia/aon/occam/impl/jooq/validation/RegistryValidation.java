package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class RegistryValidation {
	
	public static BiConsumer<Registry,AONContext> EMPTY_DOMAIN = (reg,ctx) -> {
		if (reg.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	
	public static void validate(AONContext ctx, Registry reg) throws AonCoreException{
		EMPTY_DOMAIN
			.accept(reg, ctx);
	}
	
	
}
