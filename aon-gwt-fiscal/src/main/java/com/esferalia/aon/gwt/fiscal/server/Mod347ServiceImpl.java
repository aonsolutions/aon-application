package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod347 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod347" })
public class Mod347ServiceImpl extends AonStatelessRemoteServiceServlet implements Model347Service {

	public static Mod347ServiceImpl getInstance() {
		return new Mod347ServiceImpl();
	}

	@Override
	public Mod347 initializeMod347(String domainName, String user, Integer domain) {
		return FISCAL.initializeMod347(domainName, domain, user);
	}

	@Override
	public LinkedList<Mod347> getMod347s(String domainName, String user, int domain) {
		return FISCAL.getMod347s(domainName, domain, user);
	}

	@Override
	public void deleteMod347(String domainName, String user, int domain, Mod347 mod347){
		FISCAL.deleteMod347(domainName, domain, user, mod347);
	}

	@Override
	public Mod347 saveMod347(String domainName, String user, int domain,Mod347 mod347) {
		return FISCAL.saveMod347(domainName, domain, user, mod347);
	}


	@Override
	public Mod347 getMod347(String domainName, String user, int domain, Integer id) {
		return FISCAL.getMod347(domainName, domain, user, id);
	}

	@Override
	public Mod347 saveCommentsMod347(String domainName, String user, Mod347 mod347) {
		return FISCAL.saveComments(domainName, user, mod347);
	}

	@Override
	public Mod347 changeStatusMod347(String domainName, String user, Mod347 mod347, FiscalStatus newStatus) throws AonCoreException {
		return FISCAL.changeStatusMod347(domainName, user, mod347, newStatus);
	}
	
	@Override
	public String getInfo(String domainName, String user, int domain, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod347Info(domainName, domain, user, mod347, declared, infoKey);
		
	}
	
	@Override
	public Mod347 duplicateNextYear(String domainName, String user, Integer domain, Integer id) {
		return FISCAL.duplicateNextYearMod347(domainName, domain, user, id);
	}

}
