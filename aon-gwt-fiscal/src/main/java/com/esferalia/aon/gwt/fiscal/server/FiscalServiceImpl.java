package com.esferalia.aon.gwt.fiscal.server;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.server.util.AONMVELUtils;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.Activities.Type1Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type2Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type3Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type4Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type7Activities;
import com.esferalia.aon.occam.api.model.type.Activities.TypeActivity;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Fiscal Servlet", urlPatterns = { "/aon_gwt_fiscal/Fiscal" })
public class FiscalServiceImpl extends AonRemoteServiceServlet implements FiscalService {

	private static final long serialVersionUID = -3045020929753519103L;
	
	@Override
	public Double mathExpression(String expression) throws AonCoreException {
		try {
			return AONMVELUtils.mathExpression(expression);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		}
	}
	// ------------------------------------------------------- FISCAL PARAMETERS
	@Override
	public FiscalParameters getFiscalParameters(String domainName,int domain) throws AonCoreException {
		return AON.getFiscalParameters(domainName, domain,this.getUserLogin());
	}

	@Override
	public FiscalModelMatrix getFiscalPanel(String domainName,
			int domain, int year) {
		return FISCAL.getFiscalPanel(domainName, domain,year,this.getUserLogin());
	}
	
	@Override
	public LinkedList<IFiscalModel> getAllModels(String domainName,
			int domain) {
		return FISCAL.getAllModels(domainName, domain,this.getUserLogin());
	}
	@Override
	public LinkedList<IFiscalModel> getAllModels(String domainName,
			int domain, int year) {
		return FISCAL.getAllModels(domainName, domain,year,this.getUserLogin());
	}
	
	// -------------------------------------------------------------- ACTIVITIES
	
	@Override
	public LinkedList<Activity> getActivities(int activityGroup) throws AonCoreException {
		LinkedList<Activity> list = new LinkedList<Activity>();
		TypeActivity[] types = null;
		if (activityGroup == 0) {
			types = Type1Activities.values();
		} if (activityGroup == 1) {
			types = Type2Activities.values();
		} if (activityGroup == 2) {
			types = Type3Activities.values();
		} if (activityGroup == 3) {
			types = Type4Activities.values();
		} if (activityGroup == 6) {
			types = Type7Activities.values();
		}
		if (types == null) {
			throw new AonCoreException("Grupo de actividad no soportado " + activityGroup );
		}
		Activity a;
		for (TypeActivity type : types) {
			a = new Activity();
			a.setEpigraph(type.getEpigraph());
			a.setDescription(type.getLiteral());
			list.add(a);
		}
		return list;
	}
	
	// ---------------------------------------------------------------MODELO 200
	@Override
	public LinkedList<Mod200> getMod200s(String domainName,int domain) throws AonCoreException {
		return FISCAL.getMod200s(domainName, domain,this.getUserLogin());
	}
	
	// --------------------------------------------------------------- NORMALIZED MEMORY
	@Override
	public Memory readMemory(Memory memory) throws AonCoreException {
		return null;
	}
	@Override
	public Memory saveMemory(Memory memory) throws AonCoreException {
		// TODO
		return null;
	}
	
	@Override
	public void deleteMemory(Memory memory) throws AonCoreException {
		// TODO
	}
	
	// --------------------------------------------------------------- ACCOUNT PERIOD
	@Override
	public LinkedList<AccountPeriod> getDomainPeriods(String domainName,
			int domain) throws AonCoreException {
		return ACCOUNTING.getDomainPeriods(domainName, domain, this.getUserLogin());
	}
	// --------------------------------------------------------------- ACCOUNT ENTRIES
	@Override
	public LinkedList<AccountEntry> getAccountEntries(String domainName,
			int domain, final AccountEntryParams params,int offset, int limit) throws AonCoreException {
		return ACCOUNTING.getAccountEntries(domainName, domain, this.getUserLogin(),
				params, offset, limit);
	}

	@Override
	public AccountEntry getAccountEntry(String domainName, int domain, int id)
			throws AonCoreException {
		LinkedList<AccountEntry> list = ACCOUNTING.getAccountEntries(
				domainName, domain, this.getUserLogin(), 
				p -> p.getIdProperty().eq(id)
				, 0, 1)
				;
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.getFirst();
	}

	@Override
	public AccountEntry save(String domainName, int domain, AccountEntry ae)
			throws AonCoreException {
		return ACCOUNTING.save(domainName, domain, this.getUserLogin(), ae);
	}
	@Override
	public LinkedList<SalaryEntry> getSalaryEntries(String domainName,
			int domain, Date from, Date to ) {
		return ACCOUNTING.getSalaryEntries(domainName, domain, this.getUserLogin(),from,to);
	}

	@Override
	public String getSalaryFormatted(String domainName, int domain, Date from, Date to ) {
		return ACCOUNTING.getSalaryFormatted(domainName, domain, this.getUserLogin(),from,to);
	}

	@Override
	public void deleteAccountEntry(String domainName, int domain, Integer id) {
		ACCOUNTING.deleteAccountEntry(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public AccountingInvoice initializeInvoice(String domainName, int domain, 
			AccountingRegistry registry, Integer activity, Date issueDate)
			throws AonCoreException {
		return ACCOUNTING.initializeInvoice(domainName, domain, this.getUserLogin(), 
				registry, activity, issueDate);
	}

	@Override
	public AccountingInvoice getAccountingInvoice(String domainName, int domain, Integer accountEntry)
			throws AonCoreException {
		return ACCOUNTING.getAccountingInvoice(domainName, domain, this.getUserLogin(), accountEntry);
	}

	@Override
	public AccountingInvoice getAccountingInvoiceFromInvoice(String domainName, int domain, Integer invoiceId)
			throws AonCoreException {
		return ACCOUNTING.getAccountingInvoiceFromInvoice(domainName, domain, this.getUserLogin(), invoiceId);
	}

	@Override
	public AccountingInvoice getRegistryLastAccountingInvoice(String domainName, int domain,
			Integer registryId) {
		return ACCOUNTING.getRegistryLastAccountingInvoice(domainName, domain, this.getUserLogin(), registryId);
	}
	
	@Override
	public AccountingInvoice rectifyInvoice(String domainName, int domain, Integer invoiceId,
			InvoiceRectificationData data) throws AonCoreException {
		return ACCOUNTING.rectifyInvoice(domainName, domain, this.getUserLogin(), invoiceId, data );
	}

	@Override
	public AccountingInvoice save(String domainName, int domain, AccountingInvoice invoice)
			throws AonCoreException {
		return ACCOUNTING.save(domainName, domain, this.getUserLogin(), invoice);
	}

//	@Override
//	public LinkedList<AccountEntry> insertSalaryAccountEntries(
//			String domainName, int domain, Date from, Date to, String concept,
//			Integer registryBank) {
//		List<Integer> ids = ACCOUNTING.insertSalaryEntries(domainName, domain,
//				this.getUserLogin() , from, to, concept, registryBank);
//		final Integer[] arr = ids.toArray(new Integer[ids.size()]);  
//		return ACCOUNTING.getAccountEntries(domainName, domain, this.getUserLogin()
//				, p -> p.getIdProperty().in(arr)
//						.and(p.getDomainProperty().eq(domain) )
//				, 0, 100);
//	}
//	@Override
//	public LinkedList<AccountEntry> previewSalaryAccountEntries(
//			String domainName, int domain, Date from, Date to, String concept,
//			Integer registryBank) {
//		return ACCOUNTING.previewSalaryEntries(domainName, domain,
//				this.getUserLogin() , from, to, concept, registryBank);
//	}

	@Override
	public AccountStatementReport getAccountStatement(String domainName,
			int domain, AccountStatementParams params) throws AonCoreException {
		return ACCOUNTING.getAccountStatement(domainName,domain,this.getUserLogin(),params);
	}
	@Override
	public LinkedList<AccountStatement> getAccountBalance(String domainName,
			int domain, AccountStatementParams params) throws AonCoreException {
		return ACCOUNTING.getAccountBalance(domainName,domain,this.getUserLogin(),params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public LinkedList<Finance> getAccountFinances(String domainName, int domain
			, FinanceParams params, int offset, int limit) {
		return ACCOUNTING.getAccountFinances(domainName, domain, this.getUserLogin(), params, offset, limit);		
	}
	@Override
	public FinanceEntry save(String domainName, int domain, FinanceEntry financeEntry)
			throws AonCoreException {
		return ACCOUNTING.save(domainName, domain, this.getUserLogin(), financeEntry);		
	}
	@Override
	public FinanceEntry getFinanceEntry(String domainName, int domain, Integer accountEntry) {
		return ACCOUNTING.getFinanceEntry(domainName, domain, this.getUserLogin(), accountEntry);		
	};
	
	// --------------------------------------------------------------- IRPF
	@Override
	public LinkedList<IrpfBreakdown> getIrpfBreakdownSummary(String domainName, String user, int domain,
			IRPFParams params) throws AonCoreException {
		return FISCAL.getIrpfBreakdownSummary(domainName, user, domain, params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	@Override
	public LinkedList<IrpfBreakdown> getIrpfBreakdown(String domainName, String user, int domain,
			IRPFParams params) throws AonCoreException {
		return FISCAL.getIrpfBreakdown(domainName, user, domain, params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
}
