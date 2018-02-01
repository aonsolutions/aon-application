package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod180 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod180" })
public class Mod180ServiceImpl extends AonStatelessRemoteServiceServlet implements Model180Service {

	private static final long serialVersionUID = 6311979458490221935L;

	@Override
	public Mod180 initializeMod180(String domainName, String user, Integer domain,Integer year) {
		return FISCAL.initializeMod180(domainName, domain, user, year);
	}

	@Override
	public LinkedList<Mod180> getMod180s(String domainName, String user, int domain) {
		return FISCAL.getMod180s(domainName, domain, user);
	}

	@Override
	public void deleteMod180(String domainName, String user, int domain, Mod180 mod180){
		FISCAL.deleteMod180(domainName, domain, user, mod180);
	}

	@Override
	public Mod180 saveMod180(String domainName, String user, int domain,Mod180 mod180) {
		return FISCAL.saveMod180(domainName, domain, user, mod180);
	}


	@Override
	public Mod180 getMod180(String domainName, String user, int domain, Integer id) {
		return FISCAL.getMod180(domainName, domain, user, id);
	}

	@Override
	public Mod180Detail getMod180Detail(String domainName, String user, int domain, Integer id) {
		return FISCAL.getMod180Detail(domainName, domain, user, id);
	}

	@Override
	public Mod180 saveCommentsMod180(String domainName, String user, Mod180 mod180) {
		return FISCAL.saveComments(domainName, user, mod180);
	}

	@Override
	public Mod180 changeStatusMod180(String domainName, String user, Mod180 mod180, FiscalStatus newStatus) throws AonCoreException {
		return FISCAL.changeStatusMod180(domainName, user, mod180, newStatus);
	}

	@Override
	public Mod180 duplicateNextYear(String domainName, String user, Integer domain, Integer id) {
		return FISCAL.duplicateNextYearMod180(domainName, domain, user, id);
	}
}
