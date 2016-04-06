package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.stat.IStatChartTypeVisitor;
import com.esferalia.aon.occam.api.model.stat.IStatFilterItemVisitor;
import com.esferalia.aon.occam.api.model.stat.StatChartType;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class StatDAO {
	private static final String RESULT = "Resultado";
	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yy");
	protected static final String UNKNOWN = "Desconocido"; 

	public static StatParams createStatParams(AONContext ctx) {
		StatParams params = new StatParams();
		params.setChartType(StatChartType.INVOICE_TYPE_BY_YEAR_COMBO_CHART);
		for (InvoiceType type : InvoiceType.values()) {
			params.getFilterItems().add(
					new StatFilterItem().setId(type.toString())
					.setLabel(type.getDescription())
					.setType(StatFilterType.INVOICE_TYPE));
		}
		for (ProductCategory pc : ProductDAO.getProductCategories(ctx)) {
			params.getFilterItems().add(
					new StatFilterItem().setId(AonNumberUtils.toString(pc.getId()))
					.setLabel(pc.getName())
					.setType(StatFilterType.PRODUCT_CATEGORY));
		}
		for(Workplace wp : WorkplaceDAO.getWorkplaceList(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))){
			params.getFilterItems().add(
			new StatFilterItem()
				.setId(wp.getId())
				.setLabel(wp.getDescription())
				.setType(StatFilterType.WORKPLACE));
		}
		
		CommercialDAO.getSellers(ctx).forEach(
				seller -> params.getFilterItems().add(new StatFilterItem()
							.setId(seller.getId())
							.setLabel(seller.getRegistryName())
							.setType(StatFilterType.SELLER))
						);
	
		return params;
	}
	
		
	//SelectLimitStep para ir creando la condicion de la where, primero con los InvoiceType y luego con ProductCategory...
	private static  Condition getCondition(AONContext ctx, StatParams params) {
		Condition c = INVOICE.DOMAIN.eq(ctx.getDomainId());
		c = c.and(SecurityDAO.getUserScopesCondition(ctx, INVOICE.SCOPE));
		c = c.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), INVOICE.SECURITY_LEVEL));		
		if (params.getFrom() != null) {
			c = c.and(INVOICE.ISSUE_DATE.ge(AonDateUtils.toSql(params.getFrom())));
		}
		if (params.getTo() != null) {
			c = c.and(INVOICE.ISSUE_DATE.le(AonDateUtils.toSql(params.getTo())));
		}
		StatDAOStatFilterItemVisitor visitor = new StatDAOStatFilterItemVisitor();		
		for (StatFilterItem item : params.getFilterItems() ) {
			item.getType().visit(visitor,item);
		}
		c = visitor.appendCondition(c);
		return c;
	}
	
	public static StatData<String, String, Double> getStatData(final AONContext ctx, final StatParams params) {
		
		final StatData<String, String, Double> table = new StatData<String, String, Double>();
		
		params.getChartType().visit( new IStatChartTypeVisitor() {
			
			@Override
			public void visitInvoiceTypeByYearComboChart() {
				final Field<Integer> year = DSL.year(INVOICE.ISSUE_DATE);
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(year, INVOICE.TYPE, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where( getCondition(ctx, params))
					.groupBy(year, INVOICE.TYPE)
					.orderBy(year, INVOICE.TYPE)				
					.fetch()
					.stream()
					.forEach(rec -> {
						InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
						double amount = rec.getValue(sum).doubleValue();
						String y = AonNumberUtils.toString( rec.getValue(year));
						Double d = table.get(y, RESULT);
						d = AonMathUtils.round((d == null ? 0.0 : d) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
						table.put(y, RESULT, d);
						table.put(y, type.getDescription(), amount);
					});
			}

			@Override
			public void visitInvoiceTypeByMonthsComboChart() {
				final Field<Integer> year = DSL.year(INVOICE.ISSUE_DATE);
				final Field<Integer> month = DSL.month(INVOICE.ISSUE_DATE);
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(year,month, INVOICE.TYPE, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where( getCondition(ctx, params))
					.groupBy(year,month, INVOICE.TYPE)
					.orderBy(year,month, INVOICE.TYPE)				
					.fetch()
					.stream()
					.forEach(rec -> {
						InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
						double amount = rec.getValue(sum).doubleValue();
						String m = rec.getValue(month)+"/"+rec.getValue(year);
						Double d = table.get(m, RESULT);
						d = AonMathUtils.round((d == null ? 0.0 : d) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
						table.put(m, RESULT, d);
						table.put(m, type.getDescription(), amount);
					});
			}

			@Override
			public void visitInvoiceTypeByDaysComboChart() {
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(INVOICE.ISSUE_DATE , INVOICE.TYPE, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where( getCondition(ctx, params))
					.groupBy(INVOICE.ISSUE_DATE, INVOICE.TYPE)
					.orderBy(INVOICE.ISSUE_DATE, INVOICE.TYPE)				
					.fetch().stream().forEach(rec -> {
						InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
						double amount = rec.getValue(sum).doubleValue();
						String d = FMT.format( rec.getValue(INVOICE.ISSUE_DATE));
						Double dou = table.get(d, RESULT);
						dou = AonMathUtils.round((dou == null ? 0.0 : dou) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
						table.put(d, RESULT, dou);
						table.put(d, type.getDescription(), amount);
					});
			}

			@Override
			public void visitAbcInvoiceTitular() {
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(INVOICE.REGISTRY, INVOICE.RNAME , INVOICE.TYPE, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where( getCondition(ctx, params))
					.groupBy(INVOICE.REGISTRY, INVOICE.TYPE)
					.orderBy(sum.desc())
					.fetch().stream().forEach(rec -> {
						double d = rec.getValue(sum).doubleValue();
						if (d >= 0) {
							InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
							table.put(rec.getValue(INVOICE.RNAME)
									, type.getDescription()
									, d);
						}
					});
			}

			@Override
			public void visitAbcInvoiceCategory() {
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(PCATEGORY.ID,PCATEGORY.NAME, INVOICE.TYPE, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.join(PCATEGORY).on(PRODUCT.CATEGORY.eq(PCATEGORY.ID))
					.where( getCondition(ctx, params))
					.groupBy(PCATEGORY.ID, INVOICE.TYPE)
					.orderBy(sum.desc())
					.fetch().stream().forEach(rec -> {
						InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
						table.put(AonStringUtils.defaultIfBlank(rec.getValue(PCATEGORY.NAME), UNKNOWN)
								, type.getDescription()
								, rec.getValue(sum).doubleValue());
					});
			}

			@Override
			public void visitAbcInvoiceProduct() {
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(PRODUCT.ID,PRODUCT.NAME, INVOICE.TYPE, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where( getCondition(ctx, params))
					.groupBy(PRODUCT.ID, INVOICE.TYPE)
					.orderBy(sum.desc())
					.fetch().stream().forEach(rec -> {
						double d = rec.getValue(sum).doubleValue();
						if (d >= 0) {
							InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
							table.put(AonStringUtils.defaultIfBlank(rec.getValue(PRODUCT.NAME), UNKNOWN)
									, type.getDescription()
									, d);
						}
					});
			}

			@Override
			public void visitAbcInvoiceWorkplace() {
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(INVOICE_DETAIL.WORKPLACE,WORKPLACE.DESCRIPTION, INVOICE.TYPE, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.eq(INVOICE_DETAIL.WORKPLACE))
					.where( getCondition(ctx, params))
					.groupBy(INVOICE_DETAIL.WORKPLACE, INVOICE.TYPE)
					.orderBy(sum.desc())
					.fetch().stream().forEach(rec -> {
						double d = rec.getValue(sum).doubleValue();
						if (d >= 0) {
							InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
							table.put( AonStringUtils.defaultIfBlank(rec.getValue(WORKPLACE.DESCRIPTION), UNKNOWN)
									, type.getDescription()
									, d);
						}
					});
			}

			@Override
			public void visitAbcInvoiceSeller() {
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(INVOICE_DETAIL.SELLER,REGISTRY.NAME, INVOICE.TYPE, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.leftOuterJoin(REGISTRY).on(INVOICE_DETAIL.SELLER.eq(REGISTRY.ID))
					.where( getCondition(ctx, params))
					.groupBy(INVOICE_DETAIL.SELLER, INVOICE.TYPE)
					.orderBy(sum.desc())
					.fetch().stream().forEach(rec -> {
						double d = rec.getValue(sum).doubleValue();
						if (d >= 0) {
							InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
							table.put(AonStringUtils.defaultIfBlank(rec.getValue(REGISTRY.NAME), UNKNOWN)
									, type.getDescription()
									, d);
						}
					});
			}

			@Override
			public void visitGeoProvince() {
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(GEOZONE.NAME, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.eq(INVOICE.REGISTRY).and(RADDRESS.TYPE.eq((byte) 0)))
					.join(GEOZONE).on(RADDRESS.GEOZONE.eq(GEOZONE.ID))
					.where( getCondition(ctx, params))
					.groupBy(GEOZONE.NAME)
					.orderBy(sum.desc())
					.fetch().stream().forEach(rec -> {
						double d = rec.getValue(sum).doubleValue();
						if (d >= 0) {
							table.put( "CHART"
									, AonStringUtils.defaultIfBlank(rec.getValue(GEOZONE.NAME), UNKNOWN)
									, d);
						}
					});
			}
			
		});
		return table;
	}
	private static class StatDAOStatFilterItemVisitor implements IStatFilterItemVisitor {
		
		private Condition productCategoriesCondition = null;
		private Condition invoiceTypeCondition = null;
		private Condition workplaceCondition = null;
		private Condition sellerCondition = null;

		@Override
		public void visitWorkplaceCondition(StatFilterItem item) {
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.WORKPLACE) {
					int workplaceId = AonNumberUtils.toInteger( item.getId());
					if (workplaceCondition == null) {
						workplaceCondition = INVOICE_DETAIL.WORKPLACE.eq( workplaceId );
					} else {
						workplaceCondition = workplaceCondition.or(INVOICE_DETAIL.WORKPLACE.eq(workplaceId));
					}
				}
			}
		}
		
		@Override
		public void visitProductCategoryCondition(StatFilterItem item) {
			if (item.isSelected() ) {
				int productCategoryId = AonNumberUtils.toInteger( item.getId());
				if (productCategoriesCondition == null) {
					productCategoriesCondition = PRODUCT.CATEGORY.eq( productCategoryId );
				} else {
					productCategoriesCondition = productCategoriesCondition.or(PRODUCT.CATEGORY.eq(productCategoryId));
				}
			}
		}
		
		@Override
		public void visitInvoiceTypeCondition(StatFilterItem item) {
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.INVOICE_TYPE) {
					byte invoiceType = (byte) InvoiceType.valueOf(item.getId()).ordinal();
					if (invoiceTypeCondition == null) {
						invoiceTypeCondition = INVOICE.TYPE.eq(invoiceType);
					} else {
						invoiceTypeCondition = invoiceTypeCondition.or(INVOICE.TYPE.eq(invoiceType));
					}
				}
			}
		}
		
		@Override
		public void visitSellerCondition(StatFilterItem item) {
			if (item.isSelected() ) {
				if (item.getType() == StatFilterType.SELLER) {
					int sellerId = AonNumberUtils.toInteger( item.getId());
					if (sellerCondition == null) {
						sellerCondition = INVOICE_DETAIL.SELLER.eq( sellerId );
					} else {
						sellerCondition = sellerCondition.or(INVOICE_DETAIL.SELLER.eq(sellerId));
					}
				}
			}
		}
		
		public Condition appendCondition(Condition condition) {
			if (invoiceTypeCondition != null) {
				condition = condition.and(invoiceTypeCondition);
			}
			if (productCategoriesCondition != null) {
				condition = condition.and(productCategoriesCondition);
			}
			if (workplaceCondition != null) {
				condition = condition.and(workplaceCondition);
			}
			if (sellerCondition != null) {
				condition = condition.and(sellerCondition);
			}
			return condition;
		}

	}; 
	
}
