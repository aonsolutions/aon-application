package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod180")
public interface Model180Service extends RemoteService {

	LinkedList<Mod180> getMod180s(Occam occam) throws AonCoreException;
	Mod180 get(Occam occam,Integer id) throws AonCoreException;
	Mod180 save(Occam occam,Mod180 mod180) throws AonCoreException;
	void delete(Occam occam,Mod180 mod180) throws AonCoreException;
	Mod180Detail getDetail(Occam occam,Integer id) throws AonCoreException;
	Mod180 initialize(Occam occam, Integer year) throws AonCoreException;
	Mod180 saveComments(Occam occam, Mod180 mod180) throws AonCoreException;
	Mod180 changeStatus(Occam occam, Mod180 mod180, FiscalStatus newStatus) throws AonCoreException;
	Mod180 duplicate(Occam occam, Mod180 mod180) throws AonCoreException;
	
}
