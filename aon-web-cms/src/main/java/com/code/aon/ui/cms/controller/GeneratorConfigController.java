package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.AlbumConfig;
import com.code.aon.cms.ArticleConfig;
import com.code.aon.cms.DownloadConfig;
import com.code.aon.cms.FaqConfig;
import com.code.aon.cms.HiruConfig;
import com.code.aon.cms.LinkConfig;
import com.code.aon.cms.ProductCategoryConfig;
import com.code.aon.cms.Section;
import com.code.aon.cms.SportConfig;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.util.ISectionContainer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

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

	public static Section defaultSection() throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Section.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SECTION_DEFAULT_), true);
		return (Section)bean.getList(criteria).iterator().next();
	}

	public static Section currentSection(Class pojoClass) throws ManagerBeanException{
		ISectionContainer object = (ISectionContainer) currentConfig(pojoClass);
		if (object!=null)
			return object.getSection();
		return defaultSection();
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

	public void onSaveAlbumConfig(ActionEvent event) throws ManagerBeanException{
		onSaveConfig(AlbumConfig.class);
	}
	
	public void onSaveArticleConfig(ActionEvent event) throws ManagerBeanException{
		onSaveConfig(ArticleConfig.class);
	}
	
	public void onSaveDownloadConfig(ActionEvent event) throws ManagerBeanException{
		onSaveConfig(DownloadConfig.class);
	}
	
	public void onSaveHiruConfig(ActionEvent event) throws ManagerBeanException{
		onSaveConfig(HiruConfig.class);
	}
	
	public void onSaveProductCategoryConfig(ActionEvent event) throws ManagerBeanException{
		onSaveConfig(ProductCategoryConfig.class);
	}
	
	public void onSaveSportConfig(ActionEvent event) throws ManagerBeanException{
		onSaveConfig(SportConfig.class);
	}
	
}
