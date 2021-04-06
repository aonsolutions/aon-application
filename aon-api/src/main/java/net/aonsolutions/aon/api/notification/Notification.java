package net.aonsolutions.aon.api.notification;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Notification {

	private Integer id;
	private String title;
	private String body;
	private String path_image;
	private String url;
	private String[] device_tokens;
	private JSONObject data;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getUrl() {
		return url;
	}
 
	public String[] getDeviceTokens() {
		return device_tokens;
	}
	public JSONObject getData() {
		return data;
	}
	
	public String getPathImage() {
		return path_image;
	}
	
	public Notification setPathImage(String path_image) {
		this.path_image = path_image;
		return this;
	}
	
	public Notification setTitle(String title) {
		this.title = title;
		return this;
	}
	public Notification setBody(String body) {
		this.body = body;
		return this;
	}
	public Notification setDeviceTokens(String[] device_tokens) {
		this.device_tokens = device_tokens;
		return this;
	}
	public Notification setUrl(String url) {
		this.url = url;
		return this;
	}
	public Notification setData(JSONObject data) {
		this.data = data;
		return this;
	}
	

	public Boolean send() {
		final String urlFB = "https://fcm.googleapis.com/fcm/send";
		final String keyFB = "AAAAQ_8KqDo:APA91bFXY2DUz7Ie9TM1qK9hO8RJ_8um9uKkIvT87QcyPobWunCFOvJpP4k961zzfJdGW0sUFWQUGGUMwsa9AOGsLtT0jTI_5sHl95MIgbBQBPDf6vbuOEQU16LQh84lVm1Jh2kNMl3G";
		Boolean success = false;
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		try {

		    HttpPost httpPost = new HttpPost(urlFB);
			httpPost.addHeader("Authorization", "key="+keyFB);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Accept", "*/*");
			
		    JSONObject payload = new JSONObject();
		    JSONObject notification = new JSONObject();
		    notification.put("title", getTitle());
		    notification.put("body", getBody());
		    if(getPathImage()!=null) notification.put("image", getPathImage());
		    payload.put("registration_ids", getDeviceTokens());
		    payload.put("notification", notification);
		    payload.put("data", getData());

			StringEntity params = new StringEntity(payload.toString());
		    httpPost.setEntity(params);
	
		    CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		    StatusLine response = httpResponse.getStatusLine();
		    if(response.getStatusCode() ==200) {
			    HttpEntity responseEntity = httpResponse.getEntity();
			    if(responseEntity!=null) {
			        String responseString = EntityUtils.toString(responseEntity);
			        JSONObject responseJSON = new JSONObject(responseString);
			        success = responseJSON.optInt("success") > 0;
			    }
		    } else {
		    	throw new Exception(response.getReasonPhrase());
		    }
		    httpClient.close();
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return success;
	}
}
