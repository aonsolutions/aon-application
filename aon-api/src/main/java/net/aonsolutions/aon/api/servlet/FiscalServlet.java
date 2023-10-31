package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
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
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.json.FiscalMatrixParamsJSON;
import com.esferalia.aon.occam.api.json.FiscalModelJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalMenuDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod131DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111.Mod111DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115.Mod115DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123.Mod123DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod130.Mod130DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202.Mod202DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod131Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod111.Mod111Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod115.Mod115Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod123.Mod123Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod202.Mod202Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod303.Mod303Writer;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.google.apis.drive.AonDrive;

@WebServlet(name = "AonFiscalServlet", urlPatterns = {"/ms/api/fiscal/*"})
public class FiscalServlet extends AonApiHttpServlet{
		
	private static final long serialVersionUID = -8021598700474389724L;
 
	private static final Logger LOGGER  = Logger.getLogger(FiscalServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API FISCAL SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, false);
			if ( AonStringUtils.endsWith(api.getPath(), "/models") ) {
				response(req, resp, getFiscalModels(api));
			} else if ( AonStringUtils.endsWith(api.getPath(), "/matrix") ) {
				response(req, resp, getFiscalMatrix(api));
			} else {
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API FISCAL SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			if ( AonStringUtils.endsWith(api.getPath(), "/markAsFinished") ) {
				response(req, resp, markAsFinished(api));
			} else {
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
		
	private JSONArray getFiscalMatrix(AonApiData api) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
			JSONObject jsonParams = api.getData();
			FiscalMatrixParams params = FiscalMatrixParamsJSON.fromJSON(jsonParams); 
			return FiscalMenuDAO.getDomainsModels(ctx, api.getDomain().getId(), params); 
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	private JSONArray getFiscalModels(AonApiData api) {
		try ( CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())){
			LinkedList<FiscalModel> models = new LinkedList<>();
			models.addAll( Mod303DAO.getMod303s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod111DAO.getMod111s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod115DAO.getMod115s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod123DAO.getMod123s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod130DAO.getMod130s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod131DAO.getMod131s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			models.addAll( Mod202DAO.getMod202s(ctx, api.getDomain().getId()).collect(Collectors.toCollection(LinkedList::new)));
			
			// Comprobar si está configurado "Presentación automática de modelos" y "Entorno de Pruebas de la AEAT"
			int presModelAutoEnabled = AppParamDAO.fetchIntValue(ctx, AppParam.FS_PRES_MODEL_AUTO_ENABLED);
			boolean testEnvironment = AonEnumUtils.getAonBoolean(AppParamDAO.fetchValue(ctx, AppParam.FS_AEAT_TEST_ENV));
			
			JSONArray jsonModels = new JSONArray();

			models.forEach(model-> {
				try {
					jsonModels.put(FiscalModelJSON.toJSON(model)
							.put("presModelAuto", model.getAdministration() == Administration.COMMON_TERRITORY ? presModelAutoEnabled : 0) // Presentación automática del modelo (solo modelos de la Agencia Tributaria)							
							.put("testEnvironment", testEnvironment)  // Entorno de pruebas de la AEAT
							.put("nrc", model.getNrc())
							);
					
				}
				catch (Exception e) {
					throw new AonApiException("Error al obtener el modelo "+ model.getModel().getName()+" "+e.getMessage());
				}
			});

			return jsonModels; 
		} 
	}

	private JSONObject markAsFinished(AonApiData api) {
		try ( final CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())) {
			JSONObject params = api.getData();			
			FiscalModelDeclarationType declarationType = FiscalModelDeclarationType.valueOf(JsonUtils.getString(params, IJsonNames.TYPE));
			if (declarationType != null) {
				Integer id = JsonUtils.getInteger(params, IJsonNames.ID);
				String iban = JsonUtils.getString(params, IJsonNames.IBAN);
				String bankAlias = JsonUtils.getString(params, IJsonNames.BANK_ALIAS);
				String bankBIC = JsonUtils.getString(params, IJsonNames.BIC);
				String reasonReject = params.optString("reasonReject");
				boolean reject = !reasonReject.isEmpty();
				String nrc = JsonUtils.optString(params, "nrc");  
				Integer certi = JsonUtils.getInteger(params, "certi");  				
				int presModelAuto = reject ? 0 : JsonUtils.getInt(params, "presModelAuto");  // Presentación automática del modelo				
				boolean test = JsonUtils.getboolean(params, "testEnvironment");  // Entorno de pruebas
				
				FiscalModelType modelType = FiscalModelType.safeValueOf(JsonUtils.getString(params , IJsonNames.MODEL));
				
				// Comprobar si hay presentacion automática del modelo y no se ha seleccionado certificado
				if (presModelAuto == 1 && certi == null) {
					throw new AonApiException("ERROR: Debe seleccionar certificado.");
				}
				
				// Comprobar si hay presentación automática, es ingreso y no se ha indicado NRC
				if (presModelAuto == 1 && declarationType == FiscalModelDeclarationType.DEPOSIT && AonStringUtils.isBlank(nrc)) {
					throw new AonApiException("ERROR: Debe indicar NRC.");
				}
				
				// Comprobar si hay presentación autómatica, es domiciliación o devolución y no se ha indicado IBAN
				if (presModelAuto == 1 && (declarationType == FiscalModelDeclarationType.BANK || declarationType == FiscalModelDeclarationType.PAYBACK) && AonStringUtils.isBlank(iban)) {
					throw new AonApiException("ERROR: Debe indicar IBAN.");
				}
				
				// Parámetros para la posible presentación automática del modelo 
				AEATParams aeatParams = new AEATParams()
						.setDomainName(api.getDomain().getName())
						.setDomainId(api.getDomain().getId())
						.setUser(api.getUser().getLogin())
						.setMod(id)
						.setCertificateId(certi)
						.setName("")      // -------------------------------------------------------------------------
						.setDocument("")  // Estos tres datos los dejamos en blanco, para que se cojan del certificado										
						.setPass("")      // -------------------------------------------------------------------------
						.setNrc(nrc)
						.setTest(test);
				
				// Cargamos los datos del modelo, actualizamos los datos que nos pasan desde el portal, lo marcamos como finalizado o rechazado y lo presentamos, si es el caso
				FiscalModel model = getModel(ctx, modelType, id);	
				model.setDeclarationResultType(declarationType);
				if (AonStringUtils.isNotBlank(iban) && model.getFinance() != null) {
					BankAccount ba = new BankAccount(iban);
					model.getFinance().setBankAccount(ba);
					model.getFinance().setBankAlias(bankAlias);
					model.getFinance().setBic(bankBIC);
				}
				if(reject) {
					// Marcar el modelo como Rechazado por el Cliente
					markModelAsCustomerRejected(ctx, model, reasonReject);
				} else {
					// Finalizar el modelo
					model.setNrc(nrc);
					markModelAsFinished(ctx, model);
					// Presentación automática del modelo 
					if (presModelAuto == 1) {
						send(aeatParams, model);
					}
				}					
				return new JSONObject().put("status", "OK"); 
			} else 
				throw new AonApiException("Tipo requerido");
		}
	}
	
	private FiscalModel getModel(CloseableAONContext ctx, FiscalModelType modelType, Integer id) {
		
		FiscalModel model;
		switch (modelType) {
			case M111:
				model = Mod111DAO.get(ctx, id);			
				break;
			case M115:
				model = Mod115DAO.get(ctx, id);			
				break;
			case M123:
				model = Mod123DAO.get(ctx, id);	
				break;
			case M130:
				model = Mod130DAO.get(ctx, id);	
				break;
			case M131:
				model = Mod131DAO.getMod131(ctx, id);	
				break;
			case M202:
				model = Mod202DAO.getMod202(ctx, id);
				break;
			case M303:
				model = Mod303DAO.get(ctx, id);
				break;
			default:				
				throw new AonApiException("Unexpected value FiscalModelType: " + modelType);
		}
		return model;
		
	}
	
	private void markModelAsFinished(CloseableAONContext ctx, FiscalModel model) {
		
		switch (model.getModel()) {
			case M111: Mod111DAO.markAsFinished(ctx, (Mod111) model); break;
			case M115: Mod115DAO.markAsFinished(ctx, (Mod115) model); break;		
			case M123: Mod123DAO.markAsFinished(ctx, (Mod123) model); break;	
			case M130: Mod130DAO.markAsFinished(ctx, (Mod130) model); break;	
			case M131: Mod131DAO.markAsFinished(ctx, (Mod131) model); break;	
			case M202: Mod202DAO.markAsFinished(ctx, (Mod202) model); break;
			case M303: Mod303DAO.markAsFinished(ctx, (Mod303) model); break;
			default: throw new AonApiException("Unexpected value FiscalModelType: " + model.getModel());			
		}
		
	}

	private void markModelAsCustomerRejected(CloseableAONContext ctx, FiscalModel model, String reasonReject) {
		
		switch (model.getModel()) {		
			case M111: Mod111DAO.markAsCustomerRejected(ctx, (Mod111) model, reasonReject); break;
			case M115: Mod115DAO.markAsCustomerRejected(ctx, (Mod115) model, reasonReject); break;
			case M123: Mod123DAO.markAsCustomerRejected(ctx, (Mod123) model, reasonReject); break;
			case M130: Mod130DAO.markAsCustomerRejected(ctx, (Mod130) model, reasonReject); break;
			case M131: Mod131DAO.markAsCustomerRejected(ctx, (Mod131) model, reasonReject); break;
			case M202: Mod202DAO.markAsCustomerRejected(ctx, (Mod202) model, reasonReject); break;
			case M303: Mod303DAO.markAsCustomerRejected(ctx, (Mod303) model, reasonReject); break;
			default: throw new AonApiException("Unexpected value FiscalModelType: " + model.getModel());
		}
	
	}

	// METODOS PARA LA PRESENTACION DEL MODELO
	
	private void send(AEATParams aeatParams, IFiscalModel model) {
		try {			
			String period = model.getPeriod().getName();
			if ( model.getModel() == FiscalModelType.M202) {
				if ( model.getPeriod() == Period.T1) period = "1P";
				else if ( model.getPeriod() == Period.T2) period = "2P";
				else if ( model.getPeriod() == Period.T3) period = "3P";
			}
			if ( model.getModel() == FiscalModelType.M390) {
				period = "0A";	
			}
			byte[] fileContent = getModelFile(model);
			JSONObject params = new JSONObject();
			params.put("MODELO", FiscalModelUtils.getModelName(model));
			params.put("EJERCICIO", AonNumberUtils.toString( model.getYear()));
			params.put("PERIODO", period);
			params.put("NRC", aeatParams.getNrc());			
			params.put("IDI", "ES");
			params.put("F01", getUnencodedFile(fileContent,StandardCharsets.UTF_8));
			params.put("FIR", "FirmaBasica");
//			params.put("FIRNIF", aeatParams.getDocument());
//			params.put("FIRNOMBRE", aeatParams.getName());
			
			String url = aeatParams.isTest() 
				? "https://prewww1.aeat.es/wlpl/PFTW-PICW/PresBasicaDos"
				: "https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasicaDos";

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init( getKeyManagers(aeatParams),
					new TrustManager[] { new DefaultTrustManager() },
					new SecureRandom());
			
			// Añadir los parametros FIRNIF y FIRNOMBRE cuyos valores se obtienen en la llamada a getKeyManagers
			params.put("FIRNIF", aeatParams.getDocument());
			params.put("FIRNOMBRE", aeatParams.getName());
			
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
				throw new AonApiException("ERROR EN LA PRESENTACION: Redirect code");
			} else {
				String ct = getContentTypeHeader(response);
				if (AonStringUtils.contains(ct, MimeType.JSON.getName())) {
					if (!manageJSONContent(aeatParams, model, response.body())) {						
						throw new AonApiAeatError(new String(response.body()));
					}
				} else if (AonStringUtils.contains(ct, MimeType.HTML.getName())) {
					throw new AonApiAeatException(new String(response.body()));
				} else {	
					throw new AonApiException("ERROR EN LA PRESENTACION DEL MODELO");
				}
			}
		} catch (InterruptedException e) {
			// Restore interrupted state...
			Thread.currentThread().interrupt();
		} catch (AonCoreException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			throw new AonApiException("ERROR PRESENTACION: " + e.getMessage());
		}
		
	}
	
	private byte[] getModelFile(IFiscalModel fm) throws AonCoreException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output, true, StandardCharsets.UTF_8);
		try {
			switch (fm.getModel()) {		
				case M111: Mod111Writer.fillWriter( (Mod111) fm, writer); break;
				case M115: Mod115Writer.fillWriter( (Mod115) fm, writer); break;
				case M123: Mod123Writer.fillWriter( (Mod123) fm, writer); break;
				case M130: Mod130Writer.fillWriter( (Mod130) fm, writer); break;
				case M131: Mod131Writer.fillWriter( (Mod131) fm, writer); break;
				case M202: Mod202Writer.fillWriter( (Mod202) fm, writer); break;
				case M303: Mod303Writer.fillWriter( (Mod303) fm, writer); break;
				default: throw new AonApiException("Unexpected value FiscalModelType: " + fm.getModel());
			} 
		} catch (IOException e) {
			throw new AonCoreException(e);
		}
			
		return output.toByteArray();
	}
	
	private synchronized String getUnencodedFile(byte[] content, Charset charset) {
		return changeCharacters(new String(content, charset));
	}
	
	private String changeCharacters(String fileString) {
		fileString = fileString.replace("'", " ");
		fileString = fileString.replace("&", " ");
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		return fileString;
	}
	
	private static synchronized KeyManager[] getKeyManagers(AEATParams params) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException{
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
	    
	    // Obtener los datos del presentador (NIF y Nombre) del certificado 
	    
	    CertificateInfo info = CertificateDAO.verifyCertificate(attach.getData(), params.getPass());
	    
	    // COGEMOS EL NIF Y NOMBRE DEL TITULAR DEL CERTIFICADO, SE SUPONE QUE EN LOS CERTIFICADOS DE REPRESENTACION, TAMBIEN SE DEBE
	    // COGER ESE, AUNQUE EN EL ENTORNO DE PRUEBAS SE OBLIGA A QUE EL NIF DEL DECLARANTE Y DEL CERTIFICADO SEAN IGUALES
	    params.setDocument(AonStringUtils.trimToEmpty(info.getDocument()).toUpperCase());
	    params.setName((AonStringUtils.trimToEmpty(info.getSurname()) + " " + AonStringUtils.trimToEmpty(info.getName())).toUpperCase());
	    
    	KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   		kmf.init(keyStore, params.getPass().toCharArray());
   		   		
   		return kmf.getKeyManagers();
	}
	
	private class DefaultTrustManager implements X509TrustManager {

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
	
	private String getContentTypeHeader(HttpResponse<byte[]> response) {
		return getHeader(response, AonHttpUtils.CONTENT_TYPE);
	}
	
	private String getHeader(HttpResponse<byte[]> response, String header) {
		HttpHeaders headers = response.headers();
		List<String> headerList =  headers.map().get(header);
		if (headerList != null && !headerList.isEmpty() ) {
			return headerList.get(0);
		}
		return null;
	}
	
	private boolean manageJSONContent(AEATParams aeatParams, IFiscalModel fm, byte[] body) {
		AEATResponse response = AEATJson.toJSON(body); 
		if (response.isCorrect()) 
			manageRightResponse(aeatParams, fm, new String(body));
		return response.isCorrect();
	}
	
	// Grabar la respuesta, el PDF y marcar el modelo como presentado
	private void manageRightResponse(AEATParams aeatParams, IFiscalModel fm, String aeatResponse) {
		
		fm.setNrc(aeatParams.getNrc());
		Occam occam = new Occam()
				.setDomainName(aeatParams.getDomainName())
				.setDomain(aeatParams.getDomainId())
				.setUser(aeatParams.getUser());
		switch (fm.getModel()) {		
			case M111: MODEL111.aeatPresentation(occam, (Mod111) fm , aeatResponse); break;
			case M115: MODEL115.aeatPresentation(occam, (Mod115) fm , aeatResponse); break;
			case M123: MODEL123.aeatPresentation(occam, (Mod123) fm , aeatResponse); break;
			case M130: MODEL130.aeatPresentation(occam, (Mod130) fm , aeatResponse); break;
			case M131: MODEL131.aeatPresentation(occam, (Mod131) fm , aeatResponse); break;
			case M202: MODEL202.aeatPresentation(occam, (Mod202) fm , aeatResponse); break;
			case M303: MODEL303.aeatPresentation(occam, (Mod303) fm , aeatResponse); break;
			default: throw new AonApiException("Unexpected value FiscalModelType: " + fm.getModel());
		} 
		
	}
	
	// Se utilizará para devolver los errores que se han producido en la presentación, es decir cuando la llamada al 
	// servicio de presentación del modelo es correcta, pero la Agencia Tributaria devuelve mensajes de error
	class AonApiAeatError extends AonApiException {
		
		private static final long serialVersionUID = 5030570086298704983L;

		public AonApiAeatError() {
	        super();
	    }
	    
	    public AonApiAeatError(String message) {
	        super(message);
	    }
	}
	
	// Se utilizará para devolver cualquier otro error que se produzca en la llamada a la presentación del modelo 
	class AonApiAeatException extends AonApiException {

		private static final long serialVersionUID = 735277798302398475L;

		public AonApiAeatException() {
	        super();
	    }
	    
	    public AonApiAeatException(String message) {
	        super(message);
	    }
	}
	
}

