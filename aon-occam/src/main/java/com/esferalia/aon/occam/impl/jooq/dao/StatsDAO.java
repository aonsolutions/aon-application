package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import java.math.BigDecimal;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class StatsDAO {

	private static Condition getConditions( StatParams params ) {
		Condition c = INVOICE.DOMAIN.eq(params.getDomain());
		if (params.getInvoiceTypes() != null && params.getInvoiceTypes().size() > 0) {
			Condition invoiceTypeCondition = null; 	
			for (InvoiceType type : params.getInvoiceTypes()) {
				if (invoiceTypeCondition == null) {
					invoiceTypeCondition = INVOICE.TYPE.eq(type.value()); 
				} else {
					invoiceTypeCondition = invoiceTypeCondition.or( INVOICE.TYPE.eq(type.value()));
				}
			}
			c = c.and(invoiceTypeCondition);	
		}
		System.out.println( c );
		return c;
	}
	
	public static StatData<Integer,InvoiceType,Double>
			getYearInvoiceTypeData(AONContext ctx,StatParams params) {
		final AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
		final Field<Integer> year = DSL.year(INVOICE.ISSUE_DATE);
		StatData<Integer,InvoiceType,Double> table = new StatData<Integer,InvoiceType,Double>();
		ctx.getDslContext()
			.select( sum, INVOICE.TYPE, year)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.where( getConditions(params) )
			.groupBy(year, INVOICE.TYPE)
			.orderBy(year, INVOICE.TYPE)
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
		ctx.getDslContext()
			.select( sum, INVOICE.TYPE, month)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.where( getConditions(params) )
			.and(INVOICE.ISSUE_DATE.between(
					AonDateUtils.toSql(params.getFrom())
				   ,AonDateUtils.toSql(params.getTo())))
			.groupBy(month, INVOICE.TYPE)
			.orderBy(month, INVOICE.TYPE)
			.fetch()
			.stream()
			.forEach( rec -> table.put(
					 rec.getValue(month)
					,InvoiceType.values()[rec.getValue(INVOICE.TYPE)]
					,rec.getValue(sum).doubleValue()) );
		return table;	
	}
}
