package com.esferalia.aon.gwt.stat;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.AggregateFunction;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.server.AonDateUtils;

public class StatDAO {

	// statTest
	public static List<InvoiceStat> getInvoices2(AONContext ctx, InvoiceStatParams params) {
		AggregateFunction<BigDecimal> sum = DSL.sum(INVOICE_DETAIL.TAXABLE_BASE);
		return ctx.getDslContext().select(INVOICE.ISSUE_DATE, sum).from(INVOICE).join(INVOICE_DETAIL)
				.on(INVOICE_DETAIL.INVOICE.eq(INVOICE.ID)).where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
				.groupBy(INVOICE.ISSUE_DATE).fetch().stream()
				.map(rec -> new InvoiceStat().setIssueDate(rec.getValue(INVOICE.ISSUE_DATE))
						.setTaxableBase(rec.getValue(sum).doubleValue()))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	// StatTest3
	public static List<InvoiceStat> getInvoices3(AONContext ctx3) {
		return ctx3.getDslContext()
				.select(INVOICE.RNAME, INVOICE.REGISTRY, REGISTRY.NATIONALITY, REGISTRY.ID, REGISTRY.DOMAIN)
				.from(INVOICE).join(REGISTRY).on(INVOICE.REGISTRY.eq(REGISTRY.ID)).where(INVOICE.RNAME.like("%SEKY%"))
				.and(REGISTRY.NATIONALITY.like("ES")).orderBy(INVOICE.REGISTRY).fetch().stream()
				.map(rec -> new InvoiceStat().setRname(rec.getValue(INVOICE.RNAME))
						.setRegistry(rec.getValue(INVOICE.REGISTRY)).setNationality(rec.getValue(REGISTRY.NATIONALITY))
						.setId(rec.getValue(REGISTRY.ID)).setDomain(rec.getValue(REGISTRY.DOMAIN)))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	
	
	
	
	
	// getInvoicesGrafico1
	public static List<InvoiceStat> getInvoicesGrafico1(AONContext ctx, InvoiceStatParams params) {

		java.sql.Date date1 = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(2014) );
		java.sql.Date date2 = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(2016) );

		// con esta consigo ventas, compras... entre las fechas
		return ctx.getDslContext()
				.select(DSL.count(), INVOICE.TYPE)
				.from(INVOICE)
				.where(INVOICE.DOMAIN.eq(400)
						.and(INVOICE.ISSUE_DATE						
								.between(date1).and(date2)))
				.groupBy(INVOICE.TYPE)
				.fetch()
				.stream()
				.map(rec -> new InvoiceStat()
						.setCount((int) rec.getValue(DSL.count()))
						.setType(rec.getValue(INVOICE.TYPE)))
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	
	
	
	

	// getInvoicesGrafico2
	public static List<InvoiceStat> getInvoicesGrafico2(AONContext ctx) {

		// Consigo las ventas en el 2014 por meses
		return ctx.getDslContext()
				.select(DSL.count(), INVOICE.TYPE, DSL.month(INVOICE.ISSUE_DATE))
				.from(INVOICE)
				.where(INVOICE.DOMAIN.eq(400)
						.and(INVOICE.ISSUE_DATE.like("%2014%"))
						.and(INVOICE.TYPE.like("0")))
				.groupBy(DSL.month(INVOICE.ISSUE_DATE))
				.fetch()
				.stream()
				.map(rec -> new InvoiceStat()
						.setCount((int) rec.getValue(DSL.count()))
						.setIssueDateMonth(rec.getValue(DSL.month(INVOICE.ISSUE_DATE))))
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	
	

	// getInvoicesGrafico3
	public static List<InvoiceStat> getInvoicesGrafico3(AONContext ctx) {

		//Necesito las ventas(type 0), gastos(type 1)...del 2014 POR MESES
		return ctx.getDslContext()
				.select(DSL.count(), INVOICE.TYPE, DSL.month(INVOICE.ISSUE_DATE))
				.from(INVOICE)
				.where(INVOICE.DOMAIN.eq(400)
						.and(INVOICE.ISSUE_DATE.like("%2014%")))
				.groupBy(INVOICE.TYPE, DSL.month(INVOICE.ISSUE_DATE))
				.fetch()
				.stream()
				.map(rec -> new InvoiceStat()
						.setCount((int) rec.getValue(DSL.count()))
						.setType(rec.getValue(INVOICE.TYPE))
						.setIssueDateMonth(rec.getValue(DSL.month(INVOICE.ISSUE_DATE))))
				.collect(Collectors.toCollection(LinkedList::new));
	}

}








