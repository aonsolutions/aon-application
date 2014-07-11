package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EmployeesService</code>.
 */
public interface Mod200ServiceAsync {

	void getMod200(int domain, int year, AsyncCallback<Mod200> callback);

	void initialize(Mod200 mod200, AsyncCallback<Mod200> callback);

	void calculate(Mod200 mod200, AsyncCallback<Mod200> callback);

	void save(Mod200 mod200, AsyncCallback<Mod200> callback);

	void validate(Mod200 mod200, AsyncCallback<Mod200> callback);

	void delete(Mod200 mod200, AsyncCallback<Mod200> callback);

	void dumpAEAT(Mod200 mod200, AsyncCallback<String> callback);


}
