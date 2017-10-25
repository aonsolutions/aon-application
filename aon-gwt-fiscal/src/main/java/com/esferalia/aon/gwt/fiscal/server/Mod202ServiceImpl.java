package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod202.Mod202Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod202 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod202" })
public class Mod202ServiceImpl extends AonRemoteServiceServlet implements Mod202Service {

	@Override
	public Mod202 getMod202(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod202(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod202> getMod202s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod202s(domainName, domain,this.getUserLogin());
	}

	@Override
	public Mod202 calculate(String domainName, Mod202 mod202) {
		return FISCAL.calculate(domainName,this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 save(String domainName, Mod202 mod202) {
		return FISCAL.save(domainName,this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 initialize(String domainName, int domain, Mod202 mod202) {
		return FISCAL.initializeMod202(domainName, domain,this.getUserLogin(), mod202);
	}

	@Override
	public void delete(String domainName, Mod202 mod202) {
		FISCAL.deleteMod202(domainName,this.getUserLogin(), mod202);
	}
	@Override
	public Mod202 saveComments(String domainName, Mod202 mod202) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 initializeForFinish(String domainName, Mod202 mod202) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 markAsFinished(String domainName, Mod202 mod202) {
		return FISCAL.markAsFinished(domainName, this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 markAsSent(String domainName, Mod202 mod202) {
		return FISCAL.markAsSent(domainName, this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 markAsPending(String domainName, Mod202 mod202) {
		return FISCAL.markAsPending(domainName, this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 create(String domainName, int domain, Mod202 mod202) {
		return FISCAL.createMod202(domainName, domain, this.getUserLogin(), mod202);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod202Info(domainName, domain, this.getUserLogin(), mod202, script, infoKey);
		
	}

}
