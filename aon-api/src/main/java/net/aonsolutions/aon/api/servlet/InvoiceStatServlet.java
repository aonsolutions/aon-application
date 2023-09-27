package net.aonsolutions.aon.api.servlet;
import java.util.Date;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.StatType;
import com.esferalia.aon.occam.api.model.stat.invoice.InvoiceChartType;
import com.esferalia.aon.occam.api.model.type.ChartType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "ExampleServlet", urlPatterns = {"/ms/api/example/*"})
public class InvoiceStatServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(InvoiceStatServlet.class.getName());
	
	public static final String EXAMPLES = "/";
	public static final String EXAMPLE = "/:id";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(EXAMPLES, InvoiceStatServlet::getAction)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getAction(AonApiData api) {
		
		Date from = JsonUtils.getDate(api.getData(), IJsonNames.FROM);

		StatParams params = new StatParams()
				.setChartType(InvoiceChartType.INVOICE_TYPE_BY_MONTHS_COMBO_CHART.value())
				.setDomain(api.getDomain().getId())
				.setStatType(StatType.INVOICE)
				.setFrom(from)// AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(new Date(), -11)))
				.setTo(AonDateUtils.getMonthLastDay(new Date()))
				;
		StatData<String, String, Double> result = AON.getStatData(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), params);


		JSONObject json = new JSONObject();
		
		result.getMap().keySet().stream().forEach(mes -> {
			JSONObject typeJSON = new JSONObject();
			result.getMap().get(mes).keySet().stream().forEach(type -> {
				typeJSON.put(type, result.getMap().get(mes).get(type));
			});
			
			json.put(mes, typeJSON);
		});
		return json;
	}
	
}
