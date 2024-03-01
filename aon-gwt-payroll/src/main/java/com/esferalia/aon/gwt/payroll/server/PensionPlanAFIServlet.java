package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.payroll.tgss.afi.MainPensionPlanAFIGenerator;
import com.esferalia.aon.payroll.tgss.afi.PensionPlanAFI;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "PensionPlan-AFI", urlPatterns = { "/aon_gwt_payroll/pension_plan_afi/*" })
public class PensionPlanAFIServlet extends HttpServlet {
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat dateFormatter2 = new SimpleDateFormat("ddMMyyyy");
	
	@SuppressWarnings("deprecation")
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			//Get Request Parametrers		
			Date date = dateFormatter2.parse(request.getParameter("findingDate"));
			
			Integer cccsSize = Integer.parseInt(request.getParameter("selectedCCCs"));
			ArrayList<Integer> cccIdList = new ArrayList<Integer>();
			if(0 != cccsSize) {
				for(int i=0; i<cccsSize; i++) {
					cccIdList.add(Integer.parseInt(request.getParameter("ccc"+i+"Id")));
				}
			}
				
			//Este JSON lo deberia obtener del Request cuando me llaman al Servlet
			JSONObject pensionPlanJSON = null; 
			
			//Get domain Name
			String domainName = request.getServerName();
			
			Connection connection = AonServletUtils.getConnection(domainName);
			
			Date currentDate = new Date();
			String day = currentDate.getDate() < 10 ? "0"+currentDate.getDate() : currentDate.getDate()+"";
			String month = (currentDate.getMonth()+1) < 10 ? "0"+(currentDate.getMonth()+1) : (currentDate.getMonth()+1)+"";
			String hour = currentDate.getHours() < 10 ? "0"+currentDate.getHours() : currentDate.getHours()+"";
			String minutes = currentDate.getMinutes() < 10 ? "0"+currentDate.getMinutes() : currentDate.getMinutes()+"";
			String fileName = day + month + hour + minutes;
			dateFormatter.applyPattern("yyyy/MM/dd");
			response.setContentType("text/html;charset=utf-8"/*MimeType.MIME_RTF.getName()*/);
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".AFI\"");
			ServletOutputStream output = response.getOutputStream();
			
			pensionPlanJSON = PensionPlanAFI.getPensionPlanInfo(connection, cccIdList, date);
			String pensionPlanAFI = MainPensionPlanAFIGenerator.generatePensionPlanAFI(pensionPlanJSON);
			
			output.write(pensionPlanAFI.getBytes());
			
			response.flushBuffer();
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
	}

}
