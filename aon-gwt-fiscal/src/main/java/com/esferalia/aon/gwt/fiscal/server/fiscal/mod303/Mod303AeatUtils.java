package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

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
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.occam.server.fiscal.format.Mod303Writer;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

class Mod303AeatUtils {
	
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

	private Mod303AeatUtils() {
		
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
	
	public static Mod303 getMod303(HttpServletRequest req) {
		return getMod303(getAEATParams(req)); 
	}

	public static Mod303 getMod303(AEATParams aeatParams) {
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
		Mod303 mod303 = FISCAL.getMod303(aeatParams.getDomainName(), aeatParams.getDomainId(), aeatParams.getUser(), aeatParams.getMod());
		if (mod303 == null) {
			throw new AonCoreException("[INT] Modelo no encontrado");
		}
		return mod303; 
	}

	protected static synchronized void giveRedirectBack(HttpServletResponse resp, HttpResponse<byte[]> response, HttpClient httpClient) throws IOException, InterruptedException {
		String locationHeader = Mod303AeatUtils.getLocationHeader(response); 
		if (AonStringUtils.isBlank( locationHeader)) {
			Mod303AeatUtils.giveExceptionBack(resp, "Redirect code");
		} else {
			Mod303AeatUtils.giveRedirectBack(httpClient, resp, locationHeader, MimeType.HTML);
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
		Mod303AeatUtils.giveBase64Back(resp, locationResponse.body(), mimeType);
	}

	protected static synchronized  void giveExceptionBack( HttpServletResponse resp, String ... msgs)  {
		giveExceptionBack(resp, false, msgs); 	
	}
	protected static synchronized  void giveExceptionBack( HttpServletResponse resp, boolean fromAEAT, String ... msgs)  {
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
	 
	
	protected static synchronized KeyManager[] getKeyManagers(AEATParams params) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException{
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
    
	protected static synchronized  void giveBase64Back( HttpServletResponse resp, byte[] data, MimeType mimeType )  {
		try {
			resp.setHeader(AonHttpUtils.CONTENT_TYPE, mimeType.getName());
			resp.setHeader(AonHttpUtils.CONTENT_ENCODING, StandardCharsets.UTF_8.displayName());				
			AonIOUtils.write( Base64.getEncoder().encode(data), resp.getOutputStream() );
			resp.flushBuffer();
		} catch (IOException e) {
			throw new AonCoreException(MessageFormat.format("Unexpected exception [{0}] ", e.getMessage()));	
		}
	}
	
	protected static synchronized String getUnencodedFile(byte[] content, Charset charset) {
		return changeCharacters(new String(content, charset));
	}

	protected static synchronized String getEncodedFile(byte[] content, Charset charset) throws UnsupportedEncodingException {
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
	
	protected static class DefaultTrustManager implements X509TrustManager {

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

	protected static byte[] getModelFile(Mod303 mod303) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		Mod303Writer.fillWriter(mod303, writer);
		return output.toByteArray();
	}
	
	protected static void manageJSONContent(HttpServletResponse resp, HttpClient httpClient, AEATParams aeatParams, Mod303 mod303, byte[] body) throws IOException {
		AEATResponse response = AEATJson.toJSON(body); 
		if (response.isCorrect()) {
			manageRightResponse(resp,httpClient,aeatParams,mod303,new String(body));		
		} else {
			manageWrongResponse(resp, response);
		}
	}

	private static AEATResponse manageWrongResponse(HttpServletResponse resp, AEATResponse response) throws IOException {
		if (response.getErrores() == null || response.getErrores().isEmpty()) {
			Mod303AeatUtils.giveExceptionBack(resp,"La Agencia Tributaria ha devuelto un error, pero no se han encontrado mensajes del mismo.");			
		} else {
			String[] array = response.getErrores().toArray(new String[0]);
			Mod303AeatUtils.giveExceptionBack(resp, true, array);
		}
		return response;
	}

	protected static synchronized  void giveDataResponseDataBack( HttpServletResponse resp, AEATParams params)  {
		Attach attach = AON.getAttach(params.getDomainName(), params.getDomainId(), params.getUser(), 
				f -> f.getDomainProperty().eq( params.getDomainId())
				.and(f.getSourceTypeProperty().eq( DataAttachSource.MOD303.value() ))
				.and(f.getSourceBatchProperty().eq( params.getMod() ))
				,AttachType.DATA);
		if (attach == null || attach.getData() == null || attach.getData().length == 0) {
			Mod303AeatUtils.giveExceptionBack(resp, "Declaración no encontrada" );				
		} else {
			boolean pdfContentType = MimeType.PDF == attach.getMimeType(); 
			Mod303AeatUtils.giveBase64Back(resp, attach.getData(), (pdfContentType?MimeType.PDF:MimeType.HTML));
		}
	}
	
	

	private static void manageRightResponse(HttpServletResponse resp, HttpClient httpClient, AEATParams aeatParams, Mod303 mod303, String aeatResponse) {
		FISCAL.aeatPresentationMod303(aeatParams.getDomainName(),aeatParams.getDomainId(),aeatParams.getUser(), mod303, aeatResponse);
		giveDataResponseDataBack(resp, aeatParams);
	}

}
