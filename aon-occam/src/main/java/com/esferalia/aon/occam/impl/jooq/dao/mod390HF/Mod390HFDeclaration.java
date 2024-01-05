package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.ComplementaryBeahaviour;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303Declaration;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public abstract class Mod390HFDeclaration {

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
	static final String EMTPY_JSON = "{messages : []}";

	static final double PERCENT_21 = 21.0;
	static final double PERCENT_10 = 10.0;
	static final double PERCENT_0 = 0.0;
	static final double PERCENT_4 = 4.0;
	static final double PERCENT_5 = 5.0;
	static final double PERCENT_105 = 10.5;	
	static final double PERCENT_12 = 12;
	static final double SURCHARGE_PERCENT_52 = 5.2;
	static final double SURCHARGE_PERCENT_14 = 1.4;
	static final double SURCHARGE_PERCENT_05 = 0.5;
	static final double SURCHARGE_PERCENT_175 = 1.75;
	
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
		GIPUZKOA_2023 {
			@Override boolean accept(Mod390HF mod) { return Mod390HFGipuzkoa2023Declaration.accept(mod);}
			@Override Mod390HFDeclaration get() {return new Mod390HFGipuzkoa2023Declaration();}
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

	protected static void addProrrated(Mod390Key key,Mod390HF mod,VatContext vat) {
		double amount = vat.getDeductibleQuota();
		if (mustApplyProrrate(mod,vat)) {
			mod.ensureDetail(Mod390Key.CM_072).addAmount(amount);
			amount = AonMathUtils.round(amount * mod.getProratePercent() / 100);	
		} else {
			mod.ensureDetail(Mod390Key.CM_074).addAmount(vat.getDeductibleQuota());
		}
		add( key, mod, amount);
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
	
	protected Set<Alcatraz> createOnTheFly(AONContext ctx, Mod390HF mod) {
		firstInitialization(ctx, mod);
		if (!mod.isManualDeclaration()) {
			Set<Alcatraz> invoices = createFromInvoices(ctx,mod);
			Set<Alcatraz> invoices2 = createVatAccrualKeysFromInvoices(ctx,mod);
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
	
	
	protected Set<Alcatraz> createFromInvoices(AONContext ctx, Mod390HF mod) {
		final Set<Alcatraz> invoices = new HashSet<>();
		VATDAO.getVatBreakdown(ctx,mod)
			.flatMap(vt -> Arrays.stream( getKeys() ).map( key -> new KeyedVatContext(key, vt)))
			.filter(kbr -> kbr.getKey().acceptValue(mod,kbr.getVatContext()))
			.map( kbr -> addInvoice(invoices, kbr))
			.forEach( kbr -> kbr.getKey().initialize(ctx, mod, kbr.getVatContext()) );
		return invoices;
	}
	private KeyedVatContext addInvoice( Set<Alcatraz> invoices, KeyedVatContext vt) {
		invoices.add( new Alcatraz()
				.setInvoice(vt.getVatContext().getInvoice())
				.setFinance(vt.getVatContext().getFinance())
				.setFinanceTracking(vt.getVatContext().getFinanceTracking())
				);
		return vt;	
	}
	
	static boolean hasPercent0(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_0;	
	}
	static boolean hasPercent4(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_4;	
	}
	static boolean hasPercent5(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_5;	
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

	public void prorrateRegularization(AONContext ctx, Mod390HF mod){
		if (getRegularizationKey() != null) {
			double lastPercent = mod.getProratePercent();
			double prevPercent = mod.getPreviousProratePercent();
			if ((mod.hasProrate() || mod.hasPreviousProrate()) 
				&& AonNumberUtils.notEquals(lastPercent, prevPercent)) {
				double prorratePercent = mod.getProratePercent();
				double previousProrratePercent = mod.getPreviousProratePercent();
				final Mod303 dupl = new Mod303();
				dupl.setDomain(mod.getDomain());
				dupl.setDomainName(mod.getDomainName());
				dupl.setYear(mod.getYear());
				dupl.setModel(  FiscalModelType.M303 );
				dupl.setPeriod(mod.getPeriod().isMonthPeriod()?Period.M11:Period.T3);
				dupl.setAdministration(mod.getAdministration());
				dupl.setProratePercent( prorratePercent );
				dupl.setSpecialProrateValue( mod.isSpecialProrate() );
				dupl.setPreviousProratePercent( previousProrratePercent );
				dupl.setDraft(true);
				dupl.setGenerateFromYearStart(true);
				Mod303Declaration draftDec = Mod303Declaration.getInstance(dupl); 
				draftDec.createOnTheFly(ctx, dupl);
				
				double prorratedAmount = dupl.ensureDetail(Mod303Key.CM_072).getAmount();
				double unProrratedAmount = dupl.ensureDetail(Mod303Key.CM_074).getAmount();
				mod.ensureDetail(Mod390Key.CM_072).setAmount( prorratedAmount );
				mod.ensureDetail(Mod390Key.CM_074).setAmount( unProrratedAmount );
				
				double declared = AonMathUtils.round((prorratedAmount * prevPercent / 100) + unProrratedAmount) ;
				double mustDeclared = AonMathUtils.round((prorratedAmount * lastPercent / 100) + unProrratedAmount);
				double difference  = AonMathUtils.round(mustDeclared - declared);
				mod.putAmount(getRegularizationKey(), difference);
			}
		}
	}

	public void _prorrateRegularization(AONContext ctx, Mod390HF mod){
		if (getRegularizationKey() != null) {
			double lastPercent = mod.getProratePercent();
			double prevPercent = mod.getPreviousProratePercent();
			if ((mod.hasProrate() || mod.hasPreviousProrate()) && AonNumberUtils.notEquals(lastPercent, prevPercent)) {
				MutableDouble declared = new MutableDouble();
				MutableDouble total = new MutableDouble();
				Mod390HFDAO.getM303EffectiveYearModels(ctx, mod)
					.forEach(m303 ->{
						Mod303Declaration dec = Mod303Declaration.getInstance(m303);						
						double percent = m303.getProratePercent();
						double deducedAmount = 0.0;
						if ( m303.getMap() != null && !m303.getMap().isEmpty()) {
							for(String key : m303.getMap().keySet() ) {
								if (dec.isProrrated( Mod303Key.getKey(key))) {
									deducedAmount = deducedAmount + m303.getAmount(key); 
								}
							}
						}
						declared.add(deducedAmount);
						total.add(AonMathUtils.round(deducedAmount * 100 / percent));
				});
				double mustDeclared = AonMathUtils.round(total.getValue() * mod.getProratePercent() / 100);  
				mod.putAmount(getRegularizationKey(), AonMathUtils.round(mustDeclared - declared.getValue()));
			}
		}
	}
	
	public Mod390Key[] getCompensationExplainKeys() {
		return new Mod390Key[] {};
	}
	protected String getCompensationExplain(AONContext ctx, Mod390HF mod, Mod390Key key) {
		return EMTPY_JSON;
	}
	public Mod390Key[] getSamePeriodExplainKeys() {
		return new Mod390Key[] {};
	}
	protected String getSamePeriodExplain(AONContext ctx, Mod390HF mod, Mod390Key key) {
		return EMTPY_JSON;
	}

	protected String getRegularizationExplain(AONContext ctx, Mod390HF mod, Mod390Key key) {
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
						if (Mod303Declaration.mustApplyProrrate(m303,br)) {
							sumProrratedMustDeclared.add(br.getDeductibleQuota());
						} else {
							sumUnprorratedMustDeclared.add(br.getDeductibleQuota());
						}
					});
				
				double declared = 0.0;
				if ( fm.getMap() != null && !fm.getMap().isEmpty()) {
					for(String key : fm.getMap().keySet() ) {
						if (dec.isProrrated( Mod303Key.getKey(key))) {
							declared = declared + fm.getAmount(key); 
						}
					}
				}
				double prorratedQuota = sumProrratedMustDeclared.doubleValue(); 
				double unprorratedQuota = sumUnprorratedMustDeclared.doubleValue();
				
				double mustProrrated = AonMathUtils.round(prorratedQuota * mod.getProratePercent() / 100);
				double mustDeclared = AonMathUtils.round(mustProrrated +  unprorratedQuota);
				
				double diference = AonMathUtils.round(mustDeclared - declared);
		
				sumDeclared.add(declared); 
				sumMustDeclared.add(mustDeclared);
				sumDiference.add(diference);
				
				return new StringBuilder()
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", border+noWrap) )
							.append("IVA deducible: "
								+ fm.getModelFullName()
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
						.append(DEC2.format(m303.getProratePercent()))
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
		
		
		Mod390HFDAO.getM303EffectiveYearModels(ctx, mod)
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

	protected String _getRegularizationExplain(AONContext ctx, Mod390HF mod390hf, Mod390Key key) {
		StringBuilder buf = new StringBuilder();
		buf.append("<div "
				+ "style=\"" 
				+ "padding-right: 15px; padding-left: 15px; margin-right: auto; "
				+ "margin-left: auto; width:100%; display: flex;flex-wrap: wrap; "
				+ "justify-content: center; box-sizing: border-box"
				+ "\">")
			.append("<div "
				+ "style=\"" 
				+ "border-radius: 4px; background: #fff; box-shadow: 0 6px 10px rgba(0,0,0,.08), 0 0 6px rgba(0,0,0,.05);"
				+ "transition: .3s transform cubic-bezier(.155,1.105,.295,1.12),.3s box-shadow,.3s -webkit-transform cubic-bezier(.155,1.105,.295,1.12);"
				+ "padding: 4px 5px 5px 10px; margin: 20px 10px 10px 10px; cursor: pointer;"
				+ "flex: 0 1 40%; min-height: 120px; min-width: 350px;"
				+ "\">");
		buf.append( MessageFormat.format(styledTag, "table cellspacing=\"0\"",  blockCenter+marginTop ) )
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"7\"",  textCenter+bold+fontLarger+border) )
					.append("Casilla " + key.getBoxFormatted())
				.append("</td>")
			.append("</tr>")
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td", border+noWrap) )
					.append("Declaraci\u00F3n")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td", textRight+border+noWrap+textCenter))
					.append("Total IVA deducible")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"", textRight+border+noWrap+textCenter+colorLightYellow))
					.append("IVA deducido")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"", textRight+border+noWrap+textCenter+colorLightGreen))
					.append("IVA al nuevo %")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"", textRight+border+noWrap+textCenter+colorLightOrange))
					.append("Diferencia")
				.append("</td>")
			.append("</tr>")
			
			;
		
		MutableDouble sumVat = new MutableDouble();
		MutableDouble sumDeclared = new MutableDouble();
		MutableDouble sumMustDeclared = new MutableDouble();
		MutableDouble sumDiference = new MutableDouble();

		ExplainRowManager rowManager =  new ExplainRowManager() {
			
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				sum(fm.getDeclarationResult());
				double percent = 100;
				if (!(fm instanceof Mod303)) {
					return "";
				}
				Mod303 m303 = ((Mod303)fm);
				percent = m303.getProratePercent();	
				double declared = 0.0;
				if ( fm.getMap() != null && !fm.getMap().isEmpty()) {
					for(String key : fm.getMap().keySet() ) {
						Mod303Declaration dec = Mod303Declaration.getInstance(m303);
						if (dec.isProrrated( Mod303Key.getKey(key))) {
							declared = declared + fm.getAmount(key); 
						}
					}
				}
				double total = AonMathUtils.round(declared * 100 / percent);   
				double mustDeclared = AonMathUtils.round(total * mod390hf.getProratePercent() / 100); 
				double diference = AonMathUtils.round(mustDeclared - declared);
		
				sumVat.add(total);
				sumDeclared.add(declared); 
				sumMustDeclared.add(mustDeclared);
				sumDiference.add(diference);
				
				return new StringBuilder()
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", border+noWrap) )
							.append("IVA deducible: "
								+ fm.getModelFullName()
								+ AonObjectUtils.defaultIfNull(fm.getDeclarationResultType(), t -> " (" + t.getDescription() + ")"))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border) )				
							.append(DEC2.format(total))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border+noWrap+colorLightYellow) )				
							.append(DEC2.format(percent))
							.append(" %")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightYellow) )				
							.append(DEC2.format(declared))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border+noWrap+colorLightGreen) )				
							.append(DEC2.format(mod390hf.getProratePercent()))
							.append(" %")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightGreen) )				
							.append(DEC2.format(mustDeclared))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightOrange) )				
							.append(DEC2.format(diference))
						.append("</td>")
					.append("</tr>")
					.toString();		
				}
			};		
		Mod390HFDAO.getM303EffectiveYearModels(ctx, mod390hf)
				.forEach( fm ->  buf.append( rowManager.apply(fm) ));
	
		if (rowManager.hasSomething()) {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td",  bold+fontLarger+border) )
					.append("Total")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width200+border) )
					.append(DEC2.format(AonMathUtils.round(sumVat.getValue())))
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
	
	protected <T extends FiscalModel> String getExplain( AONContext ctx, Mod390HF mod390HF, Mod390Key key, Stream<T> stream, ExplainRowManager rowManager) {
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
		buf.append("</div>");
		buf.append("</div>");
		return buf.toString();
	}
	
	protected static double getPendingCompesateAmounts(AONContext ctx, Mod390HF mod) {
		return Mod390HFDAO.getLastPeriodEffectiveModels(ctx, mod)
			.mapToDouble(fm -> AonMathUtils.round((fm.isToCompensate()? AonMathUtils.absRounded(fm.getDeclarationResult()):0.0)))
			.findFirst()
			.orElse(0.0);
	}
	
	abstract ComplementaryBeahaviour getComplementaryBehaviour(final Mod390HF mod);
	void specificInitialization(AONContext ctx, Mod390HF mod) {}
	abstract IMod390KeyDAO safeValueOf(Mod390HF mod, String key);
	abstract IMod390KeyDAO valueOf(String string);
	abstract IMod390KeyDAO[] getKeys();
	public abstract Mod390Key[] getProratedKeys();
	public abstract Mod390Key getRegularizationKey();
	abstract Set<Alcatraz> createVatAccrualKeysFromInvoices(AONContext ctx, Mod390HF mod);
	abstract double getResult(final Mod390HF mod);
	abstract Mod390HF initialize(AONContext ctx, Mod390HF mod303);

	
	
}
