package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod202.Mod202Service;
import com.esferalia.aon.gwt.fiscal.server.util.AONMVELUtils;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod202 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod202" })
public class Mod202ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod202Service {

	private static final long serialVersionUID = -4034752509161501589L;
	
	public static Mod202ServiceImpl getInstance() {
		return new Mod202ServiceImpl();
	}
	
	@Override
	public Mod202 getMod202(String domainName,String user, int domain,int id) throws AonCoreException {
		return FISCAL.getMod202(domainName, domain, user, id);
	}

	@Override
	public LinkedList<Mod202> getMod202s(String domainName,String user, int domain) throws AonCoreException {
		return FISCAL.getMod202s(domainName, domain,user);
	}

	@Override
	public Mod202 calculate(String domainName,String user, Mod202 mod202) {
		return FISCAL.calculate(domainName,user, mod202);
	}

	@Override
	public Mod202 save(String domainName,String user, Mod202 mod202) {
		return FISCAL.save(domainName,user, mod202);
	}

	@Override
	public Mod202 initialize(String domainName,String user, int domain, Mod202 mod202) {
		return FISCAL.initializeMod202(domainName, domain,user, mod202);
	}

	@Override
	public void delete(String domainName,String user, Mod202 mod202) {
		FISCAL.deleteMod202(domainName,user, mod202);
	}
	@Override
	public Mod202 saveComments(String domainName,String user, Mod202 mod202) {
		return FISCAL.saveComments(domainName, user, mod202);
	}

	@Override
	public Mod202 initializeForFinish(String domainName,String user, Mod202 mod202) {
		return FISCAL.initializeForFinish(domainName, user, mod202);
	}

	@Override
	public Mod202 markAsFinished(String domainName,String user, Mod202 mod202) {
		return FISCAL.markAsFinished(domainName, user, mod202);
	}

	@Override
	public Mod202 markAsSent(String domainName,String user, Mod202 mod202) {
		return FISCAL.markAsSent(domainName, user, mod202);
	}
	@Override
	public Mod202 markAsCustomerCheck(String domainName, String user, Mod202 mod202) throws AonCoreException {
		return FISCAL.markAsCustomerCheck(domainName, user, mod202);
	}

	@Override
	public Mod202 markAsPending(String domainName,String user, Mod202 mod202) {
		return FISCAL.markAsPending(domainName, user, mod202);
	}

	@Override
	public Mod202 create(String domainName,String user, int domain, Mod202 mod202) {
		return FISCAL.createMod202(domainName, domain, user, mod202);
	}
	@Override
	public String getInfo(String domainName,String user, int domain, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod202Info(domainName, domain, user, mod202, script, infoKey);
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
