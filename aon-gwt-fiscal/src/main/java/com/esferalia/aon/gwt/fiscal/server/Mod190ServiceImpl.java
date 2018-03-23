package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod190 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod190" })
public class Mod190ServiceImpl extends AonStatelessRemoteServiceServlet implements Model190Service {

	private static final long serialVersionUID = -313529134373496651L;

	public static Mod190ServiceImpl getInstance() {
		return new Mod190ServiceImpl();
	}
	
	@Override
	public LinkedList<Mod190> getMod190s(String domainName, String user, int domain) {
		return FISCAL.getMod190s(domainName, domain, user);
	}
	
	@Override
	public Mod190 getMod190(String domainName, String user, int domain, Integer id) {
		return FISCAL.getMod190(domainName, domain, user, id);
	}
	
	@Override
	public Mod190 initialize(String domainName, String user, Integer domain,Integer year) {
		return FISCAL.initializeMod190(domainName, domain, user, year);
	}

	@Override
	public void delete(String domainName, String user, int domain, Mod190 mod190) {
		FISCAL.deleteMod190(domainName, domain, user, mod190);
	}

	@Override
	public Mod190 save(String domainName, String user, int domain,Mod190 mod190) {
		return FISCAL.saveMod190(domainName, domain, user, mod190);
	}

	@Override
	public Mod190Detail getDetail(String domainName, String user, int domain, Integer id) {
		return FISCAL.getMod190Detail(domainName, domain, user, id);
	}

	@Override
	public Mod190 saveComments(String domainName, String user, Mod190 mod190) {
		return FISCAL.saveComments(domainName, user, mod190);
	}

	@Override
	public Mod190 changeStatus(String domainName, String user, Mod190 mod190, FiscalStatus newStatus) throws AonCoreException {
		return FISCAL.changeStatusMod190(domainName, user, mod190, newStatus);
	}

	@Override
	public Mod190 duplicateNextYear(String domainName, String user, Integer domain, Integer id) throws AonCoreException {
		return FISCAL.duplicateNextYearMod190(domainName, domain, user, id);
	}
}
