package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.NEW_DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.REMOVE_DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.CONTRACT_SWITCHER;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.net.IDN;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.config.ContractData;
import com.code.aon.ui.config.DomainData;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.ContractSwitcher;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainsController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainsController.class);
	
	private String filter;
	private String modelFilter;
	private boolean allDomains;

	public void onChangeFilter( ActionEvent event ) {
		if ( !StringUtils.equals(modelFilter, filter) ) {
			updateModel();
			this.modelFilter = this.filter;
		}
	}
	
	private void updateModel() {
		try {
			OrderByList orderByList = getCriteria().getOrderByList();
			clearCriteria();
			Criteria criteria = getCriteria();
			String descriptionAlias = getFieldName(IEntityAlias.DOMAIN_DESCRIPTION);
			if (! StringUtils.isEmpty(filter) ) {
				String nameAlias = getFieldName(IEntityAlias.DOMAIN_NAME);
				String text = "%" + this.filter + "%";
				Expression e1 = ExpressionUtilities.getLikeExpression(nameAlias, text);
				Expression e2 = ExpressionUtilities.getLikeExpression(descriptionAlias, text);
				criteria.addExpression( ExpressionUtilities.getOrExpression(e1, e2) );			
			}
			criteria.setOrderByList(orderByList);
			initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}		
	}
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@Override
	public void onSelect(ActionEvent event) {
		selectDomain( (Domain) getSelectedTO() );
	}
	
	public void selectDomain( Domain domain ) {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		ds.select(domain.getId(), domain.getDescription());
		Domain parent = domain.getParent();
		if ( parent != null && parent.getId() != null ) {
			ds.setParentDomain(parent.getId());
		} else {
			ds.setParentDomain(domain.getId());
		}
		setConfigurationMenu();		
	}
	
	private void setConfigurationMenu() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(IAuditConstants.ACTION_DENIED_CONTROLLER_NAME);
		String[] categories = null;
		String[] groups = null;
		if ( AonUtil.getRoleManager().isAdmin() ) {
			categories = new String[]{IAuditConstants.ENTERPRISE_CATEGORY, IAuditConstants.CONFIGURATION_CATEGORY};
			groups = new String[]{IAuditConstants.GROUP_ENTERPRISE_SECURITY, IAuditConstants.GROUP_CONFIG_SECURITY, IAuditConstants.GROUP_CONFIG_COMPANY};
		} else {
			categories = new String[]{IAuditConstants.CONFIGURATION_CATEGORY};
			groups = new String[]{IAuditConstants.GROUP_CONFIG_SECURITY, IAuditConstants.GROUP_CONFIG_COMPANY};			
		}
		AonUtil.getRoleManager().setSysAdmin();			
		adc.getManager().enableOnly(categories, groups);					
	}

	public int getCurrentDomainChildNumber() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(getFieldName(IEntityAlias.DOMAIN_PARENT_ID), domain.getId());
			return getManagerBean().getCount(criteria);
		}
		return 0;
	}

	public String getCurrentDomainURL() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Domain domain = (Domain) getSelectedTO();
			String name = IDN.toASCII(domain.getName());
			return name;
		}
		return null;
	}
	
	public void onNewDomain( ActionEvent event) throws ManagerBeanException {
		NewDomainController ndc = (NewDomainController) AonUtil.getRegisteredBean(NEW_DOMAIN_CONTROLLER_NAME);
		Domain domain = (Domain) getManagerBean().get(DomainManager.getCurrentDomain());
		ndc.reset( domain, null );
		setModel(null);
	}
	
	public void onRemoveDomain( ActionEvent event) throws ManagerBeanException {
		RemoveDomainController rdc = (RemoveDomainController) AonUtil.getRegisteredBean(REMOVE_DOMAIN_CONTROLLER_NAME);
		rdc.reset( (Domain) getSelectedTO() );
		rdc.setDomainDisabled(true);
		setModel(null);
	}
	
	public void onSelectChildDomain( ActionEvent event) throws ManagerBeanException {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		if ( ds.getModel().isRowAvailable() ) {
			DomainData domainData = (DomainData) ds.getModel().getRowData();
			ds.select(domainData.getId(), domainData.getDescription());
			if ( ds.isAdminDomain() ) {
				setConfigurationMenu();
			}
		}
	}

	public void onSelectContractDomain( ActionEvent event) throws ManagerBeanException {
		ContractSwitcher contractSwitcher = (ContractSwitcher) AonUtil.getRegisteredBean(CONTRACT_SWITCHER);
		if ( contractSwitcher.getModel().isRowAvailable() ) {
			ContractData contractData = (ContractData) contractSwitcher.getModel().getRowData();
			DomainSwitcher domainSwitcher = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);

			domainSwitcher.select(contractData.getDomainId(), contractData.getDomainDescription());
			if ( domainSwitcher.isAdminDomain() ) {
				setConfigurationMenu();
			}
		}
	}

	public void onParentDomain(ActionEvent event) throws ManagerBeanException {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		ds.select(ds.getParentDomain(), null);
		if ( ds.isAdminDomain() ) {
			setConfigurationMenu();
		}
	}

	public boolean isAllDomains() {
		return allDomains;
	}

	public void setAllDomains(boolean allDomains) {
		this.allDomains = allDomains;
	}
	
	public void onChangeAllDomains( ActionEvent event ) {
		changeInitExpressions(allDomains);
		updateModel();
	}
	
	private void changeInitExpressions( boolean allDomains ) {
		List<Expression> expressions = new LinkedList<Expression>();
		try {
			String type = getFieldName(IEntityAlias.DOMAIN_TYPE);
			expressions.add(ExpressionUtilities.getNotEqualExpression(type, DomainType.ADMIN));
			if (! allDomains ) {
				expressions.add(ExpressionUtilities.getNullExpression("Domain.parent"));
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		setInitExpressions(expressions);
	}
	
}