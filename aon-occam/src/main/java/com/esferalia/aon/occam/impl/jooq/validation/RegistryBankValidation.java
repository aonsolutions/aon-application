package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import static com.esferalia.aon.jooq.tables.Rbank.RBANK;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class RegistryBankValidation {
	
	private RegistryBankValidation() {}
	
	/**
	 * Throws an exception if the registryBank is null
	 */
	private static final BiConsumer<AONContext, RegistryBank> NULL = (ctx, registryBank) -> {
		if (registryBank == null)
			throw new AonCoreException(AonError.REGISTRY_BANK_NULL.getMessage());
	};
	
	/**
	 * Throws an exception if the registryBank is empty
	 */
	private static final BiConsumer<AONContext, RegistryBank> EMPTY = (ctx, registryBank) -> {
		if (registryBank.isEmpty())
			throw new AonCoreException(AonError.REGISTRY_BANK_EMPTY.getMessage());
	};
	
	/**
	 * Throws an exception if the registryBank's domain is null
	 */
	private static final BiConsumer<AONContext, RegistryBank> EMPTY_DOMAIN = (ctx, registryBank) -> {
		if (registryBank.getDomain() == null) {
			throw new AonCoreException(AonError.REGISTRY_BANK_NULL_DOMAIN.getMessage());
		}
	};
	
	/**
	 * Throws an exception if the registryBank's registry is null
	 */
	private static final BiConsumer<AONContext, RegistryBank> EMPTY_REGISTRY = (ctx, registryBank) -> {
		if (registryBank.getRegistry() == null) {
			throw new AonCoreException(AonError.REGISTRY_BANK_NULL_REGISTRY.getMessage());
		}
	};
	
	/**
	 * Throws an exception if the size of the registryBank's bic is invalid
	 */
	private static final BiConsumer<AONContext, RegistryBank> INVALID_SIZE_BIC = (ctx, registryBank) -> {
		if (registryBank.getBic().length() > RBANK.BIC.getDataType().length()) {
			throw new AonCoreException(AonError.REGISTRY_BANK_INVALID_BIC_SIZE.getMessage());
		}
	};
	
	/**
	 * Throws an exception if the size of the registryBank's suffix is invalid
	 */
	private static final BiConsumer<AONContext, RegistryBank> INVALID_SIZE_SUFFIX = (ctx, registryBank) -> {
		if (registryBank.getSuffix().length() > RBANK.SUFIX.getDataType().length()) {
			throw new AonCoreException(AonError.REGISTRY_BANK_INVALID_SUFFIX_SIZE.getMessage());
		}
	};
	
	/**
	 * Throws an exception if the size of the registryBank's requisition is invalid
	 */
	private static final BiConsumer<AONContext, RegistryBank> INVALID_SIZE_REQUISITION = (ctx, registryBank) -> {
		if (registryBank.getRequisition().length() > RBANK.REQUISITION.getDataType().length()) {
			throw new AonCoreException(AonError.REGISTRY_BANK_INVALID_REQUISITION_SIZE.getMessage());
		}
	};
	
	/**
	 * Throws an exception if the size of the registryBank's SEPA mandate reference is invalid
	 */
	private static final BiConsumer<AONContext, RegistryBank> INVALID_SIZE_SEPA_MANDATE_REF = (ctx, registryBank) -> {
		if (registryBank.getSepaMandateRef().length() > RBANK.SEPA_MANDATE_REF.getDataType().length()) {
			throw new AonCoreException(AonError.REGISTRY_BANK_INVALID_SEPA_MANDATE_REF_SIZE.getMessage());
		}
	};
	
	public static void validate(AONContext ctx, RegistryBank registryBank) throws AonCoreException {
		NULL.andThen(EMPTY)
		.andThen(EMPTY_DOMAIN)
		.andThen(EMPTY_REGISTRY)
		.andThen(INVALID_SIZE_BIC)
		.andThen(INVALID_SIZE_SUFFIX)
		.andThen(INVALID_SIZE_REQUISITION)
		.andThen(INVALID_SIZE_SEPA_MANDATE_REF)
		.accept(ctx, registryBank);
	}
}
