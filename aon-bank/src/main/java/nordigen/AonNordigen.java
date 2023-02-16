package nordigen;

import static nordigen.NordigenUtils.isRequisitionLinked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	
	private static final String[] EXTENDED_HISTORY_INSTITUTION_BLACKLIST = {
			"BRED_BREDFRPPXXX",
			"INDUSTRA_MULTLV2X",
			"LHV_LHVBEE22",
			"LUMINOR_",
			"SWEDBANK_",
			"SEB_",
			"LABORALKUTXA_CLPEES2M",
			"BANKINTER_BKBKESMM",
			"CAIXABANK_CAIXESBB",
			"BBVA_BBVAESMM",
			"COOP_EKRDEE22"
	};
	
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
	
	private static boolean isBlacklistedInstitution(NordigenInstitution institution) {
		if (institution == null) {
			return false;
		}
		return Arrays.stream(EXTENDED_HISTORY_INSTITUTION_BLACKLIST)
				.anyMatch(inst -> AonStringUtils.containsIgnoreCase(institution.getId(), inst));
	}
	
	public static NordigenAgreement createAgreement(NordigenAccessToken token, String institutionId) throws Exception {
		try {
			NordigenInstitution institution = getInstitution(token, institutionId);
			Integer maxDays = 90;
			// BORRAR ESTA CONDICIÓN SI MUCHAS INSTITUCIONES DAN PROBLEMAS. QUEDARÁN TODOS LOS ACCESOS A 90 DÍAS
			if (institution != null &&
					institution.getTransactionTotalDays() != null &&
					institution.getTransactionTotalDays() > 0 &&
					!isBlacklistedInstitution(institution)) {
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
	
	
	public static List<NordigenAccountTransaction> getBookedAccountTransactionsByBookingDate(NordigenAccessToken token, NordigenRequisition requisition, String nordigenAccountId, Date dateFrom) throws Exception {
		try {
			JSONObject json = NordigenAPI.getTransactions(token.getAccess(), nordigenAccountId, null, null);
			if (json != null) {
				JSONObject transactionsJson = json.optJSONObject("transactions");
				JSONArray bookedTransactionsJson = null;
				if (transactionsJson != null) {
					bookedTransactionsJson = transactionsJson.optJSONArray("booked");
				}
				List<NordigenAccountTransaction> allTransactions = NordigenAccountTransactionJSON.fromJSONArray(bookedTransactionsJson);
				if (allTransactions != null) {
					return allTransactions.stream().filter(tr -> {
						Date bookingDate = tr.getBookingDate() != null ? tr.getBookingDate() : new Date();
						return com.esferalia.aon.watson.util.AonDateUtils.compare(bookingDate, dateFrom) >= 0;
					}).collect(Collectors.toList());
				}
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
			JSONObject json = NordigenAPI.getTransactions(token.getAccess(), nordigenAccountId, dateFrom, null);
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
			Date lastMovementDate = getLastMovementDate(domain, login, rbankId);
			return new NordigenBankAccount()
					.setRbank(rbank)
					.setIban(rbank != null && rbank.getBankAccount() != null ? rbank.getBankAccount().getIban() : null)
					.setBankAlias(rbank != null ? rbank.getAlias() : null)
					.setLinked(AonStringUtils.isNotBlank(rbank.getRequisition()))
					.setRequisitionId(rbank.getRequisition())
					.setLastMovementDate(lastMovementDate);
		} catch (NordigenException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	
	public static NordigenBankAccount setNordigenBankAccountValues(Domain domain, String user, NordigenAccessToken token, NordigenBankAccount account) throws Exception {
		try {
			RegistryBank rbank = account.getRbank();
			if (rbank.getRequisition() != null) {
				String reqId = rbank.getRequisition();
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
	
	public static RegistryBank updateRequisitionId(Domain domain, String login, NordigenRequisition requisition, Integer rbank) {
		if (requisition != null && requisition.getId() != null && AonNumberUtils.zeroIfNull(rbank) > 0) {
			String requisitionId = requisition.getId();
			RegistryBank registryBank = AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(rbank));
			if (registryBank != null) {
				registryBank.setRequisition(requisitionId);
				return AON.saveRegistryBank(domain, login, registryBank);
			}
		}
		return null;
	}
	
	//DELETES FROM DATABASE AND NORDIGEN
	public static void deleteRequisitionByRbank(NordigenAccessToken token, Domain domain, String login, Integer rbank) throws Exception {
		if (rbank != null) {
			RegistryBank rbankObj = AON.getRegistryBank(domain, login, f -> f.getIdProperty().eq(rbank));
			String requisition = rbankObj != null ? rbankObj.getRequisition() : null;
			rbankObj.setRequisition(null);
			AON.saveRegistryBank(domain, login, rbankObj);
			if (rbankObj != null && requisition != null) {
				deleteRequisition(token, requisition);				
			}
		}
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
			.setOperationDate(transaction.getBookingDate() != null ? transaction.getBookingDate() : new Date())
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
						List<NordigenAccountTransaction> ptr = getBookedAccountTransactionsByBookingDate(token, account.getRequisition(), accId, from);
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
	
	public static List<NordigenRequisition> getAllRequisitions(NordigenAccessToken token) {
		List<NordigenRequisition> requisitionsList = new LinkedList<>();
		JSONObject requisitionsJson = NordigenAPI.getRequisitions(token.getAccess(), 1000000, 0);
		JSONArray results = requisitionsJson.optJSONArray("results");
		if (results != null) {
			requisitionsList.addAll(NordigenRequisitionJSON.fromJSONArray(results));
		}
		
		String nextUrl = requisitionsJson.optString("next", null);
		while (nextUrl != null) {
			Pattern offsetPattern = Pattern.compile("offset=(?<offset>\\d+)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = offsetPattern.matcher(nextUrl);
			String offsetStr = null;
			if (matcher.find()) {
				offsetStr = matcher.group("offset");
			}
			int offset = AonNumberUtils.toint(offsetStr);
			if (offset > 0) {
				requisitionsJson = NordigenAPI.getRequisitions(token.getAccess(), 1000000, offset);
				results = requisitionsJson.optJSONArray("results");
				if (results != null) {
					requisitionsList.addAll(NordigenRequisitionJSON.fromJSONArray(results));
				}
				nextUrl = requisitionsJson.optString("next", null);
			} else {
				nextUrl = null;
			}
		}
		return requisitionsList;
	}
	
	public static List<RegistryBank> getRbanksByRequisition(Domain domain, String login, String requisitionId) {
		List<RegistryBank> list = new ArrayList<>();
		AON.getRegistryBankStream(domain, login, f -> f.getRequisitionProperty().eq(requisitionId)).filter(Objects::nonNull).forEach(list::add);
		return list;
	}
	
	public static RegistryBank updateRbank(Domain domain, String login, RegistryBank rbank) {
		return AON.saveRegistryBank(domain, login, rbank);
	}
	
	public static void main(String[] args) throws Exception {
		Integer rbank = 6740;
		String access = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjczNTk4NTkwLCJqdGkiOiI4NWViYzZjYjQ1NDI0NWRkYmQwNGIzMzMwMzlmN2U1ZSIsImlkIjoxNjM5Miwic2VjcmV0X2lkIjoiZjM1NTk2ODUtYmJlYy00NWM0LTlkZmEtZjAxNzIxZTcxOTBlIiwiYWxsb3dlZF9jaWRycyI6WyIwLjAuMC4wLzAiLCI6Oi8wIl19.ZYtjqT0vJceYcDker2ihaIeDG_-x3uuR8aMmGo_Lxo8";
		NordigenAccessToken token = new NordigenAccessToken().setAccess(access);
//		Domain domain = new Domain().setId(7138).setName("b72384936-ayudat.aonsolutions.net");
//		String reqId = "4a7c2d29-6ab0-4afe-99bd-84c7ad7f76d7";
//		updateRequisitionId(domain, "", getRequisition(token, reqId), rbank);
		
		List<NordigenRequisition> reqs = getAllRequisitions(token);
		
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
