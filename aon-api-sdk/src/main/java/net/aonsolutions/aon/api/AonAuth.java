package net.aonsolutions.aon.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.UserJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.watson.server.AonDateUtils;



public class AonAuth {


	
	 public static Auth getAuth(String email, String auth) throws URISyntaxException, IOException, InterruptedException {
		 String response = Aon.get(auth, ":-)", "/ms/api/auth/", Collections.singletonMap(IJsonNames.EMAIL, email));
		 JSONObject responseJSON = new JSONObject(response);
		 return AuthJSON.fromJSON(responseJSON);
	 }
	 
}
