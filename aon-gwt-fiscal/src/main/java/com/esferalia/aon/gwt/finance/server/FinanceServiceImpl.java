package com.esferalia.aon.gwt.finance.server;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Finance Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Finance" })
public class FinanceServiceImpl extends AonStatelessRemoteServiceServlet implements FinanceService {

	private static final long serialVersionUID = -6729210903627762279L;

	@Override
	public Integer getInvoiceNextNumber(String domainName, Integer domainId, String user, Byte[] types, String series) throws AonCoreException {
		return AON.getInvoiceNextNumber(domainName, domainId,user, types, series);
	}
	
	@Override
	public Integer getInvoiceNextNumber(Occam occam, Byte[] types, String series) throws AonCoreException {
		return AON.getInvoiceNextNumber(occam, types, series);
	}

	@Override
	public LinkedList<FinanceTracking> getFinanceTracking(String domainName, int domainId, String user, Integer finance)
			throws AonCoreException {
		return AON.getFinanceTracking(domainName, domainId,user, finance);
	}

	@Override
	public LinkedList<Finance> getFinances(String domainName, int domain, String user, FinanceParams params, int offset,
			int limit) throws AonCoreException {
		return AON.getFinances(domainName, domain,user, params, offset, limit );
	}
	@Override
	public LinkedList<Finance> getFinancesForInvoice(String domainName, int domainId, String user, Invoice invoice)
			throws AonCoreException {
		return AON.getFinancesForInvoice(domainName, domainId,user, invoice);
	}

	@Override
	public Finance settleFinance(String domainName, int domainId, String user, Integer finance) throws AonCoreException {
		return AON.settleFinance(domainName, domainId,user, finance);
	}
	
	@Override
	public Finance unSettleFinance(String domainName, int domainId, String user, Integer finance) throws AonCoreException {
		return AON.unSettleFinance(domainName, domainId,user, finance);
	}

	@Override
	public Finance undoFinance(String domainName, int domainId, String user, Integer finance) throws AonCoreException {
		return AON.undoFinance(domainName, domainId,user, finance);
	}

	@Override
	public FinanceTracking payFinance(String domainName, int domainId, String user, FinanceTracking tracking) throws AonCoreException {
		return AON.payFinance(domainName, domainId,user, tracking);
	}

	@Override
	public FinanceTracking returnFinance(String domainName, int domainId, String user, FinanceTracking tracking) throws AonCoreException {
		return AON.returnFinance(domainName, domainId,user, tracking);
	}

	@Override
	public void deleteFinance(String domainName, int domainId, String user, Integer financeId) throws AonCoreException {
		AON.deleteFinance(domainName, domainId, user, financeId);
	}

	@Override
	public LinkedList<RegistryBank> getCompanyBanks(String domainName, int domainId, String user) throws AonCoreException {
		try {
			return AON.getCompanyRegistryBanks(domainName, domainId,user);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@Override
	public LinkedList<RegistryBank> getRegistryBanks(String domainName, int domainId, String user, Integer registry) throws AonCoreException {
		try {
			return AON.getRegistryBanks(domainName, domainId,user, registry);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
	
	public LinkedList<Finance> getAccountFinances(String domainName, int domain, String user, FinanceParams params, int offset, int limit) {
		return ACCOUNTING.getAccountFinances(domainName, domain, user, params, offset, limit);		
	}

	// --------------------------------------------------------------- VENCIMIENTO NOMINAS
	
	@Override
	public void createSettleSalaries(String domainName, int domainId, String user, Date date) throws AonCoreException {
		AON.createSettleSalaries(domainName, domainId, user, date);
	}

	@Override
	public Integer createSepaFile(String domainName, int domainId, String user, Integer fbatchId) throws AonCoreException {
		return AON.createSepaFile(domainName, domainId, user, fbatchId);
	}

	@Override
	public LinkedList<FBatch> getFBatches(String domainName, int domain, String user, FBatchParams params, int offset, int limit) throws AonCoreException {
		return AON.getFBatches(domainName, domain, user, params, offset, limit);
	}
	
	@Override
	public FBatch getFBatch(String domainName, int domain, String user, Integer fbatchId) throws AonCoreException {
		return AON.getFBatch(domainName, domain, user, fbatchId);
	}

	@Override
	public void deleteFBatches(String domainName, int domain, String user, LinkedList<Integer> fBatchIds) throws AonCoreException {
		AON.deleteFBatches(domainName, domain, user, fBatchIds);
	}

	@Override
	public FBatch createUpdateFBatch(String domainName, int domain, String user, FBatch fBatch) throws AonCoreException {
		return AON.createUpdateFBatch(domainName, domain, user, fBatch);
	}

	@Override
	public void deleteSepaFile(String domainName, int domain, String user, Integer rattachId) throws AonCoreException {
		AON.deleteAttach(domainName, domain, user, f -> f.getIdProperty().eq(rattachId), AttachType.REGISTRY);
	}

	@Override
	public FBatch recordFBatch(Occam occam, Integer fbatchId, Date paymentDate) throws AonCoreException {
		return AON.recordFBatch(occam, fbatchId, paymentDate);	
	}

	@Override
	public FBatch unrecordFBatch(Occam occam, Integer fbatchId) throws AonCoreException {
		return AON.unrecordFBatch(occam, fbatchId);	
	}
	
	@Override
	public AccountEntry getFBatchAccountEntry(Occam occam, Integer id) throws AonCoreException {
		return AON.getFBatchAccountEntry(occam, id);
	}
}
