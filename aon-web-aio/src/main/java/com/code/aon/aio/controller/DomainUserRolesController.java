package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.User;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DomainUserRolesController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String token;
	private DomainUserRoles dur;
	
	public DomainUserRoles getDur() {
		if(dur == null) {
			loadDomainUserRoles();
		}
		return dur;
	}
	
	public void setDur(DomainUserRoles dur) {
		this.dur = dur;
	}

	public boolean isAdmin() {
		return getDur().isAdmin();
	}
	
	public boolean isAlpha() {
		return getDur().isAlpha();
	}
	
	public boolean isBeta() {
		return getDur().isBeta();
	}
	
	public boolean isInvoice() {
		return getDur().isInvoice();
	}
	
	public boolean isTimecontrol() {
		return getDur().isTimecontrol();
	}
	
	public boolean isMessenger() {
		return getDur().isMessenger();
	}
	
	public boolean isPayroll() {
		return getDur().isPayroll();
	}
	
	public boolean isAccounting() {
		return getDur().isAccounting();
	}
	
	public boolean isFiscal() {
		return getDur().isFiscal();
	}

	public boolean isDocumental() {
		return getDur().isDocumental();
	}
	
	public boolean isCommercial() {
		return getDur().isCommercial();
	}
	
	public boolean isWarehouse() {
		return getDur().isWarehouse();
	}
	
	public boolean isBank() {
		return getDur().isBank();
	}
	
	public boolean isAula() {
		return getDur().hasApp(AonApp.AULA);
	}
	
	public boolean isInvofox() {
		return getDur().isInvofox();
	}

	public String getToken() {
		if(token == null && getDur().getUser().getAuth().isEmpty()) {
			token = AonToken.build(getDur().getUser(), AonDateUtils.addDays(new Date(), 1), AonUtil.getDomainName());
		} else if(token == null) {
			token = AonToken.build(getDur().getUser().getAuth(), AonDateUtils.addDays(new Date(), 1));
		}
		return token;
	}
	
	private void loadDomainUserRoles() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		User user = UserUtils.getInstance().getLoggedUser();
		this.dur = SECURITY.getDomainUserRoles(domainName, domainId, user.getLogin(), user.getId());			
		this.token = AonToken.build(getDur().getUser().getAuth(),
			AonDateUtils.addDays(new Date(), 1));
	}
	
	
}
