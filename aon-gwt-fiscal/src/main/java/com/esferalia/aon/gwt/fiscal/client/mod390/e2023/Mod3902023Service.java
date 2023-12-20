package com.esferalia.aon.gwt.fiscal.client.mod390.e2023;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902023;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod3902023")
public interface Mod3902023Service extends RemoteService {

	Mod3902023 get(Occam occam,Mod390 mod390) throws AonCoreException;
	Mod3902023 save(Occam occam,Mod3902023 mod390) throws AonCoreException;
	void delete(Occam occam, Mod3902023 mod390) throws AonCoreException;
	Mod3902023 changeStatus(Occam occam, Mod3902023 mod390, FiscalStatus status) throws AonCoreException;

}
