package net.aonsolutions.aon.api.servlet;
import java.util.Date;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.StatDataJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.StatType;
import com.esferalia.aon.occam.api.model.stat.invoice.InvoiceChartType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiStatServlet", urlPatterns = {"/ms/api/stat/*"})
public class StatServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(StatServlet.class.getName());
	
	public static final String INVOICE = "/invoice";
	public static final String FINANCE = "/finance";
	
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
				.addRoute(INVOICE, StatServlet::getAction)
				.addRoute(FINANCE, StatServlet::getFinanceStat)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getAction(AonApiData api) {
		StatData<String, String, Double> result = AON.getStatData(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), getStatParams(api));
		return StatDataJSON.toJSON(result);
	}
	
	private static StatParams getStatParams(AonApiData api) {
		Date from = JsonUtils.getDate(api.getData(), IJsonNames.FROM);
		return new StatParams()
				.setChartType(InvoiceChartType.INVOICE_TYPE_BY_MONTHS_COMBO_CHART.value())
				.setDomain(api.getDomain().getId())
				.setStatType(StatType.INVOICE)
				.setFrom(from != null ? from : AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(new Date(), -11)))
				.setTo(AonDateUtils.getMonthLastDay(new Date()))
				;
	}
	
	private static JSONObject getFinanceStat(AonApiData api) {
		StatData<String, String, Double> result = AON.getFinanceStat(
				api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
				getFinanceStatFilter(api));
		return StatDataJSON.toJSON(result);
	}
	
	private static FinanceFilter getFinanceStatFilter(AonApiData api) {
		Date from = JsonUtils.getDate(api.getData(), IJsonNames.FROM);
		Date to = JsonUtils.getDate(api.getData(), IJsonNames.TO);
		Integer status = 0;
		return f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getDueDateProperty().between(from, to)).and(f.getStatusProperty().eq(status.byteValue()));
	}
	
}
