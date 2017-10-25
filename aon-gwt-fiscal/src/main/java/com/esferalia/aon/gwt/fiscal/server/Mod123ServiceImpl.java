package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod123.Mod123Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod123 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod123" })
public class Mod123ServiceImpl extends AonRemoteServiceServlet implements Mod123Service {

	@Override
	public Mod123 getMod123(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod123(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod123> getMod123s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod123s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod123 calculate(String domainName, Mod123 mod123) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 save(String domainName, Mod123 mod123) {
		return FISCAL.save(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 saveComments(String domainName, Mod123 mod123) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 initializeForFinish(String domainName, Mod123 mod123) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 markAsFinished(String domainName, Mod123 mod123) {
		return FISCAL.markAsFinished(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 markAsSent(String domainName, Mod123 mod123) {
		return FISCAL.markAsSent(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 markAsPending(String domainName, Mod123 mod123) {
		return FISCAL.markAsPending(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 initialize(String domainName, int domain, Mod123 mod123) {
		return FISCAL.initializeMod123(domainName, domain, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 create(String domainName, int domain, Mod123 mod123) {
		return FISCAL.createMod123(domainName, domain, this.getUserLogin(), mod123);
	}

	@Override
	public void delete(String domainName, Mod123 mod123) {
		FISCAL.deleteMod123(domainName, this.getUserLogin(), mod123);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod123Info(domainName, domain, this.getUserLogin(), mod123, script, infoKey);
		
	}

}
