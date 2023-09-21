package com.esferalia.aon.occam.impl.jooq.validation;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import static com.esferalia.aon.jooq.tables.AmortizationType.AMORTIZATION_TYPE;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


import java.util.function.BiConsumer;

import org.jooq.Field;
import org.jooq.TableLike;


public class AmortizationTypeValidation {
	
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
	
	private static final BiConsumer<AmortizationType, AONContext> ID_EXISTS = (amortizationType, ctx) ->{
		if (!existsId(ctx, amortizationType, AMORTIZATION_TYPE, AMORTIZATION_TYPE.ID)) {
            throw new AonCoreException("El registro con el ID especificado no existe.");
		}
	};
	
	public static void validate(AONContext ctx, AmortizationType amortizationType) throws AonCoreException {
		AMORTIZATION_TYPE_NULL.
		andThen(AMORTIZATION_EMPTY_DOMAIN). 
		andThen(AMORTIZATION_TYPE_NULL_ACCUMULATED_ACCOUNT).
		andThen(AMORTIZATION_TYPE_NULL_DESCRIPTION).
		andThen(AMORTIZATION_TYPE_NULL_FIXED_ASSET_ACCOUNT). 
		andThen(AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT).
		accept(amortizationType, ctx);
	}
	
	
	public static boolean existsId(AONContext ctx, AmortizationType amortizationType, TableLike<?> table, Field<Integer> column) {
		return ctx.getDslContext()
				.select(column)
				.from(table)
				.where(column.eq(amortizationType.getId()))
				.stream()
				.map(rec -> rec.getValue(column))
				.findFirst()
				.orElse(null)!=null;
	}
	
	public static void validateDeletion(AONContext ctx, AmortizationType amortizationType) {
		ID_EXISTS
		.accept(amortizationType, ctx);
	}
	
}
