package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

@SuppressWarnings("serial")
@WebServlet(name = "AonTimeControlServlet", urlPatterns = {"/ms/api/timecontrol/*"})
public class TimeControlServlet extends HttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(TimeControlServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");
		Domain domain = AON.getDomain(domainName, domainId, "");
		Object object = new JSONObject();
		
		AonToken aonToken = SECURITY.getAonToken(token);
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);
		
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		domain = taskHolder.getDomain(); 
		TimeControl tc = new TimeControl();
			//	AON_SOLUTIONS.getTaskHolderTimeControl(domain.getName(), domain.getId(), "", taskHolder.getId(), startDate, endDate);

		object = tc.toJSON();
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, object, new JSONObject());	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API INVOICE SERVLET - POST METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");	
		Domain domain = AON.getDomain(domainName, domainId, "");
		User user = AON_SOLUTIONS.getUser(domain, token);
		JSONObject json = Utils.getRequestJSON(req);

		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, json, new JSONObject());	
	}

}
