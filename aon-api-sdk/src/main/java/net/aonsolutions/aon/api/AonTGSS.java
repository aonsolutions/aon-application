package net.aonsolutions.aon.api;

import static net.aonsolutions.aon.api.Aon.postJSON;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONObject;

public class AonTGSS {
    
    public static JSONObject loadIvlccc(String domainName, String userLogin, String url) throws URISyntaxException, IOException, InterruptedException {
	JSONObject jsonObject = new JSONObject().put("url", url);
	return postJSON(domainName, userLogin, "/ms/api/ivlccc", jsonObject);
    }

}
