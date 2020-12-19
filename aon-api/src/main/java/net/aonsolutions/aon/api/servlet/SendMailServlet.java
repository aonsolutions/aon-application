package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONArray;
import org.json.JSONObject;

//import com.code.aon.webservice.common.MSG;
//import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonNumberUtils;

import solutions.aon.aws.SES;

@WebServlet(name = "SendMailServlet-API", urlPatterns =	{	"/ms/api/send_mail/*",
															"/aon_gwt_aio/ms/api/send_mail/*"})
public class SendMailServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER  = Logger.getLogger(SendMailServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("SendMailServlet-API - GET METHOD");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("SendMailServlet-API - POST METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");
		Domain domain = AON.getDomain(domainName, domainId, "");
		User user = AON_SOLUTIONS.getUser(domain, token);
		AonToken aonToken = SECURITY.getAonToken(token);
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getAuth());
		
		JSONObject json = Utils.getRequestJSON(req);
		String from = "no-reply@aon.solutions"; //auth.getEmail();
		String to = json.getString("to");
		String body = json.opt("body") != null ? json.optString("body") : "";
		String subject = "";
		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
		if(pathInfo != null) {
			if("invoice".equalsIgnoreCase(pathInfo[1])) {
				subject = "Facturas";
				body = invoiceContent(domain, user.getLogin(), json.getJSONArray("invoices"));
			}
			
			if("document".equalsIgnoreCase(pathInfo[1])) {
				
			}
		}
		
		SES.sendEmail(from, to, subject, body);
	}
		
	
	private String invoiceContent(Domain domain, String login, JSONArray invoiceArray) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		LinkedList<InvoiceMail> list = new LinkedList<>();
		for (int i = 0; i < invoiceArray.length(); i++) {
			JSONObject inv = invoiceArray.getJSONObject(i);
			InvoiceMail im = new InvoiceMail();
			im.setReference(inv.opt("reference") != null ? inv.getString("reference"): getUrl(domain, login, inv));
			im.setTotal(inv.opt("total") != null ? Double.toString(inv.getDouble("total")) : getUrl(domain, login, inv));
			im.setUrl(inv.opt("file") != null ? inv.optJSONObject("file").optString("url") : getUrl(domain, login, inv));
			list.add(im);
		}
		
		VelocityContext context = new VelocityContext();
		context.put("invoices", list);
		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/invoice.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private String getUrl(Domain domain, String login, JSONObject invoice) {	
		
		String str = "domain="+ domain.getId() + "&id=" + invoice.getString("id") + "&attach_type=data";
	    String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
	    return "https://" +domain.getName() +"/ms/download_rawdoc/"  + domain.getName() + "/" + login + "/" +  result;

	}
	
	public class InvoiceMail {
		String reference;
		String total;
		String url; 
		
		public InvoiceMail() {
		}

		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}

		public String getTotal() {
			return total;
		}

		public void setTotal(String total) {
			this.total = total;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
	
}
