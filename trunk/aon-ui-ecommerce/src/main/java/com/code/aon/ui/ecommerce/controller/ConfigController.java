package com.code.aon.ui.ecommerce.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ebackoffice.enumeration.SkinType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;



public class ConfigController {

	private List<ITransferObject> configList;
	private Ecconfig activeConfig;

	public Ecconfig getActiveConfig() {
		return activeConfig;
	}

	public void setActiveConfig(Ecconfig activeConfig) {
		this.activeConfig = activeConfig;
	} 
	
	public String getSkin(){
		searchActiveConfig();
		return getActiveConfig().getSkin().getName(AonUtil.getCurrentLocale());
		//return skinName;
	}
	
	private void searchActiveConfig() {
		//getConfigList();
		Iterator<ITransferObject> it = getConfigList().iterator();
		boolean found=false;
		
		while(it.hasNext() && !found){
			Ecconfig econf = (Ecconfig)it.next();
//			if(econf.getSkin().equals(SkinType.DEFAULT)){
//				setActiveConfig(econf);
//				found = true;
//			}
			if(econf.isActive()){
				setActiveConfig(econf);
				found = true;
			}
			
		}
	}

	private List<ITransferObject> getConfigList() {
		try {
			if (configList == null) {
				IManagerBean bean = BeanManager.getManagerBean(Ecconfig.class);
				configList = bean.getList(null);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return configList;
	}
	
	public void paint(OutputStream out, Object data) throws IOException {
//		if (getAonFile() != null && (getAonFile().getSize() > 0) ) {
//			out.write(getAonFile().getData());
//		}
//		out.write(((ItemAttachment)getTo()).getData());
		out.write(activeConfig.getHeaderImg());
		//this.onSelectNext(null);
		
	}
	
	public String getCompanyName(){
		((CompanyController)FormUtil.getController("company")).onSearch(null);
		((CompanyController)FormUtil.getController("company")).onSelectFirst(null);
		return ((Company)FormUtil.getController("company").getTo()).getName();
	}

	
	
}
