package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod349 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod349" })
public class Mod349ServiceImpl extends AonStatelessRemoteServiceServlet implements Model349Service {

	private static final long serialVersionUID = -5770724790111604698L;

	public static Mod349ServiceImpl getInstance() {
		return new Mod349ServiceImpl();
	}
	
	@Override
	public Mod349 initializeMod349(String domainName, String user, Integer domain) {
		return FISCAL.initializeMod349(domainName, domain, user);
	}

	@Override
	public LinkedList<Mod349> getMod349s(String domainName, String user, int domain) {
		return FISCAL.getMod349s(domainName, domain, user);
	}

	@Override
	public void deleteMod349(String domainName, String user, int domain, Mod349 mod349){
		FISCAL.deleteMod349(domainName, domain, user, mod349);
	}

	@Override
	public Mod349 saveMod349(String domainName, String user, int domain,Mod349 mod349) {
		return FISCAL.saveMod349(domainName, domain, user, mod349);
	}


	@Override
	public Mod349 getMod349(String domainName, String user, int domain, Integer id) {
		return FISCAL.getMod349(domainName, domain, user, id);
	}

	@Override
	public Mod349Detail getMod349Detail(String domainName, String user, int domain, Integer id) {
		return FISCAL.getMod349Detail(domainName, domain, user, id);
	}

	@Override
	public Mod349 saveCommentsMod349(String domainName, String user, Mod349 mod349) {
		return FISCAL.saveComments(domainName, user, mod349);
	}

	@Override
	public Mod349 changeStatusMod349(String domainName, String user, Mod349 mod349, FiscalStatus newStatus) throws AonCoreException {
		return FISCAL.changeStatusMod349(domainName, user, mod349, newStatus);
	}
	
	@Override
	public String getInfo(String domainName, String user, int domain, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return FISCAL.getMod349Info(domainName, domain, user, mod349, detail, infoKey);
	}

}
