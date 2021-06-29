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
	
	public static BiConsumer<AONContext,Customer> COMPLETE_TRANSACTION = (ctx,customer) -> {
		if (customer.getTransaction() == null) {
			ctx.log().info("\t saving customer: autocomplete transaction: " + InvoiceTransactionType.NATIONAL);
			customer.setTransaction(InvoiceTransactionType.NATIONAL);
		}
	};

	public static BiConsumer<AONContext,Customer> COMPLETE_STATUS = (ctx,customer) -> {
		if (customer.getStatus() == null) {
			ctx.log().info("\t saving customer: autocomplete status: " + RegistryStatus.ACTIVE);
			customer.setStatus(RegistryStatus.ACTIVE);
		}
	};
	
	public static BiConsumer<AONContext,Customer> COMPLETE_SCOPE = (ctx,customer) -> {
		if(customer.getScope() == null) {
			Integer scope;
			User user = SecurityDAO.getUser(ctx);	
			Scope s = SecurityDAO.getUserScopeStream(ctx, user.getId(), f -> f.getDescriptionProperty().eq("GENERAL")).findFirst().orElse(null);
			
			if(s == null) {
				Integer[] scopes = SecurityDAO.getUserScopes(ctx, user.getId());
				if(scopes != null && scopes.length > 0)
					scope = scopes[0];
				else {
					s = SecurityDAO.getScopeStream(ctx,  f ->
						f.getDomainProperty().eq(customer.getDomain().getId())).findFirst().orElse(null);
					if(s == null) {
						s = SecurityDAO.insertScope(ctx, new Scope()
							.setDescription("GENERAL")
							.setDomain(customer.getDomain().getId()));
					}
					scope = s.getId();
				}
			} else scope = s.getId();
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
