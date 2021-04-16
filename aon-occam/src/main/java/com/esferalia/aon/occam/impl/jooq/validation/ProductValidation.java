package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ProductValidation {
	
	// ------------------------------------ PRODUCT
	
	/**
	 * El dominio del producto no puede estar vacio.
	 */
	public static BiConsumer<AONContext, Product> EMPTY_DOMAIN = (ctx, product) -> {
		if (product.getDomain() == null || product.getDomain().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El código del producto no puede estar vacio.
	 */
	public static BiConsumer<AONContext, Product> EMPTY_PRODUCT_CODE = (ctx, product) -> {
		if(AonStringUtils.isBlank(product.getCode()))
			throw new AonCoreException(AonError.EMPTY_PRODUCT_CODE.getMessage());	
	};
	
	/**
	 * El nombre del producto no puede estar vacio.
	 */
	public static BiConsumer<AONContext, Product> EMPTY_PRODUCT_NAME = (ctx, product) -> {
		if(AonStringUtils.isBlank(product.getName()))
			throw new AonCoreException(AonError.EMPTY_PRODUCT_NAME.getMessage());	
	};
	
	/**
	 * El código del producto no puede estar duplicado en el mismo dominio.
	 *  &&
	 * El código del producto no puede estar duplicado con un producto del dominio padre o hijo.
	 */
	public static BiConsumer<AONContext, Product> CHECK_VALID_CODE = (ctx, product) -> {
		int count = ctx.getDslContext().selectCount()
				.from(PRODUCT)
				.where(PRODUCT.DOMAIN.eq(product.getDomain().getId()))
				.and(PRODUCT.CODE.eq(product.getCode()))
				.fetchOne(0, int.class);
		if (count>0) 
			throw new AonCoreException(AonError.DUPLICATE_PRODUCT_CODE.format(product.getCode()));
		
		if(product.getDomain().getParentId() != null && product.getDomain().isEnableHeredity()) {
			count = ctx.getDslContext().selectCount()
				.from(PRODUCT)
				.where(PRODUCT.DOMAIN.eq(product.getDomain().getParentId()))
				.and(PRODUCT.CODE.eq(product.getCode()))
				.fetchOne(0, int.class);
			if(count>0){
				String domain = ctx.getDslContext().select(DOMAIN.NAME)
						.from(DOMAIN).where(DOMAIN.ID.eq(product.getDomain().getParentId()))
						.fetchOne().value1();
				throw new AonCoreException(AonError.DUPLICATE_PRODUCT_CODE_DOMAIN.format(domain));
			}
		}

	};
	
	public static void validate(AONContext ctx, Product product) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_PRODUCT_NAME)
			.andThen(EMPTY_PRODUCT_CODE)
			.accept(ctx, product);
	}
	
	public static void insertValidate(AONContext ctx, Product product) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_PRODUCT_NAME)
			.andThen(EMPTY_PRODUCT_CODE)
			.andThen(CHECK_VALID_CODE)
			.accept(ctx, product);
	}	
}
