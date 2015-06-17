package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Fiscal")
public interface FiscalService extends RemoteService {

	// ------------------------------------------------------- FISCAL PARAMETERS
	FiscalParameters getFiscalParameters(String domainName, int domain) throws AonCoreException;

	// ------------------------------------------------------- FISCAL PANEL
	FiscalModelMatrix getFiscalPanel(String domainName,int domain, int y);
	LinkedList<IFiscalModel> getAllModels(String domainName, int domain);
	LinkedList<IFiscalModel> getAllModels(String domainName, int domain, int year);
	
	// -------------------------------------------------------------- ACTIVITIES
	ArrayList<Activity> getActivities(int activityGroup) throws AonCoreException;
	
	// ---------------------------------------------------------- FISCAL ACTIVITIES
	FiscalActivity calculate(String domainName, FiscalActivity fa);
	ArrayList<Epigraph> getModuleEpigraphs(int year);
	ArrayList<FiscalActivity> getFiscalActivities(String domainName, int domain) throws AonCoreException;
	FiscalActivity getFiscalActivity(String domainName, int domain, int id) throws AonCoreException;
	FiscalActivity getFiscalActivityFor(String domainName,Epigraph epigraph, FiscalActivity fa);
	FiscalActivity save(String domainName, FiscalActivity fa) throws AonCoreException;
	void delete(String domainName, FiscalActivity fa) throws AonCoreException;
	
	// ---------------------------------------------------------- FISCAL MODEL
	LinkedList<FiscalModel> getFiscalModels(String domainName, int domain) throws AonCoreException;
	FiscalModel getFiscalModel(String domainName, int domain, int id) throws AonCoreException;
	FiscalModel save(String domainName, FiscalModel fm) throws AonCoreException;
	void delete(String domainName, FiscalModel fm) throws AonCoreException;

	// ---------------------------------------------------------------MODELO 190
	void deleteMod190(String domainName, int domain,Mod190 mod190) throws AonCoreException;
	Mod190 saveMod190(String domainName, int domain,Mod190 mod190) throws AonCoreException;
	ArrayList<Mod190> getMod190s(String domainName, int domain) throws AonCoreException;
	Mod190 getMod190(String domainName, int domain,Integer id) throws AonCoreException;
	Mod190Detail getMod190Detail(String domainName, int domain,Integer id) throws AonCoreException;
	Mod190 initializeMod190(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 193
	void deleteMod193(String domainName, int domain,Mod193 mod193) throws AonCoreException;
	Mod193 saveMod193(String domainName, int domain,Mod193 mod193) throws AonCoreException;
	ArrayList<Mod193> getMod193s(String domainName, int domain) throws AonCoreException;
	Mod193 getMod193(String domainName, int domain,Integer id) throws AonCoreException;
	Mod193Detail getMod193Detail(String domainName, int domain,Integer id) throws AonCoreException;
	Mod193 initializeMod193(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 180
	void deleteMod180(String domainName, int domain,Mod180 mod180) throws AonCoreException;
	Mod180 saveMod180(String domainName, int domain,Mod180 mod180) throws AonCoreException;
	ArrayList<Mod180> getMod180s(String domainName, int domain) throws AonCoreException;
	Mod180 getMod180(String domainName, int domain,Integer id) throws AonCoreException;
	Mod180Detail getMod180Detail(String domainName, int domain,Integer id) throws AonCoreException;
	Mod180 initializeMod180(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 184
	void deleteMod184(String domainName, int domain,Mod184 mod184) throws AonCoreException;
	Mod184 saveMod184(String domainName, int domain,Mod184 mod184) throws AonCoreException;
	ArrayList<Mod184> getMod184s(String domainName, int domain) throws AonCoreException;
	Mod184 getMod184(String domainName, int domain,Integer id) throws AonCoreException;
	Mod184 initializeMod184(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 390
	Mod390 getMod390(String domainName, Integer domain,Integer id) throws AonCoreException;
	ArrayList<Mod390> getMod390s(String domainName, Integer domain) throws AonCoreException;
	Mod390 saveMod390(String domainName, Integer domain,Mod390 mod390) throws AonCoreException;
	void deleteMod390(String domainName, Integer domain,Mod390 mod390) throws AonCoreException;
	ArrayList<Mod390Detail> getMod390Details(String domainName, Integer domain,Mod390 mod390) throws AonCoreException;
	Mod390 initializeMod390(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 131
	Mod131 getMod131(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod131> getMod131s(String domainName, int domain) throws AonCoreException;
	Mod131 calculateMod131(String domainName, Mod131 mod131) throws AonCoreException;
	Mod131 saveMod131(String domainName, Mod131 mod131) throws AonCoreException;
	void deleteMod131(String domainName, Mod131 mod131) throws AonCoreException;
	Mod131 initializeMod131(String domainName, int domain, Mod131 mod131) throws AonCoreException;
	
	// ---------------------------------------------------------------MODELO 131
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

	// --------------------------------------------------------------- NORMALIZED MEMORY
	Memory readMemory(Memory memory) throws AonCoreException;
	Memory saveMemory(Memory memory) throws AonCoreException;
	void deleteMemory(Memory memory) throws AonCoreException;
	
}
