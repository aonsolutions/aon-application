package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Mod303 Send AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod303SendAEAT" })
public class Mod303SendAEAT extends HttpServlet {

	private static final long serialVersionUID = -1886808421915141367L;
	private static final Logger LOGGER = Logger.getLogger(Mod303SendAEAT.class.getName());
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		try {
//			AEATParams aeatParams = Mod303AeatUtils.getAEATParams(req);
//			Mod303 mod303 = Mod303AeatUtils.getMod303(aeatParams);
//			FISCAL.markAsSent(aeatParams.getDomainName(), mod303, aeatParams.getUser());
//		} catch (AonCoreException e) {
//			Mod303AeatUtils.giveExceptionBack(resp,e.getMessage());
//		}
//	}
//	//@Override
//	protected void doPost1(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = Mod303AeatUtils.getAEATParams(req);
			Mod303 mod303 = Mod303AeatUtils.getMod303(aeatParams);
			byte[] fileContent = Mod303AeatUtils.getModelFile(mod303);
			
			JSONObject params = new JSONObject();
			params.put("MODELO", "303");
			params.put("EJERCICIO", AonNumberUtils.toString( mod303.getYear()));
			params.put("PERIODO", mod303.getPeriod().getName());
			params.put("NRC", (mod303.isStrictToDeposit()?aeatParams.getNrc() : ""));
			params.put("IDI", "ES");
			params.put("F01", Mod303AeatUtils.getUnencodedFile(fileContent,StandardCharsets.UTF_8));
			params.put("FIR", "FirmaBasica");
			params.put("FIRNIF", aeatParams.getDocument());
			params.put("FIRNOMBRE", aeatParams.getName());
			
			String url = "https://prewww1.aeat.es/wlpl/PFTW-PICW/PresBasicaDos";
//					aeatParams.isTest() ? 
//				: "DESACTIVADO_https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica");

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init( Mod303AeatUtils.getKeyManagers(aeatParams),
					new TrustManager[] { new Mod303AeatUtils.DefaultTrustManager() },
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
				Mod303AeatUtils.giveRedirectBack( resp,response,httpClient );
			} else {
				String ct = Mod303AeatUtils.getContentTypeHeader(response);
				if (AonStringUtils.contains(ct, MimeType.JSON.getName())) {
					Mod303AeatUtils.manageJSONContent( resp, httpClient, aeatParams, mod303 ,response.body() );
				} else if (AonStringUtils.contains(ct, MimeType.HTML.getName())) {
					Mod303AeatUtils.giveBase64Back(resp, response.body(), MimeType.HTML);
				} else {	
					Mod303AeatUtils.giveExceptionBack(resp,"No se ha encontrado una respuesta válida por parte de la Agencia Tributaria.");
				}
			}
			
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARNING,"Thread Interrupted! [{0}] ", e.getMessage());
			// Restore interrupted state...
			Thread.currentThread().interrupt();
		} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			Mod303AeatUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
