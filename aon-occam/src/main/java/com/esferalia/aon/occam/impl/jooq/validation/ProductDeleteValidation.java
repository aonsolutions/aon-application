package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.error.AonCoreException;

public class ProductDeleteValidation {
	
	// ------------------------------------ PRODUCT
	
	public static BiConsumer<AONContext, Integer> EXIST_CUSTOMER_FEE = (ctx, productId) -> {
		int count = ctx.getDslContext().selectCount()
				.from(CUSTOMER_FEE)
				.join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.where(PRODUCT.ID.eq(productId))
				.and(CUSTOMER_FEE.DOMAIN.eq(PRODUCT.DOMAIN))
				.fetchOne(0, int.class);
		
		if (count>0) 
			throw new AonCoreException("No se puede eliminar el producto. Est\u00e1 siendo usar por " + count + " cuotas.");

	};
	
	public static void validate(AONContext ctx, Integer productId) throws AonCoreException {
		EXIST_CUSTOMER_FEE
//			.andThen(EMPTY_PRODUCT_NAME)
			.accept(ctx, productId);
	}
}
