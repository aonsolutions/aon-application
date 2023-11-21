package com.esferalia.aon.occam.impl.jooq.dao.api;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceInfo.INVOICE_INFO;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectHavingConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Rawdoc;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.InvoiceRawDocFilter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceAndRaw;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoiceRawDocPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO.InvoiceInfoFiller;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class InvoiceApiDAO {
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	private static final InvoiceRawDocPropertiesDAO INVOICE_RAWDOC_PROPERTIES = new InvoiceRawDocPropertiesDAO();
	public static final Field<LocalDate> ISSUE_DATE =  DSL.field("issue_date", LocalDate.class);
	public static final Field<Boolean> IS_RAWDOC =  DSL.field("is_inbox", Boolean.class);
	public static final Field<Byte> TYPE = DSL.field("type", Byte.class);
	public static final Field<String> REFERENCE_CODE = DSL.field("type", String.class);
	public static final Field<String> REGISTRY_NAME = DSL.field("type", String.class);
	public static final Field<Byte> STATUS = DSL.field("status", Byte.class);
	public static final Field<Integer> MIME_TYPE = DSL.field("mime_type", Integer.class);
	public static final Field<Byte> RAWDOC_STATUS = DSL.field("rawdoc_status", Byte.class);
	
	public static Stream<InvoiceAndRaw> getInvoiceAndRaw(AONContext ctx, InvoiceFilter filter, InvoiceRawDocFilter filterRawdoc) {
		Integer page = INVOICE_PROPERTIES.getPage(filter);
		Integer perPage = INVOICE_PROPERTIES.getPerPage(filter);
		String switchSQL = " CASE ";
		for(int i = 0; i < InvoiceType.values().length; i++) {
			switchSQL += " WHEN REPLACE(JSON_EXTRACT(rawdoc.json, '$.type'), '\"', '') = \"" + InvoiceType.values()[i].getTediName() + "\" THEN " + i;
		}
		switchSQL += " ELSE 1 END";		
		
		SelectHavingStep<Record> query1 = ctx.getDslContext()
				.select(INVOICE.ID)
				.select(INVOICE.DOMAIN)
				.select(INVOICE.TOTAL)
				.select(INVOICE.REFERENCE_CODE)
				.select(INVOICE.NUMBER)
				.select(INVOICE.SERIES)
				.select(INVOICE.ISSUE_DATE)
				.select(INVOICE.RNAME)
				.select(INVOICE.TYPE)
				.select(INVOICE.STATUS)
				.select(DSL.inline(false).as(IS_RAWDOC))
				.select(DSL.inline(null, MIME_TYPE).as(MIME_TYPE))
				.select(DSL.inline(null, RAWDOC_STATUS).as(RAWDOC_STATUS))
				.from(INVOICE)
				.leftOuterJoin(INVOICE_INFO).on(INVOICE_INFO.INVOICE.eq(INVOICE.ID))
				.where(INVOICE_PROPERTIES.getConditions(filter))
				.groupBy(INVOICE.ID); 
		SelectHavingConditionStep<Record> query2 = ctx.getDslContext()
				.select(Rawdoc.RAWDOC.ID)
				.select(Rawdoc.RAWDOC.DOMAIN)
				.select(DSL.field("JSON_EXTRACT(rawdoc.json, '$.total')"))
				.select(DSL.field("REPLACE(JSON_EXTRACT(rawdoc.json, '$.reference'), '\"', '')"))
				.select(DSL.field("REPLACE(JSON_EXTRACT(rawdoc.json, '$.number'), '\"', '')"))
				.select(DSL.field("REPLACE(JSON_EXTRACT(rawdoc.json, '$.serie'), '\"', '')"))
				.select(DSL.field("DATE_FORMAT(SUBSTRING(REPLACE(JSON_EXTRACT(rawdoc.json, '$.date'), '\"', ''),1,10),'%Y-%m-%d')").as(ISSUE_DATE))
				.select(DSL.field("REPLACE(JSON_EXTRACT(rawdoc.json, '$.name'), '\"', '')"))
				.select(DSL.field(switchSQL).as(TYPE))
				.select(DSL.inline((byte) 0).as(STATUS))
				.select(DSL.inline(true).as(IS_RAWDOC))
				.select(Rawdoc.RAWDOC.MIME_TYPE)
				.select(Rawdoc.RAWDOC.STATUS.as(RAWDOC_STATUS))
				.from(Rawdoc.RAWDOC)
				.groupBy(Rawdoc.RAWDOC.ID)
				.having(INVOICE_RAWDOC_PROPERTIES.getConditions(filterRawdoc))
				.and(Rawdoc.RAWDOC.STATUS.eq((byte) 0));
		return query1.union(query2)
				.orderBy(INVOICE.ISSUE_DATE.desc())
				.limit(perPage)
				.offset(perPage * (page -1))
				.fetch().stream().map(new InvoiceAndRawFiller());
	}
	
	public static Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilter filter) {
		Integer page = INVOICE_PROPERTIES.getPage(filter);
		Integer perPage = INVOICE_PROPERTIES.getPerPage(filter);
		
		return ctx.getDslContext().select()
				.from(INVOICE)
				.leftOuterJoin(INVOICE_INFO).on(INVOICE_INFO.INVOICE.eq(INVOICE.ID))
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.groupBy(INVOICE.ID)
			.orderBy(INVOICE.ISSUE_DATE.desc(), INVOICE.ID.desc())
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new InvoiceApiFiller(ctx));
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
	
	public static Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx, Integer invoiceId) {
		return ctx.getDslContext()
				.select()
				.from(INVOICE_DETAIL)
				.leftOuterJoin(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
				.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(INVOICE_DETAIL_ACCOUNT.ACCOUNT))
				.where(INVOICE_DETAIL.INVOICE.eq(invoiceId))
				.fetch().stream().map(new InvoiceDetailApiFiller(ctx));
	}
	
	public static Stream<InvoiceTax> getInvoiceDetailTaxStream(AONContext ctx, Integer invoiceDetailId) {
		return ctx.getDslContext()
				.select()
				.from(INVOICE_TAX)
				.where(INVOICE_TAX.ID.eq(invoiceDetailId))
				.fetch().stream().map(new InvoiceTaxFiller());
	}
	
	public static class InvoiceAndRawFiller extends Filler implements Function<Record,InvoiceAndRaw> {
		@Override
		public InvoiceAndRaw apply(Record r) {
			return new InvoiceAndRaw()
				.setId(r.getValue(INVOICE.ID))
				.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
				.setRegistryName(r.getValue(INVOICE.RNAME))
				.setTotal(r.getValue(INVOICE.TOTAL))
				.setType(InvoiceType.safeValueOf(r.getValue(INVOICE.TYPE)))
				.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
				.setRecorded(r.getValue(INVOICE.STATUS) != null && r.getValue(INVOICE.STATUS) == 1 )
				.setIsInbox(r.getValue(IS_RAWDOC))
				.setMimeType(r.getValue(MIME_TYPE))
				.setNumber(r.getValue(INVOICE.NUMBER))
				.setSeries(r.getValue(INVOICE.SERIES))
				.setRawdocStatus(RawdocStatus.safeValueOf(r.getValue(RAWDOC_STATUS)));
		}
	}

	private static class InvoiceTaxFiller  implements Function<Record,InvoiceTax> {

		@Override
		public InvoiceTax apply(Record record) {
			return new InvoiceTax()
					.setTaxType(TaxType.safeValueOf(record.getValue(INVOICE_TAX.TAX_TYPE)))
					.setPercentage(record.getValue(INVOICE_TAX.PERCENTAGE))
					.setBase(record.getValue(INVOICE_TAX.BASE))
					.setSurcharge(record.getValue(INVOICE_TAX.SURCHARGE))
					.setQuota(record.getValue(INVOICE_TAX.QUOTA))
					.setSurchargeQuota(record.getValue(INVOICE_TAX.SURCHARGE_QUOTA))
					.setDeductiblePercent(record.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT))
					.setDeductibleQuota(record.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA))
					.setVatDeductionType(VatDeductionType.safeValueOf(record.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)))
					.setWithholdingType(WithholdingType.safeValueOf(record.getValue(INVOICE_TAX.WITHHOLDING_TYPE)));	
		}
	}

	public static class InvoiceApiFiller extends Filler implements Function<Record,Invoice> {
		AONContext aonCtx;
		public InvoiceApiFiller(AONContext ctx) {
			this.aonCtx = ctx;
		}
		
		@Override
		public Invoice apply(Record r) {
			Invoice invoice = new Invoice()
				.setId(r.getValue(INVOICE.ID))
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
				.setInvoiceInfo(InvoiceInfoFiller.build(r));

			try (CloseableAONContext ctx = AONContext.getAONContext(aonCtx.getDomainName(),aonCtx.getDomainId(), aonCtx.getUser())){

				invoice.setDetails(getInvoiceDetails(ctx, invoice.getId())
					.collect(Collectors.toCollection(LinkedList::new)));
			
				invoice.setFinances(FinanceDAO.getFinanceStream(ctx, f -> f.getInvoiceProperty().eq(invoice.getId()))
					.collect(Collectors.toCollection(LinkedList::new)));
			
				AccountingInvoiceDAO.fillBreakdown(ctx, invoice);
			} 
			return invoice;
		}
	}
	
	

	private static class InvoiceDetailApiFiller  implements Function<Record,InvoiceDetail> {

		AONContext aonCtx;
		public InvoiceDetailApiFiller(AONContext ctx) {
			this.aonCtx = ctx;
		}
		
		@Override
		public InvoiceDetail apply(Record record) {
			InvoiceDetail invoiceDetail = new InvoiceDetail()
				.setId(record.getValue(INVOICE_DETAIL.ID))
				.setInvoice(new Invoice().setId(record.getValue(INVOICE_DETAIL.INVOICE)))
				.setProject( record.getValue( INVOICE_DETAIL.PROJECT ))
				.setLine(record.getValue( INVOICE_DETAIL.LINE ))
				.setDescription(record.getValue( INVOICE_DETAIL.DESCRIPTION ))
				.setQuantity(record.getValue(INVOICE_DETAIL.QUANTITY))
				.setPrice(record.getValue(INVOICE_DETAIL.PRICE))
				.setDiscountExpression(record.getValue(INVOICE_DETAIL.DISCOUNT_EXPR))
				.setTaxableBase(record.getValue(INVOICE_DETAIL.TAXABLE_BASE))
				.setTaxes(record.getValue(INVOICE_DETAIL.TAXES))
				.setSeller(new Seller().copy(new Registry().setId(record.getValue(INVOICE_DETAIL.SELLER))))
				.setWorkPlace(record.getValue(INVOICE_DETAIL.WORKPLACE))
				.setWarehouse(record.getValue(INVOICE_DETAIL.WAREHOUSE))
				.setAccount(record.getValue(ACCOUNT.ID))
				.setAccountCode(record.getValue(ACCOUNT.CODE))
				.setAccountDescription(record.getValue(ACCOUNT.DESCRIPTION));
		
			try (CloseableAONContext ctx = AONContext.getAONContext(aonCtx.getDomainName(),aonCtx.getDomainId(), aonCtx.getUser())){

				invoiceDetail.setInvoiceTaxes(getInvoiceDetailTaxStream(ctx, invoiceDetail.getId())
					.collect(Collectors.toCollection(LinkedList::new)));
			}
			return invoiceDetail;
		}
	}
	
	
}
