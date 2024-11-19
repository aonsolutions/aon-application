package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.DEC2;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.border;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.fontLarger;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.paddingLeft;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.styledTag;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textCenter;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textRight;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.width150;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.width500;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityDesk;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.modules.IEpigraph;
import com.esferalia.aon.occam.api.model.fiscal.modules.IFarmerIVA;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class Mod303AEAT2024T3Declaration extends Mod303AEAT {
	
	@FunctionalInterface
	private interface ISimplifiedRegimeActivityFiller {
		void fill(Mod303 mod);
	}

	@FunctionalInterface
	private interface ISimplifiedRegimeActivityPopulator {
		void populate(Mod303 mod);
	}

	@FunctionalInterface
	private interface ISimplifiedRegimeCopier {
		void copy(Mod303 prev, Mod303 current);
	}

	protected Mod303AEAT2024T3Declaration() {

	}
	
	private static final double PERCENT0 = 0.0;
	private static final double PERCENT2 = 2.0;
	private static final double PERCENT4 = 4.0;
	private static final double PERCENT5 = 5.0;
	private static final double PERCENT75 = 7.5;
	private static final double PERCENT10 = 10.0;
	private static final double PERCENT21 = 21.0;
	
	private static final double SURCHARGE_PERCENT_0 = 0;
	private static final double SURCHARGE_PERCENT_026 = 0.26;
	private static final double SURCHARGE_PERCENT_05 = 0.5;
	private static final double SURCHARGE_PERCENT_062 = 0.62;
	private static final double SURCHARGE_PERCENT_1 = 1;
	private static final double SURCHARGE_PERCENT_14 = 1.4;
	private static final double SURCHARGE_PERCENT_175 = 1.75;
	private static final double SURCHARGE_PERCENT_52 = 5.2;
	
	public static boolean accept(Mod303 mod) {
		return mod.isAEAT() && 
			((mod.getYear() > 2024)
		 || (mod.getYear() == 2024 && 
		    	(mod.getPeriod() == Period.M09
    			|| mod.getPeriod() == Period.M10
		    	|| mod.getPeriod() == Period.M11 
		    	|| mod.getPeriod() == Period.M12 
		    	|| mod.getPeriod() == Period.T3
		    	|| mod.getPeriod() == Period.T4
		    	)
	    ));		
	}
	
	private static final Mod303Key[] COMPENSATION_EXPLAIN_KEYS = new Mod303Key[] { Mod303Key.CT_C110 };
	private static final Mod303Key[] SAME_PERIOD_EXPLAIN_KEYS = new Mod303Key[] { Mod303Key.CT_C70 };
	
	private static final Mod303Key[] PRORATE_KEYS = new Mod303Key[] { 
			Mod303Key.CT_C29, Mod303Key.CT_C31, Mod303Key.CT_C33, Mod303Key.CT_C35, 
			Mod303Key.CT_C37, Mod303Key.CT_C39, Mod303Key.CT_C41, Mod303Key.CT_C42 };

	private enum Mod303KeyDAO implements IMod303KeyDAO {

		 CM_003(Mod303Key.CM_003)
		,CM_007(Mod303Key.CM_007)
//		,CM_072(Mod303Key.CM_072)

		,CT_A12(Mod303Key.CT_A12, null, null, (ctx, mod) -> set(Mod303Key.CT_A12, mod, 2), null, null, null, null
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_A12))
		,CM_002(Mod303Key.CM_002, null, null, (ctx, mod) -> add(Mod303Key.CM_002, mod,(AonStringUtils.equals(AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY),AonStringUtils.ONE)) ? 1 : 0),null, null)
		,CT_A02(Mod303Key.CT_A02)
		,CT_A03(Mod303Key.CT_A03)
		,CT_A07(Mod303Key.CT_A07, null, null,(ctx, mod) -> add(Mod303Key.CT_A07, mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment() ? 1 : 0),null, null)
		,CT_A08(Mod303Key.CT_A08) // Compras Criterio de caja. Se incializa en la casilla 075.
		,CT_A09(Mod303Key.CT_A09)
		,CT_A10(Mod303Key.CT_A10)
		,CT_A04(Mod303Key.CT_A04)
		,CT_A05(Mod303Key.CT_A05)
		,CT_A06(Mod303Key.CT_A06)
		,CT_A13(Mod303Key.CT_A13, null, null, (ctx, mod) -> set(Mod303Key.CT_A13, mod, 2), null, null, null, null
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_A13))
		,CT_A14(Mod303Key.CT_A14, null, null, (ctx, mod) -> set(Mod303Key.CT_A14, mod, 0), null, null, null, null
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_A14))
		,CT_A11(Mod303Key.CT_A11)

		// ---------------------------------------------------------
		// ----------------------------------------- REGIMEN GENERAL
		// ---------------------------------------------------------

		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------

		// Base imponible, porcentaje y cuota al tipo.0%
		,
		CT_C150(Mod303Key.CT_C150, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CT_C150, mod, vat.getBase()), null, null, null),
		CT_C151(Mod303Key.CT_C151, null, null, (ctx, mod) -> add(Mod303Key.CT_C151, mod, PERCENT0), null, null),
		CT_C152(Mod303Key.CT_C152, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CT_C152, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota al tipo.2%
		,
		CT_C165(Mod303Key.CT_C165, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent2(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CT_C165, mod, vat.getBase()), null, null, null),
		CT_C166(Mod303Key.CT_C166, null, null, (ctx, mod) ->  
			add(Mod303Key.CT_C166, mod, (mod.getPeriod() == Period.M09 || mod.getPeriod() == Period.T3)?0.0:PERCENT2 )
			, null, null),
		CT_C167(Mod303Key.CT_C167, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent2(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CT_C167, mod, vat.getQuota()), null, null, null)

		// Base imponible, porcentaje y cuota al tipo.4%
		,
		CT_C01(Mod303Key.CT_C01, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent4(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CT_C01, mod, vat.getBase()), null, null, null),
		CT_C02(Mod303Key.CT_C02, null, null, (ctx, mod) -> add(Mod303Key.CT_C02, mod, PERCENT4), null, null),
		CT_C03(Mod303Key.CT_C03, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent4(mod,vat)),
				(ctx, mod, vat) -> add(Mod303Key.CT_C03, mod, vat.getQuota()), null, null, null)

		// Base imponible, porcentaje y cuota al tipo.0%
		,
		CT_C153(Mod303Key.CT_C153, (mod, vat) -> c154Filter(mod,vat), 
				(ctx, mod, vat) -> add(Mod303Key.CT_C153, mod, vat.getBase()), null, null, null),
		CT_C154(Mod303Key.CT_C154, null, null
				, (ctx, mod) -> add(Mod303Key.CT_C154, mod, (mod.getPeriod() == Period.M09 || mod.getPeriod() == Period.T3)?PERCENT5:PERCENT75 )
				, null, null),
		CT_C155(Mod303Key.CT_C155, (mod, vat) -> c154Filter(mod,vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C155, mod, vat.getQuota()), null, null, null)

		// Base imponible, porcentaje y cuota al tipo 10%.
		,
		CT_C04(Mod303Key.CT_C04, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent10(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CT_C04, mod, vat.getBase()), null, null, null),
		CT_C05(Mod303Key.CT_C05, null, null, (ctx, mod) -> add(Mod303Key.CT_C05, mod, PERCENT10), null, null),
		CT_C06(Mod303Key.CT_C06, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent10(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CT_C06, mod, vat.getQuota()), null, null, null)

		// Base imponible, porcentaje y cuota al tipo 21%.
		,
		CT_C07(Mod303Key.CT_C07, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent21(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CT_C07, mod, vat.getBase()), null, null, null),
		CT_C08(Mod303Key.CT_C08, null, null, (ctx, mod) -> add(Mod303Key.CT_C08, mod, PERCENT21), null, null),
		CT_C09(Mod303Key.CT_C09, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent21(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CT_C09, mod, vat.getQuota()), null, null, null)

		// Adquisiciones intracomunitarias de bienes y servicios. base y cuota.
		,
		CT_C10(Mod303Key.CT_C10, (mod, vat) -> adqIntracomunitariasFilterNoRECT(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C10, mod, vat.getBase()), null, null, null),
		CT_C11(Mod303Key.CT_C11, (mod, vat) -> adqIntracomunitariasFilterNoRECT(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C11, mod, vat.getQuota()), null, null, null)

		// Otras operaciones con inversión del sujeto pasivo (excepto. adq. intracom).
		// Base y cuota
		,
		CT_C12(Mod303Key.CT_C12, (mod, vat) -> operacionesISPFilterNoRECT(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C12, mod, vat.getBase()), null, null, null),
		CT_C13(Mod303Key.CT_C13, (mod, vat) -> operacionesISPFilterNoRECT(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C13, mod, vat.getQuota()), null, null, null)

		// Modificación bases y cuotas
		,
		CT_C14(Mod303Key.CT_C14, (mod, vat) -> modificacionBasesYCuotasFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C14, mod, vat.getBase()), null, null, null),
		CT_C15(Mod303Key.CT_C15, (mod, vat) -> modificacionBasesYCuotasFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C15, mod, vat.getQuota()), null, null, null),

//Base imponible [156]	Tipo % [157]	Cuota [158]		------>	1.75
		CT_C156(Mod303Key.CT_C156,
				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent_175(vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C156, mod, vat.getBase()), null, null, null),
		CT_C157(Mod303Key.CT_C157, null, null, (ctx, mod) -> add(Mod303Key.CT_C157, mod, SURCHARGE_PERCENT_175), null, null),
		CT_C158(Mod303Key.CT_C158,
				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent_175(vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C158, mod, vat.getSurchargeQuota()), null, null, null),

//Base imponible [168]	Tipo % [169]	Cuota [170]		15 enteros y 2 decimales. Nota 10
//"00026", "00050" 	periodos 10 y 4T de 2024 y ejercicios posteriores
		CT_C168(Mod303Key.CT_C168,
				(mod, vat) -> c169Filter(mod,vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C168, mod, vat.getBase()), null, null, null),
		CT_C169(Mod303Key.CT_C169,
				(mod, vat) -> c169Filter(mod,vat),
				(ctx, mod, vat) -> greatherQuotaPercent(Mod303Key.CT_C169, mod, vat),
				(ctx, mod) -> add(Mod303Key.CT_C169, mod, SURCHARGE_PERCENT_026), 
				null, null),
		CT_C170(Mod303Key.CT_C170,
				(mod, vat) -> c169Filter(mod,vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C170, mod, vat.getSurchargeQuota()), null, null, null),
		
//Base imponible [16]		Tipo % [17]		Cuota [18]		------>
//		Constante "00500"								09 y 3T de 2024
//		Constante "00750"								A partir de 10 y 4T de 2024 y ejercicios posteriores
		
		CT_C16(Mod303Key.CT_C16,
				(mod, vat) -> c17Filter(mod,vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C16, mod, vat.getBase()), null, null, null),
		CT_C17(Mod303Key.CT_C17,
				(mod, vat) -> c17Filter(mod,vat),
				(ctx, mod, vat) -> greatherQuotaPercent(Mod303Key.CT_C17, mod, vat)
				, (ctx, mod) -> add(Mod303Key.CT_C17, mod,
					(mod.getYear() > 2024 
				  || mod.getYear() == 2024 && (
						  	 mod.getPeriod() == Period.M10
						  || mod.getPeriod() == Period.M11
						  || mod.getPeriod() == Period.M12
						  || mod.getPeriod() == Period.T4
					  ))
						?SURCHARGE_PERCENT_1
						:0.0)
				, null, null),
		CT_C18(Mod303Key.CT_C18,
				(mod, vat) -> c17Filter(mod,vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C18, mod, vat.getSurchargeQuota()), null, null, null),

//Base imponible [19]		Tipo % [20]		Cuota [21]		------>	1.40
		
		CT_C19(Mod303Key.CT_C19,
				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent_140(vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C19, mod, vat.getBase()), null, null, null),
		CT_C20(Mod303Key.CT_C20, null, null, (ctx, mod) -> add(Mod303Key.CT_C20, mod, SURCHARGE_PERCENT_14), null, null),
		CT_C21(Mod303Key.CT_C21,
				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent_140(vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C21, mod, vat.getSurchargeQuota()), null, null, null),

//Base imponible [22]		Tipo % [23]		Cuota [24]		------>	5.20
		CT_C22(Mod303Key.CT_C22,
				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent52(vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C22, mod, vat.getBase()), null, null, null),
		CT_C23(Mod303Key.CT_C23, null, null, (ctx, mod) -> add(Mod303Key.CT_C23, mod, SURCHARGE_PERCENT_52), null, null),
		CT_C24(Mod303Key.CT_C24,
				(mod, vat) -> isCommonNationalSales(vat, mod) && vat.isSurcharge() && hasSurchargePercent52(vat),
				(ctx, mod, vat) -> add(Mod303Key.CT_C24, mod, vat.getSurchargeQuota()), null, null, null)

		// Modificaciones bases y cuotas del recargo de equivalencia
		,
		CT_C25(Mod303Key.CT_C25, (mod, vat) -> (vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()),
				(ctx, mod, vat) -> add(Mod303Key.CT_C25, mod, vat.getBase()), null, null, null),
		CT_C26(Mod303Key.CT_C26,
				(mod, vat) -> vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
						&& vat.isNational() && vat.isSales() && vat.isSurcharge() && vat.isRectification(),
				(ctx, mod, vat) -> add(Mod303Key.CT_C26, mod, vat.getSurchargeQuota()), null, null, null)

		// Total cuota devengada
		, CT_C27(Mod303Key.CT_C27, null, null, null,
			"CT_C152+CT_C167+CT_C03+CT_C155+CT_C06+CT_C09+CT_C11+CT_C13+CT_C15+CT_C170+CT_C158+CT_C18+CT_C21+CT_C24+CT_C26", null)

		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------

		// Por cuotas soportadas en operaciones interiores corrientes
		,
		CT_C28(Mod303Key.CT_C28, (mod, vat) -> operacionesInterioresCorrientesFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C28, mod, vat.getBase()), null, null, null),
		CT_C29(Mod303Key.CT_C29, (mod, vat) -> operacionesInterioresCorrientesFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CT_C29, mod, vat), null, null, null)

		// Por cuotas soportadas en operaciones interiores con bienes de inversión
		,
		CT_C30(Mod303Key.CT_C30, (mod, vat) -> operacionesInterioresInversionFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C30, mod, vat.getBase()), null, null, null),
		CT_C31(Mod303Key.CT_C31, (mod, vat) -> operacionesInterioresInversionFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CT_C31, mod, vat), null, null, null)

		// Por cuotas soportadas en las importaciones de bienes corrientes
		,
		CT_C32(Mod303Key.CT_C32, (mod, vat) -> importacionesCorrientesFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C32, mod, vat.getBase()), null, null, null),
		CT_C33(Mod303Key.CT_C33, (mod, vat) -> importacionesCorrientesFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CT_C33, mod, vat), null, null, null)

		// Por cuotas soportadas en las importaciones de bienes de inversión
		,
		CT_C34(Mod303Key.CT_C34, (mod, vat) -> importacionesInversionFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C34, mod, vat.getBase()), null, null, null),
		CT_C35(Mod303Key.CT_C35, (mod, vat) -> importacionesInversionFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CT_C35, mod, vat), null, null, null)

		// Por cuotas soportadas en adquisiciones intracomunitarias de bienes y
		// servicios corrientes
		,
		CT_C36(Mod303Key.CT_C36, (mod, vat) -> adqIntracomunitariasCorrientesFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C36, mod, vat.getBase()), null, null, null),
		CT_C37(Mod303Key.CT_C37, (mod, vat) -> adqIntracomunitariasCorrientesFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CT_C37, mod, vat), null, null, null)

		// Por cuotas soportadas en adquisiciones intracomunitarias de bienes de
		// inversión
		,
		CT_C38(Mod303Key.CT_C38, (mod, vat) -> adqIntracomunitariasInversionFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C38, mod, vat.getBase()), null, null, null),
		CT_C39(Mod303Key.CT_C39, (mod, vat) -> adqIntracomunitariasInversionFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CT_C39, mod, vat), null, null, null)

		// Rectificación de deducciones
		,
		CT_C40(Mod303Key.CT_C40, (mod, vat) -> rectificacionDeduccionesFilter(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C40, mod, vat.getBase()), null, null, null),
		CT_C41(Mod303Key.CT_C41, (mod, vat) -> rectificacionDeduccionesFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CT_C41, mod, vat), null, null, null)

		// Compensaciones Régimen Especial A.G. y P.
		, CT_C42(Mod303Key.CT_C42, (mod, vat) -> compensacionesRegAgrarioFilter(vat, mod),
				(ctx, mod, vat) -> addProrrated(Mod303Key.CT_C42, mod, vat), null, null, null)

		// Regularización inversiones
		, CT_C43(Mod303Key.CT_C43)

		// Regularización por aplicación del porcentaje definitivo de prorrata
		, CT_C44(Mod303Key.CT_C44, null, null, null
				,null
				,null
//				,"{messages : ["
//						+ "\"Total IVA deducible sin prorratear antes del periodo que se liquida: @{CM_072}\","
//						+ "\"IVA deducible con prorrata (@{CM_007}%) de los periodos anteriores:\","
//						+ "\"@{CM_072} * @{CM_007} / 100 = @{com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_007/100)}\","
//						+ "\"IVA deducible con prorrata definitiva (@{CM_003}%) de los periodos anteriores:\","
//						+ "\"@{CM_072} * @{CM_003} / 100 = @{com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_003/100)}\","
//						+ "\"Resultado\","
//						+ "\"@{com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_003/100)}"
//						+  " - @{com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_007/100)}"
//						+  " = @{com.esferalia.aon.watson.util.AonMathUtils.round(com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_003/100) - com.esferalia.aon.watson.util.AonMathUtils.round(CM_072*CM_007/100))}\","
//					+"]}"
		)

		// Total a deducir
		, CT_C45(Mod303Key.CT_C45, null, null, null,
				"CT_C29+CT_C31+CT_C33+CT_C35+CT_C37+CT_C39+CT_C41+CT_C42+CT_C43+CT_C44", null)

		// Resultado Régimen general
		, CT_C46(Mod303Key.CT_C46, null, null, null, "CT_C27-CT_C45", null)

		// --------------------------------------------------------------
		// ----------------------------------------- REGIMEN SIMPLIFICADO
		// --------------------------------------------------------------

		// (1) Actividades agrícolas, ganaderas y forestales. Código
		,CT_SA11(Mod303Key.CT_SA11, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_SA11, ensureFarmerActivity(mod, 0).getCode())
			,mod -> ensureFarmerActivity(mod, 0).setCode(mod.getDescription(Mod303Key.CT_SA11))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA11))
		// (1) Actividades agrícolas, ganaderas y forestales. Descripción
		,CT_SA1D(Mod303Key.CT_SA1D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_SA1D, ensureFarmerActivity(mod, 0).getDescription())
			,mod -> ensureFarmerActivity(mod, 0).setDescription(mod.getDescription(Mod303Key.CT_SA1D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA1D))
		// (1) Actividades agrícolas, ganaderas y forestales. Volumen de ingresos
		,CT_SA12(Mod303Key.CT_SA12, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_SA12, ensureFarmerActivity(mod, 0).getVol())
			,mod -> ensureFarmerActivity(mod, 0).setVol(mod.getAmount(Mod303Key.CT_SA12))
			,null)
		// (1) Actividades agrícolas, ganaderas y forestales. Índice de cuota
		,CT_SA13(Mod303Key.CT_SA13, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_SA13, (ensureFarmerActivity(mod, 0).getInd() * 10000))
			,mod -> ensureFarmerActivity(mod, 0).setInd(mod.getAmount(Mod303Key.CT_SA13) / 10000)
			,null)
		// (1) Actividades agrícolas, ganaderas y forestales. Cuota devengada
		,CT_SA14(Mod303Key.CT_SA14, null, null, null
			,"(hasFarmerActivity(0))?round(CT_SA12*CT_SA13/10000):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA14, ensureFarmerActivity(mod, 0).getCuo())
			,mod -> ensureFarmerActivity(mod, 0).setCuo(mod.getAmount(Mod303Key.CT_SA14))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA14))
		// (1) Actividades agrícolas, ganaderas y forestales. Porcentaje trimestral
		,CT_SA15(Mod303Key.CT_SA15, null, null, null
			,"(hasFarmerActivity(0) && !isLastPeriod())?CT_SA15:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA15, ensureFarmerActivity(mod, 0).getPor())
			,mod -> ensureFarmerActivity(mod, 0).setPor(mod.getAmount(Mod303Key.CT_SA15))
			,null)
		// (1) Actividades agrícolas, ganaderas y forestales. Ingreso a cuenta [A]
		,CT_SA16(Mod303Key.CT_SA16, null, null, null
			,"(hasFarmerActivity(0) && !isLastPeriod())?round(CT_SA14*CT_SA15/100):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA16, ensureFarmerActivity(mod, 0).getIng())
			,mod -> ensureFarmerActivity(mod, 0).setIng(mod.getAmount(Mod303Key.CT_SA16))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA16))

		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// (1) Actividades agrícolas, ganaderas y forestales. Cuota soportada
		,CT_SA17(Mod303Key.CT_SA17, null, null, null
			,"(hasFarmerActivity(0) && isLastPeriod())?CT_SA17:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA17, ensureFarmerActivity(mod, 0).getSop())
			,mod -> ensureFarmerActivity(mod, 0).setSop(mod.getAmount(Mod303Key.CT_SA17))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA17))
		// (1) Compensaciones satisfechas a sujetos pasivos en R.E.A.G.P. (4T)
		,CT_SA1B(Mod303Key.CT_SA1B, null, null, null
			,"(hasFarmerActivity(0) && isLastPeriod())?CT_SA1B:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA1B, ensureFarmerActivity(mod, 0).getCom())
			,mod -> ensureFarmerActivity(mod, 0).setCom(mod.getAmount(Mod303Key.CT_SA1B))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA1B))
		// (1) 1% de la cuota devengada por operaciones corrientes
		,CT_SA1C(Mod303Key.CT_SA1C, null, null, null
			,"(hasFarmerActivity(0) && isLastPeriod())?round(CT_SA14 * 1 / 100):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA1C, ensureFarmerActivity(mod, 0).getDev())
			,mod -> ensureFarmerActivity(mod, 0).setDev(mod.getAmount(Mod303Key.CT_SA1C))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA1C))
		// (1) Cuota soportada (4T)
		,CT_SA1A(Mod303Key.CT_SA1A, null, null, null
			,"(hasFarmerActivity(0) && isLastPeriod())?round(CT_SA17+CT_SA1B+CT_SA1C):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA1A, ensureFarmerActivity(mod, 0).getTso())
			,mod -> ensureFarmerActivity(mod, 0).setTso(mod.getAmount(Mod303Key.CT_SA1A))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA1A))
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		
		// (1) Actividades agrícolas, ganaderas y forestales. Cuota anual derivada del regimen simplificado [B]
		,CT_SA18(Mod303Key.CT_SA18, null, null, null
			,"(hasFarmerActivity(0) && isLastPeriod())?round(CT_SA14-CT_SA1A):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA18, ensureFarmerActivity(mod, 0).getCad())
			,mod -> ensureFarmerActivity(mod, 0).setCad(mod.getAmount(Mod303Key.CT_SA18))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA18))
		// (2) Actividades agrícolas, ganaderas y forestales. Código
		,CT_SA21(Mod303Key.CT_SA21, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_SA21, ensureFarmerActivity(mod, 1).getCode())
			,mod -> ensureFarmerActivity(mod, 1).setCode(mod.getDescription(Mod303Key.CT_SA21))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA21))
		// (2) Actividades agrícolas, ganaderas y forestales. Descripción
		,CT_SA2D(Mod303Key.CT_SA2D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_SA2D, ensureFarmerActivity(mod, 1).getDescription())
			,mod -> ensureFarmerActivity(mod, 1).setDescription(mod.getDescription(Mod303Key.CT_SA2D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA2D))
		// (2) Actividades agrícolas, ganaderas y forestales. Volumen de ingresos
		,CT_SA22(Mod303Key.CT_SA22, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_SA22, ensureFarmerActivity(mod, 1).getVol())
			,mod -> ensureFarmerActivity(mod, 1).setVol(mod.getAmount(Mod303Key.CT_SA22))
			,null)
		// (2) Actividades agrícolas, ganaderas y forestales. Índice de cuota
		,CT_SA23(Mod303Key.CT_SA23, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_SA23, ensureFarmerActivity(mod, 1).getInd() * 10000)
			,mod -> ensureFarmerActivity(mod, 1).setInd(mod.getAmount(Mod303Key.CT_SA23) / 10000)
			,null)
		// (2) Actividades agrícolas, ganaderas y forestales. Cuota devengada
		,CT_SA24(Mod303Key.CT_SA24, null, null, null, "(hasFarmerActivity(1))?round(CT_SA22*CT_SA23/10000):(0.0)", null
			,mod -> mod.putAmount(Mod303Key.CT_SA24, ensureFarmerActivity(mod, 1).getCuo())
			,mod -> ensureFarmerActivity(mod, 1).setCuo(mod.getAmount(Mod303Key.CT_SA24))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA24))
		// (2) Actividades agrícolas, ganaderas y forestales. Porcentaje trimestral
		,
		CT_SA25(Mod303Key.CT_SA25, null, null, null
			,"(hasFarmerActivity(1) && !isLastPeriod())?CT_SA25:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA25, ensureFarmerActivity(mod, 1).getPor())
			,mod -> ensureFarmerActivity(mod, 1).setPor(mod.getAmount(Mod303Key.CT_SA25))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA25))
		// (2) Actividades agrícolas, ganaderas y forestales. Ingreso a cuenta [A]
		,CT_SA26(Mod303Key.CT_SA26, null, null, null
			,"(hasFarmerActivity(1) && !isLastPeriod())?round(CT_SA24*CT_SA25/100):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA26, ensureFarmerActivity(mod, 1).getIng())
			,mod -> ensureFarmerActivity(mod, 1).setIng(mod.getAmount(Mod303Key.CT_SA26))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA26))
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// (1) Actividades agrícolas, ganaderas y forestales. Cuota soportada
		,CT_SA27(Mod303Key.CT_SA27, null, null, null
			,"(hasFarmerActivity(1) && isLastPeriod())?CT_SA27:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA27, ensureFarmerActivity(mod, 1).getSop())
			,mod -> ensureFarmerActivity(mod, 1).setSop(mod.getAmount(Mod303Key.CT_SA27))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA27))
		// (1) Compensaciones satisfechas a sujetos pasivos en R.E.A.G.P. (4T)
		,CT_SA2B(Mod303Key.CT_SA2B, null, null, null
			,"(hasFarmerActivity(1) && isLastPeriod())?CT_SA2B:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA2B, ensureFarmerActivity(mod, 1).getCom())
			,mod -> ensureFarmerActivity(mod, 1).setCom(mod.getAmount(Mod303Key.CT_SA2B))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA2B))
		// (1) 1% de la cuota devengada por operaciones corrientes
		,CT_SA2C(Mod303Key.CT_SA2C, null, null, null
			,"(hasFarmerActivity(1) && isLastPeriod())?round(CT_SA24 * 1 / 100):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA2C, ensureFarmerActivity(mod, 1).getDev())
			,mod -> ensureFarmerActivity(mod, 1).setDev(mod.getAmount(Mod303Key.CT_SA2C))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA2C))
		// (1) Cuota soportada (4T)
		,CT_SA2A(Mod303Key.CT_SA2A, null, null, null
			,"(hasFarmerActivity(1) && isLastPeriod())?round(CT_SA27+CT_SA2B+CT_SA2C):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA2A, ensureFarmerActivity(mod, 1).getTso())
			,mod -> ensureFarmerActivity(mod, 1).setTso(mod.getAmount(Mod303Key.CT_SA2A))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA2A))
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// (2) Actividades agrícolas, ganaderas y forestales. Cuota anual derivada del regimen simplificado [B]
		,CT_SA28(Mod303Key.CT_SA28, null, null, null
			,"(hasFarmerActivity(1) && isLastPeriod())?round(CT_SA24-CT_SA2A):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA28, ensureFarmerActivity(mod, 1).getCad())
			,mod -> ensureFarmerActivity(mod, 1).setCad(mod.getAmount(Mod303Key.CT_SA28))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA28))
		// (3) Actividades agrícolas, ganaderas y forestales. Código
		,CT_SA31(Mod303Key.CT_SA31, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_SA31, ensureFarmerActivity(mod, 2).getCode())
			,mod -> ensureFarmerActivity(mod, 2).setCode(mod.getDescription(Mod303Key.CT_SA31))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA31))
		// (3) Actividades agrícolas, ganaderas y forestales. Descripción
		,CT_SA3D(Mod303Key.CT_SA3D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_SA3D, ensureFarmerActivity(mod, 2).getDescription())
			,mod -> ensureFarmerActivity(mod, 2).setDescription(mod.getDescription(Mod303Key.CT_SA3D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA3D))
		// (3) Actividades agrícolas, ganaderas y forestales. Volumen de ingresos
		,CT_SA32(Mod303Key.CT_SA32, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_SA32, ensureFarmerActivity(mod, 2).getVol())
			,mod -> ensureFarmerActivity(mod, 2).setVol(mod.getAmount(Mod303Key.CT_SA32))
			,null)
		// (3) Actividades agrícolas, ganaderas y forestales. Índice de cuota
		,CT_SA33(Mod303Key.CT_SA33, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_SA33, ensureFarmerActivity(mod, 2).getInd() * 10000)
			,mod -> ensureFarmerActivity(mod, 2).setInd(mod.getAmount(Mod303Key.CT_SA33) / 10000)
			,null)
		// (3) Actividades agrícolas, ganaderas y forestales. Cuota devengada
		,CT_SA34(Mod303Key.CT_SA34, null, null, null
			,"(hasFarmerActivity(2))?round(CT_SA32*CT_SA33/10000):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA34, ensureFarmerActivity(mod, 2).getCuo())
			,mod -> ensureFarmerActivity(mod, 2).setCuo(mod.getAmount(Mod303Key.CT_SA34))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA34))
		// (3) Actividades agrícolas, ganaderas y forestales. Porcentaje trimestral
		,CT_SA35(Mod303Key.CT_SA35, null, null, null
			,"(hasFarmerActivity(2) && !isLastPeriod())?CT_SA35:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA35, ensureFarmerActivity(mod, 2).getPor())
			,mod -> ensureFarmerActivity(mod, 2).setPor(mod.getAmount(Mod303Key.CT_SA35))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA35))
		// (3) Actividades agrícolas, ganaderas y forestales. Ingreso a cuenta [A]
		,CT_SA36(Mod303Key.CT_SA36, null, null, null
			,"(hasFarmerActivity(2) && !isLastPeriod())?round(CT_SA34*CT_SA35/100):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA36, ensureFarmerActivity(mod, 2).getIng())
			,mod -> ensureFarmerActivity(mod, 2).setIng(mod.getAmount(Mod303Key.CT_SA36))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA36))
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// (1) Actividades agrícolas, ganaderas y forestales. Cuota soportada
		,CT_SA37(Mod303Key.CT_SA37, null, null, null
			,"(hasFarmerActivity(2) && isLastPeriod())?CT_SA37:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA37, ensureFarmerActivity(mod, 2).getSop())
			,mod -> ensureFarmerActivity(mod, 2).setSop(mod.getAmount(Mod303Key.CT_SA37))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA37))
		// (1) Compensaciones satisfechas a sujetos pasivos en R.E.A.G.P. (4T)
		,CT_SA3B(Mod303Key.CT_SA3B, null, null, null
			,"(hasFarmerActivity(2) && isLastPeriod())?CT_SA3B:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA3B, ensureFarmerActivity(mod, 2).getCom())
			,mod -> ensureFarmerActivity(mod, 2).setCom(mod.getAmount(Mod303Key.CT_SA3B))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA3B))
		// (1) 1% de la cuota devengada por operaciones corrientes
		,CT_SA3C(Mod303Key.CT_SA3C, null, null, null
			,"(hasFarmerActivity(2) && isLastPeriod())?round(CT_SA34 * 1 / 100):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA3C, ensureFarmerActivity(mod, 2).getDev())
			,mod -> ensureFarmerActivity(mod, 2).setDev(mod.getAmount(Mod303Key.CT_SA3C))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA3C))
		// (1) Cuota soportada (4T)
		,CT_SA3A(Mod303Key.CT_SA3A, null, null, null
			,"(hasFarmerActivity(2) && isLastPeriod())?round(CT_SA37+CT_SA3B+CT_SA3C):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA3A, ensureFarmerActivity(mod, 2).getTso())
			,mod -> ensureFarmerActivity(mod, 2).setTso(mod.getAmount(Mod303Key.CT_SA3A))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA3A))
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// (3) Actividades agrícolas, ganaderas y forestales. Cuota anual derivada del regimen simplificado [B]
		,CT_SA38(Mod303Key.CT_SA38, null, null, null
			,"(hasFarmerActivity(2) && isLastPeriod())?round(CT_SA34-CT_SA3A):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA38, ensureFarmerActivity(mod, 2).getCad())
			,mod -> ensureFarmerActivity(mod, 2).setCad(mod.getAmount(Mod303Key.CT_SA38))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA38))

		// (4) Actividades agrícolas, ganaderas y forestales. Código
		,CT_SA41(Mod303Key.CT_SA41, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_SA41, ensureFarmerActivity(mod, 3).getCode())
			,mod -> ensureFarmerActivity(mod, 3).setCode(mod.getDescription(Mod303Key.CT_SA41))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA41))
		// (4) Actividades agrícolas, ganaderas y forestales. Descripción
		,CT_SA4D(Mod303Key.CT_SA4D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_SA4D, ensureFarmerActivity(mod, 3).getDescription())
			,mod -> ensureFarmerActivity(mod, 3).setDescription(mod.getDescription(Mod303Key.CT_SA4D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA4D))
		// (4) Actividades agrícolas, ganaderas y forestales. Volumen de ingresos
		,CT_SA42(Mod303Key.CT_SA42, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_SA42, ensureFarmerActivity(mod, 3).getVol())
			,mod -> ensureFarmerActivity(mod, 3).setVol(mod.getAmount(Mod303Key.CT_SA42))
			,null)
		// (4) Actividades agrícolas, ganaderas y forestales. Índice de cuota
		,
		CT_SA43(Mod303Key.CT_SA43, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_SA43, ensureFarmerActivity(mod, 3).getInd() * 10000)
			,mod -> ensureFarmerActivity(mod, 3).setInd(mod.getAmount(Mod303Key.CT_SA43) / 10000)
			,null)
		// (4) Actividades agrícolas, ganaderas y forestales. Cuota devengada
		,CT_SA44(Mod303Key.CT_SA44, null, null, null
			,"(hasFarmerActivity(3))?round(CT_SA42*CT_SA43/10000):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA44, ensureFarmerActivity(mod, 3).getCuo())
			,mod -> ensureFarmerActivity(mod, 3).setCuo(mod.getAmount(Mod303Key.CT_SA44))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA44))
		// (4) Actividades agrícolas, ganaderas y forestales. Porcentaje trimestral
		,CT_SA45(Mod303Key.CT_SA45, null, null, null
			,"(hasFarmerActivity(3) && !isLastPeriod())?CT_SA45:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA45, ensureFarmerActivity(mod, 3).getPor())
			,mod -> ensureFarmerActivity(mod, 3).setPor(mod.getAmount(Mod303Key.CT_SA45))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA45))
		// (4) Actividades agrícolas, ganaderas y forestales. Ingreso a cuenta [A]
		,CT_SA46(Mod303Key.CT_SA46, null, null, null
			,"(hasFarmerActivity(3) && !isLastPeriod())?round(CT_SA44*CT_SA45/100):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA46, ensureFarmerActivity(mod, 3).getIng())
			,mod -> ensureFarmerActivity(mod, 3).setIng(mod.getAmount(Mod303Key.CT_SA46))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA46))
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// (4) Actividades agrícolas, ganaderas y forestales. Cuota soportada
		,CT_SA47(Mod303Key.CT_SA47, null, null, null
			,"(hasFarmerActivity(3) && isLastPeriod())?CT_SA47:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA47, ensureFarmerActivity(mod, 3).getSop())
			,mod -> ensureFarmerActivity(mod, 3).setSop(mod.getAmount(Mod303Key.CT_SA47))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA47))
		// (4) Compensaciones satisfechas a sujetos pasivos en R.E.A.G.P. (4T)
		,CT_SA4B(Mod303Key.CT_SA4B, null, null, null
			,"(hasFarmerActivity(3) && isLastPeriod())?CT_SA4B:(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA4B, ensureFarmerActivity(mod, 3).getCom())
			,mod -> ensureFarmerActivity(mod, 3).setCom(mod.getAmount(Mod303Key.CT_SA4B))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA4B))
		// (4) 1% de la cuota devengada por operaciones corrientes
		,CT_SA4C(Mod303Key.CT_SA4C, null, null, null
			,"(hasFarmerActivity(3) && isLastPeriod())?round(CT_SA44 * 1 / 100):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA4C, ensureFarmerActivity(mod, 3).getDev())
			,mod -> ensureFarmerActivity(mod, 3).setDev(mod.getAmount(Mod303Key.CT_SA4C))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA4C))
		// (4) Cuota soportada (4T)
		,CT_SA4A(Mod303Key.CT_SA4A, null, null, null
			,"(hasFarmerActivity(3) && isLastPeriod())?round(CT_SA47+CT_SA4B+CT_SA4C):(0.0)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_SA4A, ensureFarmerActivity(mod, 3).getTso())
			,mod -> ensureFarmerActivity(mod, 3).setTso(mod.getAmount(Mod303Key.CT_SA4A))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA4A))
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// ******************		
		// (4) Actividades agrícolas, ganaderas y forestales. Cuota anual derivada del regimen simplificado [B]
		,CT_SA48(Mod303Key.CT_SA48, null, null, null
			,"(hasFarmerActivity(3) && isLastPeriod())?round(CT_SA44-CT_SA4A):(0.0)", null
			,mod -> mod.putAmount(Mod303Key.CT_SA48, ensureFarmerActivity(mod, 3).getCad())
			,mod -> ensureFarmerActivity(mod, 3).setCad(mod.getAmount(Mod303Key.CT_SA48))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_SA48))
		
		// (1) Actividades en régimen simplificado. Epigrafe IAE
		,CT_S101(Mod303Key.CT_S101, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S101, ensureActivity(mod, 0).getEpigraph())
			,mod -> ensureActivity(mod, 0).setEpigraph(mod.getDescription(Mod303Key.CT_S101))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S101))
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,CT_S10D(Mod303Key.CT_S10D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S10D, ensureActivity(mod, 0).getDescription())
			,mod -> ensureActivity(mod, 0).setDescription(mod.getDescription(Mod303Key.CT_S10D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S10D))
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de ep\u00EDgrafes 691.9 y 722
		,CT_S102(Mod303Key.CT_S102, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S102, ensureActivity(mod, 0).getSpecialEpigraph())
			,mod -> ensureActivity(mod, 0).setSpecialEpigraph((int) mod.getAmount(Mod303Key.CT_S102))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S102))
		// (1) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,CT_S1X1(Mod303Key.CT_S1X1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1X1, ensureActivity(mod, 0).getTem())
			,mod -> ensureActivity(mod, 0).setTem((int) mod.getAmount(Mod303Key.CT_S1X1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1X1))
		// (1) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,CT_S1X2(Mod303Key.CT_S1X2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1X2, ensureActivity(mod, 0).getDia())
			,mod -> ensureActivity(mod, 0).setDia((int) mod.getAmount(Mod303Key.CT_S1X2))
			,(prev,cur) -> ensureActivityDays(prev,cur, Mod303Key.CT_S101, Mod303Key.CT_S1X2))
		// (1) Actividades en régimen simplificado. Número de empleados al inicio del ejercicio ( o al inicio de la actividad)
		,CT_S1X3(Mod303Key.CT_S1X3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1X3, ensureActivity(mod, 0).getEmp())
			,mod -> ensureActivity(mod, 0).setEmp((int) mod.getAmount(Mod303Key.CT_S1X3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1X3))
		// (1) Actividades en régimen simplificado. Si realiza la actividad en LORCA
		,CT_S1X4(Mod303Key.CT_S1X4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1X4, ensureActivity(mod, 0).getLor())
			,mod -> ensureActivity(mod, 0).setLor((int) mod.getAmount(Mod303Key.CT_S1X4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1X4))
		// (1) Reduccion extraordinaria por covid-19, art. 9 RD-Ley 35/2020)
		,CT_S1X5(Mod303Key.CT_S1X5, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1X5, ensureActivity(mod, 0).getCov())
			,mod -> ensureActivity(mod, 0).setCov((int) mod.getAmount(Mod303Key.CT_S1X5))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1X5))
		,CT_S11D(Mod303Key.CT_S11D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S11D, ensureModule(mod, 0, 0).getDescription())
			,mod -> ensureModule(mod, 0, 0).setDescription(mod.getDescription(Mod303Key.CT_S11D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S11D))
		,CT_S11I(Mod303Key.CT_S11I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S11I, ensureModule(mod, 0, 0).getValue())
			,mod -> ensureModule(mod, 0, 0).setValue(mod.getAmount(Mod303Key.CT_S11I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S11I))
		,CT_S11U(Mod303Key.CT_S11U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S11U, ensureModule(mod, 0, 0).getUnit())
			,mod -> ensureModule(mod, 0, 0).setUnit(mod.getDescription(Mod303Key.CT_S11U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S11U))
		,CT_S11F(Mod303Key.CT_S11F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S11F, ensureModule(mod, 0, 0).getFactor())
			,mod -> ensureModule(mod, 0, 0).setFactor(mod.getAmount(Mod303Key.CT_S11F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S11F))
		,CT_S11R(Mod303Key.CT_S11R, null, null, null
			,"calculateResult(0,CT_S11I,CT_S11F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S11R, ensureModule(mod, 0, 0).getResult())
			,mod -> ensureModule(mod, 0, 0).setResult(mod.getAmount(Mod303Key.CT_S11R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S11R))
		,CT_S12D(Mod303Key.CT_S12D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S12D, ensureModule(mod, 0, 1).getDescription())
			,mod -> ensureModule(mod, 0, 1).setDescription(mod.getDescription(Mod303Key.CT_S12D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S12D))
		,CT_S12I(Mod303Key.CT_S12I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S12I, ensureModule(mod, 0, 1).getValue())
			,mod -> ensureModule(mod, 0, 1).setValue(mod.getAmount(Mod303Key.CT_S12I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S12I))
		,CT_S12U(Mod303Key.CT_S12U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S12U, ensureModule(mod, 0, 1).getUnit())
			,mod -> ensureModule(mod, 0, 1).setUnit(mod.getDescription(Mod303Key.CT_S12U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S12U))
		,CT_S12F(Mod303Key.CT_S12F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S12F, ensureModule(mod, 0, 1).getFactor())
			,mod -> ensureModule(mod, 0, 1).setFactor(mod.getAmount(Mod303Key.CT_S12F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S12F))
		,CT_S12R(Mod303Key.CT_S12R, null, null, null
			,"calculateResult(0,CT_S12I,CT_S12F)"	
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S12R, ensureModule(mod, 0, 1).getResult())
			,mod -> ensureModule(mod, 0, 1).setResult(mod.getAmount(Mod303Key.CT_S12R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S12R))
		,CT_S13D(Mod303Key.CT_S13D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S13D, ensureModule(mod, 0, 2).getDescription())
			,mod -> ensureModule(mod, 0, 2).setDescription(mod.getDescription(Mod303Key.CT_S13D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S13D))
		,CT_S13I(Mod303Key.CT_S13I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S13I, ensureModule(mod, 0, 2).getValue())
			,mod -> ensureModule(mod, 0, 2).setValue(mod.getAmount(Mod303Key.CT_S13I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S13I))
		,CT_S13U(Mod303Key.CT_S13U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S13U, ensureModule(mod, 0, 2).getUnit())
			,mod -> ensureModule(mod, 0, 2).setUnit(mod.getDescription(Mod303Key.CT_S13U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S13U))
		,CT_S13F(Mod303Key.CT_S13F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S13F, ensureModule(mod, 0, 2).getFactor())
			,mod -> ensureModule(mod, 0, 2).setFactor(mod.getAmount(Mod303Key.CT_S13F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S13F))
		,CT_S13R(Mod303Key.CT_S13R, null, null, null
			,"calculateResult(0,CT_S13I,CT_S13F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S13R, ensureModule(mod, 0, 2).getResult())
			,mod -> ensureModule(mod, 0, 2).setResult(mod.getAmount(Mod303Key.CT_S13R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S13R))
		,CT_S14D(Mod303Key.CT_S14D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S14D, ensureModule(mod, 0, 3).getDescription())
			,mod -> ensureModule(mod, 0, 3).setDescription(mod.getDescription(Mod303Key.CT_S14D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S14D))
		,CT_S14I(Mod303Key.CT_S14I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S14I, ensureModule(mod, 0, 3).getValue())
			,mod -> ensureModule(mod, 0, 3).setValue(mod.getAmount(Mod303Key.CT_S14I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S14I))
		,CT_S14U(Mod303Key.CT_S14U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S14U, ensureModule(mod, 0, 3).getUnit())
			,mod -> ensureModule(mod, 0, 3).setUnit(mod.getDescription(Mod303Key.CT_S14U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S14U))
		,
		CT_S14F(Mod303Key.CT_S14F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S14F, ensureModule(mod, 0, 3).getFactor())
			,mod -> ensureModule(mod, 0, 3).setFactor(mod.getAmount(Mod303Key.CT_S14F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S14F))
		,CT_S14R(Mod303Key.CT_S14R, null, null, null
			,"calculateResult(0,CT_S14I,CT_S14F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S14R, ensureModule(mod, 0, 3).getResult())
			,mod -> ensureModule(mod, 0, 3).setResult(mod.getAmount(Mod303Key.CT_S14R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S14R))
		,CT_S15D(Mod303Key.CT_S15D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S15D, ensureModule(mod, 0, 4).getDescription())
			,mod -> ensureModule(mod, 0, 4).setDescription(mod.getDescription(Mod303Key.CT_S15D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S15D))
		,CT_S15I(Mod303Key.CT_S15I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S15I, ensureModule(mod, 0, 4).getValue())
			,mod -> ensureModule(mod, 0, 4).setValue(mod.getAmount(Mod303Key.CT_S15I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S15I))
		,CT_S15U(Mod303Key.CT_S15U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S15U, ensureModule(mod, 0, 4).getUnit())
			,mod -> ensureModule(mod, 0, 4).setUnit(mod.getDescription(Mod303Key.CT_S15U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S15U))
		,CT_S15F(Mod303Key.CT_S15F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S15F, ensureModule(mod, 0, 4).getFactor())
			,mod -> ensureModule(mod, 0, 4).setFactor(mod.getAmount(Mod303Key.CT_S15F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S15F))
		,CT_S15R(Mod303Key.CT_S15R, null, null, null
			,"calculateResult(0,CT_S15I,CT_S15F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S15R, ensureModule(mod, 0, 4).getResult())
			,mod -> ensureModule(mod, 0, 4).setResult(mod.getAmount(Mod303Key.CT_S15R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S15R))
		,CT_S16D(Mod303Key.CT_S16D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S16D, ensureModule(mod, 0, 5).getDescription())
			,mod -> ensureModule(mod, 0, 5).setDescription(mod.getDescription(Mod303Key.CT_S16D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S16D))
		,CT_S16I(Mod303Key.CT_S16I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S16I, ensureModule(mod, 0, 5).getValue())
			,mod -> ensureModule(mod, 0, 5).setValue(mod.getAmount(Mod303Key.CT_S16I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S16I))
		,CT_S16U(Mod303Key.CT_S16U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S16U, ensureModule(mod, 0, 5).getUnit())
			,mod -> ensureModule(mod, 0, 5).setUnit(mod.getDescription(Mod303Key.CT_S16U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S16U))
		,CT_S16F(Mod303Key.CT_S16F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S16F, ensureModule(mod, 0, 5).getFactor())
			,mod -> ensureModule(mod, 0, 5).setFactor(mod.getAmount(Mod303Key.CT_S16F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S16F))
		,CT_S16R(Mod303Key.CT_S16R, null, null, null
			,"calculateResult(0,CT_S16I,CT_S16F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S16R, ensureModule(mod, 0, 5).getResult())
			,mod -> ensureModule(mod, 0, 5).setResult(mod.getAmount(Mod303Key.CT_S16R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S16R))
		,CT_S17D(Mod303Key.CT_S17D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S17D, ensureModule(mod, 0, 6).getDescription())
			,mod -> ensureModule(mod, 0, 6).setDescription(mod.getDescription(Mod303Key.CT_S17D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S17D))
		,CT_S17I(Mod303Key.CT_S17I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S17I, ensureModule(mod, 0, 6).getValue())
			,mod -> ensureModule(mod, 0, 6).setValue(mod.getAmount(Mod303Key.CT_S17I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S17I))
		,CT_S17U(Mod303Key.CT_S17U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S17U, ensureModule(mod, 0, 6).getUnit())
			,mod -> ensureModule(mod, 0, 6).setUnit(mod.getDescription(Mod303Key.CT_S17U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S17U))
		,CT_S17F(Mod303Key.CT_S17F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S17F, ensureModule(mod, 0, 6).getFactor())
			,mod -> ensureModule(mod, 0, 6).setFactor(mod.getAmount(Mod303Key.CT_S17F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S17F))
		,CT_S1P1(Mod303Key.CT_S1P1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1P1, ensureActivity(mod, 0).getMay19Hours())
			,mod -> ensureActivity(mod, 0).setMay19Hours(mod.getAmount(Mod303Key.CT_S1P1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1P1))
		,CT_S1P2(Mod303Key.CT_S1P2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1P2, ensureActivity(mod, 0).getMen19Hours())
			,mod -> ensureActivity(mod, 0).setMen19Hours(mod.getAmount(Mod303Key.CT_S1P2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1P2))
		,CT_S1P3(Mod303Key.CT_S1P3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1P3, ensureActivity(mod, 0).getDisHours())
			,mod -> ensureActivity(mod, 0).setDisHours(mod.getAmount(Mod303Key.CT_S1P3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1P3))
		,CT_S1P4(Mod303Key.CT_S1P4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1P4, ensureActivity(mod, 0).getYearHours())
			,mod -> ensureActivity(mod, 0).setYearHours(mod.getAmount(Mod303Key.CT_S1P4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1P4))
		,CT_S1E1(Mod303Key.CT_S1E1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1E1, ensureActivity(mod, 0).getOwnerHours())
			,mod -> ensureActivity(mod, 0).setOwnerHours(mod.getAmount(Mod303Key.CT_S1E1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1E1))
		,CT_S1E2(Mod303Key.CT_S1E2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1E2, ensureActivity(mod, 0).isOwnerDis()?1:0)
			,mod -> ensureActivity(mod, 0).setOwnerDis(mod.getAmount(Mod303Key.CT_S1E2) == 1)
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1E2))
		,CT_S1E3(Mod303Key.CT_S1E3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1E3, ensureActivity(mod, 0).getSpouseHours())
			,mod -> ensureActivity(mod, 0).setSpouseHours(mod.getAmount(Mod303Key.CT_S1E3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1E3))
		,CT_S1E4(Mod303Key.CT_S1E4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1E4, ensureActivity(mod, 0).getChildMen18Hours())
			,mod -> ensureActivity(mod, 0).setChildMen18Hours(mod.getAmount(Mod303Key.CT_S1E4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1E4))

		,CT_S1C1(Mod303Key.CT_S1C1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1C1, ensureDesk(mod, 0, 0).getDeskCapacity())
			,mod -> ensureDesk(mod, 0, 0).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S1C1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1C1))
		,CT_S1M1(Mod303Key.CT_S1M1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1M1, ensureDesk(mod, 0, 0).getDesks())
			,mod -> ensureDesk(mod, 0, 0).setDesks((int) mod.getAmount(Mod303Key.CT_S1M1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1M1))
		,CT_S1D1(Mod303Key.CT_S1D1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1D1, ensureDesk(mod, 0, 0).getDeskDays())
			,mod -> ensureDesk(mod, 0, 0).setDeskDays((int) mod.getAmount(Mod303Key.CT_S1D1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1D1))
		,CT_S1C2(Mod303Key.CT_S1C2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1C2, ensureDesk(mod, 0, 1).getDeskCapacity())
			,mod -> ensureDesk(mod, 0, 1).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S1C2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1C2))
		,CT_S1M2(Mod303Key.CT_S1M2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1M2, ensureDesk(mod, 0, 1).getDesks())
			,mod -> ensureDesk(mod, 0, 1).setDesks((int) mod.getAmount(Mod303Key.CT_S1M2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1M2))
		,CT_S1D2(Mod303Key.CT_S1D2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1D2, ensureDesk(mod, 0, 1).getDeskDays())
			,mod -> ensureDesk(mod, 0, 1).setDeskDays((int) mod.getAmount(Mod303Key.CT_S1D2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1D2))
		,CT_S1C3(Mod303Key.CT_S1C3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1C3, ensureDesk(mod, 0, 2).getDeskCapacity())
			,mod -> ensureDesk(mod, 0, 2).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S1C3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1C3))
		,CT_S1M3(Mod303Key.CT_S1M3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1M3, ensureDesk(mod, 0, 2).getDesks())
			,mod -> ensureDesk(mod, 0, 2).setDesks((int) mod.getAmount(Mod303Key.CT_S1M3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1M3))
		,CT_S1D3(Mod303Key.CT_S1D3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1D3, ensureDesk(mod, 0, 2).getDeskDays())
			,mod -> ensureDesk(mod, 0, 2).setDeskDays((int) mod.getAmount(Mod303Key.CT_S1D3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1D3))
		,CT_S1C4(Mod303Key.CT_S1C4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1C4, ensureDesk(mod, 0, 3).getDeskCapacity())
			,mod -> ensureDesk(mod, 0, 3).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S1C4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1C4))
		,CT_S1M4(Mod303Key.CT_S1M4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1M4, ensureDesk(mod, 0, 3).getDesks())
			,mod -> ensureDesk(mod, 0, 3).setDesks((int) mod.getAmount(Mod303Key.CT_S1M4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1M4))
		,CT_S1D4(Mod303Key.CT_S1D4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S1D4, ensureDesk(mod, 0, 3).getDeskDays())
			,mod -> ensureDesk(mod, 0, 3).setDeskDays((int) mod.getAmount(Mod303Key.CT_S1D4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S1D4))

		,CT_S17R(Mod303Key.CT_S17R, null, null, null
			,"calculateResult(0,CT_S17I,CT_S17F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S17R, ensureModule(mod, 0, 6).getResult())
			,mod -> ensureModule(mod, 0, 6).setResult(mod.getAmount(Mod303Key.CT_S17R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S17R))
		// (1) Actividades en régimen simplificado. C Cuota devengada operaciones corrientes
		,CT_S117(Mod303Key.CT_S117, null, null, null, "round(CT_S11R+CT_S12R+CT_S13R+CT_S14R+CT_S15R+CT_S16R+CT_S17R)", null
			,mod -> mod.putAmount(Mod303Key.CT_S117, ensureActivity(mod, 0).getDev())
			,mod -> ensureActivity(mod, 0).setDev(mod.getAmount(Mod303Key.CT_S117))
			,null)
		// (1) Actividades en régimen simplificado. D Reducciones
		,
		CT_S118(Mod303Key.CT_S118, null, null, null, "calculateReduccion2021(0,CT_S117,CT_S1X4,CT_S1X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S118, ensureActivity(mod, 0).getRed())
			,mod -> ensureActivity(mod, 0).setRed(mod.getAmount(Mod303Key.CT_S118))
			,null)
		// (1) Actividades en régimen simplificado. Z Índice corrector actividades de
		// temporada
		,CT_S119(Mod303Key.CT_S119, null, null, null, "isLastPeriod()?0.0:calculateIndiceTemporada( CT_S1X1 )", null
			,mod -> mod.putAmount(Mod303Key.CT_S119, ensureActivity(mod, 0).getInd())
			,mod -> ensureActivity(mod, 0).setInd(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod303Key.CT_S119))
			,null)
		// (1) Actividades en régimen simplificado. E Porcentaje de ingreso a cuenta
		,CT_S120(Mod303Key.CT_S120, null, null, null
			,"calculatePorcentajeIngresoCuenta2023(0,CT_S1X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S120, ensureActivity(mod, 0).getPor())
			,mod -> ensureActivity(mod, 0).setPor(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod303Key.CT_S120))
			,null)
		// (1) Actividades en régimen simplificado. F Ingreso a cuenta ( ([C] - [D] ) x [E])
		,CT_S121(Mod303Key.CT_S121, null, null, null
			,"calculateIngresoCuenta2021(0, CT_S1X1, CT_S1X2, CT_S117, CT_S118, CT_S119, CT_S120,CT_S1X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S121, ensureActivity(mod, 0).getIng())
			,mod -> ensureActivity(mod, 0).setIng(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod303Key.CT_S121))
			,null)
		// (1) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes
		,CT_S12X(Mod303Key.CT_S12X, null, null, null, "isLastPeriod()?round(CT_S117 * 1 / 100):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S12X, ensureActivity(mod, 0).getSopx())
			,mod -> ensureActivity(mod, 0).setSopx(mod.getAmount(Mod303Key.CT_S12X))
			,null)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas
		,CT_S12Y(Mod303Key.CT_S12Y, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S12Y, ensureActivity(mod, 0).getSopy())
			,mod -> ensureActivity(mod, 0).setSopy(mod.getAmount(Mod303Key.CT_S12Y))
			,null)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas operaciones corrientes
		,CT_S122(Mod303Key.CT_S122, null, null, null, "isLastPeriod()?(CT_S12X+CT_S12Y):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S122, ensureActivity(mod, 0).getSop())
			,mod -> ensureActivity(mod, 0).setSop(mod.getAmount(Mod303Key.CT_S122))
			,null)
		// (1) Actividades en régimen simplificado. H Índice corrector de actividades de temporada
		,CT_S123(Mod303Key.CT_S123, null, null, null, "isLastPeriod()?calculateIndiceTemporada( CT_S1X1 ):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S123, ensureActivity(mod, 0).getIct())
			,mod -> ensureActivity(mod, 0).setIct(mod.getAmount(Mod303Key.CT_S123))
			,null)
		// (1) Actividades en régimen simplificado. I RESULTADO (( [C] - [D] - [G] ) x [H])
		,CT_S124(Mod303Key.CT_S124, null, null, null, "calculateResultadoAnual( CT_S117, CT_S118, CT_S122, CT_S123)",null
			,mod -> mod.putAmount(Mod303Key.CT_S124, ensureActivity(mod, 0).getRes())
			,mod -> ensureActivity(mod, 0).setRes(mod.getAmount(Mod303Key.CT_S124))
			,null)
		// (1) Actividades en régimen simplificado. J Porcentaje cuota mínima
		,CT_S125(Mod303Key.CT_S125, null, null, null, "isLastPeriod()?CT_S125:0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S125, ensureActivity(mod, 0).getPcm())
			,mod -> ensureActivity(mod, 0).setPcm(mod.getAmount(Mod303Key.CT_S125))
			,null)
		// (1) Actividades en régimen simplificado. K Devolución cuotas soportadas otros países
		,CT_S126(Mod303Key.CT_S126, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S126, ensureActivity(mod, 0).getDvc())
			,mod -> ensureActivity(mod, 0).setDvc(mod.getAmount(Mod303Key.CT_S126))
			,null)
		// (1) Actividades en régimen simplificado. L Cuota mínima
		,CT_S127(Mod303Key.CT_S127, null, null, null, "calculateCuotaMinima(CT_S117, CT_S118, CT_S125, CT_S126,CT_S123)",null
			,mod -> mod.putAmount(Mod303Key.CT_S127, ensureActivity(mod, 0).getCmn())
			,mod -> ensureActivity(mod, 0).setCmn(mod.getAmount(Mod303Key.CT_S127))
			,null)
		// (1) Actividades en régimen simplificado. M Cuota anual derivada régimen simplificado
		,CT_S128(Mod303Key.CT_S128, null, null, null, "isLastPeriod()?((CT_S127>CT_S124)?CT_S127:CT_S124):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S128, ensureActivity(mod, 0).getCad())
			,mod -> ensureActivity(mod, 0).setCad(mod.getAmount(Mod303Key.CT_S128))
			,null)
		
		// (1) Actividades en régimen simplificado. Epigrafe IAE
		,CT_S201(Mod303Key.CT_S201, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S201, ensureActivity(mod, 1).getEpigraph())
			,mod -> ensureActivity(mod, 1).setEpigraph(mod.getDescription(Mod303Key.CT_S201))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S201))
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,CT_S20D(Mod303Key.CT_S20D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S20D, ensureActivity(mod, 1).getDescription())
			,mod -> ensureActivity(mod, 1).setDescription(mod.getDescription(Mod303Key.CT_S20D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S20D))
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de ep\u00EDgrafes 691.9 y 722
		,CT_S202(Mod303Key.CT_S202, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S202, ensureActivity(mod, 1).getSpecialEpigraph())
			,mod -> ensureActivity(mod, 1).setSpecialEpigraph((int) mod.getAmount(Mod303Key.CT_S202))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S202))
		// (1) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,CT_S2X1(Mod303Key.CT_S2X1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2X1, ensureActivity(mod, 1).getTem())
			,mod -> ensureActivity(mod, 1).setTem((int) mod.getAmount(Mod303Key.CT_S2X1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2X1))
		// (1) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,CT_S2X2(Mod303Key.CT_S2X2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2X2, ensureActivity(mod, 1).getDia())
			,mod -> ensureActivity(mod, 1).setDia((int) mod.getAmount(Mod303Key.CT_S2X2))
			,(prev,cur) -> ensureActivityDays(prev,cur, Mod303Key.CT_S201, Mod303Key.CT_S2X2))
		// (1) Actividades en régimen simplificado. Número de empleados al inicio del ejercicio ( o al inicio de la actividad)
		,CT_S2X3(Mod303Key.CT_S2X3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2X3, ensureActivity(mod, 1).getEmp())
			,mod -> ensureActivity(mod, 1).setEmp((int) mod.getAmount(Mod303Key.CT_S2X3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2X3))
		// (1) Actividades en régimen simplificado. Si realiza la actividad en LORCA
		,CT_S2X4(Mod303Key.CT_S2X4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2X4, ensureActivity(mod, 1).getLor())
			,mod -> ensureActivity(mod, 1).setLor((int) mod.getAmount(Mod303Key.CT_S2X4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2X4))
		// (1) Reduccion extraordinaria por covid-19, art. 9 RD-Ley 35/2020)
		,CT_S2X5(Mod303Key.CT_S2X5, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2X5, ensureActivity(mod, 1).getCov())
			,mod -> ensureActivity(mod, 1).setCov((int) mod.getAmount(Mod303Key.CT_S2X5))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2X5))
		,CT_S21D(Mod303Key.CT_S21D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S21D, ensureModule(mod, 1, 0).getDescription())
			,mod -> ensureModule(mod, 1, 0).setDescription(mod.getDescription(Mod303Key.CT_S21D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S21D))
		,
		CT_S21I(Mod303Key.CT_S21I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S21I, ensureModule(mod, 1, 0).getValue())
			,mod -> ensureModule(mod, 1, 0).setValue(mod.getAmount(Mod303Key.CT_S21I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S21I))
		,
		CT_S21U(Mod303Key.CT_S21U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S21U, ensureModule(mod, 1, 0).getUnit())
			,mod -> ensureModule(mod, 1, 0).setUnit(mod.getDescription(Mod303Key.CT_S21U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S21U))
		,
		CT_S21F(Mod303Key.CT_S21F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S21F, ensureModule(mod, 1, 0).getFactor())
			,mod -> ensureModule(mod, 1, 0).setFactor(mod.getAmount(Mod303Key.CT_S21F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S21F))
		,
		CT_S21R(Mod303Key.CT_S21R, null, null, null
			,"calculateResult(1,CT_S21I,CT_S21F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S21R, ensureModule(mod, 1, 0).getResult())
			,mod -> ensureModule(mod, 1, 0).setResult(mod.getAmount(Mod303Key.CT_S21R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S21R))
		,
		CT_S22D(Mod303Key.CT_S22D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S22D, ensureModule(mod, 1, 1).getDescription())
			,mod -> ensureModule(mod, 1, 1).setDescription(mod.getDescription(Mod303Key.CT_S22D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S22D))
		,CT_S22I(Mod303Key.CT_S22I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S22I, ensureModule(mod, 1, 1).getValue())
			,mod -> ensureModule(mod, 1, 1).setValue(mod.getAmount(Mod303Key.CT_S22I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S22I))
		,CT_S22U(Mod303Key.CT_S22U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S22U, ensureModule(mod, 1, 1).getUnit())
			,mod -> ensureModule(mod, 1, 1).setUnit(mod.getDescription(Mod303Key.CT_S22U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S22U))
		,CT_S22F(Mod303Key.CT_S22F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S22F, ensureModule(mod, 1, 1).getFactor())
			,mod -> ensureModule(mod, 1, 1).setFactor(mod.getAmount(Mod303Key.CT_S22F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S22F))
		,CT_S22R(Mod303Key.CT_S22R, null, null, null
			,"calculateResult(1,CT_S22I,CT_S22F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S22R, ensureModule(mod, 1, 1).getResult())
			,mod -> ensureModule(mod, 1, 1).setResult(mod.getAmount(Mod303Key.CT_S22R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S22R))
		,CT_S23D(Mod303Key.CT_S23D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S23D, ensureModule(mod, 1, 2).getDescription())
			,mod -> ensureModule(mod, 1, 2).setDescription(mod.getDescription(Mod303Key.CT_S23D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S23D))
		,CT_S23I(Mod303Key.CT_S23I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S23I, ensureModule(mod, 1, 2).getValue())
			,mod -> ensureModule(mod, 1, 2).setValue(mod.getAmount(Mod303Key.CT_S23I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S23I))
		,CT_S23U(Mod303Key.CT_S23U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S23U, ensureModule(mod, 1, 2).getUnit())
			,mod -> ensureModule(mod, 1, 2).setUnit(mod.getDescription(Mod303Key.CT_S23U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S23U))
		,CT_S23F(Mod303Key.CT_S23F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S23F, ensureModule(mod, 1, 2).getFactor())
			,mod -> ensureModule(mod, 1, 2).setFactor(mod.getAmount(Mod303Key.CT_S23F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S23F))
		,CT_S23R(Mod303Key.CT_S23R, null, null, null
			,"calculateResult(1,CT_S23I,CT_S23F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S23R, ensureModule(mod, 1, 2).getResult())
			,mod -> ensureModule(mod, 1, 2).setResult(mod.getAmount(Mod303Key.CT_S23R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S23R))
		,CT_S24D(Mod303Key.CT_S24D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S24D, ensureModule(mod, 1, 3).getDescription())
			,mod -> ensureModule(mod, 1, 3).setDescription(mod.getDescription(Mod303Key.CT_S24D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S24D))
		,CT_S24I(Mod303Key.CT_S24I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S24I, ensureModule(mod, 1, 3).getValue())
			,mod -> ensureModule(mod, 1, 3).setValue(mod.getAmount(Mod303Key.CT_S24I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S24I))
		,CT_S24U(Mod303Key.CT_S24U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S24U, ensureModule(mod, 1, 3).getUnit())
			,mod -> ensureModule(mod, 1, 3).setUnit(mod.getDescription(Mod303Key.CT_S24U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S24U))
		,CT_S24F(Mod303Key.CT_S24F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S24F, ensureModule(mod, 1, 3).getFactor())
			,mod -> ensureModule(mod, 1, 3).setFactor(mod.getAmount(Mod303Key.CT_S24F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S24F))
		,CT_S24R(Mod303Key.CT_S24R, null, null, null
			,"calculateResult(1,CT_S24I,CT_S24F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S24R, ensureModule(mod, 1, 3).getResult())
			,mod -> ensureModule(mod, 1, 3).setResult(mod.getAmount(Mod303Key.CT_S24R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S24R))
		,CT_S25D(Mod303Key.CT_S25D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S25D, ensureModule(mod, 1, 4).getDescription())
			,mod -> ensureModule(mod, 1, 4).setDescription(mod.getDescription(Mod303Key.CT_S25D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S25D))
		,CT_S25I(Mod303Key.CT_S25I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S25I, ensureModule(mod, 1, 4).getValue())
			,mod -> ensureModule(mod, 1, 4).setValue(mod.getAmount(Mod303Key.CT_S25I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S25I))
		,CT_S25U(Mod303Key.CT_S25U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S25U, ensureModule(mod, 1, 4).getUnit())
			,mod -> ensureModule(mod, 1, 4).setUnit(mod.getDescription(Mod303Key.CT_S25U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S25U))
		,CT_S25F(Mod303Key.CT_S25F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S25F, ensureModule(mod, 1, 4).getFactor())
			,mod -> ensureModule(mod, 1, 4).setFactor(mod.getAmount(Mod303Key.CT_S25F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S25F))
		,CT_S25R(Mod303Key.CT_S25R, null, null, null
			,"calculateResult(1,CT_S25I,CT_S25F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S25R, ensureModule(mod, 1, 4).getResult())
			,mod -> ensureModule(mod, 1, 4).setResult(mod.getAmount(Mod303Key.CT_S25R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S25R))
		,CT_S26D(Mod303Key.CT_S26D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S26D, ensureModule(mod, 1, 5).getDescription())
			,mod -> ensureModule(mod, 1, 5).setDescription(mod.getDescription(Mod303Key.CT_S26D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S26D))
		,CT_S26I(Mod303Key.CT_S26I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S26I, ensureModule(mod, 1, 5).getValue())
			,mod -> ensureModule(mod, 1, 5).setValue(mod.getAmount(Mod303Key.CT_S26I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S26I))
		,CT_S26U(Mod303Key.CT_S26U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S26U, ensureModule(mod, 1, 5).getUnit())
			,mod -> ensureModule(mod, 1, 5).setUnit(mod.getDescription(Mod303Key.CT_S26U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S26U))
		,CT_S26F(Mod303Key.CT_S26F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S26F, ensureModule(mod, 1, 5).getFactor())
			,mod -> ensureModule(mod, 1, 5).setFactor(mod.getAmount(Mod303Key.CT_S26F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S26F))
		,CT_S26R(Mod303Key.CT_S26R, null, null, null
			,"calculateResult(1,CT_S26I,CT_S26F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S26R, ensureModule(mod, 1, 5).getResult())
			,mod -> ensureModule(mod, 1, 5).setResult(mod.getAmount(Mod303Key.CT_S26R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S26R))
		,CT_S27D(Mod303Key.CT_S27D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S27D, ensureModule(mod, 1, 6).getDescription())
			,mod -> ensureModule(mod, 1, 6).setDescription(mod.getDescription(Mod303Key.CT_S27D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S27D))
		,CT_S27I(Mod303Key.CT_S27I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S27I, ensureModule(mod, 1, 6).getValue())
			,mod -> ensureModule(mod, 1, 6).setValue(mod.getAmount(Mod303Key.CT_S27I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S27I))
		,CT_S27U(Mod303Key.CT_S27U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S27U, ensureModule(mod, 1, 6).getUnit())
			,mod -> ensureModule(mod, 1, 6).setUnit(mod.getDescription(Mod303Key.CT_S27U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S27U))
		,CT_S27F(Mod303Key.CT_S27F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S27F, ensureModule(mod, 1, 6).getFactor())
			,mod -> ensureModule(mod, 1, 6).setFactor(mod.getAmount(Mod303Key.CT_S27F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S27F))
		
		
		
		,CT_S2P1(Mod303Key.CT_S2P1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2P1, ensureActivity(mod, 1).getMay19Hours())
			,mod -> ensureActivity(mod, 1).setMay19Hours(mod.getAmount(Mod303Key.CT_S2P1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2P1))
		,CT_S2P2(Mod303Key.CT_S2P2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2P2, ensureActivity(mod, 1).getMen19Hours())
			,mod -> ensureActivity(mod, 1).setMen19Hours(mod.getAmount(Mod303Key.CT_S2P2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2P2))
		,CT_S2P3(Mod303Key.CT_S2P3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2P3, ensureActivity(mod, 1).getDisHours())
			,mod -> ensureActivity(mod, 1).setDisHours(mod.getAmount(Mod303Key.CT_S2P3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2P3))
		,CT_S2P4(Mod303Key.CT_S2P4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2P4, ensureActivity(mod, 1).getYearHours())
			,mod -> ensureActivity(mod, 1).setYearHours(mod.getAmount(Mod303Key.CT_S2P4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2P4))
		,CT_S2E1(Mod303Key.CT_S2E1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2E1, ensureActivity(mod, 1).getOwnerHours())
			,mod -> ensureActivity(mod, 1).setOwnerHours(mod.getAmount(Mod303Key.CT_S2E1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2E1))
		,CT_S2E2(Mod303Key.CT_S2E2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2E2, ensureActivity(mod, 1).isOwnerDis()?1:0)
			,mod -> ensureActivity(mod, 1).setOwnerDis(mod.getAmount(Mod303Key.CT_S2E2) == 1)
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2E2))
		,CT_S2E3(Mod303Key.CT_S2E3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2E3, ensureActivity(mod, 1).getSpouseHours())
			,mod -> ensureActivity(mod, 1).setSpouseHours(mod.getAmount(Mod303Key.CT_S2E3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2E3))
		,CT_S2E4(Mod303Key.CT_S2E4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2E4, ensureActivity(mod, 1).getChildMen18Hours())
			,mod -> ensureActivity(mod, 1).setChildMen18Hours(mod.getAmount(Mod303Key.CT_S2E4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2E4))
		
		,CT_S2C1(Mod303Key.CT_S2C1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2C1, ensureDesk(mod, 1, 0).getDeskCapacity())
			,mod -> ensureDesk(mod, 1, 0).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S2C1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2C1))
		,CT_S2M1(Mod303Key.CT_S2M1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2M1, ensureDesk(mod, 1, 0).getDesks())
			,mod -> ensureDesk(mod, 1, 0).setDesks((int) mod.getAmount(Mod303Key.CT_S2M1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2M1))
		,CT_S2D1(Mod303Key.CT_S2D1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2D1, ensureDesk(mod, 1, 0).getDeskDays())
			,mod -> ensureDesk(mod, 1, 0).setDeskDays((int) mod.getAmount(Mod303Key.CT_S2D1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2D1))
		,CT_S2C2(Mod303Key.CT_S2C2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2C2, ensureDesk(mod, 1, 1).getDeskCapacity())
			,mod -> ensureDesk(mod, 1, 1).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S2C2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2C2))
		,CT_S2M2(Mod303Key.CT_S2M2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2M2, ensureDesk(mod, 1, 1).getDesks())
			,mod -> ensureDesk(mod, 1, 1).setDesks((int) mod.getAmount(Mod303Key.CT_S2M2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2M2))
		,CT_S2D2(Mod303Key.CT_S2D2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2D2, ensureDesk(mod, 1, 1).getDeskDays())
			,mod -> ensureDesk(mod, 1, 1).setDeskDays((int) mod.getAmount(Mod303Key.CT_S2D2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2D2))
		,CT_S2C3(Mod303Key.CT_S2C3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2C3, ensureDesk(mod, 1, 2).getDeskCapacity())
			,mod -> ensureDesk(mod, 1, 2).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S2C3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2C3))
		,CT_S2M3(Mod303Key.CT_S2M3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2M3, ensureDesk(mod, 1, 2).getDesks())
			,mod -> ensureDesk(mod, 1, 2).setDesks((int) mod.getAmount(Mod303Key.CT_S2M3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2M3))
		,CT_S2D3(Mod303Key.CT_S2D3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2D3, ensureDesk(mod, 1, 2).getDeskDays())
			,mod -> ensureDesk(mod, 1, 2).setDeskDays((int) mod.getAmount(Mod303Key.CT_S2D3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2D3))
		,CT_S2C4(Mod303Key.CT_S2C4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2C4, ensureDesk(mod, 1, 3).getDeskCapacity())
			,mod -> ensureDesk(mod, 1, 3).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S2C4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2C4))
		,CT_S2M4(Mod303Key.CT_S2M4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2M4, ensureDesk(mod, 1, 3).getDesks())
			,mod -> ensureDesk(mod, 1, 3).setDesks((int) mod.getAmount(Mod303Key.CT_S2M4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2M4))
		,CT_S2D4(Mod303Key.CT_S2D4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S2D4, ensureDesk(mod, 1, 3).getDeskDays())
			,mod -> ensureDesk(mod, 1, 3).setDeskDays((int) mod.getAmount(Mod303Key.CT_S2D4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S2D4))
		
		,CT_S27R(Mod303Key.CT_S27R, null, null, null
			,"calculateResult(1,CT_S27I,CT_S27F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S27R, ensureModule(mod, 1, 6).getResult())
			,mod -> ensureModule(mod, 1, 6).setResult(mod.getAmount(Mod303Key.CT_S27R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S27R))
		// (1) Actividades en régimen simplificado. C Cuota devengada operaciones corrientes
		,CT_S217(Mod303Key.CT_S217, null, null, null, "round(CT_S21R+CT_S22R+CT_S23R+CT_S24R+CT_S25R+CT_S26R+CT_S27R)", null
			,mod -> mod.putAmount(Mod303Key.CT_S217, ensureActivity(mod, 1).getDev())
			,mod -> ensureActivity(mod, 1).setDev(mod.getAmount(Mod303Key.CT_S217))
			,null)
		// (1) Actividades en régimen simplificado. D Reducciones
		,CT_S218(Mod303Key.CT_S218, null, null, null, "calculateReduccion2021(1,CT_S217,CT_S2X4,CT_S2X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S218, ensureActivity(mod, 1).getRed())
			,mod -> ensureActivity(mod, 1).setRed(mod.getAmount(Mod303Key.CT_S218))
			,null)
		// (1) Actividades en régimen simplificado. Z Índice corrector actividades de temporada
		,CT_S219(Mod303Key.CT_S219, null, null, null, "isLastPeriod()?0.0:calculateIndiceTemporada( CT_S2X1 )", null
			,mod -> mod.putAmount(Mod303Key.CT_S219, ensureActivity(mod, 1).getInd())
			,mod -> ensureActivity(mod, 1).setInd(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod303Key.CT_S219))
			,null)
		// (1) Actividades en régimen simplificado. E Porcentaje de ingreso a cuenta
		,CT_S220(Mod303Key.CT_S220, null, null, null
			,"calculatePorcentajeIngresoCuenta2023(1,CT_S2X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S220, ensureActivity(mod, 1).getPor())
			,mod -> ensureActivity(mod, 1).setPor(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod303Key.CT_S220))
			,null)
		// (1) Actividades en régimen simplificado. F Ingreso a cuenta ( ([C] - [D] ) x [E])
		,CT_S221(Mod303Key.CT_S221, null, null, null
			,"calculateIngresoCuenta2021(1, CT_S2X1, CT_S2X2, CT_S217, CT_S218, CT_S219, CT_S220,CT_S2X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S221, ensureActivity(mod, 1).getIng())
			,mod -> ensureActivity(mod, 1).setIng(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod303Key.CT_S221))
			,null)
		// (1) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes
		,CT_S22X(Mod303Key.CT_S22X, null, null, null, "isLastPeriod()?round(CT_S217 * 1 / 100):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S22X, ensureActivity(mod, 1).getSopx())
			,mod -> ensureActivity(mod, 1).setSopx(mod.getAmount(Mod303Key.CT_S22X))
			,null)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas
		,CT_S22Y(Mod303Key.CT_S22Y, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S22Y, ensureActivity(mod, 1).getSopy())
			,mod -> ensureActivity(mod, 1).setSopy(mod.getAmount(Mod303Key.CT_S22Y))
			,null)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas operaciones corrientes
		,CT_S222(Mod303Key.CT_S222, null, null, null, "isLastPeriod()?(CT_S22X+CT_S22Y):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S222, ensureActivity(mod, 1).getSop())
			,mod -> ensureActivity(mod, 1).setSop(mod.getAmount(Mod303Key.CT_S222))
			,null)
		// (1) Actividades en régimen simplificado. H Índice corrector de actividades de temporada
		,CT_S223(Mod303Key.CT_S223, null, null, null, "isLastPeriod()?calculateIndiceTemporada( CT_S2X1 ):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S223, ensureActivity(mod, 1).getIct())
			,mod -> ensureActivity(mod, 1).setIct(mod.getAmount(Mod303Key.CT_S223))
			,null)
		// (1) Actividades en régimen simplificado. I RESULTADO (( [C] - [D] - [G] ) x [H])
		,CT_S224(Mod303Key.CT_S224, null, null, null, "calculateResultadoAnual( CT_S217, CT_S218, CT_S222, CT_S223)", null
			,mod -> mod.putAmount(Mod303Key.CT_S224, ensureActivity(mod, 1).getRes())
			,mod -> ensureActivity(mod, 1).setRes(mod.getAmount(Mod303Key.CT_S224))
			,null)
		// (1) Actividades en régimen simplificado. J Porcentaje cuota mínima
		,CT_S225(Mod303Key.CT_S225, null, null, null, "isLastPeriod()?CT_S225:0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S225, ensureActivity(mod, 1).getPcm())
			,mod -> ensureActivity(mod, 1).setPcm(mod.getAmount(Mod303Key.CT_S225))
			,null)
		// (1) Actividades en régimen simplificado. K Devolución cuotas soportadas otros países
		,CT_S226(Mod303Key.CT_S226, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S226, ensureActivity(mod, 1).getDvc())
			,mod -> ensureActivity(mod, 1).setDvc(mod.getAmount(Mod303Key.CT_S226))
			,null)
		// (1) Actividades en régimen simplificado. L Cuota mínima
		,CT_S227(Mod303Key.CT_S227, null, null, null, "calculateCuotaMinima(CT_S217, CT_S218, CT_S225, CT_S226,CT_S223)", null
			,mod -> mod.putAmount(Mod303Key.CT_S227, ensureActivity(mod, 1).getCmn())
			,mod -> ensureActivity(mod, 1).setCmn(mod.getAmount(Mod303Key.CT_S227))
			,null)
		// (1) Actividades en régimen simplificado. M Cuota anual derivada régimen simplificado
		,CT_S228(Mod303Key.CT_S228, null, null, null, "isLastPeriod()?((CT_S227>CT_S224)?CT_S227:CT_S224):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S228, ensureActivity(mod, 1).getCad())
			,mod -> ensureActivity(mod, 1).setCad(mod.getAmount(Mod303Key.CT_S228))
			,null)
		
		// (1) Actividades en régimen simplificado. Epigrafe IAE
		,CT_S301(Mod303Key.CT_S301, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S301, ensureActivity(mod, 2).getEpigraph())
			,mod -> ensureActivity(mod, 2).setEpigraph(mod.getDescription(Mod303Key.CT_S301))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S301))
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,CT_S30D(Mod303Key.CT_S30D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S30D, ensureActivity(mod, 2).getDescription())
			,mod -> ensureActivity(mod, 2).setDescription(mod.getDescription(Mod303Key.CT_S30D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S30D))
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de ep\u00EDgrafes 691.9 y 722
		,CT_S302(Mod303Key.CT_S302, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S302, ensureActivity(mod, 2).getSpecialEpigraph())
			,mod -> ensureActivity(mod, 2).setSpecialEpigraph((int) mod.getAmount(Mod303Key.CT_S302))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S302))
		// (1) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,CT_S3X1(Mod303Key.CT_S3X1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3X1, ensureActivity(mod, 2).getTem())
			,mod -> ensureActivity(mod, 2).setTem((int) mod.getAmount(Mod303Key.CT_S3X1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3X1))
		// (1) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,CT_S3X2(Mod303Key.CT_S3X2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3X2, ensureActivity(mod, 2).getDia())
			,mod -> ensureActivity(mod, 2).setDia((int) mod.getAmount(Mod303Key.CT_S3X2))
			,(prev,cur) -> ensureActivityDays(prev,cur, Mod303Key.CT_S301, Mod303Key.CT_S3X2))
		// (1) Actividades en régimen simplificado. Número de empleados al inicio del ejercicio ( o al inicio de la actividad)
		,CT_S3X3(Mod303Key.CT_S3X3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3X3, ensureActivity(mod, 2).getEmp())
			,mod -> ensureActivity(mod, 2).setEmp((int) mod.getAmount(Mod303Key.CT_S3X3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3X3))
		// (1) Actividades en régimen simplificado. Si realiza la actividad en LORCA
		,CT_S3X4(Mod303Key.CT_S3X4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3X4, ensureActivity(mod, 2).getLor())
			,mod -> ensureActivity(mod, 2).setLor((int) mod.getAmount(Mod303Key.CT_S3X4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3X3))
		// (1) Reduccion extraordinaria por covid-19, art. 9 RD-Ley 35/2020)
		,CT_S3X5(Mod303Key.CT_S3X5, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3X5, ensureActivity(mod, 2).getCov())
			,mod -> ensureActivity(mod, 2).setCov((int) mod.getAmount(Mod303Key.CT_S3X5))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3X3))
		,CT_S31D(Mod303Key.CT_S31D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S31D, ensureModule(mod, 2, 0).getDescription())
			,mod -> ensureModule(mod, 2, 0).setDescription(mod.getDescription(Mod303Key.CT_S31D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S31D))
		,CT_S31I(Mod303Key.CT_S31I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S31I, ensureModule(mod, 2, 0).getValue())
			,mod -> ensureModule(mod, 2, 0).setValue(mod.getAmount(Mod303Key.CT_S31I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S31I))
		,CT_S31U(Mod303Key.CT_S31U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S31U, ensureModule(mod, 2, 0).getUnit())
			,mod -> ensureModule(mod, 2, 0).setUnit(mod.getDescription(Mod303Key.CT_S31U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S31U))
		,CT_S31F(Mod303Key.CT_S31F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S31F, ensureModule(mod, 2, 0).getFactor())
			,mod -> ensureModule(mod, 2, 0).setFactor(mod.getAmount(Mod303Key.CT_S31F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S31F))
		,CT_S31R(Mod303Key.CT_S31R, null, null, null
			,"calculateResult(2,CT_S31I,CT_S31F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S31R, ensureModule(mod, 2, 0).getResult())
			,mod -> ensureModule(mod, 2, 0).setResult(mod.getAmount(Mod303Key.CT_S31R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S31R))
		,CT_S32D(Mod303Key.CT_S32D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S32D, ensureModule(mod, 2, 1).getDescription())
			,mod -> ensureModule(mod, 2, 1).setDescription(mod.getDescription(Mod303Key.CT_S32D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S32D))
		,CT_S32I(Mod303Key.CT_S32I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S32I, ensureModule(mod, 2, 1).getValue())
			,mod -> ensureModule(mod, 2, 1).setValue(mod.getAmount(Mod303Key.CT_S32I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S32I))
		,CT_S32U(Mod303Key.CT_S32U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S32U, ensureModule(mod, 2, 1).getUnit())
			,mod -> ensureModule(mod, 2, 1).setUnit(mod.getDescription(Mod303Key.CT_S32U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S32U))
		,CT_S32F(Mod303Key.CT_S32F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S32F, ensureModule(mod, 2, 1).getFactor())
			,mod -> ensureModule(mod, 2, 1).setFactor(mod.getAmount(Mod303Key.CT_S32F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S32F))
		,CT_S32R(Mod303Key.CT_S32R, null, null, null
			,"calculateResult(2,CT_S32I,CT_S32F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S32R, ensureModule(mod, 2, 1).getResult())
			,mod -> ensureModule(mod, 2, 1).setResult(mod.getAmount(Mod303Key.CT_S32R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S32R))
		,CT_S33D(Mod303Key.CT_S33D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S33D, ensureModule(mod, 2, 2).getDescription())
			,mod -> ensureModule(mod, 2, 2).setDescription(mod.getDescription(Mod303Key.CT_S33D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S33D))
		,CT_S33I(Mod303Key.CT_S33I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S33I, ensureModule(mod, 2, 2).getValue())
			,mod -> ensureModule(mod, 2, 2).setValue(mod.getAmount(Mod303Key.CT_S33I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S33I))
		,CT_S33U(Mod303Key.CT_S33U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S33U, ensureModule(mod, 2, 2).getUnit())
			,mod -> ensureModule(mod, 2, 2).setUnit(mod.getDescription(Mod303Key.CT_S33U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S33U))
		,CT_S33F(Mod303Key.CT_S33F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S33F, ensureModule(mod, 2, 2).getFactor())
			,mod -> ensureModule(mod, 2, 2).setFactor(mod.getAmount(Mod303Key.CT_S33F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S33F))
		,CT_S33R(Mod303Key.CT_S33R, null, null, null
			,"calculateResult(2,CT_S33I,CT_S33F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S33R, ensureModule(mod, 2, 2).getResult())
			,mod -> ensureModule(mod, 2, 2).setResult(mod.getAmount(Mod303Key.CT_S33R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S33R))
		,
		CT_S34D(Mod303Key.CT_S34D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S34D, ensureModule(mod, 2, 3).getDescription())
			,mod -> ensureModule(mod, 2, 3).setDescription(mod.getDescription(Mod303Key.CT_S34D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S34D))
		,
		CT_S34I(Mod303Key.CT_S34I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S34I, ensureModule(mod, 2, 3).getValue())
			,mod -> ensureModule(mod, 2, 3).setValue(mod.getAmount(Mod303Key.CT_S34I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S34I))
		,CT_S34U(Mod303Key.CT_S34U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S34U, ensureModule(mod, 2, 3).getUnit())
			,mod -> ensureModule(mod, 2, 3).setUnit(mod.getDescription(Mod303Key.CT_S34U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S34U))
		,CT_S34F(Mod303Key.CT_S34F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S34F, ensureModule(mod, 2, 3).getFactor())
			,mod -> ensureModule(mod, 2, 3).setFactor(mod.getAmount(Mod303Key.CT_S34F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S34F))
		,CT_S34R(Mod303Key.CT_S34R, null, null, null
			,"calculateResult(2,CT_S34I,CT_S34F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S34R, ensureModule(mod, 2, 3).getResult())
			,mod -> ensureModule(mod, 2, 3).setResult(mod.getAmount(Mod303Key.CT_S34R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S34R))
		,CT_S35D(Mod303Key.CT_S35D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S35D, ensureModule(mod, 2, 4).getDescription())
			,mod -> ensureModule(mod, 2, 4).setDescription(mod.getDescription(Mod303Key.CT_S35D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S35D))
		,CT_S35I(Mod303Key.CT_S35I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S35I, ensureModule(mod, 2, 4).getValue())
			,mod -> ensureModule(mod, 2, 4).setValue(mod.getAmount(Mod303Key.CT_S35I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S35I))
		,CT_S35U(Mod303Key.CT_S35U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S35U, ensureModule(mod, 2, 4).getUnit())
			,mod -> ensureModule(mod, 2, 4).setUnit(mod.getDescription(Mod303Key.CT_S35U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S35U))
		,CT_S35F(Mod303Key.CT_S35F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S35F, ensureModule(mod, 2, 4).getFactor())
			,mod -> ensureModule(mod, 2, 4).setFactor(mod.getAmount(Mod303Key.CT_S35F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S35F))
		,CT_S35R(Mod303Key.CT_S35R, null, null, null
			,"calculateResult(2,CT_S35I,CT_S35F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S35R, ensureModule(mod, 2, 4).getResult())
			,mod -> ensureModule(mod, 2, 4).setResult(mod.getAmount(Mod303Key.CT_S35R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S35R))
		,CT_S36D(Mod303Key.CT_S36D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S36D, ensureModule(mod, 2, 5).getDescription())
			,mod -> ensureModule(mod, 2, 5).setDescription(mod.getDescription(Mod303Key.CT_S36D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S36D))
		,CT_S36I(Mod303Key.CT_S36I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S36I, ensureModule(mod, 2, 5).getValue())
			,mod -> ensureModule(mod, 2, 5).setValue(mod.getAmount(Mod303Key.CT_S36I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S36I))
		,CT_S36U(Mod303Key.CT_S36U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S36U, ensureModule(mod, 2, 5).getUnit())
			,mod -> ensureModule(mod, 2, 5).setUnit(mod.getDescription(Mod303Key.CT_S36U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S36U))
		,CT_S36F(Mod303Key.CT_S36F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S36F, ensureModule(mod, 2, 5).getFactor())
			,mod -> ensureModule(mod, 2, 5).setFactor(mod.getAmount(Mod303Key.CT_S36F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S36F))
		,CT_S36R(Mod303Key.CT_S36R, null, null, null
			,"calculateResult(2,CT_S36I,CT_S36F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S36R, ensureModule(mod, 2, 5).getResult())
			,mod -> ensureModule(mod, 2, 5).setResult(mod.getAmount(Mod303Key.CT_S36R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S36R))
		,CT_S37D(Mod303Key.CT_S37D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S37D, ensureModule(mod, 2, 6).getDescription())
			,mod -> ensureModule(mod, 2, 6).setDescription(mod.getDescription(Mod303Key.CT_S37D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S37D))
		,CT_S37I(Mod303Key.CT_S37I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S37I, ensureModule(mod, 2, 6).getValue())
			,mod -> ensureModule(mod, 2, 6).setValue(mod.getAmount(Mod303Key.CT_S37I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S37I))
		,CT_S37U(Mod303Key.CT_S37U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S37U, ensureModule(mod, 2, 6).getUnit())
			,mod -> ensureModule(mod, 2, 6).setUnit(mod.getDescription(Mod303Key.CT_S37U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S37U))
		,CT_S37F(Mod303Key.CT_S37F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S37F, ensureModule(mod, 2, 6).getFactor())
			,mod -> ensureModule(mod, 2, 6).setFactor(mod.getAmount(Mod303Key.CT_S37F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S37F))
		

		,CT_S3P1(Mod303Key.CT_S3P1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3P1, ensureActivity(mod, 2).getMay19Hours())
			,mod -> ensureActivity(mod, 2).setMay19Hours(mod.getAmount(Mod303Key.CT_S3P1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3P1))
		,CT_S3P2(Mod303Key.CT_S3P2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3P2, ensureActivity(mod, 2).getMen19Hours())
			,mod -> ensureActivity(mod, 2).setMen19Hours(mod.getAmount(Mod303Key.CT_S3P2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3P2))
		,CT_S3P3(Mod303Key.CT_S3P3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3P3, ensureActivity(mod, 2).getDisHours())
			,mod -> ensureActivity(mod, 2).setDisHours(mod.getAmount(Mod303Key.CT_S3P3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3P3))
		,CT_S3P4(Mod303Key.CT_S3P4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3P4, ensureActivity(mod, 2).getYearHours())
			,mod -> ensureActivity(mod, 2).setYearHours(mod.getAmount(Mod303Key.CT_S3P4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3P4))
		,CT_S3E1(Mod303Key.CT_S3E1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3E1, ensureActivity(mod, 2).getOwnerHours())
			,mod -> ensureActivity(mod, 2).setOwnerHours(mod.getAmount(Mod303Key.CT_S3E1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3E1))
		,CT_S3E2(Mod303Key.CT_S3E2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3E2, ensureActivity(mod, 2).isOwnerDis()?1:0)
			,mod -> ensureActivity(mod, 2).setOwnerDis(mod.getAmount(Mod303Key.CT_S3E2) == 1)
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3E2))
		,CT_S3E3(Mod303Key.CT_S3E3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3E3, ensureActivity(mod, 2).getSpouseHours())
			,mod -> ensureActivity(mod, 2).setSpouseHours(mod.getAmount(Mod303Key.CT_S3E3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3E3))
		,CT_S3E4(Mod303Key.CT_S3E4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3E4, ensureActivity(mod, 2).getChildMen18Hours())
			,mod -> ensureActivity(mod, 2).setChildMen18Hours(mod.getAmount(Mod303Key.CT_S3E4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3E4))

		,CT_S3C1(Mod303Key.CT_S3C1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3C1, ensureDesk(mod, 2, 0).getDeskCapacity())
			,mod -> ensureDesk(mod, 2, 0).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S3C1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3C1))
		,CT_S3M1(Mod303Key.CT_S3M1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3M1, ensureDesk(mod, 2, 0).getDesks())
			,mod -> ensureDesk(mod, 2, 0).setDesks((int) mod.getAmount(Mod303Key.CT_S3M1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3M1))
		,CT_S3D1(Mod303Key.CT_S3D1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3D1, ensureDesk(mod, 2, 0).getDeskDays())
			,mod -> ensureDesk(mod, 2, 0).setDeskDays((int) mod.getAmount(Mod303Key.CT_S3D1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3D1))
		,CT_S3C2(Mod303Key.CT_S3C2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3C2, ensureDesk(mod, 2, 1).getDeskCapacity())
			,mod -> ensureDesk(mod, 2, 1).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S3C2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3C2))
		,CT_S3M2(Mod303Key.CT_S3M2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3M2, ensureDesk(mod, 2, 1).getDesks())
			,mod -> ensureDesk(mod, 2, 1).setDesks((int) mod.getAmount(Mod303Key.CT_S3M2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3M2))
		,CT_S3D2(Mod303Key.CT_S3D2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3D2, ensureDesk(mod, 2, 1).getDeskDays())
			,mod -> ensureDesk(mod, 2, 1).setDeskDays((int) mod.getAmount(Mod303Key.CT_S3D2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3D2))
		,CT_S3C3(Mod303Key.CT_S3C3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3C3, ensureDesk(mod, 2, 2).getDeskCapacity())
			,mod -> ensureDesk(mod, 2, 2).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S3C3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3C3))
		,CT_S3M3(Mod303Key.CT_S3M3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3M3, ensureDesk(mod, 2, 2).getDesks())
			,mod -> ensureDesk(mod, 2, 2).setDesks((int) mod.getAmount(Mod303Key.CT_S3M3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3M3))
		,CT_S3D3(Mod303Key.CT_S3D3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3D3, ensureDesk(mod, 2, 2).getDeskDays())
			,mod -> ensureDesk(mod, 2, 2).setDeskDays((int) mod.getAmount(Mod303Key.CT_S3D3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3D3))
		,CT_S3C4(Mod303Key.CT_S3C4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3C4, ensureDesk(mod, 2, 3).getDeskCapacity())
			,mod -> ensureDesk(mod, 2, 3).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S3C4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3C4))
		,CT_S3M4(Mod303Key.CT_S3M4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3M4, ensureDesk(mod, 2, 3).getDesks())
			,mod -> ensureDesk(mod, 2, 3).setDesks((int) mod.getAmount(Mod303Key.CT_S3M4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3M4))
		,CT_S3D4(Mod303Key.CT_S3D4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S3D4, ensureDesk(mod, 2, 3).getDeskDays())
			,mod -> ensureDesk(mod, 2, 3).setDeskDays((int) mod.getAmount(Mod303Key.CT_S3D4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S3D4))
			
		
		,CT_S37R(Mod303Key.CT_S37R, null, null, null
			,"calculateResult(2,CT_S37I,CT_S37F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S37R, ensureModule(mod, 2, 6).getResult())
			,mod -> ensureModule(mod, 2, 6).setResult(mod.getAmount(Mod303Key.CT_S37R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S37R))

		// (1) Actividades en régimen simplificado. C Cuota devengada operaciones corrientes
		,CT_S317(Mod303Key.CT_S317, null, null, null, "round(CT_S31R+CT_S32R+CT_S33R+CT_S34R+CT_S35R+CT_S36R+CT_S37R)", null
			,mod -> mod.putAmount(Mod303Key.CT_S317, ensureActivity(mod, 2).getDev())
			,mod -> ensureActivity(mod, 2).setDev(mod.getAmount(Mod303Key.CT_S317))
			,null)
		// (1) Actividades en régimen simplificado. D Reducciones
		,CT_S318(Mod303Key.CT_S318, null, null, null, "calculateReduccion2021(2,CT_S317,CT_S3X4,CT_S3X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S318, ensureActivity(mod, 2).getRed())
			,mod -> ensureActivity(mod, 2).setRed(mod.getAmount(Mod303Key.CT_S318))
			,null)
		// (1) Actividades en régimen simplificado. Z Índice corrector actividades de temporada
		,CT_S319(Mod303Key.CT_S319, null, null, null, "isLastPeriod()?0.0:calculateIndiceTemporada( CT_S3X1 )", null
			,mod -> mod.putAmount(Mod303Key.CT_S319, ensureActivity(mod, 2).getInd())
			,mod -> ensureActivity(mod, 2).setInd(mod.getAmount(Mod303Key.CT_S319))
			,null)
		// (1) Actividades en régimen simplificado. E Porcentaje de ingreso a cuenta
		,CT_S320(Mod303Key.CT_S320, null, null, null
			,"calculatePorcentajeIngresoCuenta2023(2,CT_S3X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S320, ensureActivity(mod, 2).getPor())
			,mod -> ensureActivity(mod, 2).setPor(mod.getAmount(Mod303Key.CT_S320))
			,null)
		// (1) Actividades en régimen simplificado. F Ingreso a cuenta ( ([C] - [D] ) x [E])
		,CT_S321(Mod303Key.CT_S321, null, null, null
			,"calculateIngresoCuenta2021(2, CT_S3X1, CT_S3X2, CT_S317, CT_S318, CT_S319, CT_S320,CT_S3X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S321, ensureActivity(mod, 2).getIng())
			,mod -> ensureActivity(mod, 2).setIng(mod.getAmount(Mod303Key.CT_S321))
			,null)
		// (1) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes
		,CT_S32X(Mod303Key.CT_S32X, null, null, null, "isLastPeriod()?round(CT_S317 * 1 / 100):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S32X, ensureActivity(mod, 2).getSopx())
			,mod -> ensureActivity(mod, 2).setSopx(mod.getAmount(Mod303Key.CT_S32X))
			,null)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas
		,CT_S32Y(Mod303Key.CT_S32Y, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S32Y, ensureActivity(mod, 2).getSopy())
			,mod -> ensureActivity(mod, 2).setSopy(mod.getAmount(Mod303Key.CT_S32Y))
			,null)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas operaciones corrientes
		,CT_S322(Mod303Key.CT_S322, null, null, null, "isLastPeriod()?(CT_S32X+CT_S32Y):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S322, ensureActivity(mod, 2).getSop())
			,mod -> ensureActivity(mod, 2).setSop(mod.getAmount(Mod303Key.CT_S322))
			,null)
		// (1) Actividades en régimen simplificado. H Índice corrector de actividades de temporada
		,CT_S323(Mod303Key.CT_S323, null, null, null, "isLastPeriod()?calculateIndiceTemporada( CT_S3X1 ):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S323, ensureActivity(mod, 2).getIct())
			,mod -> ensureActivity(mod, 2).setIct(mod.getAmount(Mod303Key.CT_S323))
			,null)
		// (1) Actividades en régimen simplificado. I RESULTADO (( [C] - [D] - [G] ) x [H])
		,CT_S324(Mod303Key.CT_S324, null, null, null, "calculateResultadoAnual( CT_S317, CT_S318, CT_S322, CT_S323)",null
			,mod -> mod.putAmount(Mod303Key.CT_S324, ensureActivity(mod, 2).getRes())
			,mod -> ensureActivity(mod, 2).setRes(mod.getAmount(Mod303Key.CT_S324))
			,null)
		// (1) Actividades en régimen simplificado. J Porcentaje cuota mínima
		,CT_S325(Mod303Key.CT_S325, null, null, null, "isLastPeriod()?CT_S325:0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S325, ensureActivity(mod, 2).getPcm())
			,mod -> ensureActivity(mod, 2).setPcm(mod.getAmount(Mod303Key.CT_S325))
			,null)
		// (1) Actividades en régimen simplificado. K Devolución cuotas soportadas otros países
		,CT_S326(Mod303Key.CT_S326, null, null, null, null, null
				,mod -> mod.putAmount(Mod303Key.CT_S326, ensureActivity(mod, 2).getDvc())
				,mod -> ensureActivity(mod, 2).setDvc(mod.getAmount(Mod303Key.CT_S326))
				, null )
		// (1) Actividades en régimen simplificado. L Cuota mínima
		,CT_S327(Mod303Key.CT_S327, null, null, null, "calculateCuotaMinima(CT_S317, CT_S318, CT_S325, CT_S326,CT_S323)",null
			,mod -> mod.putAmount(Mod303Key.CT_S327, ensureActivity(mod, 2).getCmn())
			,mod -> ensureActivity(mod, 2).setCmn(mod.getAmount(Mod303Key.CT_S327))
			,null)
		// (1) Actividades en régimen simplificado. M Cuota anual derivada régimen simplificado
		,CT_S328(Mod303Key.CT_S328, null, null, null, "isLastPeriod()?((CT_S327>CT_S324)?CT_S327:CT_S324):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S328, ensureActivity(mod, 2).getCad())
			,mod -> ensureActivity(mod, 2).setCad(mod.getAmount(Mod303Key.CT_S328))
			,null)

		// (1) Actividades en régimen simplificado. Epigrafe IAE
		,CT_S401(Mod303Key.CT_S401, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S401, ensureActivity(mod, 3).getEpigraph())
			,mod -> ensureActivity(mod, 3).setEpigraph(mod.getDescription(Mod303Key.CT_S401))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S401))
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,CT_S40D(Mod303Key.CT_S40D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S40D, ensureActivity(mod, 3).getDescription())
			,mod -> ensureActivity(mod, 3).setDescription(mod.getDescription(Mod303Key.CT_S40D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S40D))
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de ep\u00EDgrafes 691.9 y 722
		,CT_S402(Mod303Key.CT_S402, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S402, ensureActivity(mod, 3).getSpecialEpigraph())
			,mod -> ensureActivity(mod, 3).setSpecialEpigraph((int) mod.getAmount(Mod303Key.CT_S402))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S402))
		// (1) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en
		// los que se ejerció la actividad en el año anterior
		,
		CT_S4X1(Mod303Key.CT_S4X1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4X1, ensureActivity(mod, 3).getTem())
			,mod -> ensureActivity(mod, 3).setTem((int) mod.getAmount(Mod303Key.CT_S4X1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4X1))
		// (1) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,CT_S4X2(Mod303Key.CT_S4X2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4X2, ensureActivity(mod, 3).getDia())
			,mod -> ensureActivity(mod, 3).setDia((int) mod.getAmount(Mod303Key.CT_S4X2))
			,(prev,cur) -> ensureActivityDays(prev,cur, Mod303Key.CT_S401, Mod303Key.CT_S4X2))
		// (1) Actividades en régimen simplificado. Número de empleados al inicio del ejercicio ( o al inicio de la actividad)
		,CT_S4X3(Mod303Key.CT_S4X3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4X3, ensureActivity(mod, 3).getEmp())
			,mod -> ensureActivity(mod, 3).setEmp((int) mod.getAmount(Mod303Key.CT_S4X3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4X3))
		// (1) Actividades en régimen simplificado. Si realiza la actividad en LORCA
		,CT_S4X4(Mod303Key.CT_S4X4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4X4, ensureActivity(mod, 3).getLor())
			,mod -> ensureActivity(mod, 3).setLor((int) mod.getAmount(Mod303Key.CT_S4X4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4X4))
		// (1) Reduccion extraordinaria por covid-19, art. 9 RD-Ley 35/2020)
		,CT_S4X5(Mod303Key.CT_S4X5, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4X5, ensureActivity(mod, 3).getCov())
			,mod -> ensureActivity(mod, 3).setCov((int) mod.getAmount(Mod303Key.CT_S4X5))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4X5))
		,CT_S41D(Mod303Key.CT_S41D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S41D, ensureModule(mod, 3, 0).getDescription())
			,mod -> ensureModule(mod, 3, 0).setDescription(mod.getDescription(Mod303Key.CT_S41D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S41D))
		,CT_S41I(Mod303Key.CT_S41I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S41I, ensureModule(mod, 3, 0).getValue())
			,mod -> ensureModule(mod, 3, 0).setValue(mod.getAmount(Mod303Key.CT_S41I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S41I))
		,CT_S41U(Mod303Key.CT_S41U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S41U, ensureModule(mod, 3, 0).getUnit())
			,mod -> ensureModule(mod, 3, 0).setUnit(mod.getDescription(Mod303Key.CT_S41U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S41U))
		,CT_S41F(Mod303Key.CT_S41F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S41F, ensureModule(mod, 3, 0).getFactor())
			,mod -> ensureModule(mod, 3, 0).setFactor(mod.getAmount(Mod303Key.CT_S41F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S41F))
		,CT_S41R(Mod303Key.CT_S41R, null, null, null
			,"calculateResult(3,CT_S41I,CT_S41F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S41R, ensureModule(mod, 3, 0).getResult())
			,mod -> ensureModule(mod, 3, 0).setResult(mod.getAmount(Mod303Key.CT_S41R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S41R))
		,CT_S42D(Mod303Key.CT_S42D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S42D, ensureModule(mod, 3, 1).getDescription())
			,mod -> ensureModule(mod, 3, 1).setDescription(mod.getDescription(Mod303Key.CT_S42D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S42D))
		,CT_S42I(Mod303Key.CT_S42I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S42I, ensureModule(mod, 3, 1).getValue())
			,mod -> ensureModule(mod, 3, 1).setValue(mod.getAmount(Mod303Key.CT_S42I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S42I))
		,CT_S42U(Mod303Key.CT_S42U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S42U, ensureModule(mod, 3, 1).getUnit())
			,mod -> ensureModule(mod, 3, 1).setUnit(mod.getDescription(Mod303Key.CT_S42U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S42U))
		,CT_S42F(Mod303Key.CT_S42F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S42F, ensureModule(mod, 3, 1).getFactor())
			,mod -> ensureModule(mod, 3, 1).setFactor(mod.getAmount(Mod303Key.CT_S42F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S42F))
		,CT_S42R(Mod303Key.CT_S42R, null, null, null
			,"calculateResult(3,CT_S42I,CT_S42F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S42R, ensureModule(mod, 3, 1).getResult())
			,mod -> ensureModule(mod, 3, 1).setResult(mod.getAmount(Mod303Key.CT_S42R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S42R))
		,CT_S43D(Mod303Key.CT_S43D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S43D, ensureModule(mod, 3, 2).getDescription())
			,mod -> ensureModule(mod, 3, 2).setDescription(mod.getDescription(Mod303Key.CT_S43D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S43D))
		,CT_S43I(Mod303Key.CT_S43I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S43I, ensureModule(mod, 3, 2).getValue())
			,mod -> ensureModule(mod, 3, 2).setValue(mod.getAmount(Mod303Key.CT_S43I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S43I))
		,CT_S43U(Mod303Key.CT_S43U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S43U, ensureModule(mod, 3, 2).getUnit())
			,mod -> ensureModule(mod, 3, 2).setUnit(mod.getDescription(Mod303Key.CT_S43U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S43U))
		,CT_S43F(Mod303Key.CT_S43F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S43F, ensureModule(mod, 3, 2).getFactor())
			,mod -> ensureModule(mod, 3, 2).setFactor(mod.getAmount(Mod303Key.CT_S43F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S43F))
		,CT_S43R(Mod303Key.CT_S43R, null, null, null
			,"calculateResult(3,CT_S43I,CT_S43F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S43R, ensureModule(mod, 3, 2).getResult())
			,mod -> ensureModule(mod, 3, 2).setResult(mod.getAmount(Mod303Key.CT_S43R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S43R))
		,CT_S44D(Mod303Key.CT_S44D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S44D, ensureModule(mod, 3, 3).getDescription())
			,mod -> ensureModule(mod, 3, 3).setDescription(mod.getDescription(Mod303Key.CT_S44D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S44D))
		,CT_S44I(Mod303Key.CT_S44I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S44I, ensureModule(mod, 3, 3).getValue())
			,mod -> ensureModule(mod, 3, 3).setValue(mod.getAmount(Mod303Key.CT_S44I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S44I))
		,CT_S44U(Mod303Key.CT_S44U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S44U, ensureModule(mod, 3, 3).getUnit())
			,mod -> ensureModule(mod, 3, 3).setUnit(mod.getDescription(Mod303Key.CT_S44U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S44U))
		,CT_S44F(Mod303Key.CT_S44F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S44F, ensureModule(mod, 3, 3).getFactor())
			,mod -> ensureModule(mod, 3, 3).setFactor(mod.getAmount(Mod303Key.CT_S44F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S44F))
		,CT_S44R(Mod303Key.CT_S44R, null, null, null
			,"calculateResult(3,CT_S44I,CT_S44F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S44R, ensureModule(mod, 3, 3).getResult())
			,mod -> ensureModule(mod, 3, 3).setResult(mod.getAmount(Mod303Key.CT_S44R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S44R))
		,CT_S45D(Mod303Key.CT_S45D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S45D, ensureModule(mod, 3, 4).getDescription())
			,mod -> ensureModule(mod, 3, 4).setDescription(mod.getDescription(Mod303Key.CT_S45D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S45D))
		,CT_S45I(Mod303Key.CT_S45I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S45I, ensureModule(mod, 3, 4).getValue())
			,mod -> ensureModule(mod, 3, 4).setValue(mod.getAmount(Mod303Key.CT_S45I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S45I))
		,CT_S45U(Mod303Key.CT_S45U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S45U, ensureModule(mod, 3, 4).getUnit())
			,mod -> ensureModule(mod, 3, 4).setUnit(mod.getDescription(Mod303Key.CT_S45U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S45U))
		,CT_S45F(Mod303Key.CT_S45F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S45F, ensureModule(mod, 3, 4).getFactor())
			,mod -> ensureModule(mod, 3, 4).setFactor(mod.getAmount(Mod303Key.CT_S45F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S45F))
		,CT_S45R(Mod303Key.CT_S45R, null, null, null
			,"calculateResult(3,CT_S45I,CT_S45F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S45R, ensureModule(mod, 3, 4).getResult())
			,mod -> ensureModule(mod, 3, 4).setResult(mod.getAmount(Mod303Key.CT_S45R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S45R))
		,CT_S46D(Mod303Key.CT_S46D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S46D, ensureModule(mod, 3, 5).getDescription())
			,mod -> ensureModule(mod, 3, 5).setDescription(mod.getDescription(Mod303Key.CT_S46D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S46D))
		,CT_S46I(Mod303Key.CT_S46I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S46I, ensureModule(mod, 3, 5).getValue())
			,mod -> ensureModule(mod, 3, 5).setValue(mod.getAmount(Mod303Key.CT_S46I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S46I))
		,CT_S46U(Mod303Key.CT_S46U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S46U, ensureModule(mod, 3, 5).getUnit())
			,mod -> ensureModule(mod, 3, 5).setUnit(mod.getDescription(Mod303Key.CT_S46U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S46U))
		,CT_S46F(Mod303Key.CT_S46F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S46F, ensureModule(mod, 3, 5).getFactor())
			,mod -> ensureModule(mod, 3, 5).setFactor(mod.getAmount(Mod303Key.CT_S46F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S46F))
		,CT_S46R(Mod303Key.CT_S46R, null, null, null
			,"calculateResult(3,CT_S46I,CT_S46F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S46R, ensureModule(mod, 3, 5).getResult())
			,mod -> ensureModule(mod, 3, 5).setResult(mod.getAmount(Mod303Key.CT_S46R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S46R))
		,CT_S47D(Mod303Key.CT_S47D, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S47D, ensureModule(mod, 3, 6).getDescription())
			,mod -> ensureModule(mod, 3, 6).setDescription(mod.getDescription(Mod303Key.CT_S47D))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S47D))
		,CT_S47I(Mod303Key.CT_S47I, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S47I, ensureModule(mod, 3, 6).getValue())
			,mod -> ensureModule(mod, 3, 6).setValue(mod.getAmount(Mod303Key.CT_S47I))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S47I))
		,CT_S47U(Mod303Key.CT_S47U, null, null, null, null, null
			,mod -> mod.putDescription(Mod303Key.CT_S47U, ensureModule(mod, 3, 6).getUnit())
			,mod -> ensureModule(mod, 3, 6).setUnit(mod.getDescription(Mod303Key.CT_S47U))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S47U))
		,CT_S47F(Mod303Key.CT_S47F, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S47F, ensureModule(mod, 3, 6).getFactor())
			,mod -> ensureModule(mod, 3, 6).setFactor(mod.getAmount(Mod303Key.CT_S47F))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S47F))

		
		,CT_S4P1(Mod303Key.CT_S4P1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4P1, ensureActivity(mod, 3).getMay19Hours())
			,mod -> ensureActivity(mod, 3).setMay19Hours(mod.getAmount(Mod303Key.CT_S4P1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4P1))
		,CT_S4P2(Mod303Key.CT_S4P2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4P2, ensureActivity(mod, 3).getMen19Hours())
			,mod -> ensureActivity(mod, 3).setMen19Hours(mod.getAmount(Mod303Key.CT_S4P2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4P2))
		,CT_S4P3(Mod303Key.CT_S4P3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4P3, ensureActivity(mod, 3).getDisHours())
			,mod -> ensureActivity(mod, 3).setDisHours(mod.getAmount(Mod303Key.CT_S4P3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4P3))
		,CT_S4P4(Mod303Key.CT_S4P4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4P4, ensureActivity(mod, 3).getYearHours())
			,mod -> ensureActivity(mod, 3).setYearHours(mod.getAmount(Mod303Key.CT_S4P4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4P4))
		,CT_S4E1(Mod303Key.CT_S4E1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4E1, ensureActivity(mod, 3).getOwnerHours())
			,mod -> ensureActivity(mod, 3).setOwnerHours(mod.getAmount(Mod303Key.CT_S4E1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4E1))
		,CT_S4E2(Mod303Key.CT_S4E2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4E2, ensureActivity(mod, 3).isOwnerDis()?1:0)
			,mod -> ensureActivity(mod, 3).setOwnerDis(mod.getAmount(Mod303Key.CT_S4E2) == 1)
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4E2))
		,CT_S4E3(Mod303Key.CT_S4E3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4E3, ensureActivity(mod, 3).getSpouseHours())
			,mod -> ensureActivity(mod, 3).setSpouseHours(mod.getAmount(Mod303Key.CT_S4E3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4E3))
		,CT_S4E4(Mod303Key.CT_S4E4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4E4, ensureActivity(mod, 3).getChildMen18Hours())
			,mod -> ensureActivity(mod, 3).setChildMen18Hours(mod.getAmount(Mod303Key.CT_S4E4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4E4))
		
		,CT_S4C1(Mod303Key.CT_S4C1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4C1, ensureDesk(mod, 3, 0).getDeskCapacity())
			,mod -> ensureDesk(mod, 3, 0).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S4C1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4C1))
		,CT_S4M1(Mod303Key.CT_S4M1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4M1, ensureDesk(mod, 3, 0).getDesks())
			,mod -> ensureDesk(mod, 3, 0).setDesks((int) mod.getAmount(Mod303Key.CT_S4M1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4M1))
		,CT_S4D1(Mod303Key.CT_S4D1, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4D1, ensureDesk(mod, 3, 0).getDeskDays())
			,mod -> ensureDesk(mod, 3, 0).setDeskDays((int) mod.getAmount(Mod303Key.CT_S4D1))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4D1))
		,CT_S4C2(Mod303Key.CT_S4C2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4C2, ensureDesk(mod, 3, 1).getDeskCapacity())
			,mod -> ensureDesk(mod, 3, 1).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S4C2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4C2))
		,CT_S4M2(Mod303Key.CT_S4M2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4M2, ensureDesk(mod, 3, 1).getDesks())
			,mod -> ensureDesk(mod, 3, 1).setDesks((int) mod.getAmount(Mod303Key.CT_S4M2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4M2))
		,CT_S4D2(Mod303Key.CT_S4D2, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4D2, ensureDesk(mod, 3, 1).getDeskDays())
			,mod -> ensureDesk(mod, 3, 1).setDeskDays((int) mod.getAmount(Mod303Key.CT_S4D2))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4D2))
		,CT_S4C3(Mod303Key.CT_S4C3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4C3, ensureDesk(mod, 3, 2).getDeskCapacity())
			,mod -> ensureDesk(mod, 3, 2).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S4C3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4C3))
		,CT_S4M3(Mod303Key.CT_S4M3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4M3, ensureDesk(mod, 3, 2).getDesks())
			,mod -> ensureDesk(mod, 3, 2).setDesks((int) mod.getAmount(Mod303Key.CT_S4M3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4M3))
		,CT_S4D3(Mod303Key.CT_S4D3, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4D3, ensureDesk(mod, 3, 2).getDeskDays())
			,mod -> ensureDesk(mod, 3, 2).setDeskDays((int) mod.getAmount(Mod303Key.CT_S4D3))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4D3))
		,CT_S4C4(Mod303Key.CT_S4C4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4C4, ensureDesk(mod, 3, 3).getDeskCapacity())
			,mod -> ensureDesk(mod, 3, 3).setDeskCapacity((int) mod.getAmount(Mod303Key.CT_S4C4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4C4))
		,CT_S4M4(Mod303Key.CT_S4M4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4M4, ensureDesk(mod, 3, 3).getDesks())
			,mod -> ensureDesk(mod, 3, 3).setDesks((int) mod.getAmount(Mod303Key.CT_S4M4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4M4))
		,CT_S4D4(Mod303Key.CT_S4D4, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S4D4, ensureDesk(mod, 3, 3).getDeskDays())
			,mod -> ensureDesk(mod, 3, 3).setDeskDays((int) mod.getAmount(Mod303Key.CT_S4D4))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S4D4))

		,CT_S47R(Mod303Key.CT_S47R, null, null, null
			,"calculateResult(3,CT_S47I,CT_S47F)"
			, null
			,mod -> mod.putAmount(Mod303Key.CT_S47R, ensureModule(mod, 3, 6).getResult())
			,mod -> ensureModule(mod, 3, 6).setResult(mod.getAmount(Mod303Key.CT_S47R))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S47R))
		// (1) Actividades en régimen simplificado. C Cuota devengada operaciones // corrientes
		,CT_S417(Mod303Key.CT_S417, null, null, null, "round(CT_S41R+CT_S42R+CT_S43R+CT_S44R+CT_S45R+CT_S46R+CT_S47R)", null
			,mod -> mod.putAmount(Mod303Key.CT_S417, ensureActivity(mod, 3).getDev())
			,mod -> ensureActivity(mod, 3).setDev(mod.getAmount(Mod303Key.CT_S417))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S417))
		// (1) Actividades en régimen simplificado. D Reducciones
		,CT_S418(Mod303Key.CT_S418, null, null, null, "calculateReduccion2021(3,CT_S417,CT_S4X4,CT_S4X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S418, ensureActivity(mod, 3).getRed())
			,mod -> ensureActivity(mod, 3).setRed(mod.getAmount(Mod303Key.CT_S418))
			,(prev,cur) -> copyKey(prev,cur, Mod303Key.CT_S418))
		// (1) Actividades en régimen simplificado. Z Índice corrector actividades de temporada
		,CT_S419(Mod303Key.CT_S419, null, null, null, "isLastPeriod()?0.0:calculateIndiceTemporada( CT_S4X1 )", null
			,mod -> mod.putAmount(Mod303Key.CT_S419, ensureActivity(mod, 3).getInd())
			,mod -> ensureActivity(mod, 3).setInd(mod.getAmount(Mod303Key.CT_S419))
			,null)
		// (1) Actividades en régimen simplificado. E Porcentaje de ingreso a cuenta
		,CT_S420(Mod303Key.CT_S420, null, null, null
			,"calculatePorcentajeIngresoCuenta2023(3,CT_S4X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S420, ensureActivity(mod, 3).getPor())
			,mod -> ensureActivity(mod, 3).setPor(mod.getAmount(Mod303Key.CT_S420))
			,null)
		// (1) Actividades en régimen simplificado. F Ingreso a cuenta ( ([C] - [D] ) x [E])
		,CT_S421(Mod303Key.CT_S421, null, null, null
			,"calculateIngresoCuenta2021(3, CT_S4X1, CT_S4X2, CT_S417, CT_S418, CT_S419, CT_S420,CT_S4X5)", null
			,mod -> mod.putAmount(Mod303Key.CT_S421, ensureActivity(mod, 3).getIng())
			,mod -> ensureActivity(mod, 3).setIng(mod.getAmount(Mod303Key.CT_S421))
			,null)
		// (1) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes
		,CT_S42X(Mod303Key.CT_S42X, null, null, null, "isLastPeriod()?round(CT_S417 * 1 / 100):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S42X, ensureActivity(mod, 3).getSopx())
			,mod -> ensureActivity(mod, 3).setSopx(mod.getAmount(Mod303Key.CT_S42X))
			,null)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas
		,CT_S42Y(Mod303Key.CT_S42Y, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S42Y, ensureActivity(mod, 3).getSopy())
			,mod -> ensureActivity(mod, 3).setSopy(mod.getAmount(Mod303Key.CT_S42Y))
			,null)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas operaciones corrientes
		,CT_S422(Mod303Key.CT_S422, null, null, null, "isLastPeriod()?(CT_S42X+CT_S42Y):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S422, ensureActivity(mod, 3).getSop())
			,mod -> ensureActivity(mod, 3).setSop(mod.getAmount(Mod303Key.CT_S422))
			,null)
		// (1) Actividades en régimen simplificado. H Índice corrector de actividades de temporada
		,CT_S423(Mod303Key.CT_S423, null, null, null, "isLastPeriod()?calculateIndiceTemporada( CT_S4X1 ):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S423, ensureActivity(mod, 3).getIct())
			,mod -> ensureActivity(mod, 3).setIct(mod.getAmount(Mod303Key.CT_S423))
			,null)
		// (1) Actividades en régimen simplificado. I RESULTADO (( [C] - [D] - [G] ) x [H])
		,CT_S424(Mod303Key.CT_S424, null, null, null, "calculateResultadoAnual( CT_S417, CT_S418, CT_S422, CT_S423)",null
			,mod -> mod.putAmount(Mod303Key.CT_S424, ensureActivity(mod, 3).getRes())
			,mod -> ensureActivity(mod, 3).setRes(mod.getAmount(Mod303Key.CT_S424))
			,null)
		// (1) Actividades en régimen simplificado. J Porcentaje cuota mínima
		,CT_S425(Mod303Key.CT_S425, null, null, null, "isLastPeriod()?CT_S425:0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S425, ensureActivity(mod, 3).getPcm())
			,mod -> ensureActivity(mod, 3).setPcm(mod.getAmount(Mod303Key.CT_S425))
			,null)
		// (1) Actividades en régimen simplificado. K Devolución cuotas soportadas otros países
		,CT_S426(Mod303Key.CT_S426, null, null, null, null, null
			,mod -> mod.putAmount(Mod303Key.CT_S426, ensureActivity(mod, 3).getDvc())
			,mod -> ensureActivity(mod, 3).setDvc(mod.getAmount(Mod303Key.CT_S426))
			,null)
		// (1) Actividades en régimen simplificado. L Cuota mínima
		,CT_S427(Mod303Key.CT_S427, null, null, null, "calculateCuotaMinima(CT_S417, CT_S418, CT_S425, CT_S426,CT_S423)",null
			,mod -> mod.putAmount(Mod303Key.CT_S427, ensureActivity(mod, 3).getCmn())
			,mod -> ensureActivity(mod, 3).setCmn(mod.getAmount(Mod303Key.CT_S427))
			,null)
		// (1) Actividades en régimen simplificado. M Cuota anual derivada régimen simplificado
		,CT_S428(Mod303Key.CT_S428, null, null, null, "isLastPeriod()?((CT_S427>CT_S424)?CT_S427:CT_S424):0.0", null
			,mod -> mod.putAmount(Mod303Key.CT_S428, ensureActivity(mod, 3).getCad())
			,mod -> ensureActivity(mod, 3).setCad(mod.getAmount(Mod303Key.CT_S428))
			,null)

		// 47 Suma de ingresos a cuenta del conjunto de actividades
		, CT_S47(Mod303Key.CT_S47, null, null, null, "CT_SA16+CT_SA26+CT_SA36+CT_SA46+CT_S121+CT_S221+CT_S321+CT_S421", null)
		// 48 Suma de cuotas derivadas RS del conjunto de actividades
		, CT_S48(Mod303Key.CT_S48, null, null, null, "isLastPeriod()?(CT_SA18+CT_SA28+CT_SA38+CT_SA48+CT_S128+CT_S228+CT_S328+CT_S428):0.0", null)
		// 49 (A+B) Suma de ingresos a cuenta realizados en el ejercicio
		, CT_S49(Mod303Key.CT_S49)
		// 50 (A+B) Resultado
		, CT_S50(Mod303Key.CT_S50, null, null, null, "isLastPeriod()?(CT_S48-CT_S49):0.0", null)
		// 51 Cuotas devengadas - Adquisiciones intracomunitarias de bienes
		,
		CT_S51(Mod303Key.CT_S51,
				(mod, vat) -> adqIntracomunitariasFilterSimp(vat, mod) && AonMathUtils.isNotZero(vat.getPercentage()),
				(ctx, mod, vat) -> add(Mod303Key.CT_S51, mod, vat.getDeductibleQuota()), null, null, null)
		// 52 Cuotas devengadas - Entregas de activos fijos
		, CT_S52(Mod303Key.CT_S52, (mod, vat) -> entregasActivosFijosFilterSimp(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_S52, mod, vat.getDeductibleQuota()), null, null, null)
		// 53 Cuotas devengadas - IVA devengado por inversi\u00F3n del sujeto pasivo
		, CT_S53(Mod303Key.CT_S53, (mod, vat) -> operacionesISPFilterSimp(vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_S53, mod, vat.getDeductibleQuota()), null, null, null)
		// 54 Cuotas devengadas - Total cuota resultante
		, CT_S54(Mod303Key.CT_S54, null, null, null,
				"isLastPeriod()?(CT_S50+CT_S51+CT_S52+CT_S53):(CT_S47+CT_S51+CT_S52+CT_S53)", null)
		// 55 IVA deducible - Adquisici\u00F3n o importaci\u00F3n de activos fijos
		,
		CT_S55(Mod303Key.CT_S55,
				(mod, vat) -> vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) && !vat.isSales()
						&& vat.isInvestment(),
				(ctx, mod, vat) -> add(Mod303Key.CT_S55, mod, vat.getDeductibleQuota()), null, null, null)
		// 56 IVA deducible - Regularizaci\u00F3n bienes de inversi\u00F3n
		, CT_S56(Mod303Key.CT_S56)
		// 57 IVA deducible - Total IVA deducible
		, CT_S57(Mod303Key.CT_S57, null, null, null, "CT_S55+CT_S56", null)
		// 58 Resultado Régimen Simplificado
		, CT_S58(Mod303Key.CT_S58, null, null, null, "CT_S54-CT_S55", null)

		// ---------------------------------------------------------------
		// ----------------------------------------- INFORMACIÓN ADICIONAL
		// ---------------------------------------------------------------

		// Entregas intracomunitarias de bienes y servicios
		, CT_C59(Mod303Key.CT_C59, (mod, vat) -> ventasIntracomunitarias( vat, mod),
			(ctx, mod, vat) -> add(Mod303Key.CT_C59, mod, vat.getBase()), null, null, null)

		// Exportaciones y operaciones asimiladas
		, CT_C60(Mod303Key.CT_C60,
			(mod, vat) -> ventasExtraComunitariasCanCeuBienes(vat, mod),
			(ctx, mod, vat) -> add(Mod303Key.CT_C60, mod, vat.getBase()), null, null, null)
		//Operaciones no sujetas por reglas de localizaci\u00F3n (excepto las incluidas en la casilla 123).
		,CT_C120(Mod303Key.CT_C120, (mod, vat) -> ventasExtraComunitariasCanCeuServicios( vat, mod),
			(ctx, mod, vat) -> add(Mod303Key.CT_C120, mod, vat.getBase()), null, null, null)
		// Operaciones sujetas con inversi\u00F3n del sujeto pasivo
		,CT_C122(Mod303Key.CT_C122, (mod, vat) -> ventasISP( vat, mod),
			(ctx, mod, vat) -> add(Mod303Key.CT_C122, mod, vat.getBase()), null, null, null)
		//Operaciones no sujetas por reglas de localizaci\u00F3n acogidas a los reg\u00EDmenes especiales de ventanilla única.
		,CT_C123(Mod303Key.CT_C123)
		//Operaciones sujetas y acogidas a los reg\u00EDmenes especiales de ventanilla única.
		,CT_C124(Mod303Key.CT_C124)
/*

		// Operaciones no sujetas o con inversión del sujeto pasivo que originan el
		// derecho a deducción
		, CT_C61(Mod303Key.CT_C61, (mod, vat) -> ventasISPExtraComunitariasCanCeuServicios( vat, mod),
				(ctx, mod, vat) -> add(Mod303Key.CT_C61, mod, vat.getBase()), null, null, null)
 */		

		// Importes de las ventas a las que habiéndoles sido aplicado el régimen
		// especial del criterio de caja hubieran
		// resultado devengadas conforme a la regla general de devengo contenida en el
		// art. 75 LIVA
		,CT_C62(Mod303Key.CT_C62 , (mod, vat) -> vat.isSales() && vat.isVatAccrualRegime()
			, null, null, null, null)
		,CT_C63(Mod303Key.CT_C63 , (mod, vat) -> vat.isSales() && vat.isVatAccrualRegime()
			, null, null, null, null)

		// Importes de las adquisiciones de bienes y servicios a las que sea de
		// aplicación o afecte el régimen especial del criterio de caja
		,CT_C74(Mod303Key.CT_C74 , (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime()
			, null, null, null, null)
		,CT_C75(Mod303Key.CT_C75 , (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime()
			, null, null, null, null)

		// Regularización cuotas art. 80.Cinco.5a LIVA
		, CT_C76(Mod303Key.CT_C76)

		// Suma de resultados
		, CT_C64(Mod303Key.CT_C64, null, null, null, "CT_C46+CT_S58+CT_C76", null)

		// % Atribuible a la Administración del Estado
		, CT_C65(Mod303Key.CT_C65, null, null, (ctx, mod) -> add(Mod303Key.CT_C65, mod, 100.0), null, null)

		// Cuota atribuible a la Administración del Estado
		, CT_C66(Mod303Key.CT_C66, null, null, null, "round(CT_C64*CT_C65/100)",
				"<li><b>Resultado:</b> @{CT_C65} % de @{CT_C64} igual <b>@{CT_C66}</b></li>")

		// IVA a la importación liquidado por la Aduana pendiente de ingreso
		, CT_C77(Mod303Key.CT_C77)

		// A partir de 2021 la casilla 67 se desglosa en 3 casillas (110, 78 y 87)
		//, CT_C67(Mod303Key.CT_C67)

		// Cuotas a compensar de periodos anteriores
		, CT_C110(Mod303Key.CT_C110, null, null, (ctx,mod) -> add( Mod303Key.CT_C110, mod, getPendingCompesateAmounts( ctx, mod ))
				,null
				,null
			)

		// Cuotas a compensar de periodos anteriores aplicadas en este periodo
		// Validación que hace la Agencia Tributaria:
		//	El resultado de la autoliquidación no podrá ser A COMPENSAR, si la casilla (78)
		//	está cumplimentada, es decir, si se hubieran aplicado a la autoliquidación que se
		//	esté presentando cuotas pendientes de compensación generadas en períodos
		//	anteriores.		
		, CT_C78(Mod303Key.CT_C78, null, null,
				(ctx, mod) -> add(Mod303Key.CT_C78, mod, mod.getAmount(Mod303Key.CT_C110)), "checkC78(CT_C78,CT_C110,CT_C66,CT_C77)", null)
		
		// Cuotas a compensar de periodos previos pendientes para periodos posteriores
		, CT_C87(Mod303Key.CT_C87, null, null, null, "CT_C110-CT_C78", null)

		// Exclusivamente para sujetos pasivos que tributan conjuntamente a la
		// Administración del Estado
		// y a las Diputaciones Forales. Resultado de la regularización anual.
		, CT_C68(Mod303Key.CT_C68)

		// Resultado
		, CT_C69(Mod303Key.CT_C69, null, null, null, "CT_C66+CT_C77-CT_C78+CT_C68", null)

		// A deducir (exclusivamente en caso de autoliquidación complementaria)
		, CT_C70(Mod303Key.CT_C70, null, null, (ctx, mod) -> {
				if (mod.isComplementary()) {
					add(Mod303Key.CT_C70, mod, Mod303DAO.getSamePeriodEffectiveModels(ctx, mod)
							.filter( fm -> fm.isToDeposit() || mod.isToPayback())	
							.mapToDouble(fm -> fm.getAmount(Mod303Key.CT_C71)).sum());
				}
			  }
			,null
			,null
		)
		,CT_C109(Mod303Key.CT_C109)
		,CT_C71(Mod303Key.CT_C71, null, null, null, "CT_C69-CT_C70+CT_C109", null)

		, CT_U1D(Mod303Key.CT_U1D), CT_U1C(Mod303Key.CT_U1C), CT_U1E(Mod303Key.CT_U1E)
		, CT_U2D(Mod303Key.CT_U2D), CT_U2C(Mod303Key.CT_U2C), CT_U2E(Mod303Key.CT_U2E)
		, CT_U3D(Mod303Key.CT_U3D), CT_U3C(Mod303Key.CT_U3C), CT_U3E(Mod303Key.CT_U3E)
		, CT_U4D(Mod303Key.CT_U4D), CT_U4C(Mod303Key.CT_U4C), CT_U4E(Mod303Key.CT_U4E)
		, CT_U5D(Mod303Key.CT_U5D), CT_U5C(Mod303Key.CT_U5C), CT_U5E(Mod303Key.CT_U5E)
		, CT_U13(Mod303Key.CT_U13)
		, CT_C89(Mod303Key.CT_C89)
		, CT_C90(Mod303Key.CT_C90)
		, CT_C91(Mod303Key.CT_C91)
		, CT_C92(Mod303Key.CT_C92)
		
		, CT_C80(Mod303Key.CT_C80)
		, CT_C81(Mod303Key.CT_C81)	 
		, CT_C93(Mod303Key.CT_C93)
		, CT_C94(Mod303Key.CT_C94)
		, CT_C83(Mod303Key.CT_C83)
		, CT_C84(Mod303Key.CT_C84)
		, CT_C125(Mod303Key.CT_C125)
		, CT_C126(Mod303Key.CT_C126)
		, CT_C127(Mod303Key.CT_C127)
		, CT_C128(Mod303Key.CT_C128)
		, CT_C86(Mod303Key.CT_C86)
		, CT_C95(Mod303Key.CT_C95)
		, CT_C96(Mod303Key.CT_C96)
		, CT_C97(Mod303Key.CT_C97)
		, CT_C98(Mod303Key.CT_C98)
		, CT_C79(Mod303Key.CT_C79)
		, CT_C99(Mod303Key.CT_C99) 
		, CT_C88(Mod303Key.CT_C88, null, null, null,"isLastPeriod()?(CT_C80+CT_C81+CT_C93+CT_C94+CT_C83+CT_C84+CT_C125+CT_C126+CT_C127+CT_C128+CT_C86+CT_C95+CT_C96+CT_C97+CT_C98-CT_C79-CT_C99):(0.0)",null)

		, CT_P1C(Mod303Key.CT_P1C), CT_P1I(Mod303Key.CT_P1I), CT_P1D(Mod303Key.CT_P1D), CT_P1T(Mod303Key.CT_P1T), CT_P1P(Mod303Key.CT_P1P)
		, CT_P2C(Mod303Key.CT_P2C), CT_P2I(Mod303Key.CT_P2I), CT_P2D(Mod303Key.CT_P2D), CT_P2T(Mod303Key.CT_P2T), CT_P2P(Mod303Key.CT_P2P)
		, CT_P3C(Mod303Key.CT_P3C), CT_P3I(Mod303Key.CT_P3I), CT_P3D(Mod303Key.CT_P3D), CT_P3T(Mod303Key.CT_P3T), CT_P3P(Mod303Key.CT_P3P)
		, CT_P4C(Mod303Key.CT_P4C), CT_P4I(Mod303Key.CT_P4I), CT_P4D(Mod303Key.CT_P4D), CT_P4T(Mod303Key.CT_P4T), CT_P4P(Mod303Key.CT_P4P)
		, CT_P5C(Mod303Key.CT_P5C), CT_P5I(Mod303Key.CT_P5I), CT_P5D(Mod303Key.CT_P5D), CT_P5T(Mod303Key.CT_P5T), CT_P5P(Mod303Key.CT_P5P);

		private Mod303Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;
		private ISimplifiedRegimeActivityPopulator populator;
		private ISimplifiedRegimeActivityFiller filler;
		private ISimplifiedRegimeCopier copier;

		private Mod303KeyDAO(Mod303Key key) {
			this(key, null, null, null, null, null);
		}

		private Mod303KeyDAO(Mod303Key key, IValueAccepter acceptValue, IValueIntializer initializer,
				IValueFirstIntializer firstInitializer, String expression, String template) {
			this(key, acceptValue, initializer, firstInitializer, expression, template, null, null, null);
		}

		private Mod303KeyDAO(Mod303Key key
			,IValueAccepter acceptValue
			,IValueIntializer initializer,
			IValueFirstIntializer firstInitializer
			,String expression, String template,
			ISimplifiedRegimeActivityPopulator populator
			,ISimplifiedRegimeActivityFiller filler,
			ISimplifiedRegimeCopier copier) {
			
			this.key = key;
			this.acceptValue = acceptValue;
			this.initializer = initializer;
			this.firstInitializer = firstInitializer;
			this.expression = expression;
			this.template = template;
			this.populator = populator;
			this.filler = filler;
			this.copier = copier;
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

		public boolean hasCopier() {
			return copier != null;
		}

		@Override
		public boolean acceptValue(Mod303 mod, VatContext vctx) {
			return acceptValue != null /* && acceptModel(mod) */ && acceptValue.accept(mod, vctx);
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

		public void copy(Mod303 previous, Mod303 current) {
			if (hasCopier()) {
				copier.copy(previous, current);
			}
		}

		public void populate(Mod303 mod) {
			if (populator != null)
				populator.populate(mod);
		}

		public void fill(Mod303 mod) {
			if (filler != null)
				filler.fill(mod);
		}

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

	private static boolean c154Filter(Mod303 mod, VatContext vat) {
		// Al cambiar el tipo de porcentaje a lo largo del ejercicio
		// Si la declaraci�n es complementaria y por diferencia hay que tener
		// en cuenta que en los ejercicio anteriores donde ahora se aplica un 7.5% 
		// se aplicaba un 5%
//		return isCommonNationalSales(vat, mod) 
//			&& ( 
//				(mod.isComplementary() && vat.getPercentage() == PERCENT75)
//			 || (mod.isComplementary() && (vat.getPercentage() == PERCENT75 || vat.getPercentage() == PERCENT5))
//		); 
		
		if (!mod.isComplementary()) {
			return isCommonNationalSales(vat, mod)
				&& (mod.getYear() > 2024
				 || (mod.getYear() == 2024 
				  && (mod.getPeriod() == Period.M10 
				  ||  mod.getPeriod() == Period.M11 
				  ||  mod.getPeriod() == Period.M12 
				  ||  mod.getPeriod() == Period.T4)
				  && (vat.getPercentage() == PERCENT75)
				  )
				 || (mod.getYear() == 2024 
				  && (mod.getPeriod() == Period.M09 
				  ||  mod.getPeriod() == Period.T3)
				  && (vat.getPercentage() == PERCENT5)
				  )
				);
		} else {
			return isCommonNationalSales(vat, mod)
				&& (mod.getYear() > 2024
				 || (mod.getYear() == 2024 
				  && (mod.getPeriod() == Period.M10 
				  ||  mod.getPeriod() == Period.M11 
				  ||  mod.getPeriod() == Period.M12 
				  ||  mod.getPeriod() == Period.T4)
				  && (vat.getPercentage() == PERCENT75 
				   || vat.getPercentage() == PERCENT5)
				  )
				 || (mod.getYear() == 2024 
				  && (mod.getPeriod() == Period.M09 
				  ||  mod.getPeriod() == Period.T3)
				  && (vat.getPercentage() == PERCENT5)
				  )
				);
		}
		
		
	}

	private static boolean c169Filter(Mod303 mod, VatContext vat) {
		//"00026", "00050" 	periodos 10 y 4T de 2024 y ejercicios posteriores
		return isCommonNationalSales(vat, mod) 
			&& vat.isSurcharge()
			&&  (mod.getYear() > 2024
		 	 || (mod.getYear() == 2024 
		 	  && (mod.getPeriod() == Period.M10 
			  ||  mod.getPeriod() == Period.M11 
			  ||  mod.getPeriod() == Period.M12 
			  ||  mod.getPeriod() == Period.T4)
		 	  && (vat.getSurchargePercent() == SURCHARGE_PERCENT_026
 			   || vat.getSurchargePercent() == SURCHARGE_PERCENT_05)
			  )
			);
	}
	
	private static boolean c17Filter(Mod303 mod, VatContext vat) {
		//	Constante "00000", "00050" o "00062"	09 y 3T de 2024
		//	Constante "00100"						A partir de 10 y 4T de 2024 y ejercicios posteriores
		if (!mod.isComplementary()) {
			return isCommonNationalSales(vat, mod) && vat.isSurcharge()
				&& (mod.getYear() > 2024
				 || (mod.getYear() == 2024 
				  && (mod.getPeriod() == Period.M10 
				  ||  mod.getPeriod() == Period.M11 
				  ||  mod.getPeriod() == Period.M12 
				  ||  mod.getPeriod() == Period.T4)
				  && (vat.getSurchargePercent() == SURCHARGE_PERCENT_1)
				  )
				 || (mod.getYear() == 2024 
				  && (mod.getPeriod() == Period.M09 
				  ||  mod.getPeriod() == Period.T3)
				  && (vat.getSurchargePercent() == SURCHARGE_PERCENT_0 
				   || vat.getSurchargePercent() == SURCHARGE_PERCENT_05 
				   || vat.getSurchargePercent() == SURCHARGE_PERCENT_062)
				  )
				);
		} else {
			return isCommonNationalSales(vat, mod) && vat.isSurcharge()
				&& (mod.getYear() > 2024
				 || (mod.getYear() == 2024 
				  && (mod.getPeriod() == Period.M10 
				  ||  mod.getPeriod() == Period.M11 
				  ||  mod.getPeriod() == Period.M12 
				  ||  mod.getPeriod() == Period.T4)
				  && (vat.getSurchargePercent() == SURCHARGE_PERCENT_0 
//					  || vat.getSurchargePercent() == SURCHARGE_PERCENT_05
					  || vat.getSurchargePercent() == SURCHARGE_PERCENT_062
					  || vat.getSurchargePercent() == SURCHARGE_PERCENT_1)
				  )
				 || (mod.getYear() == 2024 
				  && (mod.getPeriod() == Period.M09 
				  ||  mod.getPeriod() == Period.T3)
				  && (vat.getSurchargePercent() == SURCHARGE_PERCENT_0 
				   || vat.getSurchargePercent() == SURCHARGE_PERCENT_05 
				   || vat.getSurchargePercent() == SURCHARGE_PERCENT_062)
				  )
				);
		}
	}


	public static void greatherQuotaPercent(Mod303Key modkey, Mod303 mod, VatContext vat) {
		if (modkey == Mod303Key.CT_C17
		&& (mod.getYear() > 2024 
		|| (mod.getYear() == 2024
		 && (mod.getPeriod() == Period.M10 
			|| mod.getPeriod() == Period.M11
			|| mod.getPeriod() == Period.M12
			|| mod.getPeriod() == Period.T4
			)))){
			FiscalModelDetail detail = mod.ensureDetail(modkey);
			detail.setAccumulatedAmount(SURCHARGE_PERCENT_1);
			detail.setResultAmount( SURCHARGE_PERCENT_1 );	
			detail.setAmount( SURCHARGE_PERCENT_1 );
		} else {
			HashMap<String, Double> map = mod.getTempMap();
			String key = modkey.getValue() +  vat.getSurchargePercent();
			Double value = map.computeIfAbsent(key, k -> Double.valueOf(0));
			value = AonMathUtils.round(value + vat.getSurchargeQuota());
			map.put( key, value);
			Entry<String, Double> maxEntry = map.entrySet()
				.stream()
				.filter( k -> AonStringUtils.startsWith(k.getKey(), modkey.getValue()))
				.max( (e1, e2) -> e1.getValue().compareTo(e2.getValue()))
				.orElse(null);
			if (maxEntry != null) {
				String v = AonStringUtils.removeStart( maxEntry.getKey() , modkey.getValue());
				double d = AonNumberUtils.todouble(v);
				FiscalModelDetail detail = mod.ensureDetail(modkey);
				detail.setAccumulatedAmount(d);
				detail.setResultAmount( d );	
				detail.setAmount( d );
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
		return true;
	}

	// -----------------------------------------------------------------------
	// --------------------------------------------------------------- FILTROS
	// -----------------------------------------------------------------------
	private static boolean isCommonNationalSales(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime() && vat.isNational()
				&& vat.isSales() && !vat.isRectification();
	}

	private static boolean hasPercent0(Mod303 mod, VatContext vat) {
		return vat.getPercentage() == PERCENT0;
	}

	private static boolean hasPercent2(Mod303 mod, VatContext vat) {
		return vat.getPercentage() == PERCENT2;
	}

	private static boolean hasPercent4(Mod303 mod, VatContext vat) {
		return vat.getPercentage() == PERCENT4;
	}

	private static boolean hasPercent10(VatContext vat) {
		return vat.getPercentage() == PERCENT10;
	}

	private static boolean hasPercent21(VatContext vat) {
		return vat.getPercentage() == PERCENT21;
	}

	private static boolean hasSurchargePercent_175(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT_175;
	}
	
	private static boolean hasSurchargePercent_140(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT_14;
	}

	private static boolean hasSurchargePercent52(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT_52;
	}

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
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime() && vat.isRectification()
				&& (vat.isNationalSales() || adqIntracomunitariasFilterGene(vat, mod)
						|| operacionesISPFilterGene(vat, mod));
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
		boolean add = !vat.isVatSurchargeRegime() && vat.isOtherISPSales();
		if ( add && mod.getYear() == 2021) {
			add = FiscalUtils.isInPeriodRange(mod, vat.getTaxDate());
		} 
		return add;
	}

	private static Mod303ActivityFarmer ensureFarmerActivity(Mod303 mod, int idx) {
		if (idx < 0 || idx > 3)
			throw new IllegalArgumentException("0, 1, 3, o 3");

		if (mod.getActivityFarmerList() == null) {
			mod.setActivityFarmerList(new LinkedList<>());
		}
		for (int i = 0; i <= idx; i++) {
			if (idx == mod.getActivityFarmerList().size()) {
				mod.getActivityFarmerList().add(new Mod303ActivityFarmer());
			}
		}
		return mod.getActivityFarmerList().get(idx);
	}

	private static Mod303Activity ensureActivity(Mod303 mod, int idx) {
		if (idx < 0 || idx > 3)
			throw new IllegalArgumentException("0, 1, 2, o 3");

		if (mod.getActivityList() == null) {
			mod.setActivityList(new LinkedList<>());
		}
		for (int i = 0; i <= idx; i++) {
			if (idx == mod.getActivityList().size()) {
				mod.getActivityList().add(new Mod303Activity());
			}
		}
		return mod.getActivityList().get(idx);
	}

	private static Mod303ActivityModule ensureModule(Mod303 mod, int act, int idx) {
		if (idx < 0 || idx > 6) throw new IllegalArgumentException("0, 1, 2, 3, 4, 5, o 6");
		Mod303Activity a = ensureActivity(mod, act);
		if (a.getModules() == null) {
			a.setModules(new LinkedList<>());
		}
		for (int i = 0; i <= idx; i++) {
			if (idx == a.getModules().size()) {
				a.getModules().add(new Mod303ActivityModule());
			}
		}
		return a.getModules().get(idx);
	}

	private static Mod303ActivityDesk ensureDesk(Mod303 mod, int act, int idx) {
		if (idx < 0 || idx > 6) throw new IllegalArgumentException("0, 1, 2, o 3");
		Mod303Activity a = ensureActivity(mod, act);
		if (a.getDesks() == null) {
			a.setDesks(new LinkedList<>());
		}
		for (int i = 0; i <= idx; i++) {
			if (idx == a.getDesks().size()) {
				a.getDesks().add(new Mod303ActivityDesk());
			}
		}
		return a.getDesks().get(idx);
	}

	@Override
	public void specificInitialization(Mod303 mod303) {
		super.specificInitialization(mod303);
		if (mod303.getDefaultVATRegime() == VATRegime.SIMPLIFIED) {
			if (AonMathUtils.isZero(mod303.getAmount(Mod303Key.CT_C46))) {
				mod303.putAmount(Mod303Key.CT_A02, 0); // Sólo Reg. Simplificado.
			} else {
				mod303.putAmount(Mod303Key.CT_A02, 1); // Reg. Simplificado. y General
			}
		} else {
			if (AonMathUtils.isNotZero(mod303.getAmount(Mod303Key.CT_S58))) {
				mod303.putAmount(Mod303Key.CT_A02, 1); // Reg. Simplificado. y General
			} else {
				mod303.putAmount(Mod303Key.CT_A02, 2); // Sólo Reg. Simplificado. y General
			}
		}
	}
	
	@Override
	public Mod303Key getRegularizationKey() {
		return Mod303Key.CT_C44;
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
					sum(fm.getAmount(Mod303Key.CT_C87));
					sum(fm.getDeclarationResult());
					return new StringBuilder().append("<tr>")
						.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+fontLarger+border+width500) )
							.append(fm.getModelFullName())
						.append("</td>")
					.append("</tr>")
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", paddingLeft+border) )
							.append("Cuotas a compensar de periodos previos pendientes para periodos posteriores ")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
							.append(DEC2.format(fm.getAmount(Mod303Key.CT_C87)))
						.append("</td>")
					.append("</tr>")
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", paddingLeft+border) )
							.append("Resultado de la liquidaci\u00F3n " +
								AonObjectUtils.defaultIfNull(fm.getDeclarationResultType(), t -> "(" + t.getDescription() + ")"))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
							.append(DEC2.format(fm.getDeclarationResult()))
						.append("</td>")
					.append("</tr>")
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
		return DeclarationInfoUtil.getRegularizationExplain( ctx, mod303, key );
	}
	
	// -----------------------------------------------------------------------
	// ----------------------------------------------- REGIMEN SIMPLIFICADO --
	// -----------------------------------------------------------------------
	@Override
	public void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303) {
		Integer previousId = Mod303DAO.getMod303s(ctx, mod303.getDomain())
			.map( m -> m.getId())
			.findFirst().orElse(null);
		if (previousId != null) {
			Mod303 previous = Mod303DAO.get(ctx, previousId);
			if (hasSimplifiedRegime( previous )) {
				mod303.putAmount(Mod303Key.CT_A02, previous.getAmount(Mod303Key.CT_A02));
				Arrays.stream(Mod303KeyDAO.values())
					.filter( key -> key.hasCopier() )
					.forEach( key -> key.copy(previous, mod303));
			}
			ensureSimplifiedRegimeActivities(mod303);
			fillSimplifiedRegime(mod303);
		}
	}

	private IEpigraph getEpigraph(Mod303 mod303, Mod303Key key) {
		return Modules2018.Epigraph.getEpigraph(mod303.getDescription(key));
	}

	private IFarmerIVA getFarmerIVA(Mod303 mod303, Mod303Key key) {
		return Modules2018.FarmerIVA.getFarmerIVA(mod303.getDescription(key));
	}

	@Override
	public void fillSimplifiedRegime(Mod303 mod303) {
		for (Mod303KeyDAO key : Mod303KeyDAO.values()) {
			key.fill(mod303);
		}
	}

	@Override
	public void populateSimplifiedRegime(Mod303 mod303) {
		for (Mod303KeyDAO key : Mod303KeyDAO.values()) {
			key.populate(mod303);
		}
	}

	private static void copyKey(Mod303 previous,Mod303 current, Mod303Key key) {
		current.putAmount(key, previous.getAmount(key));
		current.putDescription(key, previous.getDescription(key));
	}
	
	private static boolean hasSimplifiedRegime(Mod303 mod303) {
		return AonNumberUtils.notEquals(mod303.getAmount(Mod303Key.CT_A02), 2);
	}

	private static void ensureActivityDays(Mod303 previous, Mod303 current,Mod303Key epiKey,Mod303Key daysKey) {
		if (AonStringUtils.isNotBlank(current.getDescription(Mod303Key.CT_S101))) {
			Date curStart = FiscalUtils.getPeriodStart(current);
			Date curEnd = FiscalUtils.getPeriodEnd(current);
			int curMaxDias = AonNumberUtils.toint(AonDateUtils.getDaysBetweenDates(curStart, curEnd)) + 1;
			Date prevStart = FiscalUtils.getPeriodStart(previous);
			Date prevEnd = FiscalUtils.getPeriodEnd(previous);
			int prevMaxDias = AonNumberUtils.toint(AonDateUtils.getDaysBetweenDates(prevStart, prevEnd)) + 1;
			if (AonNumberUtils.equals(prevMaxDias, previous.getAmount(daysKey))) {
				current.ensureDetail(daysKey).setAmount(curMaxDias);			
			}
		}
	}

	private void ensureSimplifiedRegimeActivities(Mod303 mod303) {
		Mod303Key[][] farmerKeys = new Mod303Key[][]{
			new Mod303Key[] {Mod303Key.CT_SA11,Mod303Key.CT_SA13,Mod303Key.CT_SA15},
			new Mod303Key[] {Mod303Key.CT_SA21,Mod303Key.CT_SA23,Mod303Key.CT_SA25},
			new Mod303Key[] {Mod303Key.CT_SA31,Mod303Key.CT_SA33,Mod303Key.CT_SA35},
			new Mod303Key[] {Mod303Key.CT_SA41,Mod303Key.CT_SA43,Mod303Key.CT_SA45},
		};
		
		IntStream.range(0, farmerKeys.length)
			.boxed()
			.map(i -> farmerKeys[i])
			.filter(farmerActivity -> AonStringUtils.isNotBlank(mod303.getDescription(farmerActivity[0])))
			.forEach(farmerActivity -> {
				IFarmerIVA farmerIVA = getFarmerIVA(mod303, farmerActivity[0]);
				if (farmerIVA != null) {
					mod303.ensureDetail(farmerActivity[1]).setAmount(farmerIVA.getIndiceRendimientoNeto() * 10000);
					if (!mod303.isLastPeriod()) {
						mod303.ensureDetail(farmerActivity[2]).setAmount(farmerIVA.getPorcentaje());
					}
				}
			});
		
		Mod303Key[][] actKeys = new Mod303Key[][]{
			new Mod303Key[] {Mod303Key.CT_S101,Mod303Key.CT_S125,Mod303Key.CT_S120},
			new Mod303Key[] {Mod303Key.CT_S201,Mod303Key.CT_S225,Mod303Key.CT_S220},
			new Mod303Key[] {Mod303Key.CT_S301,Mod303Key.CT_S325,Mod303Key.CT_S320},
			new Mod303Key[] {Mod303Key.CT_S401,Mod303Key.CT_S425,Mod303Key.CT_S420},
		};
		IntStream.range(0, actKeys.length)
			.boxed()
			.map(i -> actKeys[i])
			.filter(actActivity -> AonStringUtils.isNotBlank(mod303.getDescription(actActivity[0])))
			.forEach(actActivity -> {
				IEpigraph epi = getEpigraph(mod303, actActivity[0]);
				if (epi != null) {
					mod303.ensureDetail(actActivity[1]).setAmount(epi.getPorcMin());
					if (!mod303.isLastPeriod()) {
						mod303.ensureDetail(actActivity[2]).setAmount(epi.getVatPorc());
					}
				}
			});
	}
}