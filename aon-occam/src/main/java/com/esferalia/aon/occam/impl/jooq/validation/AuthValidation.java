package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.Consumer;

import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.impl.jooq.dao.AuthDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AuthValidation {
    
    private AuthValidation() {
    
    }
	
	public static final Consumer<Auth> NULL = auth -> {
		if (auth == null) 
			throw new AonCoreException(AonError.AUTH_NULL.getMessage());
	};
	
	public static final Consumer<Auth> EMPTY = auth -> {
		if (auth.isEmpty()) 
			throw new AonCoreException(AonError.AUTH_EMPTY.getMessage());
	};
	
	public static final Consumer<Auth> EMPTY_EMAIL = auth -> {
		if (AonStringUtils.isBlank(auth.getEmail())) 
			throw new AonCoreException(AonError.AUTH_EMPTY_EMAIL.getMessage());
	};
	
	public static final Consumer<Auth> EXIST_EMAIL = auth -> {
		Auth auxAuth = AuthDAO.getAuthByEmail(auth.getEmail());
		if(!auxAuth.isEmpty() && !auth.getUuid().equals(auxAuth.getUuid())) 
			throw new AonCoreException(AonError.AUTH_EXIST_EMAIL.getMessage());
	};
	
    public static final Consumer<Auth> EXIST_PHONE = auth -> {
        if(!AonStringUtils.isBlank(auth.getPhone())) {
            Auth auxAuth = AuthDAO.getAuthByPhone(auth.getPhone());
            if(!auxAuth.isEmpty() && !auth.getUuid().equals(auxAuth.getUuid())) 
                throw new AonCoreException(AonError.AUTH_EXIST_PHONE.getMessage());
        }
    };

    public static final Consumer<Auth> EXIST_DOCUMENT = auth -> {
        if(!AonStringUtils.isBlank(auth.getDocument())) {
            Auth auxAuth = AuthDAO.getAuthByDocument(auth.getDocument());
            if(!auxAuth.isEmpty() && !auth.getUuid().equals(auxAuth.getUuid())) 
                throw new AonCoreException(AonError.AUTH_EXIST_DOCUMENT.getMessage());
        }
    };
	
	public static void validate(Auth auth) throws AonCoreException{
		NULL.andThen(EMPTY)
		.andThen(EMPTY_EMAIL)
		.andThen(EXIST_EMAIL)
		.andThen(EXIST_PHONE)
		.andThen(EXIST_DOCUMENT)
		.accept(auth);
	}
	
}
