package net.aonsolutions.aon.api.servlet.invoice;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistoryMapValue;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;

@WebServlet(name = "AonInvoiceCommunicationServlet", urlPatterns = {"/ms/api/communication/*"})
public class InvoiceCommunicationServlet extends AonApiHttpServlet{

	private static final long serialVersionUID = -5555314923648911908L;
	
	private static final Logger LOGGER  = Logger.getLogger(InvoiceCommunicationServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/history":
				response(req, resp, getInvoiceCommunicationHistory(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject getInvoiceCommunicationHistory(AonApiData api) {
		try {
			Integer invoiceId = JsonUtils.getInteger(api.getData(), IJsonNames.INVOICE);
			Occam occam = api.getOccam();
			try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
				Map<InvoiceCommunicationType, InvoiceCommunicationHistoryMapValue> h = InvoiceCommunicator.history(ctx, occam.getDomain(), invoiceId);
				return InvoiceCommunicator.historyToJSON(h).orElse(new JSONObject());
			}
		} catch (Exception e) {
			throw new AonApiException( e.getMessage(), e );
		}
	}
}
