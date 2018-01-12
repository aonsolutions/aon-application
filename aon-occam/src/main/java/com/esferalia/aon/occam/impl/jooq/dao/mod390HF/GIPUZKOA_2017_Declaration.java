package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod390HFDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class GIPUZKOA_2017_Declaration extends Mod390HFDeclaration {
	
	protected GIPUZKOA_2017_Declaration() {
		
	}
	
	public static final double PERCENT1 = 21.0;
	public static final double PERCENT2 = 10.0;
	public static final double PERCENT3 = 4.0;
	public static final double PERCENT4 = 10.5;	
	public static final double PERCENT5 = 12;
	
	public static final double SURCHARGE_PERCENT1 = 5.2;
	public static final double SURCHARGE_PERCENT2 = 1.4;
	public static final double SURCHARGE_PERCENT3 = 0.5;
	
	public static boolean accept(Mod390HF mod) {
		return  mod.isGipuzkoa() && mod.getYear() >= 2017;
	}
	private static final Mod390Key[] PRORATE_KEYS = new Mod390Key[]{
		  Mod390Key.GP_C018
		 ,Mod390Key.GP_C020
		 ,Mod390Key.GP_C022
		 ,Mod390Key.GP_C046
		 ,Mod390Key.GP_C023
	};
	
	private static enum Mod390KeyDAO implements IMod390KeyDAO {
		 GP_A000	(Mod390Key.GP_A000)
		,GP_A001	(Mod390Key.GP_A001)
		,GP_A002	(Mod390Key.GP_A002)
		,CM_003		(Mod390Key.CM_003)
		,GP_A078	(Mod390Key.GP_A078)
		,GP_A079	(Mod390Key.GP_A079)
		,GP_A100	(Mod390Key.GP_A100)
		
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al primer tipo.
		,GP_C002(Mod390Key.GP_C002
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C002,mod,vat.getBase())
			,null,null,null)
		,GP_X002(Mod390Key.GP_X002,null,null,(ctx,mod) -> add(Mod390Key.GP_X002,mod,PERCENT1),null,null)
		,GP_C003(Mod390Key.GP_C003
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C003,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al segundo tipo.
		,GP_C004(Mod390Key.GP_C004
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			 ,(ctx,mod,vat) -> add(Mod390Key.GP_C004,mod,vat.getBase())
				,null,null,null)
		,GP_X004(Mod390Key.GP_X004,null,null,(ctx,mod) -> add(Mod390Key.GP_X004,mod,PERCENT2),null,null)
		,GP_C005(Mod390Key.GP_C005
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C005,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al tercer tipo.
		,GP_C006(Mod390Key.GP_C006
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			 ,(ctx,mod,vat) -> add(Mod390Key.GP_C006,mod,vat.getBase())
			,null,null,null)
		,GP_X006(Mod390Key.GP_X006,null,null,(ctx,mod) -> add(Mod390Key.GP_X006,mod,PERCENT3),null,null)
		,GP_C007(Mod390Key.GP_C007
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C007,mod,vat.getQuota())
			,null,null,null)
		
		// Modificación bases y cuotas
		,GP_C008(Mod390Key.GP_C008
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod390Key.GP_C008,mod,vat.getBase())
			,null,null,null)
		,GP_C009(Mod390Key.GP_C009
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C009,mod,vat.getQuota())
			,null,null,null)
		
		// Recargo equivalencia al primer tipo.
		,GP_C010(Mod390Key.GP_C010
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C010,mod,vat.getBase())
			,null,null,null)
		,GP_X010(Mod390Key.GP_X010,null,null,(ctx,mod) -> add(Mod390Key.GP_X010,mod,SURCHARGE_PERCENT1),null,null)
		,GP_C011(Mod390Key.GP_C011
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent1(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C011,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al segundo tipo.
		,GP_C012(Mod390Key.GP_C012
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C012,mod,vat.getBase())
			,null,null,null)
		,GP_X012(Mod390Key.GP_X012,null,null,(ctx,mod) -> add(Mod390Key.GP_X012,mod,SURCHARGE_PERCENT2),null,null)
		,GP_C013(Mod390Key.GP_C013
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent2(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C013,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al tercer tipo.
		,GP_C014(Mod390Key.GP_C014
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C014,mod,vat.getBase())
			,null,null,null)
		,GP_X014(Mod390Key.GP_X014,null,null,(ctx,mod) -> add(Mod390Key.GP_X014,mod,SURCHARGE_PERCENT3),null,null)
		,GP_C015(Mod390Key.GP_C015
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent3(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C015,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Modificaciones bases y cuotas del recargo de equivalencia
		,GP_C016(Mod390Key.GP_C016
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C016,mod,vat.getBase())
			,null,null,null)
		,GP_C017(Mod390Key.GP_C017
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C017,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias.		
		,GP_C018(Mod390Key.GP_C018
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C018,mod,vat.getBase())
			,null,null,null)
		,GP_C019(Mod390Key.GP_C019
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C019,mod,vat.getQuota())
			,null,null,null)
		
		// Otras operaciones con inversión del sujeto pasivo (excepto. adq. intracom). Base y cuota
		,GP_C106(Mod390Key.GP_C106
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C106,mod,vat.getBase())
			,null,null,null)
		,GP_C107(Mod390Key.GP_C107
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C107,mod,vat.getQuota())
			,null,null,null)
		
		// TOTAL CUOTA DEVENGADA
		,GP_C020(Mod390Key.GP_C020,null,null,null,"GP_C003+GP_C005+GP_C007+GP_C009+GP_C011+GP_C013+GP_C015+GP_C017+GP_C019+GP_C107",null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------

		// IVA deducible en operaciones interiores 
		,GP_C021(Mod390Key.GP_C021
			,(mod,vat) -> operacionesInterioresFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C021,mod,vat.getBase())
			,null,null,null)
		,GP_C022(Mod390Key.GP_C022
			,(mod,vat) -> operacionesInterioresFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C022,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en importaciones
		,GP_C023(Mod390Key.GP_C023
			,(mod,vat) -> importacionesFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C023,mod,vat.getBase())
			,null,null,null)
		,GP_C024(Mod390Key.GP_C024
			,(mod,vat) -> importacionesFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C024,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en adquisiciones intracomunitarias de bienes y servicios corrientes
		,GP_C025(Mod390Key.GP_C025
			,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C025,mod,vat.getBase())
			,null,null,null)
		,GP_C026(Mod390Key.GP_C026
			,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C026,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Compensaciones Régimen Especial A.G. y P .
		,GP_C027(Mod390Key.GP_C027
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C027,mod,vat.getDeductibleQuota())
			,null,null,null)
		// Rectificación de deducciones
		,GP_C271(Mod390Key.GP_C271
			,(mod,vat) -> rectificaciónDeduccionesFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C271,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Regularización Inversiones
		,GP_C028(Mod390Key.GP_C028)
		
		// TOTAL A DEDUCIR
		,GP_C029(Mod390Key.GP_C029,null,null,null,"GP_C022+GP_C024+GP_C026+GP_C027+GP_C271+GP_C028",null)
		// DIFERENCIA
		,GP_C030(Mod390Key.GP_C030,null,null,null,"GP_C020-GP_C029",null)
		
		// Volumen operaciones. % GIPUZKOA
		,GP_C031(Mod390Key.GP_C031,null,null,(ctx,mod) -> add(Mod390Key.GP_C031,mod,100.0),null,null)
		,GP_C032(Mod390Key.GP_C032)
		,GP_C033(Mod390Key.GP_C033)
		,GP_C034(Mod390Key.GP_C034)
		,GP_C035(Mod390Key.GP_C035)
	
		// Cuota atribuible al Territorio Histórico de Gipuzkoa	
		,GP_C036(Mod390Key.GP_C036,null,null,null,"GP_C030*GP_C031/100",null)

		// Cuotas a compensar de períodos anteriores en el Territorio Histórico de Álava	
		,GP_C037(Mod390Key.GP_C037)
		// Total INGRESOS efectuados durante el presente ejercicion
		,GP_C038(Mod390Key.GP_C038)
		// Total DEVOLUCIONES practicadas durante el presente ejercicion
		,GP_C039(Mod390Key.GP_C039)
		// RESULTADO DE LA AUTOLIQUIDACIÓN	
		,GP_C040(Mod390Key.GP_C040,null,null,null,"GP_C036-GP_C037-GP_C038+GP_C039",null)
		
		// A ingresar
		,GP_C041(Mod390Key.GP_C041,null,null,null,"isToDeposit()?GP_C040:0.0",null)
		// A compensar
		,GP_C042(Mod390Key.GP_C042,null,null,null,"isToCompensate()?round(GP_C040*-1):0.0",null)
		// A devolver
		,GP_C043(Mod390Key.GP_C043,null,null,null,"isToPayback()?round(GP_C040*-1):0.0",null)

		// -----------------------------------------------------------
		// ------------------------------------- INFORMACION ADICIONAL
		// -----------------------------------------------------------

		// Compras de bienes corrientes
		,GP_C046 (Mod390Key.GP_C046
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent1(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C046,mod,vat.getBase())
			,null,null,null)
		,GP_X046	(Mod390Key.GP_X046,null,null,(ctx,mod) -> add(Mod390Key.GP_X046,mod,PERCENT1),null,null)
		,GP_C047 (Mod390Key.GP_C047
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent1(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C047,mod,vat.getDeductibleQuota())
			,null,null,null)
		,GP_C048	(Mod390Key.GP_C048
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent2(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C048,mod,vat.getBase())
			,null,null,null)
		,GP_X048	(Mod390Key.GP_X048,null,null,(ctx,mod) -> add(Mod390Key.GP_X048,mod,PERCENT2),null,null)
		,GP_C049	(Mod390Key.GP_C049
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent2(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C049,mod,vat.getDeductibleQuota())
			,null,null,null)
		,GP_C050	(Mod390Key.GP_C050
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent3(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C050,mod,vat.getBase())
			,null,null,null)
		,GP_X050	(Mod390Key.GP_X050,null,null,(ctx,mod) -> add(Mod390Key.GP_X050,mod,PERCENT3),null,null)
		,GP_C051	(Mod390Key.GP_C051
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent3(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C051,mod,vat.getDeductibleQuota())
			,null,null,null)
		,GP_C052	(Mod390Key.GP_C052
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent4(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C052,mod,vat.getBase())
			,null,null,null)
		,GP_C053	(Mod390Key.GP_C053
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent4(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C053,mod,vat.getDeductibleQuota())
			,null,null,null)
		,GP_C054	(Mod390Key.GP_C054
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasNoPercent(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C054,mod,vat.getBase())
			,null,null,null)
		,GP_C055	(Mod390Key.GP_C055
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasNoPercent(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C055,mod,vat.getQuota())
			,null,null,null)
		,GP_C056	(Mod390Key.GP_C056,null,null,null,"GP_C047+GP_C049+GP_C051+GP_C053",null)

		// GASTOS
		,GP_C057 (Mod390Key.GP_C057
				,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService())) && hasPercent1(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
				,(ctx,mod,vat) -> add(Mod390Key.GP_C057,mod,vat.getBase())
				,null,null,null)
		,GP_X057	(Mod390Key.GP_X057,null,null,(ctx,mod) -> add(Mod390Key.GP_X057,mod,PERCENT1),null,null)
		,GP_C058	(Mod390Key.GP_C058
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent1(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C058,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,GP_C059	(Mod390Key.GP_C059
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent2(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C059,mod,vat.getBase())
			,null,null,null)
		,GP_X059	(Mod390Key.GP_X059,null,null,(ctx,mod) -> add(Mod390Key.GP_X059,mod,PERCENT2),null,null)
		,GP_C060	(Mod390Key.GP_C060
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent2(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C060,mod,vat.getDeductibleQuota())
			,null,null,null)

		,GP_C061	(Mod390Key.GP_C061
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent3(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C061,mod,vat.getBase())
			,null,null,null)
		,GP_X061	(Mod390Key.GP_X061,null,null,(ctx,mod) -> add(Mod390Key.GP_X061,mod,PERCENT3),null,null)
		,GP_C062	(Mod390Key.GP_C062
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent3(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C062,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,GP_C063	(Mod390Key.GP_C063
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C063,mod,vat.getBase())
			,null,null,null)
		,GP_C064	(Mod390Key.GP_C064
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C064,mod,vat.getDeductibleQuota())
			,null,null,null)

		,GP_C065	(Mod390Key.GP_C065,null,null,null,"GP_C058+GP_C060+GP_C062+GP_C064",null)
		
		// Bienes de inversión
		,GP_C066	(Mod390Key.GP_C066
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C066,mod,vat.getBase())
			,null,null,null)
		,GP_X066	(Mod390Key.GP_X066,null,null,(ctx,mod) -> add(Mod390Key.GP_X066,mod,PERCENT1),null,null)
		,GP_C067	(Mod390Key.GP_C067
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent1(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C067,mod,vat.getDeductibleQuota())
			,null,null,null)
				
		,GP_C068	(Mod390Key.GP_C068
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C068,mod,vat.getBase())
			,null,null,null)
		,GP_X068	(Mod390Key.GP_X068,null,null,(ctx,mod) -> add(Mod390Key.GP_X068,mod,PERCENT2),null,null)
		,GP_C069	(Mod390Key.GP_C069
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent2(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C069,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,GP_C070	(Mod390Key.GP_C070
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent3(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C070,mod,vat.getBase())
			,null,null,null)
		,GP_X070	(Mod390Key.GP_X070,null,null,(ctx,mod) -> add(Mod390Key.GP_X070,mod,PERCENT3),null,null)
		,GP_C071	(Mod390Key.GP_C071
				,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent3(vat)
				,(ctx,mod,vat) -> add(Mod390Key.GP_C071,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,GP_C072	(Mod390Key.GP_C072
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C072,mod,vat.getBase())
			,null,null,null)
		,GP_C073	(Mod390Key.GP_C073
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> add(Mod390Key.GP_C073,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,GP_C074	(Mod390Key.GP_C074,null,null,null,"GP_C067+GP_C069+GP_C071+GP_C073",null)

		,GP_C075	(Mod390Key.GP_C075,null,null,null,"GP_C046+GP_C048+GP_C050+GP_C052+GP_C054+GP_C057+GP_C059+GP_C061+GP_C063+GP_C066+GP_C068+GP_C070+GP_C072",null)
		,GP_C076	(Mod390Key.GP_C076,null,null,null,"GP_C047+GP_C049+GP_C051+GP_C053+GP_C055+GP_C058+GP_C060+GP_C062+GP_C064+GP_C067+GP_C069+GP_C071+GP_C073",null)
		,GP_C077	(Mod390Key.GP_C077,null,null,null,"GP_C056+GP_C065+GP_C074",null)
		
		// OPERACIONES EN RÉGIMEN GENERAL
		,GP_C082  	(Mod390Key.GP_C082
			,(mod,vat) -> vat.isNationalSales() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C082,mod,vat.getBase())
			,null,null,null)
		// OPERACIONES EN RÉGIMEN ESPECIAL DE AGRICULTURA, GANADER\u00CDA Y PESCA
		,GP_C083  	(Mod390Key.GP_C083)
		// OPERACIONES EN RÉGIMEN ESPECIAL DEL RECARGO DE EQUIVALENCIA
		,GP_C084  	(Mod390Key.GP_C084)
		// ENTREGAS INTRACOMUNITARIAS DE BIENES Y DE SERVICIOS
		,GP_C085  	(Mod390Key.GP_C085
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales()
			,(ctx,mod,vat) -> add(Mod390Key.GP_C085,mod,vat.getBase())
			,null,null,null)
		// EXPORTACIONES Y OTRAS OPERACIONES EXENTAS CON DERECHO A DEDUCCIÓN
		,GP_C086 	(Mod390Key.GP_C086
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isService() && (vat.isCanCeuMelSales() || vat.isExtracommunitySales())
			,(ctx,mod,vat) -> add(Mod390Key.GP_C086,mod,vat.getBase())
			,null,null,null)
		// OPERACIONES QUE DAN LUGAR A LA INVERSI\u00D3N DEL SUJETO PASIVO
		,GP_C088  	(Mod390Key.GP_C088
			,(mod,vat) -> vat.isOtherISPSales() 
			,(ctx,mod,vat) -> add(Mod390Key.GP_C088,mod,vat.getBase())
			,null,null,null)
		// OPERACIONES NO SUJETAS
		,GP_C089  	(Mod390Key.GP_C089)
		// ENTREGAS DE BIENES OBJETO DE INSTALACI\u00D3N O MONTAJE EN OTROS ESTADOS MIEMBROS
		,GP_C090  	(Mod390Key.GP_C090)
		// ENTREGAS NO HABITUALES DE BIENES INMUEBLES
		,GP_C091  	(Mod390Key.GP_C091)
		// OPERACIONES FINANCIERAS NO HABITUALES
		,GP_C092  	(Mod390Key.GP_C092)
		// ENTREGAS DE BIENES DE INVERSI\u00D3N
		,GP_C093  	(Mod390Key.GP_C093)
		// TOTAL VOLUMEN DE OPERACIONES
		,GP_C095  	(Mod390Key.GP_C095)
		// ENTREGAS INTERIORES DE BIENES DEVENGADAS POR INVERSI\u00D3N DEL SUJETO PASIVO COMO CONSECUENCIA DE OPERACIONES TRIANGULARES
		,GP_C096  	(Mod390Key.GP_C096)
		// ADQUISICIONES INTERIORES EXENTAS
		,GP_C097  	(Mod390Key.GP_C097)
		// IMPORTACIONES EXENTAS
		,GP_C098  	(Mod390Key.GP_C098)
		// ADQUISICIONES INTRACOMUNITARIAS EXENTAS
		,GP_C099  	(Mod390Key.GP_C099)
		
		
		// Importes de las ventas a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran 
		// resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA		
		,GP_C101(Mod390Key.GP_C101,null,null,(ctx,mod) -> add(Mod390Key.GP_C101,mod,Mod390HFDAO.getVatAccrualPaymentOutputBase(ctx,mod)),null,null)
		,GP_C102(Mod390Key.GP_C102,null,null,(ctx,mod) -> add( Mod390Key.GP_C102, mod, Mod390HFDAO.getVatAccrualPaymentOutputQuota(ctx,mod) ),null,null)
		
		// Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el 
		// régimen especial del criterio de caja
		,GP_C103(Mod390Key.GP_C103,null,null,(ctx,mod) -> add(Mod390Key.GP_C103,mod,Mod390HFDAO.getVatAccrualPaymentInputBase(ctx,mod)),null,null)
		,GP_C104(Mod390Key.GP_C104,null,null,(ctx,mod) -> add(Mod390Key.GP_C104,mod, Mod390HFDAO.getVatAccrualPaymentInputQuota(ctx,mod) ),null,null)
		;
		
		private Mod390Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;

		private Mod390KeyDAO(Mod390Key key) {
			this(key,null,null,null,null,null);			
		}
		private Mod390KeyDAO(Mod390Key key
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
		public Mod390Key getKey() {
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
		public boolean acceptValue(Mod390HF mod,VatContext vctx) {
			return  acceptValue != null && acceptValue.accept(mod,vctx);
		}
		@Override
		public boolean hasAccepter() {
			return acceptValue != null;
		}
		@Override
		public void initialize(AONContext ctx,Mod390HF mod,VatContext vctx) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, vctx);
			}
		}
		@Override
		public void firstInitialize(AONContext ctx,Mod390HF mod) {
			if (firstInitializer != null) {
				firstInitializer.initialize(ctx, mod);
			}
		}
		public static Mod390KeyDAO safeValueOf(Mod390HF mod, String key) {
			if (AonStringUtils.isBlank(key)) return null; 
			for (Mod390KeyDAO keyDAO : Mod390KeyDAO.values()) {	
				if (keyDAO.getKey().getValue().equals(key) ) {
					return keyDAO;
				}
			}
			return null;
		}
	}
	@Override
	public IMod390KeyDAO[] getKeys() {
		return Mod390KeyDAO.values();
	}
	
	@Override
	public IMod390KeyDAO safeValueOf(Mod390HF mod, String key) {
		return Mod390KeyDAO.safeValueOf(mod, key);
	}
	
	@Override
	public IMod390KeyDAO valueOf(String keyValue) {
		return Mod390KeyDAO.valueOf(keyValue);
	}
	@Override
	public Mod390Key[] getProrateKeys() {
		return PRORATE_KEYS;
	}
	
	@Override
	public void specificInitialization(AONContext ctx, Mod390HF mod) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod.getYear());
		Date toDate = AonDateUtils.getYearLastDay(mod.getYear());
		LinkedList<InvoiceSeries> seriesList = InvoiceDAO.getInvoiceSeries(ctx, fromDate, toDate, false);
		Mod390Key[][] eKeys = new Mod390Key[][]{
			 new Mod390Key[]{Mod390Key.GP_SE1N,Mod390Key.GP_SE1D,Mod390Key.GP_SE1H,Mod390Key.GP_SE1X}
			,new Mod390Key[]{Mod390Key.GP_SE2N,Mod390Key.GP_SE2D,Mod390Key.GP_SE2H,Mod390Key.GP_SE2X}
			,new Mod390Key[]{Mod390Key.GP_SE3N,Mod390Key.GP_SE3D,Mod390Key.GP_SE3H,Mod390Key.GP_SE3X}
			,new Mod390Key[]{Mod390Key.GP_SE4N,Mod390Key.GP_SE4D,Mod390Key.GP_SE4H,Mod390Key.GP_SE4X}
			,new Mod390Key[]{Mod390Key.GP_SE5N,Mod390Key.GP_SE5D,Mod390Key.GP_SE5H,Mod390Key.GP_SE5X}
			};
		Mod390Key[][] rKeys = new Mod390Key[][]{
			 new Mod390Key[]{Mod390Key.GP_SR1N,Mod390Key.GP_SR1D,Mod390Key.GP_SR1H,Mod390Key.GP_SR1X}
			,new Mod390Key[]{Mod390Key.GP_SR2N,Mod390Key.GP_SR2D,Mod390Key.GP_SR2H,Mod390Key.GP_SR2X}
			,new Mod390Key[]{Mod390Key.GP_SR3N,Mod390Key.GP_SR3D,Mod390Key.GP_SR3H,Mod390Key.GP_SR3X}
			,new Mod390Key[]{Mod390Key.GP_SR4N,Mod390Key.GP_SR4D,Mod390Key.GP_SR4H,Mod390Key.GP_SR4X}
			,new Mod390Key[]{Mod390Key.GP_SR5N,Mod390Key.GP_SR5D,Mod390Key.GP_SR5H,Mod390Key.GP_SR5X}
		};
		int e = 0;
		int r = 0;
		for (InvoiceSeries series : seriesList) {
			if (series.isSeriesInfo()) {
				Mod390Key[][] keys = series.isSales()?eKeys:rKeys;
				int idx = series.isSales()?e:r;
				if (idx < 5) {
					Mod390Key seriesKeys = keys[idx][0];
					mod.putDescription(seriesKeys, series.getDescription());
					Mod390Key fromKeys = keys[idx][1];
					mod.putDescription(fromKeys, AonNumberUtils.toString( series.getFromNumber()));
					Mod390Key toKeys = keys[idx][2];
					mod.putDescription(toKeys, AonNumberUtils.toString( series.getToNumber()));
					Mod390Key countKeys = keys[idx][3];
					mod.putAmount(countKeys, series.getCount());
					if (series.isSales()) {
						++e;
					} else {
						++r;
					}
				}
			}
		}
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
	private static boolean hasPercent4(VatContext vat) {
		return vat.getPercentage() ==  PERCENT4 || vat.getPercentage() ==  PERCENT5; 	
	}
	private static boolean hasNoPercent(VatContext vat) {
		return vat.getPercentage() !=  PERCENT1 
			&& vat.getPercentage() !=  PERCENT2 
			&& vat.getPercentage() !=  PERCENT3
			&& vat.getPercentage() !=  PERCENT4
			&& vat.getPercentage() !=  PERCENT5; 	
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
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	private static boolean importacionesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
			&& !vat.isService()
			&& (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase());
	}
	private static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isFarmerRegime() && vat.isNationalPurchase();		
	}
	private static boolean rectificaciónDeduccionesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isRectification() && (vat.isPurchase() || vat.isExpenses()); 
	}
	
}
