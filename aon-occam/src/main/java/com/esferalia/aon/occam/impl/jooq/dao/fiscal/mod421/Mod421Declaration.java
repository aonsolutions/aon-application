package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod421;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;

public abstract class Mod421Declaration {
	
	private static final double ZERO = 0.0;
	private static final String EMTPY_JSON = "{messages : []}";

	private enum Declarations {
		
		 ATC_2026 {
			@Override boolean accept(Mod421 mod) { return Mod421ATC2026Declaration.accept(mod);}
			@Override Mod421Declaration get() {return new Mod421ATC2026Declaration();}
		}
		;
		
		abstract boolean accept(Mod421 mod);
		abstract Mod421Declaration get();
	}
	
	public static Mod421Declaration getInstance( Mod421 mod) {
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

	protected static void add(Mod421Key key,Mod421 mod,double amount) {
		FiscalModelDetail detail = mod.ensureDetail(key);
		detail.addAccumulatedAmount(amount);
		detail.addResultAmount( amount );	
		detail.addAmount( amount );
	}
	
	protected static void set(Mod421Key key,Mod421 mod,double amount) {
		mod.ensureDetail(key).setAccumulatedAmount(amount);
		mod.ensureDetail(key).setResultAmount( amount );	
		mod.ensureDetail(key).setAmount( amount );
	}

	protected static double getPendingCompesateAmounts(AONContext ctx, Mod421 mod) {
		// REDEFINE IN CHILD DECLARATIONS
		return ZERO;
	}	
	
	public IMod421KeyDAO getKey(Mod421Key key) {
		for (IMod421KeyDAO keyDAO : getKeys()) {
			if (keyDAO.getKey() == key) {
				return keyDAO;
			}
		}
		return null;
	}
	public void initialize(AONContext ctx, Mod421 mod421, VatContext vat) {
		for (IMod421KeyDAO key : getKeys()) {
			if (key.acceptValue(mod421,vat)) {
				key.initialize(ctx, mod421, vat);
			}
		}
	}

	public void fillSimplifiedRegime(Mod421 mod421){
	}
	public void populateSimplifiedRegime(Mod421 mod421){
	}
	public void specificInitialization(Mod421 mod421) {
	}
	
	private static class KeyedVatContext  {
		private IMod421KeyDAO key;
		private VatContext vt;
		private KeyedVatContext(IMod421KeyDAO key,VatContext vt) {
			this.key = key;
			this.vt = vt;
		}
		public IMod421KeyDAO getKey() {
			return key;
		}
		public VatContext getVatContext() {
			return vt;
		}
	}
	
	void ensureDetails(Mod421 mod421) {
		Arrays.stream( getKeys() )
			.forEach(key -> mod421.ensureDetail(key.getKey()).setExpression(key.getExpression()));
	}

	Mod421 initializeModel(AONContext ctx, Mod421 mod421) {
		initializeComplementaryAndReplacement(ctx,mod421);
		if ( !mod421.isManualDeclaration() ) {
			initializePreviousData(ctx,mod421);
		} else {
			mod421.getMessages().clear();		
			mod421.setDiffCalculationMandatory(false);
			mod421.setDiffCalculationDisabled(true);
		}
		return mod421;
	}
	
	private void initializeComplementaryAndReplacement(AONContext ctx, Mod421 mod421) {
		mod421.setReplacedNumber(null);
		if ( mod421.isComplementaryDeclarationAvailable() || mod421.isReplacementDeclarationAvailable()) {
			Mod421 previous = Mod421DAO.getSamePeriodEffectiveModels(ctx, mod421).findFirst().orElse(null);
			if (previous != null) {
				if (mod421.isComplementary()) {
					mod421.setReplacement( false );
				} else if (mod421.isReplacement()) {
					mod421.setComplementary( false );
				} else {
					mod421.setComplementary( mod421.isComplementaryDeclarationAvailable() );
					mod421.setReplacement( mod421.isReplacementDeclarationAvailable() && !mod421.isComplementary() );
				}
				mod421.setReplacedNumber(previous.getNumber());
			} else {
				mod421.setComplementary( false );
				mod421.setReplacement( false );
			}
		}
	}
	
	private void initializePreviousData(AONContext ctx, Mod421 mod421) {
		mod421.getMessages().clear();		
		mod421.setDiffCalculationMandatory(false);
		mod421.setDiffCalculationDisabled(true);

		mod421.setGenerateFromYearStartAvailable(!mod421.isFirstPeriod());
		if (mod421.isGenerateFromYearStartAvailable()) {
			Map<Integer, Long> invoices = checkPreviousInvoices(ctx, mod421);
			boolean existsInvoices = invoices != null && !invoices.isEmpty();
			if (existsInvoices) {
				mod421.addMessage("Se encontraron " + invoices.size() + " facturas no declaradas anteriores a la fecha "
						+ "de inicio de la declaraci\u00F3n.");
			}
			mod421.setGenerateFromYearStartAvailable(existsInvoices);
			mod421.setGenerateFromYearStart(existsInvoices);
		}
	}
	
	private Map<Integer, Long> checkPreviousInvoices(final AONContext ctx, final Mod421 mod421) {
		return VATDAO.getPreviousNotInModelVatBreakdown(ctx, mod421)
			.flatMap(vt -> Arrays.stream( getKeys() ).map( key -> new KeyedVatContext(key, vt)))
			.filter(kbr -> kbr.getKey().acceptValue(mod421,kbr.getVatContext()))
			.collect(Collectors.groupingBy(kbr -> kbr.getVatContext().getInvoice() 
					, Collectors.counting()));
	}
	
	protected boolean mustApplyReplacementSearch( Mod421 mod421 ) {
		return (mod421.isReplacement()
			|| (mod421.isComplementary() && getComplementaryBehaviour(mod421) == ComplementaryBeahaviour.REPLACEMENT)); 
	}
	
	protected Set<Alcatraz> createFromInvoices(AONContext ctx, Mod421 mod421) {
		final Set<Alcatraz> invoices = new HashSet<>();
		getVatContextStream(ctx, mod421)
			.flatMap(vt -> Arrays.stream( getKeys() ).map( key -> new KeyedVatContext(key, vt)))
			.filter(kbr -> kbr.getKey().acceptValue(mod421,kbr.getVatContext()))
			.map( kbr -> addInvoice(invoices, kbr))
			.forEach( kbr -> kbr.getKey().initialize(ctx, mod421, kbr.getVatContext()) );
		return invoices;
	}
	
	private Stream<VatContext> getVatContextStream(AONContext ctx, Mod421 mod421) {
		if (mustApplyReplacementSearch(mod421)) {
			mod421.setDiffCalculationMandatory(true);
			mod421.setDiffCalculationDisabled(false);
		}
		if (mod421.isDiffCalculationMandatory()) {
			mod421.setGenerateFromYearStart(true);
			return VATDAO.getVatBreakdown(ctx,mod421);
		} 
		return VATDAO.getNotInModelVatBreakdown(ctx,mod421);
	}

	private KeyedVatContext addInvoice( Set<Alcatraz> invoices, KeyedVatContext vt) {
		invoices.add( new Alcatraz()
			.setInvoice(vt.getVatContext().getInvoice())
			.setFinance(vt.getVatContext().getFinance())
			.setFinanceTracking(vt.getVatContext().getFinanceTracking()));
		;
		return vt;	
	}
	protected void initializeDeclarationType(Mod421 mod421) {
		if (AonMathUtils.isZero(mod421.getDeclarationResult() )) {
			mod421.setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
		} else if (AonMathUtils.isGreatherThanZero(mod421.getDeclarationResult())) {
			mod421.setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			mod421.setDeclarationResultType(mod421.isLastPeriod() ? FiscalModelDeclarationType.PAYBACK : FiscalModelDeclarationType.COMPENSATE);
		}
	}
	
	protected void resolveDiffCalculation(AONContext ctx, Mod421 mod421) {
		if (mod421.isDiffCalculationMandatory()) {
			Mod421DAO.getPreviousEffectiveModels(ctx, mod421)
				.flatMap(mod -> mod.getMap().values().stream())
				.filter( source -> Mod421Key.getKey(source.getType()) != null 
					&& Mod421Key.getKey(source.getType()).isDiffEnabled())
				.forEach(source -> {
					Mod421Key key = Mod421Key.getKey(source.getType());
					IMod421KeyDAO keyDAO = getKey(key);
					if (keyDAO != null) {
						FiscalModelDetail target = mod421.ensureDetail(key);
						target.setDeclaredAmount(AonMathUtils.round(target.getDeclaredAmount() + source.getAmount()));
					}
				});
			for (FiscalModelDetail detail : mod421.getMap().values()) {
				IMod421KeyDAO key = safeValueOf(mod421, detail.getType());
				if (key != null) {
					detail.setResultAmount( AonMathUtils.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));
					detail.setAmount( AonMathUtils.round(detail.getResultAmount() - detail.getAdjustAmount()));
				}
			}
		} else {
			for (FiscalModelDetail detail : mod421.getMap().values()) {
				detail.setAccumulatedAmount( AonMathUtils.round(detail.getAccumulatedAmount()));
				detail.setResultAmount( AonMathUtils.round(detail.getResultAmount()));
				detail.setAmount( AonMathUtils.round(detail.getAmount()));
			}
		}
	}

	public Set<Alcatraz> createOnTheFly(AONContext ctx, Mod421 mod421) {
		initializeSimplifiedRegime(ctx, mod421);
		firstInitialization(ctx, mod421);
		if (!mod421.isManualDeclaration()) {
			Set<Alcatraz> invoices = createFromInvoices(ctx,mod421);
			resolveDiffCalculation(ctx, mod421);
			return invoices;
		}
		return new HashSet<>();
	}
	
	private void firstInitialization(AONContext ctx, Mod421 mod421) {
		for (IMod421KeyDAO key : getKeys()) {
			FiscalModelDetail detail = mod421.ensureDetail(key.getKey());
			detail.setExpression(key.getExpression());
			key.firstInitialize(ctx, mod421);
		}
	}

	public Mod421Key[] getCompensationExplainKeys() {
		return new Mod421Key[] {};
	}
	protected String getCompensationExplain(AONContext ctx, Mod421 mod421, Mod421Key key) {
		return EMTPY_JSON;
	}
	
	public Mod421Key[] getSamePeriodExplainKeys() {
		return new Mod421Key[] {};
	}
	protected String getSamePeriodExplain(AONContext ctx, Mod421 mod421, Mod421Key key) {
		return EMTPY_JSON;
	}
	
	public abstract IMod421KeyDAO safeValueOf(Mod421 mod, String key);
	public abstract IMod421KeyDAO valueOf(String string);
	public abstract IMod421KeyDAO[] getKeys();
	
	abstract void initializeSimplifiedRegime(AONContext ctx, Mod421 mod421);
	
	abstract Mod421 initialize(AONContext ctx, Mod421 mod421);
	abstract double getResult(final Mod421 mod421);
	abstract ComplementaryBeahaviour getComplementaryBehaviour(final Mod421 mod421);
	
	public Mod421Key[] getIngresoCuentaAnteriorExplainKeys() {
		return new Mod421Key[] {};
	}
	protected String getIngresoCuentaAnteriorExplain(AONContext ctx, Mod421 mod421, Mod421Key key) {
		return EMTPY_JSON;
	}
	
}
