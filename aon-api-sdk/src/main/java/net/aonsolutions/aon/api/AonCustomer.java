package net.aonsolutions.aon.api;

import static net.aonsolutions.aon.api.Aon.get;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class AonCustomer {
	
	public static List<Customer> getCustomers(String domainName, String token) throws URISyntaxException, IOException, InterruptedException {
		
		String response = get(domainName, ":-)", token, "/ms/api/customers", Collections.EMPTY_MAP);
		JSONArray responseJson = new JSONArray(response);
 		
		return CustomerJSON.fromJSON(responseJson);
	}
    
	public static List<Customer> getCustomers(String domainName, String token, String email) throws URISyntaxException, IOException, InterruptedException {
		
		String response = get(domainName, ":-)", token, "/ms/api/customers", Collections.singletonMap(IJsonNames.EMAIL, email));
		JSONArray responseJson = new JSONArray(response);
 		
		return CustomerJSON.fromJSON(responseJson);
	}
    
}
