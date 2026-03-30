package solutions.aon.in.invoice.aws.lambda;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class Invofox {

	enum DocumentType {
		INVOICE("invoice"), 
		DELIVERY_NOTE("deliveryNote"), 
		PROMISSORY_NOTE("promissoryNote"), 
		TICKET("ticket"),
		SUPPLY_INVOICE("supplyInvoice"), 
		PURCHASE_ORDER("purchaseOrder");

		private String value;

		private DocumentType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

	}

	protected static String newCompany(String apiKey, String apiUrl, String cif, String name, Map<String, String> data)
			throws URISyntaxException, IOException, InterruptedException, NoSuchCompanyException {
		Map<String, Object> params = new HashMap<>();
		params.put("countryCode", "ES");
		params.put("taxId", cif);
		params.put("name", name);
		params.put("clientData", data);
		JSONObject company = postJSON(apiKey, apiUrl, "companies", params);
		JSONObject result = company.getJSONObject("result");
		return result.getString("_id");
	}

	protected static String getCompanyId(String apiKey, String apiUrl, String cif)
			throws URISyntaxException, IOException, InterruptedException, NoSuchCompanyException {
		JSONObject companies = get(apiKey, apiUrl, "companies", Collections.singletonMap("taxId", cif));
		int count = companies.getInt("count");
		if (count > 0) {
			JSONArray result = companies.getJSONArray("result");
			JSONObject company = result.getJSONObject(0);
			return company.getString("_id");
		}
		throw new NoSuchCompanyException(String.format("No company with taxId '%s'", cif));
	}

	protected static JSONObject newLoadBatch(String apiKey, String apiUrl, String company) throws URISyntaxException, IOException, InterruptedException {
		JSONObject loadBatch = postJSON(apiKey, apiUrl, "loadBatches", Collections.singletonMap("company", company));
		return loadBatch.getJSONObject("result");
	}

	protected static JSONObject loadDocuments(String apiKey, String apiUrl, DocumentType type, String company, JSONObject clientData, boolean beta, String... urls) throws URISyntaxException, IOException, InterruptedException {
		Map<String, String> params = new HashMap<>();
		JSONObject infoJSON = new JSONObject();
		infoJSON.put("type", type.getValue());
		infoJSON.put("company", company);
		infoJSON.put("useSplitter", "false");
		infoJSON.put("clientData", clientData);
		params.put("info", infoJSON.toString());
			
		JSONArray urlArray = new JSONArray();
		Arrays.stream(urls).forEach(urlArray::put);

		params.put("urls",urlArray.toString());
		String path = "v1/ingest/uploads";
		
		return postMultipartForm(apiKey, apiUrl, path, params);
	}

	private static JSONObject postJSON(String apiKey, String apiUrl, String path, Map<String, ?> params)
			throws URISyntaxException, IOException, InterruptedException {

		JSONObject jsonObject = new JSONObject(params);

		System.out.println("POST : " + jsonObject.toString());

		HttpRequest httpRequest = HttpRequest.newBuilder(new URI(String.format("%s/%s", apiUrl, path)))
				.setHeader("x-api-key", apiKey).setHeader("Accept", "application/json")
				.setHeader("Content-Type", "application/json").POST(BodyPublishers.ofString(jsonObject.toString()))
				.build();

		HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString());
		checkResponse(response);

		String body = response.body();
		return new JSONObject(body);
	}

	/**
	 * @param response
	 */
	private static void checkResponse(HttpResponse<String> response) {
		int statusCode = response.statusCode();

		System.out.println("RESPONSE : " + response.body());

		if (statusCode == 400) {
			String body = response.body();
			JSONObject err = new JSONObject(body);
			String code = err.getString("code");
			if ("ERR_COMPANY_ALREADY_EXISTS".equalsIgnoreCase(code)) {
				throw new AlreadyCompanyExistsException();
			} else {
				throw new IllegalArgumentException(code);
			}
		}
	}

	private static JSONObject postMultipartForm(String apiKey, String apiUrl, String path, Map<String, ?> params)
			throws URISyntaxException, IOException, InterruptedException {

		String boundary = "---------------------------7360350682899180152152769264";
		StringBuilder form = new StringBuilder();
		form.append(String.format("--%s", boundary));
		params.forEach((name, value) -> {
			form.append(String.format("\r%nContent-Disposition: form-data; name=\"%s\"\r%n", name));
			form.append(String.format("\r%n%s\r%n", value));
			form.append(String.format("--%s", boundary));
		});
		form.append(String.format("--\r%n"));

		System.out.println(form.toString());

		HttpRequest httpRequest = HttpRequest.newBuilder(new URI(String.format("%s/%s", apiUrl, path)))
				.setHeader("x-api-key", apiKey).setHeader("Accept", "application/json")
				.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary)
				.POST(BodyPublishers.ofString(form.toString())).build();

		HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString());
		checkResponse(response);

		return new JSONObject(response.body());
	}

	private static JSONObject get(String apiKey, String apiUrl, String path, Map<String, ?> params)
			throws URISyntaxException, IOException, InterruptedException {
		String query = params.entrySet().stream()
				.map(e -> String.format("%s=%s", e.getKey(),
						URLEncoder.encode(e.getValue().toString(), Charset.defaultCharset())))
				.collect(Collectors.joining("&"));

		HttpRequest httpRequest = HttpRequest.newBuilder(new URI(String.format("%s/%s?%s", apiUrl, path, query)))
				.setHeader("x-api-key", apiKey).setHeader("Accept", "application/json").build();

		String body = HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString()).body();
		return new JSONObject(body);
	}

}