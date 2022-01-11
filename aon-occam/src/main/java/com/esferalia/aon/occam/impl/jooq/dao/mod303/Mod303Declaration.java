package com.esferalia.aon.occam.impl.jooq.dao.mod303;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.Mod303DAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public abstract class Mod303Declaration {
	
	public static Mod303Declaration getInstance( Mod303 mod) {
		if (mod.getPeriod() == null) {
			throw new AonCoreException("No se ha indicado periodo para la declaración");	
		}
		if (AEAT_2021_2_Declaration.accept(mod)) 		return new AEAT_2021_2_Declaration();
		if (AEAT_2021_Declaration.accept(mod)) 		return new AEAT_2021_Declaration();
		if (AEAT_2020_Declaration.accept(mod)) 		return new AEAT_2020_Declaration();
		if (AEAT_2018_Declaration.accept(mod)) 		return new AEAT_2018_Declaration();
		if (AEAT_2017_Declaration.accept(mod)) 		return new AEAT_2017_Declaration();
		if (BIZKAIA_2018_Declaration.accept(mod)) 	return new BIZKAIA_2018_Declaration();
		if (BIZKAIA_2017_Declaration.accept(mod)) 	return new BIZKAIA_2017_Declaration();
		if (ARABA_2019_Declaration.accept(mod)) 	return new ARABA_2019_Declaration();
		if (ARABA_2017_Declaration.accept(mod)) 	return new ARABA_2017_Declaration();
		if (GIPUZKOA_2021_2_Declaration.accept(mod)) 	return new GIPUZKOA_2021_2_Declaration();
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
	
	private static boolean mustApplyProrrate(Mod303 mod,VatContext vat) {
		return (mod.hasProrate()) && (!mod.isSpecialProrate() || (mod.isSpecialProrate() && vat.getActivity() == null));
	}
	private static double getProrratePercent(Mod303 mod,VatContext vat) {
		if (mod.isLastPeriod()) {
			return (vat.isInsidePeriod())?mod.getProratePercent():mod.getPreviousProratePercent();
		} else {
			return mod.getProratePercent();
		}
	}
	
	protected static void addProrrated(Mod303Key key,Mod303 mod,VatContext vat) {
		double amount = vat.getDeductibleQuota();
		if (mustApplyProrrate(mod,vat)) {
			if ( mod.isLastPeriod() && !vat.isInsidePeriod()) {
				mod.ensureDetail(Mod303Key.CM_072).addAmount(amount);
			}
			amount = AonMathUtils.round(amount * getProrratePercent(mod, vat) / 100);	
		}
		add( key, mod, amount);
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

	public void prorrateRegularization(AONContext ctx, Mod303 mod303){
		if (mod303.isLastPeriod() && getRegularizationKey() != null) {
			double lastPercent = mod303.getProratePercent();
			double prevPercent = mod303.getPreviousProratePercent();
			if (mod303.hasProrate() && AonNumberUtils.notEquals(lastPercent, prevPercent)) {
				
				if (mod303.isDiffCalculationDisabled()) {
					final Mod303 dupl = new Mod303();
					dupl.setDomain(mod303.getDomain());
					dupl.setDomainName(mod303.getDomainName());
					dupl.setYear(mod303.getYear());
					dupl.setModel(mod303.getModel());
					dupl.setPeriod(mod303.getPeriod());
					dupl.setAdministration(mod303.getAdministration());
					dupl.setDiffCalculationDisabled(false);
					dupl.ensureDetail( dupl.getProrateKey() ).setAmount( mod303.getProratePercent() );
					dupl.ensureDetail( dupl.getPreviousProrateKey() ).setAmount( mod303.getPreviousProratePercent() );
					Mod303DAO.create(ctx, dupl);
					FiscalModelDetail c72 = dupl.ensureDetail(Mod303Key.CM_072);
					double amount = dupl.ensureDetail(Mod303Key.CM_072).getAmount();
					mod303.ensureDetail(Mod303Key.CM_072).setAmount( amount );
				}
			
			
				double amount = mod303.ensureDetail(Mod303Key.CM_072).getAmount();
				double declared = AonMathUtils.round(amount * prevPercent / 100);
				double mustDeclared = AonMathUtils.round(amount * lastPercent / 100);
				mod303.putAmount(getRegularizationKey(), AonMathUtils.round(declared - mustDeclared));
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
	public Mod303Key getRegularizationKey() {
		return null;
	};

	public abstract IMod303KeyDAO safeValueOf(Mod303 mod, String key);
	public abstract IMod303KeyDAO valueOf(String string);
	public abstract IMod303KeyDAO[] getKeys();
	public abstract Mod303Key[] getProrateKeys();
	public abstract boolean hasSimplifiedRegime();


}
