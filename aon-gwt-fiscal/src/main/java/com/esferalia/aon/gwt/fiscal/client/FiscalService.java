package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Fiscal")
public interface FiscalService extends RemoteService {

	// ------------------------------------------------------- FISCAL PARAMETERS

	FiscalParameters getFiscalParameters(String domainName, int domain) throws AonSQLException;

	// -------------------------------------------------------------- ENTERPRISE
	ArrayList<Enterprise> getEnterprises(int domain, String query)
			throws AonSQLException;

	// -------------------------------------------------------------- ACTIVITIES
	ArrayList<Activity> getActivities(int activityGroup) throws AonSQLException;
	
	// ---------------------------------------------------------------MODELO 190
	void deleteMod190(String domainName, int domain,Mod190 mod190) throws AonCoreException;
	Mod190 saveMod190(String domainName, int domain,Mod190 mod190) throws AonCoreException;
	ArrayList<Mod190> getMod190s(String domainName, int domain) throws AonCoreException;
	Mod190 getMod190(String domainName, int domain,Integer id) throws AonCoreException;
	Mod190Detail getMod190Detail(String domainName, int domain,Integer id) throws AonSQLException;
	Mod190 initializeMod190(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 193
	void deleteMod193(String domainName, int domain,Mod193 mod193) throws AonCoreException;
	Mod193 saveMod193(String domainName, int domain,Mod193 mod193) throws AonCoreException;
	ArrayList<Mod193> getMod193s(String domainName, int domain) throws AonCoreException;
	Mod193 getMod193(String domainName, int domain,Integer id) throws AonCoreException;
	Mod193Detail getMod193Detail(String domainName, int domain,Integer id) throws AonSQLException;
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

}
