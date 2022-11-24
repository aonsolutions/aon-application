package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390HF.Mod390HFDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class Mod303ARABA2022Declaration extends Mod303ARABA {
	
	protected Mod303ARABA2022Declaration() {
		
	}
	
	public static final double PERCENT1 = 4.0;
	public static final double PERCENT2 = 10.0;
	public static final double PERCENT3 = 21.0;
	public static final double SURCHARGE_PERCENT1 = 0.5;
	public static final double SURCHARGE_PERCENT2 = 1.4;
	public static final double SURCHARGE_PERCENT3 = 5.2;
	
	public static boolean accept(Mod303 mod) {
		return  mod.isAraba() && mod.getYear() > 2021;
	}
	private static final Mod303Key[] PRORATE_KEYS = new Mod303Key[]{
		 Mod303Key.AR_C030,Mod303Key.AR_C031,Mod303Key.AR_C032
		,Mod303Key.AR_C033,Mod303Key.AR_C034,Mod303Key.AR_C035,Mod303Key.AR_C036
	};
	
	private enum Mod303KeyDAO implements IMod303KeyDAO {
		 AR_C907	(Mod303Key.AR_C907)
	    ,AR_C930	(Mod303Key.AR_C930)
		,CM_002(Mod303Key.CM_002,null,null,(ctx,mod) -> add(Mod303Key.CM_002,mod,(
				 AonStringUtils.equals(AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY),AonStringUtils.ONE))?1:0),null,null)
		,CM_003		(Mod303Key.CM_003)
		,AR_C910	(Mod303Key.AR_C910,null,null,(ctx,mod) -> add(Mod303Key.AR_C910,mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment()?1:0),null,null)
		,AR_C911	(Mod303Key.AR_C911)
		,AR_C908	(Mod303Key.AR_C908)
		,AR_C909	(Mod303Key.AR_C909)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al primer tipo.
		,AR_C001	(Mod303Key.AR_C001
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C001,mod,vat.getBase())
			,null,null,null)
		,AR_C002	(Mod303Key.AR_C002,null,null,(ctx,mod) -> add(Mod303Key.AR_C002,mod,PERCENT1),null,null)
		,AR_C003	(Mod303Key.AR_C003
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C003,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al segundo tipo.
		,AR_C204	(Mod303Key.AR_C204
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			 ,(ctx,mod,vat) -> add(Mod303Key.AR_C204,mod,vat.getBase())
				,null,null,null)
		,AR_C205	(Mod303Key.AR_C205,null,null,(ctx,mod) -> add(Mod303Key.AR_C205,mod,PERCENT2),null,null)
		,AR_C206	(Mod303Key.AR_C206
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C206,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al tercer tipo.
		,AR_C207	(Mod303Key.AR_C207
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			 ,(ctx,mod,vat) -> add(Mod303Key.AR_C207,mod,vat.getBase())
			,null,null,null)
		,AR_C208	(Mod303Key.AR_C208,null,null,(ctx,mod) -> add(Mod303Key.AR_C208,mod,PERCENT3),null,null)
		,AR_C209	(Mod303Key.AR_C209
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C209,mod,vat.getQuota())
			,null,null,null)
		
		// Modificación bases y cuotas
		,AR_C370	(Mod303Key.AR_C370
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.AR_C370,mod,vat.getBase())
			,null,null,null)
		,AR_C371	(Mod303Key.AR_C371
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C371,mod,vat.getQuota())
			,null,null,null)
		
		// Otras operaciones con inversión del sujeto pasivo (excepto. adq. intracom). Base y cuota
		,AR_C372	(Mod303Key.AR_C372
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C372,mod,vat.getBase())
			,null,null,null)
		,AR_C373	(Mod303Key.AR_C373
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C373,mod,vat.getQuota())
			,null,null,null)
		
		// Recargo equivalencia al primer tipo.
		,AR_C010	(Mod303Key.AR_C010
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C010,mod,vat.getBase())
			,null,null,null)
		,AR_C011	(Mod303Key.AR_C011,null,null,(ctx,mod) -> add(Mod303Key.AR_C011,mod,SURCHARGE_PERCENT1),null,null)
		,AR_C012	(Mod303Key.AR_C012
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C012,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al segundo tipo.
		,AR_C213	(Mod303Key.AR_C213
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C213,mod,vat.getBase())
			,null,null,null)
		,AR_C214	(Mod303Key.AR_C214,null,null,(ctx,mod) -> add(Mod303Key.AR_C214,mod,SURCHARGE_PERCENT2),null,null)
		,AR_C215	(Mod303Key.AR_C215
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C215,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al tercer tipo.
		,AR_C216	(Mod303Key.AR_C216
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C216,mod,vat.getBase())
			,null,null,null)
		,AR_C217	(Mod303Key.AR_C217,null,null,(ctx,mod) -> add(Mod303Key.AR_C217,mod,SURCHARGE_PERCENT3),null,null)
		,AR_C218	(Mod303Key.AR_C218
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C218,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Modificaciones bases y cuotas del recargo de equivalencia
		,AR_C374	(Mod303Key.AR_C374
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.AR_C374,mod,vat.getBase())
			,null,null,null)
		,AR_C375	(Mod303Key.AR_C375
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.AR_C375,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias al primer tipo.		
		,AR_C019	(Mod303Key.AR_C019
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C019,mod,vat.getBase())
			,null,null,null)
		,AR_C020	(Mod303Key.AR_C020,null,null,(ctx,mod) -> add(Mod303Key.AR_C020,mod,PERCENT1),null,null)
		,AR_C021	(Mod303Key.AR_C021
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C021,mod,vat.getQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias al segundo tipo.		
		,AR_C222	(Mod303Key.AR_C222
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C222,mod,vat.getBase())
			,null,null,null)
		,AR_C223	(Mod303Key.AR_C223,null,null,(ctx,mod) -> add(Mod303Key.AR_C223,mod,PERCENT2),null,null)
		,AR_C224	(Mod303Key.AR_C224
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C224,mod,vat.getQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias al tercer tipo.		
		,AR_C225	(Mod303Key.AR_C225
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C225,mod,vat.getBase())
			,null,null,null)
		,AR_C226	(Mod303Key.AR_C226,null,null,(ctx,mod) -> add(Mod303Key.AR_C226,mod,PERCENT3),null,null)
		,AR_C227	(Mod303Key.AR_C227
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C227,mod,vat.getQuota())
			,null,null,null)
		
		// Modificaciones bases y cuotas Adq. Intrac.
		,AR_C376	(Mod303Key.AR_C376
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.AR_C376,mod,vat.getBase())
			,null,null,null)
		,AR_C377	(Mod303Key.AR_C377
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.AR_C377,mod,vat.getQuota())
			,null,null,null)
		
		// TOTAL CUOTA DEVENGADA
		,AR_C028	(Mod303Key.AR_C028,null,null,null,"AR_C003+AR_C206+AR_C209+AR_C371+AR_C373+AR_C012+AR_C215+AR_C218+AR_C375+AR_C021+AR_C224+AR_C227+AR_C377",null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------

		// IVA deducible en operaciones interiores de bienes y servicios corrientes
		,AR_C030	(Mod303Key.AR_C030
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.AR_C030,mod,vat)
			,null,null,null)
		
		// IVA deducible en operaciones interiores de bienes de inversión
		,AR_C031	(Mod303Key.AR_C031
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat )
			,(ctx,mod,vat) -> addProrrated(Mod303Key.AR_C031,mod,vat)
			,null,null,null)
		
		// IVA deducible en importaciones de bienes corrientes
		,AR_C032	(Mod303Key.AR_C032
			,(mod,vat) -> importacionesCorrientesFilter(vat,mod)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.AR_C032,mod,vat)
			,null,null,null)
		
		// IVA deducible en importaciones de bienes de inversión
		,AR_C033	(Mod303Key.AR_C033
			,(mod,vat) -> importacionesInversionFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.AR_C033,mod,vat)
			,null,null,null)
		
		// IVA deducible en adquisiciones intracomunitarias de bienes y servicios corrientes
		,AR_C034	(Mod303Key.AR_C034
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.AR_C034,mod,vat)
			,null,null,null)
		
		// IVA deducible en adquisiciones intracomunitarias de bienes de inversión
		,AR_C035	(Mod303Key.AR_C035
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.AR_C035,mod,vat)
			,null,null,null)
		
		// Compensaciones Régimen Especial A.G. y P .
		,AR_C036	(Mod303Key.AR_C036
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.AR_C036,mod,vat)
			,null,null,null)
		
		// Regularización Inversiones
		,AR_C037	(Mod303Key.AR_C037)
		
		// Rectificacion de deducciones
		,AR_C046	(Mod303Key.AR_C046
			,(mod,vat) -> rectificationDeduccionesFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C046,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// TOTAL A DEDUCIR
		,AR_C038	(Mod303Key.AR_C038,null,null,null,"AR_C030+AR_C031+AR_C032+AR_C033+AR_C034+AR_C035+AR_C036+AR_C037+AR_C046",null)
		
		// -----------------------------------------------------------
		// ------------------------------------------------- RESULTADO
		// -----------------------------------------------------------
		
		// DIFERENCIA
		,AR_C039	(Mod303Key.AR_C039,null,null,null,"AR_C028-AR_C038",null)
		
		// Volumen operaciones. % ÁLAVA
		,AR_C040	(Mod303Key.AR_C040,null,null,(ctx,mod) -> add(Mod303Key.AR_C040,mod,100.0),null,null)

		// Volumen operaciones. % GIPUZKOA
		,AR_C041	(Mod303Key.AR_C041)
		
		// Volumen operaciones. % BIZKAIA
		,AR_C042	(Mod303Key.AR_C042)
		
		// Volumen operaciones. % RESTO
		,AR_C043	(Mod303Key.AR_C043)
		
		// Cuota atribuible al Territorio Histórico de Álava	
		,AR_C044	(Mod303Key.AR_C044,null,null,null,"AR_C039*AR_C040/100",null)

		// Cuotas a compensar de períodos anteriores en el Territorio Histórico de Álava	
		,AR_C045	(Mod303Key.AR_C045,null,null,
			(ctx,mod) -> {
				if (mod.isFirstPeriod()) {
					// Primer periodo. Se busca la cuota a compensar del último periodo del ejercicio anterior.
					add( Mod303Key.AR_C045, mod, 
							Mod390HFDAO.getMod390HFs( ctx,ctx.getDomainId() )
							.filter(m390 -> m390.getYear() ==  (mod.getYear() - 1) )
							.filter(m390 -> m390.getAdministration() ==  mod.getAdministration() )
							.filter(Mod390HF::isToCompensate)
							.mapToDouble(fm -> AonMathUtils.round(fm.getAmount(Mod390Key.AR_C140)))
							.findFirst()
							.orElse(0.0));						
				} else {
					// Resto de periodos. Se busca la cuota a compensar del anterior periodo..
					add( Mod303Key.AR_C045, mod, 
							Mod303DAO.getMod303s( ctx,ctx.getDomainId() )
							.filter(m303 -> m303.getYear() == mod.getYear() )
							.filter(m303 -> m303.getPeriod().ordinal() == (mod.getPeriod().ordinal() - 1) )  
							.filter(Mod303::isToCompensate)
							.mapToDouble(fm -> AonMathUtils.round(fm.getAmount(Mod303Key.AR_C082)))
							.findFirst()
							.orElse(0.0));						
				}
			}
			,null
			,"{messages : ["
			+"@if{ mod.isFirstPeriod() }"
				+"@code{c140Key='"+ Mod390Key.AR_C140.getValue() +"';}"
				+ "\"Declaraciones del modelo 390 del ejercicio anterior:\"," 
				+"@foreach{fm : hf390models}"
					+"@if{ fm.getYear() == (mod.getYear() - 1) && fm.getAdministration() == mod.getAdministration() }"
						+ "\"Resultado A compensar @{fm.isComplementary()?' (C) ':'     '}:	Casilla [140] --> @{fm.getAmount(c140Key)}\","
					+"@end{}"
				+"@end{}"
			+"@else{}"
				+"@code{c082Key='"+ Mod303Key.AR_C082.getValue() +"';}"
				+ "\"Declaraciones del periodo anterior:\"," 
				+"@foreach{fm : lastPeriodModels}" 
					+"@if{ fm.getPeriod().ordinal() == (mod.getPeriod().ordinal() - 1) }"
						+ "\"Resultado a compensar @{fm.getPeriod().getName()}@{fm.isComplementary()?' (C) ':'     '}:	Casilla [082] --> @{fm.getAmount(c082Key)}\","
					+"@end{}"
				+"@end{}"
			+"@end{}"
			+ "\"Resultado (Cuotas a compensar de periodos anteriores): @{AR_C045}\","
			+"]}"
		)

		// RESULTADO DE LA AUTOLIQUIDACIÓN	
		,AR_C060	(Mod303Key.AR_C060,null,null,null,"AR_C044-AR_C045",null)

		// Recargo presentación extemporánea	IVA deducible por importaciones de bienes corrientes
		,AR_C061	(Mod303Key.AR_C061)

		// Intereses demora	IVA deducible por importaciones de bienes de inversión
		,AR_C062	(Mod303Key.AR_C062)
		
		// A deducir (exclusivamente en el caso de autoliquidación sustitutiva: resultado de las autoliquidaciones anteriores presentadas por el mismo concepto, ejercicio y período)
		,AR_C063	(Mod303Key.AR_C063,null,null,
				(ctx,mod) -> {
					if (mod.isReplacement()) {
						add( Mod303Key.AR_C063, mod, 
							Mod303DAO.getSamePeriodModels(ctx, mod)
								.mapToDouble(Mod303::getDeclarationResult)
								.sum());						
					}
				}
				,null
				,"{messages : ["
					+ "\"Declaraciones en el mismo periodo/ejercicio:\"," 
					+"@foreach{fm : periodModels}" 
						+ "\"Resultado @{fm.getPeriod().getName()}@{fm.isComplementary()?' (C) ':'     '}:	Casilla [080] --> @{fm.getDeclarationResult()}\","
					+"@end{}"
					+ "\"Resultado: @{AR_C080}\","
				+"]}"
		)
		
		// TOTAL DEUDA TRIBUTARIA	
		,AR_C080	(Mod303Key.AR_C080,null,null,null,"AR_C060+AR_C061+AR_C062-AR_C063",null)
		
		// TOTAL A DEVOLVER
		,AR_C081	(Mod303Key.AR_C081,null,null,null,"isToPayback()?round(AR_C080*-1):0.0",null)
				
		// TOTAL A COMPENSAR
		,AR_C082	(Mod303Key.AR_C082,null,null,null,"isToCompensate()?round(AR_C080*-1):0.0",null)				
		
		// -----------------------------------------------------------
		// ------------------------------------- INFORMACION ADICIONAL
		// -----------------------------------------------------------
		
		// Total entregas de bienes y prestaciones de servicios intracomunitarias
		,AR_C050	(Mod303Key.AR_C050
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales()
			,(ctx,mod,vat) -> add(Mod303Key.AR_C050,mod,vat.getBase())
			,null,null,null)
		
		// Total exportaciones y operaciones asimiladas
		,AR_C051	(Mod303Key.AR_C051
			,(mod,vat) -> ventasExtraComunitariasCanCeuBienes(vat, mod)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C051,mod,vat.getBase())
			,null,null,null)
		// Operaciones no sujetas por reglas de localizaci\u00F3n (excepto las incluidas en la casilla 56)
		,AR_C054(Mod303Key.AR_C054
			,(mod,vat) -> ventasExtraComunitariasCanCeuServicios(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C054,mod,vat.getBase())
			,null,null,null)
		// Operaciones sujetas con inversi\u00F3n del sujeto pasivo
		,AR_C055(Mod303Key.AR_C055
			,(mod,vat) -> ventasISP (vat, mod)
			,(ctx,mod,vat) -> add(Mod303Key.AR_C055,mod,vat.getBase())
			,null,null,null)
		
		// Operaciones no sujetas por reglas de localizaci\u00F3n acogidas a la OSS
		,AR_C056(Mod303Key.AR_C056)
		// Operaciones sujetas y acogidas a la OSS
		,AR_C058(Mod303Key.AR_C058)
		
		// Operaciones no sujetas o con inversión del sujeto pasivo que originan el derecho a deducción
		,AR_C052	(Mod303Key.AR_C052
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isOtherISPSales()  
			,(ctx,mod,vat) -> add(Mod303Key.AR_C052,mod,vat.getBase())
			,null,null,null)
		
		// Importes de las ventas a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran 
		// resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA		
		,AR_C180	(Mod303Key.AR_C180, (mod, vat) -> vat.isSales() && vat.isVatAccrualRegime()
			, null, null, null, null)
		,AR_C181	(Mod303Key.AR_C181, (mod, vat) -> vat.isSales() && vat.isVatAccrualRegime()
			, null, null, null, null)		
		// Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el 
		// régimen especial del criterio de caja
		,AR_C182	(Mod303Key.AR_C182, (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime()
			, null, null, null, null)
		,AR_C183	(Mod303Key.AR_C183, (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime()
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
	private static boolean operacionesInterioresCorrientesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& !vat.isInvestment() && !vat.isFarmerRegime() && !vat.isRectification() 
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	private static boolean operacionesInterioresInversionFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isInvestment() && !vat.isFarmerRegime() && !vat.isRectification() 
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	
	private static boolean commonImportacionesInversionFilter(VatContext vat, Mod303 mod) {
		boolean basicFilter = vat.isVatGeneralRegime(mod.getDefaultVATRegime()) 
				&& !vat.isVatSurchargeRegime() 
				&& !vat.isRectification() 
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
	private static boolean importacionesCorrientesFilter(VatContext vat, Mod303 mod) {
		return commonImportacionesInversionFilter(vat, mod) 
				&& !vat.isInvestment();
	}

	private static boolean importacionesInversionFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isInvestment() && !vat.isRectification() 
			&& !vat.isService()
			&& (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase());
	}
	private static boolean adqIntracomunitariasCorrientesFilter(VatContext vat) {
		return !vat.isInvestment() && adqIntracomunitariasFilter(vat) && !vat.isRectification();
	}
	private static boolean adqIntracomunitariasInversionFilter(VatContext vat) {
		return vat.isInvestment() && adqIntracomunitariasFilter(vat) && !vat.isRectification();
	}
	
	private static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isRectification() 
			&& vat.isFarmerRegime() && vat.isNationalPurchase();		
	}
	
	private static boolean rectificationDeduccionesFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
			&& vat.isRectification() && (vat.isPurchase() || vat.isExpenses()); 
	}
	
	public static boolean  ventasExtraComunitariasCanCeuBienes(VatContext vat, Mod303 mod) {
		return !vat.isVatSurchargeRegime() && !vat.isService() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales());
	}
	public static boolean  ventasExtraComunitariasCanCeuServicios(VatContext vat, Mod303 mod) {
		boolean add = !vat.isVatSurchargeRegime() 
			&& ((vat.isService() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales())));
		if ( add && mod.getYear() == 2021) {
			add = FiscalUtils.isInPeriodRange(mod, vat.getTaxDate());
		} 
		return add;
	}
	public static boolean  ventasISP(VatContext vat, Mod303 mod) {
		return !vat.isVatSurchargeRegime() && vat.isOtherISPSales();
	}
	
}

