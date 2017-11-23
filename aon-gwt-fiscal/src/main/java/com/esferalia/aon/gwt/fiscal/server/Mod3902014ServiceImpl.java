package com.esferalia.aon.gwt.fiscal.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2014.Mod3902014Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;

@SuppressWarnings("serial")
@WebServlet(name = "Mod3902014 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod3902014" })
public class Mod3902014ServiceImpl extends AonRemoteServiceServlet implements Mod3902014Service {

	@Override
	public Mod3902014 getMod3902014(String domainName, Integer domain,Integer id) {
		return FISCAL.getMod3902014(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod3902014 saveMod3902014(String domainName, Integer domain, Mod3902014 mod390) {
		return FISCAL.saveMod3902014(domainName, domain, this.getUserLogin(), mod390);
	}

	@Override
	public void deleteMod3902014(String domainName, Integer domain, Mod3902014 mod390) {
		FISCAL.deleteMod3902014(domainName, domain, this.getUserLogin(), mod390);
	}
	
	@Override
	public Mod3902014 initializeMod3902014(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod3902014(domainName, domain, this.getUserLogin(), year);
	}

}
