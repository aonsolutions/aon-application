package net.aonsolutions.aon.api.servlet;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@WebServlet(name = "SendMailServlet-API", urlPatterns =	{	"/ms/api/send_mail/*",
															"/aon_gwt_aio/ms/api/send_mail/*"})
public class SendMailServlet extends AonApiHttpServlet{

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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("SendMailServlet-API - POST METHOD");
		AonApiData api = initialize(req);		
		
		String to = api.getData().optString("to");
		String body = api.getData().opt("body") != null ? api.getData().optString("body") : "";
		String subject = "";
		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;

		CompanyFull cp = AON.getCompanyFull(api.getDomain().getName(), api.getDomain().getId(), "");
		if(pathInfo != null) {
			if("invoice".equalsIgnoreCase(pathInfo[1])) {
				subject = "Facturas";
				body = invoiceContent(api, api.getData().optJSONArray("invoices"));
			}
			
			if("invoice2".equalsIgnoreCase(pathInfo[1])) {
				subject = "Factura";
				body = invoice2Content(api, cp);
			}
			
			if("invoiceReject".equalsIgnoreCase(pathInfo[1])) {
				subject = "Factura Rechazada";
				body = invoiceRejectContent(api, cp);
			}
			
			if("invoiceRestore".equalsIgnoreCase(pathInfo[1])) {
				subject = "Factura Restaurada";
				body = invoiceRestoreContent(api, cp);
			}
			
			if("document".equalsIgnoreCase(pathInfo[1])) {
				subject = "Documentos";
				body = documentContent(api, api.getData().optJSONArray("documents"));				
			}
			
			if("s3Document".equalsIgnoreCase(pathInfo[1])) {
				subject = "Documentos";
				body = documentS3Content(api, api.getData().optJSONArray("documents"));
			}
		}
		
		String bcc = api.getUser().getAuth().getEmail();
		SESMessage msg = new SESMessage()
				.setTo(to)
				.setBcc(bcc != null ? bcc : "")
				.setReplyTo(bcc != null ? bcc : "")
				.setAlias(cp.getRegistry().getName())
				.setSubject(subject)
				.setBody(body);
		String m = SES.sendEmail(msg);
		JSONObject j = new JSONObject()
			.put(IJsonNames.MESSAGE, m);
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, j, new JSONObject());
	}
	
	public void sendEmailInvoiceReject(Occam occam, String to) {
		AonApiData api = new AonApiData();
		api.setDomain(new Domain().setName(occam.getDomainName()).setId(occam.getDomain()));
		api.setUser(new User().setLogin(occam.getUser()));
		CompanyFull cp = AON.getCompanyFull(api.getDomain().getName(), api.getDomain().getId(), "");

		String subject = "Factura Rechazada";
		String body = invoiceRejectContent(api, cp);

		SESMessage msg = new SESMessage()
				.setTo(to)
				.setAlias(cp.getRegistry().getName())
				.setSubject(subject)
				.setBody(body);
		SES.sendEmail(msg);		
	}
		
	private String invoiceRejectContent(AonApiData api, CompanyFull company) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		CompanyMail cm = new CompanyMail();
		cm.setLogo("https://aon.solutions/assets/aon-logo.png");
		
		Integer domainId = api.getUser().getDomain().getId();
		Company cp = AON.getCompany(api.getOccam(), f -> f.getDomainProperty().eq(domainId));
		if(cp != null && cp.getId() != null) {
			Attach attach = AON.getAttach(api.getDomain().getName(), domainId, api.getUser().getLogin(),
					f -> f.getAttachModuleProperty().eq(cp.getId())
					.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
			cm.setLogo(!attach.isEmpty()
					? "https://" + company.getRegistry().getDomain().getName() + "/aonDocuments/company.logo"	
					: "https://aon.solutions/assets/aon-logo.png");			
		} else cm.setLogo("https://aon.solutions/assets/aon-logo.png");
		
		VelocityContext context = new VelocityContext();
		context.put("company", cm);		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/invoiceReject.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private String invoiceRestoreContent(AonApiData api, CompanyFull company) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		CompanyMail cm = new CompanyMail();
		cm.setLogo("https://aon.solutions/assets/aon-logo.png");
		
		Integer domainId = api.getUser().getDomain().getId();
		Company cp = AON.getCompany(api.getOccam(), f -> f.getDomainProperty().eq(domainId));
		if(cp != null && cp.getId() != null) {
			Attach attach = AON.getAttach(api.getDomain().getName(), domainId, api.getUser().getLogin(),
					f -> f.getAttachModuleProperty().eq(cp.getId())
					.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
			cm.setLogo(!attach.isEmpty()
					? "https://" + company.getRegistry().getDomain().getName() + "/aonDocuments/company.logo"	
					: "https://aon.solutions/assets/aon-logo.png");			
		} else cm.setLogo("https://aon.solutions/assets/aon-logo.png");
		
		VelocityContext context = new VelocityContext();
		context.put("company", cm);		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/invoiceRestore.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private String invoice2Content(AonApiData api, CompanyFull company) {
		JSONObject inv = JsonUtils.getJSONObject(api.getData(), "invoice");
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		Date date = JsonUtils.getDate(inv, "date");
		InvoiceMail im = new InvoiceMail();
		im.setReference(inv.opt("reference") != null ? inv.getString("reference"): "");
		im.setTotal(inv.opt("total") != null ? Double.toString(inv.getDouble("total")) : "");
		im.setDate(AonDateUtils.format(date, "dd/MM/yyyy"));
		im.setUrl(inv.opt("file") != null 
				? inv.optJSONObject("file").optString("url") 
				: getInvoiceUrl(api.getDomain(), api.getUser().getLogin(), inv));

		StringBuilder medias = new StringBuilder();
		company.getMedias().stream().filter(f -> MediaType.FIXED_PHONE.equals(f.getMedia()) || MediaType.CELLULAR.equals(f.getMedia()))
		.forEach(r -> {
			medias.append(" " + r.getValue());
		});
		
		String web = company.getMedias().stream().filter(f -> MediaType.WEB.equals(f.getMedia())).map(r -> r.getValue()).findFirst().orElse("");
		medias.append(AonStringUtils.isBlank(web) ? web : " /" + web);
		String email = api.getUser().getAuth().getEmail() != null 
				?api.getUser().getAuth().getEmail() : "";
		CompanyMail cm = new CompanyMail();
		cm.setAddress(company.getMainAddress().getFullAddress());
		cm.setName(company.getRegistry().getName());
		cm.setMedias(medias.toString());
		cm.setEmail(email);
		Attach attach = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f -> f.getAttachModuleProperty().eq(company.getId())
				.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
		cm.setLogo(!attach.isEmpty()
				? "https://" + company.getRegistry().getDomain().getName() + "/aonDocuments/company.logo"	
				: "https://aon.solutions/assets/aon-logo.png");

		
		VelocityContext context = new VelocityContext();
		context.put("invoice", im);
		context.put("company", cm);		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/invoice2.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private String invoiceContent(AonApiData api, JSONArray invoiceArray) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		LinkedList<InvoiceMail> list = new LinkedList<>();
		for (int i = 0; i < invoiceArray.length(); i++) {
			JSONObject inv = invoiceArray.getJSONObject(i);
			InvoiceMail im = new InvoiceMail();
			im.setReference(inv.opt("reference") != null ? inv.getString("reference"): "");
			im.setTotal(inv.opt("total") != null ? Double.toString(inv.getDouble("total")) : "");
			im.setUrl(inv.opt("file") != null 
					? inv.optJSONObject("file").optString("url") 
					: getInvoiceUrl(api.getDomain(), api.getUser().getLogin(), inv));
			list.add(im);
		}
		
		VelocityContext context = new VelocityContext();
		context.put("invoices", list);
		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/invoice.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private String documentContent(AonApiData api, JSONArray documentArray) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		LinkedList<DocumentMail> list = new LinkedList<>();
		for (int i = 0; i < documentArray.length(); i++) {
			JSONObject doc = documentArray.optJSONObject(i);
			DocumentMail dm = new DocumentMail();
			dm.setDate(doc.opt("date") != null ? doc.optString("date"): "");
			dm.setTitle(doc.opt("title") != null ? doc.optString("title") : "");
			dm.setUrl(getDocumentUrl(api.getDomain(), api.getUser().getLogin(), doc));
			list.add(dm);
		}
		
		VelocityContext context = new VelocityContext();
		context.put("documents", list);
		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/document.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private String documentS3Content(AonApiData api, JSONArray documentArray) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		  LinkedList<DocumentMail> list = new LinkedList<>();
		    for (int i = 0; i < documentArray.length(); i++) {
		        JSONObject doc = documentArray.optJSONObject(i);
		        DocumentMail dm = new DocumentMail();
		        dm.setDate(doc.opt("date") != null ? doc.optString("date"): "");
		        dm.setTitle(doc.opt("name") != null ? doc.optString("name") : "");
		        dm.setUrl(getS3DocumentUrl(api.getDomain(), api.getUser().getLogin(), doc));
		        list.add(dm);
		    }
		    VelocityContext context = new VelocityContext();
		    context.put("documents", list);

		    Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/document.vm");

		    StringWriter writer = new StringWriter();
		    template.merge(context, writer);

		    return writer.toString();
	}
	
	private String getInvoiceUrl(Domain domain, String login, JSONObject invoice) {	
		JSONObject file = invoice.optJSONObject(IJsonNames.FILE);
		if(file != null && file.opt(IJsonNames.URL) != null) {
			return file.optString(IJsonNames.URL);
		} else {
			Integer id = JsonUtils.getInteger(invoice, IJsonNames.ID);
			String status = JsonUtils.getString(invoice, IJsonNames.STATUS);
			
			JSONObject json = new JSONObject();
			json.put(IJsonNames.ID, id);
			json.put(IJsonNames.SOURCE, isRawdoc(status) ? "rawdoc" : "invoice");
			json.put("domain_id", domain.getId());
			json.put("domain_name", domain.getName());
			json.put("login", login);
		    String result = Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
			return "https://" +domain.getName() +"/ms/api/download_invoice_pdf?json=" + result;
		}
	}
	
	private boolean isRawdoc(String status) {
		return "trash".equalsIgnoreCase(status) || "draft".equalsIgnoreCase(status)
			|| "refused".equalsIgnoreCase(status) || "rejected".equalsIgnoreCase(status)
			|| "inbox".equalsIgnoreCase(status);
	}
	
	private String getDocumentUrl(Domain domain, String login, JSONObject document) {	
		String str = "domain="+ domain.getId() + "&id=" + document.getInt("id") + "&attach_type=data";
	    String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
	    return "https://" +domain.getName() +"/ms/download_attachment/"  + domain.getName() + "/" + login + "/" +  result;
	}
	
	private String getS3DocumentUrl(Domain domain, String login, JSONObject document) {	
		String str = "domain="+ domain.getId() + "&id=" + document.getInt("id") + "&type="+ document.getInt(IJsonNames.TYPE);
	    String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
	    return "https://" +domain.getName() +"/ms/download_s3_document/"  + domain.getName() + "/" + login + "/" +  result;
	}
	
	public class CompanyMail {
		String name;
		String address;
		String email;
		String logo;
		String medias;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getAddress() {
			return address;
		}
		
		public void setAddress(String address) {
			this.address = address;
		}
		
		public String getEmail() {
			return email;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
		
		public String getLogo() {
			return logo;
		}
		
		public void setLogo(String logo) {
			this.logo = logo;
		}
		
		public String getMedias() {
			return medias;
		}
		
		public void setMedias(String medias) {
			this.medias = medias;
		}
	}
	
	public class InvoiceMail {
		String reference;
		String total;
		String url; 
		String date;
		
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
		
		public String getDate() {
			return date;
		}
		
		public void setDate(String date) {
			this.date = date;
		}
	}
	
	public class DocumentMail {
		String date;
		String title;
		String url; 
	
		public String getDate() {
			return date;
		}


		public void setDate(String date) {
			this.date = date;
		}


		public String getTitle() {
			return title;
		}


		public void setTitle(String title) {
			this.title = title;
		}


		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
	
}
