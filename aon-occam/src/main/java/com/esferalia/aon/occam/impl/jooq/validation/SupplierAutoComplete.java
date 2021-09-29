package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
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

	public static void autoComplete(AONContext ctx, Supplier supplier) throws AonCoreException {
		COMPLETE_TRANSACTION
		.andThen(COMPLETE_STATUS)
			.accept(ctx, supplier);

	}

}
