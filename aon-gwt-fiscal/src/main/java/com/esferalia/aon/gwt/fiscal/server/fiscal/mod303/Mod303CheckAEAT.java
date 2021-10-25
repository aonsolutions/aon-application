package com.esferalia.aon.gwt.fiscal.server.fiscal.mod303;

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

import com.esferalia.aon.gwt.fiscal.server.fiscal.mod303.aeat.RespuestaCorrecta;
import com.esferalia.aon.gwt.fiscal.server.fiscal.mod303.aeat.ServicioConsultasDirectas;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.ibm.icu.text.MessageFormat;

@WebServlet(name = "Mod303 Check AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/Mod303CheckAEAT" })
public class Mod303CheckAEAT extends HttpServlet {
	
	private static final Logger LOGGER = Logger.getLogger(Mod303CheckAEAT.class.getName()); 
	private static final long serialVersionUID = -8391437522744646639L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = Mod303AeatUtils.getAEATParams(req);
			Mod303 mod303 = Mod303AeatUtils.getMod303(aeatParams);
			String year = AonNumberUtils.toString(mod303.getYear());
					
			String urlParameters = MessageFormat.format("NIF={0}&ANR={1}&MOD=303&EJF={2}&PER={3}&FED={4}&FEH={5}&HOD={6}&HOH={7}"
					,mod303.getDocument()
					,mod303.getFullName()
					,year
					,mod303.getPeriod().getName()
					,year+"0101"
					,year+"1231"
					,"0000"
					,"2359"
					);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init( Mod303AeatUtils.getKeyManagers(aeatParams),
					new TrustManager[] { new Mod303AeatUtils.DefaultTrustManager() },
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
				.POST(HttpRequest.BodyPublishers.ofString(urlParameters.toString()))
				.build();
			HttpResponse<byte[]> response = httpClient
				.send(request, HttpResponse.BodyHandlers.ofByteArray());
			
			StringReader reader = new  StringReader(new String(response.body()));
			JAXBContext context = JAXBContext.newInstance(ServicioConsultasDirectas.class);
			Unmarshaller um = context.createUnmarshaller();
			ServicioConsultasDirectas scd = (ServicioConsultasDirectas) um.unmarshal(reader);
			if ( scd.getError() != null) {
				Mod303AeatUtils.giveExceptionBack(resp,true,scd.getError().getDescripcionError());	
			} else if ( scd.getRespuestaCorrecta()  != null) {
				StringBuffer buff = new StringBuffer();
				buff.append("<html>"
					+"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>"
					+"<style>"
					+"#aeat {"
					+"	margin: 10px;"
					+"	padding: 10px;"
					+"	border: #c4c4c4 1px solid;"
					+"	font: 12px \"arial\", \"lucida Grande\", \"Trebuchet MS\", sans-serif;"
					+"	text-align: center;"
					+"	font-weight: bold;"
					+"}"
					+"#response {"
					+"	font: 12px/1.333 \"arial\", \"lucida Grande\", \"Trebuchet MS\", sans-serif;"
					+"	margin-left: auto;"
					+"	margin-right: auto;"
					+"	border-collapse: collapse;"
					+"	width: 80%;"
					+"}"
					+"#response td {"
					+"	padding: 1px 0.5em 1px 0.5em;"
					+"	vertical-align: middle;"
					+"	border: #c4c4c4 1px solid;"
					+"}"
					+"#label {"
					+"	font-weight: bold;"
					+"	white-space: nowrap;"
					+"	width: 10%;"
					+"	background-color: AliceBlue;"
					+"}"
					+"</style>"
					+"<body>");
				buff.append("<div id=\"aeat\">La Agencia Tributaria devolvió el siguiente mensaje:</div>");
				buff.append("<table id=\"response\">");
				String labelTD = "<tr><td id=\"label\">{0}</td>"; 
				String valueTD = "<td>{0}</td></tr>";
				String valueTD2 = "<td>{0, date, DD-MM-YYYY hh:mm:ss}</td></tr>";
				
				for (RespuestaCorrecta rc : scd.getRespuestaCorrecta()) {
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
				}
				buff.append("</table>");
				buff.append("</body></html>");
				Mod303AeatUtils.giveBase64Back(resp, buff.toString().getBytes(), MimeType.HTML);
			} else{
				Mod303AeatUtils.giveExceptionBack(resp,"La Agencia Tributaria ha devuelto un mensaje, pero no se han encontrado mensajes en el mismo.");
			}
			
			
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARNING,"Thread Interrupted! [{0}] ", e.getMessage());
			// Restore interrupted state...
			Thread.currentThread().interrupt();
		} catch (JAXBException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			Mod303AeatUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

}
