package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.error.AonCoreException;

public abstract class Mod390HFDeclaration {
	
	public static Mod390HFDeclaration getInstance( Mod390HF mod) {
		
		if (BIZKAIA_2017_Declaration.accept(mod)) 	return new BIZKAIA_2017_Declaration();
		
		throw new AonCoreException("No existe una declaración para el modelo solicitado");
	}

	protected static void add(Mod390Key key,Mod390HF mod,double amount) {
		mod.ensureDetail(key).addAmount(amount);
	}
	
	public IMod390KeyDAO getKey(Mod390Key key) {
		for (IMod390KeyDAO keyDAO : getKeys()) {
			if (keyDAO.getKey() == key) {
				return keyDAO;
			}
		}
		return null;
	}
	public void initialize(AONContext ctx, Mod390HF mod, VatContext vat) {
		for (IMod390KeyDAO key : getKeys()) {
			if (key.acceptValue(mod,vat)) {
				key.initialize(ctx, mod, vat);
			}
		}
	}

	public void specificInitialization(Mod390HF mod) {}
	public abstract IMod390KeyDAO safeValueOf(Mod390HF mod, String key);
	public abstract IMod390KeyDAO valueOf(String string);
	public abstract IMod390KeyDAO[] getKeys();
	public abstract Mod390Key[] getProrateKeys();


}
