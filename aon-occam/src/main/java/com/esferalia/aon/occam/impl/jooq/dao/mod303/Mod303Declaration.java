package com.esferalia.aon.occam.impl.jooq.dao.mod303;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.error.AonCoreException;

public abstract class Mod303Declaration {
	
	public static Mod303Declaration getInstance( Mod303 mod) {
		
		if (AEAT_2018_Declaration.accept(mod)) 		return new AEAT_2018_Declaration();
		if (AEAT_2017_Declaration.accept(mod)) 		return new AEAT_2017_Declaration();
		if (BIZKAIA_2018_Declaration.accept(mod)) 	return new BIZKAIA_2018_Declaration();
		if (BIZKAIA_2017_Declaration.accept(mod)) 	return new BIZKAIA_2017_Declaration();
		if (ARABA_2019_Declaration.accept(mod)) 	return new ARABA_2019_Declaration();
		if (ARABA_2017_Declaration.accept(mod)) 	return new ARABA_2017_Declaration();
		if (GIPUZKOA_2017_Declaration.accept(mod)) 	return new GIPUZKOA_2017_Declaration();
		
		throw new AonCoreException("No existe una declaración para el modelo solicitado");
	}

	protected static void add(Mod303Key key,Mod303 mod,double amount) {
		if (key.isDiffEnabled()) {
			mod.ensureDetail(key).addAccumulatedAmount(amount);	
		} else {
			mod.ensureDetail(key).addAmount(amount);
		}
	}
	
	protected static void set(Mod303Key key,Mod303 mod,double amount) {
		if (key.isDiffEnabled()) {
			mod.ensureDetail(key).setAccumulatedAmount(amount);	
		} else {
			mod.ensureDetail(key).setAmount(amount);
		}
	}

	public IMod303KeyDAO getKey(Mod303Key key) {
		for (IMod303KeyDAO keyDAO : getKeys()) {
			if (keyDAO.getKey() == key) {
				return keyDAO;
			}
		}
		return null;
	}
	public void initialize(AONContext ctx, Mod303 mod303, VatContext vat) {
		for (IMod303KeyDAO key : getKeys()) {
			if (key.acceptValue(mod303,vat)) {
				key.initialize(ctx, mod303, vat);
			}
		}
	}

	public void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303){
	}
	public void fillSimplifiedRegime(Mod303 mod303){
		
	}
	public void populateSimplifiedRegime(Mod303 mod303){
	}
	public void specificInitialization(Mod303 mod303) {
	}

	public abstract IMod303KeyDAO safeValueOf(Mod303 mod, String key);
	public abstract IMod303KeyDAO valueOf(String string);
	public abstract IMod303KeyDAO[] getKeys();
	public abstract Mod303Key[] getProrateKeys();
	public abstract boolean hasSimplifiedRegime();


}
