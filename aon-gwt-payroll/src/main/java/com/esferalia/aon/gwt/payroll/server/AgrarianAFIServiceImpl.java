package com.esferalia.aon.gwt.payroll.server;

import com.esferalia.aon.gwt.payroll.client.AgrarianAFIService;

@SuppressWarnings("serial")
public class AgrarianAFIServiceImpl extends AonRemoteServiceServlet
		implements AgrarianAFIService {

	@Override
	public Integer getParentDomainId() {
		try {
			initFacesContext();
			return getParentDomainID();
		} finally {
			releaseFacesContext();
		}
	}

	@Override
	public Integer getDomainId() {
		try {
			initFacesContext();
			return getDomainID();
		} finally {
			releaseFacesContext();
		}
	}

	@Override
	public String getDomainName() {
		try {
			initFacesContext();
			return "REGIMEN GENERAL";
//			return getAuthPrincipal().getDomain();
		} finally {
			releaseFacesContext();
		}
	}

}
