package com.esferalia.aon.gwt.fiscal.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Mod3902015Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;

@SuppressWarnings("serial")
@WebServlet(name = "Mod3902015 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod3902015" })
public class Mod3902015ServiceImpl extends AonRemoteServiceServlet implements Mod3902015Service {

	@Override
	public Mod3902015 getMod3902015(String domainName, Integer domain,Integer id) {
		return FISCAL.getMod3902015(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod3902015 saveMod3902015(String domainName, Integer domain, Mod3902015 mod390) {
		return FISCAL.saveMod3902015(domainName, domain, this.getUserLogin(), mod390);
	}

	@Override
	public void deleteMod3902015(String domainName, Integer domain, Mod3902015 mod390) {
		FISCAL.deleteMod3902015(domainName, domain, this.getUserLogin(), mod390);
	}
	
	@Override
	public Mod3902015 initializeMod3902015(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod3902015(domainName, domain, this.getUserLogin(), year);
	}
	
}
