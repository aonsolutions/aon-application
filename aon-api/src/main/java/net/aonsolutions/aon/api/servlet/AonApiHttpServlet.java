package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.IConstants;

public class AonApiHttpServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER  = Logger.getLogger(AonApiHttpServlet.class.getName());
	
	private String token;
	private Domain domain;
	private JSONObject data;
	
	public AonApiHttpServlet() {
	
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		initialize(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		initialize(req, resp);
	}
	
	private void initialize(HttpServletRequest req, HttpServletResponse resp) {
		setToken(req.getHeader("session_id"));
		
		String domainName = req.getHeader("domain_name");
		Integer domainId = !"null".equalsIgnoreCase(req.getHeader("domain_id")) && AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
				? AonNumberUtils.toInteger(req.getHeader("domain_id")) : 0;

		Domain domain = AonStringUtils.isBlank(domainName)
				? new Domain().setName(domainName).setId(domainId)
				: AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
		setDomain(domain);
		
		setData(getRequestJSON(req));
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	public JSONObject getData() {
		return data;
	}
	
	public void setData(JSONObject data) {
		this.data = data;
	}
	
	public void error(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		resp.setStatus(400);
		JSONObject json = new JSONObject();
		json.put("message", e.getMessage());
		json.put("type", "error");
		addCorsHeader(resp);
		giveBack(req, resp, json, new JSONObject());
	}
	
	public void response(HttpServletRequest req, HttpServletResponse resp) {
		response(req, resp, new JSONObject());
	}
	
	public void response(HttpServletRequest req, HttpServletResponse resp, Object object) {
		response(req, resp, object, new JSONObject());
	}
	
	public void response(HttpServletRequest req, HttpServletResponse resp, Object object, JSONObject meta) {
		addCorsHeader(resp);
		giveBack(req, resp, object, meta);
	}
	
    protected void addCorsHeader(HttpServletResponse response){
    	//response.addHeader("Access-Control-Allow-Credentials", "true");
    	response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "*");//X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
    }
	
	protected void giveBack(HttpServletRequest req, HttpServletResponse resp,
			Object object, JSONObject meta) {
		try {
			String js = req.getParameter(IConstants.CALLBACK);
			if(js != null){
				resp.setContentType("application/javascript; charset=utf-8");     
				PrintWriter out = resp.getWriter();
				out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
				out.flush();
			} else {
				resp.setContentType("application/json");     
				PrintWriter out = resp.getWriter();
				out.print(object);
				out.flush();
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public JSONObject getRequestJSON(HttpServletRequest req){
		String line = "";
		StringBuilder bld = new StringBuilder();
		try {
			while((line = req.getReader().readLine()) != null){
				bld.append(" " + line);
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		String s = checkString(bld.toString());
		if(s == null || s.isBlank()){
			s = "{}";
		}
		return new JSONObject(s);
	}
	
	public String checkString(String str){
		return new String(str.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8") );
	}
}
