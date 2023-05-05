package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.TaxDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ProductAutoComplete {
	
	private ProductAutoComplete() {
	
	}
	
	public static final BiConsumer<AONContext, Product> COMPLETE_DOMAIN = (ctx, product) -> {
		if(product.getDomain() == null || product.getDomain().getId() == null) {
			Domain domain = AON.getDomain(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser());
			product.setDomain(domain);
		}
	};
	
	public static final BiConsumer<AONContext, Product> COMPLETE_NAME = (ctx, product) -> {
		if(AonStringUtils.isBlank(product.getName()) && !AonStringUtils.isBlank(product.getCode())) {
			ctx.log().info("\t saving product: autocomplete name: " + product.getCode());
			product.setName(product.getCode());
		}
	};
	
	public static final BiConsumer<AONContext, Product> COMPLETE_BOOLEANS = (ctx, product) -> {
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
	
	public static final BiConsumer<AONContext, Product> COMPLETE_STATUS = (ctx, product) -> {
		if (product.getStatus() == null) {
			ctx.log().info("\t saving product: autocomplete status: " + ProductStatus.ACTIVE);
			product.setStatus(ProductStatus.ACTIVE);
		}
	};
	
	public static final BiConsumer<AONContext, Product> COMPLETE_VAT = (ctx, product) -> {
		if (product.getVat() != null && product.getVat().getId() == null) {
			Tax t = TaxDAO.getTax(ctx, f -> f.getDomainProperty().eq(product.getDomain().getId())
					.and(f.getPercentageProperty().eq(product.getVat().getPercentage()))
					.and(f.getSurchargeProperty().eq(product.getVat().getSurcharge())
					.and(f.getTaxTypeProperty().eq(TaxType.VAT.value()))));
			if(t.getId() == null) {
				t = TaxDAO.save(ctx, product.getVat());
			}
			ctx.log().info("\t saving product: autocomplete VAT: " + t.getId() + " - " + t.getName());
			product.setVat(t);
		}
	};
	
	public static final BiConsumer<AONContext, Product> COMPLETE_RETENTION = (ctx, product) -> {
		if (product.getRetention() != null && product.getRetention().getId() == null
				&& product.getRetention().getPercentage() > 0) {
			Tax t = TaxDAO.getTax(ctx, f -> f.getDomainProperty().eq(product.getDomain().getId())
					.and(f.getPercentageProperty().eq(product.getRetention().getPercentage()))
					.and(f.getSurchargeProperty().eq(product.getRetention().getSurcharge())
					.and(f.getTaxTypeProperty().eq(TaxType.RETENTION.value()))));
				
			if(t.getId() == null) {
				t = TaxDAO.save(ctx, product.getRetention());
			}
			ctx.log().info("\t saving product: autocomplete RETENTION: " + t.getId() + " - " + t.getName());
			product.setRetention(t);
		}
	};

	public static void autoComplete(AONContext ctx, Product product) throws AonCoreException {
		COMPLETE_DOMAIN
		.andThen(COMPLETE_NAME)
		.andThen(COMPLETE_BOOLEANS)
		.andThen(COMPLETE_STATUS)
		.andThen(COMPLETE_VAT)
		.andThen(COMPLETE_RETENTION)
		.accept(ctx, product);
	}

}
