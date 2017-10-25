package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod131.Mod131Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod131 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod131" })
public class Mod131ServiceImpl extends AonRemoteServiceServlet implements Mod131Service {

	@Override
	public Mod131 getMod131(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod131(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod131> getMod131s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod131s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod131 calculate(String domainName, Mod131 mod131) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod131);
	}
	@Override
	public Mod131Activity calculateActivity(String domainName,int domain, Mod131Activity activity) throws AonCoreException {
		return FISCAL.calculate(domainName, domain, this.getUserLogin(), activity);
	}

	@Override
	public Mod131 save(String domainName, Mod131 mod131) {
		return FISCAL.save(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 saveComments(String domainName, Mod131 mod131) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 initializeForFinish(String domainName, Mod131 mod131) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 markAsFinished(String domainName, Mod131 mod131) {
		return FISCAL.finish(domainName, this.getUserLogin(), mod131);
	}
	@Override
	public Mod131 markAsSent(String domainName, Mod131 mod131) {
		return FISCAL.markAsSent(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 markAsPending(String domainName, Mod131 mod131) {
		return FISCAL.reopen(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 initialize(String domainName, int domain, Mod131 mod131) {
		return FISCAL.initializeMod131(domainName, domain, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 create(String domainName, int domain, Mod131 mod131) {
		return FISCAL.createMod131(domainName, domain, this.getUserLogin(), mod131);
	}

	@Override
	public void delete(String domainName, Mod131 mod131) {
		FISCAL.deleteMod131(domainName, this.getUserLogin(), mod131);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod131Info(domainName, domain, this.getUserLogin(), mod131, script, infoKey);
		
	}
	
}
