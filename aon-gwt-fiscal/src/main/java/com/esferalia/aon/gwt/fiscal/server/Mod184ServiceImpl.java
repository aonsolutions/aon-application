package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;

@SuppressWarnings("serial")
@WebServlet(name = "Mod184 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod184" })
public class Mod184ServiceImpl extends AonRemoteServiceServlet implements Model184Service {

	@Override
	public Mod184 initializeMod184(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod184(domainName, domain, this.getUserLogin(), year);
	}
	
	@Override
	public LinkedList<Mod184> getMod184s(String domainName, int domain) {
		return FISCAL.getMod184s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod184(String domainName, int domain, Mod184 mod184) {
		FISCAL.deleteMod184(domainName, domain, this.getUserLogin(), mod184);
	}

	@Override
	public Mod184 saveMod184(String domainName, int domain,Mod184 mod184) {
		return FISCAL.saveMod184(domainName, domain, this.getUserLogin(), mod184);
	}


	@Override
	public Mod184 getMod184(String domainName, int domain, Integer id) {
		return FISCAL.getMod184(domainName, domain, this.getUserLogin(), id);
	}

}
