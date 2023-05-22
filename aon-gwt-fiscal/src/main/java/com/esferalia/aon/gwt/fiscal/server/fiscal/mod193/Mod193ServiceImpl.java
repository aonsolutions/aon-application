package com.esferalia.aon.gwt.fiscal.server.fiscal.mod193;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193Service;
import com.esferalia.aon.occam.api.fiscal.MODEL193;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod193 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod193" })
public class Mod193ServiceImpl extends AonStatelessRemoteServiceServlet implements Model193Service {

	private static final long serialVersionUID = -5473472061391413070L;

	public static Mod193ServiceImpl getInstance() {
		return new Mod193ServiceImpl();
	}

	@Override
	public LinkedList<Mod193> getMod193s(Occam occam) {
		return MODEL193.getMod193s(occam);
	}

	@Override
	public Mod193 get(Occam occam, Integer id) {
		return MODEL193.get(occam, id);
	}

	@Override
	public Mod193 initialize(Occam occam,Integer year) {
		return MODEL193.initialize(occam, year);
	}
	
	@Override
	public void delete(Occam occam, Mod193 mod193) {
		MODEL193.delete(occam, mod193);
	}

	@Override
	public Mod193 save(Occam occam,Mod193 mod193) {
		return MODEL193.save(occam, mod193);
	}

	@Override
	public Mod193 saveComments(Occam occam, Mod193 mod193) {
		return MODEL193.saveComments(occam, mod193);
	}

	@Override
	public Mod193 changeStatus(Occam occam, Mod193 mod193, FiscalStatus newStatus) throws AonCoreException {
		return MODEL193.changeStatus(occam, mod193, newStatus);
	}

	@Override
	public Mod193 duplicate(Occam occam, Mod193 mod193) {
		return MODEL193.duplicate(occam, mod193);
	}
}
