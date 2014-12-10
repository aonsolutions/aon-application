package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.shared.Activity;
import com.esferalia.aon.gwt.fiscal.shared.Enterprise;
import com.esferalia.aon.gwt.fiscal.shared.Mod303Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.esferalia.aon.gwt.fiscal.shared.Mod390Detail;
import com.esferalia.aon.occam.api.model.Mod180;
import com.esferalia.aon.occam.api.model.Mod180Detail;
import com.esferalia.aon.occam.api.model.Mod190;
import com.esferalia.aon.occam.api.model.Mod190Detail;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Fiscal")
public interface FiscalService extends RemoteService {

	// ------------------------------------------------------- FISCAL PARAMETERS

	FiscalParameters getFiscalParameters(int domain) throws AonSQLException;

	// -------------------------------------------------------------- ENTERPRISE
	ArrayList<Enterprise> getEnterprises(int domain, String query)
			throws AonSQLException;

	// -------------------------------------------------------------- ACTIVITIES
	ArrayList<Activity> getActivities(int activityGroup) throws AonSQLException;
	
	// ---------------------------------------------------------------MODELO 190
	void deleteMod190(String domainName, int domain,Mod190 mod190) throws AonSQLException;
	Mod190 saveMod190(String domainName, int domain,Mod190 mod190) throws AonSQLException;
	ArrayList<Mod190> getMod190s(String domainName, int domain) throws AonSQLException;
	Mod190 getMod190(String domainName, int domain,Integer id) throws AonSQLException;
	Mod190Detail getMod190Detail(String domainName, int domain,Integer id) throws AonSQLException;

	// ---------------------------------------------------------------MODELO 180
	void deleteMod180(String domainName, int domain,Mod180 mod180) throws AonSQLException;
	Mod180 saveMod180(String domainName, int domain,Mod180 mod180) throws AonSQLException;
	ArrayList<Mod180> getMod180s(String domainName, int domain);
	Mod180 getMod180(String domainName, int domain,Integer id);
	Mod180Detail getMod180Detail(String domainName, int domain,Integer id);

	// ---------------------------------------------------------------MODELO 390
	ArrayList<Mod390Detail> getMod390Details(int domain,Integer year) throws AonSQLException;
	
	Mod303Results getMod303Results(int domain, int year) throws AonSQLException;
	
	ArrayList<Mod311Results> getMod311Results(int domain, int year) throws AonSQLException;

	Mod390 getMod390(Integer id) throws AonSQLException;

	ArrayList<Mod390> getMod390s(int domain) throws AonSQLException;
	
	Mod390 saveMod390(Mod390 mod390) throws AonSQLException;
	
	void deleteMod390(Mod390 mod390) throws AonSQLException;
}
