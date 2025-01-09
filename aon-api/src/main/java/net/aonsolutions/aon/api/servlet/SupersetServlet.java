package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "SupersetServlet", urlPatterns = { "/ms/api/superset/*" })
public class SupersetServlet extends AonApiHttpServlet {

	private static final String APPLICATION_JSON = "application/json";

	private static final Logger LOGGER = Logger.getLogger(SupersetServlet.class.getName());


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);

			if (AonStringUtils.equalsIgnoreCase("/guest_token", api.getPath())) {
				response(req, resp, getGuestToken("https://superset.aonsolutions.org", api.getData(), "admin", "admin"));
			}else {
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}

		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getGuestToken(String url, JSONObject data, String userName, String password) throws IOException, InterruptedException {
		
		CookieManager cookieManager = new CookieManager();

		JSONObject loginJsonObject = login(url, userName, password);
		String accessToken = loginJsonObject.getString("access_token");
		
		
		
		HttpResponse<String> csrfResponse = get(url + "/api/v1/security/csrf_token/", accessToken);
		csrfResponse.headers().allValues("set-cookie")
		.forEach(setCookie ->  HttpCookie.parse(setCookie).stream()
		.filter( cookie -> cookie.getName().equals("session"))
		.forEach( cookie -> {
			cookieManager.getCookieStore().add(URI.create(url), cookie);
		}));
		JSONObject csrfJsonObject = new JSONObject(csrfResponse.body());
		String csrfToken = csrfJsonObject.getString("result");
		
		JSONObject jsonObject = new JSONObject()
		.put("rls", new JSONArray().put(new JSONObject().put("clause", data.getString("clause"))))
		.put("resources", new JSONArray().put(new JSONObject().put("id", data.getString("id")).put("type", data.getString("type"))))
		.put("user", new JSONObject().put("username", data.getString("username")).put("first_name", data.getString("first_name")).put("last_name", data.getString("last_name")))
		;
		
		HttpRequest request = HttpRequest.newBuilder()
		.uri( URI.create(url + "/api/v1/security/guest_token/") )
		.header("X-CSRFToken", csrfToken)
		.header("accept", APPLICATION_JSON) 
		.header("Content-Type", APPLICATION_JSON)
		.header("Authorization", String.format("Bearer %s", accessToken))
		.POST( HttpRequest.BodyPublishers.ofString(jsonObject.toString()) )
		.build();
		
		HttpResponse<String> response = HttpClient.newBuilder()
		.cookieHandler(cookieManager)
		.build()
		.send(request, BodyHandlers.ofString());

		System.out.println(response.body());
		
		return new JSONObject(response.body());
	}

	private static JSONObject login(String url, String userName, String password) throws IOException, InterruptedException {
		JSONObject jsonObject = new JSONObject()
		.put("refresh", true)
		.put("provider", "db")
		.put("username", userName)
		.put("password", password)
		;
		
		HttpResponse<String> response = post(url + "/api/v1/security/login", jsonObject);
		
		return new JSONObject(response.body());
	}



	private static HttpResponse<String> get(String url, String accessToken) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.header("Authorization", String.format("Bearer %s", accessToken))
			.GET()
			.build();
		
		return HttpClient.newBuilder()
			.build()
			.send(request, BodyHandlers.ofString());

	}

	private static HttpResponse<String> post(String url, JSONObject postData ) throws IOException, InterruptedException {
		return post(url, postData, "accept", APPLICATION_JSON, "Content-Type", APPLICATION_JSON);
	}

	private static HttpResponse<String> post(String url, JSONObject postData, String ...headers) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri( URI.create(url) )
			.headers(headers)
			.POST( HttpRequest.BodyPublishers.ofString(postData.toString()) )
			.build();
		
		return HttpClient.newBuilder()
			.build()
			.send(request, BodyHandlers.ofString());
		
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		JSONObject postData = new JSONObject("{\n"
				+ "  \"resources\": [\n"
				+ "    {\n"
				+ "      \"id\": \"39aa7e93-a3a2-4bf2-b6c6-1297d00bd0e0\",\n"
				+ "      \"type\": \"dashboard\"\n"
				+ "    }\n"
				+ "  ],\n"
				+ "  \"rls\": [\n"
				+ "    {\n"
				+ "      \"clause\": \"domain = 5\"\n"
				+ "    }\n"
				+ "  ],\n"
				+ "  \"user\": {\n"
				+ "    \"first_name\": \"Superset\",\n"
				+ "    \"last_name\": \"Admin\",\n"
				+ "    \"username\": \"admin\"\n"
				+ "  }\n"
				+ "}");
		JSONObject jsonObject = getGuestToken("https://superset.aonsolutions.org", postData, "admin", "admin");
		System.out.println(jsonObject.toString());
	}
}
