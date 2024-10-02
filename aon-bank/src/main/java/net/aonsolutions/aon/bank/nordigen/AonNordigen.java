package net.aonsolutions.aon.bank.nordigen;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.LogData;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransactions;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.impl.jooq.dao.BankStatementDAO;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.bank.nordigen.utils.NordigenAgreementUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenBalancesUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenInstitutionUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenMetadaUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenRequisitionUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenTokenUtils;
import net.aonsolutions.aon.bank.nordigen.utils.NordigenTransactionsUtils;

public class AonNordigen  {

	private static final String LINK_REGEX = "^https\\:\\/\\/(?<link>.*?)\\/ms\\/.*$";
	private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);
	
	private AonNordigen() {
	}
	
	//Esta en NordigenServiceImpl
	public static NordigenConfiguration getConfiguration(Occam occam) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(occam)) {
			return new NordigenConfiguration()
				.setConfiguration(AON.getConfiguration(ctx))
				.setToken(NordigenTokenUtils.getNewAccessToken())
				.setAccounts(NordigenDAO.getAllAccounts(ctx));
		} catch ( AonCoreException ex) {
			AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), ex));
			throw new NordigenException(ex.getMessage());
		}
	}
	
	//Esta en NordigenServiceImpl
	public static List<NordigenInstitution> getInstitutions(NordigenAccessToken token, Country country, Boolean paymentsEnabled) {
		return NordigenAPI.getInstitutions(token.getAccess(), country, paymentsEnabled);
	}
	
	//Esta en NordigenServiceImpl
	public static List<NordigenInstitution> getInstitutionsByBic(NordigenAccessToken token, String bic) {
		if (AonStringUtils.isNotBlank(bic)) {
			List<NordigenInstitution> matchedInstitutions = 
				AonCollectionUtils.stream(AonNordigen.getInstitutions(token, null, null))
					.filter(inst -> AonStringUtils.containsIgnoreCase(inst.getBic(), bic))
					.collect(Collectors.toList());
			if (AonCollectionUtils.isNotEmpty(matchedInstitutions)) {
				return matchedInstitutions;
			}
		}
		throw new NordigenException("No available institutions");
	}
	

	//Esta en NordigenServiceImpl
	public static NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) {
		return NordigenAPI.getRequisition(token.getAccess(), requisitionId);
	}
	
	//Esta en NordigenServiceImpl
	public static List<NordigenRequisition> getDomainRequisitions(NordigenAccessToken token, String domainName) {
		LinkedList<NordigenRequisition> list = new LinkedList<>();
		AonCollectionUtils.stream( NordigenRequisitionUtils.getAllRequisitions(token) )
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

	//Esta en NordigenServiceImpl
	public static NordigenBankAccount setBankAccountValues(Occam occam, NordigenAccessToken token, NordigenBankAccount account) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(occam)) {
			RegistryBank rbank = account.getRbank();
			if (rbank.getRequisition() != null) {
				String reqId = rbank.getRequisition();
				if (AonStringUtils.isNotBlank(reqId)) {
					account.setLinked(true);
					account.setRequisition(getRequisition(token, reqId));
					account.setMetadata(NordigenMetadaUtils.getNordigenAccountMetadata(token, account.getRequisition(), rbank));
					String accountId = account.getMetadata() != null ? account.getMetadata().getId() : null;
					account.setLastMovementDate(BankStatementDAO.getLastMovementDate(ctx, account.getRbank().getId()));
					
					Thread thread3 = new Thread(() -> {
						try {
							if (account.getRequisition() != null && account.getRequisition().getInstitutionId() != null) {
								account.setInstitution(NordigenInstitutionUtils.getInstitution(token, account.getRequisition().getInstitutionId()));
							}
						} catch (Exception e) {
							AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
							e.printStackTrace();
							account.addLog(e.getMessage());
						}
					});
					thread3.start();
					
					if (account.getMetadata() != null
						&& AonStringUtils.isNotBlank(accountId)
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
								account.setBalances(NordigenBalancesUtils.getAccountBalances(token, accountId));
								account.setNotInsertedMovements(NordigenTransactionsUtils.getNotInsertedTransactions(token, account));
							} catch (Exception e) {
								AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
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
			Thread.currentThread().interrupt();
			AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
			throw new AonCoreException(e);
		}
	}
	
	//Esta en NordigenServiceImpl
	public static boolean deleteRequisitionById(NordigenAccessToken token, Occam occam, String requisitionId) {
		NordigenRequisition requisition = AonNordigen.getRequisition(token, requisitionId);
		List<RegistryBank> rbanks = NordigenRequisitionUtils.getRbanksByRequisition(occam, requisitionId);
		boolean requisitionDeleted = true;
		if (requisition != null) {
			try {				
				NordigenRequisitionUtils.deleteRequisition(token, requisition);
			} catch (Exception e) {
				AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
				requisitionDeleted = false;
			}
		}
		if (rbanks != null) {
			for (RegistryBank rbank : rbanks) {
				rbank.setRequisition(null);
				NordigenUtils.updateRbank(occam, rbank);
			}
		}
		return requisitionDeleted;
	}

	//Esta en NordigenServiceImpl
	public static void deleteRequisitionByRbank(NordigenAccessToken token, Occam occam, Integer rbankId) {
		if (rbankId == null) return;
		RegistryBank rbank = AON.getRegistryBank(occam, f -> f.getIdProperty().eq(rbankId));
		if (rbank == null) return;
		
		String requisition = rbank.getRequisition();
		rbank.setRequisition(null);
		AON.saveRegistryBank(occam, rbank);
		if (requisition != null) {
			NordigenRequisitionUtils.deleteRequisition(token, requisition);				
		}
	}
	
	//Esta en NordigenServiceImpl
	public static int insertStatements(Occam occam, NordigenBankAccount account) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(occam)) {
			return NordigenDAO.insertStatements(ctx, account);
		}
	}

	//Esta en NordigenServiceImpl
	public static List<NordigenBankStatement> getMovements(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online) {
		try (CloseableAONContext ctx =  AONContext.getAONContext(occam)) {
			List<NordigenBankStatement> list = new LinkedList<>();
			if (nordigenBankAccount != null && nordigenBankAccount.getMetadata() != null) {
				String id = nordigenBankAccount.getMetadata().getId();
				if (online) {
					NordigenAccountTransactions transactions = NordigenTransactionsUtils.getTransactions(token, id, endDate);
					list.addAll(NordigenTransactionsUtils.getPendingAccountTransactions(nordigenBankAccount, transactions));
					list.addAll(NordigenTransactionsUtils.getBookedAccountTransactions(nordigenBankAccount, transactions, endDate) );
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
					list.addAll(NordigenTransactionsUtils.getStoredBankStatements(occam, nordigenBankAccount.getRbank(), endDate));
				}
			}
			List<NordigenBankStatement> orderedList = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
			List<NordigenAccountBalance> balances = nordigenBankAccount.getBalances();
			NordigenAccountBalance consolidado = NordigenBalancesUtils.filterConsolidado(balances);
			NordigenAccountBalance real = NordigenBalancesUtils.filterReal(balances);
			NordigenAccountBalance balance = consolidado != null ? consolidado : real;
			Double remaining = (balance != null && balance.getBalanceAmount() != null) ? balance.getBalanceAmount().getAmount() : 0;
			for(NordigenBankStatement bs : orderedList) {
				bs.setCurrentBalance(remaining);
				remaining += (bs.getAmount() * (bs.isPayment() ? 1 : -1));
			}
			return orderedList;
		} catch (NordigenException e) {
			AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
			throw e;
		} catch (Exception e) {
			AON_SOLUTIONS.insertLogData(occam, new LogData(occam.getDomain(), e));
			throw mapException(e);
		}
	}

	//Esta en NordigenServiceImpl
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
		
		NordigenAgreement agreement = NordigenAgreementUtils.createAgreement(token, inst != null ? inst.getId() : institutionId);
		NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement, "https://" + occam .getDomainName() + "/ms/api/task-evaluation/rbank?rbank=" + (rbank != null ? ""+rbank.getId() : ""));
		
		NordigenRequisitionUtils.updateRequisitionId(occam, requisition, rbank.getId());
		
		String msg = "El usuario" + occam.getUser() 
		+ "del dominio " + occam.getDomainName() 
		+" vinculo el banco con id : " 
		+ nordigenBankAccount.getRbank().getId() 
		+ " y su requisition es: "+ requisition.getId() 
		;
		
		LogData data = new LogData(occam.getDomain(), msg);
		
		AON_SOLUTIONS.insertLogData(occam.getDomainName(), occam.getDomain(), occam.getUser(), data);

		return requisition;
	}
	
	// -------------------------- Métodos con tests y excepcion.

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

	static void deleteAgreement(NordigenAccessToken token, NordigenAgreement agreement)  {
		NordigenAPI.deleteEndUserAgreement(token.getAccess(), agreement != null ? agreement.getId() : null);
	}
	
	static NordigenRequisition createRequisition(NordigenAccessToken token, NordigenAgreement agreement, String redirect)  {
		return  NordigenRequisitionUtils.createRequisition(token,
			agreement != null ? agreement.getId() : null,
			agreement != null ? agreement.getInstitutionId() : null,
			redirect);
	}
}
