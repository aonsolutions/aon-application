package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.math.BigDecimal;
import java.util.LinkedList;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.SelectLimitStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.stat.SelectableEnum;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class StatsDAO {
	
	public static StatParams createStatParams( AONContext ctx) {
		LinkedList<SelectableEnum<InvoiceType>> invoiceTypes = new LinkedList<SelectableEnum<InvoiceType>>();
		for (InvoiceType type : InvoiceType.values() ) {
			invoiceTypes.add(new SelectableEnum<InvoiceType>(type).setSelected(true));
		}
		return new StatParams(ctx.getDomainName(),ctx.getDomainId(),ctx.getUser())
			.setProductCategories(ProductDAO.getProductCategories(ctx))
			.setInvoiceTypes(invoiceTypes)
			;
	}

	private static SelectLimitStep<Record3<Integer,Byte,BigDecimal>> getSentence( AONContext ctx, StatParams params,
			Field<Integer> when, AggregateFunction<BigDecimal> what ) {
		
		Condition c = INVOICE.DOMAIN.eq(params.getDomain());
		if (params.getInvoiceTypes() != null && params.getInvoiceTypes().size() > 0) {
			Condition invoiceTypeCondition = null; 	
			for (SelectableEnum<InvoiceType> type : params.getInvoiceTypes()) {
				if (type.isSelected()) {
					if (invoiceTypeCondition == null) {
						invoiceTypeCondition = INVOICE.TYPE.eq(type.getType().value()); 
					} else {
						invoiceTypeCondition = invoiceTypeCondition.or( INVOICE.TYPE.eq(type.getType().value()));
					}
				}
			}
			if (invoiceTypeCondition != null) {
				c = c.and(invoiceTypeCondition);	
			}
		}
		
		if (params.getFrom() != null ) {
			c = c.and(INVOICE.ISSUE_DATE.ge(AonDateUtils.toSql(params.getFrom())));
		}
		if (params.getTo() != null ) {
			c = c.and(INVOICE.ISSUE_DATE.le(AonDateUtils.toSql(params.getTo())));
		}
		System.out.println( c );
		
		boolean mustFilterByItemFields = false;
		if (params.getProductCategories() != null && params.getProductCategories().size() > 0) {
			Condition productCategoriesCondition = null;
			for (ProductCategory pc : params.getProductCategories()) {
				if (pc.isSelected()) {
					if (productCategoriesCondition == null) {
						productCategoriesCondition = PRODUCT.CATEGORY.eq(pc.getId()); 
					} else {
						productCategoriesCondition = productCategoriesCondition.or( PRODUCT.CATEGORY.eq(pc.getId()));
					}
				}
			}
			if (productCategoriesCondition != null) {
				c = c.and(productCategoriesCondition);
				mustFilterByItemFields = true;
			}
		} 
		if (mustFilterByItemFields) {
			return ctx.getDslContext()
					.select( when, INVOICE.TYPE, what)
					.from(INVOICE)
					.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where( c )
					.groupBy( when, INVOICE.TYPE )
					.orderBy( when, INVOICE.TYPE )
					;
		}
		return ctx.getDslContext()
				.select( when, INVOICE.TYPE, what)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
				.where( c )
				.groupBy( when, INVOICE.TYPE )
				.orderBy( when, INVOICE.TYPE )
				;
		
	}
	
	public static StatData<Integer,InvoiceType,Double>
			getYearInvoiceTypeData(AONContext ctx,StatParams params) {
		StatData<Integer,InvoiceType,Double> table = new StatData<Integer,InvoiceType,Double>();
		final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
		final Field<Integer> year = DSL.year(INVOICE.ISSUE_DATE);
		getSentence(ctx,params,year,sum) 
			.fetch()
			.stream()
			.forEach( rec -> table.put(
					 rec.getValue(year)
					,InvoiceType.values()[rec.getValue(INVOICE.TYPE)]
					,rec.getValue(sum).doubleValue()) );
		return table;	
	}

	public static StatData<Integer,InvoiceType,Double> 
			getMonthInvoiceTypeData(AONContext ctx,StatParams params) {
		final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
		final Field<Integer> month = DSL.month(INVOICE.ISSUE_DATE);
		StatData<Integer,InvoiceType,Double> table = new StatData<Integer,InvoiceType,Double>();
		getSentence(ctx,params,month,sum)
			.fetch()
			.stream()
			.forEach( rec -> table.put(
					 rec.getValue(month)
					,InvoiceType.values()[rec.getValue(INVOICE.TYPE)]
					,rec.getValue(sum).doubleValue()) );
		return table;	
	}
}
