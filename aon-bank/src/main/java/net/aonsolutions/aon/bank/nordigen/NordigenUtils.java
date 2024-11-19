package net.aonsolutions.aon.bank.nordigen;

import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.RADD_INFO_REQUISITION_ATTRIBUTE_PATTERN;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBalanceType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountBalanceJSON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenDAO;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;



public class NordigenUtils {

	public static Integer getRbankIdFromRaddinfo(RegistryAddInfo raddinfo) {
		if (raddinfo == null) {
			return null;
		}
		final Pattern aatrRegex = Pattern.compile(RADD_INFO_REQUISITION_ATTRIBUTE_PATTERN);
		Matcher matcher = aatrRegex.matcher(AonStringUtils.trimToEmpty(raddinfo.getAttribute()));
		if (matcher.matches()) {
			String id = matcher.group("rbank");
			if (AonStringUtils.isNotBlank(id)) {
				return AonNumberUtils.toInteger(id);
			}
		}
		return null;
	}

	public static boolean isRequisitionLinked(NordigenRequisition requisition) {
		if (requisition != null) {
			return NordigenRequisitionStatus.LINKED.equals(requisition.getStatus());
		}
		return false;
	}

	public static NordigenAccountBalance getLastAccountBalance(List<NordigenAccountBalance> balances) {
		if (balances != null) {
			if (balances.size() > 1) {
				Optional<NordigenAccountBalance> balance = balances.stream()
						.filter(b -> b.getReferenceDate() != null
								&& NordigenBalanceType.CLOSING_BOOKED.equals(b.getBalanceType()))
						.sorted((b1, b2) -> b1.getReferenceDate().compareTo(b2.getReferenceDate())).findFirst();
				if (balance.isPresent()) {
					return balance.get();
				}
			}
			return balances.get(0);
		}
		return null;
	}
	
	// NUEVOS METODOS PROCESAR INFO DE BANCOS
	public static boolean isTokenExpired(NordigenAccessToken token) {
		Date today = new Date();
		long tokenExpirationTime = token.getCreationDate().getTime() + token.getAccessExpires() * 1000;
        return today.getTime() > tokenExpirationTime;	    
	}
	
	public static boolean isRefreshTokenExpired(NordigenAccessToken token) {
		Date today = new Date();
		long tokenRefreshExpirationTime = token.getRefreshDate().getTime() + token.getRefreshExpires() * 1000;
        return today.getTime() > tokenRefreshExpirationTime;	    
	}
	
	public static NordigenAccessToken handleToken(NordigenAccessToken token) {
		if (isTokenExpired(token)) {
			if (!isRefreshTokenExpired(token)) {
				return NordigenAPI.refreshAccessToken(token.getRefresh());
			}else {
				return NordigenAPI.newAccessToken();
			}
		}
		return token;
	}
	
	public static JSONObject movementsToJson(List<NordigenBankStatement> movements,
			NordigenBankAccount nordigenTrueAccount) {
		JSONArray movementsJsonArray = new JSONArray();
		for (NordigenBankStatement movement : movements) {
			JSONObject movementJson = new JSONObject();
			movementJson.put("operationDate", movement.getOperationDate());
			movementJson.put("description", movement.getDescription());
			movementJson.put("status", movement.getStatus());
			movementJson.put("amount", movement.getAmount());
			movementJson.put("totalAmount", movement.getCurrentBalance());
			movementsJsonArray.put(movementJson);
		}
		JSONObject movementsJson = new JSONObject();
		movementsJson.put("movements", movementsJsonArray);
		movementsJson.put("iban",  nordigenTrueAccount.getIban());
		return movementsJson;
	}
	
	public static JSONObject bankStatementJSON(BankStatement statement) {
		JSONObject statementJSON = new JSONObject();
		statementJSON.put("id", statement.getId());
		statementJSON.put("operation_date", statement.getOperationDate());
		statementJSON.put("amount", statement.getAmount());
		statementJSON.put("description", statement.getDescription());
		statementJSON.put("status", statement.getStatus());
		return statementJSON;
	}

	//revisar
	public static JSONArray convertAccountsMovementsToJsonArray(List<NordigenBankAccount> linkedAccountList, NordigenAccessToken token, Occam occam) throws Exception {
		JSONArray movementsJsonArray = new JSONArray();
		for (NordigenBankAccount account : linkedAccountList) {
			NordigenBankAccount nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, account);
			Date lastAccessedDate = nordigenTrueAccount.getLastMovementDate();
			boolean linked = nordigenTrueAccount.isLinked();
			List<NordigenBankStatement> movements = AonNordigen.getMovements(token, occam, nordigenTrueAccount, lastAccessedDate, linked);
			JSONObject movementsJson = movementsToJson(movements, nordigenTrueAccount);
			movementsJsonArray.put(movementsJson);
		}

		return movementsJsonArray;
	}

	//MOVIMIENTOS
	public static JSONArray convertAcccountMovementsToJsonArray(List<NordigenBankAccount> linkedAccountList, Integer id, NordigenAccessToken token, Occam occam) {
		JSONArray movementsJsonArray = new JSONArray();
		linkedAccountList.stream().filter(account -> account.getRbank().getId().equals(id)).findFirst().ifPresent(account -> {
			try {
				NordigenBankAccount nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, account);
				Date lastAccessedDate = nordigenTrueAccount.getLastMovementDate();
				boolean linked = nordigenTrueAccount.isLinked();
				List<NordigenBankStatement> movements = AonNordigen.getMovements(token, occam, nordigenTrueAccount,
						lastAccessedDate, linked);
				JSONObject movementsJson = movementsToJson(movements, nordigenTrueAccount);
				movementsJsonArray.put(movementsJson);
				AonNordigen.insertStatements(occam, account);
				AonNordigen.updateAccountBalances(account, occam);
			} catch (Exception e) {
				throw new RuntimeException("Error al procesar la cuenta", e);
			}
		});

		return movementsJsonArray;
	}

	public static JSONArray processAccountBalances(List<NordigenBankAccount> linkedAccountList,
			NordigenAccessToken token, Occam occam) throws Exception {
		JSONArray balancesArray = new JSONArray();
		for (NordigenBankAccount account : linkedAccountList) {
			NordigenBankAccount nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, account);
			List<NordigenAccountBalance> balances = nordigenTrueAccount.getBalances();
			JSONObject balanceObject = new JSONObject();
			balanceObject.put("iban", account.getIban());
			balanceObject.put("balances", NordigenAccountBalanceJSON.to(balances));
			balancesArray.put(balanceObject);
		}

		return balancesArray;
	}

	public static JSONArray processSingleAccountBalance(List<NordigenBankAccount> linkedAccountList, Integer id,
			NordigenAccessToken token, Occam occam) throws Exception {
		JSONArray result = new JSONArray();
		linkedAccountList.stream().filter(account -> account.getRbank().getId().equals(id)).findFirst().ifPresent(account -> {
			try {
				NordigenBankAccount nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, account);
				List<NordigenAccountBalance> balances = nordigenTrueAccount.getBalances();
				JSONObject balanceObject = new JSONObject();
				balanceObject.put("iban", account.getIban());
				balanceObject.put("balances", NordigenAccountBalanceJSON.to(balances));
				result.put(balanceObject);
			} catch (Exception e) {
				throw new RuntimeException("Error al procesar la cuenta", e);
			}
		});
		return result;
	}
	
	public static void processAccountLinking(List<NordigenBankAccount> rAccounts, Integer id,
			NordigenAccessToken token, JSONObject jsonLink, Occam occam) throws Exception {
		for (NordigenBankAccount account : rAccounts) {
			if (account.getRbank().getId().equals(id)) {
				if (account.isLinked()) {
					jsonLink.put("link", "Already linked");
				} else {
					NordigenRequisition requisition = AonNordigen.addAccount(token, occam, account);
					jsonLink.put("link", requisition.getLink());
				}
				return;
			}
		}
		jsonLink.put("link", "IBAN no encontrado");
	}
}
