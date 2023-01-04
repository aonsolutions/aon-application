package com.esferalia.aon.gwt.finance.server;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.finance.nordigen.NordigenService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransaction;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

import nordigen.AonNordigen;

@WebServlet(name = "Nordigen Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/nordigen" })
public class NordigenServiceImpl extends AonStatelessRemoteServiceServlet implements NordigenService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public NordigenConfiguration getConfiguration(String domainName, int domain, String user) throws Exception {
		NordigenAccessToken token = AonNordigen.getNewAccessToken();
		Domain dmn = new Domain().setId(domain).setName(domainName);
//		clearIncompleteRequisitions(token, domainName, domain, user);
		List<NordigenBankAccount> accounts = AonNordigen.getAllAccounts(token, dmn, user);
		
//		AON.getRegistryAddInfoStream(domainName, domain, user, f -> f.getAttributeProperty().like("NORDIGEN"))
//		.forEach(r -> {
//			
//		});
		
		return new NordigenConfiguration()
			.setConfiguration(AON.getConfiguration(domainName, domain,user))
			.setToken(token)
			.setAccounts(accounts)
		;
		
	}
	
	@Override
	public Integer insertTransactions(String currentDomainName, int currentDomain, String user,
			NordigenBankAccount nordigenBankAccount) throws AonCoreException {
		return AonNordigen.insertStatements(new Domain().setName(currentDomainName).setId(currentDomain), user, nordigenBankAccount);
	}
	
	@Override
	public NordigenRequisition addAccount(String currentDomainName, int currentDomain, String user, NordigenAccessToken token, NordigenBankAccount nordigenBankAccount) throws Exception {
		RegistryBank rbank = nordigenBankAccount.getRbank();
		Pattern bicPattern = Pattern.compile("^(?<bic>.*?)X*$", Pattern.CASE_INSENSITIVE);
		Matcher bicMatcher = bicPattern.matcher(AonStringUtils.trimToEmpty(rbank.getBic()));
		StringBuilder bicBuilder = new StringBuilder();
		if (bicMatcher.matches()) {
			bicBuilder.append(AonStringUtils.trimToEmpty(bicMatcher.group("bic")));
		}
		final String bic = bicBuilder.toString();
		
		List<NordigenInstitution> instList = AonNordigen.getInstitutions(token, null, null).stream().filter(inst -> AonStringUtils.equalsIgnoreCase(inst.getBic(), bic)).toList();
		
		Optional<NordigenInstitution> optInstitution = Optional.ofNullable(instList.size() == 1 ? instList.get(0) : null);
		StringBuilder instIdBuilder = new StringBuilder();
		if (optInstitution.isPresent()) {
			instIdBuilder.append(AonStringUtils.trimToEmpty(optInstitution.get().getId()));
		}
		final String institutionId = instIdBuilder.toString();
		
		//EN CASO DE QUE HAYA PROBLEMAS CON EL BIC:
		NordigenInstitution inst = nordigenBankAccount.getInstitution();
		
		if (inst == null && instList.size() > 1) {
			throw new Exception("Too much institutions");
		}
		
		NordigenAgreement agreement = AonNordigen.createAgreement(token, inst != null ? inst.getId() : institutionId);
		NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement, "https://" + currentDomainName + "/ms/api/task-evaluation/rbank?rbank=" + (rbank != null ? ""+rbank.getId() : ""));
		AonNordigen.updateRequisitionId(new Domain().setName(currentDomainName).setId(currentDomain), institutionId, requisition, rbank.getId());
		
		return requisition;
	}
	
	private static <T extends Exception> void throwException (T exception) throws IllegalArgumentException{
		try {
			JSONObject errJson = new JSONObject(exception.getMessage());
			String err = errJson.getString("result");
			throw new IllegalArgumentException(err);						
		} catch (Exception subE) {
			throw new IllegalArgumentException(exception.getMessage());
		}
	}


	@Override
	public List<NordigenBankStatement> getMovements(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online) throws Exception {
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
					list.addAll(AonNordigen.getStoredBankStatements(new Domain().setName(domainName).setId(domain), user, nordigenBankAccount.getRbank(), endDate));
				}
				
			}
			List<NordigenBankStatement> orderedList = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
			
			List<NordigenAccountBalance> balances = nordigenBankAccount.getBalances();
			
			NordigenAccountBalance consolidado = AonNordigen.filterConsolidado(balances);
			NordigenAccountBalance real = AonNordigen.filterReal(balances);
			
			
			NordigenAccountBalance balance = consolidado != null ? consolidado : real;
			
			
			Double remaining = balance.getBalanceAmount() != null ? balance.getBalanceAmount().getAmount() : 0;
			
			for(NordigenBankStatement bs : orderedList) {
//				if (!bs.isPending()) {
					bs.setCurrentBalance(remaining);
					remaining += (bs.getAmount() * (bs.isPayment() ? 1 : -1));
//				}				
			}
			
			return orderedList;
		} catch (Exception e) {
			throwException(e);
			return null;
		}
	}


	@Override
	public List<NordigenInstitution> getNordigenInstitutions(NordigenAccessToken token, Country country) throws Exception {
		return AonNordigen.getInstitutions(token, country, null);
	}


	@Override
	public List<NordigenInstitution> getNordigenInstitutionsByBic(NordigenAccessToken token, String bic)
			throws Exception {
		if (AonStringUtils.isNotBlank(bic)) {
			List<NordigenInstitution> institutions = AonNordigen.getInstitutions(token, null, null);
			List<NordigenInstitution> matchedInstitutions = institutions.stream().filter(inst -> AonStringUtils.containsIgnoreCase(inst.getBic(), bic)).collect(Collectors.toList());
			
			if (matchedInstitutions != null && !matchedInstitutions.isEmpty()) {
				return matchedInstitutions;
			}
			
		}
		throw new Exception("No available institutions");
	}

	@Override
	public Boolean cancelRequisition(NordigenAccessToken token, String currentDomainName, int currentDomain,
			String user, Integer rbankId) throws Exception {
		AonNordigen.deleteRequisitionByRbank(token, new Domain().setName(currentDomainName).setId(currentDomain), user, rbankId);
		return true;
	}


	@Override
	public NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) throws Exception {
		return AonNordigen.getRequisition(token, requisitionId);
	}


	@Override
	public NordigenBankAccount setNordigenAccountValues(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount account)
			throws Exception {
		Domain d = new Domain().setName(domainName).setId(domain);
		return AonNordigen.setNordigenBankAccountValues(d, user, token, account);
	}


	@Override
	public List<NordigenBankStatement> getNotInsertedMovements(NordigenAccessToken token, String domainName, int domain,
			String user, NordigenBankAccount nordigenBankAccount) throws Exception {
		return AonNordigen.getNotInsertedTransactions(token, new Domain().setName(domainName).setId(domain), user, nordigenBankAccount);
	}

}
