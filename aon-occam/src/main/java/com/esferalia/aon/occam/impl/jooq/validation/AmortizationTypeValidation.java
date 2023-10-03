package com.esferalia.aon.occam.impl.jooq.validation;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;
import static com.esferalia.aon.jooq.tables.AmortizationType.AMORTIZATION_TYPE;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class AmortizationTypeValidation {
	
	private AmortizationTypeValidation() {
		
	}
	
	private static final BiConsumer<AmortizationType, AONContext> AMORTIZATION_TYPE_NULL = (amortizationType, ctx) -> {
		if (amortizationType == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL.getMessage());
		}
	}; 	

	private static final BiConsumer<AmortizationType, AONContext> AMORTIZATION_EMPTY_DOMAIN = (amortizationType, ctx) -> {
		if (amortizationType.getDomain() == null) {
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		}
	};
	
	private static final BiConsumer<AmortizationType, AONContext  > AMORTIZATION_TYPE_NULL_ACCUMULATED_ACCOUNT = (  amortizationType,ctx) -> {
		if (amortizationType.getAccumulatedAccount() == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL_ACCUMULATED_ACCOUNT.getMessage());
		}
	};

	private static final BiConsumer<AmortizationType, AONContext> AMORTIZATION_TYPE_NULL_DESCRIPTION = (amortizationType, ctx) -> {
		if (amortizationType.getDescription() == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL_DESCRIPTION.getMessage());
		}
	};

	private static final BiConsumer<AmortizationType, AONContext> AMORTIZATION_TYPE_NULL_FIXED_ASSET_ACCOUNT = (amortizationType, ctx) -> {
		if (amortizationType.getFixedAssetAccount() == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL_FIXED_ASSET_ACCOUNT.getMessage());
		}
	};

	private static final BiConsumer<AmortizationType, AONContext> AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT = (amortizationType, ctx) -> {
		if (amortizationType.getAllocationAccount() == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT.getMessage());
		}
	};
	
	private static final BiConsumer<AmortizationTypeParams, AONContext>AMORTIZATION_TYPE_PARAMS_NULL = (amortizationTypeParams, ctx) ->{
		if (amortizationTypeParams == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_PARAMS_NULL.getMessage());
		}
	};
	
	private static final BiConsumer<AONContext, List<Integer> > DELETE_IDS_NULL = (ctx, deleteIds) ->{
		if (deleteIds == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL.getMessage());
		}
	};
	
	private static final BiConsumer<AmortizationType, AONContext> INCORRECT_PERCENTAGE_DATA = (amortizationType, ctx) ->{
		if (amortizationType.getPercentage() == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL_PERCENTAGE.getMessage());
		}
	};
	
	private static final BiConsumer<AmortizationType, AONContext>AMORTIZATION_TYPE_INVALID_ACCUMULATED_ACCOUNT_LENGTH = (amortizationType, ctx) -> {
		String accAccount = amortizationType.getAccumulatedAccount();
		String text = AonStringUtils.repeat("h", AMORTIZATION_TYPE.ACCUMULATED_ACCOUNT.getDataType().length());
		if (accAccount != null && accAccount.length() > text.length()) {
			throw new AonCoreException(AonError.INVALID_LENGTH.getMessage());
		}
	}; 
	
	private static final BiConsumer<AmortizationType, AONContext> AMORTIZATION_TYPE_INVALID_FIXED_ASSET_ACCOUNT_LENGTH = (amortizationType, ctx) ->{
		String assAccount = amortizationType.getFixedAssetAccount();
		String text = AonStringUtils.repeat("h", AMORTIZATION_TYPE.FIXED_ASSET_ACCOUNT.getDataType().length());
		if (assAccount != null && assAccount.length()> text.length()) {
			throw new AonCoreException(AonError.INVALID_LENGTH.getMessage());
		}
	};
	
	private static final BiConsumer<AmortizationType, AONContext> AMORTIZATION_TYPE_INVALID_ALLOCATION_ACCOUNT_LENGTH = (amortizationType, ctx) ->{
		String allAccount = amortizationType.getAllocationAccount();
		String text = AonStringUtils.repeat("h", AMORTIZATION_TYPE.ALLOCATION_ACCOUNT.getDataType().length());
		if (allAccount != null && allAccount.length()> text.length()) {
			throw new AonCoreException(AonError.INVALID_LENGTH.getMessage());
		}
	};
	
	private static final BiConsumer<AmortizationType, AONContext> AMORTIZATION_TYPE_INVALID_DESCRIPTION_LENGTH = (amortizationType, ctx) ->{
		String desc = amortizationType.getDescription();
		String text = AonStringUtils.repeat("h", AMORTIZATION_TYPE.DESCRIPTION.getDataType().length());
		if (desc != null && desc.length()> text.length()) {
			throw new AonCoreException(AonError.INVALID_LENGTH.getMessage());
		}
	};
	 
	private static final Consumer< AONContext> ACCOUNTING_DOMAIN_NULL = ctx ->{
		int domain = ctx.getDomainId();
		if (domain == 0) {
			throw new AonCoreException(AonError.ACCOUNTING_DOMAIN_NULL.getMessage());
		}
	};
	
	private static final Consumer<AONContext> ACCOUNTING_USER_NULL = ctx ->{
		String user = ctx.getUser();
		if (user == null) {
			throw new AonCoreException(AonError.ACCOUNTING_USER_NULL.getMessage());
		}
	};
	
	public static void validate(AONContext ctx, AmortizationType amortizationType) throws AonCoreException {
		AMORTIZATION_TYPE_NULL.
		andThen(AMORTIZATION_EMPTY_DOMAIN). 
		andThen(AMORTIZATION_TYPE_NULL_ACCUMULATED_ACCOUNT).
		andThen(AMORTIZATION_TYPE_NULL_DESCRIPTION).
		andThen(AMORTIZATION_TYPE_NULL_FIXED_ASSET_ACCOUNT). 
		andThen(AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT).
		andThen(AMORTIZATION_TYPE_INVALID_ACCUMULATED_ACCOUNT_LENGTH).
		andThen(AMORTIZATION_TYPE_INVALID_FIXED_ASSET_ACCOUNT_LENGTH).
		andThen(AMORTIZATION_TYPE_INVALID_ALLOCATION_ACCOUNT_LENGTH).
		andThen(AMORTIZATION_TYPE_INVALID_DESCRIPTION_LENGTH).
		andThen(INCORRECT_PERCENTAGE_DATA).
		accept(amortizationType, ctx); 
	}
	
	public static void validateParams(AmortizationTypeParams params ,AONContext ctx )throws AonCoreException{
		AMORTIZATION_TYPE_PARAMS_NULL.accept(params, ctx );
	}
	
	public static void validateList(AONContext ctx, List<Integer> lista) throws AonCoreException{
		DELETE_IDS_NULL.accept(ctx, lista);
	}
	
	public static void validateAccountingCtx(AONContext ctx) throws AonCoreException{
		ACCOUNTING_DOMAIN_NULL.
		andThen(ACCOUNTING_USER_NULL).
		accept(ctx);
	}
	
}
