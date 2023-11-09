package net.aonsolutions.aon.bank.nordigen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBalanceType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountDetails;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransaction;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.StatementConcept;
import com.esferalia.aon.occam.api.model.type.StatementStatus;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonNordigen  {

	private static final String LINK_REGEX = "^https\\:\\/\\/(?<link>.*?)\\/ms\\/.*$";
	private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);

	private AonNordigen() throws IllegalAccessException {
	}
	
	static NordigenAccessToken getNewAccessToken() {
		JSONObject tokenJson = NordigenAPI.newAccessToken();
		Date tokenCreationDate = new Date();
		return NordigenJSONUtils.accessTokenFromJSON(tokenJson)
				.setCreationDate(tokenCreationDate)
				.setRefreshDate(tokenCreationDate);
	}

	static void refreshToken(NordigenAccessToken token)  {
		JSONObject json = NordigenAPI.refreshAccessToken(token.getRefresh());
		Date tokenRefreshDate = new Date();
		NordigenJSONUtils.updateAccessToken(json, token);
		token.setRefreshDate(tokenRefreshDate);			
	}
	
	// ----------------------------------------------------------------
	// -------------------------------------------------------- [PUBLIC]
	// ----------------------------------------------------------------
	public static NordigenConfiguration getConfiguration(Occam occam) {
		try {
			NordigenAccessToken token = AonNordigen.getNewAccessToken();
			List<NordigenBankAccount> accounts = AonNordigen.getAllAccounts(token, occam);
			return new NordigenConfiguration()
				.setConfiguration(AON.getConfiguration(occam))
				.setToken(token)
				.setAccounts(accounts);
		} catch ( AonCoreException ex) {
			throw new NordigenException(ex.getMessage());
		}
	}

	public static NordigenBankAccount setBankAccountValues(Occam occam, NordigenAccessToken token, NordigenBankAccount account) {
		try {
			RegistryBank rbank = account.getRbank();
			if (rbank.getRequisition() != null) {
				String reqId = rbank.getRequisition();
				if (AonStringUtils.isNotBlank(reqId)) {
					account.setLinked(true);
					account.setRequisition(getRequisition(token, reqId));
					account.setMetadata(getRbankNordigenAccountId(token, account.getRequisition(), rbank));
					String accountId = account.getMetadata() != null ? account.getMetadata().getId() : null;
					Date lastMovementDate = getLastMovementDate(occam, account.getRbank().getId());
					account.setLastMovementDate(lastMovementDate);
					
					Thread thread3 = new Thread(() -> {
						try {
							if (account.getRequisition() != null && account.getRequisition().getInstitutionId() != null) {
								account.setInstitution(getInstitution(token, account.getRequisition().getInstitutionId()));
							}
						} catch (Exception e) {
							e.printStackTrace();
							account.addLog(e.getMessage());
						}
					});
					thread3.start();
					
					if (account.getMetadata() != null
						&& AonStringUtils.isNotBlank(accountId)
						&& NordigenRequisitionStatus.LN.equals(account.getRequisition().getStatus())
					) {
						
						Thread thread1 = new Thread(() -> {
							try {
								account.setDetails(getAccountDetails(token, accountId));
							} catch (Exception e) {
								e.printStackTrace();
								account.addLog(e.getMessage());
							}
						});
						Thread thread2 = new Thread(() -> {
							try {
								account.setBalances(getAccountBalances(token, accountId));
								account.setNotInsertedMovements(getNotInsertedTransactions(token, occam, account));
							} catch (Exception e) {
								e.printStackTrace();
								account.addLog(e.getMessage());
							}
						});
						
						thread1.start();
						thread2.start();
						thread2.join();
					}
					thread3.join();
					
				}
			}
			return NordigenDAO.updateRegistryBank(occam, account);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AonCoreException(e);
		}
	}

	public static List<NordigenBankStatement> getNotInsertedTransactions(NordigenAccessToken token, Occam occam, NordigenBankAccount account) {
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
				
				try {
					thread1.join();
					thread2.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new NordigenException(e.getMessage());
				}
				
				if (!exceptionMessage.isEmpty()) {
					throw new NordigenException(exceptionMessage.toString());
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

	public static List<RegistryBank> getRbanksByRequisition(Occam occam, String requisitionId) {
		List<RegistryBank> list = new ArrayList<>();
		AON.getRegistryBankStream(occam
				, f -> f.getRequisitionProperty().eq(requisitionId))
			.filter(Objects::nonNull)
			.forEach(list::add);
		return list;
	}
	
	public static RegistryBank updateRbank(Occam occam, RegistryBank rbank) {
		return AON.saveRegistryBank(occam, rbank);
	}
	
	public static boolean deleteRequisitionById(NordigenAccessToken token, Occam occam, String requisitionId) {
		NordigenRequisition requisition = AonNordigen.getRequisition(token, requisitionId);
		List<RegistryBank> rbanks = AonNordigen.getRbanksByRequisition(occam, requisitionId);
		boolean requisitionDeleted = true;
		if (requisition != null) {
			try {				
				AonNordigen.deleteRequisition(token, requisition);
			} catch (Exception e) {
				requisitionDeleted = false;
			}
		}
		if (rbanks != null) {
			for (RegistryBank rbank : rbanks) {
				rbank.setRequisition(null);
				AonNordigen.updateRbank(occam, rbank);
			}
		}
		return requisitionDeleted;
	}

	public static void deleteRequisitionByRbank(NordigenAccessToken token, Occam occam, Integer rbankId) {
		if (rbankId == null) return;
		RegistryBank rbank = AON.getRegistryBank(occam, f -> f.getIdProperty().eq(rbankId));
		if (rbank == null) return;
		
		String requisition = rbank.getRequisition();
		rbank.setRequisition(null);
		AON.saveRegistryBank(occam, rbank);
		if (requisition != null) {
			deleteRequisition(token, requisition);				
		}
	}
	
	public static NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) {
		JSONObject json = NordigenAPI.getRequisition(token.getAccess(), requisitionId);
		return NordigenRequisitionJSON.fromJSON(json);
	}

	public static List<NordigenBankStatement> getStoredBankStatements(Occam occam, RegistryBank rbank, Date dateFrom) {
		return NordigenDAO.getBankStatements(occam, rbank, dateFrom, new Date());
	}

	public static RegistryBank updateRequisitionId(Occam occam, NordigenRequisition requisition, Integer rbank) {
		if (requisition != null && requisition.getId() != null && AonNumberUtils.zeroIfNull(rbank) > 0) {
			String requisitionId = requisition.getId();
			RegistryBank registryBank = AON.getRegistryBank(occam, f -> f.getIdProperty().eq(rbank));
			if (registryBank != null) {
				registryBank.setRequisition(requisitionId);
				return AON.saveRegistryBank(occam, registryBank);
			}
		}
		return null;
	}

	public static void deleteRequisition(NordigenAccessToken token, NordigenRequisition requisition) {
		deleteRequisition(token, requisition != null ? requisition.getId() : null);
	}

	public static int insertStatements(Occam occam, NordigenBankAccount account) {
		return NordigenDAO.insertStatements(occam, account);
	}

	private static List<NordigenAccountTransaction> getPendingAccountTransactions(NordigenAccessToken token, String nordigenAccountId, Date dateFrom) {
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
	}

	public static List<NordigenBankStatement> getMovements(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online) {
		try {
			List<NordigenBankStatement> list = new LinkedList<>();
			if (nordigenBankAccount != null && nordigenBankAccount.getMetadata() != null) {
				String id = nordigenBankAccount.getMetadata().getId();
				if (online) {
					List<NordigenAccountTransaction> pendingTransactions = AonNordigen.getPendingAccountTransactions(token, id, endDate);
					List<NordigenAccountTransaction> transactions = AonNordigen.getBookedAccountTransactionsByBookingDate(token, nordigenBankAccount.getRequisition(), id, endDate);
					
					if (pendingTransactions != null) {
						pendingTransactions.stream().map(tr -> AonNordigen.nordigenToBankStatement(nordigenBankAccount, tr)).forEach(bs -> {
							bs.setPending(true);
							list.add(bs);
						});
					}
					if (transactions != null) {
						transactions.stream().map(tr -> AonNordigen.nordigenToBankStatement(nordigenBankAccount, tr)).forEach(bs -> {
							bs.setPending(false);
							list.add(bs);
						});
					}
					
				} else {
					list.addAll(nordigenBankAccount.getNotInsertedMovements());
					list.addAll(AonNordigen.getStoredBankStatements(occam, nordigenBankAccount.getRbank(), endDate));
				}
				
			}
			List<NordigenBankStatement> orderedList = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
			
			List<NordigenAccountBalance> balances = nordigenBankAccount.getBalances();
			
			NordigenAccountBalance consolidado = AonNordigen.filterConsolidado(balances);
			NordigenAccountBalance real = AonNordigen.filterReal(balances);
			
			
			NordigenAccountBalance balance = consolidado != null ? consolidado : real;
			
			
			Double remaining = balance.getBalanceAmount() != null ? balance.getBalanceAmount().getAmount() : 0;
			
			for(NordigenBankStatement bs : orderedList) {
				bs.setCurrentBalance(remaining);
				remaining += (bs.getAmount() * (bs.isPayment() ? 1 : -1));
			}
			
			return orderedList;
		} catch (NordigenException e) {
			throw e;
		} catch (Exception e) {
			throw mapException(e);
		}
	}

	public static List<NordigenInstitution> getInstitutions(NordigenAccessToken token, Country country, Boolean paymentsEnabled) {
		JSONArray json = NordigenAPI.getInstitutions(token.getAccess(), country, paymentsEnabled);
		return NordigenInstitutionJSON.fromJSONArray(json);
	}

	public static List<NordigenInstitution> getInstitutionsByBic(NordigenAccessToken token, String bic) {
		if (AonStringUtils.isNotBlank(bic)) {
			List<NordigenInstitution> institutions = AonNordigen.getInstitutions(token, null, null);
			List<NordigenInstitution> matchedInstitutions = institutions.stream()
				.filter(inst -> AonStringUtils.containsIgnoreCase(inst.getBic(), bic))
				.collect(Collectors.toList());
			if (matchedInstitutions != null && !matchedInstitutions.isEmpty()) {
				return matchedInstitutions;
			}
		}
		throw new NordigenException("No available institutions");
	}

	public static NordigenRequisition addAccount(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount) {
		RegistryBank rbank = nordigenBankAccount.getRbank();
		Pattern bicPattern = Pattern.compile("^(?<bic>.*?)X*$", Pattern.CASE_INSENSITIVE);
		Matcher bicMatcher = bicPattern.matcher(AonStringUtils.trimToEmpty(rbank.getBic()));
		StringBuilder bicBuilder = new StringBuilder();
		if (bicMatcher.matches()) {
			bicBuilder.append(AonStringUtils.trimToEmpty(bicMatcher.group("bic")));
		}
		final String bic = bicBuilder.toString();
		
		List<NordigenInstitution> instList = AonNordigen.getInstitutions(token, null, null)
			.stream()
			.filter(inst -> AonStringUtils.equalsIgnoreCase(inst.getBic(), bic))
			.toList();
		
		Optional<NordigenInstitution> optInstitution = Optional.ofNullable(instList.size() == 1 ? instList.get(0) : null);
		StringBuilder instIdBuilder = new StringBuilder();
		if (optInstitution.isPresent()) {
			instIdBuilder.append(AonStringUtils.trimToEmpty(optInstitution.get().getId()));
		}
		final String institutionId = instIdBuilder.toString();
		
		//EN CASO DE QUE HAYA PROBLEMAS CON EL BIC:
		NordigenInstitution inst = nordigenBankAccount.getInstitution();
		
		if (inst == null && instList.size() > 1) {
			throw new NordigenException("Too much institutions");
		}
		
		NordigenAgreement agreement = AonNordigen.createAgreement(token, inst != null ? inst.getId() : institutionId);
		NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement, "https://" + occam .getDomainName() + "/ms/api/task-evaluation/rbank?rbank=" + (rbank != null ? ""+rbank.getId() : ""));
		AonNordigen.updateRequisitionId(occam, requisition, rbank.getId());
		
		return requisition;
	}

	public static List<NordigenRequisition> findAllDomainRequisitions(NordigenAccessToken token, String currentDomainName) {
		LinkedList<NordigenRequisition> list = new LinkedList<>();
		AonCollectionUtils.stream( AonNordigen.getAllRequisitions(token) )
			.filter(req -> {
				Matcher matcher = LINK_PATTERN.matcher(req.getRedirect());
				if (matcher.matches()) {
					String link = matcher.group("link");
					return link != null && AonStringUtils.equalsIgnoreCase(currentDomainName, link);
				}
				return false;
			})
			.forEach(list::add);
		return list;
	}

	// -----------------------------------------------------------------
	// -------------------------------------------------------- [PRIVATE]
	// -----------------------------------------------------------------

//	private static void deleteAgreement(NordigenAccessToken token, String agreementId) {
//		NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreementId);
//	}
	
	private static NordigenRequisition createRequisition(NordigenAccessToken token, String agreementId, String institutionId, String redirect) {
		RequisitionParams params = new RequisitionParams()
			.setAgreement(agreementId)
			.setRedirect(redirect)
			.setInstitutionId(institutionId)
			.setUserLanguage(AonLanguage.SPANISH)
			.setRedirectImmediate(true);
		JSONObject json = NordigenAPI.createRequisition(token.getAccess(), params);
		return NordigenRequisitionJSON.fromJSON(json);
	}
	
	private static void deleteRequisition(NordigenAccessToken token, String requisitionId)  {
		NordigenAPI.deleteRequisition(token.getAccess(), requisitionId);
	}
	
	private static NordigenAccountMetadata getAccountMetadata(NordigenAccessToken token, String nordigenAccountId)  {
		JSONObject json = NordigenAPI.getAccount(token.getAccess(), nordigenAccountId);
		return NordigenAccountMetadataJSON.fromJSON(json);
	}
	
	private static NordigenAccountDetails getAccountDetails(NordigenAccessToken token, String nordigenAccountId) {
		JSONObject json = NordigenAPI.getDetails(token.getAccess(), nordigenAccountId);
		if (json != null) {
			return NordigenAccountDetailsJSON.fromJSON(json.optJSONObject("account"));
		}
		return null;
	}
	
	private static List<NordigenAccountBalance> getAccountBalances(NordigenAccessToken token, String nordigenAccountId) {
		JSONObject json = NordigenAPI.getBalances(token.getAccess(), nordigenAccountId);
		if (json != null) {
			JSONArray balancesJson = json.optJSONArray("balances");
			return NordigenAccountBalanceJSON.fromJSONArray(balancesJson);
		}
		return Collections.emptyList();
	}
	
	
	private static List<NordigenAccountTransaction> getBookedAccountTransactionsByBookingDate(NordigenAccessToken token, NordigenRequisition requisition, String nordigenAccountId, Date dateFrom) {
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
	}
	
//	private static List<NordigenAccountTransaction> getBookedAccountTransactions(NordigenAccessToken token, String nordigenAccountId, Date dateFrom) {
//		JSONObject json = NordigenAPI.getTransactions(token.getAccess(), nordigenAccountId, dateFrom, new Date());
//		if (json != null) {
//			JSONObject transactionsJson = json.optJSONObject("transactions");
//			JSONArray bookedTransactionsJson = null;
//			if (transactionsJson != null) {
//				bookedTransactionsJson = transactionsJson.optJSONArray("booked");
//			}
//			return NordigenAccountTransactionJSON.fromJSONArray(bookedTransactionsJson);
//		}
//		return Collections.emptyList();
//	}
	
	private static NordigenBankAccount getAccountByRbank(Occam occam, Integer rbankId) {
		RegistryBank rbank = AON.getRegistryBank(occam, f -> f.getIdProperty().eq(rbankId));
		Date lastMovementDate = getLastMovementDate(occam, rbankId);
		return new NordigenBankAccount()
				.setRbank(rbank)
				.setIban(rbank != null && rbank.getBankAccount() != null ? rbank.getBankAccount().getIban() : null)
				.setBankAlias(rbank != null ? rbank.getAlias() : null)
				.setLinked(AonStringUtils.isNotBlank(rbank.getRequisition()))
				.setRequisitionId(rbank.getRequisition())
				.setLastMovementDate(lastMovementDate);
	}
	
	private static List<NordigenBankAccount> getAllAccounts(NordigenAccessToken token, Occam occam) {
		Company company = AON.getCompany(occam, f -> f.getDomainProperty().eq(occam.getDomain()));
		return  AON.getRegistryBankStream(occam
			,f -> f.getActiveProperty().eq(AonEnumUtils.getByte(true))
				.and(f.getRegistryProperty().eq(company.getId()))
		).map(rbank -> {
			try {
				return getAccountByRbank(occam, rbank.getId());
			} catch (Exception e) {
				return null;
			}
		}).collect(Collectors.toList());
	}
	
	private static NordigenAccountMetadata getRbankNordigenAccountId(NordigenAccessToken token, NordigenRequisition requisition, RegistryBank rbank) {
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
	
//	private static Map<Integer, NordigenRequisition> filterNonValidRequisitions(Map<Integer, NordigenRequisition> requisitions) {
//		Map<Integer, NordigenRequisition> nonValid = new HashMap<>();
//		if (requisitions != null) {
//			requisitions.entrySet().stream()
//			.filter(entry -> entry != null && !isRequisitionLinked(entry.getValue()))
//			.forEach(entry -> nonValid.put(entry.getKey(), entry.getValue()));
//		}
//		return nonValid;
//	}
	
	
	private static NordigenBankStatement nordigenToBankStatement(NordigenBankAccount account, NordigenAccountTransaction transaction) {
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
	
	private static Date getLastMovementDate(Occam occam, Integer rbankId) {
		return NordigenDAO.getLastMovementDate(occam, rbankId);
	}
	
	private static NordigenAccountBalance filterConsolidado(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance consolidado = null;
		
		consolidado = balances.stream()
				.filter(bal -> NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);
		
		if (consolidado == null && balances.size() > 0) {
			return balances.get(0);
		}
		
		return consolidado;
	}
	
	private static NordigenAccountBalance filterReal(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance real = null;
		
		real = balances.stream()
				.filter(bal -> !NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);
		
		if (real == null) {
			return filterConsolidado(balances);
		}
		
		return real;
	}
	
	private static NordigenException mapException(Exception exception) throws IllegalArgumentException{
		JSONObject errJson = new JSONObject(exception.getMessage());
		String err = errJson.getString("result");
		return new NordigenException(err);						
	}

	// -------------------------- Métodos con tests.
	static NordigenAgreement createAgreement(NordigenAccessToken token, String institutionId)  {
		Integer maxDays = 90;
		JSONObject json = NordigenAPI.createEndUserAgreement(token.getAccess(), maxDays, 90, null, institutionId);
		return NordigenAgreementJSON.fromJSON(json);
	}
	
	static NordigenAgreement getAgreement(NordigenAccessToken token, String agreementId) {
		JSONObject json = NordigenAPI.getEndUserAgreement(token.getAccess(), agreementId);
		return NordigenAgreementJSON.fromJSON(json);
	}

	static void deleteAgreement(NordigenAccessToken token, NordigenAgreement agreement)  {
		NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreement != null ? agreement.getId() : null);
	}
	
	static NordigenInstitution getInstitution(NordigenAccessToken token, String institutionId) {
		JSONObject json = NordigenAPI.getInstitution(token.getAccess(), institutionId);
		return NordigenInstitutionJSON.fromJSON(json);
	}
	
	static NordigenRequisition createRequisition(NordigenAccessToken token, NordigenAgreement agreement, String redirect)  {
		return  createRequisition(token,
			agreement != null ? agreement.getId() : null,
			agreement != null ? agreement.getInstitutionId() : null,
			redirect);
	}
}
