package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod180 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod180" })
public class Mod180ServiceImpl extends AonRemoteServiceServlet implements Model180Service {

	@Override
	public Mod180 initializeMod180(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod180(domainName, domain, this.getUserLogin(), year);
	}

	@Override
	public LinkedList<Mod180> getMod180s(String domainName, int domain) {
		return FISCAL.getMod180s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod180(String domainName, int domain, Mod180 mod180){
		FISCAL.deleteMod180(domainName, domain, this.getUserLogin(), mod180);
	}

	@Override
	public Mod180 saveMod180(String domainName, int domain,Mod180 mod180) {
		return FISCAL.saveMod180(domainName, domain, this.getUserLogin(), mod180);
	}


	@Override
	public Mod180 getMod180(String domainName, int domain, Integer id) {
		return FISCAL.getMod180(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod180Detail getMod180Detail(String domainName, int domain, Integer id) {
		return FISCAL.getMod180Detail(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod180 saveCommentsMod180(String domainName, Mod180 mod180) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod180);
	}

	@Override
	public Mod180 changeStatusMod180(String domainName, Mod180 mod180, FiscalStatus newStatus) throws AonCoreException {
		return FISCAL.changeStatusMod180(domainName, this.getUserLogin(), mod180, newStatus);
	}

	@Override
	public Mod180 duplicateNextYear(String domainName, Integer domain, Integer id) {
		return FISCAL.duplicateNextYearMod180(domainName, domain, this.getUserLogin(), id);
	}
}
