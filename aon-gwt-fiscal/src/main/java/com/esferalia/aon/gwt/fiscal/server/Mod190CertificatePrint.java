package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.RetentionCertificate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@WebServlet(name = "Mod190 Certificate Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model190CertificatePrint" })
public class Mod190CertificatePrint extends HttpServlet {
	
	private static final long serialVersionUID = -534949591948520519L;
	
	public final String REPORT_TEMPLATE_EMPLOYEE 		= "/com/code/aon/ui/fiscal/report/mod190_retentionCertificate_page1.jasper";
	public final String REPORT_TEMPLATE_PROFESSIONAL 	= "/com/code/aon/ui/fiscal/report/mod190_retentionCertificate_page2.jasper";

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod190"));
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			Mod190 mod190 = FISCAL.getMod190(domainName, domainId, user, id);

			// Trabajadores
			Map<String, RetentionCertificate> employeeCertificates = new HashMap<>();
			mod190.getDetails().forEach(detail -> {createEmployeeCertificate(mod190, detail, employeeCertificates);});
			byte[] employeeData = createReport(JRReport.class.getResourceAsStream(REPORT_TEMPLATE_EMPLOYEE), employeeCertificates.values());
			
			// Profesionales
			Map<String, RetentionCertificate> professionalCertificates = new HashMap<>();
			mod190.getDetails().forEach(detail -> {createProfessionalCertificate(mod190, detail, professionalCertificates);});
			byte[] professionalData = createReport(JRReport.class.getResourceAsStream(REPORT_TEMPLATE_PROFESSIONAL), professionalCertificates.values());
			
			resp.setContentType(MimeType.PDF.getName());
			
			String s = mod190.getName();
		    StringBuilder sb = new StringBuilder();
		    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
		        sb.append("_");
		    }
		    for (char c : s.toCharArray()) {
		        if(Character.isJavaIdentifierPart(c)) {
		            sb.append(c);
		        }
		    }		
			
		    String fileName = "CertificadoRetenciones_" 
					+ "_" + mod190.getYear() 
					+ "_" + sb.toString();
			
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
			AonIOUtils.copy(new ByteArrayInputStream(mergePdf(employeeData, professionalData)), resp.getOutputStream());
			
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}
	
	public byte[] createReport(InputStream inputStream, Collection<RetentionCertificate> values) throws JRException {
		byte[] data = null; 
		if(values!=null && values.size()>0){
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					inputStream,
					new HashMap<String, Object>(),
					new JRBeanCollectionDataSource(values));
			data = JasperExportManager.exportReportToPdf(jasperPrint);
		}
		return data;
	}

	public void createEmployeeCertificate(Mod190 mod190, Mod190Detail detail, Map<String, RetentionCertificate> map){
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
				cert.setApplicableReduction(cert.getApplicableReduction() + detail.getApplicableReduction());
				cert.setDeducibleExpense(cert.getDeducibleExpense() + detail.getDeducibleExpense());
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
					cert.setJourneyDiet(cert.getJourneyDiet() + detail.getPerception());
				} else if(detail.getSubKey().equals("05") || detail.getSubKey().equals("20")){
					cert.setIncomeExemption(cert.getIncomeExemption() + detail.getPerception());
				}
			}
			
			map.put(cert.getEmployeeDocument(), cert);
		}
	}
	

	private void createProfessionalCertificate(Mod190 mod190, Mod190Detail detail, Map<String, RetentionCertificate> map){
		if(detail.getKey().equals("G") || detail.getKey().equals("H") || detail.getKey().equals("E")){
			RetentionCertificate cert = null;
			if(map.containsKey(detail.getDocument())){
				cert = map.get(detail.getDocument());
			} else {
				cert = new RetentionCertificate();
			}
			
			if(detail.getKey().equals("G")){
				cert = completeCertificate(mod190, detail, cert, "G");
			} else if(detail.getKey().equals("E")){
				cert = completeCertificate(mod190, detail, cert, "E");
			} else if(detail.getKey().equals("H")){
				if(detail.getSubKey().equals("01")){
					cert = completeCertificate(mod190, detail, cert, "H" );
				} else {
					cert = completeCertificate(mod190, detail, cert, null );
					if( detail.getSubKey().equals("02")){
						cert.setProf1(completeCertificate(mod190, detail, new RetentionCertificate(), "H"));
					} else if(detail.getSubKey().equals("03")){
						cert.setProf2(completeCertificate(mod190, detail, new RetentionCertificate(), "H"));
					} else if(detail.getSubKey().equals("04")){
						cert.setProf3(completeCertificate(mod190, detail, new RetentionCertificate(), "H"));
					}
				}
			}
			
			map.put(cert.getEmployeeDocument(), cert);
		}
	}
		
	private RetentionCertificate completeCertificate(Mod190 mod190, Mod190Detail detail, RetentionCertificate cert, String key){
		cert.setId(null);
		cert.setDomain(mod190.getDomain());
		cert.setName(mod190.getName());
		cert.setYear(detail.getAccrualYear()>0?detail.getAccrualYear():mod190.getYear());
		cert.setEnterpriseName(mod190.getName());
		cert.setEnterpriseDocument(mod190.getDocument());
		cert.setEmployeeName(detail.getName());
		cert.setEmployeeDocument(detail.getDocument());
		
		if(AonStringUtils.isNotBlank(key) && detail.getKey().equals(key)){
			cert.setPerception(cert.getPerception() + detail.getPerception());
			cert.setRetention(cert.getRetention() + detail.getRetention());
			cert.setInKindPerception(cert.getInKindPerception() + detail.getInKindPerception());
			cert.setInKindDeposit(cert.getInKindDeposit() + detail.getInKindDeposit());
			cert.setInKindOutputDeposit(cert.getInKindOutputDeposit() + detail.getInKindOutputDeposit());
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
	

}

