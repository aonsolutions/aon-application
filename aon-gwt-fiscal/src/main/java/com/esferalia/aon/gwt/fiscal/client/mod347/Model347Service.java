package com.esferalia.aon.gwt.fiscal.client.mod347;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod347")
public interface Model347Service extends RemoteService {
	
	LinkedList<Mod347> getMod347s(Occam occam) throws AonCoreException;
	Mod347 get(Occam occam,Integer id) throws AonCoreException;
	Mod347 initialize(Occam occam, int year) throws AonCoreException;
	Mod347 reset(Occam occam, Mod347 model) throws AonCoreException;
	Mod347 save(Occam occam,Mod347 mod347) throws AonCoreException;
	void delete(Occam occam,Mod347 mod347) throws AonCoreException;
	Mod347 saveComments(Occam occam, Mod347 mod347) throws AonCoreException;
	Mod347 changeStatus(Occam occam, Mod347 mod347, FiscalStatus newStatus) throws AonCoreException;
	String getInfo(Occam occam,Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Mod347 duplicate(Occam occam, Mod347 mod347) throws AonCoreException;
	
}
