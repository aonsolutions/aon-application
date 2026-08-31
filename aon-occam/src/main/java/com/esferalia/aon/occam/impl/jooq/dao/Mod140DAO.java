package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod140;
import com.esferalia.aon.occam.api.model.fiscal.Mod140.Mod140VAT;
import com.esferalia.aon.occam.api.model.fiscal.Mod140.Mod140Withholding;
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
import com.esferalia.aon.occam.server.fiscal.format.Mod140Writer;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod140DAO {
	
	public static void getInvoices(AONContext ctx, Mod140Context m140ctx ,Mod140Params params, Writer writer  ) {
		ctx.checkRead();
		
		Result<Record> result = ctx.getDslContext()
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
				,INVOICE.INVEST_ASSET
				,INVOICE.SERVICE
				,INVOICE.TAXABLE_BASE
				,INVOICE.VAT_QUOTA
				,INVOICE.RETENTION_QUOTA
				,INVOICE.TOTAL
				
				,INVOICE_DETAIL.ID
				,INVOICE_DETAIL.TAXABLE_BASE
				,INVOICE_DETAIL.INVEST_ASSET
				
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
			.orderBy(INVOICE.TYPE,INVOICE.SERIES,INVOICE.NUMBER)
			.fetch();
		
		final MutableInt lastInvoice = new MutableInt(Integer.MIN_VALUE);
		final Mod140 inv = new Mod140();
		
		for (Record record : result) {
			Integer id = record.getValue(INVOICE.ID);
			if (!lastInvoice.getValue().equals(id)) {
				lastInvoice.setValue(id);
				if (inv.getId() != null) {
					fillInvoice(writer, m140ctx, inv);
				}
				populateInvoice(record, inv, m140ctx);
				inv.setId(id);
			}

			Integer detailId = record.getValue(INVOICE_DETAIL.ID);
			Result<Record1<String>> accountsResult = ctx.getDslContext()
				.select(ACCOUNT.CODE)
				.from(INVOICE_DETAIL_ACCOUNT)
				.join(ACCOUNT).on(ACCOUNT.ID.equal(INVOICE_DETAIL_ACCOUNT.ACCOUNT))
				.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.equal(detailId))
				.orderBy(INVOICE_DETAIL_ACCOUNT.ID.desc())
				.fetch();
			String account = null;
			for (Record accountsRecord : accountsResult) {
				account = accountsRecord.getValue(ACCOUNT.CODE);
				break;
			}
			account = AonStringUtils.defaultString(account, AonStringUtils.EMPTY);
			account = AonStringUtils.substring(account, 0, 3);
			account = AonStringUtils.rightPad(account, 3);
			
			TaxType taxType = AonEnumUtils.enumValue(TaxType.class,
					record.getValue(INVOICE_TAX.TAX_TYPE));

			double base = record.getValue(INVOICE_TAX.BASE);
			double percentage = record.getValue(INVOICE_TAX.PERCENTAGE);
			double quota = record.getValue(INVOICE_TAX.QUOTA);

			if (taxType == TaxType.RETENTION) {
				if (inv.getWithholdingData() == null) {
					inv.setWithholdingData(new Mod140Withholding());
				}
				inv.getWithholdingData().setBase(
						AonMathUtils.round(inv.getWithholdingData().getBase()
								+ base));
				inv.getWithholdingData().setQuota(
						AonMathUtils.round(inv.getWithholdingData().getQuota()
								+ quota));

				// TODO Si hay mas de un tipo de retención el dato se machaca.
				inv.getWithholdingData().setPercentage(percentage);
				inv.getWithholdingData().setWithholdingType(
						AonEnumUtils.enumValue(WithholdingType.class,
								record.getValue(INVOICE_TAX.WITHHOLDING_TYPE)));
				// ------------------------------------------------------------
			}
			if (taxType == TaxType.VAT) {
				double surcharge = record.getValue(INVOICE_TAX.SURCHARGE);
				double surchargeQuota = record
						.getValue(INVOICE_TAX.SURCHARGE_QUOTA);
				double deductibleQuota = record
						.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA);
				Mod140VAT vat = inv.ensureInvoiceVAT(account,percentage, surcharge);
				vat.setBase(AonMathUtils.round(vat.getBase() + base));
				vat.setQuota(AonMathUtils.round(vat.getQuota()
						+ ((quota == 0.0) ? AonMathUtils.round(base
								* percentage / 100) : quota)));
				vat.setSurchargeQuota(AonMathUtils.round(vat
						.getSurchargeQuota()
						+ ((surcharge != 0.0 && surchargeQuota == 0.0) ? AonMathUtils
								.round(base * surcharge / 100) : surchargeQuota)));
				vat.setDeductibleQuota(AonMathUtils.round(vat
						.getSurchargeQuota()
						+ ((deductibleQuota == 0.0) ? AonMathUtils.round(base
								* percentage / 100) : deductibleQuota)));
				// TODO Si hay mas de un tipo de retención el dato se machaca.
				vat.setVatDeductionType(AonEnumUtils.enumValue(
						VatDeductionType.class,
						record.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)));
				// -----------------------------------------------------------
			}
		}
		if (inv.getId() != null) {
			fillInvoice(writer, m140ctx , inv);
		}
	}

	private static void populateInvoice(Record record, Mod140 inv,
			Mod140Context m140ctx) {
		inv.initialize();
		inv.setDomain(record.getValue(INVOICE.DOMAIN));
		inv.setSeries(record.getValue(INVOICE.SERIES));
		inv.setNumber(record.getValue(INVOICE.NUMBER));
		inv.setEpigraph(m140ctx.getEpigraph());
		inv.setReferenceCode(record.getValue(INVOICE.REFERENCE_CODE));
		inv.setIssueDate(record.getValue(INVOICE.ISSUE_DATE));
		inv.setTaxDate(record.getValue(INVOICE.TAX_DATE));
		inv.setRectificationType(AonEnumUtils.enumValue(
				RectificationType.class,
				record.getValue(INVOICE.RECTIFICATION_TYPE)));
		inv.setRectificationInvoice(record
				.getValue(INVOICE.RECTIFICATION_INVOICE));
		inv.setRegistryDocument(record.getValue(INVOICE.RDOCUMENT));
		inv.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,
				record.getValue(INVOICE.RDOCUMENT_TYPE)));
		inv.setRegistryDocumentCountry(Country.safeValueOf(record
				.getValue(INVOICE.RDOCUMENT_COUNTRY)));
		inv.setRegistryName(record.getValue(INVOICE.RNAME));
		inv.setType(AonEnumUtils.enumValue(InvoiceType.class,
				record.getValue(INVOICE.TYPE)));
		inv.setTransaction(AonEnumUtils.enumValue(InvoiceTransactionType.class,
				record.getValue(INVOICE.TRANSACTION)));
		inv.setRecorded(com.esferalia.aon.watson.server.AonEnumUtils
				.getBoolean(record.getValue(INVOICE.STATUS)));
		inv.setSurcharge(com.esferalia.aon.watson.server.AonEnumUtils
				.getBoolean(record.getValue(INVOICE.SURCHARGE)));
		inv.setWithholding(com.esferalia.aon.watson.server.AonEnumUtils
				.getBoolean(record.getValue(INVOICE.WITHHOLDING)));
		inv.setWithholdingFarmer(com.esferalia.aon.watson.server.AonEnumUtils
				.getBoolean(record.getValue(INVOICE.WITHHOLDING_FARMER)));
		inv.setVatAccrualPayment(com.esferalia.aon.watson.server.AonEnumUtils
				.getBoolean(record.getValue(INVOICE.VAT_ACCRUAL_PAYMENT)));
		inv.setInvestment(com.esferalia.aon.watson.server.AonEnumUtils
				.getBoolean(record.getValue(INVOICE.INVESTMENT)));
		inv.setService(com.esferalia.aon.watson.server.AonEnumUtils
				.getBoolean(record.getValue(INVOICE.SERVICE)));
		inv.setTaxableBase(record.getValue(INVOICE.TAXABLE_BASE));
		inv.setVatQuota(record.getValue(INVOICE.VAT_QUOTA));
		inv.setRetentionQuota(record.getValue(INVOICE.RETENTION_QUOTA));
		inv.setTotal(record.getValue(INVOICE.TOTAL));
		if(inv.isInvestment()) {
			inv.setInvestAsset(record.getValue(INVOICE.INVEST_ASSET));
		}
	}


	private static void fillInvoice(Writer writer, Mod140Context m140ctx, Mod140 inv) {
		try {
			Mod140Writer.fill(writer, m140ctx , inv);
		} catch (IOException e) {
			throw new AonCoreException(e.getMessage(), e); 
		}
	}



	private static Collection<Condition> getConditions(Mod140Params params) {
		LinkedList<Condition> list = new LinkedList<Condition>();
		list.add(INVOICE.DOMAIN.equal(params.getDomain()));
		list.add(INVOICE.NUMBER.ge(0));   // No facturas proforma (factura proforma es la que su numero de factura es menor que cero)
		list.add(InvoiceDAO.NOT_ANNULLED); // No facturas anuladas
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
		if(params.isOnlyEmitidas()) {
			list.add(INVOICE.TYPE.eq(InvoiceType.SALES.value()));
		}
		return list;
	}
	
}
