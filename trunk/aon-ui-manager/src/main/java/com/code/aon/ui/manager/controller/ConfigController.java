package com.code.aon.ui.manager.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.manager.Config;
import com.code.aon.ui.util.AonUtil;

public class ConfigController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

	private BasicManagerBean ldapManagerBean;
	
	private String beanName;
	
	private Config to;
	
	public ConfigController() {
		initManagerBean();
		this.to = ensureConfig();
	}
	
	public Config ensureConfig() {
		try {
			List<ITransferObject> list = ldapManagerBean.getList(null);
			if (! list.isEmpty() ) {
				return (Config) list.get(0);
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error( e.getMessage(), e );
		}
		return new Config();
	}

	private void initManagerBean() {
		LdapDAO ldapDAO = new LdapDAO( Config.class );	
		this.ldapManagerBean = new BasicManagerBean(ldapDAO);			
	}
	
	public Config getTo() {
		return this.to;
	}
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public boolean isNew() {
		return false;
	}	
	
	public void accept(ActionEvent event) {
		try {
			this.ldapManagerBean.insertOrUpdate(to);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onAccept",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
	
}