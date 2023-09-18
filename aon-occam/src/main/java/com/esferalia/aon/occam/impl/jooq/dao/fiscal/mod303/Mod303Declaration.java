package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public abstract class Mod303Declaration {
	
	private static final double ZERO = 0.0;
	private static final String EMTPY_JSON = "{messages : []}";
	// 1 de Julio del 2021		
	public static final Date IVA_2021_CHANGE_DATE =  Date.from(LocalDateTime.of(2021, 7, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());	

	private enum Declarations {
		 AEAT_2023 {
			@Override boolean accept(Mod303 mod) { return Mod303AEAT2023Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303AEAT2023Declaration();}
		}
		,ARABA_2023{
			@Override boolean accept(Mod303 mod) { return Mod303ARABA2023Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303ARABA2023Declaration();}
		}
		,BIZKAIA_2023 {
			@Override boolean accept(Mod303 mod) { return Mod303BIZKAIA2023Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303BIZKAIA2023Declaration();}
		}
		,GIPUZKOA_2023 {
			@Override boolean accept(Mod303 mod) { return Mod303GIPUZKOA2023Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303GIPUZKOA2023Declaration();}
		}
		,NAVARRA_2023{
			@Override boolean accept(Mod303 mod) { return Mod303NAVARRA2023Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303NAVARRA2023Declaration();}
		}
		 // Ejercicios Anteriores
		,AEAT_2022 {
			@Override boolean accept(Mod303 mod) { return Mod303AEAT2022Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303AEAT2022Declaration();}
		}
		,AEAT_2021_2{
			@Override boolean accept(Mod303 mod) { return Mod303AEAT20212Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303AEAT20212Declaration();}
		}
		,AEAT_2021 {
			@Override boolean accept(Mod303 mod) { return Mod303AEAT2021Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303AEAT2021Declaration();}
		}
		,AEAT_2020 {
			@Override boolean accept(Mod303 mod) { return Mod303AEAT2020Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303AEAT2020Declaration();}
		}
		,AEAT_2018 {
			@Override boolean accept(Mod303 mod) { return Mod303AEAT2018Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303AEAT2018Declaration();}
		}
		,AEAT_2017 {
			@Override boolean accept(Mod303 mod) { return Mod303AEAT2017Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303AEAT2017Declaration();}
		}
		,BIZKAIA_2022 {
			@Override boolean accept(Mod303 mod) { return Mod303BIZKAIA2022Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303BIZKAIA2022Declaration();}
		}
		,BIZKAIA_2018{
			@Override boolean accept(Mod303 mod) { return Mod303BIZKAIA2018Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303BIZKAIA2018Declaration();}
		}
		,BIZKAIA_2017 {
			@Override boolean accept(Mod303 mod) { return Mod303BIZKAIA2017Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303BIZKAIA2017Declaration();}
		}
		,ARABA_2022{
			@Override boolean accept(Mod303 mod) { return Mod303ARABA2022Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303ARABA2022Declaration();}
		}
		,ARABA_2019{
			@Override boolean accept(Mod303 mod) { return Mod303ARABA2019Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303ARABA2019Declaration();}
		}
		,ARABA_2017{
			@Override boolean accept(Mod303 mod) { return Mod303ARABA2017Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303ARABA2017Declaration();}
		}
		,GIPUZKOA_2022 {
			@Override boolean accept(Mod303 mod) { return Mod303GIPUZKOA2022Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303GIPUZKOA2022Declaration();}
		}
		,GIPUZKOA_2021_2{
			@Override boolean accept(Mod303 mod) { return Mod303GIPUZKOA20212Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303GIPUZKOA20212Declaration();}
		}
		,GIPUZKOA_2017{
			@Override boolean accept(Mod303 mod) { return Mod303GIPUZKOA2017Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303GIPUZKOA2017Declaration();}
		}
		,NAVARRA_2022{
			@Override boolean accept(Mod303 mod) { return Mod303NAVARRA2022Declaration.accept(mod);}
			@Override Mod303Declaration get() {return new Mod303NAVARRA2022Declaration();}
		}
		;
		abstract boolean accept(Mod303 mod);
		abstract Mod303Declaration get();
	}
	
	public static Mod303Declaration getInstance( Mod303 mod) {
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

	protected static void add(Mod303Key key,Mod303 mod,double amount) {
		FiscalModelDetail detail = mod.ensureDetail(key);
		detail.addAccumulatedAmount(amount);
		detail.addResultAmount( amount );	
		detail.addAmount( amount );
	}
	
	public static boolean mustApplyProrrate(Mod303 mod,VatContext vat) {
		return (mod.hasProrate()) && 
			(!mod.isSpecialProrate() || (mod.isSpecialProrate() && vat.getActivity() == null));
	}
	
	protected static void addProrrated(Mod303Key key,Mod303 mod,VatContext vat) {
		double amount = vat.getDeductibleQuota();
		if (mustApplyProrrate(mod,vat)) {
			mod.ensureDetail(Mod303Key.CM_072).addAmount(vat.getDeductibleQuota());
			amount = AonMathUtils.round(amount * mod.getProratePercent() / 100);
		} else {
			mod.ensureDetail(Mod303Key.CM_074).addAmount(vat.getDeductibleQuota());
		}
		add( key, mod, amount);
	}

	protected static void set(Mod303Key key,Mod303 mod,double amount) {
		mod.ensureDetail(key).setAccumulatedAmount(amount);
		mod.ensureDetail(key).setResultAmount( amount );	
		mod.ensureDetail(key).setAmount( amount );
	}

	protected static double getPendingCompesateAmounts(AONContext ctx, Mod303 mod) {
		// REDEFINE IN CHILD DECLARATIONS
		return ZERO;
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
				
				Mod303DAO.getPreviousEffectiveModels(ctx, mod303).forEach( fm -> {
					Mod303Declaration dec =  Mod303Declaration.getInstance(fm);
					MutableDouble sumProrratedMustDeclared = new MutableDouble();
					MutableDouble sumUnprorratedMustDeclared = new MutableDouble();
					VATDAO.getModelVatBreakdown(ctx, fm)
						.filter( br -> Arrays.stream(dec.getProrateKeys())
							.map(dec::getKey )
							.anyMatch(kd -> kd.acceptValue(fm, br)))
						.forEach(br -> {
							if (mustApplyProrrate(fm,br)) {
								sumProrratedMustDeclared.add(br.getDeductibleQuota());
							} else {
								sumUnprorratedMustDeclared.add(br.getDeductibleQuota());
							}
						});
					
					double declared = 0.0;
					if ( fm.getMap() != null && !fm.getMap().isEmpty()) {
						for(String key : fm.getMap().keySet() ) {
							if (isProrrated( Mod303Key.getKey(key))) {
								declared = declared + fm.getAmount(key); 
							}
						}
					}
					
					double prorratedAmount = AonMathUtils.round(sumProrratedMustDeclared.getValue());
					double unProrratedAmount = AonMathUtils.round(sumUnprorratedMustDeclared.getValue());
					double mustDeclared = AonMathUtils.round((prorratedAmount * lastPercent / 100) + unProrratedAmount);
					double difference  = AonMathUtils.round(mustDeclared - declared);
					mod303.ensureDetail(Mod303Key.CM_072).addAmount( prorratedAmount );
					mod303.ensureDetail(Mod303Key.CM_074).addAmount( unProrratedAmount );
					mod303.addAmount(getRegularizationKey(), difference);
				});
				
			}
		}
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
	
	public boolean isProrrated(Mod303Key key) {
		if ( key != null && getProrateKeys() != null ) {
			for (Mod303Key pk : getProrateKeys()) {
				if (pk == key) return true;			
			}
		}
		return false;
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
		if ( !mod303.isManualDeclaration() ) {
			initializePreviousData(ctx,mod303);
		} else {
			mod303.getMessages().clear();		
			mod303.setDiffCalculationMandatory(false);
			mod303.setDiffCalculationDisabled(true);
		}
		return mod303;
	}
	
	private void initializeComplementaryAndReplacement(AONContext ctx, Mod303 mod303) {
		mod303.setReplacedNumber(null);
		if ( mod303.isComplementaryDeclarationAvailable() || mod303.isReplacementDeclarationAvailable()) {
			Mod303 previous = Mod303DAO.getSamePeriodEffectiveModels(ctx, mod303).findFirst().orElse(null);
			if (previous != null) {
				if (mod303.isComplementary()) {
					mod303.setReplacement( false );
				} else if (mod303.isReplacement()) {
					mod303.setComplementary( false );
				} else {
					mod303.setComplementary( mod303.isComplementaryDeclarationAvailable() );
					mod303.setReplacement( mod303.isReplacementDeclarationAvailable() && !mod303.isComplementary() );
				}
				mod303.setReplacedNumber(previous.getNumber());
			} else {
				mod303.setComplementary( false );
				mod303.setReplacement( false );
			}
		}
	}
	
	private void initializePreviousData(AONContext ctx, Mod303 mod303) {
		mod303.getMessages().clear();		
		mod303.setDiffCalculationMandatory(false);
		mod303.setDiffCalculationDisabled(true);
		boolean resolved = false;
		if (!mod303.isFirstPeriod() && mod303.getYear() == 2022 ) { 
			List<Integer> ids = FiscalModelDAO.getPreviousModels(ctx, mod303, Mod303::new)
				.map(FiscalModel::getId)
				.collect(Collectors.toCollection(LinkedList::new));
			if (ids != null && !ids.isEmpty()) {
				boolean something = ctx.getDslContext()
					.select(ALCATRAZ.ID)
					.from(ALCATRAZ)
					.where(ALCATRAZ.FS_MODEL.in(ids))
					.fetch()
					.stream()
					.findFirst()
					.isPresent();
				if (!something) {
					Date start = AonDateUtils.getYearFirstDay(mod303.getYear());
					Date end = FiscalUtils.getPeriodEnd(mod303);
					boolean existsInvoices = ctx.getDslContext()
						.select(INVOICE.ID)
						.from(INVOICE)
						.where(INVOICE.DOMAIN.eq(mod303.getDomain()))
						.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(start), AonDateUtils.toSql(end)))
						.limit(1)
						.fetch()
						.stream()
						.findFirst()
						.isPresent();
					if (existsInvoices) {
						mod303.setDiffCalculationMandatory(true);
						mod303.setDiffCalculationDisabled(false);			
						mod303.addMessage("Se han encontrado declaraciones en el ejercicio, anteriores a la que se pretende crear."
								+ " El nuevo módulo de IVA vincula las facturas con las declaraciones, de tal forma que dichas facturas no se podrán modificar ni borrar."
								+ " Para el correcto funcionamiento, se calculará el modelo por diferencia "
								+ "y se vincularán todas las facturas, desde el inicio del ejercicio, al modelo que se está creando."
								);
						resolved = true;
					}
				} 
			}
		}
		if (!resolved) {
			mod303.setGenerateFromYearStartAvailable(!mod303.isFirstPeriod());
			if (mod303.isGenerateFromYearStartAvailable()) {
				Map<Integer, Long> invoices = checkPreviousInvoices(ctx, mod303);
				boolean existsInvoices = invoices != null && !invoices.isEmpty();
				if (existsInvoices) {
					mod303.addMessage("Se encontraron " + invoices.size() + " facturas no declaradas anteriores a la fecha "
							+ "de inicio de la declaraci\u00F3n.");
				}
				mod303.setGenerateFromYearStartAvailable(existsInvoices);
				mod303.setGenerateFromYearStart(existsInvoices);
			}
		}
	}
	
	private Map<Integer, Long>  checkPreviousInvoices(final AONContext ctx, final Mod303 mod303) {
		return VATDAO.getPreviousNotInModelVatBreakdown(ctx, mod303)
			.flatMap(vt -> Arrays.stream( getKeys() ).map( key -> new KeyedVatContext(key, vt)))
			.filter(kbr -> kbr.getKey().acceptValue(mod303,kbr.getVatContext()))
			.collect(Collectors.groupingBy(kbr -> kbr.getVatContext().getInvoice() 
					, Collectors.counting()));
	}
	
	protected boolean mustApplyReplacementSearch( Mod303 mod303 ) {
		return (mod303.isReplacement()
			|| (mod303.isComplementary() && getComplementaryBehaviour(mod303) == ComplementaryBeahaviour.REPLACEMENT)); 
	}
	
	protected Set<Alcatraz> createFromInvoices(AONContext ctx, Mod303 mod303) {
		final Set<Alcatraz> invoices = new HashSet<>();
		getVatContextStream(ctx, mod303)
			.flatMap(vt -> Arrays.stream( getKeys() ).map( key -> new KeyedVatContext(key, vt)))
			.filter(kbr -> kbr.getKey().acceptValue(mod303,kbr.getVatContext()))
			.map( kbr -> addInvoice(invoices, kbr))
			.forEach( kbr -> kbr.getKey().initialize(ctx, mod303, kbr.getVatContext()) );
		return invoices;
	}
	
	private Stream<VatContext> getVatContextStream(AONContext ctx, Mod303 mod303) {
		if (mod303.isDraft()) {
			return VATDAO.getVatBreakdown(ctx,mod303);
		}
		if (mustApplyReplacementSearch(mod303)) {
			mod303.setDiffCalculationMandatory(true);
			mod303.setDiffCalculationDisabled(false);
		}
		if (mod303.isDiffCalculationMandatory()) {
			mod303.setGenerateFromYearStart(true);
			return VATDAO.getVatBreakdown(ctx,mod303);
		} 
		return VATDAO.getNotInModelVatBreakdown(ctx,mod303);
	}

	private KeyedVatContext addInvoice( Set<Alcatraz> invoices, KeyedVatContext vt) {
		invoices.add( new Alcatraz()
			.setInvoice(vt.getVatContext().getInvoice())
			.setFinance(vt.getVatContext().getFinance())
			.setFinanceTracking(vt.getVatContext().getFinanceTracking()));
		;
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
	
	protected void resolveDiffCalculation(AONContext ctx, Mod303 mod303) {
		if (mod303.isDiffCalculationMandatory()) {
			Mod303DAO.getPreviousEffectiveModels(ctx, mod303)
				.flatMap(mod -> mod.getMap().values().stream())
				.filter( source -> Mod303Key.getKey(source.getType()) != null && Mod303Key.getKey(source.getType()).isDiffEnabled())
				.forEach(source -> {
					Mod303Key key = Mod303Key.getKey(source.getType());
					IMod303KeyDAO keyDAO = getKey(key);
					if (keyDAO != null) {
						FiscalModelDetail target = mod303.ensureDetail(key);
						target.setDeclaredAmount(AonMathUtils.round(target.getDeclaredAmount() + source.getAmount()));
					}
				});
			for (FiscalModelDetail detail : mod303.getMap().values()) {
				IMod303KeyDAO key = safeValueOf(mod303, detail.getType());
				if (key != null) {
					detail.setResultAmount( AonMathUtils.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));
					detail.setAmount( AonMathUtils.round(detail.getResultAmount() - detail.getAdjustAmount()));
				}
			}
		} else {
			for (FiscalModelDetail detail : mod303.getMap().values()) {
				detail.setAccumulatedAmount( AonMathUtils.round(detail.getAccumulatedAmount()));
				detail.setResultAmount( AonMathUtils.round(detail.getResultAmount()));
				detail.setAmount( AonMathUtils.round(detail.getAmount()));
			}
		}
	}

	public Set<Alcatraz> createOnTheFly(AONContext ctx, Mod303 mod303) {
		if (hasSimplifiedRegime()) {
			initializeSimplifiedRegime(ctx, mod303);
		}
		firstInitialization(ctx, mod303);
		if (!mod303.isManualDeclaration()) {
			Set<Alcatraz> invoices = createFromInvoices(ctx,mod303);
			invoices.addAll( createVatAccrualKeysFromInvoices(ctx,mod303) );
			resolveDiffCalculation(ctx, mod303);
			return invoices;
		}
		return new HashSet<>();
	}
	
	private void firstInitialization(AONContext ctx, Mod303 mod303) {
		for (IMod303KeyDAO key : getKeys()) {
			FiscalModelDetail detail = mod303.ensureDetail(key.getKey());
			detail.setExpression(key.getExpression());
			key.firstInitialize(ctx, mod303);
		}
	}

	public Mod303Key[] getCompensationExplainKeys() {
		return new Mod303Key[] {};
	}
	protected String getCompensationExplain(AONContext ctx, Mod303 mod303, Mod303Key key) {
		return EMTPY_JSON;
	}
	protected String getRegularizationExplain(AONContext ctx, Mod303 mod303, Mod303Key key) {
		return EMTPY_JSON;
	}
	public Mod303Key[] getSamePeriodExplainKeys() {
		return new Mod303Key[] {};
	}
	protected String getSamePeriodExplain(AONContext ctx, Mod303 mod303, Mod303Key key) {
		return EMTPY_JSON;
	}
	
	public abstract IMod303KeyDAO safeValueOf(Mod303 mod, String key);
	public abstract IMod303KeyDAO valueOf(String string);
	public abstract IMod303KeyDAO[] getKeys();
	public abstract Mod303Key[] getProrateKeys();
	
	public abstract boolean hasSimplifiedRegime();
	abstract void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303);
	
	abstract Mod303 initialize(AONContext ctx, Mod303 mod303);
	abstract double getResult(final Mod303 mod303);
	abstract ComplementaryBeahaviour getComplementaryBehaviour(final Mod303 mod303);
	abstract Set<Alcatraz> createVatAccrualKeysFromInvoices(AONContext ctx, Mod303 mod303);
	
}
