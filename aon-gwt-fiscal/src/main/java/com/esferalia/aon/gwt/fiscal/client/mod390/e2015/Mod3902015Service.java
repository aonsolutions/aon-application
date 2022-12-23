package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod3902015")
public interface Mod3902015Service extends RemoteService {

	Mod3902015 get(Occam occam, Mod390 mod390) throws AonCoreException;
	Mod3902015 save(Occam occam,Mod3902015 mod390) throws AonCoreException;
	void delete(Occam occam,Mod3902015 mod390) throws AonCoreException;
	Mod3902015 changeStatus(Occam occam, Mod3902015 mod390, FiscalStatus status) throws AonCoreException;

}
