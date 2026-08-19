package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.impl.jooq.dao.ScopeDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CustomerValidation {
	
	private CustomerValidation() {
	
	}
	
	private static final BiConsumer<Customer,AONContext> EMPTY_SCOPE = (customer,ctx) -> {
		if (customer.getScope().isEmpty()) 
			throw new AonCoreException(AonError.EMPTY_SCOPE.getMessage());
	};
	
	private static final BiConsumer<Customer,AONContext> INVALID_SCOPE = (customer,ctx) -> {
		if (null == customer.getScope() || null == customer.getScope().getId())
			return;

		if (null == customer.getScope().getDomain()) {
			// Se busca en el dominio del cliente; si no aparece puede ser un ambito
			// heredado del padre, asi que NO se anula: se deja el id tal cual.
			// El getter de Customer recrea un Scope vacio si se pone a null, y eso
			// termina en "Column 'scope' cannot be null" en el UPDATE.
			ScopeDAO.get(ctx, customer.getDomain().getId(), customer.getScope().getId())
				.ifPresent(customer::setScope);

			return;
		}

		if (!customer.getScope().getDomain().equals(customer.getDomain().getId()))
			throw new AonCoreException(AonError.REGISTRY_INVALID_SCOPE.getMessage());
	};
	
	public static final BiConsumer<Customer,AONContext> EMPTY_DOCUMENT = (customer,ctx) -> {
		if(!customer.isNotIdentified() && AonStringUtils.isBlank(customer.getDocument())) {
			throw new AonCoreException(AonError.REGISTRY_EMPTY_DOCUMENT.getMessage());
		}
	};
	
	public static final BiConsumer<Customer,AONContext> EMPTY_DOCUMENT_TYPE = (customer,ctx) -> {
		if(!customer.isNotIdentified() && customer.getDocumentType() == null) {
			throw new AonCoreException(AonError.REGISTRY_EMPTY_DOCUMENT_TYPE.getMessage());
		}
	};
	
	public static final BiConsumer<Customer,AONContext> EMPTY_DOCUMENT_COUNTRY = (customer,ctx) -> {
		if(!customer.isNotIdentified() && customer.getDocumentCountry() == null) {
			throw new AonCoreException(AonError.REGISTRY_EMPTY_DOCUMENT_COUNTRY.getMessage());
		}
	};
	
	public static final BiConsumer<Customer,AONContext> NOT_VALID_DOCUMENT = (customer,ctx) -> {
		if(!customer.isNotIdentified()) {
			if(Country.ES == customer.getDocumentCountry() && AonDocumentUtil.isValid(customer.getDocument())) {
				throw new AonCoreException(AonError.REGISTRY_NOT_VALID_DOCUMENT.getMessage());
			} else if(AonDocumentUtil.isValidable(customer.getDocumentType().value(), customer.getDocumentCountry().getAeatCode(), customer.getDocument()) 
				&& AonDocumentUtil.isValidComunitaryCode(customer.getDocumentCountry().getAeatCode(), customer.getDocument())) {
				throw new AonCoreException(AonError.REGISTRY_INVALID_DOCUMENT.getMessage());
			}
		}
	};
	
	public static void validate(AONContext ctx, Customer customer) throws AonCoreException{
		EMPTY_SCOPE
		.andThen(INVALID_SCOPE)
//		.andThen(EMPTY_DOCUMENT)
//		.andThen(EMPTY_DOCUMENT_TYPE)
//		.andThen(EMPTY_DOCUMENT_COUNTRY)
//		.andThen(NOT_VALID_DOCUMENT)
		.accept(customer, ctx);
	}

	public static void validateDeletion(AONContext ctx, Integer id) {
		// TODO Auto-generated method stub
	}
	
	
}
