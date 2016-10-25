package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.util.Date;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceValidation {

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
	 * El dominio de la factura no puede estar vacio.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> EMPTY_DOMAIN = (inv,ctx) -> {
		if (inv.getDomain() == 0) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * La fecha de la factura es un dato obligatorio.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> EMPTY_DATE = (inv,ctx) -> {
		if (inv.getIssueDate() == null)
			throw new AonCoreException(AonError.INVOICE_EMPTY_DATE.getMessage());
	};

	/**
	 * La fecha IVA de la factura es un dato obligatorio.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> EMPTY_TAX_DATE = (inv,ctx) -> {
		if (inv.getTaxDate() == null)
			throw new AonCoreException(AonError.INVOICE_EMPTY_TAX_DATE.getMessage());
	};

	/**
	 * El Tipo de la factura no puede ser null.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> EMPTY_INVOICE_TYPE = (inv,ctx) -> {
		if (inv.getType() == null) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_TYPE.getMessage());
		}
	};
	
	/**
	 * El titular de la factura es un dato obligatorio.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> EMPTY_INVOICE_REGISTRY = (inv,ctx) -> {
		if (inv.getRegistry() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_REGISTRY.getMessage());
		}
	};
	
	/**
	 * El ámbito de la factura es un dato obligatorio.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> EMPTY_INVOICE_SCOPE = (inv,ctx) -> {
		if (inv.getScope() == null || inv.getScope().getId() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_SCOPE.getMessage());
		}
	};
	
	/**
	 * El Domain/Serie/Número/Tipo no puede estar duplicado
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> DUPLICATED_SERIES_NUMBER = (inv,ctx) -> {
		if (ctx.getContext().getDslContext().fetchExists( 
				ctx.getContext().getDslContext().selectOne()
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(inv.getDomain()))
					.and(INVOICE.SERIES.eq(inv.getSeries()))
					.and(INVOICE.NUMBER.eq(inv.getNumber()))
					.and(INVOICE.TYPE.eq(inv.getType().value())))) {
			throw new AonCoreException(AonError.INVOICE_DUPLICATED_SERIES_NUMBER.getMessage());
		}
	};

	/**
	 * Si se ha indicado una fecha de límte de operaciones en los parámetros de la 
	 * empresa, debe ser anterior a la fecha de factura.
	 * 
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> OPERATIONS_DEADLINE = (inv,ctx) -> {
		Date deadline = ctx.getConfiguration().getOperationsDeadline();
		if (deadline != null && deadline.after(inv.getIssueDate())) 
			throw new AonCoreException(AonError.INVOICE_OPERATIONS_DEADLINE.getMessage());
	};
	
	/**
	 * Si el año de la factura no es anterior en cinco años al actual.
	 */
	public static BiConsumer<Invoice,AonConfigurationContext> CHECK_FIVE_YEARS  = (inv,ctx) -> {
		int thisYear = AonDateUtils.getYear(new Date());
		int invoiceYear = AonDateUtils.getYear(inv.getIssueDate());
		if (invoiceYear < (thisYear-5) || invoiceYear > (thisYear+1)) {
			throw new AonCoreException(AonError.INVOICE_FIVE_YEARS.getMessage());
		}
	};
	
	/**
	 * El origen de la linea de factura es un dato obligatorio.
	 */
	public static BiConsumer<InvoiceDetail,AonConfigurationContext> EMPTY_SOURCE = (det,ctx) -> {
		if (det.getSource() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_SOURCE.getMessage());
		}
	};

	/**
	 * El centro de trabajo es un dato obligatorio.
	 */
	public static BiConsumer<InvoiceDetail,AonConfigurationContext> EMPTY_WORKPLACE = (det,ctx) -> {
		if (det.getWorkPlace() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_WORKPLACE.getMessage());
		}
	};

	public static void validateInvoice(AONContext ctx,AonConfiguration config,Invoice inv) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_DATE)
			.andThen(EMPTY_TAX_DATE)
			.andThen(EMPTY_INVOICE_TYPE)
			.andThen(EMPTY_INVOICE_REGISTRY)
			.andThen(EMPTY_INVOICE_SCOPE)
			.andThen(DUPLICATED_SERIES_NUMBER)
			.andThen(OPERATIONS_DEADLINE)
			.andThen(CHECK_FIVE_YEARS)
			.accept(inv, new AonConfigurationContext(ctx,config));

	}

	public static void validateDetail(AONContext ctx, AonConfiguration config, InvoiceDetail detail) {
		EMPTY_SOURCE
		.andThen(EMPTY_WORKPLACE)
			.accept(detail, new AonConfigurationContext(ctx,config));
	}

}
