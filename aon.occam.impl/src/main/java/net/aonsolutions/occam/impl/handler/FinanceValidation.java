package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Finance;
import net.aonsolutions.occam.api.model.FinanceTracking;
import net.aonsolutions.occam.api.model.type.FinanceType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.impl.AONContext;

class FinanceValidation {
	record FinanceValidationContext(AONContext ctx, int domain, Finance finance) {}
	record FinanceTrackingValidationContext(AONContext ctx, int domain, FinanceTracking financeTracking) {}
	
	private FinanceValidation() {
	}

	/**
	 * El dominio del vencimiento  no puede estar vacio.
	 */
	private static final Consumer<FinanceValidationContext> CHECK_EMPTY_DOMAIN = c -> {
		if (c.finance.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * La importe del vencmiento no puede ser cero.
	 */
	private static final Consumer<FinanceValidationContext> CHECK_AMOUNT_ZERO = c -> {
		if (AonMathUtils.isZero(c.finance.getAmount()))
			throw new AonCoreException(AonError.FINANCE_AMOUNT_ZERO.getMessage());
	};
	
	/**
	 * La importe del vencmiento no puede ser cero.
	 */
	private static final Consumer<FinanceTrackingValidationContext> CHECK_TRACKING_AMOUNT_ZERO = c -> {
		if (AonMathUtils.isZero(c.financeTracking.getAmount()))
			throw new AonCoreException(AonError.FINANCE_AMOUNT_ZERO.getMessage());
	};

	/**
	 * El scope del vencimiento no puede estar vacio.
	 */
	private static final Consumer<FinanceValidationContext> CHECK_EMPTY_SCOPE = c -> {
		if (c.finance.getScope() == null) 
			throw new AonCoreException(AonError.FINANCE_EMPTY_SCOPE.getMessage());
	};

	/**
	 * La cuenta bancaria debe ser valida.
	 */
	private static final Consumer<FinanceValidationContext> CHECK_BANK_ACCOUNT = c -> {
		if (c.finance.getBankAccount().isEmpty()) {
			c.finance.setBankAccount(null);
			c.finance.setBankAlias(null);
			c.finance.setBic(null);
		}
		c.finance.getBankAccount()
			.filter( b -> AonStringUtils.isEmpty(b.getBban()))
			.ifPresent(b -> {
				c.finance.setBankAccount(null);
				c.finance.setBankAlias(null);
				c.finance.setBic(null);
		});
		c.finance.getBankAccount()
			.filter( b -> !b.isValidBankAccount())
			.ifPresent(b -> {
				if (!b.isValidIbanLength()) {
					throw new AonCoreException(AonError.FINANCE_WRONG_IBAN_LENGTH.getMessage());
				} else if (!b.isValidBban()) {
					throw new AonCoreException(AonError.FINANCE_WRONG_ACCOUNT_BANK.getMessage());
				} else {
					throw new AonCoreException(AonError.FINANCE_WRONG_IBAN.getMessage());
				}
		});
	};
	
	private static final Consumer<FinanceValidationContext> PAYMENT_CHECK = c -> {
		c.finance.getInvoice()
		.ifPresent(	i ->	{
			InvoiceType invoiceType = i.getType();
			if (invoiceType == null) {
				invoiceType = c.ctx.getDslContext()
					.select(INVOICE.TYPE)
					.from(INVOICE)
					.where(INVOICE.ID.eq(i.getId()))
					.fetch()
					.stream()
					.map(rec -> InvoiceType.value(rec.getValue(INVOICE.TYPE)).orElse(null))
					.findFirst()
					.orElse(null);
			}
			if (invoiceType == null) {
				throw new AonCoreException(AonError.FINANCE_UNKNOWN_PAYMENT.getMessage());
			} else {
				if ((invoiceType == InvoiceType.SALES && c.finance.getFinanceType() == FinanceType.PAYMENT )
				||  (invoiceType != InvoiceType.SALES && c.finance.getFinanceType() != FinanceType.PAYMENT )) {
					throw new AonCoreException(AonError.FINANCE_WRONG_PAYMENT.getMessage());		
				}
			}
		});
	};

	static void validateSave(AONContext ctx, int domain, Finance finance) throws AonCoreException {
			CHECK_EMPTY_DOMAIN
			.andThen(CHECK_AMOUNT_ZERO)
			.andThen(CHECK_EMPTY_SCOPE)
			.andThen(CHECK_BANK_ACCOUNT)
			.andThen(PAYMENT_CHECK)
			.accept(new FinanceValidationContext(ctx, domain, finance));
	}
	
	/**
	 * Para borrar el status debe ser PENDING
	 */
	private static final Consumer<FinanceValidationContext> CHECK_DELETE_STATUS = c -> {
		if (!c.finance.isPending()) 
			throw new AonCoreException(AonError.DELETE_STATUS_WRONG.getMessage());
	};
	

	static void validateDelete(AONContext ctx, int domain, Finance finance) throws AonCoreException {
		CHECK_DELETE_STATUS
			.accept(new FinanceValidationContext(ctx,domain,finance));
		
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
	private static final Consumer<FinanceValidationContext> CHECK_PENDING_FOR_SETTLING = c -> {
		if (!c.finance.isPending() && !c.finance.isReturned()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_SETTLED.getMessage());
	};
	
	/**
	 * El vencimiento debe estar saldado para ser deshacer el movimiento y volver a pendiente.
	 */
	private static final Consumer<FinanceValidationContext> CHECK_SETTLE_FOR_UNSETTLING = c -> {
		if (!c.finance.isSettled()) 
			throw new AonCoreException("No se puede eleminar el movimiento saldado del vencimiento, no est\u00E1 saldado.");
	};

	/**
	 * El vencimiento debe estar pendiente o devuelto para ser pagado.
	 */
	private static final Consumer<FinanceValidationContext> CHECK_PENDING_FOR_PAYING = c -> {
		if (!c.finance.isPending() && !c.finance.isReturned()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_PAID.getMessage());
	};

	/**
	 * El vencimiento debe estar pendiente o devuelto para ser pagado.
	 */
	private static final Consumer<FinanceValidationContext> CHECK_PENDING_FOR_FRACTION = c -> {
		if (!c.finance.isPending() && !c.finance.isReturned()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_FRACTIONED.getMessage());
	};

	/**
	 * El vencimiento debe estar pendiente o devuelto para ser pagado.
	 */
	private static final Consumer<FinanceTrackingValidationContext> CHECK_DATE_FOR_PAYING = c -> {
		if (c.financeTracking.getTrackingDate() == null) 
			throw new AonCoreException(AonError.FINANCE_TRACKING_WITHOUT_DATE.getMessage());
	};

	/**
	 * El vencimiento debe estar pgado para ser devuelto.
	 */
	private static final Consumer<FinanceValidationContext> CHECK_PENDING_FOR_RETURNING = c -> {
		if (!c.finance.isPaid() && !c.finance.isBatched()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_RETURNED.getMessage());
	};

	static Finance validateSettleTracking(AONContext ctx, int domain, Integer financeId) {
		Optional<Finance> opt = FinanceHandler.get(ctx, domain, financeId);
		if (opt.isEmpty()) {
			throw new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage());
		}
		Finance finance = opt.get();
		CHECK_PENDING_FOR_SETTLING
			.accept(new FinanceValidationContext(ctx, domain, finance));
		return finance;
	}
	
	static Finance validateUnSettleTracking(AONContext ctx, int domain, Integer financeId) {
		Optional<Finance> opt = FinanceHandler.get(ctx, domain, financeId);
		if (opt.isEmpty()) {
			throw new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage());
		}
		Finance finance = opt.get();
		CHECK_SETTLE_FOR_UNSETTLING
			.accept(new FinanceValidationContext(ctx, domain, finance));
		return finance;
	}

	static Finance validateFractionTracking(AONContext ctx, int domain, Integer financeId) {
		Optional<Finance> opt = FinanceHandler.get(ctx, domain, financeId);
		if (opt.isEmpty()) {
			throw new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage());
		}
		Finance finance = opt.get();
		CHECK_PENDING_FOR_FRACTION
			.accept(new FinanceValidationContext(ctx, domain, finance));
		return finance;
	}

	private static final Consumer<FinanceTrackingValidationContext> CHECK_IF_TRACKING_IS_FROM_BATCH_FOR_UNDOING = c -> {
		if (c.financeTracking != null && c.financeTracking.isBatched()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_UNDOING.format("El último movimiento procede de una remesa"));
	};

	private static final Consumer<FinanceTrackingValidationContext> CHECK_IF_TRACKING_IS_FRACTIONED_LINK_FOR_UNDOING = c -> {
		if (c.financeTracking != null && c.financeTracking.isFractioned()) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_UNDOING.format("El último movimiento procede de un fraccionamiento"));
	};
	
	private static final Consumer<FinanceTrackingValidationContext> CHECK_IF_TRACKING_IS_FROM_STATEMENT_LINK_FOR_UNDOING = c -> {
		if (c.financeTracking != null && c.financeTracking.getBankStatementLink() != null) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_UNDOING.format("El último movimiento procede de una extracto bancario"));
	};

	private static final Consumer<FinanceTrackingValidationContext> CHECK_IF_FINANCE_IS_GROUPED_FOR_UNDOING = c -> {
		if (c.financeTracking != null && c.financeTracking.getFinance().getFinanceGroup() != null) 
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_UNDOING.format("El vencimiento se encuentra agrupado"));
	};

	static FinanceTracking validateUndoTracking(AONContext ctx, int domain, Integer financeId) {
		Finance finance = FinanceHandler.get(ctx, domain, financeId)
			.orElseThrow( () -> new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage()));
		FinanceTracking tracking = FinanceTrackingHandler.getLastTracking( ctx, finance.getId());
		CHECK_IF_TRACKING_IS_FROM_BATCH_FOR_UNDOING
			.andThen(CHECK_IF_TRACKING_IS_FROM_STATEMENT_LINK_FOR_UNDOING)
			.andThen(CHECK_IF_TRACKING_IS_FRACTIONED_LINK_FOR_UNDOING)
			.andThen(CHECK_IF_FINANCE_IS_GROUPED_FOR_UNDOING)
			.accept( new FinanceTrackingValidationContext(ctx, domain, tracking));
		return tracking;
	}

	static void validatePay(AONContext ctx, int domain, Finance finance) {
		Finance original  = FinanceHandler.get(ctx, domain, finance.getId())
			.orElseThrow( () -> new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage()));
		if (!AonNumberUtils.equals( 
				finance.getPayMethod().map(p -> p.getId()).orElse(null), 
				original.getPayMethod().map(p -> p.getId()).orElse(null))) {
			int i = ctx.getDslContext().update(FINANCE)
					.set(FINANCE.PAY_METHOD, finance.getPayMethod().map(p -> p.getId()).orElse(null))
					.set(FINANCE.MODIFICATION_USER,ctx.getUser())
					.set(FINANCE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
					.where(FINANCE.ID.equal( finance.getId()))
					.execute();
			ctx.log().info("UPDATE FINANCE  ("+i+") id: " + finance.getId() + " PayMethod");
		}
		CHECK_PENDING_FOR_PAYING
			.accept(new FinanceValidationContext(ctx, domain, original));
		CHECK_AMOUNT_ZERO
			.accept(new FinanceValidationContext(ctx, domain, finance));
	}
	static void validatePay(AONContext ctx, int domain, FinanceTracking financeTracking) {
		CHECK_DATE_FOR_PAYING.accept( new FinanceTrackingValidationContext(ctx, domain, financeTracking));
	}

	static void validateReturn(AONContext ctx, int domain, FinanceTracking financeTracking) {
		if (financeTracking.getFinance() == null || financeTracking.getFinance().getId( )== null ) {
			throw new AonCoreException(AonError.FINANCE_TRACKING_WITHOUT_FINANCE.getMessage());
		}
		Finance original = FinanceHandler.get(ctx, domain, financeTracking.getFinance().getId())
			.orElseThrow( () -> new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage()));
		CHECK_PENDING_FOR_RETURNING
			.accept(new FinanceValidationContext(ctx, domain, original));
		CHECK_TRACKING_AMOUNT_ZERO
			.accept(new FinanceTrackingValidationContext(ctx, domain, financeTracking));
	}

	static Finance validateFraction(AONContext ctx, int domain, Finance finance, List<Finance> fractions) {
		Finance original = FinanceHandler.get(ctx, domain, finance.getId())
			.orElseThrow( () -> new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage()));
		CHECK_PENDING_FOR_FRACTION
			.accept(new FinanceValidationContext(ctx, domain, original));
		if (AonCollectionUtils.isEmpty(fractions)) {
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_FRACTIONED_LIST.getMessage());
		}
		double amount = 0;
		for ( Finance fin : fractions) {
			amount = AonMathUtils.round(amount + fin.getAmount());
		}
		if ( !AonMathUtils.equals(amount, original.getAmount())) {
			throw new AonCoreException(AonError.FINANCE_CAN_NOT_BE_FRACTIONED_AMOUNT.getMessage());
		}
		return original; 
	}
}
