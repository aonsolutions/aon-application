package net.aonsolutions.occam.impl.handler;

import java.util.Date;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceAddress;
import net.aonsolutions.occam.api.model.Registry;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.InvoiceType.InvoiceTypeVisitor;
import net.aonsolutions.occam.api.model.type.RectificationType;
import net.aonsolutions.occam.impl.AONContext;


class InvoiceAutoComplete {
	
	private InvoiceAutoComplete() {
	}
	
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
				int number = InvoiceHandler.getNextNumber(ctx,inv.getDomain(), types, inv.getSeries());
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
			if (AonMathUtils.isNullOrZero(inv.getNumber())) {
				Byte[] types = new Byte[]{InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()};
				int number = InvoiceHandler.getNextNumber(ctx, inv.getDomain(), types, inv.getSeries());
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
				int number = InvoiceHandler.getNextNumber(ctx, inv.getDomain(), types, inv.getSeries());
				inv.setNumber(number);
			}
		} 
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
		if (inv.getActivity().isEmpty()) {
			inv.setActivity( ctx.getDefaultActivity( inv.getDomain() ).orElse(null) );
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
			inv.getType().visit( 
				new InvoiceTypeVisitor<Optional<? extends Registry>>() {
					@Override public Optional<? extends Registry> visitPurchase() 	{return SupplierHandler.get(ctx, inv.getDomain(), inv.getRegistry());}
					@Override public Optional<? extends Registry> visitSales() 		{return CustomerHandler.get(ctx, inv.getDomain(), inv.getRegistry());}
					@Override public Optional<? extends Registry> visitExpenses() 	{return CreditorHandler.get(ctx, inv.getDomain(), inv.getRegistry());}
					@Override public Optional<? extends Registry> visitUndeductible(){return visitExpenses(); }
			})
			.ifPresent( registry -> {
				if (AonStringUtils.isBlank(inv.getRegistryName())) {
					inv.setRegistryName(registry.getName());
				}
				if (AonStringUtils.isBlank(inv.getRegistryDocument())) {
					inv.setRegistryDocumentType(registry.getDocumentType());
					inv.setRegistryDocumentCountry(registry.getDocumentCountry());
					inv.setRegistryDocument(registry.getDocument());
				}
			});
		}
	};
	
	private static final BiConsumer<AONContext,Invoice> COMPLETE_REGISTRY_ADDRESS = (ctx,inv) -> {
		if(inv.getInvoiceAddress().isEmpty()) {
			RegistryAddressHandler.streamByRegistry(ctx, inv.getRegistry())
				.filter( ra -> ra.isMain() )
				.findFirst()
				.map(InvoiceAddress::from)
				.ifPresent(inv::setInvoiceAddress);
		}
	};
	
	private static final BiConsumer<AONContext,Invoice> ENSURE_DETAILS_LINE = (ctx,inv) -> {
		MutableInt line = new MutableInt(1);  
		inv.detailStream()
			.filter(d -> !d.isDeleted())
			.forEach(d -> {
				d.setLine(line.shortValue());
				line.add(1);
			});
	};

	private static final BiConsumer<AONContext,Invoice> COMPLETE_ATTACH = (ctx,inv) -> {
		inv.getAttach().ifPresent( a -> a.setAttachModule(inv.getId()));
	};

		
	static void completeInvoice(AONContext ctx,Invoice inv) throws AonCoreException {
		
		COMPLETE_SALES_SERIES
		.andThen(COMPLETE_PURCHASE_EXPENSES_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE)
		.andThen(COMPLETE_TAX_DATE)
		.andThen(COMPLETE_RECTIFICATION_TYPE)
		.andThen(ENSURE_REGISTRY_DATA)
		.andThen(COMPLETE_REGISTRY_ADDRESS)
		.andThen(COMPLETE_ACTIVITY)
		.andThen(COMPLETE_FIRST_FINANCE)
		.andThen(COMPLETE_ATTACH)
		.andThen(ENSURE_DETAILS_LINE)
		.accept(ctx,inv);
	}
	
}
