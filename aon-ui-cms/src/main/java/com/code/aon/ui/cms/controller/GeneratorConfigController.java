package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.FaqConfig;
import com.code.aon.cms.LinkConfig;
import com.code.aon.cms.Section;
import com.code.aon.cms.util.ISectionContainer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

/**
 * @author igayarre
 *
 */
public class GeneratorConfigController {

	private Integer sectionId;
	
	
	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}

	public static ITransferObject currentConfig(Class pojoClass) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(pojoClass);
		List<ITransferObject> list = bean.getList(null);
		if (list.size()>0)
			return list.get(0);
		return null;
	}
	
	public void initSection(Class c) {
		ISectionContainer config = null;
		try {
			config = (ISectionContainer)GeneratorConfigController.currentConfig(c);
			sectionId = config.getSection().getId();
		} catch (Exception e) {
			sectionId = -1;
		}
	}
	
	private void onSaveConfig(Class c) throws ManagerBeanException{
		try {
			ITransferObject config = (ITransferObject)GeneratorConfigController.currentConfig(c);
			if (config == null){
				config = (ITransferObject)c.newInstance();
			}
			Section section = new Section();
			if (sectionId != -1)
				section.setId(sectionId);
			else
				section = null;
			((ISectionContainer)config).setSection(section);
			IManagerBean bean = BeanManager.getManagerBean(c);
			bean.insertOrUpdate(config);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void onSaveFaqConfig(ActionEvent event) throws ManagerBeanException{
		onSaveConfig(FaqConfig.class);
	}
	
	public void onSaveLinkConfig(ActionEvent event) throws ManagerBeanException{
		onSaveConfig(LinkConfig.class);
	}

}
