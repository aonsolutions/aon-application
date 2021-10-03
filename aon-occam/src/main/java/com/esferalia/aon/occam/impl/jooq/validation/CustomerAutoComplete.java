package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
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

	public static void autoComplete(AONContext ctx, Customer customer) throws AonCoreException {
		COMPLETE_TRANSACTION
		.andThen(COMPLETE_STATUS)
			.accept(ctx, customer);

	}

}
