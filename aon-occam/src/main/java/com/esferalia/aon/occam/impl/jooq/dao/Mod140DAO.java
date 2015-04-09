package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.jooq.Condition;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Context;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Params;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.server.fiscal.format.mod140.Mod140Format;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod140DAO {
	
	public static void getInvoices(AONContext ctx, Mod140Context m140ctx ,Mod140Params params, Writer writer  ) {
		ctx.checkRead();
		final MutableInt lastInvoice = new MutableInt(Integer.MIN_VALUE);
		final Invoice invoice = new Invoice();
		ctx.getDslContext()
			.select(
				 INVOICE.ID
				,INVOICE.DOMAIN
				,INVOICE.SERIES
				,INVOICE.NUMBER
				,INVOICE.REFERENCE_CODE
				,INVOICE.ISSUE_DATE
				,INVOICE.TAX_DATE
				,INVOICE.RECTIFICATION_TYPE
				,INVOICE.RECTIFICATION_INVOICE
				,INVOICE.RDOCUMENT
				,INVOICE.RDOCUMENT_TYPE
				,INVOICE.RDOCUMENT_COUNTRY
				,INVOICE.RNAME
				,INVOICE.TYPE
				,INVOICE.TRANSACTION
				,INVOICE.STATUS
				,INVOICE.SURCHARGE
				,INVOICE.WITHHOLDING
				,INVOICE.WITHHOLDING_FARMER
				,INVOICE.VAT_ACCRUAL_PAYMENT
				,INVOICE.INVESTMENT
				,INVOICE.SERVICE
				,INVOICE.TAXABLE_BASE
				,INVOICE.VAT_QUOTA
				,INVOICE.RETENTION_QUOTA
				,INVOICE.TOTAL
				
				,INVOICE_DETAIL.TAXABLE_BASE
				
				,INVOICE_TAX.TAX_TYPE
				,INVOICE_TAX.BASE
				,INVOICE_TAX.PERCENTAGE
				,INVOICE_TAX.QUOTA
				,INVOICE_TAX.SURCHARGE
				,INVOICE_TAX.SURCHARGE_QUOTA
				,INVOICE_TAX.DEDUCTIBLE_QUOTA
				,INVOICE_TAX.WITHHOLDING_TYPE
				,INVOICE_TAX.VAT_DEDUCTION_TYPE
			)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.where(getConditions(params))
			// -------------------------------------- //
			// --------- FILTER OPTIONS ------------- //
			// -------------------------------------- //
			.orderBy(INVOICE.TYPE,INVOICE.SERIES,INVOICE.NUMBER)
			.fetch()
			.stream()
			.forEach( (record -> {
				Integer id = record.getValue( INVOICE.ID );
				if (!lastInvoice.getValue().equals(id)) {
					lastInvoice.setValue(id);
					if (invoice.getId() != null) {
						try {
							Mod140Format.fill(writer, m140ctx , invoice);
						} catch (IOException e) {
							throw new AonCoreException(e.getMessage(), e); 
						}
					}
					invoice.initialize();
					invoice.setId(id);
					invoice.setDomain(record.getValue( INVOICE.DOMAIN ));
					invoice.setSeries(record.getValue( INVOICE.SERIES ));
					invoice.setNumber(record.getValue( INVOICE.NUMBER ));
					invoice.setEpigraph(m140ctx.getEpigraph());
					invoice.setReferenceCode(record.getValue( INVOICE.REFERENCE_CODE ));
					invoice.setIssueDate(record.getValue( INVOICE.ISSUE_DATE ));
					invoice.setTaxDate(record.getValue( INVOICE.TAX_DATE ));
					invoice.setRectificationType(
							AonEnumUtils.enumValue(RectificationType.class, record.getValue( INVOICE.RECTIFICATION_TYPE))
							);
					invoice.setRectificationInvoice(record.getValue( INVOICE.RECTIFICATION_INVOICE ));
					invoice.setRegistryDocument(record.getValue( INVOICE.RDOCUMENT ));
					invoice.setRegistryDocumentType(
							AonEnumUtils.enumValue(DocumentType.class, record.getValue( INVOICE.RDOCUMENT_TYPE))
							);
					invoice.setRegistryDocumentCountry(Country.safeValueOf(record.getValue( INVOICE.RDOCUMENT_COUNTRY )));
					invoice.setRegistryName(record.getValue( INVOICE.RNAME ));
					invoice.setType(
							AonEnumUtils.enumValue(InvoiceType.class, record.getValue( INVOICE.TYPE ))
							);
					invoice.setTransaction(
							AonEnumUtils.enumValue(InvoiceTransactionType.class, record.getValue( INVOICE.TRANSACTION ))
							);
					invoice.setRecorded(
							com.esferalia.aon.watson.server.AonEnumUtils.getBoolean(record.getValue( INVOICE.STATUS))
							);
					invoice.setSurcharge(
							com.esferalia.aon.watson.server.AonEnumUtils.getBoolean(record.getValue( INVOICE.SURCHARGE))
							);
					invoice.setWithholding(
							com.esferalia.aon.watson.server.AonEnumUtils.getBoolean(record.getValue( INVOICE.WITHHOLDING ))
							);
					invoice.setWithholdingFarmer(
							com.esferalia.aon.watson.server.AonEnumUtils.getBoolean(record.getValue( INVOICE.WITHHOLDING_FARMER  ))
							);
					invoice.setVatAccrualPayment(
							com.esferalia.aon.watson.server.AonEnumUtils.getBoolean(record.getValue( INVOICE.VAT_ACCRUAL_PAYMENT ))
							);
					invoice.setInvestment(
							com.esferalia.aon.watson.server.AonEnumUtils.getBoolean(record.getValue( INVOICE.INVESTMENT ))
							);
					invoice.setService(
							com.esferalia.aon.watson.server.AonEnumUtils.getBoolean(record.getValue( INVOICE.SERVICE ))
							);
					invoice.setTaxableBase(record.getValue( INVOICE.TAXABLE_BASE ));
					invoice.setVatQuota(record.getValue( INVOICE.VAT_QUOTA ));
					invoice.setRetentionQuota(record.getValue( INVOICE.RETENTION_QUOTA ));
					invoice.setTotal(record.getValue( INVOICE.TOTAL ));
				}
				
				TaxType taxType = AonEnumUtils.enumValue(TaxType.class, record.getValue( INVOICE_TAX.TAX_TYPE));
				
				double base = record.getValue( INVOICE_TAX.BASE );
				double percentage = record.getValue( INVOICE_TAX.PERCENTAGE );
				double quota = record.getValue( INVOICE_TAX.QUOTA );
				
				if (taxType == TaxType.RETENTION) {
					if (invoice.getWithholdingData() == null ) {
						invoice.setWithholdingData( new InvoiceWithholding());
					}	
						invoice.getWithholdingData().setBase(AonMathUtils.round(
								invoice.getWithholdingData().getBase()
								+ base));
						invoice.getWithholdingData().setQuota(AonMathUtils.round(
								invoice.getWithholdingData().getQuota()
								+ quota));
						
						// TODO Si hay mas de un tipo de retención el dato se machaca.
						invoice.getWithholdingData().setPercentage(percentage);
						invoice.getWithholdingData().setWithholdingType(
							AonEnumUtils.enumValue(WithholdingType.class, record.getValue( INVOICE_TAX.WITHHOLDING_TYPE))				
								);
						// ------------------------------------------------------------
				}
				if (taxType == TaxType.VAT) {
					double surcharge = record.getValue( INVOICE_TAX.SURCHARGE );
					double surchargeQuota = record.getValue( INVOICE_TAX.SURCHARGE_QUOTA );
					double deductibleQuota = record.getValue( INVOICE_TAX.DEDUCTIBLE_QUOTA );
					InvoiceVAT vat = invoice.ensureInvoiceVAT(percentage,surcharge);
					vat.setBase(AonMathUtils.round(vat.getBase() + base));
					vat.setQuota(AonMathUtils.round(
							vat.getQuota() 
							+ ((quota == 0.0)
									?AonMathUtils.round(base * percentage / 100 )
									:quota)));
					vat.setSurchargeQuota(AonMathUtils.round(
							vat.getSurchargeQuota() 
							+ ((surcharge != 0.0 && surchargeQuota == 0.0)
									?AonMathUtils.round(base * surcharge / 100 )
									:surchargeQuota)));
					vat.setDeductibleQuota(AonMathUtils.round(
							vat.getSurchargeQuota() 
							+ ((deductibleQuota == 0.0)
									?AonMathUtils.round(base * percentage / 100 )
									:deductibleQuota)));
					// TODO Si hay mas de un tipo de retención el dato se machaca.
					vat.setVatDeductionType(
							AonEnumUtils.enumValue(VatDeductionType.class, record.getValue( INVOICE_TAX.VAT_DEDUCTION_TYPE))				
								);
					// -----------------------------------------------------------
				}
				
			}));
	}



	private static Collection<Condition> getConditions(Mod140Params params) {
		LinkedList<Condition> list = new LinkedList<Condition>();
		list.add(INVOICE.DOMAIN.equal(params.getDomain()));
		if ( params.getFromDate() != null) {
			list.add((params.isFilterByTaxDateEnabled()
					?INVOICE.TAX_DATE
					:INVOICE.ISSUE_DATE
					).greaterOrEqual(AonDateUtils.toSql( params.getFromDate())));	
		}
		if ( params.getToDate() != null) {
			list.add((params.isFilterByTaxDateEnabled()
					?INVOICE.TAX_DATE
					:INVOICE.ISSUE_DATE
					).lessOrEqual(AonDateUtils.toSql( params.getToDate())));	
		}
		return list;
	}
}
