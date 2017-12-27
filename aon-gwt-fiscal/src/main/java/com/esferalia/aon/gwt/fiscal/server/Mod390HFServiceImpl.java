package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Mod390HFService;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod390HF Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod390HF" })
public class Mod390HFServiceImpl extends AonRemoteServiceServlet implements Mod390HFService {

	// ---------------------------------------------------------------MODELO 303
	@Override
	public Mod390HF getMod390HF(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod390HF(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod390HF> getMod390HFs(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod390HFs(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod390HF calculate(String domainName, Mod390HF mod303) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod390HF save(String domainName, Mod390HF mod303) {
		return FISCAL.save(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod390HF saveComments(String domainName, Mod390HF mod303) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod390HF initializeForFinish(String domainName, Mod390HF mod303) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod390HF markAsFinished(String domainName, Mod390HF mod303) {
		return FISCAL.finish(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod390HF markAsPending(String domainName, Mod390HF mod303) {
		return FISCAL.reopen(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod390HF initialize(String domainName, int domain, Mod390HF mod303) {
		return FISCAL.initializeMod390HF(domainName, domain, this.getUserLogin(), mod303);
	}

	@Override
	public Mod390HF create(String domainName, int domain, Mod390HF mod303) {
		return FISCAL.createMod390HF(domainName, domain, this.getUserLogin(), mod303);
	}
	@Override
	public Mod390HF declarationChanged(String domainName, int domain, Mod390HF mod303) throws AonCoreException {
		return FISCAL.declarationChanged(domainName, domain, this.getUserLogin(), mod303);
	}
	@Override
	public void delete(String domainName, Mod390HF mod303) {
		FISCAL.deleteMod390HF(domainName, this.getUserLogin(), mod303);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod390HF mod303, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod390HFInfo(domainName, domain, this.getUserLogin(), mod303, script, infoKey);
		
	}

	@Override
	public Mod390HF markAsSent(String domainName, Mod390HF mod303) throws AonCoreException {
		return FISCAL.markAsSent(domainName, mod303, this.getUserLogin());
	}

}
