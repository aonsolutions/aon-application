package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.text.DecimalFormat;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public abstract class Mod303Declaration {
	private static final double ZERO = 0.0;
	static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");
	static final String styledTag = "<{0} style = \"{1}\">"; 
	static final String marginTop = "margin-top: 20px;";
	static final String border = "border: solid gray 0.5px; padding: 2px 5px;";
	static final String paddingLeft = "padding-left: 15px;";
	static final String noWrap = "white-space: nowrap;";
	static final String fontLarger = "font-size: 1.2em;";
	static final String textCenter = "text-align: center;";
	static final String colorLightYellow = "background-color: lightyellow;";
	static final String colorLightGreen = "background-color:  #DAF7A6 ;";
	static final String colorLightOrange = "background-color: #FFD580;";
	static final String textRight= "text-align: right;";
	static final String width500 = "width: 500px;";
	static final String width150 = "width: 150px;";
	static final String width200 = "width: 200px;";
	static final String bold = "font-weight: bold;";
	static final String blockCenter = "margin-left: auto;margin-right: auto;";
	
	private static final String EMTPY_JSON = "{messages : []}";
	// 1 de Julio del 2021		
	public static final Date IVA_2021_CHANGE_DATE =  Date.from(LocalDateTime.of(2021, 7, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());	

	static class ExplainRowManager implements Function<FiscalModel,String> {
		private double sum = 0.0;
		private boolean something = false;

		@Override
		public String apply(FiscalModel fm) {
			something = true;
			sum(fm.getDeclarationResult());
			return new StringBuilder()
				.append("<tr>")
					.append( MessageFormat.format(styledTag, "td", paddingLeft+border+noWrap) )
						.append("Resultado de la liquidaci\u00F3n "
							+ fm.getModelFullName()
							+ AonObjectUtils.defaultIfNull(fm.getDeclarationResultType(), t -> " (" + t.getDescription() + ")"))
					.append("</td>")
					.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
						.append(DEC2.format(fm.getDeclarationResult()))
					.append("</td>")
				.append("</tr>")
				.toString();		
		}
		public boolean hasSomething() {
			return something;
		}
		public ExplainRowManager setSomething(boolean something) {
			this.something = something;
			return this;
		}
		public double getSum() {
			return sum;
		}
		public ExplainRowManager sum(double amount) {
			sum = AonMathUtils.round(sum + amount);
			return this;
		} 
	}

	private enum Declarations {
		 AEAT_2022 {
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
				
				final Mod303 dupl = new Mod303();
				dupl.setDomain(mod303.getDomain());
				dupl.setDomainName(mod303.getDomainName());
				dupl.setYear(mod303.getYear());
				dupl.setModel(mod303.getModel());
				dupl.setPeriod(mod303.getPeriod().isMonthPeriod()?Period.M11:Period.T3);
				dupl.setAdministration(mod303.getAdministration());
				dupl.setProratePercent( mod303.getProratePercent() );
				dupl.setPreviousProratePercent( mod303.getPreviousProratePercent() );
				dupl.setSpecialProrateValue( mod303.isSpecialProrate() );
				dupl.setDraft(true);
				dupl.setGenerateFromYearStart(true);
				Mod303Declaration draftDec = Mod303Declaration.getInstance(dupl); 
				draftDec.createOnTheFly(ctx, dupl);
				
				double prorratedAmount = dupl.ensureDetail(Mod303Key.CM_072).getAmount();
				double unProrratedAmount = dupl.ensureDetail(Mod303Key.CM_074).getAmount();
				mod303.ensureDetail(Mod303Key.CM_072).setAmount( prorratedAmount );
				mod303.ensureDetail(Mod303Key.CM_074).setAmount( unProrratedAmount );
				
				double declared = AonMathUtils.round((prorratedAmount * prevPercent / 100) + unProrratedAmount) ;
				double mustDeclared = AonMathUtils.round((prorratedAmount * lastPercent / 100) + unProrratedAmount);
				double difference  = AonMathUtils.round(mustDeclared - declared);
				mod303.putAmount(getRegularizationKey(), difference);
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
		if (!mod303.isFirstPeriod() && mod303.getYear() >= 2021 ) { 
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
	
	protected Set<Integer> createFromInvoices(AONContext ctx, Mod303 mod303) {
		final Set<Integer> invoices = new HashSet<>();
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

	public Set<Integer> createOnTheFly(AONContext ctx, Mod303 mod303) {
		if (hasSimplifiedRegime()) {
			initializeSimplifiedRegime(ctx, mod303);
		}
		firstInitialization(ctx, mod303);
		if (!mod303.isManualDeclaration()) {
			Set<Integer> invoices = createFromInvoices(ctx,mod303);
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
	public Mod303Key[] getSamePeriodExplainKeys() {
		return new Mod303Key[] {};
	}
	protected String getSamePeriodExplain(AONContext ctx, Mod303 mod303, Mod303Key key) {
		return EMTPY_JSON;
	}
	
	protected String getRegularizationExplain(AONContext ctx, Mod303 mod303, Mod303Key key) {
		StringBuilder buf = new StringBuilder();
		buf.append("<div "
				+ "style=\"" 
				+ "padding-right: 15px; padding-left: 15px; margin-right: auto; "
				+ "margin-left: auto; width:100%; display: flex;flex-wrap: wrap; "
				+ "justify-content: center; box-sizing: border-box"
				+ "\">");
		buf.append( MessageFormat.format(styledTag, "table cellspacing=\"0\"",  blockCenter+marginTop ) )
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"12\"",  textCenter+bold+fontLarger+border) )
					.append("Casilla " + key.getBoxFormatted())
				.append("</td>")
			.append("</tr>")
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td",  textCenter+bold+fontLarger+border) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"5\"", textRight+bold+border+noWrap+textCenter+colorLightYellow))
					.append("DECLARADO")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"5\"", textRight+bold+border+noWrap+textCenter+colorLightGreen))
					.append("NUEVO %")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"5\"", textRight+bold+border+noWrap+textCenter+colorLightOrange))
					.append("A DECLARAR")
				.append("</td>")
			.append("</tr>")
			
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td", border+noWrap) )
					.append("Declaraci\u00F3n")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"3\"", textRight+border+noWrap+textCenter+colorLightYellow))
					.append("IVA con prorrata")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td", textRight+border+noWrap+textCenter+colorLightYellow))
					.append("IVA sin prorrata")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td", textRight+border+noWrap+textCenter+colorLightYellow))
					.append("Total IVA deducido")
				.append("</td>")
				

				.append( MessageFormat.format(styledTag, "td colspan=\"3\"", textRight+border+noWrap+textCenter+colorLightGreen))
					.append("IVA con prorrata")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td", textRight+border+noWrap+textCenter+colorLightGreen))
					.append("IVA sin prorrata")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td", textRight+border+noWrap+textCenter+colorLightGreen))
					.append("Total IVA deducido")
				.append("</td>")
				
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"", textRight+border+noWrap+textCenter+colorLightOrange))
					.append("Diferencia")
				.append("</td>")
			.append("</tr>")
			
			;
		
		MutableDouble sumDeclared = new MutableDouble();
		MutableDouble sumMustDeclared = new MutableDouble();
		MutableDouble sumDiference = new MutableDouble();

		ExplainRowManager rowManager =  new ExplainRowManager() {
			
			@Override
			public String apply(FiscalModel fm) {
				Mod303 m303 = (Mod303) fm;
				setSomething(true);
				sum(fm.getDeclarationResult());
				double percent = m303.getProratePercent();	

				Mod303Declaration dec =  Mod303Declaration.getInstance(m303);
				MutableDouble sumProrratedMustDeclared = new MutableDouble();
				MutableDouble sumUnprorratedMustDeclared = new MutableDouble();
				VATDAO.getModelVatBreakdown(ctx, m303)
					.filter( br -> Arrays.stream(dec.getProrateKeys())
							.map(dec::getKey )
							.anyMatch(kd -> kd.acceptValue(m303, br)))
					.forEach(br -> {
						if (mustApplyProrrate(m303,br)) {
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
				double prorratedQuota = sumProrratedMustDeclared.doubleValue(); 
				double unprorratedQuota = sumUnprorratedMustDeclared.doubleValue();
				
				double mustProrrated = AonMathUtils.round(prorratedQuota * mod303.getProratePercent() / 100);
				double mustDeclared = AonMathUtils.round(mustProrrated +  unprorratedQuota);
				
				double diference = AonMathUtils.round(mustDeclared - declared);
		
				sumDeclared.add(declared); 
				sumMustDeclared.add(mustDeclared);
				sumDiference.add(diference);
				
				return new StringBuilder()
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", border+noWrap) )
							.append(fm.getModelFullName()
								+ AonObjectUtils.defaultIfNull(fm.getDeclarationResultType(), t -> " (" + t.getDescription() + ")"))
						.append("</td>")
						
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightYellow) )				
							.append(DEC2.format(prorratedQuota))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border+noWrap+colorLightYellow) )				
							.append(DEC2.format(percent))
							.append(" %")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border+noWrap+colorLightYellow) )				
							.append(DEC2.format(AonMathUtils.round(declared - unprorratedQuota)))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightYellow) )				
							.append(DEC2.format(unprorratedQuota))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightYellow) )				
							.append(DEC2.format(declared))
						.append("</td>")
						
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightGreen) )				
							.append(DEC2.format(prorratedQuota))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border+noWrap+colorLightGreen) )				
						.append(DEC2.format(mod303.getProratePercent()))
							.append(" %")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border+noWrap+colorLightGreen) )				
							.append(DEC2.format(mustProrrated))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightGreen) )				
							.append(DEC2.format(unprorratedQuota))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightGreen) )				
							.append(DEC2.format(mustDeclared))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightOrange) )				
							.append(DEC2.format(diference))
						.append("</td>")
					.append("</tr>")
					.toString()
					;		
			}
		};		
		
		
		Mod303DAO.getPreviousEffectiveModels(ctx, mod303)
			.forEach( fm ->  buf.append( rowManager.apply(fm) ));
		
		if (rowManager.hasSomething()) {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td",  bold+fontLarger+border) )
					.append("Total")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width200+border+colorLightYellow) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightYellow) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightYellow) )
					.append("")
				.append("</td>")
				
				
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightYellow) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width200+border+colorLightYellow) )
					.append(DEC2.format(AonMathUtils.round(sumDeclared.getValue())))
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightGreen) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightGreen) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightGreen) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightGreen) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width200+border+colorLightGreen) )
					.append(DEC2.format(AonMathUtils.round(sumMustDeclared.getValue())))
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width200+border+colorLightOrange) )
					.append(DEC2.format(AonMathUtils.round(sumDiference.getValue())))
				.append("</td>")
			.append("</tr>");
		} else {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+fontLarger+border) )
					.append("No se han encontrado datos para el c\u00E1lculo.")
				.append("</td>")
			.append("</tr>");
		}
		buf.append("</div>");
		buf.append("</div>");
		return buf.toString();
	}

	protected <T extends FiscalModel> String getExplain( AONContext ctx, Mod303 mod303, Mod303Key key, Stream<T> stream, ExplainRowManager rowManager) {
		StringBuilder buf = new StringBuilder();
		buf.append("<div style=\"" +
				  "padding-right: 15px; padding-left: 15px; margin-right: auto; "
				+ "margin-left: auto; width:100%; display: flex;flex-wrap: wrap; "
				+ "justify-content: center; box-sizing: border-box"
				+ "\">")
			.append("<div style=\"" 
				+ "border-radius: 4px; background: #fff; box-shadow: 0 6px 10px rgba(0,0,0,.08), 0 0 6px rgba(0,0,0,.05);"
				+ "transition: .3s transform cubic-bezier(.155,1.105,.295,1.12),.3s box-shadow,.3s -webkit-transform cubic-bezier(.155,1.105,.295,1.12);"
				+ "padding: 4px 5px 5px 10px; margin: 20px 10px 10px 10px; cursor: pointer;"
				+ "flex: 0 1 40%; min-height: 120px; min-width: 350px;"
				+ "\">");
		buf.append( MessageFormat.format(styledTag, "table cellspacing=\"0\"",  blockCenter+marginTop+border ) )
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+bold+fontLarger+border) )
					.append("Casilla " + key.getBoxFormatted())
				.append("</td>")
			.append("</tr>");
		
		stream.forEach( fm ->  buf.append( rowManager.apply(fm) ));
		if (rowManager.hasSomething()) {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td",  bold+fontLarger+border) )
					.append("Total")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+fontLarger+border) )
					.append(DEC2.format(AonMathUtils.round(rowManager.getSum())))
				.append("</td>")
			.append("</tr>");
		} else {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+fontLarger+border) )
					.append("No se han encontrado datos para el c\u00E1lculo.")
				.append("</td>")
			.append("</tr>");
		}
//		buf.append("</div>");
		buf.append("</div>");
		return buf.toString();
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
	abstract Set<Integer> createVatAccrualKeysFromInvoices(AONContext ctx, Mod303 mod303);
	
}
