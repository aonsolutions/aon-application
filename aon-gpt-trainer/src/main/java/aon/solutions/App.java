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

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Lambda function entry point. You can change to use other pojo type or implement
 * a different RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java Handler</a> for more information
 */
public class App<T> implements RequestHandler<Map<String,T>, APIGatewayProxyResponseEvent> {

    public App() {
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
    	
    	 print(input, System.out);
     	
     	Map<String,T> queryStringParameters = (Map<String, T>) input.get("queryStringParameters");
     	String query = (String) queryStringParameters.get("query");
     	
    	try(Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + "/pro-aonsolutions-org",user, passwd)) {
    			
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        builder.append("[");
        while(rs.next()) {
        	if ( builder.length() > 1 ) 
        		builder.append(",");
        	
        	builder.append("{");
        	for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
        		builder.append("\"");
        		builder.append(rs.getMetaData().getColumnName(i));
        		builder.append("\"");
        		builder.append(":");
        		if ( i > 1 )
        			builder.append(",");

        		builder.append("\"");
        		builder.append(rs.getObject(i));
        		builder.append("\"");
        		
        		
			}
        	
        	builder.append("}");
        }
        builder.append("]");
        
        rs.close();
        st.close();
    		
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(builder.toString());
       
    	//return "[{\"Eroski S.L\"}, {\"Mercadona\"}, {\"BM\"} ]";
    	
    	return new APIGatewayProxyResponseEvent()
    			.withStatusCode(200)
    			.withBody(builder.toString())
    			.withHeaders(Collections.singletonMap("Content-Type", "text/plain"))
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
