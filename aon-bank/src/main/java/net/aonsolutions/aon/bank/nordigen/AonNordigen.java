package net.aonsolutions.aon.bank.nordigen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountStatus;
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
import com.esferalia.aon.occam.impl.jooq.dao.NordigenCallLogDAO;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonNordigen  {

	private static final String LINK_REGEX = "^https\\:\\/\\/(?<link>.*?)\\/ms\\/.*$";
	private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);
	private static final Integer MAX_DAYS = 90;

	
	private AonNordigen() {
	}
	
	public static NordigenConfiguration getConfiguration(Occam occam) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(occam)) {
			return new NordigenConfiguration()
				.setConfiguration(AON.getConfiguration(ctx))
				.setToken(AonNordigen.getNewAccessToken())
				.setAccounts(NordigenDAO.getAllAccounts(ctx));
		} catch ( AonCoreException ex) {
			throw new NordigenException(ex.getMessage());
		}
	}

	static NordigenAccessToken getNewAccessToken() {
		NordigenAccessToken token = NordigenAPI.newAccessToken();
		Date tokenCreationDate = new Date();
		return token
			.setCreationDate(tokenCreationDate)
			.setRefreshDate(tokenCreationDate);
	}
	
	static NordigenAccessToken refreshToken(NordigenAccessToken token)  {
		return token.refreshAccessToken( NordigenAPI.refreshAccessToken(token.getRefresh()) )
			.setRefreshDate(new Date());
	}

	static NordigenInstitution getInstitution(NordigenAccessToken token, String institutionId) {
		return NordigenAPI.getInstitution(token.getAccess(), institutionId);
	}
	
	static NordigenAgreement createAgreement(NordigenAccessToken token, String institutionId)  {
		return NordigenAPI.createEndUserAgreement(token.getAccess(), MAX_DAYS, MAX_DAYS, null, institutionId);
	}
	
	static NordigenAgreement getAgreement(NordigenAccessToken token, String agreementId) {
		return NordigenAPI.getEndUserAgreement(token.getAccess(), agreementId);
	}
	
	public static List<NordigenInstitution> getInstitutionsByCountry(NordigenAccessToken token, Country country) {
		return NordigenAPI.getInstitutionsByCountry(token.getAccess(), country);
	}

	public static List<NordigenInstitution> getInstitutionsByBic(NordigenAccessToken token, String bic) {
		if (AonStringUtils.isNotBlank(bic)) {
			List<NordigenInstitution> matchedInstitutions = 
				AonCollectionUtils.stream(AonNordigen.getInstitutionsByCountry(token, null))
					.filter(inst -> AonStringUtils.containsIgnoreCase(inst.getBic(), bic))
					.collect(Collectors.toList());
			if (AonCollectionUtils.isNotEmpty(matchedInstitutions)) {
				return matchedInstitutions;
			}
		}
		throw new NordigenException("No available institutions");
	}
	
	private static NordigenRequisition createRequisition(NordigenAccessToken token, String agreementId, String institutionId, String redirect) {
		RequisitionParams params = new RequisitionParams()
			.setAgreement(agreementId)
			.setRedirect(redirect)
			.setInstitutionId(institutionId)
			.setUserLanguage(AonLanguage.SPANISH)
			.setRedirectImmediate(true);
		return NordigenAPI.createRequisition(token.getAccess(), params);
	}
	
	public static NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) {
		return NordigenAPI.getRequisition(token.getAccess(), requisitionId);
	}
	
	private static NordigenResponse deleteRequisition(NordigenAccessToken token, String requisitionId)  {
		return NordigenAPI.deleteRequisition(token.getAccess(), requisitionId);
	}
	
	public static List<NordigenRequisition> getDomainRequisitions(NordigenAccessToken token, String domainName) {
		LinkedList<NordigenRequisition> list = new LinkedList<>();
		AonCollectionUtils.stream( AonNordigen.getAllRequisitions(token) )
			.filter(req -> {
				Matcher matcher = LINK_PATTERN.matcher(req.getRedirect());
				if (matcher.matches()) {
					String link = matcher.group("link");
					return link != null && AonStringUtils.equalsIgnoreCase(domainName, link);
				}
				return false;
			})
			.forEach(list::add);
		return list;
	}

	private static void addRequisitions(List<NordigenRequisition> list,  NordigenRequisitions requisitions) {
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
	
	private static NordigenAccountMetadata getAccountMetadata(NordigenAccessToken token, String nordigenAccountId)  {
		return NordigenAPI.getAccountMetadata(token.getAccess(), nordigenAccountId);
	}
	
	public static int getRemainingCallsToday(Occam occam, NordigenBankAccount account) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(occam)) {

		return NordigenCallLogDAO.getCallsMadeToday(ctx, occam.getDomain(), account.getRbank().getId());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static String getLatestRetryAfter(Occam occam, NordigenBankAccount account) {
	    try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
	        
	        LocalDateTime retryAfterTime = NordigenCallLogDAO.getLatestRetryAfter(ctx, occam.getDomain(), account.getRbank().getId(), "fail_update");

	        if (retryAfterTime != null) {
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm:ss");
	            return retryAfterTime.format(formatter);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	   
	    return null;
	}

//	private static NordigenAccountDetail getAccountDetail(NordigenAccessToken token, String nordigenAccountId) {
//		return NordigenAPI.getDetail(token.getAccess(), nordigenAccountId);
//	}

	private static LinkedList<NordigenAccountBalance> getAccountBalances(AONContext ctx, NordigenAccessToken token, String nordigenAccountId) {
	    return NordigenAPI.getBalances(token.getAccess(), nordigenAccountId);
	}

	private static NordigenAccountTransactions getTransactions(AONContext ctx, NordigenAccessToken token, String nordigenAccountId, Date dateFrom) {
	    return NordigenAPI.getTransactions(token.getAccess(), nordigenAccountId, null, null);
	}

	private static List<NordigenBankStatement> getPendingAccountTransactions(NordigenBankAccount account, NordigenAccountTransactions transactions) {
		if (transactions != null) {
			return AonCollectionUtils.stream( transactions.getPending() )
				.map(t -> NordigenBankAccount.toBankStatement(account,t))
				.filter( Objects::nonNull)
				.map(st -> st.setPending(true))
				.collect(Collectors.toCollection( LinkedList::new));
		}
		return new LinkedList<>();
	}
	
	private static List<NordigenBankStatement> getBookedAccountTransactions(NordigenBankAccount account, NordigenAccountTransactions transactions, Date dateFrom) {
		if (transactions != null) {
			return AonCollectionUtils.stream( transactions.getBooked() )
				.filter( Objects::nonNull)
				.filter(tr -> AonDateUtils.compare(AonDateUtils.todayIfNull(tr.getBookingDate()), dateFrom) >= 0)
				.map(t -> NordigenBankAccount.toBankStatement(account,t))
				.map(st -> st.setPending(false))
				.collect(Collectors.toCollection( LinkedList::new));
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

	
	public static NordigenBankAccount loadStoredAccount(Occam occam, NordigenAccessToken token, NordigenBankAccount account) {
	    try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
	        RegistryBank rbank = account.getRbank();

	        if (rbank != null) {
	            account = NordigenDAO.loadStoredBalance(ctx, account);
	            account.setLastMovementDate(BankStatementDAO.getLastMovementDate(ctx, rbank.getId()));
	        }
	        NordigenAccountBalance consolidado =  filterConsolidado(account.getBalances());
//	        System.out.println("consolidado : " + consolidado.getBalanceAmount().getAmount());
//	        NordigenAccountBalance real =  filterReal(account.getBalances());
//	        System.out.println("real : " + real.getBalanceAmount().getAmount());

//	        for (int i = 0; i < account.getBalances().size(); i++) {
//				System.out.println(account.getBalances().size());
//				NordigenAccountBalance bal = account.getBalances().get(0);
//				double	bankBalance = bal.getBalanceAmount().getAmount();
//				double remainder = bal.getBalanceAmount().getAmount();
//				System.out.println(bankBalance);
//				System.out.println(remainder);
//	        	System.out.println(account.getBalances().get(i).getBalanceType());
//				System.out.println(account.getBalances().get(i).getBalanceAmount().getAmount());
//			}
	        
	        double	bankBalance = consolidado != null && consolidado.getBalanceAmount() != null
				    ? AonNumberUtils.zeroIfNull(consolidado.getBalanceAmount().getAmount())
				    : 0;
	        System.out.println("bankbalance: " + bankBalance);
	        return account;
	    }
	}
	
	public static boolean waitForAccountReady(NordigenAccessToken token, NordigenBankAccount account, int maxRetries, int delayMillis) {
	    for (int i = 0; i < maxRetries; i++) {
	        try {
	            NordigenAccountMetadata metadata = getNordigenAccountMetadata(token, account.getRequisition(), account.getRbank());
	            NordigenAccountStatus status = metadata != null ? metadata.getStatus() : null;

	            if (NordigenAccountStatus.READY.equals(status)) {
	                return true;
	            }

	            Thread.sleep(delayMillis);
	        } catch (Exception e) {
	            account.addLog("Error al verificar estado de cuenta: " + e.getLocalizedMessage());
	        }
	    }
	    account.addLog("Cuenta no llegó al estado READY tras " + maxRetries + " intentos.");
	    return false;
	}

	public static NordigenBankAccount setBankAccountValues(Occam occam, NordigenAccessToken token,
			NordigenBankAccount account, boolean refresh) {

		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {

			RegistryBank rbank = account.getRbank();

			if (rbank.getRequisition() == null || AonStringUtils.isBlank(rbank.getRequisition())) {
				return account;
			}

			account.setLinked(true);
			account.setLastMovementDate(BankStatementDAO.getLastMovementDate(ctx, rbank.getId()));

			account.setRequisition(getRequisition(token, rbank.getRequisition()));
			account.setMetadata(getNordigenAccountMetadata(token, account.getRequisition(), rbank));

			try {
				if (account.getRequisition() != null && account.getRequisition().getInstitutionId() != null) {
					account.setInstitution(getInstitution(token, account.getRequisition().getInstitutionId()));
				}
			} catch (Exception e) {
				e.printStackTrace();
				account.addLog(e.getMessage());
			}

			if (!refresh) {
				System.out.println("entra en loadStoreAccount");
				return loadStoredAccount(occam, token, account);
			}
			boolean ready = waitForAccountReady(token, account, 10, 2000);

			if (!ready) {
				account.addLog("La cuenta no alcanzó el estado READY. No se obtendrán balances ni transacciones.");
				return account;
			}

			try {
				CompletableFuture<LinkedList<NordigenAccountBalance>> balancesFuture = CompletableFuture
						.supplyAsync(() -> getAccountBalances(ctx, token, account.getMetadata().getId()));

				CompletableFuture<LinkedList<NordigenBankStatement>> transactionsFuture = CompletableFuture
						.supplyAsync(() -> getNotInsertedTransactions(ctx, token, account));

				CompletableFuture.allOf(balancesFuture, transactionsFuture).join();

				LinkedList<NordigenAccountBalance> balances = balancesFuture.get();
				LinkedList<NordigenBankStatement> notInserted = transactionsFuture.get();

				account.setBalances(balances);
				account.setNotInsertedMovements(notInserted);

				if (notInserted != null && !notInserted.isEmpty()) {
					insertStatements(occam, account);
				}
				NordigenRateLimiter.registerCall(ctx, ctx.getDomainId(), account.getRbank().getId(), "update");
				NordigenDAO.updateRegistryBank(ctx, account);
			} catch (Exception e) {
				NordigenRateLimiter.handleRateLimitException(ctx, ctx.getDomainId(), account.getRbank().getId(),
						"fail_update", e);
				e.printStackTrace();
			}
		} catch (Exception e) {
			throw new AonCoreException(e);
		}
		return account;

	}

	
	public static List<NordigenBankAccount> setAllBankAccountValues(Occam occam, NordigenAccessToken token) {
	    try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {

	        List<NordigenBankAccount> accounts = NordigenDAO.getAllAccounts(ctx);

	        for (NordigenBankAccount account : accounts) {
	            RegistryBank rbank = account.getRbank();

	            if (rbank == null || AonStringUtils.isBlank(rbank.getRequisition())) {
	                continue; 
	            }

	            account.setLinked(true);
	            account.setLastMovementDate(BankStatementDAO.getLastMovementDate(ctx, rbank.getId()));

	            account.setRequisition(getRequisition(token, rbank.getRequisition()));
	            account.setMetadata(getNordigenAccountMetadata(token, account.getRequisition(), rbank));

	            try {
	                if (account.getRequisition() != null && account.getRequisition().getInstitutionId() != null) {
	                    account.setInstitution(getInstitution(token, account.getRequisition().getInstitutionId()));
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	                account.addLog("Error obteniendo institución: " + e.getMessage());
	            }

	            if (account.getMetadata() != null &&
	                AonStringUtils.isNotBlank(account.getMetadata().getId()) &&
	                NordigenRequisitionStatus.LINKED.equals(account.getRequisition().getStatus())) {

//	                try {
//	                    // Ejecutar ambas tareas en paralelo
//	                    CompletableFuture<LinkedList<NordigenAccountBalance>> balancesFuture =
//	                        CompletableFuture.supplyAsync(() -> getAccountBalances(ctx,token, account.getMetadata().getId(), account.getRbank().getId()));
//
//	                    CompletableFuture<LinkedList<NordigenBankStatement>> transactionsFuture =
//	                        CompletableFuture.supplyAsync(() -> getNotInsertedTransactions(ctx, token, account));
//
//	                    CompletableFuture.allOf(balancesFuture, transactionsFuture).join();
//
//	                    LinkedList<NordigenAccountBalance> balances = balancesFuture.get();
//	                    LinkedList<NordigenBankStatement> notInserted = transactionsFuture.get();
//
//	                    account.setBalances(balances);
//	                    account.setNotInsertedMovements(notInserted);
//
//	                    if (notInserted != null && !notInserted.isEmpty()) {
//	                        insertStatements(occam, account);
//	                    }
//
//	                    NordigenDAO.updateRegistryBank(ctx, account);
//
//	                } catch (Exception e) {
//	                    e.printStackTrace();
//	                    account.addLog("Error actualizando balances desde Nordigen: " + e.getMessage());
//	                }
	            }
	        }

	        return accounts;

	    } catch (Exception e) {
	        throw new AonCoreException(e);
	    }
	}

	
	private static LinkedList<NordigenBankStatement> getNotInsertedTransactions(AONContext ctx, NordigenAccessToken token, NordigenBankAccount account) {
		NordigenAccountMetadata metadata = account.getMetadata();
		String accId = metadata != null ? AonStringUtils.trimToNull(metadata.getId()) : null;
		Date dateFrom = guessDateForm( account );
		LinkedList<NordigenBankStatement> stList = new LinkedList<>();

			StringBuilder exceptionMessage = new StringBuilder();
			NordigenAccountTransactions transactions = AonNordigen.getTransactions(ctx, token, accId, dateFrom);
			stList.addAll( getPendingAccountTransactions(account, transactions));
			stList.addAll( getBookedAccountTransactions(account, transactions, dateFrom) );
			if (!exceptionMessage.isEmpty()) {
				throw new NordigenException(exceptionMessage.toString());
			}
			
			NordigenAccountBalance consBalance = filterConsolidado(account.getBalances());
			if (consBalance != null && consBalance.getBalanceAmount() != null) {
				double amount = consBalance.getBalanceAmount().getAmount();
				for (NordigenBankStatement statement : stList) {
					statement.setCurrentBalance(amount);
					int factor = (statement.isPayment() ? (-1) : 1);
					amount = AonMathUtils.round( amount - (statement.getAmount() * factor));
				}
			}
			return stList;
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
	
	public static List<NordigenBankStatement> getStoredBankStatements(Occam occam, RegistryBank rbank, Date dateFrom) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(occam)) {
			return NordigenDAO.getBankStatements(ctx, rbank, dateFrom, new Date());
		}

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
		try (CloseableAONContext ctx =  AONContext.getAONContext(occam)) {
			return NordigenDAO.insertStatements(ctx, account);
		}
	}

	public static List<NordigenBankStatement> getMovements(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(occam)) {
			List<NordigenBankStatement> list = new LinkedList<>();
			List<NordigenBankStatement> orderedList = new LinkedList<>();
			if (nordigenBankAccount != null && nordigenBankAccount.getMetadata() != null) {
//				System.out.println("getMovements");
//				String id = nordigenBankAccount.getMetadata().getId();
//				if (online) {
//					System.out.println(online);
//					NordigenAccountTransactions transactions = AonNordigen.getTransactions(token, id, endDate);
//					list.addAll( getPendingAccountTransactions(nordigenBankAccount, transactions));
//					list.addAll( getBookedAccountTransactions(nordigenBankAccount, transactions, endDate) );
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
//				} else {
//					list.addAll(nordigenBankAccount.getNotInsertedMovements());
					list.addAll(AonNordigen.getStoredBankStatements(occam, nordigenBankAccount.getRbank(), endDate));
					orderedList = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
					List<NordigenAccountBalance> balances = nordigenBankAccount.getBalances();
					NordigenAccountBalance consolidado = AonNordigen.filterConsolidado(balances);
					NordigenAccountBalance real = AonNordigen.filterReal(balances);
					NordigenAccountBalance balance = consolidado != null ? consolidado : real;
					Double remaining = (balance != null && balance.getBalanceAmount() != null) ? balance.getBalanceAmount().getAmount() : 0;
					for(NordigenBankStatement bs : orderedList) {
						bs.setCurrentBalance(remaining);
						remaining += (bs.getAmount() * (bs.isPayment() ? 1 : -1));
					}
//				}
				
			}
		
			return orderedList;
		} catch (NordigenException e) {
			throw e;
		} catch (Exception e) {
			throw mapException(e);
		}
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
		
		List<NordigenInstitution> instList = AonNordigen.getInstitutionsByCountry(token, null)
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
	
	
	private static NordigenAccountMetadata getNordigenAccountMetadata(NordigenAccessToken token, NordigenRequisition requisition, RegistryBank rbank) {
		if (requisition != null && rbank != null && rbank.getBankAccount() != null) {
			String iban = rbank.getBankAccount().getIban();
			if (AonStringUtils.isBlank(iban)) {
				return null;
			}
			List<String> accs = requisition.getAccounts();
			if (accs != null) {
				for (String accId : accs) {
					try {
						return getAccountMetadata(token, accId);
//						NordigenAccountDetail detail = NordigenAPI.getDetail(token.getAccess(), accId);
//						if (metadata != null && AonStringUtils.equalsIgnoreCase(iban, metadata.getIban()) && detail.getCurrency().equals("EUR")) {
//						}
					} catch (Exception e) {
						e.printStackTrace();
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
	public static Integer remainingDaysAgreement(NordigenAccessToken token, NordigenRequisition requisition) {
		NordigenAgreement agreement = NordigenAPI.getEndUserAgreement(token.getAccess(), requisition.getAgreement());
	    
	    // Fecha de aceptación del acuerdo
		Date acceptedDateRaw = agreement.getAccepted();
	    LocalDate acceptedDate = acceptedDateRaw.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    
	    // Fecha actual
	    LocalDate currentDate = LocalDate.now();
	    
	    // Días transcurridos desde la aceptación
	    long daysElapsed = ChronoUnit.DAYS.between(acceptedDate, currentDate);
	    
	    // Días válidos para acceso
	    int validDays = agreement.getAccessValidForDays();

	    // Días restantes
	    return (int) (validDays - daysElapsed);
	}
	
	
		
	private static NordigenAccountBalance filterConsolidado(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance consolidado = null;
		consolidado = balances.stream()
				.filter(bal -> NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);
		
		if (consolidado == null && balances.isEmpty()) {
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

	static void deleteAgreement(NordigenAccessToken token, NordigenAgreement agreement)  {
		NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreement != null ? agreement.getId() : null);
	}
	
	static NordigenRequisition createRequisition(NordigenAccessToken token, NordigenAgreement agreement, String redirect)  {
		return  createRequisition(token,
			agreement != null ? agreement.getId() : null,
			agreement != null ? agreement.getInstitutionId() : null,
			redirect);
	}
}
