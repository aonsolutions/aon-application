package net.aonsolutions.rest.api.function;

import static net.aonsolutions.rest.api.function.CORSUtils.getOrigin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import net.aonsolutions.rest.api.model.ServerlessInput;
import net.aonsolutions.rest.api.model.ServerlessOutput;

public class GetCORSPreflightResponse implements RequestHandler<ServerlessInput, ServerlessOutput>{
	
	public static final String FILE = "file";
	

    @Override
    public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		
    	String origin = getOrigin(serverlessInput);
    	
    	return getPreflightResponse(origin);
    	
    }
    
    
    /**
     * This is the list of headers allowed by default by the API Gateway console.
     * see: https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-cors.html.
     * and: https://docs.aws.amazon.com/AmazonS3/latest/API/RESTCommonRequestHeaders.html
     */
	public static final String []  DEFAULT_ALLOWED_HEADERS = new String [] {
		"Content-Type",        // indicates the media type of the resource
		"X-Amz-Date",          // the current date and time according to the requester (must be present for authorization)
		"Authorization",       // information required for request authentication
		"X-Api-Key",           // an AWS API key
		"X-Amz-Security-Token" // see link above		
	};

	public static final String []  DEFAULT_ALLOWED_METHODS = new String [] {
		"GET",
		"POST"
	};
	
	/**
     * Return a ServerlessOutput object that contains a preflight response to be returned
     * from a Lambda function.
     * @param origin The origin to test against the allowed list
     * @return A ServerlessOutput object containing several header => value mappings.
     */
    private static ServerlessOutput getPreflightResponse(String origin ) {
    	return getPreflightResponse(origin, "*", DEFAULT_ALLOWED_METHODS, DEFAULT_ALLOWED_HEADERS, 0);
    }

    /**
     * Return a ServerlessOutput object that contains a preflight response to be returned
     * from a Lambda function.
     * @param origin The origin to test against the allowed list
     * @param allowedOrigins A list of strings or regexes representing allowed origin URLs.
     * @param allowedMethods A list of strings representing allowed HTTP methods.
     * @param allowedHeaders A list of strings representing allowed headers. 
     * @param maxAge Time in seconds until preflight response expires.
     * @return A ServerlessOutput object containing several header => value mappings.
     */
    private static ServerlessOutput getPreflightResponse(String origin, String allowedOrigins, String allowedMethods [], String allowedHeaders [], Integer maxAge) {
    	
        ServerlessOutput serverlessOutput = new ServerlessOutput();

		Map<String,String> headers  = new HashMap<String, String>();
        headers.put("Access-Control-Allow-Origin", origin);
        headers.put("Access-Control-Allow-Headers", join(allowedHeaders));
        headers.put("Access-Control-Allow-Methods", join(allowedMethods));
        
        
        serverlessOutput.setStatusCode(204);
        serverlessOutput.setHeaders(headers);
    	
		return serverlessOutput;
    }
    
    private static String join ( String array [] ) {
    	return Arrays.stream(array).collect(Collectors.joining(","));
    }
    
    
}
