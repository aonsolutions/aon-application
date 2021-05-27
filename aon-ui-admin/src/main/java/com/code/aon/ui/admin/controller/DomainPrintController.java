package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAINS_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_BOOKING_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_PRINT_CONTROLLER_NAME;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.DomainPrintInfo;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainPrintController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainPrintController.class);

	private IControllerListener domainFilter;
	
	private DomainPrintInfo info;
	
	@Override
	public void onSearch(ActionEvent event) {
		try {
			clearCriteria();
			this.info = null;
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		super.onSearch(event);		
	}
	
	private DomainPrintInfo getCurrentInfo() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			if ( (this.info == null) || !domain.equals(this.info.getDomain()) ) {
				this.info = new DomainPrintInfo(domain);
			}
			return this.info;
		}		
		return null;
	}

	public int getCurrentDomainChildNumber() throws ManagerBeanException {
		DomainPrintInfo info = getCurrentInfo();
		if ( info != null ) {
			return info.getChildNumber();
		}
		return 0;
	}

	public int getCurrentDomainUserNumber() throws ManagerBeanException {
		DomainPrintInfo info = getCurrentInfo();
		if ( info != null ) {
			return info.getDomainUserNumber();
		}
		return 0;
	}
	
	public boolean isCurrentDomainOne() throws ManagerBeanException {
		DomainPrintInfo info = getCurrentInfo();
		if ( info != null ) {
			return info.isDomainOne();
		}
		return false;
	}

	public String getCurrentDomainModuleList() throws ManagerBeanException {
		DomainPrintInfo info = getCurrentInfo();
		if ( info != null ) {
			return info.getDomainModuleList();
		}
		return null;
	}

	public String getCurrentModifications() throws ManagerBeanException {
		DomainPrintInfo info = getCurrentInfo();
		if ( info != null ) {
			return info.getModifications();
		}
		return null;
	}	

	public String getPayer() throws ManagerBeanException {
		DomainPrintInfo info = getCurrentInfo();
		if ( info != null ) {
			return info.getPayer();
		}
		return null;				
	}
	
	public int getCurrentDomainUsedSpaceInMB() throws ManagerBeanException {
		DomainPrintInfo info = getCurrentInfo();
		if ( info != null ) {
			return info.getDomainUsedSpaceInMB();
		}
		return 0;
	}	
	
	public String getCurrentDomainURL() throws ManagerBeanException {
		DomainPrintInfo info = getCurrentInfo();
		if ( info != null ) {
			return info.getDomainURL();
		}
		return null;
	}	

	@Override
	public void onSelect(ActionEvent event) {
		DomainsController controller = (DomainsController) AonUtil.getRegisteredBean(DOMAINS_CONTROLLER_NAME);
		controller.selectDomain( (Domain) getSelectedTO() );
	}

	public void onGoToDomainHistory(ActionEvent event) throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			Integer companyId = AdminUtil.getCompanyId(domain.getId());
			DomainController controller = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
			controller.initHistory(companyId);
		}
	}

	public void onGoToDomainBooking(ActionEvent event) throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			if ( domain.getParent() != null ) {
				domain = domain.getParent();
			}
			DomainBookingController dbc = (DomainBookingController) AonUtil.getRegisteredBean(DOMAIN_BOOKING_CONTROLLER_NAME);
			dbc.init(domain);
			dbc.setBackAction(DOMAIN_PRINT_CONTROLLER_NAME + IController.SEARCH_SUFFIX);
		}
	}
	
	public IControllerListener getDomainFilter() {
		if ( this.domainFilter == null ) {
			this.domainFilter = new DomainFilter();
		}
		return this.domainFilter;
	}	
	
	private static class DomainFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {					
				Criteria criteria = controller.getCriteria();
				criteria.setSkipDomainFilter(true);
				criteria.addNotEqualExpression(controller.getFieldName(IEntityAlias.DOMAIN_TYPE), DomainType.ADMIN);
				criteria.addEqualExpression(controller.getFieldName(IEntityAlias.DOMAIN_DOMAIN_MANAGEMENT), Boolean.TRUE);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering domain", e);
			}
		}
		
	}
	
}