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

public class AEAT_2017_Declaration extends Mod303Declaration {
	
	protected AEAT_2017_Declaration() {
		
	}
	
	/*
		// Criterio de caja.
		AonConfiguration conf = ;
		;

		// Registro de devolucion.
		String taxRefund = AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY);
	 */
	private static final double PERCENT1 = 4.0;
	private static final double PERCENT2 = 10.0;
	private static final double PERCENT3 = 21.0;
	private static final double SURCHARGE_PERCENT1 = 0.5;
	private static final double SURCHARGE_PERCENT2 = 1.4;
	private static final double SURCHARGE_PERCENT3 = 5.2;
	
	public static boolean accept(Mod303 mod) {
		return  mod.isAEAT() && mod.getYear() >= 2017;
	}
	
	private static final Mod303Key[] PRORATE_KEYS = new Mod303Key[]{
		 Mod303Key.CT_C29
		,Mod303Key.CT_C31
		,Mod303Key.CT_C33
		,Mod303Key.CT_C35
		,Mod303Key.CT_C37
		,Mod303Key.CT_C39
		,Mod303Key.CT_C41
		,Mod303Key.CT_C42
	};

	private static enum Mod303KeyDAO implements IMod303KeyDAO {
		 CM_002(Mod303Key.CM_002,false,null,null,(ctx,mod) -> add(Mod303Key.CM_002,mod,(
				 AonStringUtils.equals(AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY),AonStringUtils.ONE))?1:0),null,null)
		,CM_003(Mod303Key.CM_003)
		,CT_A02(Mod303Key.CT_A02,false,null,null,(ctx,mod) -> add(Mod303Key.CT_A02,mod,2),null,null)
		,CT_A03(Mod303Key.CT_A03)
		,CT_A04(Mod303Key.CT_A04)
		,CT_A05(Mod303Key.CT_A05)
		,CT_A06(Mod303Key.CT_A06)
		,CT_A07(Mod303Key.CT_A07,false,null,null,(ctx,mod) -> add(Mod303Key.CT_A07,mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment()?1:0),null,null)
		,CT_A08(Mod303Key.CT_A08)	// Compras Criterio de caja. Se incializa en la casilla 075.
		,CT_A09(Mod303Key.CT_A09)
		,CT_A10(Mod303Key.CT_A10)
		,CT_A11(Mod303Key.CT_A11)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al primer tipo.
		,CT_C01(Mod303Key.CT_C01,true
			 ,(mod,vat) -> (isCommonNationalSales(vat) && hasPercent1(vat))
			 ,(ctx,mod,vat) -> add(Mod303Key.CT_C01,mod,vat.getBase())
			 ,null,null,null)
		,CT_C02(Mod303Key.CT_C02,false,null,null,(ctx,mod) -> add(Mod303Key.CT_C02,mod,PERCENT1),null,null)
		,CT_C03(Mod303Key.CT_C03,true
			,(mod,vat) -> (isCommonNationalSales(vat) && hasPercent1(vat))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C03,mod,vat.getQuota())
			,null,null,null)

		// Base imponible, porcentaje y cuota al segundo tipo.
		,CT_C04(Mod303Key.CT_C04,true
			,(mod,vat) -> (isCommonNationalSales(vat) && hasPercent2(vat))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C04,mod,vat.getBase())
			,null,null,null)
		,CT_C05(Mod303Key.CT_C05,false,null,null,(ctx,mod) -> add(Mod303Key.CT_C05,mod,PERCENT2),null,null)
		,CT_C06(Mod303Key.CT_C06,true
			,(mod,vat) -> (isCommonNationalSales(vat) && hasPercent2(vat))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C06,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al tercer tipo.
		,CT_C07(Mod303Key.CT_C07,true
			,(mod,vat) -> (isCommonNationalSales(vat) && hasPercent3(vat))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C07,mod,vat.getBase())
			,null,null,null)
		,CT_C08(Mod303Key.CT_C08,false,null,null,(ctx,mod) -> add(Mod303Key.CT_C08,mod,PERCENT3),null,null)
		,CT_C09(Mod303Key.CT_C09,true
			,(mod,vat) -> (isCommonNationalSales(vat) && hasPercent3(vat))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C09,mod,vat.getQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias de bienes y servicios. base y cuota.
		,CT_C10(Mod303Key.CT_C10,true
			,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C10,mod,vat.getBase())
			,null,null,null)
		,CT_C11(Mod303Key.CT_C11,true
				,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C11,mod,vat.getQuota())
			,null,null,null)
		
		// Otras operaciones con inversión del sujeto pasivo (excepto. adq. intracom). Base y cuota
		,CT_C12(Mod303Key.CT_C12,true
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C12,mod,vat.getBase())
			,null,null,null)
		,CT_C13(Mod303Key.CT_C13,true
				,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C13,mod,vat.getQuota())
			,null,null,null)
		
		// Modificación bases y cuotas
		,CT_C14(Mod303Key.CT_C14,true
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.CT_C14,mod,vat.getBase())
			,null,null,null)
		,CT_C15(Mod303Key.CT_C15,true
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C15,mod,vat.getQuota())
			,null,null,null)
		
		// Recargo equivalencia al primer tipo.
		,CT_C16(Mod303Key.CT_C16,true 
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C16,mod,vat.getBase())
			,null,null,null)
		,CT_C17(Mod303Key.CT_C17,false,null,null,(ctx,mod) -> add(Mod303Key.CT_C17,mod,SURCHARGE_PERCENT1),null,null)
		,CT_C18(Mod303Key.CT_C18,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C18,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al segundo tipo.
		,CT_C19(Mod303Key.CT_C19,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C19,mod,vat.getBase())
			,null,null,null)
		,CT_C20(Mod303Key.CT_C20,false,null,null,(ctx,mod) -> add(Mod303Key.CT_C20,mod,SURCHARGE_PERCENT2),null,null)
		,CT_C21(Mod303Key.CT_C21,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C21,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al tercer tipo.
		,CT_C22(Mod303Key.CT_C22,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C22,mod,vat.getBase())
			,null,null,null)
		,CT_C23(Mod303Key.CT_C23,false,null,null,(ctx,mod) -> add(Mod303Key.CT_C23,mod,SURCHARGE_PERCENT3),null,null)
		,CT_C24(Mod303Key.CT_C24,true
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C24,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Modificaciones bases y cuotas del recargo de equivalencia
		,CT_C25(Mod303Key.CT_C25,true
			,(mod,vat) -> (vat.isNationalSales() && vat.isSurcharge() && vat.isRectification())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C25,mod,vat.getBase())
			,null,null,null)
		,CT_C26(Mod303Key.CT_C26,true
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
						&& vat.isNational() && vat.isSales() 
						&& vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.CT_C26,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Total cuota devengada
		,CT_C27(Mod303Key.CT_C27,false,null,null,null,"CT_C03+CT_C06+CT_C09+CT_C11+CT_C13+CT_C15+CT_C18+CT_C21+CT_C24+CT_C26",null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------
		
		// Por cuotas soportadas en operaciones interiores corrientes
		,CT_C28(Mod303Key.CT_C28,true
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C28,mod,vat.getBase())
			,null,null,null)
		,CT_C29(Mod303Key.CT_C29,true
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C29,mod,vat.getDeductibleQuota())
			,null,null,null)

		// Por cuotas soportadas en operaciones interiores con bienes de inversión
		,CT_C30(Mod303Key.CT_C30,true
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat )
			,(ctx,mod,vat) -> add(Mod303Key.CT_C30,mod,vat.getBase())
			,null,null,null)
		,CT_C31(Mod303Key.CT_C31,true
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat )
			,(ctx,mod,vat) -> add(Mod303Key.CT_C31,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Por cuotas soportadas en las importaciones de bienes corrientes		
		,CT_C32(Mod303Key.CT_C32,true
			,(mod,vat) -> importacionesCorrientesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C32,mod,vat.getBase())
			,null,null,null)
		,CT_C33(Mod303Key.CT_C33,true
			,(mod,vat) -> importacionesCorrientesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C33,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Por cuotas soportadas en las importaciones de bienes de inversión
		,CT_C34(Mod303Key.CT_C34,true
			,(mod,vat) -> importacionesInversionFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C34,mod,vat.getBase())
			,null,null,null)
		,CT_C35(Mod303Key.CT_C35,true
			,(mod,vat) -> importacionesInversionFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C35,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Por cuotas soportadas en adquisiciones intracomunitarias de bienes y servicios corrientes
		,CT_C36(Mod303Key.CT_C36,true
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C36,mod,vat.getBase())
			,null,null,null)
		,CT_C37(Mod303Key.CT_C37,true
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C37,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Por cuotas soportadas en adquisiciones intracomunitarias de bienes de inversión
		,CT_C38(Mod303Key.CT_C38,true
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C38,mod,vat.getBase())
			,null,null,null)
		,CT_C39(Mod303Key.CT_C39,true
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C39,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Rectificación de deducciones
		,CT_C40(Mod303Key.CT_C40,true
			,(mod,vat) -> rectificaciónDeduccionesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C40,mod,vat.getBase())
			,null,null,null)
		,CT_C41(Mod303Key.CT_C41,true
			,(mod,vat) -> rectificaciónDeduccionesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C41,mod,vat.getDeductibleQuota())
			,null,null,null)

		// Compensaciones Régimen Especial A.G. y P.
		,CT_C42(Mod303Key.CT_C42,true
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C42,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Regularización inversiones 		
		,CT_C43(Mod303Key.CT_C43)
		
		// Regularización por aplicación del porcentaje definitivo de prorrata
		,CT_C44(Mod303Key.CT_C44)
		
		// Total a deducir
		,CT_C45(Mod303Key.CT_C45,false,null,null,null,"CT_C29+CT_C31+CT_C33+CT_C35+CT_C37+CT_C39+CT_C41+CT_C42+CT_C43+CT_C44",null)
		
		// Resultado Régimen general
		,CT_C46(Mod303Key.CT_C46,false,null,null,null,"CT_C27-CT_C45",null)
		

		// ---------------------------------------------------------------
		// ----------------------------------------- INFORMACIÓN ADICIONAL
		// ---------------------------------------------------------------
		
		// Entregas intracomunitarias de bienes y servicios
		,CT_C59(Mod303Key.CT_C59,true
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales() 
			,(ctx,mod,vat) -> add(Mod303Key.CT_C59,mod,vat.getBase())
			,null,null,null)

		// Exportaciones y operaciones asimiladas
		,CT_C60(Mod303Key.CT_C60,true
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C60,mod,vat.getBase())
			,null,null,null)
		
		// Operaciones no sujetas o con inversión del sujeto pasivo que originan el derecho a deducción
		,CT_C61(Mod303Key.CT_C61,true
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isOtherISPSales()  
			,(ctx,mod,vat) -> add(Mod303Key.CT_C61,mod,vat.getBase())
			,null,null,null)
		
		// Importes de las ventas a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran 
		// resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA		
		,CT_C62(Mod303Key.CT_C62,true
			,null,null, (ctx,mod) -> add( Mod303Key.CT_C62, mod, Mod303DAO.getVatAccrualPaymentOutputBase(ctx,mod)),null,null)
		
		,CT_C63(Mod303Key.CT_C63,true
			,null,null, (ctx,mod) -> {
			double quota = Mod303DAO.getVatAccrualPaymentOutputQuota(ctx,mod);
			add( Mod303Key.CT_C63, mod, quota );
//			double a = AonMathUtils.isZero(quota)? mod.getAmount(Mod303Key.CT_A07):(1.0);
//			add( Mod303Key.CT_A07, mod, a); 
			}
		,null,null)
		
		// Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el 
		// régimen especial del criterio de caja
		,CT_C74(Mod303Key.CT_C74,true,null,null, (ctx,mod) -> add( Mod303Key.CT_C74, mod, Mod303DAO.getVatAccrualPaymentInputBase(ctx,mod)),null,null)
		,CT_C75(Mod303Key.CT_C75,true,null,null, (ctx,mod) -> {
			double quota = Mod303DAO.getVatAccrualPaymentInputQuota(ctx,mod);
			add( Mod303Key.CT_C75, mod, quota);
			add( Mod303Key.CT_A08, mod, AonMathUtils.isZero(quota)?(0.0):(1.0));
			}
		,null,null)

		// Regularización cuotas art. 80.Cinco.5a LIVA
		,CT_C76(Mod303Key.CT_C76)
		
		// Suma de resultados
		,CT_C64(Mod303Key.CT_C64,false,null,null,null,"CT_C46+CT_C76",null) // TODO Sumar el resultado del regimen simplificado, si procede.
		
		// % Atribuible a la Administración del Estado
		,CT_C65(Mod303Key.CT_C65,false,null,null, (ctx,mod) -> add(Mod303Key.CT_C65,mod,100.0),null,null)
		
		// Cuota atribuible a la Administración del Estado
		,CT_C66(Mod303Key.CT_C66,false,null,null,null,"CT_C64*CT_C65/100",null)
				
		// IVA a la importación liquidado por la Aduana pendiente de ingreso
		,CT_C77(Mod303Key.CT_C77)
		
		// Cuotas a compensar de periodos anteriores
		,CT_C67(Mod303Key.CT_C67,false,null,null,
			(ctx,mod) -> {
				add( Mod303Key.CT_C67, mod, 
					Mod303DAO.getLastPeriodModels(ctx, mod)
					.filter(fm -> AonObjectUtils.equals( fm.getDescription(Mod303Key.CM_004),FiscalModelDeclarationType.COMPENSATE.getValue()))
					.mapToDouble(fm -> fm.getAmount(Mod303Key.CT_C71))
					.findFirst()
					.orElse(0.0));						
			}
			,null
			,"<li>Declaraciones del periodo anterior:<ul style=\"padding-left: 20px;\">" 
			+"@code{c71Key='"+ Mod303Key.CT_C71.getValue() +"';}"
			+"@foreach{fm : lastPeriodModels}" 
				+"<li>Resultado @{fm.getPeriod().getName()}@{fm.isComplementary()?' (C) ':'     '}:	Casilla [071] --> @{fm.getAmount(c71Key)}</li>"
			+"@end{}"
			+"</ul></li>"
			+"<li>Resultado: <b>@{CT_C70}</b></li>"
		)
		
		// Exclusivamente para sujetos pasivos que tributan conjuntamente a la Administración del Estado 
		// y a las Diputaciones Forales. Resultado de la regularización anual.
		,CT_C68(Mod303Key.CT_C68)
		
		// Resultado
		,CT_C69(Mod303Key.CT_C69,false,null,null,null,"CT_C66+CT_C77-CT_C67+CT_C68",null) 
		
		// A deducir (exclusivamente en caso de autoliquidación complementaria)
		,CT_C70(Mod303Key.CT_C70,false,null,null,
				(ctx,mod) -> {
					if (mod.isComplementary()) {
						add( Mod303Key.CT_C70, mod, Mod303DAO.getSamePeriodModels(ctx, mod).mapToDouble(fm -> fm.getAmount(Mod303Key.CT_C71)).sum());						
					}
				}
				,null
				,"<li>Declaraciones en el mismo periodo/ejercicio:<ul style=\"padding-left: 20px;\">" 
				+"@code{c71Key='"+ Mod303Key.CT_C71.getValue() +"';}"
				+"@foreach{fm : periodModels}" 
					+"<li>Resultado @{fm.getPeriod().getName()}@{fm.isComplementary()?' (C) ':'     '}:	Casilla [071] --> @{fm.getAmount(c71Key)}</li>"
				+"@end{}"
				+"</ul></li>"
				+"<li>Resultado: <b>@{CT_C70}</b></li>"
		)
		,CT_C71(Mod303Key.CT_C71,false,null,null,null,"CT_C69-CT_C70",null)
		
		,CT_C80(Mod303Key.CT_C80)
		,CT_C81(Mod303Key.CT_C81)
		,CT_C82(Mod303Key.CT_C82)
		,CT_C83(Mod303Key.CT_C83)
		,CT_C84(Mod303Key.CT_C84)
		,CT_C85(Mod303Key.CT_C85)
		,CT_C86(Mod303Key.CT_C86)
		,CT_C87(Mod303Key.CT_C87)
		,CT_C88(Mod303Key.CT_C88)
		,CT_U14(Mod303Key.CT_U14)
		
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
			this.template =  template;
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
			return  acceptValue != null /*&& acceptModel(mod)*/ &&  acceptValue.accept(mod,vctx);
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
	private static boolean adqIntracomunitariasFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses();
	}
	private static boolean operacionesISPFilter(VatContext vat) {
		return vat.isVatGeneralRegime() 
			&& !vat.isVatSurchargeRegime()
			&&  (vat.isOtherISPPurchase() 
			  || vat.isOtherISPExpenses() 
			  || vat.isExtracommunityExpenses() 
			  || vat.isCanCeuMelExpenses()
			);
	}
	private static boolean modificacionBasesYCuotasFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isRectification() 
			&& (vat.isNationalSales() || adqIntracomunitariasFilter(vat) || operacionesISPFilter(vat));		
	}

	private static boolean operacionesInterioresCorrientesFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& !vat.isInvestment() && !vat.isRectification() && !vat.isFarmerRegime()
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat))
			;
			
	}

	private static boolean operacionesInterioresInversionFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isInvestment() && !vat.isRectification() && !vat.isFarmerRegime() 
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	private static boolean importacionesCorrientesFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& !vat.isInvestment() && !vat.isRectification()  
			&& (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase());
	}
	private static boolean importacionesInversionFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isInvestment() && !vat.isRectification()  
			&& (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase());
	}
	private static boolean adqIntracomunitariasCorrientesFilter(VatContext vat) {
		return !vat.isInvestment() && !vat.isRectification() && adqIntracomunitariasFilter(vat);
	}
	private static boolean adqIntracomunitariasInversionFilter(VatContext vat) {
		return vat.isInvestment() && !vat.isRectification() && adqIntracomunitariasFilter(vat);
	}
	private static boolean rectificaciónDeduccionesFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isRectification() && (vat.isPurchase() || vat.isExpenses()); 
	}
	private static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isFarmerRegime() && !vat.isRectification() && vat.isNationalPurchase();		
	}
}
