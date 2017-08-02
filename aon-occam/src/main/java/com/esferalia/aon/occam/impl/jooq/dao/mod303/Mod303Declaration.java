package com.esferalia.aon.occam.impl.jooq.dao.mod303;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;

public abstract class Mod303Declaration {
	
	public static Mod303Declaration getInstance( Mod303 mod) {
		
		if (AEAT_2017_Declaration.accept(mod)) 		return new AEAT_2017_Declaration();
		if (BIZKAIA_2017_Declaration.accept(mod)) 	return new BIZKAIA_2017_Declaration();
		if (ARABA_2017_Declaration.accept(mod)) 	return new ARABA_2017_Declaration();
		
		throw new AonCoreException("No existe una declaración para el modelo solicitado");
	}

	protected static void add(Mod303Key key,Mod303 mod,double amount) {
		mod.ensureDetail(key).addAccumulatedAmount(amount);	
	}
	
	protected static void prorate(Mod303Key key,Mod303 mod,double amount) {
		amount = AonMathUtils.round( amount * mod.getProratePercent() / 100 );
		mod.ensureDetail(key).addAccumulatedAmount(amount);	
	}

	public abstract Mod303 initializeMod303(AONContext ctx, Mod303 mod303);
	public abstract void firstInitializeMod303(AONContext ctx, Mod303 mod303);
	public abstract Mod303 calculate(AONContext ctx, Mod303 mod303);
	public abstract String getInfo(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey);
	public abstract IMod303KeyDAO safeValueOf(Mod303 mod, String key);
	public abstract IMod303KeyDAO valueOf(String string);

	
}
