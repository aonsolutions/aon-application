package com.esferalia.aon.gwt.fiscal.server.fiscal.mod193;

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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.fiscal.ModelAdmonUtils;
import com.esferalia.aon.occam.api.fiscal.MODEL193;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.mod193.Mod193Writer;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

@WebServlet(name = "Mod193 Validate Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod193ValidatePrintAEAT" })
public class Mod193ValidatePrintAEAT extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(Mod193ValidatePrintAEAT.class.getName()); 
	private static final long serialVersionUID = -898176379168968783L;

	private enum AeatUrl {
		URL_1931 {

			@Override
			protected boolean accept(Mod193 mod193) {
				return true;
			}

			@Override
			protected String getUrl() {
				return "https://prewww2.aeat.es/wlpl/PFTW-PICW/ServVali";
			}

			@Override
			protected String getUrlParameters(Mod193 mod193) throws IOException {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				PrintWriter writer = new PrintWriter(output, true, StandardCharsets.ISO_8859_1);
				Mod193Writer.fillWriter(mod193, writer);
				return MessageFormat.format("MOD=193&EJF={0}&FIC={1}&IDI=ES"
						,AonNumberUtils.toString( mod193.getYear())
						,ModelAdmonUtils.getEncodedFile(output.toByteArray(),StandardCharsets.ISO_8859_1));
			}
		};
		private static AeatUrl getAeatUrl(Mod193 mod193) {
			for (AeatUrl aeatUrl : AeatUrl.values()) {
				if (aeatUrl.accept(mod193)) {
					return aeatUrl;
				}
			}
			throw new AonCoreException("No se encontró una configuración válida para la petición de validación a la AEAT." +
				" Descargue el archivo para su presentación y acceda a los servidores de la Agencia Tributaria manualmente.");
		}

		protected abstract boolean accept( Mod193 mod193);
		protected abstract String getUrl();
		protected abstract String getUrlParameters(Mod193 mod193) throws IOException;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			Occam occam = new Occam()
					.setDomainName(aeatParams.getDomainName())
					.setDomain(aeatParams.getDomainId())
					.setUser(aeatParams.getUser());
			Mod193 mod193 = MODEL193.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
			
			if (mod193 == null) {
				throw new AonCoreException("[INT] Modelo no encontrado");
			}
			AeatUrl aeatURL = AeatUrl.getAeatUrl(mod193);
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create( aeatURL.getUrl() ))
				.POST(HttpRequest.BodyPublishers.ofString(aeatURL.getUrlParameters(mod193)))
				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")
				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/x-www-form-urlencoded")
				.build();
			HttpClient httpClient = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .connectTimeout(Duration.ofSeconds(10))
	            .build();
			HttpResponse<byte[]> response = httpClient
				.send(request, HttpResponse.BodyHandlers.ofByteArray());
			String headerValue = ModelAdmonUtils.getContentTypeHeader( response );  
			boolean pdfContentType = MimeType.PDF.getName().equals(headerValue); 
			ModelAdmonUtils.giveBase64Back(resp, response.body(), (pdfContentType?MimeType.PDF:MimeType.HTML));
		} catch (InterruptedException e) {	
			LOGGER.log(Level.WARNING,"Thread Interrupted! [{0}] ", e.getMessage());
		    // Restore interrupted state...
		    Thread.currentThread().interrupt();
		} catch (IOException | AonCoreException e ) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}
}
