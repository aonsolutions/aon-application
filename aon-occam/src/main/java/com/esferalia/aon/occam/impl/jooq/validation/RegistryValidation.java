package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonDocumentUtil;
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
		if (AonStringUtils.length(reg.getName()) > REGISTRY.NAME.getDataType().length() )
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Nombre o raz\u00F3n social", REGISTRY.NAME.getDataType().length() ));
	};
	
	public static BiConsumer<Registry,AONContext> OVERFLOW_ALIAS = (reg,ctx) -> {
		if (AonStringUtils.length(reg.getAlias()) > REGISTRY.ALIAS.getDataType().length() )
			throw new AonCoreException(AonError.INVALID_LENGTH.format( "Alias", REGISTRY.ALIAS.getDataType().length()));
	};
	
	public static final BiConsumer<Registry,AONContext> EMPTY_DOCUMENT = (reg,ctx) -> {
		if(AonStringUtils.isBlank(reg.getDocument())) {
			throw new AonCoreException(AonError.REGISTRY_EMPTY_DOCUMENT.getMessage());
		}
	};

	public static final BiConsumer<Registry,AONContext> EMPTY_DOCUMENT_TYPE = (reg,ctx) -> {
		if(reg.getDocumentType() == null) {
			throw new AonCoreException(AonError.REGISTRY_EMPTY_DOCUMENT_TYPE.getMessage());
		}
	};
	
	public static final BiConsumer<Registry,AONContext> EMPTY_DOCUMENT_COUNTRY = (reg,ctx) -> {
		if(reg.getDocumentCountry() == null) {
			throw new AonCoreException(AonError.REGISTRY_EMPTY_DOCUMENT_COUNTRY.getMessage());
		}
	};
	
	public static final BiConsumer<Registry,AONContext> NOT_VALID_DOCUMENT = (reg,ctx) -> {
		if(Country.ES == reg.getDocumentCountry() && AonDocumentUtil.isValid(reg.getDocument())) {
			throw new AonCoreException(AonError.REGISTRY_NOT_VALID_DOCUMENT.getMessage());
		} else if(AonDocumentUtil.isValidable(reg.getDocumentType().value(), reg.getDocumentCountry().getAeatCode(), reg.getDocument()) 
				&& AonDocumentUtil.isValidComunitaryCode(reg.getDocumentCountry().getAeatCode(), reg.getDocument())) {
			throw new AonCoreException(AonError.REGISTRY_INVALID_DOCUMENT.getMessage());
		}
	};
	
	public static void validate(AONContext ctx, Registry reg) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(OVERFLOW_DOCUMENT)
		.andThen(OVERFLOW_NAME)
		.andThen(OVERFLOW_ALIAS)
		.accept(reg, ctx);
	}

	public static void validateDeletion(AONContext ctx, Integer id) {
		// TODO Auto-generated method stub
	}
	
	
}
