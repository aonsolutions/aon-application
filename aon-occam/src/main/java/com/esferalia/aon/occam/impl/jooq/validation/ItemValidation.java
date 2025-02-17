package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Item.ITEM;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ItemValidation {
	
	// ------------------------------------ PRODUCT
	
	/**
	 * El dominio del item no puede estar vacio.
	 */
	public static BiConsumer<AONContext, Item> EMPTY_DOMAIN = (ctx, item) -> {
		if (item.getDomain() == null || item.getDomain().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * El codigo de barras no puede estar duplicado
	 */
	public static BiConsumer<AONContext, Item> DUPLICATE_BAR_CODE = (ctx, item) -> {
		int count = ctx.getDslContext().selectCount()
				.from(ITEM)
				.where(ITEM.DOMAIN.eq(item.getDomain().getId()))
				.and(ITEM.BARCODE.eq(item.getBarcode()))
				.and(ITEM.ID.ne(item.getId()))
				.fetchOne(0, int.class);
		if (count>0) 
			throw new AonCoreException("El c\u00f3digo de barras est\u00ed duplicado");
	};
	
	public static void validate(AONContext ctx, Item item) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(DUPLICATE_BAR_CODE)
			.accept(ctx, item);
	}
}
