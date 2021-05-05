package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Mod390HFService;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod390HF Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod390HF" })
public class Mod390HFServiceImpl extends AonStatelessRemoteServiceServlet implements Mod390HFService {

	private static final long serialVersionUID = 11373213973954273L;

	public static Mod390HFServiceImpl getInstance() {
		return new Mod390HFServiceImpl();
	}

	// ---------------------------------------------------------------MODELO 303
	@Override
	public Mod390HF getMod390HF(String domainName, int domain, String user, int id) throws AonCoreException {
		return FISCAL.getMod390HF(domainName, domain, user, id);
	}

	@Override
	public LinkedList<Mod390HF> getMod390HFs(String domainName, int domain,String user) throws AonCoreException {
		return FISCAL.getMod390HFs(domainName, domain, user);
	}

	@Override
	public Mod390HF calculate(String domainName,String user, Mod390HF mod303) {
		return FISCAL.calculate(domainName, user, mod303);
	}

	@Override
	public Mod390HF save(String domainName,String user, Mod390HF mod303) {
		return FISCAL.save(domainName, user, mod303);
	}

	@Override
	public Mod390HF saveComments(String domainName,String user, Mod390HF mod303) {
		return FISCAL.saveComments(domainName, user, mod303);
	}

	@Override
	public Mod390HF initializeForFinish(String domainName,String user, Mod390HF mod303) {
		return FISCAL.initializeForFinish(domainName, user, mod303);
	}

	@Override
	public Mod390HF markAsFinished(String domainName,String user, Mod390HF mod303) {
		return FISCAL.finish(domainName, user, mod303);
	}

	@Override
	public Mod390HF markAsPending(String domainName,String user, Mod390HF mod303) {
		return FISCAL.reopen(domainName, user, mod303);
	}

	@Override
	public Mod390HF initialize(String domainName, int domain,String user, Mod390HF mod303) {
		return FISCAL.initializeMod390HF(domainName, domain, user, mod303);
	}

	@Override
	public Mod390HF create(String domainName, int domain,String user, Mod390HF mod303) {
		return FISCAL.createMod390HF(domainName, domain, user, mod303);
	}

	@Override
	public Mod390HF declarationChanged(String domainName, int domain,String user, Mod390HF mod303) throws AonCoreException {
		return FISCAL.declarationChanged(domainName, domain, user, mod303);
	}

	@Override
	public void delete(String domainName,String user, Mod390HF mod303) {
		FISCAL.deleteMod390HF(domainName, user, mod303);
	}

	@Override
	public String getInfo(String domainName, int domain,String user, Mod390HF mod303, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return FISCAL.getMod390HFInfo(domainName, domain, user, mod303, script, infoKey);

	}

	@Override
	public Mod390HF markAsSent(String domainName,String user, Mod390HF mod303) throws AonCoreException {
		return FISCAL.markAsSent(domainName, mod303, user);
	}

}
