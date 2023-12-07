package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel390Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.fiscal.mod390.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902023;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902023.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902023DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.mod390.SimpliedRegimeActivity;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303Declaration;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2023.AEATIVA2023;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2023.AEATIVA2023toMod390;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2023.Mod390toAEATIVA2023;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;


public class Mod3902023DAO {

	private static final byte ZERO_BYTE = 0;
	private static final byte ONE_BYTE = 1;
	
	public static final double PERCENT0 = 0.0;
	public static final double PERCENT4 = 4.0;
	public static final double PERCENT5 = 5.0;
	public static final double PERCENT10 = 10.0;
	public static final double PERCENT21 = 21.0;
	public static final double SURCHARGE_PERCENT00 = 0.0;
	public static final double SURCHARGE_PERCENT05 = 0.5;
	public static final double SURCHARGE_PERCENT062 = 0.62;
	public static final double SURCHARGE_PERCENT14 = 1.4;
	public static final double SURCHARGE_PERCENT52 = 5.2;
	public static final double SURCHARGE_PERCENT175 = 1.75;
	
	@FunctionalInterface
	public static interface IMod390DetailKey {
		boolean accept(Mod3902023 mod,VatContext vc);
	}

	private static class KeyedVatContext  {
		private Mod3902023DetailKeyDAO key;
		private VatContext vc;
		
		private KeyedVatContext(Mod3902023DetailKeyDAO key,VatContext vc) {
			this.key = key;
			this.vc = vc;
		}
		public Mod3902023DetailKeyDAO getKey() {
			return key;
		}
		public VatContext getVatContext() {
			return vc;
		}
	}
	
	public enum Mod3902023DetailKeyDAO implements Serializable {
		// IVA devengado
	 	// Régimen ordinario
		  C0701	(Mod3902023DetailKey.C0701, (mod, vc) -> isCommonNationalSales(vc, mod) && hasPercent0(vc))
		 ,C0002 (Mod3902023DetailKey.C0002, (mod, vc) -> isCommonNationalSales(vc, mod) && hasPercent4(vc))		
		 ,C0703	(Mod3902023DetailKey.C0703, (mod, vc) -> isCommonNationalSales(vc, mod) && hasPercent5(vc))
		 ,C0004	(Mod3902023DetailKey.C0004, (mod, vc) -> isCommonNationalSales(vc, mod) && hasPercent10(vc))
		 ,C0006	(Mod3902023DetailKey.C0006, (mod, vc) -> isCommonNationalSales(vc, mod) && hasPercent21(vc))
	 	// Operaciones intragrupo
		 ,C0705	(Mod3902023DetailKey.C0705, null)
		 ,C0501	(Mod3902023DetailKey.C0501, null)
		 ,C0707	(Mod3902023DetailKey.C0707, null)
		 ,C0503	(Mod3902023DetailKey.C0503, null)
		 ,C0505	(Mod3902023DetailKey.C0505, null)
	 	// Régimen especial de bienes usados, objetos de arte, antigüedades y objetos de colección
		 ,C0713	(Mod3902023DetailKey.C0713, null)
		 ,C0008	(Mod3902023DetailKey.C0008, null)
		 ,C0715	(Mod3902023DetailKey.C0715, null)
		 ,C0010	(Mod3902023DetailKey.C0010, null)
		 ,C0012	(Mod3902023DetailKey.C0012, null)
	 	// Régimen especial de agencias de viaje
		 ,C0014	(Mod3902023DetailKey.C0014, null)
	 	// Adquisiciones intracomunitarias de bienes
		 ,C0717	(Mod3902023DetailKey.C0717, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent0(vc))
		 ,C0022	(Mod3902023DetailKey.C0022, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent4(vc))
		 ,C0719	(Mod3902023DetailKey.C0719, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent5(vc))
		 ,C0024	(Mod3902023DetailKey.C0024, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent10(vc))
		 ,C0026	(Mod3902023DetailKey.C0026, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent21(vc))
	 	// Adquisiciones intracomunitarias de servicios
		 ,C0721	(Mod3902023DetailKey.C0721, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent0(vc))
		 ,C0546	(Mod3902023DetailKey.C0546, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent4(vc))
		 ,C0723	(Mod3902023DetailKey.C0723, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent5(vc))
		 ,C0548	(Mod3902023DetailKey.C0548, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent10(vc))
		 ,C0552	(Mod3902023DetailKey.C0552, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent21(vc))
	 	// IVA devengado en otros supuestos de inversión del sujeto pasivo
		 ,C0028	(Mod3902023DetailKey.C0028, (mod, vc) -> isOperacionesISPFilter(vc))
	 	// Modificación de bases y cuotas
		 ,C0030	(Mod3902023DetailKey.C0030, (mod, vc) -> isCommonNationalSalesRECT(vc, mod))
	 	// Modificación de bases y cuotas de operaciones intragrupo
		 ,C0650 (Mod3902023DetailKey.C0650, null)	
	 	// Modificación de bases y cuotas por auto de declaración de concurso de acreedores
		 ,C0032	(Mod3902023DetailKey.C0032, null)
	 	// Total bases y cuotas IVA
		 ,C0034	(Mod3902023DetailKey.C0034, null)
	 	// Recargo de equivalencia
		 ,C0664 (Mod3902023DetailKey.C0664, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent00(vc))
		 ,C0036 (Mod3902023DetailKey.C0036, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent05(vc))
		 ,C0666 (Mod3902023DetailKey.C0666, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent062(vc))
		 ,C0600 (Mod3902023DetailKey.C0600, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent14(vc))
		 ,C0602 (Mod3902023DetailKey.C0602, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent52(vc))
		 ,C0042	(Mod3902023DetailKey.C0042, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent175(vc))
		 ,C0044	(Mod3902023DetailKey.C0044, (mod, vc) -> isCommonNationalSalesRECT(vc, mod) && vc.isSurcharge())
	 	// Modificación recargo equivalencia
		 ,C0046	(Mod3902023DetailKey.C0046, null)
	 	//  Total cuotas IVA y recargo de equivalencia
		 ,C0047	(Mod3902023DetailKey.C0047, null)

		 // IVA deducible
		 // Operaciones interiores corrientes
		 // IVA deducible en operaciones interiores de bienes y servicios corrientes
		 ,C0191	(Mod3902023DetailKey.C0191, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && hasPercent4(vc)))
		 ,C0725	(Mod3902023DetailKey.C0725, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && hasPercent5(vc)))
		 ,C0604	(Mod3902023DetailKey.C0604, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && hasPercent10(vc)))
		 ,C0606	(Mod3902023DetailKey.C0606, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en operaciones interiores de bienes y servicios corrientes
		 ,C0049	(Mod3902023DetailKey.C0049, null)
		 // IVA deducible en operaciones intragrupo de bienes y servicios corrientes
		 ,C0507	(Mod3902023DetailKey.C0507, null)
		 ,C0727	(Mod3902023DetailKey.C0727, null)
		 ,C0608	(Mod3902023DetailKey.C0608, null)
		 ,C0610	(Mod3902023DetailKey.C0610, null)
		 // Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes y servicios corrientes
		 ,C0513	(Mod3902023DetailKey.C0513, null)
		 
		 // Operaciones interiores de bienes de inversión
		 // IVA deducible en operaciones interiores de bienes de inversión	 
		 ,C0197	(Mod3902023DetailKey.C0197, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && hasPercent4(vc)))
		 ,C0729	(Mod3902023DetailKey.C0729, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && hasPercent5(vc)))
		 ,C0612	(Mod3902023DetailKey.C0612, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && hasPercent10(vc)))
		 ,C0614	(Mod3902023DetailKey.C0614, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en operaciones interiores de bienes de inversión
		 ,C0051	(Mod3902023DetailKey.C0051, null)

		 // IVA deducible en operaciones intragrupo de bienes de inversión
		 ,C0515	(Mod3902023DetailKey.C0515, null)
		 ,C0731	(Mod3902023DetailKey.C0731, null)
		 ,C0616	(Mod3902023DetailKey.C0616, null)
		 ,C0618	(Mod3902023DetailKey.C0618, null)
		 // Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes de inversión
		 ,C0521	(Mod3902023DetailKey.C0521, null)
		 
		 // Importaciones y adquisiciones intracomunitarias de bienes y servicio
		 // IVA deducible en importaciones de bienes corrientes
		 ,C0203	(Mod3902023DetailKey.C0203, ((mod, vc) -> importacionesCorrientesFilter(vc) && hasPercent4(vc)))
		 ,C0733	(Mod3902023DetailKey.C0733, ((mod, vc) -> importacionesCorrientesFilter(vc) && hasPercent5(vc)))
		 ,C0620	(Mod3902023DetailKey.C0620, ((mod, vc) -> importacionesCorrientesFilter(vc) && hasPercent10(vc)))
		 ,C0622	(Mod3902023DetailKey.C0622, ((mod, vc) -> importacionesCorrientesFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en importaciones de bienes corrientes
		 ,C0053	(Mod3902023DetailKey.C0053, null)
		 
		 // IVA deducible en importaciones de bienes de inversión
		 ,C0209	(Mod3902023DetailKey.C0209, ((mod, vc) -> importacionesInversionFilter(vc) && hasPercent4(vc)))
		 ,C0735	(Mod3902023DetailKey.C0735, ((mod, vc) -> importacionesInversionFilter(vc) && hasPercent5(vc)))
		 ,C0624	(Mod3902023DetailKey.C0624, ((mod, vc) -> importacionesInversionFilter(vc) && hasPercent10(vc)))
		 ,C0626	(Mod3902023DetailKey.C0626, ((mod, vc) -> importacionesInversionFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en importaciones de bienes de inversión
		 ,C0055	(Mod3902023DetailKey.C0055, null)
		 
		 // IVA deducible en adquisiciones intracomunitarias de bienes corrientes
		 ,C0215	(Mod3902023DetailKey.C0215, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && hasPercent4(vc)))
		 ,C0737	(Mod3902023DetailKey.C0737, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && hasPercent5(vc)))
		 ,C0628	(Mod3902023DetailKey.C0628, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && hasPercent10(vc)))
		 ,C0630	(Mod3902023DetailKey.C0630, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes corrientes
		 ,C0057	(Mod3902023DetailKey.C0057, null)

		 // IVA deducible en adquisiciones intracomunitarias de bienes de inversión
		 ,C0221	(Mod3902023DetailKey.C0221, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && hasPercent4(vc)))
		 ,C0739	(Mod3902023DetailKey.C0739, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && hasPercent5(vc)))
		 ,C0632	(Mod3902023DetailKey.C0632, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && hasPercent10(vc)))
		 ,C0634	(Mod3902023DetailKey.C0634, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes de inversión
		 ,C0059	(Mod3902023DetailKey.C0059, null)
		 
		 // IVA deducible en adquisiciones intracomunitarias de servicios
		 ,C0588	(Mod3902023DetailKey.C0588, ((mod, vc) -> adqIntracomunitariasServicios(vc) && hasPercent4(vc)))
		 ,C0741	(Mod3902023DetailKey.C0741, ((mod, vc) -> adqIntracomunitariasServicios(vc) && hasPercent5(vc)))
		 ,C0636	(Mod3902023DetailKey.C0636, ((mod, vc) -> adqIntracomunitariasServicios(vc) && hasPercent10(vc)))
		 ,C0638	(Mod3902023DetailKey.C0638, ((mod, vc) -> adqIntracomunitariasServicios(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de servicios
		 ,C0598	(Mod3902023DetailKey.C0598, null)

		 // Compensación en régimen especial de la agricultura, ganaderia y pesca
		 ,C0061	(Mod3902023DetailKey.C0061, ((mod, vc) -> compensacionesRegAgrarioFilter(vc)))
		 // Cuotas deducibles en virtud de resolución administrativa o sentencia firmes con tipos no vigentes
		 ,C0661	(Mod3902023DetailKey.C0661, null)
		 // Rectificación de deducciones
		 ,C0062	(Mod3902023DetailKey.C0062, ((mod, vc) -> vc.isRectification() && (vc.isPurchase() || vc.isExpenses() )))
		 // Rectificación de deducciones por operaciones intragrupo
		 ,C0652	(Mod3902023DetailKey.C0652, null)
		 // Regularización de bienes de inversión
		 ,C0063	(Mod3902023DetailKey.C0063, null)
		 // Regularización por aplicación porcentaje definitivo de prorrata
		 ,C0522	(Mod3902023DetailKey.C0522, null)
		 // Suma de deducciones
		 ,C0064	(Mod3902023DetailKey.C0064, null)
		 
		 // Resultado régimen general
		 ,C0065	(Mod3902023DetailKey.C0065, null)
		 

		 // Operaciones en régimen general
		 ,C0099	 (Mod3902023DetailKey.C0099, ((mod, vc) -> (vc.isNationalSales() && vc.isVatGeneralRegime(mod.isSimplifiedRegime()?VATRegime.SIMPLIFIED:VATRegime.GENERAL))))
		 // Operaciones a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA
		 ,C0653	 (Mod3902023DetailKey.C0653, null) // MIRAR CASILLA 654
		 // Entregas intracomunitarias de bienes y servicios
		 ,C0103	 (Mod3902023DetailKey.C0103, ((mod, vc) -> (
				 vc.isIntracommunitySales() && 
				 !vc.isWithoutRightDeductionType())))

		 // Exportaciones y otras operaciones exentas con derecho a deducción
		 ,C0104	 (Mod3902023DetailKey.C0104, ((mod, vc) -> (vc.isSales() 
			&& !vc.isWithoutRightDeductionType() 
			&& vc.isExtracommunity())))
		 
		 // Operaciones exentas sin derecho a deducción
		 ,C0105	 (Mod3902023DetailKey.C0105, ((mod, vc) -> (vc.isSales() 
			&& !vc.isNational() && vc.isWithoutRightDeductionType())))
		 
		 // Operaciones no sujetas por reglas de localización (excepto las incluidas en la casilla 126)
		 ,C0110	 (Mod3902023DetailKey.C0110, ((mod, vc) -> (vc.isSales() 
			&& !vc.isWithoutRightDeductionType() 
			&& (vc.isOtherISP() || vc.isCanCeuMel()))))
		 
		 // Operaciones sujetas con inversión del sujeto pasivo
		 ,C0125	 (Mod3902023DetailKey.C0125, null)
		 // Operaciones no sujetas por reglas de localización acogidas a los regímenes especiales de ventanilla única
		 ,C0126	 (Mod3902023DetailKey.C0126, null)
		 // Operaciones sujetas y acogidas a los regímenes especiales de ventanilla única
		 ,C0127	 (Mod3902023DetailKey.C0127, null)
		 // Operaciones intragrupo valoradas conforme a lo dispuesto en los arts. 78 y 79 LIVA
		 ,C0128	 (Mod3902023DetailKey.C0128, null)
		 // Operaciones en régimen simplificado
		 ,C0100	 (Mod3902023DetailKey.C0100, ((mod, vc) -> (vc.isNationalSales() && vc.isVatSimplifiedRegime())))
		 // Operaciones en régimen especial de la agricultura, ganadería y pesca
		 ,C0101	 (Mod3902023DetailKey.C0101, null)
		 // Operaciones realizadas por sujetos pasivos acogidos al régimen especial del recargo de equivalencia
		 ,C0102	 (Mod3902023DetailKey.C0102, ((mod, vc) -> (vc.isNationalSales() && vc.isSurcharge())))
		 // Operaciones en Régimen especial de bienes usados, objetos de arte, antigüedades y objetos de colección
		 ,C0227	 (Mod3902023DetailKey.C0227, null)
		 // Operaciones en régimen especial de Agencias de Viajes
		 ,C0228	 (Mod3902023DetailKey.C0228, null)
		 // Entregas de bienes inmuebles, operaciones financieras y relativas al oro de inversión no habituales
		 ,C0106	 (Mod3902023DetailKey.C0106, null)
		 // Entregas de bienes de inversión
		 ,C0107	 (Mod3902023DetailKey.C0107, ((mod, vc) -> (vc.isNationalSales() && vc.isInvestment())))
		 // Total volumen de operaciones 
		 ,C0108	 (Mod3902023DetailKey.C0108, null)
		 
		 ;		 
		 
		private Mod3902023DetailKey key;
		private IMod390DetailKey accept;
			
		private Mod3902023DetailKeyDAO(Mod3902023DetailKey key, IMod390DetailKey accept) {
			this.key = key;
			this.accept = accept;
		}

		public Mod3902023DetailKey getKey() {
			return key;
		}
		public boolean accept(Mod3902023 mod, VatContext vc) {
			return accept!=null && accept.accept(mod,vc);
		}
		
//		public static Mod3902023DetailKey[] getKeys(Mod3902023 mod, VatContext vc) {
//			List<Mod3902023DetailKey> list = new LinkedList<>();
//			for (Mod3902023DetailKeyDAO key : Mod3902023DetailKeyDAO.values()) {
//				if (key.accept(mod,vc)) {
//					list.add(key.getKey()); 
//				}
//			}
//			return list.isEmpty()?null:list.toArray(new Mod3902023DetailKey[list.size()]);
//		}
	}

	
	private static EnumMap<Mod3902023DetailKey, Mod390Detail> getDetails(AONContext ctx, Mod3902023 mod390 ) {
		EnumMap<Mod3902023DetailKey, Mod390Detail> map = new EnumMap<>(Mod3902023DetailKey.class);
		Arrays.stream( Mod3902023DetailKey.values() )
			.forEach(key ->  map.put(key, new Mod390Detail().setKey(key).setPercent(key.getPercent())));
		VATDAO.getVatBreakdown(ctx, mod390)
			.flatMap(vt -> Arrays.stream( Mod3902023DetailKeyDAO.values() )
				.filter(key -> key.accept(mod390, vt))
				.map( key -> new KeyedVatContext(key, vt)))
			.map(kvt -> new Pair<KeyedVatContext,Mod390Detail>(kvt, map.computeIfAbsent(kvt.getKey().getKey(), k -> new Mod390Detail().setKey(k).setPercent(kvt.getVatContext().getPercentage()))))
			.forEach(Mod3902023DAO::add );
		
		// Regimen especial de criterio de caja.
		double vatAccBase = getVatAccrualPaymentOutputBase(ctx, mod390); 
		map.get(Mod3902023DetailKey.C0653).setTaxableBase( vatAccBase );
		map.get(Mod3902023DetailKey.C0654).setTaxableBase( vatAccBase );
		map.get(Mod3902023DetailKey.C0654).setQuota( getVatAccrualPaymentOutputQuota(ctx, mod390) );
		map.get(Mod3902023DetailKey.C0656).setTaxableBase( getVatAccrualPaymentInputBase(ctx, mod390) );
		map.get(Mod3902023DetailKey.C0656).setQuota( getVatAccrualPaymentInputQuota(ctx, mod390) );
		
		// Cálculo de la Regularizacion por aplicacion del porcentaje definitivo de prorrata
		
		// Si se asigna el valorhabría que sumar los declarado en las declaraciones y no sacer los datos de las facturas.
		
		
		Condition cond = FS_MODEL.YEAR.eq(mod390.getYear())
				.and(FS_MODEL.PERIOD.eq(Period.T4.value()).or(FS_MODEL.PERIOD.eq(Period.M12.value())) );
		Mod303DAO.getEffectiveModels(ctx, mod390, cond)
			.filter( m -> m.getYear( )  == mod390.getYear())
			.filter( Mod303::isAEAT)
			.filter( Mod303::isLastPeriod)
			.map(m -> m.getAmount(Mod303Key.CT_C44))
			.forEach( r -> map.computeIfAbsent(Mod3902023DetailKey.C0522, k -> new Mod390Detail().setKey(k)).setQuota(r))
		;
		return map;
	}
	
	private static void add(Pair<KeyedVatContext, Mod390Detail> pair) {
		KeyedVatContext kvt = pair.getLeft();
		Mod3902023DetailKey key = kvt.getKey().getKey();
		Mod390Detail detail = pair.getRight();
		double q = key.isSurcharge()?kvt.getVatContext().getSurchargeQuota():kvt.getVatContext().getQuota();
		if (key.isProrrataEnabled()) {
			q = kvt.getVatContext().getDeductibleQuota();
		}
		detail.setQuota( AonMathUtils.round(detail.getQuota()  + q));
		detail.setTaxableBase( AonMathUtils.round( detail.getTaxableBase() + kvt.getVatContext().getBase()));
	}
	private static void fillGeneralDeclarationResults(AONContext ctx, Mod3902023 mod390) {
		Mod303DAO.getEffectiveModels(ctx, mod390, FS_MODEL.YEAR.eq(mod390.getYear()))
			.forEach(m303 -> {
				if (m303.isEnrolledInDevolutionRegistry()) {
					mod390.setTaxRefund(true);
				}
				if (m303.isToDeposit()) {
					mod390.setBox95( AonMathUtils.round(mod390.getBox95() + m303.getDeclarationResult()));
				} else if (m303.isToPayback()) {
					if (m303.isEnrolledInDevolutionRegistry()) {
						mod390.setBox96( AonMathUtils.round(mod390.getBox96() + (m303.getDeclarationResult() * (-1))));
					}
					if (m303.isLastPeriod()) {
						mod390.setBox98( AonMathUtils.round( m303.getDeclarationResult() * (-1) ));
					} 
				} else if (m303.isToCompensate() && m303.isLastPeriod()) {
					mod390.setBox97(  AonMathUtils.round( m303.getDeclarationResult() * (-1) ));
				}
				if (m303.isFirstPeriod()) {
					mod390.setBox85(  m303.getAmount( Mod303Key.CT_C110) );
				} else if (m303.isLastPeriod()) {
					mod390.setBox662(  m303.getAmount( Mod303Key.CT_C87) );
				}
			});
		
	}
	private static void fillFromConfiguration(AONContext ctx, Mod3902023 mod390) {
		Company company = CompanyDAO.getCompany(ctx, mod390.getDomain());
		if (company != null) {
			ctx.getDslContext().select(RDIR_STAFF.DOCUMENT,RDIR_STAFF.NAME)
				.from(RDIR_STAFF)
				.where(RDIR_STAFF.DOMAIN.eq(mod390.getDomain()))
				.and(RDIR_STAFF.REGISTRY.eq(company.getId()))
				.and(RDIR_STAFF.REPRESENTATIVE.eq( (byte) 1))
				.fetch()
				.stream()
				.forEach( rec -> {
					String document = rec.getValue(RDIR_STAFF.DOCUMENT);
					String name = rec.getValue(RDIR_STAFF.NAME);
					
					if (mod390.isLegalEntity()) {
						LegalRepresentative legalRepr = new LegalRepresentative()
							.setDocument(document)
							.setName(name);
						if (mod390.getLegalRepr1() == null) mod390.setLegalRepr1(legalRepr); 
						else if (mod390.getLegalRepr2() == null) mod390.setLegalRepr2(legalRepr);
						else if (mod390.getLegalRepr3() == null) mod390.setLegalRepr3(legalRepr);
					} else {
						if (mod390.getAddress() == null) {
							mod390.setAddress(new Address().setRdocument(document).setRname(name));
						}
					}
				});
		}
	}
	
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	// **********************************************************************************************
	public static Mod3902023 create(AONContext ctx, Mod390 model) {
		Mod3902023 mod390 =  new Mod3902023();
		mod390.setId(model.getId());
		mod390.setDomain(model.getDomain());
		mod390.setDomainName(model.getDomainName());
		mod390.setEnterprise(model.getEnterprise());
		mod390.setEnterpriseName(model.getEnterpriseName());
		mod390.setYear(model.getYear());
		mod390.setAdministration(model.getAdministration());
		mod390.setStatus(model.getStatus());
		mod390.setOldStyle(false);
		mod390.setReplacement(model.isReplacement());
		mod390.setComplementary(model.isComplementary());
		mod390.setWithoutActivity(model.isWithoutActivity());
		mod390.setDocument(model.getDocument());
		mod390.setName(model.getName());
		mod390.setFirstSurname(model.getFirstSurname());
		mod390.setSecondSurname(model.getSecondSurname());
		mod390.setContactPhone(model.getContactPhone());
		mod390.setReceipt(model.getReceipt());
		mod390.setReplacedReceipt(model.getReplacedReceipt());
		mod390.setComments(model.getComments());
		mod390.setCreationUser(model.getCreationUser());
		mod390.setCreationDate(model.getCreationDate());
		mod390.setModificationUser(model.getModificationUser());
		mod390.setModificationDate(model.getModificationDate());
		
		boolean found = Mod390DAO.getByDomain(ctx, ctx.getDomainId())
			.stream()
			.filter( m390 -> m390.getYear() == 2021)
			.map( m390 -> Mod3902021DAO.getById(ctx, m390.getId()))
			.map( mod3902021 -> {
				mod390.setMainActivity(mod3902021.getMainActivity());
				mod390.setActivity1(mod3902021.getActivity1());
				mod390.setActivity2(mod3902021.getActivity2());
				mod390.setActivity3(mod3902021.getActivity3());
				mod390.setActivity4(mod3902021.getActivity4());
				mod390.setActivity5(mod3902021.getActivity5());
				mod390.setAddress(mod3902021.getAddress());
				mod390.setLegalRepr1(mod3902021.getLegalRepr1());
				mod390.setLegalRepr2(mod3902021.getLegalRepr2());
				mod390.setLegalRepr3(mod3902021.getLegalRepr3());
				return mod3902021;
			})
			.findAny()
			.isPresent()
		;
		found = found || Mod390DAO.getByDomain(ctx, ctx.getDomainId())
			.stream()
			.filter( m390 -> m390.getYear() == 2018 || m390.getYear() == 2019 || m390.getYear() == 2020)
			.map( m390 -> Mod3902018DAO.getById(ctx, m390.getId()))
			.map( mod3902018 -> {
				mod390.setMainActivity(mod3902018.getMainActivity());
				mod390.setActivity1(mod3902018.getActivity1());
				mod390.setActivity2(mod3902018.getActivity2());
				mod390.setActivity3(mod3902018.getActivity3());
				mod390.setActivity4(mod3902018.getActivity4());
				mod390.setActivity5(mod3902018.getActivity5());
				mod390.setAddress(mod3902018.getAddress());
				mod390.setLegalRepr1(mod3902018.getLegalRepr1());
				mod390.setLegalRepr2(mod3902018.getLegalRepr2());
				mod390.setLegalRepr3(mod3902018.getLegalRepr3());
				return mod3902018;
			})
			.findAny()
			.isPresent();
		
		if (!found) {
			fillFromConfiguration(ctx, mod390);	
		}
		fillSimplifedRegimeData(ctx, mod390);
		fillGeneralRegimeData(ctx, mod390);
		if (mod390.isSimplifiedRegime()) {
			fillSimplifiedDeclarationResults(ctx, mod390);
		} else {
			fillGeneralDeclarationResults(ctx, mod390);
		}
		mod390.calculate();
		return mod390;	
	}

	public static Mod3902023 getMod3902023(AONContext ctx, Mod390 m390) {
		if (m390.getId() == null) {
			return create(ctx, m390);
		}
		return getById(ctx, m390.getId());
	}

	public static Mod3902023 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod3902023 mod390 = new Mod3902023();
		ctx.getDslContext()
				.selectFrom(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(id))
				.fetch()
				.stream()
				.forEach(rec -> populate(rec, mod390));
		if (mod390.getId() != null) {
			return mod390;
		}
		return null;
	}

	private static void populate(FsModel390Record rec, Mod3902023 mod390)  {
		mod390
		.setId(rec.getValue(FS_MODEL390.ID))
		.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,rec.getValue(FS_MODEL390.ADMINISTRATION)))
		.setReplacement( rec.getValue(FS_MODEL390.REPLACEMENT)==1 )
		.setComplementary(rec.getValue(FS_MODEL390.COMPLEMENTARY)==1 )
		.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,rec.getValue(FS_MODEL390.STATUS)))
		.setYear(rec.getValue(FS_MODEL390.YEAR))
		.setDomain(rec.getValue(FS_MODEL390.DOMAIN))
		.setEnterprise(rec.getValue(FS_MODEL390.ENTERPRISE))
		.setDocument(rec.getValue(FS_MODEL390.DOCUMENT))
		.setEnterpriseName(rec.getValue(FS_MODEL390.NAME))
		.setReceipt(rec.getValue(FS_MODEL390.RECEIPT))
		.setReplacedReceipt(rec.getValue(FS_MODEL390.REPLACED_RECEIPT))
		.setComments(rec.getValue(FS_MODEL390.COMMENTS));
		
		String model = rec.getValue(FS_MODEL390.MODEL);
		if (AonStringUtils.contains(model, "<AEATIVA2023>"))  {
			StringReader reader = new StringReader(rec.getValue(FS_MODEL390.MODEL));
			try {
				if (mod390.getYear() >= 2023) {
					JAXBContext context = JAXBContext.newInstance(AEATIVA2023.class);
					Unmarshaller um = context.createUnmarshaller();
					AEATIVA2023 iva = (AEATIVA2023) um.unmarshal(reader);
					AEATIVA2023toMod390.populate(mod390, iva);
					mod390.setXmlFormat("AEAT_2023");
				} else {
					throw new IllegalArgumentException("Ejercicio incorrecto.");
				}
			} catch (ParseException | JAXBException e1) {
				e1.printStackTrace();
				throw new AonCoreException("XML PROBLEM",e1);
			}
		} else {
			mod390.setXmlFormat("INVALID");
		}
		
	}
	
	public static String getXMLContentById(AONContext ctx, Integer id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL390)
			.where(FS_MODEL390.ID.equal(id))
			.fetchOne(FS_MODEL390.MODEL);
	}


	public static Mod3902023 save(AONContext ctx, Mod3902023 mod390)  {
		if (mod390.getId() == null) {
			return insert(ctx, mod390);
		} else {
			return update(ctx, mod390);
		}
	}
	
	private static String getXMLModel( Mod3902023 mod390 ) {
		try {
			if (mod390.getYear() >= 2023) {
				AEATIVA2023 iva = Mod390toAEATIVA2023.getAEATIVA2023(mod390);
				StringWriter writer = new StringWriter();
				JAXBContext context = JAXBContext.newInstance(AEATIVA2023.class);
				Marshaller um = context.createMarshaller();
				um.setProperty("jaxb.encoding", "ISO-8859-1");
				um.marshal(iva,writer);
				return AonStringUtils.trim(writer.toString());
			} else {
				throw new IllegalArgumentException("Ejercicio incorrecto.");
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new AonCoreException("Error en conversión XML",e);
		}				
	}
	

	private static Mod3902023 insert(AONContext ctx, Mod3902023 mod390) {
		validate(ctx, mod390);
		FsModel390Record rec = ctx.getDslContext()
			.insertInto(FS_MODEL390)
			.set(FS_MODEL390.DOMAIN, mod390.getDomain())
			.set(FS_MODEL390.ENTERPRISE, mod390.getEnterprise())
			.set(FS_MODEL390.YEAR, mod390.getYear())
			.set(FS_MODEL390.ADMINISTRATION,mod390.getAdministration().value())
			.set(FS_MODEL390.STATUS,AonEnumUtils.getByte( mod390.getStatus()  ))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod390.isConfidential()))
			.set(FS_MODEL390.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL390.REPLACEMENT,AonEnumUtils.getByte(mod390.isReplacement()))
			.set(FS_MODEL390.DOCUMENT, mod390.getDocument())
			.set(FS_MODEL390.NAME, mod390.getName())
			.set(FS_MODEL390.COMMENTS, mod390.getComments())
			.set(FS_MODEL390.RECEIPT, mod390.getReceipt())
			.set(FS_MODEL390.REPLACED_RECEIPT, mod390.getReplacedReceipt())
			.set(FS_MODEL390.MODEL, getXMLModel(mod390))
			.returning(FS_MODEL390.ID).fetchOne();
		mod390.setId(rec.getId());
		return mod390;
	}
	
	private static Mod3902023 update(AONContext ctx, Mod3902023 mod390) {
		validate(ctx, mod390);
		mod390.calculate();
		ctx.getDslContext()
			.update(FS_MODEL390)
			.set(FS_MODEL390.YEAR, mod390.getYear())
			.set(FS_MODEL390.ADMINISTRATION,mod390.getAdministration().value())
			.set(FS_MODEL390.STATUS,AonEnumUtils.getByte( mod390.getStatus()  ))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod390.isConfidential()))
			.set(FS_MODEL390.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL390.REPLACEMENT,AonEnumUtils.getByte(mod390.isReplacement()))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod390.isConfidential()))
			.set(FS_MODEL390.DOCUMENT, mod390.getDocument())
			.set(FS_MODEL390.NAME, mod390.getName())
			.set(FS_MODEL390.COMMENTS, mod390.getComments())
			.set(FS_MODEL390.RECEIPT, mod390.getReceipt())
			.set(FS_MODEL390.REPLACED_RECEIPT, mod390.getReplacedReceipt())
			.set(FS_MODEL390.MODEL, getXMLModel(mod390))
			.where(FS_MODEL390.ID.equal(mod390.getId())).execute();
		return mod390;
	}

	private static void validate(AONContext ctx, Mod3902023 mod390) {
		if (mod390.isReplacement()) {
			// Se comprueba que exista la declaración sustituida.
			if (!ctx.getDslContext().selectOne()
					.from(FS_MODEL390)
					.where(FS_MODEL390.YEAR.equal(mod390.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod390.getEnterprise()))
//					.and(FS_MODEL390.RECEIPT.equal(mod390.getReplacedReceipt()))
					)
					.fetch()
					.stream()
					.findFirst()
					.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());
			

			// Se comprueba que no exista una declaración sustitutiva.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL390)
					.where(FS_MODEL390.YEAR.equal(mod390.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod390.getEnterprise()))
					.and(FS_MODEL390.REPLACEMENT.equal( ONE_BYTE ))					
					.and(FS_MODEL390.REPLACED_RECEIPT.equal(mod390.getReplacedReceipt())))
					.and(	(mod390.getId()!=null)
							?FS_MODEL390.ID.ne(mod390.getId())
							:FS_MODEL390.ID.eq(FS_MODEL390.ID)
							)
					.fetch()
					.stream()
					.findFirst()
					.isPresent()) 
					throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());
		} else {
			// Se comprueba que no exista ya una declaración.
			if (ctx.getDslContext().selectOne()
				.from(FS_MODEL390)
				.where(FS_MODEL390.YEAR.equal(mod390.getYear())
				.and(FS_MODEL390.ENTERPRISE.equal(mod390.getEnterprise()))
				.and(FS_MODEL390.REPLACEMENT.equal(ZERO_BYTE)))
				.and(	(mod390.getId()!=null)
						?FS_MODEL390.ID.ne(mod390.getId())
						:FS_MODEL390.ID.eq(FS_MODEL390.ID)
						)
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	}

	public static void delete(AONContext ctx, Mod3902023 mod390) {
		ctx.checkWrite();
		ctx.getDslContext().delete(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(mod390.getId())).execute();
	}
	
	private static Mod3902023 fillGeneralRegimeData(AONContext ctx, Mod3902023 mod390) {
		try {
			Mod390Detail det = null;
			EnumMap<Mod3902023DetailKey, Mod390Detail> map = getDetails(ctx, mod390);
			mod390.setBox99 (map.get(Mod3902023DetailKey.C0099).getTaxableBase());
			mod390.setBox100(map.get(Mod3902023DetailKey.C0100).getTaxableBase());
			mod390.setBox101(map.get(Mod3902023DetailKey.C0101).getTaxableBase());
			mod390.setBox102(map.get(Mod3902023DetailKey.C0102).getTaxableBase());
			mod390.setBox103(map.get(Mod3902023DetailKey.C0103).getTaxableBase());
			mod390.setBox104(map.get(Mod3902023DetailKey.C0104).getTaxableBase());
			mod390.setBox105(map.get(Mod3902023DetailKey.C0105).getTaxableBase());
			mod390.setBox106(map.get(Mod3902023DetailKey.C0106).getTaxableBase());
			mod390.setBox107(map.get(Mod3902023DetailKey.C0107).getTaxableBase());
			mod390.setBox108(map.get(Mod3902023DetailKey.C0108).getTaxableBase());
			mod390.setBox110(map.get(Mod3902023DetailKey.C0110).getTaxableBase());
			mod390.setBox125(map.get(Mod3902023DetailKey.C0125).getTaxableBase());
			mod390.setBox126(map.get(Mod3902023DetailKey.C0126).getTaxableBase());
			mod390.setBox127(map.get(Mod3902023DetailKey.C0127).getTaxableBase());
			mod390.setBox128(map.get(Mod3902023DetailKey.C0128).getTaxableBase());
			
			mod390.setBox227(map.get(Mod3902023DetailKey.C0227).getTaxableBase());
			mod390.setBox228(map.get(Mod3902023DetailKey.C0228).getTaxableBase());
			mod390.setBox653(map.get(Mod3902023DetailKey.C0653).getTaxableBase());
			mod390.setBox654(map.get(Mod3902023DetailKey.C0654).getTaxableBase());
			mod390.setBox655(map.get(Mod3902023DetailKey.C0654).getQuota());
			mod390.setAccrualRegime( ( map.get(Mod3902023DetailKey.C0654).getTaxableBase()  != 0 || map.get(Mod3902023DetailKey.C0654).getQuota() != 0 ) );
			mod390.setBox656(map.get(Mod3902023DetailKey.C0656).getTaxableBase());
			mod390.setBox657(map.get(Mod3902023DetailKey.C0656).getQuota());
			mod390.setAccrualRegimeTarget((map.get(Mod3902023DetailKey.C0656).getTaxableBase()  != 0 || map.get(Mod3902023DetailKey.C0656).getQuota() != 0 ));

			// ----------------------------------------------------------------------------
			// En el caso de que el declarante este acogido al regimen simplificado
			// Se utiliza toda la funcionalidad del regimen general (lectura de facturas)
			// para rellenar los campos anteriores , del 99 al 657. Sin embargo la 
			// página 5 del modelo, o sea la del regimen general debe ir vacia, por lo 
			// que se incializa el mapa.
			if (mod390.isSimplifiedRegime()) {
				map = new EnumMap<>(Mod3902023DetailKey.class);
				for (Mod3902023DetailKey key : Mod3902023DetailKey.values() ) {
					det = new Mod390Detail();
					det.setKey(key);
					det.setPercent(key.getPercent());
					map.put(key, det);
				}
			}
			// ----------------------------------------------------------------------------
			
			mod390.setGeneralRegime(map);
			return mod390;
		} catch (Exception t) {
			t.printStackTrace();
			throw t; 
		}
	}

	static class SimplifedRegimeContext {
		private Mod303Key key;
		private String description;
		private double amount;

		public SimplifedRegimeContext(Mod303Key key,String description,double amount) {
			this.key = key;
			this.description = description;
			this.amount = amount;
		}
		public Mod303Key getKey() {
			return key;
		}
		public void setKey(Mod303Key key) {
			this.key = key;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
	}
	
	@FunctionalInterface
	static interface ISimplifiedRegimeFiller {
		boolean fill(SimplifedRegimeContext src,Mod3902023 mod390);
	}

	enum SimplifiedRegimeFiller {
		CAG1     (Mod303Key.CT_SA11,(src,mod390) -> {mod390.getFarmerRegime1().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(),"-")));return true;}), 
		CAG1_V1  (Mod303Key.CT_SA12,(src,mod390) -> {mod390.getFarmerRegime1().setIncomes(src.getAmount());return true;}),
		CAG1_V2  (Mod303Key.CT_SA13,(src,mod390) -> {mod390.getFarmerRegime1().setQuotaIndex( AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
		CAG1_V3  (Mod303Key.CT_SA14,(src,mod390) -> {mod390.getFarmerRegime1().setAccrualQuota(src.getAmount());return true;}),
		CAG1_V6  (Mod303Key.CT_SA17,(src,mod390) -> {mod390.getFarmerRegime1().setInputQuotas(src.getAmount());return true;}),
		CAG1_V7  (Mod303Key.CT_SA18,(src,mod390) -> {mod390.getFarmerRegime1().setQuota(src.getAmount());return true;}),
		
		CAG2     (Mod303Key.CT_SA21,(src,mod390) -> {mod390.getFarmerRegime2().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
		CAG2_V1  (Mod303Key.CT_SA22,(src,mod390) -> {mod390.getFarmerRegime2().setIncomes(src.getAmount());return true;}),
		CAG2_V2  (Mod303Key.CT_SA23,(src,mod390) -> {mod390.getFarmerRegime2().setQuotaIndex(AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
		CAG2_V3  (Mod303Key.CT_SA24,(src,mod390) -> {mod390.getFarmerRegime2().setAccrualQuota(src.getAmount());return true;}),
		CAG2_V6  (Mod303Key.CT_SA27,(src,mod390) -> {mod390.getFarmerRegime2().setInputQuotas(src.getAmount());return true;}),
		CAG2_V7  (Mod303Key.CT_SA28,(src,mod390) -> {mod390.getFarmerRegime2().setQuota(src.getAmount());return true;}),
		
		CAC1     (Mod303Key.CT_S101,(src,mod390) -> {mod390.getSimpRegime1().setEpigrafe(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
		CAC1_M1U (Mod303Key.CT_S11I, (src,mod390) -> {mod390.getSimpRegime1().setUnit1(src.getAmount());return true;}),
		CAC1_M1I (Mod303Key.CT_S11R, (src,mod390) -> {mod390.getSimpRegime1().setAmount1(src.getAmount());return true;}),
		CAC1_M2U (Mod303Key.CT_S12I, (src,mod390) -> {mod390.getSimpRegime1().setUnit2(src.getAmount());return true;}),
		CAC1_M2I (Mod303Key.CT_S12R, (src,mod390) -> {mod390.getSimpRegime1().setAmount2(src.getAmount());return true;}),
		CAC1_M3U (Mod303Key.CT_S13I, (src,mod390) -> {mod390.getSimpRegime1().setUnit3(src.getAmount());return true;}),
		CAC1_M3I (Mod303Key.CT_S13R, (src,mod390) -> {mod390.getSimpRegime1().setAmount3(src.getAmount());return true;}),
		CAC1_M4U (Mod303Key.CT_S14I, (src,mod390) -> {mod390.getSimpRegime1().setUnit4(src.getAmount());return true;}),
		CAC1_M4I (Mod303Key.CT_S14R, (src,mod390) -> {mod390.getSimpRegime1().setAmount4(src.getAmount());return true;}),
		CAC1_M5U (Mod303Key.CT_S15I, (src,mod390) -> {mod390.getSimpRegime1().setUnit5(src.getAmount());return true;}),
		CAC1_M5I (Mod303Key.CT_S15R, (src,mod390) -> {mod390.getSimpRegime1().setAmount5(src.getAmount());return true;}),
		CAC1_M6U (Mod303Key.CT_S16I, (src,mod390) -> {mod390.getSimpRegime1().setUnit6(src.getAmount());return true;}),
		CAC1_M6I (Mod303Key.CT_S16R, (src,mod390) -> {mod390.getSimpRegime1().setAmount6(src.getAmount());return true;}),
		CAC1_M7U (Mod303Key.CT_S17I, (src,mod390) -> {mod390.getSimpRegime1().setUnit7(src.getAmount());return true;}),
		CAC1_M7I (Mod303Key.CT_S17R, (src,mod390) -> {mod390.getSimpRegime1().setAmount7(src.getAmount());return true;}),
		CAC1_C   (Mod303Key.CT_S117, (src,mod390) -> {
			mod390.getSimpRegime1().setBoxC(src.getAmount());
			mod390.setBox74( AonMathUtils.round(mod390.getBox74() + src.getAmount()));
			return true;}),
//		CAC1_C1  (Mod303Key.CT_S117, (src,mod390) -> {mod390.getSimpRegime1().setBoxC1(src.getAmount());return true;}),
		CAC1_D   (Mod303Key.CT_S118, null),
		CAC1_Z   (Mod303Key.CT_S119, null),
//		CAC1_ZA  (Mod303Key.CAC1_ZA , null),
//		CAC1_ZD  (Mod303Key.CAC1_ZD , null),
		CAC1_E   (Mod303Key.CT_S120 , null),
		CAC1_F   (Mod303Key.CT_S121 , null),
//		CAC1_G0  (Mod303Key.CAC1_G0 , (src,mod390) -> {mod390.getSimpRegime1().setBoxD(
//				AonMathUtils.round(mod390.getSimpRegime1().getBoxD() + src.getAmount()));return true;}),
		CAC1_G   (Mod303Key.CT_S122 , (src,mod390) -> {mod390.getSimpRegime1().setBoxD(
				AonMathUtils.round(mod390.getSimpRegime1().getBoxD() + src.getAmount()));return true;}),
		CAC1_H   (Mod303Key.CT_S123 , (src,mod390) -> {mod390.getSimpRegime1().setBoxE(src.getAmount());return true;}),
//		CAC1_HA  (Mod303Key.CAC1_HA , null),
//		CAC1_HD  (Mod303Key.CAC1_HD , null),
//		CAC1_HT  (Mod303Key.CAC1_HT , null),
		CAC1_I   (Mod303Key.CT_S124 , (src,mod390) -> {mod390.getSimpRegime1().setBoxF(src.getAmount());return true;}),
		CAC1_J   (Mod303Key.CT_S125 , (src,mod390) -> {mod390.getSimpRegime1().setBoxG(src.getAmount());return true;}),
		CAC1_K   (Mod303Key.CT_S126 , null),
		CAC1_L   (Mod303Key.CT_S127 , (src,mod390) -> {mod390.getSimpRegime1().setBoxI(src.getAmount());return true;}),
		CAC1_M   (Mod303Key.CT_S128 , (src,mod390) -> {mod390.getSimpRegime1().setBoxJ(src.getAmount());return true;}),
		
		
		CAC2     (Mod303Key.CT_S201    , (src,mod390) -> {
			String code = AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-"));					
			mod390.getSimpRegime2().setEpigrafe(code);
			return true;}),
		CAC2_M1U (Mod303Key.CT_S21I, (src,mod390) -> {mod390.getSimpRegime2().setUnit1(src.getAmount());return true;}),
		CAC2_M1I (Mod303Key.CT_S21R, (src,mod390) -> {mod390.getSimpRegime2().setAmount1(src.getAmount());return true;}),
		CAC2_M2U (Mod303Key.CT_S22I, (src,mod390) -> {mod390.getSimpRegime2().setUnit2(src.getAmount());return true;}),
		CAC2_M2I (Mod303Key.CT_S22R, (src,mod390) -> {mod390.getSimpRegime2().setAmount2(src.getAmount());return true;}),
		CAC2_M3U (Mod303Key.CT_S23I, (src,mod390) -> {mod390.getSimpRegime2().setUnit3(src.getAmount());return true;}),
		CAC2_M3I (Mod303Key.CT_S23R, (src,mod390) -> {mod390.getSimpRegime2().setAmount3(src.getAmount());return true;}),
		CAC2_M4U (Mod303Key.CT_S24I, (src,mod390) -> {mod390.getSimpRegime2().setUnit4(src.getAmount());return true;}),
		CAC2_M4I (Mod303Key.CT_S24R, (src,mod390) -> {mod390.getSimpRegime2().setAmount4(src.getAmount());return true;}),
		CAC2_M5U (Mod303Key.CT_S25I, (src,mod390) -> {mod390.getSimpRegime2().setUnit5(src.getAmount());return true;}),
		CAC2_M5I (Mod303Key.CT_S25R, (src,mod390) -> {mod390.getSimpRegime2().setAmount5(src.getAmount());return true;}),
		CAC2_M6U (Mod303Key.CT_S26I, (src,mod390) -> {mod390.getSimpRegime2().setUnit6(src.getAmount());return true;}),
		CAC2_M6I (Mod303Key.CT_S26R, (src,mod390) -> {mod390.getSimpRegime2().setAmount6(src.getAmount());return true;}),
		CAC2_M7U (Mod303Key.CT_S27I, (src,mod390) -> {mod390.getSimpRegime2().setUnit7(src.getAmount());return true;}),
		CAC2_M7I (Mod303Key.CT_S27R, (src,mod390) -> {mod390.getSimpRegime2().setAmount7(src.getAmount());return true;}),
		CAC2_C   (Mod303Key.CT_S217 , (src,mod390) ->	{
				mod390.getSimpRegime2().setBoxC(src.getAmount());
				mod390.setBox74( AonMathUtils.round(mod390.getBox74() + src.getAmount()));
				return true;
														}),
//		CAC2_C1  (Mod303Key.CAC2_C1 , (src,mod390) -> {mod390.getSimpRegime2().setBoxC1(src.getAmount());return true;}),
		CAC2_D   (Mod303Key.CT_S218, null),
		CAC2_Z   (Mod303Key.CT_S219 , null),
//		CAC2_ZA  (Mod303Key.CAC2_ZA , null),
//		CAC2_ZD  (Mod303Key.CAC2_ZD , null),
		CAC2_E   (Mod303Key.CT_S220 , null),
		CAC2_F   (Mod303Key.CT_S221 , null),
		
//		CAC2_G0  (Mod303Key.CAC2_G0 , (src,mod390) -> {mod390.getSimpRegime2().setBoxD(
//				AonMathUtils.round(mod390.getSimpRegime2().getBoxD() + src.getAmount()));return true;}),
		CAC2_G   (Mod303Key.CT_S222 , (src,mod390) -> {mod390.getSimpRegime2().setBoxD(
				AonMathUtils.round(mod390.getSimpRegime2().getBoxD() + src.getAmount()));return true;}),
		CAC2_H   (Mod303Key.CT_S223 , (src,mod390) -> {mod390.getSimpRegime2().setBoxE(src.getAmount());return true;}),
//		CAC2_HA  (Mod303Key.CAC2_HA , null),
//		CAC2_HD  (Mod303Key.CAC2_HD , null),
//		CAC2_HT  (Mod303Key.CAC2_HT , null),
		CAC2_I   (Mod303Key.CT_S224 , (src,mod390) -> {mod390.getSimpRegime2().setBoxF(src.getAmount());return true;}),
		CAC2_J	 (Mod303Key.CT_S225 , (src,mod390) -> {mod390.getSimpRegime2().setBoxG(src.getAmount());return true;}),
		CAC2_K   (Mod303Key.CT_S226 , null),
		CAC2_L   (Mod303Key.CT_S227 , (src,mod390) -> {mod390.getSimpRegime2().setBoxI(src.getAmount());return true;}),
		CAC2_M   (Mod303Key.CT_S228 , (src,mod390) -> {mod390.getSimpRegime2().setBoxJ(src.getAmount());return true;}),
		
//		C51      (Mod303Key.C51     , null),
//		C52      (Mod303Key.C52     , null),
//		C53      (Mod303Key.CT_S51  , null),
//		C54      (Mod303Key.C54     , null),
//		C55      (Mod303Key.C55     , null),
//		C56      (Mod303Key.C56     , null),
//		C57      (Mod303Key.C57     , null),
//		C58      (Mod303Key.C58     , null),
//		C71      (Mod303Key.C71     , null),
//		PBK      (Mod303Key.PBK     , null),
		
		;
		
		private Mod303Key key;
		private ISimplifiedRegimeFiller filler;
			
		private SimplifiedRegimeFiller(Mod303Key key,ISimplifiedRegimeFiller filler) {
			this.key = key;
			this.filler = filler;
		}
		public Mod303Key getKey() {
			return key;
		}
		public ISimplifiedRegimeFiller getFiller() {
			return filler;
		}
		public static void fill(SimplifedRegimeContext src,Mod3902023 mod390) {
			for (SimplifiedRegimeFiller filler : SimplifiedRegimeFiller.values()) {
				if (filler.getKey() ==  src.getKey() && filler.getFiller() != null) {
					filler.getFiller().fill(src, mod390);
				}
			}
		}
		
	}	
	
	private static Mod3902023 fillSimplifedRegimeData(AONContext ctx, Mod3902023 mod390) {
		mod390.setSimpRegime1(new SimpliedRegimeActivity());
		mod390.setSimpRegime2(new SimpliedRegimeActivity());
		mod390.setFarmerRegime1(new FarmerRegimeActivity());
		mod390.setFarmerRegime2(new FarmerRegimeActivity());
		mod390.setFarmerRegime3(new FarmerRegimeActivity());
		mod390.setFarmerRegime4(new FarmerRegimeActivity());
		mod390.setFarmerRegime5(new FarmerRegimeActivity());
		ctx.getDslContext().select(FS_MODEL_DETAIL.TYPE
				, FS_MODEL_DETAIL.DESCRIPTION
				, FS_MODEL_DETAIL.AMOUNT)
			.from(FS_MODEL)
			.join(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
			.where(FS_MODEL.DOMAIN.equal(mod390.getDomain()))
			.and(FS_MODEL.YEAR.equal(mod390.getYear()))
			.and(FS_MODEL.PERIOD.equal( (byte) Period.T4.ordinal()))
			.and(FS_MODEL.MODEL.equal(FiscalModelType.M303.getValue()))
		.fetch()
		.stream()
		.forEach( rec -> {
			String description = rec.getValue( FS_MODEL_DETAIL.DESCRIPTION );
			String type = rec.getValue( FS_MODEL_DETAIL.TYPE );
			double amount = rec.getValue( FS_MODEL_DETAIL.AMOUNT );
			Mod303Key key = Mod303Key.getKey(type);
			SimplifedRegimeContext src = new SimplifedRegimeContext(key, description, amount);
			SimplifiedRegimeFiller.fill(src,mod390);
			}
		);
		return mod390;
	}
	private static void fillSimplifiedDeclarationResults(AONContext ctx, Mod3902023 mod390) {
		mod390.setBox97( AonMathUtils.round(mod390.getBox97() * -1));
		mod390.setBox98( AonMathUtils.round(mod390.getBox98() * -1));
		if (mod390.getBox98() > 0) {
			mod390.setBox97( 0 ); 	
		}
		Record1<BigDecimal> rec = ctx.getDslContext()
			.select(DSL.sum(FS_MODEL_DETAIL.AMOUNT))
			.from(FS_MODEL)
			.join(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
			.where(FS_MODEL.DOMAIN.equal(mod390.getDomain()))
			.and(FS_MODEL.YEAR.equal(mod390.getYear()))
			.and(FS_MODEL_DETAIL.AMOUNT.greaterThan(0.0))
			.and(FS_MODEL.MODEL.equal("303"))
			.and(FS_MODEL_DETAIL.TYPE.equal("303-71"))
			.fetchOne();
		if (rec != null) {
			BigDecimal quota = rec.getValue(DSL.sum(FS_MODEL_DETAIL.AMOUNT)); 
			if (quota != null) {
				mod390.setBox95( quota.doubleValue());
			}
		}
	}

	public static Mod3902023 changeStatus(AONContext ctx, Mod3902023 mod390, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod390.getId() != null) {
				mod390.setStatus(newStatus);
				ctx.getDslContext().update(FS_MODEL390)
					.set(FS_MODEL390.STATUS,AonEnumUtils.getByte( mod390.getStatus()))
					.where(FS_MODEL390.ID.equal(mod390.getId()))
					.execute();
			}
			return mod390;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static double getVatAccrualPaymentOutputBase(AONContext ctx, Mod390 mod390) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod390);
		return Mod390DAO.getVatAccrualPaymentOutputBase(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentOutputQuota(AONContext ctx, Mod390 mod390) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod390);
		return Mod390DAO.getVatAccrualPaymentOutputQuota(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentInputBase(AONContext ctx, Mod390 mod390) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod390);
		return Mod390DAO.getVatAccrualPaymentInputBase(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentInputQuota(AONContext ctx, Mod390 mod390) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod390.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod390);
		return Mod390DAO.getVatAccrualPaymentInputQuota(ctx,fromDate,toDate);
	}

	// *******************
	public static Mod3902023 aeatPresentation(AONContext ctx, Mod3902023 mod, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {
			DataResponseDAO.insertAEATResponse(ctx, mod, aeatResponse);
			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
			if (mod != null && mod.getId() != null) {
				ctx.getDslContext().update(FS_MODEL390)
					.set(FS_MODEL390.RECEIPT,response.getJustificante())
					.set(FS_MODEL390.STATUS, FiscalStatus.SENT.value())
					.where(FS_MODEL390.ID.equal(mod.getId()))
					.execute();
				return getById(ctx, mod.getId());
			}
		}
		return mod;
	}
	
	// *******************


	// -----------------------------------------------------------------------
	// --------------------------------------------------------------- FILTROS
	// -----------------------------------------------------------------------

	private static boolean hasPercent0(VatContext vat) {
		return vat.getPercentage() == PERCENT0;
	}
	private static boolean hasPercent4(VatContext vat) {
		return vat.getPercentage() == PERCENT4;
	}
	private static boolean hasPercent5(VatContext vat) {
		return vat.getPercentage() == PERCENT5;
	}
	private static boolean hasPercent10(VatContext vat) {
		return vat.getPercentage() == PERCENT10;
	}
	private static boolean hasPercent21(VatContext vat) {
		return vat.getPercentage() == PERCENT21;
	}

	private static boolean hasSurchargePercent00(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT00;
	}
	private static boolean hasSurchargePercent05(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT05;
	}
	private static boolean hasSurchargePercent062(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT062;
	}
	private static boolean hasSurchargePercent14(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT14;
	}

	private static boolean hasSurchargePercent52(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT52;
	}
	private static boolean hasSurchargePercent175(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT175;
	}
	
	private static boolean isCommonNationalSales(VatContext vat, Mod3902023 mod) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isNational()
			&& vat.isSales() 
			&& !vat.isRectification();
	}
	private static boolean isCommonNationalSalesRECT(VatContext vat, Mod3902023 mod) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isNational()
			&& vat.isSales() 
			&& vat.isRectification();
	}
	private static boolean isIntracommunityPurchase(VatContext vat, Mod3902023 mod) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isRectification() 
			&& vat.isIntracommunityPurchase();
	}
	private static boolean isIntracommunityExpenses(VatContext vat, Mod3902023 mod) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isRectification() 
			&& vat.isIntracommunityExpenses();
	}
	
	private static boolean isOperacionesISPFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
			&& (vat.isOtherISPPurchase() 
			 || vat.isOtherISPExpenses() 
			 || vat.isExtracommunityExpenses()
			 || vat.isCanCeuMelExpenses() 
			 || (vat.isExtracommunityPurchase() && vat.isService())
			 || (vat.isCanCeuMelPurchase() && vat.isService()));
	}
	
	private static boolean operacionesInterioresCorrientesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isInvestment()
			&& !vat.isRectification() 
			&& !vat.isFarmerRegime() 
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || isOperacionesISPFilter(vat));
	}
	
	private static boolean operacionesInterioresInversionFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isInvestment()
			&& !vat.isRectification() 
			&& !vat.isFarmerRegime() 
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || isOperacionesISPFilter(vat));
	}
	
	private static boolean importacionesCorrientesFilter(VatContext vat) {
		return commonImportacionesFilter(vat) && !vat.isInvestment();
	}
	private static boolean importacionesInversionFilter(VatContext vat) {
		return commonImportacionesFilter(vat) && vat.isInvestment();
	}
	
	private static boolean commonImportacionesFilter(VatContext vat) {
		boolean basicFilter =  vat.isVatGeneralRegime(VATRegime.GENERAL) 
				&& !vat.isVatSurchargeRegime() 
				&& !vat.isRectification() 
				&& !vat.isService();
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
		return !vat.isInvestment() && adqIntracomunitariasFilter(vat);
	}
	private static boolean adqIntracomunitariasInversionFilter(VatContext vat) {
		return vat.isInvestment() && adqIntracomunitariasFilter(vat);
	}

	private static boolean adqIntracomunitariasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isService()
			&& !vat.isRectification()
			&& vat.isIntracommunityPurchase();
	}
	private static boolean adqIntracomunitariasServicios(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isRectification()
			&& (vat.isIntracommunityExpenses() || (vat.isIntracommunityPurchase() && vat.isService()));
	}

	private static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isFarmerRegime()
			&& !vat.isRectification() 
			&& (vat.isNationalPurchase() || vat.isNationalExpenses());
	}
	
}





