package com.code.aon.aio.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.CONTRACT_SWITCHER;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.admin.controller.DomainsController;
import com.code.aon.ui.admin.controller.IAdminConstants;
import com.code.aon.ui.config.controller.ContractSwitcher;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class SearchController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;


	private String filter; 
	private String beanName;
	
	
	public String getFilter() {
		return filter;
	}
	
	public void onEditSearch(ActionEvent event) {
		setFilter(null);
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setFilter(String filter) {
		this.filter = filter;
		getDomainSwitcher().setFilter(filter);
		getContractSwitcher().setFilter(filter);
	}
	
	public DomainSwitcher getDomainSwitcher() {
		return (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
	}
	
	public DomainsController getDomainsController() {
		return (DomainsController) AonUtil.getRegisteredBean(IAdminConstants.DOMAINS_CONTROLLER_NAME);
	}
	
	public ContractSwitcher getContractSwitcher() {
		return (ContractSwitcher) AonUtil.getRegisteredBean(CONTRACT_SWITCHER);
	}
	
	public void onSelectChildDomain( ActionEvent event) throws ManagerBeanException {
		getDomainsController().onSelectChildDomain(event);
		resetFilter();
	}
	
	public void onSelectContractDomain( ActionEvent event) throws ManagerBeanException {
		getDomainsController().onSelectContractDomain(event);
		resetFilter();
	}
	
	private void resetFilter() {
		filter = null;
	}
	
}
