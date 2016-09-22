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
import com.esferalia.aon.occam.api.model.attachment.Attach;
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
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalServiceAsync {
	// -------------------------------------------------------------- COMMON
	void mathExpression(String expression, AsyncCallback<Double> callback);
	
	// -------------------------------------------------------------- PARAMS
	void getFiscalParameters(String domainName,int domain,AsyncCallback<FiscalParameters> callback);

	// ------------------------------------------------------ FISCAL PANEL
	void getFiscalPanel(String currentDomainName,int currentDomain,int year,
			AsyncCallback<FiscalModelMatrix> asyncCallback);
	void getAllModels(String domainName, int domain, 
			AsyncCallback<LinkedList<IFiscalModel>> callback);
	void getAllModels(String domainName, int domain, int year,
			AsyncCallback<LinkedList<IFiscalModel>> callback);

	// -------------------------------------------------------------- ACTIVITIES
	void getActivities(int activityGroup,
			AsyncCallback<LinkedList<Activity>> callback);

	// ---------------------------------------------------------------MODELO 190
	void deleteMod190(String domainName, int domain,Mod190 mod190, AsyncCallback<Void> callback);
	void saveMod190(String domainName, int domain,Mod190 mod190, AsyncCallback<Mod190> callback);
	void getMod190s(String domainName, int domain, AsyncCallback<LinkedList<Mod190>> callback);
	void getMod190(String domainName, int domain,Integer id, AsyncCallback<Mod190> callback);
	void getMod190Detail(String domainName, int domain,Integer id, AsyncCallback<Mod190Detail> callback);
	void initializeMod190(String domainName, Integer domain, Integer year,AsyncCallback<Mod190> callback);

	// ---------------------------------------------------------------MODELO 180
	void deleteMod180(String domainName, int domain, Mod180 mod180,AsyncCallback<Void> callback);
	void saveMod180(String domainName, int domain, Mod180 mod180, AsyncCallback<Mod180> callback);
	void getMod180s(String domainName, int domain, AsyncCallback<LinkedList<Mod180>> callback);
	void getMod180(String domainName, int domain, Integer id, AsyncCallback<Mod180> callback);
	void getMod180Detail(String domainName, int domain, Integer id, AsyncCallback<Mod180Detail> callback);
	void initializeMod180(String domainName, Integer domain, Integer year,AsyncCallback<Mod180> callback);

	// ---------------------------------------------------------------MODELO 184
	void deleteMod184(String domainName, int domain,Mod184 mod184, AsyncCallback<Void> callback);
	void saveMod184(String domainName, int domain,Mod184 mod184, AsyncCallback<Mod184> callback);
	void getMod184s(String domainName, int domain, AsyncCallback<LinkedList<Mod184>> callback);
	void getMod184(String domainName, int domain,Integer id, AsyncCallback<Mod184> callback);
	void initializeMod184(String domainName, Integer domain, Integer year,AsyncCallback<Mod184> callback);

	// ---------------------------------------------------------------MODELO 193
	void deleteMod193(String domainName, int domain,Mod193 mod193, AsyncCallback<Void> callback);
	void saveMod193(String domainName, int domain,Mod193 mod193, AsyncCallback<Mod193> callback);
	void getMod193s(String domainName, int domain, AsyncCallback<LinkedList<Mod193>> callback);
	void getMod193(String domainName, int domain,Integer id, AsyncCallback<Mod193> callback);
	void initializeMod193(String domainName, Integer domain, Integer year,AsyncCallback<Mod193> callback);

	// ---------------------------------------------------------------MODELO 390
	void getMod390s(String domainName, Integer domain, AsyncCallback<LinkedList<Mod390>> callback);
	
	// ---------------------------------------------------------------MODELO 390 - 2014
	void getMod3902014(String domainName, Integer domain,Integer id, AsyncCallback<Mod3902014> callback);
	void saveMod3902014(String domainName, Integer domain, Mod3902014 mod390, AsyncCallback<Mod3902014> callback);
	void deleteMod3902014(String domainName, Integer domain, Mod3902014 mod390, AsyncCallback<Void> callback);
	void initializeMod3902014(String domainName, Integer domain, Integer year, AsyncCallback<Mod3902014> callback);

	// ---------------------------------------------------------------MODELO 390 - 2015
	void getMod3902015(String domainName, Integer domain,Integer id, AsyncCallback<Mod3902015> callback);
	void saveMod3902015(String domainName, Integer domain, Mod3902015 mod390, AsyncCallback<Mod3902015> callback);
	void deleteMod3902015(String domainName, Integer domain, Mod3902015 mod390, AsyncCallback<Void> callback);
	void initializeMod3902015(String domainName, Integer domain, Integer year, AsyncCallback<Mod3902015> callback);
	
	// ---------------------------------------------------------------MODELO 111
	void getMod111(String domainName, int domain, int id,AsyncCallback<Mod111> callback);
	void getMod111s(String domainName, int domain,AsyncCallback<LinkedList<Mod111>> callback);
	void calculateMod111(String domainName, Mod111 mod111,AsyncCallback<Mod111> callback);
	void deleteMod111(String currentDomainName, Mod111 mod111,AsyncCallback<Void> callback);
	void saveMod111(String domainName, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void saveCommentsMod111(String domainName, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void initializeForFinishMod111(String domainName, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void finishMod111(String domainName, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void reopenMod111(String domainName, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void initializeMod111(String domainName, int domain, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void createMod111(String domainName, int domain, Mod111 mod111, AsyncCallback<Mod111> callback);
	void getInfo(String domainName, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	
	// ---------------------------------------------------------------MODELO 115
	void getMod115(String domainName, int domain, int id,AsyncCallback<Mod115> callback);
	void getMod115s(String domainName, int domain,AsyncCallback<LinkedList<Mod115>> callback);
	void calculateMod115(String domainName, Mod115 mod115,AsyncCallback<Mod115> callback);
	void deleteMod115(String currentDomainName, Mod115 mod115,AsyncCallback<Void> callback);
	void saveMod115(String domainName, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void saveCommentsMod115(String domainName, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void initializeForFinishMod115(String domainName, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void finishMod115(String domainName, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void reopenMod115(String domainName, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void initializeMod115(String domainName, int domain, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void createMod115(String domainName, int domain, Mod115 mod115, AsyncCallback<Mod115> callback);
	void getInfo(String domainName, int domain, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);

	// ---------------------------------------------------------------MODELO 123
	void getMod123(String domainName, int domain, int id,AsyncCallback<Mod123> callback);
	void getMod123s(String domainName, int domain,AsyncCallback<LinkedList<Mod123>> callback);
	void calculateMod123(String domainName, Mod123 mod123,AsyncCallback<Mod123> callback);
	void deleteMod123(String currentDomainName, Mod123 mod123,AsyncCallback<Void> callback);
	void saveMod123(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void saveCommentsMod123(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void initializeForFinishMod123(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void finishMod123(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void reopenMod123(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void initializeMod123(String domainName, int domain, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void createMod123(String domainName, int domain, Mod123 mod123, AsyncCallback<Mod123> callback);
	void getInfo(String domainName, int domain, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);

	// ---------------------------------------------------------------MODELO 130
	void getMod130(String domainName, int domain, int id,AsyncCallback<Mod130> callback);
	void getMod130s(String domainName, int domain,AsyncCallback<LinkedList<Mod130>> callback);
	void calculateMod130(String domainName, Mod130 mod130,AsyncCallback<Mod130> callback);
	void deleteMod130(String currentDomainName, Mod130 mod130,AsyncCallback<Void> callback);
	void saveMod130(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void saveCommentsMod130(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void initializeForFinishMod130(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void finishMod130(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void reopenMod130(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void initializeMod130(String domainName, int domain, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void createMod130(String domainName, int domain, Mod130 mod130, AsyncCallback<Mod130> callback);
	void getInfo(String domainName, int domain, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	
	// ---------------------------------------------------------------MODELO 131
	void getMod131(String domainName, int domain, int id,AsyncCallback<Mod131> callback);
	void getMod131s(String domainName, int domain,AsyncCallback<LinkedList<Mod131>> callback);
	void calculateMod131(String domainName, Mod131 mod131,AsyncCallback<Mod131> callback);
	void calculateMod131Activity(String domainName, int domain, Mod131Activity activity, AsyncCallback<Mod131Activity> callback);
	void deleteMod131(String currentDomainName, Mod131 mod131,AsyncCallback<Void> callback);
	void saveMod131(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void saveCommentsMod131(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void initializeForFinishMod131(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void finishMod131(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void reopenMod131(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void initializeMod131(String domainName, int domain, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void createMod131(String domainName, int domain, Mod131 mod131, AsyncCallback<Mod131> callback);
	void getInfo(String domainName, int domain, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);

	// ---------------------------------------------------------------MODELO 202
	void getMod202(String domainName, int domain, int id,AsyncCallback<Mod202> callback);
	void getMod202s(String domainName, int domain,AsyncCallback<LinkedList<Mod202>> callback);
	void calculateMod202(String domainName, Mod202 mod202,AsyncCallback<Mod202> callback);
	void deleteMod202(String currentDomainName, Mod202 treeObject,AsyncCallback<Void> callback);
	void saveMod202(String domainName, Mod202 mod202,AsyncCallback<Mod202> asyncCallback);
	void initializeMod202(String domainName, int domain, Mod202 mod202,AsyncCallback<Mod202> asyncCallback);

	// ---------------------------------------------------------------MODELO 200 - 2013
	void initializeNewMod2002013(String domainName, int domain, Mod2002013 mod200, AsyncCallback<Mod2002013> callback);
	void initializeMod2002013(String domainName, int domain, Mod2002013 mod200,AsyncCallback<Mod2002013> callback);
	void getMod2002013ByYear(String domainName, int domain, int year,AsyncCallback<Mod2002013> callback);
	void calculateMod2002013(Mod2002013 mod200, AsyncCallback<Mod2002013> callback);
	void deleteMod2002013(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002013(Mod2002013 mod200, AsyncCallback<String> callback);
	void getMod2002013ById(String domainName, int domain, int id,AsyncCallback<Mod2002013> callback);
	void saveMod2002013(String domainName, int domain, Mod2002013 mod200,AsyncCallback<Mod2002013> callback);
	void validateMod2002013(Mod2002013 mod200, AsyncCallback<Mod2002013> callback);
	
	// ---------------------------------------------------------------MODELO 200 - 2014
	void createMod2002014(String domainName, int domain, int year,AsyncCallback<Mod2002014> callback);
	void initializeNewMod2002014(String domainName, int domain, Mod2002014 mod200, AsyncCallback<Mod2002014> callback);
	void initializeMod2002014(String domainName, int domain, Mod2002014 mod200,AsyncCallback<Mod2002014> callback);
	void getMod2002014ByYear(String domainName, int domain, int year,AsyncCallback<Mod2002014> callback);
	void calculateMod2002014(Mod2002014 mod200, AsyncCallback<Mod2002014> callback);
	void deleteMod2002014(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002014(Mod2002014 mod200, AsyncCallback<String> callback);
	void getMod2002014ById(String domainName, int domain, int id,AsyncCallback<Mod2002014> callback);
	void saveMod2002014(String domainName, int domain, Mod2002014 mod200,AsyncCallback<Mod2002014> callback);
	void validateMod2002014(Mod2002014 mod200, AsyncCallback<Mod2002014> callback);
	void importMod2002013(String domainName, int domain, Mod2002014 mod200,AsyncCallback<Mod2002014> callback);
	void fillMod2002014AccountingData(Mod2002014 mod200, AsyncCallback<Mod2002014> callback);

	// ---------------------------------------------------------------MODELO 200 - 2015
	void createMod2002015(String domainName, int domain, int year,AsyncCallback<Mod2002015> callback);
	void initializeNewMod2002015(String domainName, int domain, Mod2002015 mod200, AsyncCallback<Mod2002015> callback);
	void initializeMod2002015(String domainName, int domain, Mod2002015 mod200,AsyncCallback<Mod2002015> callback);
	void getMod2002015ByYear(String domainName, int domain, int year,AsyncCallback<Mod2002015> callback);
	void calculateMod2002015(Mod2002015 mod200, AsyncCallback<Mod2002015> callback);
	void deleteMod2002015(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002015(Mod2002015 mod200, AsyncCallback<String> callback);
	void getMod2002015ById(String domainName, int domain, int id,AsyncCallback<Mod2002015> callback);
	void saveMod2002015(String domainName, int domain, Mod2002015 mod200,AsyncCallback<Mod2002015> callback);
	void validateMod2002015(Mod2002015 mod200, AsyncCallback<Mod2002015> callback);
	void importMod2002014(String domainName, int domain, Mod2002015 mod200,AsyncCallback<Mod2002015> callback);
	void fillMod2002015AccountingData(Mod2002015 mod200, AsyncCallback<Mod2002015> callback);

	// --------------------------------------------------------------- NORMALIZED MEMORY
	void readMemory(Memory memory, AsyncCallback<Memory> callback);
	void saveMemory(Memory memory, AsyncCallback<Memory> callback);
	void deleteMemory(Memory memory, AsyncCallback<Void> callback);

	// --------------------------------------------------------------- ACCOUNT PERIOD
	void getDomainPeriods(String domainName, int domain,
			AsyncCallback<LinkedList<AccountPeriod>> callback);

	// --------------------------------------------------------------- ACCOUNT ENTRIES
	void getAccountEntries(String domainName, int domain,
			AccountEntryParams params,int offset, int limit,
			AsyncCallback<LinkedList<AccountEntry>> callback);
	void getAccountEntry(String domainName, int domain, int id,
			AsyncCallback<AccountEntry> callback);
	void save(String domainName, int domain, AccountEntry ae,
			AsyncCallback<AccountEntry> callback);
	void insertSalaryAccountEntries(String domainName, int domain, Date from,
			Date to, String concept, Integer registryBank,
			AsyncCallback<LinkedList<AccountEntry>> callback);
	void getSalaryAccountEntries(String domainName, int domain,Date from, Date to,
			AsyncCallback<LinkedList<AccountEntry>> callback);
	void deleteAccountEntry(String domainName, int domain, Integer id,
			AsyncCallback<Void> callback);
	void initializeInvoice(String domainName, int domain, InvoiceType type, Integer registry,
			Date issueDate,AsyncCallback<AccountingInvoice> callback);
	void getAccountingInvoice(String domainName, int domain, Integer accountEntry,
			AsyncCallback<AccountingInvoice> callback);

	// --------------------------------------------------------------- ACCOUNT STATEMENT
	void getAccountStatement(String domainName, int domain, 
			AccountStatementParams params,
			AsyncCallback<AccountStatementReport> callback);

	void getAccountBalance(String domainName, int domain, 
			AccountStatementParams params,
			AsyncCallback<LinkedList<AccountStatement>> callback);

	
	void getMod111Attach(String domainName, Mod111 mod111, AsyncCallback<Attach> callback);
	void getMod115Attach(String domainName, Mod115 mod115, AsyncCallback<Attach> callback);
	void getMod123Attach(String domainName, Mod123 mod123, AsyncCallback<Attach> callback);



}
