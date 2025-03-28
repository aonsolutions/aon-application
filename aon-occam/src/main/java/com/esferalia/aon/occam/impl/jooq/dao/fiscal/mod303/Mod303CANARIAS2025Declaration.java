package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.DEC2;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.border;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.fontLarger;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.paddingLeft;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.styledTag;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.textCenter;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.textRight;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.width150;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.width500;

import java.text.MessageFormat;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class Mod303CANARIAS2025Declaration extends Mod303CANARIAS {
	
//	@FunctionalInterface
//	private interface ISimplifiedRegimeActivityFiller {
//		void fill(Mod303 mod);
//	}
//
//	@FunctionalInterface
//	private interface ISimplifiedRegimeActivityPopulator {
//		void populate(Mod303 mod);
//	}
//
//	@FunctionalInterface
//	private interface ISimplifiedRegimeCopier {
//		void copy(Mod303 prev, Mod303 current);
//	}

	protected Mod303CANARIAS2025Declaration() {

	}
	
	// DEVENGADO: LOS POSIBLES PORCENTAJES DE IGIC QUE APARECEN EN EL PROGRAMA DE AYUDA SON 7 PERO EL MODELO SOLO PERMITE 6
	// PORCENTAJES DE IGIC: 0%, 3%, 5%, 7%, 9.5%, 15% Y 20%
	
	private static final double PERCENT0 = 0.0;
////	private static final double PERCENT2 = 2.0;
//	private static final double PERCENT4 = 4.0;
////	private static final double PERCENT5 = 5.0;
////	private static final double PERCENT75 = 7.5;
//	private static final double PERCENT10 = 10.0;
//	private static final double PERCENT21 = 21.0;
	
////	private static final double SURCHARGE_PERCENT_0 = 0;
////	private static final double SURCHARGE_PERCENT_026 = 0.26;
//	private static final double SURCHARGE_PERCENT_05 = 0.5;
////	private static final double SURCHARGE_PERCENT_062 = 0.62;
////	private static final double SURCHARGE_PERCENT_1 = 1;
//	private static final double SURCHARGE_PERCENT_14 = 1.4;
//	private static final double SURCHARGE_PERCENT_175 = 1.75;
//	private static final double SURCHARGE_PERCENT_52 = 5.2;
	
	public static boolean accept(Mod303 mod) {
		return (mod.isCanarias() && mod.getYear() >= 2025);
	}
	
	private static final Mod303Key[] COMPENSATION_EXPLAIN_KEYS = new Mod303Key[] { Mod303Key.CA_C043 };
	private static final Mod303Key[] SAME_PERIOD_EXPLAIN_KEYS = new Mod303Key[] { Mod303Key.CA_C044 };
	
	private static final Mod303Key[] PRORATE_KEYS = new Mod303Key[] { 
			Mod303Key.CA_C027, Mod303Key.CA_C029, Mod303Key.CA_C031, Mod303Key.CA_C033, 
			Mod303Key.CA_C035, Mod303Key.CA_C036 };

	private enum Mod303KeyDAO implements IMod303KeyDAO {

		 CM_003(Mod303Key.CM_003) // Porcentaje de prorrata
		,CM_007(Mod303Key.CM_007) // Porcentaje de prorrata antes de la regularización

//		,CT_A12(Mod303Key.CT_A12, null, null, (ctx, mod) -> set(Mod303Key.CT_A12, mod, 2), null, null, null, null,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_A12))
		,CM_002(Mod303Key.CM_002, null, null, (ctx, mod) -> add(Mod303Key.CM_002, mod,(AonStringUtils.equals(AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY),AonStringUtils.ONE)) ? 1 : 0),null, null) // Sujeto pasivo inscrito en el Registro de devolución mensual
//		,CT_A02(Mod303Key.CT_A02)
		,CA_X01(Mod303Key.CA_X01) // Autoliquidación conjunta
		,CA_X02(Mod303Key.CA_X02, null, null,(ctx, mod) -> add(Mod303Key.CA_X02, mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment() ? 1 : 0),null, null)  // Ha optado por el régimen especial del criterio de caja
		,CA_X03(Mod303Key.CA_X03) // Compras Criterio de caja. Se inicializa en la casilla 075. // FALTA
		,CA_X04(Mod303Key.CA_X04) // Es una entidad no establecida con obligaciones periódicas  // FALTA - IGUAL SE PODRIA COPIAR DEL PERIODO ANTERIOR                         
		,CA_X05(Mod303Key.CA_X05) // Ha sido declarado en concurso de acreedores en el presente período de liquidación 
		,CA_X06(Mod303Key.CA_X06) // Fecha en que se dictó el auto de declaración de concurso                          
		,CA_X07(Mod303Key.CA_X07) // Tipo de autoliquidación si declaración de concurso (preconcursal, postconcursal)  
//		,CA_X06(Mod303Key.CA_X08)
//		,CA_X13(Mod303Key.CT_A13, null, null, (ctx, mod) -> set(Mod303Key.CT_A13, mod, 2), null, null, null, null
//			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_A13))
//		,CT_A14(Mod303Key.CT_A14, null, null, (ctx, mod) -> set(Mod303Key.CT_A14, mod, 0), null, null, null, null
//			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_A14))
//		,CT_A11(Mod303Key.CT_A11)
		
		// ---------------------------------------------------------------
		// ----------------------------------- LIQUIDACION: IGIC DEVENGADO
		// ---------------------------------------------------------------

		// FALTA - PONER LAS CONDICIONES DE APLICAR LAS FACTURAS, SI LO HACEMOS POR LOS DISTINTOS PORCENTAJES O SI SE VAN ACUMULANDO CON LOS PORCENTAJES QUE SE VAYA ENCONTRANDO
		// POR AHORA VAMOS A SUPONER QUE SE HAGA POR PORCENTAJES, AUNQUE FALTA UN PORCENTAJE
		
		// Base imponible, porcentaje y cuota (1) 
		,
		CA_C001(Mod303Key.CA_C001, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C001, mod, vat.getBase()), null, null, null),
		CA_C002(Mod303Key.CA_C002, null, null, (ctx, mod) -> add(Mod303Key.CA_C002, mod, PERCENT0), null, null),
		CA_C003(Mod303Key.CA_C003, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C003, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (2) 
		,
		CA_C004(Mod303Key.CA_C004, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C004, mod, vat.getBase()), null, null, null),
		CA_C005(Mod303Key.CA_C005, null, null, (ctx, mod) -> add(Mod303Key.CA_C005, mod, PERCENT0), null, null),
		CA_C006(Mod303Key.CA_C006, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C006, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (3)
		,
		CA_C007(Mod303Key.CA_C007, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C007, mod, vat.getBase()), null, null, null),
		CA_C008(Mod303Key.CA_C008, null, null, (ctx, mod) -> add(Mod303Key.CA_C008, mod, PERCENT0), null, null),
		CA_C009(Mod303Key.CA_C009, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C009, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (4) 
		,
		CA_C010(Mod303Key.CA_C010, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C010, mod, vat.getBase()), null, null, null),
		CA_C011(Mod303Key.CA_C011, null, null, (ctx, mod) -> add(Mod303Key.CA_C011, mod, PERCENT0), null, null),
		CA_C012(Mod303Key.CA_C012, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C012, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (5) 
		,
		CA_C013(Mod303Key.CA_C013, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C013, mod, vat.getBase()), null, null, null),
		CA_C014(Mod303Key.CA_C014, null, null, (ctx, mod) -> add(Mod303Key.CA_C014, mod, PERCENT0), null, null),
		CA_C015(Mod303Key.CA_C015, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C015, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (6) 
		,
		CA_C016(Mod303Key.CA_C016, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C016, mod, vat.getBase()), null, null, null),
		CA_C017(Mod303Key.CA_C017, null, null, (ctx, mod) -> add(Mod303Key.CA_C017, mod, PERCENT0), null, null),
		CA_C018(Mod303Key.CA_C018, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_C018, mod, vat.getQuota()), null, null, null)

//		// Adquisiciones intracomunitarias de bienes y servicios. base y cuota.
//		,
//		CT_C10(Mod303Key.CT_C10, (mod, vat) -> adqIntracomunitariasFilterNoRECT(vat, mod),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C10, mod, vat.getBase()), null, null, null),
//		CT_C11(Mod303Key.CT_C11, (mod, vat) -> adqIntracomunitariasFilterNoRECT(vat, mod),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C11, mod, vat.getQuota()), null, null, null)

		// Operaciones con inversión del sujeto pasivo. Base y cuota
		,
		CA_C019(Mod303Key.CA_C019, (mod, vat) -> operacionesISPFilterNoRECT(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CA_C019, mod, vat.getBase()), null, null, null),
		CA_C020(Mod303Key.CA_C020, (mod, vat) -> operacionesISPFilterNoRECT(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CA_C020, mod, vat.getQuota()), null, null, null)

		// Modificación bases y cuotas. Base y cuota
		,
		CA_C021(Mod303Key.CA_C021, (mod, vat) -> modificacionBasesYCuotasFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CA_C021, mod, vat.getBase()), null, null, null),
		CA_C022(Mod303Key.CA_C022, (mod, vat) -> modificacionBasesYCuotasFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CA_C022, mod, vat.getQuota()), null, null, null)
		
		// Cuotas devueltas en régimen de viajeros. Base y cuota
		, CA_C023(Mod303Key.CA_C023)
		, CA_C024(Mod303Key.CA_C024)

//        // Recargo de equivalencia al 1.75%
//		
//		CT_C156(Mod303Key.CT_C156,
//				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent_175(vat),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C156, mod, vat.getBase()), null, null, null),
//		CT_C157(Mod303Key.CT_C157, null, null, (ctx, mod) -> add(Mod303Key.CT_C157, mod, SURCHARGE_PERCENT_175), null, null),
//		CT_C158(Mod303Key.CT_C158,
//				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent_175(vat),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C158, mod, vat.getSurchargeQuota()), null, null, null),
//
//		// Recargo de equivalencia al 0.50%
//		
//		CT_C168(Mod303Key.CT_C168,
//				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent_05(vat),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C168, mod, vat.getBase()), null, null, null),
//		CT_C169(Mod303Key.CT_C169, null, null, (ctx, mod) -> add(Mod303Key.CT_C169, mod, SURCHARGE_PERCENT_05), null, null),
//		CT_C170(Mod303Key.CT_C170,
//				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent_05(vat),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C170, mod, vat.getSurchargeQuota()), null, null, null),
//		
//		// NO SE DEJA CUMPLIMENTAR A PARTIR DEL 2025 - Recargo de equivalencia al 1%		
//		
////		CT_C16(Mod303Key.CT_C16,
////				(mod, vat) -> c17Filter(mod,vat),
////				(ctx, mod, vat) -> add(Mod303Key.CT_C16, mod, vat.getBase()), null, null, null),
////		CT_C17(Mod303Key.CT_C17,
////				(mod, vat) -> c17Filter(mod,vat),
////				(ctx, mod, vat) -> greatherQuotaPercent(Mod303Key.CT_C17, mod, vat)
////				, (ctx, mod) -> add(Mod303Key.CT_C17, mod,
////					(mod.getYear() > 2024 
////				  || mod.getYear() == 2024 && (
////						  	 mod.getPeriod() == Period.M10
////						  || mod.getPeriod() == Period.M11
////						  || mod.getPeriod() == Period.M12
////						  || mod.getPeriod() == Period.T4
////					  ))
////						?SURCHARGE_PERCENT_1
////						:0.0)
////				, null, null),
////		CT_C18(Mod303Key.CT_C18,
////				(mod, vat) -> c17Filter(mod,vat),
////				(ctx, mod, vat) -> add(Mod303Key.CT_C18, mod, vat.getSurchargeQuota()), null, null, null),
//		
//		CT_C16(Mod303Key.CT_C16, null, null, null, "0.0", null),
//		CT_C17(Mod303Key.CT_C17, null, null, null, "0.0", null),
//		CT_C18(Mod303Key.CT_C18, null, null, null, "0.0", null),
//
//		// Recargo de equivalencia al 1.40%
//		
//		CT_C19(Mod303Key.CT_C19,
//				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent_140(vat),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C19, mod, vat.getBase()), null, null, null),
//		CT_C20(Mod303Key.CT_C20, null, null, (ctx, mod) -> add(Mod303Key.CT_C20, mod, SURCHARGE_PERCENT_14), null, null),
//		CT_C21(Mod303Key.CT_C21,
//				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent_140(vat),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C21, mod, vat.getSurchargeQuota()), null, null, null),
//
//		// Recargo de equivalencia al 5.20%	
//		
//		CT_C22(Mod303Key.CT_C22,
//				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent52(vat),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C22, mod, vat.getBase()), null, null, null),
//		CT_C23(Mod303Key.CT_C23, null, null, (ctx, mod) -> add(Mod303Key.CT_C23, mod, SURCHARGE_PERCENT_52), null, null),
//		CT_C24(Mod303Key.CT_C24,
//				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent52(vat),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C24, mod, vat.getSurchargeQuota()), null, null, null)
//
//		// Modificaciones bases y cuotas del recargo de equivalencia
//		,
//		CT_C25(Mod303Key.CT_C25, (mod, vat) -> (vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C25, mod, vat.getBase()), null, null, null),
//		CT_C26(Mod303Key.CT_C26,
//				(mod, vat) -> vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
//						&& vat.isNational() && vat.isSales() && vat.isSurcharge() && vat.isRectification(),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C26, mod, vat.getSurchargeQuota()), null, null, null)

		// Total cuota devengada
		, CA_C025(Mod303Key.CA_C025, null, null, null,"CA_C003+CA_C006+CA_C009+CA_C012+CA_C015+CA_C018+CA_C020+CA_C022-CA_C024", null)		

		// ---------------------------------------------------------------
		// ----------------------- LIQUIDACION: IGIC DEDUCIBLE Y RESULTADO
		// ---------------------------------------------------------------

		// I.G.I.C. deducible en operaciones interiores bienes y servicios corrientes
		,
		CA_C026(Mod303Key.CA_C026, (mod, vat) -> operacionesInterioresCorrientesFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CA_C026, mod, vat.getBase()), null, null, null),
		CA_C027(Mod303Key.CA_C027, (mod, vat) -> operacionesInterioresCorrientesFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CA_C027, mod, vat), null, null, null)

		// I.G.I.C. deducible en operaciones interiores bienes de inversión
		,
		CA_C028(Mod303Key.CA_C028, (mod, vat) -> operacionesInterioresInversionFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CA_C028, mod, vat.getBase()), null, null, null),
		CA_C029(Mod303Key.CA_C029, (mod, vat) -> operacionesInterioresInversionFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CA_C029, mod, vat), null, null, null)

		// I.G.I.C. deducible por importaciones de bienes corrientes
		,
		CA_C030(Mod303Key.CA_C030, (mod, vat) -> importacionesCorrientesFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CA_C030, mod, vat.getBase()), null, null, null),
		CA_C031(Mod303Key.CA_C031, (mod, vat) -> importacionesCorrientesFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CA_C031, mod, vat), null, null, null)

		// I.G.I.C. deducible por importaciones de bienes de inversión
		,
		CA_C032(Mod303Key.CA_C032, (mod, vat) -> importacionesInversionFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CA_C032, mod, vat.getBase()), null, null, null),
		CA_C033(Mod303Key.CA_C033, (mod, vat) -> importacionesInversionFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CA_C033, mod, vat), null, null, null)

//		// Por cuotas soportadas en adquisiciones intracomunitarias de bienes y
//		// servicios corrientes
//		,
//		CT_C36(Mod303Key.CT_C36, (mod, vat) -> adqIntracomunitariasCorrientesFilter(vat, mod),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C36, mod, vat.getBase()), null, null, null),
//		CT_C37(Mod303Key.CT_C37, (mod, vat) -> adqIntracomunitariasCorrientesFilter(vat, mod),
//				(ctx, mod, vat) -> addProrrated(Mod303Key.CT_C37, mod, vat), null, null, null)
//
//		// Por cuotas soportadas en adquisiciones intracomunitarias de bienes de
//		// inversión
//		,
//		CT_C38(Mod303Key.CT_C38, (mod, vat) -> adqIntracomunitariasInversionFilter(vat, mod),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C38, mod, vat.getBase()), null, null, null),
//		CT_C39(Mod303Key.CT_C39, (mod, vat) -> adqIntracomunitariasInversionFilter(vat, mod),
//				(ctx, mod, vat) -> addProrrated(Mod303Key.CT_C39, mod, vat), null, null, null)

		// Rectificación de deducciones
		,
		CA_C034(Mod303Key.CA_C034, (mod, vat) -> rectificacionDeduccionesFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CA_C034, mod, vat.getBase()), null, null, null),
		CA_C035(Mod303Key.CA_C035, (mod, vat) -> rectificacionDeduccionesFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CA_C035, mod, vat), null, null, null)

		// Compensación régimen especial de agricultura, ganadería y pesca
		, CA_C036(Mod303Key.CA_C036, (mod, vat) -> compensacionesRegAgrarioFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CA_C036, mod, vat), null, null, null)

		// Regularización de cuotas soportadas por bienes de inversión
		, CA_C037(Mod303Key.CA_C037)
		
		// Regularización de cuotas soportadas antes del inicio de la actividad
		, CA_C038(Mod303Key.CA_C038)

		// Regularización por aplicación del porcentaje definitivo de prorrata
		, CA_C039(Mod303Key.CA_C039)
//				, null, null, null
//				,null
//				,null
////				,"{messages : ["
////						+ "\"Total IVA deducible sin prorratear antes del periodo que se liquida: @{CM_072}\","
////						+ "\"IVA deducible con prorrata (@{CM_007}%) de los periodos anteriores:\","
////						+ "\"@{CM_072} * @{CM_007} / 100 = @{com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_007/100)}\","
////						+ "\"IVA deducible con prorrata definitiva (@{CM_003}%) de los periodos anteriores:\","
////						+ "\"@{CM_072} * @{CM_003} / 100 = @{com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_003/100)}\","
////						+ "\"Resultado\","
////						+ "\"@{com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_003/100)}"
////						+  " - @{com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_007/100)}"
////						+  " = @{com.esferalia.aon.watson.util.AonMathUtils.round(com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_003/100) - com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_007/100))}\","
////					+"]}"
//		)

		// Total cuotas deducibles
		, CA_C040(Mod303Key.CA_C040, null, null, null,
				"CA_C027+CA_C029+CA_C031+CA_C033+CA_C035+CA_C036+CA_C037+CA_C038+CA_C039", null)

		// Diferencia
		, CA_C041(Mod303Key.CA_C041, null, null, null, "CA_C025-CA_C040", null)
		
		// Regularización cuotas art. 80.Cinco.5a LIVA
		, CA_C042(Mod303Key.CA_C042)
		
		// Cuotas a compensar de periodos anteriores 
		, CA_C043(Mod303Key.CA_C043, null, null, (ctx,mod) -> add( Mod303Key.CA_C043, mod, getPendingCompesateAmounts( ctx, mod )), null, null)
		
		// A deducir (exclusivamente en caso de autoliquidación complementaria) 
		, CA_C044(Mod303Key.CA_C044, null, null, (ctx, mod) -> {
				if (mod.isComplementary()) {
					add(Mod303Key.CA_C044, mod, Mod303DAO.getSamePeriodEffectiveModels(ctx, mod)
							.filter( fm -> fm.isToDeposit() || mod.isToPayback())	
							.mapToDouble(fm -> fm.getAmount(Mod303Key.CA_C045)).sum());
				}
			  }
			,null
			,null
		)
		
		// Resultado de la autoliquidación
		, CA_C045(Mod303Key.CA_C045, null, null, null, "CA_C041+CA_C042-CA_C043-CA_C044", null)

		// ---------------------------------------------------------------
		// ----------------------------------------- INFORMACIO“N ADICIONAL
		// ---------------------------------------------------------------

//		// Entregas intracomunitarias de bienes y servicios
//		, CT_C59(Mod303Key.CT_C59, (mod, vat) -> ventasIntracomunitarias( vat, mod),
//			(ctx, mod, vat) -> add(Mod303Key.CT_C59, mod, vat.getBase()), null, null, null)

		// Exportaciones y otras operaciones exentas con derecho a deducción
		, CA_C046(Mod303Key.CA_C046,
			(mod, vat) -> ventasExtraComunitariasCanCeuBienes(vat, mod),
			(ctx, mod, vat) -> add(Mod303Key.CA_C046, mod, vat.getBase()), null, null, null)
		
		// Operaciones no sujetas o con inversión del sujeto pasivo que originan el derecho a deducción
		, CA_C047(Mod303Key.CA_C047, (mod, vat) -> ventasExtraComunitariasCanCeuServicios(vat, mod) || (ventasISP(vat, mod) && !vat.isSpainDocumentCountry()),
			(ctx, mod, vat) -> add(Mod303Key.CA_C047, mod, vat.getBase()), null, null, null)
		
//		// Operaciones sujetas con inversión del sujeto pasivo (solo las ventas ISP de clientes españoles)
//		,CT_C122(Mod303Key.CT_C122, (mod, vat) -> (ventasISP(vat, mod) && vat.isSpainDocumentCountry()),
//			(ctx, mod, vat) -> add(Mod303Key.CT_C122, mod, vat.getBase()), null, null, null)
//		//Operaciones no sujetas por reglas de localización acogidas a los regímenes especiales de ventanilla única.
//		,CT_C123(Mod303Key.CT_C123, (mod, vat) -> (vat.isSalesOSS() && !vat.isSpainDocumentCountry()),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C123, mod, vat.getBase()), null, null, null)
//		
//		//Operaciones sujetas y acogidas a los regímenes especiales de ventanilla única.
//		,CT_C124(Mod303Key.CT_C124, (mod, vat) -> (vat.isSalesOSS() && vat.isSpainDocumentCountry()),
//				(ctx, mod, vat) -> add(Mod303Key.CT_C124, mod, vat.getBase()), null, null, null)

		// Importes de las entregas de bienes y prestaciones de servicios RECC
		,CA_C048(Mod303Key.CA_C048 , (mod, vat) -> vat.isSales() && vat.isVatAccrualRegime()
			, null, null, null, null)
		,CA_C049(Mod303Key.CA_C049 , (mod, vat) -> vat.isSales() && vat.isVatAccrualRegime()
			, null, null, null, null)

		// Importes de las adquisiciones de bienes y servicios RECC
		,CA_C050(Mod303Key.CA_C050 , (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime()
			, null, null, null, null)
		,CA_C051(Mod303Key.CA_C051 , (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime()
			, null, null, null, null)

//		// Suma de resultados
//		, CT_C64(Mod303Key.CT_C64, null, null, null, "CT_C46+CT_S58+CT_C76", null)
//
//		// % Atribuible a la Administración del Estado
//		, CT_C65(Mod303Key.CT_C65, null, null, (ctx, mod) -> add(Mod303Key.CT_C65, mod, 100.0), null, null)
//
//		// Cuota atribuible a la Administración del Estado
//		, CT_C66(Mod303Key.CT_C66, null, null, null, "round(CT_C64*CT_C65/100)",
//				"<li><b>Resultado:</b> @{CT_C65} % de @{CT_C64} igual <b>@{CT_C66}</b></li>")
//
//		// IVA a la importación liquidado por la Aduana pendiente de ingreso
//		, CT_C77(Mod303Key.CT_C77)
//
//
//		// Cuotas a compensar de periodos anteriores aplicadas en este periodo
//		// Validación que hace la Agencia Tributaria:
//		//	El resultado de la autoliquidación no podrá ser A COMPENSAR, si la casilla (78)
//		//	está cumplimentada, es decir, si se hubieran aplicado a la autoliquidación que se
//		//	está presentando cuotas pendientes de compensación generadas en periodos
//		//	anteriores.		
//		, CT_C78(Mod303Key.CT_C78, null, null, (ctx, mod) -> add(Mod303Key.CT_C78, mod, mod.getAmount(Mod303Key.CT_C110)), "checkC78(CT_C78,CT_C110,CT_C66,CT_C77)", null)
//		
//		// Cuotas a compensar de periodos previos pendientes para periodos posteriores
//		, CT_C87(Mod303Key.CT_C87, null, null, null, "CT_C110-CT_C78", null)
//
//		// Exclusivamente para sujetos pasivos que tributan conjuntamente a la Administración del Estado
//		// y a las Diputaciones Forales. Resultado de la Regularización anual.
//		, CT_C68(Mod303Key.CT_C68)
//		
//		, CT_C108(Mod303Key.CT_C108)
//
//		// Resultado de la autoliquidación
//		, CT_C69(Mod303Key.CT_C69, null, null, null, "CT_C66+CT_C77-CT_C78+CT_C68+CT_C108", null)
//
//		// A deducir (exclusivamente en caso de autoliquidación complementaria)
//		, CT_C70(Mod303Key.CT_C70, null, null, (ctx, mod) -> {
//				if (mod.isComplementary()) {
//					add(Mod303Key.CT_C70, mod, Mod303DAO.getSamePeriodEffectiveModels(ctx, mod)
//							.filter( fm -> fm.isToDeposit() || mod.isToPayback())	
//							.mapToDouble(fm -> fm.getAmount(Mod303Key.CT_C71)).sum());
//				}
//			  }
//			,null
//			,null
//		)
//		,CT_C109(Mod303Key.CT_C109)
//		
//		// Resultado
//		,CT_C71(Mod303Key.CT_C71, null, null, null, "CT_C69-CT_C70+CT_C109", null)
		;
		
		private Mod303Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;
//		private ISimplifiedRegimeActivityPopulator populator;
//		private ISimplifiedRegimeActivityFiller filler;
//		private ISimplifiedRegimeCopier copier;
//
//		private Mod303KeyDAO(Mod303Key key) {
//			this(key, null, null, null, null, null);
//		}
//
//		private Mod303KeyDAO(Mod303Key key, IValueAccepter acceptValue, IValueIntializer initializer,
//				IValueFirstIntializer firstInitializer, String expression, String template) {
//			this(key, acceptValue, initializer, firstInitializer, expression, template, null, null, null);
//		}
//
//		private Mod303KeyDAO(Mod303Key key
//			,IValueAccepter acceptValue
//			,IValueIntializer initializer,
//			IValueFirstIntializer firstInitializer
//			,String expression, String template,
//			ISimplifiedRegimeActivityPopulator populator
//			,ISimplifiedRegimeActivityFiller filler,
//			ISimplifiedRegimeCopier copier) {
//			
//			this.key = key;
//			this.acceptValue = acceptValue;
//			this.initializer = initializer;
//			this.firstInitializer = firstInitializer;
//			this.expression = expression;
//			this.template = template;
//			this.populator = populator;
//			this.filler = filler;
//			this.copier = copier;
//		}

		private Mod303KeyDAO(Mod303Key key) {
			this(key, null, null, null, null, null);
		}

		private Mod303KeyDAO(Mod303Key key, IValueAccepter acceptValue, IValueIntializer initializer, IValueFirstIntializer firstInitializer, String expression, String template) {
			this.key = key;
			this.acceptValue = acceptValue;
			this.initializer = initializer;
			this.firstInitializer = firstInitializer;
			this.expression = expression;
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

//		public boolean hasCopier() {
//			return copier != null;
//		}

		@Override
		public boolean acceptValue(Mod303 mod, VatContext vctx) {
			return acceptValue != null && acceptValue.accept(mod, vctx);
		}

		@Override
		public boolean hasAccepter() {
			return acceptValue != null;
		}

		@Override
		public void initialize(AONContext ctx, Mod303 mod, VatContext vctx) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, vctx);
			}
		}

		@Override
		public void firstInitialize(AONContext ctx, Mod303 mod) {
			if (firstInitializer != null) {
				firstInitializer.initialize(ctx, mod);
			}
		}

//		public void copy(Mod303 previous, Mod303 current) {
//			if (hasCopier()) {
//				copier.copy(previous, current);
//			}
//		}

//		public void populate(Mod303 mod) {
//			if (populator != null)
//				populator.populate(mod);
//		}

//		public void fill(Mod303 mod) {
//			if (filler != null)
//				filler.fill(mod);
//		}

		public static Mod303KeyDAO safeValueOf(String key) {
			if (AonStringUtils.isBlank(key))
				return null;
			for (Mod303KeyDAO keyDAO : Mod303KeyDAO.values()) {
				if (keyDAO.getKey().getValue().equals(key)) {
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

	// FALTA - HAY QUE REPASAR TODOS ESTOS FILTROS, PARA VER SI SIRVEN PARA LAS FACTURAS CON IGIC
	// -----------------------------------------------------------------------
	// --------------------------------------------------------------- FILTROS
	// -----------------------------------------------------------------------
	
	private static boolean isCommonNationalSales(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime() && vat.isNational()
				&& vat.isSales() && !vat.isRectification() && !vat.isSalesOSS();
	}

	private static boolean hasPercent0(Mod303 mod, VatContext vat) {
		return vat.getPercentage() == PERCENT0;
	}

//	private static boolean hasPercent2(Mod303 mod, VatContext vat) {
//		return vat.getPercentage() == PERCENT2;
//	}

//	private static boolean hasPercent4(Mod303 mod, VatContext vat) {
//		return vat.getPercentage() == PERCENT4;
//	}
//
//	private static boolean hasPercent10(VatContext vat) {
//		return vat.getPercentage() == PERCENT10;
//	}
//
//	private static boolean hasPercent21(VatContext vat) {
//		return vat.getPercentage() == PERCENT21;
//	}
//
//	private static boolean hasSurchargePercent_175(VatContext vat) {
//		return vat.getSurchargePercent() == SURCHARGE_PERCENT_175;
//	}
//	
//	private static boolean hasSurchargePercent_05(VatContext vat) {
//		return vat.getSurchargePercent() == SURCHARGE_PERCENT_05;
//	}
//	
//	private static boolean hasSurchargePercent_140(VatContext vat) {
//		return vat.getSurchargePercent() == SURCHARGE_PERCENT_14;
//	}
//
//	private static boolean hasSurchargePercent52(VatContext vat) {
//		return vat.getSurchargePercent() == SURCHARGE_PERCENT_52;
//	}

	private static boolean adqIntracomunitariasFilter(VatContext vat, Mod303 mod) {
		return !vat.isVatSurchargeRegime() && (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses());
	}

	private static boolean adqIntracomunitariasFilterGene(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && adqIntracomunitariasFilter(vat, mod);
	}

	private static boolean adqIntracomunitariasFilterNoRECT(VatContext vat, Mod303 mod) {
		return !vat.isRectification() && adqIntracomunitariasFilterGene(vat, mod);
	}

	private static boolean adqIntracomunitariasFilterSimp(VatContext vat, Mod303 mod) {
		return vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) && vat.isIntracommunityPurchase()
				&& !vat.isService();
	}

	private static boolean entregasActivosFijosFilterSimp(VatContext vat, Mod303 mod) {
		return vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) && vat.isSales() && vat.isInvestment();
	}

	private static boolean operacionesISPFilter(VatContext vat, Mod303 mod) {
		return !vat.isVatSurchargeRegime()
				&& (vat.isOtherISPPurchase() || vat.isOtherISPExpenses() || vat.isExtracommunityExpenses()
						|| vat.isCanCeuMelExpenses() || (vat.isExtracommunityPurchase() && vat.isService())
						|| (vat.isCanCeuMelPurchase() && vat.isService()));
	}

	private static boolean operacionesISPFilterSimp(VatContext vat, Mod303 mod) {
		return vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) && (operacionesISPFilter(vat, mod)
				|| vat.isIntracommunityExpenses() || (vat.isService() && vat.isIntracommunityPurchase()));
	}

	private static boolean operacionesISPFilterGene(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && operacionesISPFilter(vat, mod);
	}

	private static boolean operacionesISPFilterNoRECT(VatContext vat, Mod303 mod) {
		return !vat.isRectification() && operacionesISPFilterGene(vat, mod);
	}

	private static boolean modificacionBasesYCuotasFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime() && vat.isRectification() && !vat.isSalesOSS()
				&& (vat.isNationalSales() || adqIntracomunitariasFilterGene(vat, mod) || operacionesISPFilterGene(vat, mod));
	}

	private static boolean operacionesInterioresCorrientesFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime() && !vat.isInvestment()
				&& !vat.isRectification() && !vat.isFarmerRegime() && AonMathUtils.isNotZero(vat.getPercentage())
				&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilterGene(vat, mod));

	}

	private static boolean operacionesInterioresInversionFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime() && vat.isInvestment()
				&& !vat.isRectification() && !vat.isFarmerRegime() && AonMathUtils.isNotZero(vat.getPercentage())
				&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilterGene(vat, mod));
	}

	private static boolean importacionesCorrientesFilter(VatContext vat, Mod303 mod) {
		return commonImportacionesFilter(vat, mod) 
				&& !vat.isInvestment();
	}

	private static boolean importacionesInversionFilter(VatContext vat, Mod303 mod) {
		return commonImportacionesFilter(vat, mod) 
				&& vat.isInvestment();
	}

	private static boolean commonImportacionesFilter(VatContext vat, Mod303 mod) {
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

	private static boolean adqIntracomunitariasCorrientesFilter(VatContext vat, Mod303 mod) {
		return !vat.isInvestment() && !vat.isRectification() && adqIntracomunitariasFilterGene(vat, mod);
	}

	private static boolean adqIntracomunitariasInversionFilter(VatContext vat, Mod303 mod) {
		return vat.isInvestment() && !vat.isRectification() && adqIntracomunitariasFilterGene(vat, mod);
	}

	private static boolean rectificacionDeduccionesFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime() && vat.isRectification()
				&& (vat.isPurchase() || vat.isExpenses());
	}

	private static boolean compensacionesRegAgrarioFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) 
				&& !vat.isVatSurchargeRegime() 
				&& vat.isFarmerRegime()
				&& !vat.isRectification() 
				&& (vat.isNationalPurchase() || vat.isNationalExpenses());
	}

	public static boolean  ventasIntracomunitarias(VatContext vat, Mod303 mod) {
		return !vat.isVatSurchargeRegime() && vat.isIntracommunitySales();
	}
	public static boolean  ventasExtraComunitariasCanCeuBienes(VatContext vat, Mod303 mod) {
		return !vat.isVatSurchargeRegime() && !vat.isService() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales()) && !vat.isSalesOSS();
	}
	public static boolean  ventasExtraComunitariasCanCeuServicios(VatContext vat, Mod303 mod) {
		boolean add = !vat.isVatSurchargeRegime() 
			&& ((vat.isService() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales()))) && !vat.isSalesOSS();
		if ( add && mod.getYear() == 2021) {
			add = FiscalUtils.isInPeriodRange(mod, vat.getTaxDate());
		} 
		return add;
	}
	public static boolean  ventasISP(VatContext vat, Mod303 mod) {
		boolean add = !vat.isVatSurchargeRegime() && vat.isOtherISPSales();
		if ( add && mod.getYear() == 2021) {
			add = FiscalUtils.isInPeriodRange(mod, vat.getTaxDate());
		} 
		return add;
	}

//	private static Mod303ActivityFarmer ensureFarmerActivity(Mod303 mod, int idx) {
//		if (idx < 0 || idx > 3)
//			throw new IllegalArgumentException("0, 1, 3, o 3");
//
//		if (mod.getActivityFarmerList() == null) {
//			mod.setActivityFarmerList(new LinkedList<>());
//		}
//		for (int i = 0; i <= idx; i++) {
//			if (idx == mod.getActivityFarmerList().size()) {
//				mod.getActivityFarmerList().add(new Mod303ActivityFarmer());
//			}
//		}
//		return mod.getActivityFarmerList().get(idx);
//	}
//
//	private static Mod303Activity ensureActivity(Mod303 mod, int idx) {
//		if (idx < 0 || idx > 3)
//			throw new IllegalArgumentException("0, 1, 2, o 3");
//
//		if (mod.getActivityList() == null) {
//			mod.setActivityList(new LinkedList<>());
//		}
//		for (int i = 0; i <= idx; i++) {
//			if (idx == mod.getActivityList().size()) {
//				mod.getActivityList().add(new Mod303Activity());
//			}
//		}
//		return mod.getActivityList().get(idx);
//	}
//
//	private static Mod303ActivityModule ensureModule(Mod303 mod, int act, int idx) {
//		if (idx < 0 || idx > 6) throw new IllegalArgumentException("0, 1, 2, 3, 4, 5, o 6");
//		Mod303Activity a = ensureActivity(mod, act);
//		if (a.getModules() == null) {
//			a.setModules(new LinkedList<>());
//		}
//		for (int i = 0; i <= idx; i++) {
//			if (idx == a.getModules().size()) {
//				a.getModules().add(new Mod303ActivityModule());
//			}
//		}
//		return a.getModules().get(idx);
//	}
//
//	private static Mod303ActivityDesk ensureDesk(Mod303 mod, int act, int idx) {
//		if (idx < 0 || idx > 3) throw new IllegalArgumentException("0, 1, 2, o 3");
//		Mod303Activity a = ensureActivity(mod, act);
//		if (a.getDesks() == null) {
//			a.setDesks(new LinkedList<>());
//		}
//		for (int i = 0; i <= idx; i++) {
//			if (idx == a.getDesks().size()) {
//				a.getDesks().add(new Mod303ActivityDesk());
//			}
//		}
//		return a.getDesks().get(idx);
//	}

	// FALTA - NO SE SI SERA NECESARIO ALGUNA INICIALIZACION ESPECIFICA DE ALGUNA CASILLA
//	@Override
//	public void specificInitialization(Mod303 mod303) {
//		super.specificInitialization(mod303);
//		if (mod303.getDefaultVATRegime() == VATRegime.SIMPLIFIED) {
//			if (AonMathUtils.isZero(mod303.getAmount(Mod303Key.CT_C46))) {
//				mod303.putAmount(Mod303Key.CT_A02, 0); // Sólo Reg. Simplificado.
//			} else {
//				mod303.putAmount(Mod303Key.CT_A02, 1); // Reg. Simplificado. y General
//			}
//		} else {
//			if (AonMathUtils.isNotZero(mod303.getAmount(Mod303Key.CT_S58))) {
//				mod303.putAmount(Mod303Key.CT_A02, 1); // Reg. Simplificado. y General
//			} else {
//				mod303.putAmount(Mod303Key.CT_A02, 2); // Sólo Reg. Simplificado. y General
//			}
//		}
//	}
	
	@Override
	public Mod303Key getRegularizationKey() {
		return Mod303Key.CA_C039;
	}

	@Override
	public Mod303Key[] getCompensationExplainKeys() {
		return COMPENSATION_EXPLAIN_KEYS;
	}

	@Override
	protected String getCompensationExplain( AONContext ctx, Mod303 mod303, Mod303Key key) {
		return DeclarationInfoUtil.getExplain( ctx, mod303, key, Mod303DAO.getLastPeriodEffectiveModels(ctx, mod303)
			, new ExplainRowManager() {
				@Override
				public String apply(FiscalModel fm) {
					setSomething(true);
//					sum(fm.getAmount(Mod303Key.CT_C87));
					sum(fm.getDeclarationResult());
//					sum(fm.getAmount(Mod303Key.CT_C111));
					return new StringBuilder().append("<tr>")
						.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+fontLarger+border+width500) )
							.append(fm.getModelFullName())
						.append("</td>")
					.append("</tr>")
//					.append("<tr>")
//						.append( MessageFormat.format(styledTag, "td", paddingLeft+border) )
//							.append("Cuotas a compensar de periodos previos pendientes para periodos posteriores ")
//						.append("</td>")
//						.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
//							.append(DEC2.format(fm.getAmount(Mod303Key.CT_C87)))
//						.append("</td>")
//					.append("</tr>")
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", paddingLeft+border) )
							.append("Resultado " +
								AonObjectUtils.defaultIfNull(fm.getDeclarationResultType(), t -> "(" + t.getDescription() + ")"))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
							.append(DEC2.format(fm.getDeclarationResult()))
						.append("</td>")
					.append("</tr>")					
//					.append("<tr>")
//						.append( MessageFormat.format(styledTag, "td", paddingLeft+border) )
//							.append("Importe a devolver a consecuencia de la rectificación ")
//						.append("</td>")
//						.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
//							.append(DEC2.format(fm.getAmount(Mod303Key.CT_C111)))
//						.append("</td>")
//					.append("</tr>")
					.toString();
				}
			});	
	}
		
	@Override
	public Mod303Key[] getSamePeriodExplainKeys() {
		return SAME_PERIOD_EXPLAIN_KEYS;
	}
	@Override
	protected String getSamePeriodExplain( AONContext ctx, Mod303 mod303, Mod303Key key) {
		return DeclarationInfoUtil.getExplain( ctx, mod303, key, Mod303DAO.getSamePeriodEffectiveModels(ctx, mod303), new ExplainRowManager());	
	}
	@Override
	protected String getRegularizationExplain(AONContext ctx, Mod303 mod303, Mod303Key key) {
		return Mod303InfoUtil.getRegularizationExplain( ctx, mod303, key );
	}

	@Override
	void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303) {
		// NOTHING
	}

//	
//	// -----------------------------------------------------------------------
//	// ----------------------------------------------- REGIMEN SIMPLIFICADO --
//	// -----------------------------------------------------------------------
//	@Override
//	public void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303) {
//		Integer previousId = Mod303DAO.getMod303s(ctx, mod303.getDomain())
//			.map( m -> m.getId())
//			.findFirst().orElse(null);
//		if (previousId != null) {
//			Mod303 previous = Mod303DAO.get(ctx, previousId);
//			if (hasSimplifiedRegime( previous )) {
//				mod303.putAmount(Mod303Key.CT_A02, previous.getAmount(Mod303Key.CT_A02));
//				Arrays.stream(Mod303KeyDAO.values())
//					.filter( key -> key.hasCopier() )
//					.forEach( key -> key.copy(previous, mod303));
//			}
//			ensureSimplifiedRegimeActivities(mod303);
//			fillSimplifiedRegime(mod303);
//		}
//	}
//
//	private IEpigraph getEpigraph(Mod303 mod303, Mod303Key key) {
//		return Modules2025.Epigraph.getEpigraph(mod303.getDescription(key));
//	}
//
//	private IFarmerIVA getFarmerIVA(Mod303 mod303, Mod303Key key) {
//		return Modules2025.FarmerIVA.getFarmerIVA(mod303.getDescription(key));
//	}
//
//	@Override
//	public void fillSimplifiedRegime(Mod303 mod303) {
//		for (Mod303KeyDAO key : Mod303KeyDAO.values()) {
//			key.fill(mod303);
//		}
//	}
//
//	@Override
//	public void populateSimplifiedRegime(Mod303 mod303) {
//		for (Mod303KeyDAO key : Mod303KeyDAO.values()) {
//			key.populate(mod303);
//		}
//	}
//
//	private static void copyKey(Mod303 previous,Mod303 current, Mod303Key key) {
//		// En 2025 se añade una nueva actividad agricola (la 17) y la 17 anterior pasa a la 18
//		if ((previous.getYear() < 2025) && (key == Mod303Key.CT_SA11 || key == Mod303Key.CT_SA21 || key == Mod303Key.CT_SA31 || key == Mod303Key.CT_SA41) && "17".equals(previous.getDescription(key))) {
//			current.putAmount(key, 0.0);
//			current.putDescription(key, "18");
//		} else {
//			current.putAmount(key, previous.getAmount(key));		
//			current.putDescription(key, previous.getDescription(key));
//		}
//	}
//	
//	private static boolean hasSimplifiedRegime(Mod303 mod303) {
//		return AonNumberUtils.notEquals(mod303.getAmount(Mod303Key.CT_A02), 2);
//	}
//
//	private static void ensureActivityDays(Mod303 previous, Mod303 current,Mod303Key epiKey,Mod303Key daysKey) {
//		if (AonStringUtils.isNotBlank(current.getDescription(Mod303Key.CT_S101))) {
//			Date curStart = FiscalUtils.getPeriodStart(current);
//			Date curEnd = FiscalUtils.getPeriodEnd(current);
//			int curMaxDias = AonNumberUtils.toint(AonDateUtils.getDaysBetweenDates(curStart, curEnd)) + 1;
//			Date prevStart = FiscalUtils.getPeriodStart(previous);
//			Date prevEnd = FiscalUtils.getPeriodEnd(previous);
//			int prevMaxDias = AonNumberUtils.toint(AonDateUtils.getDaysBetweenDates(prevStart, prevEnd)) + 1;
//			if (AonNumberUtils.equals(prevMaxDias, previous.getAmount(daysKey))) {
//				current.ensureDetail(daysKey).setAmount(curMaxDias);			
//			}
//		}
//	}
//
//	private void ensureSimplifiedRegimeActivities(Mod303 mod303) {
//		Mod303Key[][] farmerKeys = new Mod303Key[][]{
//			new Mod303Key[] {Mod303Key.CT_SA11,Mod303Key.CT_SA13,Mod303Key.CT_SA15},
//			new Mod303Key[] {Mod303Key.CT_SA21,Mod303Key.CT_SA23,Mod303Key.CT_SA25},
//			new Mod303Key[] {Mod303Key.CT_SA31,Mod303Key.CT_SA33,Mod303Key.CT_SA35},
//			new Mod303Key[] {Mod303Key.CT_SA41,Mod303Key.CT_SA43,Mod303Key.CT_SA45},
//		};
//		
//		IntStream.range(0, farmerKeys.length)
//			.boxed()
//			.map(i -> farmerKeys[i])
//			.filter(farmerActivity -> AonStringUtils.isNotBlank(mod303.getDescription(farmerActivity[0])))
//			.forEach(farmerActivity -> {
//				IFarmerIVA farmerIVA = getFarmerIVA(mod303, farmerActivity[0]);
//				if (farmerIVA != null) {
//					mod303.ensureDetail(farmerActivity[1]).setAmount(farmerIVA.getIndiceRendimientoNeto() * 10000);
//					if (!mod303.isLastPeriod()) {
//						mod303.ensureDetail(farmerActivity[2]).setAmount(farmerIVA.getPorcentaje());
//					}
//				}
//			});
//		
//		Mod303Key[][] actKeys = new Mod303Key[][]{
//			new Mod303Key[] {Mod303Key.CT_S101,Mod303Key.CT_S125,Mod303Key.CT_S120},
//			new Mod303Key[] {Mod303Key.CT_S201,Mod303Key.CT_S225,Mod303Key.CT_S220},
//			new Mod303Key[] {Mod303Key.CT_S301,Mod303Key.CT_S325,Mod303Key.CT_S320},
//			new Mod303Key[] {Mod303Key.CT_S401,Mod303Key.CT_S425,Mod303Key.CT_S420},
//		};
//		IntStream.range(0, actKeys.length)
//			.boxed()
//			.map(i -> actKeys[i])
//			.filter(actActivity -> AonStringUtils.isNotBlank(mod303.getDescription(actActivity[0])))
//			.forEach(actActivity -> {
//				IEpigraph epi = getEpigraph(mod303, actActivity[0]);
//				if (epi != null) {
//					mod303.ensureDetail(actActivity[1]).setAmount(epi.getPorcMin());
//					if (!mod303.isLastPeriod()) {
//						mod303.ensureDetail(actActivity[2]).setAmount(epi.getVatPorc());
//					}
//				}
//			});
//	}
}