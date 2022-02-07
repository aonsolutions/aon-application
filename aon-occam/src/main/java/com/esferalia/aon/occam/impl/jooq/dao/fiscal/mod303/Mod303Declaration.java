package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public abstract class Mod303Declaration {
	
	// 1 de Julio del 2021		
	protected static final Date IVA_2021_CHANGE_DATE =  Date.from(LocalDateTime.of(2021, 7, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());	

	protected static Mod303Declaration getInstance( Mod303 mod) {
		if (mod.getPeriod() == null) {
			throw new AonCoreException("No se ha indicado periodo para la declaración");	
		}
		if (Mod303AEAT20212Declaration.accept(mod)) 		return new Mod303AEAT20212Declaration();
		if (Mod303AEAT2021Declaration.accept(mod)) 		return new Mod303AEAT2021Declaration();
		if (Mod303AEAT2020Declaration.accept(mod)) 		return new Mod303AEAT2020Declaration();
		if (Mod303AEAT2018.accept(mod)) 		return new Mod303AEAT2018();
		if (Mod303AEAT2017Declaration.accept(mod)) 		return new Mod303AEAT2017Declaration();
		if (ModBIZKAIA2022Declaration.accept(mod)) 	return new ModBIZKAIA2022Declaration();
		if (ModBIZKAIA2018Declaration.accept(mod)) 	return new ModBIZKAIA2018Declaration();
		if (ModBIZKAIA2017Declaration.accept(mod)) 	return new ModBIZKAIA2017Declaration();
		if (Mod303ARABA2022Declaration.accept(mod)) 	return new Mod303ARABA2022Declaration();
		if (Mod303ARABA2019Declaration.accept(mod)) 	return new Mod303ARABA2019Declaration();
		if (Mod303ARABA2017Declaration.accept(mod)) 	return new Mod303ARABA2017Declaration();
		if (ModGIPUZKOA2022Declaration.accept(mod)) 	return new ModGIPUZKOA2022Declaration();
		if (ModGIPUZKOA20212Declaration.accept(mod)) 	return new ModGIPUZKOA20212Declaration();
		if (ModGIPUZKOA2017Declaration.accept(mod)) 	return new ModGIPUZKOA2017Declaration();
		
		throw new AonCoreException(MessageFormat.format(
			"No existe una declaración para el modelo solicitado ({0} - {1} - {2})",
			mod.getAdministration().getDescription()
			,mod.getYear()
			,mod.getPeriod().getDescription()));
		
	}

	protected static void add(Mod303Key key,Mod303 mod,double amount) {
		if (key.isDiffEnabled()) {
			mod.ensureDetail(key).addAccumulatedAmount(amount);	
		} else {
			mod.ensureDetail(key).addAmount(amount);
		}
	}
	
	private static boolean mustApplyProrrate(Mod303 mod,VatContext vat) {
		return (mod.hasProrate()) && 
			(!mod.isSpecialProrate() || (mod.isSpecialProrate() && vat.getActivity() == null));
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
			if ((mod303.hasProrate() || mod303.hasPreviousProrate()) 
				&& AonNumberUtils.notEquals(lastPercent, prevPercent)) {
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
					double amount = dupl.ensureDetail(Mod303Key.CM_072).getAmount();
					mod303.ensureDetail(Mod303Key.CM_072).setAmount( amount );
				}
			
			
				double amount = mod303.ensureDetail(Mod303Key.CM_072).getAmount();
				double declared = AonMathUtils.round(amount * prevPercent / 100);
				double mustDeclared = AonMathUtils.round(amount * lastPercent / 100);
				mod303.putAmount(getRegularizationKey(), AonMathUtils.round(mustDeclared - declared));
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
	}
	
	protected boolean isProrrated(Mod303Key key) {
		if ( key != null && getProrateKeys() != null ) {
			for (Mod303Key pk : getProrateKeys()) {
				if (pk == key) return true;			
			}
		}
		return false;
	}

	public abstract IMod303KeyDAO safeValueOf(Mod303 mod, String key);
	public abstract IMod303KeyDAO valueOf(String string);
	public abstract IMod303KeyDAO[] getKeys();
	protected abstract Mod303Key[] getProrateKeys();
	public abstract boolean hasSimplifiedRegime();


}
