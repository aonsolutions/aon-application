package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class WarehouseValidation {
	
	public static final BiConsumer<Warehouse,AONContext> EMPTY_DOMAIN = (warehouse,ctx) -> {
		if(warehouse.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	public static final BiConsumer<Warehouse,AONContext> EMPTY_WORKPLACE = (warehouse,ctx) -> {
		if(warehouse.getWorkplace() == null) 
			throw new AonCoreException(AonError.EMPTY_WORKPLACE.getMessage());
	};
	
	public static final BiConsumer<Warehouse,AONContext> EMPTY_NAME = (warehouse,ctx) -> {
		if(AonStringUtils.isBlank(warehouse.getName())) 
			throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
	};
		
	public static void validate(AONContext ctx, Warehouse warehouse) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_WORKPLACE)
		.andThen(EMPTY_NAME)
		.accept(warehouse, ctx);
	}

	public static void validateDeletion(AONContext ctx, Integer id) {
		
		// TODO
	}
	
	public static void autocomplete(AONContext ctx, Warehouse warehouse) {
		// TODO
	}	
}
