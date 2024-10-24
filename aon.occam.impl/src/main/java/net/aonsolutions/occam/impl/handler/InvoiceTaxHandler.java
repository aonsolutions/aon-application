package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectJoinStep;

import net.aonsolutions.occam.api.model.InvoiceDetail;
import net.aonsolutions.occam.api.model.InvoiceTax;
import net.aonsolutions.occam.api.model.type.TaxType;
import net.aonsolutions.occam.api.model.type.VatDeductionType;
import net.aonsolutions.occam.api.model.type.WithholdingType;
import net.aonsolutions.occam.impl.AONContext;

class InvoiceTaxHandler {
	
	private InvoiceTaxHandler() {
	}
	
	static SelectJoinStep<Record> select(AONContext ctx){	
		return ctx.getDslContext()
				.select()
				.from(INVOICE_TAX);
	}
	
	static Stream<InvoiceTax> stream(AONContext ctx, Integer invoiceDetailId){	
		return select(ctx)
			.where(INVOICE_TAX.INVOICE_DETAIL.eq(invoiceDetailId))
			.fetch()
			.stream()
			.map(new InvoiceTaxFiller());
	}
	
//	static InvoiceDetail save(AONContext ctx, Invoice invoice, InvoiceDetail detail) {
//		AonCollectionUtils.stream(detail.getInvoiceTaxes())
//			.forEach(invoiceTax -> save(ctx, invoice, detail, invoiceTax ));
//		return detail;
//	}
	
//	private static InvoiceTax save(AONContext ctx, Invoice invoice, InvoiceDetail detail, InvoiceTax invoiceTax) {
//		if (detail.isTaxEnabled(invoice) ) {
//			InvoiceTaxAutoComplete.complete(ctx, invoice, detail, invoiceTax);
//			InvoiceTaxValidation.validate(ctx, invoice, detail, invoiceTax);
//			invoiceTax = invoiceTax.getId() != null 
//				? update(ctx, invoiceTax)
//				: insert(ctx, invoiceTax);
//		} else {
//			if (invoiceTax.getId() != null) {
//				delete(ctx, invoiceTax.getId());
//				ctx.log().debug("\t\tDELETE INVOICE TAX NO TAX ALLOWED");
//			} else {
//				ctx.log().debug("\t\tSKIPPING INVOICE TAX CREATION ({0})",(detail.isPrepayment()? "PREPAYMENT": "UNDEDUCTIBLE INVOICE"));
//			}
//		}
//
//		return invoiceTax;
//	}
	
//	private static InvoiceTax update(AONContext ctx, InvoiceTax invoiceTax) {
//		ctx.getDslContext().update(INVOICE_TAX)
//			.set(INVOICE_TAX.DOMAIN, invoiceTax.getDomain())
//			.set(INVOICE_TAX.INVOICE_DETAIL, invoiceTax.getInvoiceDetail())
//			.set(INVOICE_TAX.TAX_TYPE, invoiceTax.getTaxType().value())
//			.set(INVOICE_TAX.BASE,invoiceTax.getBase())
//			.set(INVOICE_TAX.PERCENTAGE,invoiceTax.getPercentage())
//			.set(INVOICE_TAX.QUOTA,invoiceTax.getQuota())
//			.set(INVOICE_TAX.SURCHARGE,invoiceTax.getSurcharge())
//			.set(INVOICE_TAX.SURCHARGE_QUOTA,invoiceTax.getSurchargeQuota())
//			.set(INVOICE_TAX.VAT_DEDUCTION_TYPE, invoiceTax.getVatDeductionType().value())
//			.set(INVOICE_TAX.WITHHOLDING_TYPE,invoiceTax.getWithholdingType().value())
//			.set(INVOICE_TAX.DEDUCTIBLE_PERCENT,invoiceTax.getDeductiblePercent())
//			.set(INVOICE_TAX.DEDUCTIBLE_QUOTA ,invoiceTax.getDeductibleQuota())
//			.where(INVOICE_TAX.ID.eq(invoiceTax.getId()))
//			.execute();
//		ctx.log().debug("\t\tUPDATE INVOICE TAX");
//		return invoiceTax;
//	}
//	
//	private static InvoiceTax insert(AONContext ctx, InvoiceTax invoiceTax) {
//		Integer id = ctx.getDslContext().insertInto(INVOICE_TAX)
//			.set(INVOICE_TAX.DOMAIN, invoiceTax.getDomain())
//			.set(INVOICE_TAX.INVOICE_DETAIL, invoiceTax.getInvoiceDetail())
//			.set(INVOICE_TAX.TAX_TYPE, invoiceTax.getTaxType().value())
//			.set(INVOICE_TAX.BASE,invoiceTax.getBase())
//			.set(INVOICE_TAX.PERCENTAGE,invoiceTax.getPercentage())
//			.set(INVOICE_TAX.QUOTA,invoiceTax.getQuota())
//			.set(INVOICE_TAX.SURCHARGE,invoiceTax.getSurcharge())
//			.set(INVOICE_TAX.SURCHARGE_QUOTA,invoiceTax.getSurchargeQuota())
//			.set(INVOICE_TAX.VAT_DEDUCTION_TYPE, invoiceTax.getVatDeductionType().value())
//			.set(INVOICE_TAX.WITHHOLDING_TYPE,invoiceTax.getWithholdingType().value())
//			.set(INVOICE_TAX.DEDUCTIBLE_PERCENT,invoiceTax.getDeductiblePercent())
//			.set(INVOICE_TAX.DEDUCTIBLE_QUOTA ,invoiceTax.getDeductibleQuota())
//			.returning(INVOICE_TAX.ID)
//			.fetchOne()
//			.getId();
//		ctx.log().debug("\t\tINSERT INVOICE TAX");
//		return invoiceTax.setId(id);
//	}	

	static void delete(AONContext ctx, Integer invoiceTaxId){
		ctx.getDslContext().delete(INVOICE_TAX)
			.where(INVOICE_TAX.ID.eq(invoiceTaxId))
			.execute();
	}

	static void delete(AONContext ctx, InvoiceDetail detail){
		ctx.getDslContext().delete(INVOICE_TAX)
			.where(INVOICE_TAX.INVOICE_DETAIL.eq(detail.getId()))
			.execute();
	}

	private static class InvoiceTaxFiller extends Filler<InvoiceTax> {

		@Override
		public InvoiceTax apply(Record r) {
			return build(r);
		}
		
		public static InvoiceTax build(Record r) {
			return new InvoiceTax()
				.setId(getValue(r, INVOICE_TAX.ID))
				.setDomain(getValue(r, INVOICE_TAX.DOMAIN))
				.setInvoiceDetail(getValue(r, INVOICE_TAX.INVOICE_DETAIL))
				.setTaxType(TaxType.value(getValue(r, INVOICE_TAX.TAX_TYPE)).orElse(null))
				.setPercentage(getDouble(r, INVOICE_TAX.PERCENTAGE))
				.setBase(getDouble(r, INVOICE_TAX.BASE))
				.setSurcharge(getDouble(r, INVOICE_TAX.SURCHARGE))
				.setQuota(getDouble(r, INVOICE_TAX.QUOTA))
				.setSurchargeQuota(getDouble(r, INVOICE_TAX.SURCHARGE_QUOTA))
				.setVatDeductionType(VatDeductionType.value(getValue(r, INVOICE_TAX.VAT_DEDUCTION_TYPE)).orElse(null))
				.setWithholdingType(WithholdingType.value(getValue(r, INVOICE_TAX.WITHHOLDING_TYPE)).orElse(null))
				.setDeductiblePercent(getDouble(r, INVOICE_TAX.DEDUCTIBLE_PERCENT))
				.setDeductibleQuota(getDouble(r, INVOICE_TAX.DEDUCTIBLE_QUOTA))
				.markAsClean()
			;
		}
	}
	// --------------------------------------------------------------------------------	
	// --------------------------------------------------------------------------------	
	// --------------------------------------------------------------------------------	
	// --------------------------------------------------------------------------------	
	// --------------------------------------------------------------------------------	
	// --------------------------------------------------------------------------------	
	// --------------------------------------------------------------------------------	
	
	
	
//	private static final InvoiceTaxPropertiesDAO INVOICE_TAX_PROPERTIES = new InvoiceTaxPropertiesDAO();
//	public static class InvoiceTaxPropertiesDAO implements InvoiceTaxProperties {
//		
//		public Condition[] getConditions(InvoiceTaxFilter filter) {
//			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
//			if (filterDAO == null)
//				return new Condition[0];
//
//			return new Condition[] { filterDAO.getCondition() };
//		}
//		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.ID);}
//		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE_TAX.DOMAIN);}
//		@Override public Property<Integer> getInvoiceDetailProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.INVOICE_DETAIL);}
//		@Override public Property<Byte> getTaxTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.TAX_TYPE);}
//		@Override public Property<Double> getBaseProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.BASE);}
//		@Override public Property<Double> getPercentageProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.PERCENTAGE);}
//		@Override public Property<Double> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.SURCHARGE);}
//		@Override public Property<Double> getQuotaProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.QUOTA);}
//		@Override public Property<Double> getSurchargeQuotaProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.SURCHARGE_QUOTA);}
//		@Override public Property<Byte> getVatDedcutionTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.VAT_DEDUCTION_TYPE);}
//		@Override public Property<Byte> getWithholdingTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.WITHHOLDING_TYPE);}
//		@Override public Property<Double> getDeductiblePercentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.DEDUCTIBLE_PERCENT);}
//		@Override public Property<Double> getDeductibleQuotaProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_TAX.DEDUCTIBLE_QUOTA);}
//	}
//	
//	
//	public static Stream<InvoiceTax> getStream(AONContext ctx, InvoiceTaxFilter filter){	
//		return select(ctx, filter).fetch().stream().map(new InvoiceTaxFiller());
//	}
//	
//	public static Stream<InvoiceTax> getStream(AONContext ctx, InvoiceTaxFilter filter, Integer page, Integer perPage){	
//		return select(ctx, filter)
//			.limit(perPage)
//			.offset(perPage * (page -1))
//			.fetch().stream().map(new InvoiceTaxFiller());
//	}
//	
//	public static LinkedList<InvoiceTax> getList(AONContext ctx, InvoiceTaxFilter filter){	
//		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
//	}
//	
//	public static LinkedList<InvoiceTax> getList(AONContext ctx, InvoiceTaxFilter filter, Integer page, Integer perPage){	
//		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
//	}
//	
//	public static InvoiceTax get(AONContext ctx, InvoiceTaxFilter filter) {
//		return select(ctx, filter).limit(1)
//			.fetch().stream().map(new InvoiceTaxFiller())
//			.findFirst().orElse(new InvoiceTax());
//	}
//	
	
}
