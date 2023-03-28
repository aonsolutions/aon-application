package old.com.esferalia.aon.gwt.fiscal.test.accounting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.esferalia.aon.gwt.fiscal.server.fiscal.mod190.Mod190CertificatePrint;
import com.esferalia.aon.occam.api.model.fiscal.RetentionCertificate;

import net.sf.jasperreports.engine.JRException;


public class Model190CertificatePrintTestCase {
	
	static final String TESTING_TEMPLATE_EMPLOYEE 		= "/home/COMMON-RESOURCES/aon-report/retentionCertificate/mod190_retentionCertificate_page1.jasper";
	
	static final String TESTING_TEMPLATE_PROFESSIONAL 	= "/home/COMMON-RESOURCES/aon-report/retentionCertificate/mod190_retentionCertificate_page2.jasper";
	

	public static void main(String[] args) {
		
//		Mod190CertificatePrint print = new Mod190CertificatePrint();
//		
//		try {
//			Map<String, RetentionCertificate> employeeCertificates = new HashMap<String, RetentionCertificate>();
//			employeeCertificates.put("", getTestEmployeeCertificates());
//			byte[] employeeData = print.createReport(new FileInputStream(TESTING_TEMPLATE_EMPLOYEE), employeeCertificates.values());
//			FileUtils.writeByteArrayToFile(File.createTempFile("certificado_mod190",".pdf"), employeeData);
//			
//			Map<String, RetentionCertificate> professionalCertificates = new HashMap<String, RetentionCertificate>();
//			professionalCertificates.put("", getTestProfessionalCertificates());
//			byte[] professionalData = print.createReport(new FileInputStream(TESTING_TEMPLATE_PROFESSIONAL), professionalCertificates.values());
//			FileUtils.writeByteArrayToFile(File.createTempFile("certificado_mod190_prof",".pdf"), professionalData);
//			
//		} catch (JRException e) {
//			System.out.println(e.getMessage());
//		} catch (FileNotFoundException e) {
//			System.out.println(e.getMessage());
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//		}
	}

	private static RetentionCertificate getTestEmployeeCertificates() {
		RetentionCertificate cert = new RetentionCertificate();
		cert.setId(null);
		cert.setName(null);
		cert.setYear(2015);
		cert.setEnterpriseName(null);
		cert.setEnterpriseDocument(null);
		cert.setEmployeeName(null);
		cert.setEmployeeDocument(null);
		
		cert.setPerception(0.0);
		cert.setRetention(0.0);
		cert.setInKindPerception(0.0);
		cert.setInKindDeposit(0.0);
		cert.setInKindOutputDeposit(0.0);
		
		cert.setForecastPlanContributions(0);
		cert.setDependencyContributions(0);
			
		cert.setApplicableReduction(0.0);
		cert.setDeducibleExpense(0.0);
			
		cert.setDelay1(new RetentionCertificate());
		cert.setDelay2(new RetentionCertificate());
		cert.setDelay3(new RetentionCertificate());
		cert.setDelay4(new RetentionCertificate());
		
		cert.setRefund1(new RetentionCertificate());
		cert.setRefund2(new RetentionCertificate());
		cert.setRefund3(new RetentionCertificate());
			
		cert.setJourneyDiet(0.0);
		cert.setIncomeExemption(0.0);
		
		return cert;
	}

	private static RetentionCertificate getTestProfessionalCertificates() {
		RetentionCertificate cert = new RetentionCertificate();
		cert.setId(null);
		cert.setName(null);
		cert.setYear(2015);
		cert.setEnterpriseName(null);
		cert.setEnterpriseDocument(null);
		cert.setEmployeeName(null);
		cert.setEmployeeDocument(null);
		
		cert.setPerception(0.0);
		cert.setRetention(0.0);
		cert.setInKindPerception(0.0);
		cert.setInKindDeposit(0.0);
		cert.setInKindOutputDeposit(0.0);
		
		cert.setProf1(new RetentionCertificate());
		cert.setProf2(new RetentionCertificate());
		cert.setProf3(new RetentionCertificate());

		return cert;
	}

}
