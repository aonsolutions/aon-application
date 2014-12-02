package com.code.aon.ui.config.controller;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

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
	
}
