package com.esferalia.aon.gwt.fiscal.server.fiscal.mod349;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349Service;
import com.esferalia.aon.occam.api.fiscal.MODEL349;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod349 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod349", "/aon_gwt_mod200/ms/Mod349" })
public class Mod349ServiceImpl extends AonStatelessRemoteServiceServlet implements Model349Service {

	private static final long serialVersionUID = -5770724790111604698L;

	public static Mod349ServiceImpl getInstance() {
		return new Mod349ServiceImpl();
	}
	
	@Override
	public Mod349 initialize(Occam occam, int year, Period period) {
		return MODEL349.initialize(occam, year, period);
	}

	@Override
	public LinkedList<Mod349> getMod349s(Occam occam) {
		return MODEL349.getMod349s(occam);
	}

	@Override
	public void delete(Occam occam, Mod349 mod349){
		MODEL349.delete(occam, mod349);
	}

	@Override
	public Mod349 save(Occam occam,Mod349 mod349) {
		return MODEL349.save(occam, mod349);
	}

	@Override
	public Mod349 reset(Occam occam,Mod349 mod349) {
		return MODEL349.reset(occam, mod349);
	}

	@Override
	public Mod349 get(Occam occam, Integer id) {
		return MODEL349.get(occam, id);
	}

	@Override
	public Mod349Detail getDetail(Occam occam, Integer id) {
		return MODEL349.getDetail(occam, id);
	}

	@Override
	public Mod349 saveComments(Occam occam, Mod349 mod349) {
		return MODEL349.saveComments(occam, mod349);
	}

	@Override
	public Mod349 changeStatus(Occam occam, Mod349 mod349, FiscalStatus newStatus) throws AonCoreException {
		return MODEL349.changeStatus(occam, mod349, newStatus);
	}
	
	@Override
	public String getInfo(Occam occam, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return MODEL349.getInfo(occam, mod349, detail, infoKey);
	}
	
	@Override
	public Mod349 duplicate(Occam occam, Mod349 mod349) {
		return MODEL349.duplicate(occam, mod349);
	}

}
