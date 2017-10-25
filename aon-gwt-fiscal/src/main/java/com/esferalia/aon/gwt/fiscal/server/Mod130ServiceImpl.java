package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod130.Mod130Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod130 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod130" })
public class Mod130ServiceImpl extends AonRemoteServiceServlet implements Mod130Service {

	@Override
	public Mod130 getMod130(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod130(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod130> getMod130s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod130s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod130 calculate(String domainName, Mod130 mod130) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 save(String domainName, Mod130 mod130) {
		return FISCAL.save(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 saveComments(String domainName, Mod130 mod130) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 initializeForFinish(String domainName, Mod130 mod130) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 markAsFinished(String domainName, Mod130 mod130) {
		return FISCAL.markAsFinished(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 markAsSent(String domainName, Mod130 mod130) {
		return FISCAL.markAsSent(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 markAsPending(String domainName, Mod130 mod130) {
		return FISCAL.markAsPending(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 initialize(String domainName, int domain, Mod130 mod130) {
		return FISCAL.initializeMod130(domainName, domain, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 create(String domainName, int domain, Mod130 mod130) {
		return FISCAL.createMod130(domainName, domain, this.getUserLogin(), mod130);
	}

	@Override
	public void delete(String domainName, Mod130 mod130) {
		FISCAL.deleteMod130(domainName, this.getUserLogin(), mod130);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod130Info(domainName, domain, this.getUserLogin(), mod130, script, infoKey);
		
	}
}
