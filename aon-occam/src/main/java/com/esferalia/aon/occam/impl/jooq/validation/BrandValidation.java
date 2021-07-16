package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.impl.jooq.dao.BrandDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class BrandValidation {
	
	public static BiConsumer<AONContext, Brand> NULL = (ctx, brand) -> {
		if (brand == null) 
			throw new AonCoreException(AonError.BRAND_NULL.getMessage());
	};
	
	
	public static BiConsumer<AONContext, Brand> EMPTY = (ctx, brand) -> {
		if (brand.isEmpty()) 
			throw new AonCoreException(AonError.BRAND_EMPTY.getMessage());
	};
	
	public static BiConsumer<AONContext, Brand> EMPTY_DOMAIN = (ctx, brand) -> {
		if (brand.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	public static BiConsumer<AONContext, Brand> EMPTY_NAME = (ctx, brand) -> {
		if (AonStringUtils.isBlank(brand.getName())) 
			throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
	};
	
	public static BiConsumer<AONContext, Brand> REPEAT_NAME = (ctx, brand) -> {
		Brand br = BrandDAO.get(ctx, f -> 
			f.getDomainProperty().eq(brand.getDomain())
			.and(f.getNameProperty().eq(brand.getName())));
		if(br != null && !br.isEmpty()) 
			throw new AonCoreException(AonError.BRAND_REPEAT.getMessage());
	};
	
	public static void validate(AONContext ctx, Brand brand) throws AonCoreException{
		NULL.andThen(EMPTY)
		.andThen(EMPTY_DOMAIN)
		.andThen(EMPTY_NAME)
		.andThen(REPEAT_NAME)
		.accept(ctx, brand);
	}
	
}
