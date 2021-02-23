package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.error.AonCoreException;

public class SupplierAutoComplete {
	
	public static BiConsumer<AONContext,Supplier> COMPLETE_TRANSACTION = (ctx,supplier) -> {
		if (supplier.getTransaction() == null) {
			ctx.log().info("\t saving creditor: autocomplete transaction: " + InvoiceTransactionType.NATIONAL);
			supplier.setTransaction(InvoiceTransactionType.NATIONAL);
		}
	};

	public static BiConsumer<AONContext,Supplier> COMPLETE_STATUS = (ctx,supplier) -> {
		if (supplier.getStatus() == null) {
			ctx.log().info("\t saving supplier: autocomplete status: " + RegistryStatus.ACTIVE);
			supplier.setStatus(RegistryStatus.ACTIVE);
		}
	};

	public static void autoComplete(AONContext ctx, Supplier supplier) throws AonCoreException {
		COMPLETE_TRANSACTION
		.andThen(COMPLETE_STATUS)
			.accept(ctx, supplier);

	}

}
