package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.AuthAttach;
import com.esferalia.aon.occam.api.model.security.AuthAttachType;
import com.esferalia.aon.watson.server.io.AonIOUtils;


@SuppressWarnings("serial")
@WebServlet(name = "AonAuthAvatar", urlPatterns = {"/ms/api/auth_avatar/*"})
public class AuthAvatarServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(AuthAvatarServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API DOWNLOAD INVOICE PDF AK");
		try {
			
			String param = req.getPathInfo().substring(1);
			param = new String(Base64.getDecoder().decode(param));
			JSONObject json = new JSONObject(param);

			String token = json.optString("session_id");
			AonToken aonToken = SECURITY.getAonToken(token);
			Auth auth = new Auth()
					.setAuth(aonToken.getAuth())
					.setSchema(aonToken.getSchema())
					.setUuid(aonToken.getUuid());

			AuthAttach aa = AON_SOLUTIONS.getAuthAttach(auth, f -> f.getAuthProperty().eq(auth.getAuth()).and(f.getTypeProperty().eq(AuthAttachType.AVATAR.value())));
			AonIOUtils.copy(new ByteArrayInputStream(aa.getData()), resp.getOutputStream());
			responseFile(resp, "avatar", aa.getMimetype());
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	

}
