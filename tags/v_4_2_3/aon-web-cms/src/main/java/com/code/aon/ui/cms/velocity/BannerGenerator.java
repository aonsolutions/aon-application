package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.Banner;
import com.code.aon.cms.BannerCategory;
import com.code.aon.cms.BannerCategoryDetail;
import com.code.aon.cms.BannerDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.BannerHandler;
import com.code.aon.ui.cms.velocity.attribute.BannerCategoryHandler;

public class BannerGenerator extends Generator {

	public static ArrayList<BannerHandler> getBannerList(BannerCategoryDetail groupDetail) {
		ArrayList<BannerHandler> list = new ArrayList<BannerHandler>();

		try {
			IManagerBean bean = BeanManager.getManagerBean(Banner.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BANNER_BANNER_CATEGORY_ID), groupDetail.getBannerCategory().getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BANNER_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.BANNER_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty())
				CommonGenerator.getLogger().warning("La categoria de banners " + groupDetail.getBannerCategory().getAlias() + " no tiene banners.");
			for (int i = 0; i < l.size(); i++) {
				Banner da = (Banner)l.get(i);
				IManagerBean detailBean = BeanManager.getManagerBean(BannerDetail.class);
				Criteria criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.BANNER_DETAIL_BANNER_ID), da.getId());
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.BANNER_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> ld = (List<ITransferObject>)detailBean.getList(criteria_detail);
				if (ld.isEmpty()){
					CommonGenerator.getLogger().warning("El banner " + da.getAlias() + " no esta internacionalizado.");
				}else{
					BannerDetail detail = (BannerDetail)ld.get(0);
					BannerHandler handler = new BannerHandler(detail);
					if (handler.getUrl() != null) list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			CommonGenerator.getLogger().error(e.getMessage());
		}
		
		return list;
	}
	
	public static void generate(VelocityUtil vu) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(BannerCategory.class);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(null);
			for (int i=0; i < l.size(); i++) {
				BannerCategory group = (BannerCategory)l.get(i);
				IManagerBean detailBean = BeanManager.getManagerBean(BannerCategoryDetail.class);
				Criteria detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.BANNER_CATEGORY_DETAIL_BANNER_CATEGORY_ID), group.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.BANNER_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> detailList = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (detailList.isEmpty()){
					CommonGenerator.getLogger().warning("La categoria de banners " + group.getAlias() + " no esta internacionalizada.");
				}else{
					BannerCategoryDetail detail = (BannerCategoryDetail)detailList.get(0);
					BannerCategoryHandler bch = new BannerCategoryHandler(detail);
					ArrayList<BannerHandler> accessList = getBannerList(detail);
					if (accessList != null && accessList.size() > 0) {
						vu.put("banner_category", bch);
						vu.put("banner_list", accessList);
						CommonGenerator.getLogger().info(" Generando banners " + group.getAlias() + ".");
						CommonGenerator.getCommonGenerator().chargeContext(vu, group.getSection());
						generate(vu, Templates.BANNERS, group.getAlias());
						vu.remove("banner_category");
						vu.remove("banner_list");
					}
				}
			}
		} catch (ManagerBeanException e) {
			CommonGenerator.getLogger().error(e.getMessage());
		}
	}
	public static Object getBannerHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Banner.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BANNER_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty()){
				CommonGenerator.getLogger().warning("BANNER "+ident+" REFERENCIADO NO EXISTE !!!");
				return null;
			}
			Banner a = (Banner)l.get(0);
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(BannerDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.BANNER_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.BANNER_DETAIL_BANNER_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				if (ld.isEmpty()){
					CommonGenerator.getLogger().warning("El banner " + a.getAlias() + " no esta internacionalizado.");
				}else{
					BannerDetail ad = (BannerDetail)ld.get(0);
					BannerHandler ah = new BannerHandler(ad);
					return ah;
				}
			}
		} catch (ManagerBeanException e) {
			CommonGenerator.getLogger().error(e.getMessage());
		}
		return null;
	}

	public static Object getBannerCategoryHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(BannerCategory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BANNER_CATEGORY_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty()){
				CommonGenerator.getLogger().warning("CATEGORIA DE BANNER "+ident+" REFERENCIADO NO EXISTE !!!");
				return null;
			}
			BannerCategory a = (BannerCategory)l.get(0);
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(BannerCategoryDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.BANNER_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.BANNER_CATEGORY_DETAIL_BANNER_CATEGORY_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				if (ld.isEmpty()){
					CommonGenerator.getLogger().warning("La categoria de banner " + a.getAlias() + " no esta internacionalizado.");
				}else{
					BannerCategoryDetail ad = (BannerCategoryDetail)ld.get(0);
					BannerCategoryHandler ah = new BannerCategoryHandler(ad);
					return ah;
				}
			}
		} catch (ManagerBeanException e) {
			CommonGenerator.getLogger().error(e.getMessage());
		}
		return null;
	}

}
