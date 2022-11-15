package nordigen;

import static nordigen.NordigenUtils.isRequisitionLinked;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_BALANCE_TYPE;
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
import com.esferalia.aon.occam.api.model.type.StatementConcept;
import com.esferalia.aon.occam.api.model.type.StatementStatus;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import nordigen.NordigenAPIAbstract.CreateRequisitionParams;

public class AonNordigen {
	
	private AonNordigen() throws IllegalAccessException {
		throw new IllegalAccessException("Utility class");
	}
	
	public static NordigenAccessToken getNewAccessToken() throws Exception {
		try {			
			JSONObject tokenJson = NordigenAPI.newAccessToken();
			Date tokenCreationDate = new Date();
			return NordigenJSONUtils.accessTokenFromJSON(tokenJson)
					.setCreationDate(tokenCreationDate)
					.setRefreshDate(tokenCreationDate);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static void refreshToken(NordigenAccessToken token) throws Exception {
		try {
			JSONObject json = NordigenAPI.refreshAccessToken(token.getRefresh());
			Date tokenRefreshDate = new Date();
			NordigenJSONUtils.updateAccessToken(json, token);
			token.setRefreshDate(tokenRefreshDate);			
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static List<NordigenInstitution> getInstitutions(NordigenAccessToken token, Country country, Boolean paymentsEnabled) throws Exception {
		try {
			JSONArray json = NordigenAPI.getInstitutions(token.getAccess(), country, paymentsEnabled);
			return NordigenInstitutionJSON.fromJSONArray(json);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static NordigenInstitution getInstitution(NordigenAccessToken token, String institutionId) throws Exception {
		try {
			JSONObject json = NordigenAPI.getInstitution(token.getAccess(), institutionId);
			return NordigenInstitutionJSON.fromJSON(json);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static NordigenAgreement createAgreement(NordigenAccessToken token, String institutionId) throws Exception {
		try {
			NordigenInstitution institution = getInstitution(token, institutionId);
			Integer maxDays = 90;
			if (institution != null && institution.getTransactionTotalDays() != null && institution.getTransactionTotalDays() > 0) {
				maxDays = institution.getTransactionTotalDays();
			}
			JSONObject json = NordigenAPI.createEndUserAgreement(token.getAccess(), maxDays, 90, null, institutionId);
			return NordigenAgreementJSON.fromJSON(json);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static NordigenAgreement getAgreement(NordigenAccessToken token, String agreementId) throws Exception {
		try {
			JSONObject json = NordigenAPI.getEndUserAgreement(token.getAccess(), agreementId);
			return NordigenAgreementJSON.fromJSON(json);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static void deleteAgreement(NordigenAccessToken token, String agreementId) throws Exception {
		try {
			NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreementId);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static void deleteAgreement(NordigenAccessToken token, NordigenAgreement agreement) throws Exception {
		try {
			NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreement != null ? agreement.getId() : null);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static NordigenRequisition createRequisition(NordigenAccessToken token, String agreementId, String institutionId, String redirect) throws Exception {
		try {
			CreateRequisitionParams params = new CreateRequisitionParams()
					.setAgreement(agreementId)
					.setRedirect(redirect)
					.setInstitutionId(institutionId)
					.setUserLanguage(AonLanguage.SPANISH)
					.setRedirectImmediate(true);
			
			JSONObject json = NordigenAPI.createRequisition(token.getAccess(), params);
			return NordigenRequisitionJSON.fromJSON(json);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static NordigenRequisition createRequisition(NordigenAccessToken token, NordigenAgreement agreement, String redirect) throws Exception {
		return  createRequisition(token,
				agreement != null ? agreement.getId() : null,
				agreement != null ? agreement.getInstitutionId() : null,
				redirect);
	}
	
	public static NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) throws Exception {
		try {
			JSONObject json = NordigenAPI.getRequisition(token.getAccess(), requisitionId);
			return NordigenRequisitionJSON.fromJSON(json);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static void deleteRequisition(NordigenAccessToken token, String requisitionId) throws Exception {
		try {
			NordigenAPI.deleteRequisition(token.getAccess(), requisitionId);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static void deleteRequisition(NordigenAccessToken token, NordigenRequisition requisition) throws Exception {
		try {
			deleteRequisition(token, requisition != null ? requisition.getId() : null);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static NordigenAccountMetadata getAccountMetadata(NordigenAccessToken token, String nordigenAccountId) throws Exception {
		try {
			JSONObject json = NordigenAPI.getAccount(token.getAccess(), nordigenAccountId);
			return NordigenAccountMetadataJSON.fromJSON(json);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static NordigenAccountDetails getAccountDetails(NordigenAccessToken token, String nordigenAccountId) throws Exception {
		try {
			JSONObject json = NordigenAPI.getDetails(token.getAccess(), nordigenAccountId);
			if (json != null) {
				return NordigenAccountDetailsJSON.fromJSON(json.optJSONObject("account"));
			}
			return null;
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static List<NordigenAccountBalance> getAccountBalances(NordigenAccessToken token, String nordigenAccountId) throws Exception {
		try {
			JSONObject json = NordigenAPI.getBalances(token.getAccess(), nordigenAccountId);
			if (json != null) {
				JSONArray balancesJson = json.optJSONArray("balances");
				return NordigenAccountBalanceJSON.fromJSONArray(balancesJson);
			}
			return Collections.emptyList();
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static List<NordigenAccountTransaction> getBookedAccountTransactions(NordigenAccessToken token, String nordigenAccountId, Date dateFrom) throws Exception {
		try {
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
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static List<NordigenAccountTransaction> getPendingAccountTransactions(NordigenAccessToken token, String nordigenAccountId, Date dateFrom) throws Exception {
		try {
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
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static List<NordigenBankStatement> getStoredBankStatements(Domain domain, String user, RegistryBank rbank, Date dateFrom) {
		return NordigenDAO.getBankStatements(domain, user, rbank, dateFrom, new Date());
	}
	
	public static NordigenBankAccount getAccountByRbank(Domain domain, String login, Integer rbankId) throws Exception {
		try {
			RegistryBank rbank = AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(rbankId));
			RegistryAddInfo raddInfo = NordigenDAO.getRaddInfoByRaddressId(domain, login, rbankId);
			Date lastMovementDate = getLastMovementDate(domain, login, rbankId);
			return new NordigenBankAccount()
					.setRbank(rbank)
					.setRaddInfo(raddInfo)
					.setIban(rbank != null && rbank.getBankAccount() != null ? rbank.getBankAccount().getIban() : null)
					.setBankAlias(rbank != null ? rbank.getAlias() : null)
					.setLinked(raddInfo != null && AonStringUtils.isNotBlank(raddInfo.getValue()))
					.setLastMovementDate(lastMovementDate);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	
	public static NordigenBankAccount setNordigenBankAccountValues(Domain domain, String user, NordigenAccessToken token, NordigenBankAccount account) throws Exception {
		try {
			RegistryBank rbank = account.getRbank();
			RegistryAddInfo raddInfo = account.getRaddInfo();
			if (raddInfo != null) {
				String reqId = raddInfo.getValue();
				if (AonStringUtils.isNotBlank(reqId)) {
					account.setLinked(true);
					System.out.println("req");
					account.setRequisition(getRequisition(token, reqId));
					System.out.println("meta");
					account.setMetadata(getRbankNordigenAccountId(token, account.getRequisition(), rbank));
					System.out.println("ACC_ID");
					String accountId = account.getMetadata() != null ? account.getMetadata().getId() : null;
					Date lastMovementDate = getLastMovementDate(domain, user, account.getRbank().getId());
					account.setLastMovementDate(lastMovementDate);
					
					Thread thread3 = new Thread(() -> {
						System.out.println("THREAD 3 - " + account.getBankAlias());
						try {
							if (account.getRequisition() != null && account.getRequisition().getInstitutionId() != null) {
								account.setInstitution(getInstitution(token, account.getRequisition().getInstitutionId()));
							}
						} catch (Exception e) {
							e.printStackTrace();
							account.addLog(e.getMessage());
						}
						System.out.println("THREAD 3 termina");
					});
					thread3.start();
					
					if (account.getMetadata() != null
						&& AonStringUtils.isNotBlank(accountId)
						&& NORDIGEN_REQUISITION_STATUS.LN.equals(account.getRequisition().getStatus())
					) {
						
						Thread thread1 = new Thread(() -> {
							System.out.println("THREAD 1");
							try {
								account.setDetails(getAccountDetails(token, accountId));
							} catch (Exception e) {
								e.printStackTrace();
								account.addLog(e.getMessage());
							}
							System.out.println("THREAD 1 termina");
						});
						Thread thread2 = new Thread(() -> {
							System.out.println("THREAD 2");
							try {
								account.setBalances(getAccountBalances(token, accountId));
								account.setNotInsertedMovements(getNotInsertedTransactions(token, domain , user, account));
							} catch (Exception e) {
								e.printStackTrace();
								account.addLog(e.getMessage());
							}
							System.out.println("THREAD 2 termina");
						});
						
						thread1.start();
						thread2.start();
						thread2.join();
					}
					thread3.join();
					
				}
			}
			return account;
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	//LINKED AND UNLINKED ACCOUNTS ALL TOGETHER
	public static List<NordigenBankAccount> getAllAccounts(NordigenAccessToken token, Domain domain, String login) {
		Company company = AON.getCompany(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));
		List<NordigenBankAccount> list = AON.getRegistryBankStream(domain, login,
				f -> f.getActiveProperty().eq(AonEnumUtils.getByte(true))
				.and(f.getRegistryProperty().eq(company.getId()))
		).map(rbank -> {
			try {
				return getAccountByRbank(domain, login, rbank.getId());
			} catch (Exception e) {
				return null;
			}
		}).collect(Collectors.toList());
		return list;
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
					
				} catch (Exception e) {
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
					
				} catch (Exception e) {
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
		infos.filter(info -> info != null && (("NORDIGEN[" + rbank + "]").equals(info.getAttribute()))).forEach(info -> {
			Integer id = info.getId();
			if (id != null && info.getId() > 0) {
				AON.deleteRegistryAddInfo(domain, login, info.getId());
				try {
					if (AonStringUtils.isNotBlank(info.getValue())) {
						deleteRequisition(token, info.getValue());						
					}
				} catch (Exception e) {
					//It was already deleted on Nordigen
				}
			}
		});
	}
	
	public static NordigenAccountMetadata getRbankNordigenAccountId(NordigenAccessToken token, NordigenRequisition requisition, RegistryBank rbank) {
		if (requisition != null && rbank != null && rbank.getBankAccount() != null) {
			String iban = rbank.getBankAccount().getIban();
			if (AonStringUtils.isBlank(iban)) {
				return null;
			}
			List<String> accs = requisition.getAccounts();
			if (accs != null) {
				
				for (String accId : accs) {
					System.out.println("cuenta: " + accId);
					try {
						System.out.println("cuenta - metadata empieza");
						NordigenAccountMetadata metadata = getAccountMetadata(token, accId);
						System.out.println("cuenta - metadata termina");
						if (metadata != null && AonStringUtils.equalsIgnoreCase(iban, metadata.getIban())) {
							return metadata;
						}
					} catch (Exception e) {
					}
				}
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
	
//	public static List<NordigenAccountTransaction> getNewTransactions(NordigenAccessToken token, Domain domain, String login, Integer rbankId) {
//		String lastTransactionId = null;
//		Date lastTransactionDate = null;
//		Pair<String, Date> lastIdAndDate = NordigenDAO.getMaxMovementIdAndDate(domain, login, rbankId);
//		if (lastIdAndDate != null) {			
//			lastTransactionId = lastIdAndDate.getKey();
//			lastTransactionDate = lastIdAndDate.getValue();
//		}
//		lastTransactionDate = lastTransactionDate != null ? lastTransactionDate : NordigenDAO.getLastOperationDateDB(domain, login, rbankId);
//		
//		NordigenRequisition requisition = getRequisitionByRbank(token, domain, login, rbankId);
//		String nordigenAccountId = getRbankNordigenAccountId(token, domain, login, requisition, rbankId);
//		
//		if (AonStringUtils.isNotBlank(nordigenAccountId)) {
//			List<NordigenAccountTransaction> transactions = Collections.emptyList();
//			try {
//				transactions = getBookedAccountTransactions(token, nordigenAccountId, lastTransactionDate);
//			} catch (Exception e) {
//			}
//			if (AonStringUtils.isNotBlank(lastTransactionId)) {
//				Integer lastTransactionIdInt = AonNumberUtils.toInteger(lastTransactionId);
//				return transactions.stream().filter(tr -> AonNumberUtils.toInteger(tr.getTransactionId()) > lastTransactionIdInt).collect(Collectors.toList());
//			}
//			return transactions;
//		}
//		
//		return Collections.emptyList();
//		
//	}
	
	public static NordigenBankStatement nordigenToBankStatement(NordigenBankAccount account, NordigenAccountTransaction transaction) {
		if (transaction != null) {
			NordigenBankStatement statement = new NordigenBankStatement();
			Double amount = transaction.getTransactionAmount() != null ? transaction.getTransactionAmount().getAmount() : 0;
			boolean bpayment = amount < 0;
			
			String description = "";
			if (AonStringUtils.isNotBlank(transaction.getRemittanceInformationUnstructured())) {
				description = transaction.getRemittanceInformationUnstructured();
			} else if (AonStringUtils.isNotBlank(transaction.getRemittanceInformationStructured())) {				
				description = transaction.getRemittanceInformationStructured();
			}
			
			description = AonStringUtils.substring(description, 0, 80);
			
			StringBuilder sb = new StringBuilder();
			
			if (AonStringUtils.isNotBlank(transaction.getTransactionId())) {
				sb.append(transaction.getTransactionId());
			} else if (AonStringUtils.isNotBlank(transaction.getInternalTransactionId())) {
				sb.append(transaction.getInternalTransactionId());				
			}
			statement
			.setDomain(account.getRbank().getDomain())
			.setRegistryBank(account.getRbank())
			.setOperationDate(transaction.getValueDate())
			.setCommonConcept(StatementConcept.UNKNOWN)
			.setPayment(bpayment)
			.setAmount(Math.abs(amount))
			.setDescription(description)
			.setStatus(StatementStatus.PENDING)
			.setReference1("NORDIGEN")
			.setReference2(AonStringUtils.trimToNull(AonStringUtils.substring(sb.toString(), 0, 64)));
			String id = null;
			
			if (AonStringUtils.isNotBlank(transaction.getTransactionId())) {
				id = transaction.getTransactionId();
			} else if (AonStringUtils.isNotBlank(transaction.getInternalTransactionId())) {
				id = transaction.getInternalTransactionId();
			}
			
			statement.setNordigenMovementId(id);
			return statement;
		}
		return null;
	}
	
	public static Date getLastMovementDate(Domain domain, String login, Integer rbankId) {
		return NordigenDAO.getLastMovementDate(domain, login, rbankId);
	}
	
	public static List<NordigenBankStatement> getNotInsertedTransactions(NordigenAccessToken token, Domain domain, String login, NordigenBankAccount account) throws Exception {
		if (account != null) {
			NordigenAccountMetadata metadata = account.getMetadata();
			String accId = metadata != null ? AonStringUtils.trimToNull(metadata.getId()) : null;
			Date lastMovDate = account.getLastMovementDate();
			Date today = new Date();
			Date dateFrom = null;
			if (lastMovDate != null) {
				dateFrom = AonDateUtils.addDays(lastMovDate, 1);
			} else {
				NordigenInstitution institution = account.getInstitution();
				if (institution != null && institution.getTransactionTotalDays() != null) {
					Integer days = institution.getTransactionTotalDays();
					dateFrom = AonDateUtils.addDays(today, -days);		
				}
			}
			
			if (dateFrom != null && (AonDateUtils.isSameDay(today, dateFrom) || today.compareTo(dateFrom) >= 0)) {
				List<NordigenBankStatement> stList = new LinkedList<>();
				List<NordigenBankStatement> pendingStatements = new LinkedList<>();
				List<NordigenBankStatement> bookedStatements = new LinkedList<>();
				final Date from = dateFrom;
				StringBuilder exceptionMessage = new StringBuilder();
				Thread thread1 = new Thread(() -> {
					try {
						List<NordigenAccountTransaction> ptr = getPendingAccountTransactions(token, accId, from);
						for (NordigenAccountTransaction t : ptr) {
							NordigenBankStatement st = nordigenToBankStatement(account, t);
							st.setPending(true);
							pendingStatements.add(st);
						}
					} catch (Exception e) {
						exceptionMessage.append(e.getMessage());
					}
				});
				Thread thread2 = new Thread(() -> {
					try {
						List<NordigenAccountTransaction> ptr = getBookedAccountTransactions(token, accId, from);
						for (NordigenAccountTransaction t : ptr) {
							NordigenBankStatement st = nordigenToBankStatement(account, t);
							bookedStatements.add(st);
						}
					} catch (Exception e) {
						exceptionMessage.append(e.getMessage());
					}
				});
				thread1.start();
				thread2.start();
				thread1.join();
				thread2.join();
				
				if (!exceptionMessage.isEmpty()) {
					throw new Exception(exceptionMessage.toString());
				}
				
				stList.addAll(pendingStatements);
				stList.addAll(bookedStatements);
				
				NordigenAccountBalance consBalance = filterConsolidado(account.getBalances());
				if (consBalance != null && consBalance.getBalanceAmount() != null) {
					double amount = consBalance.getBalanceAmount().getAmount();
					for (NordigenBankStatement statement : stList) {
						statement.setCurrentBalance(amount);
						amount -= statement.getAmount() * (statement.isPayment() ? (-1) : 1);
					}
				}
				
				return stList;
			}
		}
		return Collections.emptyList();
	}
	
	public static NordigenAccountBalance filterConsolidado(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance consolidado = null;
		
		consolidado = balances.stream()
				.filter(bal -> NORDIGEN_BALANCE_TYPE.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);
		
		if (consolidado == null && balances.size() > 0) {
			return balances.get(0);
		}
		
		return consolidado;
	}
	
	public static NordigenAccountBalance filterReal(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance real = null;
		
		real = balances.stream()
				.filter(bal -> !NORDIGEN_BALANCE_TYPE.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);
		
		if (real == null) {
			return filterConsolidado(balances);
		}
		
		return real;
	}
	
	public static int insertStatements(Domain domain, String login, NordigenBankAccount account) {
		return NordigenDAO.insertStatements(domain, login, account);
	}
	
	public static void main(String[] args) throws Exception {
		Integer rbank = 6740;
		String access = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjY4NTI3ODIwLCJqdGkiOiI5NjBiZDY3ZWYyMGE0MzhmOTZkMTNlODI3YWY5ZDYyMyIsImlkIjoxNjM5Miwic2VjcmV0X2lkIjoiZjM1NTk2ODUtYmJlYy00NWM0LTlkZmEtZjAxNzIxZTcxOTBlIiwiYWxsb3dlZF9jaWRycyI6WyIwLjAuMC4wLzAiLCI6Oi8wIl19.Ctdo0hiMv4auBvbegqy6avUhm-4X9P91CvavMJ_EkgQ";
		NordigenAccessToken token = new NordigenAccessToken().setAccess(access);
		Domain domain = new Domain().setId(7138).setName("b72384936-ayudat.aonsolutions.net");
		String reqId = "4a7c2d29-6ab0-4afe-99bd-84c7ad7f76d7";
		insertNewRequisitionId(domain, "", getRequisition(token, reqId), rbank);
//		List<NordigenAccountTransaction> newTr = getNewTransactions(token, domain, "", rbank);
//		List<NordigenBankAccount> allAccounts = getAllAccounts(token, domain, "");
//		allAccounts.forEach(acc -> {
//			System.out.println(acc.getIban() + " - " + acc.isLinked());
//		});
//			NordigenBankAccount acc = getAccountByRbank(token, domain, "", rbank);
//			RegistryAddInfo info = insertNewRequisitionId(domain, "", getRequisition(getNewAccessToken(), reqId), rbank);
		System.out.println("ok");
	}
	
	
	
	
	
}
