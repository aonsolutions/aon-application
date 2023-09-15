package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceAutoComplete {

	private FinanceAutoComplete() {
	
	}
	
	/**
	 * Si no hay registry, se rellena con el de invoice (si hay). 
	 */
	public static final BiConsumer<Finance,AONContext> COMPLETE_REGISTRY_IF_EMPTY = (finance,ctx) -> {
		if (!finance.isEmptyInvoice() && (finance.getRegistry() == null || finance.getRegistry().getId() == null)) {
			finance.setRegistry(new Registry().setId(finance.getInvoice().getRegistry()));
		}
	};
	
	/**
	 * Se rellenan los datos de registry, bien de la factura o del registry. 
	 */
	public static final BiConsumer<Finance,AONContext> COMPLETE_REGISTRY_DOCUMENT_IF_EMPTY = (finance,ctx) -> {
		if (AonStringUtils.isEmpty(finance.getRegistryDocument())) {
			finance.setRegistryDocument( !finance.isEmptyInvoice() 
					? finance.getInvoice().getRegistryDocument() 
					: finance.getRegistry().getDocument());
			finance.setRegistryDocumentType( !finance.isEmptyInvoice() 
					? finance.getInvoice().getRegistryDocumentType() 
					: finance.getRegistry().getDocumentType());
			finance.setRegistryDocumentCountry( !finance.isEmptyInvoice() 
					? finance.getInvoice().getRegistryDocumentCountry() 
					: finance.getRegistry().getDocumentCountry());
		}
	};

	/**
	 * Se rellena el concepto si no existe. 
	 */
	public static final BiConsumer<Finance,AONContext> COMPLETE_CONCEPT_IF_EMPTY = (finance,ctx) -> {
		if (!finance.isEmptyInvoice() && AonStringUtils.isBlank( finance.getConcept() )) {
	        finance.setConcept(finance.getInvoice().getDocumentNumber()); 
		}
	};
	
	/**
	 * Se rellena el nivel de seguridad. 
	 */
	public static final BiConsumer<Finance,AONContext> COMPLETE_SECURITY_LEVEL_IF_EMPTY = (finance,ctx) -> {
		if (finance.getSecurityLevel() == null) {
			if (!finance.isEmptyInvoice()) {
				finance.setSecurityLevel(finance.getInvoice().getSecurityLevel());
			} else {
				finance.setSecurityLevel(SecurityLevel.OFFICIAL);
			}
		}
	};
	
	/**
	 * Se rellena el nivel de seguridad. 
	 */
	public static final BiConsumer<Finance,AONContext> COMPLETE_SCOPE_IF_EMPTY = (finance,ctx) -> {
		if (finance.getScope() == null || finance.getScope().getId() == null) {
			if (!finance.isEmptyInvoice()) {
				finance.setScope(finance.getInvoice().getScope());
			} else {
				if (!finance.isPayroll()) {
					finance.setScope(SecurityDAO.getScopeFromRegistry(ctx,finance.isPayment(), finance.getRegistry().getId()));
				} else {
					finance.setScope(SecurityDAO.getScopeFromContract(ctx,finance.getDueDate(), finance.getRegistry().getId()));
				}
			}
		}
	};
	
	/**
	 * Se rellena payment. 
	 */
	public static final BiConsumer<Finance,AONContext> COMPLETE_PAYMENT = (finance,ctx) -> {
		if(finance.getInvoice() != null && finance.getInvoice().getType() != null) {
			finance.setPayment(!finance.getInvoice().isSales());
		} else if(finance.getInvoice() != null && finance.getInvoice().getType() == null) {
			InvoiceType invoiceType = finance.getInvoice().getType();
			if (invoiceType == null) {
				invoiceType = ctx.getDslContext()
					.select(INVOICE.TYPE)
					.from(INVOICE)
					.where(INVOICE.ID.eq(finance.getInvoice().getId()))
					.fetch()
					.stream()
					.map(rec -> InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)))
					.findFirst()
					.orElse(null);
			}	
			finance.setPayment(!InvoiceType.SALES.equals(invoiceType));
		}
	};
	
	/**
	 * Se rellena paymethod. 
	 */
	public static final BiConsumer<Finance,AONContext> COMPLETE_PAYMETHOD = (finance, ctx) -> {
		if(finance.getPayMethod() == null) {
			PayMethod pm = null;
			if(finance.getPayMethodName() != null) {
				pm = PayMethodDAO.get(ctx, f -> f.getNameProperty().eq(finance.getPayMethodName()));
			} else if(finance.getPayMethodType() != null) {
				pm = PayMethodDAO.get(ctx, f -> f.getTypeProperty().eq(finance.getPayMethodType().value()));
			}
			
			if(pm == null && finance.getPayMethodType() != null) {
				pm = new PayMethod()
						.setDomain(finance.getDomain())
						.setName(finance.getPayMethodName() != null  
								? finance.getPayMethodName() 
								: finance.getPayMethodType().getDescription())
						.setType(finance.getPayMethodType());
				pm = PayMethodDAO.save(ctx, pm);
			}
			if(pm != null) 
				finance.setPayMethod(pm.getId());
		}
	};
	
	public static void completeFinance(AONContext ctx, Finance finance) throws AonCoreException {
			COMPLETE_REGISTRY_IF_EMPTY
			.andThen(COMPLETE_REGISTRY_DOCUMENT_IF_EMPTY)
			.andThen(COMPLETE_CONCEPT_IF_EMPTY)
			.andThen(COMPLETE_SECURITY_LEVEL_IF_EMPTY)
			.andThen(COMPLETE_SCOPE_IF_EMPTY)
			.andThen(COMPLETE_PAYMENT)
			.andThen(COMPLETE_PAYMETHOD)
			.accept(finance, ctx);
	}

}
