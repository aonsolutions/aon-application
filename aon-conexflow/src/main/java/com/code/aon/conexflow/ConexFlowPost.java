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

import com.code.aon.AonVersion;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.jooq.DBConsults;
import com.code.aon.ui.util.AonUtil;

public class ConexFlowPost implements  Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final String RESULT_OK = "000";
	private static final String TLS = "TLS";
	private static final String HTTPS = "https";
	
	public static ConexFlow execute(String op) {
		try {
			
			byte[] xmlFile;
			ConexFlow conexFlow = getConexFlowQuery(op);
			if(op.equals(ConexFlowConstant.NEW_OP)){
				xmlFile = sendPostHttpClient(ConexFlowConstant.VALIDATE_CARD_OP, conexFlow.getQuery());
				ConexFlow cf = com.code.aon.conexflow.XMLUtils.readXml(xmlFile);
				if(cf.getRespuesta().getResultado().equals(RESULT_OK)){
					xmlFile = sendPostHttpClient(ConexFlowConstant.CREATE_TOKEN_OP, conexFlow.getQuery());
				}
			}
			else xmlFile = sendPostHttpClient(op, conexFlow.getQuery());
			conexFlow = com.code.aon.conexflow.XMLUtils.readXml(xmlFile, conexFlow.getQuery());
			
			return conexFlow;
			//TODO GUARDAR operacion EN BD DATOS !!!
			
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	
	public static ConexFlow execute(String op, Query query, Integer project, Integer domain) {
		try {
			byte[] xmlFile = sendPostHttpClient(op, query);
			ConexFlow conexFlow = com.code.aon.conexflow.XMLUtils.readXml(xmlFile, query);
		
			if(conexFlow.getRespuesta().getResultado().equals(RESULT_OK)){
				if(!op.equals(ConexFlowConstant.VALIDATE_CARD_OP)){
					DBConsults.insertConexFlowOperation(AonUtil.getDomainName(), domain, xmlFile, project,op);
				}
			}
			
			return conexFlow;			
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	protected static byte[] sendPostHttpClient(String op, Query query) throws Exception {
		String url = ConexFlowEnum.CONEXFLOW_SERVER.getCode();
		
		HttpClientBuilder base = HttpClientBuilder.create();
		// (DEPRECATED) HttpClient base = new DefaultHttpClient();

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
	    
		/* (DEPRECATED)
		SSLSocketFactory ssf = new SSLSocketFactory(ctx); //new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		ClientConnectionManager ccm = base.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme(HTTPS, ssf, 443));
		
		HttpClient client = new DefaultHttpClient(ccm, base.getParams());*/

	    HttpClient client = base.build();

	    
	    
	    HttpPost post = new HttpPost(url);
		// add header
		//post.setHeader("User-Agent", USER_AGENT);
		
		List<NameValuePair> urlParameters = getParameters(op, query);
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		
		
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + 
                                    response.getStatusLine().getStatusCode());
		/* HA DEJADO DE FUNCIONAR */
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
		
	/*	String entity = EntityUtils.toString(response.getEntity());
		String result2 = entity;
		if(query.getImporte() != null && !query.getImporte().equals("")){
			Integer pos = entity.indexOf("</Respuesta>");
			result2 = entity.substring(0, pos)+ "<Importe>"+query.getImporte()+"</Importe> "+entity.substring(pos);
		}
		
		System.out.println("ENTITY : "+ entity);
		System.out.println(result2);*/
		System.out.println(result.toString());

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
	    //Init.init();
	    //XMLUtils.outputDOM(document, baos, true);
	    
	    return array ;// baos.toByteArray();
	}
	
	private static List<NameValuePair> getParameters(String op, Query query){
		switch (op) {
		case ConexFlowConstant.SALE_OP: return ConexFlowUtils.getCardPaymentParameters(query);
		case ConexFlowConstant.PREAUTHORIZATION_OP: return ConexFlowUtils.getPreauthorizationPaymentParameters(query);
		case ConexFlowConstant.REFUND_OP: return ConexFlowUtils.getRefundParameters(query);
		case ConexFlowConstant.CANCELATION_OP: return ConexFlowUtils.getCancelationParameters(query);
		case ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP: return ConexFlowUtils.getConfirmPreauthorizationParameters(query);
		case ConexFlowConstant.REDEMPTION_OP: return ConexFlowUtils.getVoucherParameters(query);
		case ConexFlowConstant.ISSUE_OP: return ConexFlowUtils.getIssueParameters(query);
		case ConexFlowConstant.CREATE_TOKEN_OP: return ConexFlowUtils.getCreateTokenParameters(query);
		case ConexFlowConstant.DELETE_TOKEN_OP: return ConexFlowUtils.getDeleteTokenParameters(query);
		case ConexFlowConstant.VALIDATE_CARD_OP: return ConexFlowUtils.getValidateCardParameters(query);
		case ConexFlowConstant.TRANSACTION_INFO_OP: return ConexFlowUtils.getTransactionInfoParameters(query);
		default: return new ArrayList<NameValuePair>();
		}	
	}
	
	protected static ConexFlow getConexFlowQuery(String op){
		switch (op) {
		case ConexFlowConstant.SALE_OP: return ConexFlowUtils.getConexFlowCardPaymentQuery();
		case ConexFlowConstant.PREAUTHORIZATION_OP: return ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery();
		case ConexFlowConstant.REFUND_OP: return ConexFlowUtils.getConexFlowRefundQuery();
		case ConexFlowConstant.CANCELATION_OP: return ConexFlowUtils.getConexFlowCancelationQuery();
		case ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP: return ConexFlowUtils.getConexFlowConfirmPreauthorizationQuery();
		case ConexFlowConstant.REDEMPTION_OP: return ConexFlowUtils.getConexFlowVoucherQuery();
		case ConexFlowConstant.ISSUE_OP: return ConexFlowUtils.getConexFlowIssueQuery();
		case ConexFlowConstant.CREATE_TOKEN_OP: return ConexFlowUtils.getConexFlowCreateTokenQuery();
		case ConexFlowConstant.DELETE_TOKEN_OP: return ConexFlowUtils.getConexFlowDeleteTokenQuery();
		case ConexFlowConstant.VALIDATE_CARD_OP: return ConexFlowUtils.getConexFlowValidateCardQuery();
		case ConexFlowConstant.TRANSACTION_INFO_OP: return ConexFlowUtils.getConexFlowTransactionInfoQuery();
		case ConexFlowConstant.NEW_OP: return ConexFlowUtils.getConexFlowNewCardQuery();
		default: return new ConexFlow();
		}
	}
}
