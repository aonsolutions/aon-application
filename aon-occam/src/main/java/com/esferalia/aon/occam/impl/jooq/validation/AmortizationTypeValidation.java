package com.esferalia.aon.occam.impl.jooq.validation;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import static com.esferalia.aon.jooq.tables.AmortizationType.AMORTIZATION_TYPE;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import org.jooq.Field;
import org.jooq.TableLike;

public class AmortizationTypeValidation {

	// Validaciones para evitar que los campos queden vacio

	public static BiConsumer<AmortizationType, AONContext> EMPTY_DOMAIN = (amortizationType, ctx) -> {
		if (amortizationType.getDomain() == null) {
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		}
	};

	public static BiConsumer<AmortizationType, AONContext> EMPTY_DESCRIPTION = (amortizationType, ctx) -> {
		if (amortizationType.getDescription() == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL_DESCRIPTION.getMessage());
		}
	};

	public static BiConsumer<AmortizationType, AONContext> EMPTY_FIXED_ASSET_ACCOUNT = (amortizationType, ctx) -> {
		if (amortizationType.getFixedAssetAccount() == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL_FIXED_ASSET_ACCOUNT.getMessage());
		}
	};

	public static BiConsumer<AmortizationType, AONContext> EMPTY_ACCUMULATED_ACCOUNT = (amortizationType, ctx) -> {
		if (amortizationType.getAccumulatedAccount() == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL_ACCUMULATED_ACCOUNT.getMessage());
		}
	};

	public static BiConsumer<AmortizationType, AONContext> EMPTY_ALLOCATION_ACCOUNT = (amortizationType, ctx) -> {
		if (amortizationType.getAllocationAccount() == null) {
			throw new AonCoreException(AonError.AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT.getMessage());
		}
	};
	// Validacion para controlar que el numero en porcentaje no supere la
	// establecida
	public static BiConsumer<AmortizationType, AONContext> FIELD_LENGHT_EXCEEDED = (amortizationType, ctx) -> {
		Double percentage = amortizationType.getPercentage();

		if (percentage != null) {
			String percentageString = Double.toString(percentage);
			int decimalSeparatorIndex = percentageString.indexOf('.');

			if (decimalSeparatorIndex != -1 && percentageString.length() - decimalSeparatorIndex > 5) {
				throw new AonCoreException("La longitud del campo percentage excede el límite de 15 dígitos ");
			}
		}
	};
	
	//No se puede borrar tipo de amortizacion de distinto dominio
	public static BiConsumer<AmortizationType, AONContext> FROM_PARENT_DOMAIN_CHECK = (amortizationType, ctx) -> {
		if (amortizationType.getDomain().getId() != ctx.getDomainId()) {
			throw new AonCoreException(AonError.ACCOUNT_PARENT_ACCOUNT.getMessage());
		}
	};
	
	//Comprobar que los campos a borrar existen
	public static BiConsumer<AmortizationType, AONContext> DESCRIPTION_EXISTS = (amortizationType, ctx) -> {
		if (!exists(ctx, amortizationType, AMORTIZATION_TYPE, AMORTIZATION_TYPE.DESCRIPTION)) {
            throw new AonCoreException("El registro con la descripción especificada no existe.");
		}
	};
	
	public static BiConsumer<AmortizationType, AONContext> FIXED_ASSET_ACCOUNT_EXISTS = (amortizationType, ctx) -> {
		if (!exists(ctx, amortizationType, AMORTIZATION_TYPE, AMORTIZATION_TYPE.FIXED_ASSET_ACCOUNT)) {
            throw new AonCoreException("El registro con la cuenta de inmovilizado especificada no existe.");
		}
	};

	public static BiConsumer<AmortizationType, AONContext> ALLOCATION_ACCOUNT_EXISTS = (amortizationType, ctx) -> {
		if (!exists(ctx, amortizationType, AMORTIZATION_TYPE, AMORTIZATION_TYPE.ALLOCATION_ACCOUNT)) {
            throw new AonCoreException("El registro con la cuenta de dotacion especificada no existe.");
		}
	};
	
	public static BiConsumer<AmortizationType, AONContext> ACCUMULATED_ACCOUNT_EXISTS = (amortizationType, ctx) -> {
		if (!exists(ctx, amortizationType, AMORTIZATION_TYPE, AMORTIZATION_TYPE.ACCUMULATED_ACCOUNT)) {
            throw new AonCoreException("El registro con la cuenta de acumulado especificada no existe.");
		}
	};
	
	
	
	public static void validate(AONContext ctx, AmortizationType amortizationType) throws AonCoreException {
		EMPTY_DOMAIN
		.andThen(EMPTY_DESCRIPTION)
		.andThen(EMPTY_FIXED_ASSET_ACCOUNT)
		.andThen(EMPTY_ACCUMULATED_ACCOUNT)
		.andThen(EMPTY_ALLOCATION_ACCOUNT)
		.andThen(FIELD_LENGHT_EXCEEDED).accept(amortizationType, ctx);
	}
	
	public static List<String> check(AONContext ctx, AmortizationType amortizationType){
		LinkedList<String> messages = new LinkedList<String>(); 
		try {
			EMPTY_DOMAIN.accept(amortizationType, ctx);
		} catch (AonCoreException e) {
			messages.add(e.getMessage());
		}
		try {
			EMPTY_DESCRIPTION.accept(amortizationType, ctx);
		} catch (AonCoreException e) {
			messages.add(e.getMessage());
		}
		try {
			EMPTY_FIXED_ASSET_ACCOUNT.accept(amortizationType, ctx);
		} catch (AonCoreException e) {
			messages.add(e.getMessage());
		}
		try {
			EMPTY_ACCUMULATED_ACCOUNT.accept(amortizationType, ctx);
		} catch (AonCoreException e) {
			messages.add(e.getMessage());
		}
		try {
			EMPTY_ALLOCATION_ACCOUNT.accept(amortizationType, ctx);
		} catch (AonCoreException e) {
			messages.add(e.getMessage());
		}
		try {
			FIELD_LENGHT_EXCEEDED.accept(amortizationType, ctx);
		} catch (AonCoreException e) {
			messages.add(e.getMessage());
		}
		return messages;
	}
	
	
	public static boolean exists(AONContext ctx, AmortizationType amortizationType, TableLike<?> table, Field<String> column ) {
		return ctx.getDslContext()
				.select(column)
				.from(table)
				.where(column.eq(amortizationType.getId().toString()))
				.stream()
				.map(rec -> rec.getValue(column))
				.findFirst()
				.orElse(null) != null;
	}
	
	public static void validateDeletion(AONContext ctx, AmortizationType amortizationType) {
		FROM_PARENT_DOMAIN_CHECK
		.andThen(DESCRIPTION_EXISTS)
		.andThen(FIXED_ASSET_ACCOUNT_EXISTS)
		.andThen(ALLOCATION_ACCOUNT_EXISTS)
		.andThen(ACCUMULATED_ACCOUNT_EXISTS)
		.accept(amortizationType, ctx);
	}
	
	

}
