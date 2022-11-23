package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ActivityType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ActivityTypeValidation {

	private ActivityTypeValidation() {

	}
	
	public static final BiConsumer<AONContext, ActivityType> EMPTY_DOMAIN = (ctx, activityType) -> {
		if(activityType.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};

	public static final BiConsumer<AONContext, ActivityType> EMPTY_DESCRIPTION = (ctx, activityType) -> {
		if(AonStringUtils.isBlank(activityType.getDescription())) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("description"));
	};

	public static void validate(AONContext ctx, ActivityType activityType) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_DESCRIPTION)
		.accept(ctx, activityType);
	}
	
}
