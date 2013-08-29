package com.code.aon.aio.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GITkitCallbackServlet extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(GITkitCallbackServlet.class.getName());

	private static final String DEVELOPER_KEY = "AIzaSyDY7IWGM2jLX3E2QUQvtm7Sq71nn6hE6eg";
	// private static final String VERIFY_URL =
	// "https://www.googleapis.com/identitytoolkit/v1/relyingparty/verifyAssertion?key=";
	private static final String VERIFY_URL = "https://www.googleapis.com/rpc?pp=1&key=";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	/*
	 * Sends request to the identity toolkit API end point to verify the IDP
	 * response.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		JSONObject jsonObject = verifyResponse(getRequestUri(req), null);
		System.out.println(jsonObject);
	}

	// --------------------------------------------------------- Private methods

	/**
	 * Reads all the request parameters to construct the Request URI for
	 * verifyAssertion.
	 * 
	 * @param req
	 *            the {@code HttpServletRequest} object
	 * @return a string for verifyAssertion.
	 * @throws IOException
	 *             if error occurs when encoding the URL key and parameter.
	 */
	private static String getRequestUri(HttpServletRequest req)
			throws IOException {
		Map<String, String[]> params = req.getParameterMap();
		StringBuilder buf = new StringBuilder();
		buf.append(req.getRequestURL().toString());
		boolean first = true;
		for (String key : params.keySet()) {
			if (first) {
				buf.append("?");
				first = false;
			} else {
				buf.append("&");
			}
			buf.append(URLEncoder.encode(key, "UTF-8"));
			buf.append("=");
			buf.append(URLEncoder.encode(params.get(key)[0], "UTF-8"));
		}
		return buf.toString();
	}

	/**
	 * Verifying the response of IDP, and return the profile data if success.
	 * 
	 * @param requestUri
	 *            the request URI of the IDP response.
	 * @param postBody
	 *            the post data of the IDP response.
	 * @return the profile data of the user, or nothing in it if failed.
	 */
	private static JSONObject verifyResponse(String requestUri, String postBody) {
		LOGGER.info("verifyResponse:\nrequestUri = [" + requestUri
				+ "]\npostBody = [" + postBody + "]");

		JSONObject result = new JSONObject();
		try {
			URL url = new URL(VERIFY_URL + DEVELOPER_KEY);
			String postData = buildPostData(requestUri, postBody).toString();
			LOGGER.info("verifyResponse postData:\n" + postData);

			HttpURLConnection httpurlconnection = (HttpURLConnection) url
					.openConnection();
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setRequestProperty("Content-Type",
					"application/json");
			httpurlconnection.setRequestMethod("POST");
			httpurlconnection.getOutputStream().write(postData.getBytes());
			httpurlconnection.getOutputStream().flush();
			httpurlconnection.getOutputStream().close();

			String content = streamToString(httpurlconnection.getInputStream(),
					httpurlconnection.getContentEncoding());
			LOGGER.info("verifyResponse return: " + content);
			try {
				JSONArray response = new JSONArray(content);
				if (response != null && response.length() > 0) {
					JSONObject ret = response.getJSONObject(0);
					result = convertJson(ret);
				}
			} catch (JSONException e) {
				LOGGER.error(e.getMessage());
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} catch (RuntimeException e) {
			LOGGER.error(e.getMessage());
		}
		return result;
	}

	/**
	 * Builds the post data for the request to googleapi.com.
	 * 
	 * @param requestUri
	 *            the request URI of the IDP response.
	 * @param postBody
	 *            the post data of the IDP response.
	 * @return the post data in JSONArray format as required by googleapi.com.
	 */
	private static JSONArray buildPostData(String requestUri, String postBody) {
		JSONArray requests = new JSONArray();
		JSONObject request = new JSONObject();
		JSONObject params = new JSONObject();
		try {
			requests.put(request);
			request.put("method",
					"identitytoolkit.relyingparty.verifyAssertion");
			request.put("apiVersion", "v1");
			request.put("params", params);
			params.put("requestUri", requestUri);
			params.put("postBody", postBody);
			params.put("returnOauthToken", true);

		} catch (JSONException e) {
			LOGGER.error(e.getMessage());
		}
		return requests;
	}

	/**
	 * Changes the format of the returned JSONObject. RP can overwrite this
	 * method to transform the profile JSONObject as its special requirement.
	 * 
	 * @param json
	 *            the JSONObject from googleapis.com
	 * @return the transformed JSONObject
	 * @throws JSONException
	 *             if error occurs when put data into JSONObject
	 */
	private static JSONObject convertJson(JSONObject json) throws JSONException {
		JSONObject ret = new JSONObject();
		if (json.has("error")) {
			ret.put("error", json.get("error"));
		} else if (json.has("result")
				&& (json.get("result") instanceof JSONObject)
				&& json.getJSONObject("result") != null) {
			JSONObject result = json.getJSONObject("result");
			if (result.has("verifiedEmail")) {
				ret.put("email", result.get("verifiedEmail"));
				ret.put("trusted", true);
			} else if (result.has("email")) {
				ret.put("email", result.get("email"));
				ret.put("trusted", false);
				// ret.put("error", "NO_VERIFIIED_EMAIL");
			}
			if (result.has("firstName")) {
				ret.put("firstName", result.get("firstName"));
			}
			if (result.has("lastName")) {
				ret.put("lastName", result.get("lastName"));
			}
			if (result.has("fullName")) {
				ret.put("fullName", result.get("fullName"));
				String fullName = result.getString("fullName").trim();
				int index = fullName.lastIndexOf(" ");
				index = (index < 0) ? fullName.length() : index;
				if (!result.has("firstName")) {
					ret.put("firstName", fullName.substring(0, index));
				}
				if (index < fullName.length() && !result.has("lastName")) {
					ret.put("lastName", fullName.substring(index + 1).trim());
				}
			}
			if (result.has("photoUrl")) {
				ret.put("photoUrl", result.get("photoUrl"));
			}
			if (result.has("context")) {
				ret.put("context", result.get("context"));
			}
			if (result.has("oauthAccessToken")) {
				ret.put("oauthAccessToken", result.get("oauthAccessToken"));
			}
			if (result.has("oauthExpireIn")) {
				ret.put("oauthExpireIn", result.get("oauthExpireIn"));
			}
			if (result.has("oauthRefreshToken")) {
				ret.put("oauthRefreshToken", result.getInt("oauthRefreshToken"));
			}
			if (result.has("oauthRequestToken")) {
				ret.put("oauthRequestToken", result.get("oauthRequestToken"));
			}
		}
		return ret;
	}

	/**
	 * Reads the content of an {@code InputStream}, and returned as a
	 * {@code String}.
	 * 
	 * @param is
	 *            the input stream
	 * @param encoding
	 *            the encoding of the input stream
	 * @return the content String, or "" if error occurs.
	 * @throws UnsupportedEncodingException
	 *             if the encoding is unsupported.
	 */
	private static String streamToString(InputStream is, String encoding)
			throws UnsupportedEncodingException {
		InputStreamReader isr;
		if (StringUtils.isBlank(encoding)) {
			isr = new InputStreamReader(is, "UTF-8");
		} else {
			isr = new InputStreamReader(is, encoding);
		}
		BufferedReader reader = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();

		int ch;
		try {
			while ((ch = reader.read()) >= 0) {
				sb.append((char) ch);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
		return sb.toString();
	}
}
