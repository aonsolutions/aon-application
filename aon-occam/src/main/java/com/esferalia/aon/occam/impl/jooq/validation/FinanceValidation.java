package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.AccountEntryFbatch.ACCOUNT_ENTRY_FBATCH;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceTrackingDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceValidation {

	/**
	 * El dominio del vencimiento  no puede estar vacio.
	 */
	private static BiConsumer<Finance,AONContext> CHECK_EMPTY_DOMAIN = (finance,ctx) -> {
		if (finance.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * La importe del vencmiento no puede ser cero.
	 */
	private static BiConsumer<Finance,AONContext> CHECK_AMOUNT_ZERO = (finance,ctx) -> {
		if (AonMathUtils.isZero(finance.getAmount()))
			throw new AonCoreException(AonError.FINANCE_AMOUNT_ZERO.getMessage());
	};
	
	/**
	 * La importe del vencmiento no puede ser cero.
	 */
	private static BiConsumer<FinanceTracking,AONContext> CHECK_TRACKING_AMOUNT_ZERO = (financeTracking,ctx) -> {
		if (AonMathUtils.isZero(financeTracking.getAmount()))
			throw new AonCoreException(AonError.FINANCE_AMOUNT_ZERO.getMessage());
	};

	/**
	 * El scope del vencimiento no puede estar vacio.
	 */
	private static BiConsumer<Finance,AONContext> CHECK_EMPTY_SCOPE = (finance,ctx) -> {
		if (finance.getScope() == null || finance.getScope().getId() == null) 
			throw new AonCoreException(AonError.FINANCE_EMPTY_SCOPE.getMessage());
	};

	/**
	 * La cuenta bancaria debe ser valida.
	 */
	private static BiConsumer<Finance,AONContext> CHECK_BANK_ACCOUNT = (finance,ctx) -> {
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
			.andThen(CHECK_EMPTY_SCOPE)
			.andThen(CHECK_BANK_ACCOUNT)
			.accept(finance, ctx);
	}
	
	/**
	 * Para borrar el status debe ser PENDING
	 */
	private static BiConsumer<Finance,AONContext> CHECK_DELETE_STATUS = (finance,ctx) -> {
		if (!finance.isPending()) 
			throw new AonCoreException(AonError.DELETE_STATUS_WRONG.getMessage());
	};
	

	public static void validateDelete(AONContext ctx, Finance finance) throws AonCoreException {
		CHECK_DELETE_STATUS
			.accept(finance, ctx);
		
	}

	/*
	 *  **********************************************************************************************
	 *  **********************************************************************************************
 						VALIDACIONES A LA HORA DE GRABAR UN APUNTE DE TESORERIA
	 *  **********************************************************************************************
	 *  **********************************************************************************************
	 */
	// TODO ¿Debe estar aquí?
	
	
	/**
	 * Para borrar el asiento, este no puede venir de remesa
	 */
	private static BiConsumer<FinanceEntry,AONContext> CHECK_IF_ACCOUNT_ENTRY_IS_FROM_FBATCH = (entry,ctx) -> {
		if (ctx.getDslContext()
			.select(ACCOUNT_ENTRY_FBATCH.FBATCH)
			.from(ACCOUNT_ENTRY_FBATCH)
			.where(ACCOUNT_ENTRY_FBATCH.ACCOUNT_ENTRY.eq(entry.getAccountEntry().getId()))
			.fetch()
			.stream()
			.findAny()
			.isPresent())
			throw new AonCoreException(AonError.FINANCE_ENTRY_FROM_FBATCH.getMessage());
	};
	
	/**
	 * Para borrar, no puede haber movimientos posteriores
	 */
	private static BiConsumer<FinanceEntry,AONContext> CHECK_IF_TRACKINGS_ARE_LAST_TRACKING = (entry,ctx) -> {
		for (FinanceTracking ft : entry.getTrackings().values()) {
			if (!ft.isLastTracking())
				throw new AonCoreException(AonError.FINANCE_ENTRY_LATER_TRACKINGS.getMessage());
		}
	};

	public static void validateDelete(AONContext ctx, FinanceEntry entry) {
		CHECK_IF_ACCOUNT_ENTRY_IS_FROM_FBATCH
			.andThen(CHECK_IF_TRACKINGS_ARE_LAST_TRACKING)
			.accept(entry, ctx);
	}

	/*
	 *  **********************************************************************************************
	 *  **********************************************************************************************
 						VALIDACIONES A LA HORA DE GRABAR UN TRACKING
	 *  **********************************************************************************************
	 *  **********************************************************************************************
	 */
	/**
	 * El vencimiento debe estar pendiente para ser saldado.
	 */
	private static BiConsumer<Finance,AONContext> CHECK_PENDING_FOR_SETTLING = (finance,ctx) -> {
		if (!finance.isPending() && !finance.isReturned()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_SETTLED.getMessage());
	};

	/**
	 * El vencimiento debe estar pendiente o devuelto para ser pagado.
	 */
	private static BiConsumer<Finance,AONContext> CHECK_PENDING_FOR_PAYING = (finance,ctx) -> {
		if (!finance.isPending() && !finance.isReturned()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_PAID.getMessage());
	};

	/**
	 * El vencimiento debe estar pgado para ser devuelto.
	 */
	private static BiConsumer<Finance,AONContext> CHECK_PENDING_FOR_RETURNING = (finance,ctx) -> {
		if (!finance.isPaid() && !finance.isBatched()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_RETURNED.getMessage());
	};

	public static Finance validateSettleTracking(AONContext ctx, Integer financeId) {
		Finance finance = FinanceDAO.getFinance(ctx, financeId);
		if (finance == null ) {
			if (finance == null) throw new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage());
		}
		CHECK_PENDING_FOR_SETTLING
			.accept(finance, ctx);
		return finance;
	}

	private static BiConsumer<FinanceTracking,AONContext> CHECK_IF_TRACKING_IS_FROM_BATCH_FOR_UNDOING = (financeTracking,ctx) -> {
		if (financeTracking != null && financeTracking.isBatched()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_UNDOING.format("El último movimiento procede de una remesa"));
	};

	private static BiConsumer<FinanceTracking,AONContext> CHECK_IF_TRACKING_IS_FRACTIONED_LINK_FOR_UNDOING = (financeTracking,ctx) -> {
		if (financeTracking != null && financeTracking.isFractioned()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_UNDOING.format("El último movimiento procede de un fraccionamiento"));
	};
	
	private static BiConsumer<FinanceTracking,AONContext> CHECK_IF_TRACKING_IS_FROM_STATEMENT_LINK_FOR_UNDOING = (financeTracking,ctx) -> {
		if (financeTracking != null && financeTracking.getBankStatementLink() != null) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_UNDOING.format("El último movimiento procede de una extracto bancario"));
	};

	private static BiConsumer<FinanceTracking,AONContext> CHECK_IF_FINANCE_IS_GROUPED_FOR_UNDOING = (financeTracking,ctx) -> {
		if (financeTracking != null && financeTracking.getFinance().getFinanceGroup() != null) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_UNDOING.format("El vencimiento se encuentra agrupado"));
	};

	public static FinanceTracking validateUndoTracking(AONContext ctx, Integer financeId) {
		Finance finance = FinanceDAO.getFinance(ctx, financeId);
		if (finance == null ) {
			if (finance == null) throw new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage());
		}
		FinanceTracking tracking = FinanceTrackingDAO.getLastTracking( ctx, financeId);
		CHECK_IF_TRACKING_IS_FROM_BATCH_FOR_UNDOING
			.andThen(CHECK_IF_TRACKING_IS_FROM_STATEMENT_LINK_FOR_UNDOING)
			.andThen(CHECK_IF_TRACKING_IS_FRACTIONED_LINK_FOR_UNDOING)
			.andThen(CHECK_IF_FINANCE_IS_GROUPED_FOR_UNDOING)
			.accept(tracking, ctx);
		return tracking;
	}

	public static void validatePay(AONContext ctx, Finance finance) {
		Finance original = FinanceDAO.getFinance(ctx, finance.getId());
		if (original == null ) {
			if (original == null) throw new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage());
		}
		CHECK_PENDING_FOR_PAYING
			.accept(original, ctx);
		CHECK_AMOUNT_ZERO
			.accept(finance, ctx);
	}

	public static void validateReturn(AONContext ctx, FinanceTracking financeTracking) {
		if (financeTracking.getFinance() == null || financeTracking.getFinance().getId( )== null ) {
			throw new AonCoreException(AonError.FINANCE_TRACKING_WITHOUT_FINANCE.getMessage());
		}
		Finance original = FinanceDAO.getFinance(ctx, financeTracking.getFinance().getId());
		if (original == null ) {
			if (original == null) throw new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage());
		}
		CHECK_PENDING_FOR_RETURNING
			.accept(original, ctx);
		CHECK_TRACKING_AMOUNT_ZERO
			.accept(financeTracking, ctx);
	}
}
