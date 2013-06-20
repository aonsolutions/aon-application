package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.NEW_DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.REMOVE_DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.CONFIGURATION_CATEGORY;
import static com.code.aon.ui.audit.controller.IAuditConstants.ENTERPRISE_CATEGORY;
import static com.code.aon.ui.audit.controller.IAuditConstants.GROUP_CONFIG_COMPANY;
import static com.code.aon.ui.audit.controller.IAuditConstants.GROUP_CONFIG_SECURITY;
import static com.code.aon.ui.audit.controller.IAuditConstants.GROUP_ENTERPRISE_SECURITY;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainsController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainsController.class);
	
	private String filter;
	private String modelFilter;
	private boolean allDomains;

	public void onChangeFilter( ActionEvent event ) {
		if ( (!StringUtils.equals(modelFilter, filter)) ) {
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
		if ( (domain.getParent() != null) && (domain.getParent().getId() != null) ) {
			ds.setParentDomain(domain.getParent().getId());
		} else {
			ds.setParentDomain(domain.getId());
		}
		ds.setDomainManagementAvailable(true);
		setConfigurationMenu();		
	}
	
	private void setConfigurationMenu() {
		AonUtil.getRoleManager().setSysAdmin();
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		String[] categories = new String[]{ENTERPRISE_CATEGORY, CONFIGURATION_CATEGORY};
		String[] groups = new String[]{GROUP_ENTERPRISE_SECURITY, GROUP_CONFIG_SECURITY, GROUP_CONFIG_COMPANY};
		adc.enableOnly(categories, groups);					
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

	private boolean isAdminDomain() throws ManagerBeanException {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Domain domain = (Domain) bean.get(principal.getDomainId());
		return ( domain.getType() == DomainType.ADMIN );
	}
	
	public void onSelectChildDomain( ActionEvent event) throws ManagerBeanException {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		if ( ds.getModel().isRowAvailable() ) {
			Domain domain = (Domain) ds.getModel().getRowData();
			ds.select(domain.getId(), domain.getDescription());
			if ( isAdminDomain() ) {
				setConfigurationMenu();
			}
		}
	}

	public void onParentDomain(ActionEvent event) throws ManagerBeanException {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		ds.select(ds.getParentDomain(), null);
		if ( isAdminDomain() ) {
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