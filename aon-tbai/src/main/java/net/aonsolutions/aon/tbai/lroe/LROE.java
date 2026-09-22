package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.EstadoRegistroEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RegistroFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RegistrosFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.SituacionRegistroType;
import net.aonsolutions.aon.tbai.TbaiUri;
import net.aonsolutions.aon.tbai.exceptions.TbaiException;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

public class LROE implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final String LROE = "LROE";
	protected static final String DATE_FORMAT = "dd-MM-yyyy";
	
	// *****************************************************************
	// ************************ [TRANSPORTE] ***************************
	// *****************************************************************

	/**
	 * Envia la peticion al servicio de entradas de la DFB/BFA y devuelve la respuesta
	 * sin interpretar el cuerpo: las cabeceras en el JSON y el fichero XML ya
	 * descomprimido en los datos.
	 *
	 * En las cabeceras viaja el estado global del envio
	 * (eus-bizkaia-n3-tipo-respuesta) y el identificativo unico de la entrada
	 * realizada (eus-bizkaia-n3-identificativo), asi que hay que conservarlas incluso
	 * cuando falla la lectura del cuerpo: es lo que se persiste como traza del envio.
	 */
	protected static LROEResponse post(InvoiceCommunicationConfiguration icc, String uri, JSONObject json, byte[] gzip) {
		JSONObject responseJSON = new JSONObject();
		try {
			return new LROEResponse(responseJSON, execute(icc, uri, json, responseJSON, gzip));
		} catch (Exception e) {
			e.printStackTrace();
			responseJSON.put("error", true);
			responseJSON.put("errorMessage", e.getMessage());
			return new LROEResponse(responseJSON);
		}
	}

	/**
	 * Igual que {@link #post(InvoiceCommunicationConfiguration, String, JSONObject, byte[])}
	 * pero devolviendo unicamente el cuerpo de la respuesta.
	 *
	 * Se pierden las cabeceras, entre ellas el identificativo de la entrada, por lo
	 * que conviene ir migrando los llamantes a la version que devuelve
	 * {@link LROEResponse}.
	 */
	public static byte[] post(InvoiceCommunicationConfiguration icc, JSONObject json, byte[] xml) throws InvoiceCommunicationException {
		try {
			return execute(icc, TbaiUri.getUrlEmision(icc), json, new JSONObject(), xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvoiceCommunicationException(e);
		}
	}

	/**
	 * Envia el alta, la modificacion o la anulacion, e interpreta el cuerpo de la
	 * respuesta sobre el documento XML.
	 *
	 * Es el envio generico de los subcapitulos que todavia no leen su objeto de
	 * respuesta; los que lo hacen usan
	 * {@link #post(InvoiceCommunicationConfiguration, String, JSONObject, byte[])} y
	 * {@link #readRegistros(LROEResponse, RegistrosFacturaConSGType)}.
	 */
	public LROEResponse send(InvoiceCommunicationConfiguration icc, JSONObject json, byte[] xml) {
		LROEResponse response = post(icc, TbaiUri.getUrlEmision(icc), json, xml);
		if (response.getData() == null) return response;
		try {
			Document d = XMLUtils.getDocument(response.getData());
			System.out.println(XMLUtils.documentToString(d));
			readSituacionRegistro(d, response.getJson());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public LROEResponse sendConsulta(InvoiceCommunicationConfiguration icc, JSONObject json, byte[] xml) {
		LROEResponse response = post(icc, TbaiUri.getUrlConsulta(icc), json, xml);
		if (response.getData() == null) return response;
		try {
			System.out.println(XMLUtils.documentToString(XMLUtils.getDocument(response.getData())));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Realiza la llamada POST al servicio de entradas con el certificado de la
	 * configuracion, volcando las cabeceras de la respuesta en el JSON indicado y
	 * devolviendo el cuerpo descomprimido.
	 *
	 * El JSON de respuesta lo aporta el llamante para que conserve las cabeceras
	 * aunque la lectura del cuerpo falle.
	 */
	private static byte[] execute(InvoiceCommunicationConfiguration icc, String uri, JSONObject json, JSONObject responseJSON, byte[] gzip) throws Exception {
		ByteArrayInputStream key = new ByteArrayInputStream(icc.getCertificate().getData());
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(key, icc.getCertificate().getPassword().toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, icc.getCertificate().getPassword().toCharArray());

		TrustManager[] trustAll = new TrustManager[] {new TrustAllCertificates()};

		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(kmf.getKeyManagers(), trustAll, new SecureRandom());
		SSLContext.setDefault(sslContext);
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

		System.out.println("***** REQUEST *****");
		System.out.println("[POST] " + uri);
		System.out.println(json.toString());
		String contentLength = Integer.toString(gzip.length);
		System.out.println("Content-Length: " + contentLength);

		HttpsURLConnection https = (HttpsURLConnection) new URL(uri).openConnection();

		https.setHostnameVerifier(new TrustAllHosts());
		https.setRequestMethod("POST");
		https.setRequestProperty("Accept-Encoding", "gzip");
		https.setRequestProperty("Content-Encoding", "gzip");
		https.setRequestProperty("Content-Length", contentLength);
		https.setRequestProperty("Content-Type", "application/octet-stream");
		https.setRequestProperty("eus-bizkaia-n3-version", "1.0");
		https.setRequestProperty("eus-bizkaia-n3-content-type", "application/xml");
		https.setRequestProperty("eus-bizkaia-n3-data", json.toString());

		https.setDoOutput(true);
		https.setDoInput(true);
		https.setUseCaches(false);
		for (String str : https.getRequestProperties().keySet()) {
			System.out.println(str + ": " + https.getRequestProperty(str));
		}

		OutputStream os = https.getOutputStream();
		os.write(gzip);
		os.close();

		responseJSON.put("responseCode", https.getResponseCode());
		System.out.println(https.getResponseCode());
		responseJSON.put("responseMessage", https.getResponseMessage());
		System.out.println(https.getResponseMessage());
		responseJSON.put("responseContentType", https.getContentType());
		responseJSON.put("responseContentLength", https.getContentLength());
		for (String header : https.getHeaderFields().keySet()) {
			if (header != null) {
				responseJSON.put(header, https.getHeaderField(header));
				System.out.println(header + " - " + https.getHeaderField(header));
			}
		}

		return decompress(https.getInputStream().readAllBytes());
	}
	
	/**
	 * Traslada al JSON de la respuesta la situacion de los registros del alta, leida
	 * del objeto de respuesta y no del documento XML.
	 *
	 * El envio se considera erroneo si algun registro se ha rechazado (estado
	 * Incorrecto). Los registros aceptados con errores no invalidan el envio, pero su
	 * codigo y descripcion tambien se trasladan para no perderlos.
	 *
	 * Si la respuesta no trae registros no se toca el estado: lo determina la cabecera
	 * eus-bizkaia-n3-tipo-respuesta (ver LROEResponse.isError()).
	 */
	protected static LROEResponse readRegistros(LROEResponse response, RegistrosFacturaConSGType registros) {
		if (registros == null || registros.getRegistro().isEmpty()) return response;
		
		List<SituacionRegistroType> situaciones = registros.getRegistro().stream()
			.map(RegistroFacturaConSGType::getSituacionRegistro)
			.filter(Objects::nonNull)
			.toList();
		
		response.getJson().put("error", situaciones.stream()
			.anyMatch(situacion -> EstadoRegistroEnum.INCORRECTO.equals(situacion.getEstadoRegistro())));
		
		// El servicio devuelve un unico error por registro, y al usuario se le muestra
		// el del primer registro que no se ha podido anotar.
		situaciones.stream()
			.filter(situacion -> situacion.getCodigoErrorRegistro() != null
				|| situacion.getDescripcionErrorRegistroES() != null)
			.findFirst()
			.ifPresent(situacion -> putErrorRegistro(response.getJson(), situacion));
		
		return response;
	}
	
	private static void putErrorRegistro(JSONObject responseJSON, SituacionRegistroType situacion) {
		String errorCode = situacion.getCodigoErrorRegistro();
		if (errorCode != null) {
			responseJSON.put("errorCode", errorCode);
		}
		responseJSON.put("errorMessage", AonStringUtils.trimToEmpty(errorCode)
			+ " - " + AonStringUtils.trimToEmpty(situacion.getDescripcionErrorRegistroES()));
	}
	
	/**
	 * Traslada al JSON de la respuesta la situacion del registro devuelto por el
	 * servicio de entradas, leyendola del documento XML.
	 *
	 * Es el respaldo generico de los subcapitulos que todavia no leen su objeto de
	 * respuesta; los que lo hacen la sobreescriben con
	 * {@link #readRegistros(LROEResponse, RegistrosFacturaConSGType)}.
	 *
	 * Cuando el envio se rechaza por completo la respuesta no lleva el bloque de
	 * registros, por lo que en ese caso no se toca el estado: lo determina la
	 * cabecera eus-bizkaia-n3-tipo-respuesta que ya se ha volcado al JSON (ver
	 * LROEResponse.isError()).
	 */
	static void readSituacionRegistro(Document d, JSONObject responseJSON) {
		String status = text(d, "EstadoRegistro");
		if (status == null) return;
		
		boolean error = "incorrecto".equalsIgnoreCase(status);
		responseJSON.put("error", error);
		if (!error) return;
		
		String errorCode = text(d, "CodigoErrorRegistro");
		String errorMessage = text(d, "DescripcionErrorRegistroES");
		if (errorCode != null) {
			responseJSON.put("errorCode", errorCode);
		}
		// Si el registro no informa ni codigo ni descripcion se deja que el mensaje lo
		// resuelva LROEResponse.getErrorMessage() con las cabeceras de la respuesta.
		if (errorCode != null || errorMessage != null) {
			responseJSON.put("errorMessage", AonStringUtils.trimToEmpty(errorCode) + " - " + AonStringUtils.trimToEmpty(errorMessage));
		}
	}
	
	/** Texto del primer elemento con el nombre indicado, o null si no viene informado. */
	private static String text(Document d, String tagName) {
		Node node = d.getElementsByTagName(tagName).item(0);
		return node != null ? node.getTextContent() : null;
	}
	
	private static class TrustAllCertificates implements X509TrustManager {
	    public void checkClientTrusted(X509Certificate[] certs, String authType) {
	    }
	 
	    public void checkServerTrusted(X509Certificate[] certs, String authType) {
	    }
	 
	    public X509Certificate[] getAcceptedIssuers() {
	        return null;
	    }
	}
	
	private static class TrustAllHosts implements HostnameVerifier {
	    public boolean verify(String hostname, SSLSession session) {
	        return true;
	    }
	}
	
	public static byte[] decompress(byte[] file) {
	         byte[] buffer = new byte[1024];
	        try
	        {
	            GZIPInputStream is = 
	                    new GZIPInputStream(new ByteArrayInputStream(file));
	                      
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	              
	            int totalSize;
	            while((totalSize = is.read(buffer)) > 0 )
	            {
	                out.write(buffer, 0, totalSize);
	            }
	              
	            out.close();
	            is.close();
	              
	            return out.toByteArray();
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	        return null;
	          
	    }
	
	public static byte[] toGzip(byte[] data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
		GZIPOutputStream gzipStream = new GZIPOutputStream(baos);
		try {
			gzipStream.write(data);
		} finally {
			baos.close();
			gzipStream.close();
		}
		return baos.toByteArray();
	}
	
	protected LROEResponse error(Exception e) {
		e.printStackTrace();
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("error", true);
		responseJSON.put("errorMessage", e.getMessage());
		return new LROEResponse(responseJSON);
	}
	
	protected LROEResponse error(TbaiException e) {
		e.printStackTrace();
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("error", true);
		responseJSON.put("errorMessage", e.getTbaiError().getMessage());
		responseJSON.put("errorCode", e.getTbaiError().getCode());
		return new LROEResponse(responseJSON);
	}
	
	@Deprecated
	protected Object unmarshal(Class clazz, String response) throws JAXBException {
		Unmarshaller unmar =  JAXBContext.newInstance(clazz.getPackage().getName()).createUnmarshaller();
		JAXBElement o = (JAXBElement) unmar.unmarshal(new StringReader(response));
		return o.getValue();
	}

	protected byte[] marshall(Class clazz, Object object) throws JAXBException {
		final JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		jaxbMarshaller.marshal(object, bos);
		return bos.toByteArray();
	}
	
	protected Object unmarshall(Class clazz, String response) throws JAXBException {
		Unmarshaller unmar =  JAXBContext.newInstance(clazz.getPackage().getName()).createUnmarshaller();
		return unmar.unmarshal(new StringReader(response));
	}
	
	public Integer getEjercicio(InvoiceCommunicationConfiguration icc, Invoice invoice) {
		Date ejercicioDate = new Date(); 
		if(invoice.isSales()) {
			ejercicioDate = invoice.ensureFiscal().getExpDate() != null ? invoice.getFiscal().getExpDate() : invoice.getIssueDate();
		} else {
			ejercicioDate = icc.isRegistryTaxDate()
					? invoice.getTaxDate() : invoice.getCreationDate();
			if(ejercicioDate.before(invoice.getIssueDate()))
				ejercicioDate = invoice.getIssueDate();
		}
		return AonDateUtils.getYear(ejercicioDate);
	}
}
