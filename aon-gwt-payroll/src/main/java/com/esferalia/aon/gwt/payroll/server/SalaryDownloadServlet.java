package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jooq.Record1;
import org.jooq.Record2;

import com.code.aon.company.enumeration.SalaryTemplate;
import com.esferalia.aon.gwt.payroll.shared.PayrollPrintService;
import com.esferalia.aon.in.payroll.pdf.jooq.JooqPayrollBuilder;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(
		name = "Salary-Download", 
		urlPatterns = { 
				"/aon_gwt_aio/salary_download/*" ,
				"/aon_gwt_payroll/salary_download/*" 
		}
)
public class SalaryDownloadServlet extends HttpServlet {
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM_yyyy");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String domainName = req.getParameter("domainName");
		String user = req.getParameter("user");
		
		Integer enterpriseId = AonStringUtils.isBlank(req.getParameter("enterpriseId")) ? null : Integer.parseInt(req.getParameter("enterpriseId"));
		
		String salaryIdsStr = req.getParameter("salaryIds");
		List<Integer> salaryIds = new ArrayList<Integer>();
		if(AonStringUtils.isNotBlank(salaryIdsStr))
			salaryIds = Arrays.stream(salaryIdsStr.split(","))
	            .map(Integer::parseInt)
	            .collect(Collectors.toList());
		
		String fileName = "nominas";
		
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, user)) {
			Record1<String> enterpriseRegistryRecord = aonContext.getDslContext().select(REGISTRY.NAME).from(REGISTRY).where(REGISTRY.ID.eq(enterpriseId)).fetchOne();
			if(null != enterpriseRegistryRecord) fileName = "NOMINAS_" + enterpriseRegistryRecord.get(REGISTRY.NAME).replaceAll(",", "").replaceAll(" ", "_").trim();
		}
		
		Map<String, byte[]> pdfFiles = getPdfFiles(domainName, user, enterpriseId, salaryIds); 
		
		if(salaryIds.size() > 1) {
			resp.setContentType("application/zip");
			resp.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".zip");

	        try (ServletOutputStream outStream = resp.getOutputStream();
	             ZipOutputStream zipOut = new ZipOutputStream(outStream)) {
	            
	            for (Entry<String, byte[]> entry : pdfFiles.entrySet()) {
	                ZipEntry zipEntry = new ZipEntry(entry.getKey() + ".pdf");
	                zipOut.putNextEntry(zipEntry);
	                zipOut.write(entry.getValue());
	                zipOut.closeEntry();
	            }
	        }
	        
	        resp.flushBuffer();
		} else if(!pdfFiles.isEmpty()) {
			
			Entry<String, byte[]> firstEntry = pdfFiles.entrySet().iterator().next();
			
			resp.setContentType("application/pdf");
			resp.setHeader("Content-Disposition", "attachment; filename=" + firstEntry.getKey() + ".pdf");

			try (ServletOutputStream outStream = resp.getOutputStream()){
                outStream.write(firstEntry.getValue());
			}
	        
	        resp.flushBuffer();
			
		}
		
	}

	private Map<String, byte[]> getPdfFiles(String domainName, String user, Integer enterpriseId, List<Integer> salaryIds) {
		Map<String, byte[]> pdfFiles = new HashMap<String, byte[]>();
		
		// SalaryReport
		String salaryReport =  SalaryTemplate.AON_SOLUTIONS_MACLEOD.getValue();
		try {
			salaryReport = getReportKey(domainName, enterpriseId, SalaryType.SALARY);
		} catch (Exception e) {}
		
		// PayrollType
		PayrollPrintService.PayrollType payrollType ;
		
		if ( AonStringUtils.equalsIgnoreCase(salaryReport, SalaryTemplate.INVOICE_SIMPLE.getValue())) {
		    payrollType = PayrollPrintService.PayrollType.AON;
		}  else if ( AonStringUtils.equalsIgnoreCase(salaryReport, SalaryTemplate.INVOICE_CRA_GROUP.getValue())) {
			payrollType = PayrollPrintService.PayrollType.AON;
		} else if (AonStringUtils.equalsIgnoreCase(salaryReport, SalaryTemplate.AON_SOLUTIONS_MACLEOD.getValue())) {
			payrollType = PayrollPrintService.PayrollType.AON;				
		} else {
			payrollType = PayrollPrintService.PayrollType.CLASSIC;
		}
		
		for(Integer salaryId : salaryIds) {
			try (
					ByteOutputStream os = new ByteOutputStream(30 * 1024); 
					CloseableAONContext aonContext = AONContext.getAONContext(domainName, user)
			) {
				
				if (payrollType == PayrollPrintService.PayrollType.CLASSIC ) {
					JooqPayrollBuilder.generateClassicPayroll(enterpriseId
							, domainName
							, os
							, Optional.ofNullable(null)
							, salaryId);
				} else {
					JooqPayrollBuilder.generatePayroll(enterpriseId
							, domainName
							, os
							, Optional.ofNullable(null)
							, salaryId);
				}
				
				Record2<String, Date> salaryRecord = aonContext.getDslContext().select(SALARY.EMPLOYEE_NAME, SALARY.CHARGE_DATE).from(SALARY).where(SALARY.ID.eq(salaryId)).fetchOne();
				
				String fileName = salaryRecord.get(SALARY.EMPLOYEE_NAME).replaceAll(",", "").replaceAll(" ", "_").trim();
				fileName += "_" + dateFormat.format(salaryRecord.get(SALARY.CHARGE_DATE));
				
				byte[] bytes = os.getBytes();
				
				pdfFiles.put(fileName, bytes);
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e.getMessage());
			}
		}
		
		return pdfFiles;
	}
	
	protected String getReportKey(String domain, final Integer enterpriseID, SalaryType salaryType) throws SQLException {
		return PayrollServletUtils.getSalaryReport(domain, enterpriseID, salaryType);
	}
	
}