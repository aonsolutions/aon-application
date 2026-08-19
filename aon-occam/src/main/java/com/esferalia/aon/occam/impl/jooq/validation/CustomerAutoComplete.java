package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.Date;
import java.util.Objects;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.registry.CustomerFiscalStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryExpirationUtils;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
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
	
	public static final BiConsumer<AONContext,Customer> COMPLETE_EXPIRATION_DATE = (ctx,customer) -> {
		Date normalized = RegistryExpirationUtils.normalizeExpirationDate(
				customer.getStatus(), customer.getExpirationDate());

		if (!Objects.equals(normalized, customer.getExpirationDate())) {
			ctx.log().debug("\t saving customer: autocomplete expirationDate: {0}", normalized);
			customer.setExpirationDate(normalized);
		}
	};
	
	public static final BiConsumer<AONContext,Customer> COMPLETE_FISCAL_STATUS = (ctx,customer) -> {
		if (customer.getFiscalStatus() == null) {
			ctx.log().debug("\t saving customer: autocomplete status: {0}", CustomerFiscalStatus.REGISTERED);
			customer.setFiscalStatus(CustomerFiscalStatus.REGISTERED);
		}
	};

	public static final BiConsumer<AONContext, Customer> COMPLETE_SCOPE = (ctx, customer) -> {
		if(customer.getScope() == null || customer.getScope().isEmpty() || 
				(customer.getScope() != null && customer.getScope().getDomain() != null 
				&& !customer.getScope().getDomain().equals(customer.getDomain().getId()))) {
			User user = SecurityDAO.getUser(ctx);	
			Scope scope = SecurityDAO.getUserScopeStream(ctx, user.getId(), null).findFirst().orElse(new Scope());
			if(scope.isEmpty()) {
				scope = SecurityDAO.getScopeStream(ctx, f -> f.getDomainProperty().eq(customer.getDomain().getId())).findFirst().orElse(new Scope());
				if(scope.isEmpty()) {
					scope = SecurityDAO.insertScope(ctx, new Scope()
						.setDescription("EMPRESA")
						.setDomain(customer.getDomain().getId()));
					if(user.getDomain().getId().equals(customer.getDomain().getId()))
						SecurityDAO.insertUserScope(ctx, new UserScope()
							.setDomain(customer.getDomain().getId())
							.setScope(scope.getId())
							.setUserId(user.getId()));
				}
			}
			customer.setScope(scope);
		}
	};
	
	public static void autoComplete(AONContext ctx, Customer customer) throws AonCoreException {
		COMPLETE_TRANSACTION
		.andThen(COMPLETE_STATUS)
		.andThen(COMPLETE_EXPIRATION_DATE)
		.andThen(COMPLETE_FISCAL_STATUS)
		.andThen(COMPLETE_SCOPE)
		.accept(ctx, customer);
	}

}
