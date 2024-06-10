package net.aonsolutions.aon.api.servlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.RegistryBankJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountBalanceJSON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.bank.nordigen.AonNordigen;

@SuppressWarnings("serial")
@WebServlet(name = "BankServlet", urlPatterns = { "/ms/api/bank/*" })
public class BankServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(BankServlet.class.getName());
	private static Occam occam = new Occam();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API NORDIGEN BANK SERVLET - GET METHOD");
		get(req, resp);
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
			case "/allAccounts":
				response(req, resp, getAllAccounts(api));
				break;
			case "/oneBank":
				response(req, resp, getBank(api, req.getParameter("iban")));
				break;
			case "/link":
				response(req, resp, getUrlForLinkBankToNordigen(api, req.getParameter("iban")));
				break;
			case "/unlinkedAccounts":
				response(req, resp, getUnlinkedAccounts(api));
				break;
			case "/linkedAccounts":
				response(req, resp, getLinkedAccounts(api));
				break;
			case "/balances":
				response(req, resp, getBalances(api));
				break;
			case "/balancesOneAccount":
				response(req, resp, getBalancesOfOneAccountByIban(api, req.getParameter("iban")));
				break;
			case "/movements":
				response(req, resp, getMovements(api));
				break;
			case "/movementsOneAccount":
				response(req, resp, getMovementsOfOneAccountByIban(api, req.getParameter("iban")));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private static JSONArray getBank(AonApiData api, String iban) {
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
		NordigenConfiguration nc;
		JSONObject jsonBank = new JSONObject();
		JSONArray arrayBank = new JSONArray();
		List<NordigenBankAccount> accounts = new ArrayList<>();
		NordigenBankAccount bank;
		try {
			nc = AonNordigen.getConfiguration(occam);
			accounts = nc.getAccounts();
			for (int i = 0; i < accounts.size(); i++) {
				bank = accounts.get(i);
				if (bank.getIban().equals(iban)) {
					jsonBank = RegistryBankJSON.toJSON(bank.getRbank());
				}
			}
			arrayBank.put(getLogoBankOfOneAccountByIban(occam, iban));
			arrayBank.put(jsonBank);
		} catch (Exception e) {
			return new JSONArray();
		}
		return arrayBank;
	}

	private static JSONObject getLogoBankOfOneAccountByIban(Occam occam, String iban) {
		NordigenConfiguration nc;
		List<NordigenBankAccount> accounts = new ArrayList<>();
		NordigenBankAccount bank;
		JSONObject jsonLogo = new JSONObject();
		try {
			nc = AonNordigen.getConfiguration(occam);
			NordigenAccessToken token = nc.getToken();
			accounts = nc.getAccounts();
			for (int i = 0; i < accounts.size(); i++) {
				bank = accounts.get(i);
				if (bank.getIban().equals(iban)) {
					String bic = bank.getRbank().getBic().substring(0, 7);
					List<NordigenInstitution> nordigenInstitutions = AonNordigen.getInstitutionsByBic(token, bic);
					jsonLogo.put("logo", nordigenInstitutions.get(0).getLogo());
				}
			}
		} catch (Exception e) {
			return new JSONObject().put("logo", "");
		}
		return jsonLogo;
	}

	private static JSONArray getBalances(AonApiData api) {
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
		List<NordigenAccountBalance> balances = new ArrayList<>();
		JSONArray balancesArray = new JSONArray();
		JSONObject object = new JSONObject();
		try {
			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
			NordigenAccessToken token = nc.getToken();
			NordigenBankAccount nordigenAccount;
			List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
			NordigenBankAccount nordigenTrueAccount;
			ArrayList<String> ibans = new ArrayList<>();
			for (int i = 0; i < linkedAccountList.size(); i++) {
				ibans.add(linkedAccountList.get(i).getIban());
				if (linkedAccountList.get(i).getIban().equals(ibans.get(i))) {
					nordigenAccount = linkedAccountList.get(i);
					nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, nordigenAccount);
					balances = nordigenTrueAccount.getBalances();
					object.put("all balances", NordigenAccountBalanceJSON.to(balances));
					balancesArray.put(object);
				}
			}
		} catch (Exception e) {
			return new JSONArray();
		}

		return balancesArray;
	}

	private static JSONArray getBalancesOfOneAccountByIban(AonApiData api, String iban) {
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
		List<NordigenAccountBalance> balances;
		NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
		NordigenAccessToken token = nc.getToken();
		NordigenBankAccount nordigenAccount;
		List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
		NordigenBankAccount nordigenTrueAccount = new NordigenBankAccount();
		ArrayList<String> ibans = new ArrayList<>();
		for (int i = 0; i < linkedAccountList.size(); i++) {
			ibans.add(linkedAccountList.get(i).getIban());
			if (linkedAccountList.get(i).getIban().equals(iban)) {
				nordigenAccount = linkedAccountList.get(i);
				nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, nordigenAccount);
				break;
			}
		}
		balances = nordigenTrueAccount.getBalances();
		return NordigenAccountBalanceJSON.to(balances);
	}

	private static JSONArray getMovements(AonApiData api) {
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
		JSONObject movementsJson;
		JSONArray movementsJsonArray = new JSONArray();
		List<NordigenBankStatement> movements;
		NordigenBankAccount nordigenTrueAccount;
		NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
		NordigenAccessToken token = nc.getToken();
		List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
		for (NordigenBankAccount account : linkedAccountList) {
			nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, account);
			Date lastAccessedDate = nordigenTrueAccount.getMetadata().getLastAccessed();
			boolean linked = nordigenTrueAccount.isLinked();
			movements = AonNordigen.getMovements(token, occam, nordigenTrueAccount, lastAccessedDate, linked);
			movementsJson = movementsToJson(movements, nordigenTrueAccount);
			movementsJsonArray.put(movementsJson);
			//905325d3-bc92-47b4-801e-84b39461ffa7 
		}

		return movementsJsonArray;
	}

	private static JSONArray getMovementsOfOneAccountByIban(AonApiData api, String iban) {
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
		JSONObject movementsJson;
		List<NordigenBankStatement> movements;
		JSONArray movementsJsonArray = new JSONArray();
		NordigenBankAccount nordigenTrueAccount;
		NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
		NordigenAccessToken token = nc.getToken();
		NordigenBankAccount nordigenAccount;
		List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
		for (int i = 0; i < linkedAccountList.size(); i++) {

			if (linkedAccountList.get(i).getIban().equals(iban)) {
				nordigenAccount = linkedAccountList.get(i);
				nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, nordigenAccount);
				Date lastAccessedDate = nordigenTrueAccount.getMetadata().getLastAccessed();
				boolean linked = nordigenTrueAccount.isLinked();
				movements = AonNordigen.getMovements(token, occam, nordigenTrueAccount, lastAccessedDate, linked);
				movementsJson = movementsToJson(movements, nordigenTrueAccount);
				movementsJsonArray.put(movementsJson);
			}
		}
		return movementsJsonArray;
	}

	private static JSONArray getAllAccounts(AonApiData api) {
		Occam occam = new Occam();
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
		try {
			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
			JSONArray array = new JSONArray();
			List<NordigenBankAccount> accounts = nc.getAccounts();
			for (int i = 0; i < accounts.size(); i++) {
				RegistryBank bank = accounts.get(i).getRbank();
				if(accounts.get(i).isLinked()) {				
				array.put(RegistryBankJSON.toJSON(bank).put("logo",
						getLogoBankOfOneAccountByIban(occam, bank.getBankAccount().getIban()).optString("logo")).put("SyncStatus", "sync"));
				}else {
				array.put(RegistryBankJSON.toJSON(bank).put("logo",
						getLogoBankOfOneAccountByIban(occam, bank.getBankAccount().getIban()).optString("logo")).put("SyncStatus", ""));
				}
			}
			return array;
		} catch (Exception e) {
			return new JSONArray();
		}
	
	}

	private JSONArray getLinkedAccounts(AonApiData api) {
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
		NordigenConfiguration nc;
		List<NordigenBankAccount> linkedAccounts;
		JSONArray array = new JSONArray();
			nc = AonNordigen.getConfiguration(occam);
			linkedAccounts = nc.getLinkedAccounts();
			for (int i = 0; i < linkedAccounts.size(); i++) {
				RegistryBank bank = linkedAccounts.get(i).getRbank();
				array.put(RegistryBankJSON.toJSON(bank).put("SyncStatus", "sync"));
			}
		return array;
	}

	private static JSONArray getUnlinkedAccounts(AonApiData api) {
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
		NordigenConfiguration nc;
		List<NordigenBankAccount> unLinkedAccounts;
			nc = AonNordigen.getConfiguration(occam);
			unLinkedAccounts = nc.getUnlinkedAccounts();
			JSONArray array = new JSONArray();
			for (int i = 0; i < unLinkedAccounts.size(); i++) {
				RegistryBank bank = unLinkedAccounts.get(i).getRbank();
				array.put(RegistryBankJSON.toJSON(bank).put("SyncStatus", ""));
			}
		return array;
	}

	private static JSONObject getUrlForLinkBankToNordigen(AonApiData api, String iban) {
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
		String link = "";
		JSONObject jsonLink = new JSONObject();
		List<NordigenBankAccount> rAccounts = new ArrayList<>();
		NordigenConfiguration nc;
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			nc = AonNordigen.getConfiguration(occam);
			NordigenAccessToken token = nc.getToken();
			rAccounts = nc.getAccounts();
			for (int i = 0; i < rAccounts.size(); i++) {
				NordigenBankAccount account = nc.getAccounts().get(i);
				if (account.isLinked() && account.getIban().equals(iban)) {
					jsonLink.put("link", "ya esta linkeada!");
				} else if (!account.isLinked() && account.getIban().equals(iban)) {
					NordigenRequisition requistion = AonNordigen.addAccount(token, occam, account);
					link = requistion.getLink();
					jsonLink.put("link", link);
				}
			}
			return jsonLink;
		}
	}

	private static JSONObject movementsToJson(List<NordigenBankStatement> movements, NordigenBankAccount nordigenTrueAccount) {
		JSONArray movementsJsonArray = new JSONArray();
		for (NordigenBankStatement movement : movements) {
			JSONObject movementJson = new JSONObject();
			movementJson.put("operationDate", movement.getOperationDate());
			movementJson.put("description", movement.getDescription());
			movementJson.put("status", movement.getStatus());
			movementJson.put("amount", movement.getAmount());
			movementJson.put("totalAmount", movement.getCurrentBalance());
			movementJson.put("iban", nordigenTrueAccount.getIban());
			movementsJsonArray.put(movementJson);
		}
		JSONObject movementsJson = new JSONObject();
		movementsJson.put("movements", movementsJsonArray);
		return movementsJson;
	}
}
