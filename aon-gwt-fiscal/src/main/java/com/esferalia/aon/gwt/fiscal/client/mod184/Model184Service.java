package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod184")
public interface Model184Service extends RemoteService {

	LinkedList<Mod184> getMod184s(Occam occam) throws AonCoreException;
	Mod184 get(Occam occam,Integer id) throws AonCoreException;
	void delete(Occam occam,Mod184 mod184) throws AonCoreException;
	Mod184 save(Occam occam,Mod184 mod184) throws AonCoreException;
	Mod184 initialize(Occam occam, Integer year) throws AonCoreException;
	Mod184 saveComments(Occam occam, Mod184 mod184) throws AonCoreException;
	Mod184 changeStatus(Occam occam, Mod184 mod184, FiscalStatus newStatus) throws AonCoreException;
	Mod184 duplicate(Occam occam, Mod184 mod184) throws AonCoreException;
}
