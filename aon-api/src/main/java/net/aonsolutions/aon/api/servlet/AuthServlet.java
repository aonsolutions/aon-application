package net.aonsolutions.aon.api.servlet;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.AuthAttach;
import com.esferalia.aon.occam.api.model.security.AuthAttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonAuthServlet", urlPatterns = {"/ms/api/auth/*"})
public class AuthServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(AuthServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON AUTH SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
			AonToken aonToken = null;
			Auth auth;
			if(api.getData().opt(IJsonNames.EMAIL) != null) {
// TODO AUTH with dynamodb
//				auth = AuthDyn.getAuth(JsonUtils.getString(api.getData(), IJsonNames.EMAIL));
//				if(auth.isEmpty())
					auth = AON_SOLUTIONS.getAuth(api.getData().optString(IJsonNames.EMAIL));
//				else if(JsonUtils.getboolean(api.getData(), IJsonNames.AVATAR)) 
//					auth.setAvatar(S3.getPresignedURL(S3.AUTH_ATTACH_BUCKET, auth.getUuid(), AonDateUtils.addDays(new Date(), 1)));
			} else if(api.getData().opt("task_holder") != null){
				TaskHolder th = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
						f -> f.getIdProperty().eq(api.getData().optInt("task_holder")));
				User user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
						f -> f.getIdProperty().eq(th.getUserId()));
				auth = AON_SOLUTIONS.getAuth(user.getAuth().getAuth());	
			} else {
				aonToken = SECURITY.getAonToken(api.getToken());
// TODO AUTH with dynamodb
//				auth = AuthDyn.getAuthByUuid(aonToken.getUuid());
//				if(auth.isEmpty())
					auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
//				else if(JsonUtils.getboolean(api.getData(), IJsonNames.AVATAR)) 
//					auth.setAvatar(S3.getPresignedURL(S3.AUTH_ATTACH_BUCKET, auth.getUuid(), AonDateUtils.addDays(new Date(), 1)));
			}
			
			if(JsonUtils.getboolean(api.getData(), IJsonNames.AVATAR) && AonStringUtils.isBlank(auth.getAvatar())) {
				byte[] a = auth.getAuth();
				if(auth.getSchema() == null && aonToken != null) {
					auth.setSchema(aonToken.getSchema());
				}
				
				AuthAttach aa = AON_SOLUTIONS.getAuthAttach(auth, f-> f.getAuthProperty().eq(a).and(f.getTypeProperty().eq(AuthAttachType.AVATAR.value())));
			
				if(aa.getId() != null) {
					JSONObject data = new JSONObject();
					data.put("session_id", api.getToken());
					String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
					String url =  "ms/api/auth_avatar/" +  result;
					auth.setAvatar(url);
				}
			}
			
			JSONObject json = AuthJSON.toJSON(auth);
			
			
			response(req, resp, json);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("EXAMPLE SERVLET - POST METHOD");
		put(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("EXAMPLE SERVLET - PUT METHOD");
		put(req, resp);
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/password":
				response(req, resp, changePassword(api));
				break;
			case "/avatar":
				response(req, resp, saveAvatar(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject changePassword(AonApiData api) throws AonApiException {
		
		JSONObject params = api.getData();
		
		String oldPassword = params.optString("oldPassword");
		String newPassword = params.optString("newPassword");
		
		if(oldPassword.isEmpty() || newPassword.isEmpty()) {
			throw new AonApiException("Las contraseñas son requeridas.");
		} 
		
		if(!newPassword.equals(oldPassword)) {
			throw new AonApiException("Las contraseñas no coinciden.");
		} 
		
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		auth.setSchema(aonToken.getSchema());
		
		String email = auth.getEmail();
		
//		String oldPass = Utils.createPasswordHash(email, oldPassword);
//		if(!oldPass.equalsIgnoreCase(auth.getPassword())) {
//			throw new AonApiException("La contraseña actual no coincide.");
//		}
//		
		String newPass = Utils.createPasswordHash(email, newPassword);
		auth.setPassword(newPass);
		
		AON_SOLUTIONS.updateAuthPassword(auth);
		
		return new JSONObject();
	}
	
	private JSONObject saveAvatar(AonApiData api) {
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		
		if(auth.getSchema() == null) {
			auth.setSchema(aonToken.getSchema());
		}

		AuthAttach aa = AON_SOLUTIONS.getAuthAttach(auth, f -> f.getAuthProperty().eq(auth.getAuth()).and(f.getTypeProperty().eq(AuthAttachType.AVATAR.value())));
		
		String base64 = api.getData().optString(IJsonNames.CONTENT);
		String contentType = api.getData().optString(IJsonNames.CONTENT_TYPE);
		byte[] fileData = Base64.getDecoder().decode(base64);
		aa.setData(fileData)
			.setMimetype(MimeType.get(contentType))
			.setType(AuthAttachType.AVATAR)
			.setAuth(auth.getAuth());		
		AON_SOLUTIONS.saveAuthAttach(auth, aa);
		return new JSONObject();
	}
}
