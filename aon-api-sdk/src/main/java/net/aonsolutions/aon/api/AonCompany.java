package net.aonsolutions.aon.api;

import static net.aonsolutions.aon.api.Aon.get;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.json.AonCompanyJSON;

public class AonCompany {
    
//    public static JSONObject (STring domainName, String userLogin, String document ) {
//	String response = get(domainName, userLogin, "/ms/api/company/one", Collections.singletonMap(IJsonNames.DOCUMENT, document));
//	return new JSONArray(response);
//    }
    
	public static List<com.esferalia.aon.occam.api.model.AonCompany> getCompanies(String token, String domain) throws URISyntaxException, IOException, InterruptedException {
		
		String response  = get(domain , ":-)", token, "/ms/api/company/", Collections.EMPTY_MAP);
		JSONArray responseJSON = new JSONArray(response);
		return AonCompanyJSON.fromJSON(responseJSON);
		
	}

}
