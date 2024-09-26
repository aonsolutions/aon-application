package net.aonsolutions.aon.bank.nordigen;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.NordigenBankStatementFilter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountDetail;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransactions;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBalanceType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitions;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenResponse;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.impl.jooq.dao.BankStatementDAO;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonNordigen {

	private static final String LINK_REGEX = "^https\\:\\/\\/(?<link>.*?)\\/ms\\/.*$";
	private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);
	private static final Integer MAX_DAYS = 90;

	private AonNordigen() {
	}

	public static NordigenConfiguration getConfiguration(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return new NordigenConfiguration().setConfiguration(AON.getConfiguration(ctx))
					.setToken(AonNordigen.getNewAccessToken()).setAccounts(NordigenDAO.getAllAccounts(ctx));
		} catch (AonCoreException ex) {
//			NordigenUtils.logException(ex);
			throw new NordigenException(ex.getMessage());
		}
	}

	static NordigenAccessToken getNewAccessToken() {
		NordigenAccessToken token = NordigenAPI.newAccessToken();
		Date tokenCreationDate = new Date();
		return token.setCreationDate(tokenCreationDate).setRefreshDate(tokenCreationDate);
	}

	static NordigenAccessToken refreshToken(NordigenAccessToken token) {
		return token.refreshAccessToken(NordigenAPI.refreshAccessToken(token.getRefresh())).setRefreshDate(new Date());
	}

	static NordigenInstitution getInstitution(NordigenAccessToken token, String institutionId) {
		return NordigenAPI.getInstitution(token.getAccess(), institutionId);
	}

	static NordigenAgreement createAgreement(NordigenAccessToken token, String institutionId) {
		return NordigenAPI.createEndUserAgreement(token.getAccess(), MAX_DAYS, MAX_DAYS, null, institutionId);
	}

	static NordigenAgreement getAgreement(NordigenAccessToken token, String agreementId) {
		return NordigenAPI.getEndUserAgreement(token.getAccess(), agreementId);
	}

	public static List<NordigenInstitution> getInstitutions(NordigenAccessToken token, Country country,
			Boolean paymentsEnabled) {
		return NordigenAPI.getInstitutions(token.getAccess(), country, paymentsEnabled);
	}

	public static List<NordigenInstitution> getAllInstitutions(NordigenAccessToken token) {
		return NordigenAPI.getInstitutions(token.getAccess(), null, null);
	}

	public static List<NordigenInstitution> getInstitutionsByBic(NordigenAccessToken token, String bic) {
		if (AonStringUtils.isNotBlank(bic)) {
			List<NordigenInstitution> matchedInstitutions = AonCollectionUtils
					.stream(AonNordigen.getInstitutions(token, null, null))
					.filter(inst -> AonStringUtils.containsIgnoreCase(inst.getBic(), bic)).collect(Collectors.toList());
			if (AonCollectionUtils.isNotEmpty(matchedInstitutions)) {
				return matchedInstitutions;
			}
		}
		throw new NordigenException("No available institutions");
	}

	private static NordigenRequisition createRequisition(NordigenAccessToken token, String agreementId,
			String institutionId, String redirect) {
		RequisitionParams params = new RequisitionParams().setAgreement(agreementId).setRedirect(redirect)
				.setInstitutionId(institutionId).setUserLanguage(AonLanguage.SPANISH).setRedirectImmediate(true);
		return NordigenAPI.createRequisition(token.getAccess(), params);
	}

	public static NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) {
		return NordigenAPI.getRequisition(token.getAccess(), requisitionId);
	}

	private static NordigenResponse deleteRequisition(NordigenAccessToken token, String requisitionId) {
		return NordigenAPI.deleteRequisition(token.getAccess(), requisitionId);
	}

	public static List<NordigenRequisition> getDomainRequisitions(NordigenAccessToken token, String domainName) {
		LinkedList<NordigenRequisition> list = new LinkedList<>();
		AonCollectionUtils.stream(AonNordigen.getAllRequisitions(token)).filter(req -> {
			Matcher matcher = LINK_PATTERN.matcher(req.getRedirect());
			if (matcher.matches()) {
				String link = matcher.group("link");
				return link != null && AonStringUtils.equalsIgnoreCase(domainName, link);
			}
			return false;
		}).forEach(list::add);
		return list;
	}

	private static void addRequisitions(List<NordigenRequisition> list, NordigenRequisitions requisitions) {
		if (requisitions != null && requisitions.getResult() != null) {
			list.addAll(requisitions.getResult());
		}
	}

	static List<NordigenRequisition> getAllRequisitions(NordigenAccessToken token) {
		List<NordigenRequisition> list = new LinkedList<>();
		NordigenRequisitions requisitions = NordigenAPI.getRequisitions(token.getAccess(), 1000000, 0);
		addRequisitions(list, requisitions);
		String nextUrl = requisitions == null ? null : requisitions.getNext();
		while (nextUrl != null) {
			Pattern offsetPattern = Pattern.compile("offset=(?<offset>\\d+)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = offsetPattern.matcher(nextUrl);
			String offsetStr = null;
			if (matcher.find()) {
				offsetStr = matcher.group("offset");
			}
			int offset = AonNumberUtils.toint(offsetStr);
			if (offset > 0) {
				requisitions = NordigenAPI.getRequisitions(token.getAccess(), 1000000, offset);
				addRequisitions(list, requisitions);
				nextUrl = requisitions == null ? null : requisitions.getNext();
			} else {
				nextUrl = null;
			}
		}
		return list;
	}

	private static NordigenAccountMetadata getAccountMetadata(NordigenAccessToken token, String nordigenAccountId) {
		return NordigenAPI.getAccountMetadata(token.getAccess(), nordigenAccountId);
	}

	private static LinkedList<NordigenAccountBalance> getAccountBalances(NordigenAccessToken token,
			String nordigenAccountId) {
		return NordigenAPI.getBalances(token.getAccess(), nordigenAccountId);
	}

	// NUEVOOOO
	public static void updateAccountBalances(NordigenBankAccount account, Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			NordigenDAO.updateRegistryBank(ctx, account);
		}

	}

	public static Stream<BankStatement> getMovementsFromBD(Occam occam, NordigenBankStatementFilter filter,
			Integer page, Integer perPage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return NordigenDAO.getMovementsFromDB(ctx, filter, page, perPage);
		}
	}

//	private static NordigenAccountDetail getAccountDetail(NordigenAccessToken token, String nordigenAccountId) {
//		return NordigenAPI.getDetail(token.getAccess(), nordigenAccountId);
//	}

	private static NordigenAccountTransactions getTransactions(NordigenAccessToken token, String nordigenAccountId,
			Date dateFrom) {
		try {
			return NordigenAPI.getTransactions(token.getAccess(), nordigenAccountId, null, null);
		} catch (Exception e) {
//			NordigenUtils.logException(e);
			return null;
		}
	}

	private static List<NordigenBankStatement> getPendingAccountTransactions(NordigenBankAccount account,
			NordigenAccountTransactions transactions) {
		if (transactions != null) {
			return AonCollectionUtils.stream(transactions.getPending())
					.map(t -> NordigenBankAccount.toBankStatement(account, t)).filter(Objects::nonNull)
					.map(st -> st.setPending(true)).collect(Collectors.toCollection(LinkedList::new));
		}
		return new LinkedList<>();

	}

//	private static List<NordigenBankStatement> getPendingAccountTransactions(NordigenBankAccount account, NordigenAccountTransactions transactions) {
//		if (transactions != null) {
//			
//			try {
//				return AonCollectionUtils.stream( transactions.getPending())
//						.map(t -> NordigenBankAccount.toBankStatement(account,t))
//						.filter( Objects::nonNull)
//						.map(st -> st.setPending(true))
//						.collect(Collectors.toCollection( LinkedList::new));
//			} catch (Exception e) {
//				NordigenUtils.logException(e);
//			}
//
//		}
//		return new LinkedList<>();		
//	}

	private static List<NordigenBankStatement> getBookedAccountTransactions(NordigenBankAccount account,
			NordigenAccountTransactions transactions, Date dateFrom) {
		if (transactions != null) {
			return AonCollectionUtils.stream(transactions.getBooked()).filter(Objects::nonNull)
					.filter(tr -> AonDateUtils.compare(AonDateUtils.todayIfNull(tr.getBookingDate()), dateFrom) >= 0)
					.map(t -> NordigenBankAccount.toBankStatement(account, t)).map(st -> st.setPending(false))
					.collect(Collectors.toCollection(LinkedList::new));
		}
		return new LinkedList<>();
	}

	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************
	/// ************************************************************************************

	// ----------------------------------------------------------------
	// -------------------------------------------------------- [PUBLIC]
	// ----------------------------------------------------------------

	public static NordigenBankAccount setBankAccountValues(Occam occam, NordigenAccessToken token,
			NordigenBankAccount account) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			RegistryBank rbank = account.getRbank();
//			RegistryBank rbank = null;
			if (rbank.getRequisition() != null) {
				String reqId = rbank.getRequisition();
				if (AonStringUtils.isNotBlank(reqId)) {
					account.setLinked(true);
					account.setRequisition(getRequisition(token, reqId));
					account.setMetadata(getNordigenAccountMetadata(token, account.getRequisition(), rbank));
					String accountId = account.getMetadata() != null ? account.getMetadata().getId() : null;
					account.setLastMovementDate(BankStatementDAO.getLastMovementDate(ctx, account.getRbank().getId()));

					Thread thread3 = new Thread(() -> {
						try {
							if (account.getRequisition() != null
									&& account.getRequisition().getInstitutionId() != null) {
								account.setInstitution(
										getInstitution(token, account.getRequisition().getInstitutionId()));
							}
						} catch (Exception e) {
//							NordigenUtils.logException(e);
							e.printStackTrace();
							account.addLog(e.getMessage());
						}
					});
					thread3.start();

					if (account.getMetadata() != null && AonStringUtils.isNotBlank(accountId)
							&& NordigenRequisitionStatus.LINKED.equals(account.getRequisition().getStatus())) {

//						Thread thread1 = new Thread(() -> {
//							try {
//								account.setDetail(getAccountDetail(token, accountId));
//							} catch (Exception e) {
//								e.printStackTrace();
//								account.addLog(e.getMessage());
//							}
//						});
						Thread thread2 = new Thread(() -> {
							try {
								account.setBalances(getAccountBalances(token, accountId));
								account.setNotInsertedMovements(getNotInsertedTransactions(token, account));
							} catch (Exception e) {
								e.printStackTrace();
								account.addLog(e.getMessage());
							}
						});

//						thread1.start();
						thread2.start();
						thread2.join();
					}
					thread3.join();

				}
			}
			return NordigenDAO.updateRegistryBank(ctx, account);
		} catch (InterruptedException e) {
//			NordigenUtils.logException(e);
			Thread.currentThread().interrupt();
			throw new AonCoreException(e);
		}
	}

	private static LinkedList<NordigenBankStatement> getNotInsertedTransactions(NordigenAccessToken token,
			NordigenBankAccount account) {
		NordigenAccountMetadata metadata = account.getMetadata();
		String accId = metadata != null ? AonStringUtils.trimToNull(metadata.getId()) : null;
		Date dateFrom = guessDateForm(account);
		if (dateFrom != null) {
			StringBuilder exceptionMessage = new StringBuilder();
			NordigenAccountTransactions transactions = AonNordigen.getTransactions(token, accId, dateFrom);
			LinkedList<NordigenBankStatement> stList = new LinkedList<>();
			stList.addAll(getPendingAccountTransactions(account, transactions));
			stList.addAll(getBookedAccountTransactions(account, transactions, dateFrom));
			if (!exceptionMessage.isEmpty()) {
				throw new NordigenException(exceptionMessage.toString());
			}

			NordigenAccountBalance consBalance = filterConsolidado(account.getBalances());
			if (consBalance != null && consBalance.getBalanceAmount() != null) {
				double amount = consBalance.getBalanceAmount().getAmount();
				for (NordigenBankStatement statement : stList) {
					statement.setCurrentBalance(amount);
					int factor = (statement.isPayment() ? (-1) : 1);
					amount = AonMathUtils.round(amount - (statement.getAmount() * factor));
				}
			}
			return stList;
		}
		return new LinkedList<>();
	}

	private static Date guessDateForm(NordigenBankAccount account) {
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
			return dateFrom;
		}
		return null;
	}

	public static List<RegistryBank> getRbanksByRequisition(Occam occam, String requisitionId) {
		List<RegistryBank> list = new ArrayList<>();
		AON.getRegistryBankStream(occam, f -> f.getRequisitionProperty().eq(requisitionId)).filter(Objects::nonNull)
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
		if (rbankId == null)
			return;
		RegistryBank rbank = AON.getRegistryBank(occam, f -> f.getIdProperty().eq(rbankId));
		if (rbank == null)
			return;

		String requisition = rbank.getRequisition();
		rbank.setRequisition(null);
		AON.saveRegistryBank(occam, rbank);
		if (requisition != null) {
			deleteRequisition(token, requisition);
		}
	}

	public static List<NordigenBankStatement> getStoredBankStatements(Occam occam, RegistryBank rbank, Date dateFrom) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return NordigenDAO.getBankStatements(ctx, rbank, dateFrom, new Date());
		}

	}

	public static RegistryBank updateRequisitionId(Occam occam, NordigenRequisition requisition, Integer rbank) {
		try {
			if (requisition != null && requisition.getId() != null && AonNumberUtils.zeroIfNull(rbank) > 0) {
				String requisitionId = requisition.getId();
				RegistryBank registryBank = AON.getRegistryBank(occam, f -> f.getIdProperty().eq(rbank));
				if (registryBank != null) {
					registryBank.setRequisition(requisitionId);
					return AON.saveRegistryBank(occam, registryBank);
				}
			}
		} catch (Exception e) {
//			NordigenUtils.logException(e);
		}

		return null;
	}

	public static void deleteRequisition(NordigenAccessToken token, NordigenRequisition requisition) {
		deleteRequisition(token, requisition != null ? requisition.getId() : null);
	}

	public static int insertStatements(Occam occam, NordigenBankAccount account) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return NordigenDAO.insertStatements(ctx, account);
		}
	}

	public static List<NordigenBankStatement> getMovements(NordigenAccessToken token, Occam occam,
			NordigenBankAccount nordigenBankAccount, Date endDate, boolean online) {

		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			List<NordigenBankStatement> list = new LinkedList<>();
			if (nordigenBankAccount != null && nordigenBankAccount.getMetadata() != null) {
				String id = nordigenBankAccount.getMetadata().getId();
//				online = false;
				if (online) {
					NordigenAccountTransactions transactions = AonNordigen.getTransactions(token, id, endDate);
					list.addAll(getPendingAccountTransactions(nordigenBankAccount, transactions));
					list.addAll(getBookedAccountTransactions(nordigenBankAccount, transactions, endDate));
//					if (transactions != null) {
//						AonCollectionUtils.stream(AonNordigen.getPendingAccountTransactions(transactions))
//						.map(tr -> nordigenBankAccount.toBankStatement(tr)).forEach(bs -> {
//							bs.setPending(true);
//							list.add(bs);
//						});
//						AonCollectionUtils.stream(AonNordigen.getBookedAccountTransactions(transactions,endDate))
//						.map(tr -> nordigenBankAccount.toBankStatement(tr)).forEach(bs -> {
//							bs.setPending(false);
//							list.add(bs);
//						});
//					}
				} else {
					list.addAll(nordigenBankAccount.getNotInsertedMovements());
					list.addAll(AonNordigen.getStoredBankStatements(occam, nordigenBankAccount.getRbank(), endDate));
				}

			}
			List<NordigenBankStatement> orderedList = list.stream().filter(Objects::nonNull)
					.collect(Collectors.toList());
			List<NordigenAccountBalance> balances = nordigenBankAccount.getBalances();
			NordigenAccountBalance consolidado = AonNordigen.filterConsolidado(balances);

			NordigenAccountBalance real = AonNordigen.filterReal(balances);
			NordigenAccountBalance balance = consolidado != null ? consolidado : real;

			Double remaining = (balance != null && balance.getBalanceAmount() != null)
					? balance.getBalanceAmount().getAmount()
					: 0;
			for (NordigenBankStatement bs : orderedList) {
				bs.setCurrentBalance(remaining);
				remaining += (bs.getAmount() * (bs.isPayment() ? 1 : -1));
			}
			return orderedList;
		} catch (NordigenException e) {
//			NordigenUtils.logException(e);
			throw e;
		} catch (Exception e) {
			throw mapException(e);
		}
	}

	public static NordigenRequisition addAccount(NordigenAccessToken token, Occam occam,
			NordigenBankAccount nordigenBankAccount) {
		RegistryBank rbank = nordigenBankAccount.getRbank();

		if (rbank.getBankAccount().getIban().equals("GL8262400000062409")
				|| rbank.getBankAccount().getIban().equals("GL4076010000076016")) {
			NordigenAgreement agreement = AonNordigen.createAgreement(token, "SANDBOXFINANCE_SFIN0000");
			String redirect = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

			NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement, redirect);
			AonNordigen.updateRequisitionId(occam, requisition, rbank.getId());
			return requisition;

		} else {

			Pattern bicPattern = Pattern.compile("^(?<bic>.*?)X*$", Pattern.CASE_INSENSITIVE);
			Matcher bicMatcher = bicPattern.matcher(AonStringUtils.trimToEmpty(rbank.getBic()));
			StringBuilder bicBuilder = new StringBuilder();

			if (bicMatcher.matches()) {
				bicBuilder.append(AonStringUtils.trimToEmpty(bicMatcher.group("bic")));
			}
			final String bic = bicBuilder.toString();

			List<NordigenInstitution> instList = AonNordigen.getInstitutions(token, null, null).stream()
					.filter(inst -> AonStringUtils.equalsIgnoreCase(inst.getBic(), bic)).toList();

			if (instList.isEmpty()) {
				instList = AonNordigen.getInstitutionsByBic(token, bic);
			}
			Optional<NordigenInstitution> optInstitution = Optional
					.ofNullable(instList.size() == 1 ? instList.get(0) : null);
			StringBuilder instIdBuilder = new StringBuilder();
			if (optInstitution.isPresent()) {
				instIdBuilder.append(AonStringUtils.trimToEmpty(optInstitution.get().getId()));
			}
			final String institutionId = instIdBuilder.toString();

			// EN CASO DE QUE HAYA PROBLEMAS CON EL BIC:
			NordigenInstitution inst = nordigenBankAccount.getInstitution();

			if (inst == null && instList.size() > 1) {
				throw new NordigenException("Too much institutions");
			}

			NordigenAgreement agreement = AonNordigen.createAgreement(token,
					inst != null ? inst.getId() : institutionId);

			NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement,
					"https://" + occam.getDomainName() + "/ms/api/task-evaluation/rbank?rbank="
							+ (rbank != null ? "" + rbank.getId() : ""));
			AonNordigen.updateRequisitionId(occam, requisition, rbank.getId());

			return requisition;
		}
	}

	// -----------------------------------------------------------------
	// -------------------------------------------------------- [PRIVATE]
	// -----------------------------------------------------------------

//	private static void deleteAgreement(NordigenAccessToken token, String agreementId) {
//		NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreementId);
//	}

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

	private static NordigenAccountMetadata getNordigenAccountMetadata(NordigenAccessToken token,
			NordigenRequisition requisition, RegistryBank rbank) {
		if (requisition != null && rbank != null && rbank.getBankAccount() != null) {
			String iban = rbank.getBankAccount().getIban();
			if (AonStringUtils.isBlank(iban)) {
				return null;
			}
			List<String> accs = requisition.getAccounts();
			if (accs != null) {
				for (String accId : accs) {
					try {
						NordigenAccountMetadata metadata = getAccountMetadata(token, accId);
						NordigenAccountDetail detail = NordigenAPI.getDetail(token.getAccess(), accId);
						if (metadata != null && AonStringUtils.equalsIgnoreCase(iban, metadata.getIban())
								&& detail.getCurrency().equals("EUR")) {
							return metadata;
						}
					} catch (Exception e) {
//						NordigenUtils.logException(e);
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

	private static NordigenAccountBalance filterConsolidado(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance consolidado = null;
		consolidado = balances.stream().filter(bal -> NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);

		if (consolidado == null && balances.size() > 0) {
			return balances.get(0);
		}

		return consolidado;
	}

	private static NordigenAccountBalance filterReal(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance real = null;

		real = balances.stream().filter(bal -> !NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);

		if (real == null) {
			return filterConsolidado(balances);
		}

		return real;
	}

	private static NordigenException mapException(Exception exception) throws IllegalArgumentException {
		exception.printStackTrace();
		String err = null;
		try {
			JSONObject errJson = new JSONObject(exception.getMessage());
			err = errJson.getString("result");
		} catch (Exception e) {
			err = exception.getMessage();
		}
		return new NordigenException(err);
	}

	// -------------------------- Métodos con tests.

	static void deleteAgreement(NordigenAccessToken token, NordigenAgreement agreement) {
		NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreement != null ? agreement.getId() : null);
	}

	static NordigenRequisition createRequisition(NordigenAccessToken token, NordigenAgreement agreement,
			String redirect) {
		return createRequisition(token, agreement != null ? agreement.getId() : null,
				agreement != null ? agreement.getInstitutionId() : null, redirect);
	}
}
