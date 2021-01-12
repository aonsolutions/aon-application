package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RegistryValidation {
	
	public static BiConsumer<Registry,AONContext> EMPTY_DOMAIN = (reg,ctx) -> {
		if (reg.getDomain() == null || reg.getDomain().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	public static BiConsumer<Registry,AONContext> OVERFLOW_DOCUMENT = (reg,ctx) -> {
		if (AonStringUtils.length(reg.getDocument()) > REGISTRY.DOCUMENT.getDataType().length() )
			throw new AonCoreException(AonError.REGISTRY_OVERFLOW_DOCUMENT.getMessage());
	};
	
	public static BiConsumer<Registry,AONContext> OVERFLOW_NAME = (reg,ctx) -> {
		if (AonStringUtils.length(reg.getDocument()) > REGISTRY.NAME.getDataType().length() )
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Nombre o razón social", REGISTRY.NAME.getDataType().length() ));
	};
	
	public static BiConsumer<Registry,AONContext> OVERFLOW_ALIAS = (reg,ctx) -> {
		if (AonStringUtils.length(reg.getDocument()) > REGISTRY.ALIAS.getDataType().length() )
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Alias", REGISTRY.ALIAS.getDataType().length() ));
	};
	
	public static void validate(AONContext ctx, Registry reg) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(OVERFLOW_DOCUMENT)
		.andThen(OVERFLOW_NAME)
		.accept(reg, ctx);
	}
	
	
}
