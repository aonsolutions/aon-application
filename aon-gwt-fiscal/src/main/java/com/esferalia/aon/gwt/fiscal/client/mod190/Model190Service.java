package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod190")
public interface Model190Service extends RemoteService {
	
	LinkedList<Mod190> getMod190s(Occam occam) throws AonCoreException;
	Mod190 getMod190(Occam occam,Integer id) throws AonCoreException;
	Mod190Detail getDetail(Occam occam,Integer id) throws AonCoreException;
	void delete(Occam occam,Mod190 mod190) throws AonCoreException;
	Mod190 save(Occam occam,Mod190 mod190) throws AonCoreException;
	Mod190 initialize(Occam occam, Integer year);
	Mod190 saveComments(Occam occam, Mod190 mod190) throws AonCoreException;
	Mod190 changeStatus(Occam occam, Mod190 mod190, FiscalStatus newStatus) throws AonCoreException;
	Mod190 duplicate(Occam occam, Mod190 mod190) throws AonCoreException;
	LinkedList<Mod190Detail> validateSalaries(Occam occam, Mod190 mod190) throws AonCoreException;

}
