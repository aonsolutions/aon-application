package net.aonsolutions.aon.api.servlet;
import java.util.Collection;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.json.AccountOperatingReportJSON;
import com.esferalia.aon.occam.api.json.AccountPeriodsJSON;
import com.esferalia.aon.occam.api.json.AccountTrialBalanceReportJSON;
import com.esferalia.aon.occam.api.json.AccountingReportParamsJSON;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@WebServlet(name = "AonAccountingServlet", urlPatterns = {"/ms/api/accounting/*"})
public class AccountingServlet extends AonApiHttpServlet{
		
	private static final long serialVersionUID = -8021598700474389724L;
	
	private static final Logger LOGGER  = Logger.getLogger(AccountingServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API ACCOUNTING SERVLET - GET METHOD");
		try {
			manage( req, resp );
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API ACCOUNTING SERVLET - POST METHOD");
		try {
			manage( req, resp );
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
		
	private void manage(HttpServletRequest req, HttpServletResponse resp) {
		AonApiData api = initialize(req);
		
		switch (api.getPath()) {
		case "/trial":
			response(req, resp, getTrialBalance(api));
			break;
		case "/pyg":
			response(req, resp, getOperatingBalance(api));
			break;
		case "/periods":
			response(req, resp, getPeriods(api));
			break;
		default:
			throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
		}
	}

	private JSONObject getTrialBalance(AonApiData api) {
		JSONObject jsonParams = api.getData();
		AccountingReportParams params = AccountingReportParamsJSON.fromJSON( jsonParams );
		AccountTrialBalanceReport report = ACCOUNTING.getAccountTrialBalance(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), params);
		return AccountTrialBalanceReportJSON.toJSON(report);
	}
	
	private JSONObject getOperatingBalance(AonApiData api) {
		JSONObject jsonParams = api.getData();
		AccountingReportParams params = AccountingReportParamsJSON.fromJSON( jsonParams );
		AccountOperatingReport report = ACCOUNTING.getAccountOperatingReport(api.getDomain().getName(), api.getUser().getLogin(), api.getDomain().getId(), params);
		return AccountOperatingReportJSON.toJSON(report);
	}
	
	private JSONArray getPeriods(AonApiData api) {
		JSONObject jsonParams = api.getData();
		AccountingReportParams params = AccountingReportParamsJSON.fromJSON( jsonParams );
		Collection<AccountPeriod> periods = ACCOUNTING.getDomainPeriods(params.getDomainName(), params.getDomain(), params.getUser());
		return AccountPeriodsJSON.toJSON(periods);
	}
	
}

