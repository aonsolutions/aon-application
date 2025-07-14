package com.esferalia.aon.gwt.finance.server;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.finance.nordigen.NordigenService;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.bank.nordigen.AonNordigen;

@WebServlet(name = "Nordigen Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/nordigen" })
public class NordigenServiceImpl extends AonStatelessRemoteServiceServlet implements NordigenService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public NordigenConfiguration getConfiguration(Occam occam) throws NordigenException {
		return AonNordigen.getConfiguration(occam);
	}
	
	@Override
	public NordigenBankAccount setAccountValues(NordigenAccessToken token, Occam occam, NordigenBankAccount account, boolean refresh) throws NordigenException {
		return AonNordigen.setBankAccountValues(occam, token, account, refresh);
	}

	@Override
	public List<NordigenRequisition> getDomainRequisitions(NordigenAccessToken token, String domainName) throws NordigenException {
		return AonNordigen.getDomainRequisitions(token, domainName);
	}

	@Override
	public Boolean deleteRequisitionById(NordigenAccessToken token, Occam occam, String requisitionId) throws NordigenException {
		return AonNordigen.deleteRequisitionById(token, occam, requisitionId);
	}
	
	@Override
	public void cancelRequisition(NordigenAccessToken token, Occam occam, Integer rbankId) throws NordigenException {
		AonNordigen.deleteRequisitionByRbank(token, occam, rbankId);
	}

	@Override
	public NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) throws NordigenException {
		return AonNordigen.getRequisition(token, requisitionId);
	}

	@Override
	public List<NordigenBankStatement> getMovements(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online) throws NordigenException {
		return AonNordigen.getMovements(token, occam, nordigenBankAccount, endDate, online);
	}

	@Override
	public NordigenRequisition addAccount(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount) throws NordigenException {
		return AonNordigen.addAccount(token, occam, nordigenBankAccount);
	}

	@Override
	public List<NordigenInstitution> getInstitutionsByBic(NordigenAccessToken token, String bic) throws NordigenException {
		return AonNordigen.getInstitutionsByBic(token, bic);
	}

	@Override
	public Integer insertTransactions(Occam occam, NordigenBankAccount nordigenBankAccount) throws NordigenException {
		return AonNordigen.insertStatements(occam, nordigenBankAccount);
	}

	@Override
	public List<NordigenInstitution> getInstitutions(NordigenAccessToken token, Country country) throws NordigenException {
		return AonNordigen.getInstitutionsByCountry(token, country);
	}
	@Override
	public List<NordigenBankAccount> setAllBankAccountValues(Occam occam, NordigenAccessToken token) throws NordigenException {
		return AonNordigen.setAllBankAccountValues(occam, token);
	}
	@Override
	public Integer getRemainingDays(Occam occam,NordigenBankAccount account) {
		return AonNordigen.remainingDaysAgreement(occam, account);
	}
	
	@Override
	public int getRemainingCallsToday(Occam occam, NordigenBankAccount account) {
		return AonNordigen.getRemainingCallsToday(occam, account);
	}
	
	@Override
	public String getLatestRetryAfter(Occam occam, NordigenBankAccount account) {
		return AonNordigen.getLatestRetryAfter(occam, account);
	}
	
	@Override
	public List<String> getCallStatuses(Occam occam, NordigenBankAccount account){
		return AonNordigen.getCallStatuses(occam, account);
	}
    
    /*
      Se ha importados:
        import com.esferalia.aon.occam.api.model.registry.RegistryBank;
      Una vez usado ELIMINAR
    */
    @Override
    public List<RegistryBank> getByRequisitionIsNotNull(Occam occam){
      return AonNordigen.getByRequisitionIsNotNull(occam);
    }
    
    @Override
    public String getAccountIdByIban(NordigenAccessToken token, String requisitionId, RegistryBank rbank) {
    	  return AonNordigen.getAccountIdByIban(token, requisitionId, rbank);
    }
    
    @Override
    public List<BankStatement> checkIncorrectMovements(Occam occam, NordigenAccessToken token, List<String> accountIds, RegistryBank rbank){
    	return AonNordigen.checkIncorrectMovements(occam, token, accountIds, rbank);
    }

}
