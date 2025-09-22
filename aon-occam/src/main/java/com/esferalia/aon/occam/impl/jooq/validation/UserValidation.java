package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class UserValidation {
	
	private UserValidation() {
		
	}
	
	public static final BiConsumer<AONContext, User> EMPTY_DOMAIN = (ctx, user) -> {
		if(user.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};
	
	public static final BiConsumer<AONContext, User> EMPTY_LOGIN = (ctx, user) -> {
		if(AonStringUtils.isBlank(user.getLogin())) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("login"));
	};
	
	public static final BiConsumer<AONContext, User> EMPTY_NAME = (ctx, user) -> {
		if(AonStringUtils.isBlank(user.getName())) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("name"));
	};
	
	public static void validate(AONContext ctx, User user) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_LOGIN)
		.andThen(EMPTY_NAME)
		.accept(ctx, user);
	}
	
	
}
