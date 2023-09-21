package net.aonsolutions.aon.api.servlet;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;

@SuppressWarnings("serial")
@WebServlet(name = "AonNotificationTokenServlet", urlPatterns = {"/ms/api/notification-token/*"})
public class NotificationTokenServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(NotificationTokenServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON NOTIFICATION TOKEN SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		AonToken aonToken = SECURITY.getAonToken(token);
		String idStr = req.getParameter("id");
		Integer id = Integer.parseInt(idStr);
		Optional<DataResponseDetail> drd = AON.getDataResponseDetail(aonToken.getSchemaFirstDomain(), 0, "", f -> f.getDataResponseProperty().eq(id));
		JSONObject json = new JSONObject();
		
		if(drd.isPresent()) {
			json = new JSONObject(drd.get().getDataValue());
		}

		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, json, new JSONObject());
	}	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON NOTIFICATION TOKEN SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		AonToken aonToken = SECURITY.getAonToken(token);
		//Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		JSONObject json = Utils.getRequestJSON(req);
		
		DataResponse dr = new DataResponse()
				.setDomain(0)
				.setSource(DataResponseSource.NOTIFICATION_TOKEN)
				.setResponseDate(new Date());
		dr = AON.insertDataResponse(aonToken.getSchemaFirstDomain(), 0, "", dr);
		DataResponseDetail drd = new DataResponseDetail()
				.setDomain(0)
				.setDataResponse(dr.getId())
				.setDataVariable("json")
				.setDataValue(json.toString());
		AON.insertDataResponseDetail(aonToken.getSchemaFirstDomain(), 0, "", drd);
		Utils.addCorsHeader(resp);
		
		JSONObject response = new JSONObject();
		response.put("id", dr.getId());
		Utils.giveBack(req, resp, response, new JSONObject());
	}	
}
