package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class SupplierAutoComplete {
	private SupplierAutoComplete() {
		
	}
	public static final BiConsumer<AONContext,Supplier> COMPLETE_TRANSACTION = (ctx,supplier) -> {
		if (supplier.getTransaction() == null) {
			ctx.log().debug("\t saving supplier: autocomplete transaction: {0}",InvoiceTransactionType.NATIONAL);
			supplier.setTransaction(InvoiceTransactionType.NATIONAL);
		}
	};

	public static final BiConsumer<AONContext,Supplier> COMPLETE_STATUS = (ctx,supplier) -> {
		if (supplier.getStatus() == null) {
			ctx.log().debug("\t saving supplier: autocomplete status: {0}", RegistryStatus.ACTIVE);
			supplier.setStatus(RegistryStatus.ACTIVE);
		}
	};
	
	public static final BiConsumer<AONContext, Supplier> COMPLETE_SCOPE = (ctx, customer) -> {
		if(customer.getScope().isEmpty()) {
			User user = SecurityDAO.getUser(ctx);	
			Scope scope = SecurityDAO.getUserScopeStream(ctx, user.getId(), f -> f.getDescriptionProperty().eq("GENERAL")).findFirst().orElse(new Scope());
	
			if(scope.isEmpty()) {
				Integer[] scopes = SecurityDAO.getUserScopes(ctx, user.getId());
				if(scopes != null && scopes.length > 0) {
					scope = SecurityDAO.getScopeStream(ctx, f -> f.getIdProperty().eq(scopes[0])).findFirst().orElse(new Scope()) ;
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

	public static void autoComplete(AONContext ctx, Supplier supplier) throws AonCoreException {
		COMPLETE_TRANSACTION
		.andThen(COMPLETE_STATUS)
		.andThen(COMPLETE_SCOPE)
		.accept(ctx, supplier);

	}

}
