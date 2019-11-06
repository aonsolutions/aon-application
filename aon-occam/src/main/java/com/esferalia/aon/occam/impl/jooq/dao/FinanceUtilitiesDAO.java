package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.finance.utilities.MissingFinanceInvoiceItem;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class FinanceUtilitiesDAO {

	public static FinanceUtilitiesResult missingFinanceInvoices(AONContext ctx, FinanceUtilitiesParams params) {
		FinanceUtilitiesResult result = new FinanceUtilitiesResult();
		missingFinanceInvoices(ctx,params,result);
		return result;
	}	
				
	private static void missingFinanceInvoices(AONContext ctx, FinanceUtilitiesParams params, FinanceUtilitiesResult result) {
		Condition where = INVOICE.DOMAIN.equal(ctx.getDomainId())
				.and(INVOICE.TOTAL.ne(0.0))
				.and(FINANCE.ID.isNull());
		if (params != null && params.getInvoiceType() != null) {
			where = where.and(INVOICE.TYPE.eq( params.getInvoiceType().value()));
		}
		if (params != null && params.getFromDate() != null) {
			where = where.and(INVOICE.ISSUE_DATE.ge( AonDateUtils.toSql( params.getFromDate() )));
		}
		if (params != null && params.getToDate() != null) {
			where = where.and(INVOICE.ISSUE_DATE.le( AonDateUtils.toSql( params.getToDate() )));
		}
		ctx.getDslContext().select()
			.from(INVOICE)
			.leftOuterJoin(FINANCE).on(INVOICE.ID.eq(FINANCE.INVOICE))
			.where( where )
				
			.orderBy(INVOICE.DOMAIN,INVOICE.SERIES,INVOICE.NUMBER,INVOICE.TYPE)
			.fetch()
			.stream()
			.map(new MinimalInvoiceFiller())
			.forEach(invoice ->  
				result.add(new MissingFinanceInvoiceItem()
						.setInvoice(invoice)
						.setDomain(ctx.getDomainId())
						.setDomainName(ctx.getDomainName())
						.setMessage("Factura sin vencimientos: " 
								+ invoice.getType().getDescription()
								+ " " + invoice.getReferenceCode()
								+ " " + invoice.getIssueDate() 
								+ " " + invoice.getRegistryName()
								)
						)
			);
	}
	
	public static Invoice missingFinanceInvoicesFix(AONContext ctx, Integer invoiceId) {
		FinanceDAO.insertFinancesForInvoice(ctx, invoiceId);
		return InvoiceDAO.getFullInvoice(ctx, invoiceId); 
	}

	private static class MinimalInvoiceFiller  implements Function<Record,Invoice> {

		@Override
		public Invoice apply(Record record) {
			return new Invoice()
					.setId(record.getValue(INVOICE.ID))
					.setDomain(record.getValue(INVOICE.DOMAIN))
					.setType(AonEnumUtils.enumValue(InvoiceType.class,record.getValue(INVOICE.TYPE)))
					.setSeries(record.getValue(INVOICE.SERIES))
					.setNumber(record.getValue(INVOICE.NUMBER))
					.setReferenceCode(record.getValue(INVOICE.REFERENCE_CODE))
					.setIssueDate(record.getValue(INVOICE.ISSUE_DATE))
					.setTaxDate(record.getValue(INVOICE.TAX_DATE))
					.setTotal(record.getValue(INVOICE.TOTAL))
					.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class,record.getValue(INVOICE.SECURITY_LEVEL)))
					.setRegistry(record.getValue(INVOICE.REGISTRY))
					.setRegistryDocument(record.getValue(INVOICE.RDOCUMENT))
					.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,record.getValue(INVOICE.RDOCUMENT_TYPE)))
					.setRegistryDocumentCountry(Country.safeValueOf(record.getValue(INVOICE.RDOCUMENT_COUNTRY)))
					.setRegistryName(record.getValue(INVOICE.RNAME))
				;
		}
		
	}
	
}
