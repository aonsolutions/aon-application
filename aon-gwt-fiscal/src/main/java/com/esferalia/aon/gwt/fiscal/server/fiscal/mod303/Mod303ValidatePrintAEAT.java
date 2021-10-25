package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.Mod303Writer;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.ibm.icu.text.MessageFormat;

@WebServlet(name = "Mod303 Validate Print AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod303ValidatePrintAEAT" })
public class Mod303ValidatePrintAEAT extends HttpServlet {
	
	private static final Logger LOGGER = Logger.getLogger(Mod303ValidatePrintAEAT.class.getName()); 
	private static final long serialVersionUID = -8391437522744646639L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Mod303 mod303 = Mod303AeatUtils.getMod303(req);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(output, true, StandardCharsets.ISO_8859_1);
			Mod303Writer.fillWriter(mod303, writer);
			String urlParameters = MessageFormat.format("MOD=303&EJF={0}&FIC={1}&IDI=ES"
				,AonNumberUtils.toString( mod303.getYear())
				,Mod303AeatUtils.getEncodedFile(output.toByteArray(),StandardCharsets.ISO_8859_1));
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://prewww2.aeat.es/wlpl/PFTW-PICW/ServVali"))
				.POST(HttpRequest.BodyPublishers.ofString(urlParameters))
				.setHeader( AonHttpUtils.USER_AGENT  , "Java 11 HttpClient Bot")
				.setHeader( AonHttpUtils.CONTENT_TYPE, "application/x-www-form-urlencoded")
				.build();
			HttpClient httpClient = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .connectTimeout(Duration.ofSeconds(10))
	            .build();
			HttpResponse<byte[]> response = httpClient
				.send(request, HttpResponse.BodyHandlers.ofByteArray());
			String headerValue = Mod303AeatUtils.getContentTypeHeader( response );  
			boolean pdfContentType = MimeType.PDF.getName().equals(headerValue); 
			Mod303AeatUtils.giveBase64Back(resp, response.body(), (pdfContentType?MimeType.PDF:MimeType.HTML));
		} catch (InterruptedException e) {	
			LOGGER.log(Level.WARNING,"Thread Interrupted! [{0}] ", e.getMessage());
		    // Restore interrupted state...
		    Thread.currentThread().interrupt();
		} catch (IOException | AonCoreException e ) {
			Mod303AeatUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
