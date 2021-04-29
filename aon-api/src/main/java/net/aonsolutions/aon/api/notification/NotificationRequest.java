package net.aonsolutions.aon.api.notification;

import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.Notification;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationReceiver;
import java.util.LinkedList;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class NotificationRequest extends Notification {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String path_image;
	private String url;
	private JSONObject data;
	private User user;
	private LinkedList<Auth> auths;

	public String getUrl() {
		return url;
	}
	
	public LinkedList<Auth> getAuths() {
		return auths;
	}
	public JSONObject getData() {
		return data;
	}
	
	public String getPathImage() {
		return path_image;
	}
	
	public User getUser() {
		return user;
	}
	public NotificationRequest setPathImage(String path_image) {
		this.path_image = path_image;
		return this;
	}
	public NotificationRequest setAuths(LinkedList<Auth> auths) {
		this.auths = auths;
		return this;
	}
	public NotificationRequest setUrl(String url) {
		this.url = url;
		return this;
	}
	public NotificationRequest setData(JSONObject data) {
		this.data = data;
		return this;
	}
	
	public NotificationRequest setUser(User user) {
		this.user = user;
		return this;
	}
	
	public Boolean send() {
		final String urlFB = "https://fcm.googleapis.com/fcm/send";
		final String keyFB = "AAAAQ_8KqDo:APA91bFXY2DUz7Ie9TM1qK9hO8RJ_8um9uKkIvT87QcyPobWunCFOvJpP4k961zzfJdGW0sUFWQUGGUMwsa9AOGsLtT0jTI_5sHl95MIgbBQBPDf6vbuOEQU16LQh84lVm1Jh2kNMl3G";
		Boolean success = false;
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		try {
			setId(0);
			saveNotification();
		    HttpPost httpPost = new HttpPost(urlFB);
			httpPost.addHeader("Authorization", "key="+keyFB);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Accept", "*/*");
			
		    JSONObject payload = new JSONObject();
		    JSONObject notification = new JSONObject();
		    notification.put("title", getTitle());
		    notification.put("body", getBody());
		    if(getPathImage()!=null) notification.put("image", getPathImage());
		    payload.put("registration_ids", getAuthDevices());
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
	
	
	private String[] getAuthDevices(){
	  LinkedList<AuthDevice> authDevices = new LinkedList<AuthDevice>();
	  getAuths().stream().forEach(auth->{
		 try {
			  LinkedList<AuthDevice> aths = SECURITY.getAuthDevices(getDomain(), getUser().getLogin(), f-> f.getAuthProperty().eq(auth.getAuth()));
			  authDevices.addAll(aths);
		} catch (Exception e) {}
	  });
	  return authDevices.stream().map(ad -> ad.getDeviceToken()).toArray(String[]::new);
	}
	
	private void saveNotification(){
		try {
			LinkedList<NotificationReceiver> receiver = getReceiver();
			getAuths().stream().forEach(auth->{
				receiver.add(new NotificationReceiver().setAuth(auth.getAuth()));
			});
			setReceiver(receiver);
			AON_SOLUTIONS.saveNotification(getDomain(), getUser().getLogin(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
