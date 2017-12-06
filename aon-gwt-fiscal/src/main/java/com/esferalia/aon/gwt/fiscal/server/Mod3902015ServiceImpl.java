package com.esferalia.aon.gwt.fiscal.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Mod3902015Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;

@WebServlet(name = "Mod3902015 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod3902015" })
public class Mod3902015ServiceImpl extends AonRemoteServiceServlet implements Mod3902015Service {

	private static final long serialVersionUID = -2916020705631202792L;

	@Override
	public Mod3902015 getMod3902015(String domainName, Integer domain,Mod390 mod390) {
		return FISCAL.getMod3902015(domainName, domain, this.getUserLogin(), mod390);
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
	public Mod3902015 changeStatus(String domainName, Mod3902015 mod390, FiscalStatus status) {
		return FISCAL.changeStatusMod3902015(domainName, this.getUserLogin(), mod390, status);
	}
	
	
}
