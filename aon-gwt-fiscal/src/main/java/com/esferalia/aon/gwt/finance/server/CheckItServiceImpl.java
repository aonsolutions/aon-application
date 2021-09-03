package com.esferalia.aon.gwt.finance.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.finance.checkit.CheckItService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.aon.api.checkit.CheckItAPI;
import net.aonsolutions.aon.api.checkit.CheckItException;

@WebServlet(name = "Rawdoc Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/CheckIt" })
public class CheckItServiceImpl extends AonStatelessRemoteServiceServlet implements CheckItService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public CheckItConfiguration getConfiguration(String domainName, int domain, String user) throws AonCoreException {
		
		ApplicationParameter appParam = AON.getApplicationParameter(domainName, domain, user, AppParam.CHECK_IT_ENTERPRISE_ID);
		Integer enterpriseId = null;
		if (appParam != null) {
			String value = appParam.getValue();
			try {
				enterpriseId = Integer.parseInt(value);
			} catch (NumberFormatException e) {
			}
		}
		LinkedList<CheckItBankAccount> checkitAccounts = null;
		try {
			checkitAccounts =  CheckItAPI.getAccounts( enterpriseId );
		} catch (CheckItException e) {
			e.printStackTrace();
		}
		
		return new CheckItConfiguration()
			.setConfiguration( AON.getConfiguration(domainName, domain,user) )
			.setEnterpriseId( enterpriseId )
			.setCheItBanks(checkitAccounts)
		;
		
	}

}
