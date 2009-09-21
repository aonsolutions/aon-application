package com.code.aon.ui.ecommerce.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.ecommerce.util.ECommerceUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;



public class ConfigController {

	private List<ITransferObject> configList;
	private Ecconfig activeConfig;
	private Company company;
	
	public Ecconfig getActiveConfig() {
		return activeConfig;
	}

	public void setActiveConfig(Ecconfig activeConfig) {
		this.activeConfig = activeConfig;
	} 
	
	public Company getCompany() {
		if(company==null){
			searchCurrentCompany();
		}
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	public String getSkin(){
		searchActiveConfig();
		return getActiveConfig().getSkin().getName(AonUtil.getCurrentLocale());
		//return skinName;
	}
	
	private void searchActiveConfig() {
		Iterator<ITransferObject> it = getConfigList().iterator();
		boolean found=false;
		
		while(it.hasNext() && !found){
			Ecconfig econf = (Ecconfig)it.next();
			if(econf.isActive()){
				setActiveConfig(econf);
				found = true;
			}
		}
	}

	private List<ITransferObject> getConfigList() {
		try {
			if (configList == null || configList.size()>=0) {
				IManagerBean bean = BeanManager.getManagerBean(Ecconfig.class);
				configList = bean.getList(null);
			}
			if (configList == null || configList.size()<=0) {
				throw new ManagerBeanException();
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar la configuracion";
			throw new AbortProcessingException(msg, e);
		}
		return configList;
	}
	
	public void paint(OutputStream out, Object data) throws IOException {
		out.write(activeConfig.getHeaderImg());
	}
	
	public void searchCurrentCompany(){
		((CompanyController)FormUtil.getController("company")).onSearch(null);
		((CompanyController)FormUtil.getController("company")).onSelectFirst(null);
		setCompany(((Company)FormUtil.getController("company").getTo()));
	}
	
	public List<SelectItem> getPayMethodList(){
		return ECommerceUtil.payMethodList();
	}
	
}
