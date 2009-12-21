package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.DownloadCategoryDetail;
import com.code.aon.cms.DownloadConfig;
import com.code.aon.cms.DownloadDetail;
import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.GeneratorConfigController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.DownloadCategoryHandler;
import com.code.aon.ui.cms.velocity.attribute.DownloadHandler;

public class DownloadsGenerator extends Generator {
	
	public static ArrayList<DownloadHandler> getDownloadsList(DownloadCategoryDetail groupDetail) {
		ArrayList<DownloadHandler> list = new ArrayList<DownloadHandler>();

		try {
			IManagerBean bean = BeanManager.getManagerBean(Download.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_DOWNLOAD_CATEGORY_ID), groupDetail.getDownloadCategory().getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.DOWNLOAD_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			for (int i = 0; i < l.size(); i++) {
				Download da = (Download)l.get(i);
				IManagerBean detailBean = BeanManager.getManagerBean(DownloadDetail.class);
				Criteria criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.DOWNLOAD_DETAIL_DOWNLOAD_ID), da.getId());
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.DOWNLOAD_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> ld = (List<ITransferObject>)detailBean.getList(criteria_detail);
				if (ld.size() > 0) {
					DownloadDetail detail = (DownloadDetail)ld.get(0);
					DownloadHandler handler = new DownloadHandler(detail);
					list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static void generate() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.addMessage("Iniciando proceso de generación", VelocityUtil.INFO);
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Buscando plantilla seleccionada '" + ControllerUtil.getCurrentConfig().getTemplate() + "' ...", VelocityUtil.INFO);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando downloads... ", VelocityUtil.INFO);
		
		try {
			Section configSection = GeneratorConfigController.currentSection(DownloadConfig.class);
			IManagerBean bean = BeanManager.getManagerBean(DownloadCategory.class);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(null);
			ArrayList<DownloadCategoryHandler> downloadCategoryHandlerList = new ArrayList<DownloadCategoryHandler>(); 
			for (int i=0; i < l.size(); i++) {
				DownloadCategory group = (DownloadCategory)l.get(i);
				IManagerBean detailBean = BeanManager.getManagerBean(DownloadCategoryDetail.class);
				Criteria detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_DOWNLOAD_CATEGORY_ID), group.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> detailList = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (detailList.size() > 0) {
					DownloadCategoryDetail detail = (DownloadCategoryDetail)detailList.get(0);
					DownloadCategoryHandler downloadCategoryHandler = new DownloadCategoryHandler(detail);
					downloadCategoryHandlerList.add(downloadCategoryHandler);
					if (group.getSection()!=null)
						CommonGenerator.getCommonGenerator().chargeContext(vu, group.getSection());
					else
						CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
					
					int page = 0;
					Iterator<DownloadHandler> iter = downloadCategoryHandler.getList().iterator();
					List<DownloadHandler> partialLst = new ArrayList<DownloadHandler>(); 
					while (iter.hasNext()){
						partialLst.add(iter.next());
						if (partialLst.size() == group.getItemsPerPage()
								|| !iter.hasNext()){
							page++;
							vu.put("download_category", downloadCategoryHandler.getLabel());
							vu.put("back_url", Templates.DOWNLOADS.getHtmlName().replaceAll("%NAME%", DOWNLOAD_CATEGORY_LIST_PAGE));
							if (page==2){
								vu.put("group_previous", downloadCategoryHandler.getUrl());
							}else if (page>2){
								vu.put("group_previous", Templates.DOWNLOADS.getHtmlName().replaceAll("%NAME%", downloadCategoryHandler.getAlias()+"_"+(page-1)));
							}
							if (iter.hasNext()){
								vu.put("group_next", Templates.DOWNLOADS.getHtmlName().replaceAll("%NAME%", downloadCategoryHandler.getAlias()+"_"+(page+1)));
							}
							vu.put("download_group", partialLst);
							vu.addMessage(" Generando album de imagenes " + downloadCategoryHandler.getAlias() + ".", VelocityUtil.INFO);
							generate(vu, Templates.DOWNLOADS, downloadCategoryHandler.getAlias()+(page==1?"":"_"+page));
							vu.remove("back_url");
							vu.remove("group_previous");
							vu.remove("group_next");
							vu.remove("download_category");
							vu.remove("download_group");
							partialLst = new ArrayList<DownloadHandler>();
						}
					}
					partialLst = null;
					iter = null;
					/*
					vu.put("download_group", downloadCategoryHandler);
					vu.addMessage(" Generando descargas " + group.getAlias() + ".", VelocityUtil.INFO);
					if (group.getSection()!=null)
						CommonGenerator.getCommonGenerator().chargeContext(vu, group.getSection());
					else
						CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
					generate(vu, Templates.DOWNLOADS, group.getAlias());
					vu.remove("download_group");
					*/
				}
			}
			vu.put("download_categories", downloadCategoryHandlerList);
			vu.addMessage(" Generando listado categoria descargas.", VelocityUtil.INFO);
			CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
			generate(vu, Templates.DOWNLOADS, DOWNLOAD_CATEGORY_LIST_PAGE);
			vu.remove("download_categories");
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		vu.finalize();
		vu = null;
	}
	
	public static Object getDownloadsHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DownloadCategoryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_DOWNLOAD_CATEGORY_ID), ident);
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if  (l.size()>0) {
				DownloadCategoryDetail groupDetail = (DownloadCategoryDetail)l.get(0);
				DownloadCategoryHandler h = new DownloadCategoryHandler(groupDetail);
				return h;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String DOWNLOAD_CATEGORY_LIST_PAGE = "categories";
}