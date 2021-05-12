package solutions.aon.seg.social;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.seg.social.SistemaRED.AccidentType;
import solutions.aon.seg.social.SistemaRED.CauseType;
import solutions.aon.seg.social.SistemaRED.Contingencies;
import solutions.aon.seg.social.SistemaRED.ContractType;
import solutions.aon.seg.social.SistemaRED.LiquidationOrigin;
import solutions.aon.seg.social.SistemaRED.LiquidationType;
import solutions.aon.seg.social.SistemaRED.PartType;
import solutions.aon.seg.social.SistemaRED.Regime;
import solutions.aon.seg.social.SistemaRED.SituationEmployee;
import solutions.aon.seg.social.exception.ForbiddenException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Calc;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.Period;
import solutions.aon.seg.social.object.SecondaryUser;
import solutions.aon.seg.social.object.WorkerLiquidation;

public class SistemaRED {

	//Origen de la liquidación
	public static enum LiquidationOrigin{
		PRESENTADAS_POR_LA_EMPRESA("E"),
		GENERADAS_POR_LA_TGSS("G"),
		TODAS("T");
		private String value;
		private LiquidationOrigin(String value) {
			this.value=value;
		}
		public String getValue() {
			return value;
		}
	}


	public static enum Regime{
		GENERAL("0111"),
		GENERAL_ARTISTAS("0112"),
		GENERAL_CONSERVAS_VEGETALES("0132"),
		GENERAL_HOSTELERIA("0135"),
		GENERAL_CINEMATOG("0136"),
		GENERAL_OPINION_PUBLICA("0137"),
		GENERAL_AGRARIO("0163"),
		ESPECIAL_MAR_GRUPO_1("0811"),
		ESPECIAL_MAR_GRUPO_2A("0812"),
		ESPECIAL_MAR_GRUPO_2B("0813"),
		ESPECIAL_MAR_GRUPO_3("0814"),
		ESPECIAL_MAR_ASIMILADOS_GRUPO_1("0821"),
		ESPECIAL_MAR_ASIMILADOS_GRUPO_2A("0822"),
		ESPECIAL_MAR_ASIMILADOS_GRUPO_2B("0823");
		private String value;
		private Regime(String value) {
			this.value=value;
		}
		public String getValue() {
			return value;
		}
		
		public static Regime fromValue(String value) {
			for (Regime regime : Regime.values()) {
				if ( regime.getValue().equals(value))
					return regime;
			}
			return Regime.GENERAL;
		}
	}


	public static enum LiquidationType{
		L00_NORMAL("L00"),
		C02_COMP_SALARIOS_TRAMITACION_NO_CONCERTADOS("C02"),
		C03_COMP_SALARIOS_RETROACTIVOS_NO_CONCERTADO("C03"),
		C13_COMP_VACAC_RETRIBUIDAS_NO_CONCERTADOS("C13"),
		C90_COMP_POR_INCREMENTO_BASES_NO_CONCERTADOS("C90"),
		C91_COMP_NUEVOS_TRAB_Y_O_TRAMOS_NO_CONCERTA("C91"),
		L02_COMPLEMENTARIA_POR_SALARIOS_TRAM_NORMAL("L02"),
		L03_COMP_ABONO_SALARIOS_CARACTER_RETROACTIV("L03"),
		L13_VACACIONES_RETRIBUIDAS("L13"),
		L90_COMPLEMENTARIA_POR_INCREMENTO_DE_BASES("L90"),
		L91_COMP_NUEVOS_TRABAJADORES_Y_O_TRAMOS("L91"),
		L92_COMP_SALARIOS_TRAMITACIÓN_DE_OFICIO("L92"),
		L93_COMP_VAC_RETR_Y_NO_DISFR_DE_OFICIO("L93"),
		V03_COMP_ABONO_SALARIOS_RETROACTIVOS_DE_L13("V03"),
		V90_COMP_POR_INCREMENTO_DE_BASES_DE_L13("V90"),
		TODAS("T");
		
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
	}


	// CONTINGENCIES
	public enum Contingencies {
		ENFERMEDAD_COMUN, ACCIDENTE_NO_LABORAL, ACCIDENT_LABORAL, ENFERMEDAD_PROFESIONAL, PERIODOS_OBSERVACION
	}


	// CONTRACTS
	public enum ContractType {
		FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL, RESTO_Y_AUTONOMOS
	}


	// PART TYPE
	public enum PartType {
		ALTA, CONFIRMACION, BAJA
	}


	public enum SituationEmployee {
		ACTIVO, PERCEPTOR_DE_DESEMPLEO
	}


	public SistemaRED() {
	}

	public static Collection<Employee> getTotalEmployees(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String regimen, String ccc)
			throws SegSocialException {
		return SistemaREDEmployee.getTotalEmployees(certificateInputStream, certificatePassword, certificateType,
				regimen, ccc);
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

	public static byte[] getCccLaboralLife(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String regime, String ccc, Date from, Date to) throws SegSocialException {
		try {
			return SistemaREDEmployee.getCccLaboralLife(certificateInputStream, certificatePassword, certificateType,
					regime, ccc, from, to);
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

	public static Employee getEmployee(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDEmployee.getEmployee(certificateInputStream, certificatePassword, certificateType,
					regimen, ccc, nss);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Employee getEmployee(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {

		return SistemaREDEmployee.getEmployee(certificateInputStream, certificatePassword, certificateType, regimen,
				ccc, nss);
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
				ccc, SistemaRED.Regime.fromValue(regimen), startDate, endDate, SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS);
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
		return SistemaREDI.getContributionInformation(certificateInputStream, certificatePassword, certificateType,
				nss, regimen, ccc, date);
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
		return SistemaREDI.getIDCDates(certificateInputStream, certificatePassword, certificateType,
				nss, regimen, ccc);
	}

	public static Collection<Idc> getIDCDates(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDI.getIDCDates(certificateInputStream, certificatePassword, certificateType,
					nss, regimen, ccc);
		} catch (IOException e) {
			throw new SegSocialException(e);
		} 
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

	public static byte[] getIDCNSSfinal ( InputStream certificateInputStream, final String certificatePassword,
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

	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> getCalcByCCC(final byte[] certificateData,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin) throws SegSocialException{
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return getCalcByCCC(certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
		}catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> getCalcByCCC(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin) throws SegSocialException{

		return Calculations.workersCalculationQueryByCCC(certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
		
	}
	
	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> getCalcByNAF(final byte[] certificateData,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin, String... nafs) throws SegSocialException{
		try ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return Calculations.workersCalculationByCCCandNAFS(certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin, nafs);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Map<String, Map<String,Map<Period, Map<String, Calc>>>> getCalcByNAF(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin, String... nafs) throws SegSocialException{
		return Calculations.workersCalculationByCCCandNAFS(certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin, nafs);
	}

	public static Map<String, Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCC(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
			final String ccc, final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo,
			final SistemaRED.LiquidationType liqType, final SistemaRED.LiquidationOrigin liqOrigin) throws SegSocialException {
		return SistemaREDI.workersCalculationQueryByCCC(certificateInputStream, certificatePassword, certificateType,
				ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
	}

	public static Map<String, Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCC(final byte[] certificateData,
			final String certificatePassword, final String certificateType, final String ccc, final SistemaRED.Regime regime,
			final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType, final SistemaRED.LiquidationOrigin liqOrigin)
			throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDI.workersCalculationQueryByCCC(certificateInputStream, certificatePassword,
					certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static Map<String,Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCCandNAFs(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin, String... nafs) throws SegSocialException{
		return SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin, nafs);
	}
	
	public static Map<String,Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCCandNAFs(final byte[] certificateData,
			final String certificatePassword, final String certificateType, final String ccc,
			final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, final SistemaRED.LiquidationType liqType,
			final SistemaRED.LiquidationOrigin liqOrigin, String... nafs) throws SegSocialException{
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin, nafs);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static Employee sendMov(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException {

		return SistemaREDMov.sendMov(certificateInputStream, certificatePassword, certificateType, employee);
	}

	public static void movPrevDelete(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String situation, String regimen, String ctaCti, String nss, Date fecha)
			throws SegSocialException {

		SistemaREDMov.movPrevDelete(certificateInputStream, certificatePassword, certificateType, situation, regimen,
				ctaCti, nss, fecha);
	}

	public static void altaConsolidadaDelete(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String situation, String regimen, String ctaCti, String nss)
			throws SegSocialException {

		SistemaREDMov.altaConsolidadaDelete(certificateInputStream, certificatePassword, certificateType, situation,
				regimen, ctaCti, nss);
	}

	public static Collection<Employee> ipfxnaf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, ArrayList<String> nssList)
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

		SistemaREDMov.cambioGrupCtz(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti,
				nss, grup_ctz, fecha);
	}

	public static void cambioOcupacion(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String ocup,
			Date fecha) throws SegSocialException {

		SistemaREDMov.cambioOcupacion(certificateInputStream, certificatePassword, certificateType, ipf, regimen,
				ctaCti, nss, ocup, fecha);
	}

	public static void cambioCatProf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String regimen, String ctaCti, String nss, String cat, Date fecha)
			throws SegSocialException {

		SistemaREDMov.cambioCatProf(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti,
				nss, cat, fecha);
	}

	public static boolean recordCertificate(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final String docType, final String docNum, final String applicantType,
			final String reason, final Date dateFrom, final Date dateTo, final float baseCC, final float baseCP,
			final int days) throws SegSocialException {
		return Paternity.grabarCertificado(certificateInputStream, certificatePassword, certificateType,
				affiliationNumber, regime, contributionAccount, docType, docNum, applicantType, reason, dateFrom,
				dateTo, baseCC, baseCP, days);
	}

	public static boolean recordCertificate(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final String docType, final String docNum, final String applicantType,
			final String reason, final Date dateFrom, final Date dateTo, final float baseCC, final float baseCP,
			final int days) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return Paternity.grabarCertificado(certificateInputStream, certificatePassword, certificateType,
					affiliationNumber, regime, contributionAccount, docType, docNum, applicantType, reason, dateFrom,
					dateTo, baseCC, baseCP, days);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static void voidPaternity(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final Date dateFrom, final Date dateTo, final Optional<Date> startDate)
			throws SegSocialException {
		Paternity.voidPaternity(certificateInputStream, certificatePassword, certificateType, affiliationNumber, regime,
				contributionAccount, dateFrom, dateTo, startDate);
	}

	public static void voidPaternity(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String affiliationNumber, final String regime,
			final String contributionAccount, final Date dateFrom, final Date dateTo, final Optional<Date> startDate)
			throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			Paternity.voidPaternity(certificateInputStream, certificatePassword, certificateType, affiliationNumber,
					regime, contributionAccount, dateFrom, dateTo, startDate);
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
		return SistemaREDSecondaryUser.getSecondaryUsers(certificateInputStream, certificatePassword, certificateType);
	}
	
	public static Collection<SecondaryUser> getSecondaryUsers(final byte[] certificateData,
			final String certificatePassword, final String certificateType) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)){
			return SistemaREDSecondaryUser.getSecondaryUsers(certificateInputStream, certificatePassword, certificateType);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static void deleteSecondaryUser(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ipfType, final String ipf) throws SegSocialException {
		SistemaREDSecondaryUser.deleteSecondaryUser(certificateInputStream, certificatePassword, certificateType, ipfType, ipf);
	}
	
	public static void deleteSecondaryUser(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String ipfType, final String ipf) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)){
			SistemaREDSecondaryUser.deleteSecondaryUser(certificateInputStream, certificatePassword, certificateType, ipfType, ipf);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static void registerSecondaryUserByNie(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String typeIpf, final String nie,
			String naf) throws SegSocialException {
		SistemaREDSecondaryUser.registerSecondaryUserByNie(certificateInputStream, certificatePassword, certificateType, typeIpf, nie, naf);
	}
	
	public static void registerSecondaryUserByNie(final byte[] certificateData,
			final String certificatePassword, final String certificateType, final String typeIpf, final String nie,
			String naf) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)){
			SistemaREDSecondaryUser.registerSecondaryUserByNie(certificateInputStream, certificatePassword, certificateType, typeIpf, nie, naf);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------
	//												IT PARTS
	// --------------------------------------------------------------------------------------------------------------

	public static void registerITBaja(final byte[] certificateData, final String certificatePassword, final String certificateType,
			final String regime, final String ccc, final String naf, final SistemaRED.Contingencies contingency, final SistemaRED.SituationEmployee situation_employee, 
			final Optional<String> licenseNumber, final Optional<String> cias, final Optional<String> occupation, final Date startdate,
			final SistemaRED.ContractType contractType, final float baseCot , final int cotDays, final Optional<Date> fATEP, final Optional<SistemaRED.AccidentType> accidentType) throws SegSocialException {
		
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITParts.registerItBaja(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency, situation_employee, licenseNumber, cias, occupation, startdate, contractType, baseCot, cotDays, fATEP, accidentType);
		} catch (IOException | SegSocialException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static void registerITConfirmation(final byte[] certificateData, final String certificatePassword, final String certificateType,
			final String regime, final String ccc, final String naf, final SistemaRED.Contingencies contingency, final SistemaRED.SituationEmployee situation_employee, 
			final Optional<String> licenseNumber, final Optional<String> cias, final Date fbaja, final Date fconfirmation, final Optional<String> npartConfimation) throws SegSocialException {
		
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITParts.registerItConfirmation(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency, situation_employee, licenseNumber, cias, fbaja, fconfirmation, npartConfimation);
		} catch (IOException | SegSocialException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static void registerITAlta(final byte[] certificateData, final String certificatePassword, final String certificateType, 
			final String regime, final String ccc, final String naf, final SistemaRED.Contingencies contingency, final SistemaRED.SituationEmployee situation_employee, 
			final Optional<String> licenseNumber, final Optional<String> cias, final Date fbaja, final Date falta, final Optional<Date> fATEP, 
			final Optional<SistemaRED.AccidentType> accidentType, final SistemaRED.CauseType causeType) throws SegSocialException {
		
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITParts.registerItAlta(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency, situation_employee, licenseNumber, cias, fbaja, falta, fATEP, accidentType, causeType);
		} catch (IOException | SegSocialException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static void removeIT(final byte[] certificateData, final String certificatePassword, final String certificateType,
			final String regime, final String ccc, final String naf, final SistemaRED.PartType partType, final Date dateBj, final Date dateProcess) throws SegSocialException {
		
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITParts.removeIt(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, partType, dateBj, dateProcess);
		} catch (IOException | SegSocialException | InterruptedException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static byte[] pdfIT(final byte[] certificateData, final String certificatePassword, final String certificateType,
			final String regime, final String ccc, final String naf, final SistemaRED.PartType partType, final Date dateBj, final Date dateProcess) throws SegSocialException {
		
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDITParts.pdfIt(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, partType, dateBj, dateProcess);
		} catch (IOException | SegSocialException | InterruptedException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static byte[] getReportAffiliateInAlta(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaRED.getReportAffiliateInAlta(certificateInputStream, certificatePassword,
					certificateType, regimen, ccc);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static byte[] getReportAffiliateInAlta(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		return SistemaREDMov.getReportAffiliateInAlta(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
	}
	
	public static byte[] getReportAffiliateInMovPrev(final byte certificateData[], final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaRED.getReportAffiliateInMovPrev(certificateInputStream, certificatePassword,
					certificateType, regimen, ccc);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static byte[] getReportAffiliateInMovPrev(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException {
		return SistemaREDMov.getReportAffiliateInMovPrev(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
	}
	
	public static Employee sendAlta(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException {
		return SistemaREDMov.sendAlta(certificateInputStream, certificatePassword, certificateType, employee);
	}
	
	public static Employee sendAlta(final byte[] certificateData, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException {
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDMov.sendAlta(certificateInputStream, certificatePassword, certificateType, employee);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static Employee sendBaja(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException{
				return SistemaREDMov.sendBaja(certificateInputStream, certificatePassword, certificateType, employee);
	}
	
	public static Employee sendBaja(final byte[] certificateData, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException{
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return SistemaREDMov.sendBaja(certificateInputStream, certificatePassword, certificateType, employee);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

	}

}
