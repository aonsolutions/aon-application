package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EmployeesService</code>.
 */
public interface Mod200ServiceAsync {

	void getMod200(String domainName,int domain, int year, AsyncCallback<Mod200> callback);
	void initialize(String domainName,int domain, Mod200 mod200, AsyncCallback<Mod200> callback);
	void calculate(Mod200 mod200, AsyncCallback<Mod200> callback);
	void save(String domainName,int domain, Mod200 mod200, AsyncCallback<Mod200> callback);
	void validate(Mod200 mod200, AsyncCallback<Mod200> callback);
	void delete(String domainName,int domain, Mod200 mod200, AsyncCallback<Mod200> callback);
	void dumpAEAT(Mod200 mod200, AsyncCallback<String> callback);
	void getCompanyBanks(String domainName,int domain, int enterprise,AsyncCallback<ArrayList<CompanyBank>> callback);


}
