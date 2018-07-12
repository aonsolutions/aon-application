package com.code.aon.webservice.common;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;

@SuppressWarnings("serial")
@WebServlet(name = "SeriesServlet", urlPatterns = {"/series/*",
												   "/aon_gwt_aio/series/*",
												   "/aon_gwt_commercial/series/*"})
public class SeriesServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(SeriesServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Series Servlet - GET METHOD");
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String[] pathInfo = req.getPathInfo().split("/");
		
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Integer domainId = Integer.parseInt(req.getParameter(MSG.DOMAIN));
		Domain domain = AON.getDomain(domainName, domainId, userName);
	
		String md5 = Utils.getMd5(userName+domain.getName());
		
		if(accessToken.equals(md5)){
			if(pathInfo.length > 3){
				Object object = new Object();
				JSONObject meta = new JSONObject();
				switch (pathInfo[3]) {
				case "offer":
					object = getOfferSeries(domain, userName);
					break;
				case "invoice": 
					object = getInvoiceSeries(domain, userName);
					break;
				default:
					break;
				}
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}
	
	private JSONArray getOfferSeries(Domain domain, String login){
		JSONArray array = new JSONArray();
		AON.getSeriesStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getActiveProperty().eq((byte)1))
				.and(f.getOfferProperty().eq((byte) 1))).forEach(serie -> {
			array.put(ToJSON.objectToJSON(serie.getId(), serie.getCode()));
		});
		return array;
	}

	private JSONArray getInvoiceSeries(Domain domain, String login){
		JSONArray array = new JSONArray();
		AON.getSeriesStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getActiveProperty().eq((byte)1))
				.and(f.getInvoiceProperty().eq((byte) 1))).forEach(serie -> {
			array.put(ToJSON.objectToJSON(serie.getId(), serie.getCode()));
		});
		return array;
	}

}
