package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.math.BigDecimal;

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

public class StatDAO {
	private static final String GROSS_MARGIN = "Margen bruto";

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
			
		return params;
	}
	
		
	//SelectLimitStep para ir creando la condicion de la where, primero con los InvoiceType y luego con ProductCategory...
	private static  Condition getCondition(AONContext ctx, StatParams params) {
		Condition c = INVOICE.DOMAIN.eq(ctx.getDomainId());
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
						Double d = table.get(y, GROSS_MARGIN);
						d = AonMathUtils.round((d == null ? 0.0 : d) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
						table.put(y, GROSS_MARGIN, d);
						table.put(y, type.getDescription(), amount);
					});
			}

			@Override
			public void visitInvoiceTypeByMonthsComboChart() {
				final Field<Integer> month = DSL.month(INVOICE.ISSUE_DATE);
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(month, INVOICE.TYPE, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where( getCondition(ctx, params))
					.groupBy(month, INVOICE.TYPE)
					.orderBy(month, INVOICE.TYPE)				
					.fetch()
					.stream()
					.forEach(rec -> {
						InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
						double amount = rec.getValue(sum).doubleValue();
						String m = AonNumberUtils.toString( rec.getValue(month));
						Double d = table.get(m, GROSS_MARGIN);
						d = AonMathUtils.round((d == null ? 0.0 : d) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
						table.put(m, GROSS_MARGIN, d);
						table.put(m, type.getDescription(), amount);
					});
			}

			@Override
			public void visitInvoiceTypeByDaysComboChart() {
				final Field<Integer> day = DSL.day(INVOICE.ISSUE_DATE);
				final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
				ctx.getDslContext().select(day , INVOICE.TYPE, sum)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where( getCondition(ctx, params))
					.groupBy(day, INVOICE.TYPE)
					.orderBy(day, INVOICE.TYPE)				
					.fetch().stream().forEach(rec -> {
						InvoiceType type = InvoiceType.values()[rec.getValue(INVOICE.TYPE)];
						double amount = rec.getValue(sum).doubleValue();
						String d = AonNumberUtils.toString( rec.getValue(day));
						Double dou = table.get(d, GROSS_MARGIN);
						dou = AonMathUtils.round((dou == null ? 0.0 : dou) + (amount * (type == InvoiceType.SALES ? 1 : -1)));
						table.put(d, GROSS_MARGIN, dou);
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
			
		});
		return table;
	}
	private static class StatDAOStatFilterItemVisitor implements IStatFilterItemVisitor {
		
		private Condition productCategoriesCondition = null;
		private Condition invoiceTypeCondition = null;
		private Condition workplaceCondition = null;

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
			return condition;
		}
	}; 
	
}
