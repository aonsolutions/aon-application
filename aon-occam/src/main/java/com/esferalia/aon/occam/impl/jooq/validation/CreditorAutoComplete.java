package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class CreditorAutoComplete {
	
	private CreditorAutoComplete() {
		
	}
	public static final BiConsumer<AONContext,Creditor> COMPLETE_TRANSACTION = (ctx,creditor) -> {
		if (creditor.getTransaction() == null) {
			ctx.log().debug("\t saving creditor: autocomplete transaction: {0}",InvoiceTransactionType.NATIONAL);
			creditor.setTransaction(InvoiceTransactionType.NATIONAL);
		}
	};

	public static final BiConsumer<AONContext,Creditor> COMPLETE_STATUS = (ctx,creditor) -> {
		if (creditor.getStatus() == null) {
			ctx.log().debug("\t saving creditor: autocomplete status: {0}",RegistryStatus.ACTIVE);
			creditor.setStatus(RegistryStatus.ACTIVE);
		}
	};
	
	public static final BiConsumer<AONContext, Creditor> COMPLETE_SCOPE = (ctx, customer) -> {
		if(customer.getScope() == null ) {
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

	public static void autoComplete(AONContext ctx, Creditor creditor) throws AonCoreException {
		COMPLETE_TRANSACTION
		.andThen(COMPLETE_STATUS)
		.andThen(COMPLETE_SCOPE)
			.accept(ctx, creditor);

	}

}
