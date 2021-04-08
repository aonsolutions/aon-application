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

import solutions.aon.seg.social.SistemaREDI.LiquidationOrigin;
import solutions.aon.seg.social.SistemaREDI.LiquidationType;
import solutions.aon.seg.social.SistemaREDI.Regime;
import solutions.aon.seg.social.SistemaREDITParts.AccidentType;
import solutions.aon.seg.social.SistemaREDITParts.CauseType;
import solutions.aon.seg.social.SistemaREDITParts.Contingencies;
import solutions.aon.seg.social.SistemaREDITParts.ContractType;
import solutions.aon.seg.social.SistemaREDITParts.PartType;
import solutions.aon.seg.social.SistemaREDITParts.SituationEmployee;
import solutions.aon.seg.social.exception.ForbiddenException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.WorkerLiquidation;

public class SistemaRED {

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
				ccc, Regime.fromValue(regimen), startDate, endDate, LiquidationType.TODAS, LiquidationOrigin.TODAS);
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

	public static Map<String, Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCC(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
			final String ccc, final Regime regime, final Date dateFrom, final Date dateTo,
			final LiquidationType liqType, final LiquidationOrigin liqOrigin) throws SegSocialException {
		return SistemaREDI.workersCalculationQueryByCCC(certificateInputStream, certificatePassword, certificateType,
				ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
	}

	public static Map<String, Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCC(final byte[] certificateData,
			final String certificatePassword, final String certificateType, final String ccc, final Regime regime,
			final Date dateFrom, final Date dateTo, final LiquidationType liqType, final LiquidationOrigin liqOrigin)
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
			final Regime regime, final Date dateFrom, final Date dateTo, final LiquidationType liqType,
			final LiquidationOrigin liqOrigin, String... nafs) throws SegSocialException{
		return SistemaREDI.workersCalculationQueryByCCCandNAFS(certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin, nafs);
	}
	
	public static Map<String,Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCCandNAFs(final byte[] certificateData,
			final String certificatePassword, final String certificateType, final String ccc,
			final Regime regime, final Date dateFrom, final Date dateTo, final LiquidationType liqType,
			final LiquidationOrigin liqOrigin, String... nafs) throws SegSocialException{
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
	
	// --------------------------------------------------------------------------------------------------------------
	//												IT PARTS
	// --------------------------------------------------------------------------------------------------------------

	public static void registerITBaja(final byte[] certificateData, final String certificatePassword, final String certificateType,
			final String regime, final String ccc, final String naf, final Contingencies contingency, final SituationEmployee situation_employee, 
			final Optional<String> licenseNumber, final Optional<String> cias, final Optional<String> occupation, final Date startdate,
			final ContractType contractType, final float baseCot , final int cotDays, final Optional<Date> fATEP, final Optional<AccidentType> accidentType) throws SegSocialException {
		
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITParts.registerItBaja(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency, situation_employee, licenseNumber, cias, occupation, startdate, contractType, baseCot, cotDays, fATEP, accidentType);
		} catch (IOException | SegSocialException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static void registerITConfirmation(final byte[] certificateData, final String certificatePassword, final String certificateType,
			final String regime, final String ccc, final String naf, final Contingencies contingency, final SituationEmployee situation_employee, 
			final Optional<String> licenseNumber, final Optional<String> cias, final Date fbaja, final Date fconfirmation, final Optional<String> npartConfimation) throws SegSocialException {
		
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITParts.registerItConfirmation(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency, situation_employee, licenseNumber, cias, fbaja, fconfirmation, npartConfimation);
		} catch (IOException | SegSocialException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static void registerITAlta(final byte[] certificateData, final String certificatePassword, final String certificateType, 
			final String regime, final String ccc, final String naf, final Contingencies contingency, final SituationEmployee situation_employee, 
			final Optional<String> licenseNumber, final Optional<String> cias, final Date fbaja, final Date falta, final Optional<Date> fATEP, 
			final Optional<AccidentType> accidentType, final CauseType causeType) throws SegSocialException {
		
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITParts.registerItAlta(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, contingency, situation_employee, licenseNumber, cias, fbaja, falta, fATEP, accidentType, causeType);
		} catch (IOException | SegSocialException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static void removeIT(final byte[] certificateData, final String certificatePassword, final String certificateType,
			final String regime, final String ccc, final String naf, final PartType partType, final Date dateBj, final Date dateProcess) throws SegSocialException {
		
		try (InputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			SistemaREDITParts.removeIt(certificateInputStream, certificatePassword, certificateType, regime, ccc, naf, partType, dateBj, dateProcess);
		} catch (IOException | SegSocialException | InterruptedException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static byte[] pdfIT(final byte[] certificateData, final String certificatePassword, final String certificateType,
			final String regime, final String ccc, final String naf, final PartType partType, final Date dateBj, final Date dateProcess) throws SegSocialException {
		
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

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

	}

}
