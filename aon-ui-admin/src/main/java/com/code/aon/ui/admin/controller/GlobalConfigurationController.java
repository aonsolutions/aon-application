package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.Serializable;

import jakarta.el.ExpressionFactory;
import jakarta.el.MethodExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.MethodExpressionActionListener;

import org.ajax4jsf.component.html.HtmlAjaxOutputPanel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.enumeration.Module;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Company;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class GlobalConfigurationController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalConfigurationController.class);
	
	private static final String TEMPLATE_PREFFIX = "/com/code/aon/ui/admin/facelet/globalConfig/templates/";
	
	private static final String SELECTED_STYLE_CLASS = "aon-panelBar-selected";
	
	private static final String GENERAL_ID = "general";
	
	private static final String COMPANY_ID = "company";
	
	private static final String ON_SELECT_OPTION = "onSelectOption";

	private String template;
	
	private String currentOption;
	
	private String selectedPanel;
	
	private boolean entrepiseSelected;
	
	public void onInit( ActionEvent event ) {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
		controller.onLoad(event);
		setCurrentOption(COMPANY_ID);
		setSelectedPanel(GENERAL_ID);
		this.entrepiseSelected = false;
	}
	
	public void onSelectOption( ActionEvent event ) {
		setCurrentOption(event.getComponent().getId());
	}
	
	private void addActionListener( UICommand command, String action ) {
		FacesContext ctx = FacesContext.getCurrentInstance();
        ExpressionFactory f = ctx.getApplication().getExpressionFactory();
        MethodExpression me = f.createMethodExpression(ctx.getELContext(),
        		"#{" + IAdminConstants.GLOBAL_CONFIG_CONTROLLER_NAME + "." + action + "}", null, FaceletUtil.ACTION_LISTENER_SIG);
        ActionListener listener = new MethodExpressionActionListener(me);
        command.addActionListener(listener);
	}	
	
	public void highlightSelectedOption( UIComponent component, UIComponent parent ) {
		if ( component.isRendered() ) {
			if (component.getId().equals(currentOption) && HtmlAjaxOutputPanel.class.isAssignableFrom(parent.getClass()) ) {
				HtmlAjaxOutputPanel panel = (HtmlAjaxOutputPanel) parent;
				String style = StringUtils.defaultString(panel.getStyleClass());
				panel.setStyleClass(style + " " + SELECTED_STYLE_CLASS);
			}
			if ( UICommand.class.isAssignableFrom(component.getClass()) ) {
				addActionListener((UICommand) component, ON_SELECT_OPTION);
			}
		}
	}	
	
	public void onInitEnterprise( ActionEvent event ) {
		if (! entrepiseSelected) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
			Company company = companyController.obtainCompany();
			EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
			try {
				controller.select(event, company.getId());
				this.entrepiseSelected = true;
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	public void onInitDomain( ActionEvent event ) {
		DomainController controller = (DomainController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_CONTROLLER_NAME);
		try {
			if ( controller.getTo() == null ) {
				controller.select(event, DomainManager.getCurrentDomain());	
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
	}
	
	public String getTemplate() {
		return template;
	}

	private void setTemplate(String template) {
		this.template = template;
	}

	public String getCurrentOption() {
		return currentOption;
	}

	public void setCurrentOption(String currentOption) {
		this.currentOption = currentOption;
		setTemplate(TEMPLATE_PREFFIX+currentOption+".xhtml");
	}

	public String getSelectedPanel() {
		return selectedPanel;
	}

	public void setSelectedPanel(String selectedPanel) {
		this.selectedPanel = selectedPanel;
	}


	public boolean isPortalRegistered() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Integer domainId = ds.getParentDomainId();
		if ( domainId != null ) {
			Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
			try {
				return AuditManager.hasModule(domainId, appId, Module.PAYROLL_PORTAL);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}								
		}
		return false;
	}
	
}
