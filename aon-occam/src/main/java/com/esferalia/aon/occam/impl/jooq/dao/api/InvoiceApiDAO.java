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
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.Pair;

public class InvoiceApiDAO {
	
	private InvoiceApiDAO() {
		
	}
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	
	public static Stream<Invoice> getInvoices(AONContext ctx, Integer domainId, InvoiceFilter filter) {
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, domainId);
		Integer page = INVOICE_PROPERTIES.getPage(filter);
		if (page == null) page = 1;
		Integer perPage = INVOICE_PROPERTIES.getPerPage(filter);
		if (perPage == null) perPage = Integer.MAX_VALUE;
		return ctx.getDslContext().select()
			.from(INVOICE)
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.and(INVOICE.DOMAIN.eq(domainId))
			.groupBy(INVOICE.ID)
			.orderBy(INVOICE.ISSUE_DATE.desc(), INVOICE.ID.desc())
			.limit(perPage)
			.offset(perPage * (page - 1))
			.fetch()
			.stream()
			.map(new InvoiceApiFiller() )
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
	
	private static class InvoiceApiFiller extends Filler implements Function<Record,Invoice> {

		@Override
		public Invoice apply(Record r) {
			return new Invoice()
				.setId(getValue(r, INVOICE.ID))
				.setDomain(r.getValue(INVOICE.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class,r.getValue(INVOICE.TYPE)))
				.setSeries(r.getValue(INVOICE.SERIES))
				.setNumber(r.getValue(INVOICE.NUMBER))
				.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
				.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
				.setTaxDate(r.getValue(INVOICE.TAX_DATE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class, r.getValue(INVOICE.SECURITY_LEVEL)))
			
				.setRegistry(r.getValue(INVOICE.REGISTRY))
				.setRegistryDocument(r.getValue(INVOICE.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,r.getValue(INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(r.getValue(INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(r.getValue(INVOICE.RNAME))
				
				.setSigned(getBoolean(r, INVOICE.SIGNED))
//				.setAddressProvinceCode(record.getValue(GEOZONE.CODE))
//				.setAddressProvince(record.getValue(GEOZONE.NAME))
//				.setAddressTown(record.getValue(RADDRESS.CITY))
//				.setAddressZIP(record.getValue(RADDRESS.ZIP))
				
//				.setScope(new Scope().setId(record.getValue(SCOPE.ID)).setDescription(record.getValue(SCOPE.DESCRIPTION)))
				.setActivity(new EnterpriseActivity().setId(r.getValue(INVOICE.ACTIVITY)))	
				.setInvestAsset(r.getValue(INVOICE.INVEST_ASSET))
				.setProject(r.getValue(INVOICE.PROJECT))
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class,r.getValue(INVOICE.RECTIFICATION_TYPE)))	
				.setRectificationInvoice(r.getValue(INVOICE.RECTIFICATION_INVOICE))	
				.setTransaction(AonEnumUtils.enumValue(InvoiceTransactionType.class, r.getValue(INVOICE.TRANSACTION)))
				.setRecorded(r.getValue(INVOICE.STATUS) != null && r.getValue(INVOICE.STATUS) == 1 )	
				.setSurcharge(r.getValue(INVOICE.SURCHARGE) == 1 )	
				.setWithholding(r.getValue(INVOICE.WITHHOLDING) == 1 )	
				.setWithholdingFarmer(r.getValue(INVOICE.WITHHOLDING_FARMER) == 1 )	
				.setVatAccrualPayment(r.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1 )	
				.setInvestment(r.getValue(INVOICE.INVESTMENT) == 1 )	
				.setService(r.getValue(INVOICE.SERVICE) == 1 )	
				.setAdvance(r.getValue(INVOICE.ADVANCE) == 1 )	
				.setTaxableBase(r.getValue(INVOICE.TAXABLE_BASE))	
				.setVatQuota(r.getValue(INVOICE.VAT_QUOTA))	
				.setRetentionQuota(r.getValue(INVOICE.RETENTION_QUOTA))	
				.setTotal(r.getValue(INVOICE.TOTAL))	
				.setComments(r.getValue(INVOICE.COMMENTS))
				.setCreationDate(r.getValue(INVOICE.CREATION_DATE))
				.setCreationUser(r.getValue(INVOICE.CREATION_USER))
				.setModificationDate(r.getValue(INVOICE.MODIFICATION_DATE))
				.setModificationUser(r.getValue(INVOICE.MODIFICATION_USER))
				;
		}
	}
	
}
