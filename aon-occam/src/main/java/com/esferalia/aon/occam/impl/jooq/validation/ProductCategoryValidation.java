package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ProductCategoryValidation {
	
	/**
	 * El dominio del producto no puede estar vacio.
	 */
	public static BiConsumer<AONContext, ProductCategory> EMPTY_DOMAIN = (ctx, pc) -> {
		if (pc.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El nombre del producto no puede estar vacio.
	 */
	public static BiConsumer<AONContext, ProductCategory> EMPTY_NAME = (ctx, pc) -> {
		if(AonStringUtils.isBlank(pc.getName()))
			throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
	};

	public static void validate(AONContext ctx, ProductCategory pc) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_NAME)
			.accept(ctx, pc);
	}
		
}
