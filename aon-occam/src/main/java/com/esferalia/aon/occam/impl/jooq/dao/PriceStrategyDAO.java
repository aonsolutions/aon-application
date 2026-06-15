package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Catalogue.CATALOGUE;
import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.TariffCatalogue.TARIFF_CATALOGUE;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DiscountExpression;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.RItemFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class PriceStrategyDAO {

	private PriceStrategyDAO() {}
	
	public static class PriceStrategy {
		Double price;
		DiscountExpression discountExpression;
		
		public PriceStrategy(Double price, DiscountExpression discountExpression) {
			this.price = price;
			this.discountExpression = discountExpression;
		}
		
		public Double getPrice() {
			return price;
		}
		
		public PriceStrategy setPrice(Double price) {
			this.price = price;
			return this;
		}
		
		public DiscountExpression getDiscountExpression() {
			return discountExpression;
		}
		
		public PriceStrategy setDiscountExpression(DiscountExpression discountExpression) {
			this.discountExpression = discountExpression;
			return this;
		}
	}
	
	
	public static PriceStrategy calculatePriceStrategy(AONContext ctx, Integer customer, Date date, Item item) {
		RegistryItem ritem = ctx.getDslContext()
				.select()
				.from(RITEM)
				.where(RITEM.REGISTRY.eq(customer))
				.and(RITEM.ITEM.eq(item.getId()))
				.and(RITEM.PRICE.isNotNull())
				.and(RITEM.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.limit(1)
				.fetch().stream().map(new RItemFiller()).findFirst().orElse(null);
			
		if(ritem != null && (ritem.getPrice() != 0.0 || ritem.getDiscount() != 0.0)) {
			return new PriceStrategy(ritem.getPrice() == 0.0 ? item.getPrice() : ritem.getPrice(), ritem.getDiscountExpression());
		}
		
		Integer tariffId = ctx.getDslContext()
				.select(CUSTOMER.TARIFF)
				.from(CUSTOMER)
				.where(CUSTOMER.REGISTRY.eq(customer))
				.and(CUSTOMER.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.limit(1)
				.fetchOneInto(Integer.class);
		
		if (tariffId != null) {			
			Tariff tariff = TariffDAO.get(ctx, tariffId);
			
			PriceStrategy priceStrategy = ctx.getDslContext()
					.select(CATALOGUE_ITEM.PRICE, CATALOGUE_ITEM.DISCOUNT)
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
					.fetch().stream().map(r -> new PriceStrategy(r.getValue(CATALOGUE_ITEM.PRICE), new DiscountExpression(r.getValue(CATALOGUE_ITEM.DISCOUNT))))
							.findFirst().orElse(null);
				
			if(priceStrategy != null ) return priceStrategy;
		
			if(tariff.getDiscount() != 0.0) {
				return new PriceStrategy(item.getPrice(), new DiscountExpression(tariff.getDiscount()));
			}
		}
		
		return new PriceStrategy(item.getPrice(), new DiscountExpression());
	}
		
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
