package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class Mod390HFBizkaia2017Declaration extends Mod390HFBizkaiaDeclaration {
	
	private static final Mod390Key[][] E_KEYS = new Mod390Key[][]{
		 new Mod390Key[]{Mod390Key.BZ_SE1N,Mod390Key.BZ_SE1D,Mod390Key.BZ_SE1H}
		,new Mod390Key[]{Mod390Key.BZ_SE2N,Mod390Key.BZ_SE2D,Mod390Key.BZ_SE2H}
		,new Mod390Key[]{Mod390Key.BZ_SE3N,Mod390Key.BZ_SE3D,Mod390Key.BZ_SE3H}
		,new Mod390Key[]{Mod390Key.BZ_SE4N,Mod390Key.BZ_SE4D,Mod390Key.BZ_SE4H}
		,new Mod390Key[]{Mod390Key.BZ_SE5N,Mod390Key.BZ_SE5D,Mod390Key.BZ_SE5H}
		};
	private static final Mod390Key[][] R_KEYS = new Mod390Key[][]{
		 new Mod390Key[]{Mod390Key.BZ_SR1N,Mod390Key.BZ_SR1D,Mod390Key.BZ_SR1H}
		,new Mod390Key[]{Mod390Key.BZ_SR2N,Mod390Key.BZ_SR2D,Mod390Key.BZ_SR2H}
		,new Mod390Key[]{Mod390Key.BZ_SR3N,Mod390Key.BZ_SR3D,Mod390Key.BZ_SR3H}
		,new Mod390Key[]{Mod390Key.BZ_SR4N,Mod390Key.BZ_SR4D,Mod390Key.BZ_SR4H}
		,new Mod390Key[]{Mod390Key.BZ_SR5N,Mod390Key.BZ_SR5D,Mod390Key.BZ_SR5H}
	};

	Mod390HFBizkaia2017Declaration() {
	}
	
	static boolean accept(Mod390HF mod) {
		return  mod.isBizkaia() && mod.getYear() == 2017;
	}
	
	private static final Mod390Key[] PRORATE_KEYS = new Mod390Key[]{
		 Mod390Key.BZ_C060
		,Mod390Key.BZ_C061
		,Mod390Key.BZ_C062
		,Mod390Key.BZ_C063
		,Mod390Key.BZ_C064
		,Mod390Key.BZ_C065
		
		,Mod390Key.BZ_C144
		,Mod390Key.BZ_C147
		,Mod390Key.BZ_C150
		,Mod390Key.BZ_C153
		,Mod390Key.BZ_C156
		,Mod390Key.BZ_C162
		,Mod390Key.BZ_C165
		,Mod390Key.BZ_C168
		,Mod390Key.BZ_C171
		,Mod390Key.BZ_C177
		,Mod390Key.BZ_C180
		,Mod390Key.BZ_C183
		,Mod390Key.BZ_C186
	};
	
	private static enum Mod390KeyDAO implements IMod390KeyDAO {
		 BZ_C001D	(Mod390Key.BZ_C001D)
		,BZ_C001H	(Mod390Key.BZ_C001H)
		,BZ_C003	(Mod390Key.BZ_C003,null,null,(ctx,mod) -> add(Mod390Key.BZ_C003,mod,ConfigurationDAO.getConfiguration(ctx).getCompany().isVatAccrualPayment()?1:0),null,null)
		,BZ_C004	(Mod390Key.BZ_C004)		// Compras Criterio de caja. Se incializa en la casilla 133. Destinatario/a de operaciones a las que se aplica el r\u00E9gimen especial del criterio de caja
		,BZ_C005	(Mod390Key.BZ_C005)		// Opción por la aplicación de la prorrata especial		
		,BZ_C006	(Mod390Key.BZ_C006)		// Revocación de la opción por la aplicación de la prorrata especial		
		,BZ_C007	(Mod390Key.BZ_C007)		// Aplicar el régimen especial art. 163 Sexies. Cinco de la NF del IVA		
		,BZ_C080	(Mod390Key.BZ_C080) 	// RDM o Grupos de entidades		
		
		,BZ_A001	(Mod390Key.BZ_A001) 	// Actividad principal. Epigrafe. 
		,BZ_A002	(Mod390Key.BZ_A002) 	// Actividad principal. Descripcion.
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		
		// Base imponible, porcentaje y cuota al primer tipo.
		,BZ_C020(Mod390Key.BZ_C020
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C020,mod,vat.getBase())
			,null,null,null)
		,BZ_X020(Mod390Key.BZ_X020,null,null,(ctx,mod) -> add(Mod390Key.BZ_X020,mod,PERCENT_4),null,null)
		,BZ_C021(Mod390Key.BZ_C021
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C021,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al segundo tipo.
		,BZ_C022	(Mod390Key.BZ_C022
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C022,mod,vat.getBase())
			,null,null,null)
		,BZ_X022	(Mod390Key.BZ_X022,null,null,(ctx,mod) -> add(Mod390Key.BZ_X022,mod,PERCENT_10),null,null)
		,BZ_C023	(Mod390Key.BZ_C023
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C023,mod,vat.getQuota())
			,null,null,null)
		
		// Base imponible, porcentaje y cuota al tercer tipo.
		,BZ_C024	(Mod390Key.BZ_C024
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C024,mod,vat.getBase())
			,null,null,null)
		,BZ_X024	(Mod390Key.BZ_X024,null,null,(ctx,mod) -> add(Mod390Key.BZ_X024,mod,PERCENT_21),null,null)
		,BZ_C025	(Mod390Key.BZ_C025
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C025,mod,vat.getQuota())
			,null,null,null)

		// Operaciones intragrupo. Base imponible, porcentaje y cuota al primer tipo.
		,BZ_C026	(Mod390Key.BZ_C026)
		,BZ_X026	(Mod390Key.BZ_X026,null,null,(ctx,mod) -> add(Mod390Key.BZ_X026,mod,PERCENT_4),null,null)
		,BZ_C027	(Mod390Key.BZ_C027)
		// Operaciones intragrupo. Base imponible, porcentaje y cuota al segundo tipo.
		,BZ_C028	(Mod390Key.BZ_C028)
		,BZ_X028	(Mod390Key.BZ_X028,null,null,(ctx,mod) -> add(Mod390Key.BZ_X028,mod,PERCENT_10),null,null)
		,BZ_C029	(Mod390Key.BZ_C029)
		// Operaciones intragrupo. Base imponible, porcentaje y cuota al tercer tipo.
		,BZ_C030	(Mod390Key.BZ_C030)
		,BZ_X030	(Mod390Key.BZ_X030,null,null,(ctx,mod) -> add(Mod390Key.BZ_X030,mod,PERCENT_21),null,null)
		,BZ_C031	(Mod390Key.BZ_C031)
		
		// Recargo equivalencia al primer  tipo.
		,BZ_C032	(Mod390Key.BZ_C032
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent05(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C032,mod,vat.getBase())
			,null,null,null)
		,BZ_X032	(Mod390Key.BZ_X032,null,null,(ctx,mod) -> add(Mod390Key.BZ_X032,mod,SURCHARGE_PERCENT_05),null,null)
		,BZ_C033	(Mod390Key.BZ_C033
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent05(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C033,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Recargo equivalencia al segundo tipo.
		,BZ_C034	(Mod390Key.BZ_C034
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent14(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C034,mod,vat.getBase())
			,null,null,null)
		,BZ_X034	(Mod390Key.BZ_X034,null,null,(ctx,mod) -> add(Mod390Key.BZ_X034,mod,SURCHARGE_PERCENT_14),null,null)
		,BZ_C035	(Mod390Key.BZ_C035
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent14(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C035,mod,vat.getSurchargeQuota())
			,null,null,null)

		// Recargo equivalencia al tercer tipo.
		,BZ_C036	(Mod390Key.BZ_C036
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent52(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C036,mod,vat.getBase())
			,null,null,null)
		,BZ_X036	(Mod390Key.BZ_X036,null,null,(ctx,mod) -> add(Mod390Key.BZ_X036,mod,SURCHARGE_PERCENT_52),null,null)
		,BZ_C037	(Mod390Key.BZ_C037
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent52(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C037,mod,vat.getSurchargeQuota())
			,null,null,null)

		// Recargo equivalencia al cuarto tipo.
		,BZ_C038	(Mod390Key.BZ_C038
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent175(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C038,mod,vat.getBase())
			,null,null,null)
		,BZ_X038	(Mod390Key.BZ_X038,null,null,(ctx,mod) -> add(Mod390Key.BZ_X038,mod,SURCHARGE_PERCENT_175),null,null)
		,BZ_C039	(Mod390Key.BZ_C039
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent175(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C039,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		// Adquisiciones intracomunitarias. Base y cuota.
		,BZ_C040	(Mod390Key.BZ_C040
			,(mod,vat) -> adqIntracomunitariasDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C040,mod,vat.getBase())
			,null,null,null)
		,BZ_C041	(Mod390Key.BZ_C041
			,(mod,vat) -> adqIntracomunitariasDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C041,mod,vat.getQuota())
			,null,null,null)
		
		
		// IVAS devengado por inversión del sujeto pasivo. Base y cuota
		,BZ_C042	(Mod390Key.BZ_C042
			,(mod,vat) -> operacionesISPDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C042,mod,vat.getBase())
			,null,null,null)
		,BZ_C043	(Mod390Key.BZ_C043
			,(mod,vat) -> operacionesISPDevFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C043,mod,vat.getQuota())
			,null,null,null)	
		
		
		// Modificación bases y cuotas
		,BZ_C044	(Mod390Key.BZ_C044
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C044,mod,vat.getBase())
			,null,null,null)
		,BZ_C045	(Mod390Key.BZ_C045
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C045,mod,(vat.getQuota() + vat.getSurchargeQuota()))
			,null,null,null)

		// Modificación de bases y cuotas, artículo 80.3 y 80.4 NFIVA
		,BZ_C046	(Mod390Key.BZ_C046)
		,BZ_C047	(Mod390Key.BZ_C047)
		
		// Total cuota devengada
		,BZ_C048	(Mod390Key.BZ_C048,null,null,null,"BZ_C021+BZ_C023+BZ_C025+BZ_C027+BZ_C029+BZ_C031+BZ_C033+BZ_C035+BZ_C037+BZ_C039+BZ_C041+BZ_C043+BZ_C045+BZ_C047",null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------
		
		// IVA deducible en operaciones interiores
		,BZ_C060	(Mod390Key.BZ_C060
			,(mod,vat) -> operacionesInterioresFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C060,mod,vat.getDeductibleQuota())
			,null,null,null)
		,BZ_C061	(Mod390Key.BZ_C061)
		
		// IVA deducible en importaciones
		,BZ_C062	(Mod390Key.BZ_C062
			,(mod,vat) -> importacionesFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C062,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// IVA deducible en adquisiciones intracomunitarias
		,BZ_C063	(Mod390Key.BZ_C063
			,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C063,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		// Compensaciones Régimen Especial A.G. y P .
		,BZ_C064	(Mod390Key.BZ_C064
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C064,mod,vat.getDeductibleQuota())
			,null,null,null)
				
		// Regularización Inversiones
		,BZ_C065	(Mod390Key.BZ_C065)
		
		// Total a deducir
		,BZ_C066	(Mod390Key.BZ_C066,null,null,null,"BZ_C060+BZ_C061+BZ_C062+BZ_C063+BZ_C064+BZ_C065",null)

		// Diferencia
		,BZ_C095	(Mod390Key.BZ_C095,null,null,null,"BZ_C048-BZ_C066",null)
		
		// Regularización de cuotas (art.80.cinco.5a Norma Foral del IVA)
		,BZ_C120	(Mod390Key.BZ_C120)
		
		// Volumen de operaciones. Porcentaje de tributaci\u00F3n. Territ. común.
		,BZ_C081	(Mod390Key.BZ_C081,null,null,(ctx,mod) -> add(Mod390Key.BZ_C081,mod,0.0),null,null)
		,BZ_C082	(Mod390Key.BZ_C082,null,null,(ctx,mod) -> add(Mod390Key.BZ_C082,mod,0.0),null,null)
		// Volumen de operaciones. Porcentaje de tributaci\u00F3n. Alava
		,BZ_C083	(Mod390Key.BZ_C083,null,null,(ctx,mod) -> add(Mod390Key.BZ_C083,mod,0.0),null,null)
		,BZ_C084	(Mod390Key.BZ_C084,null,null,(ctx,mod) -> add(Mod390Key.BZ_C084,mod,0.0),null,null)
		// Volumen de operaciones. Porcentaje de tributaci\u00F3n. Gipuzkoa
		,BZ_C085	(Mod390Key.BZ_C085,null,null,(ctx,mod) -> add(Mod390Key.BZ_C085,mod,0.0),null,null)
		,BZ_C086	(Mod390Key.BZ_C086,null,null,(ctx,mod) -> add(Mod390Key.BZ_C086,mod,0.0),null,null)

		// ***********************************************************************************
		// ***********************************************************************************
		// ***********************************************************************************
		// Volumen de operaciones. Porcentaje de tributaci\u00F3n. Bizkaia
		,BZ_C087	(Mod390Key.BZ_C087,null,null,null,"BZ_C215",null)		  // TODO Respasar
		// ***********************************************************************************
		// ***********************************************************************************
		// ***********************************************************************************

		,BZ_C088	(Mod390Key.BZ_C088,null,null,(ctx,mod) -> add(Mod390Key.BZ_C088,mod,100.0),null,null)
		// Volumen de operaciones. Porcentaje de tributaci\u00F3n. Navarra		
		,BZ_C089	(Mod390Key.BZ_C089,null,null,(ctx,mod) -> add(Mod390Key.BZ_C089,mod,0.0),null,null)
		,BZ_C090	(Mod390Key.BZ_C090,null,null,(ctx,mod) -> add(Mod390Key.BZ_C090,mod,0.0),null,null)
		// Volumen de operaciones. Porcentaje de tributaci\u00F3n. Total		
		,BZ_C091	(Mod390Key.BZ_C091,null,null,null,null,null)
		,BZ_C092	(Mod390Key.BZ_C092,null,null,null,null,null)
//		,BZ_C091	(Mod390Key.BZ_C091,null,null,null,"BZ_C081+BZ_C083+BZ_C085+BZ_C087+BZ_C089",null)
//		,BZ_C092	(Mod390Key.BZ_C092,null,null,null,"BZ_C082+BZ_C084+BZ_C086+BZ_C088+BZ_C090",null)
		
		// Cuota atribuible a Bizkaia
		,BZ_C096	(Mod390Key.BZ_C096,null,null,null,"(BZ_C095+BZ_C120)*BZ_C088/100",null)
		
		// Cuota a compensar de periodos anteriores
		,BZ_C097	(Mod390Key.BZ_C097)
		
		// Diferencia
		,BZ_C098	(Mod390Key.BZ_C098,null,null,null,"BZ_C096-BZ_C097",null)

		// Ingresos efectuados en le Dip. Foral de Bizkaia
		,BZ_C099	(Mod390Key.BZ_C099)
		
		// Devoluciones practicadas en la Dip. Foral de Bizkaia
		,BZ_C100	(Mod390Key.BZ_C100)

		// Resultado
		,BZ_C110	(Mod390Key.BZ_C110,null,null,null,"BZ_C098-BZ_C099+BZ_C100",null)   
		
		// A compensar
		,BZ_C112	(Mod390Key.BZ_C112,null,null,null,"isToCompensate()?round(BZ_C110*-1):0.0",null)
		// A devolver
		,BZ_C113	(Mod390Key.BZ_C113,null,null,null,"isToPayback()?round(BZ_C110*-1):0.0",null)
		// A ingresar
		,BZ_C114	(Mod390Key.BZ_C114,null,null,null,"isToDeposit()?BZ_C110:0.0",null)
		
		// Cumplimentar s\u00F3lo en caso de que se trate de una autoliquidaci\u00F3n complementaria: ingresado anteriormente
		,BZ_C115	(Mod390Key.BZ_C115,null,null,
			(ctx,mod) -> {
				if (mod.isComplementary()) {
					add( Mod390Key.BZ_C115, mod, 
						Mod390HFDAO.getSamePeriodModels(ctx, mod)
							.mapToDouble(fm -> fm.getAmount(Mod390Key.BZ_C110))
							.filter(result -> AonMathUtils.isGreatherThanZero(result))
							.sum());						
				}
			} 
			,null
			,"<li>Declaraciones en el mismo periodo/ejercicio:<ul style=\"padding-left: 20px;\">" 
			+"@code{c36Key='"+ Mod390Key.BZ_C110.getValue() +"';}"
			+"@foreach{fm : periodModels}"
				+"@if{ fm.getAmount(c36Key) > 0 }"
					+"<li>Resultado @{fm.getPeriod().getName()}@{fm.isComplementary()?' (C) ':'     '}:	Casilla [110] --> @{fm.getAmount(c36Key)}</li>"
				+"@end{}"
			+"@end{}"
			+"</ul></li>"
			+"<li>Resultado: <b>@{BZ_C115}</b></li>"
			)
		// Cumplimentar s\u00F3lo en caso de que se trate de una autoliquidaci\u00F3n complementaria: devuelto anteriormente
		,BZ_C116	(Mod390Key.BZ_C116,null,null,
			(ctx,mod) -> {
				if (mod.isComplementary()) {
					add( Mod390Key.BZ_C116, mod, 
						Mod390HFDAO.getSamePeriodModels(ctx, mod)
							.mapToDouble(fm -> fm.getAmount(Mod390Key.BZ_C110))
							.filter(result -> AonMathUtils.isLessThanZero(result))
							.sum());						
				}
			} 
			,null
			,"<li>Declaraciones en el mismo periodo/ejercicio:<ul style=\"padding-left: 20px;\">" 
			+"@code{c36Key='"+ Mod390Key.BZ_C110.getValue() +"';}"
			+"@code{cm04Key='"+ Mod390Key.CM_004.getValue() +"';}"
			+"@code{compensateValue='"+ FiscalModelDeclarationType.COMPENSATE.getValue() +"';}"
			+"@foreach{fm : periodModels}"
				+"@if{ fm.getAmount(c36Key) < 0 && fm.getDescription(cm04Key) != compensateValue}"
					+"<li>Resultado @{fm.getPeriod().getName()}@{fm.isComplementary()?' (C) ':'     '}:	Casilla [110] --> @{fm.getAmount(c36Key)}</li>"
				+"@end{}"
			+"@end{}"
			+"</ul></li>"
			+"<li>Resultado: <b>@{BZ_C116}</b></li>"
			)
		// Total deuda tributaria
		,BZ_C117 (Mod390Key.BZ_C117,null,null,null,"BZ_C110-BZ_C115+BZ_C116",null)
		
		// Exclusivamente para sujetos pasivos acogidos al régimen especial del criterio de caja y para 
		// destinatarios/as de operaciones afectadas por el mismo
		
		// Importes de las entregas de bienes y prestaciones de servicios a las que habiéndoles
		// sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas
		// conforme a la regla general de devengo contenida en el artículo 75 NFIVA
		,BZ_C130	(Mod390Key.BZ_C130)
		,BZ_C131	(Mod390Key.BZ_C131)
		
		// Importes de las adquisiciones de bienes y servicios a las que sea aplicable o afecte el
		// régimen especial del criterio de caja
		,BZ_C132	(Mod390Key.BZ_C132)
		,BZ_C133	(Mod390Key.BZ_C133)

		// Existencias iniciales (1 de enero)
		,BZ_C140	(Mod390Key.BZ_C140)
		// Existencias finales (31 de diciembre)
		,BZ_C141	(Mod390Key.BZ_C141)
		
		// ---------------------------------------------------------------
		// ----------------------------------------- INFORMACION ADICIONAL
		// ---------------------------------------------------------------

		// Compras de bienes corrientes
		,BZ_C142	(Mod390Key.BZ_C142
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent4(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C142,mod,vat.getBase())
			,null,null,null)
		,BZ_X142	(Mod390Key.BZ_X142,null,null,(ctx,mod) -> add(Mod390Key.BZ_X142,mod,PERCENT_4),null,null)
		,BZ_C143	(Mod390Key.BZ_C143
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent4(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C143,mod,vat.getQuota())
			,null,null,null)
		,BZ_C144	(Mod390Key.BZ_C144
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent4(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C144,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,BZ_C145	(Mod390Key.BZ_C145
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent10(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C145,mod,vat.getBase())
			,null,null,null)
		,BZ_X145	(Mod390Key.BZ_X145,null,null,(ctx,mod) -> add(Mod390Key.BZ_X145,mod,PERCENT_10),null,null)
		,BZ_C146	(Mod390Key.BZ_C146
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent10(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C146,mod,vat.getQuota())
			,null,null,null)
		,BZ_C147	(Mod390Key.BZ_C147
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent10(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C147,mod,vat.getDeductibleQuota())
			,null,null,null)

		,BZ_C148	(Mod390Key.BZ_C148
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent21(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C148,mod,vat.getBase())
			,null,null,null)
		,BZ_X148	(Mod390Key.BZ_X148,null,null,(ctx,mod) -> add(Mod390Key.BZ_X148,mod,PERCENT_21),null,null)
		,BZ_C149	(Mod390Key.BZ_C149
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent21(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C149,mod,vat.getQuota())
			,null,null,null)
		,BZ_C150	(Mod390Key.BZ_C150
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasPercent21(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C150,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,BZ_C151	(Mod390Key.BZ_C151
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isPurchase() && !vat.isInvestment() && vat.isFarmerRegime()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C151,mod,vat.getBase())
			,null,null,null)
		,BZ_C152	(Mod390Key.BZ_C152
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isPurchase() && !vat.isInvestment() && vat.isFarmerRegime()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C152,mod,vat.getQuota())
			,null,null,null)
		,BZ_C153	(Mod390Key.BZ_C153
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isPurchase() && !vat.isInvestment() && vat.isFarmerRegime()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C153,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,BZ_C154	(Mod390Key.BZ_C154
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasNoPercent(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C154,mod,vat.getBase())
			,null,null,null)
		,BZ_C155	(Mod390Key.BZ_C155
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasNoPercent(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C155,mod,vat.getQuota())
			,null,null,null)
		,BZ_C156	(Mod390Key.BZ_C156
			,(mod,vat) -> vat.isPurchase() && !vat.isService() && hasNoPercent(vat) && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C156,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,BZ_C157	(Mod390Key.BZ_C157,null,null,null,"BZ_C142+BZ_C145+BZ_C148+BZ_C151+BZ_C154",null)
		,BZ_C158	(Mod390Key.BZ_C158,null,null,null,"BZ_C143+BZ_C146+BZ_C149+BZ_C152+BZ_C155",null)
		,BZ_C159	(Mod390Key.BZ_C159,null,null,null,"BZ_C144+BZ_C147+BZ_C150+BZ_C153+BZ_C156",null)

		// Gastos		
		,BZ_C160	(Mod390Key.BZ_C160
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService())) && hasPercent4(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C160,mod,vat.getBase())
			,null,null,null)
		,BZ_X160	(Mod390Key.BZ_X160,null,null,(ctx,mod) -> add(Mod390Key.BZ_X160,mod,PERCENT_4),null,null)
		,BZ_C161	(Mod390Key.BZ_C161
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent4(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C161,mod,vat.getQuota())
			,null,null,null)
		,BZ_C162	(Mod390Key.BZ_C162
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent4(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C162,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,BZ_C163	(Mod390Key.BZ_C163
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent10(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C163,mod,vat.getBase())
			,null,null,null)
		,BZ_X163	(Mod390Key.BZ_X163,null,null,(ctx,mod) -> add(Mod390Key.BZ_X163,mod,PERCENT_10),null,null)
		,BZ_C164	(Mod390Key.BZ_C164
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent10(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C164,mod,vat.getQuota())
			,null,null,null)
		,BZ_C165	(Mod390Key.BZ_C165
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent10(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C165,mod,vat.getDeductibleQuota())
			,null,null,null)

		,BZ_C166	(Mod390Key.BZ_C166
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent21(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C166,mod,vat.getBase())
			,null,null,null)
		,BZ_X166	(Mod390Key.BZ_X166,null,null,(ctx,mod) -> add(Mod390Key.BZ_X166,mod,PERCENT_21),null,null)
		,BZ_C167	(Mod390Key.BZ_C167
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent21(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C167,mod,vat.getQuota())
			,null,null,null)
		,BZ_C168	(Mod390Key.BZ_C168
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasPercent21(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C168,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,BZ_C169	(Mod390Key.BZ_C169
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C169,mod,vat.getBase())
			,null,null,null)
		,BZ_C170	(Mod390Key.BZ_C170
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C170,mod,vat.getQuota())
			,null,null,null)
		,BZ_C171	(Mod390Key.BZ_C171
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && (vat.isExpenses() || (vat.isPurchase() && vat.isService()))  && hasNoPercent(vat) && !vat.isFarmerRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C171,mod,vat.getDeductibleQuota())
			,null,null,null)

		,BZ_C172	(Mod390Key.BZ_C172,null,null,null,"BZ_C160+BZ_C163+BZ_C166+BZ_C169",null)
		,BZ_C173	(Mod390Key.BZ_C173,null,null,null,"BZ_C161+BZ_C164+BZ_C167+BZ_C170",null)
		,BZ_C174	(Mod390Key.BZ_C174,null,null,null,"BZ_C162+BZ_C165+BZ_C168+BZ_C171",null)

		// Bienes de inversión
		,BZ_C175	(Mod390Key.BZ_C175
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C175,mod,vat.getBase())
			,null,null,null)
		,BZ_X175	(Mod390Key.BZ_X175,null,null,(ctx,mod) -> add(Mod390Key.BZ_X175,mod,PERCENT_4),null,null)
		,BZ_C176	(Mod390Key.BZ_C176
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C176,mod,vat.getQuota())
			,null,null,null)
		,BZ_C177	(Mod390Key.BZ_C177
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C177,mod,vat.getDeductibleQuota())
			,null,null,null)
				
		,BZ_C178	(Mod390Key.BZ_C178
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C178,mod,vat.getBase())
			,null,null,null)
		,BZ_X178	(Mod390Key.BZ_X178,null,null,(ctx,mod) -> add(Mod390Key.BZ_X178,mod,PERCENT_10),null,null)
		,BZ_C179	(Mod390Key.BZ_C179
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C179,mod,vat.getQuota())
			,null,null,null)
		,BZ_C180	(Mod390Key.BZ_C180
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C180,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,BZ_C181	(Mod390Key.BZ_C181
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C181,mod,vat.getBase())
			,null,null,null)
		,BZ_X181	(Mod390Key.BZ_X181,null,null,(ctx,mod) -> add(Mod390Key.BZ_X181,mod,PERCENT_21),null,null)
		,BZ_C182	(Mod390Key.BZ_C182
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C182,mod,vat.getQuota())
			,null,null,null)
		,BZ_C183	(Mod390Key.BZ_C183
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C183,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,BZ_C184	(Mod390Key.BZ_C184
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C184,mod,vat.getBase())
			,null,null,null)
		,BZ_C185	(Mod390Key.BZ_C185
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C185,mod,vat.getQuota())
			,null,null,null)
		,BZ_C186	(Mod390Key.BZ_C186
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isFarmerRegime() && vat.isInvestment() && vat.isInput()  && hasNoPercent(vat)
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C186,mod,vat.getDeductibleQuota())
			,null,null,null)
		
		,BZ_C187	(Mod390Key.BZ_C187,null,null,null,"BZ_C175+BZ_C178+BZ_C181+BZ_C184",null)
		,BZ_C188	(Mod390Key.BZ_C188,null,null,null,"BZ_C176+BZ_C179+BZ_C182+BZ_C185",null)
		,BZ_C189	(Mod390Key.BZ_C189,null,null,null,"BZ_C177+BZ_C180+BZ_C183+BZ_C186",null)
		
		// Totales
		,BZ_C190	(Mod390Key.BZ_C190,null,null,null,"BZ_C157+BZ_C172+BZ_C187",null)
		,BZ_C191	(Mod390Key.BZ_C191,null,null,null,"BZ_C158+BZ_C173+BZ_C188",null)
		,BZ_C192	(Mod390Key.BZ_C192,null,null,null,"BZ_C159+BZ_C174+BZ_C189",null)

		// ---------------------------------------------------------------
		// ----------------------------------------------------- PRORRATAS
		// ---------------------------------------------------------------
		// Prorrata general
		,BZ_C193	(Mod390Key.BZ_C193)
		// Prorrata especial
		,BZ_C194	(Mod390Key.BZ_C194)
		
		// ---------------------------------------------------------------
		// ---------------------------------------- VOLUMEN DE OPERACIONES
		// ---------------------------------------------------------------
		// Operaciones en r\u00E9gimen general
		,BZ_C200	(Mod390Key.BZ_C200
			,(mod,vat) -> vat.isNationalSales() && !vat.isVatAccrualRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C200,mod,vat.getBase())
			,null,null,null)
		// Operaciones a las que habi\u00E9ndoles sido aplicado el r\u00E9gimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 75 de la NFIVA
		,BZ_C201	(Mod390Key.BZ_C201
			,(mod,vat) -> vat.isNationalSales() && vat.isVatAccrualRegime() && !vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C201,mod,vat.getBase())
			,null,null,null)
		// Entregas intracomunitarias exentas
		,BZ_C202	(Mod390Key.BZ_C202
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales() && !vat.isService()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C202,mod,vat.getBase())
			,null,null,null)
		// Exportaciones y otras operaciones exentas con derecho a deducci\u00F3n
		,BZ_C203	(Mod390Key.BZ_C203
			,(mod,vat) -> vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && !vat.isService() && (vat.isCanCeuMelSales() || vat.isExtracommunitySales())
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C203,mod,vat.getBase())
			,null,null,null)
		// Operaciones exentas sin derecho a deducci\u00F3n
		,BZ_C204	(Mod390Key.BZ_C204)
		// Operaciones no sujetas por reglas de localizaci\u00F3n
		,BZ_C205	(Mod390Key.BZ_C205)
		// Operaciones con inversi\u00F3n del sujeto pasivo
		,BZ_C206	(Mod390Key.BZ_C206
			,(mod,vat) -> vat.isOtherISPSales() 
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C206,mod,vat.getBase())
			,null,null,null)
		// Entregas de bienes objeto de instalaci\u00F3n o montaje en otros Estados miembros
		,BZ_C207	(Mod390Key.BZ_C207)
		// Operaciones en r\u00E9gimen simplificado
		,BZ_C208	(Mod390Key.BZ_C208)
		// Operaciones en r\u00E9gimen especial de la agricultura, ganader\u00EDa o pesca
		,BZ_C209	(Mod390Key.BZ_C209)
		// Operaciones realizadas por sujetos pasivos acogidos al r\u00E9gimen especial del recargo de equivalencia
		,BZ_C210	(Mod390Key.BZ_C210)
		// Operaciones en r\u00E9gimen especial de bienes usados, objetos de arte, antigüedades y objetos de colecci\u00F3n
		,BZ_C211	(Mod390Key.BZ_C211)
		// Operaciones en r\u00E9gimen especial de agencias de viajes
		,BZ_C212	(Mod390Key.BZ_C212)
		// Entregas de bienes inmuebles y operaciones financieras no habituales
		,BZ_C213	(Mod390Key.BZ_C213)
		// Entregas de bienes de inversi\u00F3n
		,BZ_C214	(Mod390Key.BZ_C214
			,(mod,vat) -> vat.isNationalSales() && vat.isInvestment()
			,(ctx,mod,vat) -> add(Mod390Key.BZ_C214,mod,vat.getBase())
			,null,null,null)
		// Total volumen de operaciones
		,BZ_C215	(Mod390Key.BZ_C215,null,null,null,"BZ_C200+BZ_C201+BZ_C202+BZ_C203+BZ_C204+BZ_C205+BZ_C206+BZ_C207+BZ_C208+BZ_C209+BZ_C210+BZ_C211+BZ_C212+BZ_C213+BZ_C214",null)

		// ---------------------------------------------------------------
		// ---------------------------------------- OPERACIONES ESPECIFICAS
		// ---------------------------------------------------------------
		// Otras operaciones no sujetas con derecho a deducci\u00F3n
		,BZ_C220	(Mod390Key.BZ_C214)
		// Otras operaciones no sujetas sin derecho a deducci\u00F3n
		,BZ_C221	(Mod390Key.BZ_C214)
		// Subvenciones a la explotaci\u00F3n
		,BZ_C222	(Mod390Key.BZ_C214)
		// Subvenciones de capital percibidas en el ejercicio
		,BZ_C223	(Mod390Key.BZ_C214)
		;
		
		private Mod390Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;

		private Mod390KeyDAO(Mod390Key key) {
			this(key, null, null, null, null,null);
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
			this.template =  template;
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
	
	private Mod390Key[][] getSeriresKeys( InvoiceSeries series ) {
		return series.isSales()?E_KEYS:R_KEYS;
	}

	@Override
	public void specificInitialization(AONContext ctx, Mod390HF mod) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod.getYear());
		Date toDate = AonDateUtils.getYearLastDay(mod.getYear());
		LinkedList<InvoiceSeries> seriesList = InvoiceDAO.getInvoiceSeries(ctx, fromDate, toDate, false);
		int e = 0;
		int r = 0;
		for (InvoiceSeries series : seriesList) {
			if (series.isSeriesInfo()) {
				Mod390Key[][] keys = getSeriresKeys(series);
				int idx = series.isSales()?e:r;
				if (idx < 5) {
					mod.putDescription(keys[idx][0], series.getDescription());
					mod.putDescription(keys[idx][1], AonNumberUtils.toString( series.getFromNumber()));
					mod.putDescription(keys[idx][2], AonNumberUtils.toString( series.getToNumber()));
					if (series.isSales()) {
						++e;
					} else {
						++r;
					}
				}
			}
		}
	}
	
	@Override
	double getResult(Mod390HF mod) {
		return  mod.getAmount(Mod390Key.BZ_C110);
	}
	
	@Override
	Set<Integer> createVatAccrualKeysFromInvoices(AONContext ctx, Mod390HF mod) {
		return null;
	}
	
	//	-----------------------------------------------------------------------	
	//	--------------------------------------------------------------- FILTROS	
	//	-----------------------------------------------------------------------
	static boolean isCommonNationalSales(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isNational() && vat.isSales() && !vat.isRectification();
	}
	static boolean hasPercent4(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_4;	
	}
	static boolean hasPercent10(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_10; 	
	}
	static boolean hasPercent21(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_21; 	
	}
	static boolean hasNoPercent(VatContext vat) {
		return vat.getPercentage() !=  PERCENT_4 && vat.getPercentage() !=  PERCENT_10 && vat.getPercentage() !=  PERCENT_21; 	
	}
	static boolean hasSurchargePercent05(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_05; 
	}
	static boolean hasSurchargePercent14(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_14; 
	}
	static boolean hasSurchargePercent52(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_52;
	}
	static boolean hasSurchargePercent175(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_175; 
	}
	static boolean adqIntracomunitariasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
			&& !vat.isService()
			&& vat.isIntracommunityPurchase();
	}
	static boolean adqIntracomunitariasDevFilter(VatContext vat) {
		return !vat.isRectification() && adqIntracomunitariasFilter(vat);
	}
	static boolean operacionesISPFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
				&& (vat.isOtherISPPurchase() 
					|| vat.isOtherISPExpenses() 
					|| vat.isExtracommunityExpenses() 
					|| vat.isCanCeuMelExpenses() 
					|| vat.isIntracommunityExpenses()
					|| (vat.isIntracommunityPurchase() && vat.isService())
					|| (vat.isExtracommunityPurchase() && vat.isService()) 
					|| (vat.isCanCeuMelPurchase() && vat.isService()));
	}
	static boolean operacionesISPDevFilter(VatContext vat) {
		return !vat.isRectification() && operacionesISPFilter(vat);
	}
	static boolean modificacionBasesYCuotasFilter(VatContext vat) {
		return vat.isRectification() && vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& (vat.isNationalSales() || adqIntracomunitariasFilter(vat) || operacionesISPFilter(vat));		
	}
	static boolean operacionesInterioresFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& !vat.isFarmerRegime() && AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || operacionesISPFilter(vat));
	}
	static boolean importacionesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& !vat.isService() 
			&& (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase());
	}
	static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isFarmerRegime() && vat.isNationalPurchase();		
	}
}
