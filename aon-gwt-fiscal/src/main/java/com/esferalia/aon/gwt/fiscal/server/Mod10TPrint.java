package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.gwt.common.sql.SQLUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.RetentionCertificate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

@SuppressWarnings("serial")
@WebServlet(name = "Mod10T Print", urlPatterns = { "/aon_gwt_fiscal/Model10TPrint" })
public class Mod10TPrint extends HttpServlet {
	
	public final String REPORT_TEMPLATE_EMPLOYEE 		= "/com/code/aon/ui/fiscal/report/retentionCertificate_10T.jasper";
	public final String REPORT_TEMPLATE_PROFESSIONAL 	= "/com/code/aon/ui/fiscal/report/retentionCertificate_10T_prof.jasper";

	public final String TESTING_TEMPLATE_EMPLOYEE 		= "/home/COMMON-RESOURCES/aon-report/10T/retentionCertificate_10T.jasper";
	public final String TESTING_TEMPLATE_PROFESSIONAL 	= "/home/COMMON-RESOURCES/aon-report/10T/retentionCertificate_10T_prof.jasper";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			int id = Integer.parseInt(req.getParameter("mod190"));
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String domainName = req.getParameter("domainName");
			Mod190 mod190 = AON.getMod190(domainName, domainId, id);

			commit(conn);

			JasperPrint jasperPrint = null;
			// Trabajadores
			Map<String, RetentionCertificate> employeeCertificates = new HashMap<>();
			mod190.getDetails().forEach(detail -> {createEmployeeCertificate(mod190, detail, employeeCertificates);});
			byte[] employeeData = null;
			if(employeeCertificates.size()>0){
//				jasperPrint = JasperFillManager.fillReport(
//						new BufferedInputStream(new FileInputStream(TESTING_TEMPLATE_EMPLOYEE)),
//						new HashMap<String, Object>(),
//						new JRBeanCollectionDataSource(professionalCertificates.values()));
				jasperPrint = JasperFillManager.fillReport(
						JRReport.class.getResourceAsStream(REPORT_TEMPLATE_EMPLOYEE),
						new HashMap<String, Object>(),
						new JRBeanCollectionDataSource(employeeCertificates.values()));
				employeeData = JasperExportManager.exportReportToPdf(jasperPrint);
			}
			
			// Profesionales
			Map<String, RetentionCertificate> professionalCertificates = new HashMap<>();
			mod190.getDetails().forEach(detail -> {createProfessionalCertificate(mod190, detail, professionalCertificates);});
			byte[] professionalData = null;
			if(professionalCertificates.size()>0){
//				jasperPrint = JasperFillManager.fillReport(
//						new BufferedInputStream(new FileInputStream(TESTING_TEMPLATE_PROFESSIONAL)),
//						new HashMap<String, Object>(),
//						new JRBeanCollectionDataSource(professionalCertificates.values()));
				jasperPrint = JasperFillManager.fillReport(
						JRReport.class.getResourceAsStream(REPORT_TEMPLATE_PROFESSIONAL),
						new HashMap<String, Object>(),
						new JRBeanCollectionDataSource(professionalCertificates.values()));
				professionalData = JasperExportManager.exportReportToPdf(jasperPrint);
			}
			
			
			resp.setContentType(MimeType.PDF.getName());
			String fileName = "certificado_retenciones";
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
			AonIOUtils.copy(new ByteArrayInputStream(mergePdf(employeeData, professionalData)), resp.getOutputStream());
			
			resp.flushBuffer();
		} catch (Throwable e) {
			rollback(conn);
			throw new ServletException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}
	
	private void createEmployeeCertificate(Mod190 mod190, Mod190Detail detail, Map<String, RetentionCertificate> map){
		if(detail.getKey().equals("A") || detail.getKey().equals("L")){
			RetentionCertificate cert = null;
			if(map.containsKey(detail.getDocument())){
				cert = map.get(detail.getDocument());
			} else {
				cert = new RetentionCertificate();
			}
			
			cert = completeCertificate(mod190, detail, cert, "A");
			
			// TODO: ¿se debe pedir en el 190?
			cert.setForecastPlanContributions(0);
			
			// TODO: ¿se debe pedir en el 190?
			cert.setDependencyContributions(0);
			
			if(detail.getKey().equals("A")){
				cert.setApplicableReduction(detail.getIrpfResult().getApplicableReduction());
				cert.setDeducibleExpense(detail.getIrpfResult().getDeducibleExpense());
			}
			
			cert.setDelay1(obtainRetentionCertificate(mod190.getYear()-1));
			cert.setDelay2(obtainRetentionCertificate(mod190.getYear()-2));
			cert.setDelay3(obtainRetentionCertificate(mod190.getYear()-3));
			cert.setDelay4(obtainRetentionCertificate(mod190.getYear()-4));
			
			cert.setRefund1(obtainRefunds(mod190.getYear()-1));
			cert.setRefund2(obtainRefunds(mod190.getYear()-2));
			cert.setRefund3(obtainRefunds(mod190.getYear()-3));
			
			if(detail.getKey().equals("L")){
				if(detail.getSubKey().equals("01")){
					cert.setJourneyDiet(detail.getPerception());
				} else if(detail.getSubKey().equals("20")){
					cert.setIncomeExemption(detail.getPerception());
				}
			}
			
			map.put(cert.getEmployeeDocument(), cert);
		}
	}
	

	private void createProfessionalCertificate(Mod190 mod190, Mod190Detail detail, Map<String, RetentionCertificate> map){
		if(detail.getKey().equals("G") || detail.getKey().equals("H")){
			RetentionCertificate cert = null;
			if(map.containsKey(detail.getDocument())){
				cert = map.get(detail.getDocument());
			} else {
				cert = new RetentionCertificate();
			}
			
			if(detail.getKey().equals("G")){
				cert = completeCertificate(mod190, detail, cert, "G");
			} else if(detail.getKey().equals("H")){
				cert = completeCertificate(mod190, detail, cert, null);
				if(detail.getSubKey().equals("02")){
					cert.setProf1(completeCertificate(mod190, detail, new RetentionCertificate(), "H"));
				} else if(detail.getSubKey().equals("03")){
					cert.setProf2(completeCertificate(mod190, detail, new RetentionCertificate(), "H"));
				} else if(detail.getSubKey().equals("04")){
					cert.setProf3(completeCertificate(mod190, detail, new RetentionCertificate(), "H"));
				}
			}
			
			map.put(cert.getEmployeeDocument(), cert);
		}
	}
		
	private RetentionCertificate completeCertificate(Mod190 mod190, Mod190Detail detail, RetentionCertificate cert, String key){
		cert.setId(null);
		cert.setDomain(mod190.getDomain());
		cert.setName(mod190.getName());
		cert.setYear(mod190.getYear());
		cert.setEnterpriseName(mod190.getName());
		cert.setEnterpriseDocument(mod190.getDocument());
		cert.setEmployeeName(detail.getName());
		cert.setEmployeeDocument(detail.getDocument());
		
		if(StringUtils.isNotBlank(key) && detail.getKey().equals(key)){
			cert.setPerception(detail.getPerception());
			cert.setRetention(detail.getRetention());
			cert.setInKindPerception(detail.getInKindPerception());
			cert.setInKindDeposit(detail.getInKindDeposit());
			cert.setInKindOutputDeposit(detail.getInKindOutputDeposit());
		}
		
		return cert;
	}

	// Rendimientos satisfechos en el ejercicio correspondientes a ejercicios
	// anteriores (atrasos)
	private RetentionCertificate obtainRetentionCertificate(int year) {
		// TODO
		 
		return null;
	}
	
	// Cantidades reintegradas por el perceptor en el ejercicio por haber sido
	// indebida o excesivamente percibidas en ejercicios anteriores (reintegros)
	private RetentionCertificate obtainRefunds(int year) {
		// TODO
		
		return null;
	}
	
	// ***************************
	// ***************************
	// UTILS
	// ***************************
	// ***************************
	public byte[] mergePdf(byte[]... documents) throws IOException, DocumentException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		List<PdfReader> pdfReaderList = new ArrayList<PdfReader>();
		for(byte[] document: documents){
			if(document!=null){
				pdfReaderList.add(new PdfReader(document));
			}
		}

		PdfCopyFields copy = new PdfCopyFields(outputStream);
		copy.open();

		if (null != pdfReaderList && !pdfReaderList.isEmpty()) {
			Iterator<PdfReader> iter = pdfReaderList.iterator();
			while (iter.hasNext()) {
				String pageNOs = "";
				PdfReader pdfReader = (PdfReader) iter.next();
				int noOfPages = pdfReader.getNumberOfPages();
				if (noOfPages > 0) {
					pageNOs = getNumderOfPages(noOfPages);
				}
				copy.addDocument(pdfReader, pageNOs);
			}
		}
		copy.close();
		return outputStream.toByteArray();
	}
	
	/**
	 * Function to get page numbers in string with comma separated
	 * 
	 * @param noOfPages
	 * @return
	 */
	private static String getNumderOfPages(int noOfPages) {
		String pageNOs = "";
		boolean flag = false;
		for (int i = 0; i < noOfPages; i++) {

			if (flag == true) {
				Integer c = (Integer) i;
				pageNOs = pageNOs.concat("," + c.toString());
			}
			if (flag == false) {
				Integer c = (Integer) i;
				pageNOs = c.toString();
				flag = true;
			}
		}
		return pageNOs;
	}
	
	
	
	public static void main(String[] args) {
		
		int id = 84;
		int domainId = 1721;
		String domainName = "teresanovell.esferalia.net";
		
		Mod190 mod190 = AON.getMod190(domainName, domainId, id);
		
		Mod10TPrint print = new Mod10TPrint();
		Map<String, RetentionCertificate> employeeCertificates = new HashMap<>();
		mod190.getDetails().forEach(detail -> {print.createEmployeeCertificate(mod190, detail, employeeCertificates);});
		
		try {
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					new BufferedInputStream(new FileInputStream(print.TESTING_TEMPLATE_EMPLOYEE)),
					new HashMap<String, Object>(),
					new JRBeanCollectionDataSource(employeeCertificates.values()));
			byte[] employeeData = JasperExportManager.exportReportToPdf(jasperPrint);
			FileUtils.writeByteArrayToFile(new File(
					"C:\\tmp\\certificate10T.pdf"), employeeData);
			
			jasperPrint = JasperFillManager.fillReport(
					new BufferedInputStream(new FileInputStream(print.TESTING_TEMPLATE_PROFESSIONAL)),
					new HashMap<String, Object>(),
					new JRBeanCollectionDataSource(employeeCertificates.values()));
			byte[] professionalData = JasperExportManager.exportReportToPdf(jasperPrint);
			FileUtils.writeByteArrayToFile(new File(
					"C:\\tmp\\certificate10T_prof.pdf"), professionalData);
			
		} catch (JRException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}

