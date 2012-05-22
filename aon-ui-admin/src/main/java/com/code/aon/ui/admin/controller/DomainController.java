package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainController.class);
	
	public final static int DEFAULT_MAX_DOCUMENT_SIZE = 1;
	
	public final static int DEFAULT_MAX_TOTAL_DOCUMENT_SIZE = 100;	
	
	private List<SelectItem> parentDomains;
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	public Domain getDomain() {
		return (Domain) getTo();
	}	
	
	public Domain getParentDomain() {
		Domain parent = getDomain().getParent();
		if ( (parent != null) && (parent.getId() != null) ) {
			return parent;
		}		
		return null;
	}

	public void onInit( ActionEvent event ) {
		getAdmin().resetTermsOfServiceAccepted();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateParentDomains() {
		this.parentDomains = new LinkedList<SelectItem>();
		try {
			Criteria criteria = new Criteria();
			String domainManagement = getFieldName(IEntityAlias.DOMAIN_DOMAIN_MANAGEMENT);
			Expression expr1 = ExpressionUtilities.getEqualExpression(domainManagement, Boolean.TRUE);
			Expression expr2 = ExpressionUtilities.getNullExpression("Domain.parent");
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			List<Domain> list = (List) getManagerBean().getList(criteria);
			for (Domain domain : list) {
				SelectItem item = new SelectItem(domain, domain.getName() );
				this.parentDomains.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}	
		
	public List<SelectItem> getAllParentDomains() {
		return parentDomains;
	}

	public List<SelectItem> getParentDomains() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( SelectItem item : parentDomains ) {
			if (! getDomain().equals(item.getValue()) ) {
				list.add(item);
			}
		}
		return list;
	}
	
	public boolean isShowDomainSubDomainSuffix() {
		if ( getDomain().isDomainManagement() || getAdmin().isSysAdmin() ) {
			return true;
		}
		return false;
	}

	public void numberOfUsersCheck(FacesContext context, UIComponent component, Object value) {
	}		
	
	public void documentManagementChanged( ValueChangeEvent event ) {
		boolean newValue = (Boolean) event.getNewValue();
		if (! newValue ) {
			getDomain().setMaxDocumentSize(DEFAULT_MAX_DOCUMENT_SIZE);
			getDomain().setMaxTotalDocumentSize(DEFAULT_MAX_TOTAL_DOCUMENT_SIZE);
		}
	}
	
}