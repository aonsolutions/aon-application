package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod347 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod347" })
public class Mod347ServiceImpl extends AonRemoteServiceServlet implements Model347Service {

	@Override
	public Mod347 initializeMod347(String domainName, Integer domain) {
		return FISCAL.initializeMod347(domainName, domain, this.getUserLogin());
	}

	@Override
	public LinkedList<Mod347> getMod347s(String domainName, int domain) {
		return FISCAL.getMod347s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod347(String domainName, int domain, Mod347 mod347){
		FISCAL.deleteMod347(domainName, domain, this.getUserLogin(), mod347);
	}

	@Override
	public Mod347 saveMod347(String domainName, int domain,Mod347 mod347) {
		return FISCAL.saveMod347(domainName, domain, this.getUserLogin(), mod347);
	}


	@Override
	public Mod347 getMod347(String domainName, int domain, Integer id) {
		return FISCAL.getMod347(domainName, domain, this.getUserLogin(), id);
	}

//	@Override
//	public Mod347Detail getMod347Detail(String domainName, int domain, Integer id) {
//		return FISCAL.getMod347Detail(domainName, domain, this.getUserLogin(), id);
//	}

	@Override
	public Mod347 saveCommentsMod347(String domainName, Mod347 mod347) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod347);
	}

	@Override
	public Mod347 changeStatusMod347(String domainName, Mod347 mod347, FiscalStatus newStatus) throws AonCoreException {
		return FISCAL.changeStatusMod347(domainName, this.getUserLogin(), mod347, newStatus);
	}
	
	@Override
	public String getInfo(String domainName, int domain, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod347Info(domainName, domain, this.getUserLogin(), mod347, declared, infoKey);
		
	}

}
