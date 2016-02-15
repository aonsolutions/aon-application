package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceValidation {

	/**
	 * El dominio del vencimiento  no puede estar vacio.
	 */
	public static BiConsumer<Finance,AONContext> CHECK_EMPTY_DOMAIN = (finance,ctx) -> {
		if (finance.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * La importe del vencmiento no puede ser cero.
	 */
	public static BiConsumer<Finance,AONContext> CHECK_AMOUNT_ZERO = (finance,ctx) -> {
		if (AonMathUtils.isZero(finance.getAmount()))
			throw new AonCoreException(AonError.FINANCE_AMOUNT_ZERO.getMessage());
	};
	
	/**
	 * Si no hay registry, se rellena con el de invoice (si hay). 
	 */
	public static BiConsumer<Finance,AONContext> FILL_REGISTRY_IF_EMPTY = (finance,ctx) -> {
		if (!finance.isEmptyInvoice() && (finance.getRegistry() == null || finance.getRegistry().getId() == null)) {
			finance.setRegistry(new Registry().setId(finance.getInvoice().getRegistry()));
		}
	};
	
	/**
	 * Se rellenan los datos de registry, bien de la factura o del registry. 
	 */
	public static BiConsumer<Finance,AONContext> FILL_REGISTRY_DOCUMENT_IF_EMPTY = (finance,ctx) -> {
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
	public static BiConsumer<Finance,AONContext> FILL_CONCEPT_IF_EMPTY = (finance,ctx) -> {
		if (!finance.isEmptyInvoice()) {
	        finance.setConcept(finance.getInvoice().getDocumentNumber()); 
		}
	};
	
	/**
	 * Se rellena el nivel de seguridad. 
	 */
	public static BiConsumer<Finance,AONContext> FILL_SECURITY_LEVEL_IF_EMPTY = (finance,ctx) -> {
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
	public static BiConsumer<Finance,AONContext> FILL_SCOPE_IF_EMPTY = (finance,ctx) -> {
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
	 * El scope del vencimiento no puede estar vacio.
	 */
	public static BiConsumer<Finance,AONContext> CHECK_EMPTY_SCOPE = (finance,ctx) -> {
		if (finance.getScope() == null || finance.getScope().getId() == null) 
			throw new AonCoreException(AonError.FINANCE_EMPTY_SCOPE.getMessage());
	};
	
	/**
	 * La cuenta bancaria debe ser valida.
	 */
	public static BiConsumer<Finance,AONContext> CHECK_BANK_ACCOUNT = (finance,ctx) -> {
		if (finance.getBankAccount() == null || AonStringUtils.isEmpty(finance.getBankAccount().getBban())) {
			finance.setBankAccount(null);
			finance.setBankAlias(null);
			finance.setBic(null);
		}
		if (finance.getBankAccount() != null && !finance.getBankAccount().isValidBankAccount()) {
			if (!finance.getBankAccount().isValidIbanLength()) {
				throw new AonCoreException(AonError.FINANCE_WRONG_IBAN_LENGTH.getMessage());
			} else if (!finance.getBankAccount().isValidBban()) {
				throw new AonCoreException(AonError.FINANCE_WRONG_ACCOUNT_BANK.getMessage());
			} else {
				throw new AonCoreException(AonError.FINANCE_WRONG_IBAN.getMessage());
			}
		}
	};

	public static void validateSave(AONContext ctx, Finance finance) throws AonCoreException {
			CHECK_EMPTY_DOMAIN
			.andThen(CHECK_AMOUNT_ZERO)
			.andThen(FILL_REGISTRY_IF_EMPTY)
			.andThen(FILL_REGISTRY_DOCUMENT_IF_EMPTY)
			.andThen(FILL_CONCEPT_IF_EMPTY)
			.andThen(FILL_SECURITY_LEVEL_IF_EMPTY)
			.andThen(FILL_SCOPE_IF_EMPTY)
			.andThen(CHECK_EMPTY_SCOPE)
			.andThen(CHECK_BANK_ACCOUNT)
			.accept(finance, ctx);
	}
	
	/**
	 * Para borrar el status debe ser PENDING
	 */
	public static BiConsumer<Finance,AONContext> CHECK_DELETE_STATUS = (finance,ctx) -> {
		if (finance.getFinanceStatus() != null && finance.getFinanceStatus() != FinanceStatus.PENDING) 
			throw new AonCoreException(AonError.DELETE_STATUS_WRONG.getMessage());
	};
	
	public static void validateDelete(AONContext ctx, Finance finance) throws AonCoreException {
		CHECK_DELETE_STATUS
		.accept(finance, ctx);
		
	}

}
