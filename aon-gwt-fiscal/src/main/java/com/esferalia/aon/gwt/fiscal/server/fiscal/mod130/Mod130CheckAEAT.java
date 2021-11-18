package com.esferalia.aon.gwt.fiscal.server.fiscal.mod130;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.gwt.fiscal.server.fiscal.aeat.ServicioConsultasDirectas;
import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.ibm.icu.text.MessageFormat;

@WebServlet(name = "Mod130 Check AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod130CheckAEAT" })
public class Mod130CheckAEAT extends HttpServlet {
	
	private static final Logger LOGGER = Logger.getLogger(Mod130CheckAEAT.class.getName()); 
	private static final long serialVersionUID = -8391437522744646639L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod130 mod130 = MODEL130.getMod130(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			if (mod130 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			String year = AonNumberUtils.toString(mod130.getYear());
					
			String urlParameters = MessageFormat.format("NIF={0}&ANR={1}&MOD=130&EJF={2}&PER={3}&FED={4}&FEH={5}&HOD={6}&HOH={7}"
					,mod130.getDocument()
					,mod130.getFullName()
					,year
					,mod130.getPeriod().getName()
					,year+"0101"
					,year+"1301"
					,"0000"
					,"2359"
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
			
			String url = "https://www1.agenciatributaria.gob.es/wlpl/SCEJ-MANT/ConsultaExt";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create( url ))
				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")
				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(urlParameters))
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
			LOGGER.log(Level.WARNING,"Thread Interrupted! [{0}] ", e.getMessage());
			// Restore interrupted state...
			Thread.currentThread().interrupt();
		} catch (JAXBException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
