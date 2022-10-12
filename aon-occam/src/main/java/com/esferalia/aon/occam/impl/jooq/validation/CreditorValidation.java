package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class CreditorValidation {
	
	public static BiConsumer<Creditor,AONContext> EMPTY_SCOPE = (creditor,ctx) -> {
		if (creditor.getScope().isEmpty()) 
			throw new AonCoreException(AonError.EMPTY_SCOPE.getMessage());
	};
	
	
	public static void validate(AONContext ctx, Creditor creditor) throws AonCoreException{
		EMPTY_SCOPE
			.accept(creditor, ctx);
	}

	public static void validateDeletion(AONContext ctx, Integer id) {
		// TODO Auto-generated method stub
	}
	
	
}
