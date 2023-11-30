package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.watson.error.AonCoreException;

public class RegistryBankAutoComplete {

	private RegistryBankAutoComplete() {}
	
	public static final BiConsumer<AONContext, RegistryBank> COMPLETE_ACTIVE = (ctx, registryBank) -> {
		if (registryBank != null && registryBank.isActive() == null) {
			ctx.log().debug("\t saving registry bank: autocomplete transaction: {0}","False");
			registryBank.setActive(false);
		}
	};
	
	public static void autoComplete(AONContext ctx, RegistryBank registryBank) throws AonCoreException {
		COMPLETE_ACTIVE
		.accept(ctx, registryBank);
	}
}
