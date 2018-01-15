package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod193 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod193" })
public class Mod193ServiceImpl extends AonRemoteServiceServlet implements Model193Service {

	@Override
	public LinkedList<Mod193> getMod193s(String domainName, int domain) {
		return FISCAL.getMod193s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod193 getMod193(String domainName, int domain, Integer id) {
		return FISCAL.getMod193(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod193 initialize(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod193(domainName, domain, this.getUserLogin(), year);
	}
	
	@Override
	public void delete(String domainName, int domain, Mod193 mod193) {
		FISCAL.deleteMod193(domainName, domain, this.getUserLogin(), mod193);
	}

	@Override
	public Mod193 save(String domainName, int domain,Mod193 mod193) {
		return FISCAL.saveMod193(domainName, domain, this.getUserLogin(), mod193);
	}

	@Override
	public Mod193 saveComments(String domainName, Mod193 mod193) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod193);
	}

	@Override
	public Mod193 changeStatus(String domainName, Mod193 mod193, FiscalStatus newStatus) throws AonCoreException {
		return FISCAL.changeStatusMod193(domainName, this.getUserLogin(), mod193, newStatus);
	}

	@Override
	public Mod193 duplicateNextYear(String domainName, Integer domain, Integer id) {
		return FISCAL.duplicateNextYearMod193(domainName, domain, this.getUserLogin(), id);
	}
}
