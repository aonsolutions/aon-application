package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod390 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod390" })
public class Model390ServiceImpl extends AonRemoteServiceServlet implements Model390Service {

	@Override
	public LinkedList<Mod390> getMod390s(String domainName, Integer domain) {
		return FISCAL.getMod390s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod390 initialize(String domainName, int domain, int year) {
		return FISCAL.initialize(domainName, domain, this.getUserLogin(), year);
	}

	@Override
	public Mod390 create(String domainName, int domain, Mod390 mod390) throws AonCoreException {
		return FISCAL.create(domainName, domain, this.getUserLogin(), mod390);
	}

	@Override
	public Mod390 saveComments(String domainName, Mod390 mod390) {
		return FISCAL.saveComments(domainName, mod390.getDomain(), this.getUserLogin(), mod390);
	}


}
