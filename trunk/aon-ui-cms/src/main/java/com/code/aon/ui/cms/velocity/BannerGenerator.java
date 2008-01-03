package com.code.aon.ui.cms.velocity;

import java.util.List;

import com.code.aon.cms.Banner;
import com.code.aon.cms.BannerDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.velocity.attribute.BannerHandler;

public class BannerGenerator extends Generator {

	public static Object getBannerHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Banner.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.BANNER_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			
			Banner a = (Banner)l.get(0);
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(BannerDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.BANNER_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.BANNER_DETAIL_BANNER_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				BannerDetail ad = (BannerDetail)ld.get(0);
				BannerHandler ah = new BannerHandler(ad);
				return ah;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

}
