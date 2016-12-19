package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Fiscal")
public interface FiscalService extends RemoteService {
	// ---------------------------------- COMMON
	Double mathExpression(String expression) throws AonCoreException;

	// ------------------------------------------------------- FISCAL PARAMETERS
	FiscalParameters getFiscalParameters(String domainName, int domain) throws AonCoreException;

	// ------------------------------------------------------- FISCAL PANEL
	FiscalModelMatrix getFiscalPanel(String domainName,int domain, int y);
	LinkedList<IFiscalModel> getAllModels(String domainName, int domain);
	LinkedList<IFiscalModel> getAllModels(String domainName, int domain, int year);
	
	// -------------------------------------------------------------- ACTIVITIES
	LinkedList<Activity> getActivities(int activityGroup) throws AonCoreException;
	
	// ---------------------------------------------------------------MODELO 190
	void deleteMod190(String domainName, int domain,Mod190 mod190) throws AonCoreException;
	Mod190 saveMod190(String domainName, int domain,Mod190 mod190) throws AonCoreException;
	LinkedList<Mod190> getMod190s(String domainName, int domain) throws AonCoreException;
	Mod190 getMod190(String domainName, int domain,Integer id) throws AonCoreException;
	Mod190Detail getMod190Detail(String domainName, int domain,Integer id) throws AonCoreException;
	Mod190 initializeMod190(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 193
	void deleteMod193(String domainName, int domain,Mod193 mod193) throws AonCoreException;
	Mod193 saveMod193(String domainName, int domain,Mod193 mod193) throws AonCoreException;
	LinkedList<Mod193> getMod193s(String domainName, int domain) throws AonCoreException;
	Mod193 getMod193(String domainName, int domain,Integer id) throws AonCoreException;
	Mod193 initializeMod193(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 180
	void deleteMod180(String domainName, int domain,Mod180 mod180) throws AonCoreException;
	Mod180 saveMod180(String domainName, int domain,Mod180 mod180) throws AonCoreException;
	LinkedList<Mod180> getMod180s(String domainName, int domain) throws AonCoreException;
	Mod180 getMod180(String domainName, int domain,Integer id) throws AonCoreException;
	Mod180Detail getMod180Detail(String domainName, int domain,Integer id) throws AonCoreException;
	Mod180 initializeMod180(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 184
	void deleteMod184(String domainName, int domain,Mod184 mod184) throws AonCoreException;
	Mod184 saveMod184(String domainName, int domain,Mod184 mod184) throws AonCoreException;
	LinkedList<Mod184> getMod184s(String domainName, int domain) throws AonCoreException;
	Mod184 getMod184(String domainName, int domain,Integer id) throws AonCoreException;
	Mod184 initializeMod184(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 390
	LinkedList<Mod390> getMod390s(String domainName, Integer domain) throws AonCoreException;
	
	// ---------------------------------------------------------------MODELO 390 - 2014
	Mod3902014 getMod3902014(String domainName, Integer domain,Integer id) throws AonCoreException;
	Mod3902014 saveMod3902014(String domainName, Integer domain,Mod3902014 mod390) throws AonCoreException;
	void deleteMod3902014(String domainName, Integer domain,Mod3902014 mod390) throws AonCoreException;
	Mod3902014 initializeMod3902014(String domainName, Integer domain, Integer year);
	
	// ---------------------------------------------------------------MODELO 390 - 2015
	Mod3902015 getMod3902015(String domainName, Integer domain,Integer id) throws AonCoreException;
	Mod3902015 saveMod3902015(String domainName, Integer domain,Mod3902015 mod390) throws AonCoreException;
	void deleteMod3902015(String domainName, Integer domain,Mod3902015 mod390) throws AonCoreException;
	Mod3902015 initializeMod3902015(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 111
	Mod111 getMod111(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod111> getMod111s(String domainName, int domain) throws AonCoreException;
	Mod111 calculateMod111(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 saveMod111(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 saveCommentsMod111(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 initializeForFinishMod111(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 finishMod111(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 reopenMod111(String domainName, Mod111 mod111) throws AonCoreException;
	void deleteMod111(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 initializeMod111(String domainName, int domain, Mod111 mod111);
	Mod111 createMod111(String domainName, int domain, Mod111 mod111) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

	// ---------------------------------------------------------------MODELO 115
	Mod115 getMod115(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod115> getMod115s(String domainName, int domain) throws AonCoreException;
	Mod115 calculateMod115(String domainName, Mod115 mod115) throws AonCoreException;
	Mod115 saveMod115(String domainName, Mod115 mod115) throws AonCoreException;
	Mod115 saveCommentsMod115(String domainName, Mod115 mod115) throws AonCoreException;
	Mod115 initializeForFinishMod115(String domainName, Mod115 mod115) throws AonCoreException;
	Mod115 finishMod115(String domainName, Mod115 mod115) throws AonCoreException;
	Mod115 reopenMod115(String domainName, Mod115 mod115) throws AonCoreException;
	void deleteMod115(String domainName, Mod115 mod115) throws AonCoreException;
	Mod115 initializeMod115(String domainName, int domain, Mod115 mod115);
	Mod115 createMod115(String domainName, int domain, Mod115 mod115) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

	// ---------------------------------------------------------------MODELO 123
	Mod123 getMod123(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod123> getMod123s(String domainName, int domain) throws AonCoreException;
	Mod123 calculateMod123(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 saveMod123(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 saveCommentsMod123(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 initializeForFinishMod123(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 finishMod123(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 reopenMod123(String domainName, Mod123 mod123) throws AonCoreException;
	void deleteMod123(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 initializeMod123(String domainName, int domain, Mod123 mod123);
	Mod123 createMod123(String domainName, int domain, Mod123 mod123) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

	// ---------------------------------------------------------------MODELO 130
	Mod130 getMod130(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod130> getMod130s(String domainName, int domain) throws AonCoreException;
	Mod130 calculateMod130(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 saveMod130(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 saveCommentsMod130(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 initializeForFinishMod130(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 finishMod130(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 reopenMod130(String domainName, Mod130 mod130) throws AonCoreException;
	void deleteMod130(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 initializeMod130(String domainName, int domain, Mod130 mod130);
	Mod130 createMod130(String domainName, int domain, Mod130 mod130) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

	// ---------------------------------------------------------------MODELO 131
	Mod131 getMod131(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod131> getMod131s(String domainName, int domain) throws AonCoreException;
	Mod131 calculateMod131(String domainName, Mod131 mod131) throws AonCoreException;
	Mod131Activity calculateMod131Activity(String domainName, int domain, Mod131Activity activity) throws AonCoreException;
	Mod131 saveMod131(String domainName, Mod131 mod131) throws AonCoreException;
	Mod131 saveCommentsMod131(String domainName, Mod131 mod131) throws AonCoreException;
	Mod131 initializeForFinishMod131(String domainName, Mod131 mod131) throws AonCoreException;
	Mod131 finishMod131(String domainName, Mod131 mod131) throws AonCoreException;
	Mod131 reopenMod131(String domainName, Mod131 mod131) throws AonCoreException;
	void deleteMod131(String domainName, Mod131 mod131) throws AonCoreException;
	Mod131 initializeMod131(String domainName, int domain, Mod131 mod131);
	Mod131 createMod131(String domainName, int domain, Mod131 mod131) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

	// ---------------------------------------------------------------MODELO 202
	Mod202 getMod202(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod202> getMod202s(String domainName, int domain) throws AonCoreException;
	Mod202 calculateMod202(String domainName, Mod202 mod202) throws AonCoreException;
	Mod202 saveMod202(String domainName, Mod202 mod202) throws AonCoreException;
	void deleteMod202(String domainName, Mod202 mod202) throws AonCoreException;
	Mod202 initializeMod202(String domainName, int domain, Mod202 mod202) throws AonCoreException;


	// ---------------------------------------------------------------MODELO 200 - 2013
	Mod2002013 initializeNewMod2002013(String domainName,int domain,Mod2002013 mod200) throws AonCoreException;
	Mod2002013 initializeMod2002013(String domainName,int domain,Mod2002013 mod200) throws AonCoreException;
	Mod2002013 getMod2002013ByYear(String domainName,int domain, int year) throws AonCoreException;
	Mod2002013 getMod2002013ById(String domainName,int domain, int id) throws AonCoreException;
	Mod2002013 calculateMod2002013(Mod2002013 mod200) throws AonCoreException;
	Mod2002013 saveMod2002013(String domainName,int domain,Mod2002013 mod200) throws AonCoreException;
	Mod2002013 validateMod2002013(Mod2002013 mod200) throws AonCoreException;
	void deleteMod2002013(String domainName,int domain,int id) throws AonCoreException;
	String dumpAEATMod2002013(Mod2002013 mod200) throws AonCoreException;

	// ---------------------------------------------------------------MODELO 200 - 2014
	Mod2002014 createMod2002014(String domainName,int domain,int year) throws AonCoreException;
	Mod2002014 initializeNewMod2002014(String domainName,int domain,Mod2002014 mod200) throws AonCoreException;
	Mod2002014 initializeMod2002014(String domainName,int domain,Mod2002014 mod200) throws AonCoreException;
	Mod2002014 getMod2002014ByYear(String domainName,int domain, int year) throws AonCoreException;
	Mod2002014 getMod2002014ById(String domainName,int domain, int id) throws AonCoreException;
	Mod2002014 calculateMod2002014(Mod2002014 mod200) throws AonCoreException;
	Mod2002014 saveMod2002014(String domainName,int domain,Mod2002014 mod200) throws AonCoreException;
	Mod2002014 validateMod2002014(Mod2002014 mod200) throws AonCoreException;
	void deleteMod2002014(String domainName,int domain,int id) throws AonCoreException;
	String dumpAEATMod2002014(Mod2002014 mod200) throws AonCoreException;
	Mod2002014 importMod2002013(String domainName,int domain,Mod2002014 mod200) throws AonCoreException;
	Mod2002014 fillMod2002014AccountingData(Mod2002014 mod200) throws AonCoreException;

	// ---------------------------------------------------------------MODELO 200 - 2015
	Mod2002015 createMod2002015(String domainName,int domain,int year) throws AonCoreException;
	Mod2002015 initializeNewMod2002015(String domainName,int domain,Mod2002015 mod200) throws AonCoreException;
	Mod2002015 initializeMod2002015(String domainName,int domain,Mod2002015 mod200) throws AonCoreException;
	Mod2002015 getMod2002015ByYear(String domainName,int domain, int year) throws AonCoreException;
	Mod2002015 getMod2002015ById(String domainName,int domain, int id) throws AonCoreException;
	Mod2002015 calculateMod2002015(Mod2002015 mod200) throws AonCoreException;
	Mod2002015 saveMod2002015(String domainName,int domain,Mod2002015 mod200) throws AonCoreException;
	Mod2002015 validateMod2002015(Mod2002015 mod200) throws AonCoreException;
	void deleteMod2002015(String domainName,int domain,int id) throws AonCoreException;
	String dumpAEATMod2002015(Mod2002015 mod200) throws AonCoreException;
	Mod2002015 importMod2002014(String domainName,int domain,Mod2002015 mod200) throws AonCoreException;
	Mod2002015 fillMod2002015AccountingData(Mod2002015 mod200) throws AonCoreException;

	// --------------------------------------------------------------- NORMALIZED MEMORY
	Memory readMemory(Memory memory) throws AonCoreException;
	Memory saveMemory(Memory memory) throws AonCoreException;
	void deleteMemory(Memory memory) throws AonCoreException;
	
	// -------------------------------- ------------------------------- ACCOUNT PERIOD
	LinkedList<AccountPeriod> getDomainPeriods(String domainName,int domain) throws AonCoreException;

	// --------------------------------------------------------------- ACCOUNT ENTRIES
	LinkedList<AccountEntry> getAccountEntries(String domainName,int domain
			, AccountEntryParams params,int offset, int limit) throws AonCoreException;
	AccountEntry getAccountEntry(String domainName,int domain, int id) throws AonCoreException;
	AccountEntry save(String domainName,int domain, AccountEntry ae) throws AonCoreException;
	LinkedList<AccountEntry> previewSalaryAccountEntries(String domainName,int domain, Date from, Date to,String concept,Integer registryBank) throws AonCoreException;
	LinkedList<AccountEntry> insertSalaryAccountEntries(String domainName,int domain, Date from, Date to,String concept,Integer registryBank) throws AonCoreException;
	LinkedList<AccountEntry> getSalaryAccountEntries(String domainName,int domain, Date from, Date to) throws AonCoreException;
	void deleteAccountEntry(String domainName,int domain, Integer id) throws AonCoreException;
	AccountingInvoice getAccountingInvoice(String domainName, int domain, Integer accountEntry) throws AonCoreException;
	AccountingInvoice getAccountingInvoiceFromInvoice(String domainName, int domain, Integer invoiceId) throws AonCoreException;
	AccountingInvoice save(String currentDomainName, int currentDomain, AccountingInvoice invoice) throws AonCoreException;
	AccountingInvoice initializeInvoice(String domainName,int domain, AccountingRegistry registry, Date issueDate) throws AonCoreException; 
	AccountingInvoice getRegistryLastAccountingInvoice(String currentDomainName, int currentDomain, Integer registryId) throws AonCoreException;
	AccountingInvoice rectifyInvoice(String currentDomainName, int currentDomain, Integer id,
		InvoiceRectificationData data) throws AonCoreException;
	LinkedList<SalaryEntry> getSalaryEntries(String domainName, int domain, Date from, Date to) throws AonCoreException;
	// --------------------------------------------------------------- ACCOUNT STATEMENT
	AccountStatementReport getAccountStatement(String domainName,int domain, AccountStatementParams params) throws AonCoreException;	
	LinkedList<AccountStatement> getAccountBalance(String domainName,int domain, AccountStatementParams params) throws AonCoreException;

	Attach getMod111Attach(String domainName, Mod111 mod111);
	Attach getMod115Attach(String domainName, Mod115 mod115);
	Attach getMod123Attach(String domainName, Mod123 mod123);
	
}
