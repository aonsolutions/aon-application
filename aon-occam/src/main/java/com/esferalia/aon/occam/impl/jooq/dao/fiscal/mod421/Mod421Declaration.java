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
	// 1 de Julio del 2021		
//	public static final Date IVA_2021_CHANGE_DATE =  Date.from(LocalDateTime.of(2021, 7, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());	

	private enum Declarations {
		
// ATC ----------------------------------------------------------------------------------------		

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
	
//	public static boolean mustApplyProrrate(Mod421 mod,VatContext vat) {
//		return (mod.hasProrate()) && 
//			(!mod.isSpecialProrate() || (mod.isSpecialProrate() && vat.getActivity() == null));
//	}
	
//	protected static void addProrrated(Mod421Key key,Mod421 mod,VatContext vat) {
//		double amount = vat.getDeductibleQuota();
//		if (mustApplyProrrate(mod,vat)) {
//			mod.ensureDetail(Mod421Key.CM_072).addAmount(vat.getDeductibleQuota());
//			amount = AonMathUtils.round(amount * mod.getProratePercent() / 100);
//		} else {
//			mod.ensureDetail(Mod421Key.CM_074).addAmount(vat.getDeductibleQuota());
//		}
//		add( key, mod, amount);
//	}

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

	// Cálculo de la regularización de la prorrata en el último periodo
//	public void prorrateRegularization(AONContext ctx, Mod421 mod421){
//		if (mod421.isLastPeriod() && getRegularizationKey() != null) {
//			double lastPercent = mod421.getProratePercent();
//			double prevPercent = mod421.getPreviousProratePercent();
//			
//			// A partir de 2026, se añade un check para indicar expresamente si se debe aplicar prorrata, sea el porcentaje que sea.
//			// Por lo tanto, a partir de 2026 se comprueba unicamente si el modelo actual tiene prorrata y antes del 2026 se comprueba como se hacia antes
//			boolean calculate = mod421.getYear() >= 2026 ? 
//					mod421.hasProrate() : ((mod421.hasProrate() || mod421.hasPreviousProrate()) && AonNumberUtils.notEquals(lastPercent, prevPercent));
//			
//			if (calculate) {
//				Mod421DAO.getPreviousEffectiveModels(ctx, mod421).forEach( fm -> {
//					Mod421Declaration dec =  Mod421Declaration.getInstance(fm);
//					MutableDouble sumProrratedMustDeclared = new MutableDouble();
//					MutableDouble sumUnprorratedMustDeclared = new MutableDouble();
//					VATDAO.getModelVatBreakdown(ctx, fm)
//						.filter( br -> Arrays.stream(dec.getProrateKeys())
//							.map(dec::getKey )
//							.anyMatch(kd -> kd.acceptValue(fm, br)))
//						.forEach(br -> {
//							if (mustApplyProrrate(fm,br)) {
//								sumProrratedMustDeclared.add(br.getDeductibleQuota());
//							} else {
//								sumUnprorratedMustDeclared.add(br.getDeductibleQuota());
//							}
//						});
//					
//					double declared = 0.0;
//					if ( fm.getMap() != null && !fm.getMap().isEmpty()) {
//						for(String key : fm.getMap().keySet() ) {
//							if (isProrrated( Mod421Key.getKey(key))) {
//								declared = declared + fm.getAmount(key); 
//							}
//						}
//					}
//					
//					double prorratedAmount = AonMathUtils.round(sumProrratedMustDeclared.getValue());
//					double unProrratedAmount = AonMathUtils.round(sumUnprorratedMustDeclared.getValue());
//					double mustDeclared = AonMathUtils.round((prorratedAmount * lastPercent / 100) + unProrratedAmount);
//					double difference  = AonMathUtils.round(mustDeclared - declared);
//					mod421.ensureDetail(Mod421Key.CM_072).addAmount( prorratedAmount );
//					mod421.ensureDetail(Mod421Key.CM_074).addAmount( unProrratedAmount );
//					mod421.addAmount(getRegularizationKey(), difference);
//				});
//				
//			}
//		}
//	}

	public void fillSimplifiedRegime(Mod421 mod421){
	}
	public void populateSimplifiedRegime(Mod421 mod421){
	}
	public void specificInitialization(Mod421 mod421) {
	}
	public Mod421Key getRegularizationKey() {
		return null;
	}
	
//	public boolean isProrrated(Mod421Key key) {
//		if ( key != null && getProrateKeys() != null ) {
//			for (Mod421Key pk : getProrateKeys()) {
//				if (pk == key) return true;			
//			}
//		}
//		return false;
//	}

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
//		boolean resolved = false;
//		if (!mod421.isFirstPeriod() && mod421.getYear() == 2022 ) { 
//			List<Integer> ids = FiscalModelDAO.getPreviousModels(ctx, mod421, Mod421::new)
//				.map(FiscalModel::getId)
//				.collect(Collectors.toCollection(LinkedList::new));
//			if (ids != null && !ids.isEmpty()) {
//				boolean something = ctx.getDslContext()
//					.select(ALCATRAZ.ID)
//					.from(ALCATRAZ)
//					.where(ALCATRAZ.FS_MODEL.in(ids))
//					.fetch()
//					.stream()
//					.findFirst()
//					.isPresent();
//				if (!something) {
//					Date start = AonDateUtils.getYearFirstDay(mod421.getYear());
//					Date end = FiscalUtils.getPeriodEnd(mod421);
//					boolean existsInvoices = ctx.getDslContext()
//						.select(INVOICE.ID)
//						.from(INVOICE)
//						.where(INVOICE.DOMAIN.eq(mod421.getDomain()))
//						.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(start), AonDateUtils.toSql(end)))
//						.limit(1)
//						.fetch()
//						.stream()
//						.findFirst()
//						.isPresent();
//					if (existsInvoices) {
//						mod421.setDiffCalculationMandatory(true);
//						mod421.setDiffCalculationDisabled(false);			
//						mod421.addMessage("Se han encontrado declaraciones en el ejercicio, anteriores a la que se pretende crear."
//								+ " El nuevo módulo de IVA vincula las facturas con las declaraciones, de tal forma que dichas facturas no se podrán modificar ni borrar."
//								+ " Para el correcto funcionamiento, se calculará el modelo por diferencia "
//								+ "y se vincularán todas las facturas, desde el inicio del ejercicio, al modelo que se está creando."
//								);
//						resolved = true;
//					}
//				} 
//			}
//		}
//		if (!resolved) {
		// FALTA - HABRIA QUE VER SOLO LAS FACTURAS IMPUTADAS A CANARIAS
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
//		}
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
//		if (mod421.isDraft()) {
//			return VATDAO.getVatBreakdown(ctx,mod421);
//		}
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
					
					// ÑAPA! Debido al baile de casilla en el terce trimestre de 2024
//					if ( key == Mod421Key.CT_C167 && AonMathUtils.equals(1.75,source.getAmount())) {
//						source.setAmount(0.0);
//					}
					// ----------------
					
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
//		if (hasSimplifiedRegime()) {
			initializeSimplifiedRegime(ctx, mod421);
//		}
		firstInitialization(ctx, mod421);
		if (!mod421.isManualDeclaration()) {
			Set<Alcatraz> invoices = createFromInvoices(ctx,mod421);
//			invoices.addAll( createVatAccrualKeysFromInvoices(ctx,mod421) );
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
	protected String getRegularizationExplain(AONContext ctx, Mod421 mod421, Mod421Key key) {
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
//	public abstract Mod421Key[] getProrateKeys();
	
//	public abstract boolean hasSimplifiedRegime();
	abstract void initializeSimplifiedRegime(AONContext ctx, Mod421 mod421);
	
	abstract Mod421 initialize(AONContext ctx, Mod421 mod421);
	abstract double getResult(final Mod421 mod421);
	abstract ComplementaryBeahaviour getComplementaryBehaviour(final Mod421 mod421);
//	abstract Set<Alcatraz> createVatAccrualKeysFromInvoices(AONContext ctx, Mod421 mod421);
	
}
