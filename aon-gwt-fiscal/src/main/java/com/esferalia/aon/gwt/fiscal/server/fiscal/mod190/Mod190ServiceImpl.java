package com.esferalia.aon.gwt.fiscal.server.fiscal.mod190;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190Service;
import com.esferalia.aon.occam.api.fiscal.MODEL190;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod190 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod190", "/aon_gwt_mod200/ms/Mod190" })
public class Mod190ServiceImpl extends AonStatelessRemoteServiceServlet implements Model190Service {

	private static final long serialVersionUID = -313529134373496651L;

	public static Mod190ServiceImpl getInstance() {
		return new Mod190ServiceImpl();
	}
	
	@Override
	public LinkedList<Mod190> getMod190s(Occam occam) {
		return MODEL190.getMod190s(occam);
	}
	
	@Override
	public Mod190 getMod190(Occam occam, Integer id) {
		return MODEL190.get(occam, id);
	}
	
	@Override
	public Mod190 initialize(Occam occam,Integer year) {
		return MODEL190.initialize(occam, year);
	}

	@Override
	public void delete(Occam occam, Mod190 mod190) {
		MODEL190.delete(occam, mod190);
	}

	@Override
	public Mod190 save(Occam occam,Mod190 mod190) {
		return MODEL190.save(occam, mod190);
	}

	@Override
	public Mod190Detail getDetail(Occam occam, Integer id) {
		return MODEL190.getDetail(occam, id);
	}

	@Override
	public Mod190 saveComments(Occam occam, Mod190 mod190) {
		return MODEL190.saveComments(occam, mod190);
	}

	@Override
	public Mod190 changeStatus(Occam occam, Mod190 mod190, FiscalStatus newStatus) throws AonCoreException {
		return MODEL190.changeStatus(occam, mod190, newStatus);
	}

	@Override
	public Mod190 duplicate(Occam occam, Mod190 mod190) throws AonCoreException {
		return MODEL190.duplicate(occam, mod190);
	}
	@Override
	public LinkedList<Mod190Detail> validateSalaries(Occam occam, Mod190 mod190) throws AonCoreException {
		return MODEL190.validateSalaries(occam, mod190);
	}
}
