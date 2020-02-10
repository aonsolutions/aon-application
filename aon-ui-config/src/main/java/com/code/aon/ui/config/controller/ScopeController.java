package com.code.aon.ui.config.controller;

import java.io.Serializable;
import java.util.stream.Stream;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.security.Scope;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

public class ScopeController extends BasicController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(ScopeController.class);
	
	@Override
	public void initializeModel() {
		try {
			UserUtils.getInstance().addForceHeredityDomainCondition(getCriteria(), getFieldName(IEntityAlias.SCOPE_DOMAIN) );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}		
		super.initializeModel();
	}
	
	public boolean isAyudaT() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		return ds.getDomainNameURL().contains("ayudat");
	}
	
	public void generateCompanyScopes(ActionEvent event) {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		AON.getDomainList(ds.getDomainNameURL(), ds.getDomainId(), ds.getCurrentUser(), f-> f.getParentProperty().eq(ds.getDomainId()))
		.stream().forEach(d -> {
			Company cp = AON.getCompany(d.getName(), d.getId(), ds.getCurrentUser(), f-> f.getDomainProperty().eq(d.getId()));
			Stream<Scope> a = AON.getScopeStream(ds.getDomainNameURL(), ds.getDomainId(), ds.getCurrentUser(), 
					f -> f.getDomainProperty().eq(ds.getDomainId()).and(f.getDescriptionProperty().eq(cp.getDocument())));
			if(a.count() <= 0 && cp.getDocument() != null) {
				Scope scope = new Scope()
						.setDomain(ds.getDomainId())
						.setDescription(cp.getDocument());
				scope = AON.insertScope(ds.getDomainNameURL(), ds.getDomainId(), ds.getCurrentUser(), scope);
				d.setScope(scope.getId());
				AON.updateDomainScope(d.getName(), d.getId(), ds.getCurrentUser(), d);
			}
		});
	}
	
}
