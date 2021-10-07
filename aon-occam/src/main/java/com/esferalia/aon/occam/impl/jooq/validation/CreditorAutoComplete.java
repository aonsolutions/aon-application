package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
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

	public static void autoComplete(AONContext ctx, Creditor creditor) throws AonCoreException {
		COMPLETE_TRANSACTION
		.andThen(COMPLETE_STATUS)
			.accept(ctx, creditor);

	}

}
