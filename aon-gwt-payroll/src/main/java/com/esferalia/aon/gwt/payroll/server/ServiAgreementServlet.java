package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.payroll.agreement.ServiAgreement;
import com.esferalia.aon.payroll.agreement.ServiAgreement.Extension;
import com.esferalia.aon.payroll.agreement.ServiAgreementsFilter;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "ServiAgreement-Servlet", urlPatterns = { "/aon_gwt_payroll/servi_agreement/*" })
public class ServiAgreementServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//Get Request Parametrers		
		String fileType = request.getParameter("fileType");
		
		String ssNumber = request.getParameter("ssNumber");
		String serviAgreemntCode = ServiAgreementsFilter.getServiAgreementCode(ssNumber);
		
		try {
			if(AonStringUtils.equalsIgnoreCase(fileType, "pdf"))
				response.setContentType("application/pdf");
			else if (AonStringUtils.equalsIgnoreCase(fileType, "xls"))
				response.setContentType("application/vnd.ms-excel");
			
			response.setHeader("Content-Disposition", "attachment;filename=" + ssNumber + "." + fileType);
			ServletOutputStream output = response.getOutputStream();
			
			InputStream file = ServiAgreement.get_online_file(serviAgreemntCode, getExtention(fileType));
			copy(file, output);
			response.flushBuffer();
		
		}catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} 
		
	}
	
	private Extension getExtention(String fileType) {
		switch (fileType) {
		case "pdf":
			return Extension.PDF;
		case "xls":
			return Extension.XLS;
		default:
			return null;
		}
	}

	private void copy(InputStream source, OutputStream target) throws IOException {
	    byte[] buf = new byte[8192];
	    int length;
	    while ((length = source.read(buf)) > 0) {
	        target.write(buf, 0, length);
	    }
	}

}
