package com.esferalia.aon.gwt.fiscal.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.server.util.AONMVELUtils;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Activities.Type1Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type2Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type3Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type4Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type7Activities;
import com.esferalia.aon.occam.api.model.type.Activities.TypeActivity;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
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
	public LinkedList<AccountEntry> getAccountEntries(String domainName,String user,
			int domain, final AccountEntryParams params,int offset, int limit) throws AonCoreException {
		return ACCOUNTING.getAccountEntries(domainName, domain, user, params, offset, limit);
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
	public AccountStatementReport getAccountStatement(String domainName, String user,
			int domain, AccountingReportParams params) throws AonCoreException {
		return ACCOUNTING.getAccountStatement(domainName,domain,user,params);
	}
	
	@Override
	public AccountTrialBalanceReport getAccountTrialBalanceReport(String domainName, String user, int domain,
			AccountingReportParams params) throws AonCoreException {
		return ACCOUNTING.getAccountTrialBalance(domainName,domain,user,params);
	}
	
	@Override
	public LinkedList<AccountStatement> getAccountBalance(String domainName,
			int domain, AccountingReportParams params) throws AonCoreException {
		return ACCOUNTING.getAccountBalance(domainName,domain,this.getUserLogin(),params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Override
	public AccountOperatingReport getAccountOperatingReport(String domainName, String user, int domain,
			AccountingReportParams params) throws AonCoreException {
		return ACCOUNTING.getAccountOperatingReport(domainName, user, domain,params);
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
	
	// --------------------------------------------------------------- GWT API INFO
	
	public String getLoggedUser() {
		return AonServletUtils.getLoggedUser();
	}
	
	public AonData getAonData(String domainName, Integer domainId){
		Domain domain = AON.getDomain(domainName, domainId, getLoggedUser());
		User user = AON.getUser(domain.getName(), domain.getId(), getLoggedUser());
		Integer operator = AON.getTaskHolder(domain.getName(), domainId, getLoggedUser(), 
				f -> f.getDomainProperty().eq(domainId).and(f.getUserIdProperty().eq(user.getId()))).getId();
		return new AonData().setUser(user)
				.setMd5(getMd5(user.getLogin()+domain.getName()))
				.setDomain(domain)
				.setUserOperator(operator);
	}
	
	public String getMd5(String str){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    md.update(str.getBytes());
	    byte byteData[] = md.digest();
	    //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
	     	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }       
        return sb.toString();
	}
	
	@Override
	public Integer presentationFile(String domainName, Integer domainId, String user, FiscalModelType type,
			Integer id) {
		if(FiscalModelType.M303.equals(type) 
				|| FiscalModelType.M303_RG.equals(type)
				|| FiscalModelType.M303_RS.equals(type)) {
			return Mod303ServiceImpl.getInstance().presentationFile(domainName, domainId, user, id);
		} else if(FiscalModelType.M111.equals(type)) {
			return Mod111ServiceImpl.getInstance().presentationFile(domainName, domainId, user, id);
		}  else if(FiscalModelType.M115.equals(type)) {
			return Mod115ServiceImpl.getInstance().presentationFile(domainName, domainId, user, id);
		}  else if(FiscalModelType.M123.equals(type)) {
			return Mod123ServiceImpl.getInstance().presentationFile(domainName, domainId, user, id);
		}
		return -1;
	}
	
	@Override
	public void markAsFinished(String domainName, Integer domainId, String user, IFiscalModel model) {
		if(FiscalModelType.M303.equals(model.getModel()) 
				|| FiscalModelType.M303_RG.equals(model.getModel())
				|| FiscalModelType.M303_RS.equals(model.getModel())) {
			Mod303 mod303 = FISCAL.getMod303(domainName, domainId, user, model.getId());
			for(Mod303Key k : Mod303Key.values()) {
				if(!mod303.getMap().containsKey(k.getValue())){
					mod303.getMap().put(k.getValue(), new FiscalModelDetail().setAmount(0.0));
				}
			}
			mod303 = Mod303ServiceImpl.getInstance().initializeForFinish(domainName, user, mod303);
			Mod303ServiceImpl.getInstance().markAsFinished(domainName, user, mod303);
		} else if(FiscalModelType.M111.equals(model.getModel())) {
			Mod111 mod111 = FISCAL.getMod111(domainName, domainId, user, model.getId());
			mod111 = Mod111ServiceImpl.getInstance().initializeForFinish(domainName, user, mod111);
			Mod111ServiceImpl.getInstance().markAsFinished(domainName, user, mod111);
		}  else if(FiscalModelType.M115.equals(model.getModel())) {
			Mod115 mod115 = FISCAL.getMod115(domainName, domainId, user, model.getId());
			mod115 = Mod115ServiceImpl.getInstance().initializeForFinish(domainName, user, mod115);
			Mod115ServiceImpl.getInstance().markAsFinished(domainName, user, mod115);
		}  else if(FiscalModelType.M123.equals(model.getModel())) {
			Mod123 mod123 = FISCAL.getMod123(domainName, domainId, user, model.getId());
			mod123 = Mod123ServiceImpl.getInstance().initializeForFinish(domainName, user, mod123);
			Mod123ServiceImpl.getInstance().markAsFinished(domainName, user, mod123);
		} else if(FiscalModelType.M130.equals(model.getModel())) {
			Mod130 mod130 = FISCAL.getMod130(domainName, domainId, user, model.getId());
			mod130 = Mod130ServiceImpl.getInstance().initializeForFinish(domainName, user, mod130);
			Mod130ServiceImpl.getInstance().markAsFinished(domainName, user, mod130);
		} else if(FiscalModelType.M131.equals(model.getModel())) {
			Mod131 mod131 = FISCAL.getMod131(domainName, domainId, user, model.getId());
			mod131 = Mod131ServiceImpl.getInstance().initializeForFinish(domainName, user, mod131);
			Mod131ServiceImpl.getInstance().markAsFinished(domainName, user, mod131);
		} else if(FiscalModelType.M180.equals(model.getModel())) {
			// TODO INITIALIZE FOR FINISH ??
			Mod180 mod180 =  FISCAL.getMod180(domainName, domainId, user, model.getId());
			Mod180ServiceImpl.getInstance().changeStatusMod180(domainName, user, mod180, FiscalStatus.FINISHED);
		} else if(FiscalModelType.M184.equals(model.getModel())) {
			// TODO INITIALIZE FOR FINISH ??
			Mod184 mod184 =  FISCAL.getMod184(domainName, domainId, user, model.getId());
			Mod184ServiceImpl.getInstance().changeStatusMod184(domainName, user, mod184, FiscalStatus.FINISHED);		
		} else if(FiscalModelType.M190.equals(model.getModel())) {
			// TODO INITIALIZE FOR FINISH ??
			Mod190 mod190 =  FISCAL.getMod190(domainName, domainId, user, model.getId());
			Mod190ServiceImpl.getInstance().changeStatus(domainName, user, mod190, FiscalStatus.FINISHED);
		} else if(FiscalModelType.M193.equals(model.getModel())) {
			// TODO INITIALIZE FOR FINISH ??
			Mod193 mod193 =  FISCAL.getMod193(domainName, domainId, user, model.getId());
			Mod193ServiceImpl.getInstance().changeStatus(domainName, user, mod193, FiscalStatus.FINISHED);
		} else if(FiscalModelType.M200.equals(model.getModel())) {
			// TODO
		} else if(FiscalModelType.M202.equals(model.getModel())) {
			Mod202 mod202 =  FISCAL.getMod202(domainName, domainId, user, model.getId());
			mod202 = Mod202ServiceImpl.getInstance().initializeForFinish(domainName, user, mod202);
			Mod202ServiceImpl.getInstance().markAsFinished(domainName, user, mod202);
		} else if(FiscalModelType.M310.equals(model.getModel())) {
			// TODO
		} else if(FiscalModelType.M311.equals(model.getModel())) {
			// TODO
		} else if(FiscalModelType.M340.equals(model.getModel())) {
			// TODO
		} else if(FiscalModelType.M347.equals(model.getModel())) {
			// TODO INITIALIZE FOR FINISH ??
			Mod347 mod347 =  FISCAL.getMod347(domainName, domainId, user, model.getId());
			Mod347ServiceImpl.getInstance().changeStatusMod347(domainName, user, mod347, FiscalStatus.FINISHED);			
		} else if(FiscalModelType.M349.equals(model.getModel())) {
			// TODO INITIALIZE FOR FINISH ??
			Mod349 mod349 =  FISCAL.getMod349(domainName, domainId, user, model.getId());
			Mod349ServiceImpl.getInstance().changeStatusMod349(domainName, user, mod349, FiscalStatus.FINISHED);
		} else if(FiscalModelType.M390.equals(model.getModel())) {
			// TODO INITIALIZE FOR FINISH ??
			Mod3902015 mod390 = FISCAL.getMod3902015(domainName, domainId, user, model.getId());
			Mod3902015ServiceImpl.getInstance().changeStatus(domainName, user, mod390, FiscalStatus.FINISHED);
		} else if(FiscalModelType.M390_HF.equals(model.getModel())) {
			Mod390HF mod390 = FISCAL.getMod390HF(domainName, domainId, user, model.getId());
			mod390 = Mod390HFServiceImpl.getInstance().initializeForFinish(domainName, mod390);
			Mod390HFServiceImpl.getInstance().markAsFinished(domainName, mod390);
		}
	}
}
