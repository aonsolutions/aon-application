package com.esferalia.aon.occam.impl.jooq.dao.mod303;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.Mod303DAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class BIZKAIA_2017_Declaration extends Mod303Declaration {
	
	protected BIZKAIA_2017_Declaration() {
		
	}
	
	private static final double PERCENT1 = 4.0;
	private static final double PERCENT2 = 10.0;
	private static final double PERCENT3 = 21.0;
	private static final double SURCHARGE_PERCENT1 = 0.5;
	private static final double SURCHARGE_PERCENT2 = 1.4;
	private static final double SURCHARGE_PERCENT3 = 5.2;
	private static final double SURCHARGE_PERCENT4 = 1.75;
	
	public static boolean accept(Mod303 mod) {
		return  mod.isBizkaia() && mod.getYear() >= 2017;
	}
	
	private static enum Mod303KeyDAO implements IMod303KeyDAO {
		 BZ_C185_1	(Mod303Key.BZ_C185_1)
		,BZ_C185_2	(Mod303Key.BZ_C185_2)
		,BZ_C186	(Mod303Key.BZ_C186)
		,BZ_C187	(Mod303Key.BZ_C187)
		,BZ_C002	(Mod303Key.BZ_C002)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al primer tipo.
		,BZ_C003(Mod303Key.BZ_C003
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C003,mod,vat.getBase()))
		,BZ_X003(Mod303Key.BZ_X003,(ctx,mod) -> add(Mod303Key.BZ_X003,mod,PERCENT1))
		,BZ_C004(Mod303Key.BZ_C004
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C004,mod,vat.getQuota()))
		
		// Base imponible, porcentaje y cuota al segundo tipo.
		,BZ_C005	(Mod303Key.BZ_C005
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C005,mod,vat.getBase()))
		,BZ_X005	(Mod303Key.BZ_X005,(ctx,mod) -> add(Mod303Key.BZ_X005,mod,PERCENT2))
		,BZ_C006	(Mod303Key.BZ_C006
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C006,mod,vat.getQuota()))
		
		// Base imponible, porcentaje y cuota al tercer tipo.
		,BZ_C007	(Mod303Key.BZ_C007
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C007,mod,vat.getBase()))
		,BZ_X007	(Mod303Key.BZ_X007,(ctx,mod) -> add(Mod303Key.BZ_X007,mod,PERCENT3))
		,BZ_C008	(Mod303Key.BZ_C008
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C008,mod,vat.getQuota()))

		// Recargo equivalencia al primer  tipo.
		,BZ_C009	(Mod303Key.BZ_C009
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C009,mod,vat.getBase()))
		,BZ_X009	(Mod303Key.BZ_X009,(ctx,mod) -> add(Mod303Key.BZ_X009,mod,SURCHARGE_PERCENT1))
		,BZ_C010	(Mod303Key.BZ_C010
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C010,mod,vat.getSurchargeQuota()))
		
		// Recargo equivalencia al segundo tipo.
		,BZ_C011	(Mod303Key.BZ_C011
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C011,mod,vat.getBase()))
		,BZ_X011	(Mod303Key.BZ_X011,(ctx,mod) -> add(Mod303Key.BZ_X011,mod,SURCHARGE_PERCENT2))
		,BZ_C012	(Mod303Key.BZ_C012
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C012,mod,vat.getSurchargeQuota()))
		
		// Recargo equivalencia al tercer tipo.
		,BZ_C013	(Mod303Key.BZ_C013
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C013,mod,vat.getBase()))
		,BZ_X013	(Mod303Key.BZ_X013,(ctx,mod) -> add(Mod303Key.BZ_X013,mod,SURCHARGE_PERCENT3))
		,BZ_C014	(Mod303Key.BZ_C014
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C014,mod,vat.getSurchargeQuota()))
		
		// Recargo equivalencia al cuarto tipo.
		,BZ_C015	(Mod303Key.BZ_C015
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C015,mod,vat.getBase()))
		,BZ_X015	(Mod303Key.BZ_X015,(ctx,mod) -> add(Mod303Key.BZ_X015,mod,SURCHARGE_PERCENT4))
		,BZ_C016	(Mod303Key.BZ_C016
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C016,mod,vat.getSurchargeQuota()))
		
		// Adquisiciones intracomunitarias. Base y cuota.
		,BZ_C017	(Mod303Key.BZ_C017
			,(mod,vat) -> adqIntracomunitariasDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C017,mod,vat.getBase()))
		,BZ_C018	(Mod303Key.BZ_C018
			,(mod,vat) -> adqIntracomunitariasDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C018,mod,vat.getQuota()))
		
		// IVAS devengado por inversión del sujeto pasivo. Base y cuota
		,BZ_C019	(Mod303Key.BZ_C019
			,(mod,vat) -> operacionesISPDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C019,mod,vat.getBase()))
		,BZ_C020	(Mod303Key.BZ_C020
			,(mod,vat) -> operacionesISPDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C020,mod,vat.getQuota()))	
		
		// Modificación bases y cuotas
		,BZ_C021	(Mod303Key.BZ_C021
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C021,mod,vat.getBase()))
		,BZ_C022	(Mod303Key.BZ_C022
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C022,mod,(vat.getQuota() + vat.getSurchargeQuota())))

		// Modificación de bases y cuotas, artículo 80.3 y 80.4 NFIVA
		,BZ_C046	(Mod303Key.BZ_C046)
		,BZ_C047	(Mod303Key.BZ_C047)
		
		// Total cuota devengada
		,BZ_C023	(Mod303Key.BZ_C023,null,null,null,"BZ_C004+BZ_C006+BZ_C008+BZ_C010+BZ_C012+BZ_C014+BZ_C016+BZ_C018+BZ_C020+BZ_C022+BZ_C047")
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------
		
		// IVA deducible en operaciones interiores
		,BZ_C024	(Mod303Key.BZ_C024
			,(mod,vat) -> operacionesInterioresFilter(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C024,mod,vat.getDeductibleQuota()))

		// IVA deducible en importaciones
		,BZ_C025	(Mod303Key.BZ_C025
			,(mod,vat) -> importacionesFilter(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C025,mod,vat.getDeductibleQuota()))
		
		// IVA deducible en adquisiciones intracomunitarias
		,BZ_C026	(Mod303Key.BZ_C026
			,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C026,mod,vat.getDeductibleQuota()))
		
		// Compensaciones Régimen Especial A.G. y P .
		,BZ_C027	(Mod303Key.BZ_C027
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C027,mod,vat.getDeductibleQuota()))
				
		// Regularización Inversiones
		,BZ_C028	(Mod303Key.BZ_C028)
		
		// Total a deducir
		,BZ_C030	(Mod303Key.BZ_C030,null,null,null,"BZ_C024+BZ_C025+BZ_C026+BZ_C027+BZ_C028")

		// Diferencia
		,BZ_C031	(Mod303Key.BZ_C031,null,null,null,"BZ_C023-BZ_C030")
		
		// Regularización de cuotas (art.80.cinco.5a Norma Foral del IVA)
		,BZ_C045	(Mod303Key.BZ_C045)
		
		// Porcentaje de tributaci\u00F3n en Bizkaia
		,BZ_C032	(Mod303Key.BZ_C032 ,(ctx,mod) -> add(Mod303Key.BZ_C032,mod,100.0))
		
		// Cuota atribuible a Bizkaia
		,BZ_C033	(Mod303Key.BZ_C033,null,null,null,"(BZ_C031+BZ_C045)*BZ_C032/100")
		
		// Cuota a compensar de periodos anteriores
		,BZ_C034	(Mod303Key.BZ_C034)
		
		// Resultado de la regularizaci\u00F3n anual
		,BZ_C035	(Mod303Key.BZ_C035)
		
		// Resultado
		,BZ_C036	(Mod303Key.BZ_C036,null,null,null,"BZ_C033-BZ_C034+BZ_C035")   
		
		// A compensar
		,BZ_C038	(Mod303Key.BZ_C038)
		// A devolver
		,BZ_C039	(Mod303Key.BZ_C039)
		// A ingresar
		,BZ_C040	(Mod303Key.BZ_C040)
		
		// Cumplimentar s\u00F3lo en caso de que se trate de una autoliquidaci\u00F3n complementaria: ingresado anteriormente
		,BZ_C041	(Mod303Key.BZ_C041)
		// Cumplimentar s\u00F3lo en caso de que se trate de una autoliquidaci\u00F3n complementaria: devuelto anteriormente
		,BZ_C042	(Mod303Key.BZ_C042)
		// Total deuda tributaria
		,BZ_C043	(Mod303Key.BZ_C043,null,null,null,"BZ_C036-BZ_C041+BZ_C042")
		
		// *************************		
		// *************************		
		// *************************
		
		// Exclusivamente para sujetos pasivos acogidos al régimen especial del criterio de caja y para 
		// destinatarios/as de operaciones afectadas por el mismo
		
		// Importes de las entregas de bienes y prestaciones de servicios a las que habiéndoles
		// sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas
		// conforme a la regla general de devengo contenida en el artículo 75 NFIVA
		,BZ_C200	(Mod303Key.BZ_C200,(ctx,mod) -> add(Mod303Key.BZ_C200,mod,Mod303DAO.getVatAccrualPaymentOutputBase(ctx,mod)))
		,BZ_C201	(Mod303Key.BZ_C201,(ctx,mod) -> add(Mod303Key.BZ_C201,mod,Mod303DAO.getVatAccrualPaymentOutputQuota(ctx,mod)))
		
		// Importes de las adquisiciones de bienes y servicios a las que sea aplicable o afecte el
		// régimen especial del criterio de caja
		,BZ_C202	(Mod303Key.BZ_C202,(ctx,mod) -> add(Mod303Key.BZ_C202,mod,Mod303DAO.getVatAccrualPaymentInputBase(ctx,mod)))
		,BZ_C203	(Mod303Key.BZ_C203,(ctx,mod) -> add(Mod303Key.BZ_C203,mod,Mod303DAO.getVatAccrualPaymentInputQuota(ctx,mod)))

		// ---------------------------------------------------------------
		// ----------------------------------------- INFORMACION ADICIONAL
		// ---------------------------------------------------------------

		// Compras de bienes corrientes
		,BZ_C050	(Mod303Key.BZ_C050
			,(mod,vat) -> vat.isPurchase()  && hasPercent1(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C050,mod,vat.getBase()))
		,BZ_X050	(Mod303Key.BZ_X050,(ctx,mod) -> add(Mod303Key.BZ_X050,mod,PERCENT1))
		,BZ_C051	(Mod303Key.BZ_C051
			,(mod,vat) -> vat.isPurchase()  && hasPercent1(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C051,mod,vat.getQuota()))
		,BZ_C052	(Mod303Key.BZ_C052
			,(mod,vat) -> vat.isPurchase()  && hasPercent1(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C052,mod,vat.getDeductibleQuota()))
		
		,BZ_C053	(Mod303Key.BZ_C053
			,(mod,vat) -> vat.isPurchase()  && hasPercent2(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C053,mod,vat.getBase()))
		,BZ_X053	(Mod303Key.BZ_X053,(ctx,mod) -> add(Mod303Key.BZ_X053,mod,PERCENT2))
		,BZ_C054	(Mod303Key.BZ_C054
			,(mod,vat) -> vat.isPurchase()  && hasPercent2(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C054,mod,vat.getQuota()))
		,BZ_C055	(Mod303Key.BZ_C055
			,(mod,vat) -> vat.isPurchase()  && hasPercent2(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C055,mod,vat.getDeductibleQuota()))
		
		,BZ_C056	(Mod303Key.BZ_C056
			,(mod,vat) -> vat.isPurchase()  && hasPercent3(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C056,mod,vat.getBase()))
		,BZ_X056	(Mod303Key.BZ_X056,(ctx,mod) -> add(Mod303Key.BZ_X056,mod,PERCENT3))
		,BZ_C057	(Mod303Key.BZ_C057
			,(mod,vat) -> vat.isPurchase()  && hasPercent3(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C057,mod,vat.getQuota()))
		,BZ_C058	(Mod303Key.BZ_C058
			,(mod,vat) -> vat.isPurchase()  && hasPercent3(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C058,mod,vat.getDeductibleQuota()))
		
		,BZ_C059	(Mod303Key.BZ_C059
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isPurchase() && !vat.isInvestment() && vat.isFarmerRegime()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C059,mod,vat.getBase()))
		,BZ_C060	(Mod303Key.BZ_C060
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isPurchase() && !vat.isInvestment() && vat.isFarmerRegime()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C060,mod,vat.getBase()))
		,BZ_C061	(Mod303Key.BZ_C061
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isPurchase() && !vat.isInvestment() && vat.isFarmerRegime()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C061,mod,vat.getBase()))
		
		,BZ_C062	(Mod303Key.BZ_C062
			,(mod,vat) -> vat.isPurchase()  && hasNoPercent(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C062,mod,vat.getBase()))
		,BZ_C063	(Mod303Key.BZ_C063
			,(mod,vat) -> vat.isPurchase()  && hasNoPercent(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C063,mod,vat.getBase()))
		,BZ_C064	(Mod303Key.BZ_C064
			,(mod,vat) -> vat.isPurchase()  && hasNoPercent(vat) && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C064,mod,vat.getBase()))
		
		,BZ_C065	(Mod303Key.BZ_C065,null,null,null,"BZ_C050+BZ_C053+BZ_C056+BZ_C059+BZ_C062")
		,BZ_C066	(Mod303Key.BZ_C066,null,null,null,"BZ_C051+BZ_C054+BZ_C057+BZ_C060+BZ_C063")
		,BZ_C067	(Mod303Key.BZ_C067,null,null,null,"BZ_C052+BZ_C055+BZ_C058+BZ_C061+BZ_C064")
		
		// Gastos		
		,BZ_C068	(Mod303Key.BZ_C068
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasPercent1(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C068,mod,vat.getBase()))
		,BZ_X068	(Mod303Key.BZ_X068,(ctx,mod) -> add(Mod303Key.BZ_X068,mod,PERCENT1))
		,BZ_C069	(Mod303Key.BZ_C069
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasPercent1(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C069,mod,vat.getQuota()))
		,BZ_C070	(Mod303Key.BZ_C070
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasPercent1(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C070,mod,vat.getDeductibleQuota()))
		
		,BZ_C071	(Mod303Key.BZ_C071
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasPercent2(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C071,mod,vat.getBase()))
		,BZ_X071	(Mod303Key.BZ_X071,(ctx,mod) -> add(Mod303Key.BZ_X071,mod,PERCENT2))
		,BZ_C072	(Mod303Key.BZ_C072
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasPercent2(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C072,mod,vat.getQuota()))
		,BZ_C073	(Mod303Key.BZ_C073
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasPercent2(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C073,mod,vat.getDeductibleQuota()))
		
		,BZ_C074	(Mod303Key.BZ_C074
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasPercent3(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C074,mod,vat.getBase()))
		,BZ_X074	(Mod303Key.BZ_X074,(ctx,mod) -> add(Mod303Key.BZ_X074,mod,PERCENT3))
		,BZ_C075	(Mod303Key.BZ_C075
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasPercent3(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C075,mod,vat.getQuota()))
		,BZ_C076	(Mod303Key.BZ_C076
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasPercent3(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C076,mod,vat.getDeductibleQuota()))
		
		,BZ_C077	(Mod303Key.BZ_C077
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C077,mod,vat.getBase()))
		,BZ_C078	(Mod303Key.BZ_C078
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C078,mod,vat.getQuota()))
		,BZ_C079	(Mod303Key.BZ_C079
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isExpenses()  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C079,mod,vat.getDeductibleQuota()))
		
		,BZ_C080	(Mod303Key.BZ_C080,null,null,null,"BZ_C068+BZ_C071+BZ_C074+BZ_C077")
		,BZ_C081	(Mod303Key.BZ_C081,null,null,null,"BZ_C069+BZ_C072+BZ_C075+BZ_C078")
		,BZ_C082	(Mod303Key.BZ_C082,null,null,null,"BZ_C070+BZ_C073+BZ_C076+BZ_C079")
		
		// Bienes de inversión
		,BZ_C083	(Mod303Key.BZ_C083
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C083,mod,vat.getBase()))
		,BZ_X083	(Mod303Key.BZ_X083,(ctx,mod) -> add(Mod303Key.BZ_X083,mod,PERCENT1))
		,BZ_C084	(Mod303Key.BZ_C084
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent1(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C084,mod,vat.getQuota()))
		,BZ_C085	(Mod303Key.BZ_C085
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent1(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C085,mod,vat.getDeductibleQuota()))
				
		,BZ_C086	(Mod303Key.BZ_C086
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C086,mod,vat.getBase()))
		,BZ_X086	(Mod303Key.BZ_X086,(ctx,mod) -> add(Mod303Key.BZ_X086,mod,PERCENT2))
		,BZ_C087	(Mod303Key.BZ_C087
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent2(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C087,mod,vat.getQuota()))
		,BZ_C088	(Mod303Key.BZ_C088
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent2(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C088,mod,vat.getDeductibleQuota()))
		
		,BZ_C089	(Mod303Key.BZ_C089
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C089,mod,vat.getBase()))
		,BZ_X089	(Mod303Key.BZ_X089,(ctx,mod) -> add(Mod303Key.BZ_X089,mod,PERCENT3))
		,BZ_C090	(Mod303Key.BZ_C090
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent3(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C090,mod,vat.getQuota()))
		,BZ_C091	(Mod303Key.BZ_C091
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent3(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C091,mod,vat.getDeductibleQuota()))
		
		,BZ_C092	(Mod303Key.BZ_C092
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C092,mod,vat.getBase()))
		,BZ_C093	(Mod303Key.BZ_C093
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C093,mod,vat.getQuota()))
		,BZ_C094	(Mod303Key.BZ_C094
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> prorate(Mod303Key.BZ_C093,mod,vat.getDeductibleQuota()))
		
		,BZ_C095	(Mod303Key.BZ_C095,null,null,null,"BZ_C083+BZ_C086+BZ_C089+BZ_C092")
		,BZ_C096	(Mod303Key.BZ_C096,null,null,null,"BZ_C084+BZ_C087+BZ_C090+BZ_C093")
		,BZ_C097	(Mod303Key.BZ_C097,null,null,null,"BZ_C085+BZ_C088+BZ_C091+BZ_C094")
		
		// Totales
		,BZ_C098	(Mod303Key.BZ_C098,null,null,null,"BZ_C065+BZ_C080+BZ_C095")
		,BZ_C099	(Mod303Key.BZ_C099,null,null,null,"BZ_C066+BZ_C081+BZ_C096")
		,BZ_C100	(Mod303Key.BZ_C100,null,null,null,"BZ_C067+BZ_C082+BZ_C097")
		
		// ---------------------------------------------------------------
		// ---------------------------------------- OPERACIONES ESPECIFICAS
		// ---------------------------------------------------------------
		
		,BZ_C104	(Mod303Key.BZ_C104
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && (vat.isCanCeuMelSales() || vat.isExtracommunitySales())
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C104,mod,vat.getBase()))
		,BZ_C105	(Mod303Key.BZ_C105
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales() && !vat.isService()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C105,mod,vat.getBase()))
		,BZ_C106	(Mod303Key.BZ_C106)
		,BZ_C107	(Mod303Key.BZ_C107
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isOtherISPSales()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C105,mod,vat.getBase()))
		,BZ_C108	(Mod303Key.BZ_C108
			,(mod,vat) -> vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales() && vat.isService()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C108,mod,vat.getBase()))
		;
		
		private Mod303Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;

		private Mod303KeyDAO(Mod303Key key) {
			this(key, null, null, null, null);
		}
		private Mod303KeyDAO(Mod303Key key, IValueFirstIntializer firstInitializer) {
			this(key,null,null,firstInitializer,null);			
		}
		private Mod303KeyDAO(Mod303Key key, IValueAccepter acceptValue, IValueIntializer initializer) {
			this(key,acceptValue,initializer,null,null);			
		}
		
		private Mod303KeyDAO(Mod303Key key
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueFirstIntializer firstInitializer
				, String expression) {
			this.key = key;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.firstInitializer = firstInitializer;
			this.expression =  expression;
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
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses();
	}
	private static boolean adqIntracomunitariasDevFilter(VatContext vat) {
		return !vat.isRectification() && adqIntracomunitariasFilter(vat);
	}
	private static boolean operacionesISPFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
				&& (vat.isOtherISPPurchase() || vat.isOtherISPExpenses() || vat.isExtracommunityExpenses() || vat.isCanCeuMelExpenses());
	}
	private static boolean operacionesISPDevFilter(VatContext vat) {
		return !vat.isRectification() && operacionesISPFilter(vat);
	}
	private static boolean modificacionBasesYCuotasFilter(VatContext vat) {
		return vat.isRectification() && vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& (vat.isNationalSales() || adqIntracomunitariasFilter(vat) || operacionesISPFilter(vat));		
	}
	private static boolean operacionesInterioresFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& !vat.isFarmerRegime() && AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	private static boolean importacionesFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase());
	}
	private static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime() && !vat.isVatSurchargeRegime()
			&& vat.isFarmerRegime() && vat.isNationalPurchase();		
	}
}
