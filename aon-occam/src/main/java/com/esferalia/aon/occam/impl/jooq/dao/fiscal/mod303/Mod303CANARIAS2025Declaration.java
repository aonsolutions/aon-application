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
	
	protected Mod303CANARIAS2025Declaration() {

	}
	
	// DEVENGADO: LOS POSIBLES PORCENTAJES DE IGIC QUE APARECEN EN EL PROGRAMA DE AYUDA SON 7 PERO EL MODELO SOLO PERMITE 6
	// PORCENTAJES POSIBLES DE IGIC: 0%, 3%, 5%, 7%, 9.5%, 15% Y 20%
	
	private static final double PERCENT0 = 0.0;
	private static final double PERCENT3 = 3.0;
	private static final double PERCENT5 = 5.0;
	private static final double PERCENT7 = 7.0;
	private static final double PERCENT95 = 9.5;
	private static final double PERCENT15 = 15.0;
	private static final double PERCENT20 = 20.0;
	
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

		,CM_002(Mod303Key.CM_002, null, null, (ctx, mod) -> add(Mod303Key.CM_002, mod,(AonStringUtils.equals(AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY),AonStringUtils.ONE)) ? 1 : 0),null, null) // Sujeto pasivo inscrito en el Registro de devolución mensual
		,CA_X01(Mod303Key.CA_X01) // Autoliquidación conjunta
		,CA_X02(Mod303Key.CA_X02, null, null,(ctx, mod) -> add(Mod303Key.CA_X02, mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment() ? 1 : 0),null, null)  // Ha optado por el régimen especial del criterio de caja
		,CA_X03(Mod303Key.CA_X03) // Compras Criterio de caja. Se inicializa en la casilla 075. 
		,CA_X04(Mod303Key.CA_X04) // Es una entidad no establecida con obligaciones periódicas                          
		,CA_X05(Mod303Key.CA_X05) // Ha sido declarado en concurso de acreedores en el presente período de liquidación 
		,CA_X06(Mod303Key.CA_X06) // Fecha en que se dictó el auto de declaración de concurso                          
		,CA_X07(Mod303Key.CA_X07) // Tipo de autoliquidación si declaración de concurso (preconcursal, postconcursal)  
		
		// ---------------------------------------------------------------
		// ----------------------------------- LIQUIDACION: IGIC DEVENGADO
		// ---------------------------------------------------------------

		// Base imponible, porcentaje y cuota (1)
		,
		CA_DB01(Mod303Key.CA_DB01, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DB01, mod, vat.getBase()), null, null, null),
		CA_DT01(Mod303Key.CA_DT01, null, null, (ctx, mod) -> add(Mod303Key.CA_DT01, mod, PERCENT0), null, null),
		CA_DC01(Mod303Key.CA_DC01, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent0(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DC01, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (2)
		,
		CA_DB02(Mod303Key.CA_DB02, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent3(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DB02, mod, vat.getBase()), null, null, null),
		CA_DT02(Mod303Key.CA_DT02, null, null, (ctx, mod) -> add(Mod303Key.CA_DT02, mod, PERCENT3), null, null),
		CA_DC02(Mod303Key.CA_DC02, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent3(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DC02, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (3)
		,
		CA_DB03(Mod303Key.CA_DB03, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent5(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DB03, mod, vat.getBase()), null, null, null),
		CA_DT03(Mod303Key.CA_DT03, null, null, (ctx, mod) -> add(Mod303Key.CA_DT03, mod, PERCENT5), null, null),
		CA_DC03(Mod303Key.CA_DC03, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent5(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DC03, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (4)
		,
		CA_DB04(Mod303Key.CA_DB04, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent7(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DB04, mod, vat.getBase()), null, null, null),
		CA_DT04(Mod303Key.CA_DT04, null, null, (ctx, mod) -> add(Mod303Key.CA_DT04, mod, PERCENT7), null, null),
		CA_DC04(Mod303Key.CA_DC04, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent7(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DC04, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (5)
		,
		CA_DB05(Mod303Key.CA_DB05, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent95(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DB05, mod, vat.getBase()), null, null, null),
		CA_DT05(Mod303Key.CA_DT05, null, null, (ctx, mod) -> add(Mod303Key.CA_DT05, mod, PERCENT95), null, null),
		CA_DC05(Mod303Key.CA_DC05, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent95(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DC05, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (6)
		,
		CA_DB06(Mod303Key.CA_DB06, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent15(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DB06, mod, vat.getBase()), null, null, null),
		CA_DT06(Mod303Key.CA_DT06, null, null, (ctx, mod) -> add(Mod303Key.CA_DT06, mod, PERCENT15), null, null),
		CA_DC06(Mod303Key.CA_DC06, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent15(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DC06, mod, vat.getQuota()), null, null, null)
		
		// Base imponible, porcentaje y cuota (7)
		,
		CA_DB07(Mod303Key.CA_DB07, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent20(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DB07, mod, vat.getBase()), null, null, null),
		CA_DT07(Mod303Key.CA_DT07, null, null, (ctx, mod) -> add(Mod303Key.CA_DT07, mod, PERCENT20), null, null),
		CA_DC07(Mod303Key.CA_DC07, (mod, vat) -> (isCommonNationalSales(vat, mod) && hasPercent20(vat)),
				(ctx, mod, vat) -> add(Mod303Key.CA_DC07, mod, vat.getQuota()), null, null, null)

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

		// Total cuota devengada
		, CA_C025(Mod303Key.CA_C025, null, null, null,"CA_DC01+CA_DC02+CA_DC03+CA_DC04+CA_DC05+CA_DC06+CA_DC07+CA_C020+CA_C022-CA_C024", null)

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

		// Exportaciones y otras operaciones exentas con derecho a deducción
		, CA_C046(Mod303Key.CA_C046,
			(mod, vat) -> ventasExtraComunitariasCanCeuBienes(vat, mod),
			(ctx, mod, vat) -> add(Mod303Key.CA_C046, mod, vat.getBase()), null, null, null)
		
		// Operaciones no sujetas o con inversión del sujeto pasivo que originan el derecho a deducción
		, CA_C047(Mod303Key.CA_C047, (mod, vat) -> ventasExtraComunitariasCanCeuServicios(vat, mod) || ventasISP(vat, mod),
			(ctx, mod, vat) -> add(Mod303Key.CA_C047, mod, vat.getBase()), null, null, null)

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

		;
		
		private Mod303Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;

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

	// -----------------------------------------------------------------------
	// --------------------------------------------------------------- FILTROS
	// -----------------------------------------------------------------------
	
	private static boolean hasPercent0(VatContext vat) {
		return vat.getPercentage() == PERCENT0;
	}
	
	private static boolean hasPercent3(VatContext vat) {
		return vat.getPercentage() == PERCENT3;
	}
	
	private static boolean hasPercent5(VatContext vat) {
		return vat.getPercentage() == PERCENT5;
	}
	
	private static boolean hasPercent7(VatContext vat) {
		return vat.getPercentage() == PERCENT7;
	}

	private static boolean hasPercent95(VatContext vat) {
		return vat.getPercentage() == PERCENT95;
	}

	private static boolean hasPercent15(VatContext vat) {
		return vat.getPercentage() == PERCENT15;
	}

	private static boolean hasPercent20(VatContext vat) {
		return vat.getPercentage() == PERCENT20;
	}
	
	private static boolean isCommonNationalSales(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime() && vat.isNational()
				&& vat.isSales() && !vat.isRectification() && !vat.isSalesOSS();
	}

//	private static boolean adqIntracomunitariasFilter(VatContext vat, Mod303 mod) {
//		return !vat.isVatSurchargeRegime() && (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses());
//	}
//
//	private static boolean adqIntracomunitariasFilterGene(VatContext vat, Mod303 mod) {
//		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && adqIntracomunitariasFilter(vat, mod);
//	}
//
//	private static boolean adqIntracomunitariasFilterNoRECT(VatContext vat, Mod303 mod) {
//		return !vat.isRectification() && adqIntracomunitariasFilterGene(vat, mod);
//	}
//
//	private static boolean adqIntracomunitariasFilterSimp(VatContext vat, Mod303 mod) {
//		return vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) && vat.isIntracommunityPurchase()
//				&& !vat.isService();
//	}
//
//	private static boolean entregasActivosFijosFilterSimp(VatContext vat, Mod303 mod) {
//		return vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) && vat.isSales() && vat.isInvestment();
//	}

	private static boolean operacionesISPFilter(VatContext vat, Mod303 mod) {
		return !vat.isVatSurchargeRegime()
				&& (vat.isOtherISPPurchase() || vat.isOtherISPExpenses() || vat.isExtracommunityExpenses()
						|| vat.isCanCeuMelExpenses() || (vat.isExtracommunityPurchase() && vat.isService())
						|| (vat.isCanCeuMelPurchase() && vat.isService()));
	}

//	private static boolean operacionesISPFilterSimp(VatContext vat, Mod303 mod) {
//		return vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) && (operacionesISPFilter(vat, mod)
//				|| vat.isIntracommunityExpenses() || (vat.isService() && vat.isIntracommunityPurchase()));
//	}

	private static boolean operacionesISPFilterGene(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && operacionesISPFilter(vat, mod);
	}

	private static boolean operacionesISPFilterNoRECT(VatContext vat, Mod303 mod) {
		return !vat.isRectification() && operacionesISPFilterGene(vat, mod);
	}

	private static boolean modificacionBasesYCuotasFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime() && vat.isRectification() && !vat.isSalesOSS()
				&& (vat.isNationalSales() || operacionesISPFilterGene(vat, mod));
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
			basicFilter = vat.hasDuaLinked() || vat.isVatImportation();
			return basicFilter; 
		}
		return false;
	}

//	private static boolean adqIntracomunitariasCorrientesFilter(VatContext vat, Mod303 mod) {
//		return !vat.isInvestment() && !vat.isRectification() && adqIntracomunitariasFilterGene(vat, mod);
//	}
//
//	private static boolean adqIntracomunitariasInversionFilter(VatContext vat, Mod303 mod) {
//		return vat.isInvestment() && !vat.isRectification() && adqIntracomunitariasFilterGene(vat, mod);
//	}

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

//	public static boolean  ventasIntracomunitarias(VatContext vat, Mod303 mod) {
//		return !vat.isVatSurchargeRegime() && vat.isIntracommunitySales();
//	}
	
	public static boolean ventasExtraComunitariasCanCeuBienes(VatContext vat, Mod303 mod) {
		return !vat.isVatSurchargeRegime() && !vat.isService() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales()) && !vat.isSalesOSS();
	}
	
	public static boolean ventasExtraComunitariasCanCeuServicios(VatContext vat, Mod303 mod) {
		boolean add = !vat.isVatSurchargeRegime() 
			&& ((vat.isService() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales()))) && !vat.isSalesOSS();
		if ( add && mod.getYear() == 2021) {
			add = FiscalUtils.isInPeriodRange(mod, vat.getTaxDate());
		} 
		return add;
	}
	
	public static boolean ventasISP(VatContext vat, Mod303 mod) {
		boolean add = !vat.isVatSurchargeRegime() && vat.isOtherISPSales();
		if ( add && mod.getYear() == 2021) {
			add = FiscalUtils.isInPeriodRange(mod, vat.getTaxDate());
		} 
		return add;
	}

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
					sum(fm.getDeclarationResult());
					return new StringBuilder().append("<tr>")
						.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+fontLarger+border+width500) )
							.append(fm.getModelFullName())
						.append("</td>")
					.append("</tr>")
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", paddingLeft+border) )
							.append("Resultado " +
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
		return Mod303InfoUtil.getRegularizationExplain( ctx, mod303, key );
	}

	@Override
	void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303) {
		// NOTHING
	}

}