package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.text.MessageFormat;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390HF.Mod390HFDAO;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class Mod303GIPUZKOA2022Declaration extends Mod303GIPUZKOA {
	
	protected Mod303GIPUZKOA2022Declaration() {
		
	}
	
	public static final double PERCENT1 = 21.0;
	public static final double PERCENT2 = 10.0;
	public static final double PERCENT3 = 4.0;
	public static final double SURCHARGE_PERCENT1 = 5.2;
	public static final double SURCHARGE_PERCENT2 = 1.4;
	public static final double SURCHARGE_PERCENT3 = 0.5;
	
	public static boolean accept(Mod303 mod) {
		return mod.isGipuzkoa() && mod.getYear() > 2021 && !mod.isLastPeriod();
	}
	private static final Mod303Key[] PRORATE_KEYS = new Mod303Key[]{
		  Mod303Key.GP_C018
		 ,Mod303Key.GP_C020
		 ,Mod303Key.GP_C022
		 ,Mod303Key.GP_C046
		 ,Mod303Key.GP_C023
	};
	
	private enum Mod303KeyDAO implements IMod303KeyDAO {
		 GP_I000	(Mod303Key.GP_I000
			 ,null,null,Mod303GIPUZKOA2022Declaration::addDeponentDocument,null,null)
		,GP_A001	(Mod303Key.GP_A001)
		,GP_A002	(Mod303Key.GP_A002)
		,CM_003		(Mod303Key.CM_003)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al primer tipo.
		,GP_C002(Mod303Key.GP_C002
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C002,mod,vat.getBase())
			,null,null,null)
		,GP_X002(Mod303Key.GP_X002,null,null,(ctx,mod) -> add(Mod303Key.GP_X002,mod,PERCENT1),null,null)
		,GP_C003(Mod303Key.GP_C003
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C003,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al segundo tipo.
		,GP_C004(Mod303Key.GP_C004
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			 ,(ctx,mod,vat) -> add(Mod303Key.GP_C004,mod,vat.getBase())
				,null,null,null)
		,GP_X004(Mod303Key.GP_X004,null,null,(ctx,mod) -> add(Mod303Key.GP_X004,mod,PERCENT2),null,null)
		,GP_C005(Mod303Key.GP_C005
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C005,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al tercer tipo.
		,GP_C006(Mod303Key.GP_C006
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			 ,(ctx,mod,vat) -> add(Mod303Key.GP_C006,mod,vat.getBase())
			,null,null,null)
		,GP_X006(Mod303Key.GP_X006,null,null,(ctx,mod) -> add(Mod303Key.GP_X006,mod,PERCENT3),null,null)
		,GP_C007(Mod303Key.GP_C007
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C007,mod,vat.getQuota())
			,null,null,null)
		
		// Modificación bases y cuotas
		,GP_C039(Mod303Key.GP_C039
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.GP_C039,mod,vat.getBase())
			,null,null,null)
		,GP_C040(Mod303Key.GP_C040
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C040,mod,vat.getQuota())
			,null,null,null)
		
		// Recargo equivalencia al primer tipo.
		,GP_C008(Mod303Key.GP_C008
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C008,mod,vat.getBase())
			,null,null,null)
		,GP_X008(Mod303Key.GP_X008,null,null,(ctx,mod) -> add(Mod303Key.GP_X008,mod,SURCHARGE_PERCENT1),null,null)
		,GP_C009(Mod303Key.GP_C009
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C009,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al segundo tipo.
		,GP_C010(Mod303Key.GP_C010
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C010,mod,vat.getBase())
			,null,null,null)
		,GP_X010(Mod303Key.GP_X010,null,null,(ctx,mod) -> add(Mod303Key.GP_X010,mod,SURCHARGE_PERCENT2),null,null)
		,GP_C011(Mod303Key.GP_C011
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C011,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al tercer tipo.
		,GP_C012(Mod303Key.GP_C012
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C012,mod,vat.getBase())
			,null,null,null)
		,GP_X012(Mod303Key.GP_X012,null,null,(ctx,mod) -> add(Mod303Key.GP_X012,mod,SURCHARGE_PERCENT3),null,null)
		,GP_C013(Mod303Key.GP_C013
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C013,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Modificaciones bases y cuotas del recargo de equivalencia
		,GP_C041(Mod303Key.GP_C041
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.GP_C041,mod,vat.getBase())
			,null,null,null)
		,GP_C042(Mod303Key.GP_C042
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.GP_C042,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias al primer tipo.		
		,GP_C014(Mod303Key.GP_C014
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.GP_C014,mod,vat.getBase())
			,null,null,null)
		,GP_C015(Mod303Key.GP_C015
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.GP_C015,mod,vat.getQuota())
			,null,null,null)
		
		// Otras operaciones con inversión del sujeto pasivo (excepto. adq. intracom). Base y cuota
		,GP_C043(Mod303Key.GP_C043
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C043,mod,vat.getBase())
			,null,null,null)
		,GP_C044(Mod303Key.GP_C044
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C044,mod,vat.getQuota())
			,null,null,null)
		
		// TOTAL CUOTA DEVENGADA
		,GP_C016(Mod303Key.GP_C016,null,null,null,"GP_C003+GP_C005+GP_C007+GP_C040+GP_C009+GP_C011+GP_C013+GP_C042+GP_C015+GP_C044",null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------

		// IVA deducible en operaciones interiores 
		,GP_C017(Mod303Key.GP_C017
			,(mod,vat) -> operacionesInterioresFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C017,mod,vat.getBase())
			,null,null,null)
		,GP_C018(Mod303Key.GP_C018
			,(mod,vat) -> operacionesInterioresFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.GP_C018,mod,vat)
			,null,null,null)
		
		// IVA deducible en importaciones
		,GP_C019(Mod303Key.GP_C019
			,(mod,vat) -> importacionesFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C019,mod,vat.getBase())
			,null,null,null)
		,GP_C020(Mod303Key.GP_C020
			,(mod,vat) -> importacionesFilter(vat,mod)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.GP_C020,mod,vat)
			,null,null,null)
		
		// IVA deducible en adquisiciones intracomunitarias de bienes y servicios corrientes
		,GP_C021(Mod303Key.GP_C021
			,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C021,mod,vat.getBase())
			,null,null,null)
		,GP_C022(Mod303Key.GP_C022
			,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.GP_C022,mod,vat)
			,null,null,null)
		// Rectificación de deducciones
		,GP_C045(Mod303Key.GP_C045
			,(mod,vat) -> rectificationDeduccionesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.GP_C045,mod,vat.getBase())
			,null,null,null)
		,GP_C046(Mod303Key.GP_C046
			,(mod,vat) -> rectificationDeduccionesFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.GP_C046,mod,vat)
			,null,null,null)
		
		// Compensaciones Régimen Especial A.G. y P .
		,GP_C023(Mod303Key.GP_C023
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.GP_C023,mod,vat)
			,null,null,null)
		
		// Regularización Inversiones
		,GP_C024(Mod303Key.GP_C024)
		
		// TOTAL A DEDUCIR
		,GP_C025(Mod303Key.GP_C025,null,null,null,"GP_C018+GP_C020+GP_C022+GP_C046+GP_C023+GP_C024",null)
		
		// -----------------------------------------------------------
		// ------------------------------------------------- RESULTADO
		// -----------------------------------------------------------
		
		// DIFERENCIA
		,GP_C026(Mod303Key.GP_C026,null,null,null,"GP_C016-GP_C025",null)
		
		// Volumen operaciones. % GIPUZKOA
		,GP_C027(Mod303Key.GP_C027,null,null,(ctx,mod) -> add(Mod303Key.GP_C027,mod,100.0),null,null)
	
		// Cuota atribuible al Territorio Histórico de Gipuzkoa	
		,GP_C028(Mod303Key.GP_C028,null,null,null,"GP_C026*GP_C027/100",null)

		// Cuotas a compensar de períodos anteriores en el Territorio Histórico de gipuzkoa	
		,GP_C029(Mod303Key.GP_C029,null,null, (ctx,mod) -> add( Mod303Key.GP_C029, mod, getPendingCompesateAmounts( ctx, mod )),null,null)

		// RESULTADO DE LA AUTOLIQUIDACIÓN	
		,GP_C035(Mod303Key.GP_C035,null,null,null,"GP_C028-GP_C029",null)
		
		// -----------------------------------------------------------
		// ------------------------------------- INFORMACION ADICIONAL
		// -----------------------------------------------------------
		
		// Total entregas de bienes y prestaciones de servicios intracomunitarias
		,GP_C030(Mod303Key.GP_C030
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales()
			,(ctx,mod,vat) -> add(Mod303Key.GP_C030,mod,vat.getBase())
			,null,null,null)
		
		// Total exportaciones y operaciones asimiladas
		,GP_C031(Mod303Key.GP_C031
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales())
			,(ctx,mod,vat) -> add(Mod303Key.GP_C031,mod,vat.getBase())
			,null,null,null)
		
		// Operaciones no sujetas o con inversión del sujeto pasivo que originan el derecho a deducción
		,GP_C051(Mod303Key.GP_C051)
		,GP_C052(Mod303Key.GP_C052
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isOtherISPSales()  
			,(ctx,mod,vat) -> add(Mod303Key.GP_C052,mod,vat.getBase())
			,null,null,null)
		,GP_C053(Mod303Key.GP_C053)
		,GP_C054(Mod303Key.GP_C054)
		
		// Importes de las ventas a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran 
		// resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA		
		,GP_C047(Mod303Key.GP_C047, (mod, vat) -> vat.isSales() && vat.isVatAccrualRegime()
				, null, null, null, null)
		,GP_C048(Mod303Key.GP_C048, (mod, vat) -> vat.isSales() && vat.isVatAccrualRegime()
				, null, null, null, null)
		
		// Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el 
		// régimen especial del criterio de caja
		,GP_C049(Mod303Key.GP_C049, (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime()
				, null, null, null, null)
		,GP_C050(Mod303Key.GP_C050, (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime()
				, null, null, null, null)
		;
		
		private Mod303Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;

		private Mod303KeyDAO(Mod303Key key) {
			this(key,null,null,null,null,null);			
		}
		private Mod303KeyDAO(Mod303Key key
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueFirstIntializer firstInitializer
				, String expression
				, String template) {
			this.key = key;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.firstInitializer = firstInitializer;
			this.expression =  expression;
			this.template = template;
		}
		
		
		@Override
		public Mod303Key getKey() {
			return key;
		}
		@Override
		public String getExpression() {
			return expression;
		}
		@Override
		public String getTemplate() {
			return template;
		}
		@Override
		public boolean acceptValue(Mod303 mod,VatContext vctx) {
			return  acceptValue != null && acceptValue.accept(mod,vctx);
		}
		@Override
		public boolean hasAccepter() {
			return acceptValue != null;
		}
		@Override
		public void initialize(AONContext ctx,Mod303 mod,VatContext vctx) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, vctx);
			}
		}
		@Override
		public void firstInitialize(AONContext ctx,Mod303 mod) {
			if (firstInitializer != null) {
				firstInitializer.initialize(ctx, mod);
			}
		}
		public static Mod303KeyDAO safeValueOf(String key) {
			if (AonStringUtils.isBlank(key)) return null; 
			for (Mod303KeyDAO keyDAO : Mod303KeyDAO.values()) {	
				if (keyDAO.getKey().getValue().equals(key) ) {
					return keyDAO;
				}
			}
			return null;
		}
	}
	@Override
	public IMod303KeyDAO[] getKeys() {
		return Mod303KeyDAO.values();
	}
	
	private static void addDeponentDocument(AONContext ctx, Mod303 mod) {
		Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
		if (!domain.isStandalone()) {
			Company parentCompany = CompanyDAO.getCompany(ctx, domain.getParentId());
			if (parentCompany != null) {
				mod.ensureDetail(Mod303Key.GP_I000).setDescription(parentCompany.getDocument());	
			}
		}
	}

	@Override
	public IMod303KeyDAO safeValueOf(Mod303 mod, String key) {
		return Mod303KeyDAO.safeValueOf(key);
	}
	
	@Override
	public IMod303KeyDAO valueOf(String keyValue) {
		return Mod303KeyDAO.valueOf(keyValue);
	}
	@Override
	public Mod303Key[] getProrateKeys() {
		return PRORATE_KEYS;
	}
	@Override
	public boolean hasSimplifiedRegime() {
		return false;
	}
	//	-----------------------------------------------------------------------	
	//	--------------------------------------------------------------- FILTROS	
	//	-----------------------------------------------------------------------
	private static boolean isCommonNationalSales(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isNational() && vat.isSales() && !vat.isRectification();
	}
	private static boolean hasPercent1(VatContext vat) {
		return vat.getPercentage() ==  PERCENT1;	
	}
	private static boolean hasPercent2(VatContext vat) {
		return vat.getPercentage() ==  PERCENT2; 	
	}
	private static boolean hasPercent3(VatContext vat) {
		return vat.getPercentage() ==  PERCENT3; 	
	}
	private static boolean hasSurchargePercent1(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT1; 
	}
	private static boolean hasSurchargePercent2(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT2; 
	}
	private static boolean hasSurchargePercent3(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT3;
	}
	
	private static boolean modificacionBasesYCuotasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isRectification() && (vat.isNationalSales() || operacionesISPFilter(vat));		
	}
	private static boolean operacionesISPFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& (vat.isOtherISPPurchase() 
				|| vat.isOtherISPExpenses() 
				|| vat.isExtracommunityExpenses() 
				|| vat.isCanCeuMelExpenses()
				|| (vat.isExtracommunityPurchase() && vat.isService()) 
				|| (vat.isCanCeuMelPurchase() && vat.isService()));
	}
	private static boolean adqIntracomunitariasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses());
	}
	private static boolean operacionesInterioresFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& !vat.isFarmerRegime()
			&& !vat.isRectification()
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	private static boolean importacionesFilter(VatContext vat, Mod303 mod) {
		boolean basicFilter = vat.isVatGeneralRegime(mod.getDefaultVATRegime()) 
				&& !vat.isVatSurchargeRegime() 
				&& !vat.isService();
		if (basicFilter && (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase())) {
			if (vat.getTaxDate().before( IVA_2021_CHANGE_DATE )) {
				basicFilter = true;
			} else {
				basicFilter = vat.hasDuaLinked() || vat.isVatImportation();
			}
			return basicFilter; 
		}
		return false;
	}

	private static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isFarmerRegime() && vat.isNationalPurchase();		
	}
	private static boolean rectificationDeduccionesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
				&& vat.isRectification() && (vat.isPurchase() || vat.isExpenses()); 
	}

	@Override
	public Mod303Key[] getCompensationExplainKeys() {
		return new Mod303Key[] {Mod303Key.GP_C029};
	}
	
	@Override
	protected String getCompensationExplain( AONContext ctx, Mod303 mod303, Mod303Key key) {
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
		MutableDouble sum = new MutableDouble();
		MutableBoolean something = new MutableBoolean(false);
		buf.append( MessageFormat.format(styledTag, "table cellspacing=\"0\"",  blockCenter+marginTop+border ) )
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+bold+fontLarger+border) )
					.append("Casilla " + key.getBoxFormatted())
				.append("</td>")
			.append("</tr>");
		if (mod303 .isFirstPeriod()) {
			Mod390HFDAO.getLastPeriodEffectiveModels(ctx, mod303)
				.filter(Mod390HF::isToCompensate)
				.map(fm -> { 
					something.setValue(true); 
					sum.add(fm.getDeclarationResult());
					return fm;
				})
				.forEach( fm -> painTableRow(buf,fm) )
				;
		} else {
			Mod303DAO.getLastPeriodEffectiveModels(ctx, mod303)
				.filter(Mod303::isToCompensate)
				.map(fm -> { 
					something.setValue(true); 
					sum.add(fm.getDeclarationResult());
					return fm;
				})
				.forEach( fm -> painTableRow(buf,fm) )
			;
		}
		if (something.getValue().booleanValue()) {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td",  bold+fontLarger+border) )
					.append("Total")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+fontLarger+border) )
					.append(DEC2.format(AonMathUtils.round(sum.doubleValue())))
				.append("</td>")
			.append("</tr>");
		} else {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+fontLarger+border) )
					.append("No se ha encontrado cantidad a compensar en el modelo anterior.")
				.append("</td>")
			.append("</tr>");
		}
		buf.append("</div>");
		buf.append("</div>");
		return buf.toString();
	}
	private <T extends FiscalModel> void painTableRow(StringBuilder buf, T fm) {
		buf.append("<tr>")
			.append( MessageFormat.format(styledTag, "td", paddingLeft+border+noWrap) )
				.append("Resultado de la liquidaci\u00F3n " 
					+ fm.getModelFullName()
					+ AonObjectUtils.defaultIfNull(fm.getDeclarationResultType(), t -> " (" + t.getDescription() + ")"))
			.append("</td>")
			.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
				.append(DEC2.format(fm.getDeclarationResult()))
			.append("</td>")
		.append("</tr>");
	}
	
}
