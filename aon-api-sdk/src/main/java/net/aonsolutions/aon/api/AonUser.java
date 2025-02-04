package net.aonsolutions.aon.api;

import static net.aonsolutions.aon.api.Aon.get;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.UserJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.User;

public class AonUser {
	
	public static User getUser(String domainName, String token) throws URISyntaxException, IOException, InterruptedException {
		
		String response = get(domainName, ":-)", token, "/ms/api/user/info", Collections.EMPTY_MAP);
		System.out.println(response);
		JSONObject responseJson = new JSONObject(response);
 		return UserJSON.fromJSON(responseJson);
	}
    
    public static void getUserRoles(String domainName, String userLogin) throws URISyntaxException, IOException, InterruptedException {
	String response = get(domainName, userLogin, "/ms/api/user/roles", Collections.singletonMap(IJsonNames.USER, userLogin));
	
    }
    
}
