package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class SupplierValidation {
	
	public static BiConsumer<Supplier,AONContext> EMPTY_SCOPE = (supplier,ctx) -> {
		if (supplier.getScope().isEmpty()) 
			throw new AonCoreException(AonError.EMPTY_SCOPE.getMessage());
	};
	
	
	public static void validate(AONContext ctx, Supplier supplier) throws AonCoreException{
		EMPTY_SCOPE
			.accept(supplier, ctx);
	}

	public static void validateDeletion(AONContext ctx, Integer id) {
		// TODO Auto-generated method stub
	}
	
	
}
