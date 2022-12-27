package net.aonsolutions.aon.api.notification;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.Notification;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationReceiver;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.model.security.User;

public class NotificationRequest extends Notification {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String pathName;
	private String url;
	private JSONObject data;
	private User user;
	private List<Auth> auths;

	public String getUrl() {
		return url;
	}
	
	public List<Auth> getAuths() {
		return auths;
	}
	public JSONObject getData() {
		return data;
	}
	
	public Optional<String> getPathImage() {
		return Optional.ofNullable(pathName);
	}
	
	public User getUser() {
		return user;
	}
	
	public NotificationRequest setPathImage(String pathImage) {
		this.pathName = pathImage;
		return this;
	}
	
	public NotificationRequest setAuths(List<Auth> auths) {
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
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
			setId(0);
			saveNotification();
		    String[] tokens = getAuthDevices();
		    if(tokens!=null && tokens.length>0) {
			    HttpPost httpPost = new HttpPost(urlFB);
			    
				//---------HEADER
				httpPost.addHeader("Authorization", "key="+keyFB);
				httpPost.addHeader("Accept", "*/*");
				httpPost.addHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
			  
			    //---------BODY
			    String body = getBody();
			    if(body!=null) 
			    	body = body.replaceAll("<[^>]+>|&nbsp;|\n", " ");
			    
			    //-------NOTIFICATION
			    JSONObject notification = new JSONObject();
			    getPathImage().ifPresent(path-> notification.put("image", path) );
			    notification.put("title", getTitle());
			    notification.put("body", body);
//			    notification.put("content-available", 0);
			    
			    //--------PAYLOAD
			    JSONObject payload = new JSONObject();
			    payload.put("notification", notification);
			    payload.put("registration_ids", tokens);
			    payload.put("data", getData());

			    httpPost.setEntity(new StringEntity(payload.toString(), ContentType.APPLICATION_JSON));
		
			    CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			    StatusLine response = httpResponse.getStatusLine();
			    HttpEntity responseEntity = httpResponse.getEntity();
			    
			    if(responseEntity!=null && response.getStatusCode() == 200 ) {
			        JSONObject responseJSON = new JSONObject(EntityUtils.toString(responseEntity));
			        success = responseJSON.optInt("success") > 0;
			        System.out.println("----------------NOTIFICATION SENT---------");
			        checkTokenFailed(responseJSON, tokens);
			    } else {
			    	throw new Exception(response.getReasonPhrase());
			    }
		    }
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return success;
	}
	
	
	private String[] getAuthDevices(){
	  LinkedList<AuthDevice> authDevices = new LinkedList<>();
	  getAuths().stream().forEach(auth->{
		 try {
			  LinkedList<AuthDevice> aths = SECURITY.getAuthDevices(getDomain(), getUser().getLogin(), f-> f.getAuthProperty().eq(auth.getAuth()));
			  if(!aths.isEmpty())
				  authDevices.addAll(aths);
		} catch (Exception e) {
			e.printStackTrace();
		}
	  });
	  return authDevices.stream()
			  .filter(at->at.getDeviceToken()!=null && !at.getDeviceToken().isEmpty())
			  .distinct()
			  .map(AuthDevice::getDeviceToken).toArray(String[]::new);
	}
	
	private void checkTokenFailed(JSONObject response, String[] tokens) {
		try {
			LinkedList<String> tokenList = new LinkedList<>();
			if(response.has("failure") && response.optInt("failure") > 0) {
				JSONArray results = response.getJSONArray("results");
				for (int i = 0; i < results.length(); i++) {
					JSONObject result = results.getJSONObject(i);
					if(!result.optString("error").isEmpty()) {
						tokenList.add(tokens[i]);
						System.out.println(result.optString("error")+ " TOKEN:"+ tokens[i]);
					}
				}
			}
			if(!tokenList.isEmpty()) {
				 SECURITY.deleteAuthDevice(getDomain(), getUser().getLogin(), 
						 f-> f.getDeviceTokenProperty().in(tokenList.stream().toArray(String[]::new)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveNotification(){
		try {
			LinkedList<NotificationReceiver> receiver = getReceiver();
			getAuths().stream().map(Auth::getAuth)
			.forEach(auth-> receiver.add(new NotificationReceiver().setAuth(auth)) );
			setReceiver(receiver);
			AON_SOLUTIONS.saveNotification(getDomain(), getUser().getLogin(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
