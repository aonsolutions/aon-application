package net.aonsolutions.aon.api;

import static net.aonsolutions.aon.api.Aon.get;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.DomainUserRolesJSON;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;

public class AonSecurity {
	
	public static DomainUserRoles getDomainUserRoles(String domainName, String userLogin) throws URISyntaxException, IOException, InterruptedException {
    	String response = get(domainName, userLogin, "/ms/api/domainUserRoles", Collections.emptyMap());
    	JSONObject jsonObject = new JSONObject(response); 
    	return DomainUserRolesJSON.fromJSON(jsonObject);
    }

}
