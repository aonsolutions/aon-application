package net.aonsolutions.aon.api;

import static net.aonsolutions.aon.api.Aon.get;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import com.esferalia.aon.occam.api.model.IJsonNames;

public class AonUser {
    
    public static void getUserRoles(String domainName, String userLogin) throws URISyntaxException, IOException, InterruptedException {
	String response = get(domainName, userLogin, "/ms/api/user/roles", Collections.singletonMap(IJsonNames.USER, userLogin));
	
    }
    
}
