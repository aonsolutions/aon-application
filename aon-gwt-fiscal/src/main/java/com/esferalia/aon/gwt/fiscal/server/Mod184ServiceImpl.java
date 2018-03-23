package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Service;
import com.esferalia.aon.occam.api.FISCAL;
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
	public Mod184 initializeMod184(String domainName, String user, Integer domain,Integer year) {
		return FISCAL.initializeMod184(domainName, domain, user, year);
	}
	
	@Override
	public LinkedList<Mod184> getMod184s(String domainName, String user, int domain) {
		return FISCAL.getMod184s(domainName, domain, user);
	}

	@Override
	public void deleteMod184(String domainName, String user, int domain, Mod184 mod184) {
		FISCAL.deleteMod184(domainName, domain, user, mod184);
	}

	@Override
	public Mod184 saveMod184(String domainName, String user, int domain,Mod184 mod184) {
		return FISCAL.saveMod184(domainName, domain, user, mod184);
	}


	@Override
	public Mod184 getMod184(String domainName, String user, int domain, Integer id) {
		return FISCAL.getMod184(domainName, domain, user, id);
	}

	@Override
	public Mod184 saveCommentsMod184(String domainName, String user, Mod184 mod184) {
		return FISCAL.saveComments(domainName, user, mod184);
	}

	@Override
	public Mod184 changeStatusMod184(String domainName, String user, Mod184 mod184, FiscalStatus newStatus) throws AonCoreException {
		return FISCAL.changeStatusMod184(domainName, user, mod184, newStatus);
	}

	@Override
	public Mod184 duplicateNextYear(String domainName, String user, Integer domain, Integer id) {
		return FISCAL.duplicateNextYearMod184(domainName, domain, user, id);
	}
}
