package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390HF.Mod390HFDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class Mod303BIZKAIA2022Declaration extends Mod303BIZKAIA {
	
	protected Mod303BIZKAIA2022Declaration() {
				
	}
	
	public static final double PERCENT1 = 4.0;
	public static final double PERCENT2 = 10.0;
	public static final double PERCENT3 = 21.0;
	public static final double SURCHARGE_PERCENT1 = 0.5;
	public static final double SURCHARGE_PERCENT2 = 1.4;
	public static final double SURCHARGE_PERCENT3 = 5.2;
	public static final double SURCHARGE_PERCENT4 = 1.75;
	
	public static boolean accept(Mod303 mod) {
		return  mod.isBizkaia() && mod.getYear() >= 2022 && mod.getPeriod() != Period.T4;
	}
	
	private static final Mod303Key[] PRORATE_KEYS = new Mod303Key[]{
		 Mod303Key.BZ_C024,Mod303Key.BZ_C025,Mod303Key.BZ_C026,Mod303Key.BZ_C027
		,Mod303Key.BZ_C052
		,Mod303Key.BZ_C055
		,Mod303Key.BZ_C058
		,Mod303Key.BZ_C061
		,Mod303Key.BZ_C064
		,Mod303Key.BZ_C070
		,Mod303Key.BZ_C073
		,Mod303Key.BZ_C076
		,Mod303Key.BZ_C079
		,Mod303Key.BZ_C085
		,Mod303Key.BZ_C088
		,Mod303Key.BZ_C091
		,Mod303Key.BZ_C094
	};
	
	private enum Mod303KeyDAO implements IMod303KeyDAO {
		 BZ_C185_1	(Mod303Key.BZ_C185_1)
		,BZ_C185_2	(Mod303Key.BZ_C185_2)
		,BZ_C186	(Mod303Key.BZ_C186,null,null,(ctx,mod) -> add(Mod303Key.BZ_C186,mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment()?1:0),null,null)
		,BZ_C187	(Mod303Key.BZ_C187)		// Compras Criterio de caja. Se incializa en la casilla 202.
		
		,CM_002(Mod303Key.CM_002,null,null,(ctx,mod) -> add(Mod303Key.CM_002,mod,(
				 AonStringUtils.equals(AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY),AonStringUtils.ONE))?1:0),null,null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al primer tipo.
		,BZ_C003(Mod303Key.BZ_C003
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C003,mod,vat.getBase())
			,null,null,null)
		,BZ_X003(Mod303Key.BZ_X003,null,null,(ctx,mod) -> add(Mod303Key.BZ_X003,mod,PERCENT1),null,null)
		,BZ_C004(Mod303Key.BZ_C004
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C004,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al segundo tipo.
		,BZ_C005	(Mod303Key.BZ_C005
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C005,mod,vat.getBase())
			,null,null,null)
		,BZ_X005	(Mod303Key.BZ_X005,null,null,(ctx,mod) -> add(Mod303Key.BZ_X005,mod,PERCENT2),null,null)
		,BZ_C006	(Mod303Key.BZ_C006
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C006,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al tercer tipo.
		,BZ_C007	(Mod303Key.BZ_C007
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C007,mod,vat.getBase())
			,null,null,null)
		,BZ_X007	(Mod303Key.BZ_X007,null,null,(ctx,mod) -> add(Mod303Key.BZ_X007,mod,PERCENT3),null,null)
		,BZ_C008	(Mod303Key.BZ_C008
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C008,mod,vat.getQuota())
			,null,null,null)

		// Recargo equivalencia al primer  tipo.
		,BZ_C009	(Mod303Key.BZ_C009
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C009,mod,vat.getBase())
			,null,null,null)
		,BZ_X009	(Mod303Key.BZ_X009,null,null,(ctx,mod) -> add(Mod303Key.BZ_X009,mod,SURCHARGE_PERCENT1),null,null)
		,BZ_C010	(Mod303Key.BZ_C010
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C010,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al segundo tipo.
		,BZ_C011	(Mod303Key.BZ_C011
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C011,mod,vat.getBase())
			,null,null,null)
		,BZ_X011	(Mod303Key.BZ_X011,null,null,(ctx,mod) -> add(Mod303Key.BZ_X011,mod,SURCHARGE_PERCENT2),null,null)
		,BZ_C012	(Mod303Key.BZ_C012
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C012,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al tercer tipo.
		,BZ_C013	(Mod303Key.BZ_C013
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C013,mod,vat.getBase())
			,null,null,null)
		,BZ_X013	(Mod303Key.BZ_X013,null,null,(ctx,mod) -> add(Mod303Key.BZ_X013,mod,SURCHARGE_PERCENT3),null,null)
		,BZ_C014	(Mod303Key.BZ_C014
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C014,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al cuarto tipo.
		,BZ_C015	(Mod303Key.BZ_C015
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C015,mod,vat.getBase())
			,null,null,null)
		,BZ_X015	(Mod303Key.BZ_X015,null,null,(ctx,mod) -> add(Mod303Key.BZ_X015,mod,SURCHARGE_PERCENT4),null,null)
		,BZ_C016	(Mod303Key.BZ_C016
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C016,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias. Base y cuota.
		,BZ_C017	(Mod303Key.BZ_C017
			,(mod,vat) -> adqIntracomunitariasDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C017,mod,vat.getBase())
			,null,null,null)
		,BZ_C018	(Mod303Key.BZ_C018
			,(mod,vat) -> adqIntracomunitariasDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C018,mod,vat.getQuota())
			,null,null,null)
		
		// IVAS devengado por inversión del sujeto pasivo. Base y cuota
		,BZ_C019	(Mod303Key.BZ_C019
			,(mod,vat) -> operacionesISPDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C019,mod,vat.getBase())
			,null,null,null)
		,BZ_C020	(Mod303Key.BZ_C020
			,(mod,vat) -> operacionesISPDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C020,mod,vat.getQuota())
			,null,null,null)	
		
		
		// Modificación bases y cuotas
		,BZ_C021	(Mod303Key.BZ_C021
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C021,mod,vat.getBase())
			,null,null,null)
		,BZ_C022	(Mod303Key.BZ_C022
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C022,mod,(vat.getQuota() + vat.getSurchargeQuota()))
			,null,null,null)

		// Modificación de bases y cuotas, artículo 80.3 y 80.4 NFIVA
		,BZ_C046	(Mod303Key.BZ_C046)
		,BZ_C047	(Mod303Key.BZ_C047)
		
		// Total cuota devengada
		,BZ_C023	(Mod303Key.BZ_C023,null,null,null,"BZ_C004+BZ_C006+BZ_C008+BZ_C010+BZ_C012+BZ_C014+BZ_C016+BZ_C018+BZ_C020+BZ_C022+BZ_C047",null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------
		
		// IVA deducible en operaciones interiores
		,BZ_C024	(Mod303Key.BZ_C024
			,(mod,vat) -> operacionesInterioresFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C024,mod,vat)
			,null,null,null)
		
		// IVA deducible en importaciones
		,BZ_C025	(Mod303Key.BZ_C025
			,(mod,vat) -> importacionesFilter(vat,mod)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C025,mod,vat)
			,null,null,null)
		
		// IVA deducible en adquisiciones intracomunitarias
		,BZ_C026	(Mod303Key.BZ_C026
			,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C026,mod,vat)
			,null,null,null)
		
		// Compensaciones Régimen Especial A.G. y P .
		,BZ_C027	(Mod303Key.BZ_C027
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C027,mod,vat)
			,null,null,null)
				
		// Regularización Inversiones
		,BZ_C028	(Mod303Key.BZ_C028)
		
		// Total a deducir
		,BZ_C030	(Mod303Key.BZ_C030,null,null,null,"BZ_C024+BZ_C025+BZ_C026+BZ_C027+BZ_C028",null)

		// Diferencia
		,BZ_C031	(Mod303Key.BZ_C031,null,null,null,"BZ_C023-BZ_C030",null)
		
		// Regularización de cuotas (art.80.cinco.5a Norma Foral del IVA)
		,BZ_C045	(Mod303Key.BZ_C045)
		
		// Porcentaje de tributaci\u00F3n en Bizkaia
		,BZ_C032	(Mod303Key.BZ_C032,null,null,(ctx,mod) -> add(Mod303Key.BZ_C032,mod,100.0),null,null)
		
		// Cuota atribuible a Bizkaia
		,BZ_C033	(Mod303Key.BZ_C033,null,null,null,"(BZ_C031+BZ_C045)*BZ_C032/100",null)
		
		// Cuota a compensar de periodos anteriores
		,BZ_C034	(Mod303Key.BZ_C034,null,null, (ctx,mod) -> add( Mod303Key.BZ_C034, mod, getPendingCompesateAmounts( ctx, mod )),null,null)
		// Resultado de la regularizaci\u00F3n anual
		,BZ_C035	(Mod303Key.BZ_C035)
		
		// Resultado
		,BZ_C036	(Mod303Key.BZ_C036,null,null,null,"BZ_C033-BZ_C034+BZ_C035",null)   
		
		
		// A compensar
		,BZ_C038	(Mod303Key.BZ_C038,null,null,null,"isToCompensate()?round(BZ_C036*-1):0.0",null)
		
		// A devolver
		,BZ_C039	(Mod303Key.BZ_C039,null,null,null,"isToPayback()?round(BZ_C036*-1):0.0",null)
		// A ingresar
		,BZ_C040	(Mod303Key.BZ_C040,null,null,null,"isToDeposit()?BZ_C036:0.0",null)
		
		// Cumplimentar s\u00F3lo en caso de que se trate de una autoliquidaci\u00F3n complementaria: ingresado anteriormente
		,BZ_C041	(Mod303Key.BZ_C041,null,null,
			(ctx,mod) -> {
				if (mod.isComplementary()) {
					add( Mod303Key.BZ_C041, mod, 
						Mod303DAO.getSamePeriodEffectiveModels(ctx, mod)
							.filter(Mod303::isToDeposit)
							.mapToDouble(Mod303::getDeclarationResult)
							.sum());						
				}
			} 
			,null
			,null
		)
		// Cumplimentar s\u00F3lo en caso de que se trate de una autoliquidaci\u00F3n complementaria: devuelto anteriormente
		,BZ_C042	(Mod303Key.BZ_C042,null,null,
			(ctx,mod) -> {
				if (mod.isComplementary()) {
					add( Mod303Key.BZ_C042, mod, 
						Mod303DAO.getSamePeriodEffectiveModels(ctx, mod)
							.filter(m -> m.isToPayback())
							.mapToDouble(Mod303::getDeclarationResult)
							.map(x -> AonMathUtils.round( x * (-1) ) )
							.sum());						
				}
			} 
			,null
			,null
		)
		// Total deuda tributaria
		,BZ_C043	(Mod303Key.BZ_C043,null,null,null,"BZ_C036-BZ_C041+BZ_C042",null)
		
		// Exclusivamente para sujetos pasivos acogidos al régimen especial del criterio de caja y para 
		// destinatarios/as de operaciones afectadas por el mismo
		
		// Importes de las entregas de bienes y prestaciones de servicios a las que habiéndoles
		// sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas
		// conforme a la regla general de devengo contenida en el artículo 75 NFIVA
		,BZ_C200	(Mod303Key.BZ_C200 ,(mod, vat) -> vat.isSales() && vat.isVatAccrualRegime()
			, null, null, null, null)
		,BZ_C201	(Mod303Key.BZ_C201 ,(mod, vat) -> vat.isSales() && vat.isVatAccrualRegime()
				, null, null, null, null)		
		// Importes de las adquisiciones de bienes y servicios a las que sea aplicable o afecte el
		// régimen especial del criterio de caja
		,BZ_C202	(Mod303Key.BZ_C202, (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime()
				, null, null, null, null)
		,BZ_C203	(Mod303Key.BZ_C203, (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime()
				, null, null, null, null)

		// ---------------------------------------------------------------
		// ----------------------------------------- INFORMACION ADICIONAL
		// ---------------------------------------------------------------

		// Compras de bienes corrientes
		,BZ_C050	(Mod303Key.BZ_C050
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent1(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C050,mod,vat.getBase())
			,null,null,null)
		,BZ_X050	(Mod303Key.BZ_X050,null,null,(ctx,mod) -> add(Mod303Key.BZ_X050,mod,PERCENT1),null,null)
		,BZ_C051	(Mod303Key.BZ_C051
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent1(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C051,mod,vat.getQuota())
			,null,null,null)
		,BZ_C052	(Mod303Key.BZ_C052
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent1(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C052,mod,vat)
			,null,null,null)
		
		,BZ_C053	(Mod303Key.BZ_C053
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent2(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C053,mod,vat.getBase())
			,null,null,null)
		,BZ_X053	(Mod303Key.BZ_X053,null,null,(ctx,mod) -> add(Mod303Key.BZ_X053,mod,PERCENT2),null,null)
		,BZ_C054	(Mod303Key.BZ_C054
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent2(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C054,mod,vat.getQuota())
			,null,null,null)
		,BZ_C055	(Mod303Key.BZ_C055
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent2(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C055,mod,vat)
			,null,null,null)
		
		,BZ_C056	(Mod303Key.BZ_C056
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent3(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C056,mod,vat.getBase())
			,null,null,null)
		,BZ_X056	(Mod303Key.BZ_X056,null,null,(ctx,mod) -> add(Mod303Key.BZ_X056,mod,PERCENT3),null,null)
		,BZ_C057	(Mod303Key.BZ_C057
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent3(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C057,mod,vat.getQuota())
			,null,null,null)
		,BZ_C058	(Mod303Key.BZ_C058
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent3(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C058,mod,vat)
			,null,null,null)
		
		,BZ_C059	(Mod303Key.BZ_C059
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isPurchase() && !vat.isInvestment() && vat.isFarmerRegime()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C059,mod,vat.getBase())
			,null,null,null)
		,BZ_C060	(Mod303Key.BZ_C060
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isPurchase() && !vat.isInvestment() && vat.isFarmerRegime()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C060,mod,vat.getQuota())
			,null,null,null)
		,BZ_C061	(Mod303Key.BZ_C061
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isPurchase() && !vat.isInvestment() && vat.isFarmerRegime()
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C061,mod,vat)
			,null,null,null)
		
		,BZ_C062	(Mod303Key.BZ_C062
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasNoPercent(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C062,mod,vat.getBase())
			,null,null,null)
		,BZ_C063	(Mod303Key.BZ_C063
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasNoPercent(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C063,mod,vat.getQuota())
			,null,null,null)
		,BZ_C064	(Mod303Key.BZ_C064
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasNoPercent(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C064,mod,vat)
			,null,null,null)
		
		,BZ_C065	(Mod303Key.BZ_C065,null,null,null,"BZ_C050+BZ_C053+BZ_C056+BZ_C059+BZ_C062",null)
		,BZ_C066	(Mod303Key.BZ_C066,null,null,null,"BZ_C051+BZ_C054+BZ_C057+BZ_C060+BZ_C063",null)
		,BZ_C067	(Mod303Key.BZ_C067,null,null,null,"BZ_C052+BZ_C055+BZ_C058+BZ_C061+BZ_C064",null)
		
		// Gastos		
		,BZ_C068	(Mod303Key.BZ_C068
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService())) && hasPercent1(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C068,mod,vat.getBase())
			,null,null,null)
		,BZ_X068	(Mod303Key.BZ_X068,null,null,(ctx,mod) -> add(Mod303Key.BZ_X068,mod,PERCENT1),null,null)
		,BZ_C069	(Mod303Key.BZ_C069
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent1(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C069,mod,vat.getQuota())
			,null,null,null)
		,BZ_C070	(Mod303Key.BZ_C070
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent1(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C070,mod,vat)
			,null,null,null)
		
		,BZ_C071	(Mod303Key.BZ_C071
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent2(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C071,mod,vat.getBase())
			,null,null,null)
		,BZ_X071	(Mod303Key.BZ_X071,null,null,(ctx,mod) -> add(Mod303Key.BZ_X071,mod,PERCENT2),null,null)
		,BZ_C072	(Mod303Key.BZ_C072
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent2(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C072,mod,vat.getQuota())
			,null,null,null)
		,BZ_C073	(Mod303Key.BZ_C073
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent2(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C073,mod,vat)
			,null,null,null)

		,BZ_C074	(Mod303Key.BZ_C074
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent3(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C074,mod,vat.getBase())
			,null,null,null)
		,BZ_X074	(Mod303Key.BZ_X074,null,null,(ctx,mod) -> add(Mod303Key.BZ_X074,mod,PERCENT3),null,null)
		,BZ_C075	(Mod303Key.BZ_C075
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent3(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C075,mod,vat.getQuota())
			,null,null,null)
		,BZ_C076	(Mod303Key.BZ_C076
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent3(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C076,mod,vat)
			,null,null,null)
		
		,BZ_C077	(Mod303Key.BZ_C077
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C077,mod,vat.getBase())
			,null,null,null)
		,BZ_C078	(Mod303Key.BZ_C078
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C078,mod,vat.getQuota())
			,null,null,null)
		,BZ_C079	(Mod303Key.BZ_C079
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C079,mod,vat)
			,null,null,null)
		
		,BZ_C080	(Mod303Key.BZ_C080,null,null,null,"BZ_C068+BZ_C071+BZ_C074+BZ_C077",null)
		,BZ_C081	(Mod303Key.BZ_C081,null,null,null,"BZ_C069+BZ_C072+BZ_C075+BZ_C078",null)
		,BZ_C082	(Mod303Key.BZ_C082,null,null,null,"BZ_C070+BZ_C073+BZ_C076+BZ_C079",null)
		
		// Bienes de inversión
		,BZ_C083	(Mod303Key.BZ_C083
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C083,mod,vat.getBase())
			,null,null,null)
		,BZ_X083	(Mod303Key.BZ_X083,null,null,(ctx,mod) -> add(Mod303Key.BZ_X083,mod,PERCENT1),null,null)
		,BZ_C084	(Mod303Key.BZ_C084
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C084,mod,vat.getQuota())
			,null,null,null)
		,BZ_C085	(Mod303Key.BZ_C085
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent1(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C085,mod,vat)
			,null,null,null)
				
		,BZ_C086	(Mod303Key.BZ_C086
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C086,mod,vat.getBase())
			,null,null,null)
		,BZ_X086	(Mod303Key.BZ_X086,null,null,(ctx,mod) -> add(Mod303Key.BZ_X086,mod,PERCENT2),null,null)
		,BZ_C087	(Mod303Key.BZ_C087
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C087,mod,vat.getQuota())
			,null,null,null)
		,BZ_C088	(Mod303Key.BZ_C088
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent2(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C088,mod,vat)
			,null,null,null)
		
		,BZ_C089	(Mod303Key.BZ_C089
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C089,mod,vat.getBase())
			,null,null,null)
		,BZ_X089	(Mod303Key.BZ_X089,null,null,(ctx,mod) -> add(Mod303Key.BZ_X089,mod,PERCENT3),null,null)
		,BZ_C090	(Mod303Key.BZ_C090
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C090,mod,vat.getQuota())
			,null,null,null)
		,BZ_C091	(Mod303Key.BZ_C091
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent3(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C091,mod,vat)
			,null,null,null)
		
		,BZ_C092	(Mod303Key.BZ_C092
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C092,mod,vat.getBase())
			,null,null,null)
		,BZ_C093	(Mod303Key.BZ_C093
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C093,mod,vat.getQuota())
			,null,null,null)
		,BZ_C094	(Mod303Key.BZ_C094
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C094,mod,vat)
			,null,null,null)
		
		,BZ_C095	(Mod303Key.BZ_C095,null,null,null,"BZ_C083+BZ_C086+BZ_C089+BZ_C092",null)
		,BZ_C096	(Mod303Key.BZ_C096,null,null,null,"BZ_C084+BZ_C087+BZ_C090+BZ_C093",null)
		,BZ_C097	(Mod303Key.BZ_C097,null,null,null,"BZ_C085+BZ_C088+BZ_C091+BZ_C094",null)
		
		// Totales
		,BZ_C098	(Mod303Key.BZ_C098,null,null,null,"BZ_C065+BZ_C080+BZ_C095",null)
		,BZ_C099	(Mod303Key.BZ_C099,null,null,null,"BZ_C066+BZ_C081+BZ_C096",null)
		,BZ_C100	(Mod303Key.BZ_C100,null,null,null,"BZ_C067+BZ_C082+BZ_C097",null)
		
		// ---------------------------------------------------------------
		// ---------------------------------------- OPERACIONES ESPECIFICAS
		// ---------------------------------------------------------------
		
		,BZ_C104	(Mod303Key.BZ_C104
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isService() && (vat.isCanCeuMelSales() || vat.isExtracommunitySales())
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C104,mod,vat.getBase())
			,null,null,null)
		,BZ_C105	(Mod303Key.BZ_C105
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C105,mod,vat.getBase())
			,null,null,null)
		,BZ_C106	(Mod303Key.BZ_C106
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isService() && (vat.isCanCeuMelSales() || vat.isExtracommunitySales())
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C106,mod,vat.getBase())
			,null,null,null)
		,BZ_C107	(Mod303Key.BZ_C107
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isOtherISPSales()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C107,mod,vat.getBase())
			,null,null,null)
		,BZ_C108	(Mod303Key.BZ_C108)
		,BZ_C109 (Mod303Key.BZ_C109)
		;
		
		private Mod303Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;

		private Mod303KeyDAO(Mod303Key key) {
			this(key, null, null, null, null,null);
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
	private static boolean hasNoPercent(VatContext vat) {
		return vat.getPercentage() !=  PERCENT1 && vat.getPercentage() !=  PERCENT2 && vat.getPercentage() !=  PERCENT3; 	
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
	private static boolean hasSurchargePercent4(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT4; 
	}
	private static boolean adqIntracomunitariasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
//			&& !vat.isService()
			&& (vat.isIntracommunityPurchase()
			 || vat.isIntracommunityExpenses()					
					);
	}
	private static boolean adqIntracomunitariasDevFilter(VatContext vat) {
		return adqIntracomunitariasFilter(vat);
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
	private static boolean operacionesISPDevFilter(VatContext vat) {
		return operacionesISPFilter(vat);
	}
	private static boolean modificacionBasesYCuotasFilter(VatContext vat) {
		return vat.isRectification() 
			&& vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
			&& (vat.isNationalSales());
	}
	private static boolean operacionesInterioresFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& !vat.isFarmerRegime() && AonMathUtils.isNotZero(vat.getPercentage())
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
	
	@Override
	public Mod303Key[] getCompensationExplainKeys() {
		return new Mod303Key[] {Mod303Key.BZ_C034};
	}
	@Override
	public Mod303Key[] getSamePeriodExplainKeys() {
		return new Mod303Key[] {Mod303Key.BZ_C041,Mod303Key.BZ_C042};
	}
	@Override
	protected String getSamePeriodExplain(AONContext ctx, Mod303 mod303, Mod303Key key) {
		if (key == Mod303Key.BZ_C041) {
			return getExplain(ctx, mod303, key
				, Mod303DAO.getSamePeriodEffectiveModels(ctx, mod303)
					.filter( Mod303::isToDeposit)
				, new ExplainRowManager());
		} else {
			return getExplain(ctx, mod303, key
				, Mod303DAO.getSamePeriodEffectiveModels(ctx, mod303)
					.filter( Mod303::isToPayback)
				, new ExplainRowManager());
		}
	}
	@Override
	protected String getCompensationExplain( AONContext ctx, Mod303 mod303, Mod303Key key) {
		if (mod303 .isFirstPeriod()) {
			return getExplain(ctx, mod303, key
				, Mod390HFDAO.getLastPeriodEffectiveModels(ctx, mod303)
					.filter(Mod390HF::isToCompensate)
				, new ExplainRowManager());
		} 
		return getExplain(ctx, mod303, key
				, Mod303DAO.getLastPeriodEffectiveModels(ctx, mod303)
					.filter(Mod303::isToCompensate)
				, new ExplainRowManager());
	}
	
	
}
