package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.watson.error.AonCoreException;

public class ItemAutoComplete {
	
	private ItemAutoComplete() {
	
	}
	
	public static final BiConsumer<AONContext, Item> COMPLETE_DOMAIN = (ctx, item) -> {
		if(item.getDomain() == null || item.getDomain().getId() == null) {
			Domain domain = AON.getDomain(ctx.getDomainName(), item.getDomain().getId(), ctx.getUser());
			item.setDomain(domain);
		}
	};
	
	public static final BiConsumer<AONContext, Item> COMPLETE_STATUS = (ctx, item) -> {
		if (item.getStatus() == null) {
			ctx.log().debug("\t saving item: autocomplete status: " + ProductStatus.ACTIVE);
			item.setStatus(ProductStatus.ACTIVE);
		}
	};
	
	public static void autoComplete(AONContext ctx, Item item) throws AonCoreException {
		COMPLETE_DOMAIN
		.andThen(COMPLETE_STATUS)
		.accept(ctx, item);
	}

}
