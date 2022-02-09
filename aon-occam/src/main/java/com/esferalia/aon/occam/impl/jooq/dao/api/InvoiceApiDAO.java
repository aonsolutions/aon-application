package com.esferalia.aon.occam.impl.jooq.dao.api;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class InvoiceApiDAO {
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	
	public static Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilter filter) {
		Integer page = INVOICE_PROPERTIES.getPage(filter);
		Integer perPage = INVOICE_PROPERTIES.getPerPage(filter);
		
		return ctx.getDslContext().select()
				.from(INVOICE)
				//.leftOuterJoin(INVOICE_COMMUNICATION).on(INVOICE_COMMUNICATION.INVOICE.eq(INVOICE.ID))
			.where(INVOICE_PROPERTIES.getConditions(filter))
			.groupBy(INVOICE.ID)
			.orderBy(INVOICE.ISSUE_DATE.desc(), INVOICE.ID.desc())
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new InvoiceApiFiller(ctx));
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
		public Invoice apply(Record record) {
			Invoice invoice = new Invoice()
				.setId(record.getValue(INVOICE.ID))
				.setDomain(record.getValue(INVOICE.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class,record.getValue(INVOICE.TYPE)))
				.setSeries(record.getValue(INVOICE.SERIES))
				.setNumber(record.getValue(INVOICE.NUMBER))
				.setReferenceCode(record.getValue(INVOICE.REFERENCE_CODE))
				.setIssueDate(record.getValue(INVOICE.ISSUE_DATE))
				.setTaxDate(record.getValue(INVOICE.TAX_DATE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class,record.getValue(INVOICE.SECURITY_LEVEL)))
			
				.setRegistry(record.getValue(INVOICE.REGISTRY))
				.setRegistryDocument(record.getValue(INVOICE.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,record.getValue(INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(record.getValue(INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(record.getValue(INVOICE.RNAME))
				
//				.setAddressProvinceCode(record.getValue(GEOZONE.CODE))
//				.setAddressProvince(record.getValue(GEOZONE.NAME))
//				.setAddressTown(record.getValue(RADDRESS.CITY))
//				.setAddressZIP(record.getValue(RADDRESS.ZIP))
				
//				.setScope(new Scope().setId(record.getValue(SCOPE.ID)).setDescription(record.getValue(SCOPE.DESCRIPTION)))
				.setActivity(record.getValue(INVOICE.ACTIVITY))	
				.setInvestAsset(record.getValue(INVOICE.INVEST_ASSET))
				.setProject(record.getValue(INVOICE.PROJECT))
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class,record.getValue(INVOICE.RECTIFICATION_TYPE)))	
				.setRectificationInvoice(record.getValue(INVOICE.RECTIFICATION_INVOICE))	
				.setTransaction(AonEnumUtils.enumValue(InvoiceTransactionType.class, record.getValue(INVOICE.TRANSACTION)))
				.setRecorded(record.getValue(INVOICE.STATUS) != null && record.getValue(INVOICE.STATUS) == 1 )	
				.setSurcharge(record.getValue(INVOICE.SURCHARGE) == 1 )	
				.setWithholding(record.getValue(INVOICE.WITHHOLDING) == 1 )	
				.setWithholdingFarmer(record.getValue(INVOICE.WITHHOLDING_FARMER) == 1 )	
				.setVatAccrualPayment(record.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1 )	
				.setInvestment(record.getValue(INVOICE.INVESTMENT) == 1 )	
				.setService(record.getValue(INVOICE.SERVICE) == 1 )	
				.setAdvance(record.getValue(INVOICE.ADVANCE) == 1 )	
				.setTaxableBase(record.getValue(INVOICE.TAXABLE_BASE))	
				.setVatQuota(record.getValue(INVOICE.VAT_QUOTA))	
				.setRetentionQuota(record.getValue(INVOICE.RETENTION_QUOTA))	
				.setTotal(record.getValue(INVOICE.TOTAL))	
				.setComments(record.getValue(INVOICE.COMMENTS))
				.setStatus(record.getValue(INVOICE.STATUS))
				.setCreationDate(record.getValue(INVOICE.CREATION_DATE))
				.setCreationUser(record.getValue(INVOICE.CREATION_USER))
				.setModificationDate(record.getValue(INVOICE.MODIFICATION_DATE))
				.setModificationUser(record.getValue(INVOICE.MODIFICATION_USER));
//				.setCommunicationType(InvoiceCommunicationType.safeValueOf(getValue(record, INVOICE_COMMUNICATION.TYPE)));
			
//				Integer sent = getValue(record, INVOICE_COMMUNICATION.SENT);
//				Integer annulled = getValue(record, INVOICE_COMMUNICATION.ANNULLED);
//				if(sent != null && annulled == null) {
//					invoice.setCommunicationStatus(InvoiceCommunicationStatus.SENT);
//				} else if(annulled != null) {
//					invoice.setCommunicationStatus(InvoiceCommunicationStatus.ANNULLED);
//				} else invoice.setCommunicationStatus(InvoiceCommunicationStatus.PENDING);
			try (AONContext ctx = AONContext.getAONContext(aonCtx.getDomainName(),aonCtx.getDomainId(), aonCtx.getUser())){

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
		
			try (AONContext ctx = AONContext.getAONContext(aonCtx.getDomainName(),aonCtx.getDomainId(), aonCtx.getUser())){

				invoiceDetail.setInvoiceTaxes(getInvoiceDetailTaxStream(ctx, invoiceDetail.getId())
					.collect(Collectors.toCollection(LinkedList::new)));
			}
			return invoiceDetail;
		}
	}
	
	
}
