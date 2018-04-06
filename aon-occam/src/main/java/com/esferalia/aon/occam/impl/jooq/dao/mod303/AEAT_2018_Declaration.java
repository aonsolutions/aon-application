package com.esferalia.aon.occam.impl.jooq.dao.mod303;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.modules.IEpigraph;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod303DAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AEAT_2018_Declaration extends Mod303Declaration {

	@FunctionalInterface
	private interface ISimplifiedRegimeActivityFiller {
		void fill(Mod303 mod);
	}
	@FunctionalInterface
	private interface ISimplifiedRegimeActivityPopulator {
		void populate(Mod303 mod);
	}

	protected AEAT_2018_Declaration() {
		
	}
	public static final double PERCENT1 = 4.0;
	public static final double PERCENT2 = 10.0;
	public static final double PERCENT3 = 21.0;
	public static final double SURCHARGE_PERCENT1 = 0.5;
	public static final double SURCHARGE_PERCENT2 = 1.4;
	public static final double SURCHARGE_PERCENT3 = 5.2;
	
	public static boolean accept(Mod303 mod) {
		return  mod.isAEAT() && mod.getYear() >= 2018;
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
		 CM_002(Mod303Key.CM_002,null,null,(ctx,mod) -> add(Mod303Key.CM_002,mod,(
				 AonStringUtils.equals(AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY),AonStringUtils.ONE))?1:0),null,null)
		,CM_003(Mod303Key.CM_003)
//		,CT_A02(Mod303Key.CT_A02,null,null,(ctx,mod) -> add(Mod303Key.CT_A02,mod,1),null,null)
//		,CT_A02(Mod303Key.CT_A02,null,null,null,null,null,null,null,true)
		,CT_A02(Mod303Key.CT_A02)
		,CT_A03(Mod303Key.CT_A03)
		,CT_A04(Mod303Key.CT_A04)
		,CT_A05(Mod303Key.CT_A05)
		,CT_A06(Mod303Key.CT_A06)
		,CT_A07(Mod303Key.CT_A07,null,null,(ctx,mod) -> add(Mod303Key.CT_A07,mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment()?1:0),null,null)
		,CT_A08(Mod303Key.CT_A08)	// Compras Criterio de caja. Se incializa en la casilla 075.
		,CT_A09(Mod303Key.CT_A09)
		,CT_A10(Mod303Key.CT_A10)
		,CT_A11(Mod303Key.CT_A11)
		,CT_A12(Mod303Key.CT_A12,null,null,(ctx,mod) -> add(Mod303Key.CT_A12,mod,2),null,null,null,null,true)
		,CT_A13(Mod303Key.CT_A13,null,null,(ctx,mod) -> add(Mod303Key.CT_A13,mod,2),null,null,null,null,true)
		
		// ---------------------------------------------------------
		// ----------------------------------------- REGIMEN GENERAL
		// ---------------------------------------------------------

		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al primer tipo.
		,CT_C01(Mod303Key.CT_C01
			 ,(mod,vat) -> (isCommonNationalSales(vat,mod) && hasPercent1(vat))
			 ,(ctx,mod,vat) -> add(Mod303Key.CT_C01,mod,vat.getBase())
			 ,null,null,null)
		,CT_C02(Mod303Key.CT_C02,null,null,(ctx,mod) -> add(Mod303Key.CT_C02,mod,PERCENT1),null,null)
		,CT_C03(Mod303Key.CT_C03
			,(mod,vat) -> (isCommonNationalSales(vat,mod) && hasPercent1(vat))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C03,mod,vat.getQuota())
			,null,null,null)

		// Base imponible, porcentaje y cuota al segundo tipo.
		,CT_C04(Mod303Key.CT_C04
			,(mod,vat) -> (isCommonNationalSales(vat,mod) && hasPercent2(vat))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C04,mod,vat.getBase())
			,null,null,null)
		,CT_C05(Mod303Key.CT_C05,null,null,(ctx,mod) -> add(Mod303Key.CT_C05,mod,PERCENT2),null,null)
		,CT_C06(Mod303Key.CT_C06
			,(mod,vat) -> (isCommonNationalSales(vat,mod) && hasPercent2(vat))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C06,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al tercer tipo.
		,CT_C07(Mod303Key.CT_C07
			,(mod,vat) -> (isCommonNationalSales(vat,mod) && hasPercent3(vat))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C07,mod,vat.getBase())
			,null,null,null)
		,CT_C08(Mod303Key.CT_C08,null,null,(ctx,mod) -> add(Mod303Key.CT_C08,mod,PERCENT3),null,null)
		,CT_C09(Mod303Key.CT_C09
			,(mod,vat) -> (isCommonNationalSales(vat,mod) && hasPercent3(vat))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C09,mod,vat.getQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias de bienes y servicios. base y cuota.
		,CT_C10(Mod303Key.CT_C10
			,(mod,vat) -> adqIntracomunitariasFilterGene(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C10,mod,vat.getBase())
			,null,null,null)
		,CT_C11(Mod303Key.CT_C11
			,(mod,vat) -> adqIntracomunitariasFilterGene(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C11,mod,vat.getQuota())
			,null,null,null)
		
		// Otras operaciones con inversión del sujeto pasivo (excepto. adq. intracom). Base y cuota
		,CT_C12(Mod303Key.CT_C12
			,(mod,vat) -> operacionesISPFilterGene(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C12,mod,vat.getBase())
			,null,null,null)
		,CT_C13(Mod303Key.CT_C13
			,(mod,vat) -> operacionesISPFilterGene(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C13,mod,vat.getQuota())
			,null,null,null)
		
		// Modificación bases y cuotas
		,CT_C14(Mod303Key.CT_C14
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat,mod) 
			,(ctx,mod,vat) -> add(Mod303Key.CT_C14,mod,vat.getBase())
			,null,null,null)
		,CT_C15(Mod303Key.CT_C15
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C15,mod,vat.getQuota())
			,null,null,null)
		
		// Recargo equivalencia al primer tipo.
		,CT_C16(Mod303Key.CT_C16 
			,(mod,vat) -> isCommonNationalSales(vat,mod) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C16,mod,vat.getBase())
			,null,null,null)
		,CT_C17(Mod303Key.CT_C17,null,null,(ctx,mod) -> add(Mod303Key.CT_C17,mod,SURCHARGE_PERCENT1),null,null)
		,CT_C18(Mod303Key.CT_C18
			,(mod,vat) -> isCommonNationalSales(vat,mod) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C18,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al segundo tipo.
		,CT_C19(Mod303Key.CT_C19
			,(mod,vat) -> isCommonNationalSales(vat,mod) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C19,mod,vat.getBase())
			,null,null,null)
		,CT_C20(Mod303Key.CT_C20,null,null,(ctx,mod) -> add(Mod303Key.CT_C20,mod,SURCHARGE_PERCENT2),null,null)
		,CT_C21(Mod303Key.CT_C21
			,(mod,vat) -> isCommonNationalSales(vat,mod) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C21,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al tercer tipo.
		,CT_C22(Mod303Key.CT_C22
			,(mod,vat) -> isCommonNationalSales(vat,mod) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C22,mod,vat.getBase())
			,null,null,null)
		,CT_C23(Mod303Key.CT_C23,null,null,(ctx,mod) -> add(Mod303Key.CT_C23,mod,SURCHARGE_PERCENT3),null,null)
		,CT_C24(Mod303Key.CT_C24
			,(mod,vat) -> isCommonNationalSales(vat,mod) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C24,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Modificaciones bases y cuotas del recargo de equivalencia
		,CT_C25(Mod303Key.CT_C25
			,(mod,vat) -> (vat.isNationalSales() && vat.isSurcharge() && vat.isRectification())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C25,mod,vat.getBase())
			,null,null,null)
		,CT_C26(Mod303Key.CT_C26
			,(mod,vat) -> vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
						&& vat.isNational() && vat.isSales() 
						&& vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.CT_C26,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Total cuota devengada
		,CT_C27(Mod303Key.CT_C27,null,null,null,"CT_C03+CT_C06+CT_C09+CT_C11+CT_C13+CT_C15+CT_C18+CT_C21+CT_C24+CT_C26",null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------
		
		// Por cuotas soportadas en operaciones interiores corrientes
		,CT_C28(Mod303Key.CT_C28
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C28,mod,vat.getBase())
			,null,null,null)
		,CT_C29(Mod303Key.CT_C29
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C29,mod,vat.getDeductibleQuota())
			,null,null,null)

		// Por cuotas soportadas en operaciones interiores con bienes de inversión
		,CT_C30(Mod303Key.CT_C30
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat ,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C30,mod,vat.getBase())
			,null,null,null)
		,CT_C31(Mod303Key.CT_C31
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat ,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C31,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Por cuotas soportadas en las importaciones de bienes corrientes		
		,CT_C32(Mod303Key.CT_C32
			,(mod,vat) -> importacionesCorrientesFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C32,mod,vat.getBase())
			,null,null,null)
		,CT_C33(Mod303Key.CT_C33
			,(mod,vat) -> importacionesCorrientesFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C33,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Por cuotas soportadas en las importaciones de bienes de inversión
		,CT_C34(Mod303Key.CT_C34
			,(mod,vat) -> importacionesInversionFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C34,mod,vat.getBase())
			,null,null,null)
		,CT_C35(Mod303Key.CT_C35
			,(mod,vat) -> importacionesInversionFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C35,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Por cuotas soportadas en adquisiciones intracomunitarias de bienes y servicios corrientes
		,CT_C36(Mod303Key.CT_C36
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C36,mod,vat.getBase())
			,null,null,null)
		,CT_C37(Mod303Key.CT_C37
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C37,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Por cuotas soportadas en adquisiciones intracomunitarias de bienes de inversión
		,CT_C38(Mod303Key.CT_C38
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C38,mod,vat.getBase())
			,null,null,null)
		,CT_C39(Mod303Key.CT_C39
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C39,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Rectificación de deducciones
		,CT_C40(Mod303Key.CT_C40
			,(mod,vat) -> rectificaciónDeduccionesFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C40,mod,vat.getBase())
			,null,null,null)
		,CT_C41(Mod303Key.CT_C41
			,(mod,vat) -> rectificaciónDeduccionesFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C41,mod,vat.getDeductibleQuota())
			,null,null,null)

		// Compensaciones Régimen Especial A.G. y P.
		,CT_C42(Mod303Key.CT_C42
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_C42,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Regularización inversiones 		
		,CT_C43(Mod303Key.CT_C43)
		
		// Regularización por aplicación del porcentaje definitivo de prorrata
		,CT_C44(Mod303Key.CT_C44)
		
		// Total a deducir
		,CT_C45(Mod303Key.CT_C45,null,null,null,"CT_C29+CT_C31+CT_C33+CT_C35+CT_C37+CT_C39+CT_C41+CT_C42+CT_C43+CT_C44",null)
		
		// Resultado Régimen general
		,CT_C46(Mod303Key.CT_C46,null,null,null,"CT_C27-CT_C45",null)

		// --------------------------------------------------------------
		// ----------------------------------------- REGIMEN SIMPLIFICADO
		// --------------------------------------------------------------
		
		// (1) Actividades agrícolas, ganaderas y forestales. Código
		,CT_SA11(Mod303Key.CT_SA11,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_SA11,ensureFarmerActivity(mod,0).getCode())
			,mod -> ensureFarmerActivity(mod,0).setCode(mod.getDescription(Mod303Key.CT_SA11))
			,true)
		// (1) Actividades agrícolas, ganaderas y forestales. Descripción
		,CT_SA1D(Mod303Key.CT_SA1D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_SA1D,ensureFarmerActivity(mod,0).getDescription())
			,mod -> ensureFarmerActivity(mod,0).setDescription(mod.getDescription(Mod303Key.CT_SA1D))
			,true)
		// (1) Actividades agrícolas, ganaderas y forestales. Volumen de ingresos
		,CT_SA12(Mod303Key.CT_SA12,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA12,ensureFarmerActivity(mod,0).getVol())
			,mod -> ensureFarmerActivity(mod,0).setVol(mod.getAmount(Mod303Key.CT_SA12))
			,false)
		// (1) Actividades agrícolas, ganaderas y forestales. Índice de cuota
		,CT_SA13(Mod303Key.CT_SA13,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA13,( ensureFarmerActivity(mod,0).getInd() * 10000) )
			,mod -> ensureFarmerActivity(mod,0).setInd(mod.getAmount(Mod303Key.CT_SA13) / 10000 )
			,true)
		// (1) Actividades agrícolas, ganaderas y forestales. Cuota devengada
		,CT_SA14(Mod303Key.CT_SA14,null,null,null,"(hasFarmerActivity(0))?round(CT_SA12*CT_SA13/10000):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA14,ensureFarmerActivity(mod,0).getCuo())
			,mod -> ensureFarmerActivity(mod,0).setCuo(mod.getAmount(Mod303Key.CT_SA14))
			,true)
		// (1) Actividades agrícolas, ganaderas y forestales. Porcentaje trimestral
		,CT_SA15(Mod303Key.CT_SA15,null,null,null,"(hasFarmerActivity(0) && !isLastPeriod())?CT_SA15:(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA15,ensureFarmerActivity(mod,0).getPor())
			,mod -> ensureFarmerActivity(mod,0).setPor(mod.getAmount(Mod303Key.CT_SA15))
			,true)
		// (1) Actividades agrícolas, ganaderas y forestales. Ingreso a cuenta [A]
		,CT_SA16(Mod303Key.CT_SA16,null,null,null,"(hasFarmerActivity(0) && !isLastPeriod())?round(CT_SA14*CT_SA15/100):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA16,ensureFarmerActivity(mod,0).getIng())
			,mod -> ensureFarmerActivity(mod,0).setIng(mod.getAmount(Mod303Key.CT_SA16))
			,true)
		// (1) Actividades agrícolas, ganaderas y forestales. Cuota soportada
		,CT_SA17(Mod303Key.CT_SA17,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA17,ensureFarmerActivity(mod,0).getSop())
			,mod -> ensureFarmerActivity(mod,0).setSop(mod.getAmount(Mod303Key.CT_SA17))
			,true)
		// (1) Actividades agrícolas, ganaderas y forestales. Cuota anual derivada del regimen simplificado [B]
		,CT_SA18(Mod303Key.CT_SA18,null,null,null,"(hasFarmerActivity(0) && isLastPeriod())?round(CT_SA14-CT_SA17):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA18,ensureFarmerActivity(mod,0).getCad())
			,mod -> ensureFarmerActivity(mod,0).setCad(mod.getAmount(Mod303Key.CT_SA18))
			,true)
		// (2) Actividades agrícolas, ganaderas y forestales. Código
		,CT_SA21(Mod303Key.CT_SA21,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_SA21,ensureFarmerActivity(mod,1).getCode())
			,mod -> ensureFarmerActivity(mod,1).setCode(mod.getDescription(Mod303Key.CT_SA21))
			,true)
		// (2) Actividades agrícolas, ganaderas y forestales. Descripción
		,CT_SA2D(Mod303Key.CT_SA2D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_SA2D,ensureFarmerActivity(mod,1).getDescription())
			,mod -> ensureFarmerActivity(mod,1).setDescription(mod.getDescription(Mod303Key.CT_SA2D))
			,true)
		// (2) Actividades agrícolas, ganaderas y forestales. Volumen de ingresos
		,CT_SA22(Mod303Key.CT_SA22,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA22,ensureFarmerActivity(mod,1).getVol())
			,mod -> ensureFarmerActivity(mod,1).setVol(mod.getAmount(Mod303Key.CT_SA22))
			,false)
		// (2) Actividades agrícolas, ganaderas y forestales. Índice de cuota
		,CT_SA23(Mod303Key.CT_SA23,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA23,ensureFarmerActivity(mod,1).getInd() * 10000)
			,mod -> ensureFarmerActivity(mod,1).setInd(mod.getAmount(Mod303Key.CT_SA23) / 10000)
			,true)
		// (2) Actividades agrícolas, ganaderas y forestales. Cuota devengada
		,CT_SA24(Mod303Key.CT_SA24,null,null,null,"(hasFarmerActivity(1))?round(CT_SA22*CT_SA23/10000):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA24,ensureFarmerActivity(mod,1).getCuo())
			,mod -> ensureFarmerActivity(mod,1).setCuo(mod.getAmount(Mod303Key.CT_SA24))
			,true)
		// (2) Actividades agrícolas, ganaderas y forestales. Porcentaje trimestral
		,CT_SA25(Mod303Key.CT_SA25,null,null,null,"(hasFarmerActivity(1) && !isLastPeriod())?CT_SA25:(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA25,ensureFarmerActivity(mod,1).getPor())
			,mod -> ensureFarmerActivity(mod,1).setPor(mod.getAmount(Mod303Key.CT_SA25))
			,true)
		// (2) Actividades agrícolas, ganaderas y forestales. Ingreso a cuenta [A]
		,CT_SA26(Mod303Key.CT_SA26,null,null,null,"(hasFarmerActivity(1) && !isLastPeriod())?round(CT_SA24*CT_SA25/100):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA26,ensureFarmerActivity(mod,1).getIng())
			,mod -> ensureFarmerActivity(mod,1).setIng(mod.getAmount(Mod303Key.CT_SA26))
			,true)
		// (2) Actividades agrícolas, ganaderas y forestales. Cuota soportada
		,CT_SA27(Mod303Key.CT_SA27,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA27,ensureFarmerActivity(mod,1).getSop())
			,mod -> ensureFarmerActivity(mod,1).setSop(mod.getAmount(Mod303Key.CT_SA27))
			,true)
		// (2) Actividades agrícolas, ganaderas y forestales. Cuota anual derivada del regimen simplificado [B]
		,CT_SA28(Mod303Key.CT_SA28,null,null,null,"(hasFarmerActivity(1) && isLastPeriod())?round(CT_SA24-CT_SA27):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA28,ensureFarmerActivity(mod,1).getCad())
			,mod -> ensureFarmerActivity(mod,1).setCad(mod.getAmount(Mod303Key.CT_SA28))
			,true)
		
		// (3) Actividades agrícolas, ganaderas y forestales. Código
		,CT_SA31(Mod303Key.CT_SA31,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_SA31,ensureFarmerActivity(mod,2).getCode())
			,mod -> ensureFarmerActivity(mod,2).setCode(mod.getDescription(Mod303Key.CT_SA31))
			,true)
		// (3) Actividades agrícolas, ganaderas y forestales. Descripción
		,CT_SA3D(Mod303Key.CT_SA3D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_SA3D,ensureFarmerActivity(mod,2).getDescription())
			,mod -> ensureFarmerActivity(mod,2).setDescription(mod.getDescription(Mod303Key.CT_SA3D))
			,true)
		// (3) Actividades agrícolas, ganaderas y forestales. Volumen de ingresos
		,CT_SA32(Mod303Key.CT_SA32,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA32,ensureFarmerActivity(mod,2).getVol())
			,mod -> ensureFarmerActivity(mod,2).setVol(mod.getAmount(Mod303Key.CT_SA32))
			,false)
		// (3) Actividades agrícolas, ganaderas y forestales. Índice de cuota
		,CT_SA33(Mod303Key.CT_SA33,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA33,ensureFarmerActivity(mod,2).getInd() * 10000)
			,mod -> ensureFarmerActivity(mod,2).setInd(mod.getAmount(Mod303Key.CT_SA33) / 10000)
			,true)
		// (3) Actividades agrícolas, ganaderas y forestales. Cuota devengada
		,CT_SA34(Mod303Key.CT_SA34,null,null,null,"(hasFarmerActivity(2))?round(CT_SA32*CT_SA33/10000):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA34,ensureFarmerActivity(mod,2).getCuo())
			,mod -> ensureFarmerActivity(mod,2).setCuo(mod.getAmount(Mod303Key.CT_SA34))
			,true)
		// (3) Actividades agrícolas, ganaderas y forestales. Porcentaje trimestral
		,CT_SA35(Mod303Key.CT_SA35,null,null,null,"(hasFarmerActivity(2) && !isLastPeriod())?CT_SA35:(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA35,ensureFarmerActivity(mod,2).getPor())
			,mod -> ensureFarmerActivity(mod,2).setPor(mod.getAmount(Mod303Key.CT_SA35))
			,true)
		// (3) Actividades agrícolas, ganaderas y forestales. Ingreso a cuenta [A]
		,CT_SA36(Mod303Key.CT_SA36,null,null,null,"(hasFarmerActivity(2) && !isLastPeriod())?round(CT_SA34*CT_SA35/100):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA36,ensureFarmerActivity(mod,2).getIng())
			,mod -> ensureFarmerActivity(mod,2).setIng(mod.getAmount(Mod303Key.CT_SA36))
			,true)
		// (3) Actividades agrícolas, ganaderas y forestales. Cuota soportada
		,CT_SA37(Mod303Key.CT_SA37,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA37,ensureFarmerActivity(mod,2).getSop())
			,mod -> ensureFarmerActivity(mod,2).setSop(mod.getAmount(Mod303Key.CT_SA37))
			,true)
		// (3) Actividades agrícolas, ganaderas y forestales. Cuota anual derivada del regimen simplificado [B]
		,CT_SA38(Mod303Key.CT_SA38,null,null,null,"(hasFarmerActivity(2) && isLastPeriod())?round(CT_SA34-CT_SA37):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA38,ensureFarmerActivity(mod,2).getCad())
			,mod -> ensureFarmerActivity(mod,2).setCad(mod.getAmount(Mod303Key.CT_SA38))
			,true)
		
		// (4) Actividades agrícolas, ganaderas y forestales. Código
		,CT_SA41(Mod303Key.CT_SA41,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_SA41,ensureFarmerActivity(mod,3).getCode())
			,mod -> ensureFarmerActivity(mod,3).setCode(mod.getDescription(Mod303Key.CT_SA41))
			,true)
		// (4) Actividades agrícolas, ganaderas y forestales. Descripción
		,CT_SA4D(Mod303Key.CT_SA4D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_SA4D,ensureFarmerActivity(mod,3).getDescription())
			,mod -> ensureFarmerActivity(mod,3).setDescription(mod.getDescription(Mod303Key.CT_SA4D))
			,true)
		// (4) Actividades agrícolas, ganaderas y forestales. Volumen de ingresos
		,CT_SA42(Mod303Key.CT_SA42,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA42,ensureFarmerActivity(mod,3).getVol())
			,mod -> ensureFarmerActivity(mod,3).setVol(mod.getAmount(Mod303Key.CT_SA42))
			,false)
		// (4) Actividades agrícolas, ganaderas y forestales. Índice de cuota
		,CT_SA43(Mod303Key.CT_SA43,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA43,ensureFarmerActivity(mod,3).getInd() * 10000)
			,mod -> ensureFarmerActivity(mod,3).setInd(mod.getAmount(Mod303Key.CT_SA43) / 10000)
			,true)
		// (4) Actividades agrícolas, ganaderas y forestales. Cuota devengada
		,CT_SA44(Mod303Key.CT_SA44,null,null,null,"(hasFarmerActivity(3))?round(CT_SA42*CT_SA43/10000):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA44,ensureFarmerActivity(mod,3).getCuo())
			,mod -> ensureFarmerActivity(mod,3).setCuo(mod.getAmount(Mod303Key.CT_SA44))
			,true)
		// (4) Actividades agrícolas, ganaderas y forestales. Porcentaje trimestral
		,CT_SA45(Mod303Key.CT_SA45,null,null,null,"(hasFarmerActivity(3) && !isLastPeriod())?CT_SA45:(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA45,ensureFarmerActivity(mod,3).getPor())
			,mod -> ensureFarmerActivity(mod,3).setPor(mod.getAmount(Mod303Key.CT_SA45))
			,true)
		// (4) Actividades agrícolas, ganaderas y forestales. Ingreso a cuenta [A]
		,CT_SA46(Mod303Key.CT_SA46,null,null,null,"(hasFarmerActivity(3) && !isLastPeriod())?round(CT_SA44*CT_SA45/100):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA46,ensureFarmerActivity(mod,3).getIng())
			,mod -> ensureFarmerActivity(mod,3).setIng(mod.getAmount(Mod303Key.CT_SA46))
			,true)
		// (4) Actividades agrícolas, ganaderas y forestales. Cuota soportada
		,CT_SA47(Mod303Key.CT_SA47,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_SA47,ensureFarmerActivity(mod,3).getSop())
			,mod -> ensureFarmerActivity(mod,3).setSop(mod.getAmount(Mod303Key.CT_SA47))
			,true)
		// (4) Actividades agrícolas, ganaderas y forestales. Cuota anual derivada del regimen simplificado [B]
		,CT_SA48(Mod303Key.CT_SA48,null,null,null,"(hasFarmerActivity(3) && isLastPeriod())?round(CT_SA44-CT_SA47):(0.0)",null
			,mod -> mod.putAmount(Mod303Key.CT_SA48,ensureFarmerActivity(mod,3).getCad())
			,mod -> ensureFarmerActivity(mod,3).setCad(mod.getAmount(Mod303Key.CT_SA48))
			,true)

		// (1) Actividades en régimen simplificado. Epigrafe IAE
		,CT_S101(Mod303Key.CT_S101,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S101,ensureActivity(mod,0).getEpigraph())
			,mod -> ensureActivity(mod,0).setEpigraph(mod.getDescription(Mod303Key.CT_S101))
			,true)
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,CT_S10D(Mod303Key.CT_S10D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S10D,ensureActivity(mod,0).getDescription())
			,mod -> ensureActivity(mod,0).setDescription(mod.getDescription(Mod303Key.CT_S10D))
			,true)
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de ep\u00EDgrafes 691.9 y 722
		,CT_S102(Mod303Key.CT_S102,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S102,ensureActivity(mod,0).getSpecialEpigraph())
			,mod -> ensureActivity(mod,0).setSpecialEpigraph((int) mod.getAmount(Mod303Key.CT_S102))
			,true)
		// (1) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,CT_S1X1(Mod303Key.CT_S1X1,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S1X1,ensureActivity(mod,0).getTem())
			,mod -> ensureActivity(mod,0).setTem((int) mod.getAmount(Mod303Key.CT_S1X1))
			,true)
		// (1) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,CT_S1X2(Mod303Key.CT_S1X2,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S1X2,ensureActivity(mod,0).getDia())
			,mod -> ensureActivity(mod,0).setDia((int) mod.getAmount(Mod303Key.CT_S1X2))
			,true)
		// (1) Actividades en régimen simplificado. Número de empleados al inicio del ejercicio ( o al inicio de la actividad)
		,CT_S1X3(Mod303Key.CT_S1X3,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S1X3,ensureActivity(mod,0).getEmp())
			,mod -> ensureActivity(mod,0).setEmp((int) mod.getAmount(Mod303Key.CT_S1X3))
			,true)
		// (1) Actividades en régimen simplificado. Si realiza la actividad en LORCA
		,CT_S1X4(Mod303Key.CT_S1X4,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S1X4,ensureActivity(mod,0).getLor())
			,mod -> ensureActivity(mod,0).setLor((int) mod.getAmount(Mod303Key.CT_S1X4))
			,true)
		,CT_S11D(Mod303Key.CT_S11D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S11D,ensureModule(mod,0,0).getDescription())
			,mod -> ensureModule(mod,0,0).setDescription(mod.getDescription(Mod303Key.CT_S11D))
			,true)
		,CT_S11I(Mod303Key.CT_S11I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S11I,ensureModule(mod,0,0).getValue()) 
			,mod -> ensureModule(mod,0,0).setValue(mod.getAmount(Mod303Key.CT_S11I))
			,true)
		,CT_S11U(Mod303Key.CT_S11U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S11U,ensureModule(mod,0,0).getUnit())
			,mod -> ensureModule(mod,0,0).setUnit(mod.getDescription(Mod303Key.CT_S11U))
			,true)
		,CT_S11F(Mod303Key.CT_S11F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S11F,ensureModule(mod,0,0).getFactor())
			,mod -> ensureModule(mod,0,0).setFactor(mod.getAmount(Mod303Key.CT_S11F))
			,true)
		,CT_S11R(Mod303Key.CT_S11R,null,null,null,"hasActivity(0)?round(CT_S11I*CT_S11F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S11R,ensureModule(mod,0,0).getResult())
			,mod -> ensureModule(mod,0,0).setResult(mod.getAmount(Mod303Key.CT_S11R))
			,true)
		,CT_S12D(Mod303Key.CT_S12D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S12D,ensureModule(mod,0,1).getDescription())
			,mod -> ensureModule(mod,0,1).setDescription(mod.getDescription(Mod303Key.CT_S12D))
			,true)
		,CT_S12I(Mod303Key.CT_S12I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S12I,ensureModule(mod,0,1).getValue()) 
			,mod -> ensureModule(mod,0,1).setValue(mod.getAmount(Mod303Key.CT_S12I))
			,true)
		,CT_S12U(Mod303Key.CT_S12U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S12U,ensureModule(mod,0,1).getUnit())
			,mod -> ensureModule(mod,0,1).setUnit(mod.getDescription(Mod303Key.CT_S12U))
			,true)
		,CT_S12F(Mod303Key.CT_S12F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S12F,ensureModule(mod,0,1).getFactor())
			,mod -> ensureModule(mod,0,1).setFactor(mod.getAmount(Mod303Key.CT_S12F))
			,true)
		,CT_S12R(Mod303Key.CT_S12R,null,null,null,"hasActivity(0)?round(CT_S12I*CT_S12F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S12R,ensureModule(mod,0,1).getResult())
			,mod -> ensureModule(mod,0,1).setResult(mod.getAmount(Mod303Key.CT_S12R))
			,true)
		,CT_S13D(Mod303Key.CT_S13D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S13D,ensureModule(mod,0,2).getDescription())
			,mod -> ensureModule(mod,0,2).setDescription(mod.getDescription(Mod303Key.CT_S13D))
			,true)
		,CT_S13I(Mod303Key.CT_S13I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S13I,ensureModule(mod,0,2).getValue()) 
			,mod -> ensureModule(mod,0,2).setValue(mod.getAmount(Mod303Key.CT_S13I))
			,true)
		,CT_S13U(Mod303Key.CT_S13U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S13U,ensureModule(mod,0,2).getUnit())
			,mod -> ensureModule(mod,0,2).setUnit(mod.getDescription(Mod303Key.CT_S13U))
			,true)
		,CT_S13F(Mod303Key.CT_S13F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S13F,ensureModule(mod,0,2).getFactor())
			,mod -> ensureModule(mod,0,2).setFactor(mod.getAmount(Mod303Key.CT_S13F))
			,true)
		,CT_S13R(Mod303Key.CT_S13R,null,null,null,"hasActivity(0)?round(CT_S13I*CT_S13F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S13R,ensureModule(mod,0,2).getResult())
			,mod -> ensureModule(mod,0,2).setResult(mod.getAmount(Mod303Key.CT_S13R))
			,true)

		,CT_S14D(Mod303Key.CT_S14D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S14D,ensureModule(mod,0,3).getDescription())
			,mod -> ensureModule(mod,0,3).setDescription(mod.getDescription(Mod303Key.CT_S14D))
			,true)
		,CT_S14I(Mod303Key.CT_S14I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S14I,ensureModule(mod,0,3).getValue()) 
			,mod -> ensureModule(mod,0,3).setValue(mod.getAmount(Mod303Key.CT_S14I))
			,true)
		,CT_S14U(Mod303Key.CT_S14U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S14U,ensureModule(mod,0,3).getUnit())
			,mod -> ensureModule(mod,0,3).setUnit(mod.getDescription(Mod303Key.CT_S14U))
			,true)
		,CT_S14F(Mod303Key.CT_S14F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S14F,ensureModule(mod,0,3).getFactor())
			,mod -> ensureModule(mod,0,3).setFactor(mod.getAmount(Mod303Key.CT_S14F))
			,true)
		,CT_S14R(Mod303Key.CT_S14R,null,null,null,"hasActivity(0)?round(CT_S14I*CT_S14F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S14R,ensureModule(mod,0,3).getResult())
			,mod -> ensureModule(mod,0,3).setResult(mod.getAmount(Mod303Key.CT_S14R))
			,true)
		,CT_S15D(Mod303Key.CT_S15D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S15D,ensureModule(mod,0,4).getDescription())
			,mod -> ensureModule(mod,0,4).setDescription(mod.getDescription(Mod303Key.CT_S15D))
			,true)
		,CT_S15I(Mod303Key.CT_S15I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S15I,ensureModule(mod,0,4).getValue()) 
			,mod -> ensureModule(mod,0,4).setValue(mod.getAmount(Mod303Key.CT_S15I))
			,true)
		,CT_S15U(Mod303Key.CT_S15U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S15U,ensureModule(mod,0,4).getUnit())
			,mod -> ensureModule(mod,0,4).setUnit(mod.getDescription(Mod303Key.CT_S15U))
			,true)
		,CT_S15F(Mod303Key.CT_S15F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S15F,ensureModule(mod,0,4).getFactor())
			,mod -> ensureModule(mod,0,4).setFactor(mod.getAmount(Mod303Key.CT_S15F))
			,true)
		,CT_S15R(Mod303Key.CT_S15R,null,null,null,"hasActivity(0)?round(CT_S15I*CT_S15F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S15R,ensureModule(mod,0,4).getResult())
			,mod -> ensureModule(mod,0,4).setResult(mod.getAmount(Mod303Key.CT_S15R))
			,true)
		,CT_S16D(Mod303Key.CT_S16D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S16D,ensureModule(mod,0,5).getDescription())
			,mod -> ensureModule(mod,0,5).setDescription(mod.getDescription(Mod303Key.CT_S16D))
			,true)
		,CT_S16I(Mod303Key.CT_S16I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S16I,ensureModule(mod,0,5).getValue()) 
			,mod -> ensureModule(mod,0,5).setValue(mod.getAmount(Mod303Key.CT_S16I))
			,true)
		,CT_S16U(Mod303Key.CT_S16U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S16U,ensureModule(mod,0,5).getUnit())
			,mod -> ensureModule(mod,0,5).setUnit(mod.getDescription(Mod303Key.CT_S16U))
			,true)
		,CT_S16F(Mod303Key.CT_S16F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S16F,ensureModule(mod,0,5).getFactor())
			,mod -> ensureModule(mod,0,5).setFactor(mod.getAmount(Mod303Key.CT_S16F))
			,true)
		,CT_S16R(Mod303Key.CT_S16R,null,null,null,"hasActivity(0)?round(CT_S16I*CT_S16F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S16R,ensureModule(mod,0,5).getResult())
			,mod -> ensureModule(mod,0,5).setResult(mod.getAmount(Mod303Key.CT_S16R))
			,true)
		,CT_S17D(Mod303Key.CT_S17D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S17D,ensureModule(mod,0,6).getDescription())
			,mod -> ensureModule(mod,0,6).setDescription(mod.getDescription(Mod303Key.CT_S17D))
			,true)
		,CT_S17I(Mod303Key.CT_S17I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S17I,ensureModule(mod,0,6).getValue()) 
			,mod -> ensureModule(mod,0,6).setValue(mod.getAmount(Mod303Key.CT_S17I))
			,true)
		,CT_S17U(Mod303Key.CT_S17U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S17U,ensureModule(mod,0,6).getUnit())
			,mod -> ensureModule(mod,0,6).setUnit(mod.getDescription(Mod303Key.CT_S17U))
			,true)
		,CT_S17F(Mod303Key.CT_S17F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S17F,ensureModule(mod,0,6).getFactor())
			,mod -> ensureModule(mod,0,6).setFactor(mod.getAmount(Mod303Key.CT_S17F))
			,true)
		,CT_S17R(Mod303Key.CT_S17R,null,null,null,"hasActivity(0)?round(CT_S17I*CT_S17F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S17R,ensureModule(mod,0,6).getResult())
			,mod -> ensureModule(mod,0,6).setResult(mod.getAmount(Mod303Key.CT_S17R))
			,true)

		// (1) Actividades en régimen simplificado. C Cuota devengada operaciones corrientes
		,CT_S117(Mod303Key.CT_S117,null,null,null,"CT_S11R+CT_S12R+CT_S13R+CT_S14R+CT_S15R+CT_S16R+CT_S17R",null
			,mod -> mod.putAmount(Mod303Key.CT_S117,ensureActivity(mod,0).getDev())
			,mod -> ensureActivity(mod,0).setDev(mod.getAmount(Mod303Key.CT_S117))
			,false)
		// (1) Actividades en régimen simplificado. D Reducciones
		,CT_S118(Mod303Key.CT_S118,null,null,null,"(CT_S1X4 == 1)?round(CT_S117*20/100):CT_S118",null
			,mod -> mod.putAmount(Mod303Key.CT_S118,ensureActivity(mod,0).getRed())
			,mod -> ensureActivity(mod,0).setRed(mod.getAmount(Mod303Key.CT_S118))
			,false)
		// (1) Actividades en régimen simplificado. Z Índice corrector actividades de temporada
		,CT_S119(Mod303Key.CT_S119,null,null,null, "calculateIndiceTemporada( CT_S1X1 )",null
			,mod -> mod.putAmount(Mod303Key.CT_S119,ensureActivity(mod,0).getInd())
			,mod -> ensureActivity(mod,0).setInd(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S119))
			,false)
		// (1) Actividades en régimen simplificado. E Porcentaje de ingreso a cuenta
		,CT_S120(Mod303Key.CT_S120,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S120,ensureActivity(mod,0).getPor())
			,mod -> ensureActivity(mod,0).setPor(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S120))
			,false)
		// (1) Actividades en régimen simplificado. F Ingreso a cuenta ( ([C] - [D] ) x [E])
		,CT_S121(Mod303Key.CT_S121,null,null,null, "calculateIngresoCuenta(1, CT_S1X1, CT_S1X2, CT_S117, CT_S118, CT_S119, CT_S120)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S121,ensureActivity(mod,0).getIng())
			,mod -> ensureActivity(mod,0).setIng(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S121))
			,false)
		// (1) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes
		,CT_S12X(Mod303Key.CT_S12X,null,null,null,"isLastPeriod()?round(CT_S117 * 1 / 100):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S12X,ensureActivity(mod,0).getSopx())
			,mod -> ensureActivity(mod,0).setSopx(mod.getAmount(Mod303Key.CT_S12X))
			,false)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas
		,CT_S12Y(Mod303Key.CT_S12Y,null,null,null,null
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S12Y,ensureActivity(mod,0).getSopy())
			,mod -> ensureActivity(mod,0).setSopy(mod.getAmount(Mod303Key.CT_S12Y))
			,false)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas operaciones corrientes
		,CT_S122(Mod303Key.CT_S122,null,null,null,"isLastPeriod()?(CT_S12X+CT_S12Y):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S122,ensureActivity(mod,0).getSop())
			,mod -> ensureActivity(mod,0).setSop(mod.getAmount(Mod303Key.CT_S122))
			,false)
		// (1) Actividades en régimen simplificado. H Índice corrector de actividades de temporada
		,CT_S123(Mod303Key.CT_S123,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S123,ensureActivity(mod,0).getIct())
			,mod -> ensureActivity(mod,0).setIct(mod.getAmount(Mod303Key.CT_S123))
			,false)
		// (1) Actividades en régimen simplificado. I RESULTADO (( [C] - [D] - [G] ) x [H])
		,CT_S124(Mod303Key.CT_S124,null,null,null,"calculateResultadoAnual( CT_S117, CT_S118, CT_S122, CT_S123)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S124,ensureActivity(mod,0).getRes())
			,mod -> ensureActivity(mod,0).setRes(mod.getAmount(Mod303Key.CT_S124))
			,false)
		// (1) Actividades en régimen simplificado. J Porcentaje cuota mínima
		,CT_S125(Mod303Key.CT_S125,null,null,null,"isLastPeriod()?CT_S125:0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S125,ensureActivity(mod,0).getPcm())
			,mod -> ensureActivity(mod,0).setPcm(mod.getAmount(Mod303Key.CT_S125))
			,false)
		// (1) Actividades en régimen simplificado. K Devolución cuotas soportadas otros países
		,CT_S126(Mod303Key.CT_S126,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S126,ensureActivity(mod,0).getDvc())
			,mod -> ensureActivity(mod,0).setDvc(mod.getAmount(Mod303Key.CT_S126))
			,false)
		// (1) Actividades en régimen simplificado. L Cuota mínima
		,CT_S127(Mod303Key.CT_S127,null,null,null,"calculateCuotaMinima(CT_S117, CT_S118, CT_S125, CT_S126,CT_S123)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S127,ensureActivity(mod,0).getCmn())
			,mod -> ensureActivity(mod,0).setCmn(mod.getAmount(Mod303Key.CT_S127))
			,false)
		// (1) Actividades en régimen simplificado. M Cuota anual derivada régimen simplificado
		,CT_S128(Mod303Key.CT_S128,null,null,null,"isLastPeriod()?((CT_S127>CT_S124)?CT_S127:CT_S124):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S128,ensureActivity(mod,0).getCad())
			,mod -> ensureActivity(mod,0).setCad(mod.getAmount(Mod303Key.CT_S128))
			,false)
		
		
		// (1) Actividades en régimen simplificado. Epigrafe IAE
		,CT_S201(Mod303Key.CT_S201,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S201,ensureActivity(mod,1).getEpigraph())
			,mod -> ensureActivity(mod,1).setEpigraph(mod.getDescription(Mod303Key.CT_S201))
			,true)
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,CT_S20D(Mod303Key.CT_S20D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S20D,ensureActivity(mod,1).getDescription())
			,mod -> ensureActivity(mod,1).setDescription(mod.getDescription(Mod303Key.CT_S20D))
			,true)
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de ep\u00EDgrafes 691.9 y 722
		,CT_S202(Mod303Key.CT_S202,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S202,ensureActivity(mod,1).getSpecialEpigraph())
			,mod -> ensureActivity(mod,1).setSpecialEpigraph((int) mod.getAmount(Mod303Key.CT_S202))
			,true)
		// (1) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,CT_S2X1(Mod303Key.CT_S2X1,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S2X1,ensureActivity(mod,1).getTem())
			,mod -> ensureActivity(mod,1).setTem((int) mod.getAmount(Mod303Key.CT_S2X1))
			,true)
		// (1) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,CT_S2X2(Mod303Key.CT_S2X2,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S2X2,ensureActivity(mod,1).getDia())
			,mod -> ensureActivity(mod,1).setDia((int) mod.getAmount(Mod303Key.CT_S2X2))
			,true)
		// (1) Actividades en régimen simplificado. Número de empleados al inicio del ejercicio ( o al inicio de la actividad)
		,CT_S2X3(Mod303Key.CT_S2X3,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S2X3,ensureActivity(mod,1).getEmp())
			,mod -> ensureActivity(mod,1).setEmp((int) mod.getAmount(Mod303Key.CT_S2X3))
			,true)
		// (1) Actividades en régimen simplificado. Si realiza la actividad en LORCA
		,CT_S2X4(Mod303Key.CT_S2X4,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S2X4,ensureActivity(mod,1).getLor())
			,mod -> ensureActivity(mod,1).setLor((int) mod.getAmount(Mod303Key.CT_S2X4))
			,true)
			
		,CT_S21D(Mod303Key.CT_S21D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S21D,ensureModule(mod,1,0).getDescription())
			,mod -> ensureModule(mod,1,0).setDescription(mod.getDescription(Mod303Key.CT_S21D))
			,true)
		,CT_S21I(Mod303Key.CT_S21I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S21I,ensureModule(mod,1,0).getValue()) 
			,mod -> ensureModule(mod,1,0).setValue(mod.getAmount(Mod303Key.CT_S21I))
			,true)
		,CT_S21U(Mod303Key.CT_S21U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S21U,ensureModule(mod,1,0).getUnit())
			,mod -> ensureModule(mod,1,0).setUnit(mod.getDescription(Mod303Key.CT_S21U))
			,true)
		,CT_S21F(Mod303Key.CT_S21F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S21F,ensureModule(mod,1,0).getFactor())
			,mod -> ensureModule(mod,1,0).setFactor(mod.getAmount(Mod303Key.CT_S21F))
			,true)
		,CT_S21R(Mod303Key.CT_S21R,null,null,null,"hasActivity(1)?round(CT_S21I*CT_S21F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S21R,ensureModule(mod,1,0).getResult())
			,mod -> ensureModule(mod,1,0).setResult(mod.getAmount(Mod303Key.CT_S21R))
			,true)
		
		,CT_S22D(Mod303Key.CT_S22D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S22D,ensureModule(mod,1,1).getDescription())
			,mod -> ensureModule(mod,1,1).setDescription(mod.getDescription(Mod303Key.CT_S22D))
			,true)
		,CT_S22I(Mod303Key.CT_S22I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S22I,ensureModule(mod,1,1).getValue()) 
			,mod -> ensureModule(mod,1,1).setValue(mod.getAmount(Mod303Key.CT_S22I))
			,true)
		,CT_S22U(Mod303Key.CT_S22U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S22U,ensureModule(mod,1,1).getUnit())
			,mod -> ensureModule(mod,1,1).setUnit(mod.getDescription(Mod303Key.CT_S22U))
			,true)
		,CT_S22F(Mod303Key.CT_S22F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S22F,ensureModule(mod,1,1).getFactor())
			,mod -> ensureModule(mod,1,1).setFactor(mod.getAmount(Mod303Key.CT_S22F))
			,true)
		,CT_S22R(Mod303Key.CT_S22R,null,null,null,"hasActivity(1)?round(CT_S22I*CT_S22F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S22R,ensureModule(mod,1,1).getResult())
			,mod -> ensureModule(mod,1,1).setResult(mod.getAmount(Mod303Key.CT_S22R))
			,true)
		
		,CT_S23D(Mod303Key.CT_S23D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S23D,ensureModule(mod,1,2).getDescription())
			,mod -> ensureModule(mod,1,2).setDescription(mod.getDescription(Mod303Key.CT_S23D))
			,true)
		,CT_S23I(Mod303Key.CT_S23I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S23I,ensureModule(mod,1,2).getValue()) 
			,mod -> ensureModule(mod,1,2).setValue(mod.getAmount(Mod303Key.CT_S23I))
			,true)
		,CT_S23U(Mod303Key.CT_S23U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S23U,ensureModule(mod,1,2).getUnit())
			,mod -> ensureModule(mod,1,2).setUnit(mod.getDescription(Mod303Key.CT_S23U))
			,true)
		,CT_S23F(Mod303Key.CT_S23F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S23F,ensureModule(mod,1,2).getFactor())
			,mod -> ensureModule(mod,1,2).setFactor(mod.getAmount(Mod303Key.CT_S23F))
			,true)
		,CT_S23R(Mod303Key.CT_S23R,null,null,null,"hasActivity(1)?round(CT_S23I*CT_S23F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S23R,ensureModule(mod,1,2).getResult())
			,mod -> ensureModule(mod,1,2).setResult(mod.getAmount(Mod303Key.CT_S23R))
			,true)

		,CT_S24D(Mod303Key.CT_S24D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S24D,ensureModule(mod,1,3).getDescription())
			,mod -> ensureModule(mod,1,3).setDescription(mod.getDescription(Mod303Key.CT_S24D))
			,true)
		,CT_S24I(Mod303Key.CT_S24I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S24I,ensureModule(mod,1,3).getValue()) 
			,mod -> ensureModule(mod,1,3).setValue(mod.getAmount(Mod303Key.CT_S24I))
			,true)
		,CT_S24U(Mod303Key.CT_S24U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S24U,ensureModule(mod,1,3).getUnit())
			,mod -> ensureModule(mod,1,3).setUnit(mod.getDescription(Mod303Key.CT_S24U))
			,true)
		,CT_S24F(Mod303Key.CT_S24F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S24F,ensureModule(mod,1,3).getFactor())
			,mod -> ensureModule(mod,1,3).setFactor(mod.getAmount(Mod303Key.CT_S24F))
			,true)
		,CT_S24R(Mod303Key.CT_S24R,null,null,null,"hasActivity(1)?round(CT_S24I*CT_S24F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S24R,ensureModule(mod,1,3).getResult())
			,mod -> ensureModule(mod,1,3).setResult(mod.getAmount(Mod303Key.CT_S24R))
			,true)

		,CT_S25D(Mod303Key.CT_S25D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S25D,ensureModule(mod,1,4).getDescription())
			,mod -> ensureModule(mod,1,4).setDescription(mod.getDescription(Mod303Key.CT_S25D))
			,true)
		,CT_S25I(Mod303Key.CT_S25I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S25I,ensureModule(mod,1,4).getValue()) 
			,mod -> ensureModule(mod,1,4).setValue(mod.getAmount(Mod303Key.CT_S25I))
			,true)
		,CT_S25U(Mod303Key.CT_S25U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S25U,ensureModule(mod,1,4).getUnit())
			,mod -> ensureModule(mod,1,4).setUnit(mod.getDescription(Mod303Key.CT_S25U))
			,true)
		,CT_S25F(Mod303Key.CT_S25F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S25F,ensureModule(mod,1,4).getFactor())
			,mod -> ensureModule(mod,1,4).setFactor(mod.getAmount(Mod303Key.CT_S25F))
			,true)
		,CT_S25R(Mod303Key.CT_S25R,null,null,null,"hasActivity(1)?round(CT_S25I*CT_S25F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S25R,ensureModule(mod,1,4).getResult())
			,mod -> ensureModule(mod,1,4).setResult(mod.getAmount(Mod303Key.CT_S25R))
			,true)

		,CT_S26D(Mod303Key.CT_S26D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S26D,ensureModule(mod,1,5).getDescription())
			,mod -> ensureModule(mod,1,5).setDescription(mod.getDescription(Mod303Key.CT_S26D))
			,true)
		,CT_S26I(Mod303Key.CT_S26I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S26I,ensureModule(mod,1,5).getValue()) 
			,mod -> ensureModule(mod,1,5).setValue(mod.getAmount(Mod303Key.CT_S26I))
			,true)
		,CT_S26U(Mod303Key.CT_S26U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S26U,ensureModule(mod,1,5).getUnit())
			,mod -> ensureModule(mod,1,5).setUnit(mod.getDescription(Mod303Key.CT_S26U))
			,true)
		,CT_S26F(Mod303Key.CT_S26F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S26F,ensureModule(mod,1,5).getFactor())
			,mod -> ensureModule(mod,1,5).setFactor(mod.getAmount(Mod303Key.CT_S26F))
			,true)
		,CT_S26R(Mod303Key.CT_S26R,null,null,null,"hasActivity(1)?round(CT_S26I*CT_S26F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S26R,ensureModule(mod,1,5).getResult())
			,mod -> ensureModule(mod,1,5).setResult(mod.getAmount(Mod303Key.CT_S26R))
			,true)
		
		,CT_S27D(Mod303Key.CT_S27D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S27D,ensureModule(mod,1,6).getDescription())
			,mod -> ensureModule(mod,1,6).setDescription(mod.getDescription(Mod303Key.CT_S27D))
			,true)
		,CT_S27I(Mod303Key.CT_S27I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S27I,ensureModule(mod,1,6).getValue()) 
			,mod -> ensureModule(mod,1,6).setValue(mod.getAmount(Mod303Key.CT_S27I))
			,true)
		,CT_S27U(Mod303Key.CT_S27U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S27U,ensureModule(mod,1,6).getUnit())
			,mod -> ensureModule(mod,1,6).setUnit(mod.getDescription(Mod303Key.CT_S27U))
			,true)
		,CT_S27F(Mod303Key.CT_S27F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S27F,ensureModule(mod,1,6).getFactor())
			,mod -> ensureModule(mod,1,6).setFactor(mod.getAmount(Mod303Key.CT_S27F))
			,true)
		,CT_S27R(Mod303Key.CT_S27R,null,null,null,"hasActivity(1)?round(CT_S27I*CT_S27F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S27R,ensureModule(mod,1,6).getResult())
			,mod -> ensureModule(mod,1,6).setResult(mod.getAmount(Mod303Key.CT_S27R))
			,true)
		// (1) Actividades en régimen simplificado. C Cuota devengada operaciones corrientes
		,CT_S217(Mod303Key.CT_S217,null,null,null,"CT_S21R+CT_S22R+CT_S23R+CT_S24R+CT_S25R+CT_S26R+CT_S27R",null
			,mod -> mod.putAmount(Mod303Key.CT_S217,ensureActivity(mod,1).getDev())
			,mod -> ensureActivity(mod,1).setDev(mod.getAmount(Mod303Key.CT_S217))
			,false)
		// (1) Actividades en régimen simplificado. D Reducciones
		,CT_S218(Mod303Key.CT_S218,null,null,null,"(CT_S2X4 == 1)?round(CT_S217*20/100):CT_S218",null
			,mod -> mod.putAmount(Mod303Key.CT_S218,ensureActivity(mod,1).getRed())
			,mod -> ensureActivity(mod,1).setRed(mod.getAmount(Mod303Key.CT_S218))
			,false)
		// (1) Actividades en régimen simplificado. Z Índice corrector actividades de temporada
		,CT_S219(Mod303Key.CT_S219,null,null,null, "calculateIndiceTemporada( CT_S2X1 )",null
			,mod -> mod.putAmount(Mod303Key.CT_S219,ensureActivity(mod,1).getInd())
			,mod -> ensureActivity(mod,1).setInd(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S219))
			,false)
		// (1) Actividades en régimen simplificado. E Porcentaje de ingreso a cuenta
		,CT_S220(Mod303Key.CT_S220,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S220,ensureActivity(mod,1).getPor())
			,mod -> ensureActivity(mod,1).setPor(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S220))
			,false)
		// (1) Actividades en régimen simplificado. F Ingreso a cuenta ( ([C] - [D] ) x [E])
		,CT_S221(Mod303Key.CT_S221,null,null,null,"calculateIngresoCuenta(2, CT_S2X1, CT_S2X2, CT_S217, CT_S218, CT_S219, CT_S220)", null
			,mod -> mod.putAmount(Mod303Key.CT_S221,ensureActivity(mod,1).getIng())
			,mod -> ensureActivity(mod,1).setIng(mod.isLastPeriod()?0.0:mod.getAmount(Mod303Key.CT_S221))
			,false)
		// (1) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes
		,CT_S22X(Mod303Key.CT_S22X,null,null,null,"isLastPeriod()?round(CT_S217 * 1 / 100):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S22X,ensureActivity(mod,1).getSopx())
			,mod -> ensureActivity(mod,1).setSopx(mod.getAmount(Mod303Key.CT_S22X))
			,false)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas
		,CT_S22Y(Mod303Key.CT_S22Y,null,null,null,null
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S22Y,ensureActivity(mod,1).getSopy())
			,mod -> ensureActivity(mod,1).setSopy(mod.getAmount(Mod303Key.CT_S22Y))
			,false)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas operaciones corrientes
		,CT_S222(Mod303Key.CT_S222,null,null,null,"isLastPeriod()?(CT_S22X+CT_S22Y):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S222,ensureActivity(mod,1).getSop())
			,mod -> ensureActivity(mod,1).setSop(mod.getAmount(Mod303Key.CT_S222))
			,false)
		// (1) Actividades en régimen simplificado. H Índice corrector de actividades de temporada
		,CT_S223(Mod303Key.CT_S223,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S223,ensureActivity(mod,1).getIct())
			,mod -> ensureActivity(mod,1).setIct(mod.getAmount(Mod303Key.CT_S223))
			,false)
		// (1) Actividades en régimen simplificado. I RESULTADO (( [C] - [D] - [G] ) x [H])
		,CT_S224(Mod303Key.CT_S224,null,null,null,"calculateResultadoAnual( CT_S217, CT_S218, CT_S222, CT_S223)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S224,ensureActivity(mod,1).getRes())
			,mod -> ensureActivity(mod,1).setRes(mod.getAmount(Mod303Key.CT_S224))
			,false)
		// (1) Actividades en régimen simplificado. J Porcentaje cuota mínima
		,CT_S225(Mod303Key.CT_S225,null,null,null,"isLastPeriod()?CT_S225:0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S225,ensureActivity(mod,1).getPcm())
			,mod -> ensureActivity(mod,1).setPcm(mod.getAmount(Mod303Key.CT_S225))
			,false)
		// (1) Actividades en régimen simplificado. K Devolución cuotas soportadas otros países
		,CT_S226(Mod303Key.CT_S226,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S226,ensureActivity(mod,1).getDvc())
			,mod -> ensureActivity(mod,1).setDvc(mod.getAmount(Mod303Key.CT_S226))
			,false)
		// (1) Actividades en régimen simplificado. L Cuota mínima
		,CT_S227(Mod303Key.CT_S227,null,null,null,"calculateCuotaMinima(CT_S217, CT_S218, CT_S225, CT_S226,CT_S223)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S227,ensureActivity(mod,1).getCmn())
			,mod -> ensureActivity(mod,1).setCmn(mod.getAmount(Mod303Key.CT_S227))
			,false)
		// (1) Actividades en régimen simplificado. M Cuota anual derivada régimen simplificado
		,CT_S228(Mod303Key.CT_S228,null,null,null,"isLastPeriod()?((CT_S227>CT_S224)?CT_S227:CT_S224):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S228,ensureActivity(mod,1).getCad())
			,mod -> ensureActivity(mod,1).setCad(mod.getAmount(Mod303Key.CT_S228))
			,false)

		// (1) Actividades en régimen simplificado. Epigrafe IAE
		,CT_S301(Mod303Key.CT_S301,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S301,ensureActivity(mod,2).getEpigraph())
			,mod -> ensureActivity(mod,2).setEpigraph(mod.getDescription(Mod303Key.CT_S301))
			,true)
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,CT_S30D(Mod303Key.CT_S30D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S30D,ensureActivity(mod,2).getDescription())
			,mod -> ensureActivity(mod,2).setDescription(mod.getDescription(Mod303Key.CT_S30D))
			,true)
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de ep\u00EDgrafes 691.9 y 722
		,CT_S302(Mod303Key.CT_S302,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S302,ensureActivity(mod,2).getSpecialEpigraph())
			,mod -> ensureActivity(mod,2).setSpecialEpigraph((int) mod.getAmount(Mod303Key.CT_S302))
			,true)
		// (1) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,CT_S3X1(Mod303Key.CT_S3X1,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S3X1,ensureActivity(mod,2).getTem())
			,mod -> ensureActivity(mod,2).setTem((int) mod.getAmount(Mod303Key.CT_S3X1))
			,true)
		// (1) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,CT_S3X2(Mod303Key.CT_S3X2,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S3X2,ensureActivity(mod,2).getDia())
			,mod -> ensureActivity(mod,2).setDia((int) mod.getAmount(Mod303Key.CT_S3X2))
			,true)
		// (1) Actividades en régimen simplificado. Número de empleados al inicio del ejercicio ( o al inicio de la actividad)
		,CT_S3X3(Mod303Key.CT_S3X3,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S3X3,ensureActivity(mod,2).getEmp())
			,mod -> ensureActivity(mod,2).setEmp((int) mod.getAmount(Mod303Key.CT_S3X3))
			,true)
		// (1) Actividades en régimen simplificado. Si realiza la actividad en LORCA
		,CT_S3X4(Mod303Key.CT_S3X4,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S3X4,ensureActivity(mod,2).getLor())
			,mod -> ensureActivity(mod,2).setLor((int) mod.getAmount(Mod303Key.CT_S3X4))
			,true)
			
		,CT_S31D(Mod303Key.CT_S31D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S31D,ensureModule(mod,2,0).getDescription())
			,mod -> ensureModule(mod,2,0).setDescription(mod.getDescription(Mod303Key.CT_S31D))
			,true)
		,CT_S31I(Mod303Key.CT_S31I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S31I,ensureModule(mod,2,0).getValue()) 
			,mod -> ensureModule(mod,2,0).setValue(mod.getAmount(Mod303Key.CT_S31I))
			,true)
		,CT_S31U(Mod303Key.CT_S31U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S31U,ensureModule(mod,2,0).getUnit())
			,mod -> ensureModule(mod,2,0).setUnit(mod.getDescription(Mod303Key.CT_S31U))
			,true)
		,CT_S31F(Mod303Key.CT_S31F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S31F,ensureModule(mod,2,0).getFactor())
			,mod -> ensureModule(mod,2,0).setFactor(mod.getAmount(Mod303Key.CT_S31F))
			,true)
		,CT_S31R(Mod303Key.CT_S31R,null,null,null,"hasActivity(2)?round(CT_S31I*CT_S31F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S31R,ensureModule(mod,2,0).getResult())
			,mod -> ensureModule(mod,2,0).setResult(mod.getAmount(Mod303Key.CT_S31R))
			,true)
		
		,CT_S32D(Mod303Key.CT_S32D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S32D,ensureModule(mod,2,1).getDescription())
			,mod -> ensureModule(mod,2,1).setDescription(mod.getDescription(Mod303Key.CT_S32D))
			,true)
		,CT_S32I(Mod303Key.CT_S32I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S32I,ensureModule(mod,2,1).getValue()) 
			,mod -> ensureModule(mod,2,1).setValue(mod.getAmount(Mod303Key.CT_S32I))
			,true)
		,CT_S32U(Mod303Key.CT_S32U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S32U,ensureModule(mod,2,1).getUnit())
			,mod -> ensureModule(mod,2,1).setUnit(mod.getDescription(Mod303Key.CT_S32U))
			,true)
		,CT_S32F(Mod303Key.CT_S32F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S32F,ensureModule(mod,2,1).getFactor())
			,mod -> ensureModule(mod,2,1).setFactor(mod.getAmount(Mod303Key.CT_S32F))
			,true)
		,CT_S32R(Mod303Key.CT_S32R,null,null,null,"hasActivity(2)?round(CT_S32I*CT_S32F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S32R,ensureModule(mod,2,1).getResult())
			,mod -> ensureModule(mod,2,1).setResult(mod.getAmount(Mod303Key.CT_S32R))
			,true)
		
		,CT_S33D(Mod303Key.CT_S33D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S33D,ensureModule(mod,2,2).getDescription())
			,mod -> ensureModule(mod,2,2).setDescription(mod.getDescription(Mod303Key.CT_S33D))
			,true)
		,CT_S33I(Mod303Key.CT_S33I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S33I,ensureModule(mod,2,2).getValue()) 
			,mod -> ensureModule(mod,2,2).setValue(mod.getAmount(Mod303Key.CT_S33I))
			,true)
		,CT_S33U(Mod303Key.CT_S33U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S33U,ensureModule(mod,2,2).getUnit())
			,mod -> ensureModule(mod,2,2).setUnit(mod.getDescription(Mod303Key.CT_S33U))
			,true)
		,CT_S33F(Mod303Key.CT_S33F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S33F,ensureModule(mod,2,2).getFactor())
			,mod -> ensureModule(mod,2,2).setFactor(mod.getAmount(Mod303Key.CT_S33F))
			,true)
		,CT_S33R(Mod303Key.CT_S33R,null,null,null,"hasActivity(2)?round(CT_S33I*CT_S33F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S33R,ensureModule(mod,2,2).getResult())
			,mod -> ensureModule(mod,2,2).setResult(mod.getAmount(Mod303Key.CT_S33R))
			,true)

		,CT_S34D(Mod303Key.CT_S34D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S34D,ensureModule(mod,2,3).getDescription())
			,mod -> ensureModule(mod,2,3).setDescription(mod.getDescription(Mod303Key.CT_S34D))
			,true)
		,CT_S34I(Mod303Key.CT_S34I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S34I,ensureModule(mod,2,3).getValue()) 
			,mod -> ensureModule(mod,2,3).setValue(mod.getAmount(Mod303Key.CT_S34I))
			,true)
		,CT_S34U(Mod303Key.CT_S34U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S34U,ensureModule(mod,2,3).getUnit())
			,mod -> ensureModule(mod,2,3).setUnit(mod.getDescription(Mod303Key.CT_S34U))
			,true)
		,CT_S34F(Mod303Key.CT_S34F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S34F,ensureModule(mod,2,3).getFactor())
			,mod -> ensureModule(mod,2,3).setFactor(mod.getAmount(Mod303Key.CT_S34F))
			,true)
		,CT_S34R(Mod303Key.CT_S34R,null,null,null,"hasActivity(2)?round(CT_S34I*CT_S34F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S34R,ensureModule(mod,2,3).getResult())
			,mod -> ensureModule(mod,2,3).setResult(mod.getAmount(Mod303Key.CT_S34R))
			,true)

		,CT_S35D(Mod303Key.CT_S35D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S35D,ensureModule(mod,2,4).getDescription())
			,mod -> ensureModule(mod,2,4).setDescription(mod.getDescription(Mod303Key.CT_S35D))
			,true)
		,CT_S35I(Mod303Key.CT_S35I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S35I,ensureModule(mod,2,4).getValue()) 
			,mod -> ensureModule(mod,2,4).setValue(mod.getAmount(Mod303Key.CT_S35I))
			,true)
		,CT_S35U(Mod303Key.CT_S35U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S35U,ensureModule(mod,2,4).getUnit())
			,mod -> ensureModule(mod,2,4).setUnit(mod.getDescription(Mod303Key.CT_S35U))
			,true)
		,CT_S35F(Mod303Key.CT_S35F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S35F,ensureModule(mod,2,4).getFactor())
			,mod -> ensureModule(mod,2,4).setFactor(mod.getAmount(Mod303Key.CT_S35F))
			,true)
		,CT_S35R(Mod303Key.CT_S35R,null,null,null,"hasActivity(2)?round(CT_S35I*CT_S35F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S35R,ensureModule(mod,2,4).getResult())
			,mod -> ensureModule(mod,2,4).setResult(mod.getAmount(Mod303Key.CT_S35R))
			,true)

		,CT_S36D(Mod303Key.CT_S36D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S36D,ensureModule(mod,2,5).getDescription())
			,mod -> ensureModule(mod,2,5).setDescription(mod.getDescription(Mod303Key.CT_S36D))
			,true)
		,CT_S36I(Mod303Key.CT_S36I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S36I,ensureModule(mod,2,5).getValue()) 
			,mod -> ensureModule(mod,2,5).setValue(mod.getAmount(Mod303Key.CT_S36I))
			,true)
		,CT_S36U(Mod303Key.CT_S36U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S36U,ensureModule(mod,2,5).getUnit())
			,mod -> ensureModule(mod,2,5).setUnit(mod.getDescription(Mod303Key.CT_S36U))
			,true)
		,CT_S36F(Mod303Key.CT_S36F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S36F,ensureModule(mod,2,5).getFactor())
			,mod -> ensureModule(mod,2,5).setFactor(mod.getAmount(Mod303Key.CT_S36F))
			,true)
		,CT_S36R(Mod303Key.CT_S36R,null,null,null,"hasActivity(2)?round(CT_S36I*CT_S36F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S36R,ensureModule(mod,2,5).getResult())
			,mod -> ensureModule(mod,2,5).setResult(mod.getAmount(Mod303Key.CT_S36R))
			,true)
		
		,CT_S37D(Mod303Key.CT_S37D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S37D,ensureModule(mod,2,6).getDescription())
			,mod -> ensureModule(mod,2,6).setDescription(mod.getDescription(Mod303Key.CT_S37D))
			,true)
		,CT_S37I(Mod303Key.CT_S37I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S37I,ensureModule(mod,2,6).getValue()) 
			,mod -> ensureModule(mod,2,6).setValue(mod.getAmount(Mod303Key.CT_S37I))
			,true)
		,CT_S37U(Mod303Key.CT_S37U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S37U,ensureModule(mod,2,6).getUnit())
			,mod -> ensureModule(mod,2,6).setUnit(mod.getDescription(Mod303Key.CT_S37U))
			,true)
		,CT_S37F(Mod303Key.CT_S37F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S37F,ensureModule(mod,2,6).getFactor())
			,mod -> ensureModule(mod,2,6).setFactor(mod.getAmount(Mod303Key.CT_S37F))
			,true)
		,CT_S37R(Mod303Key.CT_S37R,null,null,null,"hasActivity(2)?round(CT_S37I*CT_S37F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S37R,ensureModule(mod,2,6).getResult())
			,mod -> ensureModule(mod,2,6).setResult(mod.getAmount(Mod303Key.CT_S37R))
			,true)
 
		// (1) Actividades en régimen simplificado. C Cuota devengada operaciones corrientes
		,CT_S317(Mod303Key.CT_S317,null,null,null,"CT_S31R+CT_S32R+CT_S33R+CT_S34R+CT_S35R+CT_S36R+CT_S37R",null
			,mod -> mod.putAmount(Mod303Key.CT_S317,ensureActivity(mod,2).getDev())
			,mod -> ensureActivity(mod,2).setDev(mod.getAmount(Mod303Key.CT_S317))
			,false)
		// (1) Actividades en régimen simplificado. D Reducciones
		,CT_S318(Mod303Key.CT_S318,null,null,null,"(CT_S3X4 == 1)?round(CT_S317*20/100):CT_S318",null
			,mod -> mod.putAmount(Mod303Key.CT_S318,ensureActivity(mod,2).getRed())
			,mod -> ensureActivity(mod,2).setRed(mod.getAmount(Mod303Key.CT_S318))
			,false)
		// (1) Actividades en régimen simplificado. Z Índice corrector actividades de temporada
		,CT_S319(Mod303Key.CT_S319,null,null,null, "calculateIndiceTemporada( CT_S3X1 )",null
			,mod -> mod.putAmount(Mod303Key.CT_S319,ensureActivity(mod,2).getInd())
			,mod -> ensureActivity(mod,2).setInd(mod.getAmount(Mod303Key.CT_S319))
			,false)
		// (1) Actividades en régimen simplificado. E Porcentaje de ingreso a cuenta
		,CT_S320(Mod303Key.CT_S320,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S320,ensureActivity(mod,2).getPor())
			,mod -> ensureActivity(mod,2).setPor(mod.getAmount(Mod303Key.CT_S320))
			,false)
		// (1) Actividades en régimen simplificado. F Ingreso a cuenta ( ([C] - [D] ) x [E])
		,CT_S321(Mod303Key.CT_S321,null,null,null,"calculateIngresoCuenta(3, CT_S3X1, CT_S3X2, CT_S317, CT_S318, CT_S319, CT_S320)", null
			,mod -> mod.putAmount(Mod303Key.CT_S321,ensureActivity(mod,2).getIng())
			,mod -> ensureActivity(mod,2).setIng(mod.getAmount(Mod303Key.CT_S321))
			,false)
		// (1) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes
		,CT_S32X(Mod303Key.CT_S32X,null,null,null,"isLastPeriod()?round(CT_S317 * 1 / 100):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S32X,ensureActivity(mod,2).getSopx())
			,mod -> ensureActivity(mod,2).setSopx(mod.getAmount(Mod303Key.CT_S32X))
			,false)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas
		,CT_S32Y(Mod303Key.CT_S32Y,null,null,null,null
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S32Y,ensureActivity(mod,2).getSopy())
			,mod -> ensureActivity(mod,2).setSopy(mod.getAmount(Mod303Key.CT_S32Y))
			,false)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas operaciones corrientes
		,CT_S322(Mod303Key.CT_S322,null,null,null,"isLastPeriod()?(CT_S32X+CT_S32Y):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S322,ensureActivity(mod,2).getSop())
			,mod -> ensureActivity(mod,2).setSop(mod.getAmount(Mod303Key.CT_S322))
			,false)
		// (1) Actividades en régimen simplificado. H Índice corrector de actividades de temporada
		,CT_S323(Mod303Key.CT_S323,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S323,ensureActivity(mod,2).getIct())
			,mod -> ensureActivity(mod,2).setIct(mod.getAmount(Mod303Key.CT_S323))
			,false)
		// (1) Actividades en régimen simplificado. I RESULTADO (( [C] - [D] - [G] ) x [H])
		,CT_S324(Mod303Key.CT_S324,null,null,null,"calculateResultadoAnual( CT_S317, CT_S318, CT_S322, CT_S323)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S324,ensureActivity(mod,2).getRes())
			,mod -> ensureActivity(mod,2).setRes(mod.getAmount(Mod303Key.CT_S324))
			,false)
		// (1) Actividades en régimen simplificado. J Porcentaje cuota mínima
		,CT_S325(Mod303Key.CT_S325,null,null,null,"isLastPeriod()?CT_S325:0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S325,ensureActivity(mod,2).getPcm())
			,mod -> ensureActivity(mod,2).setPcm(mod.getAmount(Mod303Key.CT_S325))
			,false)
		// (1) Actividades en régimen simplificado. K Devolución cuotas soportadas otros países
		,CT_S326(Mod303Key.CT_S326,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S326,ensureActivity(mod,2).getDvc())
			,mod -> ensureActivity(mod,2).setDvc(mod.getAmount(Mod303Key.CT_S326))
			,false)
		// (1) Actividades en régimen simplificado. L Cuota mínima
		,CT_S327(Mod303Key.CT_S327,null,null,null,"calculateCuotaMinima(CT_S317, CT_S318, CT_S325, CT_S326,CT_S323)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S327,ensureActivity(mod,2).getCmn())
			,mod -> ensureActivity(mod,2).setCmn(mod.getAmount(Mod303Key.CT_S327))
			,false)
		// (1) Actividades en régimen simplificado. M Cuota anual derivada régimen simplificado
		,CT_S328(Mod303Key.CT_S328,null,null,null,"isLastPeriod()?((CT_S327>CT_S324)?CT_S327:CT_S324):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S328,ensureActivity(mod,2).getCad())
			,mod -> ensureActivity(mod,2).setCad(mod.getAmount(Mod303Key.CT_S328))
			,false)
		
		// (1) Actividades en régimen simplificado. Epigrafe IAE
		,CT_S401(Mod303Key.CT_S401,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S401,ensureActivity(mod,3).getEpigraph())
			,mod -> ensureActivity(mod,3).setEpigraph(mod.getDescription(Mod303Key.CT_S401))
			,true)
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,CT_S40D(Mod303Key.CT_S40D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S40D,ensureActivity(mod,3).getDescription())
			,mod -> ensureActivity(mod,3).setDescription(mod.getDescription(Mod303Key.CT_S40D))
			,true)
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de ep\u00EDgrafes 691.9 y 722
		,CT_S402(Mod303Key.CT_S402,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S402,ensureActivity(mod,3).getSpecialEpigraph())
			,mod -> ensureActivity(mod,3).setSpecialEpigraph((int) mod.getAmount(Mod303Key.CT_S402))
			,true)
		// (1) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,CT_S4X1(Mod303Key.CT_S4X1,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S4X1,ensureActivity(mod,3).getTem())
			,mod -> ensureActivity(mod,3).setTem((int) mod.getAmount(Mod303Key.CT_S4X1))
			,true)
		// (1) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,CT_S4X2(Mod303Key.CT_S4X2,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S4X2,ensureActivity(mod,3).getDia())
			,mod -> ensureActivity(mod,3).setDia((int) mod.getAmount(Mod303Key.CT_S4X2))
			,true)
		// (1) Actividades en régimen simplificado. Número de empleados al inicio del ejercicio ( o al inicio de la actividad)
		,CT_S4X3(Mod303Key.CT_S4X3,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S4X3,ensureActivity(mod,3).getEmp())
			,mod -> ensureActivity(mod,3).setEmp((int) mod.getAmount(Mod303Key.CT_S4X3))
			,true)
		// (1) Actividades en régimen simplificado. Si realiza la actividad en LORCA
		,CT_S4X4(Mod303Key.CT_S4X4,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S4X4,ensureActivity(mod,3).getLor())
			,mod -> ensureActivity(mod,3).setLor((int) mod.getAmount(Mod303Key.CT_S4X4))
			,true)
			
		,CT_S41D(Mod303Key.CT_S41D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S41D,ensureModule(mod,3,0).getDescription())
			,mod -> ensureModule(mod,3,0).setDescription(mod.getDescription(Mod303Key.CT_S41D))
			,true)
		,CT_S41I(Mod303Key.CT_S41I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S41I,ensureModule(mod,3,0).getValue()) 
			,mod -> ensureModule(mod,3,0).setValue(mod.getAmount(Mod303Key.CT_S41I))
			,true)
		,CT_S41U(Mod303Key.CT_S41U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S41U,ensureModule(mod,3,0).getUnit())
			,mod -> ensureModule(mod,3,0).setUnit(mod.getDescription(Mod303Key.CT_S41U))
			,true)
		,CT_S41F(Mod303Key.CT_S41F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S41F,ensureModule(mod,3,0).getFactor())
			,mod -> ensureModule(mod,3,0).setFactor(mod.getAmount(Mod303Key.CT_S41F))
			,true)
		,CT_S41R(Mod303Key.CT_S41R,null,null,null,"hasActivity(3)?round(CT_S41I*CT_S41F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S41R,ensureModule(mod,3,0).getResult())
			,mod -> ensureModule(mod,3,0).setResult(mod.getAmount(Mod303Key.CT_S41R))
			,true)
		
		,CT_S42D(Mod303Key.CT_S42D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S42D,ensureModule(mod,3,1).getDescription())
			,mod -> ensureModule(mod,3,1).setDescription(mod.getDescription(Mod303Key.CT_S42D))
			,true)
		,CT_S42I(Mod303Key.CT_S42I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S42I,ensureModule(mod,3,1).getValue()) 
			,mod -> ensureModule(mod,3,1).setValue(mod.getAmount(Mod303Key.CT_S42I))
			,true)
		,CT_S42U(Mod303Key.CT_S42U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S42U,ensureModule(mod,3,1).getUnit())
			,mod -> ensureModule(mod,3,1).setUnit(mod.getDescription(Mod303Key.CT_S42U))
			,true)
		,CT_S42F(Mod303Key.CT_S42F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S42F,ensureModule(mod,3,1).getFactor())
			,mod -> ensureModule(mod,3,1).setFactor(mod.getAmount(Mod303Key.CT_S42F))
			,true)
		,CT_S42R(Mod303Key.CT_S42R,null,null,null,"hasActivity(3)?round(CT_S42I*CT_S42F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S42R,ensureModule(mod,3,1).getResult())
			,mod -> ensureModule(mod,3,1).setResult(mod.getAmount(Mod303Key.CT_S42R))
			,true)
		
		,CT_S43D(Mod303Key.CT_S43D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S43D,ensureModule(mod,3,2).getDescription())
			,mod -> ensureModule(mod,3,2).setDescription(mod.getDescription(Mod303Key.CT_S43D))
			,true)
		,CT_S43I(Mod303Key.CT_S43I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S43I,ensureModule(mod,3,2).getValue()) 
			,mod -> ensureModule(mod,3,2).setValue(mod.getAmount(Mod303Key.CT_S43I))
			,true)
		,CT_S43U(Mod303Key.CT_S43U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S43U,ensureModule(mod,3,2).getUnit())
			,mod -> ensureModule(mod,3,2).setUnit(mod.getDescription(Mod303Key.CT_S43U))
			,true)
		,CT_S43F(Mod303Key.CT_S43F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S43F,ensureModule(mod,3,2).getFactor())
			,mod -> ensureModule(mod,3,2).setFactor(mod.getAmount(Mod303Key.CT_S43F))
			,true)
		,CT_S43R(Mod303Key.CT_S43R,null,null,null,"hasActivity(3)?round(CT_S43I*CT_S43F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S43R,ensureModule(mod,3,2).getResult())
			,mod -> ensureModule(mod,3,2).setResult(mod.getAmount(Mod303Key.CT_S43R))
			,true)

		,CT_S44D(Mod303Key.CT_S44D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S44D,ensureModule(mod,3,3).getDescription())
			,mod -> ensureModule(mod,3,3).setDescription(mod.getDescription(Mod303Key.CT_S44D))
			,true)
		,CT_S44I(Mod303Key.CT_S44I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S44I,ensureModule(mod,3,3).getValue()) 
			,mod -> ensureModule(mod,3,3).setValue(mod.getAmount(Mod303Key.CT_S44I))
			,true)
		,CT_S44U(Mod303Key.CT_S44U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S44U,ensureModule(mod,3,3).getUnit())
			,mod -> ensureModule(mod,3,3).setUnit(mod.getDescription(Mod303Key.CT_S44U))
			,true)
		,CT_S44F(Mod303Key.CT_S44F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S44F,ensureModule(mod,3,3).getFactor())
			,mod -> ensureModule(mod,3,3).setFactor(mod.getAmount(Mod303Key.CT_S44F))
			,true)
		,CT_S44R(Mod303Key.CT_S44R,null,null,null,"hasActivity(3)?round(CT_S44I*CT_S44F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S44R,ensureModule(mod,3,3).getResult())
			,mod -> ensureModule(mod,3,3).setResult(mod.getAmount(Mod303Key.CT_S44R))
			,true)

		,CT_S45D(Mod303Key.CT_S45D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S45D,ensureModule(mod,3,4).getDescription())
			,mod -> ensureModule(mod,3,4).setDescription(mod.getDescription(Mod303Key.CT_S45D))
			,true)
		,CT_S45I(Mod303Key.CT_S45I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S45I,ensureModule(mod,3,4).getValue()) 
			,mod -> ensureModule(mod,3,4).setValue(mod.getAmount(Mod303Key.CT_S45I))
			,true)
		,CT_S45U(Mod303Key.CT_S45U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S45U,ensureModule(mod,3,4).getUnit())
			,mod -> ensureModule(mod,3,4).setUnit(mod.getDescription(Mod303Key.CT_S45U))
			,true)
		,CT_S45F(Mod303Key.CT_S45F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S45F,ensureModule(mod,3,4).getFactor())
			,mod -> ensureModule(mod,3,4).setFactor(mod.getAmount(Mod303Key.CT_S45F))
			,true)
		,CT_S45R(Mod303Key.CT_S45R,null,null,null,"hasActivity(3)?round(CT_S45I*CT_S45F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S45R,ensureModule(mod,3,4).getResult())
			,mod -> ensureModule(mod,3,4).setResult(mod.getAmount(Mod303Key.CT_S45R))
			,true)

		,CT_S46D(Mod303Key.CT_S46D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S46D,ensureModule(mod,3,5).getDescription())
			,mod -> ensureModule(mod,3,5).setDescription(mod.getDescription(Mod303Key.CT_S46D))
			,true)
		,CT_S46I(Mod303Key.CT_S46I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S46I,ensureModule(mod,3,5).getValue()) 
			,mod -> ensureModule(mod,3,5).setValue(mod.getAmount(Mod303Key.CT_S46I))
			,true)
		,CT_S46U(Mod303Key.CT_S46U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S46U,ensureModule(mod,3,5).getUnit())
			,mod -> ensureModule(mod,3,5).setUnit(mod.getDescription(Mod303Key.CT_S46U))
			,true)
		,CT_S46F(Mod303Key.CT_S46F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S46F,ensureModule(mod,3,5).getFactor())
			,mod -> ensureModule(mod,3,5).setFactor(mod.getAmount(Mod303Key.CT_S46F))
			,true)
		,CT_S46R(Mod303Key.CT_S46R,null,null,null,"hasActivity(3)?round(CT_S46I*CT_S46F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S46R,ensureModule(mod,3,5).getResult())
			,mod -> ensureModule(mod,3,5).setResult(mod.getAmount(Mod303Key.CT_S46R))
			,true)
		
		,CT_S47D(Mod303Key.CT_S47D,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S47D,ensureModule(mod,3,6).getDescription())
			,mod -> ensureModule(mod,3,6).setDescription(mod.getDescription(Mod303Key.CT_S47D))
			,true)
		,CT_S47I(Mod303Key.CT_S47I,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S47I,ensureModule(mod,3,6).getValue()) 
			,mod -> ensureModule(mod,3,6).setValue(mod.getAmount(Mod303Key.CT_S47I))
			,true)
		,CT_S47U(Mod303Key.CT_S47U,null,null,null,null,null
			,mod -> mod.putDescription(Mod303Key.CT_S47U,ensureModule(mod,3,6).getUnit())
			,mod -> ensureModule(mod,3,6).setUnit(mod.getDescription(Mod303Key.CT_S47U))
			,true)
		,CT_S47F(Mod303Key.CT_S47F,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S47F,ensureModule(mod,3,6).getFactor())
			,mod -> ensureModule(mod,3,6).setFactor(mod.getAmount(Mod303Key.CT_S47F))
			,true)
		,CT_S47R(Mod303Key.CT_S47R,null,null,null,"hasActivity(3)?round(CT_S47I*CT_S47F):0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S47R,ensureModule(mod,3,6).getResult())
			,mod -> ensureModule(mod,3,6).setResult(mod.getAmount(Mod303Key.CT_S47R))
			,true)
 
		// (1) Actividades en régimen simplificado. C Cuota devengada operaciones corrientes
		,CT_S417(Mod303Key.CT_S417,null,null,null,"CT_S41R+CT_S42R+CT_S43R+CT_S44R+CT_S45R+CT_S46R+CT_S47R",null
			,mod -> mod.putAmount(Mod303Key.CT_S417,ensureActivity(mod,3).getDev())
			,mod -> ensureActivity(mod,3).setDev(mod.getAmount(Mod303Key.CT_S417))
			,false)
		// (1) Actividades en régimen simplificado. D Reducciones
		,CT_S418(Mod303Key.CT_S418,null,null,null,"(CT_S4X4 == 1)?round(CT_S417*20/100):CT_S418",null
			,mod -> mod.putAmount(Mod303Key.CT_S418,ensureActivity(mod,3).getRed())
			,mod -> ensureActivity(mod,3).setRed(mod.getAmount(Mod303Key.CT_S418))
			,false)
		// (1) Actividades en régimen simplificado. Z Índice corrector actividades de temporada
		,CT_S419(Mod303Key.CT_S419,null,null,null, "calculateIndiceTemporada( CT_S4X1 )",null
			,mod -> mod.putAmount(Mod303Key.CT_S419,ensureActivity(mod,3).getInd())
			,mod -> ensureActivity(mod,3).setInd(mod.getAmount(Mod303Key.CT_S419))
			,false)
		// (1) Actividades en régimen simplificado. E Porcentaje de ingreso a cuenta
		,CT_S420(Mod303Key.CT_S420,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S420,ensureActivity(mod,3).getPor())
			,mod -> ensureActivity(mod,3).setPor(mod.getAmount(Mod303Key.CT_S420))
			,false)
		// (1) Actividades en régimen simplificado. F Ingreso a cuenta ( ([C] - [D] ) x [E])
		,CT_S421(Mod303Key.CT_S421,null,null,null,"calculateIngresoCuenta(4, CT_S4X1, CT_S4X2, CT_S417, CT_S418, CT_S419, CT_S420)", null
			,mod -> mod.putAmount(Mod303Key.CT_S421,ensureActivity(mod,3).getIng())
			,mod -> ensureActivity(mod,3).setIng(mod.getAmount(Mod303Key.CT_S421))
			,false)
		// (1) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes
		,CT_S42X(Mod303Key.CT_S42X,null,null,null,"isLastPeriod()?round(CT_S417 * 1 / 100):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S42X,ensureActivity(mod,3).getSopx())
			,mod -> ensureActivity(mod,3).setSopx(mod.getAmount(Mod303Key.CT_S42X))
			,false)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas
		,CT_S42Y(Mod303Key.CT_S42Y,null,null,null,null
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S42Y,ensureActivity(mod,3).getSopy())
			,mod -> ensureActivity(mod,3).setSopy(mod.getAmount(Mod303Key.CT_S42Y))
			,false)
		// (1) Actividades en régimen simplificado. G Cuotas soportadas operaciones corrientes
		,CT_S422(Mod303Key.CT_S422,null,null,null,"isLastPeriod()?(CT_S42X+CT_S42Y):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S422,ensureActivity(mod,3).getSop())
			,mod -> ensureActivity(mod,3).setSop(mod.getAmount(Mod303Key.CT_S422))
			,false)
		// (1) Actividades en régimen simplificado. H Índice corrector de actividades de temporada
		,CT_S423(Mod303Key.CT_S423,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S423,ensureActivity(mod,3).getIct())
			,mod -> ensureActivity(mod,3).setIct(mod.getAmount(Mod303Key.CT_S423))
			,false)
		// (1) Actividades en régimen simplificado. I RESULTADO (( [C] - [D] - [G] ) x [H])
		,CT_S424(Mod303Key.CT_S424,null,null,null,"calculateResultadoAnual( CT_S417, CT_S418, CT_S422, CT_S423)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S424,ensureActivity(mod,3).getRes())
			,mod -> ensureActivity(mod,3).setRes(mod.getAmount(Mod303Key.CT_S424))
			,false)
		// (1) Actividades en régimen simplificado. J Porcentaje cuota mínima
		,CT_S425(Mod303Key.CT_S425,null,null,null,"isLastPeriod()?CT_S425:0.0",null
			,mod -> mod.putAmount(Mod303Key.CT_S425,ensureActivity(mod,3).getPcm())
			,mod -> ensureActivity(mod,3).setPcm(mod.getAmount(Mod303Key.CT_S425))
			,false)
		// (1) Actividades en régimen simplificado. K Devolución cuotas soportadas otros países
		,CT_S426(Mod303Key.CT_S426,null,null,null,null,null
			,mod -> mod.putAmount(Mod303Key.CT_S426,ensureActivity(mod,3).getDvc())
			,mod -> ensureActivity(mod,3).setDvc(mod.getAmount(Mod303Key.CT_S426))
			,false)
		// (1) Actividades en régimen simplificado. L Cuota mínima
		,CT_S427(Mod303Key.CT_S427,null,null,null,"calculateCuotaMinima(CT_S417, CT_S418, CT_S425, CT_S426,CT_S423)"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S427,ensureActivity(mod,3).getCmn())
			,mod -> ensureActivity(mod,3).setCmn(mod.getAmount(Mod303Key.CT_S427))
			,false)
		// (1) Actividades en régimen simplificado. M Cuota anual derivada régimen simplificado
		,CT_S428(Mod303Key.CT_S428,null,null,null,"isLastPeriod()?((CT_S427>CT_S424)?CT_S427:CT_S424):0.0"
			,null
			,mod -> mod.putAmount(Mod303Key.CT_S428,ensureActivity(mod,3).getCad())
			,mod -> ensureActivity(mod,3).setCad(mod.getAmount(Mod303Key.CT_S428))
			,false)

		// 47 Suma de ingresos a cuenta del conjunto de actividades
		,CT_S47(Mod303Key.CT_S47,null,null,null,"CT_SA16+CT_SA26+CT_SA36+CT_SA46+CT_S121+CT_S221+CT_S321+CT_S421",null)
		// 48 Suma de cuotas derivadas RS del conjunto de actividades
		,CT_S48(Mod303Key.CT_S48,null,null,null,"isLastPeriod()?(CT_SA18+CT_SA28+CT_SA38+CT_SA48+CT_S128+CT_S228+CT_S328+CT_S428):0.0",null)
		// 49 (A+B) Suma de ingresos a cuenta realizados en el ejercicio
		,CT_S49(Mod303Key.CT_S49)
		// 50 (A+B) Resultado
		,CT_S50(Mod303Key.CT_S50,null,null,null,"isLastPeriod()?(CT_S48-CT_S49):0.0",null)
		// 51 Cuotas devengadas - Adquisiciones intracomunitarias de bienes
		,CT_S51(Mod303Key.CT_S51
			 ,(mod,vat) -> adqIntracomunitariasFilterSimp(vat,mod) && AonMathUtils.isNotZero(vat.getPercentage()) 
			 ,(ctx,mod,vat) -> add(Mod303Key.CT_S51,mod,vat.getQuota())
			 ,null,null,null)
		// 52 Cuotas devengadas - Entregas de activos fijos
		,CT_S52(Mod303Key.CT_S52
			 ,(mod,vat) -> entregasActivosFijosFilterSimp(vat,mod)
			 ,(ctx,mod,vat) -> add(Mod303Key.CT_S52,mod,vat.getQuota())
			 ,null,null,null)
		// 53 Cuotas devengadas - IVA devengado por inversi\u00F3n del sujeto pasivo
		,CT_S53(Mod303Key.CT_S53
			,(mod,vat) -> operacionesISPFilterSimp(vat,mod)
			,(ctx,mod,vat) -> add(Mod303Key.CT_S53,mod,vat.getQuota())
			,null,null,null)
		// 54 Cuotas devengadas - Total cuota resultante
		,CT_S54(Mod303Key.CT_S54,null,null,null,"isLastPeriod()?(CT_S50+CT_S51+CT_S52+CT_S53):(CT_S47+CT_S51+CT_S52+CT_S53)",null)
		// 55 IVA deducible - Adquisici\u00F3n o importaci\u00F3n de activos fijos
		,CT_S55(Mod303Key.CT_S55
			 ,(mod,vat) -> vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) && !vat.isSales() && vat.isInvestment()
			 ,(ctx,mod,vat) -> add(Mod303Key.CT_S55,mod,vat.getQuota())
			 ,null,null,null)
		// 56 IVA deducible - Regularizaci\u00F3n bienes de inversi\u00F3n
		,CT_S56(Mod303Key.CT_S56)
		// 57 IVA deducible - Total IVA deducible
		,CT_S57(Mod303Key.CT_S57,null,null,null,"CT_S55+CT_S56",null)
		// 58 Resultado Régimen Simplificado
		,CT_S58(Mod303Key.CT_S58,null,null,null,"CT_S54-CT_S55",null)
		
		
		// ---------------------------------------------------------------
		// ----------------------------------------- INFORMACIÓN ADICIONAL
		// ---------------------------------------------------------------
		
		// Entregas intracomunitarias de bienes y servicios
		,CT_C59(Mod303Key.CT_C59
			,(mod,vat) -> !vat.isVatSurchargeRegime() && vat.isIntracommunitySales() 
			,(ctx,mod,vat) -> add(Mod303Key.CT_C59,mod,vat.getBase())
			,null,null,null)

		// Exportaciones y operaciones asimiladas
		,CT_C60(Mod303Key.CT_C60
			,(mod,vat) -> !vat.isVatSurchargeRegime() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C60,mod,vat.getBase())
			,null,null,null)
		
		// Operaciones no sujetas o con inversión del sujeto pasivo que originan el derecho a deducción
		,CT_C61(Mod303Key.CT_C61
			,(mod,vat) -> !vat.isVatSurchargeRegime() && vat.isOtherISPSales()  
			,(ctx,mod,vat) -> add(Mod303Key.CT_C61,mod,vat.getBase())
			,null,null,null)
		
		// Importes de las ventas a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran 
		// resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA		
		,CT_C62(Mod303Key.CT_C62
			,null,null, (ctx,mod) -> add( Mod303Key.CT_C62, mod, Mod303DAO.getVatAccrualPaymentOutputBase(ctx,mod)),null,null)
		
		,CT_C63(Mod303Key.CT_C63
			,null,null, (ctx,mod) -> {
			double quota = Mod303DAO.getVatAccrualPaymentOutputQuota(ctx,mod);
			add( Mod303Key.CT_C63, mod, quota );
//			double a = AonMathUtils.isZero(quota)? mod.getAmount(Mod303Key.CT_A07):(1.0);
//			add( Mod303Key.CT_A07, mod, a); 
			}
		,null,null)
		
		// Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el 
		// régimen especial del criterio de caja
		,CT_C74(Mod303Key.CT_C74,null,null, (ctx,mod) -> add( Mod303Key.CT_C74, mod, Mod303DAO.getVatAccrualPaymentInputBase(ctx,mod)),null,null)
		,CT_C75(Mod303Key.CT_C75,null,null, (ctx,mod) -> {
			double quota = Mod303DAO.getVatAccrualPaymentInputQuota(ctx,mod);
			add( Mod303Key.CT_C75, mod, quota);
			add( Mod303Key.CT_A08, mod, AonMathUtils.isZero(quota)?(0.0):(1.0));
			}
		,null,null)

		// Regularización cuotas art. 80.Cinco.5a LIVA
		,CT_C76(Mod303Key.CT_C76)
		
		// Suma de resultados
		,CT_C64(Mod303Key.CT_C64,null,null,null,"CT_C46+CT_S58+CT_C76",null) // TODO Sumar el resultado del regimen simplificado, si procede.
		
		// % Atribuible a la Administración del Estado
		,CT_C65(Mod303Key.CT_C65,null,null, (ctx,mod) -> add(Mod303Key.CT_C65,mod,100.0),null,null)
		
		// Cuota atribuible a la Administración del Estado
		,CT_C66(Mod303Key.CT_C66,null,null,null,"round(CT_C64*CT_C65/100)"
			,"<li><b>Resultado:</b> @{CT_C65} % de @{CT_C64} igual <b>@{CT_C66}</b></li>")
				
		// IVA a la importación liquidado por la Aduana pendiente de ingreso
		,CT_C77(Mod303Key.CT_C77)
		
		// Cuotas a compensar de periodos anteriores
		,CT_C67(Mod303Key.CT_C67,null,null,
			(ctx,mod) -> {
				if (mod.isFirstPeriod()) {
					// Primer periodo. Se busca la cuota a compensar del último periodo del ejercicio anterior.
					add( Mod303Key.CT_C67, mod, 
							Mod303DAO.getMod303s( ctx,ctx.getDomainId() )
							.filter(m303 -> m303.getYear() ==  (mod.getYear() - 1) )
							.filter(m303 -> m303.isLastPeriod() ) 
							.filter(fm ->  fm.isToCompensate())
							.mapToDouble(fm -> AonMathUtils.round(fm.getAmount(Mod303Key.CT_C71) * (-1)))
							.findFirst()
							.orElse(0.0));						
				} else {
					// Resto de periodos. Se busca la cuota a compensar del anterior periodo..
					add( Mod303Key.CT_C67, mod, 
							Mod303DAO.getMod303s( ctx,ctx.getDomainId() )
							.filter(m303 -> m303.getYear() == mod.getYear() )
							.filter(m303 -> m303.getPeriod().ordinal() == (mod.getPeriod().ordinal() - 1) )  
							.filter(fm ->  fm.isToCompensate())
							.mapToDouble(fm -> AonMathUtils.round(fm.getAmount(Mod303Key.CT_C71) * (-1)))
							.findFirst()
							.orElse(0.0));						
				}
			}
			,null
			,
			 "@code{c71Key='"+ Mod303Key.CT_C71.getValue() +"';}"
			+"@if{ mod.isFirstPeriod() }"
				+"<li>Declaraciones del \u00FAltimo periodo del ejercicio anterior:<ul style=\"padding-left: 20px;\">" 
				+"@foreach{fm : models}"
					+"@if{ fm.getYear() == (mod.getYear() - 1) && fm.isLastPeriod() && fm.getAdministration() == mod.getAdministration() }"
						+"<li>Resultado @{fm.getPeriod().getName()}@{fm.isComplementary()?' (C) ':'     '}:	Casilla [071] --> @{fm.getAmount(c71Key)}</li>"
					+"@end{}"
				+"@end{}"
				+"</ul></li>"
			+"@else{}"
				+"<li>Declaraciones del periodo anterior:<ul style=\"padding-left: 20px;\">" 
				+"@foreach{fm : lastPeriodModels}" 
					+"@if{ fm.getPeriod().ordinal() == (mod.getPeriod().ordinal() - 1) }"
						+"<li>Resultado @{fm.getPeriod().getName()}@{fm.isComplementary()?' (C) ':'     '}:	Casilla [071] --> @{fm.getAmount(c71Key)}</li>"
					+"@end{}"
				+"@end{}"
				+"</ul></li>"
			+"@end{}"
			+"<li>Resultado (Cuotas a compensar de periodos anteriores): <b>@{CT_C67}</b></li>"
		)
		
		// Exclusivamente para sujetos pasivos que tributan conjuntamente a la Administración del Estado 
		// y a las Diputaciones Forales. Resultado de la regularización anual.
		,CT_C68(Mod303Key.CT_C68)
		
		// Resultado
		,CT_C69(Mod303Key.CT_C69,null,null,null,"CT_C66+CT_C77-CT_C67+CT_C68",null) 
		
		// A deducir (exclusivamente en caso de autoliquidación complementaria)
		,CT_C70(Mod303Key.CT_C70,null,null,
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
		,CT_C71(Mod303Key.CT_C71,null,null,null,"CT_C69-CT_C70",null)
		
		
		,CT_U1D(Mod303Key.CT_U1D)
		,CT_U1C(Mod303Key.CT_U1C)
		,CT_U1E(Mod303Key.CT_U1E)
		,CT_U2D(Mod303Key.CT_U2D)
		,CT_U2C(Mod303Key.CT_U2C)
		,CT_U2E(Mod303Key.CT_U2E)
		,CT_U3D(Mod303Key.CT_U3D)
		,CT_U3C(Mod303Key.CT_U3C)
		,CT_U3E(Mod303Key.CT_U3E)
		,CT_U4D(Mod303Key.CT_U4D)
		,CT_U4C(Mod303Key.CT_U4C)
		,CT_U4E(Mod303Key.CT_U4E)
		,CT_U5D(Mod303Key.CT_U5D)
		,CT_U5C(Mod303Key.CT_U5C)
		,CT_U5E(Mod303Key.CT_U5E)
		,CT_U13(Mod303Key.CT_U13)
		,CT_C89(Mod303Key.CT_C89)
		,CT_C90(Mod303Key.CT_C90)
		,CT_C91(Mod303Key.CT_C91)
		,CT_C92(Mod303Key.CT_C92)
		,CT_C80(Mod303Key.CT_C80)
		,CT_C81(Mod303Key.CT_C81)
		,CT_C82(Mod303Key.CT_C82)
		,CT_C93(Mod303Key.CT_C93)
		,CT_C94(Mod303Key.CT_C94)
		,CT_C83(Mod303Key.CT_C83)
		,CT_C84(Mod303Key.CT_C84)
		,CT_C85(Mod303Key.CT_C85)
		,CT_C86(Mod303Key.CT_C86)
		,CT_C95(Mod303Key.CT_C95)
		,CT_C96(Mod303Key.CT_C96)
		,CT_C97(Mod303Key.CT_C97)
		,CT_C98(Mod303Key.CT_C98)
		,CT_C79(Mod303Key.CT_C79)
		,CT_C99(Mod303Key.CT_C99)
		,CT_C87(Mod303Key.CT_C87)
		,CT_C88(Mod303Key.CT_C88,null,null,null,"isLastPeriod()?(CT_C80+CT_C81+CT_C93+CT_C94+CT_C83+CT_C84+CT_C85+CT_C86+CT_C95+CT_C96+CT_C97+CT_C98-CT_C79-CT_C99):(0.0)",null)
		
		,CT_P1C(Mod303Key.CT_P1C)
		,CT_P1I(Mod303Key.CT_P1I)
		,CT_P1D(Mod303Key.CT_P1D)
		,CT_P1T(Mod303Key.CT_P1T)
		,CT_P1P(Mod303Key.CT_P1P)
		
		,CT_P2C(Mod303Key.CT_P2C)
		,CT_P2I(Mod303Key.CT_P2I)
		,CT_P2D(Mod303Key.CT_P2D)
		,CT_P2T(Mod303Key.CT_P2T)
		,CT_P2P(Mod303Key.CT_P2P)

		,CT_P3C(Mod303Key.CT_P3C)
		,CT_P3I(Mod303Key.CT_P3I)
		,CT_P3D(Mod303Key.CT_P3D)
		,CT_P3T(Mod303Key.CT_P3T)
		,CT_P3P(Mod303Key.CT_P3P)

		,CT_P4C(Mod303Key.CT_P4C)
		,CT_P4I(Mod303Key.CT_P4I)
		,CT_P4D(Mod303Key.CT_P4D)
		,CT_P4T(Mod303Key.CT_P4T)
		,CT_P4P(Mod303Key.CT_P4P)

		,CT_P5C(Mod303Key.CT_P5C)
		,CT_P5I(Mod303Key.CT_P5I)
		,CT_P5D(Mod303Key.CT_P5D)
		,CT_P5T(Mod303Key.CT_P5T)
		,CT_P5P(Mod303Key.CT_P5P)
;
		
		private Mod303Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;
		private ISimplifiedRegimeActivityPopulator populator;
		private ISimplifiedRegimeActivityFiller filler;
		private boolean copyable;
		
		
		private Mod303KeyDAO(Mod303Key key) {
			this(key,null,null,null,null,null);			
		}
		private Mod303KeyDAO(Mod303Key key
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueFirstIntializer firstInitializer
				, String expression
				, String template) {
			this(key,acceptValue,initializer,firstInitializer,expression,template, null, null, false);
		}
		
		private Mod303KeyDAO(Mod303Key key
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueFirstIntializer firstInitializer
				, String expression
				, String template
				, ISimplifiedRegimeActivityPopulator populator
				, ISimplifiedRegimeActivityFiller filler
				, boolean copyable) {
			this.key = key;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.firstInitializer = firstInitializer;
			this.expression =  expression;
			this.template =  template;
			this.populator = populator;
			this.filler = filler;
			this.copyable = copyable;
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
		public boolean isCopyable() {
			return copyable;
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
		public void populate(Mod303 mod) {
			if (populator != null) populator.populate(mod);
		}
		public void fill(Mod303 mod) {
			if (filler != null) filler.fill(mod);
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
	@Override
	public boolean hasSimplifiedRegime() {
		return true;
	}
	
	//	-----------------------------------------------------------------------	
	//	--------------------------------------------------------------- FILTROS	
	//	-----------------------------------------------------------------------
	private static boolean isCommonNationalSales(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
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
	private static boolean adqIntracomunitariasFilter(VatContext vat, Mod303 mod) {
		return !vat.isVatSurchargeRegime() && (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses());
	}
	
	private static boolean adqIntracomunitariasFilterGene(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && adqIntracomunitariasFilter(vat,mod);
	}
	private static boolean adqIntracomunitariasFilterSimp(VatContext vat, Mod303 mod) {
		return vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) 
				&& vat.isIntracommunityPurchase()
				&& !vat.isService();
	}
	
	private static boolean entregasActivosFijosFilterSimp(VatContext vat, Mod303 mod) {
		return vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) && vat.isSales() && vat.isInvestment();
	}

	private static boolean operacionesISPFilter(VatContext vat, Mod303 mod) {
		return !vat.isVatSurchargeRegime()
			&&  (vat.isOtherISPPurchase() 
			  || vat.isOtherISPExpenses() 
			  || vat.isExtracommunityExpenses() 
			  || vat.isCanCeuMelExpenses()
			  || (vat.isExtracommunityPurchase() && vat.isService()) 
			  || (vat.isCanCeuMelPurchase() && vat.isService())
			);
	}

	private static boolean operacionesISPFilterSimp(VatContext vat, Mod303 mod) {
		return vat.isVatSimplifiedRegime(mod.getDefaultVATRegime()) 
			&& (operacionesISPFilter(vat,mod)
			|| vat.isIntracommunityExpenses()
			|| (vat.isService() && vat.isIntracommunityPurchase()));
	}

	private static boolean operacionesISPFilterGene(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) 
				&& operacionesISPFilter(vat,mod);
	}
	private static boolean modificacionBasesYCuotasFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
			&& vat.isRectification() 
			&& (vat.isNationalSales() || adqIntracomunitariasFilterGene(vat, mod) || operacionesISPFilterGene(vat, mod));		
	}

	private static boolean operacionesInterioresCorrientesFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
			&& !vat.isInvestment() && !vat.isRectification() && !vat.isFarmerRegime()
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilterGene(vat, mod))
			;
			
	}

	private static boolean operacionesInterioresInversionFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
			&& vat.isInvestment() && !vat.isRectification() && !vat.isFarmerRegime() 
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilterGene(vat, mod));
	}
	private static boolean importacionesCorrientesFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
			&& !vat.isInvestment() && !vat.isRectification()
			&& !vat.isService()
			&& (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase());
	}
	private static boolean importacionesInversionFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
			&& vat.isInvestment() && !vat.isRectification()  
			&& !vat.isService()
			&& (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase());
	}
	private static boolean adqIntracomunitariasCorrientesFilter(VatContext vat, Mod303 mod) {
		return !vat.isInvestment() && !vat.isRectification() && adqIntracomunitariasFilterGene(vat, mod);
	}
	private static boolean adqIntracomunitariasInversionFilter(VatContext vat, Mod303 mod) {
		return vat.isInvestment() && !vat.isRectification() && adqIntracomunitariasFilterGene(vat, mod);
	}
	private static boolean rectificaciónDeduccionesFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
			&& vat.isRectification() && (vat.isPurchase() || vat.isExpenses()); 
	}
	private static boolean compensacionesRegAgrarioFilter(VatContext vat, Mod303 mod) {
		return vat.isVatGeneralRegime(mod.getDefaultVATRegime()) && !vat.isVatSurchargeRegime()
			&& vat.isFarmerRegime() && !vat.isRectification() && vat.isNationalPurchase();		
	}
	private static Mod303ActivityFarmer ensureFarmerActivity(Mod303 mod, int idx) {
		if (idx < 0 || idx > 3) throw new IllegalArgumentException("0, 1, 3, ó 3");
		
		if (mod.getActivityFarmerList() == null) {
			mod.setActivityFarmerList( new LinkedList<Mod303ActivityFarmer>());
		}
		for (int i = 0; i <= idx; i++) {
			if ( idx == mod.getActivityFarmerList().size() ) {
				mod.getActivityFarmerList().add( new Mod303ActivityFarmer() );
			}
		}
		return mod.getActivityFarmerList().get(idx);
	}
	private static Mod303Activity ensureActivity(Mod303 mod, int idx) {
		if (idx < 0 || idx > 3) throw new IllegalArgumentException("0, 1, 3, ó 3");
		
		if (mod.getActivityList() == null) {
			mod.setActivityList( new LinkedList<Mod303Activity>());
		}
		for (int i = 0; i <= idx; i++) {
			if ( idx == mod.getActivityList().size() ) {
				mod.getActivityList().add( new Mod303Activity() );
			}
		}
		return mod.getActivityList().get(idx);
	}
	
	private static Mod303ActivityModule ensureModule(Mod303 mod, int act, int idx) {
		if (idx < 0 || idx > 6) throw new IllegalArgumentException("0, 1, 2, 3, 4, 5, ó 6");
		Mod303Activity a = ensureActivity(mod, act);
		if (a.getModules() == null) {
			a.setModules( new LinkedList<Mod303ActivityModule>());
		}
		for (int i = 0; i <= idx; i++) {
			if ( idx == a.getModules().size() ) {
				a.getModules().add( new Mod303ActivityModule() );
			}
		}
		return a.getModules().get(idx);
	}
	
	//	-----------------------------------------------------------------------
	//	----------------------------------------------- REGIMEN SIMPLIFICADO --
	//	-----------------------------------------------------------------------
	@Override
	public void initializeSimplifiedRegime(AONContext ctx, Mod303 mod303) {
		Mod303 previous = (Mod303) Mod303DAO.getMod303s(ctx, mod303.getDomain()).findFirst().orElse(null);
		if (previous != null) {
			previous = Mod303DAO.getMod303(ctx, previous.getId());
			if (previous.getAmount(Mod303Key.CT_A02)  == 0 || previous.getAmount(Mod303Key.CT_A02)  == 1) {
				mod303.putAmount(Mod303Key.CT_A02, previous.getAmount(Mod303Key.CT_A02));
				for (Mod303KeyDAO key : Mod303KeyDAO.values()) {
					if ( key.isCopyable() ) {
						FiscalModelDetail prev = previous.ensureDetail(key.getKey());
						FiscalModelDetail det = mod303.ensureDetail(key.getKey());
						det.setAmount(prev.getAmount());
						det.setDescription(prev.getDescription());
						
						if (key == Mod303KeyDAO.CT_S101 && AonStringUtils.isNotBlank( mod303.getDescription(Mod303Key.CT_S101))) {
							IEpigraph epi = getEpigraph( mod303,  Mod303Key.CT_S101);
							if (epi != null) {
								if (mod303.isLastPeriod()) {
									mod303.ensureDetail(Mod303Key.CT_S125).setAmount(epi.getPorcMin());
								} else {
									mod303.ensureDetail(Mod303Key.CT_S120).setAmount(epi.getVatPorc());
								}
							}
						}
						if (key == Mod303KeyDAO.CT_S201 && AonStringUtils.isNotBlank( mod303.getDescription(Mod303Key.CT_S201))) {
							IEpigraph epi = getEpigraph( mod303,  Mod303Key.CT_S201);
							if (epi != null) {
								if (mod303.isLastPeriod()) {
									mod303.ensureDetail(Mod303Key.CT_S225).setAmount(epi.getPorcMin());
								} else {
									mod303.ensureDetail(Mod303Key.CT_S220).setAmount(epi.getVatPorc());
								}
							}
						}
						if (key == Mod303KeyDAO.CT_S301 && AonStringUtils.isNotBlank( mod303.getDescription(Mod303Key.CT_S301))) {
							IEpigraph epi = getEpigraph( mod303,  Mod303Key.CT_S301);
							if (epi != null) {
								if (mod303.isLastPeriod()) {
									mod303.ensureDetail(Mod303Key.CT_S325).setAmount(epi.getPorcMin());
								} else {
									mod303.ensureDetail(Mod303Key.CT_S320).setAmount(epi.getVatPorc());
								}
							}
						}
						if (key == Mod303KeyDAO.CT_S401 && AonStringUtils.isNotBlank( mod303.getDescription(Mod303Key.CT_S401))) {
							IEpigraph epi = getEpigraph( mod303,  Mod303Key.CT_S401);
							if (epi != null) {
								if (mod303.isLastPeriod()) {
									mod303.ensureDetail(Mod303Key.CT_S425).setAmount(epi.getPorcMin());
								} else {
									mod303.ensureDetail(Mod303Key.CT_S420).setAmount(epi.getVatPorc());
								}
							}
						}
					}
				}
			}
			fillSimplifiedRegime(mod303);
		}
	}

	private IEpigraph getEpigraph(Mod303 mod303, Mod303Key key) {
		if (mod303.getYear() < 2018) {
			com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph.getEpigraph(mod303.getDescription(key));
		} 
		return com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018.Epigraph.getEpigraph(mod303.getDescription(key)); 
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
	
	@Override
	public void specificInitialization(Mod303 mod303) {
		super.specificInitialization(mod303);
		if (mod303.getDefaultVATRegime() == VATRegime.SIMPLIFIED) {
			if (AonMathUtils.isZero( mod303.getAmount(Mod303Key.CT_C46))) {
				mod303.putAmount(Mod303Key.CT_A02,0);	// Sólo Reg. Simplificado.
			} else {
				mod303.putAmount(Mod303Key.CT_A02,1);	// Reg. Simplificado. y General
			}
		} else {
			if (AonMathUtils.isNotZero( mod303.getAmount(Mod303Key.CT_S58))) {
				mod303.putAmount(Mod303Key.CT_A02,1);	// Reg. Simplificado. y General
			} else {
				mod303.putAmount(Mod303Key.CT_A02,2);	// Sólo Reg. Simplificado. y General
			}
		}
	}

	public static void main(String[] args) {
		for (Mod303KeyDAO key : Mod303KeyDAO.values()) {
			if ( key.isCopyable() ) {
				System.out.println( key.getKey().toString() +"\t"+ key.getKey().getDescription() );
			}
		}
		
	}
	
}
