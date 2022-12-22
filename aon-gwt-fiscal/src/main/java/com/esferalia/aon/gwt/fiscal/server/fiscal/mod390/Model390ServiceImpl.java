package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390Service;
import com.esferalia.aon.occam.api.fiscal.MODEL390;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod390 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod390" })
public class Model390ServiceImpl extends AonStatelessRemoteServiceServlet implements Model390Service {

	private static final long serialVersionUID = 5103358252992734363L;

	@Override
	public Mod390 getMod390(Occam occam, Integer id) {
		return MODEL390.getMod390(occam, id);
	}

	@Override
	public LinkedList<Mod390> getMod390s(Occam occam) {
		return MODEL390.getMod390s(occam);
	}

	@Override
	public Mod390 initialize(Occam occam, int year) {
		return MODEL390.initialize(occam, year);
	}

//	@Override
//	public Mod390 create(Occam occam, Mod390 mod390) throws AonCoreException {
//		return MODEL390.create(occam, mod390);
//	}

	@Override
	public Mod390 saveComments(Occam occam, Mod390 mod390) {
		return MODEL390.saveComments(occam, mod390);
	}
	
	@Override
	public void delete(Occam occam, Mod390 mod390) {
		MODEL390.deleteMod390(occam, mod390);
	}

}
