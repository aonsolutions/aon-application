package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
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

import com.esferalia.aon.gwt.common.sql.SQLUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.RetentionCertificate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod10T Print", urlPatterns = { "/aon_gwt_fiscal/Model10TPrint" })
public class Mod10TPrint extends HttpServlet {
	
	public final String REPORT_TEMPLATE = "/com/code/aon/ui/fiscal/report/retentionCertificate_10T.jasper";

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

			Map<String, RetentionCertificate> certificates = new HashMap<>();
			mod190.getDetails().forEach(detail -> {createCertificate(mod190, detail, certificates);});
			
//			JasperPrint jasperPrint = JasperFillManager.fillReport(
//					new BufferedInputStream(new FileInputStream("/home/COMMON-RESOURCES/aon-report/10T/retentionCertificate_10T.jasper")),
//					new HashMap<String, Object>(),
//					new JRBeanCollectionDataSource(certificates.values()));
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					JRReport.class.getResourceAsStream(REPORT_TEMPLATE),
					new HashMap<String, Object>(),
					new JRBeanCollectionDataSource(certificates.values()));
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			
			resp.setContentType(MimeType.PDF.getName());
			String fileName = "certificado_retenciones";
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
			AonIOUtils.copy(new ByteArrayInputStream(data), resp.getOutputStream());
			
			resp.flushBuffer();
		} catch (Throwable e) {
			rollback(conn);
			throw new ServletException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}
	
	private void createCertificate(Mod190 mod190, Mod190Detail detail, Map<String, RetentionCertificate> map){
		RetentionCertificate cert = null;
		if(map.containsKey(detail.getDocument())){
			cert = map.get(detail.getDocument());
		} else {
			cert = new RetentionCertificate();
		}
		
		cert.setId(null);
		cert.setDomain(mod190.getDomain());
		cert.setName(mod190.getName());
		cert.setYear(mod190.getYear());
		cert.setEnterpriseName(mod190.getName());
		cert.setEnterpriseDocument(mod190.getDocument());
		cert.setEmployeeName(detail.getName());
		cert.setEmployeeDocument(detail.getDocument());
		
		if(detail.getKey().equals("A")){
			cert.setPerception(detail.getPerception());
			cert.setRetention(detail.getRetention());
			cert.setInKindPerception(detail.getInKindPerception());
			cert.setInKindDeposit(detail.getInKindDeposit());
			cert.setInKindOutputDeposit(detail.getInKindOutputDeposit());
		}
		
		// TODO: ¿se pide en el 190?
		cert.setForecastPlanContributions(0);
		
		// TODO: ¿se pide en el 190?
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
	
	
	public static void main(String[] args) {
		int id = 84;
		int domainId = 1721;
		String domainName = "teresanovell.esferalia.net";
		
		Mod190 mod190 = AON.getMod190(domainName, domainId, id);
		
		Mod10TPrint print = new Mod10TPrint();
		Map<String, RetentionCertificate> certificates = new HashMap<>();
		mod190.getDetails().forEach(detail -> {print.createCertificate(mod190, detail, certificates);});
		
		try {
			JasperPrint jasperPrint = JasperFillManager.fillReport(
					new BufferedInputStream(new FileInputStream("/home/COMMON-RESOURCES/aon-report/10T/retentionCertificate_10T.jasper")),
					new HashMap<String, Object>(),
					new JRBeanCollectionDataSource(certificates.values()));
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			FileUtils.writeByteArrayToFile(new File(
					"C:\\tmp\\certificate10T.pdf"), data);
		} catch (JRException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}

