package com.code.aon.conexflow;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.code.aon.conexflow.ConexFlow.Query;

public class ConexFlowPost implements  Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String TLS = "TLS";
	private static final String HTTPS = "https";
	
	
	/**
	 * Realiza la operación (op) de ConexFlow dada. Se envía una petición POST a
	 * ConexFlow y devuelve la respuesta correspondiente.
	 * 
	 * @param connection, Datos de conexión a ConexFlow.
	 * @param op, Operación ConexFlow a realizar.
	 * @param query, Información de la petición.
	 * @return ConexFlow, Datos de la operación realizada.
	 */
	public static ConexFlow execute(ConexFlowConnection connection, String op, Query query) {
		try {
			byte[] xmlFile = sendPostHttpClient(connection, op, query);
			return XMLUtils.readXml(xmlFile, query);
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	
	/**
	 * Envía petición POST a ConexFlow y devuelve archivo xml con la respuesta.
	 * 
	 * @param connection, Datos de conexión a ConexFlow.
	 * @param op, Operación ConexFlow a realizar.
	 * @param query, Información de la petición.
	 * @return byte[] archivo xml con la respuesta de la petición.
	 * @throws Exception
	 */
	protected static byte[] sendPostHttpClient(ConexFlowConnection connection, String op, Query query) throws Exception {
		String url = connection.getServer();
		
		HttpClientBuilder base = HttpClientBuilder.create();

		SSLContext ctx = SSLContext.getInstance(TLS);
		
		X509TrustManager tm = new X509TrustManager() {
		    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

		    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

		    public X509Certificate[] getAcceptedIssuers() {
		        return null;
		    }
		};
		
		ctx.init(null, new TrustManager[]{tm}, null);
		
	    SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(ctx);
	    base.setSSLSocketFactory(sslConnectionFactory);
	    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
	            .register(HTTPS, sslConnectionFactory)
	            .build();
	    HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
	    base.setConnectionManager(ccm);

	    HttpClient client = base.build();

	    HttpPost post = new HttpPost(url);

		List<NameValuePair> urlParameters = getParameters(op, query, connection);
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			String  s = line;
			if(query.getImporte() != null && !query.getImporte().equals("")){
				Integer pos = line.indexOf("</Respuesta>");
				Double importe = Double.parseDouble(query.getImporte()) / 100.0;
				s = line.substring(0, pos)+ "<Importe>"+importe+"</Importe> "+line.substring(pos);
			}
			result.append(s);
		}
		Document doc = stringToDom(result.toString());
		return documentToByte(doc);
	}
	
	private static Document stringToDom(String xmlSource) 
            throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }
	
	private static  byte[] documentToByte(Document document) throws TransformerException{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    StreamResult result = new StreamResult(baos);
	    
	    transformer.transform(source, result);
	    byte[] array=baos.toByteArray();
	    
	    return array ;
	}
	
	private static List<NameValuePair> getParameters(String op, Query query, ConexFlowConnection cfc){
		switch (op) {
		case ConexFlowConstant.SALE_OP: return ConexFlowUtils.getCardPaymentParameters(query, cfc);
		case ConexFlowConstant.PREAUTHORIZATION_OP: return ConexFlowUtils.getPreauthorizationPaymentParameters(query, cfc);
		case ConexFlowConstant.REFUND_OP: return ConexFlowUtils.getRefundParameters(query, cfc);
		case ConexFlowConstant.CANCELATION_OP: return ConexFlowUtils.getCancelationParameters(query, cfc);
		case ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP: return ConexFlowUtils.getConfirmPreauthorizationParameters(query, cfc);
		case ConexFlowConstant.REDEMPTION_OP: return ConexFlowUtils.getVoucherParameters(query, cfc);
		case ConexFlowConstant.ISSUE_OP: return ConexFlowUtils.getIssueParameters(query, cfc);
		case ConexFlowConstant.CREATE_TOKEN_OP: return ConexFlowUtils.getCreateTokenParameters(query, cfc);
		case ConexFlowConstant.DELETE_TOKEN_OP: return ConexFlowUtils.getDeleteTokenParameters(query, cfc);
		case ConexFlowConstant.VALIDATE_CARD_OP: return ConexFlowUtils.getValidateCardParameters(query, cfc);
		case ConexFlowConstant.TRANSACTION_INFO_OP: return ConexFlowUtils.getTransactionInfoParameters(query, cfc);
		default: return new ArrayList<NameValuePair>();
		}	
	}
}
