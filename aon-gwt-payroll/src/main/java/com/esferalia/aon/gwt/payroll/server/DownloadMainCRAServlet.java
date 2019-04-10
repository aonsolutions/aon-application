package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.payroll.jooq.JooqCRA;

@SuppressWarnings("serial")
@WebServlet(name = "Download-CRA", urlPatterns = { "/aon_gwt_payroll/download_cra/*" })
public class DownloadMainCRAServlet extends HttpServlet {
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//Get Request Parametrers
		String _domainId = request.getParameter("domainId");
		String _craBatchId = request.getParameter("craBatchId");
		
		//Get domain Name
		String domainName = request.getServerName();
		
		try {
			Date currentDate = new Date();
			String day = currentDate.getDate() < 10 ? "0"+currentDate.getDate() : currentDate.getDate()+"";
			String month = (currentDate.getMonth()+1) < 10 ? "0"+(currentDate.getMonth()+1) : (currentDate.getMonth()+1)+"";
			String hour = currentDate.getHours() < 10 ? "0"+currentDate.getHours() : currentDate.getHours()+"";
			String minutes = currentDate.getMinutes() < 10 ? "0"+currentDate.getMinutes() : currentDate.getMinutes()+"";
			String fileName = day + month + hour + minutes;
			dateFormatter.applyPattern("yyyy/MM/dd");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".CRA\"");
			ServletOutputStream output = response.getOutputStream();
			
			byte[] data = JooqCRA.getDownloadMainCRA(_domainId, domainName, _craBatchId);
			
			output.write(data);
			
			response.flushBuffer();
		
		}catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		
	}

}
