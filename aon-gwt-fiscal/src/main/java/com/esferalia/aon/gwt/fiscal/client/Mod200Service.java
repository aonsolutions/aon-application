package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod200")
public interface Mod200Service extends RemoteService {

	Mod200 getMod200(int domain, int year) throws AonSQLException;
	
	Mod200 initialize(Mod200 mod200) throws AonSQLException;

	Mod200 calculate(Mod200 mod200) throws AonSQLException;

	Mod200 save(Mod200 mod200) throws AonSQLException;

	Mod200 validate(Mod200 mod200) throws AonSQLException;

	Mod200 delete(Mod200 mod200) throws AonSQLException;

	String dumpAEAT(Mod200 mod200) throws AonSQLException;

	
}
