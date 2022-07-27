package solutions.aon.seg.social;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.NotExistingYetException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.SituationType;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;

public class TestSistemaREDMov {

	private final String CERTIFICATE_PASSWORD = "1234";
	private final String CERTIFICATE_TYPE = "pkcs12"; 
	private final String CERTIFICATE_PATH =  System.getProperty("user.home")+"/CERT.pfx"; 
	
	@Test
	@Ignore
	public void testSendAlta() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {	
			Calendar c=Calendar.getInstance();
			c.add(Calendar.DATE, 8);
			Date fecha=c.getTime();
			EmployeeBuilder builder = new EmployeeBuilder();
			Employee employee = builder
			.setRegime("0111")
			.setCtaCti("01105360062")
			.setNss("010022757387")
			.setIpf("16262835H")
			.setFra(fecha)
			.setOcup("a")
			.setColec("60888888888888")
			.setGc("04")
			.setContract("502")
//			.setCollective("968")
			.build();
			SistemaREDMov.sendAlta(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, employee);
		} catch (NotExistingYetException e) {} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}

	@Test
	@Ignore
	public void testRemoveMov() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {	
			Calendar c=Calendar.getInstance();
			c.add(Calendar.DATE, 8);
			Date fecha=c.getTime();
		    SistemaREDMov.movPrevDelete(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE,
		    		SituationType.ALTA, "0111", "01105360062", "010022757387", fecha);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Test
	@Ignore
	public void testRemoveMovConsolidated() {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			Calendar c=Calendar.getInstance();
			c.add(Calendar.DATE, 8);
			Date fecha=c.getTime();
		    SistemaREDMov.removeMovConsolidated(certificateInputStream, "jg@FNMT", "pkcs12",
		    		SituationType.BAJA,  "0111", "01105360062", "010022757387", "16262835H", fecha);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Test
	@Ignore
	public void ipfxnaf() {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
		    ArrayList<String> nssList = new ArrayList<>();
		    nssList.add("010022757387");	
		    Collection<Employee> employee = SistemaREDMov.ipfxnaf(certificateInputStream, "jg@FNMT", "pkcs12", nssList);
		    System.out.println(employee.stream().findFirst());
		} catch (NotExistingYetException e) {} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}
	
	@Test
	@Ignore
	public void nafxipf() {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
		   Employee employee = SistemaREDMov.nafxipf(certificateInputStream, "jg@FNMT", "pkcs12", "16262835H", "garcia", "perez");
		   System.out.println(employee.getNss());
		} catch (NotExistingYetException e) {} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}

	@Test
	@Ignore
	public void testValidateCert() {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("test_error.p12")){
		    SistemaREDMov.validateCert(certificateInputStream, "ZH2021Aon", "pkcs12");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Test
	@Ignore
	public void testCambioGrupCtz() {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
		    SistemaREDMov.updateQuoteGroup(certificateInputStream, "jg@FNMT", "pkcs12", "16262835H", "0111", "01105360062", "010022757387", "03", new Date());
//		    "011101105360062"
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Test
	@Ignore
	public void testCambioOcupacion() {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
		    SistemaREDMov.updateOccupation(certificateInputStream, "jg@FNMT", "pkcs12", "16262835H", "0111", "01105360062", "010022757387", "G", new Date());
//		    "011101105360062"
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	@Test
	@Ignore
	public void testDeleteAltaConsolidada() {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
		    SistemaREDMov.removeMovConsolidated(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, SituationType.ALTA, 
		    		"0111", "01105360062", "010022757387", "16262835H", new Date());
//		    "011101105360062"
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Test
	@Ignore
	public void testCambioContratoCoef() {
		try(InputStream certificateInputStream = TestSistemaREDMov.class.getResourceAsStream("FNMT.p12")){
			Calendar c=Calendar.getInstance();
			Date fecha=c.getTime();
			SistemaREDMov.updateContractCoef(certificateInputStream,"jg@FNMT", "pkcs12", 
					"16262835H", //IPF
					"0111", //REGIMEN
					"01105360062", //CCC
					"010022757387",//NSS 
					fecha,// FECHA DE CAMBIOTestSistemaREDMov
					Optional.of("502"), // CODIGO DEL CONTRATO (OPCIONAL) Optional.empty
					"725" // COEFICIENTE 3 digits o null
					
			);
		} catch (NotExistingYetException e) {} catch (IOException e) {
			fail("Wrong certificate on test");
		} catch (SegSocialException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		}
	}

}
