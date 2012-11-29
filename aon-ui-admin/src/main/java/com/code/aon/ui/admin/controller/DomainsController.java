package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainsController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainsController.class);
	
	private DataModel model;
	private String filter;
	private String modelFilter;
	
	public String getBeanName() {
		return IAdminConstants.DOMAINS_CONTROLLER_NAME;
	}	
	
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public DataModel getModel() throws ManagerBeanException {
		if ( (model == null) || (!StringUtils.equals(modelFilter, filter)) ) {
			LOGGER.info( "modelFilter: {}, filter; {}", modelFilter, filter );
			initializeModel();
		}
		return model;
	}
	
	private void initializeModel() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Criteria criteria = new Criteria();
		String descriptionAlias = bean.getFieldName(IEntityAlias.DOMAIN_DESCRIPTION);
		if (! StringUtils.isEmpty(filter) ) {
			String nameAlias = bean.getFieldName(IEntityAlias.DOMAIN_NAME);
			String text = "%" + this.filter + "%";
			Expression e1 = ExpressionUtilities.getLikeExpression(nameAlias, text);
			Expression e2 = ExpressionUtilities.getLikeExpression(descriptionAlias, text);
			criteria.addExpression( ExpressionUtilities.getOrExpression(e1, e2) );			
		}
		criteria.addOrder(descriptionAlias);
		LOGGER.info("search: {}", criteria );
		setModel(new ListDataModel(bean.getList(criteria)));
		this.modelFilter = this.filter;
	}
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public void onInit(ActionEvent event) {
		this.model = null;
		this.modelFilter = null;
		this.filter = null;
	}

	public void onSelect(ActionEvent event) throws ManagerBeanException {
		Domain domain = (Domain) getModel().getRowData();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		ds.select(domain.getId(), domain.getDescription());
		AonUtil.getRoleManager().setSysAdmin();
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		if (! ObjectUtils.equals( principal.getDomainId(), domain.getId()) )  {
			ds.setDomainManagementAvailable(true);
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			adc.enableOnlyConfig();			
		}
	}
	
}