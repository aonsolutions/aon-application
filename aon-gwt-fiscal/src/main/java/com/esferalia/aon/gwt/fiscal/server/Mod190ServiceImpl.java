package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod190 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod190" })
public class Mod190ServiceImpl extends AonRemoteServiceServlet implements Model190Service {

	private static final long serialVersionUID = -313529134373496651L;

	@Override
	public LinkedList<Mod190> getMod190s(String domainName, int domain) {
		return FISCAL.getMod190s(domainName, domain, this.getUserLogin());
	}
	
	@Override
	public Mod190 getMod190(String domainName, int domain, Integer id) {
		return FISCAL.getMod190(domainName, domain, this.getUserLogin(), id);
	}
	
	@Override
	public Mod190 initialize(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod190(domainName, domain, this.getUserLogin(), year);
	}

	@Override
	public void delete(String domainName, int domain, Mod190 mod190) {
		FISCAL.deleteMod190(domainName, domain, this.getUserLogin(), mod190);
	}

	@Override
	public Mod190 save(String domainName, int domain,Mod190 mod190) {
		return FISCAL.saveMod190(domainName, domain, this.getUserLogin(), mod190);
	}

	@Override
	public Mod190Detail getDetail(String domainName, int domain, Integer id) {
		return FISCAL.getMod190Detail(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod190 saveComments(String domainName, Mod190 mod190) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod190);
	}

	@Override
	public Mod190 changeStatus(String domainName, Mod190 mod190, FiscalStatus newStatus) throws AonCoreException {
		return FISCAL.changeStatusMod190(domainName, this.getUserLogin(), mod190, newStatus);
	}

	@Override
	public Mod190 duplicateNextYear(String domainName, Integer domain, Integer id) throws AonCoreException {
		return FISCAL.duplicateNextYearMod190(domainName, domain, this.getUserLogin(), id);
	}
}
