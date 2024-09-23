package com.esferalia.aon.gwt.fiscal.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.accounting.AmortizationService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Amortization Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Amortization" })
public class AmortizationServiceImpl extends AonStatelessRemoteServiceServlet implements AmortizationService {

	private static final long serialVersionUID = 8791955004212947200L;

	@Override
	public List<Account> getFixedAssetAccounts(String domainName, int domain, String user) throws AonCoreException {
		List<Account> fixedAssetAccounts = ACCOUNTING.getAccounts(domainName, domain, user, 
				f -> (f.getCodeProperty().like("20%").or(f.getCodeProperty().like("21%")).or(f.getCodeProperty().like("22%")))
				.and(f.getEntryEnabledProperty().eq((byte)0))
				.and(f.getLevelProperty().eq((byte)4))
				.and(f.getActiveProperty().eq((byte)1)))
				.collect(Collectors.toList());
		
		fixedAssetAccounts.sort((o1, o2) -> o1.getDomain().compareTo(o2.getDomain()));
		Collections.reverse(fixedAssetAccounts);
		
		return parseRepeatAccounts(domain, fixedAssetAccounts);
	}

	@Override
	public List<Account> getAccumulatedAccounts(String domainName, int domain, String user) throws AonCoreException {
		List<Account> accumulatedAccounts = ACCOUNTING.getAccounts(domainName, domain, user,
				f -> (f.getCodeProperty().like("280%").or(f.getCodeProperty().like("281%")).or(f.getCodeProperty().like("282%")))
				.and(f.getEntryEnabledProperty().eq((byte)0))
				.and(f.getLevelProperty().eq((byte)4))
				.and(f.getActiveProperty().eq((byte)1)))
				.collect(Collectors.toList());
		
		accumulatedAccounts.sort((o1, o2) -> o1.getDomain().compareTo(o2.getDomain()));
		Collections.reverse(accumulatedAccounts);
		
		return parseRepeatAccounts(domain, accumulatedAccounts);
	}

	@Override
	public List<Account> getAllocationAccounts(String domainName, int domain, String user) throws AonCoreException {
		List<Account> allocationAccounts = ACCOUNTING.getAccounts(domainName, domain, user,
				f -> (f.getCodeProperty().like("680%").or(f.getCodeProperty().like("681%")).or(f.getCodeProperty().like("682%")))
				.and(f.getEntryEnabledProperty().eq((byte)0))
				.and(f.getLevelProperty().eq((byte)4))
				.and(f.getActiveProperty().eq((byte)1)))
				.collect(Collectors.toList());
		
		allocationAccounts.sort((o1, o2) -> o1.getDomain().compareTo(o2.getDomain()));
		Collections.reverse(allocationAccounts);
		
		return parseRepeatAccounts(domain, allocationAccounts);
	}
	
	private List<Account> parseRepeatAccounts(int domain, List<Account> list) {
		List<Account> result = new ArrayList<>();
		
		for(Account account : list) {
			if(account.getDomain().equals(domain)) result.add(account);
			else if(!containsCode(result, account.getCode())) result.add(account);
		}
		
		result.sort((o1, o2) -> o1.getCode().compareTo(o2.getCode()));
		
		return result;
	}

	private boolean containsCode(List<Account> list, String code) {
		Optional<Account> find = list.stream().filter(account -> AonStringUtils.equalsIgnoreCase(account.getCode(), code)).findAny();
		return find.isPresent();
	}

	@Override
	public List<AmortizationType> getAmortizationTypeList(String domainName, int domain, String user, AmortizationTypeParams params) throws AonCoreException {
		return ACCOUNTING.getAmortizationTypeList(domainName, domain, user, params);
	}

	@Override
	public void deleteAmortizationTypes(String domainName, int domain, String user, List<Integer> deleteIds) throws AonCoreException {
		ACCOUNTING.deleteAmortizationTypes(domainName, domain, user, deleteIds);
	};
	
	@Override
	public void saveAmortizationType(String domainName, int domain, String user, AmortizationType amortizationType) throws AonCoreException {
		ACCOUNTING.saveAmortizationType(domainName, domain, user, amortizationType);
	};

}
