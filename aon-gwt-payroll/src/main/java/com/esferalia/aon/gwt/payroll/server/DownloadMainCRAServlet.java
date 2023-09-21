package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.CraBatch.CRA_BATCH;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jooq.Record;

import com.esferalia.aon.gwt.payroll.jooq.JooqCRA;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Download-CRA", urlPatterns = { "/aon_gwt_payroll/download_cra/*" })
public class DownloadMainCRAServlet extends HttpServlet {
	
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("ddHHmmss");
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//Get Request Parametrers
		String _craBatchId = request.getParameter("craBatchId");
		
		//Get domain Name
		String domainName = request.getServerName();
		
		try {
			
			Record craRecord = JooqCRA.getDownloadMainCRA(domainName, _craBatchId);
			
			byte[] data = craRecord.get(CRA_BATCH.OUTCOME_FILE);
			
			String craStr = new String(data);
			String craFileName = AonStringUtils.isBlank(craStr) || craStr.length() < 73 ? "" : craStr.substring(41, 49);
			
			if(AonStringUtils.isBlank(craFileName)) {
				Date fileNameDate = craRecord.get(CRA_BATCH.DATE);
				craFileName = dateFormatter.format(fileNameDate);
			}
			
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=\"" + craFileName + ".CRA\"");
			ServletOutputStream output = response.getOutputStream();
			
			output.write(data);
			
			response.flushBuffer();
		
		}catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
	}

}
