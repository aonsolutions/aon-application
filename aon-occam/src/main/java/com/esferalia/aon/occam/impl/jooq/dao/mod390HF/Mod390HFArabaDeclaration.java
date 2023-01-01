package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.ComplementaryBeahaviour;

abstract  class Mod390HFArabaDeclaration extends Mod390HFDeclaration {

	@Override
	Mod390HF initialize(AONContext ctx, Mod390HF mod) {
		mod.setComplementaryDeclarationAvailable(false);
		mod.setReplacementDeclarationAvailable(true);
		return super.initializeModel(ctx, mod);
	}	

	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod390HF mod) {
		return  ComplementaryBeahaviour.REPLACEMENT;
	}

	@Override
	public Mod390Key getRegularizationKey() {
		return Mod390Key.AR_C115;
	}
	
	@Override
	public Mod390Key[] getCompensationExplainKeys() {
		return new Mod390Key[] {Mod390Key.AR_C130};
	}
	
	@Override
	public Mod390Key[] getSamePeriodExplainKeys() {
		return new Mod390Key[] {Mod390Key.AR_C126,Mod390Key.AR_C127};
	}
	
}
