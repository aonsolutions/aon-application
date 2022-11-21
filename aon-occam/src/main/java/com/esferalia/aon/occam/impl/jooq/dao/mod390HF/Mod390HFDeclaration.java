package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;

public abstract class Mod390HFDeclaration {

	static final double PERCENT_21 = 21.0;
	static final double PERCENT_10 = 10.0;
	static final double PERCENT_4 = 4.0;
	static final double PERCENT_105 = 10.5;	
	static final double PERCENT_12 = 12;
	static final double SURCHARGE_PERCENT_52 = 5.2;
	static final double SURCHARGE_PERCENT_14 = 1.4;
	static final double SURCHARGE_PERCENT_05 = 0.5;
	static final double SURCHARGE_PERCENT_175 = 1.75;
	
	private enum Declarations {
		BIZKAIA_2022 {
			@Override boolean accept(Mod390HF mod) { return Mod390HFBizkaia2022Declaration.accept(mod);}
			@Override Mod390HFDeclaration get() {return new Mod390HFBizkaia2022Declaration();}
		},
		BIZKAIA_2018 {
			@Override boolean accept(Mod390HF mod) { return Mod390HFBizkaia2018Declaration.accept(mod);}
			@Override Mod390HFDeclaration get() {return new Mod390HFBizkaia2018Declaration();}
		},
		BIZKAIA_2017 {
			@Override boolean accept(Mod390HF mod) { return Mod390HFBizkaia2017Declaration.accept(mod);}
			@Override Mod390HFDeclaration get() {return new Mod390HFBizkaia2017Declaration();}
		},
		GIPUZKOA_2022 {
			@Override boolean accept(Mod390HF mod) { return Mod390HFGipuzkoa2022Declaration.accept(mod);}
			@Override Mod390HFDeclaration get() {return new Mod390HFGipuzkoa2022Declaration();}
		},
		GIPUZKOA_2021 {
			@Override boolean accept(Mod390HF mod) { return Mod390HFGipuzkoa2021Declaration.accept(mod);}
			@Override Mod390HFDeclaration get() {return new Mod390HFGipuzkoa2021Declaration();}
		},
		GIPUZKOA_2017 {
			@Override boolean accept(Mod390HF mod) { return Mod390HFGipuzkoa2017Declaration.accept(mod);}
			@Override Mod390HFDeclaration get() {return new Mod390HFGipuzkoa2017Declaration();}
		},
		ARABA_2022 {
			@Override boolean accept(Mod390HF mod) { return Mod390HFAraba2022Declaration.accept(mod);}
			@Override Mod390HFDeclaration get() {return new Mod390HFAraba2022Declaration();}
		},
		ARABA_2021 {
			@Override boolean accept(Mod390HF mod) { return Mod390HFAraba2021Declaration.accept(mod);}
			@Override Mod390HFDeclaration get() {return new Mod390HFAraba2021Declaration();}
		},
		ARABA_2017 {
			@Override boolean accept(Mod390HF mod) { return Mod390HFAraba2017Declaration.accept(mod);}
			@Override Mod390HFDeclaration get() {return new Mod390HFAraba2017Declaration();}
		}
		;
		
		abstract boolean accept(Mod390HF mod);
		abstract Mod390HFDeclaration get();
	}
	
	private static class KeyedVatContext  {
		private IMod390KeyDAO key;
		private VatContext vt;
		private KeyedVatContext(IMod390KeyDAO key,VatContext vt) {
			this.key = key;
			this.vt = vt;
		}
		public IMod390KeyDAO getKey() {
			return key;
		}
		public VatContext getVatContext() {
			return vt;
		}
	}

	public static Mod390HFDeclaration getInstance( Mod390HF mod) {
		if (mod.getPeriod() == null) {
			throw new AonCoreException("No se ha indicado periodo para la declaración");	
		}
		return Arrays.stream(Declarations.values())
			.filter(dec -> dec.accept(mod))
			.map(Declarations::get)
			.findFirst()
			.orElseThrow( () -> new AonCoreException(MessageFormat.format(
				"No existe una declaración para el modelo solicitado ({0} - {1} - {2})",
				mod.getAdministration().getDescription()
				,mod.getYear()
				,mod.getPeriod().getDescription())));
		
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

	protected void initializeDeclarationType(Mod390HF mod) {
		if (AonMathUtils.isZero(mod.getDeclarationResult() )) {
			mod.setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
		} else if (AonMathUtils.isGreatherThanZero(mod.getDeclarationResult())) {
			mod.setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			mod.setDeclarationResultType( FiscalModelDeclarationType.PAYBACK );
		}
	}
	
	protected Set<Integer> createOnTheFly(AONContext ctx, Mod390HF mod) {
		firstInitialization(ctx, mod);
		if (!mod.isManualDeclaration()) {
			Set<Integer> invoices = createFromInvoices(ctx,mod);
			Set<Integer> invoices2 = createVatAccrualKeysFromInvoices(ctx,mod);
			if (invoices2 != null) {
				invoices.addAll( invoices2 );
			}
			return invoices;
		}
		return new HashSet<>();
	}

	private void firstInitialization(AONContext ctx, Mod390HF mod) {
		for (IMod390KeyDAO key : getKeys()) {
			FiscalModelDetail detail = mod.ensureDetail(key.getKey());
			detail.setExpression(key.getExpression());
			key.firstInitialize(ctx, mod);
		}
	}
	
	void ensureDetails(Mod390HF mod) {
		Arrays.stream( getKeys() )
			.forEach(key -> mod.ensureDetail(key.getKey()).setExpression(key.getExpression()));
	}

	Mod390HF initializeModel(AONContext ctx, Mod390HF mod) {
		initializeComplementaryAndReplacement(ctx,mod);
		mod.getMessages().clear();		
		mod.setDiffCalculationDisabled(true);
		return mod;
	}
	
	private void initializeComplementaryAndReplacement(AONContext ctx, Mod390HF mod) {
		mod.setReplacedNumber(null);
		if ( mod.isComplementaryDeclarationAvailable() || mod.isReplacementDeclarationAvailable()) {
			Mod390HF previous = Mod390HFDAO.getSamePeriodFiscalModels(ctx, mod).findFirst().orElse(null);
			if (previous != null) {
				mod.setComplementary( mod.isComplementaryDeclarationAvailable() );
				mod.setReplacement( mod.isReplacementDeclarationAvailable() 
					&& !mod.isComplementary() );
				mod.setReplacedNumber(previous.getNumber());
			} else {
				mod.setComplementary( false );
				mod.setReplacement( false );
			}
		}
	}
	
	
	protected Set<Integer> createFromInvoices(AONContext ctx, Mod390HF mod) {
		final Set<Integer> invoices = new HashSet<>();
		VATDAO.getVatBreakdown(ctx,mod)
			.flatMap(vt -> Arrays.stream( getKeys() ).map( key -> new KeyedVatContext(key, vt)))
			.filter(kbr -> kbr.getKey().acceptValue(mod,kbr.getVatContext()))
			.map( kbr -> addInvoice(invoices, kbr))
			.forEach( kbr -> kbr.getKey().initialize(ctx, mod, kbr.getVatContext()) );
		return invoices;
	}
	private KeyedVatContext addInvoice( Set<Integer> invoices, KeyedVatContext vt) {
		invoices.add(vt.getVatContext().getInvoice());
		return vt;	
	}
	
	static boolean hasPercent4(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_4;	
	}
	static boolean hasPercent10(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_10; 	
	}
	static boolean hasPercent21(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_21; 	
	}
	static boolean hasSurchargePercent05(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_05; 
	}
	static boolean hasSurchargePercent175(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_175; 
	}
	static boolean hasSurchargePercent14(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_14;
	}
	static boolean hasSurchargePercent52(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_52;
	}

	public static boolean mustApplyProrrate(Mod390HF mod,VatContext vat) {
		return (mod.hasProrate()) && 
			(!mod.isSpecialProrate() || (mod.isSpecialProrate() && vat.getActivity() == null));
	}
	protected boolean isProrrated(Mod390Key key) {
		if ( key != null && getProratedKeys() != null ) {
			for (Mod390Key pk : getProratedKeys()) {
				if (pk == key) return true;			
			}
		}
		return false;
	}

	void specificInitialization(AONContext ctx, Mod390HF mod) {}
	abstract IMod390KeyDAO safeValueOf(Mod390HF mod, String key);
	abstract IMod390KeyDAO valueOf(String string);
	abstract IMod390KeyDAO[] getKeys();
	abstract Mod390Key[] getProratedKeys();
	abstract Set<Integer> createVatAccrualKeysFromInvoices(AONContext ctx, Mod390HF mod);
	abstract double getResult(final Mod390HF mod);
	abstract Mod390HF initialize(AONContext ctx, Mod390HF mod303);
}
