package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

abstract class Mod303ARABA extends Mod303Declaration {

	@Override
	Mod303 initialize(AONContext ctx, Mod303 mod303) {
		mod303.setComplementaryDeclarationAvailable(true);
		mod303.setReplacementDeclarationAvailable(true);
		return super.initializeModel(ctx, mod303);
	}	

	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod303 mod) {
		return  mod.isComplementary()
			?ComplementaryBeahaviour.COMPLEMENTARY
			:ComplementaryBeahaviour.REPLACEMENT;
	}

	@Override
	double getResult(Mod303 mod) {
		return mod.getAmount(Mod303Key.AR_C080);
	}
	
	@Override
	void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303) {
	}
}
