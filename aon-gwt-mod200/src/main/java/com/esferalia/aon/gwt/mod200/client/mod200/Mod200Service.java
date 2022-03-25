package com.esferalia.aon.gwt.mod200.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod200")
public interface Mod200Service extends RemoteService {

	Mod200 getMod200(Occam occam, Integer id) throws AonCoreException;
	LinkedList<Mod200> getMod200s(Occam occam) throws AonCoreException;

}
