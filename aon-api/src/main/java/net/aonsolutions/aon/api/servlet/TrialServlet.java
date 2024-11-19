package net.aonsolutions.aon.api.servlet;

import java.util.Optional;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonTrialServlet", urlPatterns = { "/ms/api/trial/*" })
public class TrialServlet extends AonApiHttpServlet {

    private static final Logger LOGGER = Logger.getLogger(TrialServlet.class.getName());

    public static final String TRIAL_DATA = "/";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    	 LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
         try {
             AonApiData api = initialize(req);

             Object object = new AonRouting(api)
             		.addRoute(TRIAL_DATA, TrialServlet::getTrialData)
                     .apply();

             response(req, resp, object);
         } catch (Exception e) {
             error(req, resp, e);
         }
    }
    
    private static JSONObject getTrialData(AonApiData api) {
    	JSONObject trialData = new JSONObject();
    	
    	Optional<ApplicationParameter> trialAppParam = AON.getApplicationParameterStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getNameProperty().eq(AppParam.TRIAL.name()).and(f.getDomainProperty().eq(api.getDomain().getId()))).findFirst();
    	if(!trialAppParam.isEmpty())
    		trialData.put("trialInvoices", trialAppParam.get().getValue());
    	
    	Integer saleInvoices = AON_SOLUTIONS.getInvoicesCount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getTypeProperty().eq(InvoiceType.SALES.value()).and(f.getDomainProperty().eq(api.getDomain().getId())));
    	trialData.put("saleInvoices", null == saleInvoices ? 0 : saleInvoices);
    	
    	Integer expenseInvoices = AON_SOLUTIONS.getInvoicesCount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getTypeProperty().eq(InvoiceType.PURCHASE.value()).or(f.getTypeProperty().eq(InvoiceType.EXPENSES.value())).and(f.getDomainProperty().eq(api.getDomain().getId())));
    	trialData.put("expenseInvoices", null == expenseInvoices ? 0 : expenseInvoices);
    	
    	Integer ticketInvoices = AON_SOLUTIONS.getInvoicesCount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getTypeProperty().eq(InvoiceType.UNDEDUCTIBLE.value()).and(f.getDomainProperty().eq(api.getDomain().getId())));
    	trialData.put("ticketInvoices", null == ticketInvoices ? 0 : ticketInvoices);
    	
    	return trialData;
    }
	
}
