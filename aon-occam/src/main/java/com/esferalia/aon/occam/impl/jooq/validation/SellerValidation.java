package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;


public class SellerValidation {
	
	private SellerValidation() {
		
	}
	
	/**
	 * Throws an exception if the question is null
	 */
	private static final BiConsumer<AONContext, Seller> NULL = (ctx, seller) -> {
		if (seller == null)
			throw new AonCoreException(AonError.SELLER_NULL.getMessage());
	};
	
	/**
	 * Throws an exception if the domain is null
	 */
	private static final BiConsumer<AONContext, Seller> EMPTY_DOMAIN = (ctx, seller) -> {
		if (seller != null && seller.getDomain() == null)
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * Throws an exception if the text is null
	 */
	private static final BiConsumer<AONContext, Seller> NULL_NAME = (ctx, seller) -> {
		if (seller != null && AonStringUtils.isBlank(seller.getName()))
			throw new AonCoreException(AonError.NULL_SELLER_NAME.getMessage());
	};
	
	public static void validate(AONContext ctx, Seller seller) throws AonCoreException{
		NULL
		.andThen(EMPTY_DOMAIN)
		.andThen(NULL_NAME)
		.accept(ctx, seller);
	}

}
