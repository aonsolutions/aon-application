package com.esferalia.aon.occam.impl.jooq.dao.api;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

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
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
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
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.watson.util.AonEnumUtils;;

public class InvoiceApiDAO {
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	

	public static Stream<Invoice> getInvoices(AONContext ctx, InvoiceFilter filter) {
		return INVOICE_PROPERTIES.build(ctx.getDslContext().select().from(INVOICE)
				.join(SCOPE).on(SCOPE.ID.eq(INVOICE.SCOPE))
				.leftOuterJoin(RADDRESS).on(RADDRESS.ID.equal(INVOICE.RADDRESS))
				.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID)), filter)
				.fetch().stream().map(new InvoiceApiFiller(ctx));	
	}
	
	public static Invoice insertInvoice(AONContext ctx, Invoice invoice) {
		if(invoice.getId() != null) {
			return InvoiceDAO.update(ctx, invoice);
		} else {
			return InvoiceDAO.insert(ctx, invoice);
		}
	}
	
	
	public static void deleteInvoice(AONContext ctx, Integer id) {
		InvoiceDAO.delete(ctx, id);
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
	


	public static class InvoiceApiFiller  implements Function<Record,Invoice> {
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
				
				.setAddressProvinceCode(record.getValue(GEOZONE.CODE))
				.setAddressProvince(record.getValue(GEOZONE.NAME))
				.setAddressTown(record.getValue(RADDRESS.CITY))
				.setAddressZIP(record.getValue(RADDRESS.ZIP))
				
				.setScope(new Scope().setId(record.getValue(SCOPE.ID)).setDescription(record.getValue(SCOPE.DESCRIPTION)))
				.setActivity(record.getValue(INVOICE.ACTIVITY))	
				.setInvestAsset(record.getValue(INVOICE.INVEST_ASSET))
				.setProject(record.getValue(INVOICE.PROJECT))
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class,record.getValue(INVOICE.RECTIFICATION_TYPE)))	
				.setRectificationInvoice(record.getValue(INVOICE.RECTIFICATION_INVOICE))	
				.setTransaction(AonEnumUtils.enumValue(InvoiceTransactionType.class,record.getValue(INVOICE.TRANSACTION)))
				.setRecorded(record.getValue(INVOICE.STATUS) == 1 )	
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
				.setSeller(new Seller().setId(record.getValue(INVOICE_DETAIL.SELLER)))
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
