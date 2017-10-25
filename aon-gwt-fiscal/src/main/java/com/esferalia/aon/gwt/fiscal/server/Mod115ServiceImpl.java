package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod115.Mod115Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod115 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod115" })
public class Mod115ServiceImpl extends AonRemoteServiceServlet implements Mod115Service {
	
	@Override
	public Mod115 getMod115(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod115(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod115> getMod115s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod115s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod115 calculate(String domainName, Mod115 mod115) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 save(String domainName, Mod115 mod115) {
		return FISCAL.save(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 saveComments(String domainName, Mod115 mod115) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 initializeForFinish(String domainName, Mod115 mod115) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 markAsFinished(String domainName, Mod115 mod115) {
		return FISCAL.markAsFinished(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 markAsSent(String domainName, Mod115 mod115) {
		return FISCAL.markAsSent(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 markAsPending(String domainName, Mod115 mod115) {
		return FISCAL.markAsPending(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 initialize(String domainName, int domain, Mod115 mod115) {
		return FISCAL.initializeMod115(domainName, domain, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 create(String domainName, int domain, Mod115 mod115) {
		return FISCAL.createMod115(domainName, domain, this.getUserLogin(), mod115);
	}

	@Override
	public void delete(String domainName, Mod115 mod115) {
		FISCAL.deleteMod115(domainName, this.getUserLogin(), mod115);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod115Info(domainName, domain, this.getUserLogin(), mod115, script, infoKey);
		
	}

}
