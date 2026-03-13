package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod421;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonMathUtils;

abstract class Mod421ATC extends Mod421Declaration {

	@Override
	Mod421 initialize(AONContext ctx, Mod421 mod421) {
		mod421.setComplementaryDeclarationAvailable(true);
		mod421.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod421);
	}	

	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod421 mod) {
		return ComplementaryBeahaviour.REPLACEMENT;
	}
	
	@Override
	double getResult(Mod421 mod) {
		return mod.getAmount(Mod421Key.C19);
	}
	
	protected static double getPendingCompesateAmounts(AONContext ctx, Mod421 mod) {
		return Mod421DAO.getLastPeriodEffectiveModels(ctx, mod)
			.mapToDouble(fm ->
				AonMathUtils.round((fm.isToCompensate() ? AonMathUtils.absRounded(fm.getDeclarationResult()) : 0.0))
			)
			.findFirst()
			.orElse(0.0);
	} 
	
	protected static double getIngresoCuentaAnterior(AONContext ctx, Mod421 mod421, Period period) {
		return Mod421DAO.getPreviousEffectiveModels(ctx, mod421)
				.filter(mod -> mod.getPeriod() == period)
				.mapToDouble(fm ->
					AonMathUtils.round(fm.getAmount(Mod421Key.C06))
				)
				.findFirst()
				.orElse(0.0);
	}

}
