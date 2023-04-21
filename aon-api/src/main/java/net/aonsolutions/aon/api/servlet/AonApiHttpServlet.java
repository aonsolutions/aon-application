package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.Options;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;

public class AonApiHttpServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER  = Logger.getLogger(AonApiHttpServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		initialize(req);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		initialize(req);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		initialize(req);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		initialize(req);
	}
	
	protected AonApiData initialize(HttpServletRequest req) {
		return initialize(req, true);
	}
	
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		response(req, resp);
	}

	protected AonApiData initialize(HttpServletRequest req, boolean check, String...method) {
		AonApiData api = new AonApiData();
		api.setMethod(method.length > 0 ? method[0] : req.getMethod());
		api.setData(api.isGet() ? getParamsJSON(req) : getRequestJSON(req));
		
		api.setToken((AonStringUtils.isEmpty(req.getHeader(IConstants.SESSION_ID)) 
				|| IConstants.NULL.equalsIgnoreCase(req.getHeader(IConstants.SESSION_ID))) 
			? IConstants.EMPTY : req.getHeader(IConstants.SESSION_ID));
		
		Domain domain = getDomain(req, api);
		api.setDomain(domain);
		
		User user = getUser(req, api, domain);
		api.setUser(user);
		
		api.setPath(req.getPathInfo()!= null || IConstants.EMPTY.equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo() : IConstants.ROOT_BAR);
		try {
			DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
			api.setDur(dur);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(check) checkAuthorization(api);
		api.setOptions(getOptions(api));
		return api;
	}
	
    private Options getOptions(AonApiData api) {
        Options options = new Options();
        options.setPage(JsonUtils.getInteger(api.getData(), IJsonNames.PAGE));
        options.setPerPage(JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE));
        options.setFull(JsonUtils.getboolean(api.getData(), IJsonNames.FULL));
        return options;
    }
    
	private Domain getDomain(HttpServletRequest req, AonApiData api) {
		String domainAux = api.getData().has("domainName") ?  api.getData().getString("domainName") : req.getServerName();
		String domainName = AonStringUtils.isBlank(req.getHeader(IConstants.DOMAIN_NAME))
				? JsonUtils.getString(api.getData(), IConstants.DOMAIN_NAME, domainAux) 
				: req.getHeader(IConstants.DOMAIN_NAME);
		Integer domainId = !IConstants.NULL.equalsIgnoreCase(req.getHeader(IConstants.DOMAIN_ID)) && AonNumberUtils.toInteger(req.getHeader(IConstants.DOMAIN_ID)) != null 
				? AonNumberUtils.toInteger(req.getHeader(IConstants.DOMAIN_ID)) 
				: JsonUtils.getInt(api.getData(), IConstants.DOMAIN_ID);
		Domain domain = new Domain().setName(domainName).setId(domainId);
		try {
			domain = AonStringUtils.isBlank(domainName)
				? new Domain().setName(domainName).setId(domainId)
				: AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return domain;
	}
	
	private User getUser(HttpServletRequest req, AonApiData api, Domain domain) {
		String domainLogin = req.getHeader(IConstants.DOMAIN_LOGIN);
		if(AonStringUtils.isBlank(domainLogin) && api.getData().opt(IConstants.DOMAIN_LOGIN) != null) {
			domainLogin = api.getData().getString(IConstants.DOMAIN_LOGIN);
		} else if(AonStringUtils.isBlank(domainLogin) && api.getData().opt("userLogin") != null) {
			domainLogin = api.getData().getString("userLogin");
		}
		User user = new User().setLogin("");
		if(!api.isPredefinedToken() && AonStringUtils.isBlank(domainLogin) && !AonStringUtils.isBlank(api.getToken()) && api.getDomain().getId() != null && api.getDomain().getId() != 0) {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
			user = AON.getUser(domain.getName(), domain.getId(), "", f -> f.getAuthProperty().eq(aonToken.getAuth())
					.and(f.getDomainProperty().eq(api.getDomain().getId())));
			if(user == null || user.getId() == null) {
				user = AON.getUser(domain.getName(), domain.getId(), "", f -> f.getAuthProperty().eq(aonToken.getAuth())
						.and(f.getDomainProperty().eq(api.getDomain().getParentId())));
			}
		} else if(api.getDomain().getId() != null && api.getDomain().getId() != 0){
			user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), domainLogin);
		}
		
		if(user.isEmpty()) {
			user = AON.getUser(api.getDomain().getName(), 0, domainLogin);
		}
		
		if((user.getAuth().isEmpty() || AonStringUtils.isBlank(user.getAuth().getEmail()))
				&& !AonStringUtils.isBlank(api.getToken()) && !api.isPredefinedToken()) {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
			user.setAuth(AON_SOLUTIONS.getAuth(user.getAuth().getAuth() != null 
					? user.getAuth().getAuth() : aonToken.getAuth()));
		}
		if(user.getLogin() == null) user.setLogin("");
		return user;
	}
	
	public void error(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		e.printStackTrace();
		resp.setStatus(400);
		JSONObject json = new JSONObject();
		String className =  e.getClass().getSimpleName();
		String message = e.getMessage()!= null ? e.getMessage() : className;
		json.put(IConstants.MESSAGE, message);
		json.put(IConstants.TYPE, IConstants.ERROR);
		json.put(IConstants.CLASS_NAME, className);
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
	
	public void responseHtml(HttpServletRequest req, HttpServletResponse resp, Object object) {
		try {
			String js = req.getParameter(IConstants.CALLBACK);
			if(js != null){
				resp.setContentType("text/html; charset=utf-8");     
				PrintWriter out = resp.getWriter();
				out.flush();
			} else {
				resp.setContentType("text/html");     
				PrintWriter out = resp.getWriter();
				out.print(object);
				out.flush();
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
	
	public void responseFile(HttpServletResponse resp, Attach attach) throws IOException {
		ByteArrayInputStream is =  new ByteArrayInputStream(attach.getData());
		responseFile(resp, attach.getDescription(), is, attach.getMimeType());
	}
	
	public void responseFile(HttpServletResponse resp, File file, MimeType mimetype ) throws IOException {
		FileInputStream is =  new FileInputStream(file);
		responseFile(resp, file.getName(), is, mimetype);
	}
	
	public void responseFile(HttpServletResponse resp, String filename, byte[] file, MimeType mimetype ) throws IOException {
		ByteArrayInputStream is =  new ByteArrayInputStream(file);
		responseFile(resp, filename, is, mimetype);
	}
	
	public void responseFile(HttpServletResponse resp, String filename, byte[] file, MimeType mimetype, String contentDisposition) throws IOException {
		ByteArrayInputStream is =  new ByteArrayInputStream(file);
		responseFile(resp, filename, is, mimetype, contentDisposition);
	}

	public void responseFile(HttpServletResponse resp, String filename, InputStream is, MimeType mimetype) throws IOException {
		responseFile(resp, filename, is, mimetype, "inline");
	}
	
	public void responseFile(HttpServletResponse resp, String filename, InputStream is, MimeType mimetype, String contentDisposition) throws IOException {
		addCorsHeader(resp);
        resp.setContentType(mimetype.getName());
		resp.setHeader(IConstants.CONTENT_DISPOSITION, contentDisposition + "; filename=\"" + filename + "." + mimetype.getExtension() +"\";");
		AonIOUtils.copy(is, resp.getOutputStream());
		resp.flushBuffer();
		is.close();
	}
	
	public void responseFile(HttpServletResponse resp, String filename, MimeType mimetype) throws IOException {
		addCorsHeader(resp);
        resp.setContentType(mimetype.getName());
		resp.setHeader(IConstants.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "." + mimetype.getExtension() +"\";");
		resp.flushBuffer();
	}
	
    protected void addCorsHeader(HttpServletResponse response) {
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
		StringBuilder bld = new StringBuilder();
		try {
			String line = "";
			while((line = req.getReader().readLine()) != null){
				bld.append(" " + line);
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		String s = bld.toString();
		
		if(s == null || s.isBlank()){
			return new JSONObject();
		} else
			return new JSONObject(s); 
	}


	public static JSONObject getParamsJSON(ServletRequest req) {
	    JSONObject jsonObj = new JSONObject();
	    @SuppressWarnings("unchecked")
		Map<String,String[]> params = req.getParameterMap();
	    for (Map.Entry<String,String[]> entry : params.entrySet()) {
	      String[] v = entry.getValue();
	      Object o = (v.length == 1) ? v[0] : v;
	      jsonObj.put(entry.getKey(), o);
	    }
	    return jsonObj;
	}
	
	private void checkAuthorization(AonApiData api) {
		if(AonStringUtils.isBlank(api.getToken())) {
			throw new AonApiException(AonApiError.UNAUTHORIZED.getMessage());
		}
		
		if(!api.isPredefinedToken()) {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
		
			if(aonToken.isExpired()) {
				throw new AonApiException(AonApiError.EXPIRED_TOKEN.getMessage());
			}
		}
	}
	
	public String decode(byte[] value){
		String decode = "";
		try{
			decode = new String(Base64.getDecoder().decode(value), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decode;
	}
	
	public static Certificate checkCertificate(AonApiData api) {
		Certificate cert = new Certificate();
		try {
			if(api.getData().opt("cert") != null) {
				Integer id = JsonUtils.getInteger(api.getData(), "cert");
				cert = AON.getCertificates(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(id))
						.findFirst().orElse(new Certificate());
			} else {
				cert =  AON.getCertificate(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), CertificateType.AEAT.name());				
			}

		} catch (Exception e) {
			throw new AonApiException("Error al obtener el certificado.");
		}
		try {
			if(!checkCert(cert.getData(), cert.getPassword())) {
				throw new AonApiException("El certificado o la contraseña no son correctos.");
			}
		} catch (Exception e) {
			throw new AonApiException("El certificado o la contraseña no son correctos.");			
		}		
		if(cert.isEmpty()) {
			throw new AonApiException("El certificado no existe.");
		}
		return cert;
		
	}
	
	public static boolean checkCert(byte[] cert, String password) {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(cert);
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(is, password.toCharArray());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
