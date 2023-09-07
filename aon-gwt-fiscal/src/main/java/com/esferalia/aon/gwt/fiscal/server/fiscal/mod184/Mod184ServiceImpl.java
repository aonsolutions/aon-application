package com.esferalia.aon.gwt.fiscal.server.fiscal.mod184;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Service;
import com.esferalia.aon.occam.api.fiscal.MODEL184;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod184 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod184" })
public class Mod184ServiceImpl extends AonStatelessRemoteServiceServlet implements Model184Service {

	private static final long serialVersionUID = 1013961629055185821L;

	public static Mod184ServiceImpl getInstance() {
		return new Mod184ServiceImpl();
	}
	
	@Override
	public Mod184 initialize(Occam occam, Integer year) {
		return MODEL184.initialize(occam, year);
	}
	
	@Override
	public LinkedList<Mod184> getMod184s(Occam occam) {
		return MODEL184.getMod184s(occam);
	}

	@Override
	public void delete(Occam occam, Mod184 mod184) {
		MODEL184.delete(occam, mod184);
	}

	@Override
	public Mod184 save(Occam occam,Mod184 mod184) {
		return MODEL184.save(occam, mod184);
	}


	@Override
	public Mod184 get(Occam occam, Integer id) {
		return MODEL184.get(occam, id);
	}

	@Override
	public Mod184 saveComments(Occam occam, Mod184 mod184) {
		return MODEL184.saveComments(occam, mod184);
	}

	@Override
	public Mod184 changeStatus(Occam occam, Mod184 mod184, FiscalStatus newStatus) throws AonCoreException {
		return MODEL184.changeStatus(occam, mod184, newStatus);
	}

	@Override
	public Mod184 duplicate(Occam occam, Mod184 mod184) {
		return MODEL184.duplicate(occam, mod184);
	}
}
