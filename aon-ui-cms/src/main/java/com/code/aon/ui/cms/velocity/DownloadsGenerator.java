package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.DownloadCategoryDetail;
import com.code.aon.cms.DownloadDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
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
	
	public static void generate(VelocityUtil vu) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DownloadCategory.class);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(null);
			for (int i=0; i < l.size(); i++) {
				DownloadCategory group = (DownloadCategory)l.get(i);
				IManagerBean detailBean = BeanManager.getManagerBean(DownloadCategoryDetail.class);
				Criteria detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_DOWNLOAD_CATEGORY_ID), group.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> detailList = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (detailList.size() > 0) {
					DownloadCategoryDetail detail = (DownloadCategoryDetail)detailList.get(0);
					ArrayList<DownloadHandler> accessList = getDownloadsList(detail);
					if (accessList != null && accessList.size() > 0) {
						vu.put("download_group", detail);
						vu.put("download_list", accessList);
						vu.addMessage(" Generando accesos directos " + group.getAlias() + ".", VelocityUtil.INFO);
						CommonGenerator.getCommonGenerator().chargeContext(vu, group.getSection());
						generate(vu, Templates.DOWNLOADS, group.getAlias());
						vu.remove("download_group");
						vu.remove("download_list");
					}
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
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
}