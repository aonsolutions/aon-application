package net.aonsolutions.aon.api.servlet;
import java.text.ParseException;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.json.AccountTrialBalanceReportJSON;
import com.esferalia.aon.occam.api.json.AccountingReportParamsJSON;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;

@WebServlet(name = "AonAccountingServlet", urlPatterns = {"/ms/api/accounting/*"})
public class AccountingServlet extends AonApiHttpServlet{
		
	private static final long serialVersionUID = -8021598700474389724L;
	
	private static final Logger LOGGER  = Logger.getLogger(AccountingServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API ACCOUNTING SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);
			manage( req, resp );
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API ACCOUNTING SERVLET - POST METHOD");
		try {
			super.doPost(req, resp);
			manage( req, resp );
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
		
	private void manage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		switch (getPath()) {
		case "/trial":
			response(req, resp, getTrialBalance());
			break;
		default:
			throw new Exception("La ruta introducida es incorrecta.");
		}
	}

	private JSONObject getTrialBalance() throws ParseException {
		JSONObject jsonParams = getData();
		AccountingReportParams params = AccountingReportParamsJSON.fromJSON( jsonParams );
		AccountTrialBalanceReport report = ACCOUNTING.getAccountTrialBalance(getDomain().getName(), getDomain().getId(), getUser().getLogin(), params);
		return AccountTrialBalanceReportJSON.toJSON(report);
	}
	
}

