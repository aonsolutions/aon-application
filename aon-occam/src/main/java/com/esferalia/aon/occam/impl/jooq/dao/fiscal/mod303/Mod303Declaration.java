package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;

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
		mod.ensureDetail(key).addAccumulatedAmount(amount);	
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
		mod.ensureDetail(key).setAccumulatedAmount(amount);	
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
//		if (mod303.isLastPeriod() && getRegularizationKey() != null) {
//			double lastPercent = mod303.getProratePercent();
//			double prevPercent = mod303.getPreviousProratePercent();
//			if ((mod303.hasProrate() || mod303.hasPreviousProrate()) 
//				&& AonNumberUtils.notEquals(lastPercent, prevPercent)) {
//				if (mod303.isDiffCalculationDisabled()) {
//					final Mod303 dupl = new Mod303();
//					dupl.setDomain(mod303.getDomain());
//					dupl.setDomainName(mod303.getDomainName());
//					dupl.setYear(mod303.getYear());
//					dupl.setModel(mod303.getModel());
//					dupl.setPeriod(mod303.getPeriod());
//					dupl.setAdministration(mod303.getAdministration());
//					dupl.setDiffCalculationDisabled(false);
//					dupl.ensureDetail( dupl.getProrateKey() ).setAmount( mod303.getProratePercent() );
//					dupl.ensureDetail( dupl.getPreviousProrateKey() ).setAmount( mod303.getPreviousProratePercent() );
//					PrevMod303DAO.create(ctx, dupl);
//					double amount = dupl.ensureDetail(Mod303Key.CM_072).getAmount();
//					mod303.ensureDetail(Mod303Key.CM_072).setAmount( amount );
//				}
//			
//			
//				double amount = mod303.ensureDetail(Mod303Key.CM_072).getAmount();
//				double declared = AonMathUtils.round(amount * prevPercent / 100);
//				double mustDeclared = AonMathUtils.round(amount * lastPercent / 100);
//				mod303.putAmount(getRegularizationKey(), AonMathUtils.round(mustDeclared - declared));
//			}
//		}
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

	
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	enum ComplementaryBeahaviour {
		COMPLEMENTARY,
		REPLACEMENT;
	}

	private static class KeyedVatContext  {
		private IMod303KeyDAO key;
		private VatContext vt;
		private KeyedVatContext(IMod303KeyDAO key,VatContext vt) {
			this.key = key;
			this.vt = vt;
		}
		public IMod303KeyDAO getKey() {
			return key;
		}
		public VatContext getVatContext() {
			return vt;
		}
	}
	
	void ensureDetails(Mod303 mod303) {
		Arrays.stream( getKeys() )
			.forEach(key -> mod303.ensureDetail(key.getKey()).setExpression(key.getExpression()));
	}

	Mod303 initializeModel(AONContext ctx, Mod303 mod303) {
		initializeComplementaryAndReplacement(ctx,mod303);
		initializePreviousData(ctx,mod303);
		return mod303;
	}
	
	private void initializeComplementaryAndReplacement(AONContext ctx, Mod303 mod303) {
		mod303.setReplacedNumber(null);
		if ( mod303.isComplementaryDeclarationAvailable() || mod303.isReplacementDeclarationAvailable()) {
			Mod303 previous = Mod303DAO.getSamePeriodFiscalModels(ctx, mod303).findFirst().orElse(null);
			if (previous != null) {
				mod303.setComplementary( mod303.isComplementaryDeclarationAvailable() );
				mod303.setReplacement( mod303.isReplacementDeclarationAvailable() 
					&& !mod303.isComplementary() );
				mod303.setReplacedNumber(previous.getNumber());
			} else {
				mod303.setComplementary( false );
				mod303.setReplacement( false );
			}
		}
	}
	
	private void initializePreviousData(AONContext ctx, Mod303 mod303) {
		mod303.getMessages().clear();		
		mod303.setGenerateFromYearStartAvailable(!mod303.isFirstPeriod());
		if (mod303.isGenerateFromYearStartAvailable()) {
			Map<Integer, Long> invoices = checkPreviousInvoices(ctx, mod303);
			boolean existsInvoices = invoices != null && !invoices.isEmpty();
			if (existsInvoices) {
				mod303.addMessage("Se encontraron " + invoices.size() + " facturas no declaradas anteriores a la fecha "
						+ "de inicio de la declaraci\u00F3n.");
			}
			mod303.setGenerateFromYearStartAvailable(existsInvoices);
		}
	}
	
	private Map<Integer, Long>  checkPreviousInvoices(final AONContext ctx, final Mod303 mod303) {
		return VATDAO.getPreviousNotInModelVatBreakdown(ctx, mod303)
			.flatMap(vt -> Arrays.stream( getKeys() ).map( key -> new KeyedVatContext(key, vt)))
			.filter(kbr -> kbr.getKey().acceptValue(mod303,kbr.getVatContext()))
			.collect(Collectors.groupingBy(kbr -> kbr.getVatContext().getInvoice() 
					, Collectors.counting()));
	}
	
	protected Set<Integer> createFromInvoices(AONContext ctx, Mod303 mod303) {
		final Set<Integer> invoices = new HashSet<>();
		Stream<VatContext> stream = null;
		System.out.println( getComplementaryBehaviour(mod303) ); 
		if (getComplementaryBehaviour(mod303) == ComplementaryBeahaviour.REPLACEMENT) {
			stream =  VATDAO.getVatBreakdown(ctx,mod303);
		} else {
			stream = VATDAO.getNotInModelVatBreakdown(ctx,mod303);
		}
		stream
			.flatMap(vt -> Arrays.stream( getKeys() ).map( key -> new KeyedVatContext(key, vt)))
			.filter(kbr -> kbr.getKey().acceptValue(mod303,kbr.getVatContext()))
			.map( kbr -> addInvoice(invoices, kbr))
			.forEach( kbr -> kbr.getKey().initialize(ctx, mod303, kbr.getVatContext()) );
		return invoices;
	}
	
	private KeyedVatContext addInvoice( Set<Integer> invoices, KeyedVatContext vt) {
		invoices.add(vt.getVatContext().getInvoice());
		return vt;	
	}
	protected void initializeDeclarationType(Mod303 mod303) {
		if (AonMathUtils.isZero(mod303.getDeclarationResult() )) {
			mod303.setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
		} else if (AonMathUtils.isGreatherThanZero(mod303.getDeclarationResult())) {
			mod303.setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			mod303.setDeclarationResultType(
				(mod303.isEnrolledInDevolutionRegistry() || mod303.isLastPeriod()) 
					?FiscalModelDeclarationType.PAYBACK
					:FiscalModelDeclarationType.COMPENSATE
							);
		}
	}

	abstract Mod303 initialize(AONContext ctx, Mod303 mod303);
	abstract double getResult(final Mod303 mod303);
	abstract ComplementaryBeahaviour getComplementaryBehaviour(final Mod303 mod303);
	abstract void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303);

	
	
}
