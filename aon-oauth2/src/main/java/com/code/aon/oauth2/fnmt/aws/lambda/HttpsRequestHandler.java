package com.code.aon.oauth2.fnmt.aws.lambda;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.code.aon.oauth2.fnmt.FnmtCertificateParser;

public class HttpsRequestHandler implements RequestHandler<Object, APIGatewayProxyResponseEvent> {

	//{
	//resource=/fnmt
	//, path=/fnmt
	//, httpMethod=GET
	//, headers={...}
	// ...
	//requestContext={
	//	...
	//	clientCert={clientCertPem=-----BEGIN CERTIFICATE-----

	
    @SuppressWarnings("unchecked")
	@Override
    public APIGatewayProxyResponseEvent handleRequest(Object input, Context context) {

    	try {
	        Map<String, ?> request = (Map<String, ?>) input;
	        Map<String, ?> requestContext = (Map<String, ?>)request.get("requestContext");
	        Map<String, ?> identity = (Map<String, ?>)requestContext.get("identity");
	        Map<String, ?> clientCert = (Map<String, ?>)identity.get("clientCert");
	        String clientCertPem = (String)clientCert.get("clientCertPem");
	
	        Map<String, ?> queryStringParameters = (Map<String, ?>)request.get("queryStringParameters");
	        String redirectUri = (String)queryStringParameters.get("redirect_uri");
	    	
			try ( InputStream is = new ByteArrayInputStream(clientCertPem.getBytes())){
				X509Certificate x509Certificate = 
				(X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(is);
				Map<String,String> propertiesOid = FnmtCertificateParser.readPropertiesOid(x509Certificate);
				String dni = propertiesOid.get(FnmtCertificateParser.DNI_OID);
		    	
				APIGatewayProxyResponseEvent response = 
				new APIGatewayProxyResponseEvent();
		    	response.setStatusCode(307);
		    	response.setIsBase64Encoded(false);
		    	response.setBody("Temporary redirect");
		    	response.setHeaders(Collections.singletonMap("Location", redirectUri+"?" + "dni=" + dni ));
		    	return response;
	        } 
    	}
		catch (Exception e) {
			APIGatewayProxyResponseEvent response = 
			new APIGatewayProxyResponseEvent();
	    	response.setStatusCode(500);
	    	response.setIsBase64Encoded(false);
	    	response.setBody(e.getMessage());
	    	return response;
        }
		
    	
    }

}
