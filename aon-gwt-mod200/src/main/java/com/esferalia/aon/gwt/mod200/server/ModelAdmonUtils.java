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
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
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
import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.fiscal.MODEL180;
import com.esferalia.aon.occam.api.fiscal.MODEL184;
import com.esferalia.aon.occam.api.fiscal.MODEL190;
import com.esferalia.aon.occam.api.fiscal.MODEL193;
import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.fiscal.MODEL347;
import com.esferalia.aon.occam.api.fiscal.MODEL349;
import com.esferalia.aon.occam.api.fiscal.MODEL3902021;
import com.esferalia.aon.occam.api.fiscal.MODEL3902022;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902021;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902022;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.server.format.mod200_2022.Mod2002022Writer;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod131Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod347Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod349Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod111.Mod111Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod115.Mod115Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod123.Mod123Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod180.Mod180Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod184.Mod184Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod190.Mod190Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod193.Mod193Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod202.Mod202Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod390.Mod3902021Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod390.Mod3902022Writer;
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
			ModelAdmonUtils.giveExceptionBack(resp,"La Agencia Tributaria ha devuelto un error, pero no se han encontrado mensajes del mismo.");			
		} else {
			String[] array = response.getErrores().toArray(new String[0]);
			ModelAdmonUtils.giveExceptionBack(resp, true, array);
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
	
	private static Mod111 getMod111(IFiscalModel fm) {
		return (fm instanceof Mod111)?(Mod111)fm:null;
	}
	private static Mod115 getMod115(IFiscalModel fm) {
		return (fm instanceof Mod115)?(Mod115)fm:null;
	}
	private static Mod123 getMod123(IFiscalModel fm) {
		return (fm instanceof Mod123)?(Mod123)fm:null;
	}
	private static Mod130 getMod130(IFiscalModel fm) {
		return (fm instanceof Mod130)?(Mod130)fm:null;
	}
	private static Mod131 getMod131(IFiscalModel fm) {
		return (fm instanceof Mod131)?(Mod131)fm:null;
	}
	private static Mod190 getMod190(IFiscalModel fm) {
		return (fm instanceof Mod190)?(Mod190)fm:null;
	}
	private static Mod202 getMod202(IFiscalModel fm) {
		return (fm instanceof Mod202)?(Mod202)fm:null;
	}
	private static Mod303 getMod303(IFiscalModel fm) {
		return (fm instanceof Mod303)?(Mod303)fm:null;
	}
	private static Mod180 getMod180(IFiscalModel fm) {
		return (fm instanceof Mod180)?(Mod180)fm:null;
	}
	private static Mod193 getMod193(IFiscalModel fm) {
		return (fm instanceof Mod193)?(Mod193)fm:null;
	}
	private static Mod184 getMod184(IFiscalModel fm) {
		return (fm instanceof Mod184)?(Mod184)fm:null;
	}
	private static Mod347 getMod347(IFiscalModel fm) {
		return (fm instanceof Mod347)?(Mod347)fm:null;
	}
	private static Mod349 getMod349(IFiscalModel fm) {
		return (fm instanceof Mod349)?(Mod349)fm:null;
	}
	
	private static byte[] getModelFile(IFiscalModel fm) throws AonCoreException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		fm.getModel().visit(new IFiscalModelTypeVisitor() {
			
			@Override 
			public void visitM111() {
				try {
					Mod111Writer.fillWriter( getMod111(fm) , writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			
			@Override public void visitM115() { 
				try {
					Mod115Writer.fillWriter( getMod115(fm), writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			@Override 
			public void visitM123() { 
				try {
					Mod123Writer.fillWriter( getMod123(fm) , writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			@Override 
			public void visitM130() { 
				try {
					Mod130Writer.fillWriter( getMod130(fm) , writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			@Override 
			public void visitM131() { 
				try {
					Mod131Writer.fillWriter( getMod131(fm) , writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			@Override 
			public void visitM202() { 
				try {
					Mod202Writer.fillWriter( getMod202(fm) , writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			@Override 
			public void visitM303() { 
				try {
					Mod303Writer.fillWriter( getMod303(fm) , writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			@Override 
			public void visitM190() { 
				try {
					Mod190Writer.fillWriter( getMod190(fm) , writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			
			@Override 
			public void visitM390() { 
				try {
					if (fm instanceof Mod3902022) {
						Mod3902022 mod = (Mod3902022) fm;
						Mod3902022Writer.fillWriter( mod , writer);
					} else  if (fm instanceof Mod3902021) {
						Mod3902021 mod = (Mod3902021) fm;
						Mod3902021Writer.fillWriter( mod , writer);
					}
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			@Override public void visitM390HF() { /* Auto-generated method stub */}
			@Override 
			public void visitM349() { 
				try {
					Mod349Writer.fillWriter( getMod349(fm), writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			
			@Override 
			public void visitM347() { 
				try {
					Mod347Writer.fillWriter( getMod347(fm), writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			
			@Override public void visitM200() { 
				try {
					if (fm instanceof Mod2002022) {
						Mod2002022 mod = (Mod2002022) fm;
						Mod2002022Writer.fillWriter( mod , writer);
					} 
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			
			@Override 
			public void visitM193() { 
				try {
					Mod193Writer.fillWriter( getMod193(fm), writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			
			@Override 
			public void visitM184() { 
				try {
					Mod184Writer.fillWriter( getMod184(fm), writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
			
			@Override 
			public void visitM180() {				
				try {
					Mod180Writer.fillWriter( getMod180(fm), writer);
				} catch (IOException e) {
					throw new AonCoreException(e);
				}
			}
		});
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
		fm.getModel().visit(new IFiscalModelTypeVisitor() {
			
			@Override 
			public void visitM111() {
				MODEL111.aeatPresentation(occam, getMod111(fm) , aeatResponse);
			}
			@Override 
			public void visitM115() { 
				MODEL115.aeatPresentation(occam, getMod115(fm) , aeatResponse);
			}
			@Override 
			public void visitM123() { 
				MODEL123.aeatPresentation(occam, getMod123(fm) , aeatResponse);
			}
			@Override 
			public void visitM130() { 
				MODEL130.aeatPresentation(occam, getMod130(fm) , aeatResponse);
			}
			@Override 
			public void visitM131() { 
				MODEL131.aeatPresentation(occam, getMod131(fm) , aeatResponse);
			}
			@Override 
			public void visitM202() { 
				MODEL202.aeatPresentation(occam, getMod202(fm) , aeatResponse);
			}
			@Override 
			public void visitM303() { 
				MODEL303.aeatPresentation(occam, getMod303(fm) , aeatResponse);
			}
			@Override 
			public void visitM390() { 
				if (fm instanceof Mod3902021) {
					Mod3902021 mod = (Mod3902021) fm;
					MODEL3902021.aeatPresentation(occam, mod , aeatResponse);
				}
				if (fm instanceof Mod3902022) {
					Mod3902022 mod = (Mod3902022) fm;
					MODEL3902022.aeatPresentation(occam, mod , aeatResponse);
				}
			}
			@Override public void visitM390HF() { /* Auto-generated method stub */}
			
			@Override 
			public void visitM349() {
				MODEL349.aeatPresentation(occam, getMod349(fm) , aeatResponse);
			}
			
			@Override 
			public void visitM347() { 
				MODEL347.aeatPresentation(occam, getMod347(fm) , aeatResponse);
			}
			
			// FALTA - LLAMADA PARA GRABAR RESPUESTA DE LA AEAT Y PONER ESTADO ENVIADO
			// SI ESTO LO LLEVAMOS A AON.GWT.COMMON TENDREMOS QUE HACERLO DEPENDER DE AON.OCCAM.MOD200
			@Override public void visitM200() {
				//MODEL2002022.aeatPresentation(occam, ((fm instanceof Mod2002022)?(Mod2002022)fm:null) , aeatResponse);				
			}
			
			@Override 
			public void visitM193() { 
				MODEL193.aeatPresentation(occam, getMod193(fm) , aeatResponse);				
			}
			
			@Override 
			public void visitM190() { 
				MODEL190.aeatPresentation(occam, getMod190(fm) , aeatResponse);
			}
			
			@Override 
			public void visitM184() { 
				MODEL184.aeatPresentation(occam, getMod184(fm) , aeatResponse);
			}
			
			@Override 
			public void visitM180() {
				MODEL180.aeatPresentation(occam, getMod180(fm) , aeatResponse);
			}
			
		});
		giveDataResponseDataBack(resp, aeatParams, fm);
	}
	
	public static void send(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel model) {
		try {
			String period = model.getPeriod().getName();
			if ( model.getModel() == FiscalModelType.M202) {
				if ( model.getPeriod() == Period.T1) period = "1P";
				else if ( model.getPeriod() == Period.T2) period = "2P";
				else if ( model.getPeriod() == Period.T3) period = "3P";
			}
			if (model.getModel() == FiscalModelType.M390 || model.getModel() == FiscalModelType.M200) {
				period = "0A";	
			}
			byte[] fileContent = getModelFile(model);
			JSONObject params = new JSONObject();
			params.put("MODELO", FiscalModelUtils.getModelName(model));
			params.put("EJERCICIO", AonNumberUtils.toString( model.getYear()));
			params.put("PERIODO", period);
			params.put("NRC", (model.isStrictToDeposit()?aeatParams.getNrc() : ""));
			params.put("IDI", "ES");
			params.put("F01", ModelAdmonUtils.getUnencodedFile(fileContent,StandardCharsets.UTF_8));
			params.put("FIR", "FirmaBasica");
			params.put("FIRNIF", aeatParams.getDocument());
			params.put("FIRNOMBRE", aeatParams.getName());
			
			String url = aeatParams.isTest() 
				? "https://prewww1.aeat.es/wlpl/PFTW-PICW/PresBasicaDos"
				: "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasicaDos";

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init( ModelAdmonUtils.getKeyManagers(aeatParams),
					new TrustManager[] { new ModelAdmonUtils.DefaultTrustManager() },
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
				ModelAdmonUtils.giveRedirectBack( resp,response,httpClient );
			} else {
				String ct = ModelAdmonUtils.getContentTypeHeader(response);
				if (AonStringUtils.contains(ct, MimeType.JSON.getName())) {
					ModelAdmonUtils.manageJSONContent( resp, aeatParams, model ,response.body() );
				} else if (AonStringUtils.contains(ct, MimeType.HTML.getName())) {
					ModelAdmonUtils.giveBase64Back(resp, response.body(), MimeType.HTML);
				} else {	
					ModelAdmonUtils.giveExceptionBack(resp,"No se ha encontrado una respuesta válida por parte de la Agencia Tributaria.");
				}
			}
		} catch (InterruptedException e) {
			// Restore interrupted state...
			Thread.currentThread().interrupt();
		} catch (AonCoreException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

	public static void checkAEAT(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel model) {
		try {
			String year = AonNumberUtils.toString(model.getYear());
			String period = model.getPeriod() == Period.YEAR ? "0A" : model.getPeriod().getName();
			
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
			sslContext.init( ModelAdmonUtils.getKeyManagers(aeatParams),
					new TrustManager[] { new ModelAdmonUtils.DefaultTrustManager() },
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
				ModelAdmonUtils.giveExceptionBack(resp,true,scd.getError().getDescripcionError());	
			} else if ( scd.getRespuestaCorrecta()  != null) {
				StringBuilder buff = ModelAdmonUtils.formatRespuestaCorrecta( scd);
				ModelAdmonUtils.giveBase64Back(resp, buff.toString().getBytes(), MimeType.HTML);
			} else{
				ModelAdmonUtils.giveExceptionBack(resp,"La Agencia Tributaria ha devuelto un mensaje, pero no se han encontrado mensajes en el mismo.");
			}
		} catch (InterruptedException e) {
			// Restore interrupted state...
			Thread.currentThread().interrupt();
		} catch (JAXBException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
	
//	 Envío a la AEAT utilizando el mecanismo TGVI Online (se utiliza para el envío de las informativas)
//	 Las instrucciones se encuentran en el documento "Especificaciones_TGVI_Online", publicado por la Agencia Tributaria
//	public static void sendOnlineTGVI(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel model) {
//		
//		try {
//			
//			// Obtenemos el contenido del fichero para la presentación del modelo
//			String fileContent = new String(getModelFile(model), StandardCharsets.UTF_8);			
//			
//			// Quitamos los retornos de carro y lo separamos en lineas, para su mejor tratamiento, pues
//			// tendremos que enviar el registro de tipo 1 por un lado y los de tipo 2 en bloques de 40000 registros
//			String[] fileLines = fileContent.split("\r\n");			
//			
//			// Calculo del total de bloques que tenemos que enviar (cada bloque tiene 40000 registros como maximo
//			int totalBlocks = (fileLines.length-1) / 40000;
//			if ((fileLines.length-1) % 40000 > 0)
//				totalBlocks++;
//			
//			// Inicializacion (Validación y Envío del Registro Tipo 1). Se obtiene el idEnvio que se utilizará para el resto de los envíos
//			String reg1 = fileLines[0]; // Registro Tipo 1
//			String idShipment = sendOnlineTGVI_1(resp, aeatParams, model, reg1, totalBlocks);
//			
//			if (idShipment != null) {				
//				// Envío de Datos (Validación y Envío de los Registros Tipo 2)								
//				if (sendOnlineTGVI_2(resp, aeatParams, model, idShipment, fileLines, totalBlocks)) {					
//					// Presentación (Si todo ha ido bien, presentación del modelo)
//					sendOnlineTGVI_3(resp, aeatParams, model, idShipment);					
//				} 
//			}
//		
//		} catch (AonCoreException e) {
//			ModelAdmonUtils.giveExceptionBack(resp, e.getMessage());
//		}
//	}
//	
//	// TGVI Online - Inicialización (Devuelve idEnvio todo ha ido bien, en caso contrario devuelve null)
//	private static String sendOnlineTGVI_1(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel model, String body, int totalBlocks) {
//		
//		try {
//			String url = aeatParams.isTest() 
//				? "https://prewww1.aeat.es/wlpl/OVPT-NTGV/InicializarEnvio"
//				: "https://www1.agenciatributaria.gob.es/wlpl/OVPT-NTGV/InicializarEnvio";
//
//			SSLContext sslContext = SSLContext.getInstance("TLS");
//			sslContext.init( ModelAdmonUtils.getKeyManagers(aeatParams),
//					new TrustManager[] { new ModelAdmonUtils.DefaultTrustManager() },
//					new SecureRandom());
//			HttpClient httpClient = HttpClient.newBuilder()
//		            .version(HttpClient.Version.HTTP_2)
//		            .connectTimeout(Duration.ofSeconds(120))
//		            .sslContext(sslContext)
//		            .build();
//
//			HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create( url ))
//				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/json;charset=UTF-8")
//				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")				
//				.setHeader( "modelo", FiscalModelUtils.getModelName(model) )          // Modelo a presentar
//				.setHeader( "ejercicio", AonNumberUtils.toString( model.getYear()))   // Ejercicio de presentación
//				.setHeader( "periodo", model.getPeriod() == Period.YEAR ? "0A" : model.getPeriod().getName())  // Periodo del modelo
//				.setHeader( "ndc", model.getDocument() )                              // NIF que identifica al declarante
//				.setHeader( "idioma", "ES")                                           // Idioma
//				.setHeader( "numbloques", AonNumberUtils.toString(totalBlocks))       // Longitud del fichero a presentar medido en bloques de 40.000 registros de T2  
//				.setHeader( "codificacion", "UTF-8")                                  // Indica el juego de caracteres usado para remitir el Registro Tipo 1 en el cuerpo de la petición 
//				.POST(HttpRequest.BodyPublishers.ofString(body))                      // El body lleva el registro Tipo 1
//				.build();
//
//			HttpResponse<byte[]> response = httpClient
//				.send(request, HttpResponse.BodyHandlers.ofByteArray());
//								
//			// Devuelve, entre otras cosas: idenvio, codigo y mensaje			
//			
//			if (response.statusCode() == 302) {
//				ModelAdmonUtils.giveRedirectBack(resp, response, httpClient);
//				return null;
//			} else {								 
//				String codigo = response.headers().firstValue("codigo").isEmpty() ? "" : response.headers().firstValue("codigo").get();
//				String idenvio = response.headers().firstValue("idenvio").isEmpty() ? "" : response.headers().firstValue("idenvio").get();
//				String mensaje = response.headers().firstValue("mensaje").isEmpty() ? "" : response.headers().firstValue("mensaje").get();
//				// codigo = 0 indica que la operación se ha llevado a cabo con exito, 
//				// en tal caso devuelve un idEnvio que se deberá pasar al resto de procesos de envío (registros tipo 2 y presentación)
//				// codigo = 8888 indica que no hay ningun error en el registro tipo 1, pero que la declaracion no podrá ser presentada, 
//				// probablemente porque ya exista una declaración anterior del mismo declarante, ejercicio y periodo que no está dada de 
//				// baja, en este caso en entorno de pruebas voy a dejar continuar porque a la hora de presentar puedo indicar manualmente 
//				// que realice la baja del expediente de la declaración anterior
//				if ("0".equals(codigo) || ("8888".equals(codigo) && aeatParams.isTest())) {
//					return idenvio;
//				} else {
//					// codigo <> 0 indica que la operación ha generado algun error					
//					ModelAdmonUtils.giveExceptionBack(resp, mensaje);
//					return null;
//				}							
//			}
//		} catch (InterruptedException e) {
//			// Restore interrupted state...
//			Thread.currentThread().interrupt();
//			return null;
//		} catch (AonCoreException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
//			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
//			return null;
//		}
//	}
//	
//	// TGVI Online - Envío de Datos
//	private static boolean sendOnlineTGVI_2(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel model, String idShipment, String[] fileLines, int totalBlocks) {
//		
//		// Si solo está el registro tipo 1 (declaraciones negativas), se devuelve true para continuar con la presentación 
//		if (fileLines.length == 1) {			
//			return true;
//		}
//		
//		// Separar los datos en bloques de 40000 registros para enviar cada bloque
//		int globalResult = 0;
//		for (int blockNum = 1; blockNum <= totalBlocks; blockNum++) {
//			int fromIndex = (40000 * (blockNum - 1)) + blockNum;
//			int toIndex = (40000 * blockNum) + 1;
//			if (toIndex > fileLines.length)
//				toIndex = fileLines.length;
//			String[] block = Arrays.copyOfRange(fileLines, fromIndex, toIndex);
//			
//			String body = "";			
//			for (String line : block)
//				body = body + line;
//			int result = sendOnlineTGVI_2_1(resp, aeatParams, body, idShipment, blockNum);
//			if (result < 0) {
//				// Si devuelve un numero negativo, es que se ha producido un error que no permite continuar
//				globalResult = -1;
//				break;
//			} else {
//				// En caso contrario se acumula el resultado, para mostrar los posibles errores al finalizar el envío de todos los bloques
//				globalResult = globalResult + result;
//			}				
//		}
//		
//		// Si ha habido errores (resultado global mayor de cero indica que al menos alguno de los bloques contenia registros con error) se obtienen los errores para mostrarlos
//		if (globalResult > 0) {
//			sendOnlineTGVI_2_2(resp, aeatParams, model, idShipment);
//		}
//		
//		return (globalResult == 0);		
//	}
//	
//	// Envio de cada bloque de datos
//	// Devuelve lo siguiente:
//	// -1 : Se ha producido un error que no permitirá continuar con el proceso
//	//  0 : Se ha enviado de forma correcta y ningún registro tiene errores
//	//  1 : Se ha enviado de forma correcta pero uno o varios registros tienen errores
//	private static int sendOnlineTGVI_2_1(HttpServletResponse resp, AEATParams aeatParams, String body, String idShipment, int blockNumber) {
//		
//		try {
//			String url = aeatParams.isTest() 
//				? "https://prewww1.aeat.es/wlpl/OVPT-NTGV/EnviarDatos"
//				: "https://www1.agenciatributaria.gob.es/wlpl/OVPT-NTGV/EnviarDatos";			
//
//			SSLContext sslContext = SSLContext.getInstance("TLS");
//			sslContext.init( ModelAdmonUtils.getKeyManagers(aeatParams),
//					new TrustManager[] { new ModelAdmonUtils.DefaultTrustManager() },
//					new SecureRandom());
//			HttpClient httpClient = HttpClient.newBuilder()
//		            .version(HttpClient.Version.HTTP_2)
//		            .connectTimeout(Duration.ofSeconds(120))
//		            .sslContext(sslContext)
//		            .build();
//
//			HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create( url ))
//				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/json;charset=UTF-8")
//				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")				
//				.setHeader( "idenvio", idShipment )                             // Identificador único de un envío, generado en la operación de Inicialización. 
//				.setHeader( "numbloque", AonNumberUtils.toString(blockNumber))  // Indica el bloque de datos que se envía.
//				.setHeader( "codificacion", "UTF-8") 							// Indica el juego de caracteres usado para remitir el bloque con los registros Tipo 2 en el cuerpo de la petición.				
//				.POST(HttpRequest.BodyPublishers.ofString(body))                // El body lleva todos los registros que se envían en este bloque
//				.build();
//
//			HttpResponse<byte[]> response = httpClient
//				.send(request, HttpResponse.BodyHandlers.ofByteArray());
//								
//			// Devuelve, entre otras cosas: codigo, mensaje			
//			
//			if (response.statusCode() == 302) {
//				ModelAdmonUtils.giveRedirectBack(resp, response, httpClient);
//				return -1; 
//			} else {			
//				String codigo = response.headers().firstValue("codigo").isEmpty() ? "" : response.headers().firstValue("codigo").get();				
//				String mensaje = response.headers().firstValue("mensaje").isEmpty() ? "" : response.headers().firstValue("mensaje").get();
//				// codigo = 0 indica que la operacion se ha llevado a cabo con exito
//				if ("0".equals(codigo)) {
//					// Aunque se devuelva codigo = 0, hay que comprobar si algún registro lleva errores, 
//					// en cuyo caso no se presentará el modelo (o se presenta todos los registros sin errores, o no se presenta)					
//					String bloquet2ko = response.headers().firstValue("bloquet2ko").isEmpty() ? "0" : response.headers().firstValue("bloquet2ko").get();
//					if ("0".equals(bloquet2ko)) {
//						return 0; // Indica que ningún registro tiene errores 
//					} else {						
//						return 1; // Indica que uno o varios registros presentan errores
//					}
//				} else {
//					// codigo <> 0 indica que la operación ha generado algun error					
//					ModelAdmonUtils.giveExceptionBack(resp, mensaje);
//					return -1; // Indica cualquier otro error que impedirá seguir con la presentación del modelo
//				}							
//			}
//		} catch (InterruptedException e) {
//			// Restore interrupted state...
//			Thread.currentThread().interrupt();
//			return -1; // Indica cualquier otro error que impedirá seguir con la presentación del modelo
//		} catch (AonCoreException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
//			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
//			return -1; // Indica cualquier otro error que impedirá seguir con la presentación del modelo
//		}
//	}
//	
//	// TGVI Online - Recuperar errores en los registros tipo 2
//	private static void sendOnlineTGVI_2_2(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel model, String idShipment) {
//		
//		try {
//			String url = aeatParams.isTest() 
//				? "https://prewww1.aeat.es/wlpl/OVPT-NTGV/RecuperarErrores"
//				: "https://www1.agenciatributaria.gob.es/wlpl/OVPT-NTGV/RecuperarErrores";			
//
//			SSLContext sslContext = SSLContext.getInstance("TLS");
//			sslContext.init( ModelAdmonUtils.getKeyManagers(aeatParams),
//					new TrustManager[] { new ModelAdmonUtils.DefaultTrustManager() },
//					new SecureRandom());
//			HttpClient httpClient = HttpClient.newBuilder()
//		            .version(HttpClient.Version.HTTP_2)
//		            .connectTimeout(Duration.ofSeconds(120))
//		            .sslContext(sslContext)
//		            .build();
//
//			HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create( url ))
//				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/json;charset=UTF-8")
//				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")				
//				.setHeader( "idenvio", idShipment )	  // Identificador único de un envío en estado FINALIZADO O	PRESENTADO			
//				.setHeader( "codificacion", "UTF-8")  // Indica el juego de caracteres usado para recuperar la información		
//				.POST(HttpRequest.BodyPublishers.ofString(""))
//				.build();
//
//			HttpResponse<byte[]> response = httpClient
//				.send(request, HttpResponse.BodyHandlers.ofByteArray());
//								
//			// Devuelve, entre otras cosas: codigo, mensaje, en el BODY están los mensajes de error			
//			
//			if (response.statusCode() == 302) {
//				ModelAdmonUtils.giveRedirectBack(resp, response, httpClient);				
//			} else {			
//				String codigo = response.headers().firstValue("codigo").isEmpty() ? "" : response.headers().firstValue("codigo").get();				
//				String mensaje = response.headers().firstValue("mensaje").isEmpty() ? "" : response.headers().firstValue("mensaje").get();				
//				String errors = new String(response.body()); // Listado de errores, se incluye una línea por cada registro erróneo
//				
//				// codigo = 0 indica que la operacion se ha llevado a cabo con exito, en tal caso mostramos los errores que ha generado la validacion
//				if ("0".equals(codigo)) {					
//					
//					// En los mensajes de error viene una linea por cada registro erroneo, con el registro completo, punto y coma, linea del 
//					// fichero donde esta el error (entre parentesis), codigo del error y mensaje de error															
//					String[] lines = errors.split("\r\n");
//					
//					// Vamos a crear un JSON con el mismo formato que el JSON que devuelve la presentación de los modelos de liquidaciones (IVA, IRPF), 
//					// para así luego poder llamar a manageJSONObject, que es lo mismo que se llama en los modelos de liquidaciones
//					JSONObject jsonErrors = new JSONObject();
//					jsonErrors.put("respuesta", new JSONObject());
//					jsonErrors.getJSONObject("respuesta");
//					
//					for (String line : lines) {
//						// La idea es mostrar cada mensaje de error como: NIF_DECLARADO - NOMBRE_DECLARADO Y LO_QUE_VENGA_DESPUES_DEL_PUNTO_Y_COMA						
//						// Excepto para el Modelo 349, para el resto de informativas el nif y el nombre están en las mismas posiciones
//						String document = line.substring(17, 26);
//						String name = line.substring(35, 75);
//						if (model.getModel() == FiscalModelType.M349) {
//							document = line.substring(75, 92);
//							name = line.substring(92, 132);							
//						}
//						String error = line.split(";")[1];						
//						String errorDescription = document + " " + name + " - " + error;
//						jsonErrors.getJSONObject("respuesta").append( "errores", errorDescription);
//					}				
//					
//					ModelAdmonUtils.manageJSONContent( resp, aeatParams, model , jsonErrors.toString().getBytes() );
//										
//				} else {
//					// codigo <> 0 indica que la operación ha generado algun error
//					ModelAdmonUtils.giveExceptionBack(resp, mensaje);					
//				}							
//			}
//		} catch (InterruptedException e) {
//			// Restore interrupted state...
//			Thread.currentThread().interrupt();			
//		} catch (AonCoreException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
//			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());			
//		}
//	}
//	
//	// TGVI Online - Presentación (los procesos 1 y 2 simplemente validan el contenido del fichero y, si todo ha ido bien, este proceso es el que realiza la presentación)
//	private static void sendOnlineTGVI_3(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel model, String idShipment) {
//		
//		try {
//			
//			// BAJA DECLARACION ANTERIOR
//			// POR AHORA ESTO SOLO SE UTILIZA EN ENTORNO DE PRUEBAS EN FASE DE DESARROLLO, PARA PROBAR 
//			// LAS SUSTITUTIVAS. EN ENTORNO DE PRODUCCION SE OBLIGARÁ A QUE EL USUARIO REALICE LA BAJA 
//			// DE LA LIQUIDACION DESDE LA OFICINA VIRTUAL DE LA AGENCIA TRIBUTARIA
//			// Si la validación ha sido correcta y solo queda la presentación, y es una sustitutiva,
//			// antes de nada se intenta dar de baja la anterior liquidación, porque si la liquidación
//			// ya existe, nos dará un error de duplicidad			
//			if (aeatParams.isTest() && model.isReplacement()) {
//				if (!sendOnlineTGVI_Delete(resp, aeatParams, model))
//					return;
//			}
//			// -------------------------
//			
//			String url = aeatParams.isTest() 
//				? "https://prewww1.aeat.es/wlpl/OVPT-NTGV/PresentarEnvio"
//				: "https://www1.agenciatributaria.gob.es/wlpl/OVPT-NTGV/PresentarEnvio";			
//
//			SSLContext sslContext = SSLContext.getInstance("TLS");
//			sslContext.init( ModelAdmonUtils.getKeyManagers(aeatParams),
//					new TrustManager[] { new ModelAdmonUtils.DefaultTrustManager() },
//					new SecureRandom());
//			HttpClient httpClient = HttpClient.newBuilder()
//		            .version(HttpClient.Version.HTTP_2)
//		            .connectTimeout(Duration.ofSeconds(120))
//		            .sslContext(sslContext)
//		            .build();
//
//			HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create( url ))
//				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/json;charset=UTF-8")
//				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")				
//				.setHeader( "idenvio", idShipment )              // Identificador único de un envío en estado FINALIZADO
//				.setHeader( "firnif", aeatParams.getDocument())  // Forma parte de la Firma no criptográfica. NIF del titular del certificado que realiza la presentación
//				.setHeader( "firnombre", aeatParams.getName())   // Forma parte de la Firma no criptográfica. NOMBRE/RAZÓN SOCIAL del titular del certificado que realiza la presentación
//				.setHeader( "fir", "FirmaBasica")                // Forma parte de la Firma no criptográfica. Valor constante
//				.POST(HttpRequest.BodyPublishers.ofString(""))
//				.build();
//
//			HttpResponse<byte[]> response = httpClient
//				.send(request, HttpResponse.BodyHandlers.ofByteArray());
//								
//			// Devuelve, entre otras cosas: codigo, mensaje, csv			
//			
//			if (response.statusCode() == 302) {
//				ModelAdmonUtils.giveRedirectBack(resp, response, httpClient);				
//			} else {
//				
//				String codigo = response.headers().firstValue("codigo").isEmpty() ? "" : response.headers().firstValue("codigo").get();
//				
//				// codigo = 0 indica que la operacion se ha llevado a cabo con exito
//				if ("0".equals(codigo)) {
//					String csv = response.headers().firstValue("csv").isEmpty() ? "" : response.headers().firstValue("csv").get();
//					String expediente = response.headers().firstValue("expediente").isEmpty() ? "" : response.headers().firstValue("expediente").get();
//					String urlPdf = aeatParams.isTest()   // URL para poder obtener el PDF, si la presentación ha sido correcta 
//							? "https://prewww2.aeat.es/wlpl/inwinvoc/es.aeat.dit.adu.eeca.catalogo.VisualizaSc?COMPLETA=SI&ORIGEN=C&CSV=" + csv
//							: "https://www2.agenciatributaria.gob.es/wlpl/inwinvoc/es.aeat.dit.adu.eeca.catalogo.VisualizaSc?COMPLETA=SI&ORIGEN=C&CSV=" + csv;		
//					
//					SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//					SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
//					
//					// Se crea un JSON con el mismo formato que devuelve la presentación de las liquidaciones (IVA, IRPF), 
//					// para así poder llamar al mismo metodo manageJSONContent, que es el que se encargará de grabar los datos
//					// en data_response y marcar el modelo como enviado 
//					JSONObject result = new JSONObject();
//					result.put("respuesta", new JSONObject());
//					result.getJSONObject("respuesta").put("correcta",new JSONObject());
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("CodigoSeguroVerificacion ", csv);
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("Fecha", DATE_FORMAT.format(new Date())); 
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("Hora", TIME_FORMAT.format(new Date()));
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("Expediente", expediente);
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("NIFPresentador", aeatParams.getDocument());
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("ApellidosNombrePresentador", aeatParams.getName());
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("NIFDeclarante", model.getDocument());
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("ApellidosNombreDeclarante", model.getName());
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("Modelo", FiscalModelUtils.getModelName(model));
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("Ejercicio", AonNumberUtils.toString( model.getYear()));
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("Periodo", model.getPeriod() == Period.YEAR ? "0A" : model.getPeriod().getName());
////					result.getJSONObject("respuesta").getJSONObject("correcta").put("Justificante", ""); // No devuelve numero de justificante 
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("Idioma","ES");
//					result.getJSONObject("respuesta").getJSONObject("correcta").put("urlPdf", urlPdf);
//					
//					ModelAdmonUtils.manageJSONContent( resp, aeatParams, model , result.toString().getBytes() );
//					
//				} else {
//					// codigo <> 0 indica que la operación ha generado algun error
//					String mensaje = response.headers().firstValue("mensaje").isEmpty() ? "" : response.headers().firstValue("mensaje").get();
//					ModelAdmonUtils.giveExceptionBack(resp, mensaje);					
//				}							
//			}
//		} catch (InterruptedException e) {
//			// Restore interrupted state...
//			Thread.currentThread().interrupt();			
//		} catch (AonCoreException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
//			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());			
//		}
//	}	
//	
//	// TGVI Online - Baja de una presentación anterior (SOLO SE UTILIZA EN ENTORNO DE PRUEBAS EN FASE DE DESARROLLO PARA PROBAR LAS SUSTITUTIVAS)
//	private static boolean sendOnlineTGVI_Delete(HttpServletResponse resp, AEATParams aeatParams, IFiscalModel model) {
//		
//		try {
//			
//			// ESTE NUMERO DE EXPEDIENTE SE INDICA DE FORMA MANUAL AQUI Y SOLO SE UTILIZA EN FASE 
//			// DE DESARROLLO PARA PROBAR LAS SUSTITUTIVAS DE LAS INFORMATIVAS EN EL ENTORNO DE PRUEBAS
//			String expediente = ""; 			 
//								
//			// Se comprueba si el numero de expediente está vacio
//			if (AonStringUtils.isEmpty(expediente))
//				return true;  // Devolvemos true para que continue con la presentación del modelo
// 
//			// Se comprueba si el numero de expediente comienza por el ejercicio y el modelo que estamos presentando
//			// si no es así, no se hace nada y se devuelve true para que continue con la presentación
//			if (!AonStringUtils.substring(expediente,0, 4).equals(AonNumberUtils.toString(model.getYear())) || 
//				!AonStringUtils.substring(expediente, 4, 7).equals(model.getModel().getValue()))
//				return true;  // Devolvemos true para que continue con la presentación del modelo
//			
//			// Intentar dar de baja el expediente que se le indica (ENTORNO DE PRUEBAS)
//			String url = "https://prewww1.aeat.es/wlpl/OVPT-NTGV/BajaDeclaracion";						
//
//			SSLContext sslContext = SSLContext.getInstance("TLS");
//			sslContext.init( ModelAdmonUtils.getKeyManagers(aeatParams),
//					new TrustManager[] { new ModelAdmonUtils.DefaultTrustManager() },
//					new SecureRandom());
//			HttpClient httpClient = HttpClient.newBuilder()
//		            .version(HttpClient.Version.HTTP_2)
//		            .connectTimeout(Duration.ofSeconds(120))
//		            .sslContext(sslContext)
//		            .build();
//
//			HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create( url ))
//				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/json;charset=UTF-8")
//				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")
//				.setHeader( "firnif", aeatParams.getDocument())
//				.setHeader( "firnombre", aeatParams.getName())
//				.setHeader( "fir", "FirmaBasica")
//				.setHeader( "modelo", FiscalModelUtils.getModelName(model) )
//				.setHeader( "ejercicio", AonNumberUtils.toString( model.getYear()))
//				.setHeader( "periodo", model.getPeriod() == Period.YEAR ? "0A" : model.getPeriod().getName())
//				.setHeader( "ndc", model.getDocument() )				
//				.setHeader( "expediente", expediente )  				
//				.POST(HttpRequest.BodyPublishers.ofString(""))
//				.build();
//
//			HttpResponse<byte[]> response = httpClient
//				.send(request, HttpResponse.BodyHandlers.ofByteArray());
//								
//			// Devuelve, entre otras cosas: codigo, mensaje			
//			
//			if (response.statusCode() == 302) {
//				ModelAdmonUtils.giveRedirectBack(resp, response, httpClient);
//				return false;
//			} else {
//				String codigo = response.headers().firstValue("codigo").isEmpty() ? "" : response.headers().firstValue("codigo").get();
//				String mensaje = response.headers().firstValue("mensaje").isEmpty() ? "" : response.headers().firstValue("mensaje").get();
//				
//				// Simplemente mostramos en la consola el codigo y el mensaje. 
//				// Si genera codigo <> 0 probablemente sea porque el modelo no existe en la Agencia Tributaria
//				// Si codigo y mensaje están en blanco, probablemente sea un error interno en el sistema
//				System.out.println("TGVI Online BAJA >> Expediente: " + expediente + " Código: " + codigo + " Mensaje: " + mensaje);
//				
//				return true;											
//			}
//		} catch (InterruptedException e) {
//			// Restore interrupted state...
//			Thread.currentThread().interrupt();
//			return false;
//		} catch (AonCoreException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
//			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
//			return false;
//		}
//	}	
	

}
