package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod390")
public interface Model390Service extends RemoteService {

	Mod390 getMod390(Occam occam, Integer id) throws AonCoreException;
	LinkedList<Mod390> getMod390s(Occam occam) throws AonCoreException;
//	Mod390 create(Occam occam, Mod390 mod390) throws AonCoreException;
	Mod390 initialize(Occam occam, int year);
	Mod390 saveComments(Occam occam, Mod390 mod390);
	void delete(Occam occam, Mod390 mod390) throws AonCoreException;
	
}
