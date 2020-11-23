package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
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
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
//import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import solutions.aon.seg.social.objects.Employee;
import solutions.aon.seg.social.objects.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.Toolkit;

import com.esferalia.aon.occam.api.model.security.Certificate;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.SistemaREDMov;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;

@SuppressWarnings("serial")
@WebServlet(name = "ComunicaServlet", urlPatterns = {"/ms/api/comunica/*"})
public class ComunicaServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON EXAMPLE SERVLET - GET METHOD");
		OutputStream os = resp.getOutputStream();
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		byte content [] = gjson.toJson(null).getBytes();
		
		try {
			String param = req.getParameter("json");
			JSONObject json = new JSONObject();
			if(param!=null) {
				param = new String(Base64.getDecoder().decode(param));
				json = new JSONObject(param);
			}
				
			String token = req.getHeader("session_id");
			String domainName = req.getHeader("domain_name");
			
			Integer domainId = !"null".equalsIgnoreCase(req.getHeader("domain_id")) && AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
					? AonNumberUtils.toInteger(req.getHeader("domain_id")) : 0;
	
			Domain domain = AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
			

			String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
			
			Utils.addCorsHeader(resp);
			
			resp.setStatus(HttpServletResponse.SC_OK);
			
		
			if(pathInfo  != null) {
				if("movements".equalsIgnoreCase(pathInfo[1])) {// mov de empleados prev de empleados
					try {
						LOGGER.info("MOVEMENTS SERVLET - POST METHOD");
						final InputStream certificateInputStream = ComunicaServlet.class.getResourceAsStream("FNMT.p12");
					    String certificatePassword = "jg@FNMT";
					    String certificateType = "pkcs12";
						content = gjson.toJson(this.getMovements(certificateInputStream, certificatePassword, certificateType, json)).getBytes();
					}
					catch (SegSocialException e) {resp.setStatus(500);content = gjson.toJson(e.getCause().getMessage()).getBytes();}
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
		OutputStream os = resp.getOutputStream();
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		byte content [] = gjson.toJson(null).getBytes();
		
		try {

			JSONObject json = Utils.getRequestJSON(req);
		
			String token = req.getHeader("session_id");
			String domainName = req.getHeader("domain_name");
			
			Integer domainId = !"null".equalsIgnoreCase(req.getHeader("domain_id")) && AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
					? AonNumberUtils.toInteger(req.getHeader("domain_id")) : 0;
			Domain domain = AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
			String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
			
			Utils.addCorsHeader(resp);
			resp.setStatus(HttpServletResponse.SC_OK);
			
			User user = AON_SOLUTIONS.getUser(domain, token);
			
	//		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
	//		byte data [] = SistemaRED.getIDCCCC(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), regime, ccc, today);
			if(pathInfo != null) {
				if("alta-directa".equalsIgnoreCase(pathInfo[1])) {
					try {
						LOGGER.info("ALTA-DIRECTA SERVLET - POST METHOD");
						final InputStream certificateInputStream = ComunicaServlet.class.getResourceAsStream("FNMT.p12");
					    String certificatePassword = "jg@FNMT";
					    String certificateType = "pkcs12";
					    content = gjson.toJson(sendMov(certificateInputStream, certificatePassword, certificateType, json)).getBytes();
					}
				    catch (SegSocialException e) {resp.setStatus(500);content = gjson.toJson(e.getCause().getMessage()).getBytes();}
				} else if("delete-mov".equalsIgnoreCase(pathInfo[1])) {
					try {
						LOGGER.info("ALTA-DIRECTA SERVLET - POST METHOD");
						final InputStream certificateInputStream = ComunicaServlet.class.getResourceAsStream("FNMT.p12");
					    String certificatePassword = "jg@FNMT";
					    String certificateType = "pkcs12";
					    content = gjson.toJson(movPrevDelete(certificateInputStream, certificatePassword, certificateType, json)).getBytes();
					}
					catch (SegSocialException e) {resp.setStatus(500);content = gjson.toJson(e.getCause().getMessage()).getBytes();}
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			resp.setStatus(500);
			content = gjson.toJson(e.getMessage()).getBytes();
		}
		
		resp.setContentType("text/html");
		resp.setContentLength(content.length);
		os.write(content);
	}
	
	
	private Collection<Employee> getMovements(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, JSONObject json) throws SegSocialException {
		
		String[][] cccAll = {
			{"01105360062", "0111"}
		};
		
		byte[] cert;
		ArrayList<Employee> employees = new ArrayList<>();
		
		for (int i = 0; i < cccAll.length; i++) {
		    String cti = cccAll[i][0];
		    String regimen = cccAll[i][1];
			try {
				cert = certificateInputStream.readAllBytes();
				try{employees.addAll(SistemaRED.getTotalEmployees(new ByteArrayInputStream(cert), certificatePassword, certificateType, regimen, cti));}
				catch(Exception e){e.printStackTrace();}
			} catch (IOException e) {throw new InvalidCertificateException();}
		}
		return employees;
	}
	
	private Employee sendMov(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType , JSONObject json) throws SegSocialException{

		//first screen
		String situacion = "AL";
		String regimen = json.getString("regimen");
		String ctaCti = json.getString("ctaCti");
		String nss = json.getString("nss");
		
		String ipf = json.getString("ipf");
		
		//second screen
		Date fecha = Toolkit.parseDate(json.getString("fecha"), "dd-MM-yyyy");
		
		String ocupacion = json.has("ocupacion")  && !json.isNull("ocupacion") ? json.getString("ocupacion") : null;
		String coefparcial = json.has("coefparcial")  && !json.isNull("coefparcial") ? json.getString("coefparcial") : null;
		String convenio =  json.has("convenio")  && !json.isNull("convenio") ? json.getString("convenio") : "60888888888888";
		String grup_ctz = json.getString("grup_ctz");
		String type_cto = json.getString("type_cto");

		String md_ctz = json.has("md_ctz")  && !json.isNull("md_ctz") ? json.getString("md_ctz") : null; //para regimen agrario
//        Ctz mensual = 1
//        Jornadas reales = 2
		EmployeeBuilder builder = new EmployeeBuilder();
		Employee employee = builder.setSituation(situacion)
		.setRegime(regimen)
		.setCtaCti(ctaCti)
		.setNss(nss)
		.setIpf(ipf)
		.setFra(fecha)
		.setOcup(ocupacion)
		.setCoef(coefparcial)
		.setColec(convenio)
		.setGc(grup_ctz)
		.setContract(type_cto)
		.setMdctz(md_ctz)
		.build();
		 
		return SistemaREDMov.sendMov(certificateInputStream, certificatePassword, certificateType, employee);
	}
	
	
	private Employee movPrevDelete(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType , JSONObject json) throws SegSocialException{

		//first screen
		String situacion = json.getString("situation");
		String regimen = json.getString("regime");
		String ctaCti = json.getString("ctaCti");
		String nss = json.getString("nss");

		//second screen
		Date fecha = Toolkit.parseDate(json.getString("fra"), "yyyy-MM-dd");

		EmployeeBuilder builder = new EmployeeBuilder();
		Employee employee = builder.setSituation(situacion)
		.setRegime(regimen)
		.setCtaCti(ctaCti)
		.setNss(nss)
		.setFra(fecha)
		.build();
		 
		return SistemaREDMov.movPrevDelete(certificateInputStream, certificatePassword, certificateType, employee);
	}
	
	
}
