package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceTaxDAO {
	
	private static Stream<InvoiceTax> getTaxesStream(AONContext ctx, Integer invoiceDetailId){	
		return ctx.getDslContext()
			.select()
			.from(INVOICE_TAX)
			.where(INVOICE_TAX.INVOICE_DETAIL.eq(invoiceDetailId))
			.fetch()
			.stream()
			.map(new InvoiceTaxFiller());
	}	
	
	static List<InvoiceTax> save(AONContext ctx, List<InvoiceTax> invoiceTaxes, InvoiceDetail detail) {
		return invoiceTaxes
			.stream()
			.map(invoiceTax -> save(ctx, invoiceTax, detail))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static InvoiceTax save(AONContext ctx, InvoiceTax invoiceTax, InvoiceDetail detail) {
		invoiceTax = invoiceTax.getId() != null 
			? update(ctx, invoiceTax, detail)
			: insert(ctx, invoiceTax, detail);
		return invoiceTax;
	}
	
	private static InvoiceTax update(AONContext ctx, InvoiceTax invoiceTax, InvoiceDetail detail) {
		ctx.getDslContext().update(INVOICE_TAX)
			.set(INVOICE_TAX.DOMAIN, detail.getDomain())
			.set(INVOICE_TAX.INVOICE_DETAIL, detail.getId())
			.set(INVOICE_TAX.TAX_TYPE, invoiceTax.getTaxType().value())
			.set(INVOICE_TAX.BASE,invoiceTax.getBase())
			.set(INVOICE_TAX.PERCENTAGE,invoiceTax.getPercentage())
			.set(INVOICE_TAX.QUOTA,invoiceTax.getQuota())
			.set(INVOICE_TAX.SURCHARGE,invoiceTax.getSurcharge())
			.set(INVOICE_TAX.SURCHARGE_QUOTA,invoiceTax.getSurchargeQuota())
			.set(INVOICE_TAX.VAT_DEDUCTION_TYPE,invoiceTax.getVatDeductionType() == null
					? VatDeductionType.WITH_RIGHT.value() 
					: invoiceTax.getVatDeductionType().value())
			.set(INVOICE_TAX.WITHHOLDING_TYPE,invoiceTax.getWithholdingType() == null 
					? WithholdingType.PROFESSIONAL.value() 
					: invoiceTax.getWithholdingType().value())
			.set(INVOICE_TAX.DEDUCTIBLE_PERCENT,invoiceTax.getDeductiblePercent())
			.set(INVOICE_TAX.DEDUCTIBLE_QUOTA ,invoiceTax.getDeductibleQuota())
			.where(INVOICE_TAX.ID.eq(invoiceTax.getId()))
			.execute();
		return invoiceTax;
	}
	
	private static InvoiceTax insert(AONContext ctx, InvoiceTax invoiceTax, InvoiceDetail detail) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_TAX)
			.set(INVOICE_TAX.DOMAIN, detail.getDomain())
			.set(INVOICE_TAX.INVOICE_DETAIL, detail.getId())
			.set(INVOICE_TAX.TAX_TYPE, invoiceTax.getTaxType().value())
			.set(INVOICE_TAX.BASE,invoiceTax.getBase())
			.set(INVOICE_TAX.PERCENTAGE,invoiceTax.getPercentage())
			.set(INVOICE_TAX.QUOTA,invoiceTax.getQuota())
			.set(INVOICE_TAX.SURCHARGE,invoiceTax.getSurcharge())
			.set(INVOICE_TAX.SURCHARGE_QUOTA,invoiceTax.getSurchargeQuota())
			.set(INVOICE_TAX.VAT_DEDUCTION_TYPE,invoiceTax.getVatDeductionType() == null
					? VatDeductionType.WITH_RIGHT.value() 
					: invoiceTax.getVatDeductionType().value())
			.set(INVOICE_TAX.WITHHOLDING_TYPE,invoiceTax.getWithholdingType() == null 
					? WithholdingType.PROFESSIONAL.value() 
					: invoiceTax.getWithholdingType().value())
			.set(INVOICE_TAX.DEDUCTIBLE_PERCENT,invoiceTax.getDeductiblePercent())
			.set(INVOICE_TAX.DEDUCTIBLE_QUOTA ,invoiceTax.getDeductibleQuota())
			.returning(INVOICE_TAX.ID).fetchOne().getId();
		return invoiceTax.setId(id);
	}	

	private static class InvoiceTaxFiller extends Filler implements Function<Record, InvoiceTax> {

		@Override
		public InvoiceTax apply(Record r) {
			return build(r);
		}
		
		public static InvoiceTax build(Record r) {
			InvoiceTax tax = new InvoiceTax()
				.setId(getValue(r, INVOICE_TAX.ID))
				.setDomain(getValue(r, INVOICE_TAX.DOMAIN))
				.setTaxType(TaxType.safeValueOf(getValue(r, INVOICE_TAX.TAX_TYPE)))
				.setPercentage(getDouble(r, INVOICE_TAX.PERCENTAGE))
				.setBase(getDouble(r, INVOICE_TAX.BASE))
				.setSurcharge(getDouble(r, INVOICE_TAX.SURCHARGE))
				.setQuota(getDouble(r, INVOICE_TAX.QUOTA))
				.setSurchargeQuota(getDouble(r, INVOICE_TAX.SURCHARGE_QUOTA))
				.setVatDeductionType(VatDeductionType.safeValueOf(getValue(r, INVOICE_TAX.VAT_DEDUCTION_TYPE)))
				.setWithholdingType(WithholdingType.safeValueOf(getValue(r, INVOICE_TAX.WITHHOLDING_TYPE)))
				.setDeductiblePercent(getDouble(r, INVOICE_TAX.DEDUCTIBLE_PERCENT))
				.setDeductibleQuota(getDouble(r, INVOICE_TAX.DEDUCTIBLE_QUOTA));
			tax
				.setQuotaEdited( AonMathUtils.isNotZero(InvoiceCalculator.getQuotaGap(tax, tax.getQuota())))
				.setSurchargeQuotaEdited( AonMathUtils.isNotZero(InvoiceCalculator.getSurchargeQuotaGap(tax, tax.getSurchargeQuota())))
				.setDeductibleQuotaEdited( AonMathUtils.isNotZero(InvoiceCalculator.getDeductibleQuotaGap(tax, tax.getDeductibleQuota())));
			return tax;

		}
	}

	static LinkedList<InvoiceTax> getInvoiceTaxes(AONContext ctx, Integer invoiceDetailId){
		return getTaxesStream(ctx, invoiceDetailId)
			.collect(Collectors.toCollection(LinkedList::new));
	}
}
