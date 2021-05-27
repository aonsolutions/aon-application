package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class CustomerValidation {
	
	public static BiConsumer<Customer,AONContext> EMPTY_SCOPE = (customer,ctx) -> {
		if (customer.getScope() == null) 
			throw new AonCoreException(AonError.EMPTY_SCOPE.getMessage());
	};
	
	
	public static void validate(AONContext ctx, Customer customer) throws AonCoreException{
		EMPTY_SCOPE
			.accept(customer, ctx);
	}

	public static void validateDeletion(AONContext ctx, Integer id) {
		// TODO Auto-generated method stub
	}
	
	
}
