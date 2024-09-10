package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryBankJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.NordigenBankStatementProperties;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.StatementStatus;
import com.esferalia.aon.watson.util.AonStringUtils;

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
		post(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			//Lista de bancos
			case "/banks":
				response(req, resp, getBanks(api));
				break;
			//Un banco
			case "/bank":
				response(req, resp, getBank(api));
				break;
			//Link para vincular con Nordigen
			case "/link":
				response(req, resp, getUrlForLinkBankToNordigen(api));
				break;
			//Balances desde Nordigen
			case "/balances":
				response(req, resp, getBalances(api));
				break;
			//Movimientos desde Nordigen
			case "/movements":
				response(req, resp, getMovements(api));
				break;
			//Movimientos desde bbdd
			case "/movementsBD":
				response(req, resp, getMovementsFromBD(api));
				break;
//				case "/movementsOneAccount":
//				response(req, resp, getMovementsByIban(api));
//				break;
//				case "/balancesOneAccount":
//				response(req, resp, getBalancesByIban(api));
//				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private void post(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			//Introduce los movimientos obtenidos de Nordigen a BD
			case "/movements":
				response(req, resp, insertMovesIntoBD(api));
				break;
			//Introduce los balances obtenidos de Nordigen a BD
			case "/balances":
				response(req, resp, insertBalancesIntoBD(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private static boolean insertMovesIntoBD(AonApiData api) {
		try {
			int id = api.getData().optInt("id");
			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
					.setUser(api.getUser().getLogin());

			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);

			NordigenAccessToken token = nc.getToken();
			List<NordigenBankAccount> lista = nc.getAccounts();
			NordigenBankAccount bank2 = new NordigenBankAccount();

			for (int i = 0; i < lista.size(); i++) {
				if (lista.get(i).getRbank().getId().equals(id)) {
					bank2 = AonNordigen.setBankAccountValues(occam, token, lista.get(i));
				}
			}
			AonNordigen.insertStatements(occam, bank2);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean insertBalancesIntoBD(AonApiData api) {
		try {
			int id = api.getData().optInt("id");
			NordigenBankAccount bank = new NordigenBankAccount();
			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
					.setUser(api.getUser().getLogin());
			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
			List<NordigenBankAccount> lista = nc.getAccounts();
			for (int i = 0; i < lista.size(); i++) {
				if (lista.get(i).getRbank().getId().equals(id)) {
					bank = lista.get(i);
				}
			}
			AonNordigen.updateAccountBalances(bank, occam);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static JSONArray getMovementsFromBD(AonApiData api) {
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());
		JSONArray array = new JSONArray();
		int page = api.getData().optInt("page");
		int perPage = api.getData().optInt("perPage");
		BankStatementFilter filter = new BankStatementFilter().setId(api.getData().optInt(IJsonNames.ID))
				.setTo(JsonUtils.getDate(api.getData(), IJsonNames.TO))
				.setFrom(JsonUtils.getDate(api.getData(), IJsonNames.FROM))
				.setAmount(api.getData().optString(IJsonNames.AMOUNT))
				.setDescription(api.getData().optString(IJsonNames.DESCRIPTION))
				.setStatus(!AonStringUtils.isBlank(api.getData().optString("status"))
						? StatementStatus.valueOf(api.getData().optString("status")).value()
						: null);

		AonNordigen.getMovementsFromBD(occam, f -> statementsFilter(f, api.getDomain().getId(), filter), page, perPage)
				.forEach(bankStatement -> array.put(NordigenUtils.bankStatementJSON(bankStatement)));
		return array;
	}

	private static JSONArray getBanks(AonApiData api) {

		Company company = AON.getCompany(api.getDomain(), api.getUser(),
				f -> f.getDomainProperty().eq(api.getDomain().getId()));
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());

		NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
		NordigenAccessToken token = nc.getToken();

		List<NordigenInstitution> instList = AonNordigen.getAllInstitutions(token);

		List<RegistryBank> bankList = AON.getRegistryBanks(api.getDomain(), api.getUser(), company.getId());

		JSONArray resultArray = new JSONArray();

		for (RegistryBank bank : bankList) {
			JSONObject bankJson = RegistryBankJSON.toJSON(bank);
			String bankBic = bank.getBic();

			if (bankBic != null) {
				for (NordigenInstitution institution : instList) {
					String instBic = institution.getBic();

					if (instBic.equals(bankBic.replace("XXX", ""))) {
						bankJson.put("logo", institution.getLogo());
						break;
					}
				}
			}
			resultArray.put(bankJson);
		}
		return resultArray;
	}

	private static JSONObject getBank(AonApiData api) {
		int id = api.getData().optInt("id");
		occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
				.setUser(api.getUser().getLogin());

		RegistryBank bank = AON.getRegistryBank(occam, f -> f.getIdProperty().eq(id));
		JSONObject bankJson = RegistryBankJSON.toJSON(bank);

		NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
		NordigenAccessToken token = nc.getToken();
		List<NordigenInstitution> instList = AonNordigen.getAllInstitutions(token);

		String bankBic = bank.getBic();

		if (bankBic != null) {
			for (NordigenInstitution institution : instList) {
				String instBic = institution.getBic();
				if (instBic.equals(bankBic.replace("XXX", ""))) {
					bankJson.put("logo", institution.getLogo());
					break;
				}
			}
		}
		return bankJson;
	}

	private static JSONObject getUrlForLinkBankToNordigen(AonApiData api) {
		JSONObject jsonLink = new JSONObject();
		int id = api.getData().optInt("id");
		try {
			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
					.setUser(api.getUser().getLogin());
			try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
				NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
				NordigenAccessToken token = nc.getToken();
				List<NordigenBankAccount> rAccounts = nc.getAccounts();
				NordigenUtils.processAccountLinking(rAccounts, id, token, jsonLink, occam);
			}
		} catch (Exception e) {
			NordigenUtils.logException(e);
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
			return NordigenUtils.processAccountBalances(linkedAccountList, token, occam);
		} catch (Exception e) {
			NordigenUtils.logException(e);
			return new JSONArray();
		}
	}

//	private static JSONArray getBalancesByIban(AonApiData api) {
//		int id = api.getData().optInt("id");
//		try {
//			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
//					.setUser(api.getUser().getLogin());
//			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
//			NordigenAccessToken token = nc.getToken();
//			List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
//			return NordigenUtils.processSingleAccountBalance(linkedAccountList, id, token, occam);
//		} catch (Exception e) {
//			NordigenUtils.logException(e);
//			return new JSONArray();
//		}
//	}

	private static JSONArray getMovements(AonApiData api) {
		try {
			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
					.setUser(api.getUser().getLogin());

			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
			NordigenAccessToken token = nc.getToken();
			List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
			return NordigenUtils.convertAccountsMovementsToJsonArray(linkedAccountList, token, occam);
		} catch (Exception e) {
			NordigenUtils.logException(e);
			return new JSONArray();
		}
	}

//	private static JSONArray getMovementsByIban(AonApiData api) {
//		int id = api.getData().optInt("id");
//		try {
//			occam.setDomain(api.getDomain().getId()).setDomainName(api.getDomain().getName())
//					.setUser(api.getUser().getLogin());
//
//			NordigenConfiguration nc = AonNordigen.getConfiguration(occam);
//			NordigenAccessToken token = nc.getToken();
//			List<NordigenBankAccount> linkedAccountList = nc.getLinkedAccounts();
//			return NordigenUtils.convertAcccountMovementsToJsonArray(linkedAccountList, id, token, occam);
//		} catch (Exception e) {
//			NordigenUtils.logException(e);
//			return new JSONArray();
//		}
//	}

	private static class BankStatementFilter {

		int id;
		int domain;
		String description;
		Byte status;
		String amount;
		Date from;
		Date to;

		public Date getFrom() {
			return from;
		}

		public BankStatementFilter setFrom(Date from) {
			this.from = from;
			return this;
		}

		public Date getTo() {
			return to;
		}

		public BankStatementFilter setTo(Date to) {
			this.to = to;
			return this;
		}

		public int getId() {
			return id;
		}

		public BankStatementFilter setId(int id) {
			this.id = id;
			return this;
		}

		public int getDomain() {
			return domain;
		}

		public BankStatementFilter setDomain(int domain) {
			this.domain = domain;
			return this;
		}

		public String getDescription() {
			return description;
		}

		public BankStatementFilter setDescription(String description) {
			this.description = description;
			return this;
		}

		public Byte getStatus() {
			return status;
		}

		public BankStatementFilter setStatus(Byte status) {
			this.status = status;
			return this;
		}

		public String getAmount() {
			return amount;
		}

		public BankStatementFilter setAmount(String amount) {
			this.amount = amount;
			return this;
		}
	}

	public static Filter statementsFilter(NordigenBankStatementProperties f, Integer domainId,
			BankStatementFilter statementFilter) {
		Filter filter = f.getDomainProperty().eq(domainId);

		if (statementFilter.getId() != 0) {
			filter = filter.and(f.getIdProperty().like(statementFilter.getId()));
		}

		if (statementFilter.getTo() != null) {
			filter = filter.and(f.getOperationDateProperty().le(statementFilter.getTo()));
		}

		if (statementFilter.getFrom() != null) {
			filter = filter.and(f.getOperationDateProperty().ge(statementFilter.getFrom()));
		}

		if (statementFilter.getAmount() != null && !AonStringUtils.isBlank(statementFilter.getAmount())) {
			filter = filter.and(f.getAmountStringProperty().like("%" + statementFilter.getAmount() + "%"));
		}

		if (statementFilter.getDescription() != null && !AonStringUtils.isBlank(statementFilter.getDescription())) {
			filter = filter.and(f.getDescriptionProperty().like("%" + statementFilter.getDescription() + "%"));
		}

		if (statementFilter.getStatus() != null) {
			filter = filter.and(f.getStatusProperty().eq(statementFilter.getStatus()));
		}

		return filter;
	}
}
