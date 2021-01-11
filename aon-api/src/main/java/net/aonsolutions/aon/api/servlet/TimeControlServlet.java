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

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlStatus;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "AonTimeControlServlet", urlPatterns = {"/ms/api/timecontrol/*"})
public class TimeControlServlet extends HttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(TimeControlServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TIMECONTROL SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		
		AonToken aonToken = SECURITY.getAonToken(token);
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);

		JSONObject responseObject = getTimeControl(taskHolder); 

		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, responseObject, new JSONObject());	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API TIMECONTROL SERVLET - POST METHOD");
		String token = req.getHeader("session_id");

		JSONObject json = Utils.getRequestJSON(req);

		AonToken aonToken = SECURITY.getAonToken(token);
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);
		
		save(taskHolder, json);
		JSONObject responseObject = getTimeControl(taskHolder); 
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, responseObject, new JSONObject());	
	}

	private JSONObject getTimeControl(TaskHolder taskHolder) {
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		TimeControl tc = new TimeControl();
		if(taskHolder != null && taskHolder.getId() != null) {
			AON_SOLUTIONS.getTaskHolderTimeControl(taskHolder.getDomain(), "", taskHolder.getId(), startDate, endDate);
		}
		return tc.toJSON();
	}
	
	private void save(TaskHolder taskHolder, JSONObject json) {
		Coordinates coordinates = new Coordinates(json.optString("coordinates"));
		
		TimeControlDetail tcd = new TimeControlDetail()
				.setDomain(taskHolder.getDomain())
				.setTaskHolder(taskHolder)
				.setComments(json.optString("comments"))
				.setCoordinates(coordinates)
				.setDate(new Date())
				.setLocation(new Location())
				.setStatus(TimeControlStatus.safeValueOf(json.optString("status")));
		
		AON_SOLUTIONS.saveTimeControlDetail(tcd.getDomain(), "", tcd);
	}
	
}
