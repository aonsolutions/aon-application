package net.aonsolutions.aon.api.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
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
	private User user;
	private JSONObject data;
	private JSONObject params;
	private String path;
	
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
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		initialize(req, resp);
	}
	
	private void initialize(HttpServletRequest req, HttpServletResponse resp) {
		setToken((AonStringUtils.isEmpty(req.getHeader(IConstants.SESSION_ID)) 
				|| IConstants.NULL.equalsIgnoreCase(req.getHeader(IConstants.SESSION_ID))) 
			? IConstants.EMPTY : req.getHeader(IConstants.SESSION_ID));
		
		String domainName = req.getHeader(IConstants.DOMAIN_NAME);
		Integer domainId = !IConstants.NULL.equalsIgnoreCase(req.getHeader(IConstants.DOMAIN_ID)) && AonNumberUtils.toInteger(req.getHeader(IConstants.DOMAIN_ID)) != null 
				? AonNumberUtils.toInteger(req.getHeader(IConstants.DOMAIN_ID)) : 0;

		Domain domain = AonStringUtils.isBlank(domainName)
				? new Domain().setName(domainName).setId(domainId)
				: AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
		setDomain(domain);
		

		String domainLogin = req.getHeader(IConstants.DOMAIN_LOGIN);
		User user = new User().setLogin("");
		if(AonStringUtils.isBlank(domainLogin) && !AonStringUtils.isBlank(getToken()) && getDomain().getId() != null && getDomain().getId() != 0) {
			AonToken aonToken = SECURITY.getAonToken(getToken());
			user = AON.getUser(domainName, domainId, "", f -> f.getAuthProperty().eq(aonToken.getAuth())
					.and(f.getDomainProperty().eq(getDomain().getId()).or(f.getDomainProperty().eq(getDomain().getParentId()))));
		} else if(getDomain().getId() != null && getDomain().getId() != 0){
			user = AON.getUser(getDomain().getName(), getDomain().getId(), domainLogin);
		}
		setUser(user);
		
		setParams(getParamsJSON(req));
		setData(getRequestJSON(req));
		
		setPath(req.getPathInfo()!= null || IConstants.EMPTY.equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo() : IConstants.ROOT_BAR);
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
	
	public JSONObject getParams() {
		return params;
	}
	
	public void setParams(JSONObject params) {
		this.params = params;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void error(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		e.printStackTrace();
		resp.setStatus(400);
		JSONObject json = new JSONObject();
		json.put(IConstants.MESSAGE, e.getMessage());
		json.put(IConstants.TYPE, IConstants.ERROR);
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
	
	public void responseFile(HttpServletRequest req, HttpServletResponse resp, File file, MimeType mimetype ) throws IOException {
		addCorsHeader(resp);
        resp.setContentType(mimetype.getName());
		resp.setHeader(IConstants.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "." + mimetype.getExtension() +"\";");
		FileInputStream fileInpurOs =  new FileInputStream(file);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		resp.flushBuffer();
		fileInpurOs.close();
	}
	
    protected void addCorsHeader(HttpServletResponse response){
    	response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.addHeader(IConstants.ACCESS_CONTROL_MAX_AGE, "1728000");
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
		String s = bld.toString();
		if(s == null || s.isBlank()){
			s = "{}";
		}
		return new JSONObject(s);
	}
	
	public static JSONObject getParamsJSON(ServletRequest req) {
	    JSONObject jsonObj = new JSONObject();
	    @SuppressWarnings("unchecked")
		Map<String,String[]> params = req.getParameterMap();
	    for (Map.Entry<String,String[]> entry : params.entrySet()) {
	      String v[] = entry.getValue();
	      Object o = (v.length == 1) ? v[0] : v;
	      jsonObj.put(entry.getKey(), o);
	    }
	    return jsonObj;
	}
}
