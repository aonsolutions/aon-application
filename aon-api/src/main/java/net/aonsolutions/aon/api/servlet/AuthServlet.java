package net.aonsolutions.aon.api.servlet;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.AUTH;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.AuthAttach;
import com.esferalia.aon.occam.api.model.security.AuthAttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.aws.s3.S3;

@SuppressWarnings("serial")
@WebServlet(name = "AonAuthServlet", urlPatterns = {"/ms/api/auth/*"})
public class AuthServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(AuthServlet.class.getName());

	public static final String ROOT = "/";
	public static final String PASSWORD = "/password";
    public static final String AVATAR = "/avatar";

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON AUTH SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
			AonToken aonToken = null;
			Auth auth;
			String email = JsonUtils.getString(api.getData(), IJsonNames.EMAIL);
			if(!AonStringUtils.isBlank(email)) {
			    auth = AUTH.getAuthByEmail(email);
			    if(auth.isEmpty()) {
	                auth = AON_SOLUTIONS.getAuth(api.getData().optString(IJsonNames.EMAIL));
                    getAvatarUrl(api, auth, aonToken);
                    AUTH.backup(auth.getEmail());
			    } else {
			        auth.setAvatar(S3.getPresignedURL(S3.AUTH_ATTACH_BUCKET,
			            auth.getUuid(), AonDateUtils.addDays(new Date(), 1)));
			    }
			} else {
				aonToken = SECURITY.getAonToken(api.getToken());
				auth = AUTH.getAuthByUuid(aonToken.getUuid());
                if(auth.isEmpty()) {
                    auth = aonToken.getSchemaFirstDomain() != null 
                            ? AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth())
                            : AON_SOLUTIONS.getAuth(aonToken.getAuth());
                    getAvatarUrl(api, auth, aonToken);
                    AUTH.backup(auth.getEmail());
                } else {
                    auth.setAvatar(S3.getPresignedURL(S3.AUTH_ATTACH_BUCKET,
                        auth.getUuid(), AonDateUtils.addDays(new Date(), 1)));
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
		doGet(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);
            
            Object object = new AonRouting(api)
                .addRoute(ROOT, AuthServlet::saveAuth)
                .addRoute(PASSWORD, AuthServlet::savePassword)
                .addRoute(AVATAR, AuthServlet::saveAvatar)
                .apply();
            
            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
	
	private static JSONObject saveAuth(AonApiData api) {
	    Auth auth = AuthJSON.fromJSON(api.getData());
	    AUTH.saveAuth(auth);
	    return new JSONObject();
    }
	
	private static JSONObject savePassword(AonApiData api) throws AonApiException {
		JSONObject params = api.getData();
		String password = params.optString("password");
		
		if(password.isEmpty()) {
			throw new AonApiException("La contraseña es requerida.");
		} 

		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		Auth auth = AUTH.getAuthByUuid(aonToken.getUuid());
		if(auth.isEmpty()) {
		    throw new AonApiException("Es necesario actualizar la información del usuario antes de cambiar la contraseña.");
		}
		String newPass = Utils.createPasswordHash(auth.getEmail(), password);

		AUTH.savePassword(aonToken.getUuid(), newPass);
		return new JSONObject();
	}
	
	@Deprecated
	private void getAvatarUrl(AonApiData api, Auth auth, AonToken aonToken) {
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
	
	private static JSONObject saveAvatar(AonApiData api) {
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		Auth auth = AUTH.getAuthByUuid(aonToken.getUuid());
	    if(auth.isEmpty()) {
	        throw new AonApiException("Es necesario actualizar la información del usuario antes de subir la imagen.");
	    }
	      
		String base64 = api.getData().optString(IJsonNames.CONTENT);
		String contentType = api.getData().optString(IJsonNames.CONTENT_TYPE);
		byte[] fileData = Base64.getDecoder().decode(base64);
		AUTH.saveAvatar(aonToken.getUuid(), fileData, MimeType.get(contentType));
		return new JSONObject();
	}
}
