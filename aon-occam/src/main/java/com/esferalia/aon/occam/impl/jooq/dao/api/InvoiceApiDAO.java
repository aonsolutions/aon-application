package com.esferalia.aon.occam.impl.jooq.dao.api;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO.InvoiceFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.Pair;

public class InvoiceApiDAO {
	
	private InvoiceApiDAO() {
		
	}
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	
	public static Stream<Invoice> getInvoices(AONContext ctx, Integer domainId, InvoiceFilter filter) {
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, domainId, false);
		Integer page = INVOICE_PROPERTIES.getPage(filter);
		if (page == null) page = 1;
		Integer perPage = INVOICE_PROPERTIES.getPerPage(filter);

		if (perPage == null) perPage = Integer.MAX_VALUE;
		return ctx.getDslContext().select()
			.from(INVOICE)
			.leftOuterJoin(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.eq(INVOICE.ID))
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.and(INVOICE.DOMAIN.eq(domainId))
			.groupBy(INVOICE.ID)
			.orderBy(INVOICE.ISSUE_DATE.desc(), INVOICE.ID.desc())
			.limit(perPage)
			.offset(perPage * (page - 1))
			.fetch()
			.stream()
			.map(new InvoiceFiller() )
			.map(i -> i.addCommunicationInfo( InvoiceInfoDAO.getMap(ctx, icc, i).orElse(null) ))
		;
	}
	
	public static Stream<Invoice> getChartInvoices(AONContext ctx, InvoiceFilter filter) {
		return ctx.getDslContext()
			.select(
				INVOICE.ID,
				INVOICE.ISSUE_DATE,
				INVOICE.TYPE,
				INVOICE.TOTAL,
				INVOICE.TAXABLE_BASE)
			.from(INVOICE)
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.groupBy(INVOICE.ID)
			.fetch().stream().map(r -> {
				return new Invoice()
					.setId(r.get(INVOICE.ID))
					.setIssueDate(r.get(INVOICE.ISSUE_DATE))
					.setType(AonEnumUtils.enumValue(InvoiceType.class,r.getValue(INVOICE.TYPE)))
					.setTotal(r.get(INVOICE.TOTAL))
					.setTaxableBase(r.get(INVOICE.TAXABLE_BASE))
					;
			});
	}
	
	public static Pair<Date, Date> getInvoicesChartPeriod(AONContext ctx, InvoiceFilter filter) {
		Record2<java.sql.Date, java.sql.Date> result = 
			ctx.getDslContext().select(
				DSL.min(INVOICE.ISSUE_DATE),
				DSL.max(INVOICE.ISSUE_DATE)
            )
            .from(INVOICE)
            .where(INVOICE_PROPERTIES.getConditions(filter))
            .fetchOne(); // Solo necesitamos un registro, no una lista

        if (result != null) {
            // Extraer las fechas directamente
            Date minDate = result.value1(); // min(INVOICE.ISSUE_DATE)
            Date maxDate = result.value2(); // max(INVOICE.ISSUE_DATE)
            return new Pair<>(minDate, maxDate);
        } 
        return null;
	}
	
	public static Integer getInvoicesCount(AONContext ctx, InvoiceFilter filter) {
		return ctx.getDslContext().selectCount()
				.from(INVOICE)
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.fetchOne(0, int.class);
	}
	
	public static Date getInvoiceExpDate(AONContext ctx, Integer id) {
		return ctx.getDslContext().select(INVOICE_FISCAL.EXP_DATE)
		.from(INVOICE_FISCAL)
		.where(INVOICE_FISCAL.DOMAIN.eq(ctx.getDomainId()))
		.and(INVOICE_FISCAL.INVOICE.eq(id))
		.and(INVOICE_FISCAL.EXP_DATE.isNotNull())
		.fetch().stream()
		.map(r ->  r.getValue(INVOICE_FISCAL.EXP_DATE))
		.findFirst().orElse(null);
	}
	
}
