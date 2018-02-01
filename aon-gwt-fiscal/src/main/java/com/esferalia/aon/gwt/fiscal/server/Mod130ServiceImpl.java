package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod130.Mod130Service;
import com.esferalia.aon.gwt.fiscal.server.util.AONMVELUtils;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod130 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod130" })
public class Mod130ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod130Service {

	private static final long serialVersionUID = 1L;
	
	@Override
	public Mod130 getMod130(String domainName, String user, int domain,int id) throws AonCoreException {
		return FISCAL.getMod130(domainName, domain, user , id);
	}

	@Override
	public LinkedList<Mod130> getMod130s(String domainName, String user, int domain) throws AonCoreException {
		return FISCAL.getMod130s(domainName, domain, user);
	}

	@Override
	public Mod130 calculate(String domainName, String user, Mod130 mod130) {
		return FISCAL.calculate(domainName, user, mod130);
	}

	@Override
	public Mod130 save(String domainName, String user, Mod130 mod130) {
		return FISCAL.save(domainName, user, mod130);
	}

	@Override
	public Mod130 saveComments(String domainName, String user, Mod130 mod130) {
		return FISCAL.saveComments(domainName, user, mod130);
	}

	@Override
	public Mod130 initializeForFinish(String domainName, String user, Mod130 mod130) {
		return FISCAL.initializeForFinish(domainName, user, mod130);
	}

	@Override
	public Mod130 markAsFinished(String domainName, String user, Mod130 mod130) {
		return FISCAL.markAsFinished(domainName, user, mod130);
	}

	@Override
	public Mod130 markAsSent(String domainName, String user, Mod130 mod130) {
		return FISCAL.markAsSent(domainName, user, mod130);
	}

	@Override
	public Mod130 markAsPending(String domainName, String user, Mod130 mod130) {
		return FISCAL.markAsPending(domainName, user, mod130);
	}

	@Override
	public Mod130 initialize(String domainName, String user, int domain, Mod130 mod130) {
		return FISCAL.initializeMod130(domainName, domain, user, mod130);
	}

	@Override
	public Mod130 create(String domainName, String user, int domain, Mod130 mod130) {
		return FISCAL.createMod130(domainName, domain, user, mod130);
	}

	@Override
	public void delete(String domainName, String user, Mod130 mod130) {
		FISCAL.deleteMod130(domainName, user, mod130);
	}
	@Override
	public String getInfo(String domainName, String user, int domain, Mod130 mod130
			, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException {
		return FISCAL.getMod130Info(domainName, domain, user, mod130, script, infoKey);
		
	}
	
	@Override
	public Double mathExpression(String expression) throws AonCoreException {
		try {
			return AONMVELUtils.mathExpression(expression);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		}
	}
}
