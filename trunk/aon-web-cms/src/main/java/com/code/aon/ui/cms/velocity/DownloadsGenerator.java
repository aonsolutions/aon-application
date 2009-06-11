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
import com.code.aon.ui.cms.IGeneratorLogger;
import com.code.aon.ui.cms.controller.GeneratorConfigController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.DownloadCategoryHandler;
import com.code.aon.ui.cms.velocity.attribute.DownloadHandler;

public class DownloadsGenerator extends Generator {

	public static final String DOWNLOAD_CATEGORY_LIST_PAGE = "categories";
	
	public static void generate() {
		DownloadsGenerator.generate(null);
	}

	public static void generate(DownloadCategory selectedCategory) {
		VelocityUtil vu = CommonGenerator.getCommonGenerator().initVelocityUtil();
		IGeneratorLogger logger = CommonGenerator.getLogger();
		
		try {
			Section configSection = GeneratorConfigController.currentSection(DownloadConfig.class);
			IManagerBean bean = BeanManager.getManagerBean(DownloadCategory.class);
			Criteria criteria = null;
			if (selectedCategory!=null){
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_ID), selectedCategory.getId());
			}
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			ArrayList<DownloadCategoryHandler> downloadCategoryHandlerList = new ArrayList<DownloadCategoryHandler>();
			for (int i=0; i < l.size(); i++) {
				DownloadCategory group = (DownloadCategory)l.get(i);
				IManagerBean detailBean = BeanManager.getManagerBean(DownloadCategoryDetail.class);
				Criteria detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_DOWNLOAD_CATEGORY_ID), group.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> detailList = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (detailList.isEmpty()) {
					logger.warning("La categoria de descargas " + group.getAlias() + " no esta internacionalizada.");
				}else{
					DownloadCategoryDetail detail = (DownloadCategoryDetail)detailList.get(0);
					DownloadCategoryHandler downloadCategoryHandler = new DownloadCategoryHandler(detail,getDownloadsList(detail));
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
							logger.info(" Generando album de imagenes " + downloadCategoryHandler.getAlias() + ".");
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
				}
			}
			vu.put("download_categories", downloadCategoryHandlerList);
			logger.info(" Generando listado categoria descargas.");
			CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
			generate(vu, Templates.DOWNLOADS, DOWNLOAD_CATEGORY_LIST_PAGE);
			vu.remove("download_categories");
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		}
		vu = null;
	}
	
	public static ArrayList<DownloadHandler> getDownloadsList(DownloadCategoryDetail groupDetail) {
		ArrayList<DownloadHandler> list = new ArrayList<DownloadHandler>();

		try {
			IManagerBean bean = BeanManager.getManagerBean(Download.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_DOWNLOAD_CATEGORY_ID), groupDetail.getDownloadCategory().getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.DOWNLOAD_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty())
				CommonGenerator.getLogger().warning("La categoria de descargas " + groupDetail.getDownloadCategory().getAlias() + " no tiene descargas.");
			for (int i = 0; i < l.size(); i++) {
				Download da = (Download)l.get(i);
				IManagerBean detailBean = BeanManager.getManagerBean(DownloadDetail.class);
				Criteria criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.DOWNLOAD_DETAIL_DOWNLOAD_ID), da.getId());
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.DOWNLOAD_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> ld = (List<ITransferObject>)detailBean.getList(criteria_detail);
				if (ld.isEmpty()) {
					CommonGenerator.getLogger().warning("La descarga " + da.getAlias() + " no esta internacionalizada.");
				}else{
					DownloadDetail detail = (DownloadDetail)ld.get(0);
					DownloadHandler handler = new DownloadHandler(detail);
					list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			CommonGenerator.getLogger().error(e.getMessage());
		}
		
		return list;
	}
	
	public static Object getDownloadsHandler(Integer ident) {
		List<ITransferObject> l;
		try {
			IManagerBean bean = BeanManager.getManagerBean(DownloadCategory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_ID), ident);
			l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty()){
				CommonGenerator.getLogger().warning("CATEGORIA DE DESCARGAS "+ident+" REFERENCIADA NO EXISTE !!!");
				return null;
			}
			DownloadCategory dc = (DownloadCategory) l.get(0);
			bean = BeanManager.getManagerBean(DownloadCategoryDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_DOWNLOAD_CATEGORY_ID), ident);
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			l = (List<ITransferObject>)bean.getList(criteria);
			if  (l.isEmpty()) {
				CommonGenerator.getLogger().warning("La categoria de descarga " + dc.getAlias() + " no esta internacionalizada.");
			}else{
				DownloadCategoryDetail groupDetail = (DownloadCategoryDetail)l.get(0);
				DownloadCategoryHandler h = new DownloadCategoryHandler(groupDetail,getDownloadsList(groupDetail));
				return h;
			}
		} catch (ManagerBeanException e) {
			CommonGenerator.getLogger().error(e.getMessage());
		}finally{
			l = null;
		}
		return null;
	}
	
}