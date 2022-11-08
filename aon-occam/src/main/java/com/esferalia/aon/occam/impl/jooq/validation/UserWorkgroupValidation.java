package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.impl.jooq.dao.UserWorkgroupDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class UserWorkgroupValidation {
	
	public static BiConsumer<AONContext, UserWorkgroup> EMPTY_DOMAIN = (ctx, userWorkgroup) -> {
		if(userWorkgroup.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};
	
	public static BiConsumer<AONContext, UserWorkgroup> EMPTY_USER = (ctx, userWorkgroup) -> {
		if(userWorkgroup.getUserId() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("user_id"));
	};
	
	public static BiConsumer<AONContext, UserWorkgroup> EMPTY_WORKGROUP = (ctx, userWorkgroup) -> {
		if(userWorkgroup.getWorkgroup().isEmpty() || userWorkgroup.getWorkgroup().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("workgroup"));
	};
	
//	public static BiConsumer<AONContext, UserWorkgroup> EXIST_USER_WORKGROUP = (ctx, userWorkgroup) -> {
//		UserWorkgroup uw = UserWorkgroupDAO.get(ctx, f -> f.getUserIdProperty().eq(userWorkgroup.getUserId())
//				.and(f.getWorkgroupProperty().eq(userWorkgroup.getWorkgroup().getId())));
//		if(!uw.isEmpty()) 
//			throw new AonCoreException(AonError.EXIST_USER_WORKGROUP.getMessage());
//	};
//	
	public static void validate(AONContext ctx, UserWorkgroup userWorkgroup) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_USER)
		.andThen(EMPTY_WORKGROUP)
//		.andThen(EXIST_USER_WORKGROUP)
		.accept(ctx, userWorkgroup);
	}
	
	
}
