package nordigen;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;

public abstract class NordigenAPIAbstract implements INordigenConstants{	
	public static final String BASE_URL = "https://ob.nordigen.com/";
	public static final String API_URL = "api/v2/";
	
	/*URL ENDPOINT BASES BY CATEGORY*/
	public static final String ACCOUNTS_URL = "accounts/";
	public static final String PREMIUM_URL = "premium/";
	public static final String AGREEMENTS_URL = "agreements/";
	public static final String INSTITUTIONS_URL = "institutions/";
	public static final String PAYMENT_URL = "payment/";
	public static final String REQUISITIONS_URL = "requisitions/";
	public static final String TOKEN_URL = "token/";
	/*------------------------------*/

	public static final int[] CORRECT_STATUS_CODES = {200, 201};
	
	@FunctionalInterface
	public static interface HeaderCallbackPost {
		public void addHeaders(HttpPost post);
	}
	
	@FunctionalInterface
	public static interface HeaderCallbackDelete {
		public void addHeaders(HttpDelete delete);
	}
	
	@FunctionalInterface
	public static interface HeaderCallbackPut {
		public void addHeaders(HttpPut delete);
	}
	
	@FunctionalInterface
	public static interface HeaderCallbackGet {
		public void addHeaders(HttpGet get);
	}
	
	public static boolean checkStatus(int statusCode) {
		return !Arrays.stream(CORRECT_STATUS_CODES).anyMatch(code -> code == statusCode);
	}
	
	public static Object post(String url, JSONObject params, HeaderCallbackPost headerCallback) throws NordigenException {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(BASE_URL + API_URL + url);
			if (headerCallback != null) {
				headerCallback.addHeaders(post);				
			}
			if (params != null) {				
				StringEntity entity = new StringEntity(params.toString());
				post.setEntity(entity);
			}
			try (CloseableHttpResponse resp = client.execute(post)) {
				if (checkStatus(resp.getStatusLine().getStatusCode())) {
					String errStr = EntityUtils.toString(resp.getEntity());					
					if (errStr != null && errStr.charAt(0) == '{') {						
						JSONObject errJson = new JSONObject(errStr);
						NordigenException.throwNordigenException(errJson);
					}
				}
				if (resp.getEntity() != null) {
					String str = EntityUtils.toString(resp.getEntity());
					if (str != null && str.charAt(0) == '{') {						
						return new JSONObject(str);
					} else if (str != null && str.charAt(0) == '[') {
						return new JSONArray(str);
					}
				}
			}
			return null;
		} catch (IOException e) {			
			return null;
		}
	}
	
	public static Object put(String url, JSONObject params, HeaderCallbackPut headerCallback) throws NordigenException {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPut put = new HttpPut(BASE_URL + API_URL + url);
			if (headerCallback != null) {
				headerCallback.addHeaders(put);				
			}
			if (params != null) {
				StringEntity entity = new StringEntity(params.toString());				
				put.setEntity(entity);
			}
			try (CloseableHttpResponse resp = client.execute(put)) {
				if (checkStatus(resp.getStatusLine().getStatusCode())) {
					String errStr = EntityUtils.toString(resp.getEntity());					
					if (errStr != null && errStr.charAt(0) == '{') {						
						JSONObject errJson = new JSONObject(errStr);
						NordigenException.throwNordigenException(errJson);
					}
				}
				if (resp.getEntity() != null) {
					String str = EntityUtils.toString(resp.getEntity());
					if (str != null && str.charAt(0) == '{') {						
						return new JSONObject(str);
					} else if (str != null && str.charAt(0) == '[') {
						return new JSONArray(str);
					}
				}
			}
			return null;
		} catch (IOException e) {			
			return null;
		}
	}
	
	public static Object get(String url, JSONObject params, HeaderCallbackGet headerCallback) throws NordigenException {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			URIBuilder uriBuilder = new URIBuilder(BASE_URL + API_URL + url);
			if (params != null) {
				params.keySet().forEach(key -> uriBuilder.addParameter(key, params.optString(key)));
			}
			URI uri = uriBuilder.build();
			HttpGet get = new HttpGet(uri);
			
			if (headerCallback != null) {
				headerCallback.addHeaders(get);				
			}
			try (CloseableHttpResponse resp = client.execute(get)) {
				if (checkStatus(resp.getStatusLine().getStatusCode())) {
					String errStr = EntityUtils.toString(resp.getEntity());					
					if (errStr != null && errStr.charAt(0) == '{') {						
						JSONObject errJson = new JSONObject(errStr);
						NordigenException.throwNordigenException(errJson);
					}
				}
				if (resp.getEntity() != null) {
					String str = EntityUtils.toString(resp.getEntity());
					if (str != null && str.charAt(0) == '{') {						
						return new JSONObject(str);
					} else if (str != null && str.charAt(0) == '[') {
						return new JSONArray(str);
					}
				}
			}
			return null;
		} catch (IOException | URISyntaxException e) {			
			return null;
		}
	}
	
	public static Object delete(String url, JSONObject params, HeaderCallbackDelete headerCallback) throws NordigenException {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			URIBuilder uriBuilder = new URIBuilder(BASE_URL + API_URL + url);
			if (params != null) {
				params.keySet().forEach(key -> uriBuilder.addParameter(key, params.optString(key)));
			}
			URI uri = uriBuilder.build();
			HttpDelete delete = new HttpDelete(uri);
			
			if (headerCallback != null) {
				headerCallback.addHeaders(delete);				
			}
			try (CloseableHttpResponse resp = client.execute(delete)) {
				if (checkStatus(resp.getStatusLine().getStatusCode())) {
					String errStr = EntityUtils.toString(resp.getEntity());					
					if (errStr != null && errStr.charAt(0) == '{') {						
						JSONObject errJson = new JSONObject(errStr);
						NordigenException.throwNordigenException(errJson);
					}
				}
				if (resp.getEntity() != null) {
					String str = EntityUtils.toString(resp.getEntity());
					if (str != null && str.charAt(0) == '{') {						
						return new JSONObject(str);
					} else if (str != null && str.charAt(0) == '[') {
						return new JSONArray(str);
					}
				}
			}
			return null;
		} catch (IOException | URISyntaxException e) {			
			return null;
		}
	}
	
	public static class CreateRequisitionParams {
		private String redirect;
		private String institutionId;
		private String agreement;
		private String reference;
		private AonLanguage userLanguage;
		private String ssn;
		private Boolean accountSelection;
		private Boolean redirectImmediate;
		
		public String getRedirect() {
			return redirect;
		}
		public CreateRequisitionParams setRedirect(String redirect) {
			this.redirect = redirect;
			return this;
		}
		public String getInstitutionId() {
			return institutionId;
		}
		public CreateRequisitionParams setInstitutionId(String institutionId) {
			this.institutionId = institutionId;
			return this;
		}
		public String getAgreement() {
			return agreement;
		}
		public CreateRequisitionParams setAgreement(String agreement) {
			this.agreement = agreement;
			return this;
		}
		public String getReference() {
			return reference;
		}
		public CreateRequisitionParams setReference(String reference) {
			this.reference = reference;
			return this;
		}
		public AonLanguage getUserLanguage() {
			return userLanguage;
		}
		public CreateRequisitionParams setUserLanguage(AonLanguage userLanguage) {
			this.userLanguage = userLanguage;
			return this;
		}
		public String getSsn() {
			return ssn;
		}
		public CreateRequisitionParams setSsn(String ssn) {
			this.ssn = ssn;
			return this;
		}
		public Boolean getAccountSelection() {
			return accountSelection;
		}
		public CreateRequisitionParams setAccountSelection(Boolean accountSelection) {
			this.accountSelection = accountSelection;
			return this;
		}
		public Boolean getRedirectImmediate() {
			return redirectImmediate;
		}
		public CreateRequisitionParams setRedirectImmediate(Boolean redirectImmediate) {
			this.redirectImmediate = redirectImmediate;
			return this;
		}
		
		public JSONObject buildJson() {
			JSONObject paramsJson = new JSONObject();
			paramsJson.put("redirect", this.redirect);
			paramsJson.put("institution_id", this.institutionId);
			paramsJson.put("agreement", this.agreement);
			paramsJson.put("reference", this.reference);
			paramsJson.put("user_language", this.userLanguage != null ? this.userLanguage.getLanguage() : null);
			paramsJson.put("ssn", this.ssn);
			paramsJson.put("account_selection", this.accountSelection);
			paramsJson.put("redirect_immediate", this.redirectImmediate);
			
			return paramsJson;
		}
		
	}
	
}
