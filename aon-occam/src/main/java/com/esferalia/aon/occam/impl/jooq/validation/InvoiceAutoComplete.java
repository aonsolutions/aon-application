package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.Date;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceAutoComplete {

	private static class AonConfigurationContext {
		
		private AONContext ctx;
		private AonConfiguration config;
		
		private AonConfigurationContext (AONContext ctx,AonConfiguration config) {
			this.ctx = ctx;
			this.config = config;
		}
		private AONContext getContext() {
			return ctx;
		}
		private AonConfiguration getConfiguration() {
			return config;
		}
	}
	
	
	/**
	 * Se rellena el número de referencia para las facturas de ventas.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_SALES_SERIES = (inv,ctx) -> {
		if (inv.isSales()) {
			if (AonStringUtils.isBlank(inv.getSeries())) {
				inv.setSeries(null);
			}
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.SALES.value()};
				int number = InvoiceDAO.getNextNumber(ctx.getContext(),types, inv.getSeries());
				inv.setNumber(number);
			}
			String referenceCode = AonStringUtils.leftPad(Integer.toString(inv.getNumber()), 6, "0");
			if (!AonStringUtils.isBlank(inv.getSeries())) {
				referenceCode = inv.getSeries() + "/" + referenceCode;
			}
			inv.setReferenceCode(referenceCode);
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_PURCHASE_EXPENSES_SERIES = (inv,ctx) -> {
		if (inv.isPurchase() || inv.isExpenses()) {
			inv.setSeries(Integer.toString(AonDateUtils.getYear(inv.getIssueDate())));
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()};
				int number = InvoiceDAO.getNextNumber(ctx.getContext(),types, inv.getSeries());
				inv.setNumber(number);
			}
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE = (inv,ctx) -> {
		if (inv.isUndeductible() || AonStringUtils.equals("<auto>",inv.getReferenceCode())) {
			inv.setReferenceCode( inv.getDocumentNumber());
		} 
	};

	/**
	 * Se rellena las serie y numero para las facturas de compras y gastos.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_UNDEDUCTIBLE_SERIES = (inv,ctx) -> {
		if (inv.isUndeductible()) {
			inv.setSeries(Integer.toString(AonDateUtils.getYear(inv.getIssueDate()==null?new Date():inv.getIssueDate())));
			if (inv.getNumber() == 0) {
				Byte[] types = new Byte[]{InvoiceType.UNDEDUCTIBLE.value()};
				int number = InvoiceDAO.getNextNumber(ctx.getContext(),types, inv.getSeries());
				inv.setNumber(number);
			}
		} 
	};
	
	/**
	 * Aseguramos el SecurityLevel.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_SECURITY_LEVEL = (inv,ctx) -> {
		if (inv.getSecurityLevel() == null) inv.setSecurityLevel(SecurityLevel.OFFICIAL); 
	};
	
	/**
	 * Aseguramos la fecha de IVA..
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_TAX_DATE = (inv,ctx) -> {
		if (inv.getTaxDate() == null) inv.setTaxDate(inv.getIssueDate());
	};
	
	/**
	 * Aseguramos la Actividad.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_ACTIVITY = (inv,ctx) -> {
		if (inv.getActivity() == null
			&& ctx.getConfiguration() != null 
			&& ctx.getConfiguration().getActivities() != null 
			&& ctx.getConfiguration().getActivities().size() == 1) {
			
			EnterpriseActivity act = ctx.getConfiguration().getMainActivity(); 
			inv.setActivity( act == null ? null : act.getId() );
		}
	};


	/**
	 * Aseguramos la fecha de IVA..
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> COMPLETE_RECTIFICATION_TYPE = (inv,ctx) -> {
		if (inv.getRectificationType() == null) inv.setRectificationType(RectificationType.NONE);
	};
	
	
	/**
	 * Aseguramos el nombre del titular de la factura.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> ENSURE_REGISTRY_DATA = (inv,ctx) -> {
		if (AonStringUtils.isBlank(inv.getRegistryName()) || AonStringUtils.isBlank(inv.getRegistryDocument())) {
			Registry registry = RegistryDAO.getRegistry(ctx.getContext(), f-> f.getIdProperty().eq(inv.getRegistry()));
			if (AonStringUtils.isBlank(inv.getRegistryName())) inv.setRegistryName(registry.getName());
			if (AonStringUtils.isBlank(inv.getRegistryDocument())) {
				inv.setRegistryDocumentType(registry.getDocumentType());
				inv.setRegistryDocumentCountry(registry.getDocumentCountry());
				inv.setRegistryDocument(registry.getDocument());
			}
		}
	};

	public static void completeInvoice(AONContext ctx, AonConfiguration config,Invoice inv) throws AonCoreException {
		
		COMPLETE_SALES_SERIES
		.andThen(COMPLETE_PURCHASE_EXPENSES_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_SERIES)
		.andThen(COMPLETE_UNDEDUCTIBLE_REFERENCE_CODE)
		.andThen(COMPLETE_TAX_DATE)
		.andThen(COMPLETE_RECTIFICATION_TYPE)
		.andThen(ENSURE_REGISTRY_DATA)
		.andThen(COMPLETE_ACTIVITY)
		.accept(inv, new AonConfigurationContext(ctx,config));

	}

}
