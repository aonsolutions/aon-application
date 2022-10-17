package nordigen;

import static nordigen.NordigenUtils.isRequisitionLinked;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountBalanceJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountDetailsJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountMetadataJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountTransactionJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAgreementJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenInstitutionJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONUtils;
import com.esferalia.aon.occam.api.json.nordigen.NordigenRequisitionJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_REQUISITION_STATUS;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountDetails;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransaction;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenDAO;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

import nordigen.NordigenAPIAbstract.CreateRequisitionParams;

public class AonNordigen {
	
	private AonNordigen() throws IllegalAccessException {
		throw new IllegalAccessException("Utility class");
	}
	
	public static NordigenAccessToken getNewAccessToken() throws NordigenException {
		JSONObject tokenJson = NordigenAPI.newAccessToken();
		Date tokenCreationDate = new Date();
		return NordigenJSONUtils.accessTokenFromJSON(tokenJson)
				.setCreationDate(tokenCreationDate)
				.setRefreshDate(tokenCreationDate);
	}
	
	public static void refreshToken(NordigenAccessToken token) throws NordigenException {
		JSONObject json = NordigenAPI.refreshAccessToken(token.getRefresh());
		Date tokenRefreshDate = new Date();
		NordigenJSONUtils.updateAccessToken(json, token);
		token.setRefreshDate(tokenRefreshDate);
	}
	
	public static List<NordigenInstitution> getInstitutions(NordigenAccessToken token, Country country, Boolean paymentsEnabled) throws NordigenException {
		JSONArray json = NordigenAPI.getInstitutions(token.getAccess(), country, paymentsEnabled);
		return NordigenInstitutionJSON.fromJSONArray(json);
	}
	
	public static NordigenInstitution getInstitution(NordigenAccessToken token, String institutionId) throws NordigenException {
		JSONObject json = NordigenAPI.getInstitution(token.getAccess(), institutionId);
		return NordigenInstitutionJSON.fromJSON(json);
	}
	
	public static NordigenAgreement createAgreement(NordigenAccessToken token, String institutionId) throws NordigenException {
		NordigenInstitution institution = getInstitution(token, institutionId);
		Integer maxDays = 90;
		if (institution != null && institution.getTransactionTotalDays() != null && institution.getTransactionTotalDays() > 0) {
			maxDays = institution.getTransactionTotalDays();
		}
		JSONObject json = NordigenAPI.createEndUserAgreement(token.getAccess(), maxDays, 90, null, institutionId);
		return NordigenAgreementJSON.fromJSON(json);
	}
	
	public static NordigenAgreement getAgreement(NordigenAccessToken token, String agreementId) throws NordigenException {
		JSONObject json = NordigenAPI.getEndUserAgreement(token.getAccess(), agreementId);
		return NordigenAgreementJSON.fromJSON(json);
	}
	
	public static void deleteAgreement(NordigenAccessToken token, String agreementId) throws NordigenException {
		NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreementId);
	}
	
	public static void deleteAgreement(NordigenAccessToken token, NordigenAgreement agreement) throws NordigenException {
		NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreement != null ? agreement.getId() : null);
	}
	
	public static NordigenRequisition createRequisition(NordigenAccessToken token, String agreementId, String institutionId, String redirect) throws NordigenException {
		CreateRequisitionParams params = new CreateRequisitionParams()
				.setAgreement(agreementId)
				.setRedirect(redirect)
				.setInstitutionId(institutionId)
				.setUserLanguage(AonLanguage.SPANISH)
				.setRedirectImmediate(true);
		
		JSONObject json = NordigenAPI.createRequisition(token.getAccess(), params);
		return NordigenRequisitionJSON.fromJSON(json);
	}
	
	public static NordigenRequisition createRequisition(NordigenAccessToken token, NordigenAgreement agreement, String redirect) throws NordigenException {
		return  createRequisition(token,
				agreement != null ? agreement.getId() : null,
				agreement != null ? agreement.getInstitutionId() : null,
				redirect);
	}
	
	public static NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) throws NordigenException {
		JSONObject json = NordigenAPI.getRequisition(token.getAccess(), requisitionId);
		return NordigenRequisitionJSON.fromJSON(json);
	}
	
	public static void deleteRequisition(NordigenAccessToken token, String requisitionId) throws NordigenException {
		NordigenAPI.deleteRequisition(token.getAccess(), requisitionId);
	}
	
	public static void deleteRequisition(NordigenAccessToken token, NordigenRequisition requisition) throws NordigenException {
		deleteRequisition(token, requisition != null ? requisition.getId() : null);
	}
	
	public static NordigenAccountMetadata getAccountMetadata(NordigenAccessToken token, String nordigenAccountId) throws NordigenException {
		JSONObject json = NordigenAPI.getAccount(token.getAccess(), nordigenAccountId);
		return NordigenAccountMetadataJSON.fromJSON(json);
	}
	
	public static NordigenAccountDetails getAccountDetails(NordigenAccessToken token, String nordigenAccountId) throws NordigenException {
		JSONObject json = NordigenAPI.getDetails(token.getAccess(), nordigenAccountId);
		if (json != null) {
			return NordigenAccountDetailsJSON.fromJSON(json.optJSONObject("account"));
		}
		return null;
	}
	
	public static List<NordigenAccountBalance> getAccountBalances(NordigenAccessToken token, String nordigenAccountId) throws NordigenException {
		JSONObject json = NordigenAPI.getBalances(token.getAccess(), nordigenAccountId);
		if (json != null) {
			JSONArray balancesJson = json.optJSONArray("balances");
			return NordigenAccountBalanceJSON.fromJSONArray(balancesJson);
		}
		return Collections.emptyList();
	}
	
	public static List<NordigenAccountTransaction> getBookedAccountTransactions(NordigenAccessToken token, String nordigenAccountId, Date dateFrom) throws NordigenException {
		JSONObject json = NordigenAPI.getTransactions(token.getAccess(), nordigenAccountId, dateFrom, new Date());
		if (json != null) {
			JSONObject transactionsJson = json.optJSONObject("transactions");
			JSONArray bookedTransactionsJson = null;
			if (transactionsJson != null) {
				bookedTransactionsJson = transactionsJson.optJSONArray("booked");
			}
			return NordigenAccountTransactionJSON.fromJSONArray(bookedTransactionsJson);
		}
		return Collections.emptyList();
	}
	
	public static List<NordigenAccountTransaction> getPendingAccountTransactions(NordigenAccessToken token, String nordigenAccountId, Date dateFrom) throws NordigenException {
		JSONObject json = NordigenAPI.getTransactions(token.getAccess(), nordigenAccountId, dateFrom, new Date());
		if (json != null) {
			
			JSONObject transactionsJson = json.optJSONObject("transactions");
			JSONArray pendingTransactionsJson = null;
			if (transactionsJson != null) {
				pendingTransactionsJson = transactionsJson.optJSONArray("pending");				
			}
			return NordigenAccountTransactionJSON.fromJSONArray(pendingTransactionsJson);
		}
		return Collections.emptyList();
	}
	
	
	
	
	public static NordigenBankAccount getAccountByRbank(NordigenAccessToken token, Domain domain, String login, Integer rbankId) throws NordigenException {
		RegistryBank rbank = AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(rbankId));
		RegistryAddInfo raddInfo = NordigenDAO.getRaddInfoByRaddressId(domain, login, rbankId);
		boolean linked = false;
		NordigenAccountMetadata metadata = null;
		NordigenAccountDetails details = null;
		NordigenAccountBalance balance = null;
		NordigenRequisition requisition = null;
		if (raddInfo != null) {
			String reqId = raddInfo.getValue();
			if (AonStringUtils.isNotBlank(reqId)) {
				linked = true;
				requisition = getRequisition(token, reqId);
				String accountId = getRbankNordigenAccountId(token, domain, login, requisition, rbankId);
				if (accountId != null) {
					metadata = getAccountMetadata(token, accountId);
					details = getAccountDetails(token, accountId);
					balance = NordigenUtils.getLastAccountBalance(getAccountBalances(token, accountId));
				}
			}
		}
		return new NordigenBankAccount()
				.setMetadata(metadata)
				.setDetails(details)
				.setBalance(balance)
				.setRbankId(rbankId)
				.setRequisition(requisition)
				.setLinked(linked)
				.setIban(rbank != null && rbank.getBankAccount() != null ? rbank.getBankAccount().getIban() : null)
				.setBankAlias(rbank != null ? rbank.getAlias() : null);
	}
	
	//LINKED AND UNLINKED ACCOUNTS ALL TOGETHER
	public static List<NordigenBankAccount> getAllAccounts(NordigenAccessToken token, Domain domain, String login) {
		Company company = AON.getCompany(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));
		return AON.getRegistryBankStream(domain, login,
				f -> f.getActiveProperty().eq(AonEnumUtils.getByte(true))
				.and(f.getRegistryProperty().eq(company.getId()))
		).map(rbank -> {
			try {
				return getAccountByRbank(token, domain, login, rbank.getId());
			} catch (NordigenException e) {
				return null;
			}
		}).collect(Collectors.toList());
		
	}
	
	public static Map<Integer, NordigenRequisition> getStoredActiveRequisitions(NordigenAccessToken token, Domain domain, String login) {
		Map<Integer, NordigenRequisition> requisitions = new HashMap<>();
		final Stream<RegistryAddInfo> infos = NordigenDAO.getAllNordigenRaddInfos(domain, login);;
		infos.forEach(info -> {
			Integer rbankId = NordigenUtils.getRbankIdFromRaddinfo(info);
			if (rbankId != null) {				
				final String requisitionId = info.getValue();
				try {
					requisitions.put(rbankId, getRequisition(token, requisitionId));
					
				} catch (NordigenException e) {
					requisitions.put(rbankId, null);
				}
			}
		});
		return requisitions;
	}
	
	public static Map<Integer, NordigenRequisition> getStoredRequisitions(NordigenAccessToken token, Domain domain, final String login) {
		Map<Integer, NordigenRequisition> requisitions = new HashMap<>();
		final Stream<RegistryAddInfo> infos = NordigenDAO.getAllNordigenRaddInfos(domain, login);
		infos.forEach(info -> {
			Integer rbankId = NordigenUtils.getRbankIdFromRaddinfo(info);
			if (rbankId != null) {				
				final String requisitionId = info.getValue();
				try {
					requisitions.put(rbankId, getRequisition(token, requisitionId));
					
				} catch (NordigenException e) {
					requisitions.put(rbankId, null);
				}
			}
		});
		return requisitions;
	}
	
	public static NordigenRequisition getRequisitionByRbank(NordigenAccessToken token, Domain domain, final String login, Integer rbankId) {
		Map<Integer, NordigenRequisition> allReqs = getStoredRequisitions(token, domain, login);
		if (rbankId != null) {
			return allReqs.getOrDefault(rbankId, null);
		}
		return null;
	}
	
	public static Map<Integer, NordigenRequisition> getStoredLinkedRequisitions(NordigenAccessToken token, Domain domain, final String login) {
		Map<Integer, NordigenRequisition> linked = new HashMap<>();
		Map<Integer, NordigenRequisition> allReqs = getStoredRequisitions(token, domain, login);
		allReqs.entrySet().stream()
		.filter(Objects::nonNull)
		.filter(entry -> entry.getValue() != null && NORDIGEN_REQUISITION_STATUS.LN.equals(entry.getValue().getStatus()))
		.forEach(entry -> linked.put(entry.getKey(), entry.getValue()));
		return linked;
	}
	
	public static RegistryAddInfo insertNewRequisitionId(Domain domain, String login, NordigenRequisition requisition, Integer rbank) {
		if (requisition != null && requisition.getId() != null && AonNumberUtils.zeroIfNull(rbank) > 0) {
			String requisitionId = requisition.getId();
			RegistryBank registryBank = AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(rbank));
			String rbankAttr = INordigenConstants.RADD_INFO_REQUISITION_ATTRIBUTE_PROFIX + rbank + INordigenConstants.RADD_INFO_REQUISITION_ATTRIBUTE_SUFFIX;
			RegistryAddInfo radd = NordigenDAO.getRaddInfoByRaddressId(domain, login, rbank);
			if (radd == null && registryBank != null) {
				Integer rbDomain = registryBank.getDomain();
				Integer rbRegistry = registryBank.getRegistry();
				RegistryAddInfo raddInfo = new RegistryAddInfo()
						.setDomain(rbDomain)
						.setRegistry(rbRegistry)
						.setDate(new Date())
						.setAttribute(rbankAttr)
						.setValue(requisitionId);
					
				AON.insertRegistryAddInfo(domain.getName(), domain.getId(), login, raddInfo);
				return NordigenDAO.getRaddInfoByRaddressId(domain, login, rbank);
			}
		}
		return null;
	}
	
	public static RegistryAddInfo updateExistingRequisitionId(Domain domain, String login, NordigenRequisition requisition, Integer rbank) {
		if (requisition != null && requisition.getId() != null && AonNumberUtils.zeroIfNull(rbank) > 0) {
			String requisitionId = requisition.getId();
			RegistryBank registryBank = AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(rbank));
			RegistryAddInfo radd = NordigenDAO.getRaddInfoByRaddressId(domain, login, rbank);
			if (radd != null && registryBank != null) {
				RegistryAddInfo updatedRaddInfo = radd.setValue(requisitionId);
				AON.updateRegistryAddInfo(domain.getName(), domain.getId(), login, updatedRaddInfo);
				return NordigenDAO.getRaddInfoByRaddressId(domain, login, rbank);
			}
		}
		return null;
	}
	
	public static RegistryAddInfo insertOrUpdateRequisitionId(Domain domain, String login, NordigenRequisition requisition, Integer rbank) {
		final RegistryAddInfo radd = NordigenDAO.getRaddInfoByRaddressId(domain, login, rbank);		
		if (radd != null) {
			return updateExistingRequisitionId(domain, login, requisition, rbank);
		} else {
			return insertNewRequisitionId(domain, login, requisition, rbank);
		}
	}
	
	//DELETES FROM DATABASE AND NORDIGEN
	public static void deleteRequisitionByRbank(NordigenAccessToken token, Domain domain, String login, Integer rbank) {
		final Stream<RegistryAddInfo> infos = NordigenDAO.getAllNordigenRaddInfos(domain, login);
		infos.filter(Objects::nonNull).forEach(info -> {
			Integer id = info.getId();
			if (id != null && info.getId() > 0) {
				AON.deleteRegistryAddInfo(domain, login, info.getId());
				try {
					if (AonStringUtils.isNotBlank(info.getValue())) {
						deleteRequisition(token, info.getValue());						
					}
				} catch (NordigenException e) {
					//It was already deleted on Nordigen
				}
			}
		});
	}
	
	public static String getRbankNordigenAccountId(NordigenAccessToken token, Domain domain, String login, NordigenRequisition requisition, Integer rbankId) {
		RegistryBank rbank = AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(rbankId));
		if (requisition != null && rbank != null && rbank.getBankAccount() != null) {
			String iban = rbank.getBankAccount().getIban();
			if (AonStringUtils.isBlank(iban)) {
				return null;
			}
			List<String> accs = requisition.getAccounts();
			if (accs != null) {
				return accs.stream().filter(accId -> {
					try {
						NordigenAccountMetadata metadata = getAccountMetadata(token, accId);
						if (metadata != null) {
							return AonStringUtils.equalsIgnoreCase(iban, metadata.getIban());
						}
					} catch (NordigenException e) {
					}
					return false;
				}).findFirst().orElse(null);
			}
		}
		return null;
	}
	
	public static Map<Integer, NordigenRequisition> filterNonValidRequisitions(Map<Integer, NordigenRequisition> requisitions) {
		Map<Integer, NordigenRequisition> nonValid = new HashMap<>();
		if (requisitions != null) {
			requisitions.entrySet().stream()
			.filter(entry -> entry != null && !isRequisitionLinked(entry.getValue()))
			.forEach(entry -> nonValid.put(entry.getKey(), entry.getValue()));
		}
		return nonValid;
	}
	
	public static List<NordigenAccountTransaction> getNewTransactions(NordigenAccessToken token, Domain domain, String login, Integer rbankId) {
		String lastTransactionId = null;
		Date lastTransactionDate = null;
		Pair<String, Date> lastIdAndDate = NordigenDAO.getMaxMovementIdAndDate(domain, login, rbankId);
		if (lastIdAndDate != null) {			
			lastTransactionId = lastIdAndDate.getKey();
			lastTransactionDate = lastIdAndDate.getValue();
		}
		lastTransactionDate = lastTransactionDate != null ? lastTransactionDate : NordigenDAO.getLastOperationDateDB(domain, login, rbankId);
		
		NordigenRequisition requisition = getRequisitionByRbank(token, domain, login, rbankId);
		String nordigenAccountId = getRbankNordigenAccountId(token, domain, login, requisition, rbankId);
		
		if (AonStringUtils.isNotBlank(nordigenAccountId)) {
			List<NordigenAccountTransaction> transactions = Collections.emptyList();
			try {
				transactions = getBookedAccountTransactions(token, nordigenAccountId, lastTransactionDate);
			} catch (NordigenException e) {
			}
			if (AonStringUtils.isNotBlank(lastTransactionId)) {
				Integer lastTransactionIdInt = AonNumberUtils.toInteger(lastTransactionId);
				return transactions.stream().filter(tr -> AonNumberUtils.toInteger(tr.getTransactionId()) > lastTransactionIdInt).collect(Collectors.toList());
			}
			return transactions;
		}
		
		return Collections.emptyList();
		
	}
	
	public static NordigenBankStatement nordigenToBankStatement(NordigenAccountTransaction transaction) {
		if (transaction != null) {
			NordigenBankStatement statement = new NordigenBankStatement();
//			statement.set
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		Integer rbank = 139;
		String access = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjY1NDg4ODg1LCJqdGkiOiIwMTkxMTM4ZmM0NmU0NWMyYTlkNGM2NGUzNzFmOTBkMiIsImlkIjoxNjM5Miwic2VjcmV0X2lkIjoiZjM1NTk2ODUtYmJlYy00NWM0LTlkZmEtZjAxNzIxZTcxOTBlIiwiYWxsb3dlZF9jaWRycyI6WyIwLjAuMC4wLzAiLCI6Oi8wIl19.pcHl9XW6vL07iePsPd30e4W2pa7Ks2-1OtiU8Ibk0jE";
		NordigenAccessToken token = new NordigenAccessToken().setAccess(access);
		Domain domain = new Domain().setId(7138).setName("b72384936-ayudat.aonsolutions.net");
//		String reqId = "25c34836-764d-4328-ac70-6b786c9c6fef";
		List<NordigenAccountTransaction> newTr = getNewTransactions(token, domain, "", rbank);
//		List<NordigenBankAccount> allAccounts = getAllAccounts(token, domain, "");
//		allAccounts.forEach(acc -> {
//			System.out.println(acc.getIban() + " - " + acc.isLinked());
//		});
//			NordigenBankAccount acc = getAccountByRbank(token, domain, "", rbank);
//			RegistryAddInfo info = insertNewRequisitionId(domain, "", getRequisition(getNewAccessToken(), reqId), rbank);
		System.out.println("ok");
	}
	
	
	
	
	
}
