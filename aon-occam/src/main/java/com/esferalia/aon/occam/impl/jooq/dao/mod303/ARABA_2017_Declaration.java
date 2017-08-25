package com.esferalia.aon.occam.impl.jooq.dao.mod303;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod303DAO;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ARABA_2017_Declaration extends Mod303Declaration {
	
	protected ARABA_2017_Declaration() {
		
	}
	
	private static final double PERCENT1 = 4.0;
	private static final double PERCENT2 = 10.0;
	private static final double PERCENT3 = 21.0;
	private static final double SURCHARGE_PERCENT1 = 0.5;
	private static final double SURCHARGE_PERCENT2 = 1.4;
	private static final double SURCHARGE_PERCENT3 = 5.2;
	
	public static boolean accept(Mod303 mod) {
		return  mod.isAraba() && mod.getYear() >= 2017;
	}
	private static final Mod303Key[] PRORATE_KEYS = new Mod303Key[]{
		 Mod303Key.AR_C030,Mod303Key.AR_C031,Mod303Key.AR_C032
		,Mod303Key.AR_C033,Mod303Key.AR_C034,Mod303Key.AR_C035,Mod303Key.AR_C036
	};
	
	private static enum Mod303KeyDAO implements IMod303KeyDAO {
		 AR_C907	(Mod303Key.AR_C907)
		,CM_002(Mod303Key.CM_002,false,null,null,(ctx,mod) -> add(Mod303Key.CM_002,mod,(
				 AonStringUtils.equals(AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY),AonStringUtils.ONE))?1:0),null,null)
		,CM_003		(Mod303Key.CM_003)
		,AR_C910	(Mod303Key.AR_C910,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C910,mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment()?1:0),null,null)
		,AR_C911	(Mod303Key.AR_C911)
		,AR_C908	(Mod303Key.AR_C908)
		,AR_C909	(Mod303Key.AR_C909)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al primer tipo.
		,AR_C001	(Mod303Key.AR_C001,true
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C001,mod,vat.getBase())
			,null,null,null)
		,AR_C002	(Mod303Key.AR_C002,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C002,mod,PERCENT1),null,null)
		,AR_C003	(Mod303Key.AR_C003,true
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C003,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al segundo tipo.
		,AR_C204	(Mod303Key.AR_C204,true
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			 ,(ctx,mod,vat) -> add(Mod303Key.AR_C204,mod,vat.getBase())
				,null,null,null)
		,AR_C205	(Mod303Key.AR_C205,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C205,mod,PERCENT2),null,null)
		,AR_C206	(Mod303Key.AR_C206,true
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C206,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al tercer tipo.
		,AR_C207	(Mod303Key.AR_C207,true
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			 ,(ctx,mod,vat) -> add(Mod303Key.AR_C207,mod,vat.getBase())
			,null,null,null)
		,AR_C208	(Mod303Key.AR_C208,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C208,mod,PERCENT3),null,null)
		,AR_C209	(Mod303Key.AR_C209,true
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C209,mod,vat.getQuota())
			,null,null,null)
		
		// Modificación bases y cuotas
		,AR_C370	(Mod303Key.AR_C370,true
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.AR_C370,mod,vat.getBase())
			,null,null,null)
		,AR_C371	(Mod303Key.AR_C371,true
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C371,mod,vat.getQuota())
			,null,null,null)
		
		// Otras operaciones con inversión del sujeto pasivo (excepto. adq. intracom). Base y cuota
		,AR_C372	(Mod303Key.AR_C372,true
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C372,mod,vat.getBase())
			,null,null,null)
		,AR_C373	(Mod303Key.AR_C373,true
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C373,mod,vat.getQuota())
			,null,null,null)
		
		// Recargo equivalencia al primer tipo.
		,AR_C010	(Mod303Key.AR_C010,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C010,mod,vat.getBase())
			,null,null,null)
		,AR_C011	(Mod303Key.AR_C011,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C011,mod,SURCHARGE_PERCENT1),null,null)
		,AR_C012	(Mod303Key.AR_C012,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C012,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al segundo tipo.
		,AR_C213	(Mod303Key.AR_C213,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C213,mod,vat.getBase())
			,null,null,null)
		,AR_C214	(Mod303Key.AR_C214,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C214,mod,SURCHARGE_PERCENT2),null,null)
		,AR_C215	(Mod303Key.AR_C215,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C215,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al tercer tipo.
		,AR_C216	(Mod303Key.AR_C216,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C216,mod,vat.getBase())
			,null,null,null)
		,AR_C217	(Mod303Key.AR_C217,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C217,mod,SURCHARGE_PERCENT3),null,null)
		,AR_C218	(Mod303Key.AR_C218,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C218,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Modificaciones bases y cuotas del recargo de equivalencia
		,AR_C374	(Mod303Key.AR_C374,true
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.AR_C374,mod,vat.getBase())
			,null,null,null)
		,AR_C375	(Mod303Key.AR_C375,true
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.AR_C375,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias al primer tipo.		
		,AR_C019	(Mod303Key.AR_C019,true
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C019,mod,vat.getBase())
			,null,null,null)
		,AR_C020	(Mod303Key.AR_C020,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C020,mod,PERCENT1),null,null)
		,AR_C021	(Mod303Key.AR_C021,true
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C021,mod,vat.getQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias al segundo tipo.		
		,AR_C222	(Mod303Key.AR_C222,true
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C222,mod,vat.getBase())
			,null,null,null)
		,AR_C223	(Mod303Key.AR_C223,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C223,mod,PERCENT2),null,null)
		,AR_C224	(Mod303Key.AR_C224,true
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C222,mod,vat.getQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias al tercer tipo.		
		,AR_C225	(Mod303Key.AR_C225,true
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C225,mod,vat.getBase())
			,null,null,null)
		,AR_C226	(Mod303Key.AR_C226,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C226,mod,PERCENT3),null,null)
		,AR_C227	(Mod303Key.AR_C227,true
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C227,mod,vat.getQuota())
			,null,null,null)
		
		// Modificaciones bases y cuotas Adq. Intrac.
		,AR_C376	(Mod303Key.AR_C376,true
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.AR_C376,mod,vat.getBase())
			,null,null,null)
		,AR_C377	(Mod303Key.AR_C377,true
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.AR_C377,mod,vat.getQuota())
			,null,null,null)
		
		// TOTAL CUOTA DEVENGADA
		,AR_C028	(Mod303Key.AR_C028,false,null,null,null,"AR_C003+AR_C206+AR_C209+AR_C371+AR_C373+AR_C012+AR_C215+AR_C218+AR_C375+AR_C021+AR_C224+AR_C227+AR_C377",null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------

		// IVA deducible en operaciones interiores de bienes y servicios corrientes
		,AR_C030	(Mod303Key.AR_C030,true
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C030,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en operaciones interiores de bienes de inversión
		,AR_C031	(Mod303Key.AR_C031,true
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat )
			,(ctx,mod,vat) -> add(Mod303Key.AR_C031,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en importaciones de bienes corrientes
		,AR_C032	(Mod303Key.AR_C032,true
			,(mod,vat) -> importacionesCorrientesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C032,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en importaciones de bienes de inversión
		,AR_C033	(Mod303Key.AR_C033,true
			,(mod,vat) -> importacionesInversionFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C033,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en adquisiciones intracomunitarias de bienes y servicios corrientes
		,AR_C034	(Mod303Key.AR_C034,true
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C034,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en adquisiciones intracomunitarias de bienes de inversión
		,AR_C035	(Mod303Key.AR_C035,true
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C035,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Compensaciones Régimen Especial A.G. y P .
		,AR_C036	(Mod303Key.AR_C036,true
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C036,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Regularización Inversiones
		,AR_C037	(Mod303Key.AR_C037)
		
		// TOTAL A DEDUCIR
		,AR_C038	(Mod303Key.AR_C038,false,null,null,null,"AR_C030+AR_C031+AR_C032+AR_C033+AR_C034+AR_C035+AR_C036+AR_C037",null)
		
		// -----------------------------------------------------------
		// ------------------------------------------------- RESULTADO
		// -----------------------------------------------------------
		
		// DIFERENCIA
		,AR_C039	(Mod303Key.AR_C039,false,null,null,null,"AR_C028-AR_C038",null)
		
		// Volumen operaciones. % ÁLAVA
		,AR_C040	(Mod303Key.AR_C040,false,null,null,(ctx,mod) -> add(Mod303Key.AR_C040,mod,100.0),null,null)

		// Volumen operaciones. % GIPUZKOA
		,AR_C041	(Mod303Key.AR_C041)
		
		// Volumen operaciones. % BIZKAIA
		,AR_C042	(Mod303Key.AR_C042)
		
		// Volumen operaciones. % RESTO
		,AR_C043	(Mod303Key.AR_C043)
		
		// Cuota atribuible al Territorio Histórico de Álava	
		,AR_C044	(Mod303Key.AR_C044,false,null,null,null,"AR_C039*AR_C040/100",null)

		// Cuotas a compensar de períodos anteriores en el Territorio Histórico de Álava	
		,AR_C045	(Mod303Key.AR_C045,false,null,null,
				(ctx,mod) -> {
					add( Mod303Key.AR_C045, mod, 
						Mod303DAO.getLastPeriodModels(ctx, mod)
						.filter(fm -> AonObjectUtils.equals( fm.getDescription(Mod303Key.CM_004),FiscalModelDeclarationType.COMPENSATE.getValue()))
						.mapToDouble(fm -> fm.getAmount(Mod303Key.AR_C080))
						.findFirst()
						.orElse(0.0));						
				}
				,null
				,"<li>Declaraciones del periodo anterior:<ul style=\"padding-left: 20px;\">" 
				+"@code{c80Key='"+ Mod303Key.AR_C080.getValue() +"';}"
				+"@code{cm04Key='"+ Mod303Key.CM_004.getValue() +"';}"
				+"@code{compensateValue='"+ FiscalModelDeclarationType.COMPENSATE.getValue() +"';}"
				+"@foreach{fm : lastPeriodModels}"
					+"@if{ fm.getDescription(cm04Key) == compensateValue}"
						+"<li>Resultado @{fm.getPeriod().getName()}@{fm.isComplementary()?' (C) ':'     '}:	Casilla [080] --> @{fm.getAmount(c80Key)}</li>"
					+"@end{}"
				+"@end{}"
				+"</ul></li>"
				+"<li>Resultado: <b>@{AR_C045}</b></li>"
			)

		// RESULTADO DE LA AUTOLIQUIDACIÓN	
		,AR_C060	(Mod303Key.AR_C060,false,null,null,null,"AR_C044-AR_C045",null)

		// Recargo presentación extemporánea	IVA deducible por importaciones de bienes corrientes
		,AR_C061	(Mod303Key.AR_C061)

		// Intereses demora	IVA deducible por importaciones de bienes de inversión
		,AR_C062	(Mod303Key.AR_C062)
		
		// A deducir (exclusivamente en el caso de autoliquidación sustitutiva: resultado de las autoliquidaciones anteriores presentadas por el mismo concepto, ejercicio y período)
		,AR_C063	(Mod303Key.AR_C063,false,null,null,
				(ctx,mod) -> {
					if (mod.isComplementary()) {
						add( Mod303Key.AR_C063, mod, Mod303DAO.getSamePeriodModels(ctx, mod).mapToDouble(fm -> fm.getAmount(Mod303Key.AR_C080)).sum());						
					}
				}
				,null
				,"<li>Declaraciones en el mismo periodo/ejercicio:<ul style=\"padding-left: 20px;\">" 
				+"@code{c80Key='"+ Mod303Key.AR_C080.getValue() +"';}"
				+"@foreach{fm : periodModels}" 
					+"<li>Resultado @{fm.getPeriod().getName()}@{fm.isComplementary()?' (C) ':'     '}:	Casilla [080] --> @{fm.getAmount(c80Key)}</li>"
				+"@end{}"
				+"</ul></li>"
				+"<li>Resultado: <b>@{AR_C080}</b></li>"
		)
		
		// TOTAL DEUDA TRIBUTARIA	
		,AR_C080	(Mod303Key.AR_C080,false,null,null,null,"AR_C060+AR_C061+AR_C062-AR_C063",null)
		
		// -----------------------------------------------------------
		// ------------------------------------- INFORMACION ADICIONAL
		// -----------------------------------------------------------
		
		// Total entregas de bienes y prestaciones de servicios intracomunitarias
		,AR_C050	(Mod303Key.AR_C050,true
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales()
			,(ctx,mod,vat) -> add(Mod303Key.AR_C050,mod,vat.getBase())
			,null,null,null)
		
		// Total exportaciones y operaciones asimiladas
		,AR_C051	(Mod303Key.AR_C051,true
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales())
			,(ctx,mod,vat) -> add(Mod303Key.AR_C051,mod,vat.getBase())
			,null,null,null)
		
		// Operaciones no sujetas o con inversión del sujeto pasivo que originan el derecho a deducción
		,AR_C052	(Mod303Key.AR_C052,true
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isOtherISPSales()  
			,(ctx,mod,vat) -> add(Mod303Key.AR_C052,mod,vat.getBase())
			,null,null,null)
		
		// Importes de las ventas a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran 
		// resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA		
		,AR_C180	(Mod303Key.AR_C180,true,null,null,(ctx,mod) -> add(Mod303Key.AR_C180,mod,Mod303DAO.getVatAccrualPaymentOutputBase(ctx,mod)),null,null)
		,AR_C181	(Mod303Key.AR_C181,true,null,null,(ctx,mod) -> {
				double quota = Mod303DAO.getVatAccrualPaymentOutputQuota(ctx,mod);
				add( Mod303Key.AR_C181, mod, quota );
			}
		,null,null)
		
		// Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el 
		// régimen especial del criterio de caja
		,AR_C182	(Mod303Key.AR_C182,true,null,null,(ctx,mod) -> add(Mod303Key.AR_C182,mod,Mod303DAO.getVatAccrualPaymentInputBase(ctx,mod)),null,null)
		,AR_C183	(Mod303Key.AR_C183,true,null,null,(ctx,mod) -> {
			double quota = Mod303DAO.getVatAccrualPaymentInputQuota(ctx,mod);
			add(Mod303Key.AR_C183,mod, quota );
			add( Mod303Key.AR_C911, mod, AonMathUtils.isZero(quota)?(0.0):(1.0)); 
			}
		,null,null)
		;
		
		private Mod303Key key;
		private boolean diffEnabled;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;

		private Mod303KeyDAO(Mod303Key key) {
			this(key,false,null,null,null,null,null);			
		}
		private Mod303KeyDAO(Mod303Key key
				, boolean diffEnabled
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueFirstIntializer firstInitializer
				, String expression
				, String template) {
			this.key = key;
			this.diffEnabled = diffEnabled;
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
		public boolean isDiffEnabled() {
			return this.diffEnabled;
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
		public static Mod303KeyDAO safeValueOf(Mod303 mod, String key) {
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
	
	@Override
	public IMod303KeyDAO safeValueOf(Mod303 mod, String key) {
		return Mod303KeyDAO.safeValueOf(mod, key);
	}
	
	@Override
	public IMod303KeyDAO valueOf(String keyValue) {
		return Mod303KeyDAO.valueOf(keyValue);
	}
	@Override
	public Mod303Key[] getProrateKeys() {
		return PRORATE_KEYS;
	}
	
	//	-----------------------------------------------------------------------	
	//	--------------------------------------------------------------- FILTROS	
	//	-----------------------------------------------------------------------
	private static boolean isCommonNationalSales(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
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
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isRectification() && (vat.isNationalSales() || operacionesISPFilter(vat));		
	}
	private static boolean operacionesISPFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& (vat.isOtherISPPurchase() || vat.isOtherISPExpenses() || vat.isExtracommunityExpenses() || vat.isCanCeuMelExpenses());
	}
	private static boolean adqIntracomunitariasFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses());
	}
	private static boolean operacionesInterioresCorrientesFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& !vat.isInvestment() && !vat.isFarmerRegime()
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	private static boolean operacionesInterioresInversionFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isInvestment() && !vat.isFarmerRegime()
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	private static boolean importacionesCorrientesFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& !vat.isInvestment()  && (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase());
	}
	private static boolean importacionesInversionFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isInvestment() && (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase());
	}
	private static boolean adqIntracomunitariasCorrientesFilter(VatContext vat) {
		return !vat.isInvestment() && adqIntracomunitariasFilter(vat);
	}
	private static boolean adqIntracomunitariasInversionFilter(VatContext vat) {
		return vat.isInvestment() && adqIntracomunitariasFilter(vat);
	}
	
	private static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isFarmerRegime() && vat.isNationalPurchase();		
	}
}
