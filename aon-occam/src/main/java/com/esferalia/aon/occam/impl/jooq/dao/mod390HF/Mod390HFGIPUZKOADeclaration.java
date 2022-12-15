package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.ComplementaryBeahaviour;

abstract class Mod390HFGIPUZKOADeclaration extends Mod390HFDeclaration {
	
	@Override
	Mod390HF initialize(AONContext ctx, Mod390HF mod) {
		mod.setComplementaryDeclarationAvailable(true);
		mod.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod);
	}
	
	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod390HF mod) {
		return ComplementaryBeahaviour.COMPLEMENTARY;
	}
	
	@Override
	public Mod390Key getRegularizationKey() {
		return null;
	}

	@Override
	public Mod390Key[] getCompensationExplainKeys() {
		return new Mod390Key[] {Mod390Key.GP_C037};
	}
	
	@Override
	public Mod390Key[] getSamePeriodExplainKeys() {
		return new Mod390Key[] {Mod390Key.GP_C038,Mod390Key.GP_C039};
	}
}
