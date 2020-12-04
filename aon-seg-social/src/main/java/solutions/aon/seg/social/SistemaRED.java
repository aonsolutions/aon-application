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

import solutions.aon.seg.social.SistemaRED_I.LiquidationOrigin;
import solutions.aon.seg.social.SistemaRED_I.LiquidationType;
import solutions.aon.seg.social.SistemaRED_I.Regime;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.objects.Employee;
import solutions.aon.seg.social.objects.WorkerLiquidation;

public class SistemaRED {

	public SistemaRED() {}

	public static Collection<Employee> getTotalEmployees(final InputStream certificateInputStream, final String certificatePassword,
													  final String certificateType, String regimen, String ccc) throws SegSocialException {
		return SistemaRED_Employee.getTotalEmployees(certificateInputStream,certificatePassword,certificateType,regimen,ccc);
	}

	public static Collection<Employee> getEmployees(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException 
	{
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {
			return SistemaRED_Employee.getEmployees(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
		} catch ( IOException e ) {throw new SegSocialException(e);}
	}
	
	public static Collection<Employee> getPrevEmployees(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException 
	{
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {
			return SistemaRED_Employee.getPrevEmployees(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
		} catch ( IOException e ) {throw new SegSocialException(e);}
	}

	public static Collection<Employee> getEmployees(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException 
	{
		return SistemaRED_Employee.getEmployees(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
	}

	public static Employee getEmployee(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return SistemaRED_Employee.getEmployee(certificateInputStream, certificatePassword, certificateType, regimen, ccc, nss);
		} catch ( IOException e ) {
			throw new SegSocialException(e);
		}
	}	

	public static Employee getEmployee(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
		
		return SistemaRED_Employee.getEmployee(certificateInputStream, certificatePassword, certificateType,regimen,  ccc, nss);
	}
	
	public static Map<String,Map<String, WorkerLiquidation>> getCosts(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc, Date startDate, Date endDate) throws SegSocialException {
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return getCosts(certificateInputStream, certificatePassword, certificateType, regimen, ccc, startDate, endDate);
		} catch ( IOException e ) {
			throw new SegSocialException(e);
		}
	}	

	public static Map<String,Map<String, WorkerLiquidation>>  getCosts(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, Date startDate, Date endDate) throws SegSocialException {	
		return SistemaRED_I.workersCalculationQueryByCCC(certificateInputStream, certificatePassword, certificateType, ccc, Regime.fromValue(regimen), startDate, endDate, LiquidationType.TODAS, LiquidationOrigin.TODAS);
	}
		
	
	public static byte[] getTA(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException {
		return SistemaRED_I.getTADuplicate(certificateInputStream, certificatePassword, certificateType, nss, regimen, ccc, date);
	}

	public static byte[] getTA(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException{
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return SistemaRED_I.getTADuplicate(certificateInputStream, certificatePassword, certificateType, nss, regimen, ccc, date);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getIDC(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException {
		return SistemaRED_I.getContributionInformation(certificateInputStream, certificatePassword, certificateType, nss, regimen, ccc, date);
	}
	
	public static byte[] getIDC(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException{
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return SistemaRED_I.getContributionInformation(certificateInputStream, certificatePassword, certificateType, nss, regimen, ccc, date);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static byte[] getIDCCCC(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, Date date) throws SegSocialException {
		return SistemaRED_I.getContributionInformationCCC(certificateInputStream, certificatePassword, certificateType, regimen, ccc, date);
	}
	
	public static byte[] getIDCCCC(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc, Date date) throws SegSocialException{
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return SistemaRED_I.getContributionInformationCCC(certificateInputStream, certificatePassword, certificateType, regimen, ccc, date);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getUp2DateSS(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc ) throws SegSocialException {
		return SistemaRED_I.getObligationAwarenessCertificate(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
	}

	public static byte[] getUp2DateSS(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc ) throws SegSocialException {
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return SistemaRED_I.getObligationAwarenessCertificate(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
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
	
	public static Map<String,Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCC(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String ccc, final Regime regime, final Date dateFrom, final Date dateTo, final LiquidationType liqType,
			final LiquidationOrigin liqOrigin) throws SegSocialException{
		return SistemaRED_I.workersCalculationQueryByCCC(certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
	}
	
	public static Map<String,Map<String, WorkerLiquidation>> getWorkersLiquidationsByCCC(final byte[] certificateData, final String certificatePassword,
			final String certificateType, final String ccc, final Regime regime, final Date dateFrom, final Date dateTo, final LiquidationType liqType,
			final LiquidationOrigin liqOrigin) throws SegSocialException{
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return SistemaRED_I.workersCalculationQueryByCCC(certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, liqType, liqOrigin);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}	
	}

	public static Employee sendMov(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException {
		
		return SistemaREDMov.sendMov(certificateInputStream, certificatePassword, certificateType, employee);
	}
	
	public static void movPrevDelete(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String situation, String regimen, String ctaCti, String nss, Date fecha) throws SegSocialException {
		
		SistemaREDMov.movPrevDelete(certificateInputStream, certificatePassword, certificateType, situation, regimen, ctaCti, nss, fecha);
	}
	
	public static void altaConsolidadaDelete(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String situation, String regimen, String ctaCti, String nss) throws SegSocialException {
		
		SistemaREDMov.altaConsolidadaDelete(certificateInputStream, certificatePassword, certificateType,  situation, regimen, ctaCti, nss);
	}
	
	public static Collection<Employee> ipfxnaf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, ArrayList<String> nssList) throws SegSocialException 
	{
		return SistemaREDMov.ipfxnaf(certificateInputStream, certificatePassword, certificateType, nssList);
	}
	
	public static Employee nafxipf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String apellido1, String apellido2) throws SegSocialException 
	{
		return SistemaREDMov.nafxipf(certificateInputStream, certificatePassword, certificateType, ipf, apellido1, apellido2);
	}
	
	public static void cambioGrupCtz(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String regimen, String ctaCti, String nss, String grup_ctz, Date fecha) throws SegSocialException {
		
		SistemaREDMov.cambioGrupCtz(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, grup_ctz, fecha);
	}
	
	public static void cambioOcupacion(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String regimen, String ctaCti, String nss, String ocup, Date fecha) throws SegSocialException {
		
		SistemaREDMov.cambioOcupacion(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, ocup, fecha);
	}
	
	public static void cambioCatProf(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String regimen, String ctaCti, String nss, String cat, Date fecha) throws SegSocialException {
		
		SistemaREDMov.cambioCatProf(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, cat, fecha);
	}
	
	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

	}

}
