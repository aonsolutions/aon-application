package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.AON;
@SuppressWarnings("serial")
@WebServlet(name = "AonTaskHolderServlet", urlPatterns = {"/ms/api/taskholder/*"})
public class TaskHolderServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(TaskHolderServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TASKHOLDER SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);
			switch (getPath()) {
				case "/":
					response(req, resp, getTaskHolders());
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API TASKHOLDER SERVLET - POST METHOD");
		try {
			super.doPost(req, resp);
			switch (getPath()) {
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	


	private JSONArray getTaskHolders() {
		Domain domain = getDomain();
		JSONArray array = new JSONArray();
		AON.getTaskHolderStream(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()))
		.forEach(th->{
			JSONObject json = new JSONObject();
			json.put("id", th.getId());
			json.put("name", th.getName());
			json.put("company", th.getDomain().getDescription());
			json.put("domain_id", th.getDomain().getId());
			array.put(json);
		});
		return array;
	}
	
}
