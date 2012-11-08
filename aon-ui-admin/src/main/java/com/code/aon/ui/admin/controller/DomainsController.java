package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.config.Domain;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DomainsController extends BasicController {

	private String filter;
	private String modelFilter;
	private DataModel filteredModel;
	
	public DataModel getModel() {
		if (model == null) {
			initializeModel();
		}
		if (StringUtils.isBlank(getFilter())) {
			return model;
		} else {
			if (!StringUtils.equals(modelFilter, filter) || filteredModel == null) {
				List<Domain> filteredList = new LinkedList<Domain>();
				@SuppressWarnings("unchecked")
				List<Domain> list = (List<Domain>) model.getWrappedData();
				for (Domain d :  list) {
					if (StringUtils.containsIgnoreCase(d.getName(), getFilter()) ||
						StringUtils.containsIgnoreCase(d.getDescription(), getFilter())) {
						filteredList.add(d);					
					}
				}
				filteredModel = new ListDataModel(filteredList);
				modelFilter = filter;
			}
			return filteredModel;
		}
	}
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void onSelect(ActionEvent event){
		Domain domain = (Domain) getModel().getRowData();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		ds.select(domain.getId(), domain.getDescription());
		AonUtil.getRoleManager().setSysAdmin();
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		if (! ObjectUtils.equals( principal.getDomainId(), domain.getId()) )  {
			ds.setDomainManagementAvailable(true);
			ds.setParentDomain(true);
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			adc.enableOnlyConfig();			
		}
	}
	
}