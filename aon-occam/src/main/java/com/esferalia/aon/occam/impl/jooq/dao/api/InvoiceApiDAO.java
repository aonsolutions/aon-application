package com.esferalia.aon.occam.impl.jooq.dao.api;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceInfo.INVOICE_INFO;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.Order.InvoiceOrder;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceNewPortal;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertyOrdersDAO.InvoicePropertyOrdersDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO.InvoiceInfoFiller;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class InvoiceApiDAO {
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	private static final InvoicePropertyOrdersDAO INVOICE_PROPERTY_ORDERS = new InvoicePropertyOrdersDAO();
	private static final Table<?> retention = INVOICE_TAX.as("retention");
	private static final Table<?> tax = INVOICE_TAX.as("tax");
	private static final Field<BigDecimal> surchargeQuota = DSL.sum(tax.field(INVOICE_TAX.SURCHARGE_QUOTA));
	private static final Field<BigDecimal> quota = DSL.sum(tax.field(INVOICE_TAX.QUOTA));
	private static final Field<Double> retentionPercentage = retention.field(INVOICE_TAX.PERCENTAGE);
	
	public static Stream<InvoiceNewPortal> getInvoiceNewPortal(AONContext ctx, InvoiceFilter filter, InvoiceOrder order) {
		Integer page = INVOICE_PROPERTIES.getPage(filter);
		Integer perPage = INVOICE_PROPERTIES.getPerPage(filter);
		return ctx.getDslContext()
			.select(INVOICE.ID)
			.select(INVOICE.DOMAIN)
			.select(INVOICE.TOTAL)
			.select(INVOICE.REFERENCE_CODE)
			.select(INVOICE.NUMBER)
			.select(INVOICE.SERIES)
			.select(INVOICE.ISSUE_DATE)
			.select(INVOICE.RNAME)
			.select(INVOICE.TYPE)
			.select(INVOICE.RDOCUMENT)
			.select(INVOICE.STATUS)
			.select(INVOICE_ATTACH.MIMETYPE)
			.select(INVOICE_INFO.fields())
			.select(INVOICE.TAXABLE_BASE)
			.select(surchargeQuota)
			.select(quota)
			.select(retentionPercentage)
			.from(INVOICE)
			.leftJoin(INVOICE_ATTACH).on(INVOICE.ID.eq(INVOICE_ATTACH.INVOICE))
			.leftJoin(INVOICE_INFO).on(INVOICE.ID.eq(INVOICE_INFO.INVOICE).and(INVOICE_INFO.TYPE.eq(InvoiceCommunicationType.EMAIL.value())))
			.leftJoin(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.leftJoin(INVOICE_TAX.asTable(retention)).on(INVOICE_DETAIL.ID.eq(retention.field(INVOICE_TAX.INVOICE_DETAIL)).and(retention.field(INVOICE_TAX.TAX_TYPE).eq((byte)2)))
			.leftJoin(INVOICE_TAX.asTable(tax)).on(INVOICE_DETAIL.ID.eq(tax.field(INVOICE_TAX.INVOICE_DETAIL)).and(tax.field(INVOICE_TAX.TAX_TYPE).eq((byte)1)))
			.groupBy(INVOICE.ID)
			.having(INVOICE_PROPERTIES.getConditions(filter))
			.orderBy(INVOICE_PROPERTY_ORDERS.getOrders(order))
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new InvoiceNewPortalFiller());
	}
	
	public static long getInvoiceNewPortalCount(AONContext ctx, InvoiceFilter filter) {
		return ctx.getDslContext()
				.select().from(INVOICE)
				.where(INVOICE_PROPERTIES.getConditions(filter))
				.fetch()
				.stream()
				.count();				
	}
	
	public static void updateInvoiceNote(AONContext ctx,Integer id,  String comment) {
		ctx.getDslContext()
		.update(INVOICE)
		.set(INVOICE.COMMENTS, comment)
		.where(INVOICE.DOMAIN.eq(ctx.getDomainId())
		.and(INVOICE.ID.eq(id)))
		.execute();
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
	
	public static class InvoiceNewPortalFiller extends Filler implements Function<Record,InvoiceNewPortal> {
		@Override
		public InvoiceNewPortal apply(Record r) {
			return new InvoiceNewPortal()
				.setId(r.getValue(INVOICE.ID))
				.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
				.setRegistryName(r.getValue(INVOICE.RNAME))
				.setRegistryDocument(r.getValue(INVOICE.RDOCUMENT))
				.setTotal(r.getValue(INVOICE.TOTAL))
				.setType(InvoiceType.safeValueOf(r.getValue(INVOICE.TYPE)))
				.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
				.setRecorded(r.getValue(INVOICE.STATUS) != null && r.getValue(INVOICE.STATUS) == 1 )
				.setNumber(r.getValue(INVOICE.NUMBER))
				.setSeries(r.getValue(INVOICE.SERIES))
				.setMimeType(MimeType.safeValueOf(r.getValue(INVOICE_ATTACH.MIMETYPE)))
				.setInvoiceInfo(InvoiceInfoFiller.build(r))
				.setVatQuota(r.getValue(quota) != null ? r.getValue(quota).doubleValue() : 0)
				.setTaxableBase(r.getValue(INVOICE.TAXABLE_BASE) != null ? r.getValue(INVOICE.TAXABLE_BASE).doubleValue() : 0)
				.setSurchargeQuota(r.getValue(surchargeQuota) != null ? r.getValue(surchargeQuota).doubleValue() : 0)
				.setRetentionPercentage(r.getValue(retentionPercentage))
				;
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
				.setWorkplace( new Workplace().setId(record.getValue(INVOICE_DETAIL.WORKPLACE)))
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
