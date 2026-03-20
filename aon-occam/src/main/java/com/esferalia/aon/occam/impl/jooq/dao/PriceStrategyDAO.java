package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.TariffCatalogue.TARIFF_CATALOGUE;

import com.esferalia.aon.occam.api.AONContext;

public class PriceStrategyDAO {

	private PriceStrategyDAO() {}

	public static Double getUnitPrice(AONContext ctx, Integer customer, Integer item) {
		Double ritemPrice = ctx.getDslContext()
			.select(RITEM.PRICE)
			.from(RITEM)
			.where(RITEM.REGISTRY.eq(customer))
			.and(RITEM.ITEM.eq(item))
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
			Integer catalogueId = ctx.getDslContext()
				.select(TARIFF_CATALOGUE.CATALOGUE)
				.from(TARIFF_CATALOGUE)
				.where(TARIFF_CATALOGUE.TARIFF.eq(tariffId))
				.and(TARIFF_CATALOGUE.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.orderBy(TARIFF_CATALOGUE.ID)
				.limit(1)
				.fetchOneInto(Integer.class);

			if (catalogueId != null) {
				Double cataloguePrice = ctx.getDslContext()
					.select(CATALOGUE_ITEM.PRICE)
					.from(CATALOGUE_ITEM)
					.where(CATALOGUE_ITEM.CATALOGUE.eq(catalogueId))
					.and(CATALOGUE_ITEM.ITEM.eq(item))
					.and(CATALOGUE_ITEM.PRICE.isNotNull())
					.and(CATALOGUE_ITEM.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
					.limit(1)
					.fetchOneInto(Double.class);

				if (cataloguePrice != null) return cataloguePrice;
			}
		}

		return ctx.getDslContext()
			.select(ITEM.PRICE)
			.from(ITEM)
			.where(ITEM.ID.eq(item))
			.and(ITEM.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.limit(1)
			.fetchOneInto(Double.class);
	}
}
