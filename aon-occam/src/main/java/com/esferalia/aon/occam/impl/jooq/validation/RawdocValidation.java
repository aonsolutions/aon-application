package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class RawdocValidation {

	/**
	 * El dominio del apunte no puede estar vacio.
	 */
	public static BiConsumer<Rawdoc,AONContext> EMPTY_DOMAIN = (rawdoc,ctx) -> {
		if (rawdoc.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * La naturaleza del documento no puede estar vacio.
	 */
	public static BiConsumer<Rawdoc,AONContext> EMPTY_NATURE = (rawdoc,ctx) -> {
		if (rawdoc.getNature() == null) 
			throw new AonCoreException(AonError.EMPTY_RAWDOC_NATURE.getMessage());
	};
	
	/**
	 * El tipo del documento no puede estar vacio.
	 */
	public static BiConsumer<Rawdoc,AONContext> EMPTY_TYPE = (rawdoc,ctx) -> {
		if (rawdoc.getType() == null) 
			throw new AonCoreException(AonError.EMPTY_RAWDOC_TYPE.getMessage());
	};

	/**
	 * El status del documento no puede estar vacio.
	 */
	public static BiConsumer<Rawdoc,AONContext> EMPTY_STATUS = (rawdoc,ctx) -> {
		if (rawdoc.getStatus() == null) 
			throw new AonCoreException(AonError.EMPTY_RAWDOC_STATUS.getMessage());
	};
	
	public static void validateRawdoc(AONContext ctx, Rawdoc doc) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_NATURE)
			.andThen(EMPTY_TYPE)
			.andThen(EMPTY_STATUS)
			.accept(doc, ctx);

	}
	
}
