package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
//import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import solutions.aon.seg.social.objects.Employee;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exceptions.SegSocialException;

@SuppressWarnings("serial")
@WebServlet(name = "ComunicaServlet", urlPatterns = {"/ms/api/comunica/*"})
public class ComunicaServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOGGER.info("AON EXAMPLE SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		String domainName = req.getHeader("domain_name");
		byte content [] = null;
		Integer domainId = !"null".equalsIgnoreCase(req.getHeader("domain_id")) && AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
				? AonNumberUtils.toInteger(req.getHeader("domain_id")) : 0;

				
		Domain domain = AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
		
		Gson gjson = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();
		OutputStream os = resp.getOutputStream();
		try {

			String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
			
			Utils.addCorsHeader(resp);
			
			resp.setStatus(HttpServletResponse.SC_OK);
			
		
			if(pathInfo  != null) {
				if("movements".equalsIgnoreCase(pathInfo[1])) {// mov de empleados prev de empleados
					content = gjson.toJson(this.getMovements()).getBytes();
				} 
			}

		} 
		catch (Exception e) {
            e.printStackTrace(); 
			resp.setStatus(500);
			content = gjson.toJson(e.getMessage()).getBytes();
		}
		resp.setContentType("text/html");
		resp.setContentLength(content.length);
		os.write(content);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("USER SERVLET - POST METHOD");
		String token = req.getHeader("session_id");
				String domainName = req.getHeader("domain_name");
		Integer domainId = !"null".equalsIgnoreCase(req.getHeader("domain_id")) && AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
				? AonNumberUtils.toInteger(req.getHeader("domain_id")) : 0;
		Domain domain = AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
		
		JSONObject json = Utils.getRequestJSON(req);
		if(pathInfo != null) {
			if("app".equalsIgnoreCase(pathInfo[1])) {
				// Ejecutar funcionalidad del api
			}
		}
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, json, new JSONObject());
	}
	
	private Collection<Employee> getMovements() throws SegSocialException {
		Collection<Employee> EmployeesAll = new LinkedList<>();
		
	    final InputStream certificateInputStream = ComunicaServlet.class.getResourceAsStream("FNMT.p12");
	    
		String[][] cccAll = {
			{"01105360062", "0111"}
		};
		
		for (int i = 0; i < cccAll.length; i++) {
		    String cti = cccAll[i][0];
		    String regimen = cccAll[i][1];
		    Collection<Employee> Employees = SistemaRED.getTotalEmployees(certificateInputStream, "jg@FNMT", "pkcs12", regimen, cti);	
		    for (Employee employee : Employees) {
				employee.setCtaCti(cti);
				employee.setRegime(regimen);
			}
		    EmployeesAll.addAll(Employees);
		}
		return EmployeesAll;
	}
	
}
