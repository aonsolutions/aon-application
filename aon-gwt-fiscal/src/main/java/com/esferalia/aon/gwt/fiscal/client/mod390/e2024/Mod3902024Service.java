package com.esferalia.aon.gwt.fiscal.client.mod390.e2024;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902024;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod3902024")
public interface Mod3902024Service extends RemoteService {

	Mod3902024 get(Occam occam,Mod390 mod390) throws AonCoreException;
	Mod3902024 save(Occam occam,Mod3902024 mod390) throws AonCoreException;
	void delete(Occam occam, Mod3902024 mod390) throws AonCoreException;
	Mod3902024 changeStatus(Occam occam, Mod3902024 mod390, FiscalStatus status) throws AonCoreException;

}
