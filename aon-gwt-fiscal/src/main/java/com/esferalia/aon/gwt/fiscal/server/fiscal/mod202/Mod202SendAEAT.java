package com.esferalia.aon.gwt.fiscal.server.fiscal.mod202;

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

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Mod202 Send AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod202SendAEAT" })
public class Mod202SendAEAT extends HttpServlet {

	private static final long serialVersionUID = -5473378254653264265L;
	
	private static final Logger LOGGER = Logger.getLogger(Mod202SendAEAT.class.getName());
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod202 mod202 = MODEL202.getMod202(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			if (mod202 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			byte[] fileContent = ModelAdmonUtils.getModelFile(mod202);
			
			JSONObject params = new JSONObject();
			params.put("MODELO", "202");
			params.put("EJERCICIO", AonNumberUtils.toString( mod202.getYear()));
			params.put("PERIODO", mod202.getPeriod().getName());
			params.put("NRC", (mod202.isStrictToDeposit()?aeatParams.getNrc() : ""));
			params.put("IDI", "ES");
			params.put("F01", ModelAdmonUtils.getUnencodedFile(fileContent,StandardCharsets.UTF_8));
			params.put("FIR", "FirmaBasica");
			params.put("FIRNIF", aeatParams.getDocument());
			params.put("FIRNOMBRE", aeatParams.getName());
			
			String url = "https://prewww1.aeat.es/wlpl/PFTW-PICW/PresBasicaDos";
//					aeatParams.isTest() ? 
//				: "DESACTIVADO_https://www1.agenciatributaria.gob.es/wlpl/PFTW-PICW/PresBasica");

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
					ModelAdmonUtils.manageJSONContent( resp, aeatParams, mod202 ,response.body() );
				} else if (AonStringUtils.contains(ct, MimeType.HTML.getName())) {
					ModelAdmonUtils.giveBase64Back(resp, response.body(), MimeType.HTML);
				} else {	
					ModelAdmonUtils.giveExceptionBack(resp,"No se ha encontrado una respuesta válida por parte de la Agencia Tributaria.");
				}
			}
			
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARNING,"Thread Interrupted! [{0}] ", e.getMessage());
			// Restore interrupted state...
			Thread.currentThread().interrupt();
		} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
