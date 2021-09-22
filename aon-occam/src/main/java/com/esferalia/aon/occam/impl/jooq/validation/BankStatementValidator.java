package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class BankStatementValidator {
	/**
	 * El dominio debe ser superior a 1
	 */
	public static BiConsumer<BankStatement, AONContext> EMPTY_DOMAIN = (bankStatement, aonContext) -> {
		if (bankStatement.getDomain() == 0)
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	/**
	 * Debe haber una cuenta bancaria asociada al extracto bancario
	 */
	public static BiConsumer<BankStatement, AONContext> EMPTY_RBANK = (bankStatement, aonContext) -> {
		if (bankStatement.getRegistryBank() == null)
			throw new AonCoreException(AonError.FINANCE_EMPTY_RBANK.getMessage());
	};
	/**
	 * El número de lote debe ser superior a 0
	 */
	public static BiConsumer<BankStatement, AONContext> EMPTY_LOT_NUMBER = (bankStatement, aonContext) -> {
		if (bankStatement.getLotNumber() <= 0)
			throw new AonCoreException(AonError.FINANCE_EMPTY_LOT_NUMBER.getMessage());
	};
	/**
	 * La fecha de operación es obligatoria
	 */
	public static BiConsumer<BankStatement, AONContext> EMPTY_OPERATION_DATE = (bankStatement, aonContext) -> {
		if (bankStatement.getOperationDate() == null) {
			throw new AonCoreException(AonError.FINANCE_EMPTY_OPERATION_DATE.getMessage());			
		}
	};
	/**
	 * El concepto común es obligatorio
	 */
	public static BiConsumer<BankStatement, AONContext> EMPTY_COMMON_CONCEPT = (bankStatement, aonContext) -> {
		if (bankStatement.getCommonConcept() == null)
			throw new AonCoreException(AonError.FINANCE_EMPTY_COMMON_CONCEPT.getMessage());
	};
	/**
	 * La cantidad es un valor abosluto, no debe ser negativa
	 */
	public static BiConsumer<BankStatement, AONContext> ZERO_OR_NEGATIVE_AMOUNT = (bankStatement, aonContext) -> {
		if (bankStatement.getAmount() < 0) {
			throw new AonCoreException(AonError.FINANCE_NEGATIVE_AMOUNT.getMessage());
		}
	};
	
	public static void validate(AONContext ctx, BankStatement bankStatement)
			throws AonCoreException {
		EMPTY_DOMAIN
		.andThen(EMPTY_RBANK)
		.andThen(EMPTY_LOT_NUMBER)
		.andThen(EMPTY_OPERATION_DATE)
		.andThen(EMPTY_COMMON_CONCEPT)
		.andThen(ZERO_OR_NEGATIVE_AMOUNT)
		.accept(bankStatement, ctx);
	}
	
}
