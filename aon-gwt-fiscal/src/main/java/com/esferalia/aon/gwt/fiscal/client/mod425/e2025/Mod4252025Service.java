package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod4252025")
public interface Mod4252025Service extends RemoteService {

	Mod4252025 get(Occam occam, Mod390 mod425) throws AonCoreException;
	Mod4252025 save(Occam occam, Mod4252025 mod425) throws AonCoreException;
	void delete(Occam occam, Mod4252025 mod425) throws AonCoreException;
	Mod4252025 changeStatus(Occam occam, Mod4252025 mod425, FiscalStatus status) throws AonCoreException;

}
