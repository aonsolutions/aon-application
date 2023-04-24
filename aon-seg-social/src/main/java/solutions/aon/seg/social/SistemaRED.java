package solutions.aon.seg.social;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.exception.ForbiddenException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Calc;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.object.PaternityCertificate;
import solutions.aon.seg.social.object.Period;
import solutions.aon.seg.social.object.SecondaryUser;
import solutions.aon.seg.social.object.SituationType;
import solutions.aon.seg.social.object.WorkerLiquidation;

public class SistemaRED {

	// Origen de la liquidaci�n
	public static enum LiquidationOrigin {
		PRESENTADAS_POR_LA_EMPRESA("E"), GENERADAS_POR_LA_TGSS("G"), TODAS("T");

		private String value;

		private LiquidationOrigin(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public static enum Regime {
		GENERAL("0111"), GENERAL_ARTISTAS("0112"), GENERAL_CONSERVAS_VEGETALES("0132"), GENERAL_HOSTELERIA("0135"),
		GENERAL_CINEMATOG("0136"), GENERAL_OPINION_PUBLICA("0137"), GENERAL_AGRARIO("0163"),
		ESPECIAL_MAR_GRUPO_1("0811"), ESPECIAL_MAR_GRUPO_2A("0812"), ESPECIAL_MAR_GRUPO_2B("0813"),
		ESPECIAL_MAR_GRUPO_3("0814"), ESPECIAL_MAR_ASIMILADOS_GRUPO_1("0821"), ESPECIAL_MAR_ASIMILADOS_GRUPO_2A("0822"),
		ESPECIAL_MAR_ASIMILADOS_GRUPO_2B("0823");

		private String value;

		private Regime(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static Regime fromValue(String value) {
			for (Regime regime : Regime.values()) {
				if (regime.getValue().equals(value))
					return regime;
			}
			return Regime.GENERAL;
		}
	}

	public static enum LiquidationType {
		L00_NORMAL("L00"), C02_COMP_SALARIOS_TRAMITACION_NO_CONCERTADOS("C02"),
		C03_COMP_SALARIOS_RETROACTIVOS_NO_CONCERTADO("C03"), C13_COMP_VACAC_RETRIBUIDAS_NO_CONCERTADOS("C13"),
		C90_COMP_POR_INCREMENTO_BASES_NO_CONCERTADOS("C90"), C91_COMP_NUEVOS_TRAB_Y_O_TRAMOS_NO_CONCERTA("C91"),
		L02_COMPLEMENTARIA_POR_SALARIOS_TRAM_NORMAL("L02"), L03_COMP_ABONO_SALARIOS_CARACTER_RETROACTIV("L03"),
		L13_VACACIONES_RETRIBUIDAS("L13"), L90_COMPLEMENTARIA_POR_INCREMENTO_DE_BASES("L90"),
		L91_COMP_NUEVOS_TRABAJADORES_Y_O_TRAMOS("L91"), L92_COMP_SALARIOS_TRAMITACION_DE_OFICIO("L92"),
		L93_COMP_VAC_RETR_Y_NO_DISFR_DE_OFICIO("L93"), V03_COMP_ABONO_SALARIOS_RETROACTIVOS_DE_L13("V03"),
		V90_COMP_POR_INCREMENTO_DE_BASES_DE_L13("V90"), TODAS("T");

		private String value;

		private LiquidationType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum AccidentType {
		LEVE, GRAVE, MUY_GRAVE
	}

	public enum CauseType {
		CURACION("01"), FALLECIMIENTO("02"), INSPECCION_MEDICA("03"), PROPUESTA_INVALIDEZ("04"),
		AGOTAMIENTO_PLAZO("05"), MEJORIA_PERMITE_TRABAJAR("06"), INCOMPARECENCIA("07"), CONTROL_INSS_12_MESES("10"),
		RECUP_CAPACIDAD_PROF("17"), INCOMP_CTOS_FORM("18"), INICIO_DE_MATERNIDAD("20"),
		ALTA_MEDICA_INSPECCION_INSS("53"), PROPUESTA_DE_IP_EN_INSS("55"), FALLECIMIENTO_COMUNICADO_DESDE_EL_INSS("56"),
		ALTA_MATEPSS_ARTICULO_128("57");

		private String value;

		private CauseType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static CauseType safeValueOf(Byte i) {
			if (i == null)
				return null;
			return safeValueOf(i.intValue());
		}

		public static CauseType safeValueOf(Integer i) {
			if (i == null)
				return null;
			if (i < 0 || i >= CauseType.values().length)
				return null;
			return CauseType.values()[i];
		}
	}

	// CONTINGENCIES
	public enum Contingencies {
		ENFERMEDAD_COMUN, ACCIDENTE_NO_LABORAL, ACCIDENT_LABORAL, ENFERMEDAD_PROFESIONAL, PERIODOS_OBSERVACION;

		public static Contingencies safeValueOf(Byte i) {
			if (i == null)
				return null;
			return safeValueOf(i.intValue());
		}

		public static Contingencies safeValueOf(Integer i) {
			if (i == null)
				return null;
			if (i < 0 || i >= Contingencies.values().length)
				return null;
			return Contingencies.values()[i];
		}
	}

	// CONTRACTS
	public enum ContractType {
		FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL, RESTO_Y_AUTONOMOS;

		public static ContractType safeValueOf(Byte i) {
			if (i == null)
				return null;
			return safeValueOf(i.intValue());
		}

		public static ContractType safeValueOf(Integer i) {
			if (i == null)
				return null;
			if (i < 0 || i >= ContractType.values().length)
				return null;
			return ContractType.values()[i];
		}

		public static ContractType safeValueOf(String i) {
			for (ContractType rs : ContractType.values()) {
				if (i.equalsIgnoreCase(rs.name()))
					return rs;
			}
			return null;
		}
	}

	// PART TYPE
	public enum PartType {
		BAJA("Baja"), CONFIRMACION("Confirmaci\u00F3n"), ALTA("Alta");

		private String description;

		private PartType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public static PartType safeValueOf(Byte i) {
			if (i == null)
				return null;
			return safeValueOf(i.intValue());
		}

		public static PartType safeValueOf(Integer i) {
			if (i == null)
				return null;
			if (i < 0 || i >= PartType.values().length)
				return null;
			return PartType.values()[i];
		}

		public static PartType safeValueOf(String i) {
			for (PartType rs : PartType.values()) {
				if (i.equalsIgnoreCase(rs.name()))
					return rs;
			}
			return null;
		}
	}

	public enum SituationEmployee {
		ACTIVO, PERCEPTOR_DE_DESEMPLEO;

		public static SituationEmployee safeValueOf(Byte i) {
			if (i == null)
				return null;
			return safeValueOf(i.intValue());
		}

		public static SituationEmployee safeValueOf(Integer i) {
			if (i == null)
				return null;
			if (i < 0 || i >= ContractType.values().length)
				return null;
			return SituationEmployee.values()[i];
		}
	}

	public SistemaRED() {
	}

	public static Collection<Employee> getTotalEmployees(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws SegSocialException {
		return SistemaREDEmployee.getTotalEmployees(certificateInputStream, certificatePassword, certificateType,
				regimen, ccc);
	}

	public static Collection<Employee> getTotalEmployees(final byte[] certificateData, final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return getTotalEmployees(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Collection<Employee> getEmployees(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDEmployee.getEmployees(certificateInputStream, certificatePassword, certificateType,
					regimen, ccc);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getCccLaboralLife(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regime, String ccc, Date from, Date to) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDEmployee.getCccLaboralLife(certificateInputStream, certificatePassword, certificateType,
					regime, ccc, from, to);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getCccLaboralLife(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date from, Date to) throws SegSocialException {
		try {
			return SistemaREDEmployee.getCccLaboralLife(certificateInputStream, certificatePassword, certificateType,
					regime, ccc, from, to);
		} catch (IOException e) {
			throw new InvalidCertificateException();
		}
	}

	public static byte[] getLaboralLife(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, String nss) throws SegSocialException {
		try {
			return SistemaREDEmployee.getLaboralLife(certificateInputStream, certificatePassword, certificateType,
					regime, ccc, nss);
		} catch (IOException e) {
			throw new InvalidCertificateException();
		}
	}

	public static Collection<Employee> getPrevEmployees(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDEmployee.getPrevEmployees(certificateInputStream, certificatePassword, certificateType,
					regimen, ccc);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Collection<Employee> getEmployees(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws SegSocialException {
		return SistemaREDEmployee.getEmployees(certificateInputStream, certificatePassword, certificateType, regimen,
				ccc);
	}

	/**
	 * Use com.esferalia.aon.in.payroll.SistemaRED2AON.getEmployeeToIDC
	 */
	@Deprecated
	public static Employee getEmployee(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDEmployee.getEmployee(certificateInputStream, certificatePassword, certificateType, regimen,
					ccc, nss);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Map<String, Map<String, WorkerLiquidation>> getCosts(final byte certificateData[],
			final String certificatePassword, final String certificateType, String regimen, String ccc, Date startDate,
			Date endDate) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return getCosts(certificateInputStream, certificatePassword, certificateType, regimen, ccc, startDate,
					endDate);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Map<String, Map<String, WorkerLiquidation>> getCosts(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc, Date startDate,
			Date endDate) throws SegSocialException {
		return SistemaREDI.workersCalculationQueryByCCC(certificateInputStream, certificatePassword, certificateType,
				ccc, SistemaRED.Regime.fromValue(regimen), startDate, endDate, SistemaRED.LiquidationType.TODAS,
				SistemaRED.LiquidationOrigin.TODAS);
	}

	public static byte[] getTA(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException {
		return SistemaREDI.getTADuplicate(certificateInputStream, certificatePassword, certificateType, nss, regimen,
				ccc, date);
	}

	public static byte[] getTA(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDI.getTADuplicate(certificateInputStream, certificatePassword, certificateType, nss,
					regimen, ccc, date);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getIDC(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException {
		return SistemaREDI.getContributionInformation(certificateInputStream, certificatePassword, certificateType, nss,
				regimen, ccc, date);
	}

	public static byte[] getIDC(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDI.getContributionInformation(certificateInputStream, certificatePassword, certificateType,
					nss, regimen, ccc, date);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Collection<Idc> getIDC(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
		try {
			return getIDCDates(certificateInputStream.readAllBytes(), certificatePassword, certificateType, regimen,
					ccc, nss);
		} catch (Exception e) {
			if (e.getMessage() != null) {
				throw new SegSocialException(e.getMessage());
			} else {
				throw new SegSocialException(e);
			}
		}
	}

	public static Collection<Idc> getIDCDates(final byte[] certificateData, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
		return SistemaREDI.getIDCDates(certificateData, certificatePassword, certificateType, nss, regimen, ccc);
	}

	public static byte[] getIDCCCC(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, Date date) throws SegSocialException {
		return SistemaREDI.getContributionInformationCCC(certificateInputStream, certificatePassword, certificateType,
				regimen, ccc, date);
	}

	public static byte[] getIDCCCC(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc, Date date) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDI.getContributionInformationCCC(certificateInputStream, certificatePassword,
					certificateType, regimen, ccc, date);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getIDCNSSfinal(InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException {
		return SistemaREDI.getContributionInformationNSS(certificateInputStream, certificatePassword, certificateType,
				regimen, ccc, nss, date);
	}

	public static byte[] getIDCNSS(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDI.getContributionInformationNSS(certificateInputStream, certificatePassword,
					certificateType, regimen, ccc, nss, date);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getUp2DateSS(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		return SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, certificatePassword,
				certificateType, regimen, ccc);
	}

	public static byte[] getUp2DateSS(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, certificatePassword,
					certificateType, regimen, ccc);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	private static void evalSwitch(FailingHttpStatusCodeException e) throws ForbiddenException, SegSocialException {
		switch (e.getStatusCode()) {
		case 403:
			throw new ForbiddenException();
		default:
			throw new SegSocialException(e);
		}
	}

	public static Map<String, Map<String, Map<Period, Map<String, Calc>>>> getCalcByCCC(final byte[] certificateData,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo,
			final SistemaRED.LiquidationType liqType, final SistemaRED.LiquidationOrigin liqOrigin)
			throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return getCalcByCCC(certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom,
					dateTo, liqType, liqOrigin);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Map<String, Map<String, Map<Period, Map<String, Calc>>>> getCalcByCCC(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
			final String ccc, final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo,
			final SistemaRED.LiquidationType liqType, final SistemaRED.LiquidationOrigin liqOrigin)
			throws SegSocialException {

		return Calculations.workersCalculationQueryByCCC(certificateInputStream, certificatePassword, certificateType,
				ccc, regime, dateFrom, dateTo, liqType, liqOrigin);

	}

	public static Map<String, Map<String, Map<Period, Map<String, Calc>>>> getCalcByNAF(final byte[] certificateData,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo,
			final SistemaRED.LiquidationType liqType, final SistemaRED.LiquidationOrigin liqOrigin, String authorized,
			String... nafs) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return Calculations.workersCalculationByCCCandNAFS(certificateInputStream, certificatePassword,
					certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin, authorized, nafs);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Map<String, Map<String, Map<Period, Map<String, Calc>>>> getCalcByNAF(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
			final String ccc, final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo,
			final SistemaRED.LiquidationType liqType, final SistemaRED.LiquidationOrigin liqOrigin, String authorized,
			String... nafs) throws SegSocialException {
		return Calculations.workersCalculationByCCCandNAFS(certificateInputStream, certificatePassword, certificateType,
				ccc, regime, dateFrom, dateTo, liqType, liqOrigin, authorized, nafs);
	}

	public static Map<String, Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCC(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
			final String ccc, final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo,
			final SistemaRED.LiquidationType liqType, final SistemaRED.LiquidationOrigin liqOrigin)
			throws SegSocialException {
		return SistemaREDI.workersCalculationQueryByCCC(certificateInputStream, certificatePassword, certificateType,
				ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
	}

	public static Map<String, Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCC(final byte[] certificateData,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo,
			final SistemaRED.LiquidationType liqType, final SistemaRED.LiquidationOrigin liqOrigin)
			throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDI.workersCalculationQueryByCCC(certificateInputStream, certificatePassword,
					certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Map<String, Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCCandNAFs(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
			final String ccc, final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo,
			final SistemaRED.LiquidationType liqType, final SistemaRED.LiquidationOrigin liqOrigin, String... nafs)
			throws SegSocialException {
		return SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, certificatePassword,
				certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin, nafs);
	}

	public static Map<String, Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCCandNAFs(
			final byte[] certificateData, final String certificatePassword, final String certificateType,
			final String ccc, final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo,
			final SistemaRED.LiquidationType liqType, final SistemaRED.LiquidationOrigin liqOrigin, String... nafs)
			throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, certificatePassword,
					certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin, nafs);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static void movPrevDelete(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, SituationType situationType, String regimen, String ctaCti, String nss,
			Date fecha) throws SegSocialException {
		SistemaREDMov.movPrevDelete(certificateInputStream, certificatePassword, certificateType, situationType,
				regimen, ctaCti, nss, fecha);
	}

	public static void removeMovConsolidated(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, SituationType situationType, String regimen, String ctaCti, String nss,
			String ipf, Date date) throws SegSocialException {
		SistemaREDMov.removeMovConsolidated(certificateInputStream, certificatePassword, certificateType, situationType,
				regimen, ctaCti, nss, ipf, date);
	}

	@Deprecated
	public static void movPrevDelete(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String situation, String regimen, String ctaCti, String nss, Date fecha)
			throws SegSocialException {
		SituationType situationType = situation.indexOf("AL") >= 0 ? SituationType.ALTA : SituationType.BAJA;
		SistemaREDMov.movPrevDelete(certificateInputStream, certificatePassword, certificateType, situationType,
				regimen, ctaCti, nss, fecha);
	}

	public static Collection<Employee> ipfxnaf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, List<String> nssList)
			throws SegSocialException {
		return SistemaREDMov.ipfxnaf(certificateInputStream, certificatePassword, certificateType, nssList);
	}

	public static Employee nafxipf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String apellido1, String apellido2) throws SegSocialException {
		return SistemaREDMov.nafxipf(certificateInputStream, certificatePassword, certificateType, ipf, apellido1,
				apellido2);
	}

	public static void cambioGrupCtz(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String grup_ctz,
			Date fecha) throws SegSocialException {

		SistemaREDMov.updateQuoteGroup(certificateInputStream, certificatePassword, certificateType, ipf, regimen,
				ctaCti, nss, grup_ctz, fecha);
	}

	public static void cambioOcupacion(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String ocup,
			Date fecha) throws SegSocialException {

		SistemaREDMov.updateOccupation(certificateInputStream, certificatePassword, certificateType, ipf, regimen,
				ctaCti, nss, ocup, fecha);
	}
	
	public static void cambioCno(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String cno,
			Date fecha) throws SegSocialException {

		SistemaREDMov.updateCno(certificateInputStream, certificatePassword, certificateType, ipf, regimen,
				ctaCti, nss, cno, fecha);
	}

	public static void cambioCatProf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String cat, Date fecha)
			throws SegSocialException {

		SistemaREDMov.updateCatProf(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti,
				nss, cat, fecha);
	}

	public static void cambioContratoCoef(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, Date fechaCambio,
			Optional<String> contract, String coef) throws SegSocialException {

		SistemaREDMov.updateContractCoef(certificateInputStream, certificatePassword, certificateType, ipf, regimen,
				ctaCti, nss, fechaCambio, contract, coef);
	}

	public static void cambioContratoCoef(final byte[] certificateData, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, Date fechaCambio,
			Optional<String> contract, String coef) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDMov.updateContractCoef(certificateInputStream, certificatePassword, certificateType, ipf, regimen,
					ctaCti, nss, fechaCambio, contract, coef);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static boolean sendPaternity(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final String docNum,
			final PaternityCertificate.ApplicantType applicantType, PaternityCertificate.ReasonType reason,
			final Date dateFrom, final Date dateTo, final float baseCC, final float baseCP, final int days)
			throws SegSocialException {
		return Paternity.sendPaternity(certificateInputStream, certificatePassword, certificateType, affiliationNumber,
				regime, contributionAccount, docNum, applicantType, reason, dateFrom, dateTo, baseCC, baseCP, days);
	}

	public static boolean sendPaternity(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final String docNum,
			final PaternityCertificate.ApplicantType applicantType, PaternityCertificate.ReasonType reason,
			final Date dateFrom, final Date dateTo, final float baseCC, final float baseCP, final int days)
			throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return Paternity.sendPaternity(certificateInputStream, certificatePassword, certificateType,
					affiliationNumber, regime, contributionAccount, docNum, applicantType, reason, dateFrom, dateTo,
					baseCC, baseCP, days);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static void removePaternity(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String nss, final String regime, final String ccc, final Date dateFrom,
			final Date dateTo, final Optional<Date> startDate) throws SegSocialException {
		Paternity.removePaternity(certificateInputStream, certificatePassword, certificateType, nss, regime, ccc,
				dateFrom, dateTo, startDate);
	}

	public static void removePaternity(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String nss, final String regime, final String ccc, final Date dateFrom,
			final Date dateTo, final Optional<Date> startDate) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			Paternity.removePaternity(certificateInputStream, certificatePassword, certificateType, nss, regime, ccc,
					dateFrom, dateTo, startDate);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getCertificatePdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final Date dateFrom, final Date dateTo, final Optional<Date> startDate)
			throws SegSocialException {
		return Paternity.getCertificatePdf(certificateInputStream, certificatePassword, certificateType,
				affiliationNumber, regime, contributionAccount, dateFrom, dateTo, startDate);
	}

	public static byte[] getCertificatePdf(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final Date dateFrom, final Date dateTo, final Optional<Date> startDate)
			throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return Paternity.getCertificatePdf(certificateInputStream, certificatePassword, certificateType,
					affiliationNumber, regime, contributionAccount, dateFrom, dateTo, startDate);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Collection<SecondaryUser> getSecondaryUsers(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType) throws SegSocialException {
		return ServicioREDSecondaryUser.getSecondaryUsers(certificateInputStream, certificatePassword, certificateType);
	}

	public static Collection<SecondaryUser> getSecondaryUsers(final byte[] certificateData,
			final String certificatePassword, final String certificateType) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return ServicioREDSecondaryUser.getSecondaryUsers(certificateInputStream, certificatePassword,
					certificateType);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static void deleteSecondaryUser(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ipfType, final String ipf) throws SegSocialException {
		ServicioREDSecondaryUser.deleteSecondaryUser(certificateInputStream, certificatePassword, certificateType,
				ipfType, ipf);
	}

	public static void deleteSecondaryUser(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String ipfType, final String ipf) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			ServicioREDSecondaryUser.deleteSecondaryUser(certificateInputStream, certificatePassword, certificateType,
					ipfType, ipf);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static void registerSecondaryUserByNie(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ipfType, final String nie,
			String naf) throws SegSocialException {
		ServicioREDSecondaryUser.registerSecondaryUserByNie(certificateInputStream, certificatePassword,
				certificateType, ipfType, nie, naf);
	}

	public static void registerSecondaryUserByNie(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String ipfType, final String nie, String naf)
			throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			ServicioREDSecondaryUser.registerSecondaryUserByNie(certificateInputStream, certificatePassword,
					certificateType, ipfType, nie, naf);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	// --------------------------------------------------------------------------------------------------------------
	// IT PARTS
	// --------------------------------------------------------------------------------------------------------------

	public static Collection<It> getIts(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String regime, final String ccc, final Date startDate,
			final Date endDate, final Optional<String> naf) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDITPart.getIts(certificateInputStream, certificatePassword, certificateType, regime, ccc,
					startDate, endDate, naf);
		} catch (IOException | SegSocialException e) {
			throw new SegSocialException(e);
		}
	}

	public static Collection<It> getIts(final InputStream cert, final String certificatePassword,
			final String certificateType, final String regime, final String ccc, final Date startDate,
			final Date endDate, final Optional<String> naf) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(cert.readAllBytes())) {
			return SistemaREDITPart.getIts(certificateInputStream, certificatePassword, certificateType, regime, ccc,
					startDate, endDate, naf);
		} catch (IOException | SegSocialException e) {
			throw new SegSocialException(e);
		}
	}

	public static void registerITBaja(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String regime, final String ccc, final String naf,
			final SistemaRED.Contingencies contingency, final SistemaRED.SituationEmployee situation_employee,
			final Date startdate, final SistemaRED.ContractType contractType, final float baseCot, final int cotDays,
			final Optional<Date> fATEP, final Optional<SistemaRED.AccidentType> accidentType,
			final Optional<String> licenseNumber, final Optional<String> cias, final Optional<String> occupation)
			throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITPart.registerItBaja(certificateInputStream, certificatePassword, certificateType, regime, ccc,
					naf, contingency, situation_employee, startdate, contractType, baseCot, cotDays, fATEP,
					accidentType, licenseNumber, cias, occupation);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static void registerITConfirmation(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String regime, final String ccc, final String naf,
			final SistemaRED.Contingencies contingency, final SistemaRED.SituationEmployee situation_employee,
			final Optional<String> licenseNumber, final Optional<String> cias, final Date fbaja,
			final Date fconfirmation, final Optional<String> npartConfimation) throws SegSocialException {

		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITPart.registerItConfirmation(certificateInputStream, certificatePassword, certificateType,
					regime, ccc, naf, contingency, situation_employee, licenseNumber, cias, fbaja, fconfirmation,
					npartConfimation);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static void registerITAlta(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String regime, final String ccc, final String naf,
			final SistemaRED.Contingencies contingency, final SistemaRED.SituationEmployee situation_employee,
			final Date fbaja, final Date falta, final Optional<Date> fATEP,
			final Optional<SistemaRED.AccidentType> accidentType, final SistemaRED.CauseType causeType,
			final Optional<String> licenseNumber, final Optional<String> cias) throws SegSocialException {

		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITPart.registerItAlta(certificateInputStream, certificatePassword, certificateType, regime, ccc,
					naf, contingency, situation_employee, fbaja, falta, fATEP, accidentType, causeType, licenseNumber,
					cias);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static void removeIT(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String regime, final String ccc, final String naf,
			final SistemaRED.PartType partType, final Date dateBj, Date dateProcess) throws SegSocialException {

		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITPart.removeIt(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf,
					partType, dateBj, dateProcess);
		} catch (IOException | InterruptedException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getITReport(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String regime, final String ccc, final String naf,
			final SistemaRED.PartType partType, final Date dateBj, final Date dateProcess) throws SegSocialException {

		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDITPart.getITReport(certificateInputStream, certificatePassword, certificateType, regime,
					ccc, naf, partType, dateBj, dateProcess);
		} catch (IOException | InterruptedException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getReportAffiliateInAlta(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDMov.getReportAffiliateInAlta(certificateInputStream, certificatePassword, certificateType,
					regimen, ccc);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getReportAffiliateInAlta(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws SegSocialException {
		return SistemaREDMov.getReportAffiliateInAlta(certificateInputStream, certificatePassword, certificateType,
				regimen, ccc);
	}

	public static byte[] getReportAffiliateInMovPrev(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDMov.getReportAffiliateInMovPrev(certificateInputStream, certificatePassword,
					certificateType, regimen, ccc);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getReportAffiliateInMovPrev(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws SegSocialException {
		return SistemaREDMov.getReportAffiliateInMovPrev(certificateInputStream, certificatePassword, certificateType,
				regimen, ccc);
	}

	public static byte[] sendAlta(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException {
		return SistemaREDMov.sendAlta(certificateInputStream, certificatePassword, certificateType, employee);
	}

	public static byte[] sendAlta(final byte[] certificateData, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDMov.sendAlta(certificateInputStream, certificatePassword, certificateType, employee);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] sendBaja(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException {
		return SistemaREDMov.sendBaja(certificateInputStream, certificatePassword, certificateType, employee);
	}

	public static byte[] sendBaja(final byte[] certificateData, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDMov.sendBaja(certificateInputStream, certificatePassword, certificateType, employee);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static void validateCert(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws SegSocialException {
		SistemaREDMov.validateCert(certificateInputStream, certificatePassword, certificateType);
	}

	public static void setCnoCertificate(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String nss, String dni, String regimen, String cc, String cno)
			throws Exception {
		AonSegSocialJuanma.setCnoCertificate(certificateInputStream, certificatePassword, certificateType, nss, dni,
				regimen, cc, cno);

	}

	public static void onlineSettlement(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String authorized, String ccc, String regimen, String startMonth,
			String startYear, String endMonth, String endYear, String liquidationType) throws Exception {
		AonSegSocialJuanma.onlineSettlementOptionCCC(certificateInputStream, certificatePassword, certificateType,
				authorized, ccc, regimen, startMonth, startYear, endMonth, endYear, liquidationType);

	}

	public static void onlineSettlement(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String authorized, String liquidationNumber, String rnt) throws Exception {
		AonSegSocialJuanma.OnlineSettlementOptionLiquidationNumber(certificateInputStream, certificatePassword, certificateType,
				authorized, liquidationNumber, rnt);
	}

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

	}

}
