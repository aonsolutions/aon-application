package com.esferalia.aon.gwt.mod200.server.e2023;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.mod200.server.Model200AdmonUtils;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.mod200.api.MODEL2002023;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.occam.mod200.server.format.mod200_2023.Mod2002023Writer;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod200 2023 Validate Print AEAT", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002023ValidatePrintAEAT" })
public class Mod2002023ValidatePrintAEAT extends HttpServlet {

	private static final long serialVersionUID = 8746029270554525955L;
	private static final Logger LOGGER = Logger.getLogger(Mod2002023ValidatePrintAEAT.class.getName()); 

	private enum AeatUrl {
		URL_2023 {

			@Override
			protected boolean accept(Mod2002023 mod200) {
				return true;
			}

			@Override
			protected String getUrl() {
				return "https://prewww2.aeat.es/wlpl/PFTW-PICW/ServVali";
			}

			@Override
			protected String getUrlParameters(Mod2002023 mod200) throws IOException {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				PrintWriter writer = new PrintWriter(output, true, StandardCharsets.ISO_8859_1);
				Mod2002023Writer.fillWriter(mod200, writer);
				return MessageFormat.format("MOD=200&EJF={0}&FIC={1}&IDI=ES"
						,AonNumberUtils.toString( mod200.getYear())
						,Model200AdmonUtils.getEncodedFile(output.toByteArray(),StandardCharsets.ISO_8859_1));
			}
		};
		private static AeatUrl getAeatUrl(Mod2002023 mod200) {
			for (AeatUrl aeatUrl : AeatUrl.values()) {
				if (aeatUrl.accept(mod200)) {
					return aeatUrl;
				}
			}
			throw new AonCoreException("No se encontró una configuración válida para la petición de validación a la AEAT." +
				" Descargue el archivo para su presentación y acceda a los servidores de la Agencia Tributaria manualmente.");
		}

		protected abstract boolean accept( Mod2002023 mod200);
		protected abstract String getUrl();
		protected abstract String getUrlParameters(Mod2002023 mod200) throws IOException;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			AEATParams aeatParams = Model200AdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod2002023 mod200 = MODEL2002023.getMod2002023ById(occam, Model200AdmonUtils.getFiscalModelId(aeatParams));
			
			if (mod200 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			AeatUrl aeatURL = AeatUrl.getAeatUrl(mod200);
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create( aeatURL.getUrl() ))
				.POST(HttpRequest.BodyPublishers.ofString(aeatURL.getUrlParameters(mod200)))
				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")
				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/x-www-form-urlencoded")
				.build();
			HttpClient httpClient = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .connectTimeout(Duration.ofSeconds(10))
	            .build();
			HttpResponse<byte[]> response = httpClient
				.send(request, HttpResponse.BodyHandlers.ofByteArray());
			String headerValue = Model200AdmonUtils.getContentTypeHeader( response );  
			boolean pdfContentType = MimeType.PDF.getName().equals(headerValue); 
			Model200AdmonUtils.giveBase64Back(resp, response.body(), (pdfContentType?MimeType.PDF:MimeType.HTML));
		} catch (InterruptedException e) {	
			LOGGER.log(Level.WARNING,"Thread Interrupted! [{0}] ", e.getMessage());
		    // Restore interrupted state...
		    Thread.currentThread().interrupt();
		} catch (IOException | AonCoreException e ) {
			Model200AdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}

