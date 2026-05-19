package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Catalogue.CATALOGUE;
import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.TariffCatalogue.TARIFF_CATALOGUE;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.watson.server.AonDateUtils;

public class PriceStrategyDAO {

	private PriceStrategyDAO() {}

	public static Double getUnitPrice(AONContext ctx, Integer customer, Date date, Item item ) {
		Double ritemPrice = ctx.getDslContext()
			.select(RITEM.PRICE)
			.from(RITEM)
			.where(RITEM.REGISTRY.eq(customer))
			.and(RITEM.ITEM.eq(item.getId()))
			.and(RITEM.PRICE.isNotNull())
			.and(RITEM.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.limit(1)
			.fetchOneInto(Double.class);

		if (ritemPrice != null && ritemPrice != 0.0) return ritemPrice;

		Integer tariffId = ctx.getDslContext()
			.select(CUSTOMER.TARIFF)
			.from(CUSTOMER)
			.where(CUSTOMER.REGISTRY.eq(customer))
			.and(CUSTOMER.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.limit(1)
			.fetchOneInto(Integer.class);

		if (tariffId != null) {			
			Double price = ctx.getDslContext()
					.select(CATALOGUE_ITEM.PRICE)
					.from(TARIFF_CATALOGUE)
					.join(CATALOGUE).on(CATALOGUE.ID.eq(TARIFF_CATALOGUE.CATALOGUE))
					.join(CATALOGUE_ITEM).on(CATALOGUE_ITEM.CATALOGUE.eq(CATALOGUE.ID))
					.where(TARIFF_CATALOGUE.TARIFF.eq(tariffId))
					.and(TARIFF_CATALOGUE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
					.and(CATALOGUE.START_DATE.lessOrEqual(AonDateUtils.toSql(date)))
					.and(CATALOGUE.END_DATE.greaterOrEqual(AonDateUtils.toSql(date)))
					.and(CATALOGUE_ITEM.PRODUCT.eq(item.getProduct().getId()))
					.and(CATALOGUE_ITEM.PRICE.isNotNull())
					.and(CATALOGUE_ITEM.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
					.orderBy(CATALOGUE.START_DATE.desc())
					.limit(1)
					.fetch().stream().map(r -> r.getValue(CATALOGUE_ITEM.PRICE)).findFirst().orElse(null);
			
			if(price != null && price != 0.0) return price;
		}

		return ctx.getDslContext()
			.select(ITEM.PRICE)
			.from(ITEM)
			.where(ITEM.ID.eq(item.getId()))
			.and(ITEM.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.limit(1)
			.fetchOneInto(Double.class);
	}
}
