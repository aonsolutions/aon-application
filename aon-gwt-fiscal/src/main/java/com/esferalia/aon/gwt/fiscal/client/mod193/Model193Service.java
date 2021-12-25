package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod193")
public interface Model193Service extends RemoteService {

	LinkedList<Mod193> getMod193s(Occam occam) throws AonCoreException;
	Mod193 get(Occam occam,Integer id) throws AonCoreException;
	void delete(Occam occam,Mod193 mod193) throws AonCoreException;
	Mod193 save(Occam occam,Mod193 mod193) throws AonCoreException;
	Mod193 initialize(Occam occam, Integer year);
	Mod193 saveComments(Occam occam, Mod193 mod193) throws AonCoreException;
	Mod193 changeStatus(Occam occam, Mod193 mod193, FiscalStatus newStatus) throws AonCoreException;
	Mod193 duplicate(Occam occam, Mod193 mod193) throws AonCoreException;

}
