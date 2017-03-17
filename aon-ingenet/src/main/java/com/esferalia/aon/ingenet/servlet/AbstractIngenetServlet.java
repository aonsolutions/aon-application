package com.esferalia.aon.ingenet.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.esferalia.aon.ingenet.api.util.IngenetXmlValidator;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;

public abstract class AbstractIngenetServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIngenetServlet.class.getName());
	
	protected static final String RECIPIENTS_TO_LOG = "udapalog@aonsolutions.es";
	protected static final String RECIPIENTS_TO_SUCCESS = "udapasuccess@aonsolutions.es";
	protected static final String RECIPIENTS_TO_FAILURES = "udapafailures@aonsolutions.es";
	
	private String scheme;
	private boolean devEnabled;
	
	private String user;
	private String password;
	private String domain;
	private Integer domainId;

	protected static final String PARAM_USERNAME = "username";
	protected static final String PARAM_PASSWORD = "password";
	protected static final String PARAM_VALUE = "value";
	
	private SimpleDateFormat dateFormatter;
	private SimpleDateFormat timeFormatter;
	
	protected String getScheme() {
		return scheme;
	}
	
	protected String getUser() {
		return user;
	}

	protected String getDomain() {
		return domain;
	}
	
	protected Integer getDomainId() {
		return domainId;
	}
	
	protected boolean isDevEnabled(){
		return devEnabled;
	}


	protected abstract void processRequest(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException;
	
	
	protected SimpleDateFormat getDateFormatter() {
		if(dateFormatter==null){
			dateFormatter = new SimpleDateFormat("yyyyMMdd");
		}
		return dateFormatter;
	}
	
	protected SimpleDateFormat getTimeFormatter() {
		if(timeFormatter==null){
			timeFormatter = new SimpleDateFormat("hhmm");
		}
		return timeFormatter;
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("***** INGENET POST - " + this.getClass().getName());
		process(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("***** INGENET GET - " + this.getClass().getName());
		process(request, response);
//		TODO do not allow GET method
//		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
	protected void process(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		loadContext(httpRequest);

		String _value = httpRequest.getParameter(PARAM_VALUE);
		String name = httpRequest.getServletPath().replaceAll("/", "")
				.replaceAll("ingenet", "");
		String content = "Se ha detectado una nueva comunicación para " + name;
		sendEmail(content, name, _value, RECIPIENTS_TO_LOG);
		saveToDisk(name, _value);
		
		if(doLogin(domain, user, password)){
			processRequest(httpRequest, httpResponse);
		} else {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
	}

	protected boolean doLogin(String domainName, String username,
			String password) {
		LOGGER.info("***** INGENET LOGIN: " + domainName + "@" + username
				+ " (using password " + (password != null ? "YES" : "NO") + ")");
		if (domainName != null && username != null && password != null
				&& domainName.matches("^udapa\\..*")) {
			return "ingenet".equals(username) && "1ng3n3t".equals(password);
		}
		return false;
	}
	
	private void loadContext(HttpServletRequest httpRequest) {
		String _domainName = httpRequest.getServerName();
		String _username = httpRequest.getParameter(PARAM_USERNAME);
		String _password = httpRequest.getParameter(PARAM_PASSWORD);
		
		scheme = httpRequest.getScheme();
		devEnabled = false;
		// TODO check devEnabled 
		devEnabled = httpRequest.getServerPort()==8080;
		scheme = devEnabled?"http":"https";
		System.out.print("local "+httpRequest.getLocalPort());
		System.out.print(" | server "+httpRequest.getServerPort());
		System.out.print(" | protocol "+httpRequest.getProtocol());
		System.out.print(" | url "+httpRequest.getRequestURL());
		System.out.print(" | scheme "+httpRequest.getScheme());
		System.out.println(" | host "+httpRequest.getHeader("host"));
		
		if (_domainName != null && _username != null && _password != null) {
			user = _username;
			password = _password;			
			domain = _domainName;
			domainId = searchDomainId(_domainName);
		}
		
	}
	
	private Integer searchDomainId(String _domainName) {
		AONContext ctx = AONContext.getAONContext(getDomain(), -1, getUser());
		Domain domain = DomainDAO.getDomain(ctx, p -> {
			return p.getNameProperty().eq(_domainName);
		});
		return domain.getId();
	}
	
	
	
	/*
	 * JAXB
	 */
	
	protected void validateRespuestaElaboracionesXmlPattern(
			InputStream xmlStream) throws IOException, SAXException {
		IngenetXmlValidator.validateXmlPattern(xmlStream,
				IngenetXmlValidator.SCHEMA_FILE_NAME_RESPUESTA_ELABORACIONES);
	}

	protected void validateConsultaElaboracionesXmlPattern(InputStream xmlStream)
			throws IOException, SAXException {
		IngenetXmlValidator.validateXmlPattern(xmlStream,
				IngenetXmlValidator.SCHEMA_FILE_NAME_CONSULTA_ELABORACIONES);
	}

	protected void validateAlbaranesXmlPattern(InputStream xmlStream)
			throws IOException, SAXException {
		IngenetXmlValidator.validateXmlPattern(xmlStream,
				IngenetXmlValidator.SCHEMA_FILE_NAME_ALBARANES);
	}
	
	/*
	 * LOGGING
	 */
	
	public MailAccount getAdminMailAccount() {
		return AON.getMailAccountList(getDomain(), getDomainId(), getUser(), 
				f -> f.getDomainProperty().eq(0)).getFirst();
	}
	
	
//	public static MailAccount getEmailSender() throws UnsupportedEncodingException {
//		MailAccount mailAccount = new MailAccount();
//		mailAccount.setEmail("admin@aonSolutions.es");
//		mailAccount.setMailUsername("admin@aonSolutions.es");
//		mailAccount.setPassword("admineM41L");
//		mailAccount.setIncomingSecurity((byte)ConnectionSecurity.TLS.ordinal());
//		mailAccount.setIncomingHost("imap.aonsolutions.es");
//		mailAccount.setOutgoingSecurity((byte)ConnectionSecurity.TLS.ordinal());
//		mailAccount.setOutgoingHost("smtp.aonsolutions.es");
//		mailAccount.setDisplayName("aonSolutions");
//		Address from = new InternetAddress( mailAccount.getEmail(), mailAccount.getDisplayName() );
//		return new EmailSender( from, mailAccount );							
//	}
	
	protected void sendEmail(String msg, String attachName, String attachValue, String... recipients) {
		JSONObject json = new JSONObject();
		try {
			String recipientsTo = "";
			if(recipients!=null){
				for(String to: recipients){
					if(!recipientsTo.isEmpty())
						recipientsTo += ",";
					recipientsTo += to;
				}
			}
			MailAccount mail = getAdminMailAccount();
			json.put("mailAccountId", mail.getId())
				.put("recipientsTo", recipientsTo)
				.put("content", msg)
				.put("subject", "[AON] Recepcion automatica de albaranes")
				.put("login", getUser())
				.put("domainName", getDomain())
				.put("domainId", getDomainId())
				.put("bcc", "eagirrezabal@aonsolutions.es");
			
			if(attachValue==null || "".equals(attachValue)){
				json.put("md5", "");
			} else {
				String encode = Base64.getEncoder().encodeToString(attachValue.getBytes());
				json.put("md5", encode)
					.put("attachName", attachName+".xml")
					.put("mimetype", MimeType.XML.ordinal());
			}
		
			String url = getScheme() + "://" + getDomain()
					+ (isDevEnabled() ? ":8080/aon-aio" : "") + "/send_email/";
			LOGGER.info("SEND EMAIL URL " + url);
			HttpClientBuilder base = HttpClientBuilder.create();
			HttpClient client = base.build();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> urlParameters =  new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("details", json.toString()));
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse resp = client.execute(post);
			System.out.println(resp);
		} catch (JSONException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e){
			LOGGER.error(e.getMessage());
		}
	}
	
	protected void saveToDisk(String namePrefix, String value) {
		byte data[] = value.getBytes();
		Path file = Paths.get("/var", "tmp", "ingenet", namePrefix + "_"
				+ new SimpleDateFormat("ddMMyyyy-hhmmss").format(new Date())
				+ ".xml");
		try {
			Path parentDir = file.getParent();
			if (!Files.exists(parentDir))
				Files.createDirectories(parentDir);
			Files.write(file, data, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			LOGGER.error("Error guardando el fichero recibido: "
					+ e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Error guardando el fichero recibido: "
					+ e.getMessage());
		}
	}
	

}
