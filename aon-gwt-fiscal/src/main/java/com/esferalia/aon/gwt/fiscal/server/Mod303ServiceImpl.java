package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod303.Mod303Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod303 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod303" })
public class Mod303ServiceImpl extends AonRemoteServiceServlet implements Mod303Service {

	// ---------------------------------------------------------------MODELO 303
	@Override
	public Mod303 getMod303(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod303(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod303> getMod303s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod303s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod303 calculateMod303(String domainName, Mod303 mod303) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod303 saveMod303(String domainName, Mod303 mod303) {
		return FISCAL.save(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod303 saveCommentsMod303(String domainName, Mod303 mod303) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod303 initializeForFinishMod303(String domainName, Mod303 mod303) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod303 finishMod303(String domainName, Mod303 mod303) {
		return FISCAL.finish(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod303 reopenMod303(String domainName, Mod303 mod303) {
		return FISCAL.reopen(domainName, this.getUserLogin(), mod303);
	}

	@Override
	public Mod303 initializeMod303(String domainName, int domain, Mod303 mod303) {
		return FISCAL.initializeMod303(domainName, domain, this.getUserLogin(), mod303);
	}

	@Override
	public Mod303 createMod303(String domainName, int domain, Mod303 mod303) {
		return FISCAL.createMod303(domainName, domain, this.getUserLogin(), mod303);
	}
	@Override
	public Mod303 declarationChanged(String domainName, int domain, Mod303 mod303) throws AonCoreException {
		return FISCAL.declarationChanged(domainName, domain, this.getUserLogin(), mod303);
	}
	@Override
	public void deleteMod303(String domainName, Mod303 mod303) {
		FISCAL.deleteMod303(domainName, this.getUserLogin(), mod303);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod303Info(domainName, domain, this.getUserLogin(), mod303, script, infoKey);
		
	}

	@Override
	public void importMod303(String domainName, int domain) throws AonCoreException {
		FISCAL.importMod303(domainName, domain, this.getUserLogin());
	}

}
