package com.esferalia.aon.gwt.fiscal.server.fiscal;

import static com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO.getDataResponseData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.server.fiscal.aeat.RespuestaCorrecta;
import com.esferalia.aon.gwt.fiscal.server.fiscal.aeat.ServicioConsultasDirectas;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.occam.server.fiscal.format.Mod111Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod123Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod131Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod202Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod303Writer;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class ModelAdmonUtils {
	
	private static final String ERROR_TEMPLATE_START = "<html>"
			+"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>"
			+"<body>";
	private static final String ERROR_TEMPLATE_AEAT = "<div style=\""
				+"font-family: arial, 'lucida Grande', 'Trebuchet MS', sans-serif;"
				+"font-weight: bold;"
				+"margin-top: 20px;"
			+"\">"
			+ "La Agencia Tributaria devolvió el siguiente mensaje:"
			+"</div>";
			
	private static final String ERROR_TEMPLATE_BEFORE = "<html>"
			+"<ul style=\""
				 +"background-attachment: scroll;"
				 +"background-clip: border-box;"
				 +"background-position: 3px 2px;"
				 +"background-repeat: no-repeat;"
				 +"background-size: auto auto;"
				 +"background-color: #ffd0d0;"
				 +"border: solid black 1px;"
				 +"font-size: small;"
				 +"font-family: arial, 'lucida Grande', 'Trebuchet MS', sans-serif;"
				 +"font-weight: bold;"
				 +"border: solid black 1px;"
				 +"padding-top: 20px;"
				 +"padding-bottom: 20px;"
			+"\">";
	private static final String ERROR_TEMPLATE_BODY = "<li>{0}</li>";
	private static final String ERROR_TEMPLATE_AFTER = "</ul>";
	private static final String ERROR_TEMPLATE_END = "</body></html>";

	private ModelAdmonUtils() {
		
	}
	
	public static String getHeader(HttpResponse<byte[]> response, String header) {
		HttpHeaders headers = response.headers();
		List<String> headerList =  headers.map().get(header);
		if (headerList != null && !headerList.isEmpty() ) {
			return headerList.get(0);
		}
		return null;
	}
	
	public static String getContentTypeHeader(HttpResponse<byte[]> response) {
		return getHeader(response, AonHttpUtils.CONTENT_TYPE);
	}
	public static String getLocationHeader(HttpResponse<byte[]> response) {
		return getHeader(response, AonHttpUtils.LOCATION);
	}
	
	public static AEATParams getAEATParams(HttpServletRequest req) {
		String aeatParamsString = req.getParameter(IRequestParamsNames.AEAT_PARAMS);
		if (AonStringUtils.isBlank(aeatParamsString)) {
			throw new AonCoreException("[INT] No se han indicado par\u00C1metros.");	
		}
		AEATParams aeatParams = null;
		try {
			aeatParams = JsonParser.parseAEATParams(aeatParamsString);
		} catch (ParseException e) {
			throw new AonCoreException(MessageFormat.format("[INT] Error en la evaluaci\u00F3n de los par\u00C1metros {0}", e.getMessage()));	
		}
		return aeatParams; 
	}
	
	public static int getFiscalModelId(HttpServletRequest req) {
		return getFiscalModelId(getAEATParams(req)); 
	}

	public static int getFiscalModelId(AEATParams aeatParams) {
		if (aeatParams == null) {
			throw new AonCoreException("[INT] Error en la evaluación de los parámetros (VACIO)");	
		}
		if (aeatParams.getDomainName()  == null) {
			throw new AonCoreException("[INT] Nombre de dominio no indicado.");	
		}
		if (aeatParams.getDomainId() == 0) {
			throw new AonCoreException("[INT] Identificador de dominio no indicado.");	
		}
		if (aeatParams.getUser()  == null) {
			throw new AonCoreException("[INT] Usuario no indicado.");	
		}
		if (aeatParams.getMod() == null) {
			throw new AonCoreException("[INT] Identificador de modelo no indicado.");	
		}
		return aeatParams.getMod();
	}

	public static synchronized void giveRedirectBack(HttpServletResponse resp, HttpResponse<byte[]> response, HttpClient httpClient) throws IOException, InterruptedException {
		String locationHeader = ModelAdmonUtils.getLocationHeader(response); 
		if (AonStringUtils.isBlank( locationHeader)) {
			ModelAdmonUtils.giveExceptionBack(resp, "Redirect code");
		} else {
			ModelAdmonUtils.giveRedirectBack(httpClient, resp, locationHeader, MimeType.HTML);
		}
	}

	private static synchronized  void giveRedirectBack( HttpClient httpClient, HttpServletResponse resp, String location, MimeType mimeType) throws IOException, InterruptedException {
		HttpRequest locationRequest = HttpRequest.newBuilder()
				.uri(URI.create( location ))
				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")
				.GET()
				.build();
		HttpResponse<byte[]> locationResponse = httpClient
				.send(locationRequest, HttpResponse.BodyHandlers.ofByteArray());
		ModelAdmonUtils.giveBase64Back(resp, locationResponse.body(), mimeType);
	}

	public static synchronized  void giveExceptionBack( HttpServletResponse resp, String ... msgs)  {
		giveExceptionBack(resp, false, msgs); 	
	}
	public static synchronized  void giveExceptionBack( HttpServletResponse resp, boolean fromAEAT, String ... msgs)  {
		StringBuilder buff = new StringBuilder();
		buff.append(ERROR_TEMPLATE_START);
		if (fromAEAT) {
			buff.append(ERROR_TEMPLATE_AEAT);	
		}
		buff.append(ERROR_TEMPLATE_BEFORE);
		for (String msg : msgs) {
			if ("keystore password was incorrect".equals(msg) ){
				msg = "La contraseña no es correcta.";	
			}
			buff.append(MessageFormat.format(ERROR_TEMPLATE_BODY, msg));
		}
		buff.append(ERROR_TEMPLATE_AFTER);
		buff.append(ERROR_TEMPLATE_END);
		giveBase64Back(resp, buff.toString().getBytes(StandardCharsets.UTF_8), MimeType.HTML);
	}
	 
	
	public static synchronized KeyManager[] getKeyManagers(AEATParams params) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException{
		Attach attach = AON.getAttach(params.getDomainName(), params.getDomainId(), params.getUser(), f ->
			f.getIdProperty().eq(params.getCertificateId())
			.and(f.getTypeProperty().eq(RegistryAttachmentType.DIGITAL_CERTIFICATE.value())), AttachType.REGISTRY, true);
		if(attach.getData() == null){
			DomainGserviceaccount g = AON.getDomainGserviceaccount(params.getDomainName(), params.getDomainId(), params.getUser());
			Drive drive = AonDrive.getInstace().serviceInitialize(g);
			attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
		}
		ByteArrayInputStream key = new ByteArrayInputStream(attach.getData());
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
	    keyStore.load(key, params.getPass().toCharArray());
    	KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   		kmf.init(keyStore, params.getPass().toCharArray());
   		return kmf.getKeyManagers();
	}
    
	public static synchronized  void giveBase64Back( HttpServletResponse resp, byte[] data, MimeType mimeType )  {
		try {
			resp.setHeader(AonHttpUtils.CONTENT_TYPE, mimeType.getName());
			resp.setHeader(AonHttpUtils.CONTENT_ENCODING, StandardCharsets.UTF_8.displayName());				
			AonIOUtils.write( Base64.getEncoder().encode(data), resp.getOutputStream() );
			resp.flushBuffer();
		} catch (IOException e) {
			throw new AonCoreException(MessageFormat.format("Unexpected exception [{0}] ", e.getMessage()));	
		}
	}
	
	public static synchronized String getUnencodedFile(byte[] content, Charset charset) {
		return changeCharacters(new String(content, charset));
	}

	public static synchronized String getEncodedFile(byte[] content, Charset charset) throws UnsupportedEncodingException {
		String fileString = changeCharacters(new String(content, charset));
		return URLEncoder.encode(fileString, charset.displayName());
	}
	
	private static String changeCharacters(String fileString) {
		fileString = fileString.replace("'", " ");
		fileString = fileString.replace("&", " ");
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		return fileString;
	}
	
	public static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private static AEATResponse manageWrongResponse(HttpServletResponse resp, AEATResponse response) {
		if (response.getErrores() == null || response.getErrores().isEmpty()) {
			ModelAdmonUtils.giveExceptionBack(resp,"La Agencia Tributaria ha devuelto un error, pero no se han encontrado mensajes del mismo.");			
		} else {
			String[] array = response.getErrores().toArray(new String[0]);
			ModelAdmonUtils.giveExceptionBack(resp, true, array);
		}
		return response;
	}

	public static synchronized  void giveDataResponseDataBack( HttpServletResponse resp, AEATParams params, FiscalModel model)  {
		Pair<DataResponseSource,DataAttachSource> pair = getDataResponseData( model );
		Attach attach = AON.getAttach(params.getDomainName(), params.getDomainId(), params.getUser(), 
				f -> f.getDomainProperty().eq( model.getDomain())
				.and(f.getSourceTypeProperty().eq( pair.getRight().value() ))
				.and(f.getSourceBatchProperty().eq( model.getId() ))
				,AttachType.DATA);
		if (attach == null || attach.getData() == null || attach.getData().length == 0) {
			ModelAdmonUtils.giveExceptionBack(resp, "Declaración no encontrada" );				
		} else {
			boolean pdfContentType = MimeType.PDF == attach.getMimeType(); 
			ModelAdmonUtils.giveBase64Back(resp, attach.getData(), (pdfContentType?MimeType.PDF:MimeType.HTML));
		}
	}
	
	public static StringBuilder formatRespuestaCorrecta(ServicioConsultasDirectas scd) {
		StringBuilder buff = new StringBuilder();
		buff.append("<html>");
		buff.append("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>");
		buff.append("<style>");
		buff.append("#aeat {");
		buff.append(" margin: 10px;");
		buff.append(" padding: 10px;");
		buff.append(" border: #c4c4c4 1px solid;");
		buff.append(" font: 12px \"arial\", \"lucida Grande\", \"Trebuchet MS\", sans-serif;");
		buff.append(" text-align: center;");
		buff.append(" font-weight: bold;");
		buff.append("}");
		buff.append("#response {");
		buff.append(" font: 12px/1.333 \"arial\", \"lucida Grande\", \"Trebuchet MS\", sans-serif;");
		buff.append(" margin-left: auto;");
		buff.append(" margin-right: auto;");
		buff.append(" border-collapse: collapse;");
		buff.append(" width: 80%;");
		buff.append("}");
		buff.append("#response td {");
		buff.append(" padding: 1px 0.5em 1px 0.5em;");
		buff.append(" vertical-align: middle;");
		buff.append(" border: #c4c4c4 1px solid;");
		buff.append("}");
		buff.append("#label {");
		buff.append(" font-weight: bold;");
		buff.append(" white-space: nowrap;");
		buff.append(" width: 10%;");
		buff.append(" background-color: AliceBlue;");
		buff.append("}");
		buff.append("</style>");
		buff.append("<body>");
		buff.append("<div id=\"aeat\">La Agencia Tributaria devolvió el siguiente mensaje:</div>");
		buff.append("<table id=\"response\">");
		String labelTD = "<tr><td id=\"label\">{0}</td>"; 
		String valueTD = "<td>{0}</td></tr>";
		String valueTD2 = "<td>{0, date, DD-MM-YYYY hh:mm:ss}</td></tr>";
		
		for (RespuestaCorrecta rc : scd.getRespuestaCorrecta()) {
			buff.append(MessageFormat.format(labelTD,"Ejercicio"));
			buff.append(MessageFormat.format(valueTD, rc.getEjercicio()));
			buff.append(MessageFormat.format(labelTD,"Modelo"));
			buff.append(MessageFormat.format(valueTD, rc.getModelo()));
			buff.append(MessageFormat.format(labelTD,"Periodo"));
			buff.append(MessageFormat.format(valueTD, rc.getPeriodo()));
			buff.append(MessageFormat.format(labelTD,"NIF"));
			buff.append(MessageFormat.format(valueTD, rc.getNif()));
			buff.append(MessageFormat.format(labelTD,"CSV"));
			buff.append(MessageFormat.format(valueTD, rc.getCsv()));
			buff.append(MessageFormat.format(labelTD,"Expediente"));
			buff.append(MessageFormat.format(valueTD, rc.getExpediente()));
			buff.append(MessageFormat.format(labelTD,"Justificante"));
			buff.append(MessageFormat.format(valueTD, rc.getJustificante()));
			if (AonStringUtils.isNotBlank(rc.getJustAnterior())) {
				buff.append(MessageFormat.format(labelTD,"Justificante anterior"));
				buff.append(MessageFormat.format(valueTD, rc.getJustAnterior()));
			}
			if (rc.getFechaYHoraPresentacion() != null) {
				buff.append(MessageFormat.format(labelTD,"Fecha y hora de presentación"));
				buff.append(MessageFormat.format(valueTD2, rc.getFechaYHoraPresentacion().toGregorianCalendar().getTime()));
			}
		}
		buff.append("</table>");
		buff.append("</body></html>");
		return buff;
	}
	
	/*
	 *  POSIBLE ENUM !!! 	
	 */
	public static byte[] getModelFile(Mod303 mod303) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		Mod303Writer.fillWriter(mod303, writer);
		return output.toByteArray();
	}
	public static byte[] getModelFile(Mod111 mod111) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		Mod111Writer.fillWriter(mod111, writer);
		return output.toByteArray();
	}
	public static byte[] getModelFile(Mod115 mod115) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		Mod115Writer.fillWriter(mod115, writer);
		return output.toByteArray();
	}
	public static byte[] getModelFile(Mod123 mod123) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		Mod123Writer.fillWriter(mod123, writer);
		return output.toByteArray();
	}
	public static byte[] getModelFile(Mod130 mod130) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		Mod130Writer.fillWriter(mod130, writer);
		return output.toByteArray();
	}
	public static byte[] getModelFile(Mod131 mod131) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		Mod131Writer.fillWriter(mod131, writer);
		return output.toByteArray();
	}
	public static byte[] getModelFile(Mod202 mod202) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		Mod202Writer.fillWriter(mod202, writer);
		return output.toByteArray();
	}
	
	public static void manageJSONContent(HttpServletResponse resp, AEATParams aeatParams, Mod303 mod303, byte[] body) {
		AEATResponse response = AEATJson.toJSON(body); 
		if (response.isCorrect()) {
			manageRightResponse(resp,aeatParams,mod303,new String(body));		
		} else {
			manageWrongResponse(resp, response);
		}
	}
	private static void manageRightResponse(HttpServletResponse resp, AEATParams aeatParams, Mod303 mod303, String aeatResponse) {
		Occam occam = new Occam()
				.setDomainName(aeatParams.getDomainName())
				.setDomain(aeatParams.getDomainId())
				.setUser(aeatParams.getUser());
		mod303 = MODEL303.aeatPresentationMod303(occam, mod303, aeatResponse);
		giveDataResponseDataBack(resp, aeatParams, mod303);
	}

	public static void manageJSONContent(HttpServletResponse resp, AEATParams aeatParams, Mod111 mod111, byte[] body) {
		AEATResponse response = AEATJson.toJSON(body); 
		if (response.isCorrect()) {
			manageRightResponse(resp,aeatParams,mod111,new String(body));		
		} else {
			manageWrongResponse(resp, response);
		}
	}
	private static void manageRightResponse(HttpServletResponse resp, AEATParams aeatParams, Mod111 mod111, String aeatResponse) {
		Occam occam = new Occam()
				.setDomainName(aeatParams.getDomainName())
				.setDomain(aeatParams.getDomainId())
				.setUser(aeatParams.getUser());
		mod111 = MODEL111.aeatPresentation(occam, mod111, aeatResponse);
		giveDataResponseDataBack(resp, aeatParams, mod111);
	}

	public static void manageJSONContent(HttpServletResponse resp, AEATParams aeatParams, Mod115 mod115, byte[] body) {
		AEATResponse response = AEATJson.toJSON(body); 
		if (response.isCorrect()) {
			manageRightResponse(resp,aeatParams,mod115,new String(body));		
		} else {
			manageWrongResponse(resp, response);
		}
	}
	private static void manageRightResponse(HttpServletResponse resp, AEATParams aeatParams, Mod115 mod115, String aeatResponse) {
		Occam occam = new Occam()
				.setDomainName(aeatParams.getDomainName())
				.setDomain(aeatParams.getDomainId())
				.setUser(aeatParams.getUser());
		mod115 = MODEL115.aeatPresentationMod115(occam, mod115, aeatResponse);
		giveDataResponseDataBack(resp, aeatParams, mod115);
	}
	
	public static void manageJSONContent(HttpServletResponse resp, AEATParams aeatParams, Mod123 mod123, byte[] body) {
		AEATResponse response = AEATJson.toJSON(body); 
		if (response.isCorrect()) {
			manageRightResponse(resp,aeatParams,mod123,new String(body));		
		} else {
			manageWrongResponse(resp, response);
		}
	}
	private static void manageRightResponse(HttpServletResponse resp, AEATParams aeatParams, Mod123 mod123, String aeatResponse) {
		Occam occam = new Occam()
				.setDomainName(aeatParams.getDomainName())
				.setDomain(aeatParams.getDomainId())
				.setUser(aeatParams.getUser());
		mod123 = MODEL123.aeatPresentation(occam, mod123, aeatResponse);
		giveDataResponseDataBack(resp, aeatParams, mod123);
	}

	public static void manageJSONContent(HttpServletResponse resp, AEATParams aeatParams, Mod130 mod130, byte[] body) {
		AEATResponse response = AEATJson.toJSON(body); 
		if (response.isCorrect()) {
			manageRightResponse(resp,aeatParams,mod130,new String(body));		
		} else {
			manageWrongResponse(resp, response);
		}
	}
	private static void manageRightResponse(HttpServletResponse resp, AEATParams aeatParams, Mod130 mod130, String aeatResponse) {
		Occam occam = new Occam()
				.setDomainName(aeatParams.getDomainName())
				.setDomain(aeatParams.getDomainId())
				.setUser(aeatParams.getUser());
		mod130 = MODEL130.aeatPresentation(occam, mod130, aeatResponse);
		giveDataResponseDataBack(resp, aeatParams, mod130);
	}
	
	public static void manageJSONContent(HttpServletResponse resp, AEATParams aeatParams, Mod131 mod131, byte[] body) {
		AEATResponse response = AEATJson.toJSON(body); 
		if (response.isCorrect()) {
			manageRightResponse(resp,aeatParams,mod131,new String(body));		
		} else {
			manageWrongResponse(resp, response);
		}
	}
	private static void manageRightResponse(HttpServletResponse resp, AEATParams aeatParams, Mod131 mod131, String aeatResponse) {
		Occam occam = new Occam()
				.setDomainName(aeatParams.getDomainName())
				.setDomain(aeatParams.getDomainId())
				.setUser(aeatParams.getUser());
		mod131 = MODEL131.aeatPresentation(occam, mod131, aeatResponse);
		giveDataResponseDataBack(resp, aeatParams, mod131);
	}
	
	public static void manageJSONContent(HttpServletResponse resp, AEATParams aeatParams, Mod202 mod202, byte[] body) {
		AEATResponse response = AEATJson.toJSON(body); 
		if (response.isCorrect()) {
			manageRightResponse(resp,aeatParams,mod202,new String(body));		
		} else {
			manageWrongResponse(resp, response);
		}
	}
	private static void manageRightResponse(HttpServletResponse resp, AEATParams aeatParams, Mod202 mod202, String aeatResponse) {
		Occam occam = new Occam()
				.setDomainName(aeatParams.getDomainName())
				.setDomain(aeatParams.getDomainId())
				.setUser(aeatParams.getUser());
		mod202 = MODEL202.aeatPresentation(occam, mod202, aeatResponse);
		giveDataResponseDataBack(resp, aeatParams, mod202);
	}
	
}
