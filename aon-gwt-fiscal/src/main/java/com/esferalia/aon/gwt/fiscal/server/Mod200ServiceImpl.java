package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod200.Mod200Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Mod200 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod200" })
public class Mod200ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod200Service {

	private static final long serialVersionUID = -3045020929753519103L;
	
	@Override
	public LinkedList<Mod200> getMod200s(String domainName,int domain,String user) throws AonCoreException {
		return FISCAL.getMod200s(domainName, domain,user);
	}
	
	@Override
	public Mod200 getMod200(String domainName,int domain,String user,Integer id) throws AonCoreException {
		return FISCAL.getMod200(domainName, domain,user,id);
	}
}
