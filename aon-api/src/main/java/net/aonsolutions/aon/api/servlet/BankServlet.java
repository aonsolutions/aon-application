package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.RegistryBankJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.bank.nordigen.AonNordigen;
import net.aonsolutions.aon.bank.nordigen.NordigenUtils;

@SuppressWarnings("serial")
@WebServlet(name = "BankServlet", urlPatterns = { "/ms/api/bank/*" })
public class BankServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(BankServlet.class.getName());

	private static Occam occam = new Occam();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String iban = req.getParameter("iban");
		try {
			get(req, resp);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error occurred during GET request processing. IBAN: " + iban, e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			try {
				resp.getWriter().write("An unexpected error occurred while processing your request.");
			} catch (IOException ioException) {
				LOGGER.log(Level.SEVERE, "Error writing error response", ioException);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API NORDIGEN BANK SERVLET - POST METHOD");
		doPost(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/banks":
				response(req, resp, getBanks(api));
				break;
			case "/bank":
				response(req, resp, getBank(api));
				break;
			case "/link":
				response(req, resp, getUrlForLinkBankToNordigen(api, req.getParameter("iban")));
				break;
			case "/balances":
				response(req, resp, getBalances(api));
				break;
			case "/balancesOneAccount":
				response(req, resp, getBalancesByIban(api, req.getParameter("iban")));
				break;
			case "/movements":
				response(req, resp, getMovements(api));
				break;
			case "/movementsOneAccount":
				response(req, resp, getMovementsByIban(api, req.getParameter("iban")));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getBanks(AonApiData api) {
		
	    Company company = AON.getCompany(api.getDomain(), api.getUser(), 
	            f -> f.getDomainProperty().eq(api.getDomain().getId()));
	    occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName()).setUser(api.getUser().getLogin());
	    
	    NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
	    NordigenAccessToken token = nc.getToken();
	    
	    List<NordigenInstitution> instList = AonNordigen.getAllInstitutions(token);
	    List<RegistryBank> bankList = AON.getRegistryBanks(
	            api.getDomain(), api.getUser(), company.getId());

	    JSONArray resultArray = new JSONArray();
	    
	    for (RegistryBank bank : bankList) {
	        JSONObject bankJson = RegistryBankJSON.toJSON(bank);
	        String bankBic = bank.getBic();
	        
	        if (bankBic != null) {
	            for (NordigenInstitution institution : instList) {
	                String instBic = institution.getBic();
	                
	                if (instBic.equals(bankBic.replace("XXX", ""))) {
//	    	            if (instBic.equals(bankBic)) {
	                    bankJson.put("logo", institution.getLogo());
	                    break;  
	                }
	            }
	        }
	        resultArray.put(bankJson);
	    }
	    return resultArray;
	}
	
	//Nueva funcion
	private static JSONObject getBank(AonApiData api) {
	    String iban = api.getData().getString("iban");
	    occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName()).setUser(api.getUser().getLogin());

	    RegistryBank bank = AON.getRegistryBank(occam, f -> f.getBankAccountProperty().eq(iban));
	    JSONObject bankJson = RegistryBankJSON.toJSON(bank);

	    NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
	    NordigenAccessToken token = nc.getToken();
	    List<NordigenInstitution> instList = AonNordigen.getAllInstitutions(token);

	    String bankBic = bank.getBic();

	    if (bankBic != null) {
	        for (NordigenInstitution institution : instList) {
	            String instBic = institution.getBic();
	            if (instBic.equals(bankBic.replace("XXX", ""))) {
//	            if (instBic.equals(bankBic)) {
	                bankJson.put("logo", institution.getLogo());
	                break;  
	            }
	        }
	    }
	    return bankJson;
	}

	private static JSONObject getUrlForLinkBankToNordigen(AonApiData api, String iban) {
		JSONObject jsonLink = new JSONObject();
		try {
			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
					.setUser(api.getUser().getLogin());
			try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
				NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
				NordigenAccessToken token = nc.getToken();
				List<NordigenBankAccount> rAccounts = nc.getAccounts();
//				rAccounts = null;
				NordigenUtils.processAccountLinking(rAccounts, iban, token, jsonLink, occam);
			}
		} catch (Exception e) {
			NordigenUtils.logException(e);
			NordigenUtils.exceptionAddInfo("IBAN: " + iban 
					+ " Domain name:" +api.getDomain().getName() 
					+ " Domain ID:" + api.getDomain().getId().toString() 
					+ " User:" + api.getUser().getLogin());
		}
		return jsonLink;
	}

	
	private static JSONArray getBalances(AonApiData api) {
		try {
			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
					.setUser(api.getUser().getLogin());
			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
			NordigenAccessToken token = nc.getToken();
			List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
//			linkedAccountList = null;
			return NordigenUtils.processAccountBalances(linkedAccountList, token, occam);
		} catch (Exception e) {
			NordigenUtils.logException(e);
			return new JSONArray();
		}
	}

	private static JSONArray getBalancesByIban(AonApiData api, String iban) {
		try {
			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
					.setUser(api.getUser().getLogin());
			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
			NordigenAccessToken token = nc.getToken();
			List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
//			linkedAccountList = null;
			return NordigenUtils.processSingleAccountBalance(linkedAccountList, iban, token, occam);
		} catch (Exception e) {
			NordigenUtils.logException(e);
			return new JSONArray();
		}
	}

	private static JSONArray getMovements(AonApiData api) {
		try {
			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
					.setUser(api.getUser().getLogin());

			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
			NordigenAccessToken token = nc.getToken();
			List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
//			linkedAccountList = null;
			return NordigenUtils.convertAccountsMovementsToJsonArray(linkedAccountList, token, occam);
		} catch (Exception e) {
			NordigenUtils.logException(e);
			return new JSONArray();
		}
	}

	private static JSONArray getMovementsByIban(AonApiData api, String iban) {
		try {
			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
					.setUser(api.getUser().getLogin());

			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
			NordigenAccessToken token = nc.getToken();
//			token = null;
			List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
			return NordigenUtils.convertAcccountMovementsToJsonArray(linkedAccountList, iban, token, occam);
		} catch (Exception e) {
			NordigenUtils.logException(e);
			return new JSONArray();
		}
	}
}
