package aon.solutions;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.esferalia.aon.occam.api.SECURITY;

/**
 * Lambda function entry point. You can change to use other pojo type or implement
 * a different RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java Handler</a> for more information
 */
public class App2<T> implements RequestHandler<Map<String,T>, APIGatewayProxyResponseEvent> {

    public App2() {
        // Initialize the SDK client outside of the handler method so that it can be reused for subsequent invocations.
        // It is initialized when the class is loaded.
        // Consider invoking a simple api here to pre-warm up the application, eg: dynamodb#listTables
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final Map<String,T>  input, final Context context) {
        // TODO: invoking the api call using rdsClient.
    	String host = System.getenv("DB_HOST");
    	String user= System.getenv("DB_USER");
    	String passwd = System.getenv("DB_PASSWD");
    	StringBuilder builder = new StringBuilder();
    	
    	 //print(input, System.out);
     	
    	String tokenBody = (String) input.get("body");
     	JSONObject tokenJSON = new JSONObject(tokenBody.toString());
     	String token = tokenJSON.getString("token");
     	System.out.print(tokenBody);
     	
     	JSONObject userData = SECURITY.decodeJWT(token);
     	
     	
         System.out.println(userData.toString());
       
    	//return "[{\"Eroski S.L\"}, {\"Mercadona\"}, {\"BM\"} ]";
    	
    	return new APIGatewayProxyResponseEvent()
    			.withStatusCode(200)
    			.withBody(userData.toString())
    			.withHeaders(Collections.singletonMap("Content-Type", "application/json"))
    			;
    	
        
    	//return builder.toString();
    }
    
    private static void print(Map<?, ?> map, PrintStream out ) {
    	map.forEach((key,value) -> {
    		out.print(key + " = ");
    		
    		if ( value instanceof Map ){
    			print((Map<?,?>)value, out);
    		} else {
    			out.print(value);
    		}
    		
    		out.println();
    	});
    }
 }
