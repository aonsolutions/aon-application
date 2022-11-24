package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

abstract  class Mod390HFArabaDeclaration extends Mod390HFDeclaration {

	@Override
	Mod390HF initialize(AONContext ctx, Mod390HF mod) {
		mod.setComplementaryDeclarationAvailable(true);
		mod.setReplacementDeclarationAvailable(true);
		return super.initializeModel(ctx, mod);
	}	

	@Override
	Mod390Key getRegularizationKey() {
		return Mod390Key.AR_C115;
	}
}
