package net.aonsolutions.aon.api;

import static net.aonsolutions.aon.api.Aon.get;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.invoice.InvofoxConfigurationJSON;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;

public class AonInvofox {
    
    public static InvofoxConfiguration getInvofoxConfiguration(String domainName, String userLogin) throws URISyntaxException, IOException, InterruptedException {
	String response = get(domainName, userLogin, "/ms/api/invofox/configuration", Collections.emptyMap());
	JSONObject jsonObject = new JSONObject(response); 
	return InvofoxConfigurationJSON.fromJSON(jsonObject);
    }
    
}
