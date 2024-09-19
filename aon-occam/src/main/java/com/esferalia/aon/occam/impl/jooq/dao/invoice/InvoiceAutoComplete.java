package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import java.util.Date;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


class InvoiceAutoComplete {
	
	private InvoiceAutoComplete() {
	}
	
	/**
	 * Se rellena el dominio.
	 */
	private static final BiConsumer<AONContext,Invoice> COMPLETE_DOMAIN = (ctx,inv) -> {
		if(inv.getDomain() == null) {
			inv.setDomain(ctx.getDomainId());
		}
	};

	/**
	 * Se rellena el número de referencia para las facturas de ventas.
	 */
	private static final BiConsumer<AONContext,Invoice> COMPLETE_SALES_SERIES = (ctx,inv) -> {
		if (inv.isSales()) {
			if (AonStringUtils.isBlank(inv.getSeries())) {
				inv.setSeries(null);
			}
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.SALES.value()};
				int number = InvoiceDAO.getNextNumber(ctx,types, inv.getSeries());
				inv.setNumber(number);
			}
			if(inv.getReferenceCode() == null || "".equals(inv.getReferenceCode())) {
				String referenceCode = AonStringUtils.leftPad(Integer.toString(inv.getNumber()), 6, "0");
				if (!AonStringUtils.isBlank(inv.getSeries())) {
					referenceCode = inv.getSeries() + "/" + referenceCode;
				}
				inv.setReferenceCode(referenceCode);
			}
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	private static final BiConsumer<AONContext,Invoice> COMPLETE_PURCHASE_EXPENSES_SERIES = (ctx,inv) -> {
		if (inv.isPurchase() || inv.isExpenses()) {
			inv.setSeries(Integer.toString(AonDateUtils.getYear(inv.getIssueDate())));
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()};
				int number = InvoiceDAO.getNextNumber(ctx,types, inv.getSeries());
				inv.setNumber(number);
			}
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	private static final BiConsumer<AONContext,Invoice> COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE = (ctx,inv) -> {
		if (inv.isUndeductible() && AonStringUtils.equalsIgnoreCase("<auto>",inv.getReferenceCode())) {
			inv.setReferenceCode( inv.getDocumentNumber());
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	private static final BiConsumer<AONContext,Invoice> COMPLETE_UNDEDUCTIBLE_SERIES = (ctx,inv) -> {
		if (inv.isUndeductible()) {
			inv.setSeries(Integer.toString(AonDateUtils.getYear(inv.getIssueDate()==null?new Date():inv.getIssueDate())));
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.UNDEDUCTIBLE.value()};
				int number = InvoiceDAO.getNextNumber(ctx,types, inv.getSeries());
				inv.setNumber(number);
			}
		} 
	};
	
	/**
	 * Aseguramos el SecurityLevel.
	 */
	private static final BiConsumer<AONContext,Invoice> COMPLETE_SECURITY_LEVEL = (ctx,inv) -> {
		if (inv.getSecurityLevel() == null) inv.setSecurityLevel(SecurityLevel.OFFICIAL); 
	};
	
	/**
	 * Aseguramos la fecha de IVA..
	 */
	private static final BiConsumer<AONContext,Invoice> COMPLETE_TAX_DATE = (ctx,inv) -> {
		if (inv.getTaxDate() == null) inv.setTaxDate(inv.getIssueDate());
	};
	
	/**
	 * Aseguramos la Actividad.
	 */
	private static final BiConsumer<AONContext,Invoice> COMPLETE_ACTIVITY = (ctx,inv) -> {
		if (inv.getActivity().isEmpty()
			&& ctx.getConfiguration() != null 
			&& ctx.getConfiguration().getActivities() != null 
			&& ctx.getConfiguration().getActivities().size() == 1) {
			
			EnterpriseActivity act = ctx.getConfiguration().getMainActivity(); 
			inv.setActivity( act == null ? new EnterpriseActivity() : act);
		}
	};


	/**
	 * Si solo hay un vencimiento, el importe será igual al total factura.
	 */
	private static final BiConsumer<AONContext,Invoice> COMPLETE_FIRST_FINANCE = (ctx,inv) -> {
		if (inv.hasFinances() && inv.getFinances().size() == 1 && inv.getFinances().get(0).isPending()) {
			inv.getFinances().get(0).setAmount( inv.getTotal());
		}
	};

	/**
	 * Aseguramos la fecha de IVA..
	 */
	private static final  BiConsumer<AONContext,Invoice> COMPLETE_RECTIFICATION_TYPE = (ctx,inv) -> {
		if (inv.getRectificationType() == null) inv.setRectificationType(RectificationType.NONE);
	};
	
	
	/**
	 * Aseguramos el nombre del titular de la factura.
	 */
	private static final BiConsumer<AONContext,Invoice> ENSURE_REGISTRY_DATA = (ctx,inv) -> {
		if (AonStringUtils.isBlank(inv.getRegistryName()) || AonStringUtils.isBlank(inv.getRegistryDocument())) {
			if (inv.getRegistry() != null) {
				Registry registry = RegistryDAO.get(ctx, inv.getRegistry());
				if (AonStringUtils.isBlank(inv.getRegistryName())) inv.setRegistryName(registry.getName());
				if (AonStringUtils.isBlank(inv.getRegistryDocument())) {
					inv.setRegistryDocumentType(registry.getDocumentType());
					inv.setRegistryDocumentCountry(registry.getDocumentCountry());
					inv.setRegistryDocument(registry.getDocument());
				}
			}
		}
	};
	
	private static final BiConsumer<AONContext,Invoice> COMPLETE_REGISTRY_ADDRESS = (ctx,inv) -> {
		if(inv.getAddress() == null || inv.getAddress().isEmpty()) {
			RegistryAddressFilter filter = inv.getRegistryAddress() != null 
					? f -> f.getDomainProperty().eq(inv.getDomain())
							.and(f.getRegistryProperty().eq(inv.getRegistry()))
							.and(f.getIdProperty().eq(inv.getRegistryAddress()))
					: f -> f.getDomainProperty().eq(inv.getDomain())
							.and(f.getRegistryProperty().eq(inv.getRegistry())
							.and(f.getTypeProperty().eq((byte) 0)));
			
			RegistryAddress raddress = RegistryAddressDAO.get(ctx, filter);
			inv.setAddress(raddress);
			inv.setRegistryAddress(raddress.getId());
		}
		
		if(inv.getAddress() != null && inv.getAddress().getDomain() == null && !inv.getAddress().isEmpty()) {
			inv.getAddress().setDomain(inv.getDomain());
		}
	};
	
	/**
	 * Se inicializa el tipo de retención y el tipo de deducción de IVA
	 */
	private static final BiConsumer<AONContext,Invoice> COMPLETE_VAT_DEDUCTION_TYPE = (ctx,inv) -> 
		AonCollectionUtils.stream(inv.getDetails())
			.flatMap(id -> AonCollectionUtils.stream(id.getInvoiceTaxes()))
			.filter(it -> it.getVatDeductionType() == null )
			.forEach( it -> it.setVatDeductionType(VatDeductionType.WITH_RIGHT));
	
	private static final BiConsumer<AONContext,Invoice> COMPLETE_IRPF_PROFESSIONAL = (ctx,inv) -> 
		AonCollectionUtils.stream(inv.getDetails())
			.flatMap(id -> AonCollectionUtils.stream(id.getInvoiceTaxes()))
			.filter(it -> it.getWithholdingType() == null )
			.forEach( it -> it.setWithholdingType(WithholdingType.PROFESSIONAL));
		
	private static final BiConsumer<AONContext,Invoice> ENSURE_DETAILS_LINE = (ctx,inv) -> {
		MutableInt line = new MutableInt(1);  
		AonCollectionUtils.stream(inv.getDetails())
			.filter(d -> !d.isDeleted())
			.forEach(d -> {
				d.setLine(line.shortValue());
				line.add(1);
			});
	};

	static void completeInvoice(AONContext ctx,Invoice inv) throws AonCoreException {
		
		COMPLETE_DOMAIN
		.andThen(COMPLETE_SALES_SERIES)
		.andThen(COMPLETE_PURCHASE_EXPENSES_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE)
		.andThen(COMPLETE_TAX_DATE)
		.andThen(COMPLETE_RECTIFICATION_TYPE)
		.andThen(ENSURE_REGISTRY_DATA)
		.andThen(COMPLETE_REGISTRY_ADDRESS)
		.andThen(COMPLETE_ACTIVITY)
		.andThen(COMPLETE_SECURITY_LEVEL)
		.andThen(COMPLETE_VAT_DEDUCTION_TYPE)
		.andThen(COMPLETE_IRPF_PROFESSIONAL)
		.andThen(COMPLETE_FIRST_FINANCE)
		.andThen(ENSURE_DETAILS_LINE)
		.accept(ctx,inv);
	}
	
}
