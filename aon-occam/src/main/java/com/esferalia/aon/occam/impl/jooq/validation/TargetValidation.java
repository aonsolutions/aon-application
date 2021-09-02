package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class TargetValidation {
	
	public static BiConsumer<AONContext, Target> EMPTY_SCOPE = (ctx, target) -> {
		if (target.getScope() == null) 
			throw new AonCoreException(AonError.EMPTY_SCOPE.getMessage());
	};
	
	
	public static void validate(AONContext ctx, Target target) throws AonCoreException{
		EMPTY_SCOPE
			.accept(ctx, target);
	}

	public static void validateDeletion(AONContext ctx, Integer id) {
		// TODO Auto-generated method stub
	}
	
	
}
