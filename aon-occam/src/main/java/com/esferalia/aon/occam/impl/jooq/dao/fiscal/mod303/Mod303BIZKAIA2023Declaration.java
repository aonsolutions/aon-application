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

class Mod303BIZKAIA2023Declaration extends Mod303BIZKAIA {
	
	protected Mod303BIZKAIA2023Declaration() {
				
	}
	public static final double PERCENT_0 = 0.0;
	public static final double PERCENT_4 = 4.0;
	public static final double PERCENT_5 = 5.0;
	public static final double PERCENT_10 = 10.0;
	public static final double PERCENT_21 = 21.0;
	
	public static final double SURCHARGE_PERCENT_0 = 0.0;
	public static final double SURCHARGE_PERCENT_05 = 0.5;
	public static final double SURCHARGE_PERCENT_062 = 0.62;
	public static final double SURCHARGE_PERCENT_14 = 1.4;
	public static final double SURCHARGE_PERCENT_52 = 5.2;
	public static final double SURCHARGE_PERCENT_175 = 1.75;
	
	public static boolean accept(Mod303 mod) {
		return  mod.isBizkaia() && mod.getYear() >= 2023 && mod.getPeriod() != Period.T4;
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
		,BZ_C186	(Mod303Key.BZ_C186,null,null,(ctx,mod) -> add(Mod303Key.BZ_C186,mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment()?1:0))
		,BZ_C187	(Mod303Key.BZ_C187)		// Compras Criterio de caja. Se incializa en la casilla 202.
		,BZ_C188	(Mod303Key.BZ_C188)		// Opci\u00F3n por la aplicaci\u00F3n de la prorrata especial 
		,BZ_C189	(Mod303Key.BZ_C189)		// Revocaci\u00F3n de la opci\u00F3n por la aplicaci\u00F3n de la prorrata especial 
		,CM_002(Mod303Key.CM_002,null,null,(ctx,mod) -> add(Mod303Key.CM_002,mod,(AonStringUtils.equals(AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY),AonStringUtils.ONE))?1:0))
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al 0%
		,BZ_C048(Mod303Key.BZ_C048,(mod,vat) -> isCommonNationalSales(vat) && hasPercent0(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C048,mod,vat.getBase()))
		,BZ_X048(Mod303Key.BZ_X048,null,null,(ctx,mod) -> add(Mod303Key.BZ_X048,mod,PERCENT_0))
		,BZ_C049(Mod303Key.BZ_C049,(mod,vat) -> isCommonNationalSales(vat) && hasPercent0(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C004,mod,vat.getQuota()))
		// Base imponible, porcentaje y cuota al 4%.
		,BZ_C003(Mod303Key.BZ_C003,(mod,vat) -> isCommonNationalSales(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C003,mod,vat.getBase()))
		,BZ_X003(Mod303Key.BZ_X003,null,null,(ctx,mod) -> add(Mod303Key.BZ_X003,mod,PERCENT_4))
		,BZ_C004(Mod303Key.BZ_C004,(mod,vat) -> isCommonNationalSales(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C004,mod,vat.getQuota()))
		// Base imponible, porcentaje y cuota al 5%
		,BZ_C110(Mod303Key.BZ_C110,(mod,vat) -> isCommonNationalSales(vat) && hasPercent5(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C110,mod,vat.getBase()))
		,BZ_X110(Mod303Key.BZ_X110,null,null,(ctx,mod) -> add(Mod303Key.BZ_X110,mod,PERCENT_5))
		,BZ_C111(Mod303Key.BZ_C111,(mod,vat) -> isCommonNationalSales(vat) && hasPercent5(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C111,mod,vat.getQuota()))
		// Base imponible, porcentaje y cuota al 10%
		,BZ_C005	(Mod303Key.BZ_C005,(mod,vat) -> isCommonNationalSales(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C005,mod,vat.getBase()))
		,BZ_X005	(Mod303Key.BZ_X005,null,null,(ctx,mod) -> add(Mod303Key.BZ_X005,mod,PERCENT_10))
		,BZ_C006	(Mod303Key.BZ_C006,(mod,vat) -> isCommonNationalSales(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C006,mod,vat.getQuota()))
		// Base imponible, porcentaje y cuota al 21%
		,BZ_C007	(Mod303Key.BZ_C007,(mod,vat) -> isCommonNationalSales(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C007,mod,vat.getBase()))
		,BZ_X007	(Mod303Key.BZ_X007,null,null,(ctx,mod) -> add(Mod303Key.BZ_X007,mod,PERCENT_21))
		,BZ_C008	(Mod303Key.BZ_C008,(mod,vat) -> isCommonNationalSales(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C008,mod,vat.getQuota()))
		// Recargo equivalencia al 0%, 0.5% y 0.62%
		,BZ_C009	(Mod303Key.BZ_C009,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent05(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C009,mod,vat.getBase()))
		,BZ_X009	(Mod303Key.BZ_X009,null,null,(ctx,mod) -> add(Mod303Key.BZ_X009,mod,SURCHARGE_PERCENT_05))
		,BZ_C010	(Mod303Key.BZ_C010,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent05(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C010,mod,vat.getSurchargeQuota()))
		// Recargo equivalencia al 1.4%
		,BZ_C011	(Mod303Key.BZ_C011,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent14(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C011,mod,vat.getBase()))
		,BZ_X011	(Mod303Key.BZ_X011,null,null,(ctx,mod) -> add(Mod303Key.BZ_X011,mod,SURCHARGE_PERCENT_14))
		,BZ_C012	(Mod303Key.BZ_C012,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent14(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C012,mod,vat.getSurchargeQuota()))
		// Recargo equivalencia al 5.2%.
		,BZ_C013	(Mod303Key.BZ_C013,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent52(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C013,mod,vat.getBase()))
		,BZ_X013	(Mod303Key.BZ_X013,null,null,(ctx,mod) -> add(Mod303Key.BZ_X013,mod,SURCHARGE_PERCENT_52))
		,BZ_C014	(Mod303Key.BZ_C014,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent52(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C014,mod,vat.getSurchargeQuota()))
		// Recargo equivalencia al 1.75%
		,BZ_C015	(Mod303Key.BZ_C015,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent175(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C015,mod,vat.getBase()))
		,BZ_X015	(Mod303Key.BZ_X015,null,null,(ctx,mod) -> add(Mod303Key.BZ_X015,mod,SURCHARGE_PERCENT_175))
		,BZ_C016	(Mod303Key.BZ_C016,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent175(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C016,mod,vat.getSurchargeQuota()))
		// Adquisiciones intracomunitarias. Base y cuota.
		,BZ_C017	(Mod303Key.BZ_C017,(mod,vat) -> adqIntracomunitariasDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C017,mod,vat.getBase()))
		,BZ_C018	(Mod303Key.BZ_C018,(mod,vat) -> adqIntracomunitariasDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C018,mod,vat.getQuota()))
		// IVAS devengado por inversión del sujeto pasivo. Base y cuota
		,BZ_C019	(Mod303Key.BZ_C019,(mod,vat) -> operacionesISPDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C019,mod,vat.getBase()))
		,BZ_C020	(Mod303Key.BZ_C020		,(mod,vat) -> operacionesISPDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C020,mod,vat.getQuota()))	
		// Modificación bases y cuotas
		,BZ_C021	(Mod303Key.BZ_C021,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C021,mod,vat.getBase()))
		,BZ_C022	(Mod303Key.BZ_C022,(mod,vat) -> modificacionBasesYCuotasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C022,mod,(vat.getQuota() + vat.getSurchargeQuota())))
		// Modificación de bases y cuotas, artículo 80.3 y 80.4 NFIVA
		,BZ_C046	(Mod303Key.BZ_C046)
		,BZ_C047	(Mod303Key.BZ_C047)
		// Total cuota devengada
		,BZ_C023	(Mod303Key.BZ_C023,null,null,null,"BZ_C049+BZ_C004+BZ_C111+BZ_C006+BZ_C008+BZ_C010+BZ_C012+BZ_C014+BZ_C016+BZ_C018+BZ_C020+BZ_C022+BZ_C047",null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------
		// IVA deducible en operaciones interiores
		,BZ_C024	(Mod303Key.BZ_C024,(mod,vat) -> operacionesInterioresFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C024,mod,vat))
		// IVA deducible en importaciones
		,BZ_C025	(Mod303Key.BZ_C025,(mod,vat) -> importacionesFilter(vat,mod)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C025,mod,vat))
		// IVA deducible en adquisiciones intracomunitarias
		,BZ_C026	(Mod303Key.BZ_C026,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C026,mod,vat))
		// Compensaciones Régimen Especial A.G. y P .
		,BZ_C027	(Mod303Key.BZ_C027
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C027,mod,vat))
		// Regularización Inversiones
		,BZ_C028	(Mod303Key.BZ_C028)
		// Regularización Prorrata
		,BZ_C029	(Mod303Key.BZ_C029)
		// Total a deducir
		,BZ_C030	(Mod303Key.BZ_C030,null,null,null,"BZ_C024+BZ_C025+BZ_C026+BZ_C027+BZ_C028+BZ_C029",null)
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
			})
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
			})
		// Total deuda tributaria
		,BZ_C043	(Mod303Key.BZ_C043,null,null,null,"BZ_C036-BZ_C041+BZ_C042",null)
		
		// Exclusivamente para sujetos pasivos acogidos al régimen especial del criterio de caja y para 
		// destinatarios/as de operaciones afectadas por el mismo
		
		// Importes de las entregas de bienes y prestaciones de servicios a las que habiéndoles
		// sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas
		// conforme a la regla general de devengo contenida en el artículo 75 NFIVA
		,BZ_C200	(Mod303Key.BZ_C200 ,(mod, vat) -> vat.isSales() && vat.isVatAccrualRegime(), null)
		,BZ_C201	(Mod303Key.BZ_C201 ,(mod, vat) -> vat.isSales() && vat.isVatAccrualRegime(), null)		
		// Importes de las adquisiciones de bienes y servicios a las que sea aplicable o afecte el
		// régimen especial del criterio de caja
		,BZ_C202	(Mod303Key.BZ_C202, (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime(), null)
		,BZ_C203	(Mod303Key.BZ_C203, (mod, vat) -> vat.isNotSales() && vat.isVatAccrualRegime(), null)

		// ---------------------------------------------------------------
		// ----------------------------------------- INFORMACION ADICIONAL
		// ---------------------------------------------------------------

		// Compras de bienes corrientes al 4%
		,BZ_C050	(Mod303Key.BZ_C050,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C050,mod,vat.getBase()))
		,BZ_X050	(Mod303Key.BZ_X050,null,null,(ctx,mod) -> add(Mod303Key.BZ_X050,mod,PERCENT_4))
		,BZ_C051	(Mod303Key.BZ_C051,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C051,mod,vat.getQuota()))
		,BZ_C052	(Mod303Key.BZ_C052,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C052,mod,vat))
		// Compras de bienes corrientes al 5%
		,BZ_C112	(Mod303Key.BZ_C112,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent5(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C112,mod,vat.getBase()))
		,BZ_X112	(Mod303Key.BZ_X112,null,null,(ctx,mod) -> add(Mod303Key.BZ_X112,mod,PERCENT_5))
		,BZ_C113	(Mod303Key.BZ_C113,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent5(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C113,mod,vat.getQuota()))
		,BZ_C114	(Mod303Key.BZ_C114,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent5(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C114,mod,vat))
		// Compras de bienes corrientes al 10%
		,BZ_C053	(Mod303Key.BZ_C053,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent10(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C053,mod,vat.getBase()))
		,BZ_X053	(Mod303Key.BZ_X053,null,null,(ctx,mod) -> add(Mod303Key.BZ_X053,mod,PERCENT_10))
		,BZ_C054	(Mod303Key.BZ_C054,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C054,mod,vat.getQuota()))
		,BZ_C055	(Mod303Key.BZ_C055,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C055,mod,vat))
		// Compras de bienes corrientes al 21%
		,BZ_C056	(Mod303Key.BZ_C056,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C056,mod,vat.getBase()))
		,BZ_X056	(Mod303Key.BZ_X056,null,null,(ctx,mod) -> add(Mod303Key.BZ_X056,mod,PERCENT_21))
		,BZ_C057	(Mod303Key.BZ_C057,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C057,mod,vat.getQuota()))
		,BZ_C058	(Mod303Key.BZ_C058,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C058,mod,vat))
		// Compras de bienes corrientes al Reg. Agricultura
		,BZ_C059	(Mod303Key.BZ_C059,(mod,vat) -> isCommonAssetPurchaseFarmer(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C059,mod,vat.getBase()))
		,BZ_C060	(Mod303Key.BZ_C060,(mod,vat) -> isCommonAssetPurchaseFarmer(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C060,mod,vat.getQuota()))
		,BZ_C061	(Mod303Key.BZ_C061,(mod,vat) -> isCommonAssetPurchaseFarmer(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C061,mod,vat))
		// Compras de bienes corrientes a otro tipo
		,BZ_C062	(Mod303Key.BZ_C062,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasOtherPercent(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C062,mod,vat.getBase()))
		,BZ_C063	(Mod303Key.BZ_C063,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasOtherPercent(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C063,mod,vat.getQuota()))
		,BZ_C064	(Mod303Key.BZ_C064,(mod,vat) -> isCommonAssetPurchase(mod,vat) && hasOtherPercent(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C064,mod,vat))
		// Compras de bienes corrientes TOTAL
		,BZ_C065	(Mod303Key.BZ_C065,null,null,null,"BZ_C050+BZ_C112+BZ_C053+BZ_C056+BZ_C059+BZ_C062",null)
		,BZ_C066	(Mod303Key.BZ_C066,null,null,null,"BZ_C051+BZ_C113+BZ_C054+BZ_C057+BZ_C060+BZ_C063",null)
		,BZ_C067	(Mod303Key.BZ_C067,null,null,null,"BZ_C052+BZ_C114+BZ_C055+BZ_C058+BZ_C061+BZ_C064",null)
		
		// Gastos al 4%		
		,BZ_C068	(Mod303Key.BZ_C068,(mod,vat) -> isExpenses(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C068,mod,vat.getBase()))
		,BZ_X068	(Mod303Key.BZ_X068,null,null,(ctx,mod) -> add(Mod303Key.BZ_X068,mod,PERCENT_4))
		,BZ_C069	(Mod303Key.BZ_C069,(mod,vat) -> isExpenses(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C069,mod,vat.getQuota()))
		,BZ_C070	(Mod303Key.BZ_C070,(mod,vat) -> isExpenses(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C070,mod,vat))
		// Gastos al 5%
		,BZ_C115	(Mod303Key.BZ_C115,(mod,vat) -> isExpenses(vat) && hasPercent5(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C115,mod,vat.getBase()))
		,BZ_X115	(Mod303Key.BZ_X115,null,null,(ctx,mod) -> add(Mod303Key.BZ_X115,mod,PERCENT_5))
		,BZ_C116	(Mod303Key.BZ_C116,(mod,vat) -> isExpenses(vat) && hasPercent5(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C116,mod,vat.getQuota()))
		,BZ_C117	(Mod303Key.BZ_C117,(mod,vat) -> isExpenses(vat) && hasPercent5(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C117,mod,vat))
		// Gastos al 10%
		,BZ_C071	(Mod303Key.BZ_C071,(mod,vat) -> isExpenses(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C071,mod,vat.getBase()))
		,BZ_X071	(Mod303Key.BZ_X071,null,null,(ctx,mod) -> add(Mod303Key.BZ_X071,mod,PERCENT_10))
		,BZ_C072	(Mod303Key.BZ_C072,(mod,vat) -> isExpenses(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C072,mod,vat.getQuota()))
		,BZ_C073	(Mod303Key.BZ_C073,(mod,vat) -> isExpenses(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C073,mod,vat))
		// Gastos al 21%
		,BZ_C074	(Mod303Key.BZ_C074,(mod,vat) -> isExpenses(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C074,mod,vat.getBase()))
		,BZ_X074	(Mod303Key.BZ_X074,null,null,(ctx,mod) -> add(Mod303Key.BZ_X074,mod,PERCENT_21))
		,BZ_C075	(Mod303Key.BZ_C075,(mod,vat) -> isExpenses(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C075,mod,vat.getQuota()))
		,BZ_C076	(Mod303Key.BZ_C076,(mod,vat) -> isExpenses(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C076,mod,vat))
		// Gastos a otro tipo
		,BZ_C077	(Mod303Key.BZ_C077,(mod,vat) -> isExpenses(vat) && hasOtherExpensesPercent(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C077,mod,vat.getBase()))
		,BZ_C078	(Mod303Key.BZ_C078,(mod,vat) -> isExpenses(vat) && hasOtherExpensesPercent(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C078,mod,vat.getQuota()))
		,BZ_C079	(Mod303Key.BZ_C079,(mod,vat) -> isExpenses(vat) && hasOtherExpensesPercent(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C079,mod,vat))
		// Gastos TOTAL
		,BZ_C080	(Mod303Key.BZ_C080,null,null,null,"BZ_C068+BZ_C115+BZ_C071+BZ_C074+BZ_C077",null)
		,BZ_C081	(Mod303Key.BZ_C081,null,null,null,"BZ_C069+BZ_C116+BZ_C072+BZ_C075+BZ_C078",null)
		,BZ_C082	(Mod303Key.BZ_C082,null,null,null,"BZ_C070+BZ_C117+BZ_C073+BZ_C076+BZ_C079",null)
		
		// Bienes de inversión al 4%
		,BZ_C083	(Mod303Key.BZ_C083,(mod,vat) -> isInvestment(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C083,mod,vat.getBase()))
		,BZ_X083	(Mod303Key.BZ_X083,null,null,(ctx,mod) -> add(Mod303Key.BZ_X083,mod,PERCENT_4))
		,BZ_C084	(Mod303Key.BZ_C084,(mod,vat) -> isInvestment(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C084,mod,vat.getQuota()))
		,BZ_C085	(Mod303Key.BZ_C085,(mod,vat) -> isInvestment(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C085,mod,vat))
		// Bienes de inversión al 10%
		,BZ_C086	(Mod303Key.BZ_C086,(mod,vat) -> isInvestment(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C086,mod,vat.getBase()))
		,BZ_X086	(Mod303Key.BZ_X086,null,null,(ctx,mod) -> add(Mod303Key.BZ_X086,mod,PERCENT_10))
		,BZ_C087	(Mod303Key.BZ_C087,(mod,vat) -> isInvestment(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C087,mod,vat.getQuota()))
		,BZ_C088	(Mod303Key.BZ_C088,(mod,vat) -> isInvestment(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C088,mod,vat))
		// Bienes de inversión al 21%
		,BZ_C089	(Mod303Key.BZ_C089,(mod,vat) -> isInvestment(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C089,mod,vat.getBase()))
		,BZ_X089	(Mod303Key.BZ_X089,null,null,(ctx,mod) -> add(Mod303Key.BZ_X089,mod,PERCENT_21))
		,BZ_C090	(Mod303Key.BZ_C090,(mod,vat) -> isInvestment(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C090,mod,vat.getQuota()))
		,BZ_C091	(Mod303Key.BZ_C091,(mod,vat) -> isInvestment(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C091,mod,vat))
		// Bienes de inversión a otro tipo
		,BZ_C092	(Mod303Key.BZ_C092,(mod,vat) -> isInvestment(vat) && hasOtherInvestmentPercent(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C092,mod,vat.getBase()))
		,BZ_C093	(Mod303Key.BZ_C093,(mod,vat) -> isInvestment(vat) && hasOtherInvestmentPercent(vat)
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C093,mod,vat.getQuota()))
		,BZ_C094	(Mod303Key.BZ_C094,(mod,vat) -> isInvestment(vat) && hasOtherInvestmentPercent(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.BZ_C094,mod,vat))
		// Bienes de inversión TOTAL
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
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C104,mod,vat.getBase()))
		,BZ_C105	(Mod303Key.BZ_C105
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C105,mod,vat.getBase()))
		,BZ_C106	(Mod303Key.BZ_C106
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isService() && (vat.isCanCeuMelSales() || vat.isExtracommunitySales())
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C106,mod,vat.getBase()))
		,BZ_C107	(Mod303Key.BZ_C107
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isOtherISPSales()
			,(ctx,mod,vat) -> add(Mod303Key.BZ_C107,mod,vat.getBase()))
		,BZ_C108 (Mod303Key.BZ_C108)
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
				, IValueIntializer initializer) {
			this(key, acceptValue, initializer, null, null,null);
		}
		private Mod303KeyDAO(Mod303Key key
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueFirstIntializer firstInitializer) {
			this(key, acceptValue, initializer, firstInitializer, null,null);
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
	private static boolean isCommonAssetPurchase(Mod303 mod, VatContext vat) {
		return vat.isPurchase() 
			&& !vat.isService() 
			&& vat.isVatGeneralRegime(VATRegime.GENERAL)
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isFarmerRegime() 
			&& !vat.isInvestment()
			&& (!(vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase())
				|| importacionesFilter(vat, mod))
			;
	}
	private static boolean isCommonAssetPurchaseFarmer(VatContext vat) {
		return vat.isPurchase() 
			&& vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isFarmerRegime() 
			&& !vat.isInvestment();
	}
	private static boolean isExpenses(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& (vat.isExpenses() || (vat.isPurchase() && vat.isService())) 
			&& !vat.isFarmerRegime() 
			&& !vat.isInvestment();
	}
	private static boolean isInvestment(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isInvestment() 
			&& vat.isInput();
	}
	private static boolean isCommonNationalSales(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isNational() && vat.isSales() && !vat.isRectification();
	}
	private static boolean hasPercent0(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_0;	
	}
	private static boolean hasPercent4(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_4;	
	}
	private static boolean hasPercent5(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_5;	
	}
	private static boolean hasPercent10(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_10; 	
	}
	private static boolean hasPercent21(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_21; 	
	}
	private static boolean hasOtherPercent(VatContext vat) {
		return vat.getPercentage() !=  PERCENT_0 
			&& vat.getPercentage() !=  PERCENT_4
			&& vat.getPercentage() !=  PERCENT_5
			&& vat.getPercentage() !=  PERCENT_10 
			&& vat.getPercentage() !=  PERCENT_21; 	
	}
	private static boolean hasOtherExpensesPercent(VatContext vat) {
		return vat.getPercentage() !=  PERCENT_4
			&& vat.getPercentage() !=  PERCENT_5
			&& vat.getPercentage() !=  PERCENT_10 
			&& vat.getPercentage() !=  PERCENT_21; 	
	}
	private static boolean hasOtherInvestmentPercent(VatContext vat) {
		return vat.getPercentage() !=  PERCENT_4
			&& vat.getPercentage() !=  PERCENT_10 
			&& vat.getPercentage() !=  PERCENT_21; 	
	}
	private static boolean hasSurchargePercent05(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_05
			|| vat.getSurchargePercent() ==  SURCHARGE_PERCENT_0
			|| vat.getSurchargePercent() ==  SURCHARGE_PERCENT_062; 
	}
	private static boolean hasSurchargePercent14(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_14; 
	}
	private static boolean hasSurchargePercent52(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_52;
	}
	private static boolean hasSurchargePercent175(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_175; 
	}
	private static boolean adqIntracomunitariasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
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
			return DeclarationInfoUtil.getExplain(ctx, mod303, key
				, Mod303DAO.getSamePeriodEffectiveModels(ctx, mod303)
					.filter( Mod303::isToDeposit)
				, new DeclarationInfoUtil.ExplainRowManager());
		} else {
			return DeclarationInfoUtil.getExplain(ctx, mod303, key
				, Mod303DAO.getSamePeriodEffectiveModels(ctx, mod303)
					.filter( Mod303::isToPayback)
				, new DeclarationInfoUtil.ExplainRowManager());
		}
	}
	@Override
	protected String getCompensationExplain( AONContext ctx, Mod303 mod303, Mod303Key key) {
		if (mod303 .isFirstPeriod()) {
			return DeclarationInfoUtil.getExplain(ctx, mod303, key
				, Mod390HFDAO.getLastPeriodEffectiveModels(ctx, mod303)
					.filter(Mod390HF::isToCompensate)
				, new DeclarationInfoUtil.ExplainRowManager());
		} 
		return DeclarationInfoUtil.getExplain(ctx, mod303, key
				, Mod303DAO.getLastPeriodEffectiveModels(ctx, mod303)
					.filter(Mod303::isToCompensate)
				, new DeclarationInfoUtil.ExplainRowManager());
	}
	
	
}
