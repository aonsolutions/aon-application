package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class CustomerAutoComplete {
	private CustomerAutoComplete() {
		
	}
	public static final BiConsumer<AONContext,Customer> COMPLETE_TRANSACTION = (ctx,customer) -> {
		if (customer.getTransaction() == null) {
			ctx.log().debug("\t saving customer: autocomplete transaction: {0}",InvoiceTransactionType.NATIONAL);
			customer.setTransaction(InvoiceTransactionType.NATIONAL);
		}
	};

	public static final BiConsumer<AONContext,Customer> COMPLETE_STATUS = (ctx,customer) -> {
		if (customer.getStatus() == null) {
			ctx.log().debug("\t saving customer: autocomplete status: {0}",RegistryStatus.ACTIVE);
			customer.setStatus(RegistryStatus.ACTIVE);
		}
	};

	public static final BiConsumer<AONContext, Customer> COMPLETE_SCOPE = (ctx, customer) -> {
		if(customer.getScope().isEmpty()) {
			User user = SecurityDAO.getUser(ctx);	
			Scope scope = SecurityDAO.getUserScopeStream(ctx, user.getId(), f -> f.getDescriptionProperty().eq("GENERAL")).findFirst().orElse(new Scope());
			if(scope.isEmpty()) {
				Integer[] scopes = null;
				try {
				    scopes = SecurityDAO.getUserScopes(ctx, user.getId());
				} catch (Exception e) { 
				    e.printStackTrace();
				}
				if(scopes != null && scopes.length > 0) {
				    Integer sc = scopes[0];
					scope = SecurityDAO.getScopeStream(ctx, f -> f.getIdProperty().eq(sc)).findFirst().orElse(new Scope());
				} else {
					scope = SecurityDAO.getScopeStream(ctx,  f ->
						f.getDomainProperty().eq(customer.getDomain().getId())).findFirst().orElse(new Scope());
					if(scope.isEmpty()) {
						scope = SecurityDAO.insertScope(ctx, new Scope()
							.setDescription("GENERAL")
							.setDomain(customer.getDomain().getId()));
					}
				}
			} 
			customer.setScope(scope);
		}
	};
	
	public static void autoComplete(AONContext ctx, Customer customer) throws AonCoreException {
		COMPLETE_TRANSACTION
		.andThen(COMPLETE_STATUS)
		.andThen(COMPLETE_SCOPE)
		.accept(ctx, customer);

	}

}
