package com.esferalia.aon.ingenet.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.esferalia.aon.ingenet.api.util.IngenetXmlValidator;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

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
	private Boolean metadata;

	protected static final String PARAM_USERNAME = "username";
	protected static final String PARAM_PASSWORD = "password";
	protected static final String PARAM_VALUE = "value";
	protected static final String PARAM_METADATA = "metadata";
	
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
	
	protected String getMailingDevelopers(AONContext ctx) {
		ApplicationParameter p = AppParamDAO.fetchOne(ctx, "MAILING_DEVELOPER");
		return p!=null?p.getValue():null;
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("***** INGENET POST - " + this.getClass().getSimpleName());
		process(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("***** INGENET GET - " + this.getClass().getSimpleName());
		process(request, response);
	}
	
	protected void process(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException {
		
		loadContext(httpRequest);
		
		if(metadata){
			flushMetadataResponse(httpRequest, httpResponse);
		} else {
			if(doLogin(domain, user, password)){
				domainId = searchDomainId(domain);
				processRequest(httpRequest, httpResponse);
			} else {
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}
		
	}
	
	private void flushMetadataResponse(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		String xml = "<metadata>";
		xml += "<LocalAddr>"+httpRequest.getLocalAddr()+"</LocalAddr>";
		xml += "<LocalPort>"+httpRequest.getLocalPort()+"</LocalPort>";
		xml += "<PathInfo>"+httpRequest.getPathInfo()+"</PathInfo>";
		xml += "<RemoteAddr>"+httpRequest.getRemoteAddr()+"</RemoteAddr>";
		xml += "<RemoteHost>"+httpRequest.getRemoteHost()+"</RemoteHost>";
		xml += "<ServerName>"+httpRequest.getServerName()+"</ServerName>";
		xml += "<ServerPort>"+httpRequest.getServerPort()+"</ServerPort>";
		xml += "<Protocol>"+httpRequest.getProtocol()+"</Protocol>";
		xml += "<RequestURI>"+httpRequest.getRequestURI()+"</RequestURI>";
		xml += "<RequestURL>"+httpRequest.getRequestURL()+"</RequestURL>";
		xml += "<Scheme>"+httpRequest.getScheme()+"</Scheme>";
		xml += "<host>"+httpRequest.getHeader("host")+"</host>";
		xml += "</metadata>";

		System.out.print("local "+httpRequest.getLocalPort());
		System.out.print(" | server "+httpRequest.getServerPort());
		System.out.print(" | protocol "+httpRequest.getProtocol());
		System.out.print(" | url "+httpRequest.getRequestURL());
		System.out.print(" | scheme "+httpRequest.getScheme());
		System.out.println(" | host "+httpRequest.getHeader("host"));

		httpResponse.setContentType("application/xml");
		PrintWriter out = httpResponse.getWriter();
		out.print(xml);
		out.flush();
	}

	protected boolean doLogin(String domainName, String username,
			String password) {
		LOGGER.info("***** INGENET LOGIN: " + username + "@" + domainName
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
		String _metadata = httpRequest.getParameter(PARAM_METADATA);
		
		scheme = httpRequest.getScheme();
		devEnabled = false;
		// TODO check devEnabled 
		devEnabled = httpRequest.getServerPort()==8080;
		scheme = devEnabled?"http":"https";
		
		user = _username;
		password = _password;
		domain = _domainName;
//		if (_domainName != null) {
//			domainId = searchDomainId(_domainName);
//		}
		metadata = new Boolean(_metadata);
		
	}
	
	private Integer searchDomainId(String _domainName) {
		CloseableAONContext ctx =null;
		try {
			ctx = AONContext.getAONContext(getDomain(), -1, getUser());
			Domain domain = DomainDAO.getDomain(ctx, p -> {
				return p.getNameProperty().eq(_domainName);
			});
			return domain.getId();
		} finally {
			if (ctx != null)
				ctx.close();
		}
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
	
	protected void validateConsultaPedidosXmlPattern(InputStream xmlStream)
			throws IOException, SAXException {
		IngenetXmlValidator.validateXmlPattern(xmlStream,
				IngenetXmlValidator.SCHEMA_FILE_NAME_CONSULTA_PEDIDOS);
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
		LinkedList<MailAccount> list = AON.getMailAccountList(getDomain(), getDomainId(), getUser(), 
				f -> f.getDomainProperty().eq(0));
		return list!=null && !list.isEmpty()?list.getFirst():null;
	}
	
	protected void sendEmail(IngenetLogLevel logLevel, String subject, String content, String attachName, String attachValue, String... recipients) {
		try {
			File file = null;
			if(!AonStringUtils.isBlank(attachValue)) {
				file = File.createTempFile(attachName, ".xml");
				AonFileUtils.writeByteArrayToFile(file, attachValue.getBytes());
			}
		
			SESMessage sesMessage = new SESMessage()
				.setAlias("Aon Solutions Dev")
				.setFrom("dev@aon.solutions")
				.setBody(content)
				.setFile(file);
			
			if (isDevEnabled()) {
				sesMessage.setTo("aibanez@aonsolutions.es");
				sesMessage.setSubject("[AON/Test-"+logLevel+"] " + subject);
				LOGGER.info("*** RUNNING TEST ENVIRONMENT, AVOID SPAM RECIPIENTS TO.");
			} else {
				sesMessage.setTo(Arrays.asList(recipients));
				sesMessage.setSubject("[AON-"+logLevel+"] " + subject);
			}
		
			SES.sendEmail(sesMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void saveToDisk2(String folder, String namePrefix, String value) {
		byte data[] = (value != null ? value : "").getBytes();
		Path file = Paths.get(
				"/var",
				"tmp",
				"ingenet",
				folder,
				namePrefix
						+ "_"
						+ new SimpleDateFormat("yyyyMMdd-hhmmss")
								.format(new Date()) + ".xml");
		try {
			Path parentDir = file.getParent();
			if (!Files.exists(parentDir))
				Files.createDirectories(parentDir);
			Files.write(file, data, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			LOGGER.error("Error guardando el fichero recibido: ",
					e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Error guardando el fichero recibido: ",
					e.getMessage());
		}
	}
	
	protected void log(IngenetLogLevel logLevel, String subject, String content, String fileName,
			String fileValue, String... recipients) {
		log(null, logLevel, subject, content, fileName, fileValue, recipients);
	}
	
	protected void log(String folder, IngenetLogLevel logLevel, String subject,
			String content, String fileName, String fileValue, String... recipients) {
		
		if(IngenetLogLevel.DEBUG==logLevel)
			LOGGER.debug(content);
		else if(IngenetLogLevel.WARN==logLevel)
			LOGGER.warn(content);
		else if(IngenetLogLevel.ERROR==logLevel)
			LOGGER.error(content);
		
		sendEmail(logLevel, subject, content, fileName, fileValue, recipients);
		if(folder!=null){
			saveToDisk2(folder, fileName, fileValue);
		}
	}
	
	public enum IngenetLogLevel {
		DEBUG, INFO, WARN, ERROR;
	}
	
}
