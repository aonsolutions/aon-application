package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod425;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
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
import com.esferalia.aon.occam.api.model.fiscal.mod390.SimpliedRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025.Mod425Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025DetailKey;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303Declaration;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod390DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024;
import com.esferalia.aon.occam.impl.jooq.dao.mod425_2025.ATC4252025toMod425;
import com.esferalia.aon.occam.impl.jooq.dao.mod425_2025.Mod425toATC4252025;
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

public class Mod4252025DAO {

	private static final byte ZERO_BYTE = 0;
	private static final byte ONE_BYTE = 1;
	
	public static final double PERCENT0 = 0.0;
	public static final double PERCENT2 = 2.0;
	public static final double PERCENT4 = 4.0;
	public static final double PERCENT5 = 5.0;
	public static final double PERCENT75 = 7.5;
	public static final double PERCENT10 = 10.0;
	public static final double PERCENT21 = 21.0;
	public static final double SURCHARGE_PERCENT00 = 0.0;
	public static final double SURCHARGE_PERCENT026 = 0.26;
	public static final double SURCHARGE_PERCENT05 = 0.5;
	public static final double SURCHARGE_PERCENT062 = 0.62;
	public static final double SURCHARGE_PERCENT10 = 1.0;
	public static final double SURCHARGE_PERCENT14 = 1.4;
	public static final double SURCHARGE_PERCENT52 = 5.2;
	public static final double SURCHARGE_PERCENT175 = 1.75;
	
	@FunctionalInterface
	public static interface IMod425DetailKey {
		boolean accept(Mod4252025 mod,VatContext vc);
	}

	private static class KeyedVatContext  {
		private Mod4252025DetailKeyDAO key;
		private VatContext vc;
		
		private KeyedVatContext(Mod4252025DetailKeyDAO key,VatContext vc) {
			this.key = key;
			this.vc = vc;
		}
		public Mod4252025DetailKeyDAO getKey() {
			return key;
		}
		public VatContext getVatContext() {
			return vc;
		}
	}
	
	public enum Mod4252025DetailKeyDAO implements Serializable {
		// IVA devengado
	 	// Régimen ordinario
		  C0701	(Mod4252025DetailKey.C0701, (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent0(vc))
		 ,C0668 (Mod4252025DetailKey.C0668, (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent2(vc))
		 ,C0002 (Mod4252025DetailKey.C0002, (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent4(vc))		
		 ,C0703	(Mod4252025DetailKey.C0703, (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent5(vc))
		 ,C0670 (Mod4252025DetailKey.C0670, (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent75(vc))
		 ,C0004	(Mod4252025DetailKey.C0004, (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent10(vc))
		 ,C0006	(Mod4252025DetailKey.C0006, (mod, vc) -> isCommonNationalSales(vc, mod) && !vc.isVatAccrualRegime() && hasPercent21(vc))
	 	// Operaciones intragrupo
		 ,C0705	(Mod4252025DetailKey.C0705, null)
		 ,C0672	(Mod4252025DetailKey.C0672, null)
		 ,C0501	(Mod4252025DetailKey.C0501, null)
		 ,C0707	(Mod4252025DetailKey.C0707, null)
		 ,C0674	(Mod4252025DetailKey.C0674, null)
		 ,C0503	(Mod4252025DetailKey.C0503, null)
		 ,C0505	(Mod4252025DetailKey.C0505, null)		 
		 // Régimen especial del criterio de caja
		 ,C0709	(Mod4252025DetailKey.C0709, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent0(vc))
		 ,C0676 (Mod4252025DetailKey.C0676, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent2(vc))
		 ,C0644 (Mod4252025DetailKey.C0644, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent4(vc))		
		 ,C0711	(Mod4252025DetailKey.C0711, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent5(vc))
		 ,C0678 (Mod4252025DetailKey.C0678, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent75(vc))
		 ,C0646	(Mod4252025DetailKey.C0646, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent10(vc))
		 ,C0648	(Mod4252025DetailKey.C0648, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isVatAccrualRegime() && hasPercent21(vc))
	 	// Régimen especial de bienes usados, objetos de arte, antigüedades y objetos de colección
		 ,C0713	(Mod4252025DetailKey.C0713, null)
		 ,C0680	(Mod4252025DetailKey.C0680, null)
		 ,C0008	(Mod4252025DetailKey.C0008, null)
		 ,C0715	(Mod4252025DetailKey.C0715, null)
		 ,C0682	(Mod4252025DetailKey.C0682, null)
		 ,C0010	(Mod4252025DetailKey.C0010, null)
		 ,C0012	(Mod4252025DetailKey.C0012, null)
	 	// Régimen especial de agencias de viaje
		 ,C0014	(Mod4252025DetailKey.C0014, null)
	 	// Adquisiciones intracomunitarias de bienes
		 ,C0717	(Mod4252025DetailKey.C0717, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent0(vc))
		 ,C0684	(Mod4252025DetailKey.C0684, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent2(vc))
		 ,C0022	(Mod4252025DetailKey.C0022, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent4(vc))
		 ,C0719	(Mod4252025DetailKey.C0719, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent5(vc))
		 ,C0686	(Mod4252025DetailKey.C0686, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent75(vc))
		 ,C0024	(Mod4252025DetailKey.C0024, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent10(vc))
		 ,C0026	(Mod4252025DetailKey.C0026, (mod, vc) -> isIntracommunityPurchase(vc, mod) && hasPercent21(vc))
	 	// Adquisiciones intracomunitarias de servicios
		 ,C0721	(Mod4252025DetailKey.C0721, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent0(vc))
		 ,C0688	(Mod4252025DetailKey.C0688, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent2(vc))
		 ,C0546	(Mod4252025DetailKey.C0546, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent4(vc))
		 ,C0723	(Mod4252025DetailKey.C0723, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent5(vc))
		 ,C0690	(Mod4252025DetailKey.C0690, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent75(vc))
		 ,C0548	(Mod4252025DetailKey.C0548, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent10(vc))
		 ,C0552	(Mod4252025DetailKey.C0552, (mod, vc) -> isIntracommunityExpenses(vc, mod) && hasPercent21(vc))
	 	// IVA devengado en otros supuestos de inversión del sujeto pasivo
		 ,C0028	(Mod4252025DetailKey.C0028, (mod, vc) -> isOperacionesISPFilter(vc))
	 	// Modificación de bases y cuotas
		 ,C0030	(Mod4252025DetailKey.C0030, (mod, vc) -> modificacionBasesYCuotasFilter(vc))
	 	// Modificación de bases y cuotas de operaciones intragrupo
		 ,C0650 (Mod4252025DetailKey.C0650, null)	
	 	// Modificación de bases y cuotas por auto de declaración de concurso de acreedores
		 ,C0032	(Mod4252025DetailKey.C0032, null)
	 	// Total bases y cuotas IVA
		 ,C0034	(Mod4252025DetailKey.C0034, null)
	 	// Recargo de equivalencia
		 ,C0664 (Mod4252025DetailKey.C0664, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent00(vc))
		 ,C0692 (Mod4252025DetailKey.C0692, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent026(vc))
		 ,C0036 (Mod4252025DetailKey.C0036, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent05(vc))
		 ,C0666 (Mod4252025DetailKey.C0666, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent062(vc))
		 ,C0694 (Mod4252025DetailKey.C0694, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent10(vc))
		 ,C0600 (Mod4252025DetailKey.C0600, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent14(vc))
		 ,C0602 (Mod4252025DetailKey.C0602, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent52(vc))
		 ,C0042	(Mod4252025DetailKey.C0042, (mod, vc) -> isCommonNationalSales(vc, mod) && vc.isSurcharge() && hasSurchargePercent175(vc))
		 ,C0044	(Mod4252025DetailKey.C0044, (mod, vc) -> isCommonNationalSalesRECT(vc, mod) && vc.isSurcharge())
	 	// Modificación recargo equivalencia
		 ,C0046	(Mod4252025DetailKey.C0046, null)
	 	//  Total cuotas IVA y recargo de equivalencia
		 ,C0047	(Mod4252025DetailKey.C0047, null)

		 // IVA deducible
		 // Operaciones interiores corrientes
		 // IVA deducible en operaciones interiores de bienes y servicios corrientes
		 ,C0696	(Mod4252025DetailKey.C0696, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && hasPercent2(vc)))
		 ,C0191	(Mod4252025DetailKey.C0191, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && hasPercent4(vc)))
		 ,C0725	(Mod4252025DetailKey.C0725, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && hasPercent5(vc)))
		 ,C0698	(Mod4252025DetailKey.C0698, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && hasPercent75(vc)))
		 ,C0604	(Mod4252025DetailKey.C0604, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && hasPercent10(vc)))
		 ,C0606	(Mod4252025DetailKey.C0606, ((mod, vc) -> operacionesInterioresCorrientesFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en operaciones interiores de bienes y servicios corrientes
		 ,C0049	(Mod4252025DetailKey.C0049, null)
		 // IVA deducible en operaciones intragrupo de bienes y servicios corrientes
		 ,C0746	(Mod4252025DetailKey.C0746, null)
		 ,C0507	(Mod4252025DetailKey.C0507, null)
		 ,C0727	(Mod4252025DetailKey.C0727, null)
		 ,C0748	(Mod4252025DetailKey.C0748, null)
		 ,C0608	(Mod4252025DetailKey.C0608, null)
		 ,C0610	(Mod4252025DetailKey.C0610, null)
		 // Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes y servicios corrientes
		 ,C0513	(Mod4252025DetailKey.C0513, null)
		 
		 // Operaciones interiores de bienes de inversión
		 // IVA deducible en operaciones interiores de bienes de inversión	 
		 ,C0750	(Mod4252025DetailKey.C0750, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && hasPercent2(vc)))
		 ,C0197	(Mod4252025DetailKey.C0197, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && hasPercent4(vc)))
		 ,C0729	(Mod4252025DetailKey.C0729, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && hasPercent5(vc)))
		 ,C0752	(Mod4252025DetailKey.C0752, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && hasPercent75(vc)))
		 ,C0612	(Mod4252025DetailKey.C0612, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && hasPercent10(vc)))
		 ,C0614	(Mod4252025DetailKey.C0614, ((mod, vc) -> operacionesInterioresInversionFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en operaciones interiores de bienes de inversión
		 ,C0051	(Mod4252025DetailKey.C0051, null)

		 // IVA deducible en operaciones intragrupo de bienes de inversión
		 ,C0754	(Mod4252025DetailKey.C0754, null)
		 ,C0515	(Mod4252025DetailKey.C0515, null)
		 ,C0731	(Mod4252025DetailKey.C0731, null)
		 ,C0756	(Mod4252025DetailKey.C0756, null)
		 ,C0616	(Mod4252025DetailKey.C0616, null)
		 ,C0618	(Mod4252025DetailKey.C0618, null)
		 // Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes de inversión
		 ,C0521	(Mod4252025DetailKey.C0521, null)
		 
		 // Importaciones y adquisiciones intracomunitarias de bienes y servicio
		 // IVA deducible en importaciones de bienes corrientes
		 ,C0758	(Mod4252025DetailKey.C0758, ((mod, vc) -> importacionesCorrientesFilter(vc) && hasPercent2(vc)))
		 ,C0203	(Mod4252025DetailKey.C0203, ((mod, vc) -> importacionesCorrientesFilter(vc) && hasPercent4(vc)))
		 ,C0733	(Mod4252025DetailKey.C0733, ((mod, vc) -> importacionesCorrientesFilter(vc) && hasPercent5(vc)))
		 ,C0760	(Mod4252025DetailKey.C0760, ((mod, vc) -> importacionesCorrientesFilter(vc) && hasPercent75(vc)))
		 ,C0620	(Mod4252025DetailKey.C0620, ((mod, vc) -> importacionesCorrientesFilter(vc) && hasPercent10(vc)))
		 ,C0622	(Mod4252025DetailKey.C0622, ((mod, vc) -> importacionesCorrientesFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en importaciones de bienes corrientes
		 ,C0053	(Mod4252025DetailKey.C0053, null)
		 
		 // IVA deducible en importaciones de bienes de inversión
		 ,C0762	(Mod4252025DetailKey.C0762, ((mod, vc) -> importacionesInversionFilter(vc) && hasPercent2(vc)))
		 ,C0209	(Mod4252025DetailKey.C0209, ((mod, vc) -> importacionesInversionFilter(vc) && hasPercent4(vc)))
		 ,C0735	(Mod4252025DetailKey.C0735, ((mod, vc) -> importacionesInversionFilter(vc) && hasPercent5(vc)))
		 ,C0764	(Mod4252025DetailKey.C0764, ((mod, vc) -> importacionesInversionFilter(vc) && hasPercent75(vc)))
		 ,C0624	(Mod4252025DetailKey.C0624, ((mod, vc) -> importacionesInversionFilter(vc) && hasPercent10(vc)))
		 ,C0626	(Mod4252025DetailKey.C0626, ((mod, vc) -> importacionesInversionFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en importaciones de bienes de inversión
		 ,C0055	(Mod4252025DetailKey.C0055, null)
		 
		 // IVA deducible en adquisiciones intracomunitarias de bienes corrientes
		 ,C0766	(Mod4252025DetailKey.C0766, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && hasPercent2(vc)))
		 ,C0215	(Mod4252025DetailKey.C0215, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && hasPercent4(vc)))
		 ,C0737	(Mod4252025DetailKey.C0737, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && hasPercent5(vc)))
		 ,C0768	(Mod4252025DetailKey.C0768, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && hasPercent75(vc)))
		 ,C0628	(Mod4252025DetailKey.C0628, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && hasPercent10(vc)))
		 ,C0630	(Mod4252025DetailKey.C0630, ((mod, vc) -> adqIntracomunitariasCorrientesFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes corrientes
		 ,C0057	(Mod4252025DetailKey.C0057, null)

		 // IVA deducible en adquisiciones intracomunitarias de bienes de inversión
		 ,C0770	(Mod4252025DetailKey.C0770, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && hasPercent2(vc)))
		 ,C0221	(Mod4252025DetailKey.C0221, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && hasPercent4(vc)))
		 ,C0739	(Mod4252025DetailKey.C0739, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && hasPercent5(vc)))
		 ,C0772	(Mod4252025DetailKey.C0772, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && hasPercent75(vc)))
		 ,C0632	(Mod4252025DetailKey.C0632, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && hasPercent10(vc)))
		 ,C0634	(Mod4252025DetailKey.C0634, ((mod, vc) -> adqIntracomunitariasInversionFilter(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes de inversión
		 ,C0059	(Mod4252025DetailKey.C0059, null)
		 
		 // IVA deducible en adquisiciones intracomunitarias de servicios
		 ,C0774	(Mod4252025DetailKey.C0774, ((mod, vc) -> adqIntracomunitariasServicios(vc) && hasPercent2(vc)))
		 ,C0588	(Mod4252025DetailKey.C0588, ((mod, vc) -> adqIntracomunitariasServicios(vc) && hasPercent4(vc)))
		 ,C0741	(Mod4252025DetailKey.C0741, ((mod, vc) -> adqIntracomunitariasServicios(vc) && hasPercent5(vc)))
		 ,C0776	(Mod4252025DetailKey.C0776, ((mod, vc) -> adqIntracomunitariasServicios(vc) && hasPercent75(vc)))
		 ,C0636	(Mod4252025DetailKey.C0636, ((mod, vc) -> adqIntracomunitariasServicios(vc) && hasPercent10(vc)))
		 ,C0638	(Mod4252025DetailKey.C0638, ((mod, vc) -> adqIntracomunitariasServicios(vc) && hasPercent21(vc)))
		 // Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de servicios
		 ,C0598	(Mod4252025DetailKey.C0598, null)

		 // Compensación en régimen especial de la agricultura, ganaderia y pesca
		 ,C0061	(Mod4252025DetailKey.C0061, ((mod, vc) -> compensacionesRegAgrarioFilter(vc)))
		 // Cuotas deducibles en virtud de resolución administrativa o sentencia firmes con tipos no vigentes
		 ,C0661	(Mod4252025DetailKey.C0661, null)
		 // Rectificación de deducciones
		 ,C0062	(Mod4252025DetailKey.C0062, ((mod, vc) -> vc.isRectification() && (vc.isPurchase() || vc.isExpenses() )))
		 // Rectificación de deducciones por operaciones intragrupo
		 ,C0652	(Mod4252025DetailKey.C0652, null)
		 // Regularización de bienes de inversión
		 ,C0063	(Mod4252025DetailKey.C0063, null)
		 // Regularización por aplicación porcentaje definitivo de prorrata
		 ,C0522	(Mod4252025DetailKey.C0522, null)
		 // Suma de deducciones
		 ,C0064	(Mod4252025DetailKey.C0064, null)
		 
		 // Resultado régimen general
		 ,C0065	(Mod4252025DetailKey.C0065, null)
		 
		 // Operaciones en régimen general
		 ,C0099	 (Mod4252025DetailKey.C0099, ((mod, vc) -> (vc.isNationalSales() && !vc.isVatAccrualRegime() && vc.isVatGeneralRegime(mod.isSimplifiedRegime()?VATRegime.SIMPLIFIED:VATRegime.GENERAL))))
		 // Operaciones a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA
		 ,C0653	 (Mod4252025DetailKey.C0653, null) // Se obtiene en getDetails()
		 // Entregas intracomunitarias de bienes y servicios
		 ,C0103	 (Mod4252025DetailKey.C0103, ((mod, vc) -> ( vc.isIntracommunitySales() && !vc.isWithoutRightDeductionType())))
		 // Exportaciones y otras operaciones exentas con derecho a deducción
		 ,C0104	 (Mod4252025DetailKey.C0104, ((mod, vc) -> (vc.isSales() && !vc.isWithoutRightDeductionType() && vc.isExtracommunity() && !vc.isService())))
		 // Operaciones exentas sin derecho a deducción (Se añaden tambien las ventas nacionales a porcentaje 0% de actividades exentas) 
		 ,C0105	 (Mod4252025DetailKey.C0105, ((mod, vc) -> ((vc.isSales() && !vc.isNational() && vc.isWithoutRightDeductionType()) || (vc.isNationalSales() && AonMathUtils.isZero(vc.getPercentage()) && vc.getVatRegime() != null && vc.isActivityVatExempt()))))
		 // Operaciones no sujetas por reglas de localización (excepto las incluidas en la casilla 126) (Se añaden tambien las extracomunitarias de servicios, que se quitan de la 104) (Se quitan las ISP de clientes españoles, que van a la 125)
		 ,C0110	 (Mod4252025DetailKey.C0110, ((mod, vc) -> (vc.isSales() && !vc.isWithoutRightDeductionType() && ((vc.isOtherISP() && !vc.isSpainDocumentCountry()) || vc.isCanCeuMel() || (vc.isExtracommunity() && vc.isService())))))
		 // Operaciones sujetas con inversión del sujeto pasivo (Ventas ISP de clientes españoles)
		 ,C0125	 (Mod4252025DetailKey.C0125, ((mod, vc) -> vc.isOtherISPSales() && vc.isSpainDocumentCountry()))
		 // Operaciones no sujetas por reglas de localización acogidas a los regímenes especiales de ventanilla única
		 ,C0126	 (Mod4252025DetailKey.C0126, null)
		 // Operaciones sujetas y acogidas a los regímenes especiales de ventanilla única
		 ,C0127	 (Mod4252025DetailKey.C0127, null)
		 // Operaciones intragrupo valoradas conforme a lo dispuesto en los arts. 78 y 79 LIVA
		 ,C0128	 (Mod4252025DetailKey.C0128, null)
		 // Operaciones en régimen simplificado
		 ,C0100	 (Mod4252025DetailKey.C0100, ((mod, vc) -> (vc.isNationalSales() && vc.isVatSimplifiedRegime())))
		 // Operaciones en régimen especial de la agricultura, ganadería y pesca
		 ,C0101	 (Mod4252025DetailKey.C0101, null)
		 // Operaciones realizadas por sujetos pasivos acogidos al régimen especial del recargo de equivalencia
		 ,C0102	 (Mod4252025DetailKey.C0102, ((mod, vc) -> (vc.isNationalSales() && vc.isVatSurchargeRegime())))
		 // Operaciones en Régimen especial de bienes usados, objetos de arte, antigüedades y objetos de colección
		 ,C0227	 (Mod4252025DetailKey.C0227, null)
		 // Operaciones en régimen especial de Agencias de Viajes
		 ,C0228	 (Mod4252025DetailKey.C0228, null)
		 // Entregas de bienes inmuebles, operaciones financieras y relativas al oro de inversión no habituales
		 ,C0106	 (Mod4252025DetailKey.C0106, null)
		 // Entregas de bienes de inversión
		 ,C0107	 (Mod4252025DetailKey.C0107, ((mod, vc) -> (vc.isNationalSales() && vc.isInvestment())))
		 // Total volumen de operaciones 
		 ,C0108	 (Mod4252025DetailKey.C0108, null)
		 // Operaciones específicas - Adquisiciones interiores de bienes y servicios exentas 
		 ,C0230	 (Mod4252025DetailKey.C0230, ((mod, vc) -> operacionesInterioresExentasFilter(vc)))
		 ;		 
		 
		private Mod4252025DetailKey key;
		private IMod425DetailKey accept;
			
		private Mod4252025DetailKeyDAO(Mod4252025DetailKey key, IMod425DetailKey accept) {
			this.key = key;
			this.accept = accept;
		}

		public Mod4252025DetailKey getKey() {
			return key;
		}
		public boolean accept(Mod4252025 mod, VatContext vc) {
			return accept!=null && accept.accept(mod,vc);
		}
		
	}
	
	private static EnumMap<Mod4252025DetailKey, Mod425Detail> getDetails(AONContext ctx, Mod4252025 mod425 ) {
		EnumMap<Mod4252025DetailKey, Mod425Detail> map = new EnumMap<>(Mod4252025DetailKey.class);
		Arrays.stream( Mod4252025DetailKey.values() )
			.forEach(key ->  map.put(key, new Mod425Detail().setKey(key).setPercent(key.getPercent())));
		VATDAO.getVatBreakdown(ctx, mod425)
			.flatMap(vt -> Arrays.stream( Mod4252025DetailKeyDAO.values() )
				.filter(key -> key.accept(mod425, vt))
				
				.map( key -> new KeyedVatContext(key, vt)))
			
			.map( kvt -> checkProrrated(ctx, mod425, kvt))
			
			.map(kvt -> new Pair<KeyedVatContext,Mod425Detail>(kvt, map.computeIfAbsent(kvt.getKey().getKey(), k -> new Mod425Detail().setKey(k).setPercent(kvt.getVatContext().getPercentage()))))
			.forEach(Mod4252025DAO::add );
		
		// Regimen especial de criterio de caja.
		double vatAccBase = getVatAccrualPaymentOutputBase(ctx, mod425); 
		map.get(Mod4252025DetailKey.C0653).setTaxableBase( vatAccBase );
		map.get(Mod4252025DetailKey.C0654).setTaxableBase( vatAccBase );
		map.get(Mod4252025DetailKey.C0654).setQuota( getVatAccrualPaymentOutputQuota(ctx, mod425) );
		map.get(Mod4252025DetailKey.C0656).setTaxableBase( getVatAccrualPaymentInputBase(ctx, mod425) );
		map.get(Mod4252025DetailKey.C0656).setQuota( getVatAccrualPaymentInputQuota(ctx, mod425) );
		
		// Cálculo de la Regularizacion por aplicacion del porcentaje definitivo de prorrata
		
		// Si se asigna el valorhabría que sumar los declarado en las declaraciones y no sacer los datos de las facturas.
		
		
		Condition cond = FS_MODEL.YEAR.eq(mod425.getYear())
				.and(FS_MODEL.PERIOD.eq(Period.T4.value()).or(FS_MODEL.PERIOD.eq(Period.M12.value())) );
		Mod303DAO.getEffectiveModels(ctx, mod425, cond)
			.filter( m -> m.getYear( )  == mod425.getYear())
			.filter( Mod303::isAEAT)
			.filter( Mod303::isLastPeriod)
			.map(m -> m.getAmount(Mod303Key.CT_C44))
			.forEach( r -> map.computeIfAbsent(Mod4252025DetailKey.C0522, k -> new Mod425Detail().setKey(k)).setQuota(r))
		;
		return map;
	}
	
	private static void add(Pair<KeyedVatContext, Mod425Detail> pair) {
		KeyedVatContext kvt = pair.getLeft();
		Mod4252025DetailKey key = kvt.getKey().getKey();
		Mod425Detail detail = pair.getRight();
		double q = key.isSurcharge()?kvt.getVatContext().getSurchargeQuota():kvt.getVatContext().getQuota();
		if (key.isProrrataEnabled()) {
			if (kvt.getVatContext().isProrrated())
				q = kvt.getVatContext().getProrrateQuota();
			else
				q = kvt.getVatContext().getDeductibleQuota();
		}
		detail.setQuota( AonMathUtils.round(detail.getQuota()  + q));
		detail.setTaxableBase( AonMathUtils.round( detail.getTaxableBase() + kvt.getVatContext().getBase()));
	}
	
	private static void fillGeneralDeclarationResults(AONContext ctx, Mod4252025 mod425) {
		Mod303DAO.getEffectiveModels(ctx, mod425, FS_MODEL.YEAR.eq(mod425.getYear()))
			.forEach(m303 -> {
				if (m303.isEnrolledInDevolutionRegistry()) {
					mod425.setTaxRefund(true);
				}
				if (m303.isToDeposit()) {
					mod425.setBox95( AonMathUtils.round(mod425.getBox95() + m303.getDeclarationResult()));
				} else if (m303.isToPayback()) {
					if (m303.isEnrolledInDevolutionRegistry()) {
						mod425.setBox96( AonMathUtils.round(mod425.getBox96() + (m303.getDeclarationResult() * (-1))));
					}
					if (m303.isLastPeriod()) {
						mod425.setBox98( AonMathUtils.round( m303.getDeclarationResult() * (-1) ));
					} 
				} else if (m303.isToCompensate() && m303.isLastPeriod()) {
					mod425.setBox97( AonMathUtils.round( m303.getDeclarationResult() * (-1) ));
				}
				if (m303.isFirstPeriod()) {
					mod425.setBox85(  m303.getAmount( Mod303Key.CT_C110) );
				} else if (m303.isLastPeriod()) {
					mod425.setBox662(  m303.getAmount( Mod303Key.CT_C87) );
				}
			});
		
	}
	
	private static void fillFromConfiguration(AONContext ctx, Mod4252025 mod425) {
		Company company = CompanyDAO.getCompany(ctx, mod425.getDomain());
		if (company != null) {
			ctx.getDslContext().select(RDIR_STAFF.DOCUMENT,RDIR_STAFF.NAME)
				.from(RDIR_STAFF)
				.where(RDIR_STAFF.DOMAIN.eq(mod425.getDomain()))
				.and(RDIR_STAFF.REGISTRY.eq(company.getId()))
				.and(RDIR_STAFF.REPRESENTATIVE.eq( (byte) 1))
				.fetch()
				.stream()
				.forEach( rec -> {
					String document = rec.getValue(RDIR_STAFF.DOCUMENT);
					String name = rec.getValue(RDIR_STAFF.NAME);
					
					if (mod425.isLegalEntity()) {
						LegalRepresentative legalRepr = new LegalRepresentative()
							.setDocument(document)
							.setName(name);
						if (mod425.getLegalRepr1() == null) mod425.setLegalRepr1(legalRepr); 
						else if (mod425.getLegalRepr2() == null) mod425.setLegalRepr2(legalRepr);
						else if (mod425.getLegalRepr3() == null) mod425.setLegalRepr3(legalRepr);
					} else {
						if (mod425.getAddress() == null) {
							mod425.setAddress(new Address().setRdocument(document).setRname(name));
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
	public static Mod4252025 create(AONContext ctx, Mod390 model) {
		Mod4252025 mod425 = new Mod4252025();
		mod425.setId(model.getId());
		mod425.setDomain(model.getDomain());
		mod425.setDomainName(model.getDomainName());
		mod425.setEnterprise(model.getEnterprise());
		mod425.setEnterpriseName(model.getEnterpriseName());
		mod425.setYear(model.getYear());
		mod425.setAdministration(model.getAdministration());
		mod425.setStatus(model.getStatus());
		mod425.setOldStyle(false);
		mod425.setReplacement(model.isReplacement());
		mod425.setComplementary(model.isComplementary());
		mod425.setWithoutActivity(model.isWithoutActivity());
		mod425.setDocument(model.getDocument());
		mod425.setName(model.getName());
		mod425.setFirstSurname(model.getFirstSurname());
		mod425.setSecondSurname(model.getSecondSurname());
		mod425.setContactPhone(model.getContactPhone());
		mod425.setReceipt(model.getReceipt());
		mod425.setReplacedReceipt(model.getReplacedReceipt());
		mod425.setComments(model.getComments());
		mod425.setCreationUser(model.getCreationUser());
		mod425.setCreationDate(model.getCreationDate());
		mod425.setModificationUser(model.getModificationUser());
		mod425.setModificationDate(model.getModificationDate());
		
		// Buscar si existe algún ejercicio anterior (EL 2025 ES EL PRIMER EJERCICIO DEL M425)
		boolean found = false;
//		boolean found = Mod390DAO.getByDomain(ctx, ctx.getDomainId())
//			.stream()
//			.filter(m390 -> m390.getYear() == 2024)
//			.map(m390 -> Mod3902023DAO.getById(ctx, m390.getId()))
//			.map(mod3902023 -> {
//				mod425.setMainActivity(mod3902023.getMainActivity());
//				mod425.setActivity1(mod3902023.getActivity1());
//				mod425.setActivity2(mod3902023.getActivity2());
//				mod425.setActivity3(mod3902023.getActivity3());
//				mod425.setActivity4(mod3902023.getActivity4());
//				mod425.setActivity5(mod3902023.getActivity5());
//				mod425.setAddress(mod3902023.getAddress());
//				mod425.setLegalRepr1(mod3902023.getLegalRepr1());
//				mod425.setLegalRepr2(mod3902023.getLegalRepr2());
//				mod425.setLegalRepr3(mod3902023.getLegalRepr3());
//				return mod3902023;
//			})
//			.findAny()
//			.isPresent();
//		
//		if (!found)
//			found = Mod390DAO.getByDomain(ctx, ctx.getDomainId())
//				.stream()
//				.filter(m390 -> m390.getYear() == 2022)
//				.map(m390 -> Mod3902022DAO.getById(ctx, m390.getId()))
//				.map(mod3902022 -> {
//					mod425.setMainActivity(mod3902022.getMainActivity());
//					mod425.setActivity1(mod3902022.getActivity1());
//					mod425.setActivity2(mod3902022.getActivity2());
//					mod425.setActivity3(mod3902022.getActivity3());
//					mod425.setActivity4(mod3902022.getActivity4());
//					mod425.setActivity5(mod3902022.getActivity5());
//					mod425.setAddress(mod3902022.getAddress());
//					mod425.setLegalRepr1(mod3902022.getLegalRepr1());
//					mod425.setLegalRepr2(mod3902022.getLegalRepr2());
//					mod425.setLegalRepr3(mod3902022.getLegalRepr3());
//					return mod3902022;
//				})
//				.findAny()
//				.isPresent();
//		
//		if (!found)
//			found = Mod390DAO.getByDomain(ctx, ctx.getDomainId())
//				.stream()
//				.filter(m390 -> m390.getYear() == 2021)
//				.map(m390 -> Mod3902021DAO.getById(ctx, m390.getId()))
//				.map(mod3902021 -> {
//					mod425.setMainActivity(mod3902021.getMainActivity());
//					mod425.setActivity1(mod3902021.getActivity1());
//					mod425.setActivity2(mod3902021.getActivity2());
//					mod425.setActivity3(mod3902021.getActivity3());
//					mod425.setActivity4(mod3902021.getActivity4());
//					mod425.setActivity5(mod3902021.getActivity5());
//					mod425.setAddress(mod3902021.getAddress());
//					mod425.setLegalRepr1(mod3902021.getLegalRepr1());
//					mod425.setLegalRepr2(mod3902021.getLegalRepr2());
//					mod425.setLegalRepr3(mod3902021.getLegalRepr3());
//					return mod3902021;
//				})
//				.findAny()
//				.isPresent();
//		
//		if (!found)
//			found =  Mod390DAO.getByDomain(ctx, ctx.getDomainId())
//				.stream()
//				.filter(m390 -> m390.getYear() == 2018 || m390.getYear() == 2019 || m390.getYear() == 2020)
//				.map(m390 -> Mod3902018DAO.getById(ctx, m390.getId()))
//				.map(mod3902018 -> {
//					mod425.setMainActivity(mod3902018.getMainActivity());
//					mod425.setActivity1(mod3902018.getActivity1());
//					mod425.setActivity2(mod3902018.getActivity2());
//					mod425.setActivity3(mod3902018.getActivity3());
//					mod425.setActivity4(mod3902018.getActivity4());
//					mod425.setActivity5(mod3902018.getActivity5());
//					mod425.setAddress(mod3902018.getAddress());
//					mod425.setLegalRepr1(mod3902018.getLegalRepr1());
//					mod425.setLegalRepr2(mod3902018.getLegalRepr2());
//					mod425.setLegalRepr3(mod3902018.getLegalRepr3());
//					return mod3902018;
//				})
//				.findAny()
//				.isPresent();
		
		if (!found) {
			fillFromConfiguration(ctx, mod425);	
		}
		
		fillSimplifedRegimeData(ctx, mod425);
		fillGeneralRegimeData(ctx, mod425);
		if (mod425.isSimplifiedRegime()) {
			fillSimplifiedDeclarationResults(ctx, mod425);
		} else {
			fillGeneralDeclarationResults(ctx, mod425);
		}
		mod425.calculate();
		return mod425;	
	}

	public static Mod4252025 getMod4252025(AONContext ctx, Mod390 m425) {
		if (m425.getId() == null) {
			return create(ctx, m425);
		}
		return getById(ctx, m425.getId());
	}

	public static Mod4252025 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod4252025 mod425 = new Mod4252025();
		ctx.getDslContext()
				.selectFrom(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(id))
				.fetch()
				.stream()
				.forEach(rec -> populate(rec, mod425));
		if (mod425.getId() != null) {
			return mod425;
		}
		return null;
	}

	private static void populate(FsModel390Record rec, Mod4252025 mod425)  {
		mod425
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
		// FALTA - CAMBIARLO POR LA CONVERSION DESDE EL XML DEL 425
		if (AonStringUtils.contains(model, "<AEATIVA2024>"))  {
			StringReader reader = new StringReader(rec.getValue(FS_MODEL390.MODEL));
			try {
				if (mod425.getYear() >= 2025) {
					JAXBContext context = JAXBContext.newInstance(AEATIVA2024.class);
					Unmarshaller um = context.createUnmarshaller();
					AEATIVA2024 iva = (AEATIVA2024) um.unmarshal(reader);
					ATC4252025toMod425.populate(mod425, iva);
					mod425.setXmlFormat("AEAT_2024");
				} else {
					throw new IllegalArgumentException("Ejercicio incorrecto.");
				}
			} catch (ParseException | JAXBException e1) {
				e1.printStackTrace();
				throw new AonCoreException("XML PROBLEM",e1);
			}
		} else {
			mod425.setXmlFormat("INVALID");
		}
		
	}
	
	public static String getXMLContentById(AONContext ctx, Integer id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL390)
			.where(FS_MODEL390.ID.equal(id))
			.fetchOne(FS_MODEL390.MODEL);
	}


	public static Mod4252025 save(AONContext ctx, Mod4252025 mod425)  {
		if (mod425.getId() == null) {
			return insert(ctx, mod425);
		} else {
			return update(ctx, mod425);
		}
	}
	
	private static String getXMLModel( Mod4252025 mod425 ) {
		try {
			if (mod425.getYear() >= 2025) {
				AEATIVA2024 iva = Mod425toATC4252025.getAEATIVA2024(mod425);
				StringWriter writer = new StringWriter();
				JAXBContext context = JAXBContext.newInstance(AEATIVA2024.class);
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
	
	private static Mod4252025 insert(AONContext ctx, Mod4252025 mod425) {
		validate(ctx, mod425);
		FsModel390Record rec = ctx.getDslContext()
			.insertInto(FS_MODEL390)
			.set(FS_MODEL390.DOMAIN, mod425.getDomain())
			.set(FS_MODEL390.ENTERPRISE, mod425.getEnterprise())
			.set(FS_MODEL390.YEAR, mod425.getYear())
			.set(FS_MODEL390.ADMINISTRATION,mod425.getAdministration().value())
			.set(FS_MODEL390.STATUS,AonEnumUtils.getByte( mod425.getStatus()  ))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod425.isConfidential()))
			.set(FS_MODEL390.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL390.REPLACEMENT,AonEnumUtils.getByte(mod425.isReplacement()))
			.set(FS_MODEL390.DOCUMENT, mod425.getDocument())
			.set(FS_MODEL390.NAME, mod425.getName())
			.set(FS_MODEL390.COMMENTS, mod425.getComments())
			.set(FS_MODEL390.RECEIPT, mod425.getReceipt())
			.set(FS_MODEL390.REPLACED_RECEIPT, mod425.getReplacedReceipt())
			.set(FS_MODEL390.MODEL, getXMLModel(mod425))
			.returning(FS_MODEL390.ID).fetchOne();
		mod425.setId(rec.getId());
		return mod425;
	}
	
	private static Mod4252025 update(AONContext ctx, Mod4252025 mod425) {
		validate(ctx, mod425);
		mod425.calculate();
		ctx.getDslContext()
			.update(FS_MODEL390)
			.set(FS_MODEL390.YEAR, mod425.getYear())
			.set(FS_MODEL390.ADMINISTRATION,mod425.getAdministration().value())
			.set(FS_MODEL390.STATUS,AonEnumUtils.getByte( mod425.getStatus()  ))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod425.isConfidential()))
			.set(FS_MODEL390.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL390.REPLACEMENT,AonEnumUtils.getByte(mod425.isReplacement()))
			.set(FS_MODEL390.SECURITY_LEVEL,AonEnumUtils.getByte(mod425.isConfidential()))
			.set(FS_MODEL390.DOCUMENT, mod425.getDocument())
			.set(FS_MODEL390.NAME, mod425.getName())
			.set(FS_MODEL390.COMMENTS, mod425.getComments())
			.set(FS_MODEL390.RECEIPT, mod425.getReceipt())
			.set(FS_MODEL390.REPLACED_RECEIPT, mod425.getReplacedReceipt())
			.set(FS_MODEL390.MODEL, getXMLModel(mod425))
			.where(FS_MODEL390.ID.equal(mod425.getId())).execute();
		return mod425;
	}

	private static void validate(AONContext ctx, Mod4252025 mod425) {
		if (mod425.isReplacement()) {
			// Se comprueba que exista la declaración sustituida.
			if (!ctx.getDslContext().selectOne()
					.from(FS_MODEL390)
					.where(FS_MODEL390.YEAR.equal(mod425.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod425.getEnterprise()))
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
					.where(FS_MODEL390.YEAR.equal(mod425.getYear())
					.and(FS_MODEL390.ENTERPRISE.equal(mod425.getEnterprise()))
					.and(FS_MODEL390.REPLACEMENT.equal( ONE_BYTE ))					
					.and(FS_MODEL390.REPLACED_RECEIPT.equal(mod425.getReplacedReceipt())))
					.and(	(mod425.getId()!=null)
							?FS_MODEL390.ID.ne(mod425.getId())
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
				.where(FS_MODEL390.YEAR.equal(mod425.getYear())
				.and(FS_MODEL390.ENTERPRISE.equal(mod425.getEnterprise()))
				.and(FS_MODEL390.REPLACEMENT.equal(ZERO_BYTE)))
				.and(	(mod425.getId()!=null)
						?FS_MODEL390.ID.ne(mod425.getId())
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

	public static void delete(AONContext ctx, Mod4252025 mod425) {
		ctx.checkWrite();
		ctx.getDslContext().delete(FS_MODEL390)
				.where(FS_MODEL390.ID.equal(mod425.getId())).execute();
	}
	
	private static Mod4252025 fillGeneralRegimeData(AONContext ctx, Mod4252025 mod425) {
		try {
			Mod425Detail det = null;
			EnumMap<Mod4252025DetailKey, Mod425Detail> map = getDetails(ctx, mod425);
			mod425.setBox99 (map.get(Mod4252025DetailKey.C0099).getTaxableBase());
			mod425.setBox100(map.get(Mod4252025DetailKey.C0100).getTaxableBase());
			mod425.setBox101(map.get(Mod4252025DetailKey.C0101).getTaxableBase());
			mod425.setBox102(map.get(Mod4252025DetailKey.C0102).getTaxableBase());
			mod425.setBox103(map.get(Mod4252025DetailKey.C0103).getTaxableBase());
			mod425.setBox104(map.get(Mod4252025DetailKey.C0104).getTaxableBase());
			mod425.setBox105(map.get(Mod4252025DetailKey.C0105).getTaxableBase());
			mod425.setBox106(map.get(Mod4252025DetailKey.C0106).getTaxableBase());
			mod425.setBox107(map.get(Mod4252025DetailKey.C0107).getTaxableBase());
			mod425.setBox108(map.get(Mod4252025DetailKey.C0108).getTaxableBase());
			mod425.setBox110(map.get(Mod4252025DetailKey.C0110).getTaxableBase());
			mod425.setBox125(map.get(Mod4252025DetailKey.C0125).getTaxableBase());
			mod425.setBox126(map.get(Mod4252025DetailKey.C0126).getTaxableBase());
			mod425.setBox127(map.get(Mod4252025DetailKey.C0127).getTaxableBase());
			mod425.setBox128(map.get(Mod4252025DetailKey.C0128).getTaxableBase());
			   
			mod425.setBox227(map.get(Mod4252025DetailKey.C0227).getTaxableBase());
			mod425.setBox228(map.get(Mod4252025DetailKey.C0228).getTaxableBase());
			mod425.setBox653(map.get(Mod4252025DetailKey.C0653).getTaxableBase());
			mod425.setBox654(map.get(Mod4252025DetailKey.C0654).getTaxableBase());
			mod425.setBox655(map.get(Mod4252025DetailKey.C0654).getQuota());
			mod425.setAccrualRegime( ( map.get(Mod4252025DetailKey.C0654).getTaxableBase()  != 0 || map.get(Mod4252025DetailKey.C0654).getQuota() != 0 ) );
			mod425.setBox656(map.get(Mod4252025DetailKey.C0656).getTaxableBase());
			mod425.setBox657(map.get(Mod4252025DetailKey.C0656).getQuota());
			mod425.setAccrualRegimeTarget((map.get(Mod4252025DetailKey.C0656).getTaxableBase()  != 0 || map.get(Mod4252025DetailKey.C0656).getQuota() != 0 ));
			mod425.setBox230(map.get(Mod4252025DetailKey.C0230).getTaxableBase());

			// ----------------------------------------------------------------------------
			// En el caso de que el declarante este acogido al regimen simplificado
			// Se utiliza toda la funcionalidad del regimen general (lectura de facturas)
			// para rellenar los campos anteriores , del 99 al 657. Sin embargo la 
			// página 5 del modelo, o sea la del regimen general debe ir vacia, por lo 
			// que se incializa el mapa.
			if (mod425.isSimplifiedRegime()) {
				map = new EnumMap<>(Mod4252025DetailKey.class);
				for (Mod4252025DetailKey key : Mod4252025DetailKey.values() ) {
					det = new Mod425Detail();
					det.setKey(key);
					det.setPercent(key.getPercent());
					map.put(key, det);
				}
			}
			// ----------------------------------------------------------------------------
			
			mod425.setGeneralRegime(map);
			return mod425;
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
		boolean fill(SimplifedRegimeContext src, Mod4252025 mod425);
	}

	enum SimplifiedRegimeFiller {
		
		CAG1     (Mod303Key.CT_SA11,(src,mod425) -> {mod425.getFarmerRegime1().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(),"-")));return true;}), 
		CAG1_V1  (Mod303Key.CT_SA12,(src,mod425) -> {mod425.getFarmerRegime1().setIncomes(src.getAmount());return true;}),
		CAG1_V2  (Mod303Key.CT_SA13,(src,mod425) -> {mod425.getFarmerRegime1().setQuotaIndex( AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
		CAG1_V3  (Mod303Key.CT_SA14,(src,mod425) -> {mod425.getFarmerRegime1().setAccrualQuota(src.getAmount());return true;}),
		CAG1_V4  (Mod303Key.CT_SA1R,(src,mod425) -> {mod425.getFarmerRegime1().setDanaReduction(src.getAmount());return true;}),
		CAG1_V6  (Mod303Key.CT_SA1A,(src,mod425) -> {mod425.getFarmerRegime1().setInputQuotas(src.getAmount());return true;}),
		CAG1_V7  (Mod303Key.CT_SA18,(src,mod425) -> {mod425.getFarmerRegime1().setQuota(src.getAmount());return true;}),
		                                    
		CAG2     (Mod303Key.CT_SA21,(src,mod425) -> {mod425.getFarmerRegime2().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
		CAG2_V1  (Mod303Key.CT_SA22,(src,mod425) -> {mod425.getFarmerRegime2().setIncomes(src.getAmount());return true;}),
		CAG2_V2  (Mod303Key.CT_SA23,(src,mod425) -> {mod425.getFarmerRegime2().setQuotaIndex(AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
		CAG2_V3  (Mod303Key.CT_SA24,(src,mod425) -> {mod425.getFarmerRegime2().setAccrualQuota(src.getAmount());return true;}),
		CAG2_V4  (Mod303Key.CT_SA2R,(src,mod425) -> {mod425.getFarmerRegime2().setDanaReduction(src.getAmount());return true;}),
		CAG2_V6  (Mod303Key.CT_SA2A,(src,mod425) -> {mod425.getFarmerRegime2().setInputQuotas(src.getAmount());return true;}),
		CAG2_V7  (Mod303Key.CT_SA28,(src,mod425) -> {mod425.getFarmerRegime2().setQuota(src.getAmount());return true;}),
		                                    
		CAG3     (Mod303Key.CT_SA31,(src,mod425) -> {mod425.getFarmerRegime3().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
		CAG3_V1  (Mod303Key.CT_SA32,(src,mod425) -> {mod425.getFarmerRegime3().setIncomes(src.getAmount());return true;}),
		CAG3_V2  (Mod303Key.CT_SA33,(src,mod425) -> {mod425.getFarmerRegime3().setQuotaIndex(AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
		CAG3_V3  (Mod303Key.CT_SA34,(src,mod425) -> {mod425.getFarmerRegime3().setAccrualQuota(src.getAmount());return true;}),
		CAG3_V4  (Mod303Key.CT_SA3R,(src,mod425) -> {mod425.getFarmerRegime3().setDanaReduction(src.getAmount());return true;}),
		CAG3_V6  (Mod303Key.CT_SA3A,(src,mod425) -> {mod425.getFarmerRegime3().setInputQuotas(src.getAmount());return true;}),
		CAG3_V7  (Mod303Key.CT_SA38,(src,mod425) -> {mod425.getFarmerRegime3().setQuota(src.getAmount());return true;}),
		                                    
		CAG4     (Mod303Key.CT_SA41,(src,mod425) -> {mod425.getFarmerRegime4().setCodigo(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
		CAG4_V1  (Mod303Key.CT_SA42,(src,mod425) -> {mod425.getFarmerRegime4().setIncomes(src.getAmount());return true;}),
		CAG4_V2  (Mod303Key.CT_SA43,(src,mod425) -> {mod425.getFarmerRegime4().setQuotaIndex(AonMathUtils.round(src.getAmount() / 10000 ,5));return true;}),
		CAG4_V3  (Mod303Key.CT_SA44,(src,mod425) -> {mod425.getFarmerRegime4().setAccrualQuota(src.getAmount());return true;}),
		CAG4_V4  (Mod303Key.CT_SA4R,(src,mod425) -> {mod425.getFarmerRegime4().setDanaReduction(src.getAmount());return true;}),
		CAG4_V6  (Mod303Key.CT_SA4A,(src,mod425) -> {mod425.getFarmerRegime4().setInputQuotas(src.getAmount());return true;}),
		CAG4_V7  (Mod303Key.CT_SA48,(src,mod425) -> {mod425.getFarmerRegime4().setQuota(src.getAmount());return true;}),
		
		CAC1     (Mod303Key.CT_S101,(src,mod425) -> {mod425.getSimpRegime1().setEpigrafe(AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-")));return true;}),
		CAC1_M1U (Mod303Key.CT_S11I, (src,mod425) -> {mod425.getSimpRegime1().setUnit1(src.getAmount());return true;}),
		CAC1_M1I (Mod303Key.CT_S11R, (src,mod425) -> {mod425.getSimpRegime1().setAmount1(src.getAmount());return true;}),
		CAC1_M2U (Mod303Key.CT_S12I, (src,mod425) -> {mod425.getSimpRegime1().setUnit2(src.getAmount());return true;}),
		CAC1_M2I (Mod303Key.CT_S12R, (src,mod425) -> {mod425.getSimpRegime1().setAmount2(src.getAmount());return true;}),
		CAC1_M3U (Mod303Key.CT_S13I, (src,mod425) -> {mod425.getSimpRegime1().setUnit3(src.getAmount());return true;}),
		CAC1_M3I (Mod303Key.CT_S13R, (src,mod425) -> {mod425.getSimpRegime1().setAmount3(src.getAmount());return true;}),
		CAC1_M4U (Mod303Key.CT_S14I, (src,mod425) -> {mod425.getSimpRegime1().setUnit4(src.getAmount());return true;}),
		CAC1_M4I (Mod303Key.CT_S14R, (src,mod425) -> {mod425.getSimpRegime1().setAmount4(src.getAmount());return true;}),
		CAC1_M5U (Mod303Key.CT_S15I, (src,mod425) -> {mod425.getSimpRegime1().setUnit5(src.getAmount());return true;}),
		CAC1_M5I (Mod303Key.CT_S15R, (src,mod425) -> {mod425.getSimpRegime1().setAmount5(src.getAmount());return true;}),
		CAC1_M6U (Mod303Key.CT_S16I, (src,mod425) -> {mod425.getSimpRegime1().setUnit6(src.getAmount());return true;}),
		CAC1_M6I (Mod303Key.CT_S16R, (src,mod425) -> {mod425.getSimpRegime1().setAmount6(src.getAmount());return true;}),
		CAC1_M7U (Mod303Key.CT_S17I, (src,mod425) -> {mod425.getSimpRegime1().setUnit7(src.getAmount());return true;}),
		CAC1_M7I (Mod303Key.CT_S17R, (src,mod425) -> {mod425.getSimpRegime1().setAmount7(src.getAmount());return true;}),
		CAC1_C   (Mod303Key.CT_S117, (src,mod425) -> {
			mod425.getSimpRegime1().setBoxC(src.getAmount());
			mod425.setBox74( AonMathUtils.round(mod425.getBox74() + src.getAmount()));
			return true;}),
		
		CAC1_C1  (Mod303Key.CT_S1R1, (src,mod425) -> {mod425.getSimpRegime1().setBoxC1(src.getAmount());return true;}),
		CAC1_C2  (Mod303Key.CT_S1R2, (src,mod425) -> {mod425.getSimpRegime1().setBoxC2(src.getAmount());return true;}),
		CAC1_D   (Mod303Key.CT_S118, null),
		CAC1_Z   (Mod303Key.CT_S119, null),
//		CAC1_ZA  (Mod303Key.CAC1_ZA , null),
//		CAC1_ZD  (Mod303Key.CAC1_ZD , null),
		CAC1_E   (Mod303Key.CT_S120 , null),
		CAC1_F   (Mod303Key.CT_S121 , null),
//		CAC1_G0  (Mod303Key.CAC1_G0 , (src,mod390) -> {mod390.getSimpRegime1().setBoxD(
//				AonMathUtils.round(mod390.getSimpRegime1().getBoxD() + src.getAmount()));return true;}),
		CAC1_G   (Mod303Key.CT_S122 , (src,mod425) -> {mod425.getSimpRegime1().setBoxD(
				AonMathUtils.round(mod425.getSimpRegime1().getBoxD() + src.getAmount()));return true;}),
		CAC1_H   (Mod303Key.CT_S123 , (src,mod425) -> {mod425.getSimpRegime1().setBoxE(src.getAmount());return true;}),
//		CAC1_HA  (Mod303Key.CAC1_HA , null),
//		CAC1_HD  (Mod303Key.CAC1_HD , null),
//		CAC1_HT  (Mod303Key.CAC1_HT , null),
		CAC1_I   (Mod303Key.CT_S124 , (src,mod425) -> {mod425.getSimpRegime1().setBoxF(src.getAmount());return true;}),
		CAC1_J   (Mod303Key.CT_S125 , (src,mod425) -> {mod425.getSimpRegime1().setBoxG(src.getAmount());return true;}),
		CAC1_K   (Mod303Key.CT_S126 , null),  
		CAC1_L   (Mod303Key.CT_S127 , (src,mod425) -> {mod425.getSimpRegime1().setBoxI(src.getAmount());return true;}),
		CAC1_M   (Mod303Key.CT_S128 , (src,mod425) -> {mod425.getSimpRegime1().setBoxJ(src.getAmount());return true;}),
		
		
		CAC2     (Mod303Key.CT_S201    , (src,mod425) -> {
			String code = AonStringUtils.trim(AonStringUtils.substringBefore(src.getDescription(), "-"));					
			mod425.getSimpRegime2().setEpigrafe(code);
			return true;}),
		CAC2_M1U (Mod303Key.CT_S21I, (src,mod425) -> {mod425.getSimpRegime2().setUnit1(src.getAmount());return true;}),
		CAC2_M1I (Mod303Key.CT_S21R, (src,mod425) -> {mod425.getSimpRegime2().setAmount1(src.getAmount());return true;}),
		CAC2_M2U (Mod303Key.CT_S22I, (src,mod425) -> {mod425.getSimpRegime2().setUnit2(src.getAmount());return true;}),
		CAC2_M2I (Mod303Key.CT_S22R, (src,mod425) -> {mod425.getSimpRegime2().setAmount2(src.getAmount());return true;}),
		CAC2_M3U (Mod303Key.CT_S23I, (src,mod425) -> {mod425.getSimpRegime2().setUnit3(src.getAmount());return true;}),
		CAC2_M3I (Mod303Key.CT_S23R, (src,mod425) -> {mod425.getSimpRegime2().setAmount3(src.getAmount());return true;}),
		CAC2_M4U (Mod303Key.CT_S24I, (src,mod425) -> {mod425.getSimpRegime2().setUnit4(src.getAmount());return true;}),
		CAC2_M4I (Mod303Key.CT_S24R, (src,mod425) -> {mod425.getSimpRegime2().setAmount4(src.getAmount());return true;}),
		CAC2_M5U (Mod303Key.CT_S25I, (src,mod425) -> {mod425.getSimpRegime2().setUnit5(src.getAmount());return true;}),
		CAC2_M5I (Mod303Key.CT_S25R, (src,mod425) -> {mod425.getSimpRegime2().setAmount5(src.getAmount());return true;}),
		CAC2_M6U (Mod303Key.CT_S26I, (src,mod425) -> {mod425.getSimpRegime2().setUnit6(src.getAmount());return true;}),
		CAC2_M6I (Mod303Key.CT_S26R, (src,mod425) -> {mod425.getSimpRegime2().setAmount6(src.getAmount());return true;}),
		CAC2_M7U (Mod303Key.CT_S27I, (src,mod425) -> {mod425.getSimpRegime2().setUnit7(src.getAmount());return true;}),
		CAC2_M7I (Mod303Key.CT_S27R, (src,mod425) -> {mod425.getSimpRegime2().setAmount7(src.getAmount());return true;}),
		CAC2_C   (Mod303Key.CT_S217 , (src,mod425) ->	{
				mod425.getSimpRegime2().setBoxC(src.getAmount());
				mod425.setBox74( AonMathUtils.round(mod425.getBox74() + src.getAmount()));
				return true;
														}),
		CAC2_C1  (Mod303Key.CT_S2R1 , (src,mod425) -> {mod425.getSimpRegime2().setBoxC1(src.getAmount());return true;}),
		CAC2_C2  (Mod303Key.CT_S2R2 , (src,mod425) -> {mod425.getSimpRegime2().setBoxC2(src.getAmount());return true;}),
		CAC2_D   (Mod303Key.CT_S218, null),
		CAC2_Z   (Mod303Key.CT_S219 , null),
//		CAC2_ZA  (Mod303Key.CAC2_ZA , null),
//		CAC2_ZD  (Mod303Key.CAC2_ZD , null),
		CAC2_E   (Mod303Key.CT_S220 , null),
		CAC2_F   (Mod303Key.CT_S221 , null),
		
//		CAC2_G0  (Mod303Key.CAC2_G0 , (src,mod390) -> {mod390.getSimpRegime2().setBoxD(
//				AonMathUtils.round(mod390.getSimpRegime2().getBoxD() + src.getAmount()));return true;}),
		CAC2_G   (Mod303Key.CT_S222 , (src,mod425) -> {mod425.getSimpRegime2().setBoxD(
				AonMathUtils.round(mod425.getSimpRegime2().getBoxD() + src.getAmount()));return true;}),
		CAC2_H   (Mod303Key.CT_S223 , (src,mod425) -> {mod425.getSimpRegime2().setBoxE(src.getAmount());return true;}),
//		CAC2_HA  (Mod303Key.CAC2_HA , null),
//		CAC2_HD  (Mod303Key.CAC2_HD , null),
//		CAC2_HT  (Mod303Key.CAC2_HT , null),
		CAC2_I   (Mod303Key.CT_S224 , (src,mod425) -> {mod425.getSimpRegime2().setBoxF(src.getAmount());return true;}),
		CAC2_J	 (Mod303Key.CT_S225 , (src,mod425) -> {mod425.getSimpRegime2().setBoxG(src.getAmount());return true;}),
		CAC2_K   (Mod303Key.CT_S226 , null),  
		CAC2_L   (Mod303Key.CT_S227 , (src,mod425) -> {mod425.getSimpRegime2().setBoxI(src.getAmount());return true;}),
		CAC2_M   (Mod303Key.CT_S228 , (src,mod425) -> {mod425.getSimpRegime2().setBoxJ(src.getAmount());return true;}),
		
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
		public static void fill(SimplifedRegimeContext src,Mod4252025 mod425) {
			for (SimplifiedRegimeFiller filler : SimplifiedRegimeFiller.values()) {
				if (filler.getKey() ==  src.getKey() && filler.getFiller() != null) {
					filler.getFiller().fill(src, mod425);
				}
			}
		}
		
	}	
	
	private static Mod4252025 fillSimplifedRegimeData(AONContext ctx, Mod4252025 mod425) {
		mod425.setSimpRegime1(new SimpliedRegimeActivity());
		mod425.setSimpRegime2(new SimpliedRegimeActivity());
		mod425.setFarmerRegime1(new FarmerRegimeActivity());
		mod425.setFarmerRegime2(new FarmerRegimeActivity());
		mod425.setFarmerRegime3(new FarmerRegimeActivity());
		mod425.setFarmerRegime4(new FarmerRegimeActivity());
		mod425.setFarmerRegime5(new FarmerRegimeActivity());
		ctx.getDslContext().select(FS_MODEL_DETAIL.TYPE
				, FS_MODEL_DETAIL.DESCRIPTION
				, FS_MODEL_DETAIL.AMOUNT)
			.from(FS_MODEL)
			.join(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
			.where(FS_MODEL.DOMAIN.equal(mod425.getDomain()))
			.and(FS_MODEL.YEAR.equal(mod425.getYear()))
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
			SimplifiedRegimeFiller.fill(src,mod425);
			}
		);
		return mod425;
	}
	private static void fillSimplifiedDeclarationResults(AONContext ctx, Mod4252025 mod425) {
		mod425.setBox97( AonMathUtils.round(mod425.getBox97() * -1));
		mod425.setBox98( AonMathUtils.round(mod425.getBox98() * -1));
		if (mod425.getBox98() > 0) {
			mod425.setBox97( 0 ); 	
		}
		Record1<BigDecimal> rec = ctx.getDslContext()
			.select(DSL.sum(FS_MODEL_DETAIL.AMOUNT))
			.from(FS_MODEL)
			.join(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
			.where(FS_MODEL.DOMAIN.equal(mod425.getDomain()))
			.and(FS_MODEL.YEAR.equal(mod425.getYear()))
			.and(FS_MODEL_DETAIL.AMOUNT.greaterThan(0.0))
			.and(FS_MODEL.MODEL.equal("303"))
			.and(FS_MODEL_DETAIL.TYPE.equal("303-71"))
			.fetchOne();
		if (rec != null) {
			BigDecimal quota = rec.getValue(DSL.sum(FS_MODEL_DETAIL.AMOUNT)); 
			if (quota != null) {
				mod425.setBox95( quota.doubleValue());
			}
		}
	}

	public static Mod4252025 changeStatus(AONContext ctx, Mod4252025 mod425, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod425.getId() != null) {
				mod425.setStatus(newStatus);
				ctx.getDslContext().update(FS_MODEL390)
					.set(FS_MODEL390.STATUS,AonEnumUtils.getByte(mod425.getStatus()))
					.where(FS_MODEL390.ID.equal(mod425.getId()))
					.execute();
			}
			return mod425;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static double getVatAccrualPaymentOutputBase(AONContext ctx, Mod390 mod425) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod425.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod425);
		return Mod390DAO.getVatAccrualPaymentOutputBase(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentOutputQuota(AONContext ctx, Mod390 mod425) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod425.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod425);
		return Mod390DAO.getVatAccrualPaymentOutputQuota(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentInputBase(AONContext ctx, Mod390 mod425) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod425.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod425);
		return Mod390DAO.getVatAccrualPaymentInputBase(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentInputQuota(AONContext ctx, Mod390 mod425) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod425.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod425);
		return Mod390DAO.getVatAccrualPaymentInputQuota(ctx,fromDate,toDate);
	}

	// *******************
	public static Mod4252025 aeatPresentation(AONContext ctx, Mod4252025 mod, String aeatResponse) {
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
	private static boolean hasPercent2(VatContext vat) {
		return vat.getPercentage() == PERCENT2;
	}
	private static boolean hasPercent4(VatContext vat) {
		return vat.getPercentage() == PERCENT4;
	}
	private static boolean hasPercent5(VatContext vat) {
		return vat.getPercentage() == PERCENT5;
	}
	private static boolean hasPercent75(VatContext vat) {
		return vat.getPercentage() == PERCENT75;
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
	private static boolean hasSurchargePercent026(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT026;
	}
	private static boolean hasSurchargePercent05(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT05;
	}
	private static boolean hasSurchargePercent062(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT062;
	}
	private static boolean hasSurchargePercent10(VatContext vat) {
		return vat.getSurchargePercent() == SURCHARGE_PERCENT10;
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
	
	private static boolean isCommonNationalSales(VatContext vat, Mod4252025 mod) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isNational()
			&& vat.isSales() 
			&& !vat.isRectification();
	}
	private static boolean isCommonNationalSalesRECT(VatContext vat, Mod4252025 mod) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isNational()
			&& vat.isSales() 
			&& vat.isRectification();
	}
	private static boolean isIntracommunityPurchase(VatContext vat, Mod4252025 mod) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isRectification() 
			&& vat.isIntracommunityPurchase();
	}
	private static boolean isIntracommunityExpenses(VatContext vat, Mod4252025 mod) {
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
	
	private static boolean modificacionBasesYCuotasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
				&& !vat.isVatSurchargeRegime() 
				&& vat.isRectification()
				&& (vat.isNationalSales() 
					|| (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses())
					|| (vat.isOtherISPPurchase() || vat.isOtherISPExpenses() || vat.isExtracommunityExpenses()
						|| vat.isCanCeuMelExpenses() || (vat.isExtracommunityPurchase() && vat.isService())
						|| (vat.isCanCeuMelPurchase() && vat.isService())));
	}
	
	private static boolean operacionesInterioresExentasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isRectification() 
			&& !vat.isFarmerRegime()
			&& AonMathUtils.isZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses() || isOperacionesISPFilter(vat));
	}
	
	// Comprobar si la factura esta unida a un modelo 303 con porcentaje de prorrata
	private static KeyedVatContext checkProrrated(AONContext ctx, Mod390 mod425, KeyedVatContext kvc) {
		
		Double prorratePercent = 
		ctx.getDslContext().select(FS_MODEL_DETAIL.AMOUNT)
			.from(ALCATRAZ)
			.leftJoin(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.leftJoin(FS_MODEL_DETAIL).on(FS_MODEL_DETAIL.FS_MODEL.equal(FS_MODEL.ID))
			.where(ALCATRAZ.INVOICE.equal(kvc.getVatContext().getInvoice()))
			.and(FS_MODEL.MODEL.equal("IVA"))
			.and(FS_MODEL_DETAIL.TYPE.equal("303-CM003"))  // Porcentaje de prorrata
			.and(FS_MODEL.YEAR.equal(mod425.getYear()))
			.fetchAny(FS_MODEL_DETAIL.AMOUNT);
		
		if (prorratePercent != null && prorratePercent > 0) {
			kvc.getVatContext().setProrrated(true);
			kvc.getVatContext().setProrratePercent(prorratePercent);	
			kvc.getVatContext().setProrrateQuota(AonMathUtils.round(kvc.getVatContext().getDeductibleQuota() * prorratePercent / 100));
		}
		return kvc;
		
	}	

}
