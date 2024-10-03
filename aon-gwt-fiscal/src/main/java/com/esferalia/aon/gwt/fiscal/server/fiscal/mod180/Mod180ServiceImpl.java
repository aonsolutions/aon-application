package com.esferalia.aon.gwt.fiscal.server.fiscal.mod180;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180Service;
import com.esferalia.aon.occam.api.fiscal.MODEL180;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod180 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod180", "/aon_gwt_mod200/ms/Mod180" })
public class Mod180ServiceImpl extends AonStatelessRemoteServiceServlet implements Model180Service {

	private static final long serialVersionUID = 6311979458490221935L;

	public static Mod180ServiceImpl getInstance() {
		return new Mod180ServiceImpl();
	}
	
	@Override
	public Mod180 initialize(Occam occam,Integer year) {
		return MODEL180.initialize(occam, year);
	}

	@Override
	public LinkedList<Mod180> getMod180s(Occam occam) {
		return MODEL180.getMod180s(occam);
	}

	@Override
	public void delete(Occam occam, Mod180 mod180){
		MODEL180.delete(occam, mod180);
	}

	@Override
	public Mod180 save(Occam occam,Mod180 mod180) {
		return MODEL180.save(occam, mod180);
	}


	@Override
	public Mod180 get(Occam occam, Integer id) {
		return MODEL180.get(occam, id);
	}

	@Override
	public Mod180Detail getDetail(Occam occam, Integer id) {
		return MODEL180.getDetail(occam, id);
	}

	@Override
	public Mod180 saveComments(Occam occam, Mod180 mod180) {
		return MODEL180.saveComments(occam, mod180);
	}

	@Override
	public Mod180 changeStatus(Occam occam, Mod180 mod180, FiscalStatus newStatus) throws AonCoreException {
		return MODEL180.changeStatus(occam, mod180, newStatus);
	}

	@Override
	public Mod180 duplicate(Occam occam, Mod180 mod180) {
		return MODEL180.duplicate(occam, mod180);
	}
}
