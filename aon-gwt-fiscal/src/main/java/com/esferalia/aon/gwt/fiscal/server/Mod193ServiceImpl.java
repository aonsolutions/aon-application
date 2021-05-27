package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193Service;
import com.esferalia.aon.occam.api.FISCAL;
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
	public LinkedList<Mod193> getMod193s(String domainName, String user, int domain) {
		return FISCAL.getMod193s(domainName, domain, user);
	}

	@Override
	public Mod193 getMod193(String domainName, String user, int domain, Integer id) {
		return FISCAL.getMod193(domainName, domain, user, id);
	}

	@Override
	public Mod193 initialize(String domainName, String user, Integer domain,Integer year) {
		return FISCAL.initializeMod193(domainName, domain, user, year);
	}
	
	@Override
	public void delete(String domainName, String user, int domain, Mod193 mod193) {
		FISCAL.deleteMod193(domainName, domain, user, mod193);
	}

	@Override
	public Mod193 save(String domainName, String user, int domain,Mod193 mod193) {
		return FISCAL.saveMod193(domainName, domain, user, mod193);
	}

	@Override
	public Mod193 saveComments(String domainName, String user, Mod193 mod193) {
		return FISCAL.saveComments(domainName, user, mod193);
	}

	@Override
	public Mod193 changeStatus(String domainName, String user, Mod193 mod193, FiscalStatus newStatus) throws AonCoreException {
		return FISCAL.changeStatusMod193(domainName, user, mod193, newStatus);
	}

	@Override
	public Mod193 duplicateNextYear(String domainName, String user, Integer domain, Integer id) {
		return FISCAL.duplicateNextYearMod193(domainName, domain, user, id);
	}
}
