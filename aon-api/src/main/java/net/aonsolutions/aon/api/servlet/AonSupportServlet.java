package net.aonsolutions.aon.api.servlet;
import java.util.Date;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.type.AppParam;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonSupportServlet", urlPatterns = {"/ms/api/aonsupport/*"})
public class AonSupportServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(AonSupportServlet.class.getName());
	
	public static final String SUPPORT = "/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}

	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
				.addRoute(SUPPORT, AonSupportServlet::getSupport)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(SUPPORT, AonSupportServlet::putAction)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getSupport(AonApiData api) {
		
		ApplicationParameter p = AON.getApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), 
		api.getUser().getLogin(), AppParam.AON_SUPPORT_ENABLED);
		
		return new JSONObject().put(IJsonNames.VALUE, !p.isEmpty()) ;
	}
	
	private static JSONObject putAction(AonApiData api) {
		boolean value = JsonUtils.getboolean(api.getData(), IJsonNames.VALUE);
	
		if(value){
			Occam occam = new Occam()
				.setDomain(api.getDomain().getId())
				.setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
			
			ApplicationParameter param = new  ApplicationParameter()
					.setDomain(api.getDomain().getId())
					.setName(AppParam.AON_SUPPORT_ENABLED)
					.setValue(String.valueOf(new Date().getTime()));
			AON.saveApplicationParameter(occam, param);
		}else{
			AON.deleteApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> f.getDomainProperty().eq(api.getDomain().getId())
			.and(f.getNameProperty().eq(AppParam.AON_SUPPORT_ENABLED.name()))
		);
		}
		
		return new JSONObject();
	}
	
}
