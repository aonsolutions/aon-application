package com.esferalia.aon.gwt.mod200.server;

import static com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO.getDataResponseData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jooq.tools.json.ParseException;
import org.json.JSONObject;

import com.esferalia.aon.gwt.mod200.server.aeat.RespuestaCorrecta;
import com.esferalia.aon.gwt.mod200.server.aeat.ServicioConsultasDirectas;
import com.esferalia.aon.gwt.mod200.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.mod200.api.MODEL2002022;
import com.esferalia.aon.occam.mod200.api.MODEL2002023;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.occam.mod200.server.format.mod200_2022.Mod2002022Writer;
import com.esferalia.aon.occam.mod200.server.format.mod200_2023.Mod2002023Writer;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.api.services.drive.Drive;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class Model200AdmonUtils {
	
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

	private Model200AdmonUtils() {
		
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
		String locationHeader = Model200AdmonUtils.getLocationHeader(response); 
		if (AonStringUtils.isBlank( locationHeader)) {
			Model200AdmonUtils.giveExceptionBack(resp, "Redirect code");
		} else {
			Model200AdmonUtils.giveRedirectBack(httpClient, resp, locationHeader, MimeType.HTML);
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
		Model200AdmonUtils.giveBase64Back(resp, locationResponse.body(), mimeType);
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
		if(AonStringUtils.isBlank(params.getPass())) {
			params.setPass(attach.getDescription().split("HIDE\\(")[1].split("\\)")[0]);
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
			Model200AdmonUtils.giveExceptionBack(resp,"La Agencia Tributaria ha devuelto un error, pero no se han encontrado mensajes del mismo.");			
		} else {
			String[] array = response.getErrores().toArray(new String[0]);
			Model200AdmonUtils.giveExceptionBack(resp, true, array);
		}
		return response;
	}

	public static synchronized  void giveDataResponseDataBack( HttpServletResponse resp, AEATParams params, IFiscalModel model)  {
		Pair<DataResponseSource,DataAttachSource> pair = getDataResponseData( model );
		Attach attach = AON.getAttach(params.getDomainName(), params.getDomainId(), params.getUser(), 
				f -> f.getDomainProperty().eq( model.getDomain())
				.and(f.getSourceTypeProperty().eq( pair.getRight().value() ))
				.and(f.getSourceBatchProperty().eq( model.getId() ))
				,AttachType.DATA);
		if (attach == null || attach.getData() == null || attach.getData().length == 0) {
			Model200AdmonUtils.giveExceptionBack(resp, "Declaración no encontrada" );				
		} else {
			boolean pdfContentType = MimeType.PDF == attach.getMimeType(); 
			Model200AdmonUtils.giveBase64Back(resp, attach.getData(), (pdfContentType?MimeType.PDF:MimeType.HTML));
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
		buff.append(" margin-bottom: 10px;");
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
		//buff.append("<table id=\"response\">");
		
		String labelTD = "<tr><td id=\"label\">{0}</td>"; 
		String valueTD = "<td>{0}</td></tr>";
		String valueTD2 = "<td>{0, date, dd/MM/YYYY HH:mm:ss}</td></tr>";
		
		for (RespuestaCorrecta rc : scd.getRespuestaCorrecta()) {
			buff.append("<table id=\"response\">");
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
			buff.append("</table>");
		}
		//buff.append("</table>");
		buff.append("</body></html>");
		return buff;
	}
	
	private static byte[] getModelFile(IFiscalModel fm) throws AonCoreException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		try {
			if (fm instanceof Mod2002022) {
				Mod2002022 mod = (Mod2002022) fm;
				Mod2002022Writer.fillWriter(mod, writer);
			}
			if (fm instanceof Mod2002023) {
				Mod2002023 mod = (Mod2002023) fm;
				Mod2002023Writer.fillWriter(mod, writer);
			}
		} catch (IOException e) {
			throw new AonCoreException(e);
		}
		return output.toByteArray();
	}

	public static void manageJSONContent(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel fm, byte[] body) {
		AEATResponse response = AEATJson.toJSON(body); 
		if (response.isCorrect()) {
			manageRightResponse(resp,aeatParams,fm,new String(body));		
		} else {
			manageWrongResponse(resp, response);
		}
	}
	private static void manageRightResponse(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel fm, String aeatResponse) {
		Occam occam = new Occam()
				.setDomainName(aeatParams.getDomainName())
				.setDomain(aeatParams.getDomainId())
				.setUser(aeatParams.getUser());
		if (fm instanceof Mod2002022) {
			Mod2002022 mod = (Mod2002022) fm;
			MODEL2002022.aeatPresentation(occam, mod, aeatResponse);
		}
		if (fm instanceof Mod2002023) {
			Mod2002023 mod = (Mod2002023) fm;
			MODEL2002023.aeatPresentation(occam, mod, aeatResponse);
		}
		giveDataResponseDataBack(resp, aeatParams, fm);
	}
	
	public static void send(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel model) {
		try {
			String period = "0A";
			byte[] fileContent = getModelFile(model);
			JSONObject params = new JSONObject();
			params.put("MODELO", FiscalModelUtils.getModelName(model));
			params.put("EJERCICIO", AonNumberUtils.toString( model.getYear()));
			params.put("PERIODO", period);
			params.put("NRC", (model.isStrictToDeposit()?aeatParams.getNrc() : ""));
			params.put("IDI", "ES");
			params.put("F01", Model200AdmonUtils.getUnencodedFile(fileContent,StandardCharsets.UTF_8));
			params.put("FIR", "FirmaBasica");
			params.put("FIRNIF", aeatParams.getDocument());
			params.put("FIRNOMBRE", aeatParams.getName());
			
			String url = aeatParams.isTest() 
				? "https://prewww1.aeat.es/wlpl/PFTW-PICW/PresBasicaDos"
				: "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasicaDos";

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init( Model200AdmonUtils.getKeyManagers(aeatParams),
					new TrustManager[] { new Model200AdmonUtils.DefaultTrustManager() },
					new SecureRandom());
			HttpClient httpClient = HttpClient.newBuilder()
		            .version(HttpClient.Version.HTTP_2)
		            .connectTimeout(Duration.ofSeconds(120))
		            .sslContext(sslContext)
		            .build();

			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create( url ))
				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/json;charset=UTF-8")
				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")
				.POST(HttpRequest.BodyPublishers.ofString(params.toString()))
				.build();
			HttpResponse<byte[]> response = httpClient
				.send(request, HttpResponse.BodyHandlers.ofByteArray());
			
			if (response.statusCode() == 302) {
				Model200AdmonUtils.giveRedirectBack( resp,response,httpClient );
			} else {
				String ct = Model200AdmonUtils.getContentTypeHeader(response);
				if (AonStringUtils.contains(ct, MimeType.JSON.getName())) {
					Model200AdmonUtils.manageJSONContent( resp, aeatParams, model ,response.body() );
				} else if (AonStringUtils.contains(ct, MimeType.HTML.getName())) {
					Model200AdmonUtils.giveBase64Back(resp, response.body(), MimeType.HTML);
				} else {	
					Model200AdmonUtils.giveExceptionBack(resp,"No se ha encontrado una respuesta válida por parte de la Agencia Tributaria.");
				}
			}
		} catch (InterruptedException e) {
			// Restore interrupted state...
			Thread.currentThread().interrupt();
		} catch (AonCoreException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			Model200AdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

	public static void checkAEAT(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel model) {
		try {
			String year = AonNumberUtils.toString(model.getYear());
			String period = "0A";
			
			String urlParameters = MessageFormat.format(
					"NIF={0}"
					+"&ANR={1}"
					+"&MOD={2}"
					+"&EJF={3}"
					+"&PER={4}"
						,model.getDocument()
						,model.getFullName()
						,FiscalModelUtils.getModelName(model)
						,year
						,period
						);
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init( Model200AdmonUtils.getKeyManagers(aeatParams),
					new TrustManager[] { new Model200AdmonUtils.DefaultTrustManager() },
					new SecureRandom());
			HttpClient httpClient = HttpClient.newBuilder()
		            .version(HttpClient.Version.HTTP_2)
		            .connectTimeout(Duration.ofSeconds(120))
		            .sslContext(sslContext)
		            .build();
			
			String url = aeatParams.isTest() ? 
					"https://prewww1.aeat.es/wlpl/SCEJ-MANT/ConsultaExt" : 
					"https://www1.agenciatributaria.gob.es/wlpl/SCEJ-MANT/ConsultaExt";
			
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create( url ))
				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")
				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(urlParameters.toString()))
				.build();
			HttpResponse<byte[]> response = httpClient
				.send(request, HttpResponse.BodyHandlers.ofByteArray());
			
			StringReader reader = new  StringReader(new String(response.body()));
			JAXBContext context = JAXBContext.newInstance(ServicioConsultasDirectas.class);
			Unmarshaller um = context.createUnmarshaller();
			ServicioConsultasDirectas scd = (ServicioConsultasDirectas) um.unmarshal(reader);
			if ( scd.getError() != null) {
				Model200AdmonUtils.giveExceptionBack(resp,true,scd.getError().getDescripcionError());	
			} else if ( scd.getRespuestaCorrecta()  != null) {
				StringBuilder buff = Model200AdmonUtils.formatRespuestaCorrecta( scd);
				Model200AdmonUtils.giveBase64Back(resp, buff.toString().getBytes(), MimeType.HTML);
			} else{
				Model200AdmonUtils.giveExceptionBack(resp,"La Agencia Tributaria ha devuelto un mensaje, pero no se han encontrado mensajes en el mismo.");
			}
		} catch (InterruptedException e) {
			// Restore interrupted state...
			Thread.currentThread().interrupt();
		} catch (JAXBException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			Model200AdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
