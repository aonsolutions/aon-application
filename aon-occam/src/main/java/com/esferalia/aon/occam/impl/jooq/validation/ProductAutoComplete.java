package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ProductAutoComplete {
	
	public static BiConsumer<AONContext, Product> COMPLETE_DOMAIN = (ctx, product) -> {
		Domain domain = AON.getDomain(ctx.getDomainName(), product.getDomain().getId(), ctx.getUser());
		product.setDomain(domain);
	};
	
	public static BiConsumer<AONContext, Product> COMPLETE_NAME = (ctx, product) -> {
		if(AonStringUtils.isBlank(product.getName()) && !AonStringUtils.isBlank(product.getCode())) {
			ctx.log().info("\t saving product: autocomplete name: " + product.getCode());
			product.setName(product.getCode());
		}
	};
	
	public static BiConsumer<AONContext, Product> COMPLETE_BOOLEANS = (ctx, product) -> {
		if(product.isSerializable() && !product.isInventoriable()) {
			ctx.log().info("\t saving product: autocomplete inventoriable: " + true);
			product.setInventoriable(true);
		}
		if(product.isInventoriable() && product.isComposition()) {
			ctx.log().info("\t saving product: autocomplete composition: " + false);
			product.setComposition(false);
		}
		if(product.isComposition() && product.isCompositionPrice()) {
			ctx.log().info("\t saving product: autocomplete compositionPrice: " + false);
			product.setCompositionPrice(false);	
		}
	};
	
	public static BiConsumer<AONContext, Product> COMPLETE_STATUS = (ctx, product) -> {
		if (product.getStatus() == null) {
			ctx.log().info("\t saving product: autocomplete status: " + ProductStatus.ACTIVE);
			product.setStatus(ProductStatus.ACTIVE);
		}
	};

	public static void autoComplete(AONContext ctx, Product product) throws AonCoreException {
		COMPLETE_DOMAIN
		.andThen(COMPLETE_NAME)
		.andThen(COMPLETE_BOOLEANS)
		.andThen(COMPLETE_STATUS)
			.accept(ctx, product);

	}

}
