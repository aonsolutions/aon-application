package com.esferalia.aon.gwt.fiscal.client.mod369;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod369")
public interface Model369Service extends RemoteService {

	LinkedList<Mod369> getMod369s(Occam occam) throws AonCoreException;
	Mod369 get(Occam occam,Integer id) throws AonCoreException;
	void delete(Occam occam,Mod369 mod369) throws AonCoreException;
	Mod369 save(Occam occam,Mod369 mod369) throws AonCoreException;
	Mod369 initialize(Occam occam, Integer year, Period period) throws AonCoreException;
	Mod369 saveComments(Occam occam, Mod369 mod369) throws AonCoreException;
	Mod369 changeStatus(Occam occam, Mod369 mod369, FiscalStatus newStatus) throws AonCoreException;
	Mod369 duplicate(Occam occam, Mod369 mod369) throws AonCoreException;
}
