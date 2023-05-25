package com.esferalia.aon.gwt.fiscal.server.fiscal.mod202;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod202.Mod202Service;
import com.esferalia.aon.occam.api.fiscal.MODEL202;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod202 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod202" })
public class Mod202ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod202Service {

	private static final long serialVersionUID = -4034752509161501589L;
	
	public static Mod202ServiceImpl getInstance() {
		return new Mod202ServiceImpl();
	}
	
	@Override
	public Mod202 getMod202(Occam occam,int id) throws AonCoreException {
		return MODEL202.getMod202(occam, id);
	}

	@Override
	public LinkedList<Mod202> getMod202s(Occam occam) throws AonCoreException {
		return MODEL202.getMod202s(occam);
	}

	@Override
	public Mod202 calculate(Occam occam, Mod202 mod202) {
		return MODEL202.calculate(occam, mod202);
	}

	@Override
	public Mod202 save(Occam occam, Mod202 mod202) {
		return MODEL202.save(occam, mod202);
	}

	@Override
	public Mod202 initialize(Occam occam, Mod202 mod202) {
		return MODEL202.initializeMod202(occam, mod202);
	}

	@Override
	public void delete(Occam occam, Mod202 mod202) {
		MODEL202.deleteMod202(occam, mod202);
	}
	@Override
	public Mod202 saveComments(Occam occam, Mod202 mod202) {
		return MODEL202.saveComments(occam, mod202);
	}

	@Override
	public Mod202 initializeForFinish(Occam occam, Mod202 mod202) {
		return MODEL202.initializeForFinish(occam, mod202);
	}

	@Override
	public Mod202 markAsFinished(Occam occam, Mod202 mod202) {
		return MODEL202.markAsFinished(occam, mod202);
	}

	@Override
	public Mod202 markAsSent(Occam occam, Mod202 mod202) {
		return MODEL202.markAsSent(occam, mod202);
	}
	@Override
	public Mod202 markAsCustomerCheck(Occam occam, Mod202 mod202) throws AonCoreException {
		return MODEL202.markAsCustomerCheck(occam, mod202);
	}

	@Override
	public Mod202 markAsPending(Occam occam, Mod202 mod202) {
		return MODEL202.markAsPending(occam, mod202);
	}

	@Override
	public Mod202 create(Occam occam, Mod202 mod202) {
		return MODEL202.createMod202(occam, mod202);
	}
	@Override
	public Mod202 reset(Occam occam, Mod202 mod202) {
		return MODEL202.resetMod202(occam, mod202);
	}
	@Override
	public String getInfo(Occam occam, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return MODEL202.getMod202Info(occam, mod202, script, infoKey);
	}
	
}
