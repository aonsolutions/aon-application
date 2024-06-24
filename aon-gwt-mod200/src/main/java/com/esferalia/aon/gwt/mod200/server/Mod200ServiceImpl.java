package com.esferalia.aon.gwt.mod200.server;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.Mod200Service;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.MODEL200;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod200 Servlet", urlPatterns = { "/aon_gwt_mod200/ms/Mod200" })
public class Mod200ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod200Service {

	private static final long serialVersionUID = -3045020929753519103L;
	
	@Override
	public LinkedList<Mod200> getMod200s(Occam occam) throws AonCoreException {
		return MODEL200.getMod200s(occam);
	}
	
	@Override
	public Mod200 getMod200(Occam occam, Integer id) throws AonCoreException {
		return MODEL200.getMod200(occam, id);
	}

	@Override
	public Mod200 saveComments(Occam occam, Mod200 mod200) throws AonCoreException {
		return MODEL200.saveComments(occam, mod200);
	}
	
}
