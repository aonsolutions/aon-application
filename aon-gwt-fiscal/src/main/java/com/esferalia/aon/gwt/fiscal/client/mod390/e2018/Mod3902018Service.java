package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod3902018")
public interface Mod3902018Service extends RemoteService {

	Mod3902018 get(Occam occam,Mod390 mod390) throws AonCoreException;
	Mod3902018 save(Occam occam,Mod3902018 mod390) throws AonCoreException;
	void delete(Occam occam, Mod3902018 mod390) throws AonCoreException;
	Mod3902018 changeStatus(Occam occam, Mod3902018 mod390, FiscalStatus status) throws AonCoreException;

}
