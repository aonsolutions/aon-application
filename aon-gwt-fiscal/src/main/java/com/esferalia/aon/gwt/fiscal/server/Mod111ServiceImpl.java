package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod111.Mod111Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod111 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod111" })
public class Mod111ServiceImpl extends AonRemoteServiceServlet implements Mod111Service {

	@Override
	public Mod111 getMod111(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod111(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod111> getMod111s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod111s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod111 calculate(String domainName, Mod111 mod111) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 save(String domainName, Mod111 mod111) {
		return FISCAL.save(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 saveComments(String domainName, Mod111 mod111) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 initializeForFinish(String domainName, Mod111 mod111) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 finish(String domainName, Mod111 mod111) {
		return FISCAL.markAsFinished(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 reopen(String domainName, Mod111 mod111) {
		return FISCAL.markAsPending(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 initialize(String domainName, int domain, Mod111 mod111) {
		return FISCAL.initializeMod111(domainName, domain, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 create(String domainName, int domain, Mod111 mod111) {
		return FISCAL.createMod111(domainName, domain, this.getUserLogin(), mod111);
	}

	@Override
	public void delete(String domainName, Mod111 mod111) {
		FISCAL.deleteMod111(domainName, this.getUserLogin(), mod111);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod111Info(domainName, domain, this.getUserLogin(), mod111, script, infoKey);
		
	}

	@Override
	public Mod111 markAsSent(String domainName, Mod111 mod111) throws AonCoreException {
		return FISCAL.markAsSent(domainName, this.getUserLogin(), mod111);
	}

}
