package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303Declaration;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390HFAraba2022Declaration extends Mod390HFArabaDeclaration {
	
	Mod390HFAraba2022Declaration() {
		
	}
	
	public static boolean accept(Mod390HF mod) {
		return  mod.isAraba() && mod.getYear() >= 2022;
	}
	
	private static final Mod390Key[] PRORATE_KEYS = new Mod390Key[]{
		 Mod390Key.AR_C045,Mod390Key.AR_C848,Mod390Key.AR_C851
		,Mod390Key.AR_C078,Mod390Key.AR_C881,Mod390Key.AR_C884
											,Mod390Key.AR_C356
		,Mod390Key.AR_C056,Mod390Key.AR_C859,Mod390Key.AR_C862
		,Mod390Key.AR_C089,Mod390Key.AR_C892,Mod390Key.AR_C895
											,Mod390Key.AR_C358
		,Mod390Key.AR_C067,Mod390Key.AR_C870,Mod390Key.AR_C873
		,Mod390Key.AR_C100,Mod390Key.AR_C363,Mod390Key.AR_C366
											,Mod390Key.AR_C360
											,Mod390Key.AR_C110
	};
	
	private static enum Mod390KeyDAO implements IMod390KeyDAO {
		 AR_C907	(Mod390Key.AR_C907)
		,AR_C918(Mod390Key.AR_C918,null,null,(ctx,mod) -> add(Mod390Key.AR_C918,mod,(
				 AonStringUtils.equals(AppParamDAO.fetchValue(ctx, AppParam.FS_TAX_REFUND_REGISTRY),AonStringUtils.ONE))?1:0),null,null)
		,AR_C910	(Mod390Key.AR_C910,null,null,(ctx,mod) -> add(Mod390Key.AR_C910,mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment()?1:0),null,null)
		,AR_C911	(Mod390Key.AR_C911)
		,AR_C908	(Mod390Key.AR_C908)
		,AR_C909	(Mod390Key.AR_C909)
		,CM_003		(Mod390Key.CM_003)
		,AR_C250	(Mod390Key.AR_C250)
		,AR_C251	(Mod390Key.AR_C251)
		,AR_C150	(Mod390Key.AR_C150)
		,AR_C152	(Mod390Key.AR_C152)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al primer tipo.
		,AR_C001	(Mod390Key.AR_C001
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C001,mod,vat.getBase())
			,null,null,null)
		,AR_C002	(Mod390Key.AR_C002,null,null,(ctx,mod) -> add(Mod390Key.AR_C002,mod,PERCENT_4),null,null)
		,AR_C003	(Mod390Key.AR_C003
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C003,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al segundo tipo.
		,AR_C804	(Mod390Key.AR_C804
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent10(vat)
			 ,(ctx,mod,vat) -> add(Mod390Key.AR_C804,mod,vat.getBase())
				,null,null,null)
		,AR_C805	(Mod390Key.AR_C805,null,null,(ctx,mod) -> add(Mod390Key.AR_C805,mod,PERCENT_10),null,null)
		,AR_C806	(Mod390Key.AR_C806
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C806,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al tercer tipo.
		,AR_C807	(Mod390Key.AR_C807
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent21(vat)
			 ,(ctx,mod,vat) -> add(Mod390Key.AR_C807,mod,vat.getBase())
			,null,null,null)
		,AR_C808	(Mod390Key.AR_C808,null,null,(ctx,mod) -> add(Mod390Key.AR_C808,mod,PERCENT_21),null,null)
		,AR_C809	(Mod390Key.AR_C809
			 ,(mod,vat) -> isCommonNationalSales(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C809,mod,vat.getQuota())
			,null,null,null)

		// Modificación bases y cuotas
		,AR_C351	(Mod390Key.AR_C351
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod390Key.AR_C351,mod,vat.getBase())
			,null,null,null)
		,AR_C352	(Mod390Key.AR_C352
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C352,mod,vat.getQuota())
			,null,null,null)
		
		// Otras operaciones con inversión del sujeto pasivo (excepto. adq. intracom). Base y cuota
		,AR_C019	(Mod390Key.AR_C019
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C019,mod,vat.getBase())
			,null,null,null)
		,AR_C020	(Mod390Key.AR_C020
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C020,mod,vat.getQuota())
			,null,null,null)
		
		// Concurso de acreedores
		,AR_C223	(Mod390Key.AR_C223)
		,AR_C224	(Mod390Key.AR_C224)
		
		// Recargo equivalencia al primer tipo.
		,AR_C025	(Mod390Key.AR_C025
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent05(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C025,mod,vat.getBase())
			,null,null,null)
		,AR_C026	(Mod390Key.AR_C026,null,null,(ctx,mod) -> add(Mod390Key.AR_C026,mod,SURCHARGE_PERCENT_05),null,null)
		,AR_C027	(Mod390Key.AR_C027
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent05(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C027,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al segundo tipo.
		,AR_C034	(Mod390Key.AR_C034
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent175(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C034,mod,vat.getBase())
			,null,null,null)
		,AR_C035	(Mod390Key.AR_C035,null,null,(ctx,mod) -> add(Mod390Key.AR_C035,mod,SURCHARGE_PERCENT_175),null,null)
		,AR_C036	(Mod390Key.AR_C036
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent175(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C036,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al tercer tipo.
		,AR_C828	(Mod390Key.AR_C828
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent14(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C828,mod,vat.getBase())
			,null,null,null)
		,AR_C829	(Mod390Key.AR_C829,null,null,(ctx,mod) -> add(Mod390Key.AR_C829,mod,SURCHARGE_PERCENT_14),null,null)
		,AR_C830	(Mod390Key.AR_C830
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent14(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C830,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al tercer tipo.
		,AR_C831	(Mod390Key.AR_C831
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent52(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C831,mod,vat.getBase())
			,null,null,null)
		,AR_C832	(Mod390Key.AR_C832,null,null,(ctx,mod) -> add(Mod390Key.AR_C832,mod,SURCHARGE_PERCENT_52),null,null)
		,AR_C833	(Mod390Key.AR_C833
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent52(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C833,mod,vat.getSurchargeQuota())
			,null,null,null)

		// Modificaciones bases y cuotas del recargo de equivalencia
		,AR_C037	(Mod390Key.AR_C037
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod390Key.AR_C037,mod,vat.getBase())
			,null,null,null)
		,AR_C038	(Mod390Key.AR_C038
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isNationalSales() && vat.isSurcharge() && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod390Key.AR_C038,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Concurso de acreedores
		,AR_C239	(Mod390Key.AR_C239)
		,AR_C240	(Mod390Key.AR_C240)
		
		// Adquisiciones intracomunitarias al primer tipo.		
		,AR_C010	(Mod390Key.AR_C010
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C010,mod,vat.getBase())
			,null,null,null)
		,AR_C011	(Mod390Key.AR_C011,null,null,(ctx,mod) -> add(Mod390Key.AR_C011,mod,PERCENT_4),null,null)
		,AR_C012	(Mod390Key.AR_C012
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C012,mod,vat.getQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias al segundo tipo.		
		,AR_C813	(Mod390Key.AR_C813
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C813,mod,vat.getBase())
			,null,null,null)
		,AR_C814	(Mod390Key.AR_C814,null,null,(ctx,mod) -> add(Mod390Key.AR_C814,mod,PERCENT_10),null,null)
		,AR_C815	(Mod390Key.AR_C815
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C815,mod,vat.getQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias al tercer tipo.		
		,AR_C816	(Mod390Key.AR_C816
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C816,mod,vat.getBase())
			,null,null,null)
		,AR_C817	(Mod390Key.AR_C817,null,null,(ctx,mod) -> add(Mod390Key.AR_C817,mod,PERCENT_21),null,null)
		,AR_C818	(Mod390Key.AR_C818
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification() && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C818,mod,vat.getQuota())
			,null,null,null)
		
		// Modificaciones bases y cuotas Adq. Intrac.
		,AR_C353	(Mod390Key.AR_C353
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod390Key.AR_C353,mod,vat.getBase())
			,null,null,null)
		,AR_C354	(Mod390Key.AR_C354
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && vat.isRectification()
			,(ctx,mod,vat) -> add(Mod390Key.AR_C354,mod,vat.getQuota())
			,null,null,null)
		
		// TOTAL CUOTA DEVENGADA
		,AR_C041	(Mod390Key.AR_C041,null,null,null,"AR_C003+AR_C806+AR_C809+AR_C352+AR_C020+AR_C224+AR_C027+AR_C036+AR_C830+AR_C833+AR_C038+AR_C240+AR_C012+AR_C815+AR_C818+AR_C354",null)

		// Minoraci\u00F3n por devoluci\u00F3n en r\u00E9gimen de viajeros
		,AR_C042	(Mod390Key.AR_C042)
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------

		// IVA deducible en operaciones interiores de bienes y servicios corrientes. Tipo 1.
		,AR_C043	(Mod390Key.AR_C043
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C043,mod,vat.getBase())
			,null,null,null)
		,AR_C044	(Mod390Key.AR_C044,null,null,(ctx,mod) -> add(Mod390Key.AR_C044,mod,PERCENT_4),null,null)
		,AR_C045	(Mod390Key.AR_C045
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C045,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en operaciones interiores de bienes y servicios corrientes. Tipo 2.
		,AR_C846	(Mod390Key.AR_C846
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C846,mod,vat.getBase())
			,null,null,null)
		,AR_C847	(Mod390Key.AR_C847,null,null,(ctx,mod) -> add(Mod390Key.AR_C847,mod,PERCENT_10),null,null)
		,AR_C848	(Mod390Key.AR_C848
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C848,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en operaciones interiores de bienes y servicios corrientes. Tipo 3.
		,AR_C849	(Mod390Key.AR_C849
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C849,mod,vat.getBase())
			,null,null,null)
		,AR_C850	(Mod390Key.AR_C850,null,null,(ctx,mod) -> add(Mod390Key.AR_C850,mod,PERCENT_21),null,null)
		,AR_C851	(Mod390Key.AR_C851
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C851,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en operaciones interiores de bienes de inversión. Tipo 1.
		,AR_C076	(Mod390Key.AR_C076
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat ) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C076,mod,vat.getBase())
			,null,null,null)
		,AR_C077	(Mod390Key.AR_C077,null,null,(ctx,mod) -> add(Mod390Key.AR_C077,mod,PERCENT_4),null,null)
		,AR_C078	(Mod390Key.AR_C078
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat ) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C078,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en operaciones interiores de bienes de inversión. Tipo 2.
		,AR_C879	(Mod390Key.AR_C879
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat ) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C879,mod,vat.getBase())
			,null,null,null)
		,AR_C880	(Mod390Key.AR_C880,null,null,(ctx,mod) -> add(Mod390Key.AR_C880,mod,PERCENT_10),null,null)
		,AR_C881	(Mod390Key.AR_C881
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat ) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C881,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en operaciones interiores de bienes de inversión. Tipo 3.
		,AR_C882	(Mod390Key.AR_C882
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat ) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C882,mod,vat.getBase())
			,null,null,null)
		,AR_C883	(Mod390Key.AR_C883,null,null,(ctx,mod) -> add(Mod390Key.AR_C883,mod,PERCENT_21),null,null)
		,AR_C884	(Mod390Key.AR_C884
			,(mod,vat) -> operacionesInterioresInversionFilter ( vat ) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C884,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Rectificación de deducciones en operaciones interiores
		,AR_C355	(Mod390Key.AR_C355
			,(mod,vat) -> operacionesInterioresRectifiedFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C355,mod,vat.getBase())
			,null,null,null)
		,AR_C356	(Mod390Key.AR_C356
			,(mod,vat) -> operacionesInterioresRectifiedFilter(vat) 
			,(ctx,mod,vat) -> add(Mod390Key.AR_C356,mod,vat.getQuota())
			,null,null,null)
		
		// IVA deducible en importaciones de bienes corrientes. Tipo 1.
		,AR_C054	(Mod390Key.AR_C054
			,(mod,vat) -> importacionesCorrientesFilter(vat,mod) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C054,mod,vat.getBase())
			,null,null,null)
		,AR_C055	(Mod390Key.AR_C055,null,null,(ctx,mod) -> add(Mod390Key.AR_C055,mod,PERCENT_4),null,null)
		,AR_C056	(Mod390Key.AR_C056
			,(mod,vat) -> importacionesCorrientesFilter(vat,mod) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C056,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en importaciones de bienes corrientes. Tipo 2.
		,AR_C857	(Mod390Key.AR_C857
			,(mod,vat) -> importacionesCorrientesFilter(vat,mod) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C857,mod,vat.getBase())
			,null,null,null)
		,AR_C858	(Mod390Key.AR_C858,null,null,(ctx,mod) -> add(Mod390Key.AR_C858,mod,PERCENT_10),null,null)
		,AR_C859	(Mod390Key.AR_C859
			,(mod,vat) -> importacionesCorrientesFilter(vat,mod) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C859,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en importaciones de bienes corrientes. Tipo 3.
		,AR_C860	(Mod390Key.AR_C860
			,(mod,vat) -> importacionesCorrientesFilter(vat,mod) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C860,mod,vat.getBase())
			,null,null,null)
		,AR_C861	(Mod390Key.AR_C861,null,null,(ctx,mod) -> add(Mod390Key.AR_C861,mod,PERCENT_21),null,null)
		,AR_C862	(Mod390Key.AR_C862
			,(mod,vat) -> importacionesCorrientesFilter(vat,mod) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C862,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en operaciones interiores de bienes de inversión. Tipo 1.
		,AR_C087	(Mod390Key.AR_C076
			,(mod,vat) -> importacionesInversionFilter(vat,mod) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C087,mod,vat.getBase())
			,null,null,null)
		,AR_C088	(Mod390Key.AR_C088,null,null,(ctx,mod) -> add(Mod390Key.AR_C088,mod,PERCENT_4),null,null)
		,AR_C089	(Mod390Key.AR_C089
			,(mod,vat) -> importacionesInversionFilter(vat,mod) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C089,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en operaciones interiores de bienes de inversión. Tipo 2.
		,AR_C890	(Mod390Key.AR_C890
			,(mod,vat) -> importacionesInversionFilter(vat,mod) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C890,mod,vat.getBase())
			,null,null,null)
		,AR_C891	(Mod390Key.AR_C891,null,null,(ctx,mod) -> add(Mod390Key.AR_C891,mod,PERCENT_10),null,null)
		,AR_C892	(Mod390Key.AR_C892
			,(mod,vat) -> importacionesInversionFilter(vat,mod) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C892,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en operaciones interiores de bienes de inversión. Tipo 3.
		,AR_C893	(Mod390Key.AR_C893
			,(mod,vat) -> importacionesInversionFilter(vat,mod) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C893,mod,vat.getBase())
			,null,null,null)
		,AR_C894	(Mod390Key.AR_C894,null,null,(ctx,mod) -> add(Mod390Key.AR_C894,mod,PERCENT_21),null,null)
		,AR_C895	(Mod390Key.AR_C895
			,(mod,vat) -> importacionesInversionFilter(vat,mod) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C895,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Rectificación de deducciones en importaciones
		,AR_C357	(Mod390Key.AR_C357
			,(mod,vat) -> importacionesRectifiedFilter(vat,mod)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C357,mod,vat.getBase())
			,null,null,null)
		,AR_C358	(Mod390Key.AR_C358
			,(mod,vat) -> importacionesRectifiedFilter(vat,mod) 
			,(ctx,mod,vat) -> add(Mod390Key.AR_C358,mod,vat.getQuota())
			,null,null,null)


		// IVA deducible en adquisiciones intracomunitarias de bienes y servicios corrientes. Tipo 1.
		,AR_C065	(Mod390Key.AR_C065
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C065,mod,vat.getBase())
			,null,null,null)
		,AR_C066	(Mod390Key.AR_C066,null,null,(ctx,mod) -> add(Mod390Key.AR_C066,mod,PERCENT_4),null,null)
		,AR_C067	(Mod390Key.AR_C067
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C067,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en adquisiciones intracomunitarias de bienes y servicios corrientes. Tipo 2.
		,AR_C868	(Mod390Key.AR_C868
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C868,mod,vat.getBase())
			,null,null,null)
		,AR_C869	(Mod390Key.AR_C869,null,null,(ctx,mod) -> add(Mod390Key.AR_C869,mod,PERCENT_10),null,null)
		,AR_C870	(Mod390Key.AR_C870
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C870,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en adquisiciones intracomunitarias de bienes y servicios corrientes. Tipo 3.
		,AR_C871	(Mod390Key.AR_C871
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C871,mod,vat.getBase())
			,null,null,null)
		,AR_C872	(Mod390Key.AR_C872,null,null,(ctx,mod) -> add(Mod390Key.AR_C872,mod,PERCENT_21),null,null)
		,AR_C873	(Mod390Key.AR_C873
			,(mod,vat) -> adqIntracomunitariasCorrientesFilter(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C873,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en adquisiciones intracomunitarias de bienes de inversión. Tipo 1.
		,AR_C098	(Mod390Key.AR_C098
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C098,mod,vat.getBase())
			,null,null,null)
		,AR_C099	(Mod390Key.AR_C099,null,null,(ctx,mod) -> add(Mod390Key.AR_C099,mod,PERCENT_4),null,null)
		,AR_C100	(Mod390Key.AR_C100
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C100,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en adquisiciones intracomunitarias de bienes de inversión. Tipo 2.
		,AR_C361	(Mod390Key.AR_C361
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C361,mod,vat.getBase())
			,null,null,null)
		,AR_C362	(Mod390Key.AR_C362,null,null,(ctx,mod) -> add(Mod390Key.AR_C362,mod,PERCENT_10),null,null)
		,AR_C363	(Mod390Key.AR_C363
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C363,mod,vat.getDeductibleQuota())
			,null,null,null)
		// IVA deducible en adquisiciones intracomunitarias de bienes de inversión. Tipo 3.
		,AR_C364	(Mod390Key.AR_C364
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C364,mod,vat.getBase())
			,null,null,null)
		,AR_C365	(Mod390Key.AR_C365,null,null,(ctx,mod) -> add(Mod390Key.AR_C365,mod,PERCENT_21),null,null)
		,AR_C366	(Mod390Key.AR_C366
			,(mod,vat) -> adqIntracomunitariasInversionFilter(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C366,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Rectificación de deducciones en adquisiciones intracomunitarias
		,AR_C359	(Mod390Key.AR_C359
			,(mod,vat) -> adqIntracomunitariasRectifiedFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C359,mod,vat.getBase())
			,null,null,null)
		,AR_C360	(Mod390Key.AR_C360
			,(mod,vat) -> adqIntracomunitariasRectifiedFilter(vat) 
			,(ctx,mod,vat) -> add(Mod390Key.AR_C360,mod,vat.getQuota())
			,null,null,null)

		// Compensaciones Régimen Especial A.G. y P .
		,AR_C109	(Mod390Key.AR_C109
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C109,mod,vat.getBase())
			,null,null,null)
		,AR_C110	(Mod390Key.AR_C110
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.AR_C110,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Regularización Inversiones
		,AR_C111	(Mod390Key.AR_C111)
		,AR_C112	(Mod390Key.AR_C112)

		// Regularización aplicación definitiva prorrata
		,AR_C115	(Mod390Key.AR_C115)
		
		// TOTAL A DEDUCIR
		,AR_C113	(Mod390Key.AR_C113,null,null,null,"AR_C045+AR_C848+AR_C851+AR_C078+AR_C881+AR_C884+AR_C356+AR_C056+AR_C859+AR_C862+AR_C089+AR_C892+AR_C895+AR_C358+AR_C067+AR_C870+AR_C873+AR_C100+AR_C363+AR_C366+AR_C360+AR_C110+AR_C112+AR_C115",null)
		
		// -----------------------------------------------------------
		// ------------------------------------------------- RESULTADO
		// -----------------------------------------------------------
		// DIFERENCIA
		,AR_C114	(Mod390Key.AR_C114,null,null,null,"AR_C041-AR_C042-AR_C113",null)
		
		// Volumen operaciones. % ÁLAVA
		,AR_C120	(Mod390Key.AR_C120,null,null,(ctx,mod) -> add(Mod390Key.AR_C120,mod,100.0),null,null)
		// Volumen operaciones. % GIPUZKOA
		,AR_C121	(Mod390Key.AR_C121)
		// Volumen operaciones. % BIZKAIA
		,AR_C122	(Mod390Key.AR_C122)
		// Volumen operaciones. % NAVARRA
		,AR_C123	(Mod390Key.AR_C123)
		// Volumen operaciones. % TERRITORIO COMUN
		,AR_C124	(Mod390Key.AR_C124)
		
		// Cuota atribuible al Territorio Histórico de Álava	
		,AR_C125	(Mod390Key.AR_C125,null,null,null,"AR_C114*AR_C120/100",null)
		// Ingresos efectuados en la Diputación Foral de Álava
		,AR_C126	(Mod390Key.AR_C126,null,null,
				(ctx,mod) -> {
					add( Mod390Key.AR_C126, mod, Mod390HFDAO.getM303YearModels(ctx, mod)
							.mapToDouble(fm -> fm.getAmount(Mod303Key.AR_C080))
							.filter(result -> AonMathUtils.isGreatherThanZero(result))
							.sum());
				} 
				,null
				,"<li>Declaraciones a ingresar en el mismo ejercicio:<ul style=\"padding-left: 20px;\">" 
						+"@code{c80Key='"+ Mod303Key.AR_C080.getValue() +"';}"
						+"@foreach{fm : m303Models}"
							+"@if{ fm.getAmount(c80Key) > 0 }"
								+"<li>Resultado @{fm.getPeriod().getName()} @{fm.isComplementary()?' (C) ':'     '}:	Casilla [080] --> @{fm.getAmount(c80Key)}</li>"
							+"@end{}"
						+"@end{}"
						+"</ul></li>"
						+"<li>Resultado: <b>@{AR_C126}</b></li>"
			)
		// Devoluciónes practicadas/solicitadas en la Diputación Foral de Álava
		,AR_C127	(Mod390Key.AR_C127,null,null,
				(ctx,mod) -> {
					add( Mod390Key.AR_C127, mod, Mod390HFDAO.getM303YearModels(ctx, mod)
							.mapToDouble(fm -> fm.getAmount(Mod303Key.AR_C081))
							.filter(result -> AonMathUtils.isNotZero(result))
							.sum());
				} 
				,null
				,"<li>Declaraciones a devolver en el mismo ejercicio:<ul style=\"padding-left: 20px;\">" 
						+"@code{c81Key='"+ Mod303Key.AR_C081.getValue() +"';}"
						+"@foreach{fm : m303Models}"
							+"@if{ fm.getAmount(c81Key) > 0 }"
								+"<li>Resultado @{fm.getPeriod().getName()} @{fm.isComplementary()?' (C) ':'     '}:	Casilla [081] --> @{fm.getAmount(c81Key)}</li>"
							+"@end{}"
						+"@end{}"
						+"</ul></li>"
						+"<li>Resultado: <b>@{AR_C127}</b></li>"
			)
		// DIFERENCIA
		,AR_C128	(Mod390Key.AR_C128,null,null,null,"AR_C126+AR_C127",null)
		// Resultado a compensar o a devolver o a ingresar del ejercicio
		,AR_C129	(Mod390Key.AR_C129,null,null,null,"AR_C125-AR_C128",null)
		// A compensar en el Territorio Histórico de \u00C1lava según declaración anual ejercicio anterior
		,AR_C130	(Mod390Key.AR_C130)
		// Resultado
		,AR_C13X	(Mod390Key.AR_C13X,null,null,null,"AR_C129-AR_C130",null)
		// Recargo presentación extemporánea	IVA deducible por importaciones de bienes corrientes
		,AR_C134	(Mod390Key.AR_C134)
		// Intereses demora	IVA deducible por importaciones de bienes de inversión
		,AR_C135	(Mod390Key.AR_C135)
		// TOTAL A COMPENSAR
		,AR_C140	(Mod390Key.AR_C140,null,null,null,"isToCompensate()?round(AR_C13X*-1):0.0",null)				
		// TOTAL A DEVOLVER
		,AR_C141	(Mod390Key.AR_C141,null,null,null,"isToPayback()?round(AR_C13X*-1):0.0",null)
		// TOTAL DEUDA TRIBUTARIA	
		,AR_C142	(Mod390Key.AR_C142,null,null,null,"isToDeposit()?AR_C13X:0.0",null)
		
		// -----------------------------------------------------------
		// ------------------------------------- INFORMACION ADICIONAL
		// -----------------------------------------------------------
		
		// Operaciones en r\u00E9gimen general
		,AR_C153	(Mod390Key.AR_C153
			,(mod,vat) -> vat.isNationalSales() && !vat.isVatAccrualRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.AR_C153,mod,vat.getBase())
			,null,null,null)
		// Operaciones a las que habi\u00E9ndoles sido aplicado el r\u00E9gimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 75 de la NFIVA
		,AR_C252	(Mod390Key.AR_C252
			,(mod,vat) -> vat.isNationalSales() && vat.isVatAccrualRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.AR_C252,mod,vat.getBase())
			,null,null,null)
		// Operaciones en Régimen Especial de la Agricultura, Ganadería y Pesca
		,AR_C154	(Mod390Key.AR_C154)
		// Operaciones en Régimen Especial Recargo de Equivalencia
		,AR_C155	(Mod390Key.AR_C155)
		// Entregas intracomunitarias exentas
		,AR_C156	(Mod390Key.AR_C156
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales() && !vat.isService()
			,(ctx,mod,vat) -> add(Mod390Key.AR_C156,mod,vat.getBase())
			,null,null,null)
		// Exportaciones y otras operaciones exentas con derecho a deducci\u00F3n
		,AR_C157	(Mod390Key.AR_C157
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isService() && (vat.isCanCeuMelSales() || vat.isExtracommunitySales())
			,(ctx,mod,vat) -> add(Mod390Key.AR_C157,mod,vat.getBase())
			,null,null,null)
		// Operaciones exentas sin derecho a deducción
		,AR_C158	(Mod390Key.AR_C158)
		// Entregas de bienes obj. de instal. o montaje en otros Estados miembros
		,AR_C210	(Mod390Key.AR_C210)
		// Operaciones con inversi\u00F3n del sujeto pasivo
		,AR_C211	(Mod390Key.AR_C211
			,(mod,vat) -> vat.isOtherISPSales() 
			,(ctx,mod,vat) -> add(Mod390Key.AR_C211,mod,vat.getBase())
			,null,null,null)
		// Entregas de bienes inmuebles y operaciones financieras no habituales
		,AR_C212	(Mod390Key.AR_C212)
		
		,AR_C214	(Mod390Key.AR_C214)	
		,AR_C215	(Mod390Key.AR_C215)
		,AR_C216	(Mod390Key.AR_C216)
		,AR_C217	(Mod390Key.AR_C217)
		,AR_C218	(Mod390Key.AR_C218)
		,AR_C219	(Mod390Key.AR_C219)
		,AR_C220	(Mod390Key.AR_C220)
		,AR_C221	(Mod390Key.AR_C221)
		
		// Entregas de bienes de inversi\u00F3n
		,AR_C161	(Mod390Key.AR_C161
			,(mod,vat) -> vat.isNationalSales() && vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.AR_C161,mod,vat.getBase())
			,null,null,null)
		// Total volumen de operaciones
		,AR_C162	(Mod390Key.AR_C162,null,null,null,"AR_C153+AR_C252+AR_C156+AR_C157+AR_C158+AR_C214+AR_C215+AR_C216+AR_C217+AR_C218+AR_C219+AR_C154+AR_C155+AR_C220+AR_C221-AR_C212-AR_C161",null)
		
		// Adquisiciones intracomunitarias de bienes exentas
		,AR_C163	(Mod390Key.AR_C163)
		// Entregas interiores de bienes devengadas por inversión del sujeto pasivo como consecuencia de operaciones triangulares
		,AR_C167	(Mod390Key.AR_C167)
		
		,AR_C253	(Mod390Key.AR_C253)
		,AR_C254	(Mod390Key.AR_C254)
		,AR_C255	(Mod390Key.AR_C255)
		,AR_C256	(Mod390Key.AR_C256)
		,AR_C257	(Mod390Key.AR_C257)
		,AR_C258	(Mod390Key.AR_C258)
		,AR_C259	(Mod390Key.AR_C259)
		,AR_C260	(Mod390Key.AR_C260)
		,AR_C261	(Mod390Key.AR_C261)
		
		// Importes de las ventas a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran 
		// resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA		
		,AR_C262	(Mod390Key.AR_C262)
		,AR_C263	(Mod390Key.AR_C263)
		
		// Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el 
		// régimen especial del criterio de caja
		,AR_C264	(Mod390Key.AR_C264)
		,AR_C265	(Mod390Key.AR_C265)
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
	public Mod390Key[] getProratedKeys() {
		return PRORATE_KEYS;
	}
	
	//	-----------------------------------------------------------------------	
	//	--------------------------------------------------------------- FILTROS	
	//	-----------------------------------------------------------------------
	private static boolean isCommonNationalSales(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isNational() && vat.isSales() && !vat.isRectification();
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
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
			&& (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses());
	}
	private static boolean operacionesInterioresCorrientesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL)
			&& !vat.isRectification()
			&& !vat.isVatSurchargeRegime()
			&& !vat.isInvestment() 
			&& !vat.isFarmerRegime()
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	
	private static boolean operacionesInterioresInversionFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isRectification()
			&& !vat.isVatSurchargeRegime()
			&& vat.isInvestment() 
			&& !vat.isFarmerRegime()
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	private static boolean operacionesInterioresRectifiedFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& vat.isRectification() 
			&& !vat.isVatSurchargeRegime()
			&& !vat.isFarmerRegime()
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	
	private static boolean importacionesCorrientesFilter(VatContext vat, Mod390HF mod) {
		return !vat.isRectification() 
			&& !vat.isInvestment()  
			&& importacionesFilter(vat, mod);
	}
	private static boolean importacionesInversionFilter(VatContext vat, Mod390HF mod) {
		return !vat.isRectification() 
			&& vat.isInvestment()  
			&& importacionesFilter(vat, mod);
	}
	private static boolean importacionesRectifiedFilter(VatContext vat, Mod390HF mod) {
		return vat.isRectification() 
			&& importacionesFilter(vat, mod);
	}
	private static boolean importacionesFilter(VatContext vat, Mod390HF mod) {
		boolean basicFilter = !vat.isVatSurchargeRegime() && !vat.isService();
		if (basicFilter && (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase())) {
			if (vat.getTaxDate().before( Mod303Declaration.IVA_2021_CHANGE_DATE )) {
				basicFilter = true;
			} else {
				basicFilter = vat.hasDuaLinked() || vat.isVatImportation();
			}
			return basicFilter; 
		}
		return false;
	}
	
	
	private static boolean adqIntracomunitariasCorrientesFilter(VatContext vat) {
		return adqIntracomunitariasFilter(vat)
			&& !vat.isRectification() 
			&& !vat.isInvestment();
	}
	private static boolean adqIntracomunitariasInversionFilter(VatContext vat) {
		return adqIntracomunitariasFilter(vat)
			&& !vat.isRectification() 
			&& vat.isInvestment();
	}
	private static boolean adqIntracomunitariasRectifiedFilter(VatContext vat) {
		return vat.isRectification() && adqIntracomunitariasFilter(vat);
	}
	
	private static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isFarmerRegime() && vat.isNationalPurchase();		
	}

	@Override
	double getResult(Mod390HF mod) {
		return mod.getAmount(Mod390Key.AR_C13X);
	}
	
	@Override
	Set<Integer> createVatAccrualKeysFromInvoices(AONContext ctx, Mod390HF mod) {
		return null;
	}
	
	
}
