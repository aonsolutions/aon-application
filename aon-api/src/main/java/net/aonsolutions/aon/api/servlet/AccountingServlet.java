package net.aonsolutions.aon.api.servlet;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.json.AccountOperatingReportJSON;
import com.esferalia.aon.occam.api.json.AccountPeriodsJSON;
import com.esferalia.aon.occam.api.json.AccountTrialBalanceReportJSON;
import com.esferalia.aon.occam.api.json.AccountingExpenseJSON;
import com.esferalia.aon.occam.api.json.AccountingIncomeJSON;
import com.esferalia.aon.occam.api.json.AccountingReportParamsJSON;
import com.esferalia.aon.occam.api.json.DomainInvoiceStatParamsJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.DomainInvoiceStatParams;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.accounting.AccountingExpense;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;

@WebServlet(name = "AonAccountingServlet", urlPatterns = {"/ms/api/accounting/*"})
public class AccountingServlet extends AonApiHttpServlet{
		
	private static final long serialVersionUID = -8021598700474389724L;
	
	private static final Logger LOGGER  = Logger.getLogger(AccountingServlet.class.getName());
	public static final String TRIAL = "/trial";
	public static final String PYG = "/pyg";
	public static final String PERIODS = "/periods";
	public static final String EXPENSES = "/expenses";
	public static final String INCOMES = "/incomes";
	public static final String INVOICES_COUNTERS = "/invoicesCounters";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(TRIAL, AccountingServlet::getTrialBalance)
				.addRoute(PYG, AccountingServlet::getOperatingBalance)
				.addRoute(PERIODS, AccountingServlet::getPeriods)
				.addRoute(EXPENSES, AccountingServlet::getExpenses)
				.addRoute(INCOMES, AccountingServlet::getIncomes)
				.addRoute(INVOICES_COUNTERS, AccountingServlet::getInvoicesCounters)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(TRIAL, AccountingServlet::getTrialBalance)
				.addRoute(PYG, AccountingServlet::getOperatingBalance)
				.addRoute(PERIODS, AccountingServlet::getPeriods)
				.addRoute(EXPENSES, AccountingServlet::setExpense)
				.addRoute(INCOMES, AccountingServlet::setIncome)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}

	}
		
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(EXPENSES, AccountingServlet::deleteExpense)
				.addRoute(INCOMES, AccountingServlet::deleteIncome)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}

	}

	private static JSONObject getTrialBalance(AonApiData api) {
		JSONObject jsonParams = api.getData();
		AccountingReportParams params = AccountingReportParamsJSON.fromJSON( jsonParams );
		AccountTrialBalanceReport report = ACCOUNTING.getAccountTrialBalance(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), params);
		return AccountTrialBalanceReportJSON.toJSON(report);
	}
	
	private static JSONObject getOperatingBalance(AonApiData api) {
		JSONObject jsonParams = api.getData();
		AccountingReportParams params = AccountingReportParamsJSON.fromJSON( jsonParams );
		AccountOperatingReport report = ACCOUNTING.getAccountOperatingReport(api.getDomain().getName(), api.getUser().getLogin(), api.getDomain().getId(), params);
		return AccountOperatingReportJSON.toJSON(report);
	}
	
	private static JSONArray getPeriods(AonApiData api) {
		JSONObject jsonParams = api.getData();
		AccountingReportParams params = AccountingReportParamsJSON.fromJSON( jsonParams );
		Occam occam = new Occam()
			.setDomainName(params.getDomainName())
			.setDomain( params.getDomain())
			.setUser( params.getUser()); 		
		Collection<AccountPeriod> periods = ACCOUNTING.getDomainPeriods(occam);
		return AccountPeriodsJSON.toJSON(periods);
	}
	
	private static JSONArray getExpenses(AonApiData api) {
		JSONObject jsonParams = api.getData();
		String query = null;
		if (jsonParams != null) {
			query = JsonUtils.getString(jsonParams, IJsonNames.VALUE);
		}
		
		Stream<JSONObject> stream = ACCOUNTING.getAccountingExpenses(api.getOccam(),api.getDomain().getId(), query)
			.map( AccountingExpenseJSON::to )
			.filter( Optional::isPresent )
			.map( Optional::get );
		JSONArray array = stream.collect(Collector.of(JSONArray::new, JSONArray::put, JSONArray::put)); 
		stream.close();
		return array;
	}
	private static JSONObject setExpense(AonApiData api) {
		JSONObject jsonParams = api.getData();
		if (JsonUtils.isEmpty(jsonParams)) {
			throw new AonCoreException("El ingreso es un dato obligatorio.");
		}
		AccountingExpense expense = AccountingExpenseJSON.from( jsonParams )
			.orElseThrow( () -> new AonCoreException("El ingreso es un dato obligatorio."));
		expense = ACCOUNTING.saveAccountingExpense(api.getOccam(), expense);
		return AccountingExpenseJSON.to( expense )
			.orElse(new JSONObject());
	}
	
	private static JSONObject deleteExpense(AonApiData api) {
		JSONObject jsonParams = api.getData();
		if (JsonUtils.isEmpty(jsonParams)) {
			throw new AonCoreException("El ingreso es un dato obligatorio.");
		}
		AccountingExpense expense = AccountingExpenseJSON.from( jsonParams )
			.orElseThrow( () -> new AonCoreException("El ingreso es un dato obligatorio."));
		ACCOUNTING.deleteAccountingExpense(api.getOccam(), expense );
		return new JSONObject();
	}
	
	private static JSONArray getIncomes(AonApiData api) {
		JSONObject jsonParams = api.getData();
		String query = null;
		if (jsonParams != null) {
			query = JsonUtils.getString(jsonParams, IJsonNames.VALUE);
		}
		
		Stream<JSONObject> stream = ACCOUNTING.getAccountingIncomes(api.getOccam(),api.getDomain().getId(), query)
			.map( AccountingIncomeJSON::to )
			.filter( Optional::isPresent )
			.map( Optional::get );
		JSONArray array = stream.collect(Collector.of(JSONArray::new, JSONArray::put, JSONArray::put)); 
		stream.close();
		return array;
	}
	private static JSONObject setIncome(AonApiData api) {
		JSONObject jsonParams = api.getData();
		if (JsonUtils.isEmpty(jsonParams)) {
			throw new AonCoreException("El ingreso es un dato obligatorio.");
		}
		AccountingIncome income = AccountingIncomeJSON.from( jsonParams )
			.orElseThrow( () -> new AonCoreException("El ingreso es un dato obligatorio."));
		income = ACCOUNTING.saveAccountingIncome(api.getOccam(), income);
		return AccountingIncomeJSON.to( income )
			.orElse(new JSONObject());
	}
	private static JSONObject deleteIncome(AonApiData api) {
		JSONObject jsonParams = api.getData();
		if (JsonUtils.isEmpty(jsonParams)) {
			throw new AonCoreException("El ingreso es un dato obligatorio.");
		}
		AccountingIncome income = AccountingIncomeJSON.from( jsonParams )
			.orElseThrow( () -> new AonCoreException("El ingreso es un dato obligatorio."));
		ACCOUNTING.deleteAccountingIncome(api.getOccam(), income );
		return new JSONObject();
	}
	
	private static JSONObject getInvoicesCounters(AonApiData api) {
//		JSONObject jsonParams = api.getData();
//		DomainInvoiceStatParamsJSON.from( jsonParams )
//			.ifPresent( params -> {
//				ACCOUNTING.deleteAccountingIncome(api.getOccam(), income );
//				
//			LOGGER.info("getInvoicesCounters: " + json.toString());
//		});
		return new JSONObject();
	}
}

